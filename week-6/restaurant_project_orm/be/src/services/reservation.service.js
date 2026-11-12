const prisma = require('../config/prisma');
const { sendReservationCreatedEvent } = require('../producers/reservation.producer');

const RESERVATION_SLOT_MS = 2 * 60 * 60 * 1000;
const ACTIVE_RESERVATION_STATUSES = ['pending', 'confirmed'];

const createReservation = async ({
  fullName,
  email,
  phone,
  tableId,
  tableNumber,
  reservationTime,
  numberOfPeople,
  note
}) => {
  const parsedTime = new Date(reservationTime);
  const parsedPeople = Number.parseInt(numberOfPeople, 10);

  if (Number.isNaN(parsedTime.getTime()) || parsedTime <= new Date()) {
    const error = new Error('Thời gian đặt bàn không hợp lệ');
    error.statusCode = 400;
    error.details = 'reservationTime phải là thời điểm trong tương lai';
    throw error;
  }

  if (!Number.isInteger(parsedPeople) || parsedPeople <= 0) {
    const error = new Error('Số lượng khách không hợp lệ');
    error.statusCode = 400;
    error.details = 'numberOfPeople phải là số nguyên lớn hơn 0';
    throw error;
  }

  const reservation = await prisma.$transaction(async (tx) => {
    const table = tableId
      ? await tx.restaurantTable.findUnique({ where: { id: tableId } })
      : await tx.restaurantTable.findUnique({ where: { table_number: String(tableNumber) } });

    if (!table) {
      const error = new Error('Bàn không tồn tại');
      error.statusCode = 404;
      throw error;
    }

    if (table.status === 'maintenance') {
      const error = new Error('Bàn đang bảo trì');
      error.statusCode = 409;
      throw error;
    }

    if (parsedPeople > table.capacity) {
      const error = new Error('Số khách vượt quá sức chứa của bàn');
      error.statusCode = 409;
      error.details = `Bàn ${table.table_number} chỉ phục vụ tối đa ${table.capacity} khách`;
      throw error;
    }

    const conflictingReservation = await tx.reservation.findFirst({
      where: {
        table_id: table.id,
        status: { in: ACTIVE_RESERVATION_STATUSES },
        reservation_time: {
          gt: new Date(parsedTime.getTime() - RESERVATION_SLOT_MS),
          lt: new Date(parsedTime.getTime() + RESERVATION_SLOT_MS)
        }
      },
      select: { id: true }
    });

    if (conflictingReservation) {
      const error = new Error('Bàn đã có lịch đặt trong khung giờ này');
      error.statusCode = 409;
      error.details = 'Mỗi lượt đặt bàn được giữ trong 2 giờ';
      throw error;
    }

    let customer = await tx.customer.findFirst({
      where: {
        OR: [
          { email },
          ...(phone ? [{ phone }] : [])
        ]
      }
    });

    if (!customer) {
      customer = await tx.customer.create({
        data: {
          full_name: fullName,
          email,
          phone: phone || null
        }
      });
    }

    return tx.reservation.create({
      data: {
        customer_id: customer.id,
        table_id: table.id,
        reservation_time: parsedTime,
        number_of_people: parsedPeople,
        status: 'pending',
        note: note || null
      },
      include: {
        customer: true,
        table: true
      }
    });
  }, { isolationLevel: 'Serializable' });

  // 4. Bắn sự kiện bất đồng bộ vào Kafka (Producer)
  // Không cần await để API phản hồi ngay lập tức cho client
  sendReservationCreatedEvent({
    id: reservation.id,
    customerEmail: reservation.customer?.email,
    customerName: reservation.customer?.full_name,
    tableNumber: reservation.table?.table_number,
    reservationTime: reservation.reservation_time,
    numberOfPeople: reservation.number_of_people
  }).catch((err) => {
    console.error('[ReservationService] Lỗi khi bắn event vào Kafka:', err.message);
  });

  // 5. Trả về kết quả ngay cho Controller
  return reservation;
};

const getReservations = async (page = 1, limit = 10) => {
  const skip = (page - 1) * limit;

  const [total, reservations] = await Promise.all([
    prisma.reservation.count(),
    prisma.reservation.findMany({
      skip,
      take: limit,
      include: {
        customer: true,
        table: true
      },
      orderBy: { created_at: 'desc' }
    })
  ]);

  return {
    data: reservations,
    pagination: {
      total,
      page,
      limit,
      totalPages: Math.ceil(total / limit)
    }
  };
};

const getReservationById = async (id) => {
  return await prisma.reservation.findUnique({
    where: { id },
    include: {
      customer: true,
      table: true
    }
  });
};

const updateReservationStatus = async (id, nextStatus) => {
  const reservation = await prisma.reservation.findUnique({ where: { id } });
  if (!reservation) {
    const error = new Error('Không tìm thấy thông tin đặt bàn');
    error.statusCode = 404;
    throw error;
  }

  const transitions = {
    pending: ['confirmed', 'cancelled'],
    confirmed: ['completed', 'cancelled']
  };

  if (!transitions[reservation.status]?.includes(nextStatus)) {
    const error = new Error('Trạng thái đặt bàn không hợp lệ');
    error.statusCode = 409;
    error.details = `Không thể chuyển từ ${reservation.status} sang ${nextStatus}`;
    throw error;
  }

  return prisma.reservation.update({
    where: { id },
    data: { status: nextStatus },
    include: { customer: true, table: true }
  });
};

module.exports = {
  createReservation,
  getReservations,
  getReservationById,
  updateReservationStatus
};

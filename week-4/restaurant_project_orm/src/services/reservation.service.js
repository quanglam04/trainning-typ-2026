const prisma = require('../config/prisma');
const { sendReservationCreatedEvent } = require('../producers/reservation.producer');

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
  // 1. Tìm hoặc tạo mới khách hàng (Customer) dựa trên email hoặc số điện thoại
  let customer;
  if (email) {
    customer = await prisma.customer.findFirst({ where: { email } });
  } else if (phone) {
    customer = await prisma.customer.findFirst({ where: { phone } });
  }

  if (!customer) {
    customer = await prisma.customer.create({
      data: {
        full_name: fullName,
        email: email || null,
        phone: phone || null
      }
    });
  }

  // 2. Tìm bàn (RestaurantTable) theo tableId hoặc tableNumber
  let table;
  if (tableId) {
    table = await prisma.restaurantTable.findUnique({ where: { id: tableId } });
  } else if (tableNumber) {
    table = await prisma.restaurantTable.findUnique({ where: { table_number: String(tableNumber) } });
  }

  if (!table) {
    const error = new Error('Bàn không tồn tại');
    error.status = 404;
    throw error;
  }

  // 3. Tạo bản ghi Đặt bàn (Reservation) trong DB
  const reservation = await prisma.reservation.create({
    data: {
      customer_id: customer.id,
      table_id: table.id,
      reservation_time: new Date(reservationTime),
      number_of_people: parseInt(numberOfPeople, 10),
      status: 'pending',
      note: note || null
    },
    include: {
      customer: true,
      table: true
    }
  });

  // 4. Bắn sự kiện bất đồng bộ vào Kafka (Producer)
  // Không cần await để API phản hồi ngay lập tức cho client
  sendReservationCreatedEvent({
    id: reservation.id,
    customerEmail: customer.email,
    customerName: customer.full_name,
    tableNumber: table.table_number,
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

module.exports = {
  createReservation,
  getReservations,
  getReservationById
};

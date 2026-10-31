const reservationService = require('../services/reservation.service');

const createReservation = async (req, res, next) => {
  try {
    const {
      fullName,
      email,
      phone,
      tableId,
      tableNumber,
      reservationTime,
      numberOfPeople,
      note
    } = req.body;

    // Validate dữ liệu bắt buộc
    if (!fullName || !email) {
      return res.status(400).json({
        error: true,
        status: 400,
        code: 400,
        message: 'Validation Error',
        details: 'fullName và email là bắt buộc để gửi thông tin xác nhận'
      });
    }

    if (!tableId && !tableNumber) {
      return res.status(400).json({
        error: true,
        status: 400,
        code: 400,
        message: 'Validation Error',
        details: 'Cần cung cấp tableId hoặc tableNumber'
      });
    }

    if (!reservationTime || !numberOfPeople) {
      return res.status(400).json({
        error: true,
        status: 400,
        code: 400,
        message: 'Validation Error',
        details: 'reservationTime và numberOfPeople là bắt buộc'
      });
    }

    const reservation = await reservationService.createReservation({
      fullName,
      email,
      phone,
      tableId,
      tableNumber,
      reservationTime,
      numberOfPeople,
      note
    });

    res.status(201).json({
      error: false,
      status: 201,
      code: 201,
      message: 'Đặt bàn thành công! Email xác nhận đang được gửi trong giây lát.',
      data: reservation
    });
  } catch (error) {
    next(error);
  }
};

const getReservations = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page, 10) || 1;
    const limit = parseInt(req.query.limit, 10) || 10;

    const result = await reservationService.getReservations(page, limit);

    res.status(200).json({
      error: false,
      status: 200,
      code: 200,
      message: 'Lấy danh sách đặt bàn thành công',
      ...result
    });
  } catch (error) {
    next(error);
  }
};

const getReservationById = async (req, res, next) => {
  try {
    const reservation = await reservationService.getReservationById(req.params.id);
    if (!reservation) {
      return res.status(404).json({
        error: true,
        status: 404,
        code: 404,
        message: 'Không tìm thấy thông tin đặt bàn',
        details: null
      });
    }

    res.status(200).json({
      error: false,
      status: 200,
      code: 200,
      message: 'Lấy chi tiết đặt bàn thành công',
      data: reservation
    });
  } catch (error) {
    next(error);
  }
};

module.exports = {
  createReservation,
  getReservations,
  getReservationById
};

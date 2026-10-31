const express = require('express');
const reservationController = require('../controllers/reservation.controller');

const router = express.Router();

// Khách hàng đặt bàn trực tuyến (Public API)
router.post('/', reservationController.createReservation);

// Xem danh sách và chi tiết đặt bàn
router.get('/', reservationController.getReservations);
router.get('/:id', reservationController.getReservationById);

module.exports = router;

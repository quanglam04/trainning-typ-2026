const express = require('express');
const reservationController = require('../controllers/reservation.controller');
const { authenticateToken, authorizeRoles } = require('../middlewares/auth.middleware');

const router = express.Router();

// Khách hàng đặt bàn trực tuyến (Public API)
router.post('/', reservationController.createReservation);

// Chỉ nhân viên có nghiệp vụ đặt bàn được xem/cập nhật dữ liệu khách hàng
router.get('/', authenticateToken, authorizeRoles('admin', 'manager', 'waiter'), reservationController.getReservations);
router.get('/:id', authenticateToken, authorizeRoles('admin', 'manager', 'waiter'), reservationController.getReservationById);
router.patch('/:id/status', authenticateToken, authorizeRoles('admin', 'manager', 'waiter'), reservationController.updateReservationStatus);

module.exports = router;

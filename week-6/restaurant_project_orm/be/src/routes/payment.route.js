const express = require('express');
const paymentController = require('../controllers/payment.controller');
const { authenticateToken, authorizeRoles } = require('../middlewares/auth.middleware');

const router = express.Router();

// Chỉ Cashier, Manager, Admin mới được thực hiện thanh toán
router.post(
  '/',
  authenticateToken,
  authorizeRoles('cashier', 'manager', 'admin'),
  paymentController.createPayment
);

// Xem thông tin thanh toán theo orderId
router.get(
  '/order/:orderId',
  authenticateToken,
  authorizeRoles('cashier', 'manager', 'admin'),
  paymentController.getPaymentByOrderId
);

module.exports = router;


const express = require('express');
const orderController = require('../controllers/order.controller');
const { authenticateToken, authorizeRoles } = require('../middlewares/auth.middleware');

const router = express.Router();

// 1. Tạo đơn hàng (Waiter, Cashier, Manager, Admin)
router.post(
  '/',
  authenticateToken,
  authorizeRoles('waiter', 'cashier', 'manager', 'admin'),
  orderController.createOrder
);

// 2. Cập nhật trạng thái đơn (Kitchen, Waiter, Manager, Admin)
router.patch(
  '/status',
  authenticateToken,
  authorizeRoles('kitchen', 'waiter', 'manager', 'admin'),
  orderController.updateStatusOrder
);

// 3. Lấy danh sách tất cả các đơn hàng cho Dashboard
router.get(
  '/',
  authenticateToken,
  orderController.getOrders
);

// 4. Lấy danh sách bàn
router.get(
  '/tables/list',
  authenticateToken,
  orderController.getTables
);

// 5. Cập nhật trạng thái bàn thủ công (Lễ tân/Waiter/Thu ngân đón khách xếp bàn, trả bàn)
router.patch(
  '/tables/:id/status',
  authenticateToken,
  authorizeRoles('waiter', 'cashier', 'manager', 'admin'),
  orderController.updateTableStatus
);

module.exports = router;

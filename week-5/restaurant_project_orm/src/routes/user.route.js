const express = require('express');
const userController = require('../controllers/user.controller');
const { authenticateToken, authorizeRoles } = require('../middlewares/auth.middleware.js');

const router = express.Router();

// Xem danh sách và chi tiết user: Admin và Manager
router.get('/', authenticateToken, authorizeRoles('admin', 'manager'), userController.getUsers);
router.get('/:id', authenticateToken, authorizeRoles('admin', 'manager'), userController.getUserById);

// Cập nhật thông tin chung: Chỉ Admin
router.put('/:id', authenticateToken, authorizeRoles('admin'), userController.updateUser);

// API DÀNH RIÊNG CHO ADMIN ĐỂ CẤP / THAY ĐỔI ROLE CỦA USER:
router.patch('/:id/role', authenticateToken, authorizeRoles('admin'), userController.updateUserRole);

// Xóa user: Chỉ Admin
router.delete('/:id', authenticateToken, authorizeRoles('admin'), userController.deleteUser);

module.exports = router;

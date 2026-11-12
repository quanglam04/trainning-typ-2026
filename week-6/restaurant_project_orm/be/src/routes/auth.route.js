const express = require('express');
const authController = require('../controllers/auth.controller.js');
const { authenticateToken, authorizeRoles } = require('../middlewares/auth.middleware');

const router = express.Router();

router.post('/register', authenticateToken, authorizeRoles('admin'), authController.createUser);
router.post('/login', authController.loginUser);
router.post('/refresh-token', authController.refreshToken);
router.post('/logout', authController.logout);


module.exports = router;

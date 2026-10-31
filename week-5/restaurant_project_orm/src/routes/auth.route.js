const express = require('express');
const authController = require('../controllers/auth.controller.js');

const router = express.Router();

router.post('/register', authController.createUser);
router.post('/login', authController.loginUser);
router.post('/refresh-token', authController.refreshToken);


module.exports = router;

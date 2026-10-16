const jwt = require('jsonwebtoken');
const ApiError = require('../utils/ApiError');
const authenticate = (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      throw new ApiError(401, 'UNAUTHORIZED', 'Vui lòng đăng nhập (Thiếu Access Token)');
    }
    const token = authHeader.split(' ')[1];
    const decoded = jwt.verify(
      token,
      process.env.ACCESS_TOKEN_SECRET || 'typ_week3_access_secret_key_2026'
    );
    req.user = decoded;
    next();
  } catch (error) {
    if (error.name === 'TokenExpiredError') {
      return next(new ApiError(401, 'TOKEN_EXPIRED', 'Access Token đã hết hạn'));
    }
    if (error.name === 'JsonWebTokenError') {
      return next(new ApiError(401, 'INVALID_TOKEN', 'Access Token không hợp lệ'));
    }
    next(error);
  }
};
const authorize = (...allowedRoles) => {
  return (req, res, next) => {
    if (!req.user) {
      return next(new ApiError(401, 'UNAUTHORIZED', 'Chưa xác thực người dùng'));
    }
    if (!allowedRoles.includes(req.user.role)) {
      return next(
        new ApiError(
          403,
          'FORBIDDEN',
          `Bạn không có quyền thực hiện thao tác này (Yêu cầu quyền: ${allowedRoles.join(', ')})`
        )
      );
    }
    next();
  };
};
module.exports = { authenticate, authorize };
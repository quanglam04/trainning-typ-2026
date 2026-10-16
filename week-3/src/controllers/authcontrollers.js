const authService = require('../services/authService');
class AuthController {
  async register(req, res, next) {
    try {
      const user = await authService.register(req.body);
      res.status(201).json({
        success: true,
        message: 'Đăng ký tài khoản thành công',
        data: user
      });
    } catch (error) {
      next(error);
    }
  }
  async login(req, res, next) {
    try {
      const result = await authService.login(req.body);
      res.status(200).json({
        success: true,
        message: 'Đăng nhập thành công',
        data: result
      });
    } catch (error) {
      next(error);
    }
  }
  async refreshToken(req, res, next) {
    try {
      const { refreshToken } = req.body;
      const tokens = await authService.refreshToken(refreshToken);
      res.status(200).json({
        success: true,
        message: 'Làm mới token thành công',
        data: tokens
      });
    } catch (error) {
      next(error);
    }
  }
  async logout(req, res, next) {
    try {
      const result = await authService.logout(req.user.userId);
      res.status(200).json({
        success: true,
        message: result.message
      });
    } catch (error) {
      next(error);
    }
  }
  async getMe(req, res, next) {
    try {
      const user = await authService.getProfile(req.user.userId);
      res.status(200).json({
        success: true,
        data: user
      });
    } catch (error) {
      next(error);
    }
  }
}
module.exports = new AuthController();
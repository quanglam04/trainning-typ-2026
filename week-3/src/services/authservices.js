const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const prisma = require('../prisma');
const ApiError = require('../utils/ApiError');
const ACCESS_SECRET = process.env.ACCESS_TOKEN_SECRET || 'typ_week3_access_secret_key_2026';
const REFRESH_SECRET = process.env.REFRESH_TOKEN_SECRET || 'typ_week3_refresh_secret_key_2026';
const ACCESS_EXPIRES = process.env.ACCESS_TOKEN_EXPIRES_IN || '15m';
const REFRESH_EXPIRES = process.env.REFRESH_TOKEN_EXPIRES_IN || '7d';
class AuthService {
  generateTokens(user) {
    const payload = {
      userId: user.id,
      email: user.email,
      role: user.role
    };
    const accessToken = jwt.sign(payload, ACCESS_SECRET, { expiresIn: ACCESS_EXPIRES });
    const refreshToken = jwt.sign({ userId: user.id }, REFRESH_SECRET, { expiresIn: REFRESH_EXPIRES });
    return { accessToken, refreshToken };
  }
  async register({ email, password, name, role }) {
    if (!email || !password) {
      throw new ApiError(400, 'BAD_REQUEST', 'Email và mật khẩu là bắt buộc');
    }
    const existingUser = await prisma.user.findUnique({ where: { email } });
    if (existingUser) {
      throw new ApiError(409, 'EMAIL_EXISTS', 'Email này đã được đăng ký');
    }
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);
    const validRole = role === 'ADMIN' ? 'ADMIN' : 'USER';
    const newUser = await prisma.user.create({
      data: {
        email,
        password: hashedPassword,
        name: name || null,
        role: validRole
      },
      select: {
        id: true,
        email: true,
        name: true,
        role: true,
        createdAt: true
      }
    });
    return newUser;
  }
  async login({ email, password }) {
    if (!email || !password) {
      throw new ApiError(400, 'BAD_REQUEST', 'Vui lòng nhập email và mật khẩu');
    }
    const user = await prisma.user.findUnique({ where: { email } });
    if (!user) {
      throw new ApiError(401, 'INVALID_CREDENTIALS', 'Email hoặc mật khẩu không chính xác');
    }
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      throw new ApiError(401, 'INVALID_CREDENTIALS', 'Email hoặc mật khẩu không chính xác');
    }
    const { accessToken, refreshToken } = this.generateTokens(user);
    await prisma.user.update({
      where: { id: user.id },
      data: { refreshToken }
    });
    return {
      user: {
        id: user.id,
        email: user.email,
        name: user.name,
        role: user.role
      },
      accessToken,
      refreshToken
    };
  }
  async refreshToken(token) {
    if (!token) {
      throw new ApiError(400, 'BAD_REQUEST', 'Vui lòng cung cấp Refresh Token');
    }
    let decoded;
    try {
      decoded = jwt.verify(token, REFRESH_SECRET);
    } catch (err) {
      throw new ApiError(401, 'INVALID_REFRESH_TOKEN', 'Refresh Token không hợp lệ hoặc đã hết hạn');
    }
    const user = await prisma.user.findUnique({ where: { id: decoded.userId } });
    if (!user || user.refreshToken !== token) {
      throw new ApiError(401, 'INVALID_REFRESH_TOKEN', 'Refresh Token không tồn tại hoặc đã bị thu hồi');
    }
    const tokens = this.generateTokens(user);
    await prisma.user.update({
      where: { id: user.id },
      data: { refreshToken: tokens.refreshToken }
    });
    return tokens;
  }
  async logout(userId) {
    await prisma.user.update({
      where: { id: userId },
      data: { refreshToken: null }
    });
    return { message: 'Đăng xuất thành công' };
  }
  async getProfile(userId) {
    const user = await prisma.user.findUnique({
      where: { id: userId },
      select: { id: true, email: true, name: true, role: true, createdAt: true }
    });
    if (!user) {
      throw new ApiError(404, 'USER_NOT_FOUND', 'Không tìm thấy người dùng');
    }
    return user;
  }
}
module.exports = new AuthService();
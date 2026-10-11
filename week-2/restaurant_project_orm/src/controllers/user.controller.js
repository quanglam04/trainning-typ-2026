const userService = require('../services/user.service');

const createUser = async (req, res, next) => {
  try {
    const { username, password, email } = req.body;
    if (!username || !password || !email) {
      return res.status(400).json({
        error: true,
        code: 400,
        message: "Validation Error",
        details: "Username, password and email are required"
      });
    }
    const user = await userService.createUser(req.body);
    delete user.password_hash;
    res.status(201).json({ error: false, code: 201, message: "User created successfully", data: user });
  } catch (error) {
    next(error);
  }
};

const getUsers = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;

    const result = await userService.getUsers(page, limit);
    res.status(200).json({ error: false, code: 200, message: "Users retrieved successfully", ...result });
  } catch (error) {
    next(error);
  }
};

const getUserById = async (req, res, next) => {
  try {
    const user = await userService.getUserById(req.params.id);
    if (!user) {
      return res.status(404).json({ error: true, code: 404, message: "User not found", details: null });
    }
    res.status(200).json({ error: false, code: 200, message: "User retrieved successfully", data: user });
  } catch (error) {
    next(error);
  }
};

const updateUser = async (req, res, next) => {
  try {
    const user = await userService.updateUser(req.params.id, req.body);
    res.status(200).json({ error: false, code: 200, message: "User updated successfully", data: user });
  } catch (error) {
    if (error.code === 'P2025') {
      return res.status(404).json({ error: true, code: 404, message: "User not found", details: null });
    }
    next(error);
  }
};

const deleteUser = async (req, res, next) => {
  try {
    await userService.deleteUser(req.params.id);
    res.status(200).json({ error: false, code: 200, message: "User deleted successfully", details: null });
  } catch (error) {
    if (error.code === 'P2025') {
      return res.status(404).json({ error: true, code: 404, message: "User not found", details: null });
    }
    next(error);
  }
};

module.exports = {
  createUser,
  getUsers,
  getUserById,
  updateUser,
  deleteUser
};

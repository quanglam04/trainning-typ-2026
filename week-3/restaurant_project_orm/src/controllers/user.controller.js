const userService = require('../services/user.service');

const getUsers = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;

    const result = await userService.getUsers(page, limit);
    res.status(200).json({
      error: false,
      code: 200,
      message: "Users retrieved successfully",
      ...result
    });
  } catch (error) {
    next(error);
  }
};

const getUserById = async (req, res, next) => {
  try {
    const user = await userService.getUserById(req.params.id);
    if (!user) {
      return res.status(404).json({
        error: true,
        code: 404,
        message: "User not found",
        details: null
      });
    }
    res.status(200).json({
      error: false,
      code: 200,
      message: "User retrieved successfully",
      data: user
    });
  } catch (error) {
    next(error);
  }
};

const updateUser = async (req, res, next) => {
  try {
    const user = await userService.updateUser(req.params.id, req.body);
    res.status(200).json({
      error: false,
      code: 200,
      message: "User updated successfully",
      data: user
    });
  } catch (error) {
    if (error.code === 'P2025') {
      return res.status(404).json({
        error: true,
        code: 404,
        message: "User not found",
        details: null
      });
    }
    next(error);
  }
};

const deleteUser = async (req, res, next) => {
  try {
    await userService.deleteUser(req.params.id);
    res.status(200).json({
      error: false,
      code: 200,
      message: "User deleted successfully",
      details: null
    });
  } catch (error) {
    if (error.code === 'P2025') {
      return res.status(404).json({
        error: true,
        code: 404,
        message: "User not found",
        details: null
      });
    }
    next(error);
  }
};

const updateUserRole = async (req, res, next) => {
  try {
    const { role } = req.body;
    const validRoles = ['admin', 'manager', 'cashier', 'kitchen', 'waiter'];

    if (!role || !validRoles.includes(role)) {
      return res.status(400).json({
        error: true,
        code: 400,
        message: "Validation Error",
        details: `Invalid role. Allowed roles are: ${validRoles.join(', ')}`
      });
    }

    const user = await userService.updateUserRole(req.params.id, role);
    res.status(200).json({
      error: false,
      code: 200,
      message: "User role updated successfully",
      data: user
    });
  } catch (error) {
    if (error.code === 'P2025') {
      return res.status(404).json({
        error: true,
        code: 404,
        message: "User not found",
        details: null
      });
    }
    next(error);
  }
};

module.exports = {
  getUsers,
  getUserById,
  updateUser,
  updateUserRole,
  deleteUser
};

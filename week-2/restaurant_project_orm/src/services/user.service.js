const prisma = require('../config/prisma');
const bcrypt = require('bcrypt');

const createUser = async (data) => {
  const salt = await bcrypt.genSalt(10);
  const hashedPassword = await bcrypt.hash(data.password, salt);

  return await prisma.user.create({
    data: {
      username: data.username,
      password_hash: hashedPassword,
      full_name: data.full_name,
      email: data.email,
      phone: data.phone,
      role: data.role,
      status: data.status || 'active'
    }
  });
};

const getUsers = async (page = 1, limit = 10) => {
  const skip = (page - 1) * limit;
  const users = await prisma.user.findMany({
    skip,
    take: limit,
    select: {
      id: true,
      username: true,
      full_name: true,
      email: true,
      phone: true,
      role: true,
      status: true,
      created_at: true
    }
  });

  const totalElements = await prisma.user.count();

  return {
    data: users,
    page,
    size: limit,
    totalPages: Math.ceil(totalElements / limit),
    totalElements
  };
};

const getUserById = async (id) => {
  return await prisma.user.findUnique({
    where: { id: parseInt(id) },
    select: {
      id: true,
      username: true,
      full_name: true,
      email: true,
      phone: true,
      role: true,
      status: true,
      created_at: true
    }
  });
};

const updateUser = async (id, data) => {
  if (data.password) {
    const salt = await bcrypt.genSalt(10);
    data.password_hash = await bcrypt.hash(data.password, salt);
    delete data.password;
  }

  return await prisma.user.update({
    where: { id: parseInt(id) },
    data,
    select: {
      id: true,
      username: true,
      full_name: true,
      email: true,
      phone: true,
      role: true,
      status: true
    }
  });
};

const deleteUser = async (id) => {
  return await prisma.user.delete({
    where: { id: parseInt(id) }
  });
};

module.exports = {
  createUser,
  getUsers,
  getUserById,
  updateUser,
  deleteUser
};

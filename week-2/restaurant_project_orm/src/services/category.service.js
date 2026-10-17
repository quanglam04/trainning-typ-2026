const prisma = require('../config/prisma');

const createCategory = async (data) => {
  return await prisma.category.create({
    data
  });
};

const getCategories = async (page = 1, limit = 10) => {
  const skip = (page - 1) * limit;
  const categories = await prisma.category.findMany({
    skip,
    take: limit,
    include: {
      products: true
    }
  });
  
  const total = await prisma.category.count();
  
  return {
    data: categories,
    meta: {
      total,
      page,
      limit,
      totalPages: Math.ceil(total / limit)
    }
  };
};

const getCategoryById = async (id) => {
  return await prisma.category.findUnique({
    where: { id: parseInt(id) },
    include: { products: true }
  });
};

const updateCategory = async (id, data) => {
  return await prisma.category.update({
    where: { id: parseInt(id) },
    data
  });
};

const deleteCategory = async (id) => {
  return await prisma.category.delete({
    where: { id: parseInt(id) }
  });
};

module.exports = {
  createCategory,
  getCategories,
  getCategoryById,
  updateCategory,
  deleteCategory
};

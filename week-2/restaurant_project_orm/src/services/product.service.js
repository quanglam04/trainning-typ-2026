const prisma = require('../config/prisma');

const createProduct = async (data) => {
  return await prisma.product.create({
    data
  });
};

const getProducts = async (page = 1, limit = 10) => {
  const skip = (page - 1) * limit;
  const products = await prisma.product.findMany({
    skip,
    take: limit,
    include: {
      category: true
    }
  });

  const total = await prisma.product.count();

  return {
    data: products,
    meta: {
      total,
      page,
      limit,
      totalPages: Math.ceil(total / limit)
    }
  };
};

const getProductById = async (id) => {
  return await prisma.product.findUnique({
    where: { id: parseInt(id) },
    include: { category: true }
  });
};

const updateProduct = async (id, data) => {
  return await prisma.product.update({
    where: { id: parseInt(id) },
    data
  });
};

const deleteProduct = async (id) => {
  return await prisma.product.delete({
    where: { id: parseInt(id) }
  });
};

module.exports = {
  createProduct,
  getProducts,
  getProductById,
  updateProduct,
  deleteProduct
};

const prisma = require('../config/prisma');
const { redisClient } = require('../config/redis');

const DEFAULT_TTL = 60;

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
  const cacheKey = `product:${id}`;

  if (redisClient.isOpen) {
    const cacheData = await redisClient.get(cacheKey);
    if (cacheData) {
      console.log(`Cache Hit. Lấy dữ liệu product:${id} từ Redis`);
      return {
        ...JSON.parse(cacheData),
        _source: 'cache'
      };
    }
  }
  console.log('Cache Miss. Query db tìm');
  const product = await prisma.product.findUnique({
    where: { id },
    include: { category: true }
  });

  if (product && redisClient.isOpen) {
    await redisClient.setEx(cacheKey, DEFAULT_TTL, JSON.stringify(product));
  }
  return product ? { ...product, _source: 'database' } : null;
};

const updateProduct = async (id, data) => {
  const product = await prisma.product.update({
    where: { id },
    data
  })

  if (redisClient.isOpen) {
    await redisClient.del(`product:${id}`);
    console.log(`DELETE cache product:${id} do product đã update`);
  }
  return product;
};

const deleteProduct = async (id) => {
  const product = await prisma.product.delete({
    where: { id }
  })
  if (redisClient.isOpen) {
    await redisClient.del(`product:${id}`);
    console.log(`DELETE cache product:${id} do product đã bị xóa`);
  }
  return product;
};

module.exports = {
  createProduct,
  getProducts,
  getProductById,
  updateProduct,
  deleteProduct
};

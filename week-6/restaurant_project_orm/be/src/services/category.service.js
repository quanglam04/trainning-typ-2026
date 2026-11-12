const prisma = require('../config/prisma');
const { redisClient, clearCachePattern } = require('../config/redis');

const CATEGORY_TTL = 300; // 5 phút

const createCategory = async (data) => {
  const category = await prisma.category.create({
    data
  });
  await clearCachePattern('categories:*');
  await clearCachePattern('products:*');
  return category;
};

const getCategories = async (page = 1, limit = 10) => {
  const cacheKey = `categories:list:p:${page}:l:${limit}`;

  if (redisClient.isOpen) {
    try {
      const cached = await redisClient.get(cacheKey);
      if (cached) {
        console.log(`[Redis HIT] Lấy danh mục từ cache: ${cacheKey}`);
        return {
          ...JSON.parse(cached),
          _source: 'cache'
        };
      }
    } catch (e) {
      console.error('[Redis Error] Đọc cache categories:', e.message);
    }
  }

  console.log(`[Redis MISS] Query database danh mục: ${cacheKey}`);
  const skip = (page - 1) * limit;
  const categories = await prisma.category.findMany({
    skip,
    take: limit,
    include: {
      products: true
    }
  });
  
  const total = await prisma.category.count();
  
  const result = {
    data: categories,
    meta: {
      total,
      page,
      limit,
      totalPages: Math.ceil(total / limit)
    }
  };

  if (redisClient.isOpen) {
    try {
      await redisClient.setEx(cacheKey, CATEGORY_TTL, JSON.stringify(result));
    } catch (e) {
      console.error('[Redis Error] Ghi cache categories:', e.message);
    }
  }

  return { ...result, _source: 'database' };
};

const getCategoryById = async (id) => {
  return await prisma.category.findUnique({
    where: { id },
    include: { products: true }
  });
};

const updateCategory = async (id, data) => {
  const updated = await prisma.category.update({
    where: { id },
    data
  });
  await clearCachePattern('categories:*');
  await clearCachePattern('products:*');
  return updated;
};

const deleteCategory = async (id) => {
  const deleted = await prisma.category.delete({
    where: { id }
  });
  await clearCachePattern('categories:*');
  await clearCachePattern('products:*');
  return deleted;
};

module.exports = {
  createCategory,
  getCategories,
  getCategoryById,
  updateCategory,
  deleteCategory
};

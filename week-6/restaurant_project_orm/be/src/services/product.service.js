const prisma = require('../config/prisma');
const { redisClient, clearCachePattern } = require('../config/redis');

const DEFAULT_TTL = 60; // 60 giây

const createProduct = async (data) => {
  const product = await prisma.product.create({
    data
  });

  // Invalidate toàn bộ cache danh sách sản phẩm khi có món mới
  await clearCachePattern('products:*');
  return product;
};

const getProducts = async (page = 1, limit = 10, filters = {}) => {
  const catKey = filters.category_id || 'all';
  const searchKey = filters.search ? encodeURIComponent(filters.search.trim()) : '';
  const cacheKey = `products:list:p:${page}:l:${limit}:c:${catKey}:s:${searchKey}`;

  // 1. Kiểm tra cache Redis
  if (redisClient.isOpen) {
    try {
      const cached = await redisClient.get(cacheKey);
      if (cached) {
        console.log(`[Redis HIT] Lấy danh sách sản phẩm từ cache key: ${cacheKey}`);
        return {
          ...JSON.parse(cached),
          _source: 'cache'
        };
      }
    } catch (err) {
      console.error('[Redis Error] Đọc cache products thất bại:', err.message);
    }
  }

  // 2. Cache Miss: Truy vấn Database
  console.log(`[Redis MISS] Query database lấy danh sách sản phẩm: ${cacheKey}`);
  const skip = (page - 1) * limit;
  const where = {};
  if (filters.category_id && filters.category_id !== 'all') {
    where.category_id = filters.category_id;
  }
  if (filters.search) {
    where.name = { contains: filters.search, mode: 'insensitive' };
  }

  const [products, total] = await Promise.all([
    prisma.product.findMany({
      where,
      skip,
      take: limit,
      include: {
        category: true
      },
      orderBy: { created_at: 'asc' }
    }),
    prisma.product.count({ where })
  ]);

  const result = {
    data: products,
    meta: {
      total,
      page,
      limit,
      totalPages: Math.ceil(total / limit) || 1
    }
  };

  // 3. Lưu vào cache Redis (TTL: 60s)
  if (redisClient.isOpen) {
    try {
      await redisClient.setEx(cacheKey, DEFAULT_TTL, JSON.stringify(result));
    } catch (err) {
      console.error('[Redis Error] Ghi cache products thất bại:', err.message);
    }
  }

  return { ...result, _source: 'database' };
};

const getProductById = async (id) => {
  const cacheKey = `product:${id}`;

  if (redisClient.isOpen) {
    const cacheData = await redisClient.get(cacheKey);
    if (cacheData) {
      console.log(`[Redis HIT] Lấy dữ liệu product:${id} từ Redis`);
      return {
        ...JSON.parse(cacheData),
        _source: 'cache'
      };
    }
  }

  console.log(`[Redis MISS] Query database tìm product:${id}`);
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
  });

  // Invalidate cache: xóa chi tiết món & danh sách phân trang
  if (redisClient.isOpen) {
    await redisClient.del(`product:${id}`);
  }
  await clearCachePattern('products:*');
  console.log(`[Redis Invalidate] Đã xóa cache product:${id} và toàn bộ products:*`);

  return product;
};

const deleteProduct = async (id) => {
  const product = await prisma.product.delete({
    where: { id }
  });

  if (redisClient.isOpen) {
    await redisClient.del(`product:${id}`);
  }
  await clearCachePattern('products:*');
  console.log(`[Redis Invalidate] Đã xóa cache product:${id} và toàn bộ products:* khi xóa món`);

  return product;
};

module.exports = {
  createProduct,
  getProducts,
  getProductById,
  updateProduct,
  deleteProduct
};

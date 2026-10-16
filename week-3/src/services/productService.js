const prisma = require('../prisma');
const ApiError = require('../utils/apiError');

class ProductService {
  async getAllProducts(page = 1, limit = 10, search = '') {
    const pageNum = Math.max(1, parseInt(page, 10));
    const limitNum = Math.max(1, parseInt(limit, 10));
    const skip = (pageNum - 1) * limitNum;

    const where = search
      ? { name: { contains: search } }
      : {};

    const [totalElements, items] = await Promise.all([
      prisma.product.count({ where }),
      prisma.product.findMany({
        where,
        skip,
        take: limitNum,
        include: { category: { select: { id: true, name: true } } },
        orderBy: { id: 'desc' }
      })
    ]);

    return {
      items,
      pagination: {
        page: pageNum,
        limit: limitNum,
        totalElements,
        totalPages: Math.ceil(totalElements / limitNum)
      }
    };
  }

  async getProductById(id) {
    const product = await prisma.product.findUnique({
      where: { id: parseInt(id, 10) },
      include: { category: true }
    });

    if (!product) {
      throw new ApiError(404, 'PRODUCT_NOT_FOUND', `Không tìm thấy sản phẩm có ID ${id}`);
    }
    return product;
  }

  async createProduct({ name, price, stock, categoryId }) {
    if (!name || price === undefined || !categoryId) {
      throw new ApiError(400, 'VALIDATION_ERROR', 'Tên, giá và categoryId là bắt buộc');
    }

    const categoryExists = await prisma.category.findUnique({
      where: { id: parseInt(categoryId, 10) }
    });
    if (!categoryExists) {
      throw new ApiError(404, 'CATEGORY_NOT_FOUND', `Không tồn tại category với ID ${categoryId}`);
    }

    return await prisma.product.create({
      data: {
        name,
        price: parseFloat(price),
        stock: parseInt(stock, 10) || 0,
        categoryId: parseInt(categoryId, 10)
      },
      include: { category: true }
    });
  }

  async updateProduct(id, data) {
    await this.getProductById(id);

    return await prisma.product.update({
      where: { id: parseInt(id, 10) },
      data: {
        ...(data.name && { name: data.name }),
        ...(data.price !== undefined && { price: parseFloat(data.price) }),
        ...(data.stock !== undefined && { stock: parseInt(data.stock, 10) }),
        ...(data.categoryId && { categoryId: parseInt(data.categoryId, 10) })
      },
      include: { category: true }
    });
  }

  async deleteProduct(id) {
    await this.getProductById(id);
    await prisma.product.delete({
      where: { id: parseInt(id, 10) }
    });
    return { message: `Đã xóa thành công sản phẩm ID ${id}` };
  }
}

module.exports = new ProductService();
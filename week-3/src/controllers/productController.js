const productService = require('../services/productService');

class ProductController {
  async getAll(req, res, next) {
    try {
      const { page, limit, search } = req.query;
      const result = await productService.getAllProducts(page, limit, search);
      res.status(200).json({
        success: true,
        data: result.items,
        pagination: result.pagination
      });
    } catch (err) {
      next(err);
    }
  }

  async getById(req, res, next) {
    try {
      const product = await productService.getProductById(req.params.id);
      res.status(200).json({ success: true, data: product });
    } catch (err) {
      next(err);
    }
  }

  async create(req, res, next) {
    try {
      const product = await productService.createProduct(req.body);
      res.status(201).json({ success: true, data: product });
    } catch (err) {
      next(err);
    }
  }

  async update(req, res, next) {
    try {
      const product = await productService.updateProduct(req.params.id, req.body);
      res.status(200).json({ success: true, data: product });
    } catch (err) {
      next(err);
    }
  }

  async delete(req, res, next) {
    try {
      const result = await productService.deleteProduct(req.params.id);
      res.status(200).json({ success: true, ...result });
    } catch (err) {
      next(err);
    }
  }
}

module.exports = new ProductController();
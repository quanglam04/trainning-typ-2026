const productService = require('../services/product.service');

const createProduct = async (req, res, next) => {
  try {
    const { name, price, category_id, description } = req.body;
    if (!name || price === undefined || !category_id) {
      return res.status(400).json({ code: 400, message: "Name, price, and category_id are required" });
    }
    const product = await productService.createProduct({
      name,
      price,
      category_id,
      description
    });
    res.status(201).json({ data: product });
  } catch (error) {
    next(error);
  }
};

const getProducts = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;
    
    const result = await productService.getProducts(page, limit);
    res.status(200).json(result);
  } catch (error) {
    next(error);
  }
};

const getProductById = async (req, res, next) => {
  try {
    const product = await productService.getProductById(req.params.id);
    if (!product) {
      return res.status(404).json({ code: 404, message: "Product not found" });
    }
    res.status(200).json({ data: product });
  } catch (error) {
    next(error);
  }
};

const updateProduct = async (req, res, next) => {
  try {
    const product = await productService.updateProduct(req.params.id, req.body);
    res.status(200).json({ data: product });
  } catch (error) {
    if (error.code === 'P2025') {
      return res.status(404).json({ code: 404, message: "Product not found" });
    }
    next(error);
  }
};

const deleteProduct = async (req, res, next) => {
  try {
    await productService.deleteProduct(req.params.id);
    res.status(200).json({ message: "Product deleted successfully" });
  } catch (error) {
    if (error.code === 'P2025') {
      return res.status(404).json({ code: 404, message: "Product not found" });
    }
    next(error);
  }
};

module.exports = {
  createProduct,
  getProducts,
  getProductById,
  updateProduct,
  deleteProduct
};

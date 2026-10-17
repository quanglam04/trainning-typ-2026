const productService = require('../services/product.service');

const createProduct = async (req, res, next) => {
  try {
    const { name, price, category_id, description } = req.body;
    if (!name || price === undefined || !category_id) {
      return res.status(400).json({
        error: true,
        status: 400,
        code: 400,
        message: "Validation Error",
        details: "Name, price, and category_id are required"
      });
    }
    const product = await productService.createProduct({
      name,
      price: parseFloat(price),
      category_id,
      description
    });
    res.status(201).json({
      error: false,
      status: 201,
      code: 201,
      message: "Product created successfully",
      data: product
    });
  } catch (error) {
    next(error);
  }
};

const getProducts = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;

    const result = await productService.getProducts(page, limit);
    res.status(200).json({
      error: false,
      status: 200,
      code: 200,
      message: "Products retrieved successfully",
      ...result
    });
  } catch (error) {
    next(error);
  }
};

const getProductById = async (req, res, next) => {
  try {
    const product = await productService.getProductById(req.params.id);
    if (!product) {
      return res.status(404).json({
        error: true,
        status: 404,
        code: 404,
        message: "Product not found",
        details: null
      });
    }
    res.status(200).json({
      error: false,
      status: 200,
      code: 200,
      message: "Product retrieved successfully",
      data: product
    });
  } catch (error) {
    next(error);
  }
};

const updateProduct = async (req, res, next) => {
  try {
    const data = { ...req.body };
    if (data.price !== undefined) {
      data.price = parseFloat(data.price);
    }
    const product = await productService.updateProduct(req.params.id, data);
    res.status(200).json({
      error: false,
      status: 200,
      code: 200,
      message: "Product updated successfully",
      data: product
    });
  } catch (error) {
    if (error.code === 'P2025') {
      return res.status(404).json({
        error: true,
        status: 404,
        code: 404,
        message: "Product not found",
        details: null
      });
    }
    next(error);
  }
};

const deleteProduct = async (req, res, next) => {
  try {
    await productService.deleteProduct(req.params.id);
    res.status(200).json({
      error: false,
      status: 200,
      code: 200,
      message: "Product deleted successfully",
      details: null
    });
  } catch (error) {
    if (error.code === 'P2025') {
      return res.status(404).json({
        error: true,
        status: 404,
        code: 404,
        message: "Product not found",
        details: null
      });
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

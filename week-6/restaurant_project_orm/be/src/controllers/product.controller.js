const productService = require('../services/product.service');

const createProduct = async (req, res, next) => {
  try {
    const { name, price, category_id, description, quantity, status } = req.body;
    if (!name || typeof name !== 'string' || !name.trim()) {
      return res.status(400).json({
        error: true,
        code: 400,
        message: "Validation Error",
        details: "Name is required and must be a non-empty string"
      });
    }

    const numPrice = Number(price);
    if (price === undefined || isNaN(numPrice) || !isFinite(numPrice) || numPrice <= 0) {
      return res.status(400).json({
        error: true,
        code: 400,
        message: "Validation Error",
        details: "Price must be a positive number (> 0)"
      });
    }

    if (!category_id) {
      return res.status(400).json({
        error: true,
        code: 400,
        message: "Validation Error",
        details: "category_id is required"
      });
    }

    let parsedQuantity = 0;
    if (quantity !== undefined) {
      parsedQuantity = Number(quantity);
      if (!Number.isInteger(parsedQuantity) || parsedQuantity < 0) {
        return res.status(400).json({
          error: true,
          code: 400,
          message: "Validation Error",
          details: "Quantity must be an integer >= 0"
        });
      }
    }

    let finalStatus = status;
    if (finalStatus !== undefined) {
      if (!['available', 'unavailable'].includes(finalStatus)) {
        return res.status(400).json({
          error: true,
          code: 400,
          message: "Validation Error",
          details: "Status must be either 'available' or 'unavailable'"
        });
      }
    } else {
      finalStatus = parsedQuantity > 0 ? 'available' : 'unavailable';
    }

    const product = await productService.createProduct({
      name: name.trim(),
      price: numPrice,
      quantity: parsedQuantity,
      status: finalStatus,
      category_id,
      description: description ? description.trim() : null
    });

    res.status(201).json({
      error: false,
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
    const page = Math.max(1, parseInt(req.query.page, 10) || 1);
    const limit = Math.min(100, Math.max(1, parseInt(req.query.limit, 10) || 10));
    const { category_id, search } = req.query;

    const result = await productService.getProducts(page, limit, { category_id, search });
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

    if (data.name !== undefined) {
      if (typeof data.name !== 'string' || !data.name.trim()) {
        return res.status(400).json({
          error: true,
          code: 400,
          message: "Validation Error",
          details: "Name must be a non-empty string"
        });
      }
      data.name = data.name.trim();
    }

    if (data.price !== undefined) {
      const numPrice = Number(data.price);
      if (isNaN(numPrice) || !isFinite(numPrice) || numPrice <= 0) {
        return res.status(400).json({
          error: true,
          code: 400,
          message: "Validation Error",
          details: "Price must be a positive number (> 0)"
        });
      }
      data.price = numPrice;
    }

    if (data.quantity !== undefined) {
      const numQty = Number(data.quantity);
      if (!Number.isInteger(numQty) || numQty < 0) {
        return res.status(400).json({
          error: true,
          code: 400,
          message: "Validation Error",
          details: "Quantity must be an integer >= 0"
        });
      }
      data.quantity = numQty;
      if (data.status === undefined) {
        data.status = numQty > 0 ? 'available' : 'unavailable';
      }
    }

    if (data.status !== undefined) {
      if (!['available', 'unavailable'].includes(data.status)) {
        return res.status(400).json({
          error: true,
          code: 400,
          message: "Validation Error",
          details: "Status must be either 'available' or 'unavailable'"
        });
      }
    }

    const product = await productService.updateProduct(req.params.id, data);
    res.status(200).json({
      error: false,
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

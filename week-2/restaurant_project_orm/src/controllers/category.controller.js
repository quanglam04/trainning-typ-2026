const categoryService = require('../services/category.service');

const createCategory = async (req, res, next) => {
  try {
    const { name, description } = req.body;
    if (!name) return res.status(400).json({ code: 400, message: "Name is required" });
    const category = await categoryService.createCategory({ name, description });
    res.status(201).json({ data: category });
  } catch (error) {
    next(error);
  }
};

const getCategories = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;

    const result = await categoryService.getCategories(page, limit);
    res.status(200).json(result);
  } catch (error) {
    next(error);
  }
};

const getCategoryById = async (req, res, next) => {
  try {
    const category = await categoryService.getCategoryById(req.params.id);
    if (!category) {
      return res.status(404).json({ code: 404, message: "Category not found" });
    }
    res.status(200).json({ data: category });
  } catch (error) {
    next(error);
  }
};

const updateCategory = async (req, res, next) => {
  try {
    const category = await categoryService.updateCategory(req.params.id, req.body);
    res.status(200).json({ data: category });
  } catch (error) {
    if (error.code === 'P2025') {
      return res.status(404).json({ code: 404, message: "Category not found" });
    }
    next(error);
  }
};

const deleteCategory = async (req, res, next) => {
  try {
    await categoryService.deleteCategory(req.params.id);
    res.status(200).json({ message: "Category deleted successfully" });
  } catch (error) {
    if (error.code === 'P2025') {
      return res.status(404).json({ code: 404, message: "Category not found" });
    }
    next(error);
  }
};

module.exports = {
  createCategory,
  getCategories,
  getCategoryById,
  updateCategory,
  deleteCategory
};

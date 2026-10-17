const express = require('express');
const categoryController = require('../controllers/category.controller');
const { authenticateToken, authorizeRoles } = require('../middlewares/auth.middleware.js');

const router = express.Router();

router.post('/', authenticateToken, authorizeRoles('admin', 'manager'), categoryController.createCategory);
router.get('/', authenticateToken, categoryController.getCategories);

router.get('/:id', authenticateToken, categoryController.getCategoryById);
router.put('/:id', authenticateToken, authorizeRoles('admin', 'manager'), categoryController.updateCategory);
router.delete('/:id', authenticateToken, authorizeRoles('admin', 'manager'), categoryController.deleteCategory);

module.exports = router;

const express = require('express');
const productController = require('../controllers/product.controller');
const { authenticateToken, authorizeRoles } = require('../middlewares/auth.middleware.js');
const router = express.Router();

router.post('/', authenticateToken, authorizeRoles('admin', 'manager'), productController.createProduct);
router.get('/', authenticateToken, productController.getProducts);

router.get('/:id', authenticateToken, productController.getProductById);
router.put('/:id', authenticateToken, authorizeRoles('admin', 'manager'), productController.updateProduct);
router.delete('/:id', authenticateToken, authorizeRoles('admin', 'manager'), productController.deleteProduct);

module.exports = router;

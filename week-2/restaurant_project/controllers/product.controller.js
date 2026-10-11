const productService = require('../services/product.service');

const getProducts = (req, res) => {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;
    const result = productService.getProducts(page, limit);
    res.json(result);
};

const getProductById = (req, res) => {
    const id = parseInt(req.params.id);
    const product = productService.getProductById(id);
    if (product) {
        res.json(product);
    } else {
        res.status(404).json({ message: 'Product not found' });
    }
};

const createProduct = (req, res) => {
    const { name, price } = req.body;
    if (!name || price === undefined) {
        return res.status(400).json({ message: 'Name and price are required' });
    }
    
    const newProduct = productService.createProduct(req.body);
    res.status(201).json(newProduct);
};

const updateProduct = (req, res) => {
    const id = parseInt(req.params.id);
    const updatedProduct = productService.updateProduct(id, req.body);
    
    if (updatedProduct) {
        res.json(updatedProduct);
    } else {
        res.status(404).json({ message: 'Product not found' });
    }
};

const deleteProduct = (req, res) => {
    const id = parseInt(req.params.id);
    const deletedProduct = productService.deleteProduct(id);
    
    if (deletedProduct) {
        res.json({ message: 'Product deleted successfully', data: deletedProduct });
    } else {
        res.status(404).json({ message: 'Product not found' });
    }
};

module.exports = {
    getProducts,
    getProductById,
    createProduct,
    updateProduct,
    deleteProduct
};

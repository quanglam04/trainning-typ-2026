const dotenv = require('dotenv');
const express = require('express');
const categoryRoute = require('./src/routes/category.route');
const productRoute = require('./src/routes/product.route');
const userRoute = require('./src/routes/user.route');

dotenv.config();

const app = express();

app.use(express.json());

// API Routes
app.use('/api/categories', categoryRoute);
app.use('/api/products', productRoute);
app.use('/api/users', userRoute);

const errorHandler = require('./src/middlewares/error.middleware');

// Error Handling Middleware
app.use(errorHandler);

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});

require('dotenv').config();
const express = require('express');
const app = express();
const port = process.env.PORT || 3000;

const productRoutes = require('./routes/product.routes');

app.use(express.json());

app.use('/api/products', productRoutes);

app.listen(port, () => {
    console.log(`Server is running at http://localhost:${port}`);
});

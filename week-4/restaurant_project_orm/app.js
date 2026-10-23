const dotenv = require('dotenv');
const express = require('express');
const categoryRoute = require('./src/routes/category.route');
const productRoute = require('./src/routes/product.route');
const userRoute = require('./src/routes/user.route');
const authRoute = require('./src/routes/auth.route');
const reservationRoute = require('./src/routes/reservation.route');
const { startReservationConsumer } = require('./src/consumers/reservation.consumer');

dotenv.config();

const app = express();

app.use(express.json());

// API Routes
app.use('/api/categories', categoryRoute);
app.use('/api/products', productRoute);
app.use('/api/users', userRoute);
app.use('/api/auth', authRoute);
app.use('/api/reservations', reservationRoute);

const errorHandler = require('./src/middlewares/error.middleware');

// Error Handling Middleware
app.use(errorHandler);

const PORT = process.env.PORT || 3000;

app.listen(PORT, async () => {
  console.log(`Server running on http://localhost:${PORT}`);
  // Khởi động Kafka Consumer để lắng nghe các event nền
  try {
    await startReservationConsumer();
  } catch (err) {
    console.error('Không thể kết nối Kafka Consumer:', err.message);
  }
});

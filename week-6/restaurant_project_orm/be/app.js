const dotenv = require('dotenv');
const express = require('express');
const cors = require('cors');
const path = require('path');
const categoryRoute = require('./src/routes/category.route');
const productRoute = require('./src/routes/product.route');
const userRoute = require('./src/routes/user.route');
const authRoute = require('./src/routes/auth.route');
const reservationRoute = require('./src/routes/reservation.route');
const orderRoute = require('./src/routes/order.route');
const paymentRoute = require('./src/routes/payment.route');
const { startReservationConsumer } = require('./src/consumers/reservation.consumer');
const { startOrderConsumer } = require('./src/consumers/order.consumer');
const { connectRedis } = require('./src/config/redis');
const { redisClient } = require('./src/config/redis');
const prisma = require('./src/config/prisma');

dotenv.config();

const app = express();

const allowedOrigins = (process.env.CORS_ORIGIN || 'http://localhost:5173,http://localhost:3000')
  .split(',')
  .map(origin => origin.trim());

app.use(cors({
  origin(origin, callback) {
    if (!origin || allowedOrigins.includes(origin)) return callback(null, true);
    const error = new Error('Origin is not allowed by CORS');
    error.statusCode = 403;
    return callback(error);
  }
}));
app.use(express.json({ limit: '1mb' }));

app.use(express.static(path.join(__dirname, 'public')));

// API Routes
app.use('/api/categories', categoryRoute);
app.use('/api/products', productRoute);
app.use('/api/users', userRoute);
app.use('/api/auth', authRoute);
app.use('/api/reservations', reservationRoute);
app.use('/api/orders', orderRoute);
app.use('/api/payments', paymentRoute);

app.get('/api/health', async (req, res) => {
  const health = {
    database: 'down',
    redis: redisClient.isReady ? 'up' : 'down'
  };

  try {
    await prisma.$queryRaw`SELECT 1`;
    health.database = 'up';
  } catch (error) {
    health.database = 'down';
  }

  const ready = health.database === 'up';
  return res.status(ready ? 200 : 503).json({
    status: ready ? 'ok' : 'degraded',
    services: health
  });
});

const errorHandler = require('./src/middlewares/error.middleware');

// Error Handling Middleware
app.use(errorHandler);

const PORT = process.env.PORT || 3000;

const validateEnvironment = () => {
  const requiredVariables = [
    'DATABASE_URL',
    'JWT_ACCESS_SECRET',
    'JWT_REFRESH_SECRET',
    'JWT_ACCESS_EXPIRES_IN',
    'JWT_REFRESH_EXPIRES_IN'
  ];
  const missing = requiredVariables.filter(name => !process.env[name]);
  if (missing.length > 0) {
    throw new Error(`Missing required environment variables: ${missing.join(', ')}`);
  }
};

const startServer = () => {
  validateEnvironment();
  return app.listen(PORT, async () => {
  console.log(`Server running on http://localhost:${PORT}`);

  if (process.env.START_BACKGROUND_WORKERS === 'false') return;

  // Khởi động Kafka Consumers
  try {
    await startReservationConsumer();
  } catch (err) {
    console.error('Không thể kết nối Reservation Kafka Consumer:', err.message);
  }

  try {
    await startOrderConsumer();
  } catch (err) {
    console.error('Không thể kết nối Order Kafka Consumer:', err.message);
  }

  // Khởi động Redis
  try {
    await connectRedis();
  } catch (err) {
    console.error('Không thể kết nối Redis:', err.message);
  }
  });
};

if (require.main === module) {
  startServer();
}

module.exports = { app, startServer, validateEnvironment };

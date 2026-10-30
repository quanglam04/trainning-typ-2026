const express = require('express');
const { createClient } = require('redis');

const app = express();
app.use(express.json());

// 1. Khởi tạo và kết nối Redis Client
const redisClient = createClient({
  url: 'redis://localhost:6379'
});

redisClient.on('error', (err) => console.error('Redis Error:', err));
redisClient.on('connect', () => console.log('Connected to Redis successfully!'));

// 2. Giả lập Database (Products & Users) với độ trễ truy vấn 350ms
const mockProductsDB = {
  1: { id: 1, name: 'MacBook Pro M3', price: 2500, stock: 10 },
  2: { id: 2, name: 'iPhone 16 Pro Max', price: 1200, stock: 25 },
  3: { id: 3, name: 'AirPods Pro 2', price: 249, stock: 50 }
};

const mockUsersDB = {
  101: { id: 101, username: 'trunghieu', role: 'developer', email: 'hieu@ptit.edu.vn' },
  102: { id: 102, username: 'nguyenvana', role: 'member', email: 'vana@ptit.edu.vn' }
};

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

async function getProductFromDB(id) {
  await sleep(350); // Giả lập query DB mất 350ms
  return mockProductsDB[id] || null;
}

async function getUserFromDB(id) {
  await sleep(350); // Giả lập query DB mất 350ms
  return mockUsersDB[id] || null;
}

// 3. API Cache Product (Luồng: Cache Miss -> DB -> Cache Set -> Cache Hit)
app.get('/products/:id', async (req, res) => {
  const start = performance.now();
  const id = req.params.id;
  const cacheKey = `product:${id}`;

  try {
    // Kiểm tra trong Redis trước
    const cachedProduct = await redisClient.get(cacheKey);

    if (cachedProduct !== null) {
      const timeMs = (performance.now() - start).toFixed(2);
      const ttl = await redisClient.ttl(cacheKey);

      // Chống Cache Penetration: Nếu trúng key đánh dấu NULL
      if (cachedProduct === 'NULL') {
        return res.status(404).json({
          cacheStatus: 'HIT (Null Object)',
          responseTimeMs: Number(timeMs),
          ttlRemaining: ttl,
          message: 'Product not found'
        });
      }

      // CACHE HIT
      return res.json({
        cacheStatus: 'HIT',
        responseTimeMs: Number(timeMs),
        ttlRemaining: ttl,
        data: JSON.parse(cachedProduct)
      });
    }

    // CACHE MISS -> Đọc từ Database
    const product = await getProductFromDB(id);

    if (!product) {
      // Lưu trạng thái NULL trong 30s để chống Cache Penetration
      await redisClient.set(cacheKey, 'NULL', { EX: 30 });
      const timeMs = (performance.now() - start).toFixed(2);
      return res.status(404).json({
        cacheStatus: 'MISS',
        responseTimeMs: Number(timeMs),
        message: 'Product not found in DB (Cached NULL for 30s)'
      });
    }

    // Lưu vào Cache với TTL = 60s + random(0..15s) để chống Cache Avalanche
    const ttl = 60 + Math.floor(Math.random() * 15);
    await redisClient.set(cacheKey, JSON.stringify(product), { EX: ttl });

    const timeMs = (performance.now() - start).toFixed(2);
    return res.json({
      cacheStatus: 'MISS -> SET',
      responseTimeMs: Number(timeMs),
      ttlSet: ttl,
      data: product
    });
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// 4. API Cache User (Sử dụng Redis Hash hoặc String)
app.get('/users/:id', async (req, res) => {
  const start = performance.now();
  const id = req.params.id;
  const cacheKey = `user:${id}`;

  try {
    const cachedUser = await redisClient.get(cacheKey);
    if (cachedUser) {
      const timeMs = (performance.now() - start).toFixed(2);
      const ttl = await redisClient.ttl(cacheKey);
      return res.json({
        cacheStatus: 'HIT',
        responseTimeMs: Number(timeMs),
        ttlRemaining: ttl,
        data: JSON.parse(cachedUser)
      });
    }

    const user = await getUserFromDB(id);
    if (!user) {
      return res.status(404).json({ cacheStatus: 'MISS', message: 'User not found' });
    }

    await redisClient.set(cacheKey, JSON.stringify(user), { EX: 120 });
    const timeMs = (performance.now() - start).toFixed(2);
    return res.json({
      cacheStatus: 'MISS -> SET',
      responseTimeMs: Number(timeMs),
      ttlSet: 120,
      data: user
    });
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// 5. API Cập nhật Product & Xóa Cache (Cache Invalidation với DEL)
app.put('/products/:id', async (req, res) => {
  const id = req.params.id;
  const { price, stock } = req.body;
  const cacheKey = `product:${id}`;

  if (!mockProductsDB[id]) {
    return res.status(404).json({ message: 'Product not found' });
  }

  // Bước 1: Cập nhật Database
  if (price !== undefined) mockProductsDB[id].price = price;
  if (stock !== undefined) mockProductsDB[id].stock = stock;

  // Bước 2: Xóa key trong Redis (Cache Invalidation)
  await redisClient.del(cacheKey);

  return res.json({
    message: 'Database updated and Redis cache invalidated (DEL)',
    data: mockProductsDB[id]
  });
});

// 6. API Bảng xếp hạng với Redis Sorted Set (ZADD / ZREVRANGE)
const LEADERBOARD_KEY = 'leaderboard:week5';

app.post('/leaderboard', async (req, res) => {
  const { player, score } = req.body;
  if (!player || score === undefined) {
    return res.status(400).json({ message: 'player and score are required' });
  }

  // Tương đương lệnh ZADD leaderboard:week5 <score> <player>
  await redisClient.zAdd(LEADERBOARD_KEY, [{ score: Number(score), value: String(player) }]);
  return res.json({ message: `Updated score for ${player}: ${score}` });
});

app.get('/leaderboard/top/:count', async (req, res) => {
  const count = Number(req.params.count) || 5;

  // Tương đương lệnh ZREVRANGE leaderboard:week5 0 (count - 1) WITHSCORES
  const result = await redisClient.zRangeWithScores(LEADERBOARD_KEY, 0, count - 1, {
    REV: true
  });

  const ranking = result.map((item, index) => ({
    rank: index + 1,
    player: item.value,
    score: item.score
  }));

  return res.json({ leaderboard: ranking });
});

// Khởi chạy Server
const PORT = 3000;
redisClient.connect().then(() => {
  app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
  });
});
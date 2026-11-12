const { createClient } = require('redis');

const redisClient = createClient({
    url: 'redis://localhost:6379'
});

redisClient.on('error', (err) => {
    console.error('Redis Error:', err.message);
});

redisClient.on('connect', () => {
    console.log('Connected to Redis');
});

const connectRedis = async () => {
    if (!redisClient.isOpen) {
        await redisClient.connect();
    }
};

/**
 * Xóa tất cả các keys khớp với pattern (ví dụ: 'products:*', 'categories:*')
 */
const clearCachePattern = async (pattern) => {
    try {
        if (!redisClient.isOpen) return;
        const keys = await redisClient.keys(pattern);
        if (keys && keys.length > 0) {
            await redisClient.del(keys);
            console.log(`[Redis] Đã xóa ${keys.length} keys theo pattern: ${pattern}`);
        }
    } catch (err) {
        console.error(`[Redis] Lỗi khi xóa pattern ${pattern}:`, err.message);
    }
};

module.exports = { redisClient, connectRedis, clearCachePattern };
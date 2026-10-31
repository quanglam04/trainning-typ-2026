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
}

module.exports = { redisClient, connectRedis };
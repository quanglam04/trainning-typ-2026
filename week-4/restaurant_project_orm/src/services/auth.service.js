const prisma = require('../config/prisma');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

const createUser = async (data) => {

    const existUser = await prisma.user.findFirst({
        where: {
            OR: [
                { email: data.email }
            ]
        }
    })

    if (existUser) {
        const error = new Error('User already exists');
        error.statusCode = 400;
        throw error;
    }

    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(data.password, salt);

    return await prisma.user.create({
        data: {
            username: data.username,
            password_hash: hashedPassword,
            full_name: data.full_name,
            email: data.email,
            phone: data.phone,
            role: 'waiter',
            status: data.status || 'active'
        }
    });
};

const loginUser = async (data) => {
    const user = await prisma.user.findFirst({
        where: {
            email: data.email
        }
    })
    if (!user) {
        const error = new Error("Authentication Error");
        error.statusCode = 401;
        error.details = "Account not found"
        throw error;
    }
    if (user.status !== 'active') {
        const error = new Error("Authentication Error");
        error.statusCode = 403;
        error.details = "Account is inactive";
        throw error;
    }

    const isMatch = await bcrypt.compare(data.password, user.password_hash);
    if (!isMatch) {
        const error = new Error("Authentication Error");
        error.statusCode = 401;
        error.details = "Invalid credentials";
        throw error;
    }

    const accesToken = jwt.sign(
        {
            id: user.id,
            username: user.username,
            role: user.role
        },
        process.env.JWT_ACCESS_SECRET,
        {
            expiresIn: process.env.JWT_ACCESS_EXPIRES_IN
        }
    )

    const refreshToken = jwt.sign(
        {
            id: user.id,
            username: user.username,
            role: user.role
        },
        process.env.JWT_REFRESH_SECRET,
        {
            expiresIn: process.env.JWT_REFRESH_EXPIRES_IN
        }
    )

    const decoded = jwt.decode(refreshToken);
    const expiresAt = new Date(decoded.exp * 1000);

    await prisma.refreshToken.create({
        data: {
            token: refreshToken,
            user_id: user.id,
            expires_at: expiresAt
        }
    });

    return {
        user: {
            id: user.id,
            username: user.username,
            full_name: user.full_name,
            email: user.email,
            role: user.role
        },
        accessToken: accesToken,
        refreshToken
    };
};

const refreshToken = async (token) => {
    let decoded;
    try {
        decoded = jwt.verify(token, process.env.JWT_REFRESH_SECRET);
    } catch (error) {
        const err = new Error("Authentication Error");
        err.statusCode = 401;
        err.details = "Invalid or expired refresh token";
        throw err;
    }

    const storedToken = await prisma.refreshToken.findUnique({
        where: {
            token,
        },
        include: { user: true }
    });

    if (!storedToken) {
        const error = new Error("Authentication Error");
        error.statusCode = 401;
        error.details = "Refresh token revoked or not found";
        throw error;
    }

    if (new Date() > storedToken.expires_at) {
        await prisma.refreshToken.delete({ where: { token } });
        const error = new Error("Authentication Error");
        error.statusCode = 401;
        error.details = "Refresh token has expired";
        throw error;
    }

    const user = storedToken.user;
    if (user.status !== 'active') {
        const error = new Error("Authentication Error");
        error.statusCode = 403;
        error.details = "Account is inactive";
        throw error;
    }

    const newAccessToken = jwt.sign(
        {
            id: user.id,
            username: user.username,
            role: user.role
        },
        process.env.JWT_ACCESS_SECRET,
        {
            expiresIn: process.env.JWT_ACCESS_EXPIRES_IN
        }
    );

    return {
        accessToken: newAccessToken
    };
}

module.exports = {
    createUser,
    loginUser,
    refreshToken
};
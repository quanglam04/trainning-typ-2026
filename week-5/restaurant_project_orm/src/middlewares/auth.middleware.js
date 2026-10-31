const jwt = require('jsonwebtoken');

const authenticateToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];

    const token = authHeader && authHeader.split(' ')[1];
    if (!token) {
        return res.status(401).json({
            error: true,
            code: 401,
            message: "Authentication Error",
            details: "Access token is required"
        });
    };

    try {
        const decoded = jwt.verify(token, process.env.JWT_ACCESS_SECRET);
        req.user = decoded;
        next();
    } catch (error) {
        if (error.name === 'TokenExpiredError') {
            return res.status(401).json({
                error: true,
                code: 401,
                message: "Authentication Error",
                details: "Access token is expired"
            });
        } else {
            return res.status(401).json({
                error: true,
                code: 401,
                message: "Authentication Error",
                details: "Invalid access token"
            });
        }
    }
}

const authorizeRoles = (...roles) => {
    return (req, res, next) => {
        if (!req.user || !roles.includes(req.user.role)) {
            return res.status(403).json({
                error: true,
                code: 403,
                message: "Authorization Error",
                details: "You do not have permission to access this resource"
            });
        }
        next();
    };
};

module.exports = {
    authenticateToken,
    authorizeRoles
};
const authService = require('../services/auth.service');

const createUser = async (req, res, next) => {
    try {
        const { username, password, email, full_name } = req.body;
        if (!username || !password || !email || !full_name) {
            return res.status(400).json({
                error: true,
                code: 400,
                message: "Validation Error",
                details: "Username, password, email and full_name are required"
            });
        }
        if (password.length < 8) {
            return res.status(400).json({
                error: true,
                code: 400,
                message: 'Validation Error',
                details: 'Password must contain at least 8 characters'
            });
        }
        const allowedRoles = ['admin', 'manager', 'cashier', 'kitchen', 'waiter'];
        if (req.body.role && !allowedRoles.includes(req.body.role)) {
            return res.status(400).json({
                error: true,
                code: 400,
                message: 'Validation Error',
                details: `Role must be one of: ${allowedRoles.join(', ')}`
            });
        }
        const user = await authService.createUser(req.body);
        delete user.password_hash;
        res.status(201).json({
            error: false,
            code: 201,
            message: "User created successfully",
            data: user
        });
    } catch (error) {
        next(error);
    }
};

const logout = async (req, res, next) => {
    try {
        const { refreshToken } = req.body;
        if (!refreshToken) {
            return res.status(400).json({
                error: true,
                code: 400,
                message: 'Validation Error',
                details: 'Refresh token is required'
            });
        }

        await authService.logout(refreshToken);
        return res.status(200).json({
            error: false,
            code: 200,
            message: 'Logout successful'
        });
    } catch (error) {
        next(error);
    }
};

const loginUser = async (req, res, next) => {
    try {
        const { email, username, password } = req.body;
        const identifier = email || username;
        if (!identifier || !password) {
            return res.status(400).json({
                error: true,
                code: 400,
                message: "Validation Error",
                details: "Email or Username and password are required"
            });
        }
        const result = await authService.loginUser({ ...req.body, email: identifier });
        res.status(200).json({
            error: false,
            code: 200,
            message: "Login successfull",
            data: result
        })
    } catch (error) {
        next(error);
    }
}

const refreshToken = async (req, res, next) => {
    try {
        const { refreshToken } = req.body;
        if (!refreshToken) {
            return res.status(400).json({
                error: true,
                code: 400,
                message: "Validation Error",
                details: "Refresh token is required"
            });
        }

        const tokens = await authService.refreshToken(refreshToken);

        res.status(200).json({
            error: false,
            code: 200,
            message: "Token refreshed successfully",
            data: tokens
        });
    } catch (error) {
        next(error);
    }
};


module.exports = {
    createUser,
    loginUser,
    refreshToken,
    logout
}

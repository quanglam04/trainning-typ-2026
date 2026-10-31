const errorHandler = (err, req, res, next) => {
  const statusCode = err.statusCode || (res.statusCode !== 200 ? res.statusCode : 500);

  res.status(statusCode).json({
    error: true,
    code: statusCode,
    message: err.message || "Internal Server Error",
    details: err.details || null
  });
};

module.exports = errorHandler;

const errorHandler = (err, req, res, next) => {
  let statusCode = err.statusCode || err.status || (res.statusCode !== 200 ? res.statusCode : 500);

  if (err.code === 'P2025') statusCode = 404;
  if (err.code === 'P2002') statusCode = 409;
  if (err.code === 'P2003') statusCode = 409;

  const isServerError = statusCode >= 500;
  if (isServerError) {
    console.error(err);
  }

  res.status(statusCode).json({
    error: true,
    code: statusCode,
    message: isServerError ? 'Internal Server Error' : (err.message || 'Request failed'),
    details: isServerError && process.env.NODE_ENV === 'production' ? null : (err.details || null)
  });
};

module.exports = errorHandler;

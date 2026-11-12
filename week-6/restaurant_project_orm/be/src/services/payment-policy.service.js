const assertOrderPayable = (order) => {
  if (order.status !== 'served') {
    const error = new Error('Order is not ready for payment');
    error.statusCode = 409;
    error.details = `Only served orders can be paid. Current status: ${order.status}`;
    throw error;
  }

  if (order.payment) {
    const error = new Error('This order already has a payment record');
    error.statusCode = 409;
    throw error;
  }
};

module.exports = { assertOrderPayable };

const paymentService = require('../services/payment.service');

const createPayment = async (req, res, next) => {
  try {
    const { order_id, payment_method } = req.body;
    if (!order_id || !payment_method) {
      return res.status(400).json({
        error: true,
        code: 400,
        message: 'Validation Error',
        details: 'order_id and payment_method are required'
      });
    }

    const cashierId = req.user.id;
    const payment = await paymentService.createPayment(req.body, cashierId);

    return res.status(201).json({
      error: false,
      code: 201,
      message: 'Payment processed successfully',
      data: payment
    });
  } catch (error) {
    next(error);
  }
};

const getPaymentByOrderId = async (req, res, next) => {
  try {
    const payment = await paymentService.getPaymentByOrderId(req.params.orderId);
    if (!payment) {
      return res.status(404).json({
        error: true,
        code: 404,
        message: 'Payment not found for this order',
        details: null
      });
    }
    return res.status(200).json({
      error: false,
      code: 200,
      message: 'Payment retrieved successfully',
      data: payment
    });
  } catch (error) {
    next(error);
  }
};

module.exports = {
  createPayment,
  getPaymentByOrderId
};


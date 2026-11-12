const test = require('node:test');
const assert = require('node:assert/strict');
const { assertOrderTransitionAllowed } = require('../src/services/order-status.service');
const { assertOrderPayable } = require('../src/services/payment-policy.service');

// 1. Kiểm tra case-insensitivity cho Role trong quy trình bếp & đơn hàng
test('role normalization: WAITER (uppercase) can transition ready to served', () => {
  assert.doesNotThrow(() => assertOrderTransitionAllowed('ready', 'served', 'WAITER'));
});

test('role normalization: Kitchen (capitalized) can transition pending to cooking', () => {
  assert.doesNotThrow(() => assertOrderTransitionAllowed('pending', 'cooking', 'Kitchen'));
});

test('role normalization: ADMIN can transition any allowed step', () => {
  assert.doesNotThrow(() => assertOrderTransitionAllowed('cooking', 'ready', 'ADMIN'));
});

// 2. Kiểm tra chính sách thanh toán
test('payment policy: served order without payment is allowed', () => {
  assert.doesNotThrow(() => assertOrderPayable({ status: 'served', payment: null }));
});

test('payment policy: pending or cooking orders reject payment with 409', () => {
  assert.throws(
    () => assertOrderPayable({ status: 'cooking', payment: null }),
    err => err.statusCode === 409
  );
});

test('payment policy: order already paid rejects duplicate payment with 409', () => {
  assert.throws(
    () => assertOrderPayable({ status: 'served', payment: { id: 'pmt-1' } }),
    err => err.statusCode === 409
  );
});

// 3. Kiểm tra logic validation sản phẩm
test('product validation logic: price must be a finite positive number', () => {
  const validatePrice = (price) => {
    const num = Number(price);
    return price !== undefined && !isNaN(num) && isFinite(num) && num > 0;
  };

  assert.equal(validatePrice(85000), true);
  assert.equal(validatePrice('85000'), true);
  assert.equal(validatePrice(0), false);
  assert.equal(validatePrice(-1000), false);
  assert.equal(validatePrice(NaN), false);
  assert.equal(validatePrice('abc'), false);
  assert.equal(validatePrice(Infinity), false);
});

test('product validation logic: quantity must be an integer >= 0', () => {
  const validateQuantity = (qty) => {
    const num = Number(qty);
    return Number.isInteger(num) && num >= 0;
  };

  assert.equal(validateQuantity(0), true);
  assert.equal(validateQuantity(50), true);
  assert.equal(validateQuantity('25'), true);
  assert.equal(validateQuantity(-5), false);
  assert.equal(validateQuantity(2.5), false);
  assert.equal(validateQuantity('abc'), false);
});

test('order item validation logic: quantity must be an integer > 0', () => {
  const validateOrderItemQty = (qty) => {
    const num = Number(qty);
    return Number.isInteger(num) && num > 0;
  };

  assert.equal(validateOrderItemQty(1), true);
  assert.equal(validateOrderItemQty('3'), true);
  assert.equal(validateOrderItemQty(0), false);
  assert.equal(validateOrderItemQty(-1), false);
  assert.equal(validateOrderItemQty(1.5), false);
  assert.equal(validateOrderItemQty(null), false);
});

// 4. Kiểm tra logic trích xuất email hóa đơn fallback từ note
test('email extraction fallback: extracts valid email from order note', () => {
  const extractEmailFromNote = (note) => {
    if (!note) return null;
    const match = note.match(/[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/);
    return match ? match[0] : null;
  };

  assert.equal(extractEmailFromNote('Khách: customer@gmail.com'), 'customer@gmail.com');
  assert.equal(extractEmailFromNote('Giao cho test.user+order@domain.co'), 'test.user+order@domain.co');
  assert.equal(extractEmailFromNote('Bàn 4 không hành'), null);
  assert.equal(extractEmailFromNote(null), null);
});


const test = require('node:test');
const assert = require('node:assert/strict');
const { assertOrderPayable } = require('../src/services/payment-policy.service');

test('served order without payment is payable', () => {
  assert.doesNotThrow(() => assertOrderPayable({ status: 'served', payment: null }));
});

for (const status of ['pending', 'confirmed', 'cooking', 'ready', 'cancelled', 'completed']) {
  test(`${status} order is not payable`, () => {
    assert.throws(
      () => assertOrderPayable({ status, payment: null }),
      error => error.statusCode === 409
    );
  });
}

test('order with an existing payment record cannot be paid twice', () => {
  assert.throws(
    () => assertOrderPayable({ status: 'served', payment: { status: 'failed' } }),
    error => error.statusCode === 409
  );
});

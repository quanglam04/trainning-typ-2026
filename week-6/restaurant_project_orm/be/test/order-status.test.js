const test = require('node:test');
const assert = require('node:assert/strict');
const {
  STATUS_UPDATE_TARGETS,
  assertOrderTransitionAllowed
} = require('../src/services/order-status.service');

test('kitchen can move a pending order to cooking', () => {
  assert.doesNotThrow(() => assertOrderTransitionAllowed('pending', 'cooking', 'kitchen'));
});

test('manager can supervise the cooking workflow', () => {
  assert.doesNotThrow(() => assertOrderTransitionAllowed('cooking', 'ready', 'manager'));
});

test('waiter cannot skip from pending to served', () => {
  assert.throws(
    () => assertOrderTransitionAllowed('pending', 'served', 'waiter'),
    error => error.statusCode === 409
  );
});

test('kitchen cannot mark an order as served', () => {
  assert.throws(
    () => assertOrderTransitionAllowed('ready', 'served', 'kitchen'),
    error => error.statusCode === 403
  );
});

test('completed is reserved for the payment flow', () => {
  assert.equal(STATUS_UPDATE_TARGETS.includes('completed'), false);
  assert.throws(
    () => assertOrderTransitionAllowed('served', 'completed', 'admin'),
    error => error.statusCode === 409
  );
});

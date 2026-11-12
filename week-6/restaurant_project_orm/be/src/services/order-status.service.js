const TRANSITIONS = Object.freeze({
  pending: Object.freeze({
    confirmed: ['waiter', 'manager', 'admin'],
    cooking: ['kitchen', 'manager', 'admin'],
    cancelled: ['manager', 'admin']
  }),
  confirmed: Object.freeze({
    cooking: ['kitchen', 'manager', 'admin'],
    cancelled: ['manager', 'admin']
  }),
  cooking: Object.freeze({
    ready: ['kitchen', 'manager', 'admin']
  }),
  ready: Object.freeze({
    served: ['waiter', 'manager', 'admin']
  })
});

const STATUS_UPDATE_TARGETS = Object.freeze([
  'confirmed',
  'cooking',
  'ready',
  'served',
  'cancelled'
]);

const assertOrderTransitionAllowed = (currentStatus, nextStatus, role) => {
  const allowedRoles = TRANSITIONS[currentStatus]?.[nextStatus];

  if (!allowedRoles) {
    const error = new Error('Invalid Order Status');
    error.statusCode = 409;
    error.details = `Cannot change order status from ${currentStatus} to ${nextStatus}`;
    throw error;
  }

  const userRole = (role || '').toLowerCase();
  const normalizedAllowedRoles = allowedRoles.map(r => r.toLowerCase());

  if (!normalizedAllowedRoles.includes(userRole)) {
    const error = new Error('Authorization Error');
    error.statusCode = 403;
    error.details = `Role ${role} cannot change order status from ${currentStatus} to ${nextStatus}`;
    throw error;
  }
};

module.exports = {
  STATUS_UPDATE_TARGETS,
  assertOrderTransitionAllowed
};

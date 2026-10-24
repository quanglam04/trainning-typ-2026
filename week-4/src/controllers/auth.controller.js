const { sendWelcomeEmailEvent } = require('../queues/email.producer');

async function register(req, res) {
  try {
    const { email, name } = req.body;
    if (!email || !name) {
      return res.status(400).json({ error: 'Missing email or name' });
    }

    const newUser = { id: Date.now().toString(), email, name };

    await sendWelcomeEmailEvent({
      eventId: `EVT-${Date.now()}`,
      userId: newUser.id,
      email: newUser.email,
      name: newUser.name,
    });

    return res.status(201).json({
      success: true,
      message: 'Đăng ký thành công! Email xác nhận đang được xử lý ngầm.',
      data: newUser,
    });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
}

module.exports = { register };
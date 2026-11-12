const dotenv = require('dotenv');
const bcrypt = require('bcrypt');
const { PrismaClient } = require('@prisma/client');

dotenv.config();

const prisma = new PrismaClient();

const main = async () => {
  const password = process.env.ADMIN_PASSWORD;
  if (!password || password.length < 12) {
    throw new Error('ADMIN_PASSWORD must contain at least 12 characters');
  }

  const email = process.env.ADMIN_EMAIL || 'admin@restaurant.local';
  const username = process.env.ADMIN_USERNAME || 'admin';
  const fullName = process.env.ADMIN_FULL_NAME || 'System Administrator';
  const passwordHash = await bcrypt.hash(password, 12);

  await prisma.user.upsert({
    where: { email },
    update: {
      username,
      full_name: fullName,
      password_hash: passwordHash,
      role: 'admin',
      status: 'active'
    },
    create: {
      email,
      username,
      full_name: fullName,
      password_hash: passwordHash,
      role: 'admin',
      status: 'active'
    }
  });

  console.log(`Admin account is ready: ${email}`);
};

main()
  .catch(error => {
    console.error(error.message);
    process.exitCode = 1;
  })
  .finally(async () => {
    await prisma.$disconnect();
  });

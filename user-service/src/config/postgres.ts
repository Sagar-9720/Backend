// postgres.js (updated)
import { Sequelize } from 'sequelize';

const {
  PG_HOST,
  PG_PORT,
  PG_USER,
  PG_PASSWORD,
  PG_DATABASE,
} = process.env;

if (!PG_HOST || !PG_PORT || !PG_USER || !PG_PASSWORD || !PG_DATABASE) {
  throw new Error('PostgreSQL environment variables are not set properly.');
}

const sequelize = new Sequelize(PG_DATABASE, PG_USER, PG_PASSWORD, {
  host: PG_HOST,
  port: Number(PG_PORT),
  dialect: 'postgres',
  logging: false,
});

export const connectPostgres = async () => {
  try {
    await sequelize.authenticate();
    console.log('✅ PostgreSQL connection established successfully.');
  } catch (error) {
    console.error('❌ Unable to connect to PostgreSQL:', error);
    process.exit(1);
  }
};

export default sequelize;

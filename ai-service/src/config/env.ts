import dotenv from 'dotenv';

dotenv.config();

export type Env = {
  port: number;
  tripServiceBaseUrl: string;
  journalServiceBaseUrl: string;
  userServiceBaseUrl: string;
  redisUrl?: string;
  geminiApiKey?: string;
  geminiModel?: string;
};

export const env: Env = {
  port: parseInt(process.env.PORT || '8086', 10),
  tripServiceBaseUrl: process.env.TRIP_SERVICE_BASE_URL || 'http://trip-service:8082',
  journalServiceBaseUrl: process.env.JOURNAL_SERVICE_BASE_URL || 'http://journal-service:8081',
  userServiceBaseUrl: process.env.USER_SERVICE_BASE_URL || 'http://user-service:5000',
  redisUrl: process.env.REDIS_URL,
  geminiApiKey: process.env.GEMINI_API_KEY,
  geminiModel: process.env.GEMINI_MODEL
};

import {createClient, RedisClientType} from 'redis';
import { logger } from '../config/logger';

export type Cache = {
  get(key: string): Promise<string | null>;
  set(key: string, value: string, ttlSeconds: number): Promise<void>;
};

export async function createCache(redisUrl?: string): Promise<Cache | null> {
  if (!redisUrl) {
    logger.warn('Redis cache disabled (REDIS_URL not set)');
    return null;
  }

  const client: RedisClientType = createClient({ url: redisUrl });
  client.on('error', (err) => logger.error('[redis] error', undefined, err));

  await client.connect();
  logger.info('✅ Connected to Redis');

  return {
    get: (key) => client.get(key),
    set: async (key, value, ttlSeconds) => {
      await client.set(key, value, { EX: ttlSeconds });
    }
  };
}

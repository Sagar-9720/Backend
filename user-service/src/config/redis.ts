import {createClient, RedisClientType} from "redis";
import {updateRedisMetrics, RedisMetrics} from "../middleware/metrics";

// === Redis Config from ENV ===
const redisPassword = process.env.REDIS_PASSWORD || "redispass";
const redisDatabase = parseInt(process.env.REDIS_DATABASE || "0", 10);
const redisTimeout = parseInt(process.env.REDIS_TIMEOUT || "2000", 10);

// === Create Redis client with direct connection ===
const redisClient: RedisClientType = createClient({
    password: redisPassword,   // ✅ use password only
    socket: {
        host: process.env.REDIS_HOST || "redis-master",
        port: parseInt(process.env.REDIS_PORT || "6379", 10),
        reconnectStrategy: (retries: number) => Math.min(retries * 100, 3000),
        connectTimeout: redisTimeout,
    },
    database: redisDatabase,
});


// === Metrics Tracking ===
let redisMetrics: RedisMetrics | null = null;

redisClient.on("error", (err) => {
    console.error("❌ Redis Client Error:", err);
});

redisClient.on("connect", () => {
    console.info(`✅ Connected to Redis`);
    redisMetrics = updateRedisMetrics(redisClient);
});

redisClient.on("end", () => {
    console.info("⚠️ Redis connection closed");
    updateRedisMetrics(null);
});

// === Connection Retry Logic ===
const MAX_RETRY_ATTEMPTS = 5;
let connectionAttempts = 0;

const connectWithRetry = async (): Promise<void> => {
    try {
        await redisClient.connect();
        connectionAttempts = 0;
    } catch (err) {
        connectionAttempts++;
        console.error(`❌ Redis connection attempt ${connectionAttempts} failed`);

        if (connectionAttempts < MAX_RETRY_ATTEMPTS) {
            const retryDelay = Math.min(1000 * Math.pow(2, connectionAttempts), 10000);
            console.info(`⏳ Retrying in ${retryDelay}ms...`);
            setTimeout(connectWithRetry, retryDelay);
        } else {
            console.error(
                `🚨 Max attempts (${MAX_RETRY_ATTEMPTS}) reached. Giving up.`
            );
        }
    }
};

// Start connection attempt
connectWithRetry();

// === Instrumented Client (with metrics) ===
const instrumentedRedisClient = {
    ...redisClient,
    get: async (key: string) => {
        const tracker = redisMetrics?.trackOperation("get");
        try {
            const result = await redisClient.get(key);
            tracker?.success();
            return result;
        } catch (error) {
            tracker?.error();
            throw error;
        }
    },
    set: async (key: string, value: string, options?: any) => {
        const tracker = redisMetrics?.trackOperation("set");
        try {
            const result = await redisClient.set(key, value, options);
            tracker?.success();
            return result;
        } catch (error) {
            tracker?.error();
            throw error;
        }
    },
    incr: async (key: string) => {
        const tracker = redisMetrics?.trackOperation("incr");
        try {
            const result = await redisClient.incr(key);
            tracker?.success();
            return result;
        } catch (error) {
            tracker?.error();
            throw error;
        }
    },
    del: async (key: string) => {
        const tracker = redisMetrics?.trackOperation("del");
        try {
            const result = await redisClient.del(key);
            tracker?.success();
            return result;
        } catch (error) {
            tracker?.error();
            throw error;
        }
    },
};

export default instrumentedRedisClient;

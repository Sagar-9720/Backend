import * as promClient from 'prom-client';
import { Request, Response, NextFunction } from 'express';

// Create a Registry to register the metrics
const register = new promClient.Registry();

// Add a default label to all metrics
promClient.collectDefaultMetrics({
  register,
  prefix: 'userservice_',
  gcDurationBuckets: [0.001, 0.01, 0.1, 1, 2, 5],
  labels: { service: 'user-service' }
});

// Custom metrics
const httpRequestDurationMicroseconds = new promClient.Histogram({
  name: 'userservice_http_request_duration_seconds',
  help: 'Duration of HTTP requests in seconds',
  labelNames: ['method', 'route', 'status_code'],
  buckets: [0.01, 0.05, 0.1, 0.5, 1, 2, 5, 10]
});

const httpRequestCounter = new promClient.Counter({
  name: 'userservice_http_requests_total',
  help: 'Total number of HTTP requests',
  labelNames: ['method', 'route', 'status_code']
});

// User-specific metrics
const userCreationCounter = new promClient.Counter({
  name: 'userservice_users_created_total',
  help: 'Total number of users created'
});

const userUpdatesCounter = new promClient.Counter({
  name: 'userservice_users_updated_total',
  help: 'Total number of user profile updates'
});

const activeUsersGauge = new promClient.Gauge({
  name: 'userservice_active_users',
  help: 'Number of users currently active'
});

// MongoDB connection pool metrics
const mongoDbConnectionPoolGauge = new promClient.Gauge({
  name: 'userservice_mongodb_connections',
  help: 'MongoDB connection pool metrics',
  labelNames: ['state']
});

// Redis metrics
const redisConnectionGauge = new promClient.Gauge({
  name: 'userservice_redis_connection_status',
  help: 'Redis connection status (1=connected, 0=disconnected)',
});

const redisOperationCounter = new promClient.Counter({
  name: 'userservice_redis_operations_total',
  help: 'Total number of Redis operations',
  labelNames: ['operation', 'status']
});

const redisOperationDuration = new promClient.Histogram({
  name: 'userservice_redis_operation_duration_seconds',
  help: 'Duration of Redis operations in seconds',
  labelNames: ['operation'],
  buckets: [0.001, 0.005, 0.01, 0.05, 0.1, 0.5]
});

// Kafka metrics
const kafkaConnectionGauge = new promClient.Gauge({
  name: 'userservice_kafka_connection_status',
  help: 'Kafka connection status (1=connected, 0=disconnected)',
});

const kafkaMessageCounter = new promClient.Counter({
  name: 'userservice_kafka_messages_total',
  help: 'Total number of Kafka messages',
  labelNames: ['topic', 'operation', 'status']
});

const kafkaBatchSizeGauge = new promClient.Gauge({
  name: 'userservice_kafka_current_batch_size',
  help: 'Current size of Kafka message batch',
  labelNames: ['topic']
});

// View metrics
const viewCountGauge = new promClient.Gauge({
  name: 'userservice_view_count',
  help: 'Current view count for content',
  labelNames: ['content_type', 'content_id']
});

const viewIncrementCounter = new promClient.Counter({
  name: 'userservice_view_increments_total',
  help: 'Total number of view increments',
  labelNames: ['content_type']
});

const viewBatchProcessCounter = new promClient.Counter({
  name: 'userservice_view_batch_process_total',
  help: 'Total number of view batch processes',
  labelNames: ['status']
});

// Register custom metrics
register.registerMetric(httpRequestDurationMicroseconds);
register.registerMetric(httpRequestCounter);
register.registerMetric(userCreationCounter);
register.registerMetric(userUpdatesCounter);
register.registerMetric(activeUsersGauge);
register.registerMetric(mongoDbConnectionPoolGauge);
register.registerMetric(redisConnectionGauge);
register.registerMetric(redisOperationCounter);
register.registerMetric(redisOperationDuration);
register.registerMetric(kafkaConnectionGauge);
register.registerMetric(kafkaMessageCounter);
register.registerMetric(kafkaBatchSizeGauge);
register.registerMetric(viewCountGauge);
register.registerMetric(viewIncrementCounter);
register.registerMetric(viewBatchProcessCounter);

// Type definitions
export interface RedisMetricsTracker {
  success: () => void;
  error: () => void;
}

export interface RedisMetrics {
  trackOperation: (operation: string) => RedisMetricsTracker;
}

export interface KafkaMessageTracker {
  success: () => void;
  error: () => void;
}

export interface KafkaMetrics {
  trackMessage: (topic: string, operation: string) => KafkaMessageTracker;
  updateBatchSize: (topic: string, size: number) => void;
}

// Middleware function to track HTTP requests
const metricsMiddleware = (req: Request, res: Response, next: NextFunction): void => {
  const start = process.hrtime();

  // Record request
  res.on('finish', () => {
    const route = req.route ? req.route.path : req.path;
    const method = req.method;
    const statusCode = res.statusCode;

    // Observe HTTP request duration
    const duration = process.hrtime(start);
    const durationInSeconds = duration[0] + duration[1] / 1e9;

    // Record metrics
    httpRequestDurationMicroseconds
      .labels(method, route, statusCode.toString())
      .observe(durationInSeconds);

    httpRequestCounter
      .labels(method, route, statusCode.toString())
      .inc();
  });

  next();
};

// Metrics endpoint to expose Prometheus metrics
const getMetrics = async (req: Request, res: Response): Promise<void> => {
  try {
    res.set('Content-Type', register.contentType);
    res.end(await register.metrics());
  } catch (err) {
    res.status(500).end(err);
  }
};

// Update MongoDB connection metrics
const updateMongoMetrics = (mongooseConnection: any): void => {
  if (mongooseConnection && mongooseConnection.connection) {
    const connectionState = mongooseConnection.connection.readyState;
    // 0 = disconnected, 1 = connected, 2 = connecting, 3 = disconnecting
    mongoDbConnectionPoolGauge.set({ state: 'connected' }, connectionState === 1 ? 1 : 0);
    mongoDbConnectionPoolGauge.set({ state: 'connecting' }, connectionState === 2 ? 1 : 0);
    mongoDbConnectionPoolGauge.set({ state: 'disconnecting' }, connectionState === 3 ? 1 : 0);
    mongoDbConnectionPoolGauge.set({ state: 'disconnected' }, connectionState === 0 ? 1 : 0);
  }
};

// Update Redis metrics
const updateRedisMetrics = (redisClient: any): RedisMetrics => {
  // Set connection status
  const isConnected = redisClient?.isOpen || false;
  redisConnectionGauge.set(isConnected ? 1 : 0);

  return {
    trackOperation: (operation: string): RedisMetricsTracker => {
      const timer = redisOperationDuration.startTimer({ operation });
      return {
        success: (): void => {
          redisOperationCounter.inc({ operation, status: 'success' }, 1);
          timer();
        },
        error: (): void => {
          redisOperationCounter.inc({ operation, status: 'error' }, 1);
          timer();
        }
      };
    }
  };
};

// Update Kafka metrics
const updateKafkaMetrics = (producer: any): KafkaMetrics => {
  // Set connection status
  const isConnected = producer?.isConnected || false;
  kafkaConnectionGauge.set(isConnected ? 1 : 0);

  return {
    trackMessage: (topic: string, operation: string): KafkaMessageTracker => {
      return {
        success: (): void => {
          kafkaMessageCounter.inc({ topic, operation, status: 'success' }, 1);
        },
        error: (): void => {
          kafkaMessageCounter.inc({ topic, operation, status: 'error' }, 1);
        }
      };
    },
    updateBatchSize: (topic: string, size: number): void => {
      kafkaBatchSizeGauge.set({ topic }, size);
    }
  };
};

// Update view metrics
const updateViewMetrics = (contentType: string, contentId: string, count: number): void => {
  viewCountGauge.set({ content_type: contentType, content_id: contentId }, count);
};

const incrementViewCounter = (contentType: string): void => {
  viewIncrementCounter.inc({ content_type: contentType }, 1);
};

const trackViewBatchProcess = (status: string): void => {
  viewBatchProcessCounter.inc({ status }, 1);
};

export {
  register,
  metricsMiddleware,
  getMetrics,
  updateMongoMetrics,
  updateRedisMetrics,
  updateKafkaMetrics,
  updateViewMetrics,
  incrementViewCounter,
  trackViewBatchProcess,
  userCreationCounter,
  userUpdatesCounter,
  activeUsersGauge
};

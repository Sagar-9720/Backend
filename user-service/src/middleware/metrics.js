const promClient = require('prom-client');
const collectDefaultMetrics = promClient.collectDefaultMetrics;

// Create a Registry to register the metrics
const register = new promClient.Registry();

// Add a default label to all metrics
collectDefaultMetrics({
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

// Register custom metrics
register.registerMetric(httpRequestDurationMicroseconds);
register.registerMetric(httpRequestCounter);
register.registerMetric(userCreationCounter);
register.registerMetric(userUpdatesCounter);
register.registerMetric(activeUsersGauge);
register.registerMetric(mongoDbConnectionPoolGauge);

// Middleware function to track HTTP requests
const metricsMiddleware = (req, res, next) => {
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
      .labels(method, route, statusCode)
      .observe(durationInSeconds);

    httpRequestCounter
      .labels(method, route, statusCode)
      .inc();
  });

  next();
};

// Metrics endpoint to expose Prometheus metrics
const getMetrics = async (req, res) => {
  try {
    res.set('Content-Type', register.contentType);
    res.end(await register.metrics());
  } catch (err) {
    res.status(500).end(err);
  }
};

// Update MongoDB connection metrics
const updateMongoMetrics = (mongooseConnection) => {
  if (mongooseConnection && mongooseConnection.connection) {
    const connectionState = mongooseConnection.connection.readyState;
    // 0 = disconnected, 1 = connected, 2 = connecting, 3 = disconnecting
    mongoDbConnectionPoolGauge.set({ state: 'connected' }, connectionState === 1 ? 1 : 0);
    mongoDbConnectionPoolGauge.set({ state: 'connecting' }, connectionState === 2 ? 1 : 0);
    mongoDbConnectionPoolGauge.set({ state: 'disconnecting' }, connectionState === 3 ? 1 : 0);
    mongoDbConnectionPoolGauge.set({ state: 'disconnected' }, connectionState === 0 ? 1 : 0);

    // Set number of available connections if available
    if (mongooseConnection.connection.db && mongooseConnection.connection.db.serverConfig) {
      const pool = mongooseConnection.connection.db.serverConfig.s.pool;
      if (pool) {
        mongoDbConnectionPoolGauge.set({ state: 'available' }, pool.available || 0);
        mongoDbConnectionPoolGauge.set({ state: 'pending' }, pool.pending || 0);
      }
    }
  }
};

// User service business metrics
const incrementUserCreation = () => {
  userCreationCounter.inc();
};

const incrementUserUpdate = () => {
  userUpdatesCounter.inc();
};

const setActiveUsers = (count) => {
  activeUsersGauge.set(count);
};

module.exports = {
  register,
  metricsMiddleware,
  getMetrics,
  updateMongoMetrics,
  incrementUserCreation,
  incrementUserUpdate,
  setActiveUsers
};

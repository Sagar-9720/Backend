import express from 'express';
import cors from 'cors';
import { connectPostgres } from './config/postgres';
import { connectMongo } from './config/mongo';
import bodyParser from 'body-parser';
import eurekaClient from './config/eureka';
import promClient from 'prom-client';

// Only import relevant routes
import savedTripRoutes from './routes/saved_trip.routes';
import commentRoutes from './routes/comment.routes';
import likeRoutes from './routes/like.routes';

// Only import relevant models for database sync
import SavedTrip from './models/saved_trip.model';
import Like from './models/like.model';

const app = express();
const PORT = process.env.PORT || 5000;

// Prometheus metrics setup
promClient.collectDefaultMetrics();
app.get('/metrics', async (req, res) => {
  res.set('Content-Type', promClient.register.contentType);
  res.end(await promClient.register.metrics());
});

app.use(cors());
app.use(express.json());

// Health check endpoint
app.get('/health', (req, res) => {
  res.status(200).json({ 
    status: 'healthy', 
    service: 'user-service',
    timestamp: new Date().toISOString()
  });
});

// Register only relevant routes
app.use('/api/users/saved-trips', savedTripRoutes);
app.use('/api/users/comments', commentRoutes);
app.use('/api/users/like', likeRoutes);

(async () => {
  try {
    await connectPostgres();
    await connectMongo();

    // Only sync models relevant to comments, likes, and saved trips
    await SavedTrip.sync();
    await Like.sync();

    app.listen(PORT, () => {
      console.log(`User Service is running on port ${PORT}`);
      console.log(`Health check available at http://localhost:${PORT}/health`);
      // Start Eureka client registration
      eurekaClient.start((error: any) => {
        if (error) {
          console.error('Eureka registration failed:', error);
        } else {
          console.log('User Service registered with Eureka');
        }
      });
    });
  } catch (error) {
    console.error('Failed to start server:', error);
    process.exit(1);
  }
})();

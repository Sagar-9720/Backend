import express from 'express';
import cors from 'cors';
import promClient from 'prom-client';
import loadConfig from './config/loadConfig';
import {startConfigBusListener} from './services/configBusListener';
import axios from 'axios';
import NodeCache from 'node-cache';
import axiosRetry from 'axios-retry';

const startServer = async () => {
    try {
        // Step 1: Load configuration from Spring Cloud Config Server
        const config = await loadConfig();

        // Step 2: Merge config into process.env
        Object.entries(config).forEach(([key, value]) => {
            process.env[key] = String(value);
        });

        // Step 3: Dynamically import DB and other dependencies (now env is ready)
        const {connectPostgres} = await import('./config/postgres');
        const {connectMongo} = await import('./config/mongo');
        const {startEurekaClient, getAuthServiceUrl} = await import('./config/eureka');

        // Routes and models
        const savedTripRoutes = (await import('./routes/saved_trip.routes')).default;
        const commentRoutes = (await import('./routes/comment.routes')).default;
        const likeRoutes = (await import('./routes/like.routes')).default;
        const SavedTrip = (await import('./models/saved_trip.model')).default;
        const Like = (await import('./models/like.model')).default;

        // Step 4: Initialize Express app
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

        // Register routes
        app.use('/api/users/saved-trips', savedTripRoutes);
        app.use('/api/users/comments', commentRoutes);
        app.use('/api/users/like', likeRoutes);

        // Token validation cache (2 min TTL)
        const tokenCache = new NodeCache({stdTTL: 120, checkperiod: 150});

        // Token validation response type
        interface TokenValidationData {
            valid: boolean;
            userId: string;
            username: string;
            email: string;
            role: string;
            message?: string;

            [key: string]: any;
        }

        // --- Bearer Token Validation Middleware ---
        app.use(async (req, res, next) => {
            if (req.path === '/health' || req.path === '/metrics') return next();
            const authHeader = req.headers['authorization'];
            if (!authHeader || !authHeader.startsWith('Bearer ')) {
                return res.status(401).json({message: 'Missing or invalid Authorization header'});
            }
            // Check cache first
            const cached = tokenCache.get(authHeader) as TokenValidationData | undefined;
            if (cached && cached.valid) {
                (req as any).user = cached;
                return next();
            }
            try {
                // Discover auth-service from Eureka
                const authServiceUrl = getAuthServiceUrl();
                if (!authServiceUrl) {
                    return res.status(503).json({message: 'Auth service unavailable'});
                }
                console.log('Validating token with:', authServiceUrl);
                console.log('Authorization header:', authHeader);
                const response = await axios.post(
                    authServiceUrl,
                    {},
                    {headers: {Authorization: authHeader}}
                );
                console.log('Validation response:', response.data);
                const data = response.data.data as TokenValidationData;
                if (!data?.valid) {
                    return res.status(401).json({message: data?.message || 'Invalid token'});
                }
                // Cache the result
                tokenCache.set(authHeader, data);
                (req as any).user = data;
                next();
            } catch (err) {
                // Fallback logic: return a custom response if all retries fail
                return res.status(401).json({message: 'Token validation failed (with retry/fallback)'});
            }
        });

        // Add axios-retry for Feign-like retry logic
        axiosRetry(axios, {retries: 3, retryDelay: axiosRetry.exponentialDelay});

        // Step 5: Connect to databases
        await connectPostgres();
        await connectMongo();

        // Step 6: Sync models
        await SavedTrip.sync();
        await Like.sync();

        // Step 7: Start server
        await startEurekaClient();
        app.listen(PORT, () => {
            console.log(`🚀 User Service is running on port ${PORT}`);
            console.log(`✅ Health check available at http://localhost:${PORT}/health`);
        });

        // Start Kafka config bus listener (non-blocking)
        startConfigBusListener().catch(err => console.error('[ConfigBus] Failed to start:', err));

    } catch (err) {
        console.error('❌ Failed to start User Service:', err);
        process.exit(1);
    }
};

// Start application
startServer();

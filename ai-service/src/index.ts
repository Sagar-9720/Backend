import express from 'express';
import cors from 'cors';
import {env} from './config/env';
import {createCache} from './services/cache';
import {trendingTrips, trendingJournals, trendingDestinations} from './services/trendingAggregations';
import {buildTools} from './mcp/tools';
import {chatWithTools} from './services/geminiChatService';
import {McpToolServer} from './mcp/server';
import { logger } from './config/logger';
import { startEurekaClient } from './config/eureka';

function getUserInfoHeader(req: express.Request): string | undefined {
  return req.header('X-UserInfo') || undefined;
}

async function main() {
  const app = express();
  app.disable('x-powered-by');
  app.use(cors());
  app.use(express.json());

  // simple request logger (keep it lightweight)
  app.use((req, res, next) => {
    const start = Date.now();
    const requestId = req.header('X-Request-Id') || undefined;
    res.on('finish', () => {
      logger.info('http_request', {
        requestId,
        method: req.method,
        path: req.originalUrl || req.path,
        status: res.statusCode,
        durationMs: Date.now() - start
      });
    });
    next();
  });

  const cache = await createCache(env.redisUrl);
  const tools = buildTools(env);
  const mcpServer = new McpToolServer(tools);

  logger.info('ai-service config loaded', {
    port: env.port,
    tripServiceBaseUrl: env.tripServiceBaseUrl,
    journalServiceBaseUrl: env.journalServiceBaseUrl,
    userServiceBaseUrl: env.userServiceBaseUrl,
    redisEnabled: Boolean(env.redisUrl),
    geminiEnabled: Boolean(env.geminiApiKey)
  });

  // Log routes at startup (helps debug 404s behind gateway)
  app.on('mount', () => {
    // no-op
  });

  app.get('/health', (req, res) => {
    res.status(200).json({
      status: 'healthy',
      service: 'ai-service',
      timestamp: new Date().toISOString()
    });
  });

  // MCP debug endpoints
  app.get('/api/mcp/tools', (req, res) => {
    res.status(200).json({ tools: mcpServer.listTools() });
  });

  app.post('/api/mcp/call', async (req, res) => {
    try {
      const name = String(req.body?.name || '').trim();
      const args = req.body?.args ?? {};
      if (!name) return res.status(400).json({ error: 'name is required' });

      const xUserInfo = getUserInfoHeader(req);
      if (!xUserInfo) return res.status(401).json({ error: 'Missing X-UserInfo (call via gateway with Authorization header)' });

      const result = await mcpServer.callTool(name, args, { xUserInfo });
      res.status(200).json({ result });
    } catch (e: any) {
      const message = e?.response?.data ? JSON.stringify(e.response.data) : (e?.message || 'Tool call failed');
      logger.error('mcp_call_failed', { path: req.path }, e);
      res.status(500).json({ error: message });
    }
  });

  // Trending endpoint
  // GET /api/ai/trending?entity=trip|journal|destination&limit=10&debug=true
  app.get('/api/ai/trending', async (req, res) => {
    try {
      const entity = String(req.query.entity || 'trip');
      const limit = parseInt(String(req.query.limit || '10'), 10);
      const debug = String(req.query.debug || 'false') === 'true';

      const xUserInfo = getUserInfoHeader(req);
      if (!xUserInfo) return res.status(401).json({ error: 'Missing X-UserInfo (call via gateway with Authorization header)' });

      if (entity === 'trip') {
        return res.status(200).json(await trendingTrips(env, xUserInfo, limit, cache, debug));
      }
      if (entity === 'journal') {
        return res.status(200).json(await trendingJournals(env, xUserInfo, limit, cache, debug));
      }
      if (entity === 'destination') {
        return res.status(200).json(await trendingDestinations(env, xUserInfo, limit, cache, debug));
      }

      return res.status(400).json({ error: "entity must be one of: trip, journal, destination" });
    } catch (e: any) {
      const message = e?.response?.data ? JSON.stringify(e.response.data) : (e?.message || 'Failed to generate trending');
      logger.error('trending_failed', { path: req.path, query: req.query }, e);
      res.status(500).json({ error: message });
    }
  });

  // Chat endpoint (Gemini + tool calling)
  // POST /api/ai/chat { "message": "..." }
  app.post('/api/ai/chat', async (req, res) => {
    try {
      const message = String(req.body?.message || '').trim();
      if (!message) return res.status(400).json({ error: 'message is required' });

      if (!env.geminiApiKey) {
        return res.status(400).json({ error: 'GEMINI_API_KEY is not set for ai-service' });
      }

      const xUserInfo = getUserInfoHeader(req);
      if (!xUserInfo) return res.status(401).json({ error: 'Missing X-UserInfo (call via gateway with Authorization header)' });

      const result = await chatWithTools(
        tools,
        { apiKey: env.geminiApiKey, model: env.geminiModel },
        message,
        async (toolName, args) => mcpServer.callTool(toolName, args, { xUserInfo })
      );

      // Always return the structured JSON contract (preferred).
      // If we somehow didn't get JSON, return a schema-shaped error.
      const payload: any =
        result.json ??
        ({
          type: 'error',
          request: {},
          error: { code: 'NO_STRUCTURED_OUTPUT', message: 'No structured JSON returned by model.' },
          sources: { toolsUsed: result.toolsUsed.map((t) => t.name) }
        } as any);

      const status = payload?.type === 'error' && payload?.error?.code === 'TOOLS_REQUIRED' ? 422 : 200;
      res.status(status).json(payload);
    } catch (e: any) {
      const message = e?.response?.data ? JSON.stringify(e.response.data) : (e?.message || 'Chat failed');
      logger.error('chat_failed', { path: req.path }, e);
      res.status(500).json({
        type: 'error',
        request: {},
        error: { code: 'CHAT_FAILED', message },
        sources: { toolsUsed: [] }
      });
    }
  });

  // Express error handler (so we log stack traces)
  app.use((err: any, req: express.Request, res: express.Response) => {
    logger.error('unhandled_error', { path: req.originalUrl || req.path }, err);
    res.status(500).json({ error: 'INTERNAL_SERVER_ERROR', message: err?.message || 'unknown error' });
  });

  // Express default 404 handler (avoid confusing "static resource" errors)
  app.use((req, res) => {
    logger.warn('not_found', {
      method: req.method,
      path: req.originalUrl || req.path
    });
    res.status(404).json({ error: 'NOT_FOUND', path: req.originalUrl || req.path });
  });

  await startEurekaClient();

  app.listen(env.port, () => {
    logger.info('🚀 ai-service is running', { port: env.port });
    logger.info('✅ health check available', { url: `http://localhost:${env.port}/health` });

    // Dump registered routes
    try {
      const routes: Array<{ method: string; path: string }> = [];

      // Express 4 uses app._router.stack; Express 5 uses app.router.stack
      const router: any = (app as any)._router || (app as any).router;
      const stack: any[] = router?.stack || [];

      for (const layer of stack) {
        if (layer?.route?.path) {
          const methods = Object.keys(layer.route.methods || {}).filter((m) => layer.route.methods[m]);
          for (const m of methods) routes.push({ method: m.toUpperCase(), path: layer.route.path });
        }
      }
      logger.info('routes_registered', { count: routes.length, routes });
    } catch (e) {
      logger.warn('routes_dump_failed', undefined);
    }
  });
}

main().catch((err) => {
  logger.error('Failed to start ai-service', undefined, err);
  process.exit(1);
});

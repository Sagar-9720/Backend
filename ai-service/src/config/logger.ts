export type LogLevel = 'debug' | 'info' | 'warn' | 'error';

const levelWeights: Record<LogLevel, number> = {
  debug: 10,
  info: 20,
  warn: 30,
  error: 40
};

function getMinLevel(): LogLevel {
  const raw = String(process.env.LOG_LEVEL || 'info').toLowerCase();
  if (raw === 'debug' || raw === 'info' || raw === 'warn' || raw === 'error') return raw;
  return 'info';
}

function shouldLog(level: LogLevel) {
  return levelWeights[level] >= levelWeights[getMinLevel()];
}

function safeError(err: unknown) {
  if (!err) return undefined;
  if (err instanceof Error) {
    return { name: err.name, message: err.message, stack: process.env.NODE_ENV === 'production' ? undefined : err.stack };
  }
  return { message: String(err) };
}

function write(level: LogLevel, message: string, meta?: Record<string, unknown>) {
  if (!shouldLog(level)) return;

  const record = {
    level: level.toUpperCase(),
    timestamp: new Date().toISOString(),
    service: process.env.SERVICE_NAME || 'ai-service',
    message,
    ...(meta ? { meta } : {})
  };

  // Keep logs docker-friendly (one JSON per line)
  // eslint-disable-next-line no-console
  const out = level === 'error' ? console.error : level === 'warn' ? console.warn : console.log;
  out(JSON.stringify(record));
}

export const logger = {
  debug: (message: string, meta?: Record<string, unknown>) => write('debug', message, meta),
  info: (message: string, meta?: Record<string, unknown>) => write('info', message, meta),
  warn: (message: string, meta?: Record<string, unknown>) => write('warn', message, meta),
  error: (message: string, meta?: Record<string, unknown>, err?: unknown) =>
    write('error', message, { ...(meta || {}), ...(err ? { error: safeError(err) } : {}) })
};


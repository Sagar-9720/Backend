import { Eureka } from 'eureka-js-client';
import { logger } from './logger';

const APP_NAME = process.env.SERVICE_NAME || 'ai-service';
const APP_PORT = parseInt(process.env.PORT || '8086', 10);

const EUREKA_HOST = process.env.EUREKA_SERVER_HOST || 'eureka-server';
const EUREKA_PORT = parseInt(process.env.EUREKA_SERVER_PORT || '8761', 10);

// In docker/k8s, this should be the service DNS name so other services can reach it.
const HOSTNAME = process.env.EUREKA_INSTANCE_HOSTNAME || 'ai-service';
const IP_ADDR = process.env.EUREKA_INSTANCE_IPADDR || '127.0.0.1';

const eurekaClient = new Eureka({
  instance: {
    app: APP_NAME,
    hostName: HOSTNAME,
    ipAddr: IP_ADDR,
    port: { $: APP_PORT, '@enabled': true },
    vipAddress: APP_NAME,
    statusPageUrl: `http://${HOSTNAME}:${APP_PORT}/health`,
    healthCheckUrl: `http://${HOSTNAME}:${APP_PORT}/health`,
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn'
    }
  },
  eureka: {
    host: EUREKA_HOST,
    port: EUREKA_PORT,
    servicePath: '/eureka/apps/',
    maxRetries: 10,
    requestRetryDelay: 2000
  }
});

export function startEurekaClient(): Promise<void> {
  return new Promise((resolve, reject) => {
    eurekaClient.start((error: unknown) => {
      if (error) {
        logger.error('Eureka registration failed', { eurekaHost: EUREKA_HOST, eurekaPort: EUREKA_PORT }, error);
        reject(error);
        return;
      }

      logger.info('✅ AI Service registered with Eureka', { app: APP_NAME, hostName: HOSTNAME, port: APP_PORT });
      resolve();
    });
  });
}

export default eurekaClient;

import { Eureka } from 'eureka-js-client';

const APP_NAME = 'user-service';
const APP_PORT = parseInt(process.env.PORT || '5000', 10); // This is the port your app listens on
const EUREKA_HOST = process.env.EUREKA_SERVER_HOST || 'eureka-server'; // Docker service name
const EUREKA_PORT = 8761;

const HOSTNAME = process.env.EUREKA_INSTANCE_HOSTNAME || 'user-service'; // inside Docker, hostname should match the container/service name
const IP_ADDR = process.env.EUREKA_INSTANCE_IPADDR || '127.0.0.1'; // optional override

const eurekaClient = new Eureka({
  instance: {
    app: APP_NAME,
    hostName: HOSTNAME,
    ipAddr: IP_ADDR,
    port: { '$': APP_PORT, '@enabled': true },
    vipAddress: APP_NAME,
    statusPageUrl: `http://${HOSTNAME}:${APP_PORT}/health`,
    healthCheckUrl: `http://${HOSTNAME}:${APP_PORT}/health`,
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
  },
  eureka: {
    host: EUREKA_HOST,
    port: EUREKA_PORT,
    servicePath: '/eureka/apps/',
    maxRetries: 10,
    requestRetryDelay: 2000,
  },
});

export default eurekaClient;

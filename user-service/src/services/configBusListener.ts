import { Kafka } from 'kafkajs';
import loadConfig from '../config/loadConfig';
import promClient from 'prom-client';

const KAFKA_BROKER = process.env.KAFKA_BROKER || 'kafka:9092';
const BUS_TOPIC = process.env.SPRING_CLOUD_BUS_TOPIC || 'springCloudBus';
const GROUP_ID = process.env.KAFKA_GROUP_ID || 'user-service-bus-group';

const configRefreshCounter = new promClient.Counter({
  name: 'user_service_config_refresh_total',
  help: 'Total number of config refresh events received via Spring Cloud Bus',
});
const configRefreshLastTimestamp = new promClient.Gauge({
  name: 'user_service_config_refresh_last_timestamp',
  help: 'Timestamp of the last config refresh event (seconds since epoch)',
});

export async function startConfigBusListener() {
  const kafka = new Kafka({ brokers: [KAFKA_BROKER] });
  const consumer = kafka.consumer({ groupId: GROUP_ID });
  await consumer.connect();
  await consumer.subscribe({ topic: BUS_TOPIC, fromBeginning: false });

  consumer.run({
    eachMessage: async ({ message }) => {
      try {
        const value = message.value?.toString();
        if (value && value.includes('RefreshRemoteApplicationEvent')) {
          console.log('[ConfigBus] Refresh event received, reloading config...');
          await loadConfig();
          configRefreshCounter.inc();
          configRefreshLastTimestamp.set(Date.now() / 1000);
        }
      } catch (err) {
        console.error('[ConfigBus] Error handling bus event:', err);
      }
    },
  });

  console.log(`[ConfigBus] Listening for config refresh events on topic ${BUS_TOPIC}`);
}

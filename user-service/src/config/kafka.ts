import { Kafka, Producer, Consumer } from 'kafkajs';
import { updateKafkaMetrics, KafkaMetrics } from '../middleware/metrics';

// Get environment variables from .env file
const kafkaHost = process.env.KAFKA_BROKERS || 'kafka:9092';
const viewTopic = process.env.KAFKA_VIEW_TOPIC || 'view-increments';
const batchSize = parseInt(process.env.KAFKA_BATCH_SIZE || '50', 10);
const consumerGroup = process.env.KAFKA_CONSUMER_GROUP || 'view-processor-group';

// Create Kafka client with better connection options
const kafka = new Kafka({
    clientId: 'view-service',
    brokers: kafkaHost.split(','),
    retry: {
        initialRetryTime: 300,
        retries: 10,
        maxRetryTime: 30000,
        factor: 0.2
    },
    connectionTimeout: 5000
});

// Create producer
const producer = kafka.producer({
    allowAutoTopicCreation: true,
    transactionTimeout: 30000
});
let isProducerConnected = false;
let kafkaMetrics: KafkaMetrics;

// Connection attempt counter
let connectionAttempts = 0;
const MAX_RETRY_ATTEMPTS = 5;

// Initialize producer with retry mechanism
const connectProducerWithRetry = async () => {
    try {
        await producer.connect();
        isProducerConnected = true;
        console.info(`Connected to Kafka broker(s): ${kafkaHost}`);

        // Update metrics
        kafkaMetrics = updateKafkaMetrics(producer);
        connectionAttempts = 0; // Reset counter on success
    } catch (err: any) {
        connectionAttempts++;
        console.error(`Kafka connection attempt ${connectionAttempts} failed: ${err.message}`);
        console.error(`Kafka connection details: brokers=${kafkaHost}`);

        updateKafkaMetrics(null);

        if (connectionAttempts < MAX_RETRY_ATTEMPTS) {
            // Exponential backoff retry
            const retryDelay = Math.min(1000 * Math.pow(2, connectionAttempts), 10000);
            console.info(`Retrying Kafka connection in ${retryDelay}ms...`);
            setTimeout(connectProducerWithRetry, retryDelay);
        } else {
            console.error(`Max Kafka connection attempts (${MAX_RETRY_ATTEMPTS}) reached. Giving up.`);
        }
    }
};

// Start connection attempt
connectProducerWithRetry();

// Create consumer with retry mechanism
const createConsumer = async (groupId: string = consumerGroup): Promise<Consumer> => {
    const consumer = kafka.consumer({
        groupId,
        sessionTimeout: 30000,
        heartbeatInterval: 5000,
        retry: {
            initialRetryTime: 300,
            retries: 10
        }
    });

    let consumerConnectAttempts = 0;

    const tryConnect = async (): Promise<Consumer> => {
        try {
            await consumer.connect();
            console.info(`Connected Kafka consumer with group ${groupId}`);
            return consumer;
        } catch (err: any) {
            consumerConnectAttempts++;
            console.error(`Kafka consumer connection attempt ${consumerConnectAttempts} failed: ${err.message}`);

            if (consumerConnectAttempts < MAX_RETRY_ATTEMPTS) {
                const retryDelay = Math.min(1000 * Math.pow(2, consumerConnectAttempts), 10000);
                console.info(`Retrying Kafka consumer connection in ${retryDelay}ms...`);

                return new Promise((resolve) => {
                    setTimeout(() => {
                        resolve(tryConnect());
                    }, retryDelay);
                });
            }
            throw new Error(`Failed to connect Kafka consumer after ${MAX_RETRY_ATTEMPTS} attempts`);
        }
    };

    return tryConnect();
};

// Send messages with metrics tracking and robust error handling
const sendMessage = async (topic: string, messages: any[]): Promise<void> => {
    if (!isProducerConnected) {
        console.warn('Kafka producer not connected, attempting to connect...');
        await connectProducerWithRetry();
        if (!isProducerConnected) {
            throw new Error('Kafka producer not connected and reconnection failed');
        }
    }

    // Update batch size metrics
    if (kafkaMetrics) {
        kafkaMetrics.updateBatchSize(topic, messages.length);
    }

    // Track the operation
    const tracker = kafkaMetrics?.trackMessage(topic, 'send');

    try {
        await producer.send({
            topic,
            messages: messages.map(message => ({
                value: JSON.stringify(message)
            }))
        });
        tracker?.success();
    } catch (err: any) {
        console.error(`Error sending message to ${topic}:`, err.message);
        tracker?.error();

        // Try to reconnect on error
        if (err.name === 'KafkaJSConnectionError') {
            isProducerConnected = false;
            connectProducerWithRetry();
        }

        throw err;
    }
};

// Graceful shutdown
if (typeof global.process !== 'undefined') {
    global.process.on('SIGTERM', async () => {
        console.log('SIGTERM signal received, closing Kafka connections');
        try {
            await producer.disconnect();
            console.log('Kafka producer disconnected');
        } catch (err: any) {
            console.error('Error disconnecting Kafka producer:', err.message);
        }
        updateKafkaMetrics(null);
    });
}

export { kafka, producer, createConsumer, sendMessage, viewTopic, batchSize, consumerGroup };

// Enhanced View Service with Redis caching and Kafka batch processing
import {IViewService} from '../interfaces/viewService.interface';
import {Request} from 'express';
import {ViewResponse} from '../response/response';
import View from '../models/view.model';
import redisClient from '../config/redis';
import {sendMessage, createConsumer} from '../config/kafka';
import {Op} from 'sequelize';

// Kafka topics
const VIEW_INCREMENT_TOPIC = 'view-increments';
const BATCH_SIZE = 50; // Process in batches of 50

// Redis key prefixes
const VIEW_COUNT_PREFIX = 'view:count:';

export class ViewService implements IViewService {
    constructor() {
        // Initialize Kafka consumer for view increments
        this.initializeViewConsumer().catch(err => {
            console.error('Failed to initialize view consumer:', err);
        });
    }

    /**
     * Initialize Kafka consumer to process batched view increments
     */
    private async initializeViewConsumer(): Promise<void> {
        try {
            const consumer = await createConsumer('view-processor-group');

            await consumer.subscribe({topic: VIEW_INCREMENT_TOPIC, fromBeginning: false});

            await consumer.run({
                eachBatchAutoResolve: true,
                eachBatch: async ({batch, resolveOffset, heartbeat, isRunning, isStale}) => {
                    const viewIncrements: Map<string, number> = new Map();

                    // Process messages and group by content ID
                    for (const message of batch.messages) {
                        if (!isRunning() || isStale()) break;

                        try {
                            const viewData = JSON.parse(message.value!.toString());
                            const contentKey = this.getContentKey(viewData);

                            // Group increments by content key
                            if (viewIncrements.has(contentKey)) {
                                viewIncrements.set(contentKey, viewIncrements.get(contentKey)! + 1);
                            } else {
                                viewIncrements.set(contentKey, 1);
                            }

                            resolveOffset(message.offset);
                            await heartbeat();
                        } catch (e) {
                            console.error('Error processing view message:', e);
                        }
                    }

                    // Process the grouped increments in a single batch
                    if (viewIncrements.size > 0) {
                        await this.processBatchedViewIncrements(viewIncrements);
                    }
                }
            });

            console.log('View consumer initialized successfully');
        } catch (error) {
            console.error('Error initializing view consumer:', error);
        }
    }

    /**
     * Process batched view increments from Kafka
     */
    private async processBatchedViewIncrements(viewIncrements: Map<string, number>): Promise<void> {
        for (const [contentKey, incrementCount] of viewIncrements.entries()) {
            try {
                const [type, id] = contentKey.split(':');

                // Build query condition
                const queryCondition: any = {};
                if (type === 'trip') queryCondition.trip_id = id;
                else if (type === 'journal') queryCondition.journal_id = id;
                else if (type === 'destination') queryCondition.destination_id = id;
                else continue;

                // Update or create view record in database
                const [view, created] = await View.findOrCreate({
                    where: queryCondition,
                    defaults: {
                        view_count: incrementCount,
                        ...queryCondition
                    }
                });

                if (!created) {
                    // If record exists, increment view_count
                    await view.increment('view_count', {by: incrementCount});
                }

                // Update Redis cache with the new count
                const redisKey = VIEW_COUNT_PREFIX + contentKey;
                const newCount = created ? incrementCount : (view.view_count + incrementCount);
                await redisClient.set(redisKey, newCount.toString());

                console.log(`Updated view count for ${contentKey} by ${incrementCount}, new total: ${newCount}`);
            } catch (error) {
                console.error(`Error processing batch update for ${contentKey}:`, error);
            }
        }
    }

    /**
     * Get standardized content key for Redis and grouping
     */
    private getContentKey(data: any): string {
        if (data.trip_id) return `trip:${data.trip_id}`;
        if (data.journal_id) return `journal:${data.journal_id}`;
        if (data.destination_id) return `destination:${data.destination_id}`;
        throw new Error('Invalid content data - no ID found');
    }

    /**
     * Get view count for content (trip, journal, destination)
     * Uses Redis cache first, falls back to database
     */
    async getViewCount(req: Request): Promise<ViewResponse | null> {
        const {trip_id, journal_id, destination_id} = req.query;

        // Validate that at least one reference ID is provided
        if (!trip_id && !journal_id && !destination_id) {
            throw new Error('At least one of trip_id, journal_id, or destination_id must be provided');
        }

        try {
            // Determine content type and ID
            let contentKey: string;
            const queryCondition: any = {};

            if (trip_id) {
                contentKey = `trip:${trip_id}`;
                queryCondition.trip_id = trip_id;
            } else if (journal_id) {
                contentKey = `journal:${journal_id}`;
                queryCondition.journal_id = journal_id;
            } else {
                contentKey = `destination:${destination_id}`;
                queryCondition.destination_id = destination_id;
            }

            // First try to get from Redis cache
            const redisKey = VIEW_COUNT_PREFIX + contentKey;
            const cachedCount = await redisClient.get(redisKey);

            if (cachedCount) {
                // If in cache, return the cached value
                return {
                    id: contentKey, // Using contentKey as ID since we don't need actual DB ID
                    trip_id: trip_id?.toString() ?? undefined,
                    journal_id: journal_id?.toString() ?? undefined,
                    destination_id: destination_id?.toString() ?? undefined,
                    view_count: parseInt(cachedCount, 10),
                    createdAt: new Date(),
                    updatedAt: new Date()
                };
            }

            // If not in cache, get from database
            const view = await View.findOne({where: queryCondition});

            if (!view) {
                // No view record exists yet
                return null;
            }

            // Cache the result in Redis for future requests
            await redisClient.set(redisKey, view.view_count.toString());

            // Return the database result
            return {
                id: view.id.toString(),
                trip_id: view.trip_id?.toString() ?? undefined,
                journal_id: view.journal_id?.toString() ?? undefined,
                destination_id: view.destination_id?.toString() ?? undefined,
                view_count: view.view_count,
                createdAt: view.created_at,
                updatedAt: view.updated_at
            };
        } catch (error) {
            console.error('Error getting view count:', error);
            throw error;
        }
    }

    /**
     * Increment view count for content using Redis + Kafka
     * 1. Increment in Redis immediately
     * 2. Send increment event to Kafka for batched processing
     */
    async increaseViewCount(req: Request): Promise<{ increasedView: boolean; view?: ViewResponse }> {
        const {trip_id, journal_id, destination_id} = req.body;

        // Validate that at least one reference ID is provided
        if (!trip_id && !journal_id && !destination_id) {
            throw new Error('At least one of trip_id, journal_id, or destination_id must be provided');
        }

        try {
            // Determine content type and create view data
            const viewData: any = {};
            let contentKey: string;

            if (trip_id) {
                viewData.trip_id = trip_id;
                contentKey = `trip:${trip_id}`;
            } else if (journal_id) {
                viewData.journal_id = journal_id;
                contentKey = `journal:${journal_id}`;
            } else {
                viewData.destination_id = destination_id;
                contentKey = `destination:${destination_id}`;
            }

            // 1. Increment in Redis immediately
            const redisKey = VIEW_COUNT_PREFIX + contentKey;
            const newCount = await redisClient.incr(redisKey);

            // 2. Send to Kafka for batch processing
            await sendMessage(VIEW_INCREMENT_TOPIC, [viewData]);

            // Return the incremented count (no DB view object, so construct response)
            return {
                increasedView: true,
                view: {
                    id: contentKey, // Use contentKey as a synthetic ID
                    trip_id: trip_id?.toString() ?? undefined,
                    journal_id: journal_id?.toString() ?? undefined,
                    destination_id: destination_id?.toString() ?? undefined,
                    view_count: newCount, // The incremented value from Redis
                    createdAt: new Date(),
                    updatedAt: new Date()
                }
            };
        } catch (error) {
            console.error('Error incrementing view:', error);
            throw error;
        }
    }
}

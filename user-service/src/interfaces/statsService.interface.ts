import {Request} from 'express';
import {StatsResponse} from '../services/stats.service';

export interface IStatsService {
    getStats(req: Request): Promise<StatsResponse>;
    getStatsBatch(req: Request): Promise<StatsResponse[]>;
}


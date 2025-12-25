import {Request, Response} from 'express';
import {StatsService} from '../services/stats.service';

const statsService = new StatsService();

export const getStats = async (req: Request, res: Response) => {
    try {
        const result = await statsService.getStats(req);
        res.status(200).json(result);
    } catch (e: any) {
        res.status(400).json({
            error: e?.message || 'Failed to get stats'
        });
    }
};

export const getStatsBatch = async (req: Request, res: Response) => {
    try {
        const result = await statsService.getStatsBatch(req);
        res.status(200).json(result);
    } catch (e: any) {
        res.status(400).json({
            error: e?.message || 'Failed to get batch stats'
        });
    }
};


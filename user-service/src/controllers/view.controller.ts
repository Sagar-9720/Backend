import {Request, Response} from 'express';
import {ViewService} from "../services/view.service";

// Controller for SavedTrip endpoints
const viewService = new ViewService();

export const increaseView = async (req: Request, res: Response) => {
    const result = await viewService.increaseViewCount(req);
    res.status(200).json(result);
};

export const getViews = async (req: Request, res: Response) => {
    const result = await viewService.getViewCount(req);
    res.status(200).json(result);
};


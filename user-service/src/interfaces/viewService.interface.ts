import {Request} from "express";
import {ViewResponse} from '../response/response';

// Interface for View service
export interface IViewService {

    increaseViewCount(req: Request): Promise<{ increasedView: boolean; view?: ViewResponse }>;

    getViewCount(req: Request): Promise<ViewResponse | null>;
}

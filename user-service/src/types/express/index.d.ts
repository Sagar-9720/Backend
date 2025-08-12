import 'express';

declare module 'express' {
  export interface Request {
    user?: {
      userId: string;
      username: string;
      email: string;
      role: string;
      [key: string]: any;
    };
  }
}


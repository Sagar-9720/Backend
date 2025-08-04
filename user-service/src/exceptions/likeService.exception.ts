// Exception for Like service
export class LikeServiceException extends Error {
  status: number;
  constructor(message: string, status = 400) {
    super(message);
    this.status = status;
    Object.setPrototypeOf(this, LikeServiceException.prototype);
  }
}


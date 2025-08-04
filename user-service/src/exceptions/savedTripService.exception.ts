// Exception for SavedTrip service
export class SavedTripServiceException extends Error {
  status: number;
  constructor(message: string, status = 400) {
    super(message);
    this.status = status;
    Object.setPrototypeOf(this, SavedTripServiceException.prototype);
  }
}


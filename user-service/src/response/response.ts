// Response type for Comment
export type CommentResponse = {
    id: string;
    user_id: string;
    trip_id?: string;
    itenary_id?: string;
    destination_id?: string;
    comment: string;
    createdAt: Date;
    updatedAt: Date;
};

// Response type for Like
export type LikeResponse = {
    id: string;
    user_id: string;
    trip_id?: string;
    itinerary_id?: string;
    destination_id?: string;
    created_at: Date;
    updated_at: Date;
};

// Response type for SavedTrip
export type SavedTripResponse = {
    id: string;
    user_id: string;
    trip_id?: string;
    itinerary_id?: string;
    destination_id?: string;
    created_at: Date;
    updated_at: Date;
};


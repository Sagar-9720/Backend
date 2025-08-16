// Response type for Comment
export type CommentResponse = {
    id: string;
    user_id: string;
    trip_id?: string;
    journal_id?: string;
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
    journal_id?: string;
    destination_id?: string;
    created_at: Date;
    updated_at: Date;
};

// Response type for SavedTrip
export type SavedTripResponse = {
    id: string;
    user_id: string;
    trip_id?: string;
    journal_id?: string;
    destination_id?: string;
    created_at: Date;
    updated_at: Date;
};

export type ViewResponse = {
    id: string;
    user_id: string;
    trip_id?: string;
    journal_id?: string;
    destination_id?: string;
    view_count?: number;
    createdAt: Date;
    updatedAt: Date;
}


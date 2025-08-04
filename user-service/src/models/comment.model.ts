import { Schema, model, Document, Types } from 'mongoose';

export interface CommentAttributes {
    user_id: number;
    trip_id?: number;
    itenary_id?: number;
    destination_id?: number;
    comment: string;
    createdAt?: Date;
    updatedAt?: Date;
}

export class Comment implements CommentAttributes {
    user_id: number;
    trip_id?: number;
    itenary_id?: number;
    destination_id?: number;
    comment: string;
    createdAt?: Date;
    updatedAt?: Date;

    constructor(attrs: CommentAttributes) {
        this.user_id = attrs.user_id;
        this.trip_id = attrs.trip_id;
        this.itenary_id = attrs.itenary_id;
        this.destination_id = attrs.destination_id;
        this.comment = attrs.comment;
        this.createdAt = attrs.createdAt;
        this.updatedAt = attrs.updatedAt;
    }
}

const CommentSchema = new Schema<Document & CommentAttributes>(
    {
        user_id: { type: Schema.Types.Number, ref: 'User', required: true },
        trip_id: { type: Schema.Types.Number, ref: 'Trip', required: false },
        itenary_id: { type: Schema.Types.Number, ref: 'Itenary', required: false },
        destination_id: { type: Schema.Types.Number, ref: 'Destination', required: false },
        comment: { type: String, required: true },
    },
    {
        timestamps: true,
    }
);

export default model<Document & CommentAttributes>('Comment', CommentSchema);
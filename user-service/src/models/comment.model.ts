import {Schema, model, Document, Types} from 'mongoose';

export interface CommentAttributes {
    user_id: number;
    trip_id?: number;
    destination_id?: number;
    journal_id?: string;
    comment: string;
    createdAt?: Date;
    updatedAt?: Date;
}

export class Comment implements CommentAttributes {
    user_id: number;
    trip_id?: number;
    journal_id?: string;
    destination_id?: number;
    comment: string;
    createdAt?: Date;
    updatedAt?: Date;

    constructor(attrs: CommentAttributes) {
        this.user_id = attrs.user_id;
        this.trip_id = attrs.trip_id;
        this.journal_id= attrs.journal_id;
        this.destination_id = attrs.destination_id;
        this.comment = attrs.comment;
        this.createdAt = attrs.createdAt;
        this.updatedAt = attrs.updatedAt;
    }
}

const CommentSchema = new Schema<Document & CommentAttributes>(
    {
        user_id: {type: Schema.Types.Number, required: true},
        trip_id: {type: Schema.Types.Number, required: false},
        journal_id: {type: Schema.Types.String, required: false},
        destination_id: {type: Schema.Types.Number, required: false},
        comment: {type: String, required: true},
    },
    {
        timestamps: true,
    }
);

export default model<Document & CommentAttributes>('Comment', CommentSchema);
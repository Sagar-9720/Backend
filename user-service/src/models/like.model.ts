import {Model, DataTypes, Optional} from 'sequelize';
import sequelize from '../config/postgres';

interface LikeAttributes {
    id: number;
    user_id: number;
    trip_id?: number | null;
    journal_id?: string | null;
    destination_id?: number | null;
    created_at?: Date;
    updated_at?: Date;
}

type LikeCreationAttributes = Optional<LikeAttributes, 'id' | 'trip_id' | 'journal_id' | 'destination_id' | 'created_at' | 'updated_at'>;

class Like extends Model<LikeAttributes, LikeCreationAttributes> implements LikeAttributes {
    public id!: number;
    public user_id!: number;
    public trip_id!: number | null;
    public journal_id!: string | null;
    public destination_id!: number | null;
    public readonly created_at!: Date;
    public readonly updated_at!: Date;
}

Like.init(
    {
        id: {
            type: DataTypes.INTEGER.UNSIGNED,
            autoIncrement: true,
            primaryKey: true,
        },
        user_id: {
            type: DataTypes.INTEGER.UNSIGNED,
            allowNull: false,
        },
        trip_id: {
            type: DataTypes.INTEGER.UNSIGNED,
            allowNull: true,
        },
        journal_id: {
            type: DataTypes.STRING,
            allowNull: true,
        },
        destination_id: {
            type: DataTypes.INTEGER.UNSIGNED,
            allowNull: true,
        },
        created_at: {
            type: DataTypes.DATE,
            allowNull: false,
            defaultValue: DataTypes.NOW,
        },
        updated_at: {
            type: DataTypes.DATE,
            allowNull: false,
            defaultValue: DataTypes.NOW,
        },
    },
    {
        sequelize,
        tableName: 'likes',
        timestamps: true,
        createdAt: 'created_at',
        updatedAt: 'updated_at',
    }
);

export default Like;
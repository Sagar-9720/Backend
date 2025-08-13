import {Model, DataTypes, Optional} from 'sequelize';
import sequelize from '../config/postgres';

interface SavedTripAttributes {
    id: number;
    user_id: number;
    trip_id?: number | null;
    itinerary_id?: number | null;
    destination_id?: number | null;
    created_at?: Date;
    updated_at?: Date;
}

type SavedTripCreationAttributes = Optional<SavedTripAttributes, 'id' | 'trip_id' | 'itinerary_id' | 'destination_id' | 'created_at' | 'updated_at'>;

class SavedTrip extends Model<SavedTripAttributes, SavedTripCreationAttributes> implements SavedTripAttributes {
    public id!: number;
    public user_id!: number;
    public trip_id!: number | null;
    public itinerary_id!: number | null;
    public destination_id!: number | null;
    public readonly created_at!: Date;
    public readonly updated_at!: Date;
}

SavedTrip.init(
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
        itinerary_id: {
            type: DataTypes.INTEGER.UNSIGNED,
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
        tableName: 'saved_trips',
        timestamps: true,
        createdAt: 'created_at',
        updatedAt: 'updated_at',
    }
);

export default SavedTrip;
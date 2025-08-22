import {Model, DataTypes, Optional} from 'sequelize';
import sequelize from '../config/postgres';

interface ViewAttributes {
    id: number;
    trip_id?: number | null;
    journal_id?: string | null;
    destination_id?: number | null;
    view_count: number; // Making view_count required
    created_at?: Date;
    updated_at?: Date;
}

type ViewCreationAttributes = Optional<ViewAttributes, 'id' | 'trip_id' | 'journal_id' | 'destination_id' | 'created_at' | 'updated_at'>;

class View extends Model<ViewAttributes, ViewCreationAttributes> implements ViewAttributes {
    public id!: number;
    public trip_id!: number | null;
    public journal_id!: string | null;
    public destination_id!: number | null;
    public view_count!: number; // Required field
    public readonly created_at!: Date;
    public readonly updated_at!: Date;
}

View.init(
    {
        id: {
            type: DataTypes.INTEGER.UNSIGNED,
            autoIncrement: true,
            primaryKey: true,
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
        view_count: {
            type: DataTypes.INTEGER.UNSIGNED,
            allowNull: false,
            defaultValue: 0,
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
        tableName: 'view',
        timestamps: true,
        createdAt: 'created_at',
        updatedAt: 'updated_at',
    }
);

export default View;
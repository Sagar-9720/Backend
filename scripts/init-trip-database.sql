DO
$$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'trip_service_db') THEN
            CREATE DATABASE trip_service_db;
        END IF;
    END
$$;



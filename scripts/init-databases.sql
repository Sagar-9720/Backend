DO
$$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'travelmate_auth') THEN
            CREATE DATABASE travelmate_auth;
        END IF;

        IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'trip_service_db') THEN
            CREATE DATABASE trip_service_db;
        END IF;

        IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'user_service_db') THEN
            CREATE DATABASE user_service_db;
        END IF;
    END
$$;

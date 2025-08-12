-- Create user_service_db if not exists (safe for idempotency)
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'user_service_db') THEN
            CREATE DATABASE user_service_db;
        END IF;
    END
$$;

-- Connect to the database
\c user_service_db;

-- Add table creation and demo data insertion here as needed.

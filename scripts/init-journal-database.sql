DO
$$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'journal_service_db') THEN
            CREATE DATABASE journal_service_db;
        END IF;
    END
$$;


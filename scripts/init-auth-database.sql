DO
$$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'travelmate_auth') THEN
            CREATE DATABASE travelmate_auth;
        END IF;
    END
$$;


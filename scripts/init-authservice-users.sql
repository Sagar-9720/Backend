-- Demo user passwords:
-- admin@travelmate.com: admin123
-- subadmin1@travelmate.com: subadmin123
-- subadmin2@travelmate.com: subadmin1234
-- user@travelmate.com: user123

-- Description: Initialization script for Auth-service
-- Inserts default roles and 4 users (1 Admin, 2 Subadmins, 1 User) into the database.
-- All users have admin credentials for demo purposes.

-- Insert roles if not present
INSERT INTO roles (name, description, created_at, updated_at)
SELECT 'ADMIN', 'Administrator', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (name, description, created_at, updated_at)
SELECT 'SUBADMIN', 'Sub Administrator', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'SUBADMIN');

INSERT INTO roles (name, description, created_at, updated_at)
SELECT 'USER', 'Regular User', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'USER');

-- Insert users (passwords should be bcrypt-hashed in production)
INSERT INTO users (name, email, phone, password, dob, gender, profile_img, email_verified, created_at, updated_at,
                   role_id, request_delete)
SELECT 'Admin User',
       'admin@travelmate.com',
       '1234567890',
       '$2a$10$Gt45PnNX8olN75cS4X8rj.70m9tKQfHMQ8FKlKjUQ9SEDMETDXab.',
       '1990-01-01',
       'MALE',
       NULL,
       FALSE,
       NOW(),
       NOW(),
       (SELECT role_id FROM roles WHERE name = 'ADMIN'),
       FALSE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@travelmate.com');

INSERT INTO users (name, email, phone, password, dob, gender, profile_img, email_verified, created_at, updated_at,
                   role_id, request_delete)
SELECT 'Subadmin One',
       'subadmin1@travelmate.com',
       '1234567891',
       '$2a$10$sgq1fGw.iEoUrz2nojXoCe./7wj8i7l/jKJGQy7xVGvs2LxmNS8ua',
       '1992-02-02',
       'FEMALE',
       NULL,
       FALSE,
       NOW(),
       NOW(),
       (SELECT role_id FROM roles WHERE name = 'SUBADMIN'),
       FALSE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'subadmin1@travelmate.com');

INSERT INTO users (name, email, phone, password, dob, gender, profile_img, email_verified, created_at, updated_at,
                   role_id, request_delete)
SELECT 'Subadmin Two',
       'subadmin2@travelmate.com',
       '1234567892',
       '$2a$10$xmxW7FBAhM3fIYXZFdiGeuBWHMsNB4O0TN/ieY1urLipyEoocbcU2',
       '1993-03-03',
       'MALE',
       NULL,
       FALSE,
       NOW(),
       NOW(),
       (SELECT role_id FROM roles WHERE name = 'SUBADMIN'),
       FALSE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'subadmin2@travelmate.com');

INSERT INTO users (name, email, phone, password, dob, gender, profile_img, email_verified, created_at, updated_at,
                   role_id, request_delete)
SELECT 'Regular User',
       'user@travelmate.com',
       '1234567893',
       '$2a$10$DodpBVpOOYO5ca1nIZ8nTuyeBsOun0WLCOEAdFeU7pcjp0pIE28jG',
       '1994-04-04',
       'OTHER',
       NULL,
       FALSE,
       NOW(),
       NOW(),
       (SELECT role_id FROM roles WHERE name = 'USER'),
       FALSE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user@travelmate.com');

-- Note: Replace '$2a$10$adminpasswordhash' with actual bcrypt hashes for your admin password.

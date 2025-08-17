-- Demo data for saved_trips and likes tables for user-service
-- Assumes users and trips/itineraries/destinations exist with these IDs

-- Saved Trips
INSERT INTO saved_trips (user_id, trip_id, created_at, updated_at)
VALUES (1, 1, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Admin User saves Manali Summer Escape
INSERT INTO saved_trips (user_id, trip_id, created_at, updated_at)
VALUES (2, 2, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Subadmin One saves Manali Winter Wonderland
INSERT INTO saved_trips (user_id, journal_id, created_at, updated_at)
VALUES (3, 1, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Subadmin Two saves Manali Adventure Day 1
INSERT INTO saved_trips (user_id, destination_id, created_at, updated_at)
VALUES (4, 1, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Regular User saves Manali destination

-- Likes
INSERT INTO likes (user_id, trip_id, created_at, updated_at)
VALUES (1, 2, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Admin User likes Manali Winter Wonderland
INSERT INTO likes (user_id, journal_id, created_at, updated_at)
VALUES (2, 2, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Subadmin One likes Manali Adventure Day 2
INSERT INTO likes (user_id, destination_id, created_at, updated_at)
VALUES (3, 1, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Subadmin Two likes Manali destination
INSERT INTO likes (user_id, trip_id, created_at, updated_at)
VALUES (4, 3, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Regular User likes Leh Adventure

-- Add more demo data as needed for testing
-- 2. Views (demo data for journals)
INSERT INTO view (user_id, journal_id, view_count, created_at, updated_at)
VALUES (1, '1', 5, NOW(), NOW()),
       (1, '2', 2, NOW(), NOW()),
       (2, '3', 3, NOW(), NOW()),
       (2, '4', 1, NOW(), NOW()),
       (3, '5', 4, NOW(), NOW()),
       (3, '6', 1, NOW(), NOW()),
       (4, '7', 6, NOW(), NOW()),
       (4, '8', 2, NOW(), NOW());
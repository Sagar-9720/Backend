-- Demo data for saved_trips and likes tables for user-service
-- Assumes users and trips/itineraries/destinations exist with these IDs

-- Saved Trips
INSERT INTO saved_trips (user_id, trip_id, created_at, updated_at)
VALUES (1, 1, NOW(), NOW())
ON CONFLICT DO NOTHING; -- Admin User saves Manali Summer Escape
INSERT INTO saved_trips (user_id, trip_id, created_at, updated_at)
VALUES (2, 2, NOW(), NOW())
ON CONFLICT DO NOTHING; -- Subadmin One saves Manali Winter Wonderland
INSERT INTO saved_trips (user_id, journal_id, created_at, updated_at)
VALUES (3, 1, NOW(), NOW())
ON CONFLICT DO NOTHING; -- Subadmin Two saves Manali Adventure Day 1
INSERT INTO saved_trips (user_id, destination_id, created_at, updated_at)
VALUES (4, 1, NOW(), NOW())
ON CONFLICT DO NOTHING;
-- Regular User saves Manali destination

-- Likes
INSERT INTO likes (user_id, trip_id, created_at, updated_at)
VALUES (1, 2, NOW(), NOW())
ON CONFLICT DO NOTHING; -- Admin User likes Manali Winter Wonderland
INSERT INTO likes (user_id, journal_id, created_at, updated_at)
VALUES (2, 2, NOW(), NOW())
ON CONFLICT DO NOTHING; -- Subadmin One likes Manali Adventure Day 2
INSERT INTO likes (user_id, destination_id, created_at, updated_at)
VALUES (3, 1, NOW(), NOW())
ON CONFLICT DO NOTHING; -- Subadmin Two likes Manali destination
INSERT INTO likes (user_id, trip_id, created_at, updated_at)
VALUES (4, 3, NOW(), NOW())
ON CONFLICT DO NOTHING;
-- Regular User likes Leh Adventure

-- Add more demo data as needed for testing
-- 2. Views (demo data for journals)
INSERT INTO view (journal_id, trip_id, destination_id, view_count, created_at, updated_at)
VALUES (1, NULL, NULL, 10, NOW(), NOW()), -- journal_id 1, 10 views
       (2, NULL, NULL, 15, NOW(), NOW()), -- journal_id 2, 15 views
       (NULL, 1, NULL, 20, NOW(), NOW()), -- trip_id 1, 20 views
       (NULL, 2, NULL, 25, NOW(), NOW()), -- trip_id 2, 25 views
       (NULL, NULL, 1, 30, NOW(), NOW()), -- destination_id 1, 30 views
       (NULL, NULL, 2, 35, NOW(), NOW()), -- destination_id 2, 35 views
       (3, NULL, NULL, 40, NOW(), NOW()), -- journal_id 3, 40 views
       (NULL, 3, NULL, 45, NOW(), NOW()); -- trip_id 3, 45 views
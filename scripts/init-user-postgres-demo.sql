-- Demo data for saved_trips and likes tables for user-service
-- Assumes users and trips/itineraries/destinations exist with these IDs

-- Saved Trips
INSERT INTO saved_trips (user_id, trip_id, created_at, updated_at)
VALUES (1, 1, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Admin User saves Manali Summer Escape
INSERT INTO saved_trips (user_id, trip_id, created_at, updated_at)
VALUES (2, 2, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Subadmin One saves Manali Winter Wonderland
INSERT INTO saved_trips (user_id, itinerary_id, created_at, updated_at)
VALUES (3, 1, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Subadmin Two saves Manali Adventure Day 1
INSERT INTO saved_trips (user_id, destination_id, created_at, updated_at)
VALUES (4, 1, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Regular User saves Manali destination

-- Likes
INSERT INTO likes (user_id, trip_id, created_at, updated_at)
VALUES (1, 2, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Admin User likes Manali Winter Wonderland
INSERT INTO likes (user_id, itinerary_id, created_at, updated_at)
VALUES (2, 2, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Subadmin One likes Manali Adventure Day 2
INSERT INTO likes (user_id, destination_id, created_at, updated_at)
VALUES (3, 1, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Subadmin Two likes Manali destination
INSERT INTO likes (user_id, trip_id, created_at, updated_at)
VALUES (4, 3, NOW(), NOW()) ON CONFLICT DO NOTHING; -- Regular User likes Leh Adventure

-- Add more demo data as needed for testing


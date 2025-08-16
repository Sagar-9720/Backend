-- 1. Tags (demo tags for trips, destinations, itineraries)
INSERT INTO tag (name, usage_count)
VALUES ('Adventure', 4)
ON CONFLICT (name) DO NOTHING;
INSERT INTO tag (name, usage_count)
VALUES ('Nature', 5)
ON CONFLICT (name) DO NOTHING;
INSERT INTO tag (name, usage_count)
VALUES ('Culture', 1)
ON CONFLICT (name) DO NOTHING;
INSERT INTO tag (name, usage_count)
VALUES ('Family', 2)
ON CONFLICT (name) DO NOTHING;
INSERT INTO tag (name, usage_count)
VALUES ('Winter', 1)
ON CONFLICT (name) DO NOTHING;
INSERT INTO tag (name, usage_count)
VALUES ('Summer', 1)
ON CONFLICT (name) DO NOTHING;
INSERT INTO tag (name, usage_count)
VALUES ('Luxury', 0)
ON CONFLICT (name) DO NOTHING;
INSERT INTO tag (name, usage_count)
VALUES ('Budget', 1)
ON CONFLICT (name) DO NOTHING;
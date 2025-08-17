-- Description: Initialization script for Trip-service
-- Inserts countries, regions, destinations, itineraries, and trips with realistic data.

-- 1. Countries
INSERT INTO country (name)
VALUES ('India')
ON CONFLICT (name) DO NOTHING;
INSERT INTO country (name)
VALUES ('United States')
ON CONFLICT (name) DO NOTHING;
INSERT INTO country (name)
VALUES ('United Kingdom')
ON CONFLICT (name) DO NOTHING;
INSERT INTO country (name)
VALUES ('UAE')
ON CONFLICT (name) DO NOTHING;
INSERT INTO country (name)
VALUES ('Australia')
ON CONFLICT (name) DO NOTHING;
INSERT INTO country (name)
VALUES ('Russia')
ON CONFLICT (name) DO NOTHING;
INSERT INTO country (name)
VALUES ('France')
ON CONFLICT (name) DO NOTHING;
INSERT INTO country (name)
VALUES ('Japan')
ON CONFLICT (name) DO NOTHING;
INSERT INTO country (name)
VALUES ('Italy')
ON CONFLICT (name) DO NOTHING;

-- 2. Regions
INSERT INTO region (name, country_id)
VALUES ('Himachal Pradesh', (SELECT id FROM country WHERE name = 'India'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('Jammu', (SELECT id FROM country WHERE name = 'India'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('Ladakh', (SELECT id FROM country WHERE name = 'India'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('California', (SELECT id FROM country WHERE name = 'United States'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('England', (SELECT id FROM country WHERE name = 'United Kingdom'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('Uttarakhand', (SELECT id FROM country WHERE name = 'India'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('Scotland', (SELECT id FROM country WHERE name = 'United Kingdom'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('Dubai', (SELECT id FROM country WHERE name = 'UAE'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('Abu Dhabi', (SELECT id FROM country WHERE name = 'UAE'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('New South Wales', (SELECT id FROM country WHERE name = 'Australia'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('Moscow', (SELECT id FROM country WHERE name = 'Russia'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('Saint Petersburg', (SELECT id FROM country WHERE name = 'Russia'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('Île-de-France', (SELECT id FROM country WHERE name = 'France'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('Tokyo', (SELECT id FROM country WHERE name = 'Japan'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('Kyoto', (SELECT id FROM country WHERE name = 'Japan'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('Lombardy', (SELECT id FROM country WHERE name = 'Italy'))
ON CONFLICT (name) DO NOTHING;
INSERT INTO region (name, country_id)
VALUES ('New York', (SELECT id FROM country WHERE name = 'United States'))
ON CONFLICT (name) DO NOTHING;

-- 3. Destinations
INSERT INTO destination (name, region_id, description, image_url)
VALUES ('Manali', (SELECT id FROM region WHERE name = 'Himachal Pradesh'),
        'A beautiful hill station in Himachal Pradesh.',
        'https://www.incredibleindia.gov.in/sites/default/files/styles/hero_image/public/2023-03/Manali.jpg'),
       ('Leh', (SELECT id FROM region WHERE name = 'Ladakh'), 'Gateway to adventure in Ladakh.',
        'https://images.unsplash.com/photo-1564013791-8d5c2e3f1b5d'),
       ('Yosemite', (SELECT id FROM region WHERE name = 'California'), 'Famous national park in California.',
        'https://www.nps.gov/common/uploads/structured_data/3C7F3C1E-1DD8-B71B-0B1F9E8E2D5F4D0E.jpg'),
       ('London', (SELECT id FROM region WHERE name = 'England'), 'Capital city of England.',
        'https://images.unsplash.com/photo-1521747116042-5a810fda9664'),
       ('Rishikesh', (SELECT id FROM region WHERE name = 'Uttarakhand'),
        'Yoga capital of the world, famous for river rafting.',
        'https://images.unsplash.com/photo-1506748686214-3a1b0f9f5c8f'),
       ('Edinburgh', (SELECT id FROM region WHERE name = 'Scotland'), 'Historic and cultural capital of Scotland.',
        'https://images.unsplash.com/photo-1561948952-8c6f3a3d3c8e'),
       ('Burj Khalifa', (SELECT id FROM region WHERE name = 'Dubai'), 'Tallest building in the world.',
        'https://images.unsplash.com/photo-1561948952-8c6f3a3d3c8e'),
       ('Sheikh Zayed Mosque', (SELECT id FROM region WHERE name = 'Abu Dhabi'), 'Famous mosque in Abu Dhabi.',
        'https://images.unsplash.com/photo-1561948952-8c6f3a3d3c8e'),
       ('Sydney Opera House', (SELECT id FROM region WHERE name = 'New South Wales'),
        'Iconic performing arts center in Sydney.', 'https://images.unsplash.com/photo-1561948952-8c6f3a3d3c8e'),
       ('Red Square', (SELECT id FROM region WHERE name = 'Moscow'), 'Historic square in Moscow.',
        'https://images.unsplash.com/photo-1561948952-8c6f3a3d3c8e'),
       ('Hermitage Museum', (SELECT id FROM region WHERE name = 'Saint Petersburg'), 'World-famous museum.',
        'https://images.unsplash.com/photo-1561948952-8c6f3a3d3c8e'),
       ('Eiffel Tower', (SELECT id FROM region WHERE name = 'Île-de-France'), 'Iconic symbol of Paris.',
        'https://images.unsplash.com/photo-1561948952-8c6f3a3d3c8e'),
       ('Mount Fuji', (SELECT id FROM region WHERE name = 'Tokyo'), 'Japan’s tallest mountain.',
        'https://images.unsplash.com/photo-1561948952-8c6f3a3d3c8e'),
       ('Fushimi Inari Shrine', (SELECT id FROM region WHERE name = 'Kyoto'), 'Famous shrine in Kyoto.',
        'https://images.unsplash.com/photo-1561948952-8c6f3a3d3c8e'),
       ('Milan Cathedral', (SELECT id FROM region WHERE name = 'Lombardy'), 'Gothic cathedral in Milan.',
        'https://images.unsplash.com/photo-1561948952-8c6f3a3d3c8e'),
       ('Statue of Liberty', (SELECT id FROM region WHERE name = 'New York'), 'Famous statue in New York.',
        'https://images.unsplash.com/photo-1561948952-8c6f3a3d3c8e'),
       ('Edinburgh Castle', (SELECT id FROM region WHERE name = 'Scotland'), 'Historic fortress in Edinburgh.',
        'https://images.unsplash.com/photo-1561948952-8c6f3a3d3c8e');

-- 4. Itineraries (for Manali, Leh, Yosemite, London)
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Manali Adventure', (SELECT id FROM destination WHERE name = 'Manali'), 'Arrive and explore Mall Road.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Manali Adventure', (SELECT id FROM destination WHERE name = 'Manali'), 'Visit Solang Valley.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Leh Explorer', (SELECT id FROM destination WHERE name = 'Leh'), 'Arrive and acclimatize.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Leh Explorer', (SELECT id FROM destination WHERE name = 'Leh'), 'Visit Pangong Lake.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Yosemite Classic', (SELECT id FROM destination WHERE name = 'Yosemite'), 'Explore Yosemite Valley.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('London Highlights', (SELECT id FROM destination WHERE name = 'London'), 'See Buckingham Palace.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Rishikesh Adventure', (SELECT id FROM destination WHERE name = 'Rishikesh'), 'Arrive and try river rafting.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Rishikesh Adventure', (SELECT id FROM destination WHERE name = 'Rishikesh'), 'Yoga and Ganga Aarti.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Edinburgh Explorer', (SELECT id FROM destination WHERE name = 'Edinburgh'), 'Visit Edinburgh Castle.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Dubai Highlights', (SELECT id FROM destination WHERE name = 'Burj Khalifa'), 'Visit Burj Khalifa and Dubai Mall.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Abu Dhabi Culture', (SELECT id FROM destination WHERE name = 'Sheikh Zayed Mosque'), 'Tour the Sheikh Zayed Mosque.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Sydney Opera Day', (SELECT id FROM destination WHERE name = 'Sydney Opera House'), 'Opera House and Harbour Bridge walk.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Moscow Heritage', (SELECT id FROM destination WHERE name = 'Red Square'), 'Red Square and Kremlin tour.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Saint Petersburg Art', (SELECT id FROM destination WHERE name = 'Hermitage Museum'), 'Explore Hermitage Museum.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Paris Romance', (SELECT id FROM destination WHERE name = 'Eiffel Tower'), 'Eiffel Tower and Seine cruise.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Fuji Adventure', (SELECT id FROM destination WHERE name = 'Mount Fuji'), 'Climb Mount Fuji.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Kyoto Temples', (SELECT id FROM destination WHERE name = 'Fushimi Inari Shrine'), 'Visit Fushimi Inari and Gion.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Milan Art', (SELECT id FROM destination WHERE name = 'Milan Cathedral'), 'Tour Milan Cathedral and city center.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('NYC Icons', (SELECT id FROM destination WHERE name = 'Statue of Liberty'), 'Statue of Liberty and Ellis Island.');
INSERT INTO itinerary (itinerary_name, destination_id, description)
VALUES ('Edinburgh Castle Day', (SELECT id FROM destination WHERE name = 'Edinburgh Castle'), 'Tour Edinburgh Castle and Royal Mile.');

-- 5. Trips (reuse and mix itineraries)
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Manali Summer Escape', 'A fun summer trip to Manali.', '2025-09-01 09:00:00', '2025-09-03 18:00:00', 12000.00,
        (SELECT id FROM destination WHERE name = 'Manali'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Manali Winter Wonderland', 'Experience snow in Manali.', '2025-12-15 09:00:00', '2025-12-17 18:00:00', 15000.00,
        (SELECT id FROM destination WHERE name = 'Manali'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Leh Adventure', 'Adventure trip to Leh.', '2025-09-05 09:00:00', '2025-09-07 20:00:00', 20000.00,
        (SELECT id FROM destination WHERE name = 'Leh'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Yosemite Explorer', 'Explore Yosemite National Park.', '2025-10-01 09:00:00', '2025-10-02 17:00:00', 18000.00,
        (SELECT id FROM destination WHERE name = 'Yosemite'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('London City Tour', 'Discover London''s best sights.', '2025-11-01 09:00:00', '2025-11-02 18:00:00', 25000.00,
        (SELECT id FROM destination WHERE name = 'London'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Rishikesh Spiritual Retreat', 'Yoga and adventure in Rishikesh.', '2025-09-10 08:00:00', '2025-09-12 20:00:00', 9000.00,
        (SELECT id FROM destination WHERE name = 'Rishikesh'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Edinburgh Heritage Walk', 'Explore the history of Edinburgh.', '2025-10-10 09:00:00', '2025-10-11 18:00:00', 22000.00,
        (SELECT id FROM destination WHERE name = 'Edinburgh'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Manali & Leh Combo', 'Combo trip covering Manali and Leh.', '2025-09-01 09:00:00', '2025-09-07 20:00:00', 30000.00,
        (SELECT id FROM destination WHERE name = 'Manali'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Dubai Luxury Tour', 'Experience luxury in Dubai.', '2025-10-01 09:00:00', '2025-10-03 18:00:00', 35000.00,
        (SELECT id FROM destination WHERE name = 'Burj Khalifa'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Sydney City Break', 'Discover Sydney''s icons.', '2025-11-01 08:00:00', '2025-11-02 18:00:00', 28000.00,
        (SELECT id FROM destination WHERE name = 'Sydney Opera House'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Moscow & St. Petersburg', 'Explore Russia''s two capitals.', '2025-12-01 09:00:00', '2025-12-03 18:00:00', 32000.00,
        (SELECT id FROM destination WHERE name = 'Red Square'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Paris Dream', 'Romantic trip to Paris.', '2025-09-20 09:00:00', '2025-09-21 18:00:00', 40000.00,
        (SELECT id FROM destination WHERE name = 'Eiffel Tower'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Fuji & Kyoto', 'Japan highlights: Fuji and Kyoto.', '2025-08-15 06:00:00', '2025-08-17 18:00:00', 37000.00,
        (SELECT id FROM destination WHERE name = 'Mount Fuji'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Milan Art Tour', 'Art and architecture in Milan.', '2025-09-10 09:00:00', '2025-09-11 18:00:00', 26000.00,
        (SELECT id FROM destination WHERE name = 'Milan Cathedral'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('NYC Essentials', 'See the best of New York.', '2025-10-10 08:00:00', '2025-10-11 18:00:00', 30000.00,
        (SELECT id FROM destination WHERE name = 'Statue of Liberty'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;
INSERT INTO trip (title, description, start_date, end_date, price, main_destination_id, created_by, is_active, created_at, updated_at)
VALUES ('Edinburgh Royal Tour', 'History and culture in Edinburgh.', '2025-09-15 09:00:00', '2025-09-16 18:00:00', 21000.00,
        (SELECT id FROM destination WHERE name = 'Edinburgh Castle'), 'admin', true, '2025-08-13 00:00:00', '2025-08-13 00:00:00')
ON CONFLICT (title) DO NOTHING;

-- 6. Trip Itinerary Details (mapping Trip, Itinerary, Day, Arrival, Departure)
INSERT INTO trip_itinerary_detail (trip_id, itinerary_id, day_number, arrival_time, departure_time)
VALUES ((SELECT id FROM trip WHERE title = 'Manali Summer Escape'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Manali Adventure' LIMIT 1), 1, '2025-09-01 09:00:00', '2025-09-01 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Manali Summer Escape'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Manali Adventure' LIMIT 1 OFFSET 1), 2, '2025-09-02 09:00:00', '2025-09-02 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Leh Adventure'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Leh Explorer' LIMIT 1), 1, '2025-09-05 09:00:00', '2025-09-05 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Leh Adventure'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Leh Explorer' LIMIT 1 OFFSET 1), 2, '2025-09-06 09:00:00', '2025-09-06 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Yosemite Explorer'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Yosemite Classic' LIMIT 1), 1, '2025-10-01 09:00:00', '2025-10-01 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'London City Tour'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'London Highlights' LIMIT 1), 1, '2025-11-01 09:00:00', '2025-11-01 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Rishikesh Spiritual Retreat'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Rishikesh Adventure' LIMIT 1), 1, '2025-09-10 08:00:00', '2025-09-10 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Rishikesh Spiritual Retreat'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Rishikesh Adventure' LIMIT 1 OFFSET 1), 2, '2025-09-11 08:00:00', '2025-09-11 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Edinburgh Heritage Walk'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Edinburgh Explorer' LIMIT 1), 1, '2025-10-10 09:00:00', '2025-10-10 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Manali & Leh Combo'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Manali Adventure' LIMIT 1), 1, '2025-09-01 09:00:00', '2025-09-01 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Manali & Leh Combo'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Leh Explorer' LIMIT 1 OFFSET 1), 2, '2025-09-06 09:00:00', '2025-09-06 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Dubai Luxury Tour'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Dubai Highlights' LIMIT 1), 1, '2025-10-01 09:00:00', '2025-10-01 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Dubai Luxury Tour'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Abu Dhabi Culture' LIMIT 1), 2, '2025-10-02 09:00:00', '2025-10-02 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Sydney City Break'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Sydney Opera Day' LIMIT 1), 1, '2025-11-01 08:00:00', '2025-11-01 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Moscow & St. Petersburg'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Moscow Heritage' LIMIT 1), 1, '2025-12-01 09:00:00', '2025-12-01 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Moscow & St. Petersburg'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Saint Petersburg Art' LIMIT 1), 2, '2025-12-02 09:00:00', '2025-12-02 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Paris Dream'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Paris Romance' LIMIT 1), 1, '2025-09-20 09:00:00', '2025-09-20 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Fuji & Kyoto'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Fuji Adventure' LIMIT 1), 1, '2025-08-15 06:00:00', '2025-08-15 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Fuji & Kyoto'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Kyoto Temples' LIMIT 1), 2, '2025-08-16 09:00:00', '2025-08-16 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Milan Art Tour'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Milan Art' LIMIT 1), 1, '2025-09-10 09:00:00', '2025-09-10 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'NYC Essentials'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'NYC Icons' LIMIT 1), 1, '2025-10-10 08:00:00', '2025-10-10 18:00:00'),
       ((SELECT id FROM trip WHERE title = 'Edinburgh Royal Tour'),
        (SELECT id FROM itinerary WHERE itinerary_name = 'Edinburgh Castle Day' LIMIT 1), 1, '2025-09-15 09:00:00', '2025-09-15 18:00:00');

-- 7. Itinerary Activities (Demo Data)
INSERT INTO itinerary_activity (activity_name, description, trip_itinerary_detail_id)
VALUES ('Mall Road Walk', 'Evening walk at Mall Road', (SELECT id FROM trip_itinerary_detail WHERE day_number = 1 AND trip_id = (SELECT id FROM trip WHERE title = 'Manali Summer Escape'))),
       ('Solang Valley Adventure', 'Paragliding and zorbing at Solang Valley', (SELECT id FROM trip_itinerary_detail WHERE day_number = 2 AND trip_id = (SELECT id FROM trip WHERE title = 'Manali Summer Escape'))),
       ('Acclimatization', 'Rest and acclimatize to high altitude', (SELECT id FROM trip_itinerary_detail WHERE day_number = 1 AND trip_id = (SELECT id FROM trip WHERE title = 'Leh Adventure'))),
       ('Pangong Lake Visit', 'Day trip to Pangong Lake', (SELECT id FROM trip_itinerary_detail WHERE day_number = 2 AND trip_id = (SELECT id FROM trip WHERE title = 'Leh Adventure'))),
       ('Yosemite Valley Tour', 'Explore Yosemite Valley', (SELECT id FROM trip_itinerary_detail WHERE day_number = 1 AND trip_id = (SELECT id FROM trip WHERE title = 'Yosemite Explorer'))),
       ('Buckingham Palace Visit', 'See Buckingham Palace', (SELECT id FROM trip_itinerary_detail WHERE day_number = 1 AND trip_id = (SELECT id FROM trip WHERE title = 'London City Tour'))),
       ('River Rafting', 'Try river rafting in Rishikesh', (SELECT id FROM trip_itinerary_detail WHERE day_number = 1 AND trip_id = (SELECT id FROM trip WHERE title = 'Rishikesh Spiritual Retreat'))),
       ('Yoga and Ganga Aarti', 'Yoga session and Ganga Aarti', (SELECT id FROM trip_itinerary_detail WHERE day_number = 2 AND trip_id = (SELECT id FROM trip WHERE title = 'Rishikesh Spiritual Retreat'))),
       ('Edinburgh Castle Tour', 'Visit Edinburgh Castle', (SELECT id FROM trip_itinerary_detail WHERE day_number = 1 AND trip_id = (SELECT id FROM trip WHERE title = 'Edinburgh Heritage Walk')));


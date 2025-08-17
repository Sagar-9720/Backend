// MongoDB initialization script for trip-service demo travel journals
// Run with: mongo < scripts/init-mongodb-journalservice.js

db = db.getSiblingDB('journaldb');

db.travel_journals.deleteMany({}); // Clean slate for demo

db.travel_journals.insertMany([
    // User 1: One public, one private
    {
        userId: "1",
        tripId: "1",
        title: "Manali Arrival",
        note: "Arrived in Manali, checked into hotel, and explored Mall Road.",
        isPublic: true,
        location: {
            lat: 32.2432,
            lng: 77.1892,
            placeName: "Manali, Himachal Pradesh, India"
        },
        country: "India",
        city: "Manali",
        category: "Adventure",
        sections: [
            {
                dayTitle: "Day 1 - Arrival in Manali",
                content: "Reached Manali and explored Mall Road.",
                media: [
                    {
                        url: "https://example.com/manali1.jpg",
                        caption: "Mall Road evening vibes",
                        type: "IMAGE",
                        uploadedAt: new Date("2025-08-01T09:30:00Z")
                    }
                ]
            }
        ],
        tags: ["Adventure", "Nature", "Summer"],
        entryDate: new Date("2025-08-01T10:00:00Z"),
        createdAt: new Date("2025-08-01T09:00:00Z"),
        updatedAt: new Date("2025-08-01T10:00:00Z"),
        deletedAt: null
    },
    {
        userId: "1",
        tripId: "1",
        title: "Manali Secret Spots",
        note: "Discovered some hidden cafes and local hangouts.",
        isPublic: false,
        location: {
            lat: 32.2432,
            lng: 77.1892,
            placeName: "Manali, Himachal Pradesh, India"
        },
        country: "India",
        city: "Manali",
        category: "Culture",
        sections: [
            {
                dayTitle: "Day 2 - Hidden Cafes",
                content: "Visited local cafes and met new friends.",
                media: [
                    {
                        url: "https://example.com/manali-cafe.jpg",
                        caption: "Cozy cafe corner",
                        type: "IMAGE",
                        uploadedAt: new Date("2025-08-02T11:00:00Z")
                    }
                ]
            }
        ],
        tags: ["Culture", "Cafe", "Local"],
        entryDate: new Date("2025-08-02T12:00:00Z"),
        createdAt: new Date("2025-08-02T11:00:00Z"),
        updatedAt: new Date("2025-08-02T12:00:00Z"),
        deletedAt: null
    },
    // User 2: One public, one private
    {
        userId: "2",
        tripId: "2",
        title: "Solang Valley Snow Day",
        note: "Enjoyed skiing and snow play in Solang Valley.",
        isPublic: false,
        location: {
            lat: 32.3182,
            lng: 77.1587,
            placeName: "Solang Valley, Himachal Pradesh, India"
        },
        country: "India",
        city: "Manali",
        category: "Winter",
        sections: [
            {
                dayTitle: "Day 2 - Solang Valley Snow",
                content: "Skiing, snow fights, and cable car ride.",
                media: [
                    {
                        url: "https://example.com/solang1.jpg",
                        caption: "Snow fun with family",
                        type: "IMAGE",
                        uploadedAt: new Date("2025-08-02T10:30:00Z")
                    }
                ]
            }
        ],
        tags: ["Adventure", "Winter", "Family"],
        entryDate: new Date("2025-08-02T11:00:00Z"),
        createdAt: new Date("2025-08-02T10:00:00Z"),
        updatedAt: new Date("2025-08-02T11:00:00Z"),
        deletedAt: null
    },
    {
        userId: "2",
        tripId: "2",
        title: "Solang Valley Public Journal",
        note: "Shared my favorite snow moments.",
        isPublic: true,
        location: {
            lat: 32.3182,
            lng: 77.1587,
            placeName: "Solang Valley, Himachal Pradesh, India"
        },
        country: "India",
        city: "Manali",
        category: "Adventure",
        sections: [
            {
                dayTitle: "Day 2 - Public Snow Fun",
                content: "Posted photos and stories for friends.",
                media: [
                    {
                        url: "https://example.com/solang2.jpg",
                        caption: "Public snow post",
                        type: "IMAGE",
                        uploadedAt: new Date("2025-08-02T12:00:00Z")
                    }
                ]
            }
        ],
        tags: ["Adventure", "Public", "Snow"],
        entryDate: new Date("2025-08-02T13:00:00Z"),
        createdAt: new Date("2025-08-02T12:00:00Z"),
        updatedAt: new Date("2025-08-02T13:00:00Z"),
        deletedAt: null
    },
    // User 3: One public, one private
    {
        userId: "3",
        tripId: "3",
        title: "Leh Adventure Begins",
        note: "Acclimatizing in Leh and preparing for Pangong Lake ride.",
        isPublic: true,
        location: {
            lat: 34.1526,
            lng: 77.5771,
            placeName: "Leh, Ladakh, India"
        },
        country: "India",
        city: "Leh",
        category: "Budget",
        sections: [
            {
                dayTitle: "Day 1 - Leh City",
                content: "Explored Leh market, monasteries and tasted local food.",
                media: [
                    {
                        url: "https://example.com/leh1.jpg",
                        caption: "Leh market street",
                        type: "IMAGE",
                        uploadedAt: new Date("2025-08-03T11:30:00Z")
                    }
                ]
            }
        ],
        tags: ["Adventure", "Nature", "Budget"],
        entryDate: new Date("2025-08-03T12:00:00Z"),
        createdAt: new Date("2025-08-03T11:00:00Z"),
        updatedAt: new Date("2025-08-03T12:00:00Z"),
        deletedAt: null
    },
    {
        userId: "3",
        tripId: "3",
        title: "Leh Private Journal",
        note: "Personal notes on acclimatization and local food.",
        isPublic: false,
        location: {
            lat: 34.1526,
            lng: 77.5771,
            placeName: "Leh, Ladakh, India"
        },
        country: "India",
        city: "Leh",
        category: "Food",
        sections: [
            {
                dayTitle: "Day 1 - Food Trail",
                content: "Tried momos and thukpa at a local eatery.",
                media: [
                    {
                        url: "https://example.com/leh-food.jpg",
                        caption: "Momos and thukpa",
                        type: "IMAGE",
                        uploadedAt: new Date("2025-08-03T13:00:00Z")
                    }
                ]
            }
        ],
        tags: ["Food", "Private", "Leh"],
        entryDate: new Date("2025-08-03T14:00:00Z"),
        createdAt: new Date("2025-08-03T13:00:00Z"),
        updatedAt: new Date("2025-08-03T14:00:00Z"),
        deletedAt: null
    },
    // User 4: One public, one private
    {
        userId: "4",
        tripId: "4",
        title: "Yosemite National Park Journey",
        note: "Capturing Yosemite’s iconic spots across 3 days.",
        isPublic: true,
        location: {
            lat: 37.8651,
            lng: -119.5383,
            placeName: "Yosemite National Park, USA"
        },
        country: "USA",
        city: "Yosemite",
        category: "Nature",
        sections: [
            {
                dayTitle: "Day 1 - Yosemite Valley",
                content: "Explored Yosemite Valley waterfalls and trails.",
                media: [
                    {
                        url: "https://example.com/yosemite1.jpg",
                        caption: "Yosemite Valley view",
                        type: "IMAGE",
                        uploadedAt: new Date("2025-08-04T12:30:00Z")
                    }
                ]
            }
        ],
        tags: ["Nature", "Family", "Culture", "Hiking", "Wildlife", "Viewpoint"],
        entryDate: new Date("2025-08-04T13:00:00Z"),
        createdAt: new Date("2025-08-04T12:00:00Z"),
        updatedAt: new Date("2025-08-07T10:00:00Z"),
        deletedAt: null
    },
    {
        userId: "4",
        tripId: "4",
        title: "Yosemite Private Reflections",
        note: "Personal thoughts and sketches from Yosemite.",
        isPublic: false,
        location: {
            lat: 37.8651,
            lng: -119.5383,
            placeName: "Yosemite National Park, USA"
        },
        country: "USA",
        city: "Yosemite",
        category: "Reflection",
        sections: [
            {
                dayTitle: "Day 2 - Sketching by the Falls",
                content: "Spent the afternoon sketching waterfalls.",
                media: [
                    {
                        url: "https://example.com/yosemite-sketch.jpg",
                        caption: "Waterfall sketch",
                        type: "IMAGE",
                        uploadedAt: new Date("2025-08-05T15:00:00Z")
                    }
                ]
            }
        ],
        tags: ["Reflection", "Private", "Art"],
        entryDate: new Date("2025-08-05T16:00:00Z"),
        createdAt: new Date("2025-08-05T15:00:00Z"),
        updatedAt: new Date("2025-08-05T16:00:00Z"),
        deletedAt: null
    }
]);

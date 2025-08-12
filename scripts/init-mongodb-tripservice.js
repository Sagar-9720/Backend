// MongoDB initialization script for trip-service demo travel journals
// Run with: mongo < scripts/init-mongodb-tripservice.js

db = db.getSiblingDB('trip_service_db');
db.travel_journals.insertMany([
  {
    userId: "1",
    tripId: "1",
    title: "Manali Arrival",
    note: "Arrived in Manali, checked into hotel, and explored Mall Road.",
    entryDate: new Date("2025-08-01T10:00:00Z"),
    location: {
      lat: 32.2432,
      lng: 77.1892,
      placeName: "Manali, Himachal Pradesh, India"
    },
    tags: ["Adventure", "Nature", "Summer"],
    isPublic: true,
    images: [
      { url: "https://example.com/manali1.jpg", caption: "Mall Road" },
      { url: "https://example.com/manali2.jpg", caption: "Hotel View" }
    ],
    createdAt: new Date("2025-08-01T09:00:00Z"),
    updatedAt: new Date("2025-08-01T10:00:00Z")
  },
  {
    userId: "2",
    tripId: "2",
    title: "Snow Day",
    note: "Enjoyed the snow and visited Solang Valley.",
    entryDate: new Date("2025-08-02T11:00:00Z"),
    location: {
      lat: 32.3182,
      lng: 77.1587,
      placeName: "Solang Valley, Himachal Pradesh, India"
    },
    tags: ["Adventure", "Winter", "Family"],
    isPublic: false,
    images: [
      { url: "https://example.com/solang1.jpg", caption: "Snow Fun" }
    ],
    createdAt: new Date("2025-08-02T10:00:00Z"),
    updatedAt: new Date("2025-08-02T11:00:00Z")
  },
  {
    userId: "3",
    tripId: "3",
    title: "Leh Adventure Begins",
    note: "Acclimatizing in Leh and preparing for Pangong Lake.",
    entryDate: new Date("2025-08-03T12:00:00Z"),
    location: {
      lat: 34.1526,
      lng: 77.5771,
      placeName: "Leh, Ladakh, India"
    },
    tags: ["Adventure", "Nature", "Budget"],
    isPublic: true,
    images: [
      { url: "https://example.com/leh1.jpg", caption: "Leh City" }
    ],
    createdAt: new Date("2025-08-03T11:00:00Z"),
    updatedAt: new Date("2025-08-03T12:00:00Z")
  },
  {
    userId: "4",
    tripId: "4",
    title: "Yosemite Day 1",
    note: "Explored Yosemite Valley and enjoyed the waterfalls.",
    entryDate: new Date("2025-08-04T13:00:00Z"),
    location: {
      lat: 37.8651,
      lng: -119.5383,
      placeName: "Yosemite National Park, USA"
    },
    tags: ["Nature", "Family", "Culture"],
    isPublic: false,
    images: [
      { url: "https://example.com/yosemite1.jpg", caption: "Yosemite Valley" }
    ],
    createdAt: new Date("2025-08-04T12:00:00Z"),
    updatedAt: new Date("2025-08-04T13:00:00Z")
  },
  {
    userId: "4",
    tripId: "4",
    title: "Yosemite Day 2",
    note: "Hiked to Vernal Fall and spotted a deer on the trail.",
    entryDate: new Date("2025-08-05T09:30:00Z"),
    location: {
      lat: 37.7286,
      lng: -119.5360,
      placeName: "Vernal Fall, Yosemite National Park, USA"
    },
    tags: ["Hiking", "Wildlife", "Nature"],
    isPublic: true,
    images: [
      { url: "https://example.com/vernalfall.jpg", caption: "Vernal Fall" },
      { url: "https://example.com/deer.jpg", caption: "Deer on Trail" }
    ],
    createdAt: new Date("2025-08-05T08:00:00Z"),
    updatedAt: new Date("2025-08-05T09:30:00Z")
  },
  {
    userId: "4",
    tripId: "4",
    title: "Yosemite Day 3",
    note: "Visited Glacier Point for breathtaking views of Half Dome.",
    entryDate: new Date("2025-08-06T15:00:00Z"),
    location: {
      lat: 37.7304,
      lng: -119.5730,
      placeName: "Glacier Point, Yosemite National Park, USA"
    },
    tags: ["Viewpoint", "Photography", "Nature"],
    isPublic: true,
    images: [
      { url: "https://example.com/glacierpoint.jpg", caption: "Glacier Point View" }
    ],
    createdAt: new Date("2025-08-06T14:00:00Z"),
    updatedAt: new Date("2025-08-06T15:00:00Z")
  },
  {
    userId: "4",
    tripId: "4",
    title: "Yosemite Farewell",
    note: "Packed up and said goodbye to Yosemite. Already planning the next trip!",
    entryDate: new Date("2025-08-07T10:00:00Z"),
    location: {
      lat: 37.8651,
      lng: -119.5383,
      placeName: "Yosemite National Park, USA"
    },
    tags: ["Farewell", "Memories", "Travel"],
    isPublic: false,
    images: [
      { url: "https://example.com/yosemite-farewell.jpg", caption: "Goodbye Yosemite" }
    ],
    createdAt: new Date("2025-08-07T09:00:00Z"),
    updatedAt: new Date("2025-08-07T10:00:00Z")
  }
]);

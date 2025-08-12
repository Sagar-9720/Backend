// MongoDB initialization script for user-service demo data
// Run with: mongo < scripts/init-mongodb-user-service.js

// 1. Comments collection
db = db.getSiblingDB('user_service_db');
db.comments.insertMany([
  {
    user_id: 1, // Admin User
    trip_id: 1, // Manali Summer Escape
    comment: 'Amazing trip! Highly recommended.',
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    user_id: 2, // Subadmin One
    destination_id: 1, // Manali
    comment: 'Beautiful destination, loved the scenery.',
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    user_id: 3, // Subadmin Two
    itinerary_id: 1, // Manali Adventure Day 1
    comment: 'Mall Road was fun!',
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    user_id: 4, // Regular User
    trip_id: 2, // Manali Winter Wonderland
    comment: 'Snow experience was magical.',
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

// 2. Trip Journals collection
db.journals.insertMany([
  {
    user_id: 1, // Admin User
    trip_id: 1, // Manali Summer Escape
    title: 'Day 1 in Manali',
    content: 'Arrived in Manali, explored Mall Road and local food.',
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    user_id: 4, // Regular User
    trip_id: 2, // Manali Winter Wonderland
    title: 'Snowfall Memories',
    content: 'First time seeing snow, built a snowman!',
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

// Add more demo data as needed.


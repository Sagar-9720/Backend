// MongoDB initialization script for user-service demo data
// Run with: mongo < scripts/init-mongodb-user-service.js

// 1. Comments collection
db = db.getSiblingDB('user_service');
db.comments.insertMany([
  {
    user_id: 1, // Admin User
    trip_id: 2, // Manali Summer Escape
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
// db.journals.insertMany([...]) // Removed: user-service should not have journals collection

// Add more demo data as needed.

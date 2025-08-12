// MongoDB initialization script for trip-service demo journals
// Run with: mongo < scripts/init-mongodb-tripservice.js

db = db.getSiblingDB('trip_service_db');
db.journals.insertMany([
  {
    user_id: 1, // Admin User
    trip_id: 1, // Manali Summer Escape
    title: 'Manali Arrival',
    content: 'Arrived in Manali, checked into hotel, and explored Mall Road.',
    tags: ['Adventure', 'Nature', 'Summer'],
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    user_id: 2, // Subadmin One
    trip_id: 2, // Manali Winter Wonderland
    title: 'Snow Day',
    content: 'Enjoyed the snow and visited Solang Valley.',
    tags: ['Adventure', 'Winter', 'Family'],
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    user_id: 3, // Subadmin Two
    trip_id: 3, // Leh Adventure
    title: 'Leh Adventure Begins',
    content: 'Acclimatizing in Leh and preparing for Pangong Lake.',
    tags: ['Adventure', 'Nature', 'Budget'],
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    user_id: 4, // Regular User
    trip_id: 4, // Yosemite Explorer
    title: 'Yosemite Day 1',
    content: 'Explored Yosemite Valley and enjoyed the waterfalls.',
    tags: ['Nature', 'Family', 'Culture'],
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

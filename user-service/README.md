# Social Features Service - Travel Mate

This service provides social features for the Travel Mate application, specifically handling comments, likes, and saved trips for trips, itineraries, and destinations.

## Features

- **Saved Trips**: Save trips, itineraries, or destinations
- **Comments**: Comment on trips, itineraries, or destinations
- **Likes**: Like/unlike trips, itineraries, or destinations

## Tech Stack

- **Backend**: Node.js with Express.js
- **Language**: TypeScript
- **Databases**: 
  - PostgreSQL (for likes, saved trips)
  - MongoDB (for comments)
- **ORM**: Sequelize for PostgreSQL, Mongoose for MongoDB

## Project Structure

- controllers/
- routes/
- models/
- services/
- interfaces/
- exceptions/
- response/

## Usage

This service is intended to be used as part of the Travel Mate backend, providing APIs for social interactions. All authentication and user management are handled by the authservice.

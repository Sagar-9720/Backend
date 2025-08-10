# TripService API Documentation

This service manages trip-related data and operations for the TravelMate platform.

## Base URLs
- `/api/trip/trips` (Trips)
- `/api/trip/countries` (Countries)
- `/api/trip/destinations` (Destinations)
- `/api/trip/itineraries` (Itineraries)
- `/api/trip/regions` (Regions)
- `/api/trip/journals` (Travel Journals)
- `/api/trip/tags` (Tags)

## Data Models (DTOs)

### TripModel
- `id` (Long, optional for create)
- `title` (String, required)
- `description` (String, required)
- `startDate` (LocalDateTime, required)
- `endDate` (LocalDateTime, required)
- `price` (BigDecimal, required)
- `mainDestinationId` (Long, required)
- `createdBy` (String, optional)
- `itineraries` (List<Itinerary>, optional)

### DestinationModel
- `id` (Long, optional for create)
- `name` (String, required)
- `regionId` (Long, required)
- `description` (String, required)
- `imageUrl` (String, optional)

### ItineraryModel
- `id` (Long, optional for create)
- `itineraryName` (String, required)
- `destinationId` (Long, required)
- `dayNumber` (Integer, required)
- `description` (String, required)
- `arrivalTime` (String, optional)
- `departureTime` (String, optional)

### TravelJournalModel
- `id` (String, optional for create)
- `userId` (String, required)
- `tripId` (String, required)
- `title` (String, required)
- `note` (String, required)
- `entryDate` (LocalDateTime, required)
- `location` (Location, optional)
- `tags` (List<String>, optional)
- `isPublic` (Boolean, required)
- `images` (List<ImageEntry>, optional)
- `createdAt` (LocalDateTime, optional)
- `updatedAt` (LocalDateTime, optional)

### CountryModel
- `id` (Long, optional for create)
- `name` (String, required)

### RegionModel
- `id` (Long, optional for create)
- `name` (String, required)
- `countryId` (Long, required)

### TagModel
- `id` (Long, optional for create)
- `name` (String, required)
- `usageCount` (Long, optional)

## Endpoints

### Trips
- **GET /** — List all trips (public)
  - **Response:** `CustomResponseEntity<List<TripModel>>`
- **GET /{id}** — Get trip by ID (public)
  - **Response:** `CustomResponseEntity<TripModel>`
- **POST /** — Create trip (**JWT required**)
  - **Request:** `TripModel` (see above for required fields)
  - **Response:** `CustomResponseEntity<TripModel>`
- **PUT /{id}** — Update trip (**JWT required**)
  - **Request:** `TripModel`
  - **Response:** `CustomResponseEntity<TripModel>`
- **DELETE /{id}** — Delete trip (**JWT required**)
  - **Response:** `CustomResponseEntity<Void>`
- **POST /request** — Request a trip (**JWT required**)
  - **Request:** `TripModel`
  - **Response:** `CustomResponseEntity<List<TripModel>>`
- **POST /approve/{requestId}** — Approve trip request (**JWT required**)
  - **Request:** `TripRequest`
  - **Response:** `CustomResponseEntity<TripModel>`
- **GET /by-destination?destinationName=...** — Get trips by destination name (public)
  - **Response:** `CustomResponseEntity<List<TripModel>>`
- **GET /by-price-range?startPrice=...&endPrice=...** — Get trips by price range (public)
  - **Response:** `CustomResponseEntity<List<TripModel>>`
- **GET /requests/user/{userId}** — Get trip requests by user (public)
  - **Response:** `CustomResponseEntity<List<TripModel>>`
- **POST /auto-delete** — Auto-delete trips by date (admin/internal)
  - **Response:** `CustomResponseEntity<Void>`

### Countries
- **GET /** — List all countries (public)
  - **Response:** `CustomResponseEntity<List<CountryModel>>`

### Destinations
- **GET /** — List all destinations (public)
  - **Response:** `CustomResponseEntity<List<DestinationModel>>`
- **GET /{id}** — Get destination by ID (public)
  - **Response:** `CustomResponseEntity<DestinationModel>`
- **POST /** — Create destination (**JWT required**)
  - **Request:** `DestinationModel`
  - **Response:** `CustomResponseEntity<DestinationModel>`
- **PUT /{id}** — Update destination (**JWT required**)
  - **Request:** `DestinationModel`
  - **Response:** `CustomResponseEntity<DestinationModel>`
- **DELETE /{id}** — Delete destination (**JWT required**)
  - **Response:** `CustomResponseEntity<Void>`
- **GET /region/{regionId}** — Get destinations by region (public)
  - **Response:** `CustomResponseEntity<List<DestinationModel>>`
- **GET /country/{countryId}** — Get destinations by country (public)
  - **Response:** `CustomResponseEntity<List<DestinationModel>>`
- **GET /search?name=...** — Search destinations by name (public)
  - **Response:** `CustomResponseEntity<List<DestinationModel>>`

### Itineraries
- **GET /** — List all itineraries (public)
  - **Response:** `CustomResponseEntity<List<ItineraryModel>>`
- **GET /{id}** — Get itinerary by ID (public)
  - **Response:** `CustomResponseEntity<ItineraryModel>`
- **POST /** — Create itinerary (**JWT required**)
  - **Request:** `ItineraryModel`
  - **Response:** `CustomResponseEntity<ItineraryModel>`
- **PUT /** — Update itinerary (**JWT required**)
  - **Request:** `ItineraryModel`
  - **Response:** `CustomResponseEntity<ItineraryModel>`
- **DELETE /{id}** — Delete itinerary (**JWT required**)
  - **Response:** `CustomResponseEntity<ItineraryModel>`
- **GET /destination/{destinationId}** — Get itineraries by destination (public)
  - **Response:** `CustomResponseEntity<List<ItineraryModel>>`

### Regions
- **GET /** — List all regions (public)
  - **Response:** `CustomResponseEntity<List<RegionModel>>`

### Travel Journals
- **GET /** — List all public journals (public)
  - **Response:** `CustomResponseEntity<List<TravelJournalModel>>`
- **POST /** — Create journal (**JWT required**)
  - **Request:** `TravelJournalModel`
  - **Response:** `CustomResponseEntity<TravelJournalModel>`
- **PUT /** — Update journal (**JWT required**)
  - **Request:** `TravelJournalModel`
  - **Response:** `CustomResponseEntity<TravelJournalModel>`
- **DELETE /{id}** — Delete journal (**JWT required**)
  - **Response:** `CustomResponseEntity<TravelJournalModel>`

### Tags
- **GET /** — List all tags (public)
  - **Response:** `CustomResponseEntity<List<TagModel>>`
- **GET /{id}** — Get tag by ID (public)
  - **Response:** `CustomResponseEntity<TagModel>`
- **GET /name/{name}** — Get tag by name (public)
  - **Response:** `CustomResponseEntity<TagModel>`
- **POST /** — Create tag (may require authentication)
  - **Request:** `TagModel`
  - **Response:** `CustomResponseEntity<TagModel>`
- **DELETE /{id}** — Delete tag (may require authentication)
  - **Response:** `CustomResponseEntity<Void>`

## Security
- All modifying endpoints (POST, PUT, DELETE) require a valid JWT in the `Authorization` header: `Bearer <token>`
- Read-only endpoints (GET) are public unless otherwise specified.

## Error Handling
- All responses are wrapped in `CustomResponseEntity` with status, message, data, and path.
- Standard HTTP status codes are used.

## Data Models
- See the `model` and `entity` packages for full request/response model details.

---
For more details, see the source code or contact the maintainers.

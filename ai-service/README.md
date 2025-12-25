# ai-service

Node/TypeScript microservice that provides AI-powered endpoints (starting with trending trips) by aggregating data from:
- trip-service
- journal-service
- user-service (stats API)

## Endpoints

- `GET /health`
- `GET /api/ai/trending?entity=trip&limit=10&debug=true`

## Environment

- `PORT` (default 8086)
- `TRIP_SERVICE_BASE_URL` (default `http://trip-service:8082`)
- `JOURNAL_SERVICE_BASE_URL` (default `http://journal-service:8081`)
- `USER_SERVICE_BASE_URL` (default `http://user-service:5000`)
- `REDIS_URL` (optional) e.g. `redis://redis-master:6379`

## Notes

This is an MVP without Gemini/MCP tool-calling yet. Next steps:
- add MCP server + tool registry
- add Gemini integration
- expand trending to destination/journals
- add home feed endpoint


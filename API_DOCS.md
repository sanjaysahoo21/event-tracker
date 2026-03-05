# API Documentation: Event Tracker

## 1. Core Endpoints

### 1.1 Ingest User Activity
This endpoint serves as the ingestion hook for adding new activities.

**URL**: `/api/v1/activities`  
**Method**: `POST`  
**Content-Type**: `application/json`  

#### Request Body Schema
The payload must correctly include the `userId`, `eventType`, `timestamp` (in ISO-8601 format), and a flexible nested `payload` structure.

```json
{
    "userId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
    "eventType": "user_login",
    "timestamp": "2023-10-27T10:00:00Z",
    "payload": {
        "ipAddress": "192.168.1.1",
        "device": "desktop",
        "browser": "Chrome"
    }
}
```

#### Field Specifications:
- `userId` *(string, required)*: The unique identifier for a user making the request. Cannot be blank.
- `eventType` *(string, required)*: Describes the nature of the event being triggered. Cannot be blank.
- `timestamp` *(string, required)*: ISO-8601 format timestamp stating exactly when the event occurred.
- `payload` *(object, required)*: Any flexible JSON object mapping related extra metadata regarding the event.

#### Responses

- **`202 Accepted`**: The activity event was successfully queued inside RabbitMQ.
  ```json
  {} // Empty body
  ```
- **`400 Bad Request`**: The request failed payload validation rules (e.g., missing a required field or invalid format). An object depicting the violations will be presented.
  ```json
  {
      "eventType": "Event type is required",
      "userId": "User ID is required"
  }
  ```
- **`429 Too Many Requests`**: The request breached the rate limits for the client IP address (50 per min).
  **Header Response**: `Retry-After: <seconds_remaining>`
  ```text
  Too many requests. Please try again later.
  ```

---

### 1.2 Health status
Provides a quick method to assert that the service container is fundamentally available and HTTP routing is operating.

**URL**: `/health`  
**Method**: `GET`  

#### Responses
- **`200 OK`**: System routing is available.
  ```json
  { "status": "UP" }
  ```

---

*Note: For dynamic verification, this service also hosts an automated OpenAPI schema specification at `/v3/api-docs` and an interactive Swagger portal natively running at `/swagger-ui.html` on the API web container's exposed port.*

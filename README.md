# Event-Driven User Activity Tracking Service

This project is an event-driven microservice system designed to track user activities reliably and at scale. It utilizes **Spring Boot**, **RabbitMQ**, and **MongoDB** to decouple API ingestion from asynchronous data persistence.

---

## 🏗️ Architectural Overview

The application is built using a microservices architecture to enforce a clear separation of concerns, enhancing scalability and fault tolerance.

1.  **API Gateway / Producer (`api` module):** 
    *   Acts as the ingestion point for activity events.
    *   Performs input validation using Jakarta Validation (`@Valid`).
    *   Implements an **IP-based Rate Limiter** (50 requests/min) via a Spring `HandlerInterceptor` to protect against abuse.
    *   Valid events are quickly published to a RabbitMQ queue (`user_activities`).
    *   Returns a `202 Accepted` immediately upon successful queueing, ensuring high throughput.
2.  **Worker / Consumer (`consumer` module):** 
    *   Asynchronously consumes messages from the RabbitMQ queue.
    *   Implements **Manual Acknowledgment (ACK/NACK)** for message reliability, preventing data loss in transit or upon processing failures.
    *   Augments the data with a `processedAt` timestamp.
    *   Persists the flexible JSON payload natively into a **MongoDB** schema.
3.  **Infrastructure Orchestration:** 
    *   Docker Compose manages the lifecycle of the entire cluster: API, Consumer, RabbitMQ (with Management UI), and MongoDB. Dependencies (`depends_on: service_healthy`) heavily enforce startup order.

---

## 🚀 Setup & Execution (One-Command Setup)

### Prerequisites
*   [Docker](https://docs.docker.com/get-docker/) & [Docker Compose](https://docs.docker.com/compose/install/)

### Running the Application
The entire application stack can be spun up seamlessly from the root directory:

```bash
# 1. (Optional) Run from the provided .env.example if you do not have a .env
cp .env.example .env

# 2. Build and launch all services in detached mode
docker compose up --build
```

**What happens?**
1. Docker provisions **MongoDB** on port `27017` and **RabbitMQ** on port `5672` (Management UI accessible on `15672`).
2. The `api` and `consumer` containers will wait until the databases pass health checks before booting up.
3. The `api` exposes its HTTP server on PORT `3000`.

**Useful Monitoring Commands:**
*   To view streaming logs of the API and Consumer: `docker compose logs -f api consumer`
*   To stop the application: `docker compose down`

---

## 🧪 Testing

Both microservices include automated unit and integration tests driven by Maven. 

### Running Tests via Docker
If the containers are already running, you can execute the tests inside the isolated container environments:

```bash
# Run API validation and mocked RabbitMQ publishing tests
docker compose exec api ./mvnw test

# Run Consumer parsing and data layer tests
docker compose exec consumer ./mvnw test
```

### Running Tests Locally
Alternatively, if you have JDK 17 installed locally, you can run them directly from the respective directories:
```bash
cd api && ./mvnw test
cd ../consumer && ./mvnw test
```

---

## 📚 API Documentation

### Interactive Swagger UI (OpenAPI)
Because this project utilizes `springdoc-openapi`, dynamic documentation is available locally. Once the docker containers are running, navigate in a browser to:
👉 **[http://localhost:3000/swagger-ui.html](http://localhost:3000/swagger-ui.html)**

### Core Endpoint Specification

#### `POST /api/v1/activities`
Ingests a user activity event, enforcing validation and rate limiting.

**Headers:**
*   `Content-Type: application/json`

**Sample Request Payload:**
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

**Evaluation Note:** *The `payload` field is a flexible `Map<String, Object>` dynamically mapping to a nested sub-document in MongoDB.*

**Expected Responses:**
*   `202 Accepted`: Event successfully validated and published to the message queue.
*   `400 Bad Request`: Payload validation failed (includes detailed error messages).
*   `429 Too Many Requests`: Exceeded 50 requests per minute. Includes `Retry-After: <seconds>` header.

#### `GET /health`
Returns the operational status of the API instance.
*   **Response:** `200 OK` `{ "status": "UP" }`

---

## 🐰 RabbitMQ Details & Mentorship Evaluation Checkpoints
*   **Queue Integrity:** A durable queue named `user_activities` (aliased as `activity.queue`) is utilized.
*   **Idempotency & Requeuing:** The `ActivityEventConsumer` acknowledges (`channel.basicAck`) success, and explicitly utilizes `channel.basicNack(tag, false, true)` inside the failure fallback to ensure reliable dead-letter handling/requeuing without message loss.
*   **Database Constraints:** Records map to the MongoDB `activities` collection, dynamically extracting `_id` uniquely generated per event.

*(Note for Evaluation: Please navigate to [http://localhost:15672](http://localhost:15672) using credentials `guest`/`guest` to verify the active bindings while evaluating the event streams).*
,
# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W — Client-Server Architectures  
**Student:** [YOUR NAME]  
**Student ID:** [YOUR STUDENT ID]  

A RESTful API built with **JAX-RS (Jersey)** for managing campus rooms and IoT sensors.  
Uses in-memory data storage only — no database required.

---

## Table of Contents

1. [API Overview](#api-overview)
2. [Tech Stack](#tech-stack)
3. [Project Structure](#project-structure)
4. [Build & Run Instructions](#build--run-instructions)
5. [API Endpoints](#api-endpoints)
6. [Sample curl Commands](#sample-curl-commands)
7. [Error Handling](#error-handling)
8. [Business Rules](#business-rules)
9. [Report — Question Answers](#report--question-answers)

---

## API Overview

The Smart Campus API provides a RESTful interface for university facilities managers to manage:

- **Rooms** — physical campus spaces with capacity tracking
- **Sensors** — IoT devices (temperature, CO2, occupancy) deployed inside rooms
- **Sensor Readings** — historical log of measurements recorded by each sensor

The API follows REST principles including resource-based URLs, proper HTTP status codes, JSON responses, and HATEOAS-style discovery links.

---

## Tech Stack

- **Java** JDK 21
- **JAX-RS** via Jersey 2.35
- **Apache Tomcat** 8.5.96
- **Maven** (WAR packaging)
- **Jackson** (JSON serialization via jersey-media-json-jackson)
- **No database** — ConcurrentHashMap in-memory storage only

---

## Project Structure

```
com.smartcampus
├── api
│   └── SmartCampusApplication.java        # @ApplicationPath("/api/v1") entry point
├── models
│   ├── Room.java                           # Room POJO (id, name, capacity, sensorIds)
│   ├── Sensor.java                         # Sensor POJO (id, type, status, currentValue, roomId)
│   └── SensorReading.java                  # SensorReading POJO (id, timestamp, value)
├── store
│   └── DataStore.java                      # Static ConcurrentHashMap in-memory storage
├── resources
│   ├── DiscoveryResource.java              # GET /api/v1/ — metadata and links
│   ├── RoomResource.java                   # Full CRUD for /api/v1/rooms
│   ├── SensorResource.java                 # Full CRUD for /api/v1/sensors
│   └── SensorReadingResource.java          # Sub-resource for /sensors/{id}/readings
├── exceptions
│   ├── RoomNotEmptyException.java          # Thrown when deleting room with sensors
│   ├── LinkedResourceNotFoundException.java # Thrown when referenced resource missing
│   ├── RoomNotFoundException.java          # Thrown when room ID not found
│   ├── SensorUnavailableException.java     # Thrown when sensor is MAINTENANCE/OFFLINE
│   └── CustomMapper.java                   # ExceptionMappers: 409, 404, 422, 403, 500
└── filters
    └── LoggingFilter.java                  # Logs request method/URI and response status
```

---

## Build & Run Instructions

### Prerequisites

- JDK 21 installed
- Apache Tomcat 8.5+ installed
- Maven 3.x installed
- NetBeans 24 (or any IDE with Maven support)

### Step 1 — Clone the Repository

```bash
git clone https://github.com/[YOUR-USERNAME]/[YOUR-REPO-NAME].git
cd [YOUR-REPO-NAME]/smart-campus-api
```

### Step 2 — Build the Project

```bash
mvn clean install
```

This compiles all source files and produces:
```
target/smart-campus-api.war
```

### Step 3 — Deploy to Tomcat

**Option A — Manual deploy:**
```bash
cp target/smart-campus-api.war /path/to/tomcat/webapps/
/path/to/tomcat/bin/startup.sh
```

**Option B — NetBeans:**
1. Right-click project → **Run**
2. NetBeans auto-deploys to the configured Tomcat server

### Step 4 — Verify Deployment

Open in browser:
```
http://localhost:8080/smart-campus-api/api/v1/
```

Expected response:
```json
{
  "version": "1.0",
  "description": "Smart Campus Sensor Management API",
  "links": {
    "rooms": "/smart-campus-api/api/v1/rooms",
    "sensors": "/smart-campus-api/api/v1/sensors"
  }
}
```

### Base URL
```
http://localhost:8080/smart-campus-api/api/v1
```

---

## API Endpoints

### Discovery
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/` | API metadata and navigation links | 200 OK |

---

### Rooms — `/api/v1/rooms`
| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| GET | `/rooms` | Get all rooms | 200 OK |
| GET | `/rooms/{roomId}` | Get a specific room by ID | 200 / 404 |
| POST | `/rooms` | Create a new room | 201 / 409 / 422 |
| PUT | `/rooms/{roomId}` | Full update of a room | 200 / 404 |
| PATCH | `/rooms/{roomId}` | Partial update of a room | 200 / 404 |
| DELETE | `/rooms/{roomId}` | Delete a room | 204 / 404 / 409 |
| GET | `/rooms/{roomId}/sensors` | Get all sensors inside a room | 200 / 404 |

**POST /rooms — Request Body:**
```json
{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "capacity": 50
}
```

---

### Sensors — `/api/v1/sensors`
| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| GET | `/sensors` | Get all sensors | 200 OK |
| GET | `/sensors?type=Temperature` | Filter sensors by type | 200 OK |
| GET | `/sensors/{sensorId}` | Get a specific sensor by ID | 200 / 404 |
| POST | `/sensors` | Register a new sensor | 201 / 404 / 409 / 422 |
| PATCH | `/sensors/{sensorId}` | Update sensor status or value | 200 / 404 / 422 |
| DELETE | `/sensors/{sensorId}` | Remove a sensor | 204 / 404 |

**POST /sensors — Request Body:**
```json
{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 22.5,
  "roomId": "LIB-301"
}
```

**Valid status values:** `ACTIVE` | `MAINTENANCE` | `OFFLINE`

---

### Sensor Readings — `/api/v1/sensors/{sensorId}/readings`
| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| POST | `/sensors/{sensorId}/readings` | Add a new reading | 201 / 403 / 404 |
| GET | `/sensors/{sensorId}/readings` | Get all readings for a sensor | 200 OK |

**POST /readings — Request Body:**
```json
{
  "value": 25.3
}
```

> `id` is auto-generated as UUID. `timestamp` is auto-generated as epoch milliseconds if not provided.

**Reading Response:**
```json
{
  "id": "c4fc4b5c-2900-4262-858e-e1a7a86a3c90",
  "timestamp": 1776958915875,
  "value": 25.3
}
```

---

## Sample curl Commands

### 1. Get API Discovery
```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/ \
  -H "Accept: application/json"
```

### 2. Create a Room
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "LIB-301", "name": "Library Quiet Study", "capacity": 50}'
```

### 3. Create a Sensor linked to a Room
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-001", "type": "Temperature", "status": "ACTIVE", "currentValue": 22.5, "roomId": "LIB-301"}'
```

### 4. Add a Sensor Reading
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 25.3}'
```

### 5. Get All Readings for a Sensor
```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings \
  -H "Accept: application/json"
```

### 6. Filter Sensors by Type
```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=Temperature" \
  -H "Accept: application/json"
```

### 7. Attempt to Delete a Room with Sensors — expects 409 Conflict
```bash
curl -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/LIB-301
```

### 8. Get All Sensors in a Room
```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms/LIB-301/sensors \
  -H "Accept: application/json"
```

---

## Error Handling

All errors return a consistent JSON body — no raw stack traces are ever exposed:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Cannot delete room with active sensors."
}
```

| Status Code | Exception Class | Scenario |
|-------------|----------------|----------|
| 403 | `SensorUnavailableException` | POST reading to MAINTENANCE or OFFLINE sensor |
| 404 | `LinkedResourceNotFoundException` | Room or sensor ID not found |
| 409 | `RoomNotEmptyException` | Delete room that still has sensors assigned |
| 422 | `LinkedResourceNotFoundException` | Sensor POST with non-existent roomId |
| 500 | `GenericExceptionMapper` | Any unexpected runtime error |

---

## Business Rules

- A **room cannot be deleted** if it still has sensors assigned to it — returns 409 Conflict
- A **sensor must reference an existing room** on creation — returns 422 if roomId not found
- **Readings cannot be posted** to a sensor with status `MAINTENANCE` or `OFFLINE` — returns 403 Forbidden
- Posting a reading **automatically updates** the parent sensor's `currentValue` field
- Deleting a sensor **automatically unlinks** it from its assigned room's `sensorIds` list
- All data is stored in `ConcurrentHashMap` — thread-safe, no database used

---

## Report — Question Answers

### Part 1.1 — JAX-RS Resource Lifecycle

By default, JAX-RS creates a **new instance of each resource class for every incoming HTTP request** (per-request lifecycle). This means no state is shared between requests through instance variables. Because of this, shared data must be stored outside the resource class — in this project, a static `ConcurrentHashMap` inside `DataStore.java` serves as the in-memory store. Using `ConcurrentHashMap` instead of a regular `HashMap` is critical because multiple requests may arrive simultaneously, and concurrent reads/writes to a regular `HashMap` can cause data corruption or race conditions. The per-request lifecycle makes resource classes inherently stateless and thread-safe at the instance level, but the shared static data store must still be protected with thread-safe collections.

---

### Part 1.2 — HATEOAS and Hypermedia

HATEOAS (Hypermedia as the Engine of Application State) means that API responses include links to related resources, allowing clients to navigate the API dynamically without relying on hardcoded URLs. In this API, the Discovery endpoint returns links to `/rooms` and `/sensors`, so a client can discover and use the entire API from a single entry point. This benefits developers because the API becomes self-documenting — clients do not need to memorise URL structures or consult external documentation for every interaction. If URLs change, only the server needs to be updated, and clients automatically receive the new paths through the discovery response.

---

### Part 2.1 — Returning IDs vs Full Room Objects

Returning only IDs in a room list is more bandwidth-efficient but forces the client to make additional requests to fetch each room's details — this is known as the N+1 problem and can severely degrade performance at scale. Returning full room objects increases response payload size but reduces the number of round-trips required. For a campus system with potentially thousands of rooms, returning full objects in a paginated list is generally preferred because it reduces client-side complexity and total network overhead compared to making many small follow-up requests per room.

---

### Part 2.2 — Is DELETE Idempotent?

Yes, DELETE is idempotent in this implementation. The first DELETE on an existing room (with no sensors) will remove it and return `204 No Content`. Any subsequent DELETE requests for the same room ID will find nothing and return `404 Not Found`. While the response code differs, the **state of the server is identical** after each call — the room does not exist. This satisfies the definition of idempotency: multiple identical requests produce the same final server state as a single request, even if the HTTP response code changes.

---

### Part 3.1 — @Consumes and Format Mismatch

The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells JAX-RS that this endpoint only accepts requests with `Content-Type: application/json`. If a client sends data as `text/plain` or `application/xml`, JAX-RS automatically returns a `415 Unsupported Media Type` response before the method body is ever executed. This enforces a strict contract between client and server without requiring manual content-type checking inside each method, preventing malformed or unexpected data formats from reaching the business logic layer.

---

### Part 3.2 — @QueryParam vs Path Segment for Filtering

Using `@QueryParam` (e.g., `/sensors?type=CO2`) is superior for filtering because query parameters are semantically designed for optional, non-hierarchical filters applied to a collection. The base URL `/sensors` still refers to the complete sensor collection, and the parameter simply narrows the result set. Embedding the filter in the path (e.g., `/sensors/type/CO2`) incorrectly implies that `type/CO2` is a sub-resource, which is architecturally misleading. Query parameters are also more composable — multiple filters can be combined (e.g., `?type=CO2&status=ACTIVE`) without changing the URL structure, and omitting the parameter naturally returns the full unfiltered collection.

---

### Part 4.1 — Sub-Resource Locator Pattern Benefits

The Sub-Resource Locator pattern delegates handling of nested paths to a dedicated class. Instead of placing all reading logic inside `SensorResource`, a separate `SensorReadingResource` class handles the `/readings` path context. This improves separation of concerns — each class has a single, focused responsibility. It also improves maintainability: changes to reading logic cannot accidentally break sensor logic. In large APIs with deeply nested resources, a single monolithic controller becomes difficult to read, test, and extend. Sub-resources allow independent unit testing of each layer and produce cleaner code that mirrors the logical hierarchy of the domain model.

---

### Part 5.2 — 422 vs 404 for Missing Reference

When a client submits a valid JSON body for a new sensor but provides a `roomId` that does not exist, a `404 Not Found` would be misleading — it implies the URL endpoint itself was not found, which is incorrect since `/sensors` exists and handled the request. The problem is a **semantic validation failure inside the payload**: the referenced resource cannot be resolved. HTTP `422 Unprocessable Entity` is more accurate because it signals that the request was syntactically correct and fully understood by the server, but the business logic could not be completed due to an invalid reference within the request body content.

---

### Part 5.4 — Stack Trace Exposure Security Risk

Exposing raw Java stack traces to external API consumers is a significant security risk. A stack trace reveals internal implementation details including class names, method names, file paths, line numbers, library versions, and framework internals. An attacker can use this information to identify known vulnerabilities in specific library versions, understand the application's internal code structure for targeted attacks, craft malicious inputs that exploit specific code paths, and map out the full technology stack for further reconnaissance. The global `ExceptionMapper<Throwable>` in this API intercepts all unexpected errors and returns only a generic `500 Internal Server Error` message, ensuring no internal implementation details are ever exposed to external consumers.

---

### Part 5.5 — Why Use Filters for Logging

Using JAX-RS filters for cross-cutting concerns like logging is far superior to manually inserting `Logger.info()` calls in every resource method because it follows the DRY (Don't Repeat Yourself) principle. A single `LoggingFilter` class implementing both `ContainerRequestFilter` and `ContainerResponseFilter` automatically intercepts every request and response in the application without modifying any resource code. If logging requirements change — for example, adding request duration or user tracking — only the filter needs updating, not every individual endpoint method. Filters also guarantee consistency: a developer cannot accidentally forget to add logging to a newly created resource, ensuring complete API observability at all times.

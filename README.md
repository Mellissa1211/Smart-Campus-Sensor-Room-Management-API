# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W — Client-Server Architectures

**Student:** Mellissa.R.G.Mangalaari
**Student ID:** 20232750 / w2153574

A RESTful API built with **JAX-RS (Jersey)** for managing campus rooms and IoT sensors. Uses in-memory data storage only — no database required.

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

- **Rooms** — physical campus spaces with capacity and occupancy tracking
- **Sensors** — IoT devices (temperature, CO2, occupancy) deployed inside rooms
- **Sensor Readings** — historical log of measurements recorded by each sensor

The API follows REST principles including resource-based URLs, proper HTTP status codes, meaningful JSON responses, and HATEOAS-style discovery links.

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
smart-campus-api/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── smartcampus/
        │           ├── api/
        │           │   └── SmartCampusApplication.java       # @ApplicationPath("/api/v1") entry point
        │           ├── models/
        │           │   ├── Room.java                         # Room POJO (id, name, capacity, sensorIds)
        │           │   ├── Sensor.java                       # Sensor POJO (id, type, status, currentValue, roomId)
        │           │   └── SensorReading.java                # SensorReading POJO (id, timestamp, value)
        │           ├── store/
        │           │   └── DataStore.java                    # Static ConcurrentHashMap in-memory storage
        │           ├── resources/
        │           │   ├── DiscoveryResource.java            # GET /api/v1/ — metadata and HATEOAS links
        │           │   ├── RoomResource.java                 # Full CRUD for /api/v1/rooms
        │           │   ├── SensorResource.java               # Full CRUD for /api/v1/sensors
        │           │   └── SensorReadingResource.java        # Sub-resource for /sensors/{id}/readings
        │           ├── exceptions/
        │           │   ├── RoomNotEmptyException.java        # Thrown when deleting room with sensors
        │           │   ├── LinkedResourceNotFoundException.java  # Thrown when referenced resource missing
        │           │   ├── RoomNotFoundException.java        # Thrown when room ID not found
        │           │   ├── SensorUnavailableException.java   # Thrown when sensor is MAINTENANCE/OFFLINE
        │           │   └── CustomMapper.java                 # ExceptionMappers: 409, 404, 422, 403, 500
        │           └── filters/
        │               └── LoggingFilter.java                # Logs request method/URI and response status
        └── webapp/
            └── WEB-INF/
                └── web.xml                                   # Servlet configuration
```

---

## Build & Run Instructions

### Prerequisites

- JDK 21
- Apache Tomcat 8.5+
- Maven 3.x
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

Open in your browser:

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

The JAX-RS specification defines a **per-request lifecycle** as the default behaviour for resource classes. This means the runtime instantiates a completely fresh object of the resource class each time an HTTP request is matched to it, and that object is discarded once the response has been sent.

This design has a direct consequence for managing in-memory data. Any field declared at the instance level of a resource class — such as `private Map<String, Room> rooms = new HashMap<>()` — would be re-initialised on every request, making it impossible to retain data between calls. To work around this, all shared state must live outside the resource instance entirely.

In this project, a dedicated `DataStore` class holds three static `ConcurrentHashMap` collections for rooms, sensors, and readings. Being static means these maps exist at the class level and persist for the entire lifetime of the application, shared across every resource instance.

The choice of `ConcurrentHashMap` over a standard `HashMap` is critical. A JAX-RS container like Tomcat processes incoming requests on a thread pool, meaning two requests can execute simultaneously. A plain `HashMap` is not thread-safe — concurrent modifications can corrupt its internal structure. `ConcurrentHashMap` uses segment-level locking to guarantee that simultaneous reads and writes from different threads do not interfere with each other, preventing data loss or race conditions.

---

### Part 1.2 — HATEOAS and Hypermedia

HATEOAS — Hypermedia As The Engine Of Application State — is the principle that an API response should contain not just data, but also **links describing what actions the client can take next**. Rather than requiring developers to memorise or hard-code every endpoint URL, the API guides the client through available transitions dynamically.

In this project, a GET request to the root `/api/v1/` returns links to `/rooms` and `/sensors`, allowing any client to discover the entire API surface from a single known entry point — the same way a web browser discovers pages by following hyperlinks rather than requiring users to type every URL manually.

The advantages over static documentation include:

- **Loose coupling:** Client code references the link returned in the response rather than a hard-coded path. If the server URL structure changes, clients automatically receive the updated path with no client-side code change required.
- **Self-describing API:** A client with no prior knowledge can explore the API by following links, making the system inherently discoverable without external reference material.
- **Reduced integration errors:** Static documentation becomes outdated as APIs evolve. HATEOAS ensures the client always receives current, server-authoritative paths.
- **Workflow guidance:** Links can contextually indicate which operations are available given the current resource state.

---

### Part 2.1 — IDs vs Full Objects in List Responses

The choice between returning IDs only or full objects in a collection endpoint involves a trade-off between network efficiency and client simplicity.

Returning only IDs is more bandwidth-efficient but forces the client to make one additional GET request per room to fetch its details — known as the **N+1 problem** — which dramatically increases total request count and degrades performance at scale. Returning full objects increases payload size but delivers all necessary data in a single round-trip, eliminating the need for follow-up requests.

For a campus-wide system managing potentially thousands of rooms, returning full objects is the preferred approach when clients need to display names, capacities, and sensor assignments together. This project returns complete Room objects to eliminate the N+1 problem and reduce overall API call volume, consistent with REST best practice for resource representation completeness.

---

### Part 2.2 — Is DELETE Idempotent?

The HTTP specification classifies DELETE as an **idempotent** method, meaning that issuing the same request multiple times must leave the server in the same state as issuing it once. Importantly, idempotency applies to **server state**, not necessarily to the HTTP response code returned.

In this implementation:

- **First call** — room exists with no sensors assigned: the room is removed and the server returns `204 No Content`.
- **Second call** — room no longer exists: a `LinkedResourceNotFoundException` is thrown and the server returns `404 Not Found`.

The server state after both calls is identical — the room does not exist in either case — which satisfies the definition of idempotency at the state level. The difference in response code (204 vs 404) is a matter of informational accuracy rather than a violation of idempotency. This project returns 404 on the second call because providing accurate feedback about what actually occurred is more useful to API consumers than masking the fact that the resource was already absent.

---

### Part 3.1 — @Consumes and Content-Type Mismatch

The `@Consumes` annotation declares a content negotiation contract between the client and the JAX-RS runtime. When a request arrives, Jersey inspects the `Content-Type` header and attempts to find a resource method whose `@Consumes` value matches it.

If a client submits a request with `Content-Type: text/plain` or `Content-Type: application/xml` to a method annotated with `@Consumes(MediaType.APPLICATION_JSON)`, the JAX-RS runtime finds no matching method and automatically returns an **HTTP 415 Unsupported Media Type** response. This happens entirely within the framework — the resource method body is never executed.

This has two important implications. First, malformed or unexpected payloads in unsupported formats are rejected at the framework level before reaching application code, reducing the attack surface. Second, developers do not need to add Content-Type checking logic inside resource methods — the annotation acts as a declarative guard that the framework enforces automatically, keeping each method focused purely on business logic.

---

### Part 3.2 — @QueryParam vs Path Segment for Filtering

The distinction comes down to the fundamental semantics of URI design in REST. A **URL path** identifies a specific resource or sub-resource. A **query parameter** modifies or refines the view of an already-identified resource.

Using `@QueryParam` (e.g., `/sensors?type=CO2`) is correct because the base URL `/sensors` still identifies the same sensor collection — the parameter simply narrows the result set. Embedding the filter in the path (e.g., `/sensors/type/CO2`) incorrectly implies that `type/CO2` is a distinct sub-resource, which violates the path-as-resource-identifier principle.

Query parameters are also more composable — multiple filters can be combined trivially (e.g., `?type=CO2&status=ACTIVE`) without modifying the URL structure, and omitting the parameter naturally returns the complete unfiltered collection. A path-based approach would require the server to define an exponentially growing set of path patterns for every possible filter combination, making the API rigid and difficult to extend.

---

### Part 4.1 — Sub-Resource Locator Pattern

The Sub-Resource Locator pattern enables a parent resource class to delegate responsibility for a nested path segment to a completely separate class. In this API, `SensorResource` handles all operations on `/api/v1/sensors` and delegates the `/readings` path to an instance of `SensorReadingResource` via a locator method annotated with `@Path` only — no HTTP method annotation.

The architectural advantages over a monolithic controller are significant:

- **Single Responsibility Principle:** `SensorResource` manages sensor registration, retrieval, and deletion. `SensorReadingResource` manages the reading history for a specific sensor context. Neither class is polluted with the other's logic.
- **Controlled complexity:** A single class handling every nested path would grow into hundreds of lines with tightly interwoven concerns. Sub-resources impose natural boundaries that keep each file readable and maintainable.
- **Independent testability:** `SensorReadingResource` can be instantiated directly with a test sensor ID and tested in complete isolation, without spinning up a full HTTP context or the parent resource class.
- **Extensibility:** Adding a further nested level simply requires creating another sub-resource class and adding one locator method, leaving all existing classes unchanged.
- **Contextual injection:** The parent locator passes the resolved `sensorId` directly into the sub-resource constructor, so the sub-resource always operates in a specific, validated context without needing to re-parse path parameters.

---

### Part 5.2 — Why HTTP 422 Instead of 404?

The distinction hinges on **what** was not found and **where** the problem originates.

**HTTP 404 Not Found** communicates that the URL the client requested does not exist on the server. It is the correct response when a client issues `GET /api/v1/rooms/NONEXISTENT-ID` — the resource identified by that URL cannot be located.

When a client issues `POST /api/v1/sensors` with a body containing `"roomId": "NONEXISTENT"`, the situation is fundamentally different. The endpoint `/api/v1/sensors` does exist — a 404 would falsely imply otherwise. The JSON syntax is valid — a 400 Bad Request would also be inaccurate. The request was received, parsed, and understood — the problem is purely semantic: the payload references an entity that cannot be resolved.

**HTTP 422 Unprocessable Entity** was specifically defined for this scenario. It tells the client precisely: *"Your request arrived correctly and your JSON is well-formed, but the data inside it refers to something that does not exist. Correct your payload and try again."* This level of specificity reduces debugging time for API consumers significantly compared to a generic 404.

---

### Part 5.4 — Security Risks of Exposing Stack Traces

Returning raw stack traces in API error responses is classified as an **information disclosure vulnerability** — one of the OWASP Top 10 security risks. The specific dangers include:

- **Dependency fingerprinting:** Stack traces expose exact library names and version numbers. An attacker can cross-reference these against published CVE databases to identify known exploits targeting that exact version.
- **Internal architecture mapping:** Package and class names reveal the internal module structure, naming conventions, and data access patterns of the application, providing a roadmap for navigating the codebase.
- **Execution path disclosure:** The ordered call stack reveals exactly which methods executed and in what sequence, allowing an attacker to identify where authentication checks or access controls are — or are not — applied.
- **Server environment leakage:** Stack traces may include absolute file system paths, revealing the deployment directory structure and server configuration.
- **Fuzzing roadmap:** Exception types such as `NullPointerException` at specific line numbers indicate unhandled edge cases, giving an attacker precise targets for input fuzzing to trigger further failures.

The `GenericExceptionMapper` in this project addresses all of these risks by intercepting every unhandled `Throwable`, logging the full stack trace server-side only, and returning only a generic `500 Internal Server Error` JSON body to the external consumer.

---

### Part 5.5 — Filters vs Manual Logging

Logging is a **cross-cutting concern** — it applies uniformly to all endpoints regardless of their individual business logic. JAX-RS filters are the architecturally correct mechanism for exactly this category of requirement.

The advantages over manual per-method logging are:

- **DRY principle:** A single `LoggingFilter` class implementing both `ContainerRequestFilter` and `ContainerResponseFilter` applies automatically to every request and response. Manual logging requires duplicating the same call in every resource method across dozens of endpoints.
- **Separation of concerns:** Resource methods should contain only domain logic. Embedding logging statements inside them conflates two unrelated responsibilities and makes the code harder to read.
- **Guaranteed coverage:** Filters are registered at the framework level and execute for every matched request — it is impossible to accidentally omit them from a newly added endpoint.
- **Centralised maintenance:** If the logging format needs to change, exactly one file needs to be modified rather than every resource method across the entire codebase.
- **Pre and post bracketing:** `ContainerRequestFilter` executes before the resource method (capturing method and URI on the way in) while `ContainerResponseFilter` executes after (capturing the final status code on the way out). This clean before/after structure is impossible to replicate reliably with manual calls placed inside method bodies.

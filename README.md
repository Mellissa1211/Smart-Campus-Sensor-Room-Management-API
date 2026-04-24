Here’s your README cleaned up and ready to paste directly into a Markdown file:

# Smart Campus Sensor & Room Management API

A RESTful API built with **JAX-RS (Jersey)** for managing campus rooms and IoT sensors. Uses in-memory data storage — no database required.

---

## Tech Stack

- **Java** (JDK 21)
- **JAX-RS** via Jersey 2.35
- **Apache Tomcat** 8.5.96
- **Maven** (WAR packaging)
- **Jackson** (JSON serialization)

---

## Project Structure


com.smartcampus
├── api
│ └── SmartCampusApplication.java # JAX-RS entry point (@ApplicationPath)
├── models
│ ├── Room.java # Room POJO
│ ├── Sensor.java # Sensor POJO
│ └── SensorReading.java # SensorReading POJO
├── store
│ └── DataStore.java # In-memory ConcurrentHashMap storage
├── resources
│ ├── DiscoveryResource.java # GET /api/v1/
│ ├── RoomResource.java # Room CRUD endpoints
│ ├── SensorResource.java # Sensor CRUD endpoints
│ └── SensorReadingResource.java # Sub-resource for readings
├── exceptions
│ ├── RoomNotEmptyException.java
│ ├── LinkedResourceNotFoundException.java
│ ├── RoomNotFoundException.java
│ ├── SensorUnavailableException.java
│ └── CustomMapper.java # ExceptionMappers (409, 404, 422, 500)
└── filters
└── LoggingFilter.java # Logs request URI and response status


---

## Setup & Running

### Prerequisites
- JDK 21  
- Apache Tomcat 8.5+  
- Maven 3.x  
- NetBeans 24 (or any IDE)

### Build
```bash
mvn clean install
Deploy
Copy target/smart-campus-api.war to Tomcat's webapps/ folder, or
In NetBeans: Right-click project → Run (auto-deploys to configured Tomcat)
Base URL
http://localhost:8080/smart-campus-api/api/v1
API Endpoints
Discovery
Method	Endpoint	Description	Response
GET	/	API metadata and links	200 OK

Response:

{
  "version": "1.0",
  "description": "Smart Campus Sensor Management API",
  "links": {
    "rooms": "/smart-campus-api/api/v1/rooms",
    "sensors": "/smart-campus-api/api/v1/sensors"
  }
}
Rooms
Method	Endpoint	Description	Response
GET	/rooms	Get all rooms	200 OK
GET	/rooms/{roomId}	Get room by ID	200 OK / 404
POST	/rooms	Create a new room	201 Created / 409 / 422
PUT	/rooms/{roomId}	Full update of a room	200 OK / 404
PATCH	/rooms/{roomId}	Partial update of a room	200 OK / 404
DELETE	/rooms/{roomId}	Delete a room	204 No Content / 404 / 409
GET	/rooms/{roomId}/sensors	Get all sensors in a room	200 OK / 404

Create Room — Request Body:

{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "capacity": 50
}
Sensors
Method	Endpoint	Description	Response
GET	/sensors	Get all sensors	200 OK
GET	/sensors?type=Temperature	Filter sensors by type	200 OK
GET	/sensors/{sensorId}	Get sensor by ID	200 OK / 404
POST	/sensors	Create a new sensor	201 Created / 404 / 409 / 422
PATCH	/sensors/{sensorId}	Update sensor status/value	200 OK / 404 / 422
DELETE	/sensors/{sensorId}	Delete a sensor	204 No Content / 404

Create Sensor — Request Body:

{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 22.5,
  "roomId": "LIB-301"
}

Valid status values: ACTIVE, MAINTENANCE, OFFLINE

Sensor Readings
Method	Endpoint	Description	Response
POST	/sensors/{sensorId}/readings	Add a new reading	201 Created / 404 / 422
GET	/sensors/{sensorId}/readings	Get all readings for sensor	200 OK

Add Reading — Request Body:

{
  "value": 25.3
}

id and timestamp are auto-generated if not provided.

Reading Response:

{
  "id": "c4fc4b5c-2900-4262-858e-e1a7a86a3c90",
  "timestamp": 1776958915875,
  "value": 25.3
}
Error Responses

All errors return a consistent JSON body:

{
  "status": 404,
  "error": "Not Found",
  "message": "Room not found: LIB-999"
}
Status Code	Meaning	When
404	Not Found	Room or sensor ID does not exist
409	Conflict	Duplicate ID, or deleting room with active sensors
422	Unprocessable Entity	Missing required fields, invalid status, sensor is MAINTENANCE/OFFLINE
500	Internal Server Error	Unexpected server error
Business Rules
A room cannot be deleted if it has sensors assigned to it (returns 409)
A sensor must be linked to an existing room on creation (returns 404 if room not found)
Readings cannot be added to a sensor with status MAINTENANCE or OFFLINE (returns 422)
Adding a reading automatically updates the sensor's currentValue
Deleting a sensor automatically unlinks it from its room
Testing

Use Postman to test all endpoints. Example flow:

1. POST /rooms
2. POST /sensors
3. POST /sensors/{id}/readings
4. GET  /sensors/{id}/readings
5. GET  /sensors/{id}
6. DELETE /rooms/{id}   → should return 409
7. DELETE /sensors/{id}
8. DELETE /rooms/{id}   → now returns 204
Logging

All requests and responses are logged via LoggingFilter.java, which implements both ContainerRequestFilter and ContainerResponseFilter. Logs include the HTTP method, request URI, and response status code.

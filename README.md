# Smart Campus Management System API (5COSC022W)

## 🎯 Project Scope
This project implements a RESTful web service for managing IoT sensor data across a university campus. It adheres to the **Richardson Maturity Model Level 2** by using proper HTTP verbs, resource-based URIs, and standard status codes.

---

## 🏗 System Architecture (Task 1.1)
The application follows a **3-Tier Client-Server Architecture**:
1.  **Presentation Tier:** Handled via Postman/Client-side requests using JSON.
2.  **Logic Tier (Middleware):** JAX-RS Resource classes (`SensorResource`, `RoomResource`) handling business logic and validation.
3.  **Data Tier:** In-memory `DataStore` simulating a persistent database (as restricted by Task 8).

---

## 🛠 Required Technologies
As per the technology restrictions in the specification:
* **Java Version:** JDK 21 (Source/Target 1.8)
* **Framework:** JAX-RS (Jersey Implementation)
* **Build Tool:** Maven 3.8+
* **Testing:** Postman Collection

---

## 📡 API Design & Endpoints

### Discovery & Versioning
* **Entry Point:** `/api/v1/`
* **HATEOAS:** The root endpoint provides a JSON map of available resource links to facilitate API exploration.

### Core Resources
| Feature | Endpoint | HTTP Method | Implementation Detail |
| :--- | :--- | :--- | :--- |
| **Room Management** | `/rooms` | `GET`, `POST` | Supports CRUD for campus locations. |
| **Sensor Registry** | `/sensors` | `POST` | Validates `roomId` exists before registration (Task 5.2). |
| **Sensor Filtering** | `/sensors?type=X` | `GET` | Uses QueryParams to filter by sensor category (Task 3.2). |
| **Historical Data** | `/sensors/{id}/read` | `GET`, `POST` | Sub-resource locator for time-series data (Task 4.1). |

---

## 🛡 Robustness & Error Handling (Task 5)
Custom `ExceptionMappers` are used to translate Java exceptions into meaningful HTTP responses:
* **Constraint Validation:** Prevents deletion of occupied rooms (Status **409**).
* **State Management:** Blocks readings from sensors in `MAINTENANCE` mode (Status **403**).
* **Referential Integrity:** Ensures sensors are only added to valid rooms (Status **422**).

---

## 📊 Logging & Auditing (Task 5.5)
A `LoggingFilter` is registered to provide a server-side audit trail.
* **Logs include:** HTTP Method, Request URI, and Final Response Status.
* **Levels:** Successes are logged as `INFO`, while caught exceptions are logged as `ERROR`.

---

## 🧪 Testing Report (Summary)
To verify the API, use the provided Postman collection. Key test cases include:
1.  **Positive Case:** Successful creation of a Room and subsequent Sensor registration.
2.  **Negative Case (422):** Registering a sensor to a non-existent Room ID.
3.  **Constraint Case (409):** Attempting to delete a Room that still contains active sensors.

---

## 📝 Student Information
* **Name:** [Your Name]
* **IIT ID:** [Your ID]
* **UoW ID:** [Your ID]
* **Module:** Client-Server Architectures (5COSC022W)

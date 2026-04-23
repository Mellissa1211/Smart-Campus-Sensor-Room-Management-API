# 🏛️ Smart Campus Infrastructure API
**Student Name:** [Your Name]  
**UOW ID:** [Your ID]  
**IIT ID:** [Your ID]

---

## 📋 Project Overview
The **Smart Campus API** is a high-performance RESTful middleware designed to manage a university’s physical and IoT ecosystem. Built using the **JAX-RS (Jakarta RESTful Web Services)** specification, the system facilitates the management of campus rooms and diverse sensor hardware (e.g., CO2, Temperature, Occupancy).

The service emphasizes a hierarchical resource design, strict error handling, and observability through centralized logging, ensuring the campus infrastructure remains reliable and data-rich.

---

## 🛠️ Technology Architecture
This API follows a decoupled architectural style to separate data modeling from resource delivery. It is built to run on standard servlet containers without the overhead of heavy frameworks.

| Component | Technology |
| :--- | :--- |
| **Language** | Java 8 / 11 |
| **API Framework** | JAX-RS (Jersey Implementation) |
| **Server** | Apache Tomcat 8.5 |
| **Build System** | Maven |
| **Persistence** | Thread-safe In-Memory Collections (ConcurrentHashMap) |

### 📂 Project Structure
```text
Smart_Campus_API/
├── src/main/java/com/smartcampus/
│   ├── api/             # API Bootstrapping & Versioning
│   ├── models/          # Core POJOs (Room, Sensor, Reading)
│   ├── resources/       # REST Resource Controllers
│   ├── store/           # Centralized In-Memory Data Management
│   ├── exceptions/      # Custom Exception & Error Mapping
│   └── filters/         # Request/Response Interceptors

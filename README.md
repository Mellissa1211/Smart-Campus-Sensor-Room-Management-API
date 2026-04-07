# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W – Client-Server Architectures  
**Module Leader:** Hamed Hamzeh  
**Author:** [Your Name]  
**Submission Year:** 2025/26  

---

## 1. Project Overview

The **Smart Campus API** is a RESTful web service for managing **Rooms**, **Sensors**, and **Sensor Readings** at the University of Westminster.  
It is designed using **JAX-RS (Jersey)** and stores data in-memory using **HashMap** and **ArrayList**.  

**Key Features:**

- RESTful API following resource-based design principles
- Versioned entry point: `/api/v1`
- Sub-resource pattern for sensor readings
- Advanced error handling and logging
- Thread-safe in-memory storage

**Core Resources:**

- **Room** – represents a room on campus  
- **Sensor** – represents a sensor installed in a room  
- **SensorReading** – historical readings from each sensor  

---

## 2. Technology Stack

- **Java 8**  
- **Maven 3.8+**  
- **JAX-RS (Jersey 2.35)**  
- **Maven Plugins:** `maven-compiler-plugin 3.11.0`, `maven-war-plugin 3.4.0`  
- **IDE:** NetBeans  
- **Testing Tool:** cURL (command-line)  

---

## 3. Setup Instructions

### Clone the project
```bash
git clone https://github.com/yourusername/smart-campus-api.git
cd smart-campus-api

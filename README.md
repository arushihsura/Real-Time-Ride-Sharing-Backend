# 🚗 Uber-App — Real-Time Ride Sharing Backend

A backend implementation of an **Uber/Ola-style ride-sharing system** built using **Spring Boot microservices**.

The project handles the complete ride lifecycle — from updating driver locations and finding nearby drivers to requesting, accepting, starting, and completing rides.

---

## 📌 Features

- 🚘 Driver location tracking
- 📍 Geospatial nearby-driver search
- 🔎 Automatic driver matching
- 🚕 Ride request creation
- ✅ Driver assignment
- ▶️ Ride start and completion
- 💰 Estimated and actual fare calculation
- 👤 Rider ride-history retrieval
- 🗑️ Driver location removal
- 🌐 RESTful APIs
- 🔄 Microservice-based architecture

---

## 🏗️ Architecture

The application is divided into independent services responsible for different parts of the ride-sharing workflow.

```text
                    ┌─────────────────────┐
                    │      Rider App      │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │     Ride Service    │
                    │     Port: 8083      │
                    └──────────┬──────────┘
                               │
                     Find nearby drivers
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Location Service    │
                    │     Port: 8082      │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Driver Locations    │
                    │ + Geospatial Search │
                    └─────────────────────┘
```

### Services

| Service | Port | Responsibility |
|---|---:|---|
| Location Service | `8082` | Driver location updates, nearby-driver search and driver removal |
| Ride Service | `8083` | Ride requests, driver assignment, ride lifecycle and fare |

---

## 🔄 Ride Lifecycle

A typical ride follows this lifecycle:

```text
MATCHING
    │
    ▼
ACCEPTED
    │
    ▼
RIDE_STARTED
    │
    ▼
COMPLETED
```

### Example

```text
Rider requests ride
        ↓
Ride created
        ↓
Status: MATCHING
        ↓
Nearby driver identified
        ↓
Driver assigned
        ↓
Status: ACCEPTED
        ↓
Ride starts
        ↓
Status: RIDE_STARTED
        ↓
Ride completes
        ↓
Status: COMPLETED
        ↓
Actual fare calculated
```

---

# 🚀 Getting Started

## Prerequisites

Make sure the following are installed:

- Java
- Maven
- Git
- PowerShell
- An IDE such as IntelliJ IDEA

Verify Java and Maven:

```powershell
java -version
mvn -version
```

---

## 📥 Clone the Repository

```powershell
git clone https://github.com/YOUR_USERNAME/Uber-App.git
cd Uber-App
```

---

# ▶️ Running the Application

Start the **Location Service** on:

```text
http://localhost:8082
```

Start the **Ride Service** on:

```text
http://localhost:8083
```

The exact commands depend on the project structure. For a Maven Spring Boot application, a service can generally be started using:

```powershell
mvn spring-boot:run
```

---

# 📡 API Documentation

## 1. Update Driver Location

Updates the current location of a driver.

### Endpoint

```http
POST /api/v1/locations/drivers/update
```

### Example

```powershell
Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8082/api/v1/locations/drivers/update" `
  -ContentType "application/json" `
  -Body '{
    "driverId": "D101",
    "latitude": 37.7749,
    "longitude": -122.4194
  }'
```

### Response

```text
Driver Location updated
```

---

# 2. Find Nearby Drivers

Searches for drivers within a specified radius of a location.

### Endpoint

```http
GET /api/v1/locations/drivers/nearby
```

### Parameters

| Parameter | Description |
|---|---|
| `latitude` | Pickup latitude |
| `longitude` | Pickup longitude |
| `radius` | Search radius in kilometers |

### Example

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8082/api/v1/locations/drivers/nearby?latitude=37.7750&longitude=-122.4195&radius=5.0"
```

### Example Response

```text
driverId     latitude       longitude       distanceInKm
--------     --------       ---------       ------------
D101         37.7749        -122.4194       0.0141
driver-1     37.7749        -122.4194       0.0141
driver-2     37.7800        -122.4100       1.0035
```

This demonstrates **location-based driver discovery**, where drivers are returned with their calculated distance from the pickup location.

---

# 3. Request a Ride

Creates a new ride request.

### Endpoint

```http
POST /api/v1/rides/request
```

### Example

```powershell
$ride = Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8083/api/v1/rides/request" `
  -ContentType "application/json" `
  -Body '{
    "riderId": "R100",
    "pickupLatitude": 37.7750,
    "pickupLongitude": -122.4195,
    "pickupAddress": "Market St",
    "dropLatitude": 37.7890,
    "dropLongitude": -122.4010,
    "dropAddress": "Financial District"
  }'
```

### Example Response

```text
id            : fffcbe28-84f9-4f2a-9206-4633cae9b607
riderId       : R100
status        : MATCHING
estimatedFare : 77.01
```

A newly requested ride initially enters:

```text
MATCHING
```

---

# 4. Get Ride Details

Retrieves the current state of a ride.

### Endpoint

```http
GET /api/v1/rides/{rideId}
```

### Example

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8083/api/v1/rides/$($ride.id)"
```

### Example Result

```text
id       : fffcbe28-84f9-4f2a-9206-4633cae9b607
riderId  : R100
driverId : D101
status   : ACCEPTED
```

The ride has now been assigned to driver `D101`.

---

# 5. Start a Ride

Starts an accepted ride.

### Endpoint

```http
PUT /api/v1/rides/{rideId}/start
```

### Example

```powershell
Invoke-RestMethod `
  -Method Put `
  -Uri "http://localhost:8083/api/v1/rides/$($ride.id)/start"
```

### Example Response

```text
status    : RIDE_STARTED
startedAt : 2026-10-04T22:06:10
```

---

# 6. Complete a Ride

Completes the active ride and records the actual fare.

### Endpoint

```http
PUT /api/v1/rides/{rideId}/complete
```

### Example

```powershell
Invoke-RestMethod `
  -Method Put `
  -Uri "http://localhost:8083/api/v1/rides/$($ride.id)/complete"
```

### Example Response

```text
status      : COMPLETED
actualFare  : 77.01
completedAt : 2026-10-04T22:06:20
```

---

# 7. Get Rider Ride History

Retrieves rides associated with a rider.

### Endpoint

```http
GET /api/v1/rides/rider/{riderId}
```

### Example

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8083/api/v1/rides/rider/R100"
```

### Example

```text
id                                   status       driverId    actualFare
--                                   ------       --------    ----------
fffcbe28-84f9-4f2a-9206-4633cae9b607 COMPLETED    D101        77.01
```

---

# 8. Remove Driver

Removes a driver's stored location.

### Endpoint

```http
DELETE /api/v1/locations/drivers/{driverId}
```

### Example

```powershell
Invoke-RestMethod `
  -Method Delete `
  -Uri "http://localhost:8082/api/v1/locations/drivers/driver-1"
```

### Response

```text
Driver removed successfully
```

---

# 🧪 Complete End-to-End Test

The following sequence demonstrates the complete workflow.

## Step 1 — Add Driver Locations

```powershell
Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8082/api/v1/locations/drivers/update" `
  -ContentType "application/json" `
  -Body '{
    "driverId": "driver-1",
    "latitude": 37.7749,
    "longitude": -122.4194
  }'

Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8082/api/v1/locations/drivers/update" `
  -ContentType "application/json" `
  -Body '{
    "driverId": "driver-2",
    "latitude": 37.7800,
    "longitude": -122.4100
  }'
```

---

## Step 2 — Find Nearby Drivers

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8082/api/v1/locations/drivers/nearby?latitude=37.7750&longitude=-122.4195&radius=5.0"
```

The service identifies drivers based on their geographic distance from the pickup point.

---

## Step 3 — Request a Ride

```powershell
$ride = Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8083/api/v1/rides/request" `
  -ContentType "application/json" `
  -Body '{
    "riderId": "R100",
    "pickupLatitude": 37.7750,
    "pickupLongitude": -122.4195,
    "pickupAddress": "Market St",
    "dropLatitude": 37.7890,
    "dropLongitude": -122.4010,
    "dropAddress": "Financial District"
  }'
```

Initial state:

```text
MATCHING
```

---

## Step 4 — Check Assignment

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8083/api/v1/rides/$($ride.id)"
```

Result:

```text
driverId : D101
status   : ACCEPTED
```

---

## Step 5 — Start the Ride

```powershell
Invoke-RestMethod `
  -Method Put `
  -Uri "http://localhost:8083/api/v1/rides/$($ride.id)/start"
```

Result:

```text
RIDE_STARTED
```

---

## Step 6 — Complete the Ride

```powershell
Invoke-RestMethod `
  -Method Put `
  -Uri "http://localhost:8083/api/v1/rides/$($ride.id)/complete"
```

Result:

```text
COMPLETED
actualFare : 77.01
```

---

## Step 7 — View Rider History

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8083/api/v1/rides/rider/R100"
```

The completed ride appears in the rider's ride history.

---

# 📊 Tested Workflow

The implemented workflow was successfully tested as:

```text
Driver Location Update
        ↓
Nearby Driver Search
        ↓
Ride Request
        ↓
MATCHING
        ↓
Driver Assignment
        ↓
ACCEPTED
        ↓
Ride Start
        ↓
RIDE_STARTED
        ↓
Ride Completion
        ↓
COMPLETED
        ↓
Fare + Ride History
```

Example tested values:

| Parameter | Value |
|---|---|
| Rider | `R100` |
| Assigned Driver | `D101` |
| Pickup | Market St |
| Destination | Financial District |
| Estimated Fare | `77.01` |
| Actual Fare | `77.01` |
| Final Status | `COMPLETED` |

---

# 🧮 Geospatial Matching

The Location Service calculates the distance between the pickup coordinates and available driver coordinates.

For example:

```text
Pickup:
37.7750, -122.4195

Driver D101:
37.7749, -122.4194

Distance:
~0.014 km
```

A driver farther away was also tested:

```text
Driver driver-2:
37.7800, -122.4100

Distance:
~1.0035 km
```

This enables the ride service to identify suitable nearby drivers for a ride request.

---

# 🛠️ Technology Stack

The project is designed as a backend-focused ride-sharing system using:

- **Java**
- **Spring Boot**
- **REST APIs**
- **Maven**
- **Microservices Architecture**
- **Geospatial Location Search**
- **JSON**
- **PowerShell / REST API testing**
- **Git & GitHub**

---


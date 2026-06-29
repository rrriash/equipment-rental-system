# Equipment Rental System

Microservice-based Spring Boot application for managing university equipment rental.

The system consists of three independent Spring Boot services combined in one Maven multi-module project:

* `user-service` — user management
* `equipment-service` — equipment and category management
* `booking-service` — equipment booking and inter-service interaction

## Tech Stack

* Java 17
* Spring Boot
* Maven
* REST API
* HTTP
* PostgreSQL / in-memory storage depending on configuration
* JUnit
* Lombok
* Postman / curl
* Git, GitHub
* Microservice architecture basics

## Services and Ports

| Service           | Port |
| ----------------- | ---: |
| user-service      | 8081 |
| equipment-service | 8082 |
| booking-service   | 8083 |

## Features

### User Service

* Create users
* Get users
* Block and unblock users
* Validate user status
* Handle duplicate emails
* Return unified error responses

### Equipment Service

* Create equipment categories
* Add equipment
* Get available equipment
* Update equipment status
* Validate inventory numbers
* Return unified error responses

### Booking Service

* Create equipment booking requests
* Check user existence and status through `user-service`
* Check equipment availability through `equipment-service`
* Approve, issue and return equipment
* Update equipment status during the booking lifecycle
* Create mock notifications
* Handle booking conflicts and invalid status transitions
* Log execution time for booking creation

## Build

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home \
  mvn clean package -DskipTests
```

## Run

Run each service in a separate terminal.

### user-service

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home \
  java -jar user-service/target/user-service-1.0.0.jar
```

### equipment-service

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home \
  java -jar equipment-service/target/equipment-service-1.0.0.jar
```

### booking-service

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home \
  java -jar booking-service/target/booking-service-1.0.0.jar
```

The services should be started in this order:

1. `user-service`
2. `equipment-service`
3. `booking-service`

## API Examples

### Create User

```bash
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Ivan Ivanov","email":"ivan@uni.ru","role":"STUDENT"}'
```

### Get Users

```bash
curl http://localhost:8081/users
```

### Block User

```bash
curl -X PATCH http://localhost:8081/users/1/block
```

### Create Equipment Category

```bash
curl -X POST http://localhost:8082/equipment-categories \
  -H "Content-Type: application/json" \
  -d '{"name":"Computer Equipment","description":"Laptops and projectors"}'
```

### Add Equipment

```bash
curl -X POST http://localhost:8082/equipment \
  -H "Content-Type: application/json" \
  -d '{"name":"Dell Laptop","type":"LAPTOP","description":"14-inch laptop","inventoryNumber":"INV-001","categoryId":1}'
```

### Get Available Equipment

```bash
curl http://localhost:8082/equipment/available
```

### Create Booking

```bash
curl -X POST http://localhost:8083/bookings \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"equipmentId":1,"startDate":"2026-07-01","endDate":"2026-07-05"}'
```

### Approve Booking

```bash
curl -X PATCH http://localhost:8083/bookings/1/approve
```

### Issue Equipment

```bash
curl -X PATCH http://localhost:8083/bookings/1/issue
```

### Return Equipment

```bash
curl -X PATCH http://localhost:8083/bookings/1/return
```

### Get Booking Notifications

```bash
curl http://localhost:8083/bookings/1/notifications
```

## Inter-Service Interaction

The `booking-service` communicates synchronously with:

* `user-service` to check whether the user exists and is active;
* `equipment-service` to check equipment availability and update equipment status.

Booking lifecycle:

1. User and equipment are validated.
2. Booking request is created.
3. Equipment status changes to `BOOKED` after approval.
4. Equipment status changes to `ISSUED` when it is issued.
5. Equipment status changes back to `AVAILABLE` after return.
6. Mock notification is created and logged.

## Error Response Format

All services return errors in a unified format:

```json
{
  "code": "EQUIPMENT_NOT_FOUND",
  "message": "Equipment not found",
  "details": "Equipment with id = 99 does not exist"
}
```

## Error Cases

| Situation                                | HTTP Status | Error Code                        |
| ---------------------------------------- | ----------: | --------------------------------- |
| User not found                           |         404 | `USER_NOT_FOUND`                  |
| User is blocked                          |         403 | `USER_BLOCKED`                    |
| Equipment not found                      |         404 | `EQUIPMENT_NOT_FOUND`             |
| Equipment is not available               |         409 | `EQUIPMENT_NOT_AVAILABLE`         |
| Invalid booking period                   |         400 | `INVALID_BOOKING_PERIOD`          |
| Booking date conflict                    |         409 | `BOOKING_PERIOD_CONFLICT`         |
| More than 3 active bookings              |         409 | `TOO_MANY_ACTIVE_BOOKINGS`        |
| User or equipment service is unavailable |         503 | `SERVICE_UNAVAILABLE`             |
| Duplicate email                          |         409 | `EMAIL_ALREADY_EXISTS`            |
| Duplicate inventory number               |         409 | `INVENTORY_NUMBER_ALREADY_EXISTS` |
| Invalid booking operation                |         409 | `INVALID_BOOKING_OPERATION`       |
| Internal server error                    |         500 | `INTERNAL_ERROR`                  |

## Project Status

Educational backend project developed as part of university coursework.

The project demonstrates:

* Java backend development
* Spring Boot REST API
* Maven multi-module structure
* Basic microservice architecture
* Inter-service communication
* Unified error handling
* JUnit testing
* API testing with curl and Postman

# 🏭 Laptop Manufacturing Warehouse Stock Control System

A complete **Java Spring Boot microservices backend** for managing laptop component inventory across warehouses. This project demonstrates practical backend development skills including microservices architecture, REST APIs, event-driven communication with Kafka, Docker containerization, and clean code practices.

---

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Business Problem](#business-problem)
- [Features](#features)
- [Architecture](#architecture)
- [Microservices](#microservices)
- [Technology Stack](#technology-stack)
- [Database Design](#database-design)
- [API Documentation](#api-documentation)
- [Kafka Event Flow](#kafka-event-flow)
- [Stock Management Flow](#stock-management-flow)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Swagger URLs](#swagger-urls)
- [Postman Collection](#postman-collection)
- [Testing](#testing)
- [Project Explanation](#project-explanation)
- [Future Enhancements](#future-enhancements)
- [Troubleshooting](#troubleshooting)

---

## Project Overview

A laptop manufacturing company receives components (processors, RAM, SSDs, motherboards, batteries, displays, keyboards, chargers) from suppliers and stores them in warehouses. This system tracks:

- **Products** — laptop component catalog with categories and minimum stock levels
- **Warehouses** — storage locations with capacity tracking
- **Suppliers** — component vendor information
- **Inventory** — real-time stock quantities per product per warehouse
- **Stock Movements** — complete transaction history (IN, OUT, TRANSFER)
- **Notifications** — automated alerts for stock events and low-stock warnings

---

## Business Problem

Managing laptop component inventory across multiple warehouses requires:
1. Tracking quantities of each component in each warehouse
2. Recording all stock movements for audit purposes
3. Alerting when stock falls below minimum levels
4. Preventing stock-out situations by validating quantities before issuing
5. Supporting stock transfers between warehouses

---

## Features

- ✅ Full CRUD operations for Products, Warehouses, Suppliers, Inventory
- ✅ Stock IN / OUT / TRANSFER with business validation
- ✅ Real-time inventory tracking with available quantity calculation
- ✅ Low-stock detection and automatic Kafka alerts
- ✅ Centralized API Gateway routing
- ✅ Circuit Breaker for inter-service resilience
- ✅ Event-driven notifications via Apache Kafka
- ✅ Swagger/OpenAPI documentation for every service
- ✅ Docker Compose for one-command startup
- ✅ Unit tests with JUnit 5 and Mockito
- ✅ Clean layered architecture (Controller → Service → Repository)

---

## Architecture

```
                     CLIENT (Postman / REST Client)
                              |
                              v
                    +-------------------+
                    |   API Gateway     |
                    |     :8080         |
                    +--------+----------+
                             |
         +-------------------+---------------------+
         |           |           |           |      |
         v           v           v           v      v
    +---------+ +---------+ +---------+ +----------+----------+
    | Product | |Warehouse| |Supplier | |Inventory | Stock    |
    | Service | | Service | | Service | | Service  | Movement |
    |  :8081  | |  :8082  | |  :8083  | |  :8084   | :8085    |
    +---------+ +---------+ +---------+ +-----+----+----+-----+
         |           |           |            |         |
         v           v           v            v         v
      [MySQL]     [MySQL]     [MySQL]      [MySQL]   [MySQL]
                                                       |
                                                  [Kafka] ──> Notification Service :8086
```

### Why are the services separated this way?

| Service | Reason for Separation |
|---------|----------------------|
| **Product Service** | Product catalog is a core domain. Other services reference products by ID. Changes to product structure don't affect stock management. |
| **Warehouse Service** | Warehouse locations can be added/modified independently of inventory operations. |
| **Supplier Service** | Supplier management is a separate business concern that could evolve independently (e.g., adding purchase orders). |
| **Inventory Service** | The core business service tracking real-time quantities. Depends on Product/Warehouse IDs but owns the quantity data. |
| **Stock Movement Service** | Transaction recording and business rules for stock operations. Orchestrates inventory updates and event publishing. |
| **Notification Service** | Decoupled via Kafka. Can be replaced or scaled independently without affecting core operations. |

---

## Microservices

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| API Gateway | 8080 | — | Routes requests, request logging |
| Product Service | 8081 | `warehouse_product_db` | Laptop component catalog |
| Warehouse Service | 8082 | `warehouse_service_db` | Storage locations |
| Supplier Service | 8083 | `warehouse_supplier_db` | Component vendors |
| Inventory Service | 8084 | `warehouse_inventory_db` | Stock quantities |
| Stock Movement Service | 8085 | `warehouse_stock_movement_db` | Stock transactions |
| Notification Service | 8086 | `warehouse_notification_db` | Event notifications |

---

## Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 11 | Programming language |
| Spring Boot | 2.7.18 | Application framework |
| Spring Cloud Gateway | 2021.0.9 | API Gateway |
| Spring Data JPA | 2.7.x | Data access |
| Hibernate | 5.6.x | ORM |
| MySQL | 8.0 | Relational database |
| Apache Kafka | 3.x | Event streaming |
| Resilience4j | 2.3.0 | Circuit breaker |
| springdoc-openapi | 1.6.15 | API documentation |
| JUnit 5 | 5.9.x | Testing framework |
| Mockito | 4.x | Mocking framework |
| Lombok | 1.18.x | Boilerplate reduction |
| Docker | Latest | Containerization |
| Maven | 3.x | Build tool |

### About Lombok

This project uses **Lombok** to reduce boilerplate code. Annotations used:
- `@Data` — generates getters, setters, toString, equals, hashCode
- `@Builder` — generates builder pattern
- `@NoArgsConstructor` / `@AllArgsConstructor` — generates constructors

These are compile-time annotations; no Lombok code exists in the compiled classes.

---

## Database Design

Each microservice has its own database (Database per Service pattern). Services use **ID references** instead of JPA relationships to maintain loose coupling.

```
Product Service → warehouse_product_db → products table
Warehouse Service → warehouse_service_db → warehouses table
Supplier Service → warehouse_supplier_db → suppliers table
Inventory Service → warehouse_inventory_db → inventory table (productId, warehouseId)
Stock Movement → warehouse_stock_movement_db → stock_movements table (productId, warehouseId)
Notification → warehouse_notification_db → notification_logs table
```

### Why no direct JPA relationships between services?

In microservices, each service owns its own database. Using `@ManyToOne` between entities in different services would require shared database access, creating tight coupling and preventing independent deployment. Instead, we use ID references and resolve data via REST calls.

See [docs/database-design.md](docs/database-design.md) for the complete ER diagram.

### Schema Management

This project uses **Hibernate `ddl-auto=update`** for automatic schema creation/updates during development. In a production environment, you would use migration tools like **Flyway** or **Liquibase** for controlled, versioned database migrations.

---

## API Documentation

### Product Service

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/products` | Create a product |
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product by ID |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Deactivate a product |
| GET | `/api/products/category/{category}` | Get by category |
| GET | `/api/products/search?name=...` | Search by name |

### Warehouse Service

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/warehouses` | Create warehouse |
| GET | `/api/warehouses` | Get all warehouses |
| GET | `/api/warehouses/{id}` | Get by ID |
| PUT | `/api/warehouses/{id}` | Update warehouse |
| DELETE | `/api/warehouses/{id}` | Deactivate warehouse |

### Supplier Service

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/suppliers` | Create supplier |
| GET | `/api/suppliers` | Get all suppliers |
| GET | `/api/suppliers/{id}` | Get by ID |
| PUT | `/api/suppliers/{id}` | Update supplier |
| DELETE | `/api/suppliers/{id}` | Deactivate supplier |
| GET | `/api/suppliers/search?name=...` | Search by name |

### Inventory Service

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/inventory` | Create inventory record |
| GET | `/api/inventory` | Get all inventory |
| GET | `/api/inventory/{id}` | Get by ID |
| PUT | `/api/inventory/{id}` | Update inventory |
| DELETE | `/api/inventory/{id}` | Delete inventory |
| GET | `/api/inventory/product/{productId}` | Get by product |
| GET | `/api/inventory/warehouse/{warehouseId}` | Get by warehouse |
| GET | `/api/inventory/low-stock` | Get low-stock items |

### Stock Movement Service

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/stock-movements` | Create movement (IN/OUT/TRANSFER) |
| GET | `/api/stock-movements` | Get all movements |
| GET | `/api/stock-movements/{id}` | Get by ID |
| GET | `/api/stock-movements/product/{productId}` | Get by product |
| GET | `/api/stock-movements/warehouse/{warehouseId}` | Get by warehouse |
| GET | `/api/stock-movements/type/{type}` | Get by type |

### Notification Service

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/notifications` | Get all notifications |

---

## Kafka Event Flow

```
Stock Movement Service ──publish──> Kafka ──consume──> Notification Service
```

### Topics

| Topic | Events | Purpose |
|-------|--------|---------|
| `stock-events` | StockReceivedEvent, StockIssuedEvent, StockTransferredEvent | General stock activity |
| `low-stock-events` | LowStockEvent | Critical low-stock alerts |

### Example Low-Stock Alert (logged by Notification Service)

```
⚠️ LOW STOCK ALERT
Product ID: 4 (16GB DDR4 RAM)
Warehouse ID: 1 (Raw Material Warehouse)
Current Stock: 15
Minimum Level: 80
```

---

## Stock Management Flow

### Complete Business Flow Example

```
1. Create Product (Intel Core i5)         → POST /api/products
2. Create Warehouse (WH-001)              → POST /api/warehouses
3. Create Supplier (TechParts India)      → POST /api/suppliers
4. Create Inventory record (product=1, warehouse=1, qty=0)
                                          → POST /api/inventory
5. Stock IN: Receive 100 units            → POST /api/stock-movements
   → Inventory becomes 100
   → Kafka: StockReceivedEvent published
   → Notification Service logs the event

6. Stock OUT: Issue 30 units              → POST /api/stock-movements
   → Validates: available (100) >= requested (30) ✓
   → Inventory becomes 70
   → Kafka: StockIssuedEvent published

7. Stock OUT: Issue 55 units              → POST /api/stock-movements
   → Inventory becomes 15
   → 15 <= minimumStockLevel (50)
   → Kafka: LowStockEvent published!
   → Notification Service: ⚠️ LOW STOCK ALERT

8. Stock OUT: Try to issue 20 units       → POST /api/stock-movements
   → Validates: available (15) < requested (20) ✗
   → 400 Bad Request: "Insufficient stock"
```

---

## Project Structure

```
laptop-warehouse-management/
├── pom.xml                              # Parent Maven POM
├── docker-compose.yml                   # Docker Compose config
├── .gitignore
├── .env.example
├── README.md
│
├── api-gateway/                         # Spring Cloud Gateway
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/
│       ├── java/.../gateway/
│       │   ├── ApiGatewayApplication.java
│       │   └── filter/LoggingFilter.java
│       └── resources/application.yml
│
├── product-service/                     # Product management
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/java/.../product/
│       │   ├── controller/
│       │   ├── service/impl/
│       │   ├── repository/
│       │   ├── entity/
│       │   ├── dto/request/ & response/
│       │   ├── exception/
│       │   ├── mapper/
│       │   └── config/
│       ├── main/resources/
│       │   ├── application.yml
│       │   └── data.sql
│       └── test/java/.../
│
├── warehouse-service/                   # (same structure)
├── supplier-service/                    # (same structure)
├── inventory-service/                   # + client/ (REST client)
├── stock-movement-service/              # + kafka/ (producer) + client/
├── notification-service/                # + kafka/ (consumer)
│
├── docs/
│   ├── architecture.md
│   ├── service-communication.md
│   ├── database-design.md
│   ├── stock-in-sequence.md
│   ├── stock-out-sequence.md
│   └── deployment.md
│
├── postman/
│   └── warehouse-management.postman_collection.json
│
└── docker/
    └── mysql-init/init.sql
```

---

## Prerequisites

### For Local Development

- **Java 11** (JDK)
- **Maven 3.6+**
- **MySQL 8.0** running on `localhost:3306`
- **Apache Kafka** running on `localhost:9092`

---

## Swagger URLs

| Service | Swagger UI URL |
|---------|---------------|
| Product Service | http://localhost:8081/swagger-ui.html |
| Warehouse Service | http://localhost:8082/swagger-ui.html |
| Supplier Service | http://localhost:8083/swagger-ui.html |
| Inventory Service | http://localhost:8084/swagger-ui.html |
| Stock Movement Service | http://localhost:8085/swagger-ui.html |
| Notification Service | http://localhost:8086/swagger-ui.html |

---

## Postman Collection

Import the Postman collection from `postman/warehouse-management.postman_collection.json`.

The collection includes:
- All CRUD operations for every service
- A complete business flow demo (Step 1-5)
- Example requests with sample data

---

## Testing

```bash
# Run all tests
mvn clean test

# Run tests for a specific service
cd product-service && mvn test
cd inventory-service && mvn test
cd stock-movement-service && mvn test
```

### Test Coverage

| Service | Tests | What's Tested |
|---------|-------|---------------|
| Product Service | `ProductServiceImplTest`, `ProductControllerTest` | CRUD, duplicate detection, validation, MockMvc |
| Inventory Service | `InventoryServiceImplTest` | Stock add/remove, insufficient stock, duplicate inventory |
| Stock Movement | `StockMovementServiceImplTest` | IN/OUT/TRANSFER, inactive product, missing destination |
| Notification | `StockEventConsumerTest` | Kafka event consumption, notification persistence |

---

## Project Explanation

> "I built a warehouse stock control system for a laptop manufacturing company using **Java Spring Boot microservices**. The company receives laptop components like processors, RAM, and SSDs from suppliers and stores them in warehouses.
>
> The backend has **7 microservices**: Product, Warehouse, Supplier, Inventory, Stock Movement, Notification, and an API Gateway. Each service has its own **MySQL database** and follows a **layered architecture** — Controller, Service, Repository — with **DTOs** to decouple the API from the database layer.
>
> The **Stock Movement Service** is the core business service. When stock comes IN, it calls the **Inventory Service** via REST to update quantities, then publishes a **Kafka event**. The **Notification Service** consumes these events and logs alerts. If stock falls below the minimum level, a **low-stock alert** is automatically generated.
>
> I used **Resilience4j Circuit Breaker** on the Inventory-to-Product REST call so that if Product Service goes down, the system degrades gracefully with fallback values instead of cascading failures.
>
> For deployment, I created **Dockerfiles** for each service and a **Docker Compose** configuration that spins up the entire system — MySQL, Kafka, all services — with one command. I also wrote **unit tests** using JUnit 5 and Mockito covering the important business logic like stock validation and event publishing."

---

## Future Enhancements

These features were intentionally omitted to keep the project simple, but could be added:

- 🔐 **Security**: Spring Security with JWT authentication
- 🔍 **Service Discovery**: Spring Cloud Eureka for dynamic service registration
- 📊 **Distributed Tracing**: Spring Cloud Sleuth + Zipkin
- 📧 **Real Notifications**: Email/SMS integration via SendGrid or Twilio
- 🗂️ **Purchase Orders**: Link suppliers to incoming stock movements
- 📈 **Reporting**: Stock level reports and movement analytics
- 🔄 **Database Migrations**: Flyway or Liquibase for production schema management
- 🚀 **CI/CD**: GitHub Actions pipeline for automated build/test/deploy
- ☸️ **Kubernetes**: Container orchestration for production deployment
- 💾 **Caching**: Redis for frequently accessed product/warehouse data

---

## Troubleshooting

### MySQL connection refused

```bash
# Check if MySQL is running
mysql -u root -p -e "SHOW DATABASES;"
# Verify the databases exist
```

### Kafka connection issues

```bash
# Check if Kafka is running
kafka-broker-api-versions --bootstrap-server localhost:9092
# Check if topics exist
kafka-topics --list --bootstrap-server localhost:9092
```

### Service won't start

1. Check that the required port is not already in use
2. Check MySQL is running and accessible
3. Check the `application.yml` configuration
4. Check the Maven build completes without errors: `mvn clean compile`

### Docker Compose issues

```bash
# Check container status
docker-compose ps

# View specific service logs
docker-compose logs product-service

# Rebuild a specific service
docker-compose build product-service

# Clean restart
docker-compose down -v
docker-compose up --build
```

---


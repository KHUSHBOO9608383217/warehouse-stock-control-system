# Deployment Architecture

## Docker Deployment Diagram

```mermaid
graph TB
    subgraph Docker["Docker Compose Environment"]
        subgraph Infra["Infrastructure"]
            MySQL["MySQL 8.0<br/>:3306"]
            ZK["Zookeeper<br/>:2181"]
            Kafka["Kafka<br/>:9092"]
        end
        
        subgraph App["Application Services"]
            GW["API Gateway<br/>:8080"]
            PS["Product Service<br/>:8081"]
            WS["Warehouse Service<br/>:8082"]
            SS["Supplier Service<br/>:8083"]
            IS["Inventory Service<br/>:8084"]
            SMS["Stock Movement<br/>:8085"]
            NS["Notification Service<br/>:8086"]
        end
    end
    
    ZK --> Kafka
    MySQL --> PS
    MySQL --> WS
    MySQL --> SS
    MySQL --> IS
    MySQL --> SMS
    MySQL --> NS
    Kafka --> SMS
    Kafka --> NS
    
    GW --> PS
    GW --> WS
    GW --> SS
    GW --> IS
    GW --> SMS
    GW --> NS
```

## Starting the System

### Using Docker Compose

```bash
# Build and start all services
docker-compose up --build

# Start in background
docker-compose up --build -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Running Locally (without Docker)

**Prerequisites:**
- Java 11
- Maven 3.6+
- MySQL 8.0 running on localhost:3306
- Kafka running on localhost:9092

```bash
# 1. Create databases
mysql -u root -p < docker/mysql-init/init.sql

# 2. Build all services
mvn clean install -DskipTests

# 3. Start each service (in separate terminals)
cd product-service && mvn spring-boot:run
cd warehouse-service && mvn spring-boot:run
cd supplier-service && mvn spring-boot:run
cd inventory-service && mvn spring-boot:run
cd stock-movement-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

## Port Mapping

| Service | Port |
|---------|------|
| API Gateway | 8080 |
| Product Service | 8081 |
| Warehouse Service | 8082 |
| Supplier Service | 8083 |
| Inventory Service | 8084 |
| Stock Movement Service | 8085 |
| Notification Service | 8086 |
| MySQL | 3306 |
| Kafka | 9092 |
| Zookeeper | 2181 |

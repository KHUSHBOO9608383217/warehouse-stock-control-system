# High-Level Architecture

## System Architecture Diagram

```mermaid
graph TB
    Client["🖥️ Client<br/>Postman / REST Client"]
    
    subgraph Gateway["API Gateway :8080"]
        GW["Spring Cloud Gateway<br/>Request Routing & Logging"]
    end
    
    subgraph Services["Microservices"]
        PS["Product Service<br/>:8081"]
        WS["Warehouse Service<br/>:8082"]
        SS["Supplier Service<br/>:8083"]
        IS["Inventory Service<br/>:8084"]
        SMS["Stock Movement Service<br/>:8085"]
        NS["Notification Service<br/>:8086"]
    end
    
    subgraph Databases["MySQL Databases"]
        PDB[("warehouse_product_db")]
        WDB[("warehouse_service_db")]
        SDB[("warehouse_supplier_db")]
        IDB[("warehouse_inventory_db")]
        SMDB[("warehouse_stock_movement_db")]
        NDB[("warehouse_notification_db")]
    end
    
    subgraph Messaging["Event Streaming"]
        K["Apache Kafka"]
    end
    
    Client --> GW
    GW --> PS
    GW --> WS
    GW --> SS
    GW --> IS
    GW --> SMS
    GW --> NS
    
    PS --> PDB
    WS --> WDB
    SS --> SDB
    IS --> IDB
    SMS --> SMDB
    NS --> NDB
    
    SMS -->|"Publish Events"| K
    K -->|"Consume Events"| NS
    
    IS -->|"REST"| PS
    SMS -->|"REST"| IS
    SMS -->|"REST"| PS
```

## Architecture Principles

1. **Single Responsibility**: Each microservice owns one business domain
2. **Database per Service**: Each service has its own database schema — no shared databases
3. **API Gateway**: Single entry point for all client requests
4. **Event-Driven**: Kafka for asynchronous notifications (stock events, low-stock alerts)
5. **Synchronous REST**: Inter-service calls use REST when immediate response is needed
6. **Circuit Breaker**: Resilience4j protects against cascading failures

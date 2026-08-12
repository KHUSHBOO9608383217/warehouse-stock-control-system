# Database Design

## Database per Service Architecture

Each microservice has its own MySQL database. Services communicate via REST APIs, **never** by directly accessing another service's database. This ensures loose coupling and allows each service to evolve its schema independently.

```mermaid
erDiagram
    PRODUCT {
        bigint id PK
        varchar productCode UK
        varchar name
        varchar category
        varchar description
        varchar unit
        int minimumStockLevel
        boolean active
        datetime createdAt
        datetime updatedAt
    }
    
    WAREHOUSE {
        bigint id PK
        varchar warehouseCode UK
        varchar name
        varchar location
        varchar managerName
        int capacity
        boolean active
        datetime createdAt
        datetime updatedAt
    }
    
    SUPPLIER {
        bigint id PK
        varchar supplierCode UK
        varchar name
        varchar contactPerson
        varchar email
        varchar phone
        varchar address
        boolean active
        datetime createdAt
        datetime updatedAt
    }
    
    INVENTORY {
        bigint id PK
        bigint productId FK
        bigint warehouseId FK
        int quantity
        int reservedQuantity
        int availableQuantity
        datetime createdAt
        datetime updatedAt
    }
    
    STOCK_MOVEMENT {
        bigint id PK
        bigint productId FK
        bigint warehouseId FK
        bigint destinationWarehouseId FK
        int quantity
        varchar movementType
        varchar referenceNumber
        varchar remarks
        datetime createdAt
    }
    
    NOTIFICATION_LOG {
        bigint id PK
        varchar eventType
        varchar message
        datetime createdAt
    }
    
    PRODUCT ||--o{ INVENTORY : "productId"
    WAREHOUSE ||--o{ INVENTORY : "warehouseId"
    PRODUCT ||--o{ STOCK_MOVEMENT : "productId"
    WAREHOUSE ||--o{ STOCK_MOVEMENT : "warehouseId"
```

## Why No Direct JPA Relationships?

In a microservices architecture, entities like `Product` and `Warehouse` are owned by different services with different databases. Using JPA `@ManyToOne` relationships between them would:

1. **Violate service boundaries** — it would require shared database access
2. **Create tight coupling** — changes to one service's schema would break other services
3. **Prevent independent deployment** — services couldn't be deployed independently

Instead, we use **ID references** (`productId`, `warehouseId`) and resolve them via REST calls when needed.

## Database Mapping

| Service | Database | Primary Entity |
|---------|----------|---------------|
| Product Service | `warehouse_product_db` | `products` |
| Warehouse Service | `warehouse_service_db` | `warehouses` |
| Supplier Service | `warehouse_supplier_db` | `suppliers` |
| Inventory Service | `warehouse_inventory_db` | `inventory` |
| Stock Movement Service | `warehouse_stock_movement_db` | `stock_movements` |
| Notification Service | `warehouse_notification_db` | `notification_logs` |

# Stock IN Sequence Diagram

## Flow: Receiving Stock into Warehouse

```mermaid
sequenceDiagram
    participant C as Client
    participant GW as API Gateway
    participant SMS as Stock Movement Service
    participant PS as Product Service
    participant IS as Inventory Service
    participant DB as MySQL
    participant K as Kafka
    participant NS as Notification Service
    
    C->>GW: POST /api/stock-movements
    GW->>SMS: Route request
    
    SMS->>PS: GET /api/products/{id} (check active)
    PS-->>SMS: Product active = true
    
    SMS->>IS: PUT /api/inventory/add-stock
    IS->>DB: UPDATE inventory SET quantity += N
    DB-->>IS: Updated
    IS-->>SMS: Stock added successfully
    
    SMS->>DB: INSERT INTO stock_movements
    DB-->>SMS: Movement recorded
    
    SMS->>K: Publish StockReceivedEvent
    SMS->>PS: GET minimumStockLevel
    PS-->>SMS: minimumStockLevel = 50
    
    Note over SMS: Check: currentStock <= minimumStockLevel?
    
    SMS-->>GW: 201 Created
    GW-->>C: Stock movement response
    
    K->>NS: Consume StockReceivedEvent
    NS->>DB: INSERT INTO notification_logs
    Note over NS: Log: "Received 100 units..."
```

## Example Request

```json
POST /api/stock-movements
{
    "productId": 1,
    "warehouseId": 1,
    "quantity": 100,
    "movementType": "IN",
    "referenceNumber": "PO-2024-001",
    "remarks": "Received from supplier SUP-001"
}
```

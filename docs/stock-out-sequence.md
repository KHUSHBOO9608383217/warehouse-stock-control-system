# Stock OUT Sequence Diagram

## Flow: Issuing Stock from Warehouse

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
    
    SMS->>IS: PUT /api/inventory/remove-stock
    
    Note over IS: Validate: availableQuantity >= requested?
    
    alt Sufficient Stock
        IS->>DB: UPDATE inventory SET quantity -= N
        DB-->>IS: Updated
        IS-->>SMS: Stock removed successfully
        
        SMS->>DB: INSERT INTO stock_movements
        DB-->>SMS: Movement recorded
        
        SMS->>K: Publish StockIssuedEvent
        
        SMS->>PS: GET minimumStockLevel
        PS-->>SMS: minimumStockLevel = 20
        SMS->>IS: GET current stock
        IS-->>SMS: currentStock = 15
        
        Note over SMS: 15 <= 20 → LOW STOCK!
        SMS->>K: Publish LowStockEvent
        
        SMS-->>GW: 201 Created
        GW-->>C: Stock movement response
        
        K->>NS: Consume StockIssuedEvent
        NS->>DB: Log notification
        
        K->>NS: Consume LowStockEvent
        NS->>DB: Log LOW STOCK ALERT
        Note over NS: ⚠️ LOW STOCK ALERT
        
    else Insufficient Stock
        IS-->>SMS: 400 Bad Request (Insufficient stock)
        SMS-->>GW: Error response
        GW-->>C: 400 "Insufficient stock. Available: 15, Requested: 50"
    end
```

## Example: Insufficient Stock

```json
POST /api/stock-movements
{
    "productId": 1,
    "warehouseId": 1,
    "quantity": 50,
    "movementType": "OUT",
    "referenceNumber": "MFG-2024-001",
    "remarks": "Issued to manufacturing line"
}

// Response (400 Bad Request):
{
    "success": false,
    "message": "Insufficient stock. Available: 15, Requested: 50"
}
```

# API Documentation & Testing Guide

## Base URL
```
http://localhost:8080/api
```

## Authentication

All endpoints except `/api/auth/login` require a valid JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## API Endpoints

### 1. Authentication

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Success Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "fullName": "System Administrator",
  "message": "Login successful"
}
```

**Error Response (401):**
```json
{
  "token": null,
  "username": null,
  "fullName": null,
  "message": "Invalid username or password"
}
```

#### Validate Token
```http
GET /api/auth/validate?username=admin
Authorization: Bearer <token>
```

**Success Response (200):**
```
Token is valid
```

---

### 2. Lost Items Management

#### Report Lost Item
```http
POST /api/items/report
Authorization: Bearer <token>
Content-Type: application/json

{
  "itemName": "iPhone 13 Pro",
  "description": "Black iPhone 13 Pro with blue case",
  "location": "Library 2nd Floor",
  "studentName": "John Smith",
  "contactInfo": "john.smith@email.com / 555-0123"
}
```

**Success Response (201):**
```json
{
  "id": 1,
  "itemName": "iPhone 13 Pro",
  "description": "Black iPhone 13 Pro with blue case",
  "location": "Library 2nd Floor",
  "studentName": "John Smith",
  "contactInfo": "john.smith@email.com / 555-0123",
  "reportedDate": "2025-10-20T14:30:00",
  "found": false,
  "foundDate": null,
  "foundBy": null,
  "remarks": null
}
```

#### Get All Items
```http
GET /api/items
Authorization: Bearer <token>
```

**Response (200):**
```json
[
  {
    "id": 1,
    "itemName": "iPhone 13 Pro",
    "description": "Black iPhone 13 Pro with blue case",
    "location": "Library 2nd Floor",
    "studentName": "John Smith",
    "contactInfo": "john.smith@email.com / 555-0123",
    "reportedDate": "2025-10-20T14:30:00",
    "found": false,
    "foundDate": null,
    "foundBy": null,
    "remarks": null
  }
]
```

#### Get Lost Items Only
```http
GET /api/items/lost
Authorization: Bearer <token>
```

Returns only items where `found = false`

#### Get Found Items Only
```http
GET /api/items/found
Authorization: Bearer <token>
```

Returns only items where `found = true`

#### Get Item by ID
```http
GET /api/items/1
Authorization: Bearer <token>
```

**Success Response (200):**
```json
{
  "id": 1,
  "itemName": "iPhone 13 Pro",
  ...
}
```

**Error Response (404):**
```json
"Item not found with id: 1"
```

#### Mark Item as Found
```http
PUT /api/items/1/found
Authorization: Bearer <token>
Content-Type: application/json

{
  "foundBy": "Jane Doe",
  "remarks": "Found in cafeteria, turned in to security desk"
}
```

**Success Response (200):**
```json
{
  "id": 1,
  "itemName": "iPhone 13 Pro",
  "description": "Black iPhone 13 Pro with blue case",
  "location": "Library 2nd Floor",
  "studentName": "John Smith",
  "contactInfo": "john.smith@email.com / 555-0123",
  "reportedDate": "2025-10-20T14:30:00",
  "found": true,
  "foundDate": "2025-10-20T16:45:00",
  "foundBy": "Jane Doe",
  "remarks": "Found in cafeteria, turned in to security desk"
}
```

#### Search by Item Name
```http
GET /api/items/search/item?name=iphone
Authorization: Bearer <token>
```

Returns all items where `itemName` contains "iphone" (case-insensitive)

#### Search by Location
```http
GET /api/items/search/location?location=library
Authorization: Bearer <token>
```

Returns all items where `location` contains "library" (case-insensitive)

#### Search by Student Name
```http
GET /api/items/search/student?name=john
Authorization: Bearer <token>
```

Returns all items where `studentName` contains "john" (case-insensitive)

#### Delete Item
```http
DELETE /api/items/1
Authorization: Bearer <token>
```

**Success Response (200):**
```
Item deleted successfully
```

**Error Response (404):**
```
Item not found with id: 1
```

---

## Testing with PowerShell

### 1. Login and Get Token
```powershell
$loginData = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $loginData

$token = $response.token
Write-Host "Token: $token"
```

### 2. Report Lost Item
```powershell
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$itemData = @{
    itemName = "Laptop"
    description = "Dell XPS 15 with stickers"
    location = "Computer Lab A"
    studentName = "Alice Johnson"
    contactInfo = "alice.j@email.com"
} | ConvertTo-Json

$item = Invoke-RestMethod -Uri "http://localhost:8080/api/items/report" `
    -Method POST `
    -Headers $headers `
    -Body $itemData

Write-Host "Item created with ID: $($item.id)"
```

### 3. Get All Items
```powershell
$headers = @{
    "Authorization" = "Bearer $token"
}

$items = Invoke-RestMethod -Uri "http://localhost:8080/api/items" `
    -Method GET `
    -Headers $headers

$items | Format-Table -Property id, itemName, location, found
```

### 4. Mark Item as Found
```powershell
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$foundData = @{
    foundBy = "Security Staff"
    remarks = "Turned in to lost and found desk"
} | ConvertTo-Json

$updated = Invoke-RestMethod -Uri "http://localhost:8080/api/items/1/found" `
    -Method PUT `
    -Headers $headers `
    -Body $foundData

Write-Host "Item marked as found: $($updated.found)"
```

### 5. Search Items
```powershell
$headers = @{
    "Authorization" = "Bearer $token"
}

$results = Invoke-RestMethod -Uri "http://localhost:8080/api/items/search/item?name=laptop" `
    -Method GET `
    -Headers $headers

$results | Format-List
```

### 6. Delete Item
```powershell
$headers = @{
    "Authorization" = "Bearer $token"
}

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/items/1" `
    -Method DELETE `
    -Headers $headers

Write-Host $response
```

---

## Testing with cURL

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 2. Report Lost Item
```bash
curl -X POST http://localhost:8080/api/items/report \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "itemName": "Backpack",
    "description": "Blue Jansport backpack",
    "location": "Cafeteria",
    "studentName": "Bob Wilson",
    "contactInfo": "bob.w@email.com"
  }'
```

### 3. Get All Items
```bash
curl -X GET http://localhost:8080/api/items \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 4. Mark as Found
```bash
curl -X PUT http://localhost:8080/api/items/1/found \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "foundBy": "Janitor",
    "remarks": "Found under table"
  }'
```

### 5. Delete Item
```bash
curl -X DELETE http://localhost:8080/api/items/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Complete PowerShell Test Script

Save as `test-api.ps1`:

```powershell
# Test Script for Lost and Found API

Write-Host "=== Lost and Found API Test ===" -ForegroundColor Green
Write-Host ""

# 1. Login
Write-Host "1. Testing Login..." -ForegroundColor Yellow
$loginData = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginData
    
    $token = $response.token
    Write-Host "✓ Login successful" -ForegroundColor Green
    Write-Host "  Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
} catch {
    Write-Host "✗ Login failed: $_" -ForegroundColor Red
    exit
}

Write-Host ""

# 2. Report Lost Item
Write-Host "2. Reporting Lost Item..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$itemData = @{
    itemName = "Test Item - Laptop"
    description = "MacBook Pro 16 inch"
    location = "Computer Lab"
    studentName = "Test Student"
    contactInfo = "test@email.com"
} | ConvertTo-Json

try {
    $item = Invoke-RestMethod -Uri "http://localhost:8080/api/items/report" `
        -Method POST `
        -Headers $headers `
        -Body $itemData
    
    Write-Host "✓ Item reported successfully" -ForegroundColor Green
    Write-Host "  ID: $($item.id)" -ForegroundColor Gray
    $itemId = $item.id
} catch {
    Write-Host "✗ Failed to report item: $_" -ForegroundColor Red
}

Write-Host ""

# 3. Get All Items
Write-Host "3. Getting All Items..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
}

try {
    $items = Invoke-RestMethod -Uri "http://localhost:8080/api/items" `
        -Method GET `
        -Headers $headers
    
    Write-Host "✓ Retrieved $($items.Count) items" -ForegroundColor Green
    $items | Format-Table -Property id, itemName, location, found -AutoSize
} catch {
    Write-Host "✗ Failed to get items: $_" -ForegroundColor Red
}

Write-Host ""

# 4. Search Items
Write-Host "4. Searching Items..." -ForegroundColor Yellow
try {
    $results = Invoke-RestMethod -Uri "http://localhost:8080/api/items/search/item?name=laptop" `
        -Method GET `
        -Headers $headers
    
    Write-Host "✓ Found $($results.Count) items matching 'laptop'" -ForegroundColor Green
} catch {
    Write-Host "✗ Search failed: $_" -ForegroundColor Red
}

Write-Host ""

# 5. Mark as Found
Write-Host "5. Marking Item as Found..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$foundData = @{
    foundBy = "Test Admin"
    remarks = "Automated test"
} | ConvertTo-Json

try {
    $updated = Invoke-RestMethod -Uri "http://localhost:8080/api/items/$itemId/found" `
        -Method PUT `
        -Headers $headers `
        -Body $foundData
    
    Write-Host "✓ Item marked as found" -ForegroundColor Green
    Write-Host "  Status: $($updated.found)" -ForegroundColor Gray
    Write-Host "  Found by: $($updated.foundBy)" -ForegroundColor Gray
} catch {
    Write-Host "✗ Failed to mark as found: $_" -ForegroundColor Red
}

Write-Host ""

# 6. Delete Item
Write-Host "6. Deleting Item..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
}

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/items/$itemId" `
        -Method DELETE `
        -Headers $headers
    
    Write-Host "✓ Item deleted successfully" -ForegroundColor Green
} catch {
    Write-Host "✗ Failed to delete item: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Green
```

Run the test:
```powershell
.\test-api.ps1
```

---

## Error Codes

| Code | Meaning | Description |
|------|---------|-------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 401 | Unauthorized | Invalid credentials or token |
| 404 | Not Found | Resource not found |
| 500 | Internal Server Error | Server error |

---

## Notes

- All timestamps are in ISO 8601 format
- All search queries are case-insensitive
- JWT tokens expire after 24 hours (configurable in application.properties)
- Dates are automatically set by the server

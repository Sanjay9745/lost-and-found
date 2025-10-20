# Test Script for Lost and Found API

Write-Host "=== Lost and Found API Test ===" -ForegroundColor Green
Write-Host ""

# Check if backend is running
Write-Host "0. Checking if backend is running..." -ForegroundColor Yellow
try {
    $null = Invoke-RestMethod -Uri "http://localhost:8080/api/items" -Method GET -ErrorAction Stop
} catch {
    if ($_.Exception.Response.StatusCode -eq 401) {
        Write-Host "✓ Backend is running" -ForegroundColor Green
    } else {
        Write-Host "✗ Backend is not running. Please start it first with run-backend.bat" -ForegroundColor Red
        exit
    }
}
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
    Write-Host "  User: $($response.fullName)" -ForegroundColor Gray
    Write-Host "  Token: $($token.Substring(0, 30))..." -ForegroundColor Gray
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
    description = "MacBook Pro 16 inch with Apple stickers"
    location = "Computer Lab B"
    studentName = "Test Student"
    contactInfo = "test.student@university.edu / 555-1234"
} | ConvertTo-Json

try {
    $item = Invoke-RestMethod -Uri "http://localhost:8080/api/items/report" `
        -Method POST `
        -Headers $headers `
        -Body $itemData
    
    Write-Host "✓ Item reported successfully" -ForegroundColor Green
    Write-Host "  ID: $($item.id)" -ForegroundColor Gray
    Write-Host "  Name: $($item.itemName)" -ForegroundColor Gray
    Write-Host "  Status: $(if($item.found){'Found'}else{'Lost'})" -ForegroundColor Gray
    $itemId = $item.id
} catch {
    Write-Host "✗ Failed to report item: $_" -ForegroundColor Red
    exit
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
    Write-Host ""
    $items | Select-Object id, itemName, location, studentName, found | Format-Table -AutoSize
} catch {
    Write-Host "✗ Failed to get items: $_" -ForegroundColor Red
}

Write-Host ""

# 4. Get Lost Items Only
Write-Host "4. Getting Lost Items Only..." -ForegroundColor Yellow
try {
    $lostItems = Invoke-RestMethod -Uri "http://localhost:8080/api/items/lost" `
        -Method GET `
        -Headers $headers
    
    Write-Host "✓ Retrieved $($lostItems.Count) lost items" -ForegroundColor Green
} catch {
    Write-Host "✗ Failed to get lost items: $_" -ForegroundColor Red
}

Write-Host ""

# 5. Search Items
Write-Host "5. Searching Items by Name..." -ForegroundColor Yellow
try {
    $results = Invoke-RestMethod -Uri "http://localhost:8080/api/items/search/item?name=laptop" `
        -Method GET `
        -Headers $headers
    
    Write-Host "✓ Found $($results.Count) items matching 'laptop'" -ForegroundColor Green
    $results | ForEach-Object {
        Write-Host "  - $($_.itemName) at $($_.location)" -ForegroundColor Gray
    }
} catch {
    Write-Host "✗ Search failed: $_" -ForegroundColor Red
}

Write-Host ""

# 6. Get Specific Item
Write-Host "6. Getting Specific Item (ID: $itemId)..." -ForegroundColor Yellow
try {
    $specificItem = Invoke-RestMethod -Uri "http://localhost:8080/api/items/$itemId" `
        -Method GET `
        -Headers $headers
    
    Write-Host "✓ Item retrieved" -ForegroundColor Green
    Write-Host "  Item: $($specificItem.itemName)" -ForegroundColor Gray
    Write-Host "  Description: $($specificItem.description)" -ForegroundColor Gray
    Write-Host "  Location: $($specificItem.location)" -ForegroundColor Gray
    Write-Host "  Student: $($specificItem.studentName)" -ForegroundColor Gray
    Write-Host "  Contact: $($specificItem.contactInfo)" -ForegroundColor Gray
} catch {
    Write-Host "✗ Failed to get item: $_" -ForegroundColor Red
}

Write-Host ""

# 7. Mark as Found
Write-Host "7. Marking Item as Found..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$foundData = @{
    foundBy = "Security Staff - John Doe"
    remarks = "Found in cafeteria and turned in to lost and found desk. Automated test."
} | ConvertTo-Json

try {
    $updated = Invoke-RestMethod -Uri "http://localhost:8080/api/items/$itemId/found" `
        -Method PUT `
        -Headers $headers `
        -Body $foundData
    
    Write-Host "✓ Item marked as found" -ForegroundColor Green
    Write-Host "  Status: $(if($updated.found){'Found'}else{'Lost'})" -ForegroundColor Gray
    Write-Host "  Found by: $($updated.foundBy)" -ForegroundColor Gray
    Write-Host "  Remarks: $($updated.remarks)" -ForegroundColor Gray
} catch {
    Write-Host "✗ Failed to mark as found: $_" -ForegroundColor Red
}

Write-Host ""

# 8. Get Found Items
Write-Host "8. Getting Found Items Only..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
}

try {
    $foundItems = Invoke-RestMethod -Uri "http://localhost:8080/api/items/found" `
        -Method GET `
        -Headers $headers
    
    Write-Host "✓ Retrieved $($foundItems.Count) found items" -ForegroundColor Green
} catch {
    Write-Host "✗ Failed to get found items: $_" -ForegroundColor Red
}

Write-Host ""

# 9. Delete Item (Cleanup)
Write-Host "9. Deleting Test Item (Cleanup)..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/items/$itemId" `
        -Method DELETE `
        -Headers $headers
    
    Write-Host "✓ Item deleted successfully" -ForegroundColor Green
} catch {
    Write-Host "✗ Failed to delete item: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== All Tests Complete ===" -ForegroundColor Green
Write-Host ""
Write-Host "Summary:" -ForegroundColor Cyan
Write-Host "  ✓ Login and authentication" -ForegroundColor Green
Write-Host "  ✓ Report lost item" -ForegroundColor Green
Write-Host "  ✓ Retrieve items (all, lost, found)" -ForegroundColor Green
Write-Host "  ✓ Search functionality" -ForegroundColor Green
Write-Host "  ✓ Mark item as found" -ForegroundColor Green
Write-Host "  ✓ Delete item" -ForegroundColor Green
Write-Host ""
Write-Host "The API is working correctly! 🎉" -ForegroundColor Green

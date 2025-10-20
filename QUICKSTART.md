# Quick Start Guide

## Option 1: Using Batch Scripts (Windows)

### Step 1: Start Backend
Double-click `run-backend.bat` or run in terminal:
```bash
run-backend.bat
```

Wait until you see: "Started LostAndFoundApplication"

### Step 2: Start Frontend
In a NEW terminal, double-click `run-frontend.bat` or run:
```bash
run-frontend.bat
```

## Option 2: Using Maven Commands

### Terminal 1 - Start Backend:
```bash
cd e:\Projects\java\lost-and-found
mvn spring-boot:run
```

### Terminal 2 - Start Frontend:
```bash
cd e:\Projects\java\lost-and-found
mvn exec:java -Dexec.mainClass="com.lostandfound.swing.SwingApplication"
```

## Login Credentials

**Username:** admin  
**Password:** admin123

## Troubleshooting

### "Port 8080 already in use"
Kill the process using port 8080:
```bash
netstat -ano | findstr :8080
taskkill /PID <process_id> /F
```

### "Connection refused" in Swing app
Make sure the backend is running and fully started before launching the frontend.

### Maven not found
Install Maven and add it to your system PATH.
Download from: https://maven.apache.org/download.cgi

## Usage Flow

1. **Login** with admin credentials
2. **Report Lost Item** - Click button and fill in the form
3. **View Items** - See all lost and found items in the table
4. **Filter** - Use dropdown to show: All Items, Lost Items, or Found Items
5. **Search** - Search items by name
6. **Mark as Found** - Select item and click "Mark as Found"
7. **Delete** - Select item and click "Delete Item"
8. **Refresh** - Click to reload the latest data

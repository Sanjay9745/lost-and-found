# Installation Guide

## Prerequisites Installation

### 1. Java Development Kit (JDK) 17+

#### Check if Java is installed:
```bash
java -version
```

#### If not installed, download and install:
- Download from: https://adoptium.net/ (Recommended)
- Or Oracle JDK: https://www.oracle.com/java/technologies/downloads/
- Install and add to PATH

### 2. Apache Maven

#### Check if Maven is installed:
```bash
mvn -version
```

#### If not installed:

**Option A: Using Chocolatey (Recommended for Windows)**
```bash
choco install maven
```

**Option B: Manual Installation**
1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to System Environment Variables:
   - Add `MAVEN_HOME = C:\Program Files\Apache\maven`
   - Add `%MAVEN_HOME%\bin` to PATH
4. Restart terminal and verify: `mvn -version`

## Building the Project

Once prerequisites are installed:

```bash
cd e:\Projects\java\lost-and-found
mvn clean install
```

This will:
- Download all dependencies
- Compile the code
- Run tests
- Create executable JAR files

## Running the Application

### Method 1: Using Batch Scripts (Easiest)

1. **Start Backend:**
   - Double-click `run-backend.bat`
   - Wait for "Started LostAndFoundApplication" message

2. **Start Frontend:**
   - Double-click `run-frontend.bat`
   - Login with: admin / admin123

### Method 2: Using Maven Commands

**Terminal 1 - Backend:**
```bash
cd e:\Projects\java\lost-and-found
mvn spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
cd e:\Projects\java\lost-and-found
mvn exec:java -Dexec.mainClass="com.lostandfound.swing.SwingApplication"
```

### Method 3: Using JAR Files (After building)

**Terminal 1 - Backend:**
```bash
java -jar target/lost-and-found-1.0.0.jar
```

**Terminal 2 - Frontend:**
```bash
java -cp target/lost-and-found-1.0.0.jar com.lostandfound.swing.SwingApplication
```

## First Time Setup

1. Install Java JDK 17+
2. Install Maven
3. Navigate to project directory
4. Run: `mvn clean install`
5. Start backend: `run-backend.bat`
6. Start frontend: `run-frontend.bat`
7. Login with default credentials

## Verification

After starting the backend, verify it's running:
- Open browser: http://localhost:8080/h2-console
- API test: http://localhost:8080/api/items

## Common Issues

### Maven command not found
- Make sure Maven is installed and in PATH
- Restart terminal after installation
- Verify with: `mvn -version`

### Java version mismatch
- Make sure Java 17 or higher is installed
- Check with: `java -version`
- Update JAVA_HOME environment variable if needed

### Port 8080 already in use
```bash
# Find process
netstat -ano | findstr :8080
# Kill process (replace PID)
taskkill /PID <PID> /F
```

### Connection refused in Swing app
- Make sure backend is fully started
- Check console for "Started LostAndFoundApplication"
- Verify backend is accessible: http://localhost:8080/h2-console

## Next Steps

See `QUICKSTART.md` for usage instructions and workflow.

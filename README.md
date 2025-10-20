# Lost and Found Management System

A comprehensive Lost and Found management system built with **Java Spring Boot** backend and **Java Swing** frontend.

## Features

### Backend (Spring Boot)
- RESTful API for managing lost items
- JWT-based admin authentication
- H2 in-memory database
- Spring Security for authorization
- Complete CRUD operations for lost items

### Frontend (Swing Desktop Application)
- Admin login interface
- Report lost items with details
- Mark items as found
- Search and filter functionality
- Clean and intuitive UI

## Technology Stack

- **Backend:**
  - Java 17
  - Spring Boot 3.1.5
  - Spring Data JPA
  - Spring Security
  - H2 Database
  - JWT (JSON Web Tokens)
  - Maven

- **Frontend:**
  - Java Swing
  - Jackson for JSON processing

## Project Structure

```
lost-and-found/
├── src/main/java/com/lostandfound/
│   ├── LostAndFoundApplication.java          # Main Spring Boot application
│   ├── config/
│   │   └── DataInitializer.java              # Creates default admin user
│   ├── controller/
│   │   ├── AuthController.java               # Authentication endpoints
│   │   └── LostItemController.java           # Lost item CRUD endpoints
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── LostItemRequest.java
│   │   └── MarkFoundRequest.java
│   ├── model/
│   │   ├── Admin.java                        # Admin entity
│   │   └── LostItem.java                     # Lost item entity
│   ├── repository/
│   │   ├── AdminRepository.java
│   │   └── LostItemRepository.java
│   ├── security/
│   │   ├── JwtUtil.java                      # JWT token utilities
│   │   └── SecurityConfig.java               # Security configuration
│   ├── service/
│   │   ├── AdminService.java
│   │   └── LostItemService.java
│   └── swing/
│       ├── SwingApplication.java             # Swing main class
│       ├── api/
│       │   └── ApiClient.java                # REST API client
│       └── ui/
│           ├── LoginFrame.java               # Login window
│           └── MainFrame.java                # Main application window
└── src/main/resources/
    └── application.properties                # Application configuration
```

## How to Run

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Step 1: Start the Backend Server

```bash
# Navigate to project directory
cd e:\Projects\java\lost-and-found

# Run the Spring Boot application
mvn spring-boot:run
```

The backend server will start at `http://localhost:8080`

### Step 2: Start the Swing Frontend

In a separate terminal:

```bash
# Run the Swing application
mvn exec:java -Dexec.mainClass="com.lostandfound.swing.SwingApplication"
```

## Default Admin Credentials

- **Username:** `admin`
- **Password:** `admin123`

## API Endpoints

### Authentication
- `POST /api/auth/login` - Admin login
- `GET /api/auth/validate` - Validate JWT token

### Lost Items Management
- `POST /api/items/report` - Report a lost item
- `GET /api/items` - Get all items
- `GET /api/items/lost` - Get only lost items
- `GET /api/items/found` - Get only found items
- `GET /api/items/{id}` - Get item by ID
- `PUT /api/items/{id}/found` - Mark item as found
- `DELETE /api/items/{id}` - Delete an item
- `GET /api/items/search/item?name={name}` - Search by item name
- `GET /api/items/search/location?location={location}` - Search by location
- `GET /api/items/search/student?name={name}` - Search by student name

## Database

The application uses H2 in-memory database. You can access the H2 console at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/lostandfound`
- Username: `admin`
- Password: `admin123`

## Workflow

1. **Student Reports Lost Item:**
   - Admin opens the Swing application
   - Admin logs in with credentials
   - Admin clicks "Report Lost Item"
   - Enters: Item name, description, location, student name, contact info
   - System saves the item with status "Lost"

2. **Someone Finds the Item:**
   - Admin selects the item from the table
   - Admin clicks "Mark as Found"
   - Enters: Who found it and any remarks
   - System updates the item status to "Found" with timestamp

3. **Search and Filter:**
   - Filter items by: All Items, Lost Items, or Found Items
   - Search items by name
   - View complete details in the table

## Data Model

### Admin
- ID (Auto-generated)
- Username (Unique)
- Password (Encrypted)
- Full Name
- Email
- Role

### Lost Item
- ID (Auto-generated)
- Item Name
- Description
- Location (where it was lost)
- Student Name
- Contact Information
- Reported Date
- Found Status (true/false)
- Found Date
- Found By
- Remarks

## Security Features

- Password encryption using BCrypt
- JWT-based authentication
- Stateless session management
- CORS enabled for frontend communication
- Protected API endpoints (except login)

## Future Enhancements

- Email notifications to students when items are found
- Image upload for lost items
- Student self-service portal
- Reports and analytics dashboard
- Multiple admin roles and permissions
- SMS notifications

## License

This project is created for educational purposes.

## Author

Professional Coding Assistant

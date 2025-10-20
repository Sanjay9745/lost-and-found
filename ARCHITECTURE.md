# System Architecture

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                          PRESENTATION LAYER                          │
│                                                                       │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    Java Swing Desktop UI                       │  │
│  │  ┌──────────────┐              ┌──────────────────────────┐   │  │
│  │  │ LoginFrame   │              │     MainFrame            │   │  │
│  │  │              │              │  - JTable (Items List)   │   │  │
│  │  │ - Username   │──────────────│  - Report Lost Item      │   │  │
│  │  │ - Password   │  After Login │  - Mark as Found         │   │  │
│  │  │ - Login Btn  │              │  - Search & Filter       │   │  │
│  │  └──────────────┘              │  - Delete Item           │   │  │
│  │                                 └──────────────────────────┘   │  │
│  └────────────────────┬────────────────────────────────────────────┘  │
└────────────────────────┼───────────────────────────────────────────────┘
                         │
                         │ HTTP/REST
                         │ (JSON)
                         │
┌────────────────────────▼───────────────────────────────────────────────┐
│                          API CLIENT LAYER                              │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                        ApiClient.java                            │  │
│  │  - POST /api/auth/login                                          │  │
│  │  - GET /api/items                                                │  │
│  │  - POST /api/items/report                                        │  │
│  │  - PUT /api/items/{id}/found                                     │  │
│  │  - DELETE /api/items/{id}                                        │  │
│  │  - JWT Token Management                                          │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└────────────────────────┬───────────────────────────────────────────────┘
                         │
                         │ HTTP Requests
                         │ Port 8080
                         │
┌────────────────────────▼───────────────────────────────────────────────┐
│                      SPRING BOOT BACKEND                               │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐  │
│  │                    CONTROLLER LAYER                             │  │
│  │  ┌──────────────────┐         ┌──────────────────────────┐     │  │
│  │  │ AuthController   │         │  LostItemController      │     │  │
│  │  │ /api/auth/*      │         │  /api/items/*            │     │  │
│  │  └────────┬─────────┘         └──────────┬───────────────┘     │  │
│  └───────────┼────────────────────────────────┼──────────────────────┘  │
│              │                                │                         │
│  ┌───────────▼────────────────────────────────▼──────────────────────┐  │
│  │                    SECURITY LAYER                                │  │
│  │  ┌────────────┐    ┌──────────────┐    ┌────────────────────┐  │  │
│  │  │SecurityConfig│   │  JwtUtil     │    │  BCrypt Password  │  │  │
│  │  │- JWT Filter │    │- Generate    │    │  Encoder          │  │  │
│  │  │- CORS       │    │- Validate    │    │                    │  │  │
│  │  └────────────┘    └──────────────┘    └────────────────────┘  │  │
│  └───────────┬────────────────────────────────┬──────────────────────┘  │
│              │                                │                         │
│  ┌───────────▼────────────────────────────────▼──────────────────────┐  │
│  │                    SERVICE LAYER                                 │  │
│  │  ┌──────────────────┐         ┌──────────────────────────┐      │  │
│  │  │  AdminService    │         │  LostItemService         │      │  │
│  │  │  - login()       │         │  - reportLostItem()      │      │  │
│  │  │  - createAdmin() │         │  - markAsFound()         │      │  │
│  │  │  - validateToken│         │  - getAllItems()         │      │  │
│  │  └────────┬─────────┘         └──────────┬───────────────┘      │  │
│  └───────────┼────────────────────────────────┼──────────────────────┘  │
│              │                                │                         │
│  ┌───────────▼────────────────────────────────▼──────────────────────┐  │
│  │                  REPOSITORY LAYER (Spring Data JPA)              │  │
│  │  ┌──────────────────┐         ┌──────────────────────────┐      │  │
│  │  │AdminRepository   │         │  LostItemRepository      │      │  │
│  │  │- findByUsername()│         │  - findByFound()         │      │  │
│  │  │- save()          │         │  - searchByItemName()    │      │  │
│  │  └────────┬─────────┘         └──────────┬───────────────┘      │  │
│  └───────────┼────────────────────────────────┼──────────────────────┘  │
│              │                                │                         │
│  ┌───────────▼────────────────────────────────▼──────────────────────┐  │
│  │                      ENTITY LAYER                                │  │
│  │  ┌──────────────────┐         ┌──────────────────────────┐      │  │
│  │  │  Admin Entity    │         │  LostItem Entity         │      │  │
│  │  │  @Entity         │         │  @Entity                 │      │  │
│  │  │  - id            │         │  - id                    │      │  │
│  │  │  - username      │         │  - itemName              │      │  │
│  │  │  - password      │         │  - description           │      │  │
│  │  │  - fullName      │         │  - location              │      │  │
│  │  │  - email         │         │  - studentName           │      │  │
│  │  │  - role          │         │  - contactInfo           │      │  │
│  │  └────────┬─────────┘         │  - reportedDate          │      │  │
│  │           │                   │  - found                 │      │  │
│  │           │                   │  - foundDate             │      │  │
│  │           │                   │  - foundBy               │      │  │
│  │           │                   └──────────┬───────────────┘      │  │
│  └───────────┼────────────────────────────────┼──────────────────────┘  │
└──────────────┼────────────────────────────────┼────────────────────────┘
               │                                │
               │            JPA/Hibernate       │
               │                                │
┌──────────────▼────────────────────────────────▼────────────────────────┐
│                        DATABASE LAYER                                  │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                      H2 Database                                 │  │
│  │  ┌────────────────┐         ┌──────────────────────────┐        │  │
│  │  │  ADMINS Table  │         │  LOST_ITEMS Table        │        │  │
│  │  │                │         │                          │        │  │
│  │  │  Default:      │         │  - All lost items        │        │  │
│  │  │  admin/admin123│         │  - Lost/Found status     │        │  │
│  │  └────────────────┘         └──────────────────────────┘        │  │
│  │                                                                  │  │
│  │  File Location: ./data/lostandfound.mv.db                       │  │
│  │  Console: http://localhost:8080/h2-console                      │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘
```

## Request Flow Example

### 1. Login Flow
```
User enters credentials in LoginFrame
    ↓
ApiClient.post("/auth/login", {username, password})
    ↓
AuthController.login()
    ↓
AdminService.login()
    ↓
AdminRepository.findByUsername()
    ↓
Password verification (BCrypt)
    ↓
JwtUtil.generateToken()
    ↓
Return JWT token to UI
    ↓
Store token in ApiClient
    ↓
Open MainFrame
```

### 2. Report Lost Item Flow
```
Admin fills form in MainFrame
    ↓
ApiClient.post("/items/report", itemData)
    ↓
LostItemController.reportLostItem()
    ↓
LostItemService.reportLostItem()
    ↓
Create LostItem entity (found = false)
    ↓
LostItemRepository.save()
    ↓
Insert into LOST_ITEMS table
    ↓
Return saved item to UI
    ↓
Refresh table display
```

### 3. Mark as Found Flow
```
Admin selects item and clicks "Mark as Found"
    ↓
Enter "Found By" information
    ↓
ApiClient.put("/items/{id}/found", {foundBy, remarks})
    ↓
LostItemController.markAsFound()
    ↓
LostItemService.markAsFound()
    ↓
Update item: found = true, foundDate = now
    ↓
LostItemRepository.save()
    ↓
Update LOST_ITEMS table
    ↓
Return updated item to UI
    ↓
Refresh table display
```

## Technology Stack Detail

### Backend Technologies
```
Spring Boot 3.1.5
├── Spring Web (REST API)
├── Spring Data JPA (Database Access)
├── Spring Security (Authentication/Authorization)
├── H2 Database (Embedded Database)
├── Lombok (Boilerplate Reduction)
├── JWT (io.jsonwebtoken)
└── Jackson (JSON Processing)
```

### Frontend Technologies
```
Java Swing
├── JFrame (Window Management)
├── JTable (Data Display)
├── JDialog (Modal Forms)
├── SwingWorker (Background Tasks)
└── Jackson (JSON Parsing)
```

## Security Architecture

```
┌──────────────────────────────────────────────┐
│         Security Configuration               │
├──────────────────────────────────────────────┤
│  - CORS: Enabled for all origins             │
│  - CSRF: Disabled (Stateless JWT)            │
│  - Session: STATELESS                        │
│  - Public Endpoints: /api/auth/login         │
│  - Protected: All other /api/* endpoints     │
└──────────────────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────┐
│          JWT Authentication                  │
├──────────────────────────────────────────────┤
│  1. User logs in with credentials            │
│  2. Server validates username/password       │
│  3. Server generates JWT token               │
│  4. Client stores token                      │
│  5. Client sends token in Authorization      │
│     header for subsequent requests           │
│  6. Server validates token on each request   │
└──────────────────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────┐
│         Password Security                    │
├──────────────────────────────────────────────┤
│  - Algorithm: BCrypt                         │
│  - Strength: 10 rounds (default)             │
│  - Stored: Encrypted hash in database        │
│  - Never: Plain text passwords               │
└──────────────────────────────────────────────┘
```

## Data Model Relationships

```
┌─────────────────┐
│     Admin       │
│─────────────────│       No Direct Relationship
│  - id (PK)      │       (Admin manages items via UI)
│  - username     │
│  - password     │
│  - fullName     │
│  - email        │
│  - role         │
└─────────────────┘

┌─────────────────────┐
│     LostItem        │
│─────────────────────│
│  - id (PK)          │
│  - itemName         │
│  - description      │
│  - location         │
│  - studentName      │← Student who lost it
│  - contactInfo      │
│  - reportedDate     │
│  - found (boolean)  │← Status flag
│  - foundDate        │
│  - foundBy          │← Person who found it
│  - remarks          │
└─────────────────────┘
```

## Deployment Architecture

```
┌────────────────────────────────────────────┐
│          Development Environment            │
├────────────────────────────────────────────┤
│  Backend:  http://localhost:8080           │
│  Frontend: Swing Desktop Application       │
│  Database: ./data/lostandfound.mv.db       │
│  Console:  http://localhost:8080/h2-console│
└────────────────────────────────────────────┘

┌────────────────────────────────────────────┐
│       Production Deployment (Future)        │
├────────────────────────────────────────────┤
│  Backend:  Deploy Spring Boot to:          │
│            - Heroku, AWS, Azure, etc.       │
│  Frontend: Distribute as executable JAR    │
│  Database: Replace H2 with:                │
│            - PostgreSQL, MySQL, etc.        │
└────────────────────────────────────────────┘
```

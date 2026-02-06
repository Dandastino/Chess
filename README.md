# ♟️Chess Game - Backend Project

A powerful **Java Spring Boot backend** for playing, analyzing, and monitoring chess games.  
Designed in multiple phases from **medium rule-based logic → advanced Stockfish-powered AI insights**, and ready to support a full frontend later.

![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Security-JWT-6DB33F?logo=springsecurity&logoColor=white)
![WebSockets](https://img.shields.io/badge/Protocol-WebSocket-010101?logo=socket.io&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-003B57?logo=postgresql&logoColor=white)
![Stockfish](https://img.shields.io/badge/Engine-Stockfish-000000)
![Elo Rating](https://img.shields.io/badge/Rating-Elo–System-FFD700)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apache-maven&logoColor=white)
![REST API](https://img.shields.io/badge/API-REST-009688?logo=api&logoColor=white)
![Email](https://img.shields.io/badge/Email-SMTP-EA4335?logo=gmail&logoColor=white)
![Cloudinary](https://img.shields.io/badge/CDN-Cloudinary-3448C5?logo=cloudinary&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?logo=opensourceinitiative&logoColor=white)
![Status](https://img.shields.io/badge/Status-Production%20Ready-brightgreen)

---

## Table of Contents
1. [Quick Start](#-quick-start)
2. [Prerequisites & Dependencies](#-prerequisites--dependencies)
3. [Environment Configuration](#-environment-configuration)
4. [Running the Application](#-running-the-application)
5. [Testing](#-testing)
6. [Project Architecture](#-project-architecture)
7. [REST API Documentation](#-rest-api-documentation)
8. [Features & Implementation](#-features--implementation)
9. [Database Design](#-database-design)
10. [Third-Party Integrations](#-third-party-integrations)
11. [Security](#-security)

---

## Quick Start

### Clone the Repository
```bash
git clone https://github.com/Dandastino/Chess
cd chess
```

### Quick Setup (Linux/macOS)
```bash
# 1. Copy environment template
cp env.example.properties env.properties

# 2. Create PostgreSQL database
createdb chess

# 3. Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:3001`

---

## Prerequisites & Dependencies

### Required Tools
| Tool | Version | Purpose |
|------|---------|---------|
| **Java (JDK)** | 21 or higher | Runtime environment |
| **Maven** | 3.9+ | Build automation (or use `./mvnw` wrapper) |
| **PostgreSQL** | 13+ | Database |

### Installation by OS

**macOS:**
```bash
brew install openjdk@21 postgresql
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get install openjdk-21-jdk postgresql postgresql-contrib
```

**Windows:**
- Download [JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
- Download [PostgreSQL](https://www.postgresql.org/download/windows/)

---

## Environment Configuration

### Step 1: Copy Template
```bash
cp env.example.properties env.properties
```

### Step 2: Configure Database
```properties
# PostgreSQL Connection
PG_HOST=localhost
PG_PORT=5432
PG_DATABASE=chess
PG_USERNAME=postgres
PG_PASSWORD=your_password
```

### Step 3: Configure Security
```properties
# JWT Secret (generate a random 64+ character string)
JWT_SECRET=your_secure_jwt_secret_key_here_min_64_characters
```

### Step 4: Configure Optional Services

**Email (Mailtrap/SMTP):**
```properties
MAIL_HOST=smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USERNAME=your_mailtrap_username
MAIL_PASSWORD=your_mailtrap_password
```

**Cloudinary (Image Upload):**
```properties
CLOUDINARY_NAME=your_cloudinary_name
CLOUDINARY_KEY=your_api_key
CLOUDINARY_SEC=your_api_secret
```

**Stockfish (Chess AI):**
```properties
stockfish.path=/usr/local/bin/stockfish     # macOS
# stockfish.path=/usr/games/stockfish       # Linux
# stockfish.path=C:\Program Files\Stockfish\stockfish.exe  # Windows
```

---

## Running the Application

### Option 1: Development Mode (with auto-reload)
```powershell
.\mvnw spring-boot:run
```

### Option 2: Build & Run JAR
```bash
# Clean build
.\mvnw clean package -DskipTests

# Run JAR
java -jar target\chess-0.0.1-SNAPSHOT.jar
```

**Server will be available at:** `http://localhost:3001`

---

### Manual API Testing (Postman)

**Import Steps:**
1. Open Postman → Click "Import"
2. Select the `postman_collection.json`
3. Set variables in Postman environment:
   - `baseURL`: http://localhost:3001
   - `accessToken`: (filled after login)

**Authentication Workflow:**

**Step 1: Register User**
```http
POST /users
Content-Type: application/json

{
  "username": "Grandmaster",
  "email": "test@chess.com",
  "password": "Password123!",
  "country": "Egypt"
}
```

**Step 2: Login**
```http
POST /auth/login
Content-Type: application/json

{
  "email": "test@chess.com",
  "password": "Password123!"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Step 3: Set Authorization**
- Open Postman → Click "Headers" tab
- Add header: `Authorization: Bearer {your_accessToken}`
- Copy-paste the token from Step 2 response

**Now all other endpoints will be accessible!**

---

## Project Architecture

### Tech Stack Overview
```
Frontend Layer          (Phase 3 - Not included)
        ↓
REST API / WebSocket    (Spring Boot Controllers)
        ↓
Service Layer          (Business Logic)
        ↓
Data Access Layer      (JPA Repository + PostgreSQL)
        ↓
External Services      (Stockfish, Cloudinary, SMTP)
```

### Real-Time Communication Flow
```
REST API Call
     ↓
Service Layer Updates Database
     ↓
GameBroadcastService Sends WebSocket Message
     ↓
Connected Clients Receive Real-Time Update
```

**WebSocket Topics:**
| Topic | Purpose |
|-------|---------|
| `/topic/game/{gameId}/move` | New move broadcasted |
| `/topic/game/{gameId}/status` | Game finish/resign/draw |
| `/topic/game/{gameId}/players` | Player join/leave |

---

### Error Handling & Status Codes

| Status | Exception | Meaning |
|--------|-----------|---------|
| **200** | OK | Request successful |
| **201** | CREATED | Resource created |
| **204** | NO CONTENT | Delete successful |
| **400** | BadRequestException | Invalid input or logic error |
| **401** | UnauthorizedException | Invalid credentials or missing token |
| **403** | ForbiddenException | Access denied |
| **404** | NotFoundException | Resource doesn't exist |
| **409** | ConflictException | Resource conflict or duplicate |
| **500** | UploadException | File upload failed |
| **502** | MailException | Email sending failed |

---

## Features & Implementation

### Phase 1 — Core Backend 

**Core Components Created:**

1. **User Management** (`src/main/java/dandastino/chess/users/`)
   - `User.java` - User entity with authentication and Elo rating (default 1200)
   - `UserController.java` - REST endpoints for registration, profile, avatar upload
   - `UserService.java` - User business logic
   - `UsersRepository.java` - Database access layer

2. **Authentication & Security** (`src/main/java/dandastino/chess/auth/`)
   - `AuthController.java` - Login/logout endpoints
   - `AuthService.java` - JWT token generation and validation
   - `AuthFilter.java` - Security filter for protected routes
   - `SecurityConfig.java` - Spring Security configuration
   - JWT-based authentication with bcrypt password hashing

3. **Game Management** (`src/main/java/dandastino/chess/games/`)
   - `Game.java` - Game entity with players, time control, status tracking
   - `GameController.java` - REST endpoints for game operations
   - `GameService.java` - Game logic and lifecycle management
   - `GamesRepository.java` - Database queries

4. **Move Handling** (`src/main/java/dandastino/chess/moves/`)
   - `Move.java` - Move entity with SAN notation, FEN tracking
   - `MoveController.java` - REST endpoints for creating/retrieving moves
   - `MoveService.java` - Move validation and storage
   - `MovesRepository.java` - Database access

5. **Chess Engine Logic** (`src/main/java/dandastino/chess/gameLogic/`)
   - `ChessEngine.java` - Pure Java move validator (~426 lines)
   - `Board.java` - Board state representation
   - `Fen.java` - FEN string parsing and generation
   - `MoveValidator.java` - Legal move validation
   - `MoveGenerator.java` - Pseudo-legal move generation
   - `BoardUtils.java` - Utility methods for board operations
   - Detects: Check, Checkmate, Stalemate, Draw conditions

6. **Game State Tracking** (`src/main/java/dandastino/chess/gameStates/`)
   - `GameState.java` - Position tracking with FEN storage
   - `GameStateService.java` - State management
   - `GameStateRepository.java` - Database persistence

7. **User Settings** (`src/main/java/dandastino/chess/userSettings/`)
   - `UserSetting.java` - Player preferences and configuration
   - `UserSettingController.java` - REST endpoints
   - `UserSettingService.java` - Settings management

8. **Friends & Social** (`src/main/java/dandastino/chess/friends/`)
   - `Friend.java` - Friend relationship entity
   - `FriendController.java` - Friend endpoints
   - `FriendService.java` - Friend logic
   - `FriendsRepository.java` - Database access

9. **Real-Time Communication** (`src/main/java/dandastino/chess/websocket/`)
   - `WebSocketConfig.java` - WebSocket STOMP configuration
   - `GameWebSocketController.java` - WebSocket message handlers
   - `GameBroadcastService.java` - Centralized broadcast logic
   - Automatic broadcasting integrated with REST API

10. **Additional Features**
    - `EmailService.java` - SMTP email notifications
    - `ExceptionsHandler.java` - Centralized error handling
    - `ValidationException.java` - Custom validation errors
    - Role-based access control

**REST API Endpoints (Phase 1):**
```bash
# User Management
POST   /users                                   # Register new user
GET    /users                                   # Get all users
GET    /users/{id}                              # Get user profile
PATCH  /users/{id}                              # Update profile
PATCH  /users/{id}/avatar                       # Upload avatar
GET    /users/leaderboard                       # Top 100 players by Elo rating
GET    /users/{id}/stats                        # Get player statistics 

# Authentication
POST   /auth/login                              # Login 
POST   /auth/logout                             # Logout 
POST   /auth/refresh                            # Refresh JWT token with new expiration

# Games
POST   /games                                   # Create new game
GET    /games/{id}                              # Get game details
GET    /games/available                         # List waiting games 
POST   /games/{id}/join                         # Join available game 
PATCH  /games/{id}/finish                       # End game 
PATCH  /games/{id}/resign                       # Resign from game 
PATCH  /games/{id}/draw                         # Propose or accept draw 

# User Settings
GET    /user-settings                           # Get all user settings
GET    /user-settings/user/{user_id}            # Get user settings by user ID 
PUT    /user-settings/user/{user_id}            # Update user settings 

# Moves
POST   /moves                                   # Create move
GET    /moves/{id}                              # Get move details
GET    /moves/game/{game_id}                    #  
GET    /moves/player/{player_id}                # Get player moves

# Messages
GET    /messages                                # Get all messages
GET    /messages/{id}                           # Get message by ID
POST   /messages                                # Send message
PUT    /messages/{id}                           # Update message
DELETE /messages/{id}                           # Delete message
GET    /messages/game/{gameId}                  # Get messages in game
GET    /messages/user/{userId}                  # Get user's sent messages
GET    /messages/between/{userId1}/{userId2}    # Get messages between two users
POST   /messages/{id}/read                      # Mark message as read

# Friends
POST   /friends                                 # Add friend
DELETE /friends/user/{userId}/user/{userId}     # Remove friend
GET    /friends                                 # Get all friend list
GET    /friends/user/{userId}                   # Get single friend list

# Game States
GET    /game-states                             # Get all game states
GET    /game-states/{game_state_id}             # Get game state by ID
POST   /game-states                             # Create game state
PUT    /game-states/{game_state_id}             # Update game state
DELETE /game-states/{game_state_id}             # Delete game state
GET    /game-states/game/{game_id}              # Get game states by game

```

**Key Features:**
- Full FEN parsing with castling and en-passant support
- Pseudo-legal move generation filtered by safety checks
- Check/Checkmate/Stalemate detection
- Draw by repetition and 50-move rule
- SAN notation generation with check/mate markers
- Elo rating system with default 1200
- Time control support (bullet, blitz, classical)
- JWT authentication with bcrypt passwords
- Real-time multiplayer via WebSocket STOMP
- Avatar upload to Cloudinary CDN
- Email notifications via SMTP

### Phase 2 — AI Chess Analysis

1. **Engine Directory** (`src/main/java/dandastino/chess/engine/`)
   - `StockfishEngine.java` - UCI protocol implementation for Stockfish communication
   - `EngineAnalysis.java` - Data class for engine analysis results
   - `GameAnalysisService.java` - Main orchestrator for all AI features
   - `GameAnalysisController.java` - REST API endpoints (6 endpoints total)
   - `Phase2Initializer.java` - Startup initialization component

2. **Move Analysis** (`src/main/java/dandastino/chess/moveAnalyses/`)
   - `MoveAnalysisEngineService.java` - Move quality analysis and blunder detection

3. **Opening Classification** (`src/main/java/dandastino/chess/openings/`)
   - `OpeningClassificationService.java` - ECO code matching and opening classification
   - Pre-loaded with 5 popular openings: Italian, Sicilian, French, Caro-Kann, Ruy Lopez

4. **Cheating Detection** (`src/main/java/dandastino/chess/cheatingAnalyses/`)
   - `CheatingDetectionService.java` - Suspicion scoring using 4-metric weighted algorithm

**REST API Endpoints:**
```bash
GET  /api/analysis/game/{gameId}        # Full game analysis
GET  /api/analysis/move/{moveId}        # Move details
GET  /api/analysis/insights/{gameId}    # Player insights only
POST /api/analysis/init                 # Initialize engine
POST /api/analysis/shutdown             # Shutdown engine
GET  /api/analysis/health               # Service health check

# Game Openings
GET    /game-openings                           # Get all game openings
GET    /game-openings/{game_opening_id}         # Get game opening by ID
POST   /game-openings                           # Create game opening
DELETE /game-openings/{game_opening_id}         # Delete game opening
GET    /game-openings/game/{game_id}            # Get game openings by game
GET    /game-openings/opening/{opening_id}      # Get game openings by opening

```

**Configuration (application.properties):**
```properties
stockfish.path=/usr/local/bin/stockfish  # macOS
# stockfish.path=/usr/games/stockfish    # Linux
# stockfish.path=C:\\Program Files\\Stockfish\\stockfish.exe   # Windows
```

**Installation:**
```bash
# macOS
brew install stockfish

# Linux
sudo apt-get install stockfish

# Windows 
# Download from https://stockfishchess.org/
```

**Move Analysis Service:**
- Store engine evaluation after every move in `MoveAnalysis` entity
- Compare player's move with Stockfish's best move
- Calculate centipawn loss (CPL) to detect blunders:
  - **Blunder**: CPL > 300
  - **Mistake**: CPL 150-300
  - **Inaccuracy**: CPL 50-150
  - **Good Move**: CPL < 50
- Automatically classifies moves into Review enum (Brilliant, Great, Best, Mistake, Miss, Blunder)

**Opening Classification:**
- Pre-loaded with 5 common openings: Italian Game (C50), Sicilian Defense (B20), French Defense (C00), Caro-Kann Defense (B10), Ruy Lopez (C60)
- Match first 4-10 moves against known opening patterns
- Store opening name and ECO code in `GameOpening` entity
- Tracks player's opening preferences and statistics
- Example: e4 e5 Nf3 Nc6 → Italian Game (C50)

**Anti-Cheating Detection:**
- Analyzes player behavior using a weighted scoring system combining 4 independent metrics
- Engine Accuracy (40% weight): Measures percentage of moves that match Stockfish's best moves. High accuracy across all game phases indicates possible engine assistance
- Timing Anomaly (25% weight): Detects unusual move timing patterns - moves played suspiciously fast in complex positions or slow in simple ones
- Skill Spike (20% weight): Identifies sudden rating improvements that deviate from expected learning curve. Rapid Elo gains may indicate account takeover or cheating
- Move Complexity (15% weight): Analyzes whether moves are consistently brilliant in objectively complex positions, which is rare for humans
- Produces suspicion scores from 0.0 (very low) to 1.0 (very high) with risk levels: Very Low, Low, Medium, High, Very High
- Generates detailed reports explaining which metrics triggered suspicion alerts

**Player Insights Generation:**
- Automatically analyzes completed games to extract actionable patterns
- Generates blunder and mistake counts per game with contextual information
- Creates personalized recommendations based on recurring patterns (e.g., blunders in the opening phase)
- Tracks player performance by opening, time control, and opponent rating
- Produces human-readable insights like "Your best opening is the Italian Game (C50)" or "You play too fast after move 20"
- Stores insights for historical tracking and trend analysis across multiple games

**Performance & Architecture:**
- Stockfish analysis runs at configurable depth (default 20 plies), which provides strong evaluation accuracy
- Analysis times vary: Depth 15 takes ~2 seconds, Depth 20 takes ~5 seconds, Depth 25 takes ~30+ seconds per position
- GameAnalysisService orchestrates all 4 analysis services (move analysis, opening classification, cheating detection, insights) in a coordinated pipeline
- Phase2Initializer component automatically initializes Stockfish engine on application startup
- If Stockfish is not installed, the system logs a warning but continues running - AI features gracefully degrade to unavailable rather than crashing the app
- All analysis results are cached in the database (MoveAnalysis, Opening, CheatingAnalysis tables) for quick retrieval on repeated game reviews

### Phase 3 — Frontend (Future)

---

## Database Design

The application uses **PostgreSQL** with a fully normalized, relational schema.

**UML Diagram:**
![Database Schema](/Image/drawSQL-image-export-2025-11-27.png)

**Key Tables:**
- **users** - Player accounts with Elo ratings
- **games** - Game records with players, FEN positions, results
- **moves** - Move history with SAN notation, FEN state, timing
- **game_states** - Position snapshots for replay
- **move_analyses** - Engine evaluation per move
- **game_openings** - Opening classification with ECO codes
- **cheating_analyses** - Anti-cheat detection scores
- **messages** - In-game chat
- **friends** - Friend relationships
- **user_settings** - Player preferences

**Key Features:**
- Automatic schema creation via Hibernate DDL
- ForeignKey constraints for referential integrity
- Indexed queries for performance
- Timestamp tracking (created_at, updated_at, finished_at)

---

## Third-Party Integrations

### WebSocket (STOMP) - Real-Time Updates [Built-in]
- **Purpose**: Enable real-time bidirectional communication between server and all connected players.
- **Type**: Built-in to Spring Framework (not a third-party service)
- **Protocol**: STOMP over WebSocket with SockJS fallback
- **Endpoint**: `ws://localhost:8080/ws/chess`

#### Features:
- **Real-time moves**: When a player makes a move, all connected players see it instantly
- **Game status updates**: Checkmate, stalemate, resignation, and draw events broadcast immediately
- **Player connection tracking**: Know when players join/leave a game
- **No database lag**: Direct server-to-client push notifications


#### WebSocket Topics:
| Topic | Purpose |
|-------|---------|
| `/topic/game/{gameId}/move` | Broadcast of moves in a game |
| `/topic/game/{gameId}/status` | Game status changes (finish, resign, draw) |
| `/topic/game/{gameId}/players` | Player connection/disconnection events |


#### Integration Points:
- **MoveService**: Automatically broadcasts moves when created
- **GameService**: Automatically broadcasts game finish events
- **GameBroadcastService**: Centralized broadcasting logic
- **GameWebSocketController**: Handles direct STOMP messages

---

### Email (SMTP) - Notifications [Optional]
    - Purpose: send confirmation emails, password reset links, notifications.
    - Endpoint: `POST /api/email/send` (see above). Implementation uses Spring `JavaMailSender` (`EmailService`).
    - How to test: set `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD` in `env.properties`, start the app, then:
        ```bash
        curl -X POST http://localhost:3001/api/email/send \
            -H "Content-Type: application/json" \
            -d '{"to":"you@example.com","subject":"Test","body":"Hello"}'
        ```
    - Error handling: SMTP failures produce `502 Bad Gateway` with a message. Validation errors produce `400`.

### Cloudinary - Image Hosting [Optional]
    - Purpose: offload hosting of uploaded images to a CDN-backed service, reduce storage and bandwidth on the app server, and get transformed/optimized image URLs.
    - How to test: set `CLOUDINARY_NAME`, `CLOUDINARY_KEY`, `CLOUDINARY_SEC` in `env.properties`, then use the avatar upload endpoint (Postman form-data or curl with `-F avatar=@path/to/file`).
    - Error handling: file validation errors produce `400`, oversized uploads `413`, and upload failures return `500` with a descriptive message.

### Stockfish - Chess AI [Optional]
    - **Type**: Local chess engine executable (not a cloud API service)
    - **Purpose**: Provides advanced chess analysis, move evaluation, and best move suggestions for Phase 2 AI features
    - **Protocol**: UCI (Universal Chess Interface) via stdin/stdout communication
    - **Configuration**: Set `stockfish.path=stockfish/stockfish.exe` in `application.properties`
    - **Performance**: Depth 15 (~2s), Depth 20 (~5s), Depth 25 (~30s) per position
    - **Error handling**: If Stockfish is not installed or path is incorrect, the system logs a warning and gracefully degrades - AI features become unavailable without crashing the app
    - **How to test**: After installation, start the app and call `GET /api/analysis/health` to verify engine status


## Security

### Authentication & Authorization
- **JWT (JSON Web Tokens)** - Stateless, secure token-based authentication
- **Bcrypt Password Hashing** - Passwords never stored in plain text
- **Spring Security** - Framework-level protection on all endpoints
- **CORS Protection** - Cross-origin requests validated
- **Role-Based Access Control** - User permissions enforced

### Data Protection
- **HTTPS Ready** - Deploy with SSL/TLS certificates
- **Database Encryption** - Sensitive data can be encrypted at rest
- **Input Validation** - All inputs validated with Jakarta constraints
- **SQL Injection Prevention** - Using parameterized queries (JPA)
- **WebSocket Security** - STOMP messages authenticated

---

## Performance & Scaling

**Optimization Features:**
- Database query optimization with proper indexing
- Lazy loading for relationships
- Caching of frequently accessed data
- WebSocket for efficient real-time communication (no polling)
- Connection pooling for database

**Monitoring:**
- Application logs: `logs/chess.log`
- Database query logs: Enable slow query log in PostgreSQL
- JVM metrics: Use tools like JConsole or VisualVM

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---
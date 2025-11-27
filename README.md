# ♟️Chess Game

A powerful **Java Spring Boot backend** for playing, analyzing, and monitoring chess games.  
Designed in multiple phases from **medium rule-based logic → advanced Stockfish-powered AI insights**, and ready to support a full frontend later.

![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Security-JWT-6DB33F?logo=springsecurity&logoColor=white)
![WebSockets](https://img.shields.io/badge/Protocol-WebSocket-010101?logo=socket.io&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-003B57?logo=postgresql&logoColor=white)
![Stockfish](https://img.shields.io/badge/Engine-Stockfish-000000)
![Elo Rating](https://img.shields.io/badge/Rating-Elo–System-FFD700)

---
## 🚀 Project Vision

This backend aims to solve real problems in chess platforms by providing:

- Reliable **move validation & game state handling**
- Smart **AI analysis using Stockfish**
- Automatic **opening classification (ECO codes)**
- Player behavior insights & **blunder detection**
- Optional **anti-cheating & suspicion scoring**
- Multiplayer support via **WebSockets**
- Friends & challenge system
- Game replay & PGN export

## 🧠 AI Integration Strategy

No need for Python unless you want more complex ML. This project integrates AI directly by:

### ✅ Using pure Java:
- Chess move validator (no engine, medium version)
- FEN generation and board tracking
- Rule-based opening classification
- Engine evaluation request handling

### ✅ Engine AI (Stockfish):
- Backend communicates using **UCI protocol**
- Sends `FEN string` → gets **evaluation score**, best move, depth
- Detects:
    - Inaccuracies (swing > 50cp)
    - Mistakes (swing > 150cp)
    - Blunders (swing > 300cp)
- Stores evaluation per move in PostgreSQL

---

## 📌 Feature Checklist per Phase

### Phase 1 — Backend Core (Medium Version)
- [x] User Registration & Login
- [x] Player profile & settings support
- [x] Elo Rating system (default 1200)
- [x] Create/Join chess games
- [x] Time controls (bullet, blitz, classical)
- [x] Move validation engine (pure Java)
- [x] Board state update (FEN storage)
- [x] Detect: Check, Mate, Stalemate, Repetition draws, Insufficient material
- [x] Store move history & PGN export
- [x] Real-time updates using Spring WebSockets

### Phase 2 — AI Chess Analysis (Advanced Version)
- [x] Stockfish engine integration (UCI)
- [x] FEN → evaluation score per move
- [x] Best move suggestions
- [x] Blunder/Mistake/Inaccuracy detection
- [x] Opening classification with ECO codes
- [ ] Anti-cheating suspicion score
- [ ] Insights like:
    - *"You blunder knights early"*
    - *"You play too fast after move 20"*
    - *"Your best opening is X"*

### Phase 3 — Frontend (Later Stage)
> Not part of this repository yet, backend is frontend-ready.

---

##  🏗 Database Design Overview (PostgreSQL)

UML Diagram fully relational, normalized, and scalable:

![UML Diagram](/Image/drawSQL-image-export-2025-11-27.png)

---

## 🔐 Security & Performance
- Passwords stored with **bcrypt hash**
- Login secured with **JWT**
- WebSocket channels protected via **Spring Security**
- Pagination and optimized queries in **PostgreSQL**

---


# EduQuest

EduQuest is a web-based learning platform for school students. It combines lessons, quizzes, interactive learning games, progress tracking, and gamification. The project also includes offline learning support so cached learning content and locally recorded progress can be used when the network is unavailable and synchronized later.



## Features

- Role-aware experiences for students, teachers, parents, and super administrators.
- Teacher tools for managing learning modules and activities, classroom challenges, and classroom progress analytics.
- Student learning through lessons, quizzes, and interactive games.
- Progress tracking, XP, badges, streaks, missions, and leaderboards.
- Parent views for child progress and activity analytics.
- AI Tutor chat with explain, simplify, hint, practice, and chat modes, lesson context, and language selection. Gemini access is configured on the backend.
- Offline data storage with Dexie/IndexedDB, repositories, network detection, and synchronization services.
- Progressive Web App configuration with an installable manifest, service worker, asset caching, and background sync support.

## Architecture

```text
Browser (React + TypeScript + Vite)
  ├── Role-aware pages and learning UI
  ├── Dexie / IndexedDB offline stores and sync services
  ├── PWA service worker
  └── /api requests through the Vite development proxy
             │
             ▼
Spring Boot REST API (Java 17, port 8081)
  ├── Authentication and role-protected controllers
  ├── Learning, progress, gamification, analytics, and AI services
  └── Spring Data JPA
             │
             ▼
        PostgreSQL
```

## Technology stack

| Area | Technologies |
| --- | --- |
| Frontend | React 18, TypeScript, Vite 5, Tailwind CSS, React Router, Axios |
| Offline and PWA | Dexie 4, IndexedDB, `vite-plugin-pwa`, Workbox |
| Backend | Java 17, Spring Boot 3.2.5, Spring Web, Spring Security, Spring Data JPA, Bean Validation |
| Database | PostgreSQL |
| Authentication | JWT (`jjwt` 0.11.5) |
| AI | Gemini API through a backend service; key supplied through `GEMINI_API_KEY` |

## Repository layout

```text
backend/      Spring Boot API, persistence, security, and services
frontend/     React web application, PWA, and offline learning code
docs/         Project diagrams and documentation assets
```

## Prerequisites

- Java 17
- Maven
- Node.js and npm
- PostgreSQL

Create a local PostgreSQL database named `eduquest_v2` and configure connection details securely for your environment. The checked-in backend configuration currently uses `127.0.0.1:5432`; review its credentials and JWT secret before running or publishing the project.

AI Tutor requests require a valid Gemini API key on the backend. Set it in the backend process environment; never place it in frontend code or a `VITE_` variable.

PowerShell example for the current terminal session:

```powershell
$env:GEMINI_API_KEY = "your-gemini-api-key"
```

## Run locally

### 1. Start the backend

In a terminal:

```powershell
cd backend
mvn spring-boot:run
```

The backend is configured for port `8081`. The database must be running and configured before startup.

### 2. Start the frontend

In a second terminal:

```powershell
cd frontend
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000). During Vite development, `/api` requests are proxied to `http://localhost:8081`.

## Build and checks

Run from the relevant directory:

```powershell
# Backend tests
cd backend
mvn test

# Frontend type-check and production build
cd ..\frontend
npm run build

# Frontend lint
npm run lint
```

These are the repository's available commands; running them is separate from following this setup guide.

## Offline and PWA behavior

The frontend defines IndexedDB stores and offline repositories under `frontend/src/offline`. The Vite PWA setup is in `frontend/vite.config.ts`; it configures the web app manifest, service worker generation, static asset caching, learning-content runtime caching, and a background sync queue for student sync requests.

Offline availability depends on content being cached on the device before the connection is lost. Gemini-powered AI responses require internet access and a valid backend API key.

## Configuration and security

- Keep database passwords, JWT signing keys, and provider API keys out of source control.
- Configure secrets in the backend environment or a local configuration file excluded by `.gitignore`.
- Do not expose Gemini credentials through frontend environment variables or browser requests.
- Use HTTPS and replace development credentials and secrets before any shared or production deployment.

## Documentation

See `EDUQUEST_PROJECT_DOCUMENTATION.md` and the `docs/` directory for additional project documentation and diagrams.

# ⚡ FitTracker Pro - Daily Activity & Fitness Tracking System

A clean, beginner-friendly **Java & Spring Boot** application where users can log daily activities (workouts, calories burned, duration), set fitness goals, view comprehensive progress reports, and track performance improvements over time.

---

## 🌟 Key Features

1. **User Profiles**: Manage users with personalized daily calorie targets.
2. **Activity & Workout Logging**:
   - Log workouts across types: `RUNNING`, `CYCLING`, `SWIMMING`, `WEIGHTLIFTING`, `WALKING`, `YOGA`, `HIIT`, etc.
   - Record duration (minutes), calories burned (kcal), distance (km), and custom notes.
3. **Goal Setting & Automatic Achievement Tracking**:
   - Set targets for **Calories Burned**, **Workout Minutes**, or **Workout Count** with deadlines.
   - Live progress bars automatically update whenever an activity is logged.
   - Goals automatically transition to `ACHIEVED` when targets are reached.
4. **Progress Reports**:
   - **Weekly (7 days)** and **Monthly (30 days)** aggregations.
   - Total workouts, total active minutes, total calories burned.
   - Daily breakdown table showing day-by-day activity summaries.
5. **Improvement Tracking Over Time**:
   - Compares the **Current 7-Day Window** against the **Previous 7-Day Window**.
   - Calculates percentage increases/decreases (`+33.3% workouts`, `+68.1% calories burned`).
   - Evaluates performance status (`EXCELLENT_PROGRESS`, `STEADY_PROGRESS`, `NEEDS_BOOST`).
   - Generates personalized motivational coaching feedback.
6. **Interactive Pie & Doughnut Charts (Chart.js)**:
   - **Calories Burned by Activity Type** (Pie Chart showing percentage and calories per workout category).
   - **Workout Sessions by Activity Type** (Doughnut Chart displaying distribution across running, cycling, swimming, yoga, etc.).
7. **Zero-Setup In-Memory Database (H2)**:
   - Starts instantly with seeded demo data.
   - Includes interactive web console at `/h2-console`.
7. **Interactive Web Dashboard & Swagger UI**:
   - Clean, modern dashboard served at `http://localhost:8080/`.
   - Full OpenAPI / Swagger documentation at `http://localhost:8080/swagger-ui.html`.

---

## 🏛️ Architecture & Easy Spring Boot Concepts

This project follows the industry-standard **Layered Architecture (Controller-Service-Repository-Entity)**:

```
┌─────────────────────────────────────────────────────────────┐
│                       Client Layer                          │
│          Web Dashboard (HTML/JS)  /  REST Clients / Curl    │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTP JSON Requests
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                 Controller Layer (@RestController)          │
│   UserController  •  ActivityController  •  ReportController│
│   - Receives HTTP requests (GET, POST, DELETE)              │
│   - Validates input (@Valid)                                │
│   - Returns JSON responses (ResponseEntity)                 │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                   Service Layer (@Service)                  │
│     UserService  •  ActivityService  •  ReportService       │
│   - Contains business logic and calculations                │
│   - Improvement percentage and trend comparisons            │
│   - Goal progress recalculation                             │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                 Repository Layer (@Repository)              │
│   UserRepository  •  ActivityRepository  •  GoalRepository  │
│   - Spring Data JPA interfaces (extends JpaRepository)      │
│   - Auto-generates SQL queries without writing boilerplate  │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                  Database / Entity Layer                    │
│   User (@Entity)  •  Activity (@Entity)  •  Goal (@Entity)  │
│   - H2 In-Memory Database                                   │
└─────────────────────────────────────────────────────────────┘
```

### Essential Spring Boot Annotations Demystified:

| Annotation | Where it's used | What it does in simple terms |
| :--- | :--- | :--- |
| `@SpringBootApplication` | `ActivityTrackerApplication.java` | The main entry point. Automatically configures beans and starts the embedded Tomcat server. |
| `@Entity` & `@Table` | `User`, `Activity`, `Goal` | Tells JPA/Hibernate to map this Java class directly to a database table. |
| `@Id` & `@GeneratedValue` | Entity Primary Keys | Tells the database to automatically generate incrementing unique IDs (1, 2, 3...). |
| `@Repository` | Interfaces extending `JpaRepository` | Spring automatically provides `save()`, `findAll()`, `findById()`, and `deleteById()`. |
| `@Service` | Business logic classes | Marks classes where fitness calculations, comparisons, and business rules live. |
| `@RestController` | HTTP endpoint handlers | Marks the class as a web controller where every method returns JSON data directly. |
| `@GetMapping` / `@PostMapping` | Controller methods | Maps HTTP requests (`GET /api/reports/weekly/1`, `POST /api/activities`) to Java methods. |
| `@Valid` | Method arguments in controllers | Automatically validates rules like `@NotNull`, `@Min(1)`, and `@Email`. |

---

## 🚀 Getting Started

### Prerequisites
- **Java 17+** (Java 17, 19, 21 are supported)
- **Maven 3.8+**

### 1. Run the Application with Maven
In your terminal, simply run:

```bash
mvn spring-boot:run
```

The application will automatically compile and start on **port 8080**.

### 2. Access the Applications in Your Browser

| Tool | URL | Description |
| :--- | :--- | :--- |
| **Interactive Dashboard** | [http://localhost:8080/](http://localhost:8080/) | Visual tracker, log workouts, set goals, track improvement |
| **Swagger UI (OpenAPI)** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Interactive API tester and documentation |
| **H2 Database Console** | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) | Inspect DB tables (`JDBC URL: jdbc:h2:mem:fitnessdb`, `User: sa`, `Password: <blank>`) |

---

## 📡 REST API Documentation

### 1. Users (`/api/users`)
- `POST /api/users` - Create user
- `GET /api/users` - List all users
- `GET /api/users/{id}` - Get user by ID

### 2. Activities (`/api/activities`)
- `POST /api/activities` - Log a workout:
  ```json
  {
    "userId": 1,
    "activityType": "RUNNING",
    "durationMinutes": 35,
    "caloriesBurned": 360,
    "distanceKm": 5.2,
    "activityDate": "2026-10-30",
    "notes": "Morning jog in the park"
  }
  ```
- `GET /api/activities/user/{userId}` - Get all workouts for a user (newest first)
- `GET /api/activities/user/{userId}/recent?days=7` - Get workouts from the last N days
- `DELETE /api/activities/{id}` - Delete an activity (and update goals automatically)

### 3. Goals (`/api/goals`)
- `POST /api/goals` - Set a goal:
  ```json
  {
    "userId": 1,
    "title": "Burn 2000 kcal this week",
    "goalType": "CALORIES_BURNED",
    "targetValue": 2000,
    "startDate": "2026-10-24",
    "endDate": "2026-10-31"
  }
  ```
- `GET /api/goals/user/{userId}` - List user's goals with live percentage completion

### 4. Progress Reports & Improvement Tracker (`/api/reports`)
- `GET /api/reports/weekly/{userId}` - 7-day progress report with daily breakdown
- `GET /api/reports/monthly/{userId}` - 30-day progress report
- `GET /api/reports/improvements/{userId}` - Current week vs previous week improvement comparison:
  ```json
  {
    "currentWorkouts": 4,
    "previousWorkouts": 3,
    "workoutChange": 1,
    "workoutImprovementPct": 33.3,
    "currentCalories": 1580,
    "previousCalories": 940,
    "calorieChange": 640,
    "calorieImprovementPct": 68.1,
    "improvementStatus": "EXCELLENT_PROGRESS",
    "motivationalMessage": "🎉 Outstanding progress! You burned 640 more calories (+68.1%) and logged 1 more workout session(s) compared to the previous week. Keep pushing!"
  }
  ```


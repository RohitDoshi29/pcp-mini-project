-- ===================================================================
-- [PRESENTATION POINT] RELATIONAL DATABASE SCHEMA: schema.sql
-- ===================================================================
-- - ROLE: Relational Data Definition Language (DDL) specifying tables and relationships.
-- - KEY RELATIONAL CONCEPTS:
--   1. PRIMARY KEY: Unique identifier for each row (id BIGINT AUTO_INCREMENT).
--   2. FOREIGN KEY: Relational link between child tables (activities, goals) and parent table (users).
--   3. ON DELETE CASCADE: If a user account is deleted, all their workouts and goals are deleted automatically.
--   4. UNIQUE CONSTRAINT: Ensures no two users can register with the same email.
-- ===================================================================

-- 1. USERS TABLE: Holds user profiles and daily targets
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    daily_calorie_target INT DEFAULT 500,
    created_at TIMESTAMP NOT NULL
);

-- 2. ACTIVITIES TABLE: Stores individual workout events
CREATE TABLE IF NOT EXISTS activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    duration_minutes INT NOT NULL,
    calories_burned INT NOT NULL,
    distance_km DOUBLE,
    activity_date DATE NOT NULL,
    notes VARCHAR(255),
    logged_at TIMESTAMP NOT NULL,
    -- Foreign Key: Links each activity to a specific user in users(id)
    CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. GOALS TABLE: Stores fitness milestones and current progress
CREATE TABLE IF NOT EXISTS goals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    goal_type VARCHAR(50) NOT NULL,
    target_value DOUBLE NOT NULL,
    current_value DOUBLE DEFAULT 0.0,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    -- Foreign Key: Links each goal to a specific user in users(id)
    CONSTRAINT fk_goal_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);



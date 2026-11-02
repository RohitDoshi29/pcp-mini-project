package com.fitness.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ===================================================================
 * [PRESENTATION POINT] MODEL / ENTITY LAYER: User
 * ===================================================================
 * - ROLE: Represents the `users` table in the SQL database.
 * - CONCEPTS:
 *   1. `@Entity`: Tells JPA/Hibernate to map this Java object to a relational SQL table.
 *   2. `@Table(name = "users")`: Explicitly sets the SQL table name to "users".
 *   3. `@Id` & `@GeneratedValue`: Designates `id` as the primary key with AUTO_INCREMENT.
 *   4. `@Column`: Sets column constraints such as `nullable = false` (NOT NULL) and `unique = true`.
 *   5. Default values: Initializes sensible defaults (e.g. daily target of 500 kcal, creation timestamp).
 * ===================================================================
 */
@Entity
@Table(name = "users")
public class User {

    // Primary Key: Auto-incremented unique ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User's full name (Cannot be NULL)
    @Column(nullable = false)
    private String name;

    // Unique email address for login/identification (Cannot be NULL, duplicates forbidden)
    @Column(nullable = false, unique = true)
    private String email;

    // Daily calorie goal in kcal (default: 500)
    private Integer dailyCalorieTarget = 500;

    // Timestamp when the user account was registered
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default no-argument constructor (required by Hibernate/JPA to instantiate objects)
    public User() {}

    // Parameterized constructor for easy creation
    public User(String name, String email, Integer dailyCalorieTarget) {
        this.name = name;
        this.email = email;
        this.dailyCalorieTarget = dailyCalorieTarget != null ? dailyCalorieTarget : 500;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters for Java encapsulation
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getDailyCalorieTarget() { return dailyCalorieTarget; }
    public void setDailyCalorieTarget(Integer dailyCalorieTarget) { this.dailyCalorieTarget = dailyCalorieTarget; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}


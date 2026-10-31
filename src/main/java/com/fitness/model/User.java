package com.fitness.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Entity representing a registered user in the fitness system.
 * 
 * Spring Boot Concept:
 * - @Entity: Marks this class as a JPA database entity (maps to 'users' table).
 * - @Table: Specifies the table name in the database.
 * - @Id: Specifies the primary key of this entity.
 * - @GeneratedValue: Tells Hibernate to automatically increment the ID.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    // Optional daily calorie burn target (default: 500 kcal)
    private Integer dailyCalorieTarget = 500;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default constructor needed by JPA
    public User() {
    }

    public User(String name, String email, Integer dailyCalorieTarget) {
        this.name = name;
        this.email = email;
        this.dailyCalorieTarget = dailyCalorieTarget != null ? dailyCalorieTarget : 500;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDailyCalorieTarget() {
        return dailyCalorieTarget;
    }

    public void setDailyCalorieTarget(Integer dailyCalorieTarget) {
        this.dailyCalorieTarget = dailyCalorieTarget;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}


package com.fitness.model;

/**
 * ===================================================================
 * [PRESENTATION POINT] ENUM: ActivityType
 * ===================================================================
 * - ROLE: Defines a fixed, type-safe list of supported workout activities.
 * - CONCEPTS:
 *   1. Type Safety: Prevents spelling mistakes (e.g. "Runing" vs "RUNNING").
 *   2. Database mapping: Stored as VARCHAR text strings in SQL (`@Enumerated(EnumType.STRING)`).
 *   3. Extensibility: Easy to add new activity types in the future without changing database schemas.
 * ===================================================================
 */
public enum ActivityType {
    RUNNING,        // Running / Jogging
    CYCLING,        // Bicycle ride / Spin class
    SWIMMING,       // Pool / Open water swim
    WEIGHTLIFTING,  // Gym strength training
    WALKING,        // Daily steps / Outdoor walking
    YOGA,           // Flexibility / Mindfulness
    HIIT,           // High Intensity Interval Training
    OTHER           // General workouts / Sports
}



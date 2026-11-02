package com.fitness.dto;

import com.fitness.model.ActivityType;
import java.time.LocalDate;

/**
 * ===================================================================
 * [PRESENTATION POINT] DTO LAYER: ActivityRequest
 * ===================================================================
 * - ROLE: Data Transfer Object (DTO) carrying workout input data from the frontend form to the backend.
 * - CONCEPTS:
 *   1. What is a DTO?: A plain Java object used to carry data between client and server.
 *   2. Why use DTOs?: Decouples API payloads from database tables and allows clean JSON mapping.
 *   3. Jackson Deserialization: Spring automatically converts JSON sent in HTTP POST `@RequestBody`
 *      into this Java object.
 * ===================================================================
 */
public class ActivityRequest {
    public Long userId;               // ID of the user logging this activity
    public ActivityType activityType; // RUNNING, CYCLING, YOGA, etc.
    public Integer durationMinutes;   // Exercise duration in minutes
    public Integer caloriesBurned;    // Estimated or measured calories burned
    public Double distanceKm;         // Optional distance in km (can be null for yoga/weightlifting)
    public LocalDate activityDate;    // Date when workout took place
    public String notes;              // Optional user remarks or notes
}


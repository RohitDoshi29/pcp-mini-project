package com.fitness.dto;

import com.fitness.model.GoalType;
import java.time.LocalDate;

/**
 * ===================================================================
 * [PRESENTATION POINT] DTO LAYER: GoalRequest
 * ===================================================================
 * - ROLE: Data Transfer Object (DTO) capturing goal creation parameters from the frontend modal.
 * - CONCEPTS:
 *   1. Encapsulates all fields required to instantiate a new `Goal` entity.
 *   2. Prevents clients from tampering with calculated fields like `currentValue` or `status`.
 * ===================================================================
 */
public class GoalRequest {
    public Long userId;         // ID of the target user
    public String title;        // Human-readable title (e.g. "Burn 2000 kcal")
    public GoalType goalType;   // CALORIES_BURNED, WORKOUT_MINUTES, or WORKOUT_COUNT
    public Double targetValue;  // Numeric target value to achieve
    public LocalDate startDate; // Start date of target window
    public LocalDate endDate;   // Deadline date
}


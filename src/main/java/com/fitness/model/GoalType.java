package com.fitness.model;

/**
 * ===================================================================
 * [PRESENTATION POINT] ENUM: GoalType
 * ===================================================================
 * - ROLE: Defines what metric a user is trying to reach in a fitness goal.
 * - METRIC TYPES:
 *   1. CALORIES_BURNED: Aggregates total calories burned (e.g. "Burn 3,000 kcal this week").
 *   2. WORKOUT_MINUTES: Aggregates total exercise minutes (e.g. "Exercise for 180 minutes").
 *   3. WORKOUT_COUNT: Counts total number of workout sessions (e.g. "Complete 5 workouts").
 * ===================================================================
 */
public enum GoalType {
    CALORIES_BURNED,    // e.g., Target 3000 kcal burned
    WORKOUT_MINUTES,    // e.g., Target 200 minutes of exercise
    WORKOUT_COUNT       // e.g., Target 5 completed workouts
}



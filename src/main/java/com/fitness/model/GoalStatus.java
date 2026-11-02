package com.fitness.model;

/**
 * ===================================================================
 * [PRESENTATION POINT] ENUM: GoalStatus
 * ===================================================================
 * - ROLE: Tracks the lifecycle state of a fitness goal.
 * - LIFECYCLE STATES:
 *   1. IN_PROGRESS: Default state when a goal is created; still open for progress updates.
 *   2. ACHIEVED: Automatically set by `GoalService` when `currentValue >= targetValue`.
 *   3. EXPIRED: Target deadline passed (`endDate < today`) without reaching target value.
 * ===================================================================
 */
public enum GoalStatus {
    IN_PROGRESS,    // Goal is active and currently being tracked
    ACHIEVED,       // Target was met or exceeded before the deadline
    EXPIRED         // Deadline passed without meeting target
}



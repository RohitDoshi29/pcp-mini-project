package com.fitness.service;

import com.fitness.dto.GoalRequest;
import com.fitness.model.Goal;
import com.fitness.model.GoalStatus;
import com.fitness.model.GoalType;
import com.fitness.model.User;
import com.fitness.repository.ActivityRepository;
import com.fitness.repository.GoalRepository;
import com.fitness.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * ===================================================================
 * [PRESENTATION POINT] SERVICE LAYER: GoalService
 * ===================================================================
 * - ROLE: The automated Goal Evaluation Engine.
 * - CONCEPTS:
 *   1. Automatic Status Transition: Switches goals to ACHIEVED or EXPIRED.
 *   2. Dynamic Progress Calculation: Uses Native SQL queries to aggregate workouts
 *      within the goal's exact start and end dates.
 * ===================================================================
 */
@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    // Constructor Injection
    public GoalService(GoalRepository goalRepository, UserRepository userRepository, ActivityRepository activityRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * [FUNCTION] createGoal(GoalRequest request)
     * - PURPOSE: Creates and initializes a new fitness goal.
     * - STEP-BY-STEP CODE FLOW:
     *   STEP 1: Verify the user exists.
     *   STEP 2: Construct Goal object with title, target value, and dates.
     *   STEP 3: Save goal to database.
     *   STEP 4: Run recalculateGoalProgress() immediately in case workouts already exist in this date window.
     */
    public Goal createGoal(GoalRequest request) {
        // STEP 1: Verify user
        User user = userRepository.findById(request.userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // STEP 2: Create Goal entity
        Goal goal = new Goal(
                user,
                request.title,
                request.goalType,
                request.targetValue,
                request.startDate,
                request.endDate
        );

        // STEP 3: Initial save
        Goal saved = goalRepository.save(goal);

        // STEP 4: Calculate progress against existing workouts
        recalculateGoalProgress(saved);
        return goalRepository.save(saved);
    }

    /**
     * [FUNCTION] getUserGoals(Long userId)
     * - PURPOSE: Returns all goals for a user with up-to-date progress.
     * - FLOW: Recalculates all goals before returning to guarantee 100% accurate progress bars.
     */
    public List<Goal> getUserGoals(Long userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);
        for (Goal goal : goals) {
            recalculateGoalProgress(goal);
        }
        return goalRepository.saveAll(goals);
    }

    /**
     * [FUNCTION] recalculateGoalProgress(Goal goal)
     * - PURPOSE: The core automated logic determining if a goal is completed.
     * - STEP-BY-STEP CODE FLOW:
     *   STEP 1: Extract userId, startDate, and endDate.
     *   STEP 2: Run targeted SQL queries based on GoalType:
     *           - CALORIES_BURNED: calls sumCaloriesBurnedBetween()
     *           - WORKOUT_MINUTES: calls sumDurationMinutesBetween()
     *           - WORKOUT_COUNT:   calls countWorkoutsBetween()
     *   STEP 3: Set currentValue = calculated progress.
     *   STEP 4: AUTOMATIC STATUS CHECK:
     *           - If progress >= targetValue: set status = ACHIEVED!
     *           - Else if today is past endDate: set status = EXPIRED.
     *           - Otherwise: set status = IN_PROGRESS.
     */
    public void recalculateGoalProgress(Goal goal) {
        Long userId = goal.getUser().getId();
        LocalDate start = goal.getStartDate();
        LocalDate end = goal.getEndDate();

        double progress = 0.0;

        // STEP 2: Query database based on goal type
        if (goal.getGoalType() == GoalType.CALORIES_BURNED) {
            Integer total = activityRepository.sumCaloriesBurnedBetween(userId, start, end);
            progress = total != null ? total.doubleValue() : 0.0;
        } else if (goal.getGoalType() == GoalType.WORKOUT_MINUTES) {
            Integer total = activityRepository.sumDurationMinutesBetween(userId, start, end);
            progress = total != null ? total.doubleValue() : 0.0;
        } else if (goal.getGoalType() == GoalType.WORKOUT_COUNT) {
            Long count = activityRepository.countWorkoutsBetween(userId, start, end);
            progress = count != null ? count.doubleValue() : 0.0;
        }

        // STEP 3: Update current progress value
        goal.setCurrentValue(progress);

        // STEP 4: Automated status transition logic
        if (progress >= goal.getTargetValue()) {
            goal.setStatus(GoalStatus.ACHIEVED);
        } else if (LocalDate.now().isAfter(goal.getEndDate())) {
            goal.setStatus(GoalStatus.EXPIRED);
        } else {
            goal.setStatus(GoalStatus.IN_PROGRESS);
        }
    }

    /**
     * [FUNCTION] updateGoalStatus(Long goalId, GoalStatus status)
     * - PURPOSE: Manually updates a goal's status.
     */
    public Goal updateGoalStatus(Long goalId, GoalStatus status) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));
        goal.setStatus(status);
        return goalRepository.save(goal);
    }

    /**
     * [FUNCTION] refreshGoalsForUser(Long userId)
     * - PURPOSE: Helper invoked whenever a workout is added or deleted.
     * - FLOW: Loops through user goals and updates progress values.
     */
    public void refreshGoalsForUser(Long userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);
        for (Goal goal : goals) {
            recalculateGoalProgress(goal);
        }
        goalRepository.saveAll(goals);
    }
}

package com.fitness.service;

import com.fitness.dto.GoalRequest;
import com.fitness.exception.ResourceNotFoundException;
import com.fitness.model.Goal;
import com.fitness.model.GoalStatus;
import com.fitness.model.GoalType;
import com.fitness.model.User;
import com.fitness.repository.ActivityRepository;
import com.fitness.repository.GoalRepository;
import com.fitness.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class handling fitness goals and tracking achievement.
 */
@Service
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    public GoalService(GoalRepository goalRepository, UserRepository userRepository, ActivityRepository activityRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * Create a new goal for a user.
     */
    public Goal createGoal(GoalRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Goal end date cannot be before start date.");
        }

        Goal goal = new Goal(
                user,
                request.getTitle(),
                request.getGoalType(),
                request.getTargetValue(),
                request.getStartDate(),
                request.getEndDate()
        );

        Goal savedGoal = goalRepository.save(goal);
        recalculateGoalProgress(savedGoal);
        return goalRepository.save(savedGoal);
    }

    /**
     * Get all goals for a user and refresh their live progress against activities.
     */
    public List<Goal> getUserGoals(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        List<Goal> goals = goalRepository.findByUserId(userId);
        for (Goal goal : goals) {
            recalculateGoalProgress(goal);
        }
        return goalRepository.saveAll(goals);
    }

    /**
     * Update progress of a single goal based on activities in its date range.
     */
    public void recalculateGoalProgress(Goal goal) {
        Long userId = goal.getUser().getId();
        LocalDate start = goal.getStartDate();
        LocalDate end = goal.getEndDate();

        double progress = 0.0;
        if (goal.getGoalType() == GoalType.CALORIES_BURNED) {
            Integer totalCalories = activityRepository.sumCaloriesBurnedBetween(userId, start, end);
            progress = totalCalories != null ? totalCalories.doubleValue() : 0.0;
        } else if (goal.getGoalType() == GoalType.WORKOUT_MINUTES) {
            Integer totalMinutes = activityRepository.sumDurationMinutesBetween(userId, start, end);
            progress = totalMinutes != null ? totalMinutes.doubleValue() : 0.0;
        } else if (goal.getGoalType() == GoalType.WORKOUT_COUNT) {
            Long count = activityRepository.countWorkoutsBetween(userId, start, end);
            progress = count != null ? count.doubleValue() : 0.0;
        }

        goal.setCurrentValue(progress);

        // Update status automatically
        if (progress >= goal.getTargetValue()) {
            goal.setStatus(GoalStatus.ACHIEVED);
        } else if (LocalDate.now().isAfter(goal.getEndDate())) {
            goal.setStatus(GoalStatus.EXPIRED);
        } else {
            goal.setStatus(GoalStatus.IN_PROGRESS);
        }
    }

    /**
     * Manually change a goal's status if needed.
     */
    public Goal updateGoalStatus(Long goalId, GoalStatus status) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with ID: " + goalId));
        goal.setStatus(status);
        return goalRepository.save(goal);
    }

    /**
     * Refresh all goals of a user when an activity is logged or removed.
     */
    public void refreshGoalsForUser(Long userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);
        for (Goal goal : goals) {
            recalculateGoalProgress(goal);
        }
        goalRepository.saveAll(goals);
    }
}


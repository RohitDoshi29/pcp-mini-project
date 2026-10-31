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

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    public GoalService(GoalRepository goalRepository, UserRepository userRepository, ActivityRepository activityRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    public Goal createGoal(GoalRequest request) {
        User user = userRepository.findById(request.userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Goal goal = new Goal(
                user,
                request.title,
                request.goalType,
                request.targetValue,
                request.startDate,
                request.endDate
        );

        Goal saved = goalRepository.save(goal);
        recalculateGoalProgress(saved);
        return goalRepository.save(saved);
    }

    public List<Goal> getUserGoals(Long userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);
        for (Goal goal : goals) {
            recalculateGoalProgress(goal);
        }
        return goalRepository.saveAll(goals);
    }

    public void recalculateGoalProgress(Goal goal) {
        Long userId = goal.getUser().getId();
        LocalDate start = goal.getStartDate();
        LocalDate end = goal.getEndDate();

        double progress = 0.0;
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

        goal.setCurrentValue(progress);

        if (progress >= goal.getTargetValue()) {
            goal.setStatus(GoalStatus.ACHIEVED);
        } else if (LocalDate.now().isAfter(goal.getEndDate())) {
            goal.setStatus(GoalStatus.EXPIRED);
        } else {
            goal.setStatus(GoalStatus.IN_PROGRESS);
        }
    }

    public Goal updateGoalStatus(Long goalId, GoalStatus status) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));
        goal.setStatus(status);
        return goalRepository.save(goal);
    }

    public void refreshGoalsForUser(Long userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);
        for (Goal goal : goals) {
            recalculateGoalProgress(goal);
        }
        goalRepository.saveAll(goals);
    }
}

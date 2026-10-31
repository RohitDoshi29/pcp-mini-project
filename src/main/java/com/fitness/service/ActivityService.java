package com.fitness.service;

import com.fitness.dto.ActivityRequest;
import com.fitness.model.Activity;
import com.fitness.model.User;
import com.fitness.repository.ActivityRepository;
import com.fitness.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final GoalService goalService;

    public ActivityService(ActivityRepository activityRepository, UserRepository userRepository, GoalService goalService) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.goalService = goalService;
    }

    public Activity logActivity(ActivityRequest request) {
        User user = userRepository.findById(request.userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        LocalDate date = request.activityDate != null ? request.activityDate : LocalDate.now();

        Activity activity = new Activity(
                user,
                request.activityType,
                request.durationMinutes,
                request.caloriesBurned,
                request.distanceKm,
                date,
                request.notes
        );

        Activity saved = activityRepository.save(activity);
        goalService.refreshGoalsForUser(user.getId());
        return saved;
    }

    public List<Activity> getUserActivities(Long userId) {
        return activityRepository.findByUserIdOrderByActivityDateDescLoggedAtDesc(userId);
    }

    public List<Activity> getRecentActivities(Long userId, int days) {
        LocalDate start = LocalDate.now().minusDays(days);
        return activityRepository.findByUserIdAndActivityDateBetweenOrderByActivityDateAsc(userId, start, LocalDate.now());
    }

    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));
        Long userId = activity.getUser().getId();
        activityRepository.delete(activity);
        goalService.refreshGoalsForUser(userId);
    }
}

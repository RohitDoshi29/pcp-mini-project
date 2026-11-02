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

/**
 * ===================================================================
 * [PRESENTATION POINT] SERVICE LAYER: ActivityService
 * ===================================================================
 * - ROLE: The "Chef" managing workout logging and coordinating goal updates.
 * - CONCEPTS:
 *   1. @Service: Registers this class as a Spring Bean holding business logic.
 *   2. Cross-Service Communication: Calls GoalService so that logging a workout
 *      immediately triggers goal progress recalculation.
 * ===================================================================
 */
@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final GoalService goalService;

    // Constructor Injection: Spring injects required repository and service beans
    public ActivityService(ActivityRepository activityRepository, UserRepository userRepository, GoalService goalService) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.goalService = goalService;
    }

    /**
     * [FUNCTION] logActivity(ActivityRequest request)
     * - PURPOSE: Saves a workout and updates user goals.
     * - STEP-BY-STEP CODE FLOW:
     *   STEP 1: Check if the user exists in database; if not, throw HTTP 404.
     *   STEP 2: Use provided activity date, or default to today (LocalDate.now()).
     *   STEP 3: Assemble the Activity entity object.
     *   STEP 4: Save to database using Spring Data JPA (activityRepository.save).
     *   STEP 5: Trigger goalService.refreshGoalsForUser() to recalculate goal progress.
     */
    public Activity logActivity(ActivityRequest request) {
        // STEP 1: Look up user by ID
        User user = userRepository.findById(request.userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // STEP 2: Default to today's date if date was omitted
        LocalDate date = request.activityDate != null ? request.activityDate : LocalDate.now();

        // STEP 3: Create Activity entity
        Activity activity = new Activity(
                user,
                request.activityType,
                request.durationMinutes,
                request.caloriesBurned,
                request.distanceKm,
                date,
                request.notes
        );

        // STEP 4: Save workout to SQL database
        Activity saved = activityRepository.save(activity);

        // STEP 5: Automatically recalculate user's goals (turns goal to ACHIEVED if target met)
        goalService.refreshGoalsForUser(user.getId());

        return saved;
    }

    /**
     * [FUNCTION] getUserActivities(Long userId)
     * - PURPOSE: Fetches full workout history for a user, newest sessions first.
     * - FLOW: Calls Native SQL query in ActivityRepository.
     */
    public List<Activity> getUserActivities(Long userId) {
        return activityRepository.findByUserIdOrderByActivityDateDescLoggedAtDesc(userId);
    }

    /**
     * [FUNCTION] getRecentActivities(Long userId, int days)
     * - PURPOSE: Returns activities within the past N days.
     * - FLOW: Computes startDate = now() - days, and runs SQL date-range filter.
     */
    public List<Activity> getRecentActivities(Long userId, int days) {
        LocalDate start = LocalDate.now().minusDays(days);
        return activityRepository.findByUserIdAndActivityDateBetweenOrderByActivityDateAsc(userId, start, LocalDate.now());
    }

    /**
     * [FUNCTION] deleteActivity(Long id)
     * - PURPOSE: Removes a workout and adjusts goals.
     * - STEP-BY-STEP CODE FLOW:
     *   STEP 1: Find activity by ID.
     *   STEP 2: Delete from SQL database.
     *   STEP 3: Refresh user goals so progress bars decrease to reflect deletion.
     */
    public void deleteActivity(Long id) {
        // STEP 1: Verify activity exists
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));
        
        Long userId = activity.getUser().getId();

        // STEP 2: Delete record from database
        activityRepository.delete(activity);

        // STEP 3: Recalculate goals after deletion
        goalService.refreshGoalsForUser(userId);
    }
}

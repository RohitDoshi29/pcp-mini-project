package com.fitness.config;

import com.fitness.model.*;
import com.fitness.repository.ActivityRepository;
import com.fitness.repository.GoalRepository;
import com.fitness.repository.UserRepository;
import com.fitness.service.GoalService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * ===================================================================
 * [PRESENTATION POINT] CONFIG / DATA SEEDER: DataInitializer
 * ===================================================================
 * - ROLE: Seeds realistic dummy data upon application startup so the app is immediately usable.
 * - CONCEPTS:
 *   1. `@Component`: Spring manages this class as a generic Spring Bean.
 *   2. `CommandLineRunner`: Spring Boot callback interface whose `run(String... args)` method is
 *      executed once immediately after the application context has fully loaded.
 *   3. Idempotent Seeding: Checks `userRepository.count() > 0` to prevent duplicate seeding.
 *   4. Realistic Timeline Data: Seeds workouts across past days so that weekly improvement reports
 *      and charts display rich progress out of the box during live demos.
 * ===================================================================
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final GoalRepository goalRepository;
    private final GoalService goalService;

    // Spring automatically injects required repositories and services
    public DataInitializer(UserRepository userRepository, ActivityRepository activityRepository, 
                           GoalRepository goalRepository, GoalService goalService) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.goalRepository = goalRepository;
        this.goalService = goalService;
    }

    /**
     * [FUNCTION] run
     * - PURPOSE: Executes data seeding when the server finishes starting.
     * - CODE FLOW:
     *   Step 1: Guard check - skip if data already exists in database.
     *   Step 2: Seed two sample users ("Alex Miller" and "Sarah Connor").
     *   Step 3: Seed workout activities across previous week vs current week for comparative metrics.
     *   Step 4: Seed active fitness goals.
     *   Step 5: Run GoalService to evaluate goal progress and calculate completion percentages.
     */
    @Override
    public void run(String... args) {
        // Step 1: Skip if database already contains users
        if (userRepository.count() > 0) {
            return;
        }

        System.out.println("🌱 Seeding sample fitness data...");

        // Step 2: Create Sample Users
        User alex = userRepository.save(new User("Alex Miller", "alex@example.com", 600));
        User sarah = userRepository.save(new User("Sarah Connor", "sarah@example.com", 500));

        LocalDate today = LocalDate.now();

        // Step 3: Seed Sample Activities for Alex
        // Previous Week activities (to establish baseline metrics)
        activityRepository.save(new Activity(alex, ActivityType.RUNNING, 30, 310, 5.0, today.minusDays(12), "Morning park run"));
        activityRepository.save(new Activity(alex, ActivityType.WEIGHTLIFTING, 45, 280, null, today.minusDays(10), "Upper body workout"));
        activityRepository.save(new Activity(alex, ActivityType.CYCLING, 40, 350, 12.0, today.minusDays(8), "Evening bike ride"));

        // Current Week activities (increased calories & duration to demonstrate positive improvement!)
        activityRepository.save(new Activity(alex, ActivityType.RUNNING, 35, 360, 5.8, today.minusDays(6), "Outdoor interval run"));
        activityRepository.save(new Activity(alex, ActivityType.HIIT, 40, 420, null, today.minusDays(4), "Full body HIIT circuit"));
        activityRepository.save(new Activity(alex, ActivityType.CYCLING, 50, 460, 15.5, today.minusDays(2), "Scenic hill climb ride"));
        activityRepository.save(new Activity(alex, ActivityType.WEIGHTLIFTING, 45, 340, null, today, "Leg day & core workout"));

        // Step 4: Seed Sample Goals for Alex
        goalRepository.save(new Goal(alex, "Burn 1500 kcal this week", GoalType.CALORIES_BURNED, 1500.0, today.minusDays(6), today));
        goalRepository.save(new Goal(alex, "Complete 4 Workouts", GoalType.WORKOUT_COUNT, 4.0, today.minusDays(6), today));
        goalRepository.save(new Goal(alex, "Exercise for 180 Minutes", GoalType.WORKOUT_MINUTES, 180.0, today.minusDays(6), today));

        // Sample Activities & Goal for Sarah
        activityRepository.save(new Activity(sarah, ActivityType.YOGA, 50, 210, null, today.minusDays(5), "Vinyasa flow yoga"));
        activityRepository.save(new Activity(sarah, ActivityType.WALKING, 45, 220, 4.2, today.minusDays(3), "Brisk walking in nature"));
        activityRepository.save(new Activity(sarah, ActivityType.SWIMMING, 40, 380, 1.2, today.minusDays(1), "Freestyle laps"));

        goalRepository.save(new Goal(sarah, "Burn 1000 kcal this week", GoalType.CALORIES_BURNED, 1000.0, today.minusDays(6), today));

        // Step 5: Recalculate goal status & progress values
        goalService.refreshGoalsForUser(alex.getId());
        goalService.refreshGoalsForUser(sarah.getId());

        System.out.println("✅ Sample fitness data seeded successfully!");
    }
}



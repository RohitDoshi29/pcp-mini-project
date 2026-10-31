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
 * Initializes realistic sample data on application startup.
 * 
 * Spring Boot Concept:
 * - CommandLineRunner: Spring runs the run() method automatically after the application starts.
 * - This ensures you have instant sample data to test without having to manually insert records!
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final GoalRepository goalRepository;
    private final GoalService goalService;

    public DataInitializer(UserRepository userRepository, ActivityRepository activityRepository, 
                           GoalRepository goalRepository, GoalService goalService) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.goalRepository = goalRepository;
        this.goalService = goalService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Data already seeded
        }

        System.out.println("🌱 Seeding sample fitness data...");

        // 1. Create Sample Users
        User alex = userRepository.save(new User("Alex Miller", "alex@example.com", 600));
        User sarah = userRepository.save(new User("Sarah Connor", "sarah@example.com", 500));

        LocalDate today = LocalDate.now();

        // 2. Sample Activities for Alex
        // Previous Week (Days 12, 10, 8 ago)
        activityRepository.save(new Activity(alex, ActivityType.RUNNING, 30, 310, 5.0, today.minusDays(12), "Morning park run"));
        activityRepository.save(new Activity(alex, ActivityType.WEIGHTLIFTING, 45, 280, null, today.minusDays(10), "Upper body workout"));
        activityRepository.save(new Activity(alex, ActivityType.CYCLING, 40, 350, 12.0, today.minusDays(8), "Evening bike ride"));

        // Current Week (Days 6, 4, 2, and Today) -> Notice increased calories & workouts to show improvement!
        activityRepository.save(new Activity(alex, ActivityType.RUNNING, 35, 360, 5.8, today.minusDays(6), "Outdoor interval run"));
        activityRepository.save(new Activity(alex, ActivityType.HIIT, 40, 420, null, today.minusDays(4), "Full body HIIT circuit"));
        activityRepository.save(new Activity(alex, ActivityType.CYCLING, 50, 460, 15.5, today.minusDays(2), "Scenic hill climb ride"));
        activityRepository.save(new Activity(alex, ActivityType.WEIGHTLIFTING, 45, 340, null, today, "Leg day & core workout"));

        // 3. Sample Goals for Alex
        goalRepository.save(new Goal(alex, "Burn 1500 kcal this week", GoalType.CALORIES_BURNED, 1500.0, today.minusDays(6), today));
        goalRepository.save(new Goal(alex, "Complete 4 Workouts", GoalType.WORKOUT_COUNT, 4.0, today.minusDays(6), today));
        goalRepository.save(new Goal(alex, "Exercise for 180 Minutes", GoalType.WORKOUT_MINUTES, 180.0, today.minusDays(6), today));

        // 4. Sample Activities & Goal for Sarah
        activityRepository.save(new Activity(sarah, ActivityType.YOGA, 50, 210, null, today.minusDays(5), "Vinyasa flow yoga"));
        activityRepository.save(new Activity(sarah, ActivityType.WALKING, 45, 220, 4.2, today.minusDays(3), "Brisk walking in nature"));
        activityRepository.save(new Activity(sarah, ActivityType.SWIMMING, 40, 380, 1.2, today.minusDays(1), "Freestyle laps"));

        goalRepository.save(new Goal(sarah, "Burn 1000 kcal this week", GoalType.CALORIES_BURNED, 1000.0, today.minusDays(6), today));

        // 5. Recalculate goal status & progress values
        goalService.refreshGoalsForUser(alex.getId());
        goalService.refreshGoalsForUser(sarah.getId());

        System.out.println("✅ Sample fitness data seeded successfully!");
    }
}


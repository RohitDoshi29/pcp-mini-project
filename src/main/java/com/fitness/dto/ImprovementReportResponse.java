package com.fitness.dto;

import java.time.LocalDate;

/**
 * Report showing user improvement over time (e.g., This Week vs Last Week).
 * Calculates percentage changes and offers personalized motivational feedback.
 */
public class ImprovementReportResponse {

    private Long userId;
    private String userName;

    // Time periods being compared
    private LocalDate currentPeriodStart;
    private LocalDate currentPeriodEnd;
    private LocalDate previousPeriodStart;
    private LocalDate previousPeriodEnd;

    // Current period totals
    private long currentWorkouts;
    private int currentCalories;
    private int currentMinutes;

    // Previous period totals
    private long previousWorkouts;
    private int previousCalories;
    private int previousMinutes;

    // Changes (+/-)
    private long workoutChange;
    private int calorieChange;
    private int minuteChange;

    // Percentage improvements (+/-)
    private double workoutImprovementPct;
    private double calorieImprovementPct;
    private double minuteImprovementPct;

    // Overall status: "EXCELLENT_PROGRESS", "STEADY_PROGRESS", "NEEDS_BOOST"
    private String improvementStatus;

    // Human-readable motivational coaching feedback
    private String motivationalMessage;

    public ImprovementReportResponse() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getCurrentPeriodStart() {
        return currentPeriodStart;
    }

    public void setCurrentPeriodStart(LocalDate currentPeriodStart) {
        this.currentPeriodStart = currentPeriodStart;
    }

    public LocalDate getCurrentPeriodEnd() {
        return currentPeriodEnd;
    }

    public void setCurrentPeriodEnd(LocalDate currentPeriodEnd) {
        this.currentPeriodEnd = currentPeriodEnd;
    }

    public LocalDate getPreviousPeriodStart() {
        return previousPeriodStart;
    }

    public void setPreviousPeriodStart(LocalDate previousPeriodStart) {
        this.previousPeriodStart = previousPeriodStart;
    }

    public LocalDate getPreviousPeriodEnd() {
        return previousPeriodEnd;
    }

    public void setPreviousPeriodEnd(LocalDate previousPeriodEnd) {
        this.previousPeriodEnd = previousPeriodEnd;
    }

    public long getCurrentWorkouts() {
        return currentWorkouts;
    }

    public void setCurrentWorkouts(long currentWorkouts) {
        this.currentWorkouts = currentWorkouts;
    }

    public int getCurrentCalories() {
        return currentCalories;
    }

    public void setCurrentCalories(int currentCalories) {
        this.currentCalories = currentCalories;
    }

    public int getCurrentMinutes() {
        return currentMinutes;
    }

    public void setCurrentMinutes(int currentMinutes) {
        this.currentMinutes = currentMinutes;
    }

    public long getPreviousWorkouts() {
        return previousWorkouts;
    }

    public void setPreviousWorkouts(long previousWorkouts) {
        this.previousWorkouts = previousWorkouts;
    }

    public int getPreviousCalories() {
        return previousCalories;
    }

    public void setPreviousCalories(int previousCalories) {
        this.previousCalories = previousCalories;
    }

    public int getPreviousMinutes() {
        return previousMinutes;
    }

    public void setPreviousMinutes(int previousMinutes) {
        this.previousMinutes = previousMinutes;
    }

    public long getWorkoutChange() {
        return workoutChange;
    }

    public void setWorkoutChange(long workoutChange) {
        this.workoutChange = workoutChange;
    }

    public int getCalorieChange() {
        return calorieChange;
    }

    public void setCalorieChange(int calorieChange) {
        this.calorieChange = calorieChange;
    }

    public int getMinuteChange() {
        return minuteChange;
    }

    public void setMinuteChange(int minuteChange) {
        this.minuteChange = minuteChange;
    }

    public double getWorkoutImprovementPct() {
        return workoutImprovementPct;
    }

    public void setWorkoutImprovementPct(double workoutImprovementPct) {
        this.workoutImprovementPct = workoutImprovementPct;
    }

    public double getCalorieImprovementPct() {
        return calorieImprovementPct;
    }

    public void setCalorieImprovementPct(double calorieImprovementPct) {
        this.calorieImprovementPct = calorieImprovementPct;
    }

    public double getMinuteImprovementPct() {
        return minuteImprovementPct;
    }

    public void setMinuteImprovementPct(double minuteImprovementPct) {
        this.minuteImprovementPct = minuteImprovementPct;
    }

    public String getImprovementStatus() {
        return improvementStatus;
    }

    public void setImprovementStatus(String improvementStatus) {
        this.improvementStatus = improvementStatus;
    }

    public String getMotivationalMessage() {
        return motivationalMessage;
    }

    public void setMotivationalMessage(String motivationalMessage) {
        this.motivationalMessage = motivationalMessage;
    }
}


package com.fitness.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Request payload for creating or updating a User.
 */
public class UserRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @Positive(message = "Daily calorie target must be positive")
    private Integer dailyCalorieTarget;

    public UserRequest() {
    }

    public UserRequest(String name, String email, Integer dailyCalorieTarget) {
        this.name = name;
        this.email = email;
        this.dailyCalorieTarget = dailyCalorieTarget;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDailyCalorieTarget() {
        return dailyCalorieTarget;
    }

    public void setDailyCalorieTarget(Integer dailyCalorieTarget) {
        this.dailyCalorieTarget = dailyCalorieTarget;
    }
}


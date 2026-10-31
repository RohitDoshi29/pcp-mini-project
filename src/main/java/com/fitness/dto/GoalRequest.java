package com.fitness.dto;

import com.fitness.model.GoalType;
import java.time.LocalDate;

public class GoalRequest {
    public Long userId;
    public String title;
    public GoalType goalType;
    public Double targetValue;
    public LocalDate startDate;
    public LocalDate endDate;
}

package com.fitness.exception;

/**
 * Custom Exception thrown when an item (User, Activity, Goal) cannot be found.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}


package com.fitness.service;

import com.fitness.dto.UserRequest;
import com.fitness.exception.ResourceNotFoundException;
import com.fitness.model.User;
import com.fitness.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for User operations.
 * 
 * Spring Boot Concepts:
 * - @Service: Marks this as a Service Component in the Spring context (business logic layer).
 * - @Transactional: Ensures database changes in methods succeed together or rollback on failure.
 * - Constructor Injection: Recommended Spring practice to inject dependencies (UserRepository) cleanly.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email '" + request.getEmail() + "' already exists.");
        }
        User user = new User(request.getName(), request.getEmail(), request.getDailyCalorieTarget());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}


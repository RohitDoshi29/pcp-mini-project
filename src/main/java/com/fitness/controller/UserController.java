package com.fitness.controller;

import com.fitness.model.User;
import com.fitness.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===================================================================
 * [PRESENTATION POINT] CONTROLLER LAYER: UserController
 * ===================================================================
 * - ROLE: Manages User Accounts and profiles.
 * - CONCEPTS:
 *   1. Direct Entity binding: Accepts `User` directly in `@RequestBody` without needing bloated DTOs.
 *   2. Simple CRUD operations for user accounts.
 * ===================================================================
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    // Constructor Injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * [ENDPOINT 1] POST /api/users
     * - PURPOSE: Registers a new user account (name, email, daily calorie target).
     * - FLOW:
     *   1. Checks for duplicate emails in UserService.
     *   2. Saves user in SQL database.
     *   3. Sends HTTP 201 Created.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        // CODE FLOW: Register user via service
        return userService.createUser(user);
    }

    /**
     * [ENDPOINT 2] GET /api/users
     * - PURPOSE: Lists all registered users (used by the navbar dropdown switcher in UI).
     */
    @GetMapping
    public List<User> getAllUsers() {
        // CODE FLOW: Fetch all users
        return userService.getAllUsers();
    }

    /**
     * [ENDPOINT 3] GET /api/users/{id}
     * - PURPOSE: Retrieves a single user profile by ID.
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        // CODE FLOW: Fetch user by primary key
        return userService.getUserById(id);
    }
}

package com.fitness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point of the Daily Activity & Fitness Tracker Application.
 * 
 * Spring Boot Concept:
 * - @SpringBootApplication: Convenience annotation that combines:
 *   1. @Configuration: Tags the class as the source of bean definitions.
 *   2. @EnableAutoConfiguration: Tells Spring Boot to configure beans based on classpath settings.
 *   3. @ComponentScan: Automatically scans and detects @Component, @Service, @Repository, and @RestController.
 */
@SpringBootApplication
public class ActivityTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivityTrackerApplication.class, args);
        System.out.println("==========================================================");
        System.out.println("🚀 Fitness Activity Tracker is running!");
        System.out.println("🌐 Web Dashboard:    http://localhost:8080/");
        System.out.println("📄 Swagger UI API:    http://localhost:8080/swagger-ui.html");
        System.out.println("🗄️ H2 Database:       http://localhost:8080/h2-console");
        System.out.println("==========================================================");
    }
}


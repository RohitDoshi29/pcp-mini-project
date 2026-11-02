package com.fitness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ===================================================================
 * [PRESENTATION POINT] MAIN APPLICATION ENTRYPOINT: ActivityTrackerApplication
 * ===================================================================
 * - ROLE: Starts up the entire Spring Boot application and embedded server.
 * - CONCEPTS:
 *   1. `@SpringBootApplication`: Meta-annotation combining three critical Spring features:
 *      a) `@Configuration`: Defines this class as the central bean configuration source.
 *      b) `@EnableAutoConfiguration`: Automatically sets up Tomcat web server, H2 database,
 *         JPA Hibernate, and JSON serializers without complex XML setup.
 *      c) `@ComponentScan`: Recursively scans package `com.fitness` for `@Controller`,
 *         `@Service`, `@Repository`, and `@Component` classes to register as Spring Beans.
 *   2. `SpringApplication.run()`: Starts the Spring IoC container, builds the Dependency
 *      Injection graph, starts embedded Tomcat on port 8080, and runs any `CommandLineRunner` beans.
 * ===================================================================
 */
@SpringBootApplication
public class ActivityTrackerApplication {

    public static void main(String[] args) {
        // CODE FLOW: Launch Spring Boot context
        SpringApplication.run(ActivityTrackerApplication.class, args);
        
        // Console banner with helpful URLs for testing and presentation
        System.out.println("==========================================================");
        System.out.println("🚀 Fitness Activity Tracker is running!");
        System.out.println("🌐 Web Dashboard:    http://localhost:8080/");
        System.out.println("📄 Swagger UI API:    http://localhost:8080/swagger-ui.html");
        System.out.println("🗄️ H2 Database:       http://localhost:8080/h2-console");
        System.out.println("==========================================================");
    }
}



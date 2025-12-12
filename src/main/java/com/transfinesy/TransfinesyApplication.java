package com.transfinesy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application entry point for transFINESy Web UI.
 * 
 * This is the main class that starts the Spring Boot application.
 * It initializes the Spring context, starts the embedded Tomcat server,
 * and enables auto-configuration for Spring Boot features.
 * 
 * The @SpringBootApplication annotation is equivalent to:
 * - @Configuration: Marks this class as a source of bean definitions
 * - @EnableAutoConfiguration: Enables Spring Boot auto-configuration
 * - @ComponentScan: Scans for Spring components in the package and sub-packages
 * 
 * @author transFINESy Development Team
 * @version 1.0
 */
@SpringBootApplication
public class TransfinesyApplication {
    /**
     * Main method that launches the Spring Boot application.
     * 
     * @param args Command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(TransfinesyApplication.class, args);
    }
}







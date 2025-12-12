package com.transfinesy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Performance optimization configuration class.
 * 
 * This class configures static resource caching to improve application performance.
 * By caching CSS, JavaScript, and image files, the browser can reuse cached resources
 * instead of downloading them on every page load.
 * 
 * Key Features:
 * - Configures static resource handlers for CSS, JS, and images
 * - Sets cache period to 1 year (31536000 seconds)
 * - Improves page load times for returning users
 * - Reduces server load and bandwidth usage
 * 
 * Cached Resources:
 * - CSS files: /css/**
 * - JavaScript files: /js/**
 * - Image files: /images/**
 * 
 * Cache Period:
 * - 1 year (31536000 seconds)
 * - Browsers will cache resources for 1 year
 * - Cache can be cleared by browser or server update
 * 
 * @author transFINESy Development Team
 */
@Configuration
public class PerformanceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cache static resources for 1 year
        registry.addResourceHandler("/css/**", "/js/**", "/images/**")
                .addResourceLocations("classpath:/static/css/", "classpath:/static/js/", "classpath:/static/images/")
                .setCachePeriod(31536000); // 1 year in seconds
    }
}







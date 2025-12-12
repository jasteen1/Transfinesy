package com.transfinesy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database configuration class for managing database connections.
 * 
 * This class provides database connection management using Spring Boot's DataSource
 * with connection pooling for optimal performance. Connection pooling reuses existing
 * connections instead of creating new ones for each query, significantly improving
 * application performance.
 * 
 * Key Features:
 * - Uses Spring's DataSource for connection pooling
 * - Provides static method for backward compatibility
 * - Automatic connection reuse from pool
 * - Fallback initialization from Spring context
 * 
 * Performance Benefits:
 * - Eliminates connection creation overhead
 * - Reduces database connection time
 * - Improves query response times
 * - Handles concurrent requests efficiently
 * 
 * Usage:
 * - Static method: DBConfig.getConnection() (for repositories)
 * - Instance method: dbConfig.getConnectionInstance() (for dependency injection)
 * 
 * @author transFINESy Development Team
 */
@Component
public class DBConfig implements ApplicationContextAware {
    
    private static volatile DataSource staticDataSource;
    private static volatile ApplicationContext applicationContext;
    private final DataSource dataSource;
    
    @Autowired
    public DBConfig(DataSource dataSource) {
        this.dataSource = dataSource;
        // Set static reference immediately when bean is created
        DBConfig.staticDataSource = dataSource;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        // Store context for fallback initialization
        DBConfig.applicationContext = applicationContext;
        // Ensure DataSource is available statically
        if (staticDataSource == null && applicationContext != null) {
            try {
                staticDataSource = applicationContext.getBean(DataSource.class);
            } catch (Exception e) {
                // Ignore - will try again in getConnection()
            }
        }
    }
    
    /**
     * Static method for backward compatibility with existing repository code.
     * Now uses connection pooling instead of creating new connections.
     * This is the key performance fix - connections are reused from a pool.
     */
    public static Connection getConnection() throws SQLException {
        // Try to get from static reference first
        if (staticDataSource != null) {
            return staticDataSource.getConnection();
        }
        
        // Fallback: try to get from Spring context if available
        if (applicationContext != null) {
            try {
                staticDataSource = applicationContext.getBean(DataSource.class);
                if (staticDataSource != null) {
                    return staticDataSource.getConnection();
                }
            } catch (Exception e) {
                // Continue to throw error below
            }
        }
        
        throw new IllegalStateException("DBConfig not initialized. Make sure Spring Boot has started and DBConfig bean is created. Error: DataSource is null.");
    }
    
    /**
     * Instance method (for dependency injection usage).
     * Gets a database connection from the connection pool.
     */
    public Connection getConnectionInstance() throws SQLException {
        return dataSource.getConnection();
    }
}

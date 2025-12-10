package com.transfinesy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database configuration class.
 * Uses Spring Boot's DataSource with connection pooling for MUCH better performance.
 * Connection pooling reuses connections instead of creating new ones each time.
 * 
 * This fixes the major performance issue where every query created a new database connection.
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

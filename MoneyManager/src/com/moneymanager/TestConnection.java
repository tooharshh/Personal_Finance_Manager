package com.moneymanager;

import com.moneymanager.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Test class to diagnose database connection issues
 */
public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        // Test 1: Basic connection
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection successful!");
                
                // Test 2: Check if users table exists
                try {
                    PreparedStatement stmt = conn.prepareStatement("SHOW TABLES LIKE 'users'");
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        System.out.println("✓ Users table exists!");
                        
                        // Test 3: Check table structure
                        PreparedStatement descStmt = conn.prepareStatement("DESCRIBE users");
                        ResultSet descRs = descStmt.executeQuery();
                        System.out.println("Users table structure:");
                        while (descRs.next()) {
                            System.out.println("  - " + descRs.getString("Field") + " (" + descRs.getString("Type") + ")");
                        }
                        
                        // Test 4: Count existing users
                        PreparedStatement countStmt = conn.prepareStatement("SELECT COUNT(*) as user_count FROM users");
                        ResultSet countRs = countStmt.executeQuery();
                        if (countRs.next()) {
                            System.out.println("✓ Total users in database: " + countRs.getInt("user_count"));
                        }
                        
                    } else {
                        System.out.println("✗ ERROR: Users table does not exist!");
                        System.out.println("Please run the database setup script: database/money_manager.sql");
                    }
                } catch (Exception e) {
                    System.out.println("✗ ERROR checking users table: " + e.getMessage());
                }
                
            } else {
                System.out.println("✗ ERROR: Failed to establish database connection!");
            }
        } catch (Exception e) {
            System.out.println("✗ ERROR: Database connection failed!");
            System.out.println("Error details: " + e.getMessage());
            System.out.println("\nPossible solutions:");
            System.out.println("1. Make sure MySQL server is running");
            System.out.println("2. Check database name 'money_manager' exists");
            System.out.println("3. Verify username 'root' and password '1234567890'");
            System.out.println("4. Ensure MySQL is running on localhost:3306");
        }
    }
}
package com.moneymanager.util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtil {
    
    // Email validation pattern
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    
    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid email format, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return pattern.matcher(email).matches();
    }
    
    /**
     * Validate username
     * @param username Username to validate
     * @return true if valid username, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        // Username should be 3-20 characters, alphanumeric and underscore only
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }
    
    /**
     * Validate password strength
     * @param password Password to validate
     * @return true if valid password, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // Password should be at least 6 characters
        return true;
    }
    
    /**
     * Validate full name
     * @param fullName Full name to validate
     * @return true if valid full name, false otherwise
     */
    public static boolean isValidFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        // Full name should be 2-50 characters, letters and spaces only
        return fullName.trim().matches("^[a-zA-Z\\s]{2,50}$");
    }
    
    /**
     * Check if string is empty or null
     * @param str String to check
     * @return true if empty or null, false otherwise
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Validate numeric input
     * @param value String value to validate
     * @return true if valid number, false otherwise
     */
    public static boolean isValidNumber(String value) {
        if (isEmpty(value)) {
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate positive number
     * @param value String value to validate
     * @return true if valid positive number, false otherwise
     */
    public static boolean isValidPositiveNumber(String value) {
        if (!isValidNumber(value)) {
            return false;
        }
        try {
            double num = Double.parseDouble(value);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
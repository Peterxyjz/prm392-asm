package com.example.myapplication.utils;

/**
 * Logger utility class for centralized logging
 * Provides different log levels and formatted output
 */
public class Logger {
    
    private static final String TAG = "SakuraRestaurant";
    private static boolean isDebugMode = true; // Set to false in production
    
    /**
     * Log debug message
     * @param message Debug message
     */
    public static void d(String message) {
        if (isDebugMode) {
            android.util.Log.d(TAG, message);
        }
    }
    
    /**
     * Log debug message with tag
     * @param tag Custom tag
     * @param message Debug message
     */
    public static void d(String tag, String message) {
        if (isDebugMode) {
            android.util.Log.d(TAG + "_" + tag, message);
        }
    }
    
    /**
     * Log info message
     * @param message Info message
     */
    public static void i(String message) {
        android.util.Log.i(TAG, message);
    }
    
    /**
     * Log info message with tag
     * @param tag Custom tag
     * @param message Info message
     */
    public static void i(String tag, String message) {
        android.util.Log.i(TAG + "_" + tag, message);
    }
    
    /**
     * Log warning message
     * @param message Warning message
     */
    public static void w(String message) {
        android.util.Log.w(TAG, message);
    }
    
    /**
     * Log warning message with tag
     * @param tag Custom tag
     * @param message Warning message
     */
    public static void w(String tag, String message) {
        android.util.Log.w(TAG + "_" + tag, message);
    }
    
    /**
     * Log error message
     * @param message Error message
     */
    public static void e(String message) {
        android.util.Log.e(TAG, message);
    }
    
    /**
     * Log error message with tag
     * @param tag Custom tag
     * @param message Error message
     */
    public static void e(String tag, String message) {
        android.util.Log.e(TAG + "_" + tag, message);
    }
    
    /**
     * Log error message with exception
     * @param message Error message
     * @param throwable Exception
     */
    public static void e(String message, Throwable throwable) {
        android.util.Log.e(TAG, message, throwable);
    }
    
    /**
     * Log error message with tag and exception
     * @param tag Custom tag
     * @param message Error message
     * @param throwable Exception
     */
    public static void e(String tag, String message, Throwable throwable) {
        android.util.Log.e(TAG + "_" + tag, message, throwable);
    }
    
    /**
     * Set debug mode
     * @param debug True to enable debug logs
     */
    public static void setDebugMode(boolean debug) {
        isDebugMode = debug;
    }
    
    /**
     * Log user action for analytics
     * @param action User action
     * @param details Additional details
     */
    public static void logUserAction(String action, String details) {
        i("USER_ACTION", action + " - " + details);
    }
    
    /**
     * Log cart operation
     * @param operation Cart operation
     * @param itemId Item ID
     * @param quantity Quantity
     */
    public static void logCartOperation(String operation, int itemId, int quantity) {
        i("CART", operation + " - Item ID: " + itemId + ", Quantity: " + quantity);
    }
    
    /**
     * Log authentication event
     * @param event Auth event
     * @param username Username
     */
    public static void logAuthEvent(String event, String username) {
        i("AUTH", event + " - User: " + username);
    }
    
    // Private constructor to prevent instantiation
    private Logger() {
        throw new AssertionError("This class should not be instantiated");
    }
}
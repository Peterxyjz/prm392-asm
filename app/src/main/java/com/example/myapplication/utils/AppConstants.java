package com.example.myapplication.utils;

/**
 * Centralized constants for the application
 * Extracted from scattered hardcoded values to improve maintainability
 */
public class AppConstants {
    
    // SharedPreferences Keys
    public static final String PREFS_USER = "user_prefs";
    public static final String PREFS_CART = "cart_prefs";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_CURRENT_USERNAME = "current_username";
    public static final String KEY_CART_ITEMS = "cart_items";
    
    // User Profile Keys (suffixes)
    public static final String KEY_SUFFIX_FULL_NAME = "_full_name";
    public static final String KEY_SUFFIX_ADDRESS = "_address";
    public static final String KEY_SUFFIX_PHONE = "_phone";
    
    // Business Logic Constants
    public static final double FREE_SHIPPING_THRESHOLD = 100000.0;
    public static final double DELIVERY_FEE = 15000.0;
    public static final String DEFAULT_ADDRESS = "Nhập địa chỉ giao hàng";
    public static final int SPLASH_DELAY_MS = 2000;
    
    // Food Categories
    public static final String CATEGORY_ALL = "All";
    public static final String CATEGORY_NOODLES = "Noodles";
    public static final String CATEGORY_SUSHI = "Sushi";
    public static final String CATEGORY_RICE = "Rice";
    public static final String CATEGORY_APPETIZER = "Appetizer";
    
    // UI Constants
    public static final int DEFAULT_QUANTITY = 1;
    public static final int MIN_QUANTITY = 1;
    public static final String CURRENCY_SYMBOL = "₫";
    
    // Validation Constants
    public static final int MIN_USERNAME_LENGTH = 1;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MIN_PHONE_LENGTH = 10;
    public static final int MAX_PHONE_LENGTH = 15;
    
    // Restaurant Information
    public static final String RESTAURANT_NAME = "Sakura Restaurant";
    public static final String RESTAURANT_ADDRESS = "30 Đ. Tân Thắng, Sơn Kỳ, Tân Phú, Hồ Chí Minh 700000";
    public static final String RESTAURANT_PHONE = "028 1234 5678";
    public static final String RESTAURANT_HOURS = "10:00 - 22:00 (Thứ 2 - Chủ nhật)";
    public static final double RESTAURANT_LAT = 10.7915;
    public static final double RESTAURANT_LNG = 106.6255;
    
    // Private constructor to prevent instantiation
    private AppConstants() {
        throw new AssertionError("This class should not be instantiated");
    }
}
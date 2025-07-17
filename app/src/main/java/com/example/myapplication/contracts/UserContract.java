package com.example.myapplication.contracts;

import com.example.myapplication.model.User;

/**
 * Contract interface for User management operations
 * Defines the expected behavior for user-related functionality
 */
public interface UserContract {
    
    /**
     * Interface for User Manager operations
     */
    interface UserManager {
        
        /**
         * Check if user is currently logged in
         * @return True if logged in, false otherwise
         */
        boolean isLoggedIn();
        
        /**
         * Perform user login
         * @param username Username for login
         * @return True if login successful, false otherwise
         */
        boolean login(String username);
        
        /**
         * Logout current user
         */
        void logout();
        
        /**
         * Get current logged in user
         * @return User object if logged in, null otherwise
         */
        User getCurrentUser();
        
        /**
         * Update user information
         * @param fullName Updated full name
         * @param address Updated address
         * @param phone Updated phone number
         */
        void updateUserInfo(String fullName, String address, String phone);
        
        /**
         * Check if username already exists
         * @param username Username to check
         * @return True if exists, false otherwise
         */
        boolean isExistingUser(String username);
        
        /**
         * Get current username
         * @return Current username or empty string
         */
        String getCurrentUsername();
    }
    
    /**
     * Interface for User-related UI callbacks
     */
    interface UserView {
        
        /**
         * Called when login is successful
         * @param user Logged in user
         */
        void onLoginSuccess(User user);
        
        /**
         * Called when login fails
         * @param errorMessage Error message
         */
        void onLoginFailure(String errorMessage);
        
        /**
         * Called when user info is updated successfully
         */
        void onUserInfoUpdated();
        
        /**
         * Called when user info update fails
         * @param errorMessage Error message
         */
        void onUserInfoUpdateFailure(String errorMessage);
        
        /**
         * Called when user is logged out
         */
        void onUserLoggedOut();
    }
}
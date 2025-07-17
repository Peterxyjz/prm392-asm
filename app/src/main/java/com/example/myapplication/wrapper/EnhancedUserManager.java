package com.example.myapplication.wrapper;

import android.content.Context;
import com.example.myapplication.contracts.UserContract;
import com.example.myapplication.manager.UserManager;
import com.example.myapplication.model.User;
import com.example.myapplication.utils.ValidationUtils;

/**
 * Enhanced wrapper around existing UserManager
 * Adds validation and error handling without modifying original code
 * Implements UserContract interface for better structure
 */
public class EnhancedUserManager implements UserContract.UserManager {
    
    private final UserManager originalManager;
    private UserContract.UserView userView;
    
    /**
     * Constructor
     * @param context Application context
     */
    public EnhancedUserManager(Context context) {
        this.originalManager = UserManager.getInstance(context);
    }
    
    /**
     * Set user view callback
     * @param userView User view callback
     */
    public void setUserView(UserContract.UserView userView) {
        this.userView = userView;
    }
    
    @Override
    public boolean isLoggedIn() {
        return originalManager.isLoggedIn();
    }
    
    @Override
    public boolean login(String username) {
        // Add validation before calling original method
        ValidationUtils.ValidationResult validation = ValidationUtils.validateUsername(username);
        
        if (!validation.isValid()) {
            if (userView != null) {
                userView.onLoginFailure(validation.getMessage());
            }
            return false;
        }
        
        // Call original method
        boolean result = originalManager.login(ValidationUtils.cleanInput(username));
        
        // Notify view
        if (userView != null) {
            if (result) {
                User user = originalManager.getCurrentUser();
                userView.onLoginSuccess(user);
            } else {
                userView.onLoginFailure("Đăng nhập thất bại");
            }
        }
        
        return result;
    }
    
    @Override
    public void logout() {
        originalManager.logout();
        if (userView != null) {
            userView.onUserLoggedOut();
        }
    }
    
    @Override
    public User getCurrentUser() {
        return originalManager.getCurrentUser();
    }
    
    @Override
    public void updateUserInfo(String fullName, String address, String phone) {
        // Validate all inputs
        ValidationUtils.ValidationResult fullNameValidation = ValidationUtils.validateFullName(fullName);
        ValidationUtils.ValidationResult addressValidation = ValidationUtils.validateAddress(address);
        ValidationUtils.ValidationResult phoneValidation = ValidationUtils.validatePhone(phone);
        
        if (!fullNameValidation.isValid()) {
            if (userView != null) {
                userView.onUserInfoUpdateFailure(fullNameValidation.getMessage());
            }
            return;
        }
        
        if (!addressValidation.isValid()) {
            if (userView != null) {
                userView.onUserInfoUpdateFailure(addressValidation.getMessage());
            }
            return;
        }
        
        if (!phoneValidation.isValid()) {
            if (userView != null) {
                userView.onUserInfoUpdateFailure(phoneValidation.getMessage());
            }
            return;
        }
        
        // Call original method with cleaned inputs
        originalManager.updateUserInfo(
            ValidationUtils.cleanInput(fullName),
            ValidationUtils.cleanInput(address),
            ValidationUtils.cleanInput(phone)
        );
        
        if (userView != null) {
            userView.onUserInfoUpdated();
        }
    }
    
    @Override
    public boolean isExistingUser(String username) {
        if (ValidationUtils.isEmpty(username)) {
            return false;
        }
        // Check if user exists by trying to find username
        User user = originalManager.getCurrentUser();
        if (user != null && user.getUsername().equals(ValidationUtils.cleanInput(username))) {
            return true;
        }
        return false;
    }
    
    @Override
    public String getCurrentUsername() {
        User user = originalManager.getCurrentUser();
        return user != null ? user.getUsername() : "";
    }
    
    /**
     * Additional helper methods
     */
    
    /**
     * Get welcome message for current user
     * @return Welcome message string
     */
    public String getWelcomeMessage() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            String name = ValidationUtils.isEmpty(currentUser.getFullName()) 
                ? currentUser.getUsername() 
                : currentUser.getFullName();
            return "Chào " + name + "!";
        }
        return "Chào mừng!";
    }
    
    /**
     * Check if user profile is complete
     * @return True if profile has all required fields
     */
    public boolean isProfileComplete() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return !ValidationUtils.isEmpty(currentUser.getFullName()) &&
               !ValidationUtils.isEmpty(currentUser.getPhone()) &&
               ValidationUtils.validateAddress(currentUser.getAddress()).isValid();
    }
    
    /**
     * Get user display name
     * @return Full name if available, otherwise username
     */
    public String getUserDisplayName() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            return ValidationUtils.isEmpty(currentUser.getFullName()) 
                ? currentUser.getUsername() 
                : currentUser.getFullName();
        }
        return "";
    }
}
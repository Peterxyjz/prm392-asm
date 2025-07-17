package com.example.myapplication.contracts;

import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.FoodItem;
import java.util.List;

/**
 * View interface contracts for Activities
 * Defines methods that Activities should implement to work with Controllers
 */
public interface ViewContracts {
    
    /**
     * Interface for MenuActivity view operations
     */
    interface MenuView {
        /**
         * Update food items list in RecyclerView
         * @param foodItems List of food items to display
         */
        void updateFoodItems(List<FoodItem> foodItems);
        
        /**
         * Update cart count badge
         * @param count Number of items in cart
         */
        void updateCartCount(int count);
        
        /**
         * Show error message to user
         * @param message Error message
         */
        void showError(String message);
        
        /**
         * Update category filter selection
         * @param category Selected category
         */
        void updateCategoryFilter(String category);
        
        /**
         * Navigate to cart screen
         */
        void navigateToCart();
        
        /**
         * Navigate back to previous screen
         */
        void navigateBack();
        
        /**
         * Refresh food adapter (notify data changed)
         */
        void refreshAdapter();
    }
    
    /**
     * Interface for CartActivity view operations
     */
    interface CartView {
        /**
         * Show empty cart state
         */
        void showEmptyCart();
        
        /**
         * Show cart items list
         * @param cartItems List of items in cart
         */
        void showCartItems(List<CartItem> cartItems);
        
        /**
         * Update price display
         * @param subtotal Formatted subtotal
         * @param deliveryFee Formatted delivery fee
         * @param total Formatted total
         * @param isFreeShipping Whether free shipping applies
         */
        void updatePrices(String subtotal, String deliveryFee, String total, boolean isFreeShipping);
        
        /**
         * Update user address display
         * @param address User address
         */
        void updateAddress(String address);
        
        /**
         * Show confirmation dialog for item removal
         * @param itemId Item ID to remove
         * @param itemName Item name for confirmation
         */
        void showRemoveConfirmation(int itemId, String itemName);
        
        /**
         * Show checkout confirmation dialog
         * @param total Total amount
         * @param address Delivery address
         * @param estimatedTime Estimated delivery time
         */
        void showCheckoutConfirmation(String total, String address, String estimatedTime);
        
        /**
         * Show success message
         * @param message Success message
         */
        void showSuccess(String message);
        
        /**
         * Show error message
         * @param message Error message
         */
        void showError(String message);
        
        /**
         * Navigate to main screen after successful checkout
         */
        void navigateToMain();
        
        /**
         * Navigate back to previous screen
         */
        void navigateBack();
        
        /**
         * Navigate to menu screen
         */
        void navigateToMenu();
        
        /**
         * Show checkout success state
         */
        void showCheckoutSuccess();
    }
}
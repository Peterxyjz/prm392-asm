package com.example.myapplication.contracts;

import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.FoodItem;
import java.util.List;

/**
 * Contract interface for Cart management operations
 * Defines the expected behavior for cart-related functionality
 */
public interface CartContract {
    
    /**
     * Interface for Cart Manager operations
     */
    interface CartManager {
        
        /**
         * Add item to cart
         * @param foodItem Food item to add
         * @param quantity Quantity to add
         */
        void addToCart(FoodItem foodItem, int quantity);
        
        /**
         * Remove item from cart
         * @param foodItemId ID of food item to remove
         */
        void removeFromCart(int foodItemId);
        
        /**
         * Update quantity of item in cart
         * @param foodItemId ID of food item
         * @param newQuantity New quantity
         */
        void updateQuantity(int foodItemId, int newQuantity);
        
        /**
         * Get all items in cart
         * @return List of cart items
         */
        List<CartItem> getCartItems();
        
        /**
         * Get total count of items in cart
         * @return Total count
         */
        int getCartItemCount();
        
        /**
         * Get total price of all items in cart
         * @return Total price
         */
        double getTotalPrice();
        
        /**
         * Clear all items from cart
         */
        void clearCart();
        
        /**
         * Get specific cart item
         * @param foodItemId ID of food item
         * @return CartItem if found, null otherwise
         */
        CartItem getCartItem(int foodItemId);
        
        /**
         * Check if item is in cart
         * @param foodItemId ID of food item
         * @return True if in cart, false otherwise
         */
        boolean isInCart(int foodItemId);
    }
    
    /**
     * Interface for Cart-related UI callbacks
     */
    interface CartView {
        
        /**
         * Called when cart is updated
         */
        void onCartUpdated();
        
        /**
         * Called when item is added to cart
         * @param cartItem Added cart item
         */
        void onItemAddedToCart(CartItem cartItem);
        
        /**
         * Called when item is removed from cart
         * @param foodItemId ID of removed item
         */
        void onItemRemovedFromCart(int foodItemId);
        
        /**
         * Called when cart is cleared
         */
        void onCartCleared();
        
        /**
         * Called when cart operation fails
         * @param errorMessage Error message
         */
        void onCartOperationFailure(String errorMessage);
    }
}
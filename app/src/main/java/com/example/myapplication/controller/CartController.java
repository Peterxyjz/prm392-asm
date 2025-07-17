package com.example.myapplication.controller;

import com.example.myapplication.activity.CartActivity;
import com.example.myapplication.contracts.CartContract;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.FoodItem;
import com.example.myapplication.utils.Logger;
import com.example.myapplication.utils.PriceUtils;
import com.example.myapplication.utils.ValidationUtils;
import com.example.myapplication.wrapper.EnhancedCartManager;
import com.example.myapplication.wrapper.EnhancedUserManager;
import java.util.List;

/**
 * Controller class for Cart functionality
 * Extracted from CartActivity to separate business logic from UI
 * Handles cart operations, price calculations, and checkout logic
 */
public class CartController {
    
    private CartActivity activity;
    private EnhancedCartManager cartManager;
    private EnhancedUserManager userManager;
    
    /**
     * Constructor
     * @param activity CartActivity instance
     */
    public CartController(CartActivity activity) {
        this.activity = activity;
        this.cartManager = new EnhancedCartManager(activity);
        this.userManager = new EnhancedUserManager(activity);
        
        Logger.i("CartController", "Controller initialized");
    }
    
    /**
     * Initialize controller
     * Set up callbacks and load initial data
     */
    public void initialize() {
        Logger.d("CartController", "Initializing cart controller");
        
        // Setup cart manager callback
        cartManager.setCartView(new CartContract.CartView() {
            @Override
            public void onCartUpdated() {
                refreshCartDisplay();
            }
            
            @Override
            public void onItemAddedToCart(CartItem cartItem) {
                Logger.logCartOperation("ADD", cartItem.getFoodItem().getId(), cartItem.getQuantity());
                refreshCartDisplay();
            }
            
            @Override
            public void onItemRemovedFromCart(int foodItemId) {
                Logger.logCartOperation("REMOVE", foodItemId, 0);
                refreshCartDisplay();
            }
            
            @Override
            public void onCartCleared() {
                Logger.logCartOperation("CLEAR", 0, 0);
                refreshCartDisplay();
                // Success - cart cleared, activity will update UI automatically
            }
            
            @Override
            public void onCartOperationFailure(String errorMessage) {
                Logger.w("CartController", "Cart operation failed: " + errorMessage);
                android.widget.Toast.makeText(activity, errorMessage, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        
        // Load initial data
        refreshCartDisplay();
        loadUserAddress();
    }
    
    /**
     * Refresh cart display
     */
    public void refreshCartDisplay() {
        Logger.d("CartController", "Refreshing cart display");
        
        List<CartItem> cartItems = cartManager.getCartItems();
        
        if (cartItems.isEmpty()) {
            // Empty cart - let activity handle UI update
        } else {
            // Cart has items - let activity handle UI update
            updatePriceDisplay();
        }
    }
    
    /**
     * Update price display
     */
    public void updatePriceDisplay() {
        Logger.d("CartController", "Updating price display");
        
        double subtotal = cartManager.getTotalPrice();
        double deliveryFee = cartManager.getDeliveryFee();
        double finalTotal = cartManager.getFinalTotal();
        
        // Price updated - let activity handle its own updatePriceDisplay()
        
        Logger.d("CartController", String.format("Price updated - Subtotal: %.0f, Delivery: %.0f, Total: %.0f", 
                subtotal, deliveryFee, finalTotal));
    }
    
    /**
     * Load user address
     */
    public void loadUserAddress() {
        Logger.d("CartController", "Loading user address");
        
        try {
            String address = userManager.getCurrentUser().getAddress();
            // Address loaded - let activity handle UI update
        } catch (Exception e) {
            Logger.e("CartController", "Error loading user address", e);
            // Error loading address - let activity handle UI update
        }
    }
    
    /**
     * Handle quantity increase
     * @param foodItemId Food item ID
     */
    public void handleQuantityIncrease(int foodItemId) {
        Logger.d("CartController", "Increasing quantity for item: " + foodItemId);
        
        CartItem cartItem = cartManager.getCartItem(foodItemId);
        if (cartItem != null) {
            int newQuantity = cartItem.getQuantity() + 1;
            cartManager.updateQuantity(foodItemId, newQuantity);
        }
    }
    
    /**
     * Handle quantity decrease
     * @param foodItemId Food item ID
     */
    public void handleQuantityDecrease(int foodItemId) {
        Logger.d("CartController", "Decreasing quantity for item: " + foodItemId);
        
        CartItem cartItem = cartManager.getCartItem(foodItemId);
        if (cartItem != null) {
            if (cartItem.getQuantity() == 1) {
                // Show confirmation dialog for removal
                // Show confirmation - let activity handle dialog
            } else {
                int newQuantity = cartItem.getQuantity() - 1;
                cartManager.updateQuantity(foodItemId, newQuantity);
            }
        }
    }
    
    /**
     * Handle confirmed item removal
     * @param foodItemId Food item ID to remove
     */
    public void handleConfirmedRemoval(int foodItemId) {
        Logger.d("CartController", "Confirmed removal of item: " + foodItemId);
        cartManager.removeFromCart(foodItemId);
    }
    
    /**
     * Handle address edit
     * @param newAddress New address
     */
    public void handleAddressEdit(String newAddress) {
        Logger.d("CartController", "Updating address: " + newAddress);
        
        ValidationUtils.ValidationResult validation = ValidationUtils.validateAddress(newAddress);
        if (!validation.isValid()) {
            android.widget.Toast.makeText(activity, validation.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            // Update user address
            String cleanAddress = ValidationUtils.cleanInput(newAddress);
            userManager.getCurrentUser().setAddress(cleanAddress);
            userManager.updateUserInfo(
                userManager.getCurrentUser().getFullName(),
                cleanAddress,
                userManager.getCurrentUser().getPhone()
            );
            
            // Address updated - let activity handle UI update
            android.widget.Toast.makeText(activity, "Địa chỉ đã được cập nhật", android.widget.Toast.LENGTH_SHORT).show();
            
            Logger.logUserAction("ADDRESS_UPDATE", cleanAddress);
            
        } catch (Exception e) {
            Logger.e("CartController", "Error updating address", e);
            android.widget.Toast.makeText(activity, "Không thể cập nhật địa chỉ", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Handle checkout process
     */
    public void handleCheckout() {
        Logger.d("CartController", "Starting checkout process");
        
        // Validate cart
        ValidationUtils.ValidationResult cartValidation = cartManager.validateForCheckout();
        if (!cartValidation.isValid()) {
            android.widget.Toast.makeText(activity, cartValidation.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Validate user address
        String userAddress = userManager.getCurrentUser().getAddress();
        ValidationUtils.ValidationResult addressValidation = ValidationUtils.validateAddress(userAddress);
        if (!addressValidation.isValid()) {
            android.widget.Toast.makeText(activity, "Vui lòng cập nhật địa chỉ giao hàng hợp lệ", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show checkout confirmation
        // Show checkout confirmation - let activity handle dialog

        
        Logger.logUserAction("CHECKOUT_INITIATED", "Total: " + cartManager.getFormattedFinalTotal());
    }
    
    /**
     * Handle confirmed checkout
     */
    public void handleConfirmedCheckout() {
        Logger.d("CartController", "Processing confirmed checkout");
        
        if (cartManager == null) {
            return;
        }
        
        try {
            // Log checkout details
            Logger.i("CartController", "Checkout confirmed - " + 
                    "Items: " + cartManager.getCartItemCount() + 
                    ", Total: " + cartManager.getFormattedFinalTotal());
            
            // Clear cart
            cartManager.clearCart();
            
            // Navigate back to main - let activity handle navigation
            
            Logger.logUserAction("CHECKOUT_COMPLETED", "Success");
            
        } catch (Exception e) {
            Logger.e("CartController", "Error processing checkout", e);
            android.widget.Toast.makeText(activity, "Đặt hàng thất bại. Vui lòng thử lại.", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Calculate estimated delivery time
     * @return Delivery time string
     */
    private String calculateDeliveryTime() {
        // Simple calculation based on current time
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.MINUTE, 30); // Add 30 minutes
        
        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        return timeFormat.format(calendar.getTime());
    }
    
    /**
     * Get cart summary
     * @return Cart summary string
     */
    public String getCartSummary() {
        return String.format("Giỏ hàng: %d món - %s", 
                cartManager.getCartItemCount(), 
                cartManager.getFormattedFinalTotal());
    }
    
    /**
     * Check if cart is empty
     * @return True if cart is empty
     */
    public boolean isCartEmpty() {
        return cartManager == null || cartManager.isEmpty();
    }
    
    /**
     * Get free shipping info
     * @return Free shipping message
     */
    public String getFreeShippingInfo() {
        if (cartManager == null) {
            return "";
        }
        
        if (cartManager.isFreeShippingEligible()) {
            return "Miễn phí giao hàng";
        } else {
            double remaining = cartManager.getRemainingForFreeShipping();
            return "Thêm " + PriceUtils.formatPrice(remaining) + " để được miễn phí giao hàng";
        }
    }
    
    /**
     * Handle back navigation
     */
    public void handleBackNavigation() {
        Logger.d("CartController", "Handling back navigation");
        Logger.logUserAction("BACK_FROM_CART", "");
        
        // Navigate back - let activity handle navigation
    }
    
    /**
     * Handle navigation to menu
     */
    public void handleNavigateToMenu() {
        Logger.d("CartController", "Navigating to menu");
        Logger.logUserAction("NAVIGATE_TO_MENU", "From cart");
        
        // Navigate to menu - let activity handle navigation
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        Logger.d("CartController", "Cleaning up controller");
        
        // Clear references to prevent memory leaks
        activity = null;
        cartManager = null;
        userManager = null;
    }
}
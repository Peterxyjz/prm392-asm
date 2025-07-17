package com.example.myapplication.wrapper;

import android.content.Context;
import com.example.myapplication.contracts.CartContract;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.FoodItem;
import com.example.myapplication.utils.ValidationUtils;
import com.example.myapplication.utils.PriceUtils;
import java.util.List;

/**
 * Enhanced wrapper around existing CartManager
 * Adds validation and error handling without modifying original code
 * Implements CartContract interface for better structure
 */
public class EnhancedCartManager implements CartContract.CartManager {
    
    private final CartManager originalManager;
    private CartContract.CartView cartView;
    
    /**
     * Constructor
     * @param context Application context
     */
    public EnhancedCartManager(Context context) {
        this.originalManager = CartManager.getInstance(context);
    }
    
    /**
     * Set cart view callback
     * @param cartView Cart view callback
     */
    public void setCartView(CartContract.CartView cartView) {
        this.cartView = cartView;
    }
    
    @Override
    public void addToCart(FoodItem foodItem, int quantity) {
        // Validate inputs
        if (foodItem == null) {
            if (cartView != null) {
                cartView.onCartOperationFailure("Món ăn không hợp lệ");
            }
            return;
        }
        
        ValidationUtils.ValidationResult quantityValidation = ValidationUtils.validateQuantity(quantity);
        if (!quantityValidation.isValid()) {
            if (cartView != null) {
                cartView.onCartOperationFailure(quantityValidation.getMessage());
            }
            return;
        }
        
        // Call original method
        originalManager.addToCart(foodItem, quantity);
        
        // Notify view
        if (cartView != null) {
            CartItem cartItem = originalManager.getCartItem(foodItem.getId());
            if (cartItem != null) {
                cartView.onItemAddedToCart(cartItem);
            }
            cartView.onCartUpdated();
        }
    }
    
    @Override
    public void removeFromCart(int foodItemId) {
        // Validate input
        if (foodItemId <= 0) {
            if (cartView != null) {
                cartView.onCartOperationFailure("ID món ăn không hợp lệ");
            }
            return;
        }
        
        // Call original method
        originalManager.removeFromCart(foodItemId);
        
        // Notify view
        if (cartView != null) {
            cartView.onItemRemovedFromCart(foodItemId);
            cartView.onCartUpdated();
        }
    }
    
    @Override
    public void updateQuantity(int foodItemId, int newQuantity) {
        // Validate inputs
        if (foodItemId <= 0) {
            if (cartView != null) {
                cartView.onCartOperationFailure("ID món ăn không hợp lệ");
            }
            return;
        }
        
        if (newQuantity > 0) {
            ValidationUtils.ValidationResult quantityValidation = ValidationUtils.validateQuantity(newQuantity);
            if (!quantityValidation.isValid()) {
                if (cartView != null) {
                    cartView.onCartOperationFailure(quantityValidation.getMessage());
                }
                return;
            }
        }
        
        // Call original method
        originalManager.updateQuantity(foodItemId, newQuantity);
        
        // Notify view
        if (cartView != null) {
            if (newQuantity <= 0) {
                cartView.onItemRemovedFromCart(foodItemId);
            }
            cartView.onCartUpdated();
        }
    }
    
    @Override
    public List<CartItem> getCartItems() {
        return originalManager.getCartItems();
    }
    
    @Override
    public int getCartItemCount() {
        return originalManager.getCartItemCount();
    }
    
    @Override
    public double getTotalPrice() {
        return originalManager.getTotalPrice();
    }
    
    @Override
    public void clearCart() {
        originalManager.clearCart();
        if (cartView != null) {
            cartView.onCartCleared();
            cartView.onCartUpdated();
        }
    }
    
    @Override
    public CartItem getCartItem(int foodItemId) {
        return originalManager.getCartItem(foodItemId);
    }
    
    @Override
    public boolean isInCart(int foodItemId) {
        return originalManager.isInCart(foodItemId);
    }
    
    /**
     * Additional helper methods
     */
    
    /**
     * Get formatted subtotal
     * @return Formatted subtotal string
     */
    public String getFormattedSubtotal() {
        return PriceUtils.formatPrice(getTotalPrice());
    }
    
    /**
     * Get delivery fee
     * @return Delivery fee amount
     */
    public double getDeliveryFee() {
        return PriceUtils.calculateDeliveryFee(getTotalPrice());
    }
    
    /**
     * Get formatted delivery fee
     * @return Formatted delivery fee string
     */
    public String getFormattedDeliveryFee() {
        return PriceUtils.formatPrice(getDeliveryFee());
    }
    
    /**
     * Get final total including delivery
     * @return Final total amount
     */
    public double getFinalTotal() {
        return PriceUtils.calculateFinalTotal(getTotalPrice());
    }
    
    /**
     * Get formatted final total
     * @return Formatted final total string
     */
    public String getFormattedFinalTotal() {
        return PriceUtils.formatPrice(getFinalTotal());
    }
    
    /**
     * Check if cart is empty
     * @return True if cart is empty
     */
    public boolean isEmpty() {
        return getCartItemCount() == 0;
    }
    
    /**
     * Check if free shipping is eligible
     * @return True if free shipping applies
     */
    public boolean isFreeShippingEligible() {
        return PriceUtils.isFreeShippingEligible(getTotalPrice());
    }
    
    /**
     * Get remaining amount for free shipping
     * @return Amount needed for free shipping, 0 if already eligible
     */
    public double getRemainingForFreeShipping() {
        return PriceUtils.getRemainingForFreeShipping(getTotalPrice());
    }
    
    /**
     * Get formatted remaining amount for free shipping
     * @return Formatted remaining amount string
     */
    public String getFormattedRemainingForFreeShipping() {
        return PriceUtils.formatPrice(getRemainingForFreeShipping());
    }
    
    /**
     * Validate cart for checkout
     * @return ValidationResult with status and message
     */
    public ValidationUtils.ValidationResult validateForCheckout() {
        if (isEmpty()) {
            return new ValidationUtils.ValidationResult(false, "Giỏ hàng trống");
        }
        
        if (getTotalPrice() <= 0) {
            return new ValidationUtils.ValidationResult(false, "Tổng tiền không hợp lệ");
        }
        
        return new ValidationUtils.ValidationResult(true, "Giỏ hàng hợp lệ");
    }
}
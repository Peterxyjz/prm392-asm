package com.example.myapplication.utils;

import android.view.View;
import android.widget.TextView;

/**
 * Utility class for common UI operations
 * Extracted from activities to reduce code duplication
 */
public class UIUtils {
    
    /**
     * Update cart badge with count
     * @param badge TextView showing cart count
     * @param count Number of items in cart
     */
    public static void updateCartBadge(TextView badge, int count) {
        if (badge != null) {
            badge.setText(String.valueOf(count));
            // Always show badge but change visibility based on count if needed
            badge.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Set view visibility safely
     * @param view View to modify
     * @param visible True to show, false to hide
     */
    public static void setViewVisibility(View view, boolean visible) {
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
    
    /**
     * Set text safely to TextView
     * @param textView TextView to update
     * @param text Text to set
     */
    public static void setTextSafely(TextView textView, String text) {
        if (textView != null && text != null) {
            textView.setText(text);
        }
    }
    
    /**
     * Set text safely to TextView with fallback
     * @param textView TextView to update
     * @param text Text to set
     * @param fallback Fallback text if main text is null/empty
     */
    public static void setTextSafely(TextView textView, String text, String fallback) {
        if (textView != null) {
            String displayText = (text != null && !text.trim().isEmpty()) ? text : fallback;
            textView.setText(displayText);
        }
    }
    
    /**
     * Enable/disable view safely
     * @param view View to modify
     * @param enabled True to enable, false to disable
     */
    public static void setViewEnabled(View view, boolean enabled) {
        if (view != null) {
            view.setEnabled(enabled);
        }
    }
    
    /**
     * Format quantity display
     * @param quantity Quantity number
     * @return Formatted quantity string
     */
    public static String formatQuantity(int quantity) {
        return String.valueOf(Math.max(quantity, 0));
    }
    
    /**
     * Get display text for category
     * @param category Category key
     * @return Localized display text
     */
    public static String getCategoryDisplayText(String category) {
        switch (category) {
            case AppConstants.CATEGORY_ALL:
                return "Tất cả";
            case AppConstants.CATEGORY_NOODLES:
                return "Mì";
            case AppConstants.CATEGORY_SUSHI:
                return "Sushi";
            case AppConstants.CATEGORY_RICE:
                return "Cơm";
            case AppConstants.CATEGORY_APPETIZER:
                return "Khai vị";
            default:
                return category;
        }
    }
    
    // Private constructor to prevent instantiation
    private UIUtils() {
        throw new AssertionError("This class should not be instantiated");
    }
}
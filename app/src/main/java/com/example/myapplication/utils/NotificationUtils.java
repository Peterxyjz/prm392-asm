package com.example.myapplication.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.example.myapplication.R;
import com.example.myapplication.activity.CartActivity;
import com.example.myapplication.activity.MenuActivity;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.manager.UserManager;

/**
 * Enhanced NotificationUtils - Utility class for handling app notifications
 * Specifically for cart notifications when app opens
 */
public class NotificationUtils {
    
    private static final String PREFS_NAME = "notification_prefs";
    private static final String KEY_LAST_NOTIFICATION_TIME = "last_notification_time";
    private static final String KEY_NOTIFICATION_ENABLED = "notification_enabled";
    private static final String KEY_LAST_NOTIFIED_USER = "last_notified_user";
    
    // Show notification every 10 minutes maximum (reduced from 30 minutes)
    private static final long NOTIFICATION_COOLDOWN = 10 * 60 * 1000; // 10 minutes
    
    /**
     * Show cart notification dialog if cart has items and conditions are met
     * Enhanced version with better user handling
     * @param context Current context
     * @param cartManager CartManager instance
     */
    public static void showCartNotificationIfNeeded(Context context, CartManager cartManager) {
        try {
            // Check if notifications are enabled
            if (!isNotificationEnabled(context)) {
                Logger.d("NotificationUtils", "Notifications disabled");
                return;
            }
            
            // Get current user
            UserManager userManager = UserManager.getInstance(context);
            if (!userManager.isLoggedIn()) {
                Logger.d("NotificationUtils", "User not logged in");
                return;
            }
            
            String currentUsername = userManager.getCurrentUser().getUsername();
            
            // Check if cart has items
            int cartCount = cartManager.getCartItemCount();
            Logger.d("NotificationUtils", "Cart count: " + cartCount + " for user: " + currentUsername);
            
            if (cartCount <= 0) {
                Logger.d("NotificationUtils", "Cart is empty");
                return;
            }
            
            // Check notification cooldown for this specific user
            if (!shouldShowNotificationForUser(context, currentUsername)) {
                Logger.d("NotificationUtils", "Notification on cooldown for user: " + currentUsername);
                return;
            }
            
            // Show notification dialog
            Logger.d("NotificationUtils", "Showing cart notification for user: " + currentUsername + " with " + cartCount + " items");
            showCartNotificationDialog(context, cartCount, currentUsername);
            
            // Update last notification time for this user
            updateLastNotificationTime(context, currentUsername);
            
        } catch (Exception e) {
            Logger.e("NotificationUtils", "Error showing cart notification", e);
        }
    }
    
    /**
     * Force show cart notification (bypass cooldown) for testing
     * @param context Current context
     * @param cartManager CartManager instance
     */
    public static void forceShowCartNotification(Context context, CartManager cartManager) {
        try {
            UserManager userManager = UserManager.getInstance(context);
            if (!userManager.isLoggedIn()) {
                return;
            }
            
            String currentUsername = userManager.getCurrentUser().getUsername();
            int cartCount = cartManager.getCartItemCount();
            
            if (cartCount > 0) {
                Logger.d("NotificationUtils", "Force showing cart notification for user: " + currentUsername);
                showCartNotificationDialog(context, cartCount, currentUsername);
                updateLastNotificationTime(context, currentUsername);
            }
        } catch (Exception e) {
            Logger.e("NotificationUtils", "Error force showing cart notification", e);
        }
    }
    
    /**
     * Show custom cart notification dialog with Japanese theme
     * @param context Current context
     * @param cartCount Number of items in cart
     * @param username Current username for logging
     */
    private static void showCartNotificationDialog(Context context, int cartCount, String username) {
        try {
            // Create custom dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View customView = LayoutInflater.from(context).inflate(R.layout.dialog_cart_notification, null);
            builder.setView(customView);
            
            // Get dialog views
            TextView tvTitle = customView.findViewById(R.id.tvNotificationTitle);
            TextView tvMessage = customView.findViewById(R.id.tvNotificationMessage);
            Button btnViewCart = customView.findViewById(R.id.btnViewCart);
            Button btnContinueShopping = customView.findViewById(R.id.btnContinueShopping);
            Button btnDismiss = customView.findViewById(R.id.btnDismiss);
            
            // Set content
            tvTitle.setText("🛒 Giỏ hàng của bạn");
            String message = String.format("Xin chào %s!\n\nBạn có %d món trong giỏ hàng.\n" +
                    "Bạn có muốn tiếp tục mua sắm hoặc xem giỏ hàng không?", username, cartCount);
            tvMessage.setText(message);
            
            // Create dialog
            AlertDialog dialog = builder.create();
            dialog.setCancelable(true);
            
            // Set button click listeners
            btnViewCart.setOnClickListener(v -> {
                Logger.logUserAction("CART_NOTIFICATION_VIEW_CART", "User clicked view cart from notification");
                Intent intent = new Intent(context, CartActivity.class);
                context.startActivity(intent);
                dialog.dismiss();
            });
            
            btnContinueShopping.setOnClickListener(v -> {
                Logger.logUserAction("CART_NOTIFICATION_CONTINUE_SHOPPING", "User clicked continue shopping from notification");
                Intent intent = new Intent(context, MenuActivity.class);
                context.startActivity(intent);
                dialog.dismiss();
            });
            
            btnDismiss.setOnClickListener(v -> {
                Logger.logUserAction("CART_NOTIFICATION_DISMISS", "User dismissed cart notification");
                dialog.dismiss();
            });
            
            // Show dialog
            dialog.show();
            
            // Style dialog window
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.circle_background));
            }
            
            Logger.logUserAction("CART_NOTIFICATION_SHOWN", "Cart notification shown to user: " + username + " with " + cartCount + " items");
            
        } catch (Exception e) {
            Logger.e("NotificationUtils", "Error showing cart notification dialog", e);
            // Fallback: show simple dialog
            showSimpleCartReminder(context, cartCount);
        }
    }
    
    /**
     * Check if notification should be shown for specific user based on cooldown
     * @param context Current context
     * @param username Current username
     * @return true if should show notification
     */
    private static boolean shouldShowNotificationForUser(Context context, String username) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Check if this user was the last one to receive notification
        String lastNotifiedUser = prefs.getString(KEY_LAST_NOTIFIED_USER, "");
        long lastNotificationTime = prefs.getLong(KEY_LAST_NOTIFICATION_TIME, 0);
        long currentTime = System.currentTimeMillis();
        
        // If different user, always show (reset cooldown)
        if (!username.equals(lastNotifiedUser)) {
            Logger.d("NotificationUtils", "Different user, showing notification. Last: " + lastNotifiedUser + ", Current: " + username);
            return true;
        }
        
        // Same user - check cooldown
        boolean shouldShow = (currentTime - lastNotificationTime) > NOTIFICATION_COOLDOWN;
        Logger.d("NotificationUtils", "Same user cooldown check. Should show: " + shouldShow + 
                ". Time since last: " + (currentTime - lastNotificationTime) + "ms");
        
        return shouldShow;
    }
    
    /**
     * Update last notification time for specific user
     * @param context Current context
     * @param username Current username
     */
    private static void updateLastNotificationTime(Context context, String username) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putLong(KEY_LAST_NOTIFICATION_TIME, System.currentTimeMillis())
                .putString(KEY_LAST_NOTIFIED_USER, username)
                .apply();
        
        Logger.d("NotificationUtils", "Updated last notification time for user: " + username);
    }
    
    /**
     * Check if cart notifications are enabled
     * @param context Current context
     * @return true if notifications are enabled
     */
    public static boolean isNotificationEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_NOTIFICATION_ENABLED, true); // Default: enabled
    }
    
    /**
     * Enable or disable cart notifications
     * @param context Current context
     * @param enabled true to enable, false to disable
     */
    public static void setNotificationEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply();
        
        Logger.d("NotificationUtils", "Notification enabled set to: " + enabled);
    }
    
    /**
     * Reset notification cooldown (for testing purposes)
     * @param context Current context
     */
    public static void resetNotificationCooldown(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_LAST_NOTIFICATION_TIME)
                .remove(KEY_LAST_NOTIFIED_USER)
                .apply();
        
        Logger.d("NotificationUtils", "Notification cooldown reset");
    }
    
    /**
     * Show simple cart reminder notification (fallback)
     * @param context Current context
     * @param cartCount Number of items in cart
     */
    public static void showSimpleCartReminder(Context context, int cartCount) {
        String title = "🍜 Sakura Restaurant";
        String message = String.format("Bạn có %d món trong giỏ hàng. Đừng quên hoàn tất đơn hàng!", cartCount);
        
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Xem giỏ hàng", (dialog, which) -> {
                    Intent intent = new Intent(context, CartActivity.class);
                    context.startActivity(intent);
                })
                .setNegativeButton("Tiếp tục mua", (dialog, which) -> {
                    Intent intent = new Intent(context, MenuActivity.class);
                    context.startActivity(intent);
                })
                .setNeutralButton("Đóng", null)
                .show();
    }
    
    /**
     * Debug method to check notification status
     * @param context Current context
     */
    public static void debugNotificationStatus(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean enabled = prefs.getBoolean(KEY_NOTIFICATION_ENABLED, true);
            long lastTime = prefs.getLong(KEY_LAST_NOTIFICATION_TIME, 0);
            String lastUser = prefs.getString(KEY_LAST_NOTIFIED_USER, "");
            long currentTime = System.currentTimeMillis();
            long timeSince = currentTime - lastTime;
            
            UserManager userManager = UserManager.getInstance(context);
            String currentUser = userManager.isLoggedIn() ? userManager.getCurrentUser().getUsername() : "none";
            
            CartManager cartManager = CartManager.getInstance(context);
            int cartCount = cartManager.getCartItemCount();
            
            Logger.d("NotificationUtils", "=== NOTIFICATION DEBUG ===");
            Logger.d("NotificationUtils", "Enabled: " + enabled);
            Logger.d("NotificationUtils", "Current user: " + currentUser);
            Logger.d("NotificationUtils", "Cart count: " + cartCount);
            Logger.d("NotificationUtils", "Last notified user: " + lastUser);
            Logger.d("NotificationUtils", "Time since last notification: " + timeSince + "ms");
            Logger.d("NotificationUtils", "Cooldown period: " + NOTIFICATION_COOLDOWN + "ms");
            Logger.d("NotificationUtils", "Should show: " + shouldShowNotificationForUser(context, currentUser));
            Logger.d("NotificationUtils", "=========================");
            
        } catch (Exception e) {
            Logger.e("NotificationUtils", "Error in debug", e);
        }
    }
}

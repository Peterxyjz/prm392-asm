package com.example.myapplication.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.FoodItem;
import com.example.myapplication.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced CartManager với separate cart per user
 * Mỗi user có giỏ hàng riêng biệt
 */
public class CartManager {
    private static final String TAG = "CartManager";
    
    // Constants
    private static final String PREFS_NAME = "cart_prefs";
    private static final String KEY_CART_ITEMS_SUFFIX = "_cart_items"; // Will be: {username}_cart_items

    private SharedPreferences prefs;        // SharedPreferences instance
    private static CartManager instance;    // Singleton instance
    private List<CartItem> cartItems;       // Danh sách món trong giỏ hàng
    private Gson gson;                      // Gson để serialize/deserialize
    private Context context;                // Context để access UserManager
    private String currentUserCart = "";    // Track current user's cart

    /**
     * Private constructor để đảm bảo Singleton pattern
     * @param context Application context
     */
    private CartManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.cartItems = new ArrayList<>();
        loadCartForCurrentUser(); // Load cart cho user hiện tại
    }

    /**
     * Lấy instance duy nhất của CartManager (Singleton pattern)
     * @param context Application context
     * @return CartManager instance
     */
    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Load cart cho user hiện tại
     */
    public void loadCartForCurrentUser() {
        try {
            UserManager userManager = UserManager.getInstance(context);
            if (!userManager.isLoggedIn()) {
                Logger.d(TAG, "No user logged in, clearing cart");
                cartItems.clear();
                currentUserCart = "";
                return;
            }

            String username = userManager.getCurrentUser().getUsername();
            
            // Nếu user đã thay đổi, load cart mới
            if (!username.equals(currentUserCart)) {
                Logger.d(TAG, "User changed from '" + currentUserCart + "' to '" + username + "', loading new cart");
                currentUserCart = username;
                loadCartFromPrefs();
            }
            
        } catch (Exception e) {
            Logger.e(TAG, "Error loading cart for current user", e);
            cartItems.clear();
            currentUserCart = "";
        }
    }

    /**
     * Load danh sách giỏ hàng từ SharedPreferences cho user hiện tại
     */
    private void loadCartFromPrefs() {
        try {
            if (currentUserCart.isEmpty()) {
                cartItems.clear();
                return;
            }

            String cartKey = currentUserCart + KEY_CART_ITEMS_SUFFIX;
            String json = prefs.getString(cartKey, "");
            
            if (!json.isEmpty()) {
                Type type = new TypeToken<List<CartItem>>(){}.getType();
                List<CartItem> loadedItems = gson.fromJson(json, type);
                cartItems = loadedItems != null ? loadedItems : new ArrayList<>();
                Logger.d(TAG, "Loaded " + cartItems.size() + " items for user: " + currentUserCart);
            } else {
                cartItems = new ArrayList<>();
                Logger.d(TAG, "No saved cart found for user: " + currentUserCart);
            }
            
        } catch (Exception e) {
            Logger.e(TAG, "Error loading cart from prefs for user: " + currentUserCart, e);
            cartItems = new ArrayList<>();
        }
    }

    /**
     * Lưu danh sách giỏ hàng vào SharedPreferences cho user hiện tại
     */
    private void saveCartToPrefs() {
        try {
            if (currentUserCart.isEmpty()) {
                Logger.w(TAG, "No current user, cannot save cart");
                return;
            }

            String cartKey = currentUserCart + KEY_CART_ITEMS_SUFFIX;
            String json = gson.toJson(cartItems);
            prefs.edit().putString(cartKey, json).apply();
            
            Logger.d(TAG, "Saved " + cartItems.size() + " items for user: " + currentUserCart);
            
        } catch (Exception e) {
            Logger.e(TAG, "Error saving cart to prefs for user: " + currentUserCart, e);
        }
    }

    /**
     * Thêm món ăn vào giỏ hàng
     */
    public void addToCart(FoodItem foodItem, int quantity) {
        try {
            loadCartForCurrentUser(); // Ensure we have current user's cart
            
            if (currentUserCart.isEmpty()) {
                Logger.w(TAG, "No user logged in, cannot add to cart");
                return;
            }

            // Kiểm tra món đã có trong giỏ chưa
            for (CartItem cartItem : cartItems) {
                if (cartItem.getFoodItem().getId() == foodItem.getId()) {
                    // Món đã có - tăng số lượng
                    cartItem.setQuantity(cartItem.getQuantity() + quantity);
                    saveCartToPrefs();
                    Logger.d(TAG, "Updated quantity for item: " + foodItem.getName() + " for user: " + currentUserCart);
                    return;
                }
            }
            
            // Món chưa có - thêm mới
            cartItems.add(new CartItem(foodItem, quantity));
            saveCartToPrefs();
            Logger.d(TAG, "Added new item: " + foodItem.getName() + " for user: " + currentUserCart);
            
        } catch (Exception e) {
            Logger.e(TAG, "Error adding to cart", e);
        }
    }

    /**
     * Xóa món ăn khỏi giỏ hàng
     */
    public void removeFromCart(int foodItemId) {
        try {
            loadCartForCurrentUser();
            
            if (currentUserCart.isEmpty()) {
                Logger.w(TAG, "No user logged in, cannot remove from cart");
                return;
            }

            cartItems.removeIf(cartItem -> cartItem.getFoodItem().getId() == foodItemId);
            saveCartToPrefs();
            Logger.d(TAG, "Removed item with ID: " + foodItemId + " for user: " + currentUserCart);
            
        } catch (Exception e) {
            Logger.e(TAG, "Error removing from cart", e);
        }
    }

    /**
     * Cập nhật số lượng món ăn trong giỏ hàng
     */
    public void updateQuantity(int foodItemId, int newQuantity) {
        try {
            if (newQuantity <= 0) {
                removeFromCart(foodItemId);
                return;
            }
            
            loadCartForCurrentUser();
            
            if (currentUserCart.isEmpty()) {
                Logger.w(TAG, "No user logged in, cannot update quantity");
                return;
            }
            
            for (CartItem cartItem : cartItems) {
                if (cartItem.getFoodItem().getId() == foodItemId) {
                    cartItem.setQuantity(newQuantity);
                    saveCartToPrefs();
                    Logger.d(TAG, "Updated quantity to " + newQuantity + " for item ID: " + foodItemId + " for user: " + currentUserCart);
                    return;
                }
            }
            
        } catch (Exception e) {
            Logger.e(TAG, "Error updating quantity", e);
        }
    }

    /**
     * Lấy danh sách tất cả món trong giỏ hàng
     */
    public List<CartItem> getCartItems() {
        try {
            loadCartForCurrentUser(); // Always ensure we have current user's cart
            return new ArrayList<>(cartItems);
        } catch (Exception e) {
            Logger.e(TAG, "Error getting cart items", e);
            return new ArrayList<>();
        }
    }

    /**
     * Đếm tổng số lượng tất cả món trong giỏ hàng
     */
    public int getCartItemCount() {
        try {
            loadCartForCurrentUser();
            int count = 0;
            for (CartItem item : cartItems) {
                count += item.getQuantity();
            }
            Logger.d(TAG, "Cart count: " + count + " for user: " + currentUserCart);
            return count;
        } catch (Exception e) {
            Logger.e(TAG, "Error getting cart item count", e);
            return 0;
        }
    }

    /**
     * Tính tổng tiền của tất cả món trong giỏ hàng
     */
    public double getTotalPrice() {
        try {
            loadCartForCurrentUser();
            double total = 0;
            for (CartItem item : cartItems) {
                total += item.getTotalPrice();
            }
            return total;
        } catch (Exception e) {
            Logger.e(TAG, "Error getting total price", e);
            return 0;
        }
    }

    /**
     * Xóa toàn bộ giỏ hàng (sau khi thanh toán thành công)
     */
    public void clearCart() {
        try {
            loadCartForCurrentUser();
            
            if (currentUserCart.isEmpty()) {
                Logger.w(TAG, "No user logged in, cannot clear cart");
                return;
            }

            cartItems.clear();
            saveCartToPrefs();
            Logger.d(TAG, "Cleared cart for user: " + currentUserCart);
            
        } catch (Exception e) {
            Logger.e(TAG, "Error clearing cart", e);
        }
    }

    /**
     * Tìm CartItem theo ID món ăn
     */
    public CartItem getCartItem(int foodItemId) {
        try {
            loadCartForCurrentUser();
            for (CartItem cartItem : cartItems) {
                if (cartItem.getFoodItem().getId() == foodItemId) {
                    return cartItem;
                }
            }
            return null;
        } catch (Exception e) {
            Logger.e(TAG, "Error getting cart item", e);
            return null;
        }
    }

    /**
     * Kiểm tra món ăn có trong giỏ hàng hay không
     */
    public boolean isInCart(int foodItemId) {
        return getCartItem(foodItemId) != null;
    }

    /**
     * Get current user's cart identifier
     */
    public String getCurrentUserCart() {
        return currentUserCart;
    }

    /**
     * Force reload cart for current user (useful after login/logout)
     */
    public void forceReloadCart() {
        Logger.d(TAG, "Force reloading cart");
        loadCartForCurrentUser();
    }

    /**
     * Debug method to check cart status
     */
    public void debugCartStatus() {
        try {
            Logger.d(TAG, "=== CART DEBUG ===");
            Logger.d(TAG, "Current user cart: " + currentUserCart);
            Logger.d(TAG, "Cart items count: " + cartItems.size());
            Logger.d(TAG, "Total items: " + getCartItemCount());
            Logger.d(TAG, "Total price: " + getTotalPrice());
            
            for (int i = 0; i < cartItems.size(); i++) {
                CartItem item = cartItems.get(i);
                Logger.d(TAG, "Item " + i + ": " + item.getFoodItem().getName() + " x" + item.getQuantity());
            }
            Logger.d(TAG, "==================");
            
        } catch (Exception e) {
            Logger.e(TAG, "Error in cart debug", e);
        }
    }

    /**
     * Get cart summary for user
     */
    public String getCartSummary() {
        try {
            loadCartForCurrentUser();
            int itemCount = getCartItemCount();
            double totalPrice = getTotalPrice();
            
            if (itemCount == 0) {
                return "Giỏ hàng trống";
            }
            
            return String.format("Giỏ hàng: %d món - %.0f₫", itemCount, totalPrice);
            
        } catch (Exception e) {
            Logger.e(TAG, "Error getting cart summary", e);
            return "Lỗi giỏ hàng";
        }
    }
}

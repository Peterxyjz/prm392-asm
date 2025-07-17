package com.example.myapplication.controller;

import com.example.myapplication.contracts.CartContract;
import com.example.myapplication.contracts.ViewContracts;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.FoodItem;
import com.example.myapplication.repository.FoodRepository;
import com.example.myapplication.utils.Logger;
import com.example.myapplication.wrapper.EnhancedCartManager;
import java.util.List;

/**
 * Controller class for Menu functionality
 * Extracted from MenuActivity to separate business logic from UI
 * Follows MVC pattern by handling user interactions and data flow
 */
public class MenuController {
    
    private ViewContracts.MenuView menuView;
    private FoodRepository foodRepository;
    private EnhancedCartManager cartManager;
    private String currentCategory = "All";
    
    /**
     * Constructor
     * @param menuView MenuView interface implementation
     */
    public MenuController(ViewContracts.MenuView menuView) {
        this.menuView = menuView;
        this.foodRepository = FoodRepository.getInstance();
        // Note: cartManager needs context, will be set separately
        
        Logger.i("MenuController", "Controller initialized");
    }
    
    /**
     * Set cart manager (needs to be called after constructor)
     * @param cartManager Enhanced cart manager instance
     */
    public void setCartManager(EnhancedCartManager cartManager) {
        this.cartManager = cartManager;
    }
    
    /**
     * Initialize controller
     * Set up initial state and load data
     */
    public void initialize() {
        Logger.d("MenuController", "Initializing menu controller");
        
        // Load initial food items
        loadFoodItems();
        
        // Setup cart manager callback
        if (cartManager != null) {
            cartManager.setCartView(new CartContract.CartView() {
                @Override
                public void onCartUpdated() {
                    updateCartCount();
                }
                
                @Override
                public void onItemAddedToCart(CartItem cartItem) {
                    Logger.logCartOperation("ADD", cartItem.getFoodItem().getId(), cartItem.getQuantity());
                    if (menuView != null) {
                        menuView.refreshAdapter();
                    }
                }
                
                @Override
                public void onItemRemovedFromCart(int foodItemId) {
                    Logger.logCartOperation("REMOVE", foodItemId, 0);
                    if (menuView != null) {
                        menuView.refreshAdapter();
                    }
                }
                
                @Override
                public void onCartCleared() {
                    Logger.logCartOperation("CLEAR", 0, 0);
                    if (menuView != null) {
                        menuView.refreshAdapter();
                    }
                }
                
                @Override
                public void onCartOperationFailure(String errorMessage) {
                    Logger.w("MenuController", "Cart operation failed: " + errorMessage);
                    if (menuView != null) {
                        menuView.showError(errorMessage);
                    }
                }
            });
        }
        
        updateCartCount();
    }
    
    /**
     * Load food items for current category
     */
    public void loadFoodItems() {
        try {
            Logger.d("MenuController", "Loading food items for category: " + currentCategory);
            
            List<FoodItem> items = foodRepository.findByCategory(currentCategory);
            if (menuView != null) {
                menuView.updateFoodItems(items);
            }
            
            Logger.i("MenuController", "Loaded " + items.size() + " food items");
            
        } catch (Exception e) {
            Logger.e("MenuController", "Error loading food items", e);
            if (menuView != null) {
                menuView.showError("Không thể tải danh sách món ăn");
            }
        }
    }
    
    /**
     * Handle category filter selection
     * @param category Selected category
     */
    public void handleCategoryFilter(String category) {
        Logger.d("MenuController", "Filtering by category: " + category);
        
        if (category == null || category.equals(currentCategory)) {
            return;
        }
        
        currentCategory = category;
        loadFoodItems();
        
        // Update UI to reflect selected category
        if (menuView != null) {
            menuView.updateCategoryFilter(category);
        }
        
        Logger.logUserAction("CATEGORY_FILTER", category);
    }
    
    /**
     * Get all available categories
     * @return List of categories
     */
    public List<String> getAllCategories() {
        try {
            return foodRepository.getAllCategories();
        } catch (Exception e) {
            Logger.e("MenuController", "Error getting categories", e);
            return java.util.Arrays.asList("All"); // Fallback
        }
    }
    
    /**
     * Get current category
     * @return Current selected category
     */
    public String getCurrentCategory() {
        return currentCategory;
    }
    
    /**
     * Handle add to cart action
     * @param foodItem Food item to add
     * @param quantity Quantity to add
     */
    public void handleAddToCart(FoodItem foodItem, int quantity) {
        Logger.d("MenuController", "Adding to cart: " + foodItem.getName() + " x" + quantity);
        cartManager.addToCart(foodItem, quantity);
    }
    
    /**
     * Handle remove from cart action
     * @param foodItemId Food item ID to remove
     */
    public void handleRemoveFromCart(int foodItemId) {
        Logger.d("MenuController", "Removing from cart: " + foodItemId);
        cartManager.removeFromCart(foodItemId);
    }
    
    /**
     * Handle quantity update
     * @param foodItemId Food item ID
     * @param newQuantity New quantity
     */
    public void handleQuantityUpdate(int foodItemId, int newQuantity) {
        Logger.d("MenuController", "Updating quantity: " + foodItemId + " to " + newQuantity);
        cartManager.updateQuantity(foodItemId, newQuantity);
    }
    
    /**
     * Get cart manager
     * @return Enhanced cart manager
     */
    public EnhancedCartManager getCartManager() {
        return cartManager;
    }
    
    /**
     * Update cart count display
     */
    public void updateCartCount() {
        if (cartManager != null && menuView != null) {
            int count = cartManager.getCartItemCount();
            menuView.updateCartCount(count);
        }
    }
    
    /**
     * Handle search functionality
     * @param query Search query
     */
    public void handleSearch(String query) {
        try {
            Logger.d("MenuController", "Searching for: " + query);
            
            List<FoodItem> results = foodRepository.searchFoodItems(query);
            if (menuView != null) {
                menuView.updateFoodItems(results);
            }
            
            Logger.i("MenuController", "Search returned " + results.size() + " results");
            Logger.logUserAction("SEARCH", query);
            
        } catch (Exception e) {
            Logger.e("MenuController", "Error searching food items", e);
            if (menuView != null) {
                menuView.showError("Không thể tìm kiếm món ăn");
            }
        }
    }
    
    /**
     * Handle sort by price
     * @param ascending True for ascending, false for descending
     */
    public void handleSortByPrice(boolean ascending) {
        try {
            Logger.d("MenuController", "Sorting by price (ascending: " + ascending + ")");
            
            List<FoodItem> sortedItems = foodRepository.getFoodItemsSortedByPrice(ascending);
            if (menuView != null) {
                menuView.updateFoodItems(sortedItems);
            }
            
            Logger.i("MenuController", "Sorted " + sortedItems.size() + " items by price");
            Logger.logUserAction("SORT_BY_PRICE", ascending ? "ASC" : "DESC");
            
        } catch (Exception e) {
            Logger.e("MenuController", "Error sorting food items", e);
            if (menuView != null) {
                menuView.showError("Không thể sắp xếp món ăn");
            }
        }
    }
    
    /**
     * Handle price range filter
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     */
    public void handlePriceRangeFilter(double minPrice, double maxPrice) {
        try {
            Logger.d("MenuController", "Filtering by price range: " + minPrice + " - " + maxPrice);
            
            List<FoodItem> filteredItems = foodRepository.findByPriceRange(minPrice, maxPrice);
            if (menuView != null) {
                menuView.updateFoodItems(filteredItems);
            }
            
            Logger.i("MenuController", "Price filter returned " + filteredItems.size() + " items");
            Logger.logUserAction("PRICE_FILTER", minPrice + "-" + maxPrice);
            
        } catch (Exception e) {
            Logger.e("MenuController", "Error filtering by price range", e);
            if (menuView != null) {
                menuView.showError("Không thể lọc theo giá");
            }
        }
    }
    
    /**
     * Reset all filters
     */
    public void resetFilters() {
        Logger.d("MenuController", "Resetting all filters");
        
        currentCategory = "All";
        loadFoodItems();
        if (menuView != null) {
            menuView.updateCategoryFilter(currentCategory);
        }
        
        Logger.logUserAction("RESET_FILTERS", "");
    }
    
    /**
     * Get repository statistics
     * @return Statistics string
     */
    public String getStatistics() {
        return foodRepository.getStatistics();
    }
    
    /**
     * Handle navigation to cart
     */
    public void handleNavigateToCart() {
        Logger.d("MenuController", "Navigating to cart");
        Logger.logUserAction("NAVIGATE_TO_CART", "From menu");
        
        if (menuView != null) {
            menuView.navigateToCart();
        }
    }
    
    /**
     * Handle back navigation
     */
    public void handleBackNavigation() {
        Logger.d("MenuController", "Handling back navigation");
        Logger.logUserAction("BACK_FROM_MENU", "");
        
        if (menuView != null) {
            menuView.navigateBack();
        }
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        Logger.d("MenuController", "Cleaning up controller");
        
        // Clear references to prevent memory leaks
        menuView = null;
        foodRepository = null;
        cartManager = null;
    }
}
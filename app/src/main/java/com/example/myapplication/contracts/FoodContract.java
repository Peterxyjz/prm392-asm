package com.example.myapplication.contracts;

import com.example.myapplication.model.FoodItem;
import java.util.List;

/**
 * Contract interface for Food data operations
 * Defines the expected behavior for food-related functionality
 */
public interface FoodContract {
    
    /**
     * Interface for Food Data Manager operations
     */
    interface FoodDataManager {
        
        /**
         * Get all food items
         * @return List of all food items
         */
        List<FoodItem> getAllFoodItems();
        
        /**
         * Get food item by ID
         * @param id Food item ID
         * @return FoodItem if found, null otherwise
         */
        FoodItem getFoodItemById(int id);
        
        /**
         * Get food items by category
         * @param category Category name
         * @return List of food items in category
         */
        List<FoodItem> getFoodItemsByCategory(String category);
        
        /**
         * Get all available categories
         * @return List of category names
         */
        List<String> getAllCategories();
    }
    
    /**
     * Interface for Food-related UI callbacks
     */
    interface FoodView {
        
        /**
         * Called when food items are loaded
         * @param foodItems List of food items
         */
        void onFoodItemsLoaded(List<FoodItem> foodItems);
        
        /**
         * Called when food items are filtered
         * @param filteredItems List of filtered items
         * @param category Selected category
         */
        void onFoodItemsFiltered(List<FoodItem> filteredItems, String category);
        
        /**
         * Called when food data loading fails
         * @param errorMessage Error message
         */
        void onFoodDataLoadFailure(String errorMessage);
    }
    
    /**
     * Interface for Food Repository operations (for future extensibility)
     */
    interface FoodRepository {
        
        /**
         * Load food items from data source
         * @return List of food items
         */
        List<FoodItem> loadFoodItems();
        
        /**
         * Search food items by name
         * @param searchQuery Search query
         * @return List of matching food items
         */
        List<FoodItem> searchFoodItems(String searchQuery);
        
        /**
         * Get food items sorted by price
         * @param ascending True for ascending, false for descending
         * @return List of sorted food items
         */
        List<FoodItem> getFoodItemsSortedByPrice(boolean ascending);
    }
}
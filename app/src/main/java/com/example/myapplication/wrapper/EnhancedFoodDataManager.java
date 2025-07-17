package com.example.myapplication.wrapper;

import com.example.myapplication.contracts.FoodContract;
import com.example.myapplication.manager.FoodDataManager;
import com.example.myapplication.model.FoodItem;
import com.example.myapplication.utils.ValidationUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Enhanced wrapper around existing FoodDataManager
 * Adds additional functionality and implements contracts
 * Provides repository-like interface for future extensibility
 */
public class EnhancedFoodDataManager implements FoodContract.FoodDataManager, FoodContract.FoodRepository {
    
    private FoodContract.FoodView foodView;
    
    /**
     * Set food view callback
     * @param foodView Food view callback
     */
    public void setFoodView(FoodContract.FoodView foodView) {
        this.foodView = foodView;
    }
    
    @Override
    public List<FoodItem> getAllFoodItems() {
        try {
            List<FoodItem> items = FoodDataManager.getAllFoodItems();
            if (foodView != null) {
                foodView.onFoodItemsLoaded(items);
            }
            return items;
        } catch (Exception e) {
            if (foodView != null) {
                foodView.onFoodDataLoadFailure("Không thể tải danh sách món ăn: " + e.getMessage());
            }
            return new ArrayList<>();
        }
    }
    
    @Override
    public FoodItem getFoodItemById(int id) {
        if (id <= 0) {
            return null;
        }
        return FoodDataManager.getFoodItemById(id);
    }
    
    @Override
    public List<FoodItem> getFoodItemsByCategory(String category) {
        if (ValidationUtils.isEmpty(category)) {
            return getAllFoodItems();
        }
        
        try {
            List<FoodItem> items = FoodDataManager.getFoodItemsByCategory(category);
            if (foodView != null) {
                foodView.onFoodItemsFiltered(items, category);
            }
            return items;
        } catch (Exception e) {
            if (foodView != null) {
                foodView.onFoodDataLoadFailure("Không thể lọc món ăn theo danh mục: " + e.getMessage());
            }
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<String> getAllCategories() {
        try {
            return FoodDataManager.getAllCategories();
        } catch (Exception e) {
            if (foodView != null) {
                foodView.onFoodDataLoadFailure("Không thể tải danh mục: " + e.getMessage());
            }
            return new ArrayList<>();
        }
    }
    
    // Repository interface implementations
    
    @Override
    public List<FoodItem> loadFoodItems() {
        return getAllFoodItems();
    }
    
    @Override
    public List<FoodItem> searchFoodItems(String searchQuery) {
        if (ValidationUtils.isEmpty(searchQuery)) {
            return getAllFoodItems();
        }
        
        List<FoodItem> allItems = getAllFoodItems();
        List<FoodItem> searchResults = new ArrayList<>();
        String query = searchQuery.toLowerCase().trim();
        
        for (FoodItem item : allItems) {
            if (item.getName().toLowerCase().contains(query) ||
                item.getDescription().toLowerCase().contains(query) ||
                item.getCategory().toLowerCase().contains(query)) {
                searchResults.add(item);
            }
        }
        
        return searchResults;
    }
    
    @Override
    public List<FoodItem> getFoodItemsSortedByPrice(boolean ascending) {
        List<FoodItem> items = new ArrayList<>(getAllFoodItems());
        
        Collections.sort(items, new Comparator<FoodItem>() {
            @Override
            public int compare(FoodItem item1, FoodItem item2) {
                int result = Double.compare(item1.getPrice(), item2.getPrice());
                return ascending ? result : -result;
            }
        });
        
        return items;
    }
    
    /**
     * Additional helper methods
     */
    
    /**
     * Get food items in price range
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of food items in price range
     */
    public List<FoodItem> getFoodItemsInPriceRange(double minPrice, double maxPrice) {
        List<FoodItem> allItems = getAllFoodItems();
        List<FoodItem> filteredItems = new ArrayList<>();
        
        for (FoodItem item : allItems) {
            if (item.getPrice() >= minPrice && item.getPrice() <= maxPrice) {
                filteredItems.add(item);
            }
        }
        
        return filteredItems;
    }
    
    /**
     * Get cheapest food item
     * @return Cheapest food item or null if no items
     */
    public FoodItem getCheapestFoodItem() {
        List<FoodItem> items = getFoodItemsSortedByPrice(true);
        return items.isEmpty() ? null : items.get(0);
    }
    
    /**
     * Get most expensive food item
     * @return Most expensive food item or null if no items
     */
    public FoodItem getMostExpensiveFoodItem() {
        List<FoodItem> items = getFoodItemsSortedByPrice(false);
        return items.isEmpty() ? null : items.get(0);
    }
    
    /**
     * Get average price of all food items
     * @return Average price
     */
    public double getAveragePrice() {
        List<FoodItem> items = getAllFoodItems();
        if (items.isEmpty()) {
            return 0;
        }
        
        double total = 0;
        for (FoodItem item : items) {
            total += item.getPrice();
        }
        
        return total / items.size();
    }
    
    /**
     * Get food items count by category
     * @param category Category name
     * @return Number of items in category
     */
    public int getFoodItemsCountByCategory(String category) {
        return getFoodItemsByCategory(category).size();
    }
    
    /**
     * Check if food item exists
     * @param id Food item ID
     * @return True if exists, false otherwise
     */
    public boolean foodItemExists(int id) {
        return getFoodItemById(id) != null;
    }
    
    /**
     * Get random food item
     * @return Random food item or null if no items
     */
    public FoodItem getRandomFoodItem() {
        List<FoodItem> items = getAllFoodItems();
        if (items.isEmpty()) {
            return null;
        }
        
        int randomIndex = (int) (Math.random() * items.size());
        return items.get(randomIndex);
    }
    
    /**
     * Get food items by partial name match
     * @param partialName Partial name to search
     * @return List of matching food items
     */
    public List<FoodItem> getFoodItemsByPartialName(String partialName) {
        if (ValidationUtils.isEmpty(partialName)) {
            return new ArrayList<>();
        }
        
        List<FoodItem> allItems = getAllFoodItems();
        List<FoodItem> matches = new ArrayList<>();
        String searchTerm = partialName.toLowerCase().trim();
        
        for (FoodItem item : allItems) {
            if (item.getName().toLowerCase().contains(searchTerm)) {
                matches.add(item);
            }
        }
        
        return matches;
    }
}
package com.example.myapplication.repository;

import com.example.myapplication.contracts.FoodContract;
import com.example.myapplication.manager.FoodDataManager;
import com.example.myapplication.model.FoodItem;
import com.example.myapplication.utils.Logger;
import java.util.List;

/**
 * Repository implementation for Food data
 * Wraps existing FoodDataManager without modifying it
 * Provides abstraction layer for future data source changes
 */
public class FoodRepository implements FoodContract.FoodRepository {
    
    private static FoodRepository instance;
    
    /**
     * Get singleton instance
     * @return FoodRepository instance
     */
    public static synchronized FoodRepository getInstance() {
        if (instance == null) {
            instance = new FoodRepository();
        }
        return instance;
    }
    
    /**
     * Private constructor for singleton
     */
    private FoodRepository() {
        Logger.i("FoodRepository", "Repository initialized");
    }
    
    @Override
    public List<FoodItem> loadFoodItems() {
        try {
            Logger.d("FoodRepository", "Loading all food items");
            List<FoodItem> items = FoodDataManager.getAllFoodItems();
            Logger.i("FoodRepository", "Loaded " + items.size() + " food items");
            return items;
        } catch (Exception e) {
            Logger.e("FoodRepository", "Error loading food items", e);
            throw new RuntimeException("Failed to load food items", e);
        }
    }
    
    @Override
    public List<FoodItem> searchFoodItems(String searchQuery) {
        try {
            Logger.d("FoodRepository", "Searching food items with query: " + searchQuery);
            
            if (searchQuery == null || searchQuery.trim().isEmpty()) {
                return loadFoodItems();
            }
            
            List<FoodItem> allItems = loadFoodItems();
            List<FoodItem> results = new java.util.ArrayList<>();
            String query = searchQuery.toLowerCase().trim();
            
            for (FoodItem item : allItems) {
                if (matchesSearchQuery(item, query)) {
                    results.add(item);
                }
            }
            
            Logger.i("FoodRepository", "Found " + results.size() + " items matching query: " + searchQuery);
            return results;
            
        } catch (Exception e) {
            Logger.e("FoodRepository", "Error searching food items", e);
            throw new RuntimeException("Failed to search food items", e);
        }
    }
    
    @Override
    public List<FoodItem> getFoodItemsSortedByPrice(boolean ascending) {
        try {
            Logger.d("FoodRepository", "Getting food items sorted by price (ascending: " + ascending + ")");
            
            List<FoodItem> items = new java.util.ArrayList<>(loadFoodItems());
            
            java.util.Collections.sort(items, new java.util.Comparator<FoodItem>() {
                @Override
                public int compare(FoodItem item1, FoodItem item2) {
                    int result = Double.compare(item1.getPrice(), item2.getPrice());
                    return ascending ? result : -result;
                }
            });
            
            Logger.i("FoodRepository", "Sorted " + items.size() + " items by price");
            return items;
            
        } catch (Exception e) {
            Logger.e("FoodRepository", "Error sorting food items by price", e);
            throw new RuntimeException("Failed to sort food items by price", e);
        }
    }
    
    /**
     * Additional repository methods
     */
    
    /**
     * Get food item by ID
     * @param id Food item ID
     * @return FoodItem or null if not found
     */
    public FoodItem findById(int id) {
        try {
            Logger.d("FoodRepository", "Finding food item by ID: " + id);
            FoodItem item = FoodDataManager.getFoodItemById(id);
            
            if (item != null) {
                Logger.d("FoodRepository", "Found food item: " + item.getName());
            } else {
                Logger.w("FoodRepository", "Food item not found with ID: " + id);
            }
            
            return item;
        } catch (Exception e) {
            Logger.e("FoodRepository", "Error finding food item by ID: " + id, e);
            return null;
        }
    }
    
    /**
     * Get food items by category
     * @param category Category name
     * @return List of food items in category
     */
    public List<FoodItem> findByCategory(String category) {
        try {
            Logger.d("FoodRepository", "Finding food items by category: " + category);
            
            List<FoodItem> items;
            if ("All".equals(category)) {
                items = loadFoodItems();
            } else {
                items = FoodDataManager.getFoodItemsByCategory(category);
            }
            
            Logger.i("FoodRepository", "Found " + items.size() + " items in category: " + category);
            return items;
            
        } catch (Exception e) {
            Logger.e("FoodRepository", "Error finding food items by category: " + category, e);
            throw new RuntimeException("Failed to find food items by category", e);
        }
    }
    
    /**
     * Get all available categories
     * @return List of category names
     */
    public List<String> getAllCategories() {
        try {
            Logger.d("FoodRepository", "Getting all categories");
            List<String> categories = FoodDataManager.getAllCategories();
            Logger.i("FoodRepository", "Found " + categories.size() + " categories");
            return categories;
        } catch (Exception e) {
            Logger.e("FoodRepository", "Error getting categories", e);
            throw new RuntimeException("Failed to get categories", e);
        }
    }
    
    /**
     * Get food items in price range
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of food items in range
     */
    public List<FoodItem> findByPriceRange(double minPrice, double maxPrice) {
        try {
            Logger.d("FoodRepository", "Finding food items in price range: " + minPrice + " - " + maxPrice);
            
            List<FoodItem> allItems = loadFoodItems();
            List<FoodItem> filteredItems = new java.util.ArrayList<>();
            
            for (FoodItem item : allItems) {
                if (item.getPrice() >= minPrice && item.getPrice() <= maxPrice) {
                    filteredItems.add(item);
                }
            }
            
            Logger.i("FoodRepository", "Found " + filteredItems.size() + " items in price range");
            return filteredItems;
            
        } catch (Exception e) {
            Logger.e("FoodRepository", "Error finding food items by price range", e);
            throw new RuntimeException("Failed to find food items by price range", e);
        }
    }
    
    /**
     * Check if search query matches food item
     * @param item Food item to check
     * @param query Search query (lowercase)
     * @return True if matches
     */
    private boolean matchesSearchQuery(FoodItem item, String query) {
        return item.getName().toLowerCase().contains(query) ||
               item.getDescription().toLowerCase().contains(query) ||
               item.getCategory().toLowerCase().contains(query);
    }
    
    /**
     * Get repository statistics
     * @return Statistics string
     */
    public String getStatistics() {
        try {
            List<FoodItem> items = loadFoodItems();
            List<String> categories = getAllCategories();
            
            double totalPrice = 0;
            double minPrice = Double.MAX_VALUE;
            double maxPrice = Double.MIN_VALUE;
            
            for (FoodItem item : items) {
                totalPrice += item.getPrice();
                minPrice = Math.min(minPrice, item.getPrice());
                maxPrice = Math.max(maxPrice, item.getPrice());
            }
            
            double avgPrice = items.isEmpty() ? 0 : totalPrice / items.size();
            
            return String.format("Total items: %d, Categories: %d, Avg price: %.0f, Min: %.0f, Max: %.0f",
                    items.size(), categories.size() - 1, avgPrice, minPrice, maxPrice);
            
        } catch (Exception e) {
            Logger.e("FoodRepository", "Error getting statistics", e);
            return "Statistics unavailable";
        }
    }
}
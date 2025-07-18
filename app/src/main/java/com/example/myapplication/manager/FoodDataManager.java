package com.example.myapplication.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.myapplication.R;
import com.example.myapplication.model.FoodItem;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manager để quản lý dữ liệu món ăn với persistent storage
 * Sử dụng SharedPreferences với JSON để lưu trữ
 */
public class FoodDataManager {
    private static final String PREF_NAME = "food_data";
    private static final String KEY_FOOD_ITEMS = "food_items";
    private static final String KEY_LAST_ID = "last_id";
    private static final String TAG = "FoodDataManager";
    
    private static List<FoodItem> foodItems;
    private static SharedPreferences sharedPreferences;
    private static Gson gson = new Gson();

    /**
     * Khởi tạo manager với context
     */
    public static void initialize(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            loadFoodItems();
        }
    }

    /**
     * Lấy danh sách tất cả món ăn
     */
    public static List<FoodItem> getAllFoodItems() {
        if (foodItems == null) {
            loadFoodItems();
        }
        return new ArrayList<>(foodItems);
    }

    /**
     * Load dữ liệu món ăn từ SharedPreferences
     * Nếu chưa có dữ liệu, khởi tạo data mặc định
     */
    private static void loadFoodItems() {
        try {
            if (sharedPreferences == null) {
                foodItems = new ArrayList<>();
                return;
            }

            String jsonString = sharedPreferences.getString(KEY_FOOD_ITEMS, null);
            if (jsonString != null) {
                Type listType = new TypeToken<List<FoodItem>>(){}.getType();
                foodItems = gson.fromJson(jsonString, listType);
                if (foodItems == null) {
                    foodItems = new ArrayList<>();
                }
            } else {
                // Nếu chưa có dữ liệu, khởi tạo data mặc định
                initializeDefaultFoodItems();
                saveFoodItems();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading food items: " + e.getMessage(), e);
            initializeDefaultFoodItems();
        }
    }

    /**
     * Lưu danh sách món ăn vào SharedPreferences
     */
    private static void saveFoodItems() {
        try {
            if (sharedPreferences != null && foodItems != null) {
                String jsonString = gson.toJson(foodItems);
                sharedPreferences.edit()
                    .putString(KEY_FOOD_ITEMS, jsonString)
                    .apply();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving food items: " + e.getMessage(), e);
        }
    }

    /**
     * Khởi tạo dữ liệu món ăn mặc định
     */
    private static void initializeDefaultFoodItems() {
        foodItems = new ArrayList<>();
        
        foodItems.add(new FoodItem(1, "Ramen Tonkotsu", 
            "Mì ramen truyền thống với nước dùng xương heo đậm đà, thịt xá xíu và trứng lòng đào", 
            85000, R.drawable.ramen, "Noodles", true, null));
            
        foodItems.add(new FoodItem(2, "Sushi Set", 
            "Set sushi tươi ngon gồm cá hồi, cá ngừ, tôm và tamago", 
            120000, R.drawable.sushi, "Sushi", true, null));
            
        foodItems.add(new FoodItem(3, "Udon Tempura", 
            "Mì udon với tôm tempura giòn rụm và nước dùng dashi thanh mát", 
            75000, R.drawable.udon, "Noodles", true, null));
            
        foodItems.add(new FoodItem(4, "Bento Box", 
            "Hộp cơm Nhật truyền thống với cơm, thịt nướng, tempura và pickles", 
            95000, R.drawable.bento, "Rice", true, null));
            
        foodItems.add(new FoodItem(5, "Cơm Gà Teriyaki", 
            "Cơm trắng với gà nướng teriyaki, rau củ và nước sốt đặc biệt", 
            68000, R.drawable.comga, "Rice", true, null));
            
        foodItems.add(new FoodItem(6, "Cơm Lươn Nhật", 
            "Cơm với lươn nướng kabayaki, rau củ và nước sốt unagi", 
            110000, R.drawable.comluon, "Rice", false, null)); // Mặc định hết món
            
        foodItems.add(new FoodItem(7, "Mandu Gyoza", 
            "Bánh xếp Nhật chiên giòn với nhân thịt heo và rau củ", 
            45000, R.drawable.mandu, "Appetizer", true, null));
    }

    /**
     * Thêm món ăn mới
     */
    public static boolean addFoodItem(String name, String description, double price, 
                                    String category, boolean available, String imageUrl) {
        try {
            if (foodItems == null) {
                loadFoodItems();
            }

            int newId = getNextId();
            FoodItem newItem = new FoodItem(newId, name, description, price, 
                                          R.drawable.ramen, category, available, imageUrl);
            
            foodItems.add(newItem);
            saveFoodItems();
            
            // Cập nhật last ID
            sharedPreferences.edit().putInt(KEY_LAST_ID, newId).apply();
            
            Log.d(TAG, "Added new food item: " + name);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error adding food item: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Cập nhật món ăn
     */
    public static boolean updateFoodItem(int id, String name, String description, double price, 
                                       String category, boolean available, String imageUrl) {
        try {
            if (foodItems == null) {
                loadFoodItems();
            }

            for (FoodItem item : foodItems) {
                if (item.getId() == id) {
                    item.setName(name);
                    item.setDescription(description);
                    item.setPrice(price);
                    item.setCategory(category);
                    item.setAvailable(available);
                    if (imageUrl != null) {
                        item.setImageUrl(imageUrl);
                    }
                    
                    saveFoodItems();
                    Log.d(TAG, "Updated food item: " + name);
                    return true;
                }
            }
            
            Log.w(TAG, "Food item not found for update: " + id);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error updating food item: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Xóa món ăn
     */
    public static boolean deleteFoodItem(int id) {
        try {
            if (foodItems == null) {
                loadFoodItems();
            }

            Iterator<FoodItem> iterator = foodItems.iterator();
            while (iterator.hasNext()) {
                FoodItem item = iterator.next();
                if (item.getId() == id) {
                    iterator.remove();
                    saveFoodItems();
                    Log.d(TAG, "Deleted food item: " + id);
                    return true;
                }
            }
            
            Log.w(TAG, "Food item not found for deletion: " + id);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting food item: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Toggle trạng thái available của món ăn
     */
    public static boolean toggleFoodAvailability(int id) {
        try {
            if (foodItems == null) {
                loadFoodItems();
            }

            for (FoodItem item : foodItems) {
                if (item.getId() == id) {
                    item.setAvailable(!item.isAvailable());
                    saveFoodItems();
                    Log.d(TAG, "Toggled availability for: " + item.getName() + " to " + item.isAvailable());
                    return true;
                }
            }
            
            Log.w(TAG, "Food item not found for toggle: " + id);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error toggling food availability: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Tìm món ăn theo ID
     */
    public static FoodItem getFoodItemById(int id) {
        if (foodItems == null) {
            loadFoodItems();
        }
        
        for (FoodItem item : foodItems) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    /**
     * Lấy danh sách món ăn theo danh mục
     */
    public static List<FoodItem> getFoodItemsByCategory(String category) {
        List<FoodItem> allItems = getAllFoodItems();
        List<FoodItem> filteredItems = new ArrayList<>();
        
        for (FoodItem item : allItems) {
            if (item.getCategory().equals(category)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    /**
     * Lấy danh sách món ăn available (cho customer)
     */
    public static List<FoodItem> getAvailableFoodItems() {
        List<FoodItem> allItems = getAllFoodItems();
        List<FoodItem> availableItems = new ArrayList<>();
        
        for (FoodItem item : allItems) {
            if (item.isAvailable()) {
                availableItems.add(item);
            }
        }
        return availableItems;
    }

    /**
     * Lấy danh sách tất cả danh mục
     */
    public static List<String> getAllCategories() {
        try {
            List<String> categories = new ArrayList<>();
            categories.add("All");
            categories.add("Noodles");
            categories.add("Sushi");
            categories.add("Rice");
            categories.add("Appetizer");
            return categories;
        } catch (Exception e) {
            Log.e(TAG, "Error in getAllCategories: " + e.getMessage(), e);
            List<String> fallback = new ArrayList<>();
            fallback.add("All");
            return fallback;
        }
    }

    /**
     * Lấy ID tiếp theo cho món ăn mới
     */
    private static int getNextId() {
        if (sharedPreferences == null) {
            return 1;
        }
        
        int lastId = sharedPreferences.getInt(KEY_LAST_ID, 0);
        
        // Kiểm tra xem có ID nào bị trùng không
        if (foodItems != null) {
            for (FoodItem item : foodItems) {
                if (item.getId() > lastId) {
                    lastId = item.getId();
                }
            }
        }
        
        return lastId + 1;
    }

    /**
     * Reset dữ liệu về mặc định (cho debug)
     */
    public static void resetToDefault(Context context) {
        if (sharedPreferences == null) {
            initialize(context);
        }
        
        sharedPreferences.edit().clear().apply();
        initializeDefaultFoodItems();
        saveFoodItems();
        Log.d(TAG, "Reset food data to default");
    }
}

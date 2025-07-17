package com.example.myapplication.manager;

import com.example.myapplication.R;
import com.example.myapplication.model.FoodItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Static class quản lý dữ liệu món ăn
 * Cung cấp dữ liệu hard-coded cho ứng dụng demo
 */
public class FoodDataManager {
    private static List<FoodItem> foodItems; // Danh sách tất cả món ăn

    /**
     * Lấy danh sách tất cả món ăn
     * Lazy initialization - chỉ khởi tạo khi được gọi lần đầu
     * @return Copy của danh sách tất cả món ăn
     */
    public static List<FoodItem> getAllFoodItems() {
        try {
            if (foodItems == null) {
                initializeFoodItems();
            }
            return new ArrayList<>(foodItems);
        } catch (Exception e) {
            android.util.Log.e("FoodDataManager", "Error in getAllFoodItems: " + e.getMessage(), e);
            return new ArrayList<>(); // Return empty list instead of null
        }
    }

    /**
     * Khởi tạo danh sách món ăn với dữ liệu hard-coded
     * Sử dụng các hình ảnh có sẵn trong drawable folder
     */
    private static void initializeFoodItems() {
        foodItems = new ArrayList<>();
        
        // Thêm các món ăn với thông tin chi tiết
        foodItems.add(new FoodItem(1, "Ramen Tonkotsu", 
            "Mì ramen truyền thống với nước dùng xương heo đậm đà, thịt xá xíu và trứng lòng đào", 
            85000, R.drawable.ramen, "Noodles"));
            
        foodItems.add(new FoodItem(2, "Sushi Set", 
            "Set sushi tươi ngon gồm cá hồi, cá ngừ, tôm và tamago", 
            120000, R.drawable.sushi, "Sushi"));
            
        foodItems.add(new FoodItem(3, "Udon Tempura", 
            "Mì udon với tôm tempura giòn rụm và nước dùng dashi thanh mát", 
            75000, R.drawable.udon, "Noodles"));
            
        foodItems.add(new FoodItem(4, "Bento Box", 
            "Hộp cơm Nhật truyền thống với cơm, thịt nướng, tempura và pickles", 
            95000, R.drawable.bento, "Rice"));
            
        foodItems.add(new FoodItem(5, "Cơm Gà Teriyaki", 
            "Cơm trắng với gà nướng teriyaki, rau củ và nước sốt đặc biệt", 
            68000, R.drawable.comga, "Rice"));
            
        foodItems.add(new FoodItem(6, "Cơm Lươn Nhật", 
            "Cơm với lươn nướng kabayaki, rau củ và nước sốt unagi", 
            110000, R.drawable.comluon, "Rice"));
            
        foodItems.add(new FoodItem(7, "Mandu Gyoza", 
            "Bánh xếp Nhật chiên giòn với nhân thịt heo và rau củ", 
            45000, R.drawable.mandu, "Appetizer"));
    }

    /**
     * Tìm món ăn theo ID
     * @param id ID của món ăn cần tìm
     * @return FoodItem nếu tìm thấy, null nếu không tìm thấy
     */
    public static FoodItem getFoodItemById(int id) {
        if (foodItems == null) {
            initializeFoodItems();
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
     * @param category Tên danh mục cần lọc
     * @return Danh sách món ăn thuộc danh mục đó
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
     * Lấy danh sách tất cả danh mục có sẵn
     * @return Danh sách tên các danh mục (bao gồm "All")
     */
    public static List<String> getAllCategories() {
        try {
            List<String> categories = new ArrayList<>();
            categories.add("All");        // Danh mục "Tất cả"
            categories.add("Noodles");    // Mì
            categories.add("Sushi");      // Sushi
            categories.add("Rice");       // Cơm
            categories.add("Appetizer");  // Khai vị
            return categories;
        } catch (Exception e) {
            android.util.Log.e("FoodDataManager", "Error in getAllCategories: " + e.getMessage(), e);
            List<String> fallback = new ArrayList<>();
            fallback.add("All");
            return fallback;
        }
    }
    
    /**
     * Create a simple test food item as fallback
     */
    private static FoodItem createTestFoodItem() {
        return new FoodItem(999, "Test Food", "Test description", 50000, R.drawable.ramen, "Noodles");
    }
}
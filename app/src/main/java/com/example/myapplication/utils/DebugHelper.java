package com.example.myapplication.utils;

import android.content.Context;
import android.widget.Toast;
import com.example.myapplication.manager.FoodDataManager;
import com.example.myapplication.model.FoodItem;
import java.util.List;

/**
 * Helper class for debugging purposes
 */
public class DebugHelper {
    
    public static void checkFoodDataIntegrity(Context context) {
        try {
            List<FoodItem> foodItems = FoodDataManager.getAllFoodItems();
            android.util.Log.d("DebugHelper", "Food items count: " + foodItems.size());
            
            for (FoodItem item : foodItems) {
                if (item == null) {
                    android.util.Log.e("DebugHelper", "Found null food item");
                    continue;
                }
                
                android.util.Log.d("DebugHelper", "Food: " + item.getName() + 
                    ", Price: " + item.getPrice() + 
                    ", Image: " + item.getImageResource() + 
                    ", Category: " + item.getCategory());
                    
                // Check if image resource exists
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        context.getResources().getDrawable(item.getImageResource(), null);
                    } else {
                        context.getResources().getDrawable(item.getImageResource());
                    }
                } catch (Exception e) {
                    android.util.Log.e("DebugHelper", "Image resource not found for " + item.getName() + ": " + item.getImageResource());
                }
            }
            
            List<String> categories = FoodDataManager.getAllCategories();
            android.util.Log.d("DebugHelper", "Categories: " + categories.toString());
            
        } catch (Exception e) {
            android.util.Log.e("DebugHelper", "Error in checkFoodDataIntegrity: " + e.getMessage(), e);
            Toast.makeText(context, "Debug error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    public static void logViewState(String activity, String message) {
        android.util.Log.d("ViewState_" + activity, message);
    }
}

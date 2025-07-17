package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.manager.FoodDataManager;
import com.example.myapplication.model.FoodItem;
import java.util.List;

/**
 * Simple Menu Activity for testing
 */
public class SimpleMenuActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            android.util.Log.d("SimpleMenuActivity", "onCreate started");
            
            // Create simple layout programmatically to avoid XML issues
            LinearLayout mainLayout = new LinearLayout(this);
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            mainLayout.setPadding(20, 20, 20, 20);
            
            // Title
            TextView title = new TextView(this);
            title.setText("Simple Menu");
            title.setTextSize(24);
            title.setPadding(0, 0, 0, 20);
            mainLayout.addView(title);
            
            // Test food data
            List<FoodItem> foodItems = FoodDataManager.getAllFoodItems();
            android.util.Log.d("SimpleMenuActivity", "Got " + foodItems.size() + " food items");
            
            TextView countText = new TextView(this);
            countText.setText("Found " + foodItems.size() + " food items");
            countText.setPadding(0, 0, 0, 20);
            mainLayout.addView(countText);
            
            // Add food items as simple text views
            for (FoodItem item : foodItems) {
                if (item != null) {
                    TextView foodView = new TextView(this);
                    foodView.setText(item.getName() + " - " + String.format("%.0f₫", item.getPrice()));
                    foodView.setPadding(0, 10, 0, 10);
                    foodView.setTextSize(16);
                    mainLayout.addView(foodView);
                }
            }
            
            // Back button
            Button backBtn = new Button(this);
            backBtn.setText("Back");
            backBtn.setOnClickListener(v -> finish());
            mainLayout.addView(backBtn);
            
            setContentView(mainLayout);
            android.util.Log.d("SimpleMenuActivity", "onCreate completed successfully");
            
        } catch (Exception e) {
            android.util.Log.e("SimpleMenuActivity", "Error in onCreate: " + e.getMessage(), e);
            
            // Show minimal error layout
            LinearLayout errorLayout = new LinearLayout(this);
            errorLayout.setOrientation(LinearLayout.VERTICAL);
            errorLayout.setPadding(20, 20, 20, 20);
            
            TextView errorText = new TextView(this);
            errorText.setText("Error: " + e.getMessage());
            errorText.setTextSize(16);
            errorLayout.addView(errorText);
            
            Button backBtn = new Button(this);
            backBtn.setText("Back");
            backBtn.setOnClickListener(v -> finish());
            errorLayout.addView(backBtn);
            
            setContentView(errorLayout);
        }
    }
}

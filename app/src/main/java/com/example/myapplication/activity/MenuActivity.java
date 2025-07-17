package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapter.FoodAdapter;
import com.example.myapplication.activity.CartActivity;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.manager.FoodDataManager;
import com.example.myapplication.model.FoodItem;
import java.text.NumberFormat;
import java.util.List;
import com.example.myapplication.utils.DebugHelper;

public class MenuActivity extends AppCompatActivity implements FoodAdapter.OnCartUpdateListener {
    private RecyclerView recyclerViewFood;
    private LinearLayout layoutCategories;
    private TextView tvCartCountMenu;
    private LinearLayout btnCartFromMenu;
    private ImageButton btnBack;

    private FoodAdapter foodAdapter;
    private CartManager cartManager;
    private List<FoodItem> allFoodItems;
    private String selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            android.util.Log.d("MenuActivity", "onCreate started");
            
            setContentView(R.layout.activity_menu);
            DebugHelper.logViewState("MenuActivity", "Layout set successfully");

            initViews();
            DebugHelper.logViewState("MenuActivity", "Views initialized");
            
            cartManager = CartManager.getInstance(this);
            DebugHelper.logViewState("MenuActivity", "CartManager initialized");
            
            // Debug: Check food data integrity after basic setup
            DebugHelper.checkFoodDataIntegrity(this);
            
            allFoodItems = FoodDataManager.getAllFoodItems();
            android.util.Log.d("MenuActivity", "Got " + allFoodItems.size() + " food items");

            setupRecyclerView();
            DebugHelper.logViewState("MenuActivity", "RecyclerView setup completed");
            
            setupCategoryFilters();
            DebugHelper.logViewState("MenuActivity", "Category filters setup completed");
            
            setupClickListeners();
            updateCartCount();
            
            android.util.Log.d("MenuActivity", "onCreate completed successfully");
        } catch (Exception e) {
            android.util.Log.e("MenuActivity", "Error in onCreate: " + e.getMessage(), e);
            // Show error and go back
            android.widget.Toast.makeText(this, "Lỗi khởi tạo menu: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        try {
            recyclerViewFood = findViewById(R.id.recyclerViewMenu);
            layoutCategories = findViewById(R.id.layoutCategories);
            tvCartCountMenu = findViewById(R.id.tvCartCountMenu);
            btnCartFromMenu = findViewById(R.id.btnCartFromMenu);
            btnBack = findViewById(R.id.btnBack);
            
            // Null checks
            if (recyclerViewFood == null) {
                throw new RuntimeException("recyclerViewMenu not found in layout");
            }
            if (layoutCategories == null) {
                throw new RuntimeException("layoutCategories not found in layout");
            }
            if (tvCartCountMenu == null) {
                throw new RuntimeException("tvCartCountMenu not found in layout");
            }
            if (btnCartFromMenu == null) {
                throw new RuntimeException("btnCartFromMenu not found in layout");
            }
            if (btnBack == null) {
                throw new RuntimeException("btnBack not found in layout");
            }
            
            android.util.Log.d("MenuActivity", "All views found successfully");
        } catch (Exception e) {
            android.util.Log.e("MenuActivity", "Error in initViews: " + e.getMessage(), e);
            throw e; // Re-throw to be caught by onCreate
        }
    }

    private void setupRecyclerView() {
        try {
            foodAdapter = new FoodAdapter(allFoodItems, cartManager);
            foodAdapter.setOnCartUpdateListener(this);
            recyclerViewFood.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewFood.setAdapter(foodAdapter);
        } catch (Exception e) {
            android.util.Log.e("MenuActivity", "Error in setupRecyclerView: " + e.getMessage(), e);
            android.widget.Toast.makeText(this, "Lỗi thiết lập danh sách món ăn", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void setupCategoryFilters() {
        try {
            List<String> categories = FoodDataManager.getAllCategories();
            
            for (String category : categories) {
                Button categoryButton = new Button(this);
                categoryButton.setText(category.equals("All") ? "Tất cả" : category);
                categoryButton.setTextSize(12);
                categoryButton.setPadding(32, 16, 32, 16);
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, 
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(8, 0, 8, 0);
                categoryButton.setLayoutParams(params);
                
                if (category.equals(selectedCategory)) {
                    categoryButton.setBackgroundTintList(getColorStateList(R.color.primary_red));
                    categoryButton.setTextColor(getColor(android.R.color.white));
                } else {
                    categoryButton.setBackgroundTintList(getColorStateList(android.R.color.white));
                    categoryButton.setTextColor(getColor(R.color.primary_red));
                }
                
                categoryButton.setOnClickListener(v -> {
                    selectedCategory = category;
                    filterFoodItems(category);
                    updateCategoryButtons();
                });
                
                layoutCategories.addView(categoryButton);
            }
        } catch (Exception e) {
            android.util.Log.e("MenuActivity", "Error in setupCategoryFilters: " + e.getMessage(), e);
            android.widget.Toast.makeText(this, "Lỗi thiết lập bộ lọc danh mục", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCategoryButtons() {
        List<String> categories = FoodDataManager.getAllCategories();
        
        for (int i = 0; i < layoutCategories.getChildCount(); i++) {
            Button button = (Button) layoutCategories.getChildAt(i);
            String category = categories.get(i);
            
            if (category.equals(selectedCategory)) {
                button.setBackgroundTintList(getColorStateList(R.color.primary_red));
                button.setTextColor(getColor(android.R.color.white));
            } else {
                button.setBackgroundTintList(getColorStateList(android.R.color.white));
                button.setTextColor(getColor(R.color.primary_red));
            }
        }
    }

    private void filterFoodItems(String category) {
        List<FoodItem> filteredItems;
        if (category.equals("All")) {
            filteredItems = FoodDataManager.getAllFoodItems();
        } else {
            filteredItems = FoodDataManager.getFoodItemsByCategory(category);
        }
        foodAdapter.updateFoodItems(filteredItems);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnCartFromMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });
    }

    private void updateCartCount() {
        int cartCount = cartManager.getCartItemCount();
        tvCartCountMenu.setText(String.valueOf(cartCount));
        tvCartCountMenu.setVisibility(cartCount > 0 ? View.VISIBLE : View.VISIBLE);
    }

    @Override
    public void onCartUpdated() {
        updateCartCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
        foodAdapter.notifyDataSetChanged(); // Refresh adapter to update cart states
    }
}
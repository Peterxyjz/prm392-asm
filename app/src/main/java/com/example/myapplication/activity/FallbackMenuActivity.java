package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapter.FoodAdapter;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.manager.FoodDataManager;
import com.example.myapplication.model.FoodItem;
import java.util.List;

/**
 * Fallback Menu Activity with simpler layout
 */
public class FallbackMenuActivity extends AppCompatActivity implements FoodAdapter.OnCartUpdateListener {
    private RecyclerView recyclerViewFood;
    private TextView tvCartCount;
    private LinearLayout btnCart;
    
    private FoodAdapter foodAdapter;
    private CartManager cartManager;
    private List<FoodItem> allFoodItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            android.util.Log.d("FallbackMenuActivity", "onCreate started");
            
            // Create layout programmatically to avoid XML issues
            createLayout();
            
            // Initialize managers
            cartManager = CartManager.getInstance(this);
            allFoodItems = FoodDataManager.getAllFoodItems();
            android.util.Log.d("FallbackMenuActivity", "Got " + allFoodItems.size() + " food items");
            
            // Setup RecyclerView
            setupRecyclerView();
            updateCartCount();
            
            android.util.Log.d("FallbackMenuActivity", "onCreate completed successfully");
            
        } catch (Exception e) {
            android.util.Log.e("FallbackMenuActivity", "Error in onCreate: " + e.getMessage(), e);
            showErrorLayout(e.getMessage());
        }
    }
    
    private void createLayout() {
        // Main scroll view
        ScrollView scrollView = new ScrollView(this);
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(20, 20, 20, 20);
        
        // Header
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setPadding(0, 0, 0, 20);
        
        // Title
        TextView title = new TextView(this);
        title.setText("Thực Đơn");
        title.setTextSize(24);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        headerLayout.addView(title);
        
        // Cart button
        btnCart = new LinearLayout(this);
        btnCart.setOrientation(LinearLayout.HORIZONTAL);
        btnCart.setPadding(20, 10, 20, 10);
        btnCart.setBackgroundColor(0xFFE0E0E0);
        
        TextView cartLabel = new TextView(this);
        cartLabel.setText("Giỏ hàng: ");
        btnCart.addView(cartLabel);
        
        tvCartCount = new TextView(this);
        tvCartCount.setText("0");
        tvCartCount.setTextColor(0xFFFF0000);
        btnCart.addView(tvCartCount);
        
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });
        
        headerLayout.addView(btnCart);
        mainLayout.addView(headerLayout);
        
        // Back button
        Button backBtn = new Button(this);
        backBtn.setText("← Quay lại");
        backBtn.setOnClickListener(v -> finish());
        mainLayout.addView(backBtn);
        
        // RecyclerView for food items
        recyclerViewFood = new RecyclerView(this);
        recyclerViewFood.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            0, 1));
        mainLayout.addView(recyclerViewFood);
        
        scrollView.addView(mainLayout);
        setContentView(scrollView);
    }
    
    private void setupRecyclerView() {
        try {
            foodAdapter = new FoodAdapter(allFoodItems, cartManager);
            foodAdapter.setOnCartUpdateListener(this);
            recyclerViewFood.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewFood.setAdapter(foodAdapter);
            android.util.Log.d("FallbackMenuActivity", "RecyclerView setup completed");
        } catch (Exception e) {
            android.util.Log.e("FallbackMenuActivity", "Error in setupRecyclerView: " + e.getMessage(), e);
            
            // Fallback: show food items as simple text views
            LinearLayout parent = (LinearLayout) recyclerViewFood.getParent();
            parent.removeView(recyclerViewFood);
            
            for (FoodItem item : allFoodItems) {
                TextView foodView = new TextView(this);
                foodView.setText(item.getName() + " - " + String.format("%.0f₫", item.getPrice()));
                foodView.setPadding(0, 20, 0, 20);
                foodView.setTextSize(16);
                foodView.setBackgroundColor(0xFFFFFFFF);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 
                    LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 5, 0, 5);
                foodView.setLayoutParams(params);
                parent.addView(foodView);
            }
        }
    }
    
    private void updateCartCount() {
        try {
            int cartCount = cartManager.getCartItemCount();
            tvCartCount.setText(String.valueOf(cartCount));
        } catch (Exception e) {
            android.util.Log.e("FallbackMenuActivity", "Error updating cart count: " + e.getMessage(), e);
            tvCartCount.setText("?");
        }
    }
    
    private void showErrorLayout(String errorMessage) {
        LinearLayout errorLayout = new LinearLayout(this);
        errorLayout.setOrientation(LinearLayout.VERTICAL);
        errorLayout.setPadding(20, 20, 20, 20);
        
        TextView errorText = new TextView(this);
        errorText.setText("Lỗi: " + errorMessage);
        errorText.setTextSize(16);
        errorText.setPadding(0, 0, 0, 20);
        errorLayout.addView(errorText);
        
        Button backBtn = new Button(this);
        backBtn.setText("Quay lại");
        backBtn.setOnClickListener(v -> finish());
        errorLayout.addView(backBtn);
        
        setContentView(errorLayout);
    }
    
    @Override
    public void onCartUpdated() {
        updateCartCount();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
        if (foodAdapter != null) {
            foodAdapter.notifyDataSetChanged();
        }
    }
}

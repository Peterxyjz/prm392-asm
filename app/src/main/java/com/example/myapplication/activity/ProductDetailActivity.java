package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.manager.FoodDataManager;
import com.example.myapplication.model.FoodItem;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * ProductDetailActivity - Màn hình chi tiết sản phẩm
 * FIXED: Added proper error handling, null checks, and crash prevention
 */
public class ProductDetailActivity extends AppCompatActivity {
    
    private static final String TAG = "ProductDetailActivity";
    
    // UI Components
    private ImageView ivFoodImage;
    private TextView tvFoodName, tvFoodDescription, tvFoodPrice, tvFoodCategory;
    private ImageButton btnBack;
    
    // Data
    private FoodItem foodItem;
    private NumberFormat formatter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_product_detail);
            
            // Get food ID from Intent with proper validation
            if (!validateIntent()) {
                return;
            }
            
            // Get food item with null check
            if (!loadFoodItem()) {
                return;
            }
            
            // Initialize components
            initViews();
            initManagers();
            displayFoodInfo();
            setupClickListeners();
            
        } catch (Exception e) {
            showErrorAndFinish("Có lỗi khi tải thông tin món ăn");
        }
    }
    
    /**
     * SOLUTION 2: Validate Intent data before processing
     * Prevents crashes from invalid or missing data
     */
    private boolean validateIntent() {
        try {
            Intent intent = getIntent();
            if (intent == null) {
                showErrorAndFinish("Không thể tải thông tin món ăn");
                return false;
            }
            
            int foodId = intent.getIntExtra("food_id", -1);
            if (foodId == -1) {
                showErrorAndFinish("Món ăn không tồn tại");
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            showErrorAndFinish("Có lỗi khi xử lý dữ liệu");
            return false;
        }
    }
    
    /**
     * SOLUTION 2: Load food item with proper null checking
     * Prevents crashes from missing food items
     */
    private boolean loadFoodItem() {
        try {
            int foodId = getIntent().getIntExtra("food_id", -1);
            foodItem = FoodDataManager.getFoodItemById(foodId);
            
            if (foodItem == null) {
                showErrorAndFinish("Món ăn không tồn tại");
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            showErrorAndFinish("Có lỗi khi tải thông tin món ăn");
            return false;
        }
    }
    
    /**
     * SOLUTION 2: Safe view initialization with null checks
     */
    private void initViews() {
        try {
            ivFoodImage = findViewById(R.id.ivFoodImage);
            tvFoodName = findViewById(R.id.tvFoodName);
            tvFoodDescription = findViewById(R.id.tvFoodDescription);
            tvFoodPrice = findViewById(R.id.tvFoodPrice);
            tvFoodCategory = findViewById(R.id.tvFoodCategory);
            btnBack = findViewById(R.id.btnBack);
            
            // Verify all views are found
            if (ivFoodImage == null || tvFoodName == null || tvFoodDescription == null || 
                tvFoodPrice == null || tvFoodCategory == null || btnBack == null) {
                throw new RuntimeException("One or more views not found in layout");
            }
            
        } catch (Exception e) {
            showErrorAndFinish("Có lỗi khi khởi tạo giao diện");
        }
    }
    
    /**
     * SOLUTION 2: Safe manager initialization
     */
    private void initManagers() {
        try {
            formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            
        } catch (Exception e) {
            // Use fallback formatting if currency formatter fails
            formatter = null;
        }
    }
    
    /**
     * SOLUTION 2: Safe display of food information with error handling
     */
    private void displayFoodInfo() {
        try {
            if (foodItem == null) {
                return;
            }
            
            // Safe image loading
            try {
                ivFoodImage.setImageResource(foodItem.getImageResource());
            } catch (Exception e) {
                ivFoodImage.setImageResource(R.drawable.sushi); // Fallback image
            }
            
            // Safe text setting
            tvFoodName.setText(foodItem.getName() != null ? foodItem.getName() : "Món ăn");
            tvFoodDescription.setText(foodItem.getDescription() != null ? foodItem.getDescription() : "Không có mô tả");
            
            // Safe price formatting
            String priceText;
            if (formatter != null) {
                priceText = formatter.format(foodItem.getPrice()).replace("₫", "₫");
            } else {
                // Fallback price formatting
                priceText = String.format("%.0f₫", foodItem.getPrice());
            }
            tvFoodPrice.setText(priceText);
            
            // Safe category display
            tvFoodCategory.setText("🍜 " + getCategoryName(foodItem.getCategory()));
            
        } catch (Exception e) {
            showErrorAndFinish("Có lỗi khi hiển thị thông tin món ăn");
        }
    }
    
    /**
     * SOLUTION 2: Safe click listener setup
     */
    private void setupClickListeners() {
        try {
            btnBack.setOnClickListener(v -> {
                try {
                    finish();
                } catch (Exception e) {
                    finish(); // Still try to finish
                }
            });
            
        } catch (Exception e) {
            // Non-critical error, continue execution
        }
    }
    
    /**
     * SOLUTION 2: Safe category name conversion
     */
    private String getCategoryName(String category) {
        try {
            if (category == null) {
                return "Khác";
            }
            
            switch (category) {
                case "Noodles": return "Mì";
                case "Sushi": return "Sushi";
                case "Rice": return "Cơm";
                case "Appetizer": return "Khai vị";
                default: return category;
            }
            
        } catch (Exception e) {
            return "Khác";
        }
    }
    
    /**
     * SOLUTION 2: Centralized error handling
     */
    private void showErrorAndFinish(String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Silent fail if toast fails
        } finally {
            finish();
        }
    }
    
    /**
     * SOLUTION 2: Override onBackPressed for safe back navigation
     */
    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (Exception e) {
            finish();
        }
    }
}
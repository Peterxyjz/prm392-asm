package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.activity.CartActivity;
import com.example.myapplication.activity.LoginActivity;
import com.example.myapplication.activity.MenuActivity;
import com.example.myapplication.activity.ProfileActivity;
import com.example.myapplication.activity.BillHistoryActivity;
import com.example.myapplication.activity.MapActivity;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.manager.UserManager;
import com.example.myapplication.manager.BillManager;
import com.example.myapplication.manager.FoodDataManager;
import com.example.myapplication.model.User;
import com.example.myapplication.utils.NotificationUtils;

/**
 * MainActivity - Trang chủ của ứng dụng
 * Hiển thị thông tin nhà hàng, số lượng giỏ hàng và các nút điều hướng chính
 */
public class MainActivity extends AppCompatActivity {
    // Khai báo các view components
    private TextView tvWelcome, tvCartCount;
    private Button btnViewMenu;
    private LinearLayout btnCart, btnBillHistory, btnMap;
    private ImageButton btnProfile;
    
    // Managers để quản lý dữ liệu
    private UserManager userManager;
    private CartManager cartManager;

    /**
     * Hàm được gọi khi Activity được tạo
     * Kiểm tra trạng thái đăng nhập và khởi tạo giao diện
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Khởi tạo UserManager để kiểm tra trạng thái đăng nhập
        userManager = UserManager.getInstance(this);
        
        // Khởi tạo FoodDataManager - quan trọng để load data món ăn
        FoodDataManager.initialize(this);
        
        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (!userManager.isLoggedIn()) {
            // Chưa đăng nhập -> chuyển đến LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Đóng MainActivity để không quay lại được
            return;
        }
        
        // FIXED: Kiểm tra role của user để chuyển hướng đúng
        if (userManager.isCurrentUserOwner()) {
            // User là Owner -> chuyển đến OwnerDashboardActivity
            Intent intent = new Intent(this, com.example.myapplication.activity.owner.OwnerDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }
        
        // Đã đăng nhập -> thiết lập giao diện
        setContentView(R.layout.activity_main);
        
        initViews();                    // Khởi tạo các view
        cartManager = CartManager.getInstance(this); // Khởi tạo CartManager
        cartManager.forceReloadCart();  // Force reload cart cho user hiện tại
        
        setupClickListeners();         // Thiết lập sự kiện click
        updateWelcomeMessage();        // Cập nhật lời chào
        updateCartCount();             // Cập nhật số lượng giỏ hàng
        
        // Show cart notification with delay to ensure everything is loaded
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing() && !isDestroyed()) {
                NotificationUtils.showCartNotificationIfNeeded(this, cartManager);
            }
        }, 1000); // 1 second delay
    }

    /**
     * Khởi tạo và liên kết các view components với layout
     */
    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvCartCount = findViewById(R.id.tvCartCount);
        btnViewMenu = findViewById(R.id.btnViewMenu);
        btnCart = findViewById(R.id.btnCart);
        btnBillHistory = findViewById(R.id.btnBillHistory);
        btnMap = findViewById(R.id.btnMap);
        btnProfile = findViewById(R.id.btnProfile);
    }

    /**
     * Thiết lập sự kiện click cho các nút
     */
    private void setupClickListeners() {
        // Nút xem thực đơn -> chuyển đến MenuActivity
        btnViewMenu.setOnClickListener(v -> {
            try {
                android.util.Log.d("MainActivity", "Opening MenuActivity");
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                android.util.Log.e("MainActivity", "Error opening MenuActivity: " + e.getMessage(), e);
                android.widget.Toast.makeText(this, "Lỗi mở menu: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                
                // Try FallbackMenuActivity as backup
                try {
                    Intent fallbackIntent = new Intent(this, com.example.myapplication.activity.FallbackMenuActivity.class);
                    startActivity(fallbackIntent);
                } catch (Exception e2) {
                    android.util.Log.e("MainActivity", "FallbackMenuActivity also failed: " + e2.getMessage(), e2);
                    
                    // Try SimpleMenuActivity as backup
                    try {
                        Intent simpleIntent = new Intent(this, com.example.myapplication.activity.SimpleMenuActivity.class);
                        startActivity(simpleIntent);
                    } catch (Exception e3) {
                        android.util.Log.e("MainActivity", "SimpleMenuActivity also failed: " + e3.getMessage(), e3);
                        
                        // Open debug activity as last resort
                        try {
                            Intent debugIntent = new Intent(this, com.example.myapplication.activity.DebugMenuActivity.class);
                            startActivity(debugIntent);
                        } catch (Exception e4) {
                            android.util.Log.e("MainActivity", "Even debug activity failed: " + e4.getMessage(), e4);
                        }
                    }
                }
            }
        });

        // Nút giỏ hàng -> chuyển đến CartActivity
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });

        // Nút lịch sử đơn hàng -> chuyển đến BillHistoryActivity
        btnBillHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, BillHistoryActivity.class);
            startActivity(intent);
        });

        // Nút vị trí nhà hàng -> chuyển đến MapActivity (external Google Maps)
        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        });

        // Nút profile -> chuyển đến ProfileActivity
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
        
        // Long click on profile for debug features
        btnProfile.setOnLongClickListener(v -> {
            android.widget.Toast.makeText(this, "🔔 Debug Menu & Data", android.widget.Toast.LENGTH_SHORT).show();
            
            // Debug cart status
            cartManager.debugCartStatus();
            
            // Debug bills status
            BillManager billManager = BillManager.getInstance(this);
            billManager.debugBillsStatus();
            
            // Debug food data status
            android.util.Log.d("MainActivity", "Total food items: " + FoodDataManager.getAllFoodItems().size());
            android.util.Log.d("MainActivity", "Available food items: " + FoodDataManager.getAvailableFoodItems().size());
            
            // Debug notification status
            NotificationUtils.debugNotificationStatus(this);
            NotificationUtils.resetNotificationCooldown(this);
            NotificationUtils.forceShowCartNotification(this, cartManager);
            return true;
        });
    }

    /**
     * Cập nhật lời chào dựa trên thông tin người dùng hiện tại
     * Hiển thị tên đầy đủ nếu có, nếu không thì hiển thị username
     */
    private void updateWelcomeMessage() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            String welcomeText = "Chào " + 
                (currentUser.getFullName().isEmpty() ? currentUser.getUsername() : currentUser.getFullName());
            tvWelcome.setText(welcomeText);
        }
    }

    /**
     * Cập nhật số lượng món trong giỏ hàng hiển thị trên badge
     * Luôn hiển thị badge (kể cả khi = 0) để người dùng biết có chức năng giỏ hàng
     */
    private void updateCartCount() {
        int cartCount = cartManager.getCartItemCount();
        tvCartCount.setText(String.valueOf(cartCount));
        tvCartCount.setVisibility(cartCount > 0 ? View.VISIBLE : View.VISIBLE);
    }

    /**
     * Hàm được gọi khi Activity trở lại foreground
     * Cập nhật lại thông tin giỏ hàng và lời chào (có thể đã thay đổi từ activity khác)
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();      // Cập nhật số lượng giỏ hàng
        updateWelcomeMessage(); // Cập nhật lời chào (có thể user đã update profile)
    }
}

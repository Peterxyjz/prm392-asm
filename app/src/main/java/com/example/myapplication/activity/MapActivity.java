package com.example.myapplication.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.utils.AppConstants;

/**
 * MapActivity - Hiển thị thông tin vị trí nhà hàng Sakura Restaurant
 * Cho phép mở Google Maps để xem chỉ đường
 */
public class MapActivity extends AppCompatActivity {
    
    // UI Components
    private ImageButton btnBack;
    private TextView tvRestaurantName, tvRestaurantAddress, tvRestaurantPhone, tvRestaurantHours;
    private Button btnOpenMaps, btnCallRestaurant, btnGetDirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        initViews();
        setupClickListeners();
        loadRestaurantInfo();
    }

    /**
     * Khởi tạo các view components
     */
    private void initViews() {
        btnBack = findViewById(R.id.btnBackFromMap);
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvRestaurantAddress = findViewById(R.id.tvRestaurantAddress);
        tvRestaurantPhone = findViewById(R.id.tvRestaurantPhone);
        tvRestaurantHours = findViewById(R.id.tvRestaurantHours);
        btnOpenMaps = findViewById(R.id.btnOpenMaps);
        btnCallRestaurant = findViewById(R.id.btnCallRestaurant);
        btnGetDirections = findViewById(R.id.btnGetDirections);
    }

    /**
     * Thiết lập sự kiện click cho các nút
     */
    private void setupClickListeners() {
        // Nút Back
        btnBack.setOnClickListener(v -> finish());
        
        // Nút mở Google Maps
        btnOpenMaps.setOnClickListener(v -> openGoogleMaps());
        
        // Nút gọi điện thoại
        btnCallRestaurant.setOnClickListener(v -> callRestaurant());
        
        // Nút chỉ đường
        btnGetDirections.setOnClickListener(v -> getDirections());
    }

    /**
     * Load thông tin nhà hàng vào UI
     */
    private void loadRestaurantInfo() {
        tvRestaurantName.setText(AppConstants.RESTAURANT_NAME);
        tvRestaurantAddress.setText(AppConstants.RESTAURANT_ADDRESS);
        tvRestaurantPhone.setText(AppConstants.RESTAURANT_PHONE);
        tvRestaurantHours.setText(AppConstants.RESTAURANT_HOURS);
    }

    /**
     * Mở Google Maps để hiển thị vị trí nhà hàng
     */
    private void openGoogleMaps() {
        try {
            // Tạo URI cho Google Maps với tọa độ và tên địa điểm
            String uri = String.format("geo:%f,%f?q=%f,%f(%s)", 
                AppConstants.RESTAURANT_LAT, AppConstants.RESTAURANT_LNG, 
                AppConstants.RESTAURANT_LAT, AppConstants.RESTAURANT_LNG, 
                Uri.encode(AppConstants.RESTAURANT_NAME));
            
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps"); // Ưu tiên Google Maps
            
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Fallback: mở trong trình duyệt nếu không có Google Maps
                openMapsInBrowser();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở bản đồ", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Mở Google Maps trong trình duyệt web
     */
    private void openMapsInBrowser() {
        try {
            String url = String.format("https://www.google.com/maps/search/?api=1&query=%f,%f", 
                AppConstants.RESTAURANT_LAT, AppConstants.RESTAURANT_LNG);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở bản đồ", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gọi điện thoại đến nhà hàng
     */
    private void callRestaurant() {
        try {
            String phoneNumber = AppConstants.RESTAURANT_PHONE.replaceAll("[^0-9+]", ""); // Chỉ giữ số và dấu +
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể gọi điện", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Mở Google Maps với chỉ đường từ vị trí hiện tại đến nhà hàng
     */
    private void getDirections() {
        try {
            // URI cho chỉ đường Google Maps
            String uri = String.format("google.navigation:q=%f,%f", AppConstants.RESTAURANT_LAT, AppConstants.RESTAURANT_LNG);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Fallback: mở directions trong trình duyệt
                String url = String.format("https://www.google.com/maps/dir/?api=1&destination=%f,%f", 
                    AppConstants.RESTAURANT_LAT, AppConstants.RESTAURANT_LNG);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Không thể lấy chỉ đường", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Static method để mở MapActivity từ activity khác
     */
    public static void start(android.content.Context context) {
        Intent intent = new Intent(context, MapActivity.class);
        context.startActivity(intent);
    }
}

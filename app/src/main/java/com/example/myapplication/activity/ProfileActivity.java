package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.manager.UserManager;
import com.example.myapplication.model.User;
import com.example.myapplication.utils.NotificationUtils;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.widget.SwitchCompat;

/**
 * ProfileActivity - Màn hình quản lý thông tin cá nhân
 * Đã cập nhật: không còn password field
 */
public class ProfileActivity extends AppCompatActivity {
    private TextInputEditText etProfileUsername, etProfileFullName, etProfilePhone, etProfileAddress;
    private Button btnUpdateProfile, btnLogout;
    private ImageButton btnBackFromProfile;
    private SwitchCompat switchNotifications;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        userManager = UserManager.getInstance(this);
        
        loadUserInfo();
        setupClickListeners();
    }

    /**
     * Khởi tạo và liên kết các view components với layout
     */
    private void initViews() {
        etProfileUsername = findViewById(R.id.etProfileUsername);
        etProfileFullName = findViewById(R.id.etProfileFullName);
        etProfilePhone = findViewById(R.id.etProfilePhone);
        etProfileAddress = findViewById(R.id.etProfileAddress);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnLogout = findViewById(R.id.btnLogout);
        btnBackFromProfile = findViewById(R.id.btnBackFromProfile);
        switchNotifications = findViewById(R.id.switchNotifications);
    }

    /**
     * Load thông tin người dùng hiện tại vào các EditText
     */
    private void loadUserInfo() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            etProfileUsername.setText(currentUser.getUsername());
            etProfileFullName.setText(currentUser.getFullName());
            etProfilePhone.setText(currentUser.getPhone());
            etProfileAddress.setText(currentUser.getAddress());
        }
        
        // Load notification setting
        boolean isNotificationEnabled = NotificationUtils.isNotificationEnabled(this);
        switchNotifications.setChecked(isNotificationEnabled);
    }

    /**
     * Thiết lập sự kiện click cho các nút
     */
    private void setupClickListeners() {
        btnBackFromProfile.setOnClickListener(v -> finish());
        
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
        
        btnLogout.setOnClickListener(v -> showLogoutDialog());
        
        // Notification switch listener
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NotificationUtils.setNotificationEnabled(this, isChecked);
            String message = isChecked ? "Đã bật thông báo giỏ hàng" : "Đã tắt thông báo giỏ hàng";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Cập nhật thông tin profile của người dùng
     * Validate input và gọi UserManager để lưu
     */
    private void updateProfile() {
        String fullName = etProfileFullName.getText().toString().trim();
        String phone = etProfilePhone.getText().toString().trim();
        String address = etProfileAddress.getText().toString().trim();

        // Validate thông tin bắt buộc
        if (fullName.isEmpty()) {
            etProfileFullName.setError("Vui lòng nhập họ tên");
            return;
        }

        if (address.isEmpty()) {
            etProfileAddress.setError("Vui lòng nhập địa chỉ");
            return;
        }

        // Cập nhật thông tin
        userManager.updateUserInfo(fullName, address, phone);
        Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Hiển thị dialog xác nhận đăng xuất
     */
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
            .setPositiveButton("Đăng xuất", (dialog, which) -> {
                userManager.logout();
                Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                
                // Navigate to login và clear tất cả activity khác
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
}
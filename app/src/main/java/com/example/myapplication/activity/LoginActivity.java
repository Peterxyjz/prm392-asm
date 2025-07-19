package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.manager.UserManager;

/**
 * Enhanced LoginActivity với support cho Sign Up/Login
 */
public class LoginActivity extends AppCompatActivity {
    
    // UI Components
    private TextView btnTabLogin, btnTabSignup;
    private LinearLayout layoutLogin, layoutSignup;
    private TextView tvMessage;
    
    // Login Form
    private EditText etLoginUsername, etLoginPassword;
    private Button btnLogin, btnQuickLogin;
    
    // Signup Form
    private EditText etSignupUsername, etSignupEmail, etSignupPassword, 
                     etSignupFullName, etSignupPhone;
    private Button btnSignup;
    
    // Manager
    private UserManager userManager;
    
    // Current mode
    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Khởi tạo UserManager
        userManager = UserManager.getInstance(this);
        
        // Kiểm tra đã đăng nhập chưa
        if (userManager.isLoggedIn()) {
            // FIXED: Kiểm tra role để chuyển hướng đúng
            if (userManager.isCurrentUserOwner()) {
                navigateToOwnerDashboard();
            } else {
                navigateToMain();
            }
            return;
        }
        
        setContentView(R.layout.activity_login);
        
        initViews();
        setupTabSwitching();
        setupLoginForm();
        setupSignupForm();
    }

    /**
     * Khởi tạo các view components
     */
    private void initViews() {
        // Tab buttons
        btnTabLogin = findViewById(R.id.btnTabLogin);
        btnTabSignup = findViewById(R.id.btnTabSignup);
        
        // Layouts
        layoutLogin = findViewById(R.id.layoutLogin);
        layoutSignup = findViewById(R.id.layoutSignup);
        
        // Message
        tvMessage = findViewById(R.id.tvMessage);
        
        // Login form
        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnQuickLogin = findViewById(R.id.btnQuickLogin);
        
        // Signup form
        etSignupUsername = findViewById(R.id.etSignupUsername);
        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        etSignupFullName = findViewById(R.id.etSignupFullName);
        etSignupPhone = findViewById(R.id.etSignupPhone);
        btnSignup = findViewById(R.id.btnSignup);
    }

    /**
     * Setup tab switching between Login and Signup
     */
    private void setupTabSwitching() {
        btnTabLogin.setOnClickListener(v -> switchToLogin());
        btnTabSignup.setOnClickListener(v -> switchToSignup());
    }

    /**
     * Switch to Login tab
     */
    private void switchToLogin() {
        isLoginMode = true;
        
        // Update tab buttons - using TextView styling
        btnTabLogin.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button_filled));
        btnTabLogin.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        
        btnTabSignup.setBackground(ContextCompat.getDrawable(this, android.R.color.transparent));
        btnTabSignup.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        
        // Update layouts
        layoutLogin.setVisibility(View.VISIBLE);
        layoutSignup.setVisibility(View.GONE);
        
        hideMessage();
    }

    /**
     * Switch to Signup tab
     */
    private void switchToSignup() {
        isLoginMode = false;
        
        // Update tab buttons - using TextView styling
        btnTabSignup.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button_filled));
        btnTabSignup.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        
        btnTabLogin.setBackground(ContextCompat.getDrawable(this, android.R.color.transparent));
        btnTabLogin.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        
        // Update layouts
        layoutLogin.setVisibility(View.GONE);
        layoutSignup.setVisibility(View.VISIBLE);
        
        hideMessage();
    }

    /**
     * Setup Login form events
     */
    private void setupLoginForm() {
        btnLogin.setOnClickListener(v -> performLogin());
        btnQuickLogin.setOnClickListener(v -> showQuickLoginDialog());
    }

    /**
     * Setup Signup form events
     */
    private void setupSignupForm() {
        btnSignup.setOnClickListener(v -> performSignup());
    }

    /**
     * Thực hiện đăng nhập với email/username và password
     */
    private void performLogin() {
        String usernameOrEmail = etLoginUsername.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();
        
        if (usernameOrEmail.isEmpty()) {
            showErrorMessage("Vui lòng nhập tên đăng nhập hoặc email");
            return;
        }
        
        if (password.isEmpty()) {
            showErrorMessage("Vui lòng nhập mật khẩu");
            return;
        }
        
        // Thực hiện đăng nhập
        UserManager.LoginResult result = userManager.login(usernameOrEmail, password);
        
        if (result.isSuccess()) {
            showSuccessMessage(result.getMessage());
            
            // Kiểm tra role và chuyển đến Activity tương ứng
            if (userManager.isCurrentUserOwner()) {
                navigateToOwnerDashboard();
            } else {
                navigateToMain();
            }
        } else {
            showErrorMessage(result.getMessage());
        }
    }

    /**
     * Thực hiện đăng ký tài khoản mới
     */
    private void performSignup() {
        String username = etSignupUsername.getText().toString().trim();
        String email = etSignupEmail.getText().toString().trim();
        String password = etSignupPassword.getText().toString().trim();
        String fullName = etSignupFullName.getText().toString().trim();
        String phone = etSignupPhone.getText().toString().trim();
        
        // Thực hiện đăng ký
        UserManager.SignUpResult result = userManager.signUp(username, email, password, fullName, phone);
        
        if (result.isSuccess()) {
            showSuccessMessage(result.getMessage());
            navigateToMain();
        } else {
            showErrorMessage(result.getMessage());
        }
    }

    /**
     * Hiển thị dialog đăng nhập nhanh (backward compatibility)
     */
    private void showQuickLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đăng nhập nhanh");
        builder.setMessage("Chỉ cần nhập tên để đăng nhập/tạo tài khoản mới");
        
        EditText editText = new EditText(this);
        editText.setHint("Nhập tên của bạn");
        builder.setView(editText);
        
        builder.setPositiveButton("Đăng nhập", (dialog, which) -> {
            String username = editText.getText().toString().trim();
            if (!username.isEmpty()) {
                boolean success = userManager.login(username);
                if (success) {
                    showSuccessMessage("Đăng nhập thành công!");
                    // FIXED: Kiểm tra role sau khi đăng nhập
                    if (userManager.isCurrentUserOwner()) {
                        navigateToOwnerDashboard();
                    } else {
                        navigateToMain();
                    }
                } else {
                    showErrorMessage("Đăng nhập thất bại");
                }
            } else {
                showErrorMessage("Vui lòng nhập tên");
            }
        });
        
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    /**
     * Hiển thị thông báo lỗi
     */
    private void showErrorMessage(String message) {
        tvMessage.setText(message);
        tvMessage.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        tvMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Hiển thị thông báo thành công
     */
    private void showSuccessMessage(String message) {
        tvMessage.setText(message);
        tvMessage.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        tvMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Ẩn thông báo
     */
    private void hideMessage() {
        tvMessage.setVisibility(View.GONE);
    }

    /**
     * Xóa nội dung các form
     */
    private void clearForms() {
        // Clear login form
        etLoginUsername.setText("");
        etLoginPassword.setText("");
        
        // Clear signup form
        etSignupUsername.setText("");
        etSignupEmail.setText("");
        etSignupPassword.setText("");
        etSignupFullName.setText("");
        etSignupPhone.setText("");
    }

    /**
     * Chuyển đến MainActivity
     */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    /**
     * Chuyển đến Owner Dashboard
     */
    private void navigateToOwnerDashboard() {
        try {
            Intent intent = new Intent(this, com.example.myapplication.activity.owner.OwnerDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            showErrorMessage("Lỗi không tìm thấy Owner Dashboard: " + e.getMessage());
        }
    }

    /**
     * Xử lý nút Back
     */
    @Override
    public void onBackPressed() {
        // Hiển thị dialog confirm thoát
        new AlertDialog.Builder(this)
            .setTitle("Thoát ứng dụng")
            .setMessage("Bạn có muốn thoát ứng dụng không?")
            .setPositiveButton("Thoát", (dialog, which) -> {
                super.onBackPressed();
                finishAffinity(); // Đóng toàn bộ app
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
}
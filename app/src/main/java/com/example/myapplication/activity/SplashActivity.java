package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.manager.UserManager;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.utils.NotificationUtils;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            UserManager userManager = UserManager.getInstance(this);
            
            Intent intent;
            if (userManager.isLoggedIn()) {
                // FIXED: Kiểm tra role để chuyển hướng đúng
                if (userManager.isCurrentUserOwner()) {
                    // Owner -> chuyển đến OwnerDashboardActivity
                    intent = new Intent(SplashActivity.this, com.example.myapplication.activity.owner.OwnerDashboardActivity.class);
                } else {
                    // Customer -> chuyển đến MainActivity
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
            } else {
                // Chưa đăng nhập -> chuyển đến LoginActivity
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
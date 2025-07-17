package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.utils.DebugHelper;

/**
 * Debug Activity to test MenuActivity launch
 */
public class DebugMenuActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create simple layout programmatically
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        
        TextView title = new TextView(this);
        title.setText("Debug Menu Test");
        title.setTextSize(20);
        title.setPadding(0, 0, 0, 30);
        layout.addView(title);
        
        Button testDataBtn = new Button(this);
        testDataBtn.setText("Test Food Data");
        testDataBtn.setOnClickListener(v -> {
            DebugHelper.checkFoodDataIntegrity(this);
            android.widget.Toast.makeText(this, "Check logcat for results", android.widget.Toast.LENGTH_SHORT).show();
        });
        layout.addView(testDataBtn);
        
        Button openMenuBtn = new Button(this);
        openMenuBtn.setText("Open Menu Activity");
        openMenuBtn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                android.util.Log.e("DebugMenuActivity", "Error opening MenuActivity: " + e.getMessage(), e);
                android.widget.Toast.makeText(this, "Error: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            }
        });
        layout.addView(openMenuBtn);
        
        Button openSimpleMenuBtn = new Button(this);
        openSimpleMenuBtn.setText("Open Simple Menu");
        openSimpleMenuBtn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, SimpleMenuActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                android.util.Log.e("DebugMenuActivity", "Error opening SimpleMenuActivity: " + e.getMessage(), e);
                android.widget.Toast.makeText(this, "Error: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            }
        });
        layout.addView(openSimpleMenuBtn);
        
        Button openFallbackMenuBtn = new Button(this);
        openFallbackMenuBtn.setText("Open Fallback Menu");
        openFallbackMenuBtn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, FallbackMenuActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                android.util.Log.e("DebugMenuActivity", "Error opening FallbackMenuActivity: " + e.getMessage(), e);
                android.widget.Toast.makeText(this, "Error: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            }
        });
        layout.addView(openFallbackMenuBtn);
        
        Button backBtn = new Button(this);
        backBtn.setText("Back to Main");
        backBtn.setOnClickListener(v -> finish());
        layout.addView(backBtn);
        
        setContentView(layout);
    }
}

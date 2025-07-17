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
import com.example.myapplication.adapter.BillAdapter;
import com.example.myapplication.manager.BillManager;
import com.example.myapplication.manager.UserManager;
import com.example.myapplication.model.Bill;
import com.example.myapplication.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * BillHistoryActivity - Màn hình lịch sử đơn hàng với Modern Filter Design
 * FIXED: Replaced ugly spinner with beautiful filter chips
 */
public class BillHistoryActivity extends AppCompatActivity implements BillAdapter.OnBillClickListener {
    
    private static final String TAG = "BillHistoryActivity";
    
    // UI Components
    private ImageButton btnBack;
    private TextView tvTotalOrders, tvTotalSpent;
    
    // Modern Filter Chips (replacing spinner)
    private TextView chipFilterAll, chipFilterPending, chipFilterDelivered;
    private TextView currentSelectedChip;
    
    private RecyclerView recyclerViewBills;
    private LinearLayout layoutEmptyBills;
    private Button btnGoToMenu;
    
    // Data
    private BillManager billManager;
    private UserManager userManager;
    private BillAdapter billAdapter;
    private String currentFilter = "ALL";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_history);
        
        initManagers();
        initViews();
        setupRecyclerView();
        setupFilterChips();
        setupClickListeners();
        updateStatistics();
        loadBills();
    }
    
    private void initManagers() {
        billManager = BillManager.getInstance(this);
        userManager = UserManager.getInstance(this);
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);
        
        // Modern Filter Chips
        chipFilterAll = findViewById(R.id.chipFilterAll);
        chipFilterPending = findViewById(R.id.chipFilterPending);
        chipFilterDelivered = findViewById(R.id.chipFilterDelivered);
        
        recyclerViewBills = findViewById(R.id.recyclerViewBills);
        layoutEmptyBills = findViewById(R.id.layoutEmptyBills);
        btnGoToMenu = findViewById(R.id.btnGoToMenu);
        
        // Set default selected chip
        currentSelectedChip = chipFilterAll;
    }
    
    private void setupRecyclerView() {
        billAdapter = new BillAdapter(new ArrayList<>());
        billAdapter.setOnBillClickListener(this);
        recyclerViewBills.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBills.setAdapter(billAdapter);
    }
    
    /**
     * SOLUTION 1: Replace ugly spinner with modern filter chips
     * Beautiful, intuitive, and fits better with the app design
     */
    private void setupFilterChips() {
        chipFilterAll.setOnClickListener(v -> {
            selectFilterChip(chipFilterAll, "ALL");
        });
        
        chipFilterPending.setOnClickListener(v -> {
            selectFilterChip(chipFilterPending, Bill.STATUS_PENDING);
        });
        
        chipFilterDelivered.setOnClickListener(v -> {
            selectFilterChip(chipFilterDelivered, Bill.STATUS_DELIVERED);
        });
    }
    
    /**
     * Handle filter chip selection with smooth UI transitions
     */
    private void selectFilterChip(TextView selectedChip, String filter) {
        // Reset all chips to unselected state
        resetAllChips();
        
        // Set selected chip to selected state
        selectedChip.setBackground(getDrawable(R.drawable.chip_selected_pressed));
        selectedChip.setTextColor(getColor(R.color.white));
        
        // Update current selection
        currentSelectedChip = selectedChip;
        currentFilter = filter;
        
        // Load bills with new filter
        loadBills();
        
        // Add subtle animation
        selectedChip.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(150)
                .withEndAction(() -> {
                    selectedChip.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(150)
                            .start();
                })
                .start();
    }
    
    /**
     * Reset all filter chips to unselected state
     */
    private void resetAllChips() {
        chipFilterAll.setBackground(getDrawable(R.drawable.chip_unselected_pressed));
        chipFilterAll.setTextColor(getColor(R.color.text_primary));
        
        chipFilterPending.setBackground(getDrawable(R.drawable.chip_unselected_pressed));
        chipFilterPending.setTextColor(getColor(R.color.text_primary));
        
        chipFilterDelivered.setBackground(getDrawable(R.drawable.chip_unselected_pressed));
        chipFilterDelivered.setTextColor(getColor(R.color.text_primary));
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnGoToMenu.setOnClickListener(v -> {
            try {
                android.util.Log.d("BillHistoryActivity", "Opening FallbackMenuActivity from empty state");
                Intent intent = new Intent(this, FallbackMenuActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                android.util.Log.e("BillHistoryActivity", "Error opening FallbackMenuActivity: " + e.getMessage(), e);
                android.widget.Toast.makeText(this, "Lỗi mở menu: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                
                // Try SimpleMenuActivity as backup
                try {
                    Intent simpleIntent = new Intent(this, SimpleMenuActivity.class);
                    startActivity(simpleIntent);
                } catch (Exception e2) {
                    android.util.Log.e("BillHistoryActivity", "SimpleMenuActivity also failed: " + e2.getMessage(), e2);
                    
                    // Try DebugMenuActivity as last resort
                    try {
                        Intent debugIntent = new Intent(this, DebugMenuActivity.class);
                        startActivity(debugIntent);
                    } catch (Exception e3) {
                        android.util.Log.e("BillHistoryActivity", "All menu activities failed: " + e3.getMessage(), e3);
                    }
                }
            }
        });
    }
    
    private void updateStatistics() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            int totalOrders = billManager.getBillCountByUsername(currentUser.getUsername());
            double totalSpent = billManager.getTotalSpentByUsername(currentUser.getUsername());
            
            tvTotalOrders.setText(String.valueOf(totalOrders));
            tvTotalSpent.setText(formatPrice(totalSpent));
        }
    }
    
    private void loadBills() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            List<Bill> bills;
            
            if (currentFilter.equals("ALL")) {
                bills = billManager.getBillsByUsername(currentUser.getUsername());
            } else {
                bills = billManager.getBillsByUsernameAndStatus(currentUser.getUsername(), currentFilter);
            }
            
            if (bills.isEmpty()) {
                showEmptyState();
            } else {
                showBillsList(bills);
            }
        }
    }
    
    private void showEmptyState() {
        recyclerViewBills.setVisibility(View.GONE);
        layoutEmptyBills.setVisibility(View.VISIBLE);
    }
    
    private void showBillsList(List<Bill> bills) {
        recyclerViewBills.setVisibility(View.VISIBLE);
        layoutEmptyBills.setVisibility(View.GONE);
        billAdapter.updateBills(bills);
    }
    
    private String formatPrice(double price) {
        return String.format("%.0f₫", price);
    }
    
    @Override
    public void onBillClick(Bill bill) {
        Intent intent = new Intent(this, BillDetailActivity.class);
        intent.putExtra("bill_id", bill.getBillId());
        startActivity(intent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Force reload bills when returning to activity
        billManager.forceReloadBills();
        updateStatistics();
        loadBills();
    }
}
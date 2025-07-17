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
import java.util.List;

/**
 * BillDetailActivity - Chi tiết hóa đơn cụ thể
 */
public class BillDetailActivity extends AppCompatActivity {
    
    private ImageButton btnBack;
    private TextView tvBillId, tvBillDate, tvBillStatus, tvBillTotal;
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress;
    private RecyclerView recyclerViewBillItems;
    private Button btnUpdateStatus;
    
    private BillManager billManager;
    private Bill currentBill;
    private int billId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);
        
        // Get bill ID from intent
        billId = getIntent().getIntExtra("bill_id", -1);
        if (billId == -1) {
            finish();
            return;
        }
        
        initManager();
        initViews();
        loadBillDetails();
        setupClickListeners();
    }
    
    private void initManager() {
        billManager = BillManager.getInstance(this);
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvBillId = findViewById(R.id.tvBillId);
        tvBillDate = findViewById(R.id.tvBillDate);
        tvBillStatus = findViewById(R.id.tvBillStatus);
        tvBillTotal = findViewById(R.id.tvBillTotal);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);
        recyclerViewBillItems = findViewById(R.id.recyclerViewBillItems);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
    }
    
    private void loadBillDetails() {
        currentBill = billManager.getBillById(billId);
        if (currentBill == null) {
            finish();
            return;
        }
        
        // Display bill info
        tvBillId.setText("#" + currentBill.getBillId());
        tvBillDate.setText(currentBill.getFormattedDate());
        tvBillStatus.setText(currentBill.getStatusName());
        tvBillTotal.setText(formatPrice(currentBill.getTotalAmount()));
        
        // Display customer info
        tvCustomerName.setText(currentBill.getFullName());
        tvCustomerPhone.setText(currentBill.getPhone());
        tvCustomerAddress.setText(currentBill.getDeliveryAddress());
        
        // Set status color
        updateStatusColor();
        
        // Setup RecyclerView for bill items
        setupRecyclerView();
    }
    
    private void setupRecyclerView() {
        // You would create a BillItemAdapter here
        // For now, we'll just hide the RecyclerView
        recyclerViewBillItems.setVisibility(View.GONE);
    }
    
    private void updateStatusColor() {
        String currentStatus = currentBill.getCurrentStatus();
        int color;
        switch (currentStatus) {
            case Bill.STATUS_PENDING:
                color = getColor(R.color.primary_orange);
                break;
            case Bill.STATUS_DELIVERED:
                color = getColor(R.color.success_green);
                break;
            default:
                color = getColor(R.color.text_primary);
        }
        tvBillStatus.setTextColor(color);
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnUpdateStatus.setOnClickListener(v -> {
            // Show status update dialog
            showUpdateStatusDialog();
        });
    }
    
    private void showUpdateStatusDialog() {
        String[] statuses = {
            "Chờ xử lý",
            "Đã giao hàng"
        };
        
        String[] statusValues = {
            Bill.STATUS_PENDING,
            Bill.STATUS_DELIVERED
        };
        
        new android.app.AlertDialog.Builder(this)
            .setTitle("Cập nhật trạng thái")
            .setItems(statuses, (dialog, which) -> {
                String newStatus = statusValues[which];
                billManager.updateBillStatus(billId, newStatus);
                loadBillDetails(); // Refresh display
            })
            .show();
    }
    
    private String formatPrice(double price) {
        return String.format("%.0f₫", price);
    }
}

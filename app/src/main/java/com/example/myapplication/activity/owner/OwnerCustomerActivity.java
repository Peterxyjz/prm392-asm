package com.example.myapplication.activity.owner;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapter.OwnerCustomerAdapter;
import com.example.myapplication.manager.BillManager;
import com.example.myapplication.manager.UserManager;
import com.example.myapplication.model.Bill;
import com.example.myapplication.model.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * OwnerCustomerActivity - Quản lý khách hàng cho Owner
 * Hiển thị danh sách khách hàng và thông tin chi tiết
 */
public class OwnerCustomerActivity extends AppCompatActivity implements OwnerCustomerAdapter.OnCustomerClickListener {

    // UI Components
    private RecyclerView recyclerViewCustomers;
    private OwnerCustomerAdapter customerAdapter;
    
    // Data
    private UserManager userManager;
    private BillManager billManager;
    private List<User> customerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_customer);
        
        setupToolbar();
        initViews();
        setupRecyclerView();
        loadCustomerData();
    }

    /**
     * Thiết lập toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản Lý Khách Hàng");
        }
    }

    /**
     * Khởi tạo các view components
     */
    private void initViews() {
        recyclerViewCustomers = findViewById(R.id.recyclerViewCustomers);
    }

    /**
     * Thiết lập RecyclerView
     */
    private void setupRecyclerView() {
        recyclerViewCustomers.setLayoutManager(new LinearLayoutManager(this));
        customerAdapter = new OwnerCustomerAdapter(this, this);
        recyclerViewCustomers.setAdapter(customerAdapter);
    }

    /**
     * Load dữ liệu khách hàng
     */
    private void loadCustomerData() {
        userManager = UserManager.getInstance(this);
        billManager = BillManager.getInstance(this);
        
        // Lấy tất cả khách hàng từ UserManager
        customerList = userManager.getAllCustomers();
        customerAdapter.updateCustomerList(customerList);
        
        // Update title with customer count
        updateTitle();
    }

    /**
     * Cập nhật title với số lượng khách hàng
     */
    private void updateTitle() {
        int totalCustomers = customerList != null ? customerList.size() : 0;
        String title = String.format("Quản Lý Khách Hàng (%d)", totalCustomers);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    /**
     * Hiển thị chi tiết khách hàng với custom layout
     */
    private void showCustomerDetails(User customer) {
        // Get additional statistics
        int orderCount = billManager.getBillCountByUsername(customer.getUsername());
        double totalSpent = billManager.getTotalSpentByUsername(customer.getUsername());
        List<Bill> customerOrders = billManager.getBillsByUsername(customer.getUsername());
        
        // Calculate additional stats
        int completedOrders = 0;
        int cancelledOrders = 0;
        String lastOrderDate = "Chưa có đơn hàng";
        
        if (!customerOrders.isEmpty()) {
            for (Bill order : customerOrders) {
                if (Bill.STATUS_DELIVERED.equals(order.getStatus())) {
                    completedOrders++;
                } else if (Bill.STATUS_CANCELLED.equals(order.getStatus())) {
                    cancelledOrders++;
                }
            }
            
            // Find most recent order
            Bill mostRecentOrder = customerOrders.get(0);
            for (Bill order : customerOrders) {
                if (order.getOrderDate().after(mostRecentOrder.getOrderDate())) {
                    mostRecentOrder = order;
                }
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            lastOrderDate = sdf.format(mostRecentOrder.getOrderDate());
        }

        // Create custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_customer_details, null);
        
        // Customer Info Section
        TextView tvCustomerName = dialogView.findViewById(R.id.tvCustomerName);
        TextView tvUsername = dialogView.findViewById(R.id.tvUsername);
        TextView tvEmail = dialogView.findViewById(R.id.tvEmail);
        TextView tvPhone = dialogView.findViewById(R.id.tvPhone);
        TextView tvAddress = dialogView.findViewById(R.id.tvAddress);
        TextView tvJoinDate = dialogView.findViewById(R.id.tvJoinDate);
        TextView tvVerifiedStatus = dialogView.findViewById(R.id.tvVerifiedStatus);
        
        // Statistics Section
        TextView tvTotalOrders = dialogView.findViewById(R.id.tvTotalOrders);
        TextView tvCompletedOrders = dialogView.findViewById(R.id.tvCompletedOrders);
        TextView tvCancelledOrders = dialogView.findViewById(R.id.tvCancelledOrders);
        TextView tvTotalSpent = dialogView.findViewById(R.id.tvTotalSpent);
        TextView tvLastOrder = dialogView.findViewById(R.id.tvLastOrder);
        
        // Set customer info
        tvCustomerName.setText(customer.getFullName().isEmpty() ? customer.getUsername() : customer.getFullName());
        tvUsername.setText(customer.getUsername());
        tvEmail.setText(customer.getEmail().isEmpty() ? "Chưa có email" : customer.getEmail());
        tvPhone.setText(customer.getPhone().isEmpty() ? "Chưa có số điện thoại" : customer.getPhone());
        tvAddress.setText(customer.getAddress());
        
        SimpleDateFormat joinDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvJoinDate.setText(joinDateFormat.format(new Date(customer.getCreatedDate())));
        
        tvVerifiedStatus.setText(customer.isVerified() ? "Đã xác thực" : "Chưa xác thực");
        tvVerifiedStatus.setTextColor(getResources().getColor(
            customer.isVerified() ? android.R.color.holo_green_dark : android.R.color.holo_orange_dark
        ));
        
        // Set statistics
        tvTotalOrders.setText(String.valueOf(orderCount));
        tvCompletedOrders.setText(String.valueOf(completedOrders));
        tvCancelledOrders.setText(String.valueOf(cancelledOrders));
        tvTotalSpent.setText(String.format("%.0f₫", totalSpent));
        tvLastOrder.setText(lastOrderDate);
        
        // Show dialog
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Đóng", null)
                .setNeutralButton("Xem Lịch Sử", (dialog, which) -> {
                    showCustomerOrderHistory(customer);
                })
                .show();
    }

    /**
     * Hiển thị lịch sử đơn hàng của khách hàng
     */
    private void showCustomerOrderHistory(User customer) {
        List<Bill> customerOrders = billManager.getBillsByUsername(customer.getUsername());
        
        if (customerOrders.isEmpty()) {
            showToast("Khách hàng này chưa có đơn hàng nào");
            return;
        }
        
        // Sort orders by date (newest first)
        customerOrders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
        
        StringBuilder history = new StringBuilder();
        history.append("📋 LỊCH SỬ ĐƠN HÀNG\n");
        history.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        history.append("👤 Khách hàng: ").append(customer.getFullName()).append("\n");
        history.append("📊 Tổng cộng: ").append(customerOrders.size()).append(" đơn hàng\n\n");
        
        for (int i = 0; i < Math.min(customerOrders.size(), 10); i++) { // Show max 10 orders
            Bill order = customerOrders.get(i);
            
            // Status emoji
            String statusEmoji = getStatusEmoji(order.getStatus());
            
            history.append("🛍️ ĐƠN HÀNG #").append(order.getBillId()).append("\n");
            history.append("├─ 📅 Ngày: ").append(order.getFormattedDate()).append("\n");
            history.append("├─ 💰 Giá trị: ").append(String.format("%.0f₫", order.getTotalAmount())).append("\n");
            history.append("├─ ").append(statusEmoji).append(" Trạng thái: ").append(order.getStatusName()).append("\n");
            history.append("└─ 🍽️ Số món: ").append(order.getTotalItemCount()).append(" món\n");
            
            if (i < Math.min(customerOrders.size(), 10) - 1) {
                history.append("\n");
            }
        }
        
        if (customerOrders.size() > 10) {
            history.append("\n⋯ Và ").append(customerOrders.size() - 10).append(" đơn hàng khác nữa");
        }
        
        new AlertDialog.Builder(this)
                .setTitle("📋 Lịch Sử Đơn Hàng")
                .setMessage(history.toString())
                .setPositiveButton("Đóng", null)
                .show();
    }

    /**
     * Lấy emoji cho trạng thái đơn hàng
     */
    private String getStatusEmoji(String status) {
        switch (status) {
            case Bill.STATUS_PENDING: return "⏳";
            case Bill.STATUS_CONFIRMED: return "✅";
            case Bill.STATUS_PREPARING: return "👨‍🍳";
            case Bill.STATUS_READY: return "🎯";
            case Bill.STATUS_DELIVERING: return "🚚";
            case Bill.STATUS_DELIVERED: return "✅";
            case Bill.STATUS_CANCELLED: return "❌";
            default: return "❓";
        }
    }

    /**
     * Hiển thị thống kê tổng quan
     */
    private void showCustomerStatistics() {
        if (customerList == null || customerList.isEmpty()) {
            showToast("Chưa có khách hàng nào");
            return;
        }
        
        int totalCustomers = customerList.size();
        int activeCustomers = 0; // customers with at least 1 order
        int verifiedCustomers = 0;
        double totalRevenue = billManager.getTotalRevenue();
        
        for (User customer : customerList) {
            if (customer.isVerified()) {
                verifiedCustomers++;
            }
            
            if (billManager.getBillCountByUsername(customer.getUsername()) > 0) {
                activeCustomers++;
            }
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("📊 THỐNG KÊ KHÁCH HÀNG\n");
        stats.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        stats.append("👥 TỔNG QUAN KHÁCH HÀNG\n");
        stats.append("├─ 👤 Tổng số: ").append(totalCustomers).append(" khách hàng\n");
        stats.append("├─ 🎯 Có đơn hàng: ").append(activeCustomers).append(" khách\n");
        stats.append("├─ ✅ Đã xác thực: ").append(verifiedCustomers).append(" khách\n");
        stats.append("└─ ❌ Chưa xác thực: ").append(totalCustomers - verifiedCustomers).append(" khách\n\n");
        
        stats.append("💰 THỐNG KÊ DOANH THU\n");
        stats.append("├─ 💵 Tổng doanh thu: ").append(String.format("%.0f₫", totalRevenue)).append("\n");
        
        if (activeCustomers > 0) {
            double avgRevenuePerCustomer = totalRevenue / activeCustomers;
            stats.append("└─ 📊 TB/khách hàng: ").append(String.format("%.0f₫", avgRevenuePerCustomer)).append("\n");
        } else {
            stats.append("└─ 📊 TB/khách hàng: 0₫\n");
        }
        
        new AlertDialog.Builder(this)
                .setTitle("📊 Thống Kê Tổng Quan")
                .setMessage(stats.toString())
                .setPositiveButton("Đóng", null)
                .show();
    }

    /**
     * Hiển thị Toast message
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.owner_customer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_refresh) {
            loadCustomerData();
            showToast("Đã làm mới danh sách khách hàng");
            return true;
        } else if (id == R.id.action_statistics) {
            showCustomerStatistics();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    // OwnerCustomerAdapter.OnCustomerClickListener implementations
    @Override
    public void onCustomerClick(User customer) {
        showCustomerDetails(customer);
    }

    @Override
    public void onViewOrderHistory(User customer) {
        showCustomerOrderHistory(customer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCustomerData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

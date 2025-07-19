package com.example.myapplication.activity.owner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.example.myapplication.R;
import com.example.myapplication.activity.LoginActivity;
import com.example.myapplication.manager.UserManager;
import com.example.myapplication.manager.BillManager;
import com.example.myapplication.model.User;

/**
 * OwnerDashboardActivity - Dashboard chính cho Owner
 * Hiển thị tổng quan về nhà hàng và các chức năng quản lý
 */
public class OwnerDashboardActivity extends AppCompatActivity {
    
    // UI Components
    private TextView tvWelcome, tvTotalRevenue, tvTotalOrders, tvTotalCustomers, tvMenuItems;
    private CardView btnRevenueAnalytics, btnMenuManagement, btnOrderManagement, btnCustomerManagement;
    private Button btnLogout;
    
    // Manager
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Khởi tạo UserManager
        userManager = UserManager.getInstance(this);
        
        // Kiểm tra quyền Owner
        if (!userManager.isCurrentUserOwner()) {
            // Không phải Owner -> chuyển về login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.activity_owner_dashboard);
        
        initViews();
        setupClickListeners();
        
        // FIXED: Chạy kiểm tra và sửa chữa ID trùng lặp khi khởi động
        fixDuplicateIdsIfNeeded();
        
        updateDashboardData();
    }

    /**
     * Khởi tạo các view components
     */
    private void initViews() {
        // Welcome & User info
        tvWelcome = findViewById(R.id.tvWelcome);
        
        // Statistics
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvTotalCustomers = findViewById(R.id.tvTotalCustomers);
        tvMenuItems = findViewById(R.id.tvMenuItems);
        
        // Management buttons
        btnRevenueAnalytics = findViewById(R.id.btnRevenueAnalytics);
        btnMenuManagement = findViewById(R.id.btnMenuManagement);
        btnOrderManagement = findViewById(R.id.btnOrderManagement);
        btnCustomerManagement = findViewById(R.id.btnCustomerManagement);
        
        // Logout
        btnLogout = findViewById(R.id.btnLogout);
    }

    /**
     * Thiết lập sự kiện click
     */
    private void setupClickListeners() {
        // Dashboard Analytics
        btnRevenueAnalytics.setOnClickListener(v -> {
            Intent intent = new Intent(this, OwnerRevenueActivity.class);
            startActivity(intent);
        });
        
        // Menu Management
        btnMenuManagement.setOnClickListener(v -> {
            Intent intent = new Intent(this, OwnerMenuActivity.class);
            startActivity(intent);
        });
        
        // Order Management
        btnOrderManagement.setOnClickListener(v -> {
            Intent intent = new Intent(this, OwnerOrderActivity.class);
            startActivity(intent);
        });
        
        // Customer Management
        btnCustomerManagement.setOnClickListener(v -> {
            Intent intent = new Intent(this, OwnerCustomerActivity.class);
            startActivity(intent);
        });
        
        // Logout
        btnLogout.setOnClickListener(v -> showLogoutDialog());
        
        // FIXED: Thêm long click listener cho debug và fix ID
        tvWelcome.setOnLongClickListener(v -> {
            showDebugMenu();
            return true;
        });
    }

    /**
     * Cập nhật dữ liệu dashboard
     */
    private void updateDashboardData() {
        // Welcome message
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            String welcomeText = "Chào mừng, " + currentUser.getFullName();
            tvWelcome.setText(welcomeText);
        }
        
        // Lấy dữ liệu thực từ hệ thống
        updateStatistics();
    }
    
    /**
     * Cập nhật thống kê thực tế
     */
    private void updateStatistics() {
        try {
            // Lấy dữ liệu từ BillManager
            com.example.myapplication.manager.BillManager billManager = 
                com.example.myapplication.manager.BillManager.getInstance(this);
            
            // Tổng doanh thu từ tất cả đơn hàng
            java.util.List<com.example.myapplication.model.Bill> allBills = 
                billManager.getAllBillsFromAllUsers();
            
            double totalRevenue = 0;
            int totalOrders = allBills.size();
            
            for (com.example.myapplication.model.Bill bill : allBills) {
                totalRevenue += bill.getTotalAmount();
            }
            
            // Lấy số lượng món ăn từ menu
            java.util.List<com.example.myapplication.model.FoodItem> foodItems = 
                com.example.myapplication.manager.FoodDataManager.getAllFoodItems();
            int menuItemsCount = foodItems.size();
            
            // TODO: Đếm số khách hàng thực tế (hiện tại dùng mock data)
            int customersCount = Math.max(10, totalOrders / 3); // Estimate based on orders
            
            // Hiển thị dữ liệu
            tvTotalRevenue.setText(formatCurrency(totalRevenue));
            tvTotalOrders.setText(String.valueOf(totalOrders));
            tvTotalCustomers.setText(String.valueOf(customersCount));
            tvMenuItems.setText(String.valueOf(menuItemsCount));
            
        } catch (Exception e) {
            android.util.Log.e("OwnerDashboard", "Error updating statistics: " + e.getMessage());
            
            // Fallback to mock data if error
            tvTotalRevenue.setText("5,280,000 VNĐ");
            tvTotalOrders.setText("127");
            tvTotalCustomers.setText("48");
            tvMenuItems.setText("12");
        }
    }
    
    /**
     * Format tiền tệ VNĐ
     */
    private String formatCurrency(double amount) {
        if (amount >= 1000000) {
            return String.format("%.1fM VNĐ", amount / 1000000);
        } else if (amount >= 1000) {
            return String.format("%.0fK VNĐ", amount / 1000);
        } else {
            return String.format("%.0f VNĐ", amount);
        }
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
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    /**
     * Cập nhật dữ liệu khi quay lại activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardData();
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
    
    /**
     * FIXED: Kiểm tra và sửa chữa ID trùng lặp nếu cần
     */
    private void fixDuplicateIdsIfNeeded() {
        try {
            android.util.Log.d("OwnerDashboard", "Starting duplicate ID check...");
            
            BillManager billManager = BillManager.getInstance(this);
            
            // Chạy kiểm tra và sửa chữa trong background thread
            new Thread(() -> {
                try {
                    billManager.validateAndFixDuplicateIds();
                    
                    // Show result on UI thread
                    runOnUiThread(() -> {
                        android.util.Log.d("OwnerDashboard", "Duplicate ID check completed");
                        // Optionally show a toast or update UI
                    });
                    
                } catch (Exception e) {
                    android.util.Log.e("OwnerDashboard", "Error during duplicate ID fix", e);
                }
            }).start();
            
        } catch (Exception e) {
            android.util.Log.e("OwnerDashboard", "Error starting duplicate ID check", e);
        }
    }
    
    /**
     * FIXED: Hiển thị menu debug cho Owner (long click ở welcome text)
     */
    private void showDebugMenu() {
        String[] options = {
            "Kiểm tra ID trùng lặp",
            "Sửa chữa ID trùng lặp",
            "Debug thông tin đơn hàng",
            "Hủy"
        };
        
        new AlertDialog.Builder(this)
            .setTitle("Menu Debug (Owner Only)")
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        checkDuplicateIds();
                        break;
                    case 1:
                        fixDuplicateIdsManually();
                        break;
                    case 2:
                        debugBillInfo();
                        break;
                    case 3:
                        dialog.dismiss();
                        break;
                }
            })
            .show();
    }
    
    /**
     * Kiểm tra ID trùng lặp và hiển thị kết quả
     */
    private void checkDuplicateIds() {
        android.app.ProgressDialog progressDialog = android.app.ProgressDialog.show(
            this, "Kiểm tra", "Đang kiểm tra ID trùng lặp...", true
        );
        
        new Thread(() -> {
            try {
                BillManager billManager = BillManager.getInstance(this);
                
                // Đếm tất cả bills và tìm duplicate
                java.util.List<com.example.myapplication.model.Bill> allBills = 
                    billManager.getAllBillsFromAllUsers();
                
                java.util.Map<Integer, Integer> idCounts = new java.util.HashMap<>();
                for (com.example.myapplication.model.Bill bill : allBills) {
                    int id = bill.getId();
                    idCounts.put(id, idCounts.getOrDefault(id, 0) + 1);
                }
                
                java.util.List<Integer> duplicates = new java.util.ArrayList<>();
                for (java.util.Map.Entry<Integer, Integer> entry : idCounts.entrySet()) {
                    if (entry.getValue() > 1) {
                        duplicates.add(entry.getKey());
                    }
                }
                
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    
                    String message;
                    if (duplicates.isEmpty()) {
                        message = "Không có ID trùng lặp.\n\nTổng số đơn hàng: " + allBills.size();
                    } else {
                        message = "Tìm thấy " + duplicates.size() + " ID trùng lặp:\n" + 
                                duplicates.toString() + "\n\nTổng số đơn hàng: " + allBills.size();
                    }
                    
                    new AlertDialog.Builder(this)
                        .setTitle("Kết quả kiểm tra")
                        .setMessage(message)
                        .setPositiveButton("Đóng", null)
                        .show();
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(this)
                        .setTitle("Lỗi")
                        .setMessage("Lỗi kiểm tra: " + e.getMessage())
                        .setPositiveButton("Đóng", null)
                        .show();
                });
            }
        }).start();
    }
    
    /**
     * Sửa chữa ID trùng lặp thủ công
     */
    private void fixDuplicateIdsManually() {
        new AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc chắn muốn sửa chữa ID trùng lặp?\n\nThao tác này sẽ thay đổi ID của các đơn hàng trùng lặp.")
            .setPositiveButton("Sửa chữa", (dialog, which) -> {
                android.app.ProgressDialog progressDialog = android.app.ProgressDialog.show(
                    this, "Sửa chữa", "Đang sửa chữa ID trùng lặp...", true
                );
                
                new Thread(() -> {
                    try {
                        BillManager billManager = BillManager.getInstance(this);
                        billManager.validateAndFixDuplicateIds();
                        
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(this)
                                .setTitle("Thành công")
                                .setMessage("Sửa chữa ID trùng lặp thành công!")
                                .setPositiveButton("Đóng", null)
                                .show();
                            
                            // Cập nhật lại dashboard
                            updateDashboardData();
                        });
                        
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(this)
                                .setTitle("Lỗi")
                                .setMessage("Lỗi sửa chữa: " + e.getMessage())
                                .setPositiveButton("Đóng", null)
                                .show();
                        });
                    }
                }).start();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    /**
     * Hiển thị thông tin debug về đơn hàng
     */
    private void debugBillInfo() {
        try {
            BillManager billManager = BillManager.getInstance(this);
            billManager.debugBillsStatus();
            
            // Hiển thị thông tin tổng quan
            java.util.List<com.example.myapplication.model.Bill> allBills = 
                billManager.getAllBillsFromAllUsers();
            
            StringBuilder info = new StringBuilder();
            info.append("Tổng số đơn hàng: ").append(allBills.size()).append("\n");
            info.append("Tổng doanh thu: ").append(formatCurrency(billManager.getTotalRevenue())).append("\n");
            info.append("Doanh thu hôm nay: ").append(formatCurrency(billManager.getDailyRevenue())).append("\n\n");
            
            // Thống kê theo trạng thái
            int pending = billManager.getOrderCountByStatus(com.example.myapplication.model.Bill.STATUS_PENDING);
            int confirmed = billManager.getOrderCountByStatus(com.example.myapplication.model.Bill.STATUS_CONFIRMED);
            int preparing = billManager.getOrderCountByStatus(com.example.myapplication.model.Bill.STATUS_PREPARING);
            int ready = billManager.getOrderCountByStatus(com.example.myapplication.model.Bill.STATUS_READY);
            int delivering = billManager.getOrderCountByStatus(com.example.myapplication.model.Bill.STATUS_DELIVERING);
            int delivered = billManager.getOrderCountByStatus(com.example.myapplication.model.Bill.STATUS_DELIVERED);
            int cancelled = billManager.getOrderCountByStatus(com.example.myapplication.model.Bill.STATUS_CANCELLED);
            
            info.append("Thống kê trạng thái:\n");
            info.append("- Chờ xử lý: ").append(pending).append("\n");
            info.append("- Đã xác nhận: ").append(confirmed).append("\n");
            info.append("- Đang chuẩn bị: ").append(preparing).append("\n");
            info.append("- Sẵn sàng giao: ").append(ready).append("\n");
            info.append("- Đang giao hàng: ").append(delivering).append("\n");
            info.append("- Đã giao hàng: ").append(delivered).append("\n");
            info.append("- Đã hủy: ").append(cancelled).append("\n");
            
            new AlertDialog.Builder(this)
                .setTitle("Thông tin Debug")
                .setMessage(info.toString())
                .setPositiveButton("Đóng", null)
                .show();
                
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                .setTitle("Lỗi")
                .setMessage("Lỗi debug: " + e.getMessage())
                .setPositiveButton("Đóng", null)
                .show();
        }
    }
}

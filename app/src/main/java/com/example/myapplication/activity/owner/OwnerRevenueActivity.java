package com.example.myapplication.activity.owner;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.myapplication.R;

/**
 * OwnerRevenueActivity - Dashboard doanh thu cho Owner
 * Hiển thị biểu đồ và thống kê doanh thu theo thời gian
 */
public class OwnerRevenueActivity extends AppCompatActivity {

    // UI Components
    private TextView tvTodayRevenue, tvWeekRevenue, tvMonthRevenue, tvYearRevenue;
    private TextView tvTodayOrders, tvWeekOrders, tvMonthOrders, tvYearOrders;
    private TextView tvBestSellingItem, tvPeakHour, tvAverageOrderValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_revenue);
        
        setupToolbar();
        initViews();
        loadRevenueData();
    }

    /**
     * Thiết lập toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Dashboard Doanh Thu");
        }
    }

    /**
     * Khởi tạo các view components
     */
    private void initViews() {
        // Revenue statistics
        tvTodayRevenue = findViewById(R.id.tvTodayRevenue);
        tvWeekRevenue = findViewById(R.id.tvWeekRevenue);
        tvMonthRevenue = findViewById(R.id.tvMonthRevenue);
        tvYearRevenue = findViewById(R.id.tvYearRevenue);
        
        // Order statistics
        tvTodayOrders = findViewById(R.id.tvTodayOrders);
        tvWeekOrders = findViewById(R.id.tvWeekOrders);
        tvMonthOrders = findViewById(R.id.tvMonthOrders);
        tvYearOrders = findViewById(R.id.tvYearOrders);
        
        // Analytics
        tvBestSellingItem = findViewById(R.id.tvBestSellingItem);
        tvPeakHour = findViewById(R.id.tvPeakHour);
        tvAverageOrderValue = findViewById(R.id.tvAverageOrderValue);
    }

    /**
     * Load dữ liệu doanh thu
     */
    private void loadRevenueData() {
        try {
            // Lấy dữ liệu thực từ BillManager
            com.example.myapplication.manager.BillManager billManager = 
                com.example.myapplication.manager.BillManager.getInstance(this);
            
            java.util.List<com.example.myapplication.model.Bill> allBills = 
                billManager.getAllBillsFromAllUsers();
            
            // Tính toán doanh thu theo thời gian
            calculateRevenueByPeriod(allBills);
            
            // Tính toán số đơn hàng theo thời gian
            calculateOrdersByPeriod(allBills);
            
            // Cập nhật analytics
            updateAnalytics(allBills);
            
        } catch (Exception e) {
            android.util.Log.e("OwnerRevenue", "Error loading revenue data: " + e.getMessage());
            
            // Fallback to mock data if error
            loadMockData();
        }
    }
    
    /**
     * Tính toán doanh thu theo thời gian
     */
    private void calculateRevenueByPeriod(java.util.List<com.example.myapplication.model.Bill> bills) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.util.Date now = new java.util.Date();
        
        double todayRevenue = 0;
        double weekRevenue = 0;
        double monthRevenue = 0;
        double yearRevenue = 0;
        
        // Get time boundaries
        calendar.setTime(now);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        java.util.Date todayStart = calendar.getTime();
        
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -7);
        java.util.Date weekStart = calendar.getTime();
        
        calendar.setTime(now);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        java.util.Date monthStart = calendar.getTime();
        
        calendar.set(java.util.Calendar.DAY_OF_YEAR, 1);
        java.util.Date yearStart = calendar.getTime();
        
        // Calculate revenue for each period
        for (com.example.myapplication.model.Bill bill : bills) {
            java.util.Date orderDate = bill.getOrderDate();
            double amount = bill.getTotalAmount();
            
            if (orderDate.after(todayStart)) {
                todayRevenue += amount;
            }
            if (orderDate.after(weekStart)) {
                weekRevenue += amount;
            }
            if (orderDate.after(monthStart)) {
                monthRevenue += amount;
            }
            if (orderDate.after(yearStart)) {
                yearRevenue += amount;
            }
        }
        
        // Update UI
        tvTodayRevenue.setText(formatCurrency(todayRevenue));
        tvWeekRevenue.setText(formatCurrency(weekRevenue));
        tvMonthRevenue.setText(formatCurrency(monthRevenue));
        tvYearRevenue.setText(formatCurrency(yearRevenue));
    }
    
    /**
     * Tính toán số đơn hàng theo thời gian
     */
    private void calculateOrdersByPeriod(java.util.List<com.example.myapplication.model.Bill> bills) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.util.Date now = new java.util.Date();
        
        int todayOrders = 0;
        int weekOrders = 0;
        int monthOrders = 0;
        int yearOrders = bills.size(); // All bills for year
        
        // Get time boundaries (same as revenue calculation)
        calendar.setTime(now);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        java.util.Date todayStart = calendar.getTime();
        
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -7);
        java.util.Date weekStart = calendar.getTime();
        
        calendar.setTime(now);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        java.util.Date monthStart = calendar.getTime();
        
        // Count orders for each period
        for (com.example.myapplication.model.Bill bill : bills) {
            java.util.Date orderDate = bill.getOrderDate();
            
            if (orderDate.after(todayStart)) {
                todayOrders++;
            }
            if (orderDate.after(weekStart)) {
                weekOrders++;
            }
            if (orderDate.after(monthStart)) {
                monthOrders++;
            }
        }
        
        // Update UI
        tvTodayOrders.setText(String.valueOf(todayOrders));
        tvWeekOrders.setText(String.valueOf(weekOrders));
        tvMonthOrders.setText(String.valueOf(monthOrders));
        tvYearOrders.setText(String.valueOf(yearOrders));
    }
    
    /**
     * Cập nhật analytics
     */
    private void updateAnalytics(java.util.List<com.example.myapplication.model.Bill> bills) {
        if (bills.isEmpty()) {
            tvBestSellingItem.setText("Chưa có dữ liệu");
            tvPeakHour.setText("Chưa có dữ liệu");
            tvAverageOrderValue.setText("0 VNĐ");
            return;
        }
        
        // Calculate average order value
        double totalRevenue = 0;
        for (com.example.myapplication.model.Bill bill : bills) {
            totalRevenue += bill.getTotalAmount();
        }
        double averageOrderValue = totalRevenue / bills.size();
        tvAverageOrderValue.setText(formatCurrency(averageOrderValue));
        
        // TODO: Implement actual best selling item calculation
        // For now, use mock data
        tvBestSellingItem.setText("Ramen Tonkotsu");
        tvPeakHour.setText("19:00 - 21:00");
    }
    
    /**
     * Load mock data nếu có lỗi
     */
    private void loadMockData() {
        tvTodayRevenue.setText("480,000 VNĐ");
        tvWeekRevenue.setText("2,850,000 VNĐ");
        tvMonthRevenue.setText("12,340,000 VNĐ");
        tvYearRevenue.setText("156,780,000 VNĐ");
        
        tvTodayOrders.setText("12");
        tvWeekOrders.setText("68");
        tvMonthOrders.setText("342");
        tvYearOrders.setText("4,125");
        
        tvBestSellingItem.setText("Ramen Tonkotsu");
        tvPeakHour.setText("19:00 - 21:00");
        tvAverageOrderValue.setText("385,000 VNĐ");
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

package com.example.myapplication.activity.owner;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapter.OwnerOrderAdapter;
import com.example.myapplication.manager.BillManager;
import com.example.myapplication.model.Bill;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * OwnerOrderActivity - Quản lý đơn hàng cho Owner
 * Hiển thị danh sách đơn hàng và chuyển trạng thái
 */
public class OwnerOrderActivity extends AppCompatActivity implements OwnerOrderAdapter.OnOrderActionListener {

    // UI Components
    private RecyclerView recyclerViewOrders;
    private OwnerOrderAdapter orderAdapter;
    
    // Data
    private BillManager billManager;
    private List<Bill> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_order);
        
        setupToolbar();
        initViews();
        setupRecyclerView();
        loadOrderData();
    }

    /**
     * Thiết lập toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản Lý Đơn Hàng");
        }
    }

    /**
     * Khởi tạo các view components
     */
    private void initViews() {
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
    }

    /**
     * Thiết lập RecyclerView
     */
    private void setupRecyclerView() {
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OwnerOrderAdapter(this, this);
        recyclerViewOrders.setAdapter(orderAdapter);
    }

    /**
     * Load dữ liệu đơn hàng
     */
    private void loadOrderData() {
        billManager = BillManager.getInstance(this);
        // Owner có thể xem tất cả đơn hàng từ tất cả khách hàng
        orderList = billManager.getAllBillsFromAllUsers();
        orderAdapter.updateOrderList(orderList);
        
        // Update title with order count
        updateTitle();
    }

    /**
     * Cập nhật title với số lượng đơn hàng
     */
    private void updateTitle() {
        int totalOrders = orderList != null ? orderList.size() : 0;
        int pendingOrders = 0;
        int completedOrders = 0;
        
        if (orderList != null) {
            for (Bill order : orderList) {
                if (order.isCompleted()) {
                    completedOrders++;
                } else {
                    pendingOrders++;
                }
            }
        }
        
        String title = String.format("Quản Lý Đơn Hàng (%d)", totalOrders);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    /**
     * Chuyển trạng thái đơn hàng lên trạng thái tiếp theo
     */
    private void advanceOrderStatus(Bill order) {
        String nextStatus = order.getNextStatus();
        if (nextStatus != null) {
            // Confirm action for important status changes
            if (Bill.STATUS_CANCELLED.equals(nextStatus) || 
                Bill.STATUS_DELIVERED.equals(nextStatus)) {
                confirmStatusChange(order, nextStatus);
            } else {
                updateOrderStatus(order, nextStatus);
            }
        }
    }

    /**
     * Hiển thị dialog xác nhận thay đổi trạng thái
     */
    private void confirmStatusChange(Bill order, String newStatus) {
        String message;
        if (Bill.STATUS_DELIVERED.equals(newStatus)) {
            message = "Xác nhận đơn hàng #" + order.getBillId() + " đã được giao thành công?";
        } else {
            message = "Xác nhận thay đổi trạng thái đơn hàng #" + order.getBillId() + "?";
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Xác Nhận")
                .setMessage(message)
                .setPositiveButton("Xác Nhận", (dialog, which) -> {
                    updateOrderStatus(order, newStatus);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Cập nhật trạng thái đơn hàng
     */
    private void updateOrderStatus(Bill order, String newStatus) {
        boolean success = billManager.updateBillStatusForOwner(order.getBillId(), newStatus);
        
        if (success) {
            // Get status name for display
            order.setStatus(newStatus); // Temporarily set to get name
            String statusName = order.getStatusName();
            
            showToast("Đã cập nhật đơn hàng #" + order.getBillId() + " thành: " + statusName);
            
            // Reload data to reflect changes
            loadOrderData();
        } else {
            showToast("Lỗi: Không thể cập nhật trạng thái đơn hàng");
        }
    }

    /**
     * Hủy đơn hàng
     */
    private void cancelOrder(Bill order) {
        new AlertDialog.Builder(this)
                .setTitle("Hủy Đơn Hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng #" + order.getBillId() + "?\n\n" +
                           "Thao tác này không thể hoàn tác.")
                .setPositiveButton("Hủy Đơn", (dialog, which) -> {
                    updateOrderStatus(order, Bill.STATUS_CANCELLED);
                })
                .setNegativeButton("Không", null)
                .show();
    }

    /**
     * Hiển thị chi tiết đơn hàng với custom layout
     */
    private void showOrderDetails(Bill order) {
        // Create custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_order_details, null);
        
        // Order Header Section
        TextView tvOrderId = dialogView.findViewById(R.id.tvOrderId);
        TextView tvOrderDate = dialogView.findViewById(R.id.tvOrderDate);
        TextView tvOrderStatus = dialogView.findViewById(R.id.tvOrderStatus);
        
        // Customer Info Section
        TextView tvCustomerName = dialogView.findViewById(R.id.tvCustomerName);
        TextView tvPhone = dialogView.findViewById(R.id.tvPhone);
        TextView tvAddress = dialogView.findViewById(R.id.tvAddress);
        
        // Order Summary Section
        TextView tvItemCount = dialogView.findViewById(R.id.tvItemCount);
        TextView tvTotalAmount = dialogView.findViewById(R.id.tvTotalAmount);
        
        // Order Items Container
        LinearLayout layoutOrderItems = dialogView.findViewById(R.id.layoutOrderItems);
        
        // Set order header
        tvOrderId.setText("Đơn hàng #" + order.getBillId());
        tvOrderDate.setText(order.getFormattedDate());
        tvOrderStatus.setText(order.getStatusName());
        
        // Set status color
        int statusColor = getStatusColor(order.getStatus());
        tvOrderStatus.setTextColor(statusColor);
        
        // Set customer info
        tvCustomerName.setText(order.getFullName());
        tvPhone.setText(order.getPhone());
        tvAddress.setText(order.getDeliveryAddress());
        
        // Set order summary
        tvItemCount.setText(String.valueOf(order.getTotalItemCount()));
        tvTotalAmount.setText(String.format("%.0f₫", order.getTotalAmount()));
        
        // Add order items dynamically
        populateOrderItems(layoutOrderItems, order);
        
        // Show dialog
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Đóng", null)
                .show();
    }

    /**
     * Thêm các món ăn vào layout chi tiết đơn hàng
     */
    private void populateOrderItems(LinearLayout container, Bill order) {
        container.removeAllViews();
        
        // Check if bill has BillItems (preferred) or CartItems
        if (order.getBillItems() != null && !order.getBillItems().isEmpty()) {
            for (Bill.BillItem item : order.getBillItems()) {
                View itemView = LayoutInflater.from(this).inflate(R.layout.item_order_detail_food, container, false);
                
                TextView tvFoodName = itemView.findViewById(R.id.tvFoodName);
                TextView tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
                TextView tvQuantity = itemView.findViewById(R.id.tvQuantity);
                TextView tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
                
                tvFoodName.setText(item.getFoodName());
                tvFoodPrice.setText(String.format("%.0f₫", item.getPrice()));
                tvQuantity.setText(String.valueOf(item.getQuantity()));
                tvItemTotal.setText(String.format("%.0f₫", item.getTotalPrice()));
                
                container.addView(itemView);
            }
        } else if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (int i = 0; i < order.getItems().size(); i++) {
                var item = order.getItems().get(i);
                View itemView = LayoutInflater.from(this).inflate(R.layout.item_order_detail_food, container, false);
                
                TextView tvFoodName = itemView.findViewById(R.id.tvFoodName);
                TextView tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
                TextView tvQuantity = itemView.findViewById(R.id.tvQuantity);
                TextView tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
                
                tvFoodName.setText(item.getFoodItem().getName());
                tvFoodPrice.setText(String.format("%.0f₫", item.getFoodItem().getPrice()));
                tvQuantity.setText(String.valueOf(item.getQuantity()));
                tvItemTotal.setText(String.format("%.0f₫", item.getTotalPrice()));
                
                container.addView(itemView);
            }
        } else {
            // No items found
            TextView noItemsView = new TextView(this);
            noItemsView.setText("Không có thông tin chi tiết món ăn");
            noItemsView.setTextColor(getResources().getColor(android.R.color.darker_gray));
            noItemsView.setPadding(16, 16, 16, 16);
            container.addView(noItemsView);
        }
    }

    /**
     * Lấy màu sắc cho trạng thái đơn hàng
     */
    private int getStatusColor(String status) {
        switch (status) {
            case Bill.STATUS_PENDING:
                return getResources().getColor(android.R.color.holo_orange_dark);
            case Bill.STATUS_CONFIRMED:
                return getResources().getColor(android.R.color.holo_blue_bright);
            case Bill.STATUS_PREPARING:
                return getResources().getColor(android.R.color.holo_purple);
            case Bill.STATUS_READY:
                return getResources().getColor(android.R.color.holo_blue_dark);
            case Bill.STATUS_DELIVERING:
                return getResources().getColor(android.R.color.holo_orange_light);
            case Bill.STATUS_DELIVERED:
                return getResources().getColor(android.R.color.holo_green_dark);
            case Bill.STATUS_CANCELLED:
                return getResources().getColor(android.R.color.holo_red_dark);
            default:
                return getResources().getColor(android.R.color.darker_gray);
        }
    }

    /**
     * Hiển thị Toast message
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.owner_order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_refresh) {
            loadOrderData();
            showToast("Đã làm mới danh sách đơn hàng");
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    // OwnerOrderAdapter.OnOrderActionListener implementations
    @Override
    public void onAdvanceOrderStatus(Bill order) {
        advanceOrderStatus(order);
    }

    @Override
    public void onCancelOrder(Bill order) {
        cancelOrder(order);
    }

    @Override
    public void onViewOrderDetails(Bill order) {
        showOrderDetails(order);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrderData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

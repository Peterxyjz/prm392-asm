package com.example.myapplication.activity.owner;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapter.OwnerOrderAdapter;
import com.example.myapplication.manager.BillManager;
import com.example.myapplication.model.Bill;
import java.util.List;

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
     * Hiển thị chi tiết đơn hàng
     */
    private void showOrderDetails(Bill order) {
        StringBuilder details = new StringBuilder();
        details.append("Đơn hàng #").append(order.getBillId()).append("\n\n");
        details.append("Khách hàng: ").append(order.getFullName()).append("\n");
        details.append("SĐT: ").append(order.getPhone()).append("\n");
        details.append("Địa chỉ: ").append(order.getDeliveryAddress()).append("\n");
        details.append("Ngày đặt: ").append(order.getFormattedDate()).append("\n");
        details.append("Trạng thái: ").append(order.getStatusName()).append("\n");
        details.append("Tổng tiền: ").append(String.format("%.0f₫", order.getTotalAmount())).append("\n\n");
        
        details.append("Chi tiết món:\n");
        if (order.getBillItems() != null && !order.getBillItems().isEmpty()) {
            for (Bill.BillItem item : order.getBillItems()) {
                details.append("• ").append(item.getFoodName())
                       .append(" x").append(item.getQuantity())
                       .append(" = ").append(String.format("%.0f₫", item.getTotalPrice()))
                       .append("\n");
            }
        } else if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (int i = 0; i < order.getItems().size(); i++) {
                var item = order.getItems().get(i);
                details.append("• ").append(item.getFoodItem().getName())
                       .append(" x").append(item.getQuantity())
                       .append(" = ").append(String.format("%.0f₫", item.getTotalPrice()))
                       .append("\n");
            }
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Chi Tiết Đơn Hàng")
                .setMessage(details.toString())
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

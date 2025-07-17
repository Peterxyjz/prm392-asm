package com.example.myapplication.activity.owner;

import android.os.Bundle;
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
    }

    /**
     * Chuyển trạng thái đơn hàng
     */
    private void updateOrderStatus(Bill order, String newStatus) {
        // TODO: Implement actual status update
        order.setStatus(newStatus);
        order.setLastUpdated(new java.util.Date());
        
        // Save to database
        // billManager.updateBill(order);
        
        showToast("Đã cập nhật trạng thái đơn hàng #" + order.getBillId() + " thành: " + order.getStatusName());
        
        // Reload data
        loadOrderData();
    }

    /**
     * Hiển thị Toast message
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // OwnerOrderAdapter.OnOrderActionListener implementations
    @Override
    public void onAcceptOrder(Bill order) {
        updateOrderStatus(order, Bill.STATUS_DELIVERED);
    }

    @Override
    public void onCompleteOrder(Bill order) {
        updateOrderStatus(order, Bill.STATUS_DELIVERED);
    }

    @Override
    public void onViewOrderDetails(Bill order) {
        // TODO: Implement order details view
        showToast("Xem chi tiết đơn hàng #" + order.getBillId());
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

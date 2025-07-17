package com.example.myapplication.activity.owner;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapter.OwnerCustomerAdapter;
import com.example.myapplication.manager.UserManager;
import com.example.myapplication.model.User;
import java.util.ArrayList;
import java.util.List;

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
        
        // TODO: Implement method to get all customers from UserManager
        // For now, create mock data
        customerList = createMockCustomerData();
        customerAdapter.updateCustomerList(customerList);
    }

    /**
     * Tạo dữ liệu khách hàng mock
     * TODO: Thay thế bằng dữ liệu thực từ database
     */
    private List<User> createMockCustomerData() {
        List<User> mockCustomers = new ArrayList<>();
        
        // Mock customer 1
        User customer1 = new User("user1", "user1@gmail.com", "password", 
            "Nguyễn Văn A", "123 Nguyễn Văn Cừ, Q5, TP.HCM", "0123456789");
        customer1.setRole(UserManager.ROLE_CUSTOMER);
        mockCustomers.add(customer1);
        
        // Mock customer 2
        User customer2 = new User("user2", "user2@gmail.com", "password", 
            "Trần Thị B", "456 Lê Lợi, Q1, TP.HCM", "0987654321");
        customer2.setRole(UserManager.ROLE_CUSTOMER);
        mockCustomers.add(customer2);
        
        // Mock customer 3
        User customer3 = new User("user3", "user3@gmail.com", "password", 
            "Lê Văn C", "789 Hai Bà Trưng, Q3, TP.HCM", "0369852147");
        customer3.setRole(UserManager.ROLE_CUSTOMER);
        mockCustomers.add(customer3);
        
        return mockCustomers;
    }

    /**
     * Hiển thị Toast message
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // OwnerCustomerAdapter.OnCustomerClickListener implementations
    @Override
    public void onCustomerClick(User customer) {
        showToast("Xem thông tin chi tiết: " + customer.getFullName());
        // TODO: Implement customer details view
    }

    @Override
    public void onViewOrderHistory(User customer) {
        showToast("Xem lịch sử đơn hàng của: " + customer.getFullName());
        // TODO: Implement customer order history view
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

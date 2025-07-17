package com.example.myapplication.activity.owner;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapter.OwnerMenuAdapter;
import com.example.myapplication.model.FoodItem;
import com.example.myapplication.manager.FoodDataManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

/**
 * OwnerMenuActivity - Quản lý menu cho Owner
 * CRUD món ăn, thiết lập trạng thái còn/hết món
 */
public class OwnerMenuActivity extends AppCompatActivity implements OwnerMenuAdapter.OnMenuItemClickListener {

    // UI Components
    private RecyclerView recyclerViewMenu;
    private FloatingActionButton fabAddFood;
    private OwnerMenuAdapter menuAdapter;
    
    // Data
    private FoodDataManager foodDataManager;
    private List<FoodItem> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_menu);
        
        setupToolbar();
        initViews();
        setupRecyclerView();
        loadMenuData();
    }

    /**
     * Thiết lập toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản Lý Menu");
        }
    }

    /**
     * Khởi tạo các view components
     */
    private void initViews() {
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        fabAddFood = findViewById(R.id.fabAddFood);
        
        // FAB click listener
        fabAddFood.setOnClickListener(v -> showAddFoodDialog());
    }

    /**
     * Thiết lập RecyclerView
     */
    private void setupRecyclerView() {
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));
        menuAdapter = new OwnerMenuAdapter(this, this);
        recyclerViewMenu.setAdapter(menuAdapter);
    }

    /**
     * Load dữ liệu menu
     */
    private void loadMenuData() {
        foodList = FoodDataManager.getAllFoodItems();
        menuAdapter.updateFoodList(foodList);
    }

    /**
     * Hiển thị dialog thêm món ăn mới
     */
    private void showAddFoodDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_food, null);
        
        EditText etName = dialogView.findViewById(R.id.etFoodName);
        EditText etDescription = dialogView.findViewById(R.id.etFoodDescription);
        EditText etPrice = dialogView.findViewById(R.id.etFoodPrice);
        EditText etCategory = dialogView.findViewById(R.id.etFoodCategory);
        Switch switchAvailable = dialogView.findViewById(R.id.switchAvailable);
        
        // Set mặc định available = true
        switchAvailable.setChecked(true);
        
        new AlertDialog.Builder(this)
            .setTitle("Thêm Món Ăn Mới")
            .setView(dialogView)
            .setPositiveButton("Thêm", (dialog, which) -> {
                String name = etName.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();
                String category = etCategory.getText().toString().trim();
                boolean isAvailable = switchAvailable.isChecked();
                
                if (validateInput(name, description, priceStr, category)) {
                    double price = Double.parseDouble(priceStr);
                    addNewFood(name, description, price, category, isAvailable);
                }
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    /**
     * Hiển thị dialog chỉnh sửa món ăn
     */
    private void showEditFoodDialog(FoodItem foodItem) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_food, null);
        
        EditText etName = dialogView.findViewById(R.id.etFoodName);
        EditText etDescription = dialogView.findViewById(R.id.etFoodDescription);
        EditText etPrice = dialogView.findViewById(R.id.etFoodPrice);
        EditText etCategory = dialogView.findViewById(R.id.etFoodCategory);
        Switch switchAvailable = dialogView.findViewById(R.id.switchAvailable);
        
        // Điền thông tin hiện tại
        etName.setText(foodItem.getName());
        etDescription.setText(foodItem.getDescription());
        etPrice.setText(String.valueOf(foodItem.getPrice()));
        etCategory.setText(foodItem.getCategory());
        // TODO: Thêm trạng thái available vào FoodItem model
        switchAvailable.setChecked(true);
        
        new AlertDialog.Builder(this)
            .setTitle("Chỉnh Sửa Món Ăn")
            .setView(dialogView)
            .setPositiveButton("Cập Nhật", (dialog, which) -> {
                String name = etName.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();
                String category = etCategory.getText().toString().trim();
                boolean isAvailable = switchAvailable.isChecked();
                
                if (validateInput(name, description, priceStr, category)) {
                    double price = Double.parseDouble(priceStr);
                    updateFood(foodItem.getId(), name, description, price, category, isAvailable);
                }
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    /**
     * Validate input data
     */
    private boolean validateInput(String name, String description, String priceStr, String category) {
        if (name.isEmpty()) {
            showToast("Vui lòng nhập tên món ăn");
            return false;
        }
        
        if (description.isEmpty()) {
            showToast("Vui lòng nhập mô tả món ăn");
            return false;
        }
        
        if (priceStr.isEmpty()) {
            showToast("Vui lòng nhập giá món ăn");
            return false;
        }
        
        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                showToast("Giá món ăn phải lớn hơn 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showToast("Giá món ăn không hợp lệ");
            return false;
        }
        
        if (category.isEmpty()) {
            showToast("Vui lòng nhập danh mục món ăn");
            return false;
        }
        
        return true;
    }

    /**
     * Thêm món ăn mới
     */
    private void addNewFood(String name, String description, double price, String category, boolean isAvailable) {
        // TODO: Implement actual add food functionality
        // For now, just show a toast
        showToast("Thêm món ăn: " + name + " - " + (isAvailable ? "Còn món" : "Hết món"));
        
        // Reload data
        loadMenuData();
    }

    /**
     * Cập nhật món ăn
     */
    private void updateFood(int foodId, String name, String description, double price, String category, boolean isAvailable) {
        // TODO: Implement actual update food functionality
        showToast("Cập nhật món ăn: " + name + " - " + (isAvailable ? "Còn món" : "Hết món"));
        
        // Reload data
        loadMenuData();
    }

    /**
     * Xóa món ăn
     */
    private void deleteFood(FoodItem foodItem) {
        new AlertDialog.Builder(this)
            .setTitle("Xóa Món Ăn")
            .setMessage("Bạn có chắc chắn muốn xóa món \"" + foodItem.getName() + "\" không?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                // TODO: Implement actual delete functionality
                showToast("Đã xóa món: " + foodItem.getName());
                loadMenuData();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    /**
     * Toggle trạng thái còn/hết món
     */
    private void toggleFoodAvailability(FoodItem foodItem) {
        // TODO: Implement actual toggle functionality
        // For now, just show toast
        boolean newStatus = !true; // Assume current status is available
        showToast(foodItem.getName() + " - " + (newStatus ? "Đã chuyển thành CÒN MÓN" : "Đã chuyển thành HẾT MÓN"));
        
        // Reload data
        loadMenuData();
    }

    /**
     * Hiển thị Toast message
     */
    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    // OwnerMenuAdapter.OnMenuItemClickListener implementations
    @Override
    public void onEditClick(FoodItem foodItem) {
        showEditFoodDialog(foodItem);
    }

    @Override
    public void onDeleteClick(FoodItem foodItem) {
        deleteFood(foodItem);
    }

    @Override
    public void onToggleAvailabilityClick(FoodItem foodItem) {
        toggleFoodAvailability(foodItem);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

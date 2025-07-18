package com.example.myapplication.activity.owner;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapter.OwnerMenuAdapter;
import com.example.myapplication.model.FoodItem;
import com.example.myapplication.manager.FoodDataManager;
import com.example.myapplication.utils.ImageUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Arrays;
import java.util.List;

/**
 * OwnerMenuActivity - Quản lý menu cho Owner
 * CRUD món ăn, thiết lập trạng thái còn/hết món, upload ảnh
 */
public class OwnerMenuActivity extends AppCompatActivity implements OwnerMenuAdapter.OnMenuItemClickListener {

    // UI Components
    private RecyclerView recyclerViewMenu;
    private FloatingActionButton fabAddFood;
    private OwnerMenuAdapter menuAdapter;
    
    // Data
    private List<FoodItem> foodList;
    
    // Image upload
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private ImageView currentImagePreview;
    private Button currentRemoveImageButton;
    private boolean isEditMode = false;
    private FoodItem currentEditingFood = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_menu);
        
        // Initialize data manager
        FoodDataManager.initialize(this);
        
        setupImagePicker();
        setupToolbar();
        initViews();
        setupRecyclerView();
        loadMenuData();
    }

    /**
     * Setup image picker launcher
     */
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null && currentImagePreview != null) {
                        // Hiển thị ảnh đã chọn
                        currentImagePreview.setImageURI(selectedImageUri);
                        
                        // Hiển thị nút xóa ảnh
                        if (currentRemoveImageButton != null) {
                            currentRemoveImageButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        );
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
        isEditMode = false;
        currentEditingFood = null;
        selectedImageUri = null;
        showFoodDialog("Thêm Món Ăn Mới", null);
    }

    /**
     * Hiển thị dialog chỉnh sửa món ăn
     */
    private void showEditFoodDialog(FoodItem foodItem) {
        isEditMode = true;
        currentEditingFood = foodItem;
        selectedImageUri = null;
        showFoodDialog("Chỉnh Sửa Món Ăn", foodItem);
    }

    /**
     * Hiển thị dialog thêm/sửa món ăn
     */
    private void showFoodDialog(String title, FoodItem foodItem) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_food, null);
        
        // Find views
        ImageView ivImagePreview = dialogView.findViewById(R.id.ivImagePreview);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnRemoveImage = dialogView.findViewById(R.id.btnRemoveImage);
        EditText etName = dialogView.findViewById(R.id.etFoodName);
        EditText etDescription = dialogView.findViewById(R.id.etFoodDescription);
        EditText etPrice = dialogView.findViewById(R.id.etFoodPrice);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);
        Switch switchAvailable = dialogView.findViewById(R.id.switchAvailable);
        
        // Set current references for image picker
        currentImagePreview = ivImagePreview;
        currentRemoveImageButton = btnRemoveImage;
        
        // Setup category spinner
        setupCategorySpinner(spinnerCategory);
        
        // Setup image selection
        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnRemoveImage.setOnClickListener(v -> {
            selectedImageUri = null;
            if (isEditMode && currentEditingFood != null) {
                // Load ảnh mặc định khi xóa ảnh custom
                ivImagePreview.setImageResource(currentEditingFood.getImageResource());
            } else {
                ivImagePreview.setImageResource(R.drawable.ramen);
            }
            btnRemoveImage.setVisibility(View.GONE);
        });
        
        // Điền thông tin nếu đang edit
        if (foodItem != null) {
            etName.setText(foodItem.getName());
            etDescription.setText(foodItem.getDescription());
            etPrice.setText(String.valueOf(foodItem.getPrice()));
            switchAvailable.setChecked(foodItem.isAvailable());
            
            // Set category
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCategory.getAdapter();
            int position = adapter.getPosition(foodItem.getCategory());
            if (position >= 0) {
                spinnerCategory.setSelection(position);
            }
            
            // Load ảnh hiện tại
            if (foodItem.hasCustomImage() && ImageUtils.imageExists(foodItem.getImageUrl())) {
                Bitmap customBitmap = ImageUtils.loadBitmapFromPath(foodItem.getImageUrl());
                if (customBitmap != null) {
                    ivImagePreview.setImageBitmap(customBitmap);
                    btnRemoveImage.setVisibility(View.VISIBLE);
                }
            } else {
                ivImagePreview.setImageResource(foodItem.getImageResource());
            }
        } else {
            // Mặc định cho add new
            switchAvailable.setChecked(true);
            ivImagePreview.setImageResource(R.drawable.ramen);
        }
        
        // Tạo dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton(isEditMode ? "Cập Nhật" : "Thêm", null)
            .setNegativeButton("Hủy", null)
            .create();
            
        dialog.show();
        
        // Override positive button để validate
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String category = (String) spinnerCategory.getSelectedItem();
            boolean isAvailable = switchAvailable.isChecked();
            
            if (validateInput(name, description, priceStr, category)) {
                double price = Double.parseDouble(priceStr);
                
                if (isEditMode) {
                    updateFood(currentEditingFood.getId(), name, description, price, category, isAvailable);
                } else {
                    addNewFood(name, description, price, category, isAvailable);
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * Setup category spinner
     */
    private void setupCategorySpinner(Spinner spinner) {
        List<String> categories = Arrays.asList("Noodles", "Sushi", "Rice", "Appetizer");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Mở image picker
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh món ăn"));
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
        
        if (category == null || category.isEmpty()) {
            showToast("Vui lòng chọn danh mục món ăn");
            return false;
        }
        
        return true;
    }

    /**
     * Thêm món ăn mới
     */
    private void addNewFood(String name, String description, double price, String category, boolean isAvailable) {
        try {
            String imageUrl = null;
            
            // Lưu ảnh nếu có
            if (selectedImageUri != null) {
                // Tạo temporary ID để lưu ảnh
                int tempId = (int) System.currentTimeMillis();
                imageUrl = ImageUtils.saveImageToInternalStorage(this, selectedImageUri, tempId);
                
                if (imageUrl == null) {
                    showToast("Không thể lưu ảnh. Món ăn sẽ được thêm với ảnh mặc định.");
                }
            }
            
            // Thêm vào database
            boolean success = FoodDataManager.addFoodItem(name, description, price, category, isAvailable, imageUrl);
            
            if (success) {
                showToast("Đã thêm món ăn: " + name);
                loadMenuData(); // Refresh data
            } else {
                showToast("Lỗi khi thêm món ăn");
                // Xóa ảnh đã lưu nếu thêm món ăn thất bại
                if (imageUrl != null) {
                    ImageUtils.deleteImage(imageUrl);
                }
            }
        } catch (Exception e) {
            showToast("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Cập nhật món ăn
     */
    private void updateFood(int foodId, String name, String description, double price, String category, boolean isAvailable) {
        try {
            String imageUrl = null;
            
            // Xử lý ảnh
            if (selectedImageUri != null) {
                // Có ảnh mới được chọn
                imageUrl = ImageUtils.saveImageToInternalStorage(this, selectedImageUri, foodId);
                
                if (imageUrl == null) {
                    showToast("Không thể lưu ảnh mới. Sẽ giữ ảnh cũ.");
                    // Giữ ảnh cũ nếu có
                    if (currentEditingFood != null) {
                        imageUrl = currentEditingFood.getImageUrl();
                    }
                } else {
                    // Xóa ảnh cũ nếu có
                    if (currentEditingFood != null && currentEditingFood.hasCustomImage()) {
                        ImageUtils.deleteImage(currentEditingFood.getImageUrl());
                    }
                }
            } else {
                // Không có ảnh mới, giữ ảnh cũ
                if (currentEditingFood != null) {
                    imageUrl = currentEditingFood.getImageUrl();
                }
            }
            
            // Cập nhật database
            boolean success = FoodDataManager.updateFoodItem(foodId, name, description, price, category, isAvailable, imageUrl);
            
            if (success) {
                showToast("Đã cập nhật món ăn: " + name);
                loadMenuData(); // Refresh data
            } else {
                showToast("Lỗi khi cập nhật món ăn");
            }
        } catch (Exception e) {
            showToast("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Xóa món ăn
     */
    private void deleteFood(FoodItem foodItem) {
        new AlertDialog.Builder(this)
            .setTitle("Xóa Món Ăn")
            .setMessage("Bạn có chắc chắn muốn xóa món \"" + foodItem.getName() + "\" không?\n\nHành động này không thể hoàn tác.")
            .setPositiveButton("Xóa", (dialog, which) -> {
                try {
                    // Xóa ảnh custom nếu có
                    if (foodItem.hasCustomImage()) {
                        ImageUtils.deleteImage(foodItem.getImageUrl());
                    }
                    
                    // Xóa khỏi database
                    boolean success = FoodDataManager.deleteFoodItem(foodItem.getId());
                    
                    if (success) {
                        showToast("Đã xóa món: " + foodItem.getName());
                        loadMenuData(); // Refresh data
                    } else {
                        showToast("Lỗi khi xóa món ăn");
                    }
                } catch (Exception e) {
                    showToast("Lỗi: " + e.getMessage());
                }
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    /**
     * Toggle trạng thái còn/hết món
     */
    private void toggleFoodAvailability(FoodItem foodItem) {
        try {
            boolean success = FoodDataManager.toggleFoodAvailability(foodItem.getId());
            
            if (success) {
                boolean newStatus = !foodItem.isAvailable();
                showToast(foodItem.getName() + " - " + (newStatus ? "Đã chuyển thành CÒN MÓN" : "Đã chuyển thành HẾT MÓN"));
                loadMenuData(); // Refresh data
            } else {
                showToast("Lỗi khi thay đổi trạng thái món ăn");
            }
        } catch (Exception e) {
            showToast("Lỗi: " + e.getMessage());
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear references để tránh memory leak
        currentImagePreview = null;
        currentRemoveImageButton = null;
        selectedImageUri = null;
    }
}

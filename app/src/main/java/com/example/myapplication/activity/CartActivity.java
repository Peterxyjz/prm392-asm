package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.CartAdapter;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.manager.UserManager;
import com.example.myapplication.manager.BillManager;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.User;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * CartActivity - Màn hình quản lý giỏ hàng
 * Hiển thị danh sách món đã chọn, tính toán tổng tiền và xử lý thanh toán
 */
public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartUpdateListener {
    // Khai báo các view components
    private RecyclerView recyclerViewCart;
    private LinearLayout layoutEmptyCart, layoutCartContent;
    private TextView tvDeliveryAddress, tvSubtotal, tvDeliveryFee, tvTotal;
    private Button btnCheckout;
    private ImageButton btnBackFromCart;

    // Managers và Adapter
    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private UserManager userManager;
    private BillManager billManager;
    private NumberFormat formatter;

    // Constants cho tính toán phí giao hàng
    private static final double DELIVERY_FEE = 15000;           // Phí giao hàng cố định
    private static final double FREE_DELIVERY_THRESHOLD = 100000; // Ngưỡng miễn phí giao hàng

    /**
     * Hàm được gọi khi Activity được tạo
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();                        // Khởi tạo các view
        cartManager = CartManager.getInstance(this);   // Khởi tạo CartManager
        userManager = UserManager.getInstance(this);   // Khởi tạo UserManager
        billManager = BillManager.getInstance(this);   // Khởi tạo BillManager
        formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")); // Format tiền VND

        setupRecyclerView();               // Thiết lập RecyclerView
        setupClickListeners();            // Thiết lập sự kiện click
        updateCartDisplay();              // Cập nhật hiển thị giỏ hàng
        loadUserAddress();                // Load địa chỉ giao hàng
    }

    /**
     * Khởi tạo và liên kết các view components với layout
     */
    private void initViews() {
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
        layoutCartContent = findViewById(R.id.layoutCartContent);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvTotal = findViewById(R.id.tvTotal);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBackFromCart = findViewById(R.id.btnBackFromCart);
    }

    /**
     * Thiết lập RecyclerView với CartAdapter
     */
    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(cartManager.getCartItems(), cartManager);
        cartAdapter.setOnCartUpdateListener(this); // Set listener để nhận thông báo khi giỏ hàng thay đổi
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCart.setAdapter(cartAdapter);
    }

    /**
     * Thiết lập sự kiện click cho các nút
     */
    private void setupClickListeners() {
        // Nút back - quay lại màn hình trước
        btnBackFromCart.setOnClickListener(v -> finish());
        
        // Nút thanh toán
        btnCheckout.setOnClickListener(v -> performCheckout());
        
        // Click vào địa chỉ giao hàng để chỉnh sửa
        tvDeliveryAddress.setOnClickListener(v -> showAddressDialog());
    }

    /**
     * Load địa chỉ giao hàng từ thông tin user hiện tại
     */
    private void loadUserAddress() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null && !currentUser.getAddress().isEmpty()) {
            tvDeliveryAddress.setText(currentUser.getAddress());
        }
    }

    /**
     * Hiển thị dialog để cập nhật địa chỉ giao hàng
     */
    private void showAddressDialog() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser == null) return;

        // Tạo EditText để nhập địa chỉ mới
        android.widget.EditText editText = new android.widget.EditText(this);
        editText.setText(currentUser.getAddress());
        editText.setHint("Nhập địa chỉ giao hàng");

        // Hiển thị AlertDialog
        new AlertDialog.Builder(this)
            .setTitle("Cập nhật địa chỉ giao hàng")
            .setView(editText)
            .setPositiveButton("Cập nhật", (dialog, which) -> {
                String newAddress = editText.getText().toString().trim();
                if (!newAddress.isEmpty()) {
                    // Cập nhật địa chỉ trong UserManager
                    userManager.updateUserInfo(currentUser.getFullName(), newAddress, currentUser.getPhone());
                    tvDeliveryAddress.setText(newAddress);
                    Toast.makeText(this, "Đã cập nhật địa chỉ", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    /**
     * Cập nhật hiển thị giỏ hàng - hiện empty state hoặc danh sách món
     */
    private void updateCartDisplay() {
        List<CartItem> cartItems = cartManager.getCartItems();
        
        if (cartItems.isEmpty()) {
            // Giỏ hàng trống - hiển thị empty state
            layoutEmptyCart.setVisibility(View.VISIBLE);
            layoutCartContent.setVisibility(View.GONE);
        } else {
            // Có món trong giỏ - hiển thị danh sách và tính toán
            layoutEmptyCart.setVisibility(View.GONE);
            layoutCartContent.setVisibility(View.VISIBLE);
            
            cartAdapter.updateCartItems(cartItems); // Cập nhật adapter
            updatePriceDisplay();                   // Cập nhật hiển thị giá
        }
    }

    /**
     * Cập nhật hiển thị giá tiền (tạm tính, phí giao hàng, tổng cộng)
     */
    private void updatePriceDisplay() {
        double subtotal = cartManager.getTotalPrice();
        // Tính phí giao hàng: miễn phí nếu >= 100k, 15k nếu < 100k
        double deliveryFee = subtotal >= FREE_DELIVERY_THRESHOLD ? 0 : DELIVERY_FEE;
        double total = subtotal + deliveryFee;

        // Cập nhật UI
        tvSubtotal.setText(formatter.format(subtotal).replace("₫", "₫"));
        
        if (deliveryFee == 0) {
            tvDeliveryFee.setText("Miễn phí");
            tvDeliveryFee.setTextColor(getColor(R.color.success_green));
        } else {
            tvDeliveryFee.setText(formatter.format(deliveryFee).replace("₫", "₫"));
            tvDeliveryFee.setTextColor(getColor(R.color.text_secondary));
        }
        
        tvTotal.setText(formatter.format(total).replace("₫", "₫"));
    }

    /**
     * Xử lý quá trình thanh toán
     * Kiểm tra điều kiện và hiển thị dialog xác nhận
     */
    private void performCheckout() {
        // Kiểm tra giỏ hàng không rỗng
        if (cartManager.getCartItems().isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra địa chỉ giao hàng đã được cập nhật
        User currentUser = userManager.getCurrentUser();
        if (currentUser == null || currentUser.getAddress().equals("Nhập địa chỉ giao hàng")) {
            Toast.makeText(this, "Vui lòng cập nhật địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            showAddressDialog(); // Hiển thị dialog cập nhật địa chỉ
            return;
        }

        // Tính tổng tiền cuối cùng
        double total = cartManager.getTotalPrice() + 
                      (cartManager.getTotalPrice() >= FREE_DELIVERY_THRESHOLD ? 0 : DELIVERY_FEE);

        // Hiển thị dialog xác nhận đơn hàng
        new AlertDialog.Builder(this)
            .setTitle("Xác nhận đặt hàng")
            .setMessage("Tổng tiền: " + formatter.format(total).replace("₫", "₫") + 
                       "\nGiao hàng đến: " + currentUser.getAddress() + 
                       "\n\nBạn có muốn đặt hàng?")
            .setPositiveButton("Đặt hàng", (dialog, which) -> {
                // Tạo Bill trước khi clear cart
                billManager.createBill(
                    currentUser.getUsername(),
                    cartManager.getCartItems(),
                    total,
                    currentUser.getAddress(),
                    currentUser.getPhone(),
                    currentUser.getFullName()
                );
                
                // Xử lý khi người dùng xác nhận đặt hàng
                cartManager.clearCart(); // Xóa toàn bộ giỏ hàng
                
                Toast.makeText(this, "Đặt hàng thành công! Đơn hàng sẽ được giao trong 30-45 phút.", 
                              Toast.LENGTH_LONG).show();
                
                // Quay về MainActivity và xóa tất cả activity khác khỏi stack
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    /**
     * Callback được gọi khi giỏ hàng được cập nhật từ CartAdapter
     * Implement từ CartAdapter.OnCartUpdateListener
     */
    @Override
    public void onCartUpdated() {
        updateCartDisplay(); // Cập nhật lại hiển thị giỏ hàng
    }

    /**
     * Hàm được gọi khi Activity trở lại foreground
     * Cập nhật lại dữ liệu để đảm bảo consistency
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateCartDisplay(); // Refresh dữ liệu giỏ hàng
        
        // Force refresh adapter to ensure correct images
        if (cartAdapter != null) {
            cartAdapter.updateCartItems(cartManager.getCartItems());
            cartAdapter.notifyDataSetChanged();
        }
    }
}
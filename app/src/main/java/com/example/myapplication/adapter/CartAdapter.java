package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.model.CartItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter cho RecyclerView hiển thị danh sách món ăn trong giỏ hàng
 * Quản lý việc hiển thị và tương tác với từng món trong CartActivity
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;          // Danh sách món trong giỏ hàng
    private CartManager cartManager;           // Manager quản lý giỏ hàng
    private OnCartUpdateListener onCartUpdateListener; // Listener để thông báo khi giỏ hàng thay đổi

    /**
     * Interface để thông báo khi giỏ hàng được cập nhật
     */
    public interface OnCartUpdateListener {
        void onCartUpdated();
    }

    /**
     * Constructor khởi tạo adapter
     * @param cartItems Danh sách món trong giỏ hàng
     * @param cartManager Manager quản lý giỏ hàng
     */
    public CartAdapter(List<CartItem> cartItems, CartManager cartManager) {
        this.cartItems = cartItems;
        this.cartManager = cartManager;
    }

    /**
     * Thiết lập listener để nhận thông báo khi giỏ hàng thay đổi
     * @param listener OnCartUpdateListener
     */
    public void setOnCartUpdateListener(OnCartUpdateListener listener) {
        this.onCartUpdateListener = listener;
    }

    /**
     * Tạo ViewHolder mới cho item
     * @param parent ViewGroup chứa RecyclerView
     * @param viewType Loại view (không sử dụng trong trường hợp này)
     * @return CartViewHolder mới được tạo
     */
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    /**
     * Bind dữ liệu vào ViewHolder tại vị trí cụ thể
     * @param holder ViewHolder cần bind dữ liệu
     * @param position Vị trí của item trong danh sách
     */
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    /**
     * @return Số lượng item trong danh sách
     */
    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    /**
     * Cập nhật danh sách món trong giỏ hàng
     * @param newCartItems Danh sách món mới
     */
    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged(); // Thông báo RecyclerView cập nhật giao diện
    }

    /**
     * ViewHolder class quản lý view của từng item trong giỏ hàng
     */
    class CartViewHolder extends RecyclerView.ViewHolder {
        // Khai báo các view components trong item layout
        private ImageView ivCartFoodImage;
        private TextView tvCartFoodName, tvCartFoodDescription, tvCartFoodPrice;
        private TextView tvCartQuantity, tvCartItemTotal;
        private ImageButton btnCartDecrease, btnCartIncrease;

        /**
         * Constructor khởi tạo ViewHolder và liên kết các view
         * @param itemView View của item
         */
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCartFoodImage = itemView.findViewById(R.id.ivCartFoodImage);
            tvCartFoodName = itemView.findViewById(R.id.tvCartFoodName);
            tvCartFoodDescription = itemView.findViewById(R.id.tvCartFoodDescription);
            tvCartFoodPrice = itemView.findViewById(R.id.tvCartFoodPrice);
            tvCartQuantity = itemView.findViewById(R.id.tvCartQuantity);
            tvCartItemTotal = itemView.findViewById(R.id.tvCartItemTotal);
            btnCartDecrease = itemView.findViewById(R.id.btnCartDecrease);
            btnCartIncrease = itemView.findViewById(R.id.btnCartIncrease);
        }

        /**
         * Bind dữ liệu món ăn trong giỏ hàng vào view và thiết lập sự kiện
         * @param cartItem Thông tin món ăn trong giỏ hàng
         */
        public void bind(CartItem cartItem) {
            // Format tiền VND
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            
            // Debug: Log thông tin món ăn
            android.util.Log.d("CartAdapter", "Binding item: " + cartItem.getFoodItem().getName() + 
                ", Image Resource: " + cartItem.getFoodItem().getImageResource());
            
            // Hiển thị thông tin chi tiết món ăn
            // Clear previous image first
            ivCartFoodImage.setImageDrawable(null);
            ivCartFoodImage.setImageResource(cartItem.getFoodItem().getImageResource());
            tvCartFoodName.setText(cartItem.getFoodItem().getName());
            tvCartFoodDescription.setText(cartItem.getFoodItem().getDescription());
            tvCartFoodPrice.setText(formatter.format(cartItem.getFoodItem().getPrice()).replace("₫", "₫/món"));
            tvCartQuantity.setText(String.valueOf(cartItem.getQuantity()));
            tvCartItemTotal.setText(formatter.format(cartItem.getTotalPrice()).replace("₫", "₫"));

            // Sự kiện click nút "+" - tăng số lượng
            btnCartIncrease.setOnClickListener(v -> {
                cartManager.updateQuantity(cartItem.getFoodItem().getId(), cartItem.getQuantity() + 1);
                cartItem.setQuantity(cartItem.getQuantity() + 1); // Cập nhật local object
                notifyItemChanged(getAdapterPosition()); // Cập nhật UI item này
                if (onCartUpdateListener != null) {
                    onCartUpdateListener.onCartUpdated(); // Thông báo activity cập nhật tổng tiền
                }
            });

            // Sự kiện click nút "-" - giảm số lượng hoặc xóa khỏi giỏ hàng
            btnCartDecrease.setOnClickListener(v -> {
                if (cartItem.getQuantity() == 1) {
                    // Nếu số lượng = 1, hiển thị dialog xác nhận xóa
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("Xóa món ăn")
                           .setMessage("Bạn có muốn xóa món này khỏi giỏ hàng?")
                           .setPositiveButton("Xóa", (dialog, which) -> {
                               // User xác nhận xóa
                               cartManager.removeFromCart(cartItem.getFoodItem().getId());
                               cartItems.remove(getAdapterPosition()); // Xóa khỏi local list
                               notifyItemRemoved(getAdapterPosition()); // Animate removal
                               notifyItemRangeChanged(getAdapterPosition(), cartItems.size()); // Update positions
                               if (onCartUpdateListener != null) {
                                   onCartUpdateListener.onCartUpdated(); // Thông báo activity
                               }
                           })
                           .setNegativeButton("Hủy", null) // User hủy - không làm gì
                           .show();
                } else {
                    // Nếu số lượng > 1, chỉ giảm số lượng
                    cartManager.updateQuantity(cartItem.getFoodItem().getId(), cartItem.getQuantity() - 1);
                    cartItem.setQuantity(cartItem.getQuantity() - 1); // Cập nhật local object
                    notifyItemChanged(getAdapterPosition()); // Cập nhật UI
                    if (onCartUpdateListener != null) {
                        onCartUpdateListener.onCartUpdated(); // Thông báo activity
                    }
                }
            });
        }
    }
}
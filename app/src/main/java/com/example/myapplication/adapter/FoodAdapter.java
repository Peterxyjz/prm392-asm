package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.activity.ProductDetailActivity;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.FoodItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter cho RecyclerView hiển thị danh sách món ăn trong MenuActivity
 * Quản lý việc hiển thị và tương tác với từng món ăn
 */
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private List<FoodItem> foodItems;          // Danh sách món ăn cần hiển thị
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
     * @param foodItems Danh sách món ăn
     * @param cartManager Manager quản lý giỏ hàng
     */
    public FoodAdapter(List<FoodItem> foodItems, CartManager cartManager) {
        this.foodItems = foodItems;
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
     * @return FoodViewHolder mới được tạo
     */
    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    /**
     * Bind dữ liệu vào ViewHolder tại vị trí cụ thể
     * @param holder ViewHolder cần bind dữ liệu
     * @param position Vị trí của item trong danh sách
     */
    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.bind(foodItem);
    }

    /**
     * @return Số lượng item trong danh sách
     */
    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    /**
     * Cập nhật danh sách món ăn mới (khi filter theo category)
     * @param newFoodItems Danh sách món ăn mới
     */
    public void updateFoodItems(List<FoodItem> newFoodItems) {
        this.foodItems = newFoodItems;
        notifyDataSetChanged(); // Thông báo RecyclerView cập nhật giao diện
    }

    /**
     * ViewHolder class quản lý view của từng item món ăn
     */
    class FoodViewHolder extends RecyclerView.ViewHolder {
        // Khai báo các view components trong item layout
        private ImageView ivFoodImage;
        private TextView tvFoodName, tvFoodDescription, tvFoodPrice;
        private Button btnAdd;                    // Nút thêm vào giỏ hàng (hiện khi chưa có trong giỏ)
        private LinearLayout layoutQuantityControls; // Layout chứa nút +/- (hiện khi đã có trong giỏ)
        private ImageButton btnDecrease, btnIncrease;
        private TextView tvQuantity;

        /**
         * Constructor khởi tạo ViewHolder và liên kết các view
         * @param itemView View của item
         */
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodDescription = itemView.findViewById(R.id.tvFoodDescription);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            layoutQuantityControls = itemView.findViewById(R.id.layoutQuantityControls);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }

        /**
         * Bind dữ liệu món ăn vào view và thiết lập sự kiện
         * @param foodItem Thông tin món ăn cần hiển thị
         */
        public void bind(FoodItem foodItem) {
            if (foodItem == null) {
                android.util.Log.e("FoodAdapter", "FoodItem is null in bind method");
                return;
            }
            
            // Hiển thị thông tin cơ bản của món ăn
            try {
                ivFoodImage.setImageResource(foodItem.getImageResource());
            } catch (Exception e) {
                android.util.Log.e("FoodAdapter", "Error setting image resource: " + e.getMessage());
                ivFoodImage.setImageResource(R.drawable.ramen); // Default image
            }
            
            tvFoodName.setText(foodItem.getName() != null ? foodItem.getName() : "Unknown Food");
            tvFoodDescription.setText(foodItem.getDescription() != null ? foodItem.getDescription() : "");
            
            // Format giá tiền theo định dạng VND - Safe formatting
            try {
                NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                String formattedPrice = formatter.format(foodItem.getPrice()) + "₫";
                tvFoodPrice.setText(formattedPrice);
            } catch (Exception e) {
                // Fallback formatting if NumberFormat fails
                tvFoodPrice.setText(String.format("%.0f₫", foodItem.getPrice()));
            }

            // Kiểm tra món ăn đã có trong giỏ hàng chưa để hiển thị UI phù hợp
            CartItem cartItem = cartManager.getCartItem(foodItem.getId());
            if (cartItem != null) {
                // Món đã có trong giỏ -> hiển thị controls điều chỉnh số lượng
                layoutQuantityControls.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.GONE);
                tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
            } else {
                // Món chưa có trong giỏ -> hiển thị nút "THÊM"
                layoutQuantityControls.setVisibility(View.GONE);
                btnAdd.setVisibility(View.VISIBLE);
            }

            // Sự kiện click vào item -> mở Product Detail
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                intent.putExtra("food_id", foodItem.getId());
                itemView.getContext().startActivity(intent);
            });

            // Sự kiện click nút "THÊM" - thêm món vào giỏ hàng lần đầu
            btnAdd.setOnClickListener(v -> {
                cartManager.addToCart(foodItem, 1);
                notifyItemChanged(getAdapterPosition()); // Cập nhật item này
                if (onCartUpdateListener != null) {
                    onCartUpdateListener.onCartUpdated(); // Thông báo giỏ hàng đã thay đổi
                }
            });

            // Sự kiện click nút "+" - tăng số lượng
            btnIncrease.setOnClickListener(v -> {
                CartItem currentCartItem = cartManager.getCartItem(foodItem.getId());
                if (currentCartItem != null) {
                    cartManager.updateQuantity(foodItem.getId(), currentCartItem.getQuantity() + 1);
                    notifyItemChanged(getAdapterPosition());
                    if (onCartUpdateListener != null) {
                        onCartUpdateListener.onCartUpdated();
                    }
                }
            });

            // Sự kiện click nút "-" - giảm số lượng hoặc xóa khỏi giỏ hàng
            btnDecrease.setOnClickListener(v -> {
                CartItem currentCartItem = cartManager.getCartItem(foodItem.getId());
                if (currentCartItem != null) {
                    if (currentCartItem.getQuantity() == 1) {
                        // Nếu số lượng = 1, hỏi xác nhận xóa khỏi giỏ hàng
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(itemView.getContext());
                        builder.setTitle("Xóa món ăn")
                               .setMessage("Bạn có muốn xóa món này khỏi giỏ hàng?")
                               .setPositiveButton("Xóa", (dialog, which) -> {
                                   cartManager.removeFromCart(foodItem.getId());
                                   notifyItemChanged(getAdapterPosition());
                                   if (onCartUpdateListener != null) {
                                       onCartUpdateListener.onCartUpdated();
                                   }
                               })
                               .setNegativeButton("Hủy", null)
                               .show();
                    } else {
                        // Nếu số lượng > 1, chỉ giảm số lượng
                        cartManager.updateQuantity(foodItem.getId(), currentCartItem.getQuantity() - 1);
                        notifyItemChanged(getAdapterPosition());
                        if (onCartUpdateListener != null) {
                            onCartUpdateListener.onCartUpdated();
                        }
                    }
                }
            });
        }
    }
}
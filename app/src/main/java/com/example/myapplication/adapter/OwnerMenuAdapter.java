package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.FoodItem;
import com.example.myapplication.utils.PriceUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * OwnerMenuAdapter - Adapter cho RecyclerView trong OwnerMenuActivity
 * Hiển thị danh sách món ăn với các nút quản lý cho Owner
 */
public class OwnerMenuAdapter extends RecyclerView.Adapter<OwnerMenuAdapter.MenuViewHolder> {

    public interface OnMenuItemClickListener {
        void onEditClick(FoodItem foodItem);
        void onDeleteClick(FoodItem foodItem);
        void onToggleAvailabilityClick(FoodItem foodItem);
    }

    private Context context;
    private List<FoodItem> foodList;
    private OnMenuItemClickListener listener;

    public OwnerMenuAdapter(Context context, OnMenuItemClickListener listener) {
        this.context = context;
        this.foodList = new ArrayList<>();
        this.listener = listener;
    }

    public void updateFoodList(List<FoodItem> newFoodList) {
        this.foodList.clear();
        if (newFoodList != null) {
            this.foodList.addAll(newFoodList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_owner_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        FoodItem foodItem = foodList.get(position);
        holder.bind(foodItem);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivFoodImage;
        private TextView tvFoodName, tvFoodDescription, tvFoodPrice, tvFoodCategory, tvAvailabilityStatus;
        private Button btnEdit, btnDelete, btnToggleAvailability;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodDescription = itemView.findViewById(R.id.tvFoodDescription);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvFoodCategory = itemView.findViewById(R.id.tvFoodCategory);
            tvAvailabilityStatus = itemView.findViewById(R.id.tvAvailabilityStatus);
            
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnToggleAvailability = itemView.findViewById(R.id.btnToggleAvailability);
        }

        public void bind(FoodItem foodItem) {
            // Set food information
            tvFoodName.setText(foodItem.getName());
            tvFoodDescription.setText(foodItem.getDescription());
            tvFoodPrice.setText(PriceUtils.formatPrice(foodItem.getPrice()));
            tvFoodCategory.setText(foodItem.getCategory());
            
            // Set food image
            ivFoodImage.setImageResource(foodItem.getImageResource());
            
            // TODO: Hiển thị trạng thái available từ database
            // For now, mock data
            boolean isAvailable = true; // Mock data
            updateAvailabilityStatus(isAvailable);
            
            // Set click listeners
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(foodItem);
                }
            });
            
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(foodItem);
                }
            });
            
            btnToggleAvailability.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onToggleAvailabilityClick(foodItem);
                }
            });
        }

        private void updateAvailabilityStatus(boolean isAvailable) {
            if (isAvailable) {
                tvAvailabilityStatus.setText("CÒN MÓN");
                tvAvailabilityStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                btnToggleAvailability.setText("Đặt Hết Món");
                btnToggleAvailability.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                tvAvailabilityStatus.setText("HẾT MÓN");
                tvAvailabilityStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                btnToggleAvailability.setText("Đặt Còn Món");
                btnToggleAvailability.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
            }
        }
    }
}

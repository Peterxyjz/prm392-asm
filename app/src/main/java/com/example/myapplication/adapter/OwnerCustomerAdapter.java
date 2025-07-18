package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.manager.BillManager;
import com.example.myapplication.model.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * OwnerCustomerAdapter - Adapter cho RecyclerView trong OwnerCustomerActivity
 * Hiển thị danh sách khách hàng với thông tin chi tiết cho Owner
 */
public class OwnerCustomerAdapter extends RecyclerView.Adapter<OwnerCustomerAdapter.CustomerViewHolder> {

    public interface OnCustomerClickListener {
        void onCustomerClick(User customer);
        void onViewOrderHistory(User customer);
    }

    private Context context;
    private List<User> customerList;
    private OnCustomerClickListener listener;
    private BillManager billManager;

    public OwnerCustomerAdapter(Context context, OnCustomerClickListener listener) {
        this.context = context;
        this.customerList = new ArrayList<>();
        this.listener = listener;
        this.billManager = BillManager.getInstance(context);
    }

    public void updateCustomerList(List<User> newCustomerList) {
        this.customerList.clear();
        if (newCustomerList != null) {
            this.customerList.addAll(newCustomerList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_owner_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        User customer = customerList.get(position);
        holder.bind(customer);
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    class CustomerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCustomerName, tvCustomerEmail, tvCustomerPhone, tvCustomerAddress, tvJoinDate, tvOrderCount, tvTotalSpent;
        private Button btnViewHistory, btnViewDetails;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerEmail = itemView.findViewById(R.id.tvCustomerEmail);
            tvCustomerPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvCustomerAddress = itemView.findViewById(R.id.tvCustomerAddress);
            tvJoinDate = itemView.findViewById(R.id.tvJoinDate);
            tvOrderCount = itemView.findViewById(R.id.tvOrderCount);
            tvTotalSpent = itemView.findViewById(R.id.tvTotalSpent);
            
            btnViewHistory = itemView.findViewById(R.id.btnViewHistory);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }

        public void bind(User customer) {
            // Set customer information
            tvCustomerName.setText(customer.getFullName().isEmpty() ? customer.getUsername() : customer.getFullName());
            tvCustomerEmail.setText(customer.getEmail().isEmpty() ? "Chưa có email" : customer.getEmail());
            tvCustomerPhone.setText(customer.getPhone().isEmpty() ? "Chưa có số điện thoại" : customer.getPhone());
            tvCustomerAddress.setText(customer.getAddress());
            
            // Format join date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String joinDate = sdf.format(new Date(customer.getCreatedDate()));
            tvJoinDate.setText("Tham gia: " + joinDate);
            
            // Get actual order count and total spent from BillManager
            int orderCount = billManager.getBillCountByUsername(customer.getUsername());
            double totalSpent = billManager.getTotalSpentByUsername(customer.getUsername());
            
            tvOrderCount.setText(orderCount + " đơn hàng");
            
            // Show total spent if available
            if (tvTotalSpent != null) {
                if (totalSpent > 0) {
                    tvTotalSpent.setText(String.format("%.0f₫", totalSpent));
                } else {
                    tvTotalSpent.setText("0₫");
                }
            }
            
            // Set click listeners
            btnViewDetails.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCustomerClick(customer);
                }
            });
            
            btnViewHistory.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewOrderHistory(customer);
                }
            });
            
            // Click on entire item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCustomerClick(customer);
                }
            });
        }
    }
}

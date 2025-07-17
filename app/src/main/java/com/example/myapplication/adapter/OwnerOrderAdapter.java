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
import com.example.myapplication.model.Bill;
import com.example.myapplication.utils.PriceUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * OwnerOrderAdapter - Adapter cho RecyclerView trong OwnerOrderActivity
 * Hiển thị danh sách đơn hàng với các nút quản lý cho Owner
 */
public class OwnerOrderAdapter extends RecyclerView.Adapter<OwnerOrderAdapter.OrderViewHolder> {

    public interface OnOrderActionListener {
        void onAcceptOrder(Bill order);
        void onCompleteOrder(Bill order);
        void onViewOrderDetails(Bill order);
    }

    private Context context;
    private List<Bill> orderList;
    private OnOrderActionListener listener;

    public OwnerOrderAdapter(Context context, OnOrderActionListener listener) {
        this.context = context;
        this.orderList = new ArrayList<>();
        this.listener = listener;
    }

    public void updateOrderList(List<Bill> newOrderList) {
        this.orderList.clear();
        if (newOrderList != null) {
            this.orderList.addAll(newOrderList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_owner_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Bill order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderId, tvCustomerName, tvOrderDate, tvTotalAmount, tvItemCount, tvStatus, tvDeliveryAddress;
        private Button btnAccept, btnComplete, btnViewDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDeliveryAddress = itemView.findViewById(R.id.tvDeliveryAddress);
            
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }

        public void bind(Bill order) {
            // Set order information
            tvOrderId.setText("Đơn hàng #" + order.getBillId());
            tvCustomerName.setText(order.getFullName());
            tvOrderDate.setText(order.getFormattedDate());
            tvTotalAmount.setText(PriceUtils.formatPrice(order.getTotalAmount()));
            tvItemCount.setText(order.getTotalItemCount() + " món");
            tvDeliveryAddress.setText(order.getDeliveryAddress());
            
            // Set status
            updateStatusDisplay(order);
            
            // Set button visibility and listeners
            setupButtons(order);
        }

        private void updateStatusDisplay(Bill order) {
            String status = order.getCurrentStatus();
            tvStatus.setText(order.getStatusName());
            
            // Set status color
            int statusColor;
            switch (status) {
                case Bill.STATUS_PENDING:
                    statusColor = context.getResources().getColor(android.R.color.holo_orange_dark);
                    break;
                case Bill.STATUS_DELIVERED:
                    statusColor = context.getResources().getColor(android.R.color.holo_green_dark);
                    break;
                default:
                    statusColor = context.getResources().getColor(android.R.color.darker_gray);
                    break;
            }
            tvStatus.setTextColor(statusColor);
        }

        private void setupButtons(Bill order) {
            String status = order.getCurrentStatus();
            
            // Reset visibility
            btnAccept.setVisibility(View.GONE);
            btnComplete.setVisibility(View.GONE);
            btnViewDetails.setVisibility(View.VISIBLE);
            
            // Set button visibility based on status
            switch (status) {
                case Bill.STATUS_PENDING:
                    btnAccept.setVisibility(View.VISIBLE);
                    btnAccept.setText("Xác Nhận");
                    break;
                case Bill.STATUS_DELIVERED:
                    // Order is completed, no action buttons needed
                    break;
            }
            
            // Set click listeners
            btnAccept.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAcceptOrder(order);
                }
            });
            
            btnComplete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCompleteOrder(order);
                }
            });
            
            btnViewDetails.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewOrderDetails(order);
                }
            });
        }
    }
}

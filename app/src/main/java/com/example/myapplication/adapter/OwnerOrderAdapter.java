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
        void onAdvanceOrderStatus(Bill order);
        void onCancelOrder(Bill order);
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
        private Button btnAdvance, btnCancel, btnViewDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDeliveryAddress = itemView.findViewById(R.id.tvDeliveryAddress);
            
            btnAdvance = itemView.findViewById(R.id.btnAccept); // Reuse existing button
            btnCancel = itemView.findViewById(R.id.btnComplete); // Reuse existing button 
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
            
            // Set status color based on the status color from model
            int statusColor;
            switch (status) {
                case Bill.STATUS_PENDING:
                    statusColor = context.getResources().getColor(android.R.color.holo_orange_dark);
                    break;
                case Bill.STATUS_CONFIRMED:
                    statusColor = context.getResources().getColor(android.R.color.holo_blue_bright);
                    break;
                case Bill.STATUS_PREPARING:
                    statusColor = context.getResources().getColor(android.R.color.holo_purple);
                    break;
                case Bill.STATUS_READY:
                    statusColor = context.getResources().getColor(android.R.color.holo_blue_dark);
                    break;
                case Bill.STATUS_DELIVERING:
                    statusColor = context.getResources().getColor(android.R.color.holo_orange_light);
                    break;
                case Bill.STATUS_DELIVERED:
                    statusColor = context.getResources().getColor(android.R.color.holo_green_dark);
                    break;
                case Bill.STATUS_CANCELLED:
                    statusColor = context.getResources().getColor(android.R.color.holo_red_dark);
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
            btnAdvance.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnViewDetails.setVisibility(View.VISIBLE);
            
            // Setup buttons based on order status
            if (order.canAdvanceToNextStatus()) {
                btnAdvance.setVisibility(View.VISIBLE);
                btnAdvance.setText(order.getStatusActionName());
                
                // Set button color based on action
                switch (status) {
                    case Bill.STATUS_PENDING:
                        btnAdvance.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_bright));
                        break;
                    case Bill.STATUS_CONFIRMED:
                        btnAdvance.setBackgroundColor(context.getResources().getColor(android.R.color.holo_purple));
                        break;
                    case Bill.STATUS_PREPARING:
                        btnAdvance.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_dark));
                        break;
                    case Bill.STATUS_READY:
                        btnAdvance.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));
                        break;
                    case Bill.STATUS_DELIVERING:
                        btnAdvance.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
                        break;
                    default:
                        btnAdvance.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
                        break;
                }
            }
            
            // Show cancel button for orders that can be cancelled
            if (order.canCancel()) {
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setText("Hủy Đơn");
                btnCancel.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
            }
            
            // Set click listeners
            btnAdvance.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAdvanceOrderStatus(order);
                }
            });
            
            btnCancel.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelOrder(order);
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

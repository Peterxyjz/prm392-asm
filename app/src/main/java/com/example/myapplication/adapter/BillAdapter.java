package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.Bill;
import java.util.ArrayList;
import java.util.List;

/**
 * BillAdapter - Adapter cho RecyclerView hiển thị danh sách hóa đơn
 */
public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    
    private List<Bill> bills;
    private OnBillClickListener listener;
    
    /**
     * Interface để xử lý sự kiện click vào bill
     */
    public interface OnBillClickListener {
        void onBillClick(Bill bill);
    }
    
    /**
     * Constructor
     */
    public BillAdapter(List<Bill> bills) {
        this.bills = bills != null ? bills : new ArrayList<>();
    }
    
    /**
     * Set listener cho sự kiện click
     */
    public void setOnBillClickListener(OnBillClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = bills.get(position);
        holder.bind(bill, listener);
    }
    
    @Override
    public int getItemCount() {
        return bills.size();
    }
    
    /**
     * Cập nhật danh sách bills
     */
    public void updateBills(List<Bill> newBills) {
        this.bills = newBills != null ? newBills : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder cho bill item
     */
    static class BillViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvBillId, tvBillStatus, tvBillDate, tvBillItemCount;
        private TextView tvBillItemsSummary, tvBillTotal;
        
        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvBillId = itemView.findViewById(R.id.tvBillId);
            tvBillStatus = itemView.findViewById(R.id.tvBillStatus);
            tvBillDate = itemView.findViewById(R.id.tvBillDate);
            tvBillItemCount = itemView.findViewById(R.id.tvBillItemCount);
            tvBillItemsSummary = itemView.findViewById(R.id.tvBillItemsSummary);
            tvBillTotal = itemView.findViewById(R.id.tvBillTotal);
        }
        
        public void bind(Bill bill, OnBillClickListener listener) {
            // Set bill ID
            tvBillId.setText("#" + bill.getBillId());
            
            // Set status with appropriate color
            String currentStatus = bill.getCurrentStatus();
            tvBillStatus.setText(bill.getStatusName());
            int statusColor = getStatusColor(currentStatus);
            tvBillStatus.setTextColor(statusColor);
            
            // Set formatted date
            tvBillDate.setText(bill.getFormattedDate());
            
            // Set item count
            int itemCount = bill.getTotalItemCount();
            tvBillItemCount.setText(itemCount + " món");
            
            // Set items summary
            tvBillItemsSummary.setText(bill.getItemsSummary());
            
            // Set total amount
            tvBillTotal.setText(formatPrice(bill.getTotalAmount()));
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBillClick(bill);
                }
            });
        }
        
        /**
         * Lấy màu sắc cho trạng thái
         */
        private int getStatusColor(String status) {
            switch (status) {
                case Bill.STATUS_PENDING:
                    return itemView.getContext().getColor(R.color.primary_orange);
                case Bill.STATUS_DELIVERED:
                    return itemView.getContext().getColor(R.color.success_green);
                default:
                    return itemView.getContext().getColor(R.color.text_primary);
            }
        }
        
        /**
         * Format giá tiền
         */
        private String formatPrice(double price) {
            return String.format("%.0f₫", price);
        }
    }
}

package com.example.myapplication.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Bill Model - Đại diện cho một hóa đơn/đơn hàng
 */
public class Bill {
    
    /**
     * Inner class for Bill Items
     */
    public static class BillItem {
        private int foodId;
        private String foodName;
        private double price;
        private int quantity;
        
        public BillItem(int foodId, String foodName, double price, int quantity) {
            this.foodId = foodId;
            this.foodName = foodName;
            this.price = price;
            this.quantity = quantity;
        }
        
        // Getters and Setters
        public int getFoodId() { return foodId; }
        public void setFoodId(int foodId) { this.foodId = foodId; }
        
        public String getFoodName() { return foodName; }
        public void setFoodName(String foodName) { this.foodName = foodName; }
        
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        
        public double getTotalPrice() { return price * quantity; }
    }
    // Các trạng thái đơn hàng
    public static final String STATUS_PENDING = "PENDING";       // Chờ xử lý
    public static final String STATUS_CONFIRMED = "CONFIRMED";   // Đã xác nhận
    public static final String STATUS_PREPARING = "PREPARING";   // Đang chuẩn bị
    public static final String STATUS_READY = "READY";           // Sẵn sàng giao
    public static final String STATUS_DELIVERING = "DELIVERING"; // Đang giao hàng
    public static final String STATUS_DELIVERED = "DELIVERED";   // Đã giao hàng
    public static final String STATUS_CANCELLED = "CANCELLED";   // Đã hủy
    
    // Thời gian tự động chuyển trạng thái (45 phút)
    public static final long AUTO_DELIVERY_TIME = 45 * 60 * 1000; // 45 minutes in milliseconds
    
    private int billId;
    private String username;
    private List<CartItem> items;
    private List<BillItem> billItems;  // Add BillItem list
    private double totalAmount;
    private String deliveryAddress;
    private String phone;
    private String fullName;
    private Date orderDate;
    private Date lastUpdated;
    private String status;
    private String notes;
    
    // Constructor
    public Bill(int billId, String username, List<CartItem> items, double totalAmount, 
                String deliveryAddress, String phone, String fullName, Date orderDate, String status) {
        this.billId = billId;
        this.username = username;
        this.items = items;
        this.billItems = new ArrayList<>();
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.phone = phone;
        this.fullName = fullName;
        this.orderDate = orderDate;
        this.lastUpdated = orderDate;
        this.status = status;
        this.notes = "";
    }
    
    // Methods for BillItems
    public List<BillItem> getBillItems() { return billItems; }
    public void setBillItems(List<BillItem> billItems) { this.billItems = billItems; }
    
    public void addBillItem(BillItem billItem) {
        if (this.billItems == null) {
            this.billItems = new ArrayList<>();
        }
        this.billItems.add(billItem);
    }
    
    // Getters and Setters
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    
    // Alias method for getId() - required by BillManager
    public int getId() { return billId; }
    public void setId(int id) { this.billId = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    
    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    /**
     * Lấy ngày đặt hàng dưới dạng format
     */
    public String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(orderDate);
    }
    
    /**
     * Lấy tên trạng thái tiếng Việt
     */
    public String getStatusName() {
        switch (status) {
            case STATUS_PENDING: return "Chờ xử lý";
            case STATUS_CONFIRMED: return "Đã xác nhận";
            case STATUS_PREPARING: return "Đang chuẩn bị";
            case STATUS_READY: return "Sẵn sàng giao";
            case STATUS_DELIVERING: return "Đang giao hàng";
            case STATUS_DELIVERED: return "Đã giao hàng";
            case STATUS_CANCELLED: return "Đã hủy";
            default: return "Không xác định";
        }
    }
    
    /**
     * Lấy màu sắc cho trạng thái
     */
    public String getStatusColor() {
        switch (status) {
            case STATUS_PENDING: return "#ff9800";      // Orange
            case STATUS_CONFIRMED: return "#2196f3";    // Blue
            case STATUS_PREPARING: return "#9c27b0";    // Purple
            case STATUS_READY: return "#00bcd4";        // Cyan
            case STATUS_DELIVERING: return "#ff5722";   // Deep Orange
            case STATUS_DELIVERED: return "#4caf50";    // Green
            case STATUS_CANCELLED: return "#f44336";    // Red
            default: return "#666666";                  // Gray
        }
    }
    
    /**
     * Kiểm tra và cập nhật trạng thái tự động
     * Nếu đơn hàng đã quá 45 phút thì chuyển thành DELIVERED
     */
    public void checkAndUpdateStatus() {
        if (status.equals(STATUS_DELIVERING)) {
            long currentTime = System.currentTimeMillis();
            long orderTime = orderDate.getTime();
            
            if (currentTime - orderTime >= AUTO_DELIVERY_TIME) {
                status = STATUS_DELIVERED;
                lastUpdated = new Date();
            }
        }
    }
    
    /**
     * Lấy trạng thái hiện tại (sau khi check auto-update)
     */
    public String getCurrentStatus() {
        checkAndUpdateStatus();
        return status;
    }
    
    /**
     * Tính số lượng món trong đơn hàng
     */
    public int getTotalItemCount() {
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }
    
    /**
     * Lấy tên món đầu tiên + "và X món khác" nếu có nhiều món
     */
    public String getItemsSummary() {
        if (items.isEmpty()) return "Không có món nào";
        
        if (items.size() == 1) {
            return items.get(0).getFoodItem().getName();
        } else {
            return items.get(0).getFoodItem().getName() + " và " + (items.size() - 1) + " món khác";
        }
    }
    
    /**
     * Kiểm tra xem đơn hàng có thể hủy không
     */
    public boolean canCancel() {
        return status.equals(STATUS_PENDING) || status.equals(STATUS_CONFIRMED);
    }
    
    /**
     * Kiểm tra xem đơn hàng đã hoàn thành chưa
     */
    public boolean isCompleted() {
        return status.equals(STATUS_DELIVERED) || status.equals(STATUS_CANCELLED);
    }
    
    /**
     * Lấy trạng thái tiếp theo có thể chuyển đổi
     */
    public String getNextStatus() {
        switch (status) {
            case STATUS_PENDING: return STATUS_CONFIRMED;
            case STATUS_CONFIRMED: return STATUS_PREPARING;
            case STATUS_PREPARING: return STATUS_READY;
            case STATUS_READY: return STATUS_DELIVERING;
            case STATUS_DELIVERING: return STATUS_DELIVERED;
            default: return null;
        }
    }
    
    /**
     * Kiểm tra xem có thể chuyển sang trạng thái tiếp theo không
     */
    public boolean canAdvanceToNextStatus() {
        return getNextStatus() != null && !isCompleted();
    }
    
    /**
     * Lấy tên action cho trạng thái hiện tại
     */
    public String getStatusActionName() {
        switch (status) {
            case STATUS_PENDING: return "Xác Nhận";
            case STATUS_CONFIRMED: return "Bắt Đầu Làm";
            case STATUS_PREPARING: return "Món Đã Xong";
            case STATUS_READY: return "Bắt Đầu Giao";
            case STATUS_DELIVERING: return "Đã Giao Xong";
            default: return null;
        }
    }
    
    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", username='" + username + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", orderDate=" + orderDate +
                '}';
    }
}
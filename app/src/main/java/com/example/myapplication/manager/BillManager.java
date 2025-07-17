package com.example.myapplication.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.myapplication.model.Bill;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.FoodItem;
import com.example.myapplication.manager.FoodDataManager;
import com.example.myapplication.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enhanced BillManager với separate bills per user
 * Mỗi user có lịch sử đơn hàng riêng biệt
 */
public class BillManager {
    private static final String TAG = "BillManager";
    
    private static final String PREFS_NAME = "bill_prefs";
    private static final String KEY_BILLS_SUFFIX = "_bills";        // Will be: {username}_bills
    private static final String KEY_NEXT_ID_SUFFIX = "_next_id";    // Will be: {username}_next_id
    
    private static BillManager instance;
    private SharedPreferences prefs;
    private Gson gson;
    private Context context;
    private String currentUserBills = "";    // Track current user's bills
    
    private BillManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        loadBillsForCurrentUser();
    }
    
    public static synchronized BillManager getInstance(Context context) {
        if (instance == null) {
            instance = new BillManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Load bills cho user hiện tại
     */
    public void loadBillsForCurrentUser() {
        try {
            UserManager userManager = UserManager.getInstance(context);
            if (!userManager.isLoggedIn()) {
                Logger.d(TAG, "No user logged in, cannot load bills");
                currentUserBills = "";
                return;
            }

            String username = userManager.getCurrentUser().getUsername();
            
            // Nếu user đã thay đổi, cập nhật reference
            if (!username.equals(currentUserBills)) {
                Logger.d(TAG, "User changed from '" + currentUserBills + "' to '" + username + "', updating bills reference");
                currentUserBills = username;
            }
            
        } catch (Exception e) {
            Logger.e(TAG, "Error loading bills for current user", e);
            currentUserBills = "";
        }
    }
    
    /**
     * Tạo hóa đơn mới cho user hiện tại
     */
    public Bill createBill(String customerName, List<CartItem> cartItems, double totalAmount,
                          String deliveryAddress, String phone, String fullName) {
        try {
            loadBillsForCurrentUser();
            
            if (currentUserBills.isEmpty()) {
                Logger.w(TAG, "No user logged in, cannot create bill");
                return null;
            }
            
            // Get next bill ID for this user
            int billId = getNextBillId();
            
            // Create bill items from cart items
            List<Bill.BillItem> billItems = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                FoodItem food = cartItem.getFoodItem();
                Bill.BillItem billItem = new Bill.BillItem(
                    food.getId(),
                    food.getName(),
                    food.getPrice(),
                    cartItem.getQuantity()
                );
                billItems.add(billItem);
            }
            
            // Create new bill
            Bill bill = new Bill(billId, customerName, new ArrayList<>(), totalAmount,
                               deliveryAddress, phone, fullName, new Date(), Bill.STATUS_PENDING);
            
            // Set bill items
            bill.setBillItems(billItems);
            
            // Save bill
            saveBill(bill);
            
            Logger.d(TAG, "Created bill #" + billId + " for user: " + currentUserBills + 
                     " with " + billItems.size() + " items, total: " + totalAmount);
            
            return bill;
            
        } catch (Exception e) {
            Logger.e(TAG, "Error creating bill", e);
            return null;
        }
    }
    
    /**
     * Lưu hóa đơn cho user hiện tại
     */
    private void saveBill(Bill bill) {
        try {
            if (currentUserBills.isEmpty()) {
                Logger.w(TAG, "No current user, cannot save bill");
                return;
            }
            
            List<Bill> bills = getBillsForCurrentUser();
            bills.add(bill);
            
            String billsKey = currentUserBills + KEY_BILLS_SUFFIX;
            String json = gson.toJson(bills);
            prefs.edit().putString(billsKey, json).apply();
            
            Logger.d(TAG, "Saved bill #" + bill.getId() + " for user: " + currentUserBills);
            
        } catch (Exception e) {
            Logger.e(TAG, "Error saving bill", e);
        }
    }
    
    /**
     * Lấy danh sách hóa đơn cho user hiện tại
     */
    public List<Bill> getBillsForCurrentUser() {
        try {
            loadBillsForCurrentUser();
            
            if (currentUserBills.isEmpty()) {
                Logger.d(TAG, "No user logged in, returning empty bill list");
                return new ArrayList<>();
            }
            
            String billsKey = currentUserBills + KEY_BILLS_SUFFIX;
            String json = prefs.getString(billsKey, "");
            
            if (!json.isEmpty()) {
                Type type = new TypeToken<List<Bill>>(){}.getType();
                List<Bill> bills = gson.fromJson(json, type);
                Logger.d(TAG, "Loaded " + (bills != null ? bills.size() : 0) + " bills for user: " + currentUserBills);
                return bills != null ? bills : new ArrayList<>();
            } else {
                Logger.d(TAG, "No saved bills found for user: " + currentUserBills);
                return new ArrayList<>();
            }
            
        } catch (Exception e) {
            Logger.e(TAG, "Error getting bills for current user", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Lấy hóa đơn theo ID cho user hiện tại
     */
    public Bill getBillById(int billId) {
        try {
            List<Bill> bills = getBillsForCurrentUser();
            for (Bill bill : bills) {
                if (bill.getId() == billId) {
                    return bill;
                }
            }
            Logger.d(TAG, "Bill #" + billId + " not found for user: " + currentUserBills);
            return null;
            
        } catch (Exception e) {
            Logger.e(TAG, "Error getting bill by ID", e);
            return null;
        }
    }
    
    /**
     * Lấy ID tiếp theo cho hóa đơn của user hiện tại
     */
    private int getNextBillId() {
        try {
            if (currentUserBills.isEmpty()) {
                Logger.w(TAG, "No current user, returning default bill ID");
                return 1;
            }
            
            String nextIdKey = currentUserBills + KEY_NEXT_ID_SUFFIX;
            int nextId = prefs.getInt(nextIdKey, 1);
            
            // Update next ID for this user
            prefs.edit().putInt(nextIdKey, nextId + 1).apply();
            
            Logger.d(TAG, "Generated bill ID: " + nextId + " for user: " + currentUserBills);
            return nextId;
            
        } catch (Exception e) {
            Logger.e(TAG, "Error getting next bill ID", e);
            return 1;
        }
    }
    
    /**
     * Thống kê tổng số đơn hàng cho user hiện tại
     */
    public int getTotalOrderCount() {
        try {
            return getBillsForCurrentUser().size();
        } catch (Exception e) {
            Logger.e(TAG, "Error getting total order count", e);
            return 0;
        }
    }
    
    /**
     * Thống kê tổng chi tiêu cho user hiện tại
     */
    public double getTotalSpending() {
        try {
            List<Bill> bills = getBillsForCurrentUser();
            double total = 0;
            for (Bill bill : bills) {
                total += bill.getTotalAmount();
            }
            return total;
            
        } catch (Exception e) {
            Logger.e(TAG, "Error getting total spending", e);
            return 0;
        }
    }
    
    /**
     * Lấy danh sách đơn hàng theo trạng thái cho user hiện tại
     */
    public List<Bill> getBillsByStatus(String status) {
        try {
            List<Bill> bills = getBillsForCurrentUser();
            return bills.stream()
                       .filter(bill -> status.equals(bill.getStatus()))
                       .collect(Collectors.toList());
                       
        } catch (Exception e) {
            Logger.e(TAG, "Error getting bills by status", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Force reload bills for current user (useful after login/logout)
     */
    public void forceReloadBills() {
        Logger.d(TAG, "Force reloading bills");
        loadBillsForCurrentUser();
    }
    
    /**
     * Get current user's bills identifier
     */
    public String getCurrentUserBills() {
        return currentUserBills;
    }
    
    /**
     * Debug method to check bills status
     */
    public void debugBillsStatus() {
        try {
            Logger.d(TAG, "=== BILLS DEBUG ===");
            Logger.d(TAG, "Current user bills: " + currentUserBills);
            
            List<Bill> bills = getBillsForCurrentUser();
            Logger.d(TAG, "Total bills count: " + bills.size());
            Logger.d(TAG, "Total spending: " + getTotalSpending());
            
            for (int i = 0; i < bills.size(); i++) {
                Bill bill = bills.get(i);
                Logger.d(TAG, "Bill " + i + ": #" + bill.getId() + " - " + bill.getTotalAmount() + "₫ - " + bill.getStatus());
            }
            Logger.d(TAG, "==================");
            
        } catch (Exception e) {
            Logger.e(TAG, "Error in bills debug", e);
        }
    }
    
    /**
     * Get bills summary for user
     */
    public String getBillsSummary() {
        try {
            loadBillsForCurrentUser();
            int orderCount = getTotalOrderCount();
            double totalSpending = getTotalSpending();
            
            if (orderCount == 0) {
                return "Chưa có đơn hàng nào";
            }
            
            return String.format("Lịch sử: %d đơn - %.0f₫", orderCount, totalSpending);
            
        } catch (Exception e) {
            Logger.e(TAG, "Error getting bills summary", e);
            return "Lỗi lịch sử đơn hàng";
        }
    }
    
    /**
     * Clear all bills for current user (for testing)
     */
    public void clearBillsForCurrentUser() {
        try {
            loadBillsForCurrentUser();
            
            if (currentUserBills.isEmpty()) {
                Logger.w(TAG, "No user logged in, cannot clear bills");
                return;
            }
            
            String billsKey = currentUserBills + KEY_BILLS_SUFFIX;
            String nextIdKey = currentUserBills + KEY_NEXT_ID_SUFFIX;
            
            prefs.edit()
                 .remove(billsKey)
                 .remove(nextIdKey)
                 .apply();
                 
            Logger.d(TAG, "Cleared all bills for user: " + currentUserBills);
            
        } catch (Exception e) {
            Logger.e(TAG, "Error clearing bills", e);
        }
    }
    
    /**
     * Update bill status
     */
    public void updateBillStatus(int billId, String newStatus) {
        try {
            List<Bill> bills = getBillsForCurrentUser();
            boolean updated = false;
            
            for (Bill bill : bills) {
                if (bill.getId() == billId) {
                    bill.setStatus(newStatus);
                    bill.setLastUpdated(new Date());
                    updated = true;
                    break;
                }
            }
            
            if (updated) {
                // Save updated bills
                String billsKey = currentUserBills + KEY_BILLS_SUFFIX;
                String json = gson.toJson(bills);
                prefs.edit().putString(billsKey, json).apply();
                
                Logger.d(TAG, "Updated bill #" + billId + " status to: " + newStatus);
            } else {
                Logger.w(TAG, "Bill #" + billId + " not found for status update");
            }
            
        } catch (Exception e) {
            Logger.e(TAG, "Error updating bill status", e);
        }
    }
    
    /**
     * Get bills by username
     */
    public List<Bill> getBillsByUsername(String username) {
        try {
            String billsKey = username + KEY_BILLS_SUFFIX;
            String json = prefs.getString(billsKey, "");
            
            if (!json.isEmpty()) {
                Type type = new TypeToken<List<Bill>>(){}.getType();
                List<Bill> bills = gson.fromJson(json, type);
                return bills != null ? bills : new ArrayList<>();
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            Logger.e(TAG, "Error getting bills by username", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get bills by username and status
     */
    public List<Bill> getBillsByUsernameAndStatus(String username, String status) {
        try {
            List<Bill> allBills = getBillsByUsername(username);
            return allBills.stream()
                          .filter(bill -> status.equals(bill.getStatus()))
                          .collect(Collectors.toList());
                          
        } catch (Exception e) {
            Logger.e(TAG, "Error getting bills by username and status", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get total bill count by username
     */
    public int getBillCountByUsername(String username) {
        try {
            return getBillsByUsername(username).size();
        } catch (Exception e) {
            Logger.e(TAG, "Error getting bill count by username", e);
            return 0;
        }
    }
    
    /**
     * Get total spending by username
     */
    public double getTotalSpentByUsername(String username) {
        try {
            List<Bill> bills = getBillsByUsername(username);
            double total = 0;
            for (Bill bill : bills) {
                total += bill.getTotalAmount();
            }
            return total;
            
        } catch (Exception e) {
            Logger.e(TAG, "Error getting total spent by username", e);
            return 0;
        }
    }
}

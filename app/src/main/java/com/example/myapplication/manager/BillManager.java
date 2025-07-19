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
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Enhanced BillManager với separate bills per user
 * Mỗi user có lịch sử đơn hàng riêng biệt
 */
public class BillManager {
    private static final String TAG = "BillManager";
    
    private static final String PREFS_NAME = "bill_prefs";
    private static final String KEY_BILLS_SUFFIX = "_bills";        // Will be: {username}_bills
    private static final String KEY_GLOBAL_NEXT_ID = "global_next_bill_id";  // Global unique ID
    
    private static BillManager instance;
    private SharedPreferences prefs;
    private Gson gson;
    private Context context;
    private String currentUserBills = "";    // Track current user's bills
    
    // FIXED: Add synchronization lock for thread safety
    private static final Object ID_LOCK = new Object();
    
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
            
            // Get next GLOBAL unique bill ID
            int billId = getNextGlobalBillId();
            
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
            
            // Create new bill - FIX: Pass cartItems để giữ backward compatibility
            Bill bill = new Bill(billId, customerName, cartItems, totalAmount,
                               deliveryAddress, phone, fullName, new Date(), Bill.STATUS_PENDING);
            
            // Set bill items (primary data)
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
     * FIXED: Thread-safe và đơn giản hóa logic tạo ID để tránh trùng lặp
     */
    private int getNextGlobalBillId() {
        synchronized (ID_LOCK) {
            try {
                // Step 1: Get current max ID from SharedPreferences
                int nextId = prefs.getInt(KEY_GLOBAL_NEXT_ID, 1);
                
                // Step 2: Find actual max ID from all existing bills (safety check)
                int actualMaxId = findActualMaxBillId();
                
                // Step 3: Use the higher value to ensure no collision
                int safeId = Math.max(nextId, actualMaxId + 1);
                
                // Step 4: Update the stored next ID for future use
                prefs.edit().putInt(KEY_GLOBAL_NEXT_ID, safeId + 1).apply();
                
                Logger.d(TAG, "Generated safe bill ID: " + safeId + 
                         " (stored next: " + nextId + ", actual max: " + actualMaxId + ")");
                
                return safeId;
                
            } catch (Exception e) {
                Logger.e(TAG, "Error in getNextGlobalBillId", e);
                // Fallback: Use timestamp-based unique ID
                int fallbackId = (int) (System.currentTimeMillis() % 1000000);
                Logger.w(TAG, "Using fallback ID: " + fallbackId);
                return fallbackId;
            }
        }
    }
    
    /**
     * FIXED: Tìm ID lớn nhất thực tế từ tất cả bills
     */
    private int findActualMaxBillId() {
        int maxId = 0;
        
        try {
            // Scan through all users' bills
            for (String key : prefs.getAll().keySet()) {
                if (key.endsWith(KEY_BILLS_SUFFIX)) {
                    String json = prefs.getString(key, "");
                    if (!json.isEmpty()) {
                        Type type = new TypeToken<List<Bill>>(){}.getType();
                        List<Bill> userBills = gson.fromJson(json, type);
                        if (userBills != null) {
                            for (Bill bill : userBills) {
                                maxId = Math.max(maxId, bill.getId());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, "Error finding actual max bill ID", e);
        }
        
        return maxId;
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
            
            prefs.edit()
                 .remove(billsKey)
                 .apply();
                 
            Logger.d(TAG, "Cleared all bills for user: " + currentUserBills);
            
        } catch (Exception e) {
            Logger.e(TAG, "Error clearing bills", e);
        }
    }
    
    /**
     * FIXED: Update bill status for any user with proper synchronization (Owner function)
     */
    public boolean updateBillStatusForOwner(int billId, String newStatus) {
        synchronized (ID_LOCK) {
            try {
                Logger.d(TAG, "Attempting to update bill #" + billId + " to status: " + newStatus);
                
                // Search through all users' bills
                int totalBillsSearched = 0;
                for (String key : prefs.getAll().keySet()) {
                    if (key.endsWith(KEY_BILLS_SUFFIX)) {
                        Logger.d(TAG, "Searching in bills key: " + key);
                        String json = prefs.getString(key, "");
                        if (!json.isEmpty()) {
                            Type type = new TypeToken<List<Bill>>(){}.getType();
                            List<Bill> userBills = gson.fromJson(json, type);
                            
                            if (userBills != null) {
                                totalBillsSearched += userBills.size();
                                boolean updated = false;
                                for (Bill bill : userBills) {
                                    Logger.d(TAG, "Checking bill #" + bill.getId() + " (status: " + bill.getStatus() + ")");
                                    if (bill.getId() == billId) {
                                        Logger.d(TAG, "Found matching bill #" + billId + ", updating status from " + bill.getStatus() + " to " + newStatus);
                                        bill.setStatus(newStatus);
                                        bill.setLastUpdated(new Date());
                                        updated = true;
                                        break;
                                    }
                                }
                                
                                if (updated) {
                                    // Save updated bills back to that user's data
                                    String updatedJson = gson.toJson(userBills);
                                    prefs.edit().putString(key, updatedJson).apply();
                                    
                                    Logger.d(TAG, "Successfully updated bill #" + billId + " status to: " + newStatus + " in key: " + key);
                                    return true;
                                }
                            }
                        }
                    }
                }
                
                Logger.w(TAG, "Bill #" + billId + " not found for status update (searched " + totalBillsSearched + " bills total)");
                return false;
                
            } catch (Exception e) {
                Logger.e(TAG, "Error updating bill status for owner", e);
                return false;
            }
        }
    }
    
    /**
     * Update bill status for current user
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
    
    /**
     * Get all bills from all users (for Owner)
     */
    public List<Bill> getAllBillsFromAllUsers() {
        try {
            List<Bill> allBills = new ArrayList<>();
            
            // Get all keys from SharedPreferences
            for (String key : prefs.getAll().keySet()) {
                if (key.endsWith(KEY_BILLS_SUFFIX)) {
                    String json = prefs.getString(key, "");
                    if (!json.isEmpty()) {
                        Type type = new TypeToken<List<Bill>>(){}.getType();
                        List<Bill> userBills = gson.fromJson(json, type);
                        if (userBills != null) {
                            allBills.addAll(userBills);
                        }
                    }
                }
            }
            
            // Sort by order date (newest first)
            allBills.sort((bill1, bill2) -> bill2.getOrderDate().compareTo(bill1.getOrderDate()));
            
            Logger.d(TAG, "Loaded " + allBills.size() + " bills from all users");
            return allBills;
            
        } catch (Exception e) {
            Logger.e(TAG, "Error getting all bills from all users", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get orders by status from all users (for Owner)
     */
    public List<Bill> getAllOrdersByStatus(String status) {
        try {
            List<Bill> allBills = getAllBillsFromAllUsers();
            return allBills.stream()
                          .filter(bill -> status.equals(bill.getStatus()))
                          .collect(Collectors.toList());
                          
        } catch (Exception e) {
            Logger.e(TAG, "Error getting all orders by status", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get order counts by status for Owner dashboard
     */
    public int getOrderCountByStatus(String status) {
        try {
            return getAllOrdersByStatus(status).size();
        } catch (Exception e) {
            Logger.e(TAG, "Error getting order count by status", e);
            return 0;
        }
    }
    
    /**
     * Get total revenue from all users (for Owner)
     */
    public double getTotalRevenue() {
        try {
            List<Bill> allBills = getAllBillsFromAllUsers();
            double total = 0;
            for (Bill bill : allBills) {
                if (!Bill.STATUS_CANCELLED.equals(bill.getStatus())) {
                    total += bill.getTotalAmount();
                }
            }
            return total;
            
        } catch (Exception e) {
            Logger.e(TAG, "Error getting total revenue", e);
            return 0;
        }
    }
    
    /**
     * Get daily revenue (for Owner)
     */
    public double getDailyRevenue() {
        try {
            List<Bill> allBills = getAllBillsFromAllUsers();
            double total = 0;
            
            // Get today's date
            Date today = new Date();
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String todayStr = dateFormat.format(today);
            
            for (Bill bill : allBills) {
                if (!Bill.STATUS_CANCELLED.equals(bill.getStatus())) {
                    String billDateStr = dateFormat.format(bill.getOrderDate());
                    if (todayStr.equals(billDateStr)) {
                        total += bill.getTotalAmount();
                    }
                }
            }
            
            return total;
            
        } catch (Exception e) {
            Logger.e(TAG, "Error getting daily revenue", e);
            return 0;
        }
    }
    
    /**
     * FIXED: Phương thức kiểm tra và sửa chữa ID trùng lặp
     */
    public void validateAndFixDuplicateIds() {
        synchronized (ID_LOCK) {
            try {
                Logger.d(TAG, "Starting duplicate ID validation...");
                
                List<Integer> allIds = new ArrayList<>();
                List<String> allKeys = new ArrayList<>();
                
                // Collect all bill IDs
                for (String key : prefs.getAll().keySet()) {
                    if (key.endsWith(KEY_BILLS_SUFFIX)) {
                        String json = prefs.getString(key, "");
                        if (!json.isEmpty()) {
                            Type type = new TypeToken<List<Bill>>(){}.getType();
                            List<Bill> userBills = gson.fromJson(json, type);
                            if (userBills != null) {
                                for (Bill bill : userBills) {
                                    allIds.add(bill.getId());
                                }
                                allKeys.add(key);
                            }
                        }
                    }
                }
                
                // Check for duplicates
                List<Integer> duplicates = new ArrayList<>();
                for (int i = 0; i < allIds.size(); i++) {
                    for (int j = i + 1; j < allIds.size(); j++) {
                        if (allIds.get(i).equals(allIds.get(j))) {
                            if (!duplicates.contains(allIds.get(i))) {
                                duplicates.add(allIds.get(i));
                            }
                        }
                    }
                }
                
                if (duplicates.isEmpty()) {
                    Logger.d(TAG, "No duplicate IDs found. Total bills: " + allIds.size());
                } else {
                    Logger.w(TAG, "Found " + duplicates.size() + " duplicate IDs: " + duplicates);
                    fixDuplicateIdsInternal(duplicates, allKeys);
                }
                
            } catch (Exception e) {
                Logger.e(TAG, "Error in duplicate ID validation", e);
            }
        }
    }
    
    /**
     * FIXED: Sửa chữa ID trùng lặp bằng cách gán lại ID mới duy nhất
     */
    private void fixDuplicateIdsInternal(List<Integer> duplicateIds, List<String> allKeys) {
        try {
            int maxId = findActualMaxBillId();
            int nextAvailableId = maxId + 1;
            
            Logger.d(TAG, "Starting to fix duplicate IDs. Next available ID: " + nextAvailableId);
            
            for (String key : allKeys) {
                String json = prefs.getString(key, "");
                if (!json.isEmpty()) {
                    Type type = new TypeToken<List<Bill>>(){}.getType();
                    List<Bill> userBills = gson.fromJson(json, type);
                    
                    if (userBills != null) {
                        boolean hasChanges = false;
                        for (Bill bill : userBills) {
                            if (duplicateIds.contains(bill.getId())) {
                                int oldId = bill.getId();
                                bill.setId(nextAvailableId);
                                Logger.d(TAG, "Fixed duplicate: Changed bill ID from " + oldId + " to " + nextAvailableId + " in " + key);
                                nextAvailableId++;
                                hasChanges = true;
                            }
                        }
                        
                        if (hasChanges) {
                            // Save the updated bills
                            String updatedJson = gson.toJson(userBills);
                            prefs.edit().putString(key, updatedJson).apply();
                            Logger.d(TAG, "Saved fixed bills for " + key);
                        }
                    }
                }
            }
            
            // Update the global next ID
            prefs.edit().putInt(KEY_GLOBAL_NEXT_ID, nextAvailableId).apply();
            Logger.d(TAG, "Updated global next ID to: " + nextAvailableId);
            
        } catch (Exception e) {
            Logger.e(TAG, "Error fixing duplicate IDs", e);
        }
    }
}

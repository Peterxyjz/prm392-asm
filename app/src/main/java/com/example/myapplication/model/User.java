package com.example.myapplication.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Enhanced User model với email, password và validation
 */
public class User {
    private String username;      // Tên đăng nhập (unique)
    private String email;         // Email (unique) - NEW
    private String passwordHash;  // Password đã mã hóa - NEW
    private String fullName;      // Họ và tên đầy đủ
    private String address;       // Địa chỉ giao hàng
    private String phone;         // Số điện thoại
    private long createdDate;     // Ngày tạo tài khoản - NEW
    private boolean isVerified;   // Trạng thái xác thực - NEW
    private String role;          // Vai trò (CUSTOMER/OWNER) - NEW

    /**
     * Constructor cho việc tạo user mới (với password)
     */
    public User(String username, String email, String password, String fullName, String address, String phone) {
        this.username = username;
        this.email = email;
        this.passwordHash = hashPassword(password);
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.createdDate = System.currentTimeMillis();
        this.isVerified = false;
        this.role = "CUSTOMER";
    }

    /**
     * Constructor cho việc load user từ storage (password đã hash)
     */
    public User(String username, String email, String passwordHash, String fullName, 
                String address, String phone, long createdDate, boolean isVerified) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.createdDate = createdDate;
        this.isVerified = isVerified;
        this.role = "CUSTOMER";
    }

    /**
     * Mã hóa password bằng SHA-256
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Kiểm tra password có đúng không
     */
    public boolean verifyPassword(String password) {
        return this.passwordHash.equals(hashPassword(password));
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }

    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        // Ít nhất 6 ký tự, có chữ và số
        boolean hasLetter = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        
        return hasLetter && hasDigit;
    }

    /**
     * Validate phone number (Vietnam format)
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        // Loại bỏ spaces và dashes
        phone = phone.replaceAll("[\\s-]", "");
        
        // Kiểm tra format số điện thoại VN: 10-11 chữ số, bắt đầu bằng 0
        return phone.matches("^0[0-9]{9,10}$");
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public long getCreatedDate() { return createdDate; }
    public void setCreatedDate(long createdDate) { this.createdDate = createdDate; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", isVerified=" + isVerified +
                '}';
    }
}
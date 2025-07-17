package com.example.myapplication.utils;

import com.example.myapplication.model.CartItem;
import java.util.List;

/**
 * Utility class cho validation
 */
public class ValidationUtils {

    /**
     * Check if string is empty or null
     */
    public static boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    /**
     * Validate cart có items hay không
     */
    public static ValidationResult validateCart(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return new ValidationResult(false, "Giỏ hàng trống");
        }
        return new ValidationResult(true, "Valid");
    }

    /**
     * Validate địa chỉ giao hàng
     */
    public static ValidationResult validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return new ValidationResult(false, "Địa chỉ không được để trống");
        }
        
        if (address.equals("Nhập địa chỉ giao hàng")) {
            return new ValidationResult(false, "Vui lòng nhập địa chỉ giao hàng thực");
        }
        
        if (address.trim().length() < 10) {
            return new ValidationResult(false, "Địa chỉ quá ngắn (tối thiểu 10 ký tự)");
        }
        
        return new ValidationResult(true, "Valid");
    }

    /**
     * Validate username
     */
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "Tên đăng nhập không được để trống");
        }
        
        if (username.trim().length() < 3) {
            return new ValidationResult(false, "Tên đăng nhập phải có ít nhất 3 ký tự");
        }
        
        // Chỉ cho phép chữ cái, số và dấu gạch dưới
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return new ValidationResult(false, "Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới");
        }
        
        return new ValidationResult(true, "Valid");
    }

    /**
     * Validate email format
     */
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Email không được để trống");
        }
        
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailPattern)) {
            return new ValidationResult(false, "Định dạng email không hợp lệ");
        }
        
        return new ValidationResult(true, "Valid");
    }

    /**
     * Validate password strength
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "Mật khẩu không được để trống");
        }
        
        if (password.length() < 6) {
            return new ValidationResult(false, "Mật khẩu phải có ít nhất 6 ký tự");
        }
        
        // Kiểm tra có chữ và số
        boolean hasLetter = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        
        if (!hasLetter || !hasDigit) {
            return new ValidationResult(false, "Mật khẩu phải chứa cả chữ cái và số");
        }
        
        return new ValidationResult(true, "Valid");
    }

    /**
     * Validate phone number (Vietnam format)
     */
    public static ValidationResult validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return new ValidationResult(false, "Số điện thoại không được để trống");
        }
        
        // Loại bỏ spaces và dashes
        String cleanPhone = phone.replaceAll("[\\s-]", "");
        
        // Kiểm tra format số điện thoại VN: 10-11 chữ số, bắt đầu bằng 0
        if (!cleanPhone.matches("^0[0-9]{9,10}$")) {
            return new ValidationResult(false, "Số điện thoại không đúng định dạng (VD: 0123456789)");
        }
        
        return new ValidationResult(true, "Valid");
    }

    /**
     * Validate full name
     */
    public static ValidationResult validateFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return new ValidationResult(false, "Họ tên không được để trống");
        }
        
        if (fullName.trim().length() < 2) {
            return new ValidationResult(false, "Họ tên quá ngắn");
        }
        
        if (fullName.trim().length() > 50) {
            return new ValidationResult(false, "Họ tên quá dài (tối đa 50 ký tự)");
        }
        
        // Chỉ cho phép chữ cái, khoảng trắng và một số ký tự đặc biệt tiếng Việt
        if (!fullName.matches("^[a-zA-ZÀ-ỹ\\s]+$")) {
            return new ValidationResult(false, "Họ tên chỉ được chứa chữ cái và khoảng trắng");
        }
        
        return new ValidationResult(true, "Valid");
    }

    /**
     * Validate quantity
     */
    public static ValidationResult validateQuantity(int quantity) {
        if (quantity <= 0) {
            return new ValidationResult(false, "Số lượng phải lớn hơn 0");
        }
        
        if (quantity > 99) {
            return new ValidationResult(false, "Số lượng không được vượt quá 99");
        }
        
        return new ValidationResult(true, "Valid");
    }

    /**
     * Clean input string (trim and normalize)
     */
    public static String cleanInput(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().replaceAll("\\s+", " ");
    }

    /**
     * Result class cho validation
     */
    public static class ValidationResult {
        private boolean valid;
        private String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
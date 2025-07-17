package com.example.myapplication.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.myapplication.model.User;
import java.util.HashSet;
import java.util.Set;

/**
 * Enhanced UserManager với support cho Sign Up/Login với email và password
 */
public class UserManager {
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_USERNAME = "current_username";
    private static final String KEY_REGISTERED_USERS = "registered_users";
    
    // User data keys pattern: {username}_{field}
    private static final String KEY_EMAIL_SUFFIX = "_email";
    private static final String KEY_PASSWORD_SUFFIX = "_password_hash";
    private static final String KEY_FULLNAME_SUFFIX = "_full_name";
    private static final String KEY_ADDRESS_SUFFIX = "_address";
    private static final String KEY_PHONE_SUFFIX = "_phone";
    private static final String KEY_CREATED_DATE_SUFFIX = "_created_date";
    private static final String KEY_VERIFIED_SUFFIX = "_verified";

    private static UserManager instance;
    private SharedPreferences prefs;
    private Context context;

    private UserManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Đăng ký tài khoản mới
     */
    public SignUpResult signUp(String username, String email, String password, 
                              String fullName, String phone) {
        
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            return new SignUpResult(false, "Tên đăng nhập không được để trống");
        }
        
        if (!User.isValidEmail(email)) {
            return new SignUpResult(false, "Email không hợp lệ");
        }
        
        if (!User.isValidPassword(password)) {
            return new SignUpResult(false, "Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ và số");
        }
        
        if (fullName == null || fullName.trim().isEmpty()) {
            return new SignUpResult(false, "Họ tên không được để trống");
        }
        
        if (!User.isValidPhone(phone)) {
            return new SignUpResult(false, "Số điện thoại không hợp lệ");
        }

        // Kiểm tra username đã tồn tại
        if (isUsernameExists(username)) {
            return new SignUpResult(false, "Tên đăng nhập đã tồn tại");
        }

        // Kiểm tra email đã tồn tại
        if (isEmailExists(email)) {
            return new SignUpResult(false, "Email đã được sử dụng");
        }

        // Tạo user mới
        User newUser = new User(username, email, password, fullName, "Nhập địa chỉ giao hàng", phone);
        
        // Lưu user
        if (saveUser(newUser)) {
            // Tự động đăng nhập sau khi đăng ký thành công
            setCurrentUser(username);
            return new SignUpResult(true, "Đăng ký thành công!");
        } else {
            return new SignUpResult(false, "Lỗi khi lưu thông tin tài khoản");
        }
    }

    /**
     * Đăng nhập với username/email và password
     */
    public LoginResult login(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            return new LoginResult(false, "Tên đăng nhập/Email không được để trống");
        }
        
        if (password == null || password.trim().isEmpty()) {
            return new LoginResult(false, "Mật khẩu không được để trống");
        }

        // Tìm user theo username hoặc email
        String username = findUsernameByUsernameOrEmail(usernameOrEmail);
        
        if (username == null) {
            return new LoginResult(false, "Tài khoản không tồn tại");
        }

        // Kiểm tra password
        User user = loadUserByUsername(username);
        if (user != null && user.verifyPassword(password)) {
            setCurrentUser(username);
            return new LoginResult(true, "Đăng nhập thành công!");
        } else {
            return new LoginResult(false, "Mật khẩu không đúng");
        }
    }

    /**
     * Đăng nhập đơn giản (backward compatibility với code cũ)
     */
    public boolean login(String username) {
        if (isUsernameExists(username)) {
            setCurrentUser(username);
            return true;
        } else {
            // Tạo user mới với thông tin mặc định (không có email/password)
            User newUser = new User(username, "", "default", "", "Nhập địa chỉ giao hàng", "");
            if (saveUser(newUser)) {
                setCurrentUser(username);
                return true;
            }
        }
        return false;
    }

    /**
     * Kiểm tra trạng thái đăng nhập
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Lấy thông tin user hiện tại
     */
    public User getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }
        
        String currentUsername = prefs.getString(KEY_CURRENT_USERNAME, "");
        return loadUserByUsername(currentUsername);
    }

    /**
     * Cập nhật thông tin user
     */
    public boolean updateUserInfo(String fullName, String address, String phone) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // Validate
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        
        if (!User.isValidPhone(phone)) {
            return false;
        }

        // Update user info
        currentUser.setFullName(fullName);
        currentUser.setAddress(address);
        currentUser.setPhone(phone);

        return saveUser(currentUser);
    }

    /**
     * Đăng xuất
     */
    public void logout() {
        prefs.edit()
             .putBoolean(KEY_IS_LOGGED_IN, false)
             .putString(KEY_CURRENT_USERNAME, "")
             .apply();
    }

    /**
     * Lưu user vào SharedPreferences
     */
    private boolean saveUser(User user) {
        try {
            SharedPreferences.Editor editor = prefs.edit();
            String username = user.getUsername();

            // Lưu thông tin user
            editor.putString(username + KEY_EMAIL_SUFFIX, user.getEmail());
            editor.putString(username + KEY_PASSWORD_SUFFIX, user.getPasswordHash());
            editor.putString(username + KEY_FULLNAME_SUFFIX, user.getFullName());
            editor.putString(username + KEY_ADDRESS_SUFFIX, user.getAddress());
            editor.putString(username + KEY_PHONE_SUFFIX, user.getPhone());
            editor.putLong(username + KEY_CREATED_DATE_SUFFIX, user.getCreatedDate());
            editor.putBoolean(username + KEY_VERIFIED_SUFFIX, user.isVerified());

            // Thêm username vào danh sách registered users
            Set<String> registeredUsers = prefs.getStringSet(KEY_REGISTERED_USERS, new HashSet<>());
            registeredUsers.add(username);
            editor.putStringSet(KEY_REGISTERED_USERS, registeredUsers);

            editor.apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load user từ SharedPreferences
     */
    private User loadUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }

        try {
            String email = prefs.getString(username + KEY_EMAIL_SUFFIX, "");
            String passwordHash = prefs.getString(username + KEY_PASSWORD_SUFFIX, "");
            String fullName = prefs.getString(username + KEY_FULLNAME_SUFFIX, "");
            String address = prefs.getString(username + KEY_ADDRESS_SUFFIX, "Nhập địa chỉ giao hàng");
            String phone = prefs.getString(username + KEY_PHONE_SUFFIX, "");
            long createdDate = prefs.getLong(username + KEY_CREATED_DATE_SUFFIX, System.currentTimeMillis());
            boolean verified = prefs.getBoolean(username + KEY_VERIFIED_SUFFIX, false);

            return new User(username, email, passwordHash, fullName, address, phone, createdDate, verified);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Kiểm tra username đã tồn tại
     */
    private boolean isUsernameExists(String username) {
        Set<String> registeredUsers = prefs.getStringSet(KEY_REGISTERED_USERS, new HashSet<>());
        return registeredUsers.contains(username);
    }

    /**
     * Kiểm tra email đã tồn tại
     */
    private boolean isEmailExists(String email) {
        Set<String> registeredUsers = prefs.getStringSet(KEY_REGISTERED_USERS, new HashSet<>());
        
        for (String username : registeredUsers) {
            String userEmail = prefs.getString(username + KEY_EMAIL_SUFFIX, "");
            if (email.equalsIgnoreCase(userEmail)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tìm username theo username hoặc email
     */
    private String findUsernameByUsernameOrEmail(String usernameOrEmail) {
        Set<String> registeredUsers = prefs.getStringSet(KEY_REGISTERED_USERS, new HashSet<>());
        
        // Kiểm tra trực tiếp username trước
        if (registeredUsers.contains(usernameOrEmail)) {
            return usernameOrEmail;
        }
        
        // Kiểm tra email
        for (String username : registeredUsers) {
            String userEmail = prefs.getString(username + KEY_EMAIL_SUFFIX, "");
            if (usernameOrEmail.equalsIgnoreCase(userEmail)) {
                return username;
            }
        }
        
        return null;
    }

    /**
     * Set user hiện tại
     */
    private void setCurrentUser(String username) {
        prefs.edit()
             .putBoolean(KEY_IS_LOGGED_IN, true)
             .putString(KEY_CURRENT_USERNAME, username)
             .apply();
    }

    /**
     * Result class cho Sign Up
     */
    public static class SignUpResult {
        private boolean success;
        private String message;

        public SignUpResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }

    /**
     * Result class cho Login
     */
    public static class LoginResult {
        private boolean success;
        private String message;

        public LoginResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
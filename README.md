# 🍜 Restaurant Management App

**PRM392 Assignment - Food Ordering & Restaurant Management System**

Ứng dụng Android quản lý nhà hàng toàn diện với hai vai trò: **Customer** (khách hàng) và **Owner** (chủ nhà hàng), được phát triển với Java và Android SDK.

![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![Language](https://img.shields.io/badge/language-Java-orange.svg)
![MinSDK](https://img.shields.io/badge/Min%20SDK-24-blue.svg)
![TargetSDK](https://img.shields.io/badge/Target%20SDK-35-blue.svg)

---

## 📱 Tính năng chính

### 👥 Dành cho Customer (Khách hàng)
- **🔐 Đăng ký/Đăng nhập** với email và password
- **📜 Xem thực đơn** với các danh mục món ăn
- **🛒 Quản lý giỏ hàng** (thêm/xóa/sửa số lượng)
- **💳 Đặt hàng** với thông tin giao hàng
- **📋 Lịch sử đơn hàng** và theo dõi trạng thái
- **👤 Quản lý hồ sơ** cá nhân
- **🗺️ Xem vị trí nhà hàng**
- **🔔 Thông báo giỏ hàng** thông minh

### 🏪 Dành cho Owner (Chủ nhà hàng)
- **📊 Dashboard tổng quan** doanh thu và đơn hàng
- **🍽️ Quản lý thực đơn** (thêm/sửa/xóa/ẩn món ăn)
- **📋 Quản lý đơn hàng** và cập nhật trạng thái
- **👥 Quản lý khách hàng**
- **💰 Báo cáo doanh thu** theo ngày/tháng
- **🔧 Công cụ debug** kiểm tra hệ thống

---

## 🏗️ Kiến trúc ứng dụng

### 📁 Cấu trúc thư mục
```
app/src/main/java/com/example/myapplication/
├── 📱 activity/                 # Các màn hình
│   ├── owner/                   # Màn hình dành cho Owner
│   ├── LoginActivity.java       # Đăng nhập/Đăng ký
│   ├── MainActivity.java        # Trang chủ Customer
│   ├── MenuActivity.java        # Thực đơn
│   ├── CartActivity.java        # Giỏ hàng
│   └── ...
├── 🎨 adapter/                  # RecyclerView Adapters
├── 💾 manager/                  # Quản lý dữ liệu
│   ├── UserManager.java         # Quản lý người dùng
│   ├── BillManager.java         # Quản lý đơn hàng
│   ├── CartManager.java         # Quản lý giỏ hàng
│   └── FoodDataManager.java     # Quản lý món ăn
├── 📝 model/                    # Các model dữ liệu
│   ├── User.java               # Model người dùng
│   ├── FoodItem.java           # Model món ăn
│   ├── Bill.java               # Model đơn hàng
│   └── CartItem.java           # Model giỏ hàng
├── 🛠️ utils/                   # Tiện ích
└── 🔧 wrapper/                 # Helper classes
```

### 🏛️ Kiến trúc Pattern
- **📊 Data Management**: Singleton Pattern cho các Manager
- **🔄 State Management**: SharedPreferences cho lưu trữ cục bộ
- **🎯 Observer Pattern**: Interface callbacks cho UI updates
- **🏗️ Builder Pattern**: Cho complex object creation
- **🔒 Thread Safety**: Synchronized methods cho concurrent access

---

## 🚀 Công nghệ sử dụng

| Thành phần | Công nghệ | Phiên bản |
|------------|-----------|-----------|
| **🖋️ Ngôn ngữ** | Java | 11 |
| **📱 Platform** | Android | API 24-35 |
| **🎨 UI Framework** | Material Design | 1.12.0 |
| **📐 Layout** | ConstraintLayout + RecyclerView | Latest |
| **💾 Data Storage** | SharedPreferences + Gson | 2.10.1 |
| **🔗 View Binding** | Android View Binding | Enabled |
| **🛠️ Build Tool** | Gradle (Kotlin DSL) | 8.11.1 |

---

## 📦 Cài đặt và chạy dự án

### 📋 Yêu cầu hệ thống
- **🔧 Android Studio**: Arctic Fox hoặc mới hơn
- **📱 Android SDK**: API Level 24-35
- **☕ Java**: JDK 11 hoặc mới hơn
- **📏 Thiết bị**: Android 7.0 (API 24) trở lên

### 🏃‍♂️ Bước chạy dự án

1. **📥 Clone repository**
   ```bash
   git clone git@github.com:Peterxyjz/prm392-asm.git
   cd prm392-asm
   ```

2. **🔨 Mở trong Android Studio**
   - Mở Android Studio
   - Chọn "Open an existing project"
   - Điều hướng đến thư mục `prm392-asm`

3. **🔄 Sync dependencies**
   ```bash
   ./gradlew sync
   ```

4. **▶️ Chạy ứng dụng**
   - Kết nối thiết bị Android hoặc mở emulator
   - Nhấn **Run** (Ctrl+R) trong Android Studio

---

## 👤 Thông tin đăng nhập

### 🏪 Tài khoản Owner (Chủ nhà hàng)
```
👤 Username: owner
📧 Email: owner@gmail.com
🔑 Password: Huy123
```

### 👥 Tài khoản Customer
```
Có thể đăng ký tài khoản mới hoặc sử dụng quick login
```

---

## 🎮 Hướng dẫn sử dụng

### 🏪 Dành cho Owner

1. **🚀 Đăng nhập**
   - Sử dụng tài khoản owner ở trên
   - Hệ thống tự động chuyển đến Owner Dashboard

2. **📊 Xem Dashboard**
   - Tổng quan doanh thu, đơn hàng, khách hàng
   - Thống kê theo trạng thái đơn hàng

3. **🍽️ Quản lý Menu**
   - Thêm món ăn mới với hình ảnh
   - Chỉnh sửa thông tin món ăn
   - Ẩn/hiện món ăn

4. **📋 Quản lý Đơn hàng**
   - Xem danh sách đơn hàng realtime
   - Cập nhật trạng thái: Chờ xử lý → Xác nhận → Chuẩn bị → Sẵn sàng → Giao hàng → Hoàn thành
   - Xem chi tiết đơn hàng

5. **🔧 Debug Tools**
   - Long click vào "Chào mừng..." để mở menu debug
   - Kiểm tra và sửa chữa ID trùng lặp
   - Xem thông tin hệ thống

### 👥 Dành cho Customer

1. **📝 Đăng ký/Đăng nhập**
   - Đăng ký với email, password, thông tin cá nhân
   - Hoặc sử dụng quick login đơn giản

2. **🛒 Đặt hàng**
   - Xem thực đơn theo danh mục
   - Thêm món vào giỏ hàng
   - Điều chỉnh số lượng trong giỏ hàng
   - Checkout với thông tin giao hàng

3. **📋 Theo dõi đơn hàng**
   - Xem lịch sử đơn hàng
   - Theo dõi trạng thái realtime
   - Xem chi tiết từng đơn hàng

4. **👤 Quản lý hồ sơ**
   - Cập nhật thông tin cá nhân
   - Thay đổi địa chỉ giao hàng mặc định

---

## 💾 Cấu trúc dữ liệu

### 🗃️ Lưu trữ cục bộ (SharedPreferences)

| Key Pattern | Mô tả | Ví dụ |
|-------------|-------|-------|
| `{username}_bills` | Đơn hàng của user | `john_bills` |
| `{username}_cart` | Giỏ hàng của user | `john_cart` |
| `{username}_email` | Email của user | `john_email` |
| `global_next_bill_id` | ID đơn hàng tiếp theo | `1001` |
| `is_logged_in` | Trạng thái đăng nhập | `true/false` |

### 📊 Models chính

```java
// User - Người dùng
User {
    username, email, passwordHash, 
    fullName, address, phone, 
    role (CUSTOMER/OWNER)
}

// FoodItem - Món ăn  
FoodItem {
    id, name, description, price,
    imageResource, category, available
}

// Bill - Đơn hàng
Bill {
    billId, username, items, totalAmount,
    deliveryAddress, status, orderDate
}

// CartItem - Giỏ hàng
CartItem {
    foodItem, quantity, notes
}
```

---

## 🔧 Tính năng nâng cao

### 🛡️ Bảo mật
- **🔐 Password Hashing**: SHA-256 với salt
- **✅ Input Validation**: Email, phone, password format
- **🎭 Role-based Access**: Customer/Owner permissions
- **🔒 Thread Safety**: Synchronized data operations

### 📱 UX/UI
- **🎨 Material Design**: Modern Android UI
- **📱 Responsive**: Hỗ trợ nhiều kích thước màn hình
- **🔔 Smart Notifications**: Thông báo giỏ hàng thông minh
- **🔄 Real-time Updates**: Cập nhật trạng thái tự động

### 🚀 Performance
- **⚡ Efficient Data Loading**: Lazy loading cho large lists
- **💾 Local Caching**: Cache món ăn và đơn hàng
- **🧵 Background Processing**: Async operations
- **🔄 Auto ID Management**: Tự động sửa ID trùng lặp

---

## 🐛 Troubleshooting

### ❗ Lỗi thường gặp

| Lỗi | Nguyên nhân | Giải pháp |
|-----|-------------|-----------|
| **ID đơn hàng trùng lặp** | Race condition | Sử dụng debug menu để fix |
| **Không cập nhật được trạng thái** | Đơn hàng có ID trùng | Chạy `validateAndFixDuplicateIds()` |
| **Owner vào màn hình Customer** | Lỗi role checking | Đã fix trong code mới |
| **Giỏ hàng không sync** | User context issue | Logout và login lại |

### 🔧 Debug Tools

1. **Owner Debug Menu**:
   - Long click "Chào mừng..." trong Owner Dashboard
   - Chọn "Kiểm tra ID trùng lặp" hoặc "Sửa chữa ID trùng lặp"

2. **Logs**:
   ```bash
   adb logcat | grep "BillManager\|UserManager\|CartManager"
   ```

---

## 🎯 Kế hoạch phát triển

### 🔮 Tính năng tương lai
- [ ] **🌐 API Integration**: Kết nối server backend
- [ ] **📷 Image Upload**: Upload ảnh món ăn từ camera
- [ ] **💳 Payment Gateway**: Tích hợp thanh toán online
- [ ] **📊 Advanced Analytics**: Báo cáo chi tiết hơn
- [ ] **🔔 Push Notifications**: Thông báo đẩy realtime
- [ ] **⭐ Rating System**: Đánh giá món ăn
- [ ] **🎁 Loyalty Program**: Chương trình khách hàng thân thiết

### 🐛 Cần cải thiện
- [ ] **🔄 Data Sync**: Đồng bộ dữ liệu đa thiết bị
- [ ] **🎨 UI Polish**: Cải thiện giao diện
- [ ] **📱 Tablet Support**: Hỗ trợ tablet tốt hơn
- [ ] **🌍 Internationalization**: Đa ngôn ngữ
- [ ] **♿ Accessibility**: Hỗ trợ người khuyết tật

---

## 👨‍💻 Thông tin phát triển

### 🏗️ Architecture Decisions
- **📱 Native Android**: Chọn native thay vì cross-platform cho performance
- **💾 Local Storage**: Sử dụng SharedPreferences thay vì SQLite cho đơn giản
- **🎯 Singleton Pattern**: Đảm bảo data consistency
- **🔒 Thread Safety**: Sử dụng synchronized cho concurrent access

### 📊 Performance Metrics
- **🚀 App Start Time**: < 2 giây
- **💾 Memory Usage**: < 50MB average
- **🔋 Battery Efficient**: Optimized background tasks
- **📱 Smooth UI**: 60 FPS target

---

## 📞 Liên hệ & Hỗ trợ

- **👨‍🎓 Developer**: PRM392 Student
- **📧 Email**: Contact via course instructor
- **📚 Course**: PRM392 - Mobile Programming
- **🏫 University**: FPT University

---

## 📄 License

Dự án được phát triển cho mục đích học tập trong khóa học PRM392.

---

**⭐ Cảm ơn bạn đã sử dụng Restaurant Management App! ⭐**

*Nếu gặp vấn đề gì, hãy kiểm tra phần Troubleshooting hoặc sử dụng debug tools có sẵn trong app.*

# Sakura Restaurant - Ứng dụng đặt món ăn Nhật Bản

## ✨ CẬP NHẬT MỚI: HỖ TRỢ NHIỀU USER
**Bây giờ nhiều người có thể sử dụng cùng 1 thiết bị với tên khác nhau!**

## Luồng đăng nhập mới (đã cập nhật)

### 🆕 Hỗ trợ nhiều user trên cùng 1 thiết bị:

1. **User đầu tiên** (minat):
   - Nhập "minat" → "Chào mừng minat! Tài khoản mới đã được tạo."
   - Logout

2. **User thứ hai** (duc):
   - Nhập "duc" → "Chào mừng duc! Tài khoản mới đã được tạo."
   - Logout

3. **User đầu tiên quay lại**:
   - Nhập "minat" → "Chào mừng trở lại, minat!"
   - App sẽ load lại thông tin cũ (address, phone đã lưu)

4. **User thứ hai quay lại**:
   - Nhập "duc" → "Chào mừng trở lại, duc!"
   - App sẽ load thông tin riêng của duc

### 🔄 **Cách hoạt động:**
- Mỗi username có 1 profile riêng biệt
- Thông tin được lưu riêng cho từng user (address, phone, full name)
- Giỏ hàng được chia sẻ chung (design choice cho đơn giản)
- Logout chỉ đổi user, không xóa dữ liệu

## Tính năng chính

### 🔐 Đăng nhập đa người dùng
- Chỉ cần nhập tên để đăng nhập/tạo tài khoản
- Hỗ trợ nhiều user khác nhau trên cùng 1 thiết bị
- Tự động nhận biết user cũ/mới
- Lưu thông tin riêng biệt cho từng user
- Không cần password

### 🏠 Trang chủ
- Giới thiệu nhà hàng Sakura Restaurant
- Thông tin dịch vụ giao hàng
- Nút truy cập thực đơn và giỏ hàng
- Hiển thị số lượng món trong giỏ hàng
- **🗺️ Vị trí nhà hàng** - Xem địa chỉ và bản đồ

### 🍜 Thực đơn
- Danh sách món ăn Nhật Bản với hình ảnh đầy đủ
- Bộ lọc theo danh mục (Noodles, Sushi, Rice, Appetizer)
- Thêm/bớt số lượng món trực tiếp từ thực đơn
- Hiển thị trạng thái món đã có trong giỏ hàng

### 🛒 Giỏ hàng
- Hiển thị đầy đủ thông tin món: hình ảnh, tên, mô tả, giá
- Điều chỉnh số lượng với nút +/-
- Xác nhận xóa món khi số lượng = 1 và nhấn nút -
- Tính toán tự động: tạm tính, phí giao hàng, tổng cộng
- Miễn phí giao hàng cho đơn hàng trên 100.000₫
- Xóa toàn bộ giỏ hàng sau khi thanh toán thành công

### 👤 Thông tin cá nhân
- Cập nhật họ tên, số điện thoại, địa chỉ giao hàng riêng cho từng user
- Tên đăng nhập không thể thay đổi
- Đăng xuất để chuyển user

### 🗺️ Vị trí nhà hàng
- Hiển thị thông tin chi tiết nhà hàng: địa chỉ, số điện thoại, giờ mở cửa
- Mở Google Maps để xem vị trí chính xác
- Lấy chỉ đường từ vị trí hiện tại đến nhà hàng
- Gọi điện trực tiếp đến nhà hàng
- Thông tin giao thông và chỗ đữ xe

### 💰 Thanh toán
- Xác nhận thông tin giao hàng của user hiện tại
- Hiển thị chi tiết đơn hàng
- Thông báo thành công và thời gian giao hàng dự kiến

## Cấu trúc dữ liệu (SharedPreferences)

```
user_prefs:
├── is_logged_in: true/false
├── current_username: "minat"
├── minat_full_name: "Minh Anh"
├── minat_address: "123 Nguyen Hue"
├── minat_phone: "0123456789"
├── duc_full_name: "Duc Anh"
├── duc_address: "456 Le Loi"
└── duc_phone: "0987654321"

cart_prefs:
├── minat_cart_items: [{"foodItem":{...}, "quantity":2}]
└── duc_cart_items: [{"foodItem":{...}, "quantity":1}]

bill_prefs:
├── minat_bills: [{"id":1, "items":[...], "total":285000}]
├── minat_next_id: 2
├── duc_bills: []
└── duc_next_id: 1
```

## Demo scenarios

### Scenario 1: Gia đình sử dụng chung
```
Bố: "minat" → Profile riêng với địa chỉ nhà
Mẹ: "lan" → Profile riêng với địa chỉ công ty
Con: "nam" → Profile riêng với địa chỉ trường học
```

### Scenario 2: Bạn bè share thiết bị
```
Bạn A: "duc" → Thông tin riêng
Bạn B: "huy" → Thông tin riêng
Bạn C: "linh" → Thông tin riêng
```

## Ưu điểm của cách thiết kế này

✅ **Flexibility**: Nhiều người dùng cùng 1 thiết bị
✅ **Privacy**: Mỗi user có thông tin riêng
✅ **Simplicity**: Không cần password phức tạp
✅ **User Experience**: Nhanh chóng đăng nhập/chuyển user
✅ **Data Persistence**: Nhớ thông tin từng user
✅ **Family Friendly**: Phù hợp cho gia đình sử dụng chung

## Technical Implementation

### UserManager Methods:
```java
login(username)              // Cho phép bất kỳ tên nào
isExistingUser(username)     // Check user đã tồn tại chưa  
getCurrentUser()             // Lấy thông tin user hiện tại
updateUserInfo()             // Cập nhật riêng cho user hiện tại
logout()                     // Chỉ đổi trạng thái, giữ data
```

### Data Storage Strategy:
- **Key Pattern**: `{username}_{field_name}`
- **Separation**: Mỗi user có namespace riêng
- **Current State**: Track user nào đang đăng nhập
- **Persistence**: Data không bị mất khi logout

Bây giờ bạn có thể test với nhiều tên khác nhau: minat, duc, lan, huy, v.v... 🎉
# 📚 ỨNG DỤNG QUẢN LÝ THƯ VIỆN

Ứng dụng Android quản lý mượn - trả sách cho sinh viên được xây dựng bằng Java và Firebase.

---

## 🚀 BẮT ĐẦU NHANH

**📋 TỔNG KẾT DỰ ÁN?** → Đọc **[FINAL_SUMMARY.md](./FINAL_SUMMARY.md)** ⭐ MỚI!

**MỚI SỬ DỤNG?** → Đọc **[INDEX.md](./INDEX.md)** để biết đọc tài liệu nào trước!

**SETUP NHANH 5 PHÚT?** → Xem **[QUICKSTART.md](./QUICKSTART.md)**

**KIỂM TRA ĐÃ CÀI ĐẶT ĐỦ?** → Check **[CHECKLIST.md](./CHECKLIST.md)**

**TÍNH NĂNG MỚI:**
- ✅ **[CRUD Sách](./CRUD_SUMMARY.md)** - Thêm/Sửa/Xóa sách
- ✅ **[Hệ thống Duyệt](./APPROVAL_SUMMARY.md)** - Duyệt user & mượn sách

---

## 🎯 Mục tiêu đề tài

Xây dựng ứng dụng quản lý mượn – trả sách cho sinh viên với 2 loại người dùng:
- **Sinh viên**: Đăng ký, đăng nhập, xem sách, mượn/trả sách, xem lịch sử mượn
- **Admin (Thủ thư)**: Thêm/sửa/xóa sách, duyệt yêu cầu mượn, thống kê lượt mượn

## 🏗️ Kiến trúc ứng dụng

### Frontend
- **Platform**: Android (Java)
- **IDE**: Android Studio
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36

### Backend
- **Firebase Authentication**: Quản lý đăng nhập/đăng ký
- **Cloud Firestore**: Database NoSQL lưu trữ dữ liệu
- **Firebase Storage**: Lưu trữ hình ảnh sách (optional)

## 📱 Các màn hình chính

### Dành cho Sinh viên
1. **Màn hình Đăng nhập/Đăng ký**
   - Đăng ký tài khoản mới
   - Đăng nhập với email/password
   - Quên mật khẩu

2. **Trang chủ - Danh sách sách**
   - Hiển thị tất cả sách trong thư viện
   - RecyclerView với CardView
   - Xem chi tiết sách
   - Nút "Mượn sách"

3. **Phiếu mượn của tôi**
   - Danh sách sách đã mượn
   - Trạng thái: Đang mượn / Đã trả
   - Nút "Trả sách"
   - Hiển thị ngày mượn, hạn trả

4. **Tài khoản**
   - Thông tin cá nhân
   - Nút đăng xuất

### Dành cho Admin
1. **Quản lý sách**
   - Xem danh sách sách
   - Thêm sách mới (FAB button)
   - Sửa thông tin sách
   - Xóa sách

2. **Quản lý mượn trả**
   - Xem tất cả phiếu mượn
   - Thông tin người mượn
   - Trạng thái mượn/trả

3. **Thống kê**
   - Tổng số sách
   - Tổng lượt mượn
   - Số phiếu đang mượn
   - Biểu đồ thống kê (TODO)

4. **Tài khoản**
   - Thông tin admin
   - Đăng xuất

## 🗄️ Cấu trúc dữ liệu Firestore

### Collection: `books`
```javascript
{
  id: "B001",
  title: "Lập trình Java cơ bản",
  author: "Nguyễn Văn A",
  category: "Công nghệ thông tin",
  quantity: 5,
  imageUrl: "url_to_image",
  description: "Mô tả sách..."
}
```

### Collection: `users`
```javascript
{
  uid: "user_uid_from_auth",
  name: "Huỳnh Chí Phúc",
  email: "phuc@gmail.com",
  role: "student" // hoặc "admin"
}
```

### Collection: `borrows`
```javascript
{
  borrowId: "br001",
  userId: "user_uid",
  userName: "Huỳnh Chí Phúc",
  bookId: "B001",
  bookTitle: "Lập trình Java cơ bản",
  borrowDate: Timestamp,
  dueDate: Timestamp,
  returnDate: Timestamp hoặc null,
  status: "Đang mượn" // hoặc "Đã trả", "Quá hạn"
}
```

## 🛠️ Công nghệ sử dụng

### Dependencies
- **AndroidX Libraries**:
  - AppCompat
  - Material Design Components
  - RecyclerView
  - CardView
  - ConstraintLayout

- **Firebase SDK**:
  - Firebase BOM 33.5.1
  - Firebase Authentication
  - Cloud Firestore
  - Firebase Storage

- **Image Loading**:
  - Glide 4.16.0

### Build System
- Gradle 8.13.0
- Java 11

## 📦 Cấu trúc thư mục

```
app/src/main/java/com/example/do_an/
├── activities/
│   ├── LoginActivity.java
│   ├── RegisterActivity.java
│   ├── StudentMainActivity.java
│   └── AdminMainActivity.java
├── fragments/
│   ├── BookListFragment.java
│   ├── MyBorrowsFragment.java
│   ├── ProfileFragment.java
│   ├── AdminBookManagementFragment.java
│   ├── AdminBorrowManagementFragment.java
│   └── AdminStatisticsFragment.java
├── adapters/
│   ├── BookAdapter.java
│   ├── BorrowAdapter.java
│   ├── AdminBookAdapter.java
│   └── AdminBorrowAdapter.java
├── models/
│   ├── Book.java
│   ├── User.java
│   └── Borrow.java
└── MainActivity.java (legacy)

app/src/main/res/
├── layout/
│   ├── activity_login.xml
│   ├── activity_register.xml
│   ├── activity_student_main.xml
│   ├── activity_admin_main.xml
│   ├── fragment_*.xml
│   └── item_*.xml
└── menu/
    ├── bottom_nav_menu_student.xml
    └── bottom_nav_menu_admin.xml
```

## ⚙️ Chức năng chi tiết

### ✅ Đã hoàn thành

#### Sinh viên:
- [x] Đăng ký tài khoản
- [x] Đăng nhập
- [x] Xem danh sách sách
- [x] Mượn sách (tự động giảm quantity)
- [x] Xem phiếu mượn của mình
- [x] Trả sách (tự động tăng quantity)
- [x] Xem thông tin tài khoản
- [x] Đăng xuất

#### Admin:
- [x] Đăng nhập
- [x] Xem tất cả sách
- [x] Xóa sách
- [x] Xem tất cả phiếu mượn
- [x] Thống kê cơ bản
- [x] Đăng xuất

### 🔄 Đang phát triển (TODO)
- [ ] Thêm sách mới
- [ ] Sửa thông tin sách
- [ ] Tìm kiếm sách
- [ ] Lọc sách theo thể loại
- [ ] Thông báo sắp đến hạn trả
- [ ] Quét QR code
- [ ] Biểu đồ thống kê (MPAndroidChart)
- [ ] Upload ảnh sách từ device
- [ ] Quên mật khẩu

## 🚀 Hướng dẫn chạy ứng dụng

### Bước 1: Cài đặt môi trường
1. Cài đặt **Android Studio** (latest version)
2. Clone hoặc download project này
3. Mở project trong Android Studio

### Bước 2: Cấu hình Firebase
👉 **XEM CHI TIẾT TẠI**: [FIREBASE_SETUP.md](./FIREBASE_SETUP.md)

Tóm tắt:
1. Tạo Firebase project
2. Thêm Android app với package name: `com.example.do_an`
3. Tải file `google-services.json` và copy vào thư mục `app/`
4. Enable Firebase Authentication (Email/Password)
5. Tạo Cloud Firestore database

### Bước 3: Sync và Build
```bash
# Sync Gradle
File → Sync Project with Gradle Files

# Build project
Build → Make Project

# Run app
Run → Run 'app'
```

### Bước 4: Tạo tài khoản thử nghiệm

#### Admin:
- Email: `admin@gmail.com`
- Password: `admin123`

#### Sinh viên:
- Email: `student@gmail.com`
- Password: `student123`

## 📊 Use Cases

### Use Case 1: Sinh viên mượn sách
1. Đăng nhập vào app
2. Xem danh sách sách ở tab "Sách"
3. Click vào sách muốn mượn
4. Click nút "Mượn sách"
5. Xác nhận mượn
6. Sách được thêm vào "Phiếu mượn"
7. Số lượng sách giảm 1

### Use Case 2: Sinh viên trả sách
1. Vào tab "Phiếu mượn"
2. Tìm sách cần trả (status = "Đang mượn")
3. Click nút "Trả sách"
4. Xác nhận trả
5. Status chuyển thành "Đã trả"
6. Số lượng sách tăng 1

### Use Case 3: Admin quản lý sách
1. Đăng nhập với tài khoản admin
2. Vào tab "Quản lý sách"
3. Xem danh sách tất cả sách
4. Click nút "+" để thêm sách mới
5. Click icon "Edit" để sửa
6. Click icon "Delete" để xóa

### Use Case 4: Admin xem thống kê
1. Vào tab "Thống kê"
2. Xem các số liệu:
   - Tổng số sách trong thư viện
   - Tổng số lượt mượn
   - Số phiếu đang mượn

## 🎨 Giao diện

### Bottom Navigation
- **Sinh viên**: 3 tabs (Sách, Phiếu mượn, Tài khoản)
- **Admin**: 4 tabs (Quản lý sách, Quản lý mượn, Thống kê, Tài khoản)

### Material Design Components
- CardView cho item lists
- FloatingActionButton cho thêm sách
- TextInputLayout cho forms
- ProgressBar cho loading states
- AlertDialog cho confirmations

## 📝 Notes

### Business Logic
- Thời hạn mượn sách: **14 ngày**
- Số lượng sách tự động cập nhật khi mượn/trả
- Mỗi user có thể mượn nhiều sách
- Admin có thể xem tất cả hoạt động mượn/trả

### Security
- Firebase Rules hiện đang ở **test mode** (cho development)
- Production cần update rules để bảo mật hơn

### Performance
- Sử dụng RecyclerView cho danh sách
- Glide cache images
- Firestore offline persistence

## 🤝 Đóng góp

Dự án này là đồ án môn học. Mọi đóng góp và góp ý xin gửi về:
- Email: [your-email]
- GitHub: [your-github]

## 📄 License

This project is for educational purposes.

---

**Developed with ❤️ by [Your Name]**
**Last Updated: November 2025**


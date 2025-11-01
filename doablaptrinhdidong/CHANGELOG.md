# 📝 CHANGELOG

## [1.3.0] - 2025-11-01

### 🐛 Fixed
- **CRITICAL:** Fixed NullPointerException crash khi sinh viên mượn sách
  - Thêm null checks cho `userId` và `borrowId`
  - Sử dụng `final` variables trong lambda expressions
  - Thêm fallback UUID nếu Firestore trả về null
  - File: `BookAdapter.java`

### ✨ Improved
- **Quên Mật Khẩu:** Đơn giản hóa không cần Cloud Functions
  - Sử dụng Firebase Auth `sendPasswordResetEmail()` trực tiếp
  - Không cần Blaze plan hay thẻ Visa nữa
  - Email được gửi miễn phí bởi Firebase
  - UI reset password đẹp do Firebase cung cấp
  - File: `ForgotPasswordActivity.java`

### 🗑️ Removed
- Xóa tất cả code liên quan Cloud Functions trong ForgotPasswordActivity
- Xóa Firebase Functions imports không cần thiết
- Xóa verification code logic phức tạp
- Đơn giản hóa từ 2 bước xuống 1 bước

### 📚 Documentation
- Thêm `FIXED_CRASH_AND_FORGOT_PASSWORD.md` - Chi tiết đầy đủ
- Thêm `QUICK_TEST_FIXED_FEATURES.md` - Hướng dẫn test nhanh
- Thêm `SUMMARY_CHANGES_01_11_2025.md` - Tổng hợp thay đổi
- Cập nhật `README.md` với thông tin mới nhất

---

## [1.2.0] - 2025-10-31

### ✨ Added
- Tính năng CRUD sách cho Admin
- Hệ thống duyệt user mới đăng ký
- Hệ thống duyệt yêu cầu mượn sách
- Tìm kiếm sách theo tên, tác giả, thể loại

### 📚 Documentation
- `CRUD_SUMMARY.md`
- `APPROVAL_SUMMARY.md`
- `SEARCH_FEATURE_SUMMARY.md`

---

## [1.1.0] - 2025-10-30

### ✨ Added
- Chức năng quên mật khẩu (với Cloud Functions)
- Email integration setup
- Firebase Functions deploy instructions

### 📚 Documentation
- `FEATURE_FORGOT_PASSWORD.md`
- `SETUP_EMAIL_CLOUD_FUNCTIONS.md`
- `QUICK_START_EMAIL.md`

---

## [1.0.0] - 2025-10-29

### ✨ Initial Release
- Đăng ký/Đăng nhập
- Phân quyền Admin/Sinh viên
- Quản lý sách
- Mượn/Trả sách
- Firebase Firestore integration
- Firebase Authentication

### 📚 Documentation
- `README.md`
- `QUICKSTART.md`
- `FIREBASE_SETUP.md`
- `BUILD_INSTRUCTIONS.md`

---

## Legend

- 🐛 `Fixed` - Bug fixes
- ✨ `Added` - New features
- ⚡ `Improved` - Enhancements
- 🗑️ `Removed` - Removed features
- 📚 `Documentation` - Documentation updates
- 🔒 `Security` - Security updates
- ⚠️ `Deprecated` - Soon-to-be removed features

---

_Maintained by: Library Management Team_


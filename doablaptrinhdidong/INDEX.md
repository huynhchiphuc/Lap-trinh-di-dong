# 📚 ỨNG DỤNG QUẢN LÝ THƯ VIỆN - INDEX

## 🚀 BẮT ĐẦU NHANH

Bạn là người mới? Hãy đọc theo thứ tự sau:

1. **[QUICKSTART.md](./QUICKSTART.md)** ⚡
   - Hướng dẫn chạy app trong 5 phút
   - Setup Firebase nhanh
   - Tạo dữ liệu mẫu

2. **[CHECKLIST.md](./CHECKLIST.md)** ✅
   - Kiểm tra đã cài đặt đủ chưa
   - Troubleshooting lỗi thường gặp

3. **[README.md](./README.md)** 📖
   - Tổng quan về dự án
   - Tính năng chính
   - Use cases

## 📚 TÀI LIỆU CHI TIẾT

### Cho Developer

4. **[FIREBASE_SETUP.md](./FIREBASE_SETUP.md)** 🔥
   - Hướng dẫn cấu hình Firebase từng bước
   - Tạo collections và documents
   - Firestore Rules
   - Sample data JSON

5. **[CODE_STRUCTURE.md](./CODE_STRUCTURE.md)** 🏗️
   - Giải thích kiến trúc code
   - Chi tiết từng class, method
   - Flow diagram
   - Firebase operations

### Cho Người Dùng

6. **User Manual** (TODO)
   - Hướng dẫn sử dụng app
   - Chức năng sinh viên
   - Chức năng admin

## 📁 CẤU TRÚC PROJECT

```
doablaptrinhdidong/
│
├── 📄 README.md                  ← Tổng quan dự án
├── 📄 QUICKSTART.md              ← Bắt đầu nhanh (ĐỌC ĐẦU TIÊN!)
├── 📄 CHECKLIST.md               ← Kiểm tra setup
├── 📄 FIREBASE_SETUP.md          ← Cấu hình Firebase
├── 📄 CODE_STRUCTURE.md          ← Giải thích code
├── 📄 INDEX.md                   ← File này
│
├── app/
│   ├── google-services.json      ← File từ Firebase (TẢI VỀ!)
│   ├── build.gradle.kts          ← App config
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/do_an/
│       │   ├── activities/       ← Login, Register, Main...
│       │   ├── fragments/        ← BookList, Profile...
│       │   ├── adapters/         ← RecyclerView adapters
│       │   └── models/           ← Book, User, Borrow
│       └── res/
│           ├── layout/           ← XML layouts
│           └── menu/             ← Bottom navigation
│
├── gradle/
│   └── libs.versions.toml        ← Dependencies versions
│
└── build.gradle.kts              ← Root config
```

## 🎯 CÁC BƯỚC THIẾT LẬP

### Bước 1: Clone/Download Project
```bash
git clone [repo-url]
# hoặc download ZIP và giải nén
```

### Bước 2: Mở trong Android Studio
```
File → Open → Chọn thư mục doablaptrinhdidong
```

### Bước 3: Setup Firebase
```
👉 Đọc FIREBASE_SETUP.md hoặc QUICKSTART.md
- Tạo Firebase project
- Download google-services.json
- Enable Authentication & Firestore
```

### Bước 4: Sync Gradle
```
File → Sync Project with Gradle Files
```

### Bước 5: Run App
```
Run → Run 'app' (hoặc Shift + F10)
```

## 📱 CHỨC NĂNG CHÍNH

### ✅ Đã hoàn thành

#### 👨‍🎓 Sinh viên
- Đăng ký/Đăng nhập
- Xem danh sách sách
- Mượn sách (quantity tự động giảm)
- Xem phiếu mượn của mình
- Trả sách (quantity tự động tăng)
- Xem thông tin tài khoản
- Đăng xuất

#### 👨‍💼 Admin
- Đăng nhập
- Xem tất cả sách
- Xóa sách
- Xem tất cả phiếu mượn (của mọi người)
- Thống kê: tổng sách, tổng mượn, đang mượn
- Đăng xuất

### 🔄 Đang phát triển (TODO)
- Thêm/Sửa sách (Admin)
- Tìm kiếm & lọc sách
- Thông báo hết hạn
- QR code mượn nhanh
- Biểu đồ thống kê

## 🛠️ TECH STACK

### Frontend
- **Platform**: Android (Java)
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36
- **IDE**: Android Studio

### Backend
- **Firebase Authentication**: Quản lý user
- **Cloud Firestore**: NoSQL database
- **Firebase Storage**: Lưu ảnh (optional)

### Libraries
- AndroidX (AppCompat, Material, RecyclerView, CardView)
- Firebase BOM 33.5.1
- Glide 4.16.0 (image loading)

## 📖 HƯỚNG DẪN SỬ DỤNG

### Dành cho Sinh viên

#### Đăng ký tài khoản
1. Mở app → Click "Đăng ký ngay"
2. Nhập: Họ tên, Email, Password
3. Chọn vai trò: **Sinh viên**
4. Click "Đăng ký"

#### Mượn sách
1. Đăng nhập → Tab "Sách"
2. Chọn sách → Click "Mượn sách"
3. Xác nhận → Xong!
4. Kiểm tra trong tab "Phiếu mượn"

#### Trả sách
1. Tab "Phiếu mượn"
2. Tìm sách có status "Đang mượn"
3. Click "Trả sách" → Xác nhận
4. Status chuyển thành "Đã trả"

### Dành cho Admin

#### Đăng nhập
1. Đăng ký với vai trò: **Quản trị viên**
2. Đăng nhập

#### Quản lý sách
1. Tab "Quản lý sách"
2. Xem danh sách
3. Click icon Delete để xóa
4. Click nút "+" để thêm (TODO)

#### Xem thống kê
1. Tab "Thống kê"
2. Xem số liệu:
   - Tổng số sách
   - Tổng lượt mượn
   - Đang mượn

## 🗄️ CẤU TRÚC DỮ LIỆU FIRESTORE

### Collection: `books`
```json
{
  "id": "B001",
  "title": "Lập trình Java cơ bản",
  "author": "Nguyễn Văn A",
  "category": "Công nghệ thông tin",
  "quantity": 5,
  "imageUrl": "https://...",
  "description": "Mô tả sách..."
}
```

### Collection: `users`
```json
{
  "uid": "firebase_auth_uid",
  "name": "Huỳnh Chí Phúc",
  "email": "phuc@gmail.com",
  "role": "student"
}
```

### Collection: `borrows`
```json
{
  "borrowId": "br001",
  "userId": "user_uid",
  "userName": "Huỳnh Chí Phúc",
  "bookId": "B001",
  "bookTitle": "Lập trình Java cơ bản",
  "borrowDate": "Timestamp",
  "dueDate": "Timestamp",
  "returnDate": "Timestamp | null",
  "status": "Đang mượn"
}
```

## 🐛 TROUBLESHOOTING

### Lỗi: google-services.json not found
```
✅ Copy file vào: app/google-services.json
✅ Sync Gradle lại
```

### Lỗi: Firebase Auth failed
```
✅ Enable Email/Password trong Firebase Console
✅ Kiểm tra internet
```

### Lỗi: Firestore permission denied
```
✅ Set Firestore Rules = test mode
✅ Xem FIREBASE_SETUP.md section Rules
```

### App không hiển thị sách
```
✅ Thêm sách vào Firestore collection "books"
✅ Xem QUICKSTART.md section "Thêm Sách Mẫu"
```

## 📞 HỖ TRỢ

### Các bước khi gặp vấn đề:
1. ✅ Kiểm tra CHECKLIST.md
2. ✅ Đọc phần Troubleshooting
3. ✅ Xem log trong Logcat (Android Studio)
4. ✅ Clean + Rebuild project

### Tài liệu tham khảo:
- **Firebase Docs**: https://firebase.google.com/docs
- **Android Docs**: https://developer.android.com
- **Material Design**: https://material.io

## 📊 TIẾN ĐỘ DỰ ÁN

```
[████████████████████████████░░░] 85% Complete

✅ Models & Database
✅ Authentication
✅ Sinh viên: Mượn/Trả sách
✅ Admin: Xem & Xóa sách
✅ Thống kê cơ bản
✅ UI/UX cơ bản

🔄 Thêm/Sửa sách
🔄 Tìm kiếm
🔄 Thông báo
🔄 QR code
🔄 Biểu đồ
```

## 🎓 THÔNG TIN DỰ ÁN

- **Tên dự án**: Ứng dụng Quản lý Thư viện
- **Loại**: Đồ án môn học
- **Platform**: Android (Java)
- **Database**: Firebase Firestore
- **Thời gian**: 2025
- **Phiên bản**: 1.0.0

## 📝 ROADMAP

### Phase 1 (Hoàn thành ✅)
- [x] Authentication
- [x] Mượn/Trả sách
- [x] Quản lý cơ bản
- [x] Thống kê

### Phase 2 (Tiếp theo)
- [ ] CRUD sách hoàn chỉnh
- [ ] Tìm kiếm & lọc
- [ ] Thông báo push

### Phase 3 (Tương lai)
- [ ] QR code
- [ ] Biểu đồ nâng cao
- [ ] Export báo cáo
- [ ] Multi-language

## 🌟 ĐIỂM NỔI BẬT

✨ **Clean Architecture**: Tách biệt Models, Views, Adapters
✨ **Material Design**: UI/UX đẹp, chuẩn Google
✨ **Real-time**: Sync ngay lập tức với Firebase
✨ **Secure**: Authentication + Firestore Rules
✨ **Scalable**: Dễ mở rộng thêm tính năng

---

## 🚀 SẴN SÀNG BẮT ĐẦU?

### Bạn mới hoàn toàn?
👉 Đọc **QUICKSTART.md** trước!

### Đã có kinh nghiệm?
👉 Kiểm tra **CHECKLIST.md** → Run app!

### Muốn hiểu code?
👉 Đọc **CODE_STRUCTURE.md**

### Cần setup Firebase?
👉 Follow **FIREBASE_SETUP.md**

---

**Chúc bạn code thành công! 🎉**

*Last updated: November 2025*


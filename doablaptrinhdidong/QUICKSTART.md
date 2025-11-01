# ⚡ HƯỚNG DẪN NHANH

## 🚀 Chạy ứng dụng trong 5 phút

### Bước 1: Mở Project
```
1. Mở Android Studio
2. File → Open → Chọn thư mục "doablaptrinhdidong"
3. Đợi Gradle sync xong
```

### Bước 2: Cấu hình Firebase (BẮT BUỘC)

#### 2.1. Tạo Firebase Project
1. Vào https://console.firebase.google.com/
2. Click "Add project" → Đặt tên "LibraryManagement"
3. Bỏ qua Google Analytics (không cần)
4. Click "Create project"

#### 2.2. Thêm Android App
1. Click biểu tượng Android ở trang chủ Firebase
2. Package name: **`com.example.do_an`** (QUAN TRỌNG!)
3. App nickname: "Library App" (tùy chọn)
4. Click "Register app"

#### 2.3. Download google-services.json
1. Click "Download google-services.json"
2. Copy file vào: **`D:\doablaptrinhdidong\app\`**
3. Vị trí đúng: `app/google-services.json` (cùng cấp với build.gradle.kts)

#### 2.4. Enable Authentication
1. Trong Firebase Console, vào **"Authentication"**
2. Click "Get started"
3. Tab "Sign-in method" → Enable **"Email/Password"**
4. Click "Save"

#### 2.5. Tạo Firestore Database
1. Vào **"Firestore Database"**
2. Click "Create database"
3. Chọn location: **"asia-southeast1 (Singapore)"**
4. Chọn **"Start in test mode"**
5. Click "Enable"

### Bước 3: Sync Gradle
```
1. Trong Android Studio: File → Sync Project with Gradle Files
2. Đợi sync hoàn tất (khoảng 1-2 phút)
```

### Bước 4: Chạy App
```
1. Click nút Run (▶️) hoặc Shift + F10
2. Chọn emulator hoặc thiết bị thật
3. Đợi app build và cài đặt
```

## 📝 Tạo Tài Khoản Thử Nghiệm

### Đăng ký Admin
```
Email: admin@gmail.com
Password: admin123456
Vai trò: Quản trị viên
```

### Đăng ký Sinh viên
```
Email: student@gmail.com
Password: student123
Vai trò: Sinh viên
```

## 📚 Thêm Sách Mẫu (Cho Admin)

Sau khi đăng ký admin, vào Firestore Console để thêm sách:

### Cách 1: Qua Firebase Console
1. Vào Firestore Database
2. Click "Start collection"
3. Collection ID: **`books`**
4. Document ID: **`B001`**
5. Thêm fields:

| Field | Type | Value |
|-------|------|-------|
| id | string | B001 |
| title | string | Lập trình Java cơ bản |
| author | string | Nguyễn Văn A |
| category | string | Công nghệ thông tin |
| quantity | number | 5 |
| imageUrl | string | https://via.placeholder.com/300x400?text=Java |
| description | string | Sách hướng dẫn Java từ cơ bản đến nâng cao |

6. Click "Save"
7. Lặp lại cho sách khác (B002, B003...)

### Cách 2: Import JSON (Nhanh hơn)
Sử dụng Firebase CLI hoặc copy code sau vào Firestore:

**Sách 1 (B001):**
```json
{
  "id": "B001",
  "title": "Lập trình Java cơ bản",
  "author": "Nguyễn Văn A",
  "category": "Công nghệ thông tin",
  "quantity": 5,
  "imageUrl": "https://via.placeholder.com/300x400?text=Java+Book",
  "description": "Sách hướng dẫn học Java từ cơ bản đến nâng cao cho người mới bắt đầu"
}
```

**Sách 2 (B002):**
```json
{
  "id": "B002",
  "title": "Lập trình Android",
  "author": "Trần Thị B",
  "category": "Công nghệ thông tin",
  "quantity": 3,
  "imageUrl": "https://via.placeholder.com/300x400?text=Android+Book",
  "description": "Hướng dẫn phát triển ứng dụng Android với Java và Kotlin"
}
```

**Sách 3 (B003):**
```json
{
  "id": "B003",
  "title": "Cơ sở dữ liệu",
  "author": "Lê Văn C",
  "category": "Công nghệ thông tin",
  "quantity": 4,
  "imageUrl": "https://via.placeholder.com/300x400?text=Database",
  "description": "Giáo trình cơ sở dữ liệu quan hệ và SQL"
}
```

**Sách 4 (B004):**
```json
{
  "id": "B004",
  "title": "Python cho người mới",
  "author": "Phạm Thị D",
  "category": "Lập trình",
  "quantity": 6,
  "imageUrl": "https://via.placeholder.com/300x400?text=Python",
  "description": "Học Python từ zero đến hero"
}
```

**Sách 5 (B005):**
```json
{
  "id": "B005",
  "title": "Trí tuệ nhân tạo",
  "author": "Hoàng Văn E",
  "category": "AI/ML",
  "quantity": 2,
  "imageUrl": "https://via.placeholder.com/300x400?text=AI",
  "description": "Nhập môn trí tuệ nhân tạo và machine learning"
}
```

## ✅ Kiểm Tra Hoạt Động

### Test Case 1: Đăng nhập Sinh viên
1. Mở app → Click "Đăng ký ngay"
2. Nhập thông tin sinh viên
3. Chọn "Sinh viên"
4. Click "Đăng ký"
5. Đăng nhập lại
6. Kiểm tra 3 tabs: Sách, Phiếu mượn, Tài khoản

### Test Case 2: Mượn Sách
1. Vào tab "Sách"
2. Chọn 1 quyển sách
3. Click "Mượn sách"
4. Xác nhận
5. Vào tab "Phiếu mượn" → Kiểm tra sách vừa mượn

### Test Case 3: Trả Sách
1. Vào tab "Phiếu mượn"
2. Tìm sách có status "Đang mượn"
3. Click "Trả sách"
4. Xác nhận
5. Kiểm tra status chuyển thành "Đã trả"

### Test Case 4: Admin Quản Lý
1. Đăng xuất
2. Đăng ký tài khoản Admin
3. Kiểm tra 4 tabs: Quản lý sách, Quản lý mượn, Thống kê, Tài khoản
4. Xem danh sách tất cả sách
5. Thử xóa 1 sách
6. Xem thống kê

## 🐛 Troubleshooting

### Lỗi: "google-services.json is missing"
**Giải pháp:**
- Kiểm tra file `google-services.json` đã copy đúng chỗ: `app/google-services.json`
- Sync Gradle lại: File → Sync Project with Gradle Files

### Lỗi: "FirebaseAuth failed"
**Giải pháp:**
- Kiểm tra đã enable Email/Password trong Firebase Console
- Kiểm tra internet connection
- Package name trong Firebase phải là: `com.example.do_an`

### Lỗi: "Firestore permission denied"
**Giải pháp:**
- Kiểm tra Firestore Rules đang ở "test mode"
- Rules phải như sau:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2025, 12, 31);
    }
  }
}
```

### Lỗi: Build failed
**Giải pháp:**
- Clean project: Build → Clean Project
- Rebuild: Build → Rebuild Project
- Invalidate caches: File → Invalidate Caches / Restart

### App không hiển thị sách
**Giải pháp:**
- Kiểm tra đã thêm sách vào Firestore chưa
- Collection phải tên là "books"
- Document ID phải có (B001, B002...)
- Kiểm tra internet connection

### Không mượn được sách
**Giải pháp:**
- Kiểm tra quantity > 0
- Kiểm tra đã đăng nhập chưa
- Xem log trong Logcat

## 📱 Chức Năng Đã Có

### Sinh viên ✅
- [x] Đăng ký/Đăng nhập
- [x] Xem danh sách sách
- [x] Mượn sách (quantity tự động giảm)
- [x] Xem phiếu mượn
- [x] Trả sách (quantity tự động tăng)
- [x] Xem thông tin tài khoản
- [x] Đăng xuất

### Admin ✅
- [x] Đăng nhập
- [x] Xem tất cả sách
- [x] Xóa sách
- [x] Xem tất cả phiếu mượn
- [x] Thống kê: tổng sách, tổng mượn, đang mượn
- [x] Đăng xuất

### TODO (Chưa làm) 🔄
- [ ] Thêm/Sửa sách (Admin)
- [ ] Tìm kiếm sách
- [ ] Thông báo hết hạn
- [ ] QR code

## 📞 Liên Hệ Hỗ Trợ

Nếu gặp vấn đề, hãy:
1. Kiểm tra lại FIREBASE_SETUP.md
2. Xem log trong Logcat
3. Kiểm tra internet
4. Clean + Rebuild project

---

**Chúc bạn thành công! 🎉**


# Hướng dẫn cài đặt Firebase cho ứng dụng Quản lý Thư viện

## Bước 1: Tạo Firebase Project

1. Truy cập https://console.firebase.google.com/
2. Click "Add project" (Thêm dự án)
3. Đặt tên project: "LibraryManagement" hoặc tên bạn muốn
4. Làm theo các bước để tạo project

## Bước 2: Thêm Android App vào Firebase

1. Trong Firebase Console, click vào biểu tượng Android
2. Package name: `com.example.do_an`
3. App nickname: Library Management (tùy chọn)
4. Click "Register app"

## Bước 3: Tải file google-services.json

1. Sau khi đăng ký app, Firebase sẽ cho bạn tải file `google-services.json`
2. Copy file này vào thư mục: `app/` (cùng cấp với `build.gradle.kts`)
3. **QUAN TRỌNG**: File này phải nằm ở `D:\doablaptrinhdidong\app\google-services.json`

## Bước 4: Kích hoạt các dịch vụ Firebase

### 4.1. Firebase Authentication
1. Trong Firebase Console, vào "Authentication" → "Get started"
2. Vào tab "Sign-in method"
3. Enable "Email/Password"

### 4.2. Cloud Firestore
1. Vào "Firestore Database" → "Create database"
2. Chọn "Start in test mode" (cho development)
3. Chọn location gần nhất (asia-southeast1)

**Rules cho Firestore (Test mode):**
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

### 4.3. Firebase Storage (Tùy chọn - cho ảnh sách)
1. Vào "Storage" → "Get started"
2. Chọn "Start in test mode"

## Bước 5: Tạo dữ liệu mẫu

### 5.1. Tạo tài khoản Admin
1. Chạy app, đăng ký tài khoản với vai trò "Quản trị viên"
2. Email: admin@gmail.com
3. Password: admin123

### 5.2. Thêm sách mẫu vào Firestore
Vào Firestore Console, tạo collection "books" với các document:

**Sách 1:**
```json
{
  "id": "B001",
  "title": "Lập trình Java cơ bản",
  "author": "Nguyễn Văn A",
  "category": "Công nghệ thông tin",
  "quantity": 5,
  "imageUrl": "https://via.placeholder.com/300x400?text=Java+Book",
  "description": "Sách hướng dẫn học Java từ cơ bản đến nâng cao"
}
```

**Sách 2:**
```json
{
  "id": "B002",
  "title": "Lập trình Android",
  "author": "Trần Thị B",
  "category": "Công nghệ thông tin",
  "quantity": 3,
  "imageUrl": "https://via.placeholder.com/300x400?text=Android+Book",
  "description": "Hướng dẫn phát triển ứng dụng Android"
}
```

**Sách 3:**
```json
{
  "id": "B003",
  "title": "Cơ sở dữ liệu",
  "author": "Lê Văn C",
  "category": "Công nghệ thông tin",
  "quantity": 4,
  "imageUrl": "https://via.placeholder.com/300x400?text=Database+Book",
  "description": "Giáo trình cơ sở dữ liệu"
}
```

## Bước 6: Sync và Build Project

1. Mở Android Studio
2. Click "Sync Project with Gradle Files"
3. Đợi sync hoàn tất
4. Build và chạy app

## Cấu trúc Project

```
📦 do_an
├── 📂 models/           # Book, User, Borrow
├── 📂 activities/       # Login, Register, StudentMain, AdminMain
├── 📂 fragments/        # BookList, MyBorrows, Profile, Admin...
├── 📂 adapters/         # BookAdapter, BorrowAdapter, Admin...
└── 📂 res/
    ├── layout/          # XML layouts
    └── menu/            # Bottom navigation menus
```

## Tính năng đã implement

### Sinh viên:
- ✅ Đăng ký/Đăng nhập
- ✅ Xem danh sách sách
- ✅ Mượn sách
- ✅ Xem phiếu mượn
- ✅ Trả sách
- ✅ Xem thông tin tài khoản

### Admin:
- ✅ Đăng nhập
- ✅ Xem danh sách sách
- ✅ Xóa sách
- ✅ Xem danh sách mượn trả
- ✅ Thống kê (tổng sách, tổng mượn, đang mượn)
- ⏳ Thêm/Sửa sách (TODO)

## Troubleshooting

### Lỗi: "google-services.json not found"
- Kiểm tra file đã copy đúng vị trí: `app/google-services.json`
- Sync lại project

### Lỗi: "Firebase Authentication failed"
- Kiểm tra đã enable Email/Password trong Firebase Console
- Kiểm tra internet connection

### Lỗi: "Firestore permission denied"
- Kiểm tra Firestore Rules đã đúng chưa
- Đảm bảo đang ở test mode

## License & Contact
Đồ án: Ứng dụng Quản lý Thư viện
Sinh viên: [Tên bạn]
Email: [Email của bạn]


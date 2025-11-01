# 🔨 HƯỚNG DẪN BUILD & CHẠY APP

## ⚡ NHANH NHẤT - 30 PHÚT

### 1️⃣ Chuẩn bị môi trường (5 phút)

**Cần có:**
- ✅ Android Studio (latest version)
- ✅ JDK 11 hoặc mới hơn
- ✅ Android Emulator hoặc thiết bị thật
- ✅ Internet connection

**Kiểm tra:**
```bash
java -version  # Phải >= 11
```

---

### 2️⃣ Setup Firebase (15 phút)

#### A. Tạo Firebase Project
1. Vào https://console.firebase.google.com/
2. Click **"Add project"**
3. Tên project: `LibraryManagement` (hoặc tùy ý)
4. Tắt Google Analytics (không cần)
5. Click **"Create project"**

#### B. Thêm Android App
1. Trong Firebase Console, click biểu tượng **Android**
2. Package name: **`com.example.do_an`** ⚠️ QUAN TRỌNG!
3. App nickname: `Library App` (tùy chọn)
4. Click **"Register app"**

#### C. Download google-services.json
1. Download file `google-services.json`
2. Copy vào thư mục: `D:\doablaptrinhdidong\app\`
3. Verify vị trí: File phải nằm ở `app/google-services.json`

#### D. Enable Firebase Services

**Authentication:**
```
Firebase Console → Authentication → Get started
→ Sign-in method → Email/Password → Enable → Save
```

**Firestore Database:**
```
Firebase Console → Firestore Database → Create database
→ Location: asia-southeast1 (Singapore)
→ Start in test mode → Enable
```

**Firestore Rules (Test mode):**
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2025, 12, 31);
    }
  }
}
```

---

### 3️⃣ Build Project (5 phút)

#### A. Mở Project
```
1. Mở Android Studio
2. File → Open → Chọn thư mục "doablaptrinhdidong"
3. Đợi Gradle sync tự động
```

#### B. Verify google-services.json
```
Check file tồn tại:
app/google-services.json ✅
```

#### C. Sync Gradle
```
File → Sync Project with Gradle Files
Đợi sync xong (1-2 phút)
```

#### D. Clean & Rebuild
```
Build → Clean Project
Build → Rebuild Project
Đợi build xong (2-3 phút)
```

---

### 4️⃣ Thêm dữ liệu mẫu (5 phút)

#### Tạo Admin Account trước
```
1. Run app → Đăng ký
2. Email: admin@gmail.com
3. Password: admin123456
4. Vai trò: Quản trị viên
5. Đăng ký → Đăng nhập admin
```

#### Thêm sách qua Firebase Console
```
Firebase Console → Firestore → Start collection

Collection ID: books

Document 1:
- Document ID: B001
- Fields:
  id: "B001"
  title: "Lập trình Java cơ bản"
  author: "Nguyễn Văn A"
  category: "Công nghệ thông tin"
  quantity: 5
  imageUrl: "https://via.placeholder.com/300x400?text=Java"
  description: "Sách học Java từ cơ bản đến nâng cao"

Document 2:
- Document ID: B002
- Fields:
  id: "B002"
  title: "Lập trình Android"
  author: "Trần Thị B"
  category: "Công nghệ thông tin"
  quantity: 3
  imageUrl: "https://via.placeholder.com/300x400?text=Android"
  description: "Hướng dẫn phát triển app Android"

Document 3:
- Document ID: B003
- Fields:
  id: "B003"
  title: "Cơ sở dữ liệu"
  author: "Lê Văn C"
  category: "Công nghệ thông tin"
  quantity: 4
  imageUrl: "https://via.placeholder.com/300x400?text=Database"
  description: "Giáo trình CSDL quan hệ"
```

**Hoặc thêm nhanh qua Admin:**
```
1. Đăng nhập admin
2. Tab "Sách" → Click nút "+"
3. Nhập thông tin sách
4. Click "LƯU"
5. Lặp lại cho các sách khác
```

---

### 5️⃣ Run & Test (5 phút)

#### A. Run App
```
1. Click nút Run (▶️) hoặc Shift + F10
2. Chọn emulator hoặc device
3. Đợi app cài đặt và mở
```

#### B. Test Flow đầy đủ

**Test 1: Đăng ký & Duyệt**
```
1. Đăng ký tài khoản sinh viên
   Email: student@gmail.com
   Password: student123
   ✅ Thông báo: "Chờ admin duyệt"

2. Thử đăng nhập
   ❌ Phải bị chặn: "Tài khoản đang chờ admin duyệt"

3. Đăng nhập admin → Tab "Duyệt user"
   Click "Duyệt" cho student

4. Đăng nhập student lại
   ✅ Vào được app
```

**Test 2: Mượn sách**
```
1. Student → Tab "Sách"
2. Chọn sách → Click "Mượn sách"
   ✅ Thông báo: "Đã gửi yêu cầu, chờ admin duyệt"

3. Tab "Phiếu mượn"
   ✅ Status: "Chờ duyệt" (màu cam)
   ❌ KHÔNG có nút trả sách

4. Admin → Tab "Mượn trả"
   Click "Duyệt"
   ✅ Quantity giảm 1

5. Student → Tab "Phiếu mượn"
   ✅ Status: "Đang mượn" (màu xanh)
   ✅ CÓ nút trả sách
```

**Test 3: Trả sách**
```
1. Student → Tab "Phiếu mượn"
2. Click "Trả sách" → Xác nhận
   ✅ Status: "Đã trả"
   ✅ Quantity tăng 1
```

**Test 4: CRUD Sách (Admin)**
```
1. Admin → Tab "Sách"
2. Click "+" → Thêm sách mới
   ✅ Sách xuất hiện

3. Click icon Edit → Sửa thông tin
   ✅ Thông tin cập nhật

4. Click icon Delete → Xóa sách
   ✅ Sách biến mất
```

---

## 🔧 TROUBLESHOOTING

### Lỗi 1: Cannot resolve symbol 'activity_edit_book'
```bash
Fix:
1. File → Invalidate Caches / Restart
2. Build → Clean Project
3. Build → Rebuild Project
```

### Lỗi 2: google-services.json not found
```bash
Fix:
1. Kiểm tra file có đúng vị trí: app/google-services.json
2. Copy lại file từ Firebase Console
3. Sync Gradle
```

### Lỗi 3: Firebase Auth failed
```bash
Fix:
1. Firebase Console → Authentication
2. Verify Email/Password đã enable
3. Check internet connection
4. Verify package name = com.example.do_an
```

### Lỗi 4: Firestore permission denied
```bash
Fix:
1. Firebase Console → Firestore → Rules
2. Verify test mode rules:
   allow read, write: if request.time < timestamp.date(2025, 12, 31);
3. Publish rules
```

### Lỗi 5: Không thấy sách
```bash
Fix:
1. Kiểm tra đã thêm sách vào Firestore chưa
2. Collection phải tên "books"
3. Document phải có field "id"
4. Check internet connection
```

### Lỗi 6: Build failed
```bash
Fix:
1. Build → Clean Project
2. Build → Rebuild Project
3. File → Invalidate Caches → Restart
4. Xóa folder .gradle và build, sync lại
```

---

## 📊 BUILD CHECKLIST

### Pre-build:
- [ ] Android Studio installed
- [ ] JDK 11+ installed
- [ ] Firebase project created
- [ ] google-services.json downloaded
- [ ] google-services.json copied to app/

### Build:
- [ ] Project opened in Android Studio
- [ ] Gradle synced successfully
- [ ] No compile errors
- [ ] Clean + Rebuild successful

### Firebase:
- [ ] Authentication enabled
- [ ] Firestore database created
- [ ] Firestore rules set to test mode
- [ ] Admin account created

### Data:
- [ ] Books collection created
- [ ] At least 3 books added
- [ ] Test accounts created

### Test:
- [ ] App runs on emulator/device
- [ ] Login works
- [ ] Register works (with approval)
- [ ] Borrow works (with approval)
- [ ] Return works
- [ ] CRUD books works (admin)

---

## ⚙️ BUILD CONFIGURATIONS

### Debug Build:
```gradle
buildTypes {
    debug {
        debuggable true
        minifyEnabled false
    }
}
```

### Release Build (Production):
```gradle
buildTypes {
    release {
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        signingConfig signingConfigs.release
    }
}
```

---

## 📱 DEVICES SUPPORTED

- **Minimum SDK:** API 24 (Android 7.0 Nougat)
- **Target SDK:** API 36
- **Tested on:**
  - Emulator: Pixel 5 API 34
  - Emulator: Pixel 7 API 36
  - Real device: [Your device]

---

## 🎉 BUILD THÀNH CÔNG!

Nếu đã làm theo tất cả bước trên:
✅ App chạy được  
✅ Đăng ký/Đăng nhập hoạt động  
✅ Hệ thống duyệt hoạt động  
✅ Mượn/Trả sách hoạt động  
✅ CRUD sách hoạt động  

**Chúc mừng! App của bạn đã sẵn sàng! 🚀**

---

*Last updated: November 1, 2025*


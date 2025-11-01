# ✅ CHECKLIST CÀI ĐẶT

## Danh sách kiểm tra trước khi chạy app

### 1. Môi trường phát triển
- [ ] Đã cài đặt Android Studio
- [ ] Đã cài đặt JDK 11+
- [ ] Đã có emulator hoặc thiết bị Android

### 2. Firebase Setup
- [ ] Đã tạo Firebase project
- [ ] Đã thêm Android app với package name: `com.example.do_an`
- [ ] Đã download file `google-services.json`
- [ ] File `google-services.json` đã copy vào thư mục `app/`
- [ ] Đã enable Firebase Authentication (Email/Password)
- [ ] Đã tạo Cloud Firestore database (test mode)
- [ ] Firestore location: asia-southeast1 (Singapore)

### 3. Firestore Rules
Kiểm tra rules trong Firestore Console:
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

### 4. Collections trong Firestore
- [ ] Collection "books" đã tạo
- [ ] Đã thêm ít nhất 1 document vào "books"
- [ ] Document có đủ fields: id, title, author, category, quantity, imageUrl, description

### 5. Project Files
- [ ] File `app/google-services.json` tồn tại
- [ ] File `app/build.gradle.kts` có plugin google-services
- [ ] File `build.gradle.kts` (root) có plugin google-services
- [ ] File `gradle/libs.versions.toml` có firebase dependencies

### 6. Gradle Sync
- [ ] Đã sync Gradle thành công
- [ ] Không có error trong Build Output
- [ ] Dependencies đã download xong

### 7. Permissions trong AndroidManifest.xml
- [ ] `<uses-permission android:name="android.permission.INTERNET"/>`
- [ ] `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`

### 8. Activities đã đăng ký
- [ ] LoginActivity
- [ ] RegisterActivity
- [ ] StudentMainActivity
- [ ] AdminMainActivity

### 9. Build thành công
- [ ] Build → Clean Project (không lỗi)
- [ ] Build → Rebuild Project (không lỗi)
- [ ] Build → Make Project (không lỗi)

### 10. Test run
- [ ] App chạy được trên emulator/device
- [ ] Màn hình login hiển thị
- [ ] Có thể đăng ký tài khoản mới
- [ ] Có thể đăng nhập

## Cấu trúc file cần có

```
doablaptrinhdidong/
├── app/
│   ├── build.gradle.kts          ✅ Có plugin google-services
│   ├── google-services.json      ✅ File từ Firebase (QUAN TRỌNG!)
│   ├── proguard-rules.pro
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml    ✅ Đã đăng ký activities
│           ├── java/com/example/do_an/
│           │   ├── activities/        ✅ 4 activities
│           │   ├── adapters/          ✅ 4 adapters
│           │   ├── fragments/         ✅ 6 fragments
│           │   ├── models/            ✅ 3 models
│           │   └── MainActivity.java  (legacy)
│           └── res/
│               ├── layout/            ✅ Nhiều XML files
│               ├── menu/              ✅ 2 bottom nav menus
│               └── values/
├── gradle/
│   └── libs.versions.toml        ✅ Firebase versions
├── build.gradle.kts              ✅ Root build file
├── settings.gradle.kts
├── README.md                     ✅ Tài liệu chính
├── FIREBASE_SETUP.md             ✅ Hướng dẫn Firebase
├── QUICKSTART.md                 ✅ Hướng dẫn nhanh
└── CHECKLIST.md                  ✅ File này
```

## Version Requirements

```
Min SDK: 24 (Android 7.0)
Target SDK: 36
Compile SDK: 36
Java: 11
Gradle: 8.13.0
Firebase BOM: 33.5.1
```

## Kiểm tra build.gradle.kts

### app/build.gradle.kts phải có:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)  // ← QUAN TRỌNG
}

android {
    namespace = "com.example.do_an"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "com.example.do_an"  // ← Package name
        minSdk = 24
        targetSdk = 36
    }
    
    buildFeatures {
        viewBinding = true  // ← Enable ViewBinding
    }
}

dependencies {
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    
    // Glide
    implementation(libs.glide)
    
    // AndroidX
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    // ... others
}
```

## Các bước sau khi checklist hoàn thành

1. **Sync Gradle**
   - File → Sync Project with Gradle Files
   - Đợi hoàn tất

2. **Clean Build**
   - Build → Clean Project
   - Build → Rebuild Project

3. **Run App**
   - Click Run (▶️)
   - Chọn device/emulator
   - Đợi cài đặt

4. **Test Flow**
   - Đăng ký tài khoản sinh viên
   - Đăng nhập
   - Xem sách (nếu đã thêm vào Firestore)
   - Thử mượn sách

## ⚠️ Lỗi thường gặp

### 1. google-services.json not found
```
Giải pháp: Copy file vào đúng thư mục app/
Vị trí: D:\doablaptrinhdidong\app\google-services.json
```

### 2. Plugin [id: 'com.google.gms.google-services'] was not found
```
Giải pháp: Kiểm tra libs.versions.toml có:
[versions]
googleServices = "4.4.2"

[plugins]
google-services = { id = "com.google.gms.google-services", version.ref = "googleServices" }
```

### 3. FirebaseApp initialization unsuccessful
```
Giải pháp:
- Kiểm tra package name trong Firebase Console = com.example.do_an
- Download lại google-services.json
- Sync Gradle lại
```

### 4. Firestore permission denied
```
Giải pháp: Vào Firestore Rules, set test mode:
allow read, write: if request.time < timestamp.date(2025, 12, 31);
```

## 📞 Cần trợ giúp?

1. Kiểm tra lại từng bước trong checklist này
2. Đọc FIREBASE_SETUP.md chi tiết hơn
3. Xem QUICKSTART.md để setup nhanh
4. Kiểm tra log trong Logcat

---

**Nếu tất cả checklist đã ✅, bạn đã sẵn sàng chạy app! 🚀**


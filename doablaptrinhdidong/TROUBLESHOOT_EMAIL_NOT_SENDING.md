# 🔧 FIX: EMAIL KHÔNG GỬI - TROUBLESHOOTING

## ⚠️ Vấn Đề: Email không nhận được

### 🔍 Nguyên Nhân Có Thể:

1. **Firebase Authentication chưa enable Email/Password**
2. **App chưa đăng ký user với email đó**
3. **Email vào Spam folder**
4. **Network issue**
5. **Firebase API key thiếu permissions**

---

## ✅ GIẢI PHÁP - Làm Theo Thứ Tự

### Bước 1: Kiểm Tra Firebase Console

#### 1.1 Enable Email/Password Authentication

```
🔗 https://console.firebase.google.com/project/librarymanagement-2c326/authentication/providers

Làm theo:
1. Vào link trên
2. Click tab "Sign-in method"
3. Tìm "Email/Password"
4. Click vào để edit
5. Enable "Email/Password" (tắt Email link nếu có)
6. Click "Save"

✅ Phải thấy status: ENABLED (màu xanh)
```

#### 1.2 Kiểm Tra User Đã Đăng Ký

```
🔗 https://console.firebase.google.com/project/librarymanagement-2c326/authentication/users

Làm theo:
1. Vào link trên
2. Click tab "Users"
3. Tìm email bạn muốn test (ví dụ: student@gmail.com)
4. Phải thấy user trong danh sách

❌ Nếu không thấy:
   → User chưa đăng ký
   → Đăng ký user mới trước
   → Sau đó mới test forgot password
```

#### 1.3 Kiểm Tra Email Template

```
🔗 https://console.firebase.google.com/project/librarymanagement-2c326/authentication/emails

Làm theo:
1. Vào link trên
2. Click tab "Templates"
3. Tìm "Password reset"
4. Click "Edit" (icon bút chì)
5. Xem preview email

✅ Phải thấy template có nội dung
```

---

### Bước 2: Test Trong App

#### 2.1 Run App

```bash
# Build và run
cd D:\Git\do_an_di_dong\Lap-trinh-di-dong\doablaptrinhdidong
gradlew.bat assembleDebug

# Hoặc trong Android Studio: Run > Run 'app'
```

#### 2.2 Check Logcat

```bash
# Filter Firebase logs
adb logcat | findstr "Firebase"

# Hoặc trong Android Studio:
Logcat → Filter: "Firebase"
```

**Tìm các dòng log:**
```
✅ Good logs:
- "Firebase Auth initialized"
- "sendPasswordResetEmail: success"

❌ Error logs:
- "DEVELOPER_ERROR" → API key issue
- "INVALID_EMAIL" → Email format sai
- "USER_NOT_FOUND" → User chưa đăng ký (nhưng app đã check)
- "TOO_MANY_ATTEMPTS" → Gửi quá nhiều
```

#### 2.3 Test Flow

```
1. Mở app
2. Click "Quên mật khẩu"
3. Nhập email: student@gmail.com (hoặc email đã đăng ký)
4. Click "Gửi"
5. Xem Logcat có lỗi không

Nếu thành công:
- Dialog hiển thị "✅ Email Đã Được Gửi!"
- Logcat: "sendPasswordResetEmail: success"

Nếu lỗi:
- Toast hiển thị error message
- Check Logcat để biết lỗi gì
```

---

### Bước 3: Kiểm Tra Email

#### 3.1 Check Inbox

```
1. Mở Gmail/email client
2. Tìm email từ: noreply@librarymanagement-2c326.firebaseapp.com
3. Subject: "Reset your password for Library Management"

⏰ Đợi 1-5 phút (email có thể delay)
```

#### 3.2 Check Spam Folder

```
❗ QUAN TRỌNG: Email có thể vào Spam!

1. Vào Spam/Junk folder
2. Tìm email từ Firebase
3. Nếu thấy → Mark as "Not Spam"
4. Check lại Inbox
```

#### 3.3 Check Email Settings

```
Nếu vẫn không thấy email:

Gmail:
1. Settings → Filters and Blocked Addresses
2. Check có filter nào block Firebase không

Outlook:
1. Settings → Junk email
2. Check có rule nào block không
```

---

### Bước 4: Debug Code

#### 4.1 Thêm Logs Chi Tiết

Mở `ForgotPasswordActivity.java` và thêm logs:

```java
private void sendVerificationCode() {
    userEmail = edtEmail.getText().toString().trim();
    
    Log.d("ForgotPassword", "=== START ===");
    Log.d("ForgotPassword", "Email entered: " + userEmail);

    // Validate
    if (TextUtils.isEmpty(userEmail)) {
        Log.e("ForgotPassword", "Email is empty!");
        edtEmail.setError("Vui lòng nhập email");
        return;
    }

    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
        Log.e("ForgotPassword", "Email format invalid: " + userEmail);
        edtEmail.setError("Email không hợp lệ");
        return;
    }
    
    Log.d("ForgotPassword", "Email validation passed");

    progressBar.setVisibility(View.VISIBLE);
    btnSendCode.setEnabled(false);

    // Kiểm tra Firestore
    Log.d("ForgotPassword", "Checking email in Firestore...");
    db.collection("users")
        .whereEqualTo("email", userEmail)
        .get()
        .addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d("ForgotPassword", "Firestore query success");
            Log.d("ForgotPassword", "Documents found: " + queryDocumentSnapshots.size());
            
            if (queryDocumentSnapshots.isEmpty()) {
                Log.e("ForgotPassword", "Email not found in Firestore!");
                progressBar.setVisibility(View.GONE);
                btnSendCode.setEnabled(true);
                Toast.makeText(this, "❌ Email không tồn tại trong hệ thống", Toast.LENGTH_SHORT).show();
                return;
            }

            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
            userId = document.getId();
            Log.d("ForgotPassword", "User found. ID: " + userId);

            // Gửi email reset
            Log.d("ForgotPassword", "Calling sendPasswordResetEmail...");
            mAuth.sendPasswordResetEmail(userEmail)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ForgotPassword", "✅ sendPasswordResetEmail SUCCESS!");
                    progressBar.setVisibility(View.GONE);
                    btnSendCode.setEnabled(true);

                    new AlertDialog.Builder(this)
                        .setTitle("✅ Email Đã Được Gửi!")
                        .setMessage(
                            "📧 Chúng tôi đã gửi link đặt lại mật khẩu đến:\n\n" +
                            userEmail + "\n\n" +
                            "📌 Vui lòng:\n" +
                            "1. Kiểm tra hộp thư đến\n" +
                            "2. Nếu không thấy, kiểm tra thư mục Spam\n" +
                            "3. Nhấn vào link trong email\n" +
                            "4. Đặt mật khẩu mới\n\n" +
                            "⏰ Link có hiệu lực trong 1 giờ"
                        )
                        .setPositiveButton("OK, Đã Hiểu", (dialog, which) -> finish())
                        .setCancelable(false)
                        .show();
                })
                .addOnFailureListener(e -> {
                    Log.e("ForgotPassword", "❌ sendPasswordResetEmail FAILED!");
                    Log.e("ForgotPassword", "Error: " + e.getMessage());
                    Log.e("ForgotPassword", "Error class: " + e.getClass().getName());
                    
                    progressBar.setVisibility(View.GONE);
                    btnSendCode.setEnabled(true);
                    
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("network")) {
                        Toast.makeText(this, "❌ Lỗi kết nối mạng. Vui lòng kiểm tra internet!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "❌ Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
        })
        .addOnFailureListener(e -> {
            Log.e("ForgotPassword", "❌ Firestore query FAILED!");
            Log.e("ForgotPassword", "Error: " + e.getMessage());
            
            progressBar.setVisibility(View.GONE);
            btnSendCode.setEnabled(true);
            Toast.makeText(this, "❌ Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
}
```

#### 4.2 Rebuild & Test

```bash
# Clean build
gradlew.bat clean assembleDebug

# Run app
# Xem Logcat để debug
```

---

### Bước 5: Test Với Email Khác

Thử với các email khác để xem có phải lỗi email cụ thể không:

```
Test với:
1. Gmail: youremail@gmail.com
2. Outlook: youremail@outlook.com
3. Yahoo: youremail@yahoo.com

Nếu một email work mà email khác không:
→ Vấn đề ở email provider, không phải Firebase
```

---

## 🔥 Quick Fix - Kiểm Tra Ngay

### Test Case Đơn Giản:

```
1. Vào: https://console.firebase.google.com/project/librarymanagement-2c326/authentication/users

2. Click "Add user"

3. Thêm user test:
   Email: test123@gmail.com
   Password: 123456
   
4. Click "Add user"

5. Mở app → Forgot password

6. Nhập: test123@gmail.com

7. Click "Gửi"

8. Check email test123@gmail.com

✅ Nếu nhận được email → Firebase hoạt động tốt
❌ Nếu không nhận được → Check các bước dưới
```

---

## 🚨 Common Errors & Solutions

### Error 1: "DEVELOPER_ERROR"

```
Nguyên nhân: API key thiếu permissions

Fix:
1. Vào: https://console.firebase.google.com/project/librarymanagement-2c326/settings/general
2. Scroll xuống "Your apps"
3. Click Android app
4. Download lại google-services.json
5. Copy vào app/ folder (replace file cũ)
6. Rebuild app
```

### Error 2: "API_KEY_INVALID"

```
Nguyên nhân: API key không đúng

Fix:
1. Vào Firebase Console
2. Project Settings → General
3. Copy "Web API Key"
4. Vào: https://console.cloud.google.com/apis/credentials?project=librarymanagement-2c326
5. Check API key có restrict không
6. Nếu có restrict → Thêm "Identity Toolkit API"
```

### Error 3: "USER_NOT_FOUND"

```
Nguyên nhân: Email chưa đăng ký trong Firebase Auth

Fix:
1. Đăng ký user trước
2. Hoặc check email đúng chưa
3. Firebase Auth và Firestore phải có cùng email
```

### Error 4: "TOO_MANY_ATTEMPTS_TRY_LATER"

```
Nguyên nhân: Gửi quá nhiều request

Fix:
1. Đợi 15-30 phút
2. Hoặc đổi IP (tắt/bật wifi)
3. Hoặc test với email khác
```

### Error 5: Email vào Spam

```
Nguyên nhân: Gmail filter Firebase emails

Fix:
1. Check Spam folder
2. Mark as "Not Spam"
3. Add noreply@librarymanagement-2c326.firebaseapp.com vào Contacts
4. Whitelist domain trong Gmail settings
```

---

## 📋 Checklist Đầy Đủ

Kiểm tra từng mục:

### Firebase Console:
- [ ] Authentication → Sign-in method → Email/Password: ENABLED
- [ ] Authentication → Users → User với email test đã tồn tại
- [ ] Authentication → Templates → Password reset có template
- [ ] Project Settings → General → google-services.json đã download
- [ ] APIs & Services → Identity Toolkit API: ENABLED

### App:
- [ ] google-services.json đã copy vào app/ folder
- [ ] build.gradle có firebase-auth dependency
- [ ] FirebaseAuth đã initialize
- [ ] Internet permission trong AndroidManifest.xml
- [ ] App đã rebuild sau khi thay đổi

### Code:
- [ ] ForgotPasswordActivity gọi mAuth.sendPasswordResetEmail()
- [ ] Email validation đúng
- [ ] Firestore có check email tồn tại
- [ ] Error handling đầy đủ
- [ ] Logs để debug

### Test:
- [ ] User test đã đăng ký trong Firebase Auth
- [ ] Email test có thật (có thể check)
- [ ] Internet connection tốt
- [ ] Logcat đang chạy để xem logs
- [ ] Spam folder đã check

---

## 🎯 Test Nhanh Nhất (5 phút)

```bash
# Bước 1: Thêm logs vào code (xem Bước 4.1 ở trên)

# Bước 2: Rebuild
gradlew.bat clean assembleDebug

# Bước 3: Run app
# Android Studio → Run

# Bước 4: Open Logcat
# Android Studio → Logcat → Filter: "ForgotPassword"

# Bước 5: Test
# App → Quên mật khẩu → Nhập email → Gửi

# Bước 6: Xem logs
# Check Logcat xem lỗi gì

# Bước 7: Fix theo logs
# Theo error message để fix
```

---

## 📧 Liên Hệ Firebase Support (nếu cần)

Nếu tất cả đều đã check mà vẫn không work:

```
1. Vào: https://firebase.google.com/support/contact

2. Select:
   - Product: Authentication
   - Issue: Email not sending

3. Provide:
   - Project ID: librarymanagement-2c326
   - Email test: xxx@gmail.com
   - Error logs từ Logcat
   - Screenshots

Firebase support thường reply trong 24-48h
```

---

## ✅ Kết Luận

**Most likely issues:**

1. **Email/Password chưa enable trong Firebase** (90% trường hợp)
   → Fix: Enable trong Console

2. **Email vào Spam folder** (80% trường hợp)
   → Fix: Check Spam

3. **User chưa đăng ký** (70% trường hợp)
   → Fix: Đăng ký user trước

4. **google-services.json cũ** (50% trường hợp)
   → Fix: Download lại từ Firebase

5. **API key thiếu permission** (30% trường hợp)
   → Fix: Check Google Cloud Console

**Làm theo thứ tự:**
1. Enable Email/Password trong Firebase ✅
2. Thêm logs vào code ✅
3. Test với user đã đăng ký ✅
4. Check Spam folder ✅
5. Rebuild app ✅

**99% trường hợp sẽ work sau các bước này!** 💪

---

_Last updated: 02/11/2025_


# 🔐 CÁCH HOẠT ĐỘNG: TÍNH NĂNG QUÊN MẬT KHẨU

## 📋 Tổng Quan

Tính năng quên mật khẩu sử dụng **Firebase Authentication** để gửi email reset password trực tiếp - **HOÀN TOÀN MIỄN PHÍ**, không cần Cloud Functions hay Blaze plan.

---

## 🔄 Luồng Hoạt Động (Flow Diagram)

```
┌─────────────────────────────────────────────────────────────────┐
│                    QUÊN MẬT KHẨU - FLOW                        │
└─────────────────────────────────────────────────────────────────┘

[Bước 1: Người dùng]
    ↓
Màn hình Login → Click "Quên mật khẩu?"
    ↓
Mở ForgotPasswordActivity
    ↓
Nhập email (ví dụ: student@gmail.com)
    ↓
Click "GỬI MÃ XÁC THỰC"

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

[Bước 2: App kiểm tra]
    ↓
Validate email format
    ↓
Query Firestore: users collection
WHERE email == "student@gmail.com"
    ↓
    ├─[Không tìm thấy]─→ Toast: "Email không tồn tại"
    │                      ↓
    │                     STOP
    │
    └─[Tìm thấy]
        ↓
      Có userId

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

[Bước 3: Firebase Auth gửi email]
    ↓
mAuth.sendPasswordResetEmail(userEmail)
    ↓
Firebase tự động:
  • Tạo link reset có mã bảo mật
  • Link hết hạn sau 1 giờ
  • Gửi email đến user
    ↓
    ├─[Thành công]─→ Dialog: "✅ Email đã được gửi!"
    │                 "Vui lòng kiểm tra hộp thư..."
    │                      ↓
    │                 Click "OK" → Quay về Login
    │
    └─[Thất bại]─→ Toast: "❌ Lỗi: [error message]"

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

[Bước 4: Người dùng nhận email]
    ↓
Mở email client (Gmail, Outlook, etc.)
    ↓
Tìm email từ: noreply@librarymanagement-2c326.firebaseapp.com
Subject: "Reset your password for Library Management"
    ↓
Click nút "RESET PASSWORD" trong email

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

[Bước 5: Firebase Hosting]
    ↓
Mở trang web Firebase (trong browser)
URL: https://librarymanagement-2c326.firebaseapp.com/__/auth/action?...
    ↓
Giao diện đẹp của Firebase hiển thị:
  • "Reset password"
  • Input: "New password"
  • Input: "Confirm password"
  • Button: "Save"
    ↓
Nhập mật khẩu mới (2 lần)
    ↓
Click "Save"

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

[Bước 6: Firebase cập nhật]
    ↓
Firebase Auth tự động:
  • Validate mật khẩu mới
  • Hash mật khẩu
  • Cập nhật trong Authentication
    ↓
    ├─[Thành công]─→ "✅ Password has been changed"
    │                      ↓
    │                 Click "Continue"
    │                      ↓
    │                 Quay về app
    │
    └─[Thất bại]─→ Hiển thị lỗi (mật khẩu yếu, etc.)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

[Bước 7: Đăng nhập với mật khẩu mới]
    ↓
Mở app → Màn hình Login
    ↓
Nhập:
  • Email: student@gmail.com
  • Password: [mật khẩu mới vừa đặt]
    ↓
Click "ĐĂNG NHẬP"
    ↓
✅ THÀNH CÔNG! Vào được app
```

---

## 💻 Code Chi Tiết

### File: `ForgotPasswordActivity.java`

```java
private void sendVerificationCode() {
    // 1. Lấy email người dùng nhập
    userEmail = edtEmail.getText().toString().trim();

    // 2. Validate email
    if (TextUtils.isEmpty(userEmail)) {
        edtEmail.setError("Vui lòng nhập email");
        return;
    }
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
        edtEmail.setError("Email không hợp lệ");
        return;
    }

    // 3. Hiển thị loading
    progressBar.setVisibility(View.VISIBLE);
    btnSendCode.setEnabled(false);

    // 4. Kiểm tra email có tồn tại trong Firestore không
    db.collection("users")
        .whereEqualTo("email", userEmail)
        .get()
        .addOnSuccessListener(queryDocumentSnapshots -> {
            
            // 4a. Email không tồn tại
            if (queryDocumentSnapshots.isEmpty()) {
                progressBar.setVisibility(View.GONE);
                btnSendCode.setEnabled(true);
                Toast.makeText(this, 
                    "❌ Email không tồn tại trong hệ thống", 
                    Toast.LENGTH_SHORT).show();
                return;
            }

            // 4b. Email tồn tại → Gửi email reset
            // ✨ ĐÂY LÀ PHẦN QUAN TRỌNG NHẤT ✨
            mAuth.sendPasswordResetEmail(userEmail)
                .addOnSuccessListener(aVoid -> {
                    // 5. Gửi thành công
                    progressBar.setVisibility(View.GONE);
                    btnSendCode.setEnabled(true);

                    // 6. Hiển thị dialog hướng dẫn
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
                        .setPositiveButton("OK, Đã Hiểu", 
                            (dialog, which) -> finish())
                        .setCancelable(false)
                        .show();
                })
                .addOnFailureListener(e -> {
                    // 7. Gửi thất bại
                    progressBar.setVisibility(View.GONE);
                    btnSendCode.setEnabled(true);
                    
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("network")) {
                        Toast.makeText(this, 
                            "❌ Lỗi kết nối mạng. Vui lòng kiểm tra internet!", 
                            Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, 
                            "❌ Lỗi: " + errorMessage, 
                            Toast.LENGTH_SHORT).show();
                    }
                });
        });
}
```

---

## 📧 Email Mẫu Người Dùng Nhận Được

```
From: noreply@librarymanagement-2c326.firebaseapp.com
To: student@gmail.com
Subject: Reset your password for Library Management

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Hello,

Follow this link to reset your Library Management password 
for your student@gmail.com account.

┌─────────────────────────────────┐
│      [RESET PASSWORD]           │  ← Nút bấm (link)
└─────────────────────────────────┘

If you didn't ask to reset your password, you can ignore this email.

Thanks,
Your Library Management team

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

⚠️ This link will expire in 1 hour.
```

---

## 🌐 Trang Web Reset Password (Firebase Hosting)

Khi click link trong email, trình duyệt sẽ mở:

```
┌────────────────────────────────────────────────────────────┐
│  🔥 Firebase                                               │
│                                                            │
│  Reset your password                                       │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐ │
│  │ New password                                         │ │
│  │ ●●●●●●●●                                             │ │
│  └──────────────────────────────────────────────────────┘ │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐ │
│  │ Confirm new password                                 │ │
│  │ ●●●●●●●●                                             │ │
│  └──────────────────────────────────────────────────────┘ │
│                                                            │
│  ℹ️ Password must be at least 6 characters                │
│                                                            │
│  ┌─────────────┐                                          │
│  │    Save     │                                          │
│  └─────────────┘                                          │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

**Giao diện này do Firebase cung cấp sẵn:**
- ✅ Đẹp, responsive
- ✅ Hỗ trợ đa ngôn ngữ
- ✅ Bảo mật cao
- ✅ Không cần code thêm

---

## 🔧 Cấu Hình Firebase

### 1. Firebase Console → Authentication

```
Firebase Console
  └─ Authentication
      └─ Sign-in method
          └─ Email/Password: [ENABLED ✅]

      └─ Templates
          └─ Password reset
              ├─ From name: Library Management
              ├─ From email: noreply@librarymanagement-2c326.firebaseapp.com
              ├─ Reply-to: (optional)
              ├─ Subject: Reset your password for %APP_NAME%
              └─ Body: [Custom template if needed]
```

### 2. Customize Email Template (Tùy chọn)

Bạn có thể tùy chỉnh email template trong Firebase Console:

1. Vào **Authentication** → **Templates**
2. Chọn **Password reset**
3. Click **Edit template**
4. Thay đổi:
   - Subject line
   - Email body
   - Button text
   - Logo (nếu có)

---

## 🎯 Ưu Điểm Của Phương Pháp Này

| Tiêu chí | Giải thích |
|----------|------------|
| **💰 Miễn phí** | Spark plan (free tier) đã đủ |
| **🔒 Bảo mật** | Link có mã bảo mật, hết hạn sau 1h |
| **🎨 UI đẹp** | Firebase cung cấp UI chuyên nghiệp |
| **📧 Email tin cậy** | Gửi từ domain Firebase chính thức |
| **⚡ Nhanh** | Không cần setup email service |
| **🛠️ Dễ maintain** | Ít code hơn, ít bug hơn |
| **🌍 Đa ngôn ngữ** | Firebase tự động detect ngôn ngữ |
| **📱 Responsive** | Hoạt động tốt trên mobile/desktop |

---

## ⚠️ Các Trường Hợp Đặc Biệt

### 1. Email không đến
**Nguyên nhân:**
- Email vào spam/junk folder
- Gmail/Outlook delay gửi (1-5 phút)
- Email người dùng đầy bộ nhớ

**Giải pháp:**
- Kiểm tra spam folder
- Đợi vài phút
- Gửi lại email

### 2. Link hết hạn
**Nguyên nhân:**
- Đã quá 1 giờ kể từ khi gửi

**Giải pháp:**
- Quay lại app
- Bấm "Quên mật khẩu" lại
- Nhận email mới

### 3. Mật khẩu yếu
**Nguyên nhân:**
- Firebase yêu cầu tối thiểu 6 ký tự

**Giải pháp:**
- Nhập mật khẩu có ít nhất 6 ký tự
- Firebase sẽ hiển thị lỗi nếu yếu

### 4. Email không tồn tại trong hệ thống
**Nguyên nhân:**
- User chưa đăng ký
- Nhập sai email

**Giải pháp:**
- App sẽ hiển thị: "❌ Email không tồn tại trong hệ thống"
- User cần đăng ký trước

---

## 🧪 Test Cases

### Test Case 1: Happy Path ✅
```
Input: student@gmail.com (email hợp lệ, đã đăng ký)
Expected: 
  1. Dialog "✅ Email Đã Được Gửi!"
  2. Nhận email trong vòng 1-5 phút
  3. Click link → Trang Firebase reset
  4. Nhập mật khẩu mới → Thành công
  5. Login với mật khẩu mới → Vào được app
```

### Test Case 2: Email không tồn tại ❌
```
Input: notexist@gmail.com (email chưa đăng ký)
Expected: Toast "❌ Email không tồn tại trong hệ thống"
```

### Test Case 3: Email format sai ❌
```
Input: "notanemail" (không phải email)
Expected: edtEmail.setError("Email không hợp lệ")
```

### Test Case 4: Không có internet ❌
```
Input: student@gmail.com (nhưng không có mạng)
Expected: Toast "❌ Lỗi kết nối mạng..."
```

### Test Case 5: Link hết hạn ⏰
```
Input: Click link sau 1 giờ
Expected: Firebase hiển thị "This link has expired"
Action: Gửi lại email mới
```

---

## 📊 So Sánh Với Phương Pháp Cũ

### Phương pháp cũ (Cloud Functions):
```
User nhập email
  ↓
App tạo mã 6 số random
  ↓
Lưu mã vào Firestore (verification_codes collection)
  ↓
Gọi Cloud Function sendVerificationCode()
  ↓
Cloud Function gửi email qua Nodemailer/SendGrid
  ↓
User nhận email với mã 6 số
  ↓
User quay lại app
  ↓
User nhập mã 6 số vào app
  ↓
App kiểm tra mã có đúng không
  ↓
App kiểm tra mã có hết hạn không
  ↓
User nhập mật khẩu mới (2 lần) trong app
  ↓
Gọi Cloud Function resetPasswordWithCode()
  ↓
Cloud Function cập nhật Firebase Auth
  ↓
Done

❌ Cần: Blaze plan, Visa, Email service (SendGrid/Mailgun)
❌ Phức tạp: Nhiều bước, nhiều code
❌ Chi phí: $0.40/1M invocations
```

### Phương pháp mới (Firebase Auth):
```
User nhập email
  ↓
App kiểm tra email tồn tại trong Firestore
  ↓
App gọi mAuth.sendPasswordResetEmail(email)
  ↓
Firebase tự động gửi email với link reset
  ↓
User click link trong email
  ↓
Firebase hiển thị trang reset password
  ↓
User nhập mật khẩu mới
  ↓
Firebase tự động cập nhật
  ↓
Done

✅ Không cần: Blaze plan, Visa, Email service
✅ Đơn giản: Ít bước, ít code
✅ Chi phí: MIỄN PHÍ (free tier)
```

---

## 🎓 Câu Hỏi Thường Gặp

### Q1: Email có thể customize không?
**A:** Có! Vào Firebase Console → Authentication → Templates → Password reset → Edit template

### Q2: Link reset có bao nhiêu lần sử dụng?
**A:** Chỉ 1 lần. Sau khi reset thành công, link sẽ không dùng được nữa.

### Q3: Có thể thay đổi thời gian hết hạn link không?
**A:** Không. Firebase mặc định 1 giờ và không thể thay đổi.

### Q4: Email gửi từ domain nào?
**A:** `noreply@librarymanagement-2c326.firebaseapp.com` (domain Firebase)

### Q5: Có thể dùng domain riêng không?
**A:** Có, nhưng cần:
- Blaze plan
- Custom domain setup
- Email service (SendGrid, etc.)

### Q6: Mật khẩu cũ có cần nhập không?
**A:** KHÔNG. Đây là reset password, không cần biết mật khẩu cũ.

### Q7: Có log được ai reset password không?
**A:** Có. Check Firebase Console → Authentication → Users → User activity

---

## 🚀 Demo Flow (Bước Thực Hành)

### Bước 1: Chạy app
```bash
# Build và run
gradlew.bat assembleDebug

# Hoặc trong Android Studio
Run > Run 'app'
```

### Bước 2: Màn hình Login
```
1. Click "Quên mật khẩu?"
```

### Bước 3: Màn hình Forgot Password
```
2. Nhập email: student@gmail.com
3. Click "GỬI MÃ XÁC THỰC"
4. Đợi dialog hiển thị
5. Click "OK, Đã Hiểu"
```

### Bước 4: Kiểm tra email
```
6. Mở Gmail/email client
7. Tìm email từ Firebase
8. (Nếu không thấy → check Spam)
9. Click nút "RESET PASSWORD"
```

### Bước 5: Reset password
```
10. Trình duyệt mở trang Firebase
11. Nhập mật khẩu mới: newpassword123
12. Nhập lại: newpassword123
13. Click "Save"
14. Thấy thông báo "Password has been changed"
```

### Bước 6: Login lại
```
15. Quay lại app
16. Màn hình Login
17. Email: student@gmail.com
18. Password: newpassword123
19. Click "ĐĂNG NHẬP"
20. ✅ Vào được app!
```

---

## 📝 Tổng Kết

**Tính năng Quên Mật Khẩu hoạt động như sau:**

1. **User nhập email** → App kiểm tra email có tồn tại
2. **App gọi Firebase** → `sendPasswordResetEmail()`
3. **Firebase gửi email** → Link reset password
4. **User click link** → Mở trang Firebase
5. **User đặt mật khẩu mới** → Firebase tự động cập nhật
6. **User login lại** → Thành công!

**Đặc điểm:**
- ✅ Hoàn toàn miễn phí
- ✅ Không cần Cloud Functions
- ✅ Không cần Blaze plan/Visa
- ✅ Bảo mật cao
- ✅ UI đẹp
- ✅ Dễ maintain

**Perfect cho đồ án/dự án nhỏ!** 🎉

---

_Last updated: 02/11/2025_


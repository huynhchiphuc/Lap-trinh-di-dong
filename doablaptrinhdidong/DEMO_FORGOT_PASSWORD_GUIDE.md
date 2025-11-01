# 📸 DEMO TÍNH NĂNG QUÊN MẬT KHẨU - Screenshots Guide

## 🎬 Video Demo Flow

### Scene 1: Màn Hình Login
```
┌─────────────────────────────────────┐
│  📚 LIBRARY MANAGEMENT              │
│                                     │
│  ┌───────────────────────────────┐  │
│  │ Email                         │  │
│  │ student@gmail.com             │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │ Password                      │  │
│  │ ●●●●●●                        │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌─────────────────────────────────┐│
│  │      ĐĂNG NHẬP                  ││
│  └─────────────────────────────────┘│
│                                     │
│  ┌─────────────────────────────────┐│
│  │      ĐĂNG KÝ                    ││
│  └─────────────────────────────────┘│
│                                     │
│  Quên mật khẩu? 👈 [CLICK VÀO ĐÂY] │
│                                     │
└─────────────────────────────────────┘

ACTION: Click vào text "Quên mật khẩu?"
```

---

### Scene 2: Màn Hình Forgot Password
```
┌─────────────────────────────────────┐
│  ← [Back]    QUÊN MẬT KHẨU         │
│                                     │
│  Nhập email để nhận link đặt lại   │
│  mật khẩu                           │
│                                     │
│  ┌───────────────────────────────┐  │
│  │ Email                         │  │
│  │ student@gmail.com 👈 [NHẬP]   │  │ 
│  └───────────────────────────────┘  │
│                                     │
│  ┌─────────────────────────────────┐│
│  │   GỬI MÃ XÁC THỰC 👈 [CLICK]   ││
│  └─────────────────────────────────┘│
│                                     │
│  ⏪ Quay lại đăng nhập              │
│                                     │
└─────────────────────────────────────┘

ACTION: 
1. Nhập email: student@gmail.com
2. Click "GỬI MÃ XÁC THỰC"
```

---

### Scene 3: Loading
```
┌─────────────────────────────────────┐
│  ← [Back]    QUÊN MẬT KHẨU         │
│                                     │
│  Nhập email để nhận link đặt lại   │
│  mật khẩu                           │
│                                     │
│  ┌───────────────────────────────┐  │
│  │ Email                         │  │
│  │ student@gmail.com             │  │
│  └───────────────────────────────┘  │
│                                     │
│            🔄 Loading...            │
│                                     │
│  [GỬI MÃ XÁC THỰC] (disabled)      │
│                                     │
└─────────────────────────────────────┘

STATE: App đang kiểm tra email và gửi link
```

---

### Scene 4: Dialog Thành Công
```
┌─────────────────────────────────────┐
│  ┌─────────────────────────────────┐│
│  │  ✅ Email Đã Được Gửi!         ││
│  │                                 ││
│  │  📧 Chúng tôi đã gửi link đặt   ││
│  │  lại mật khẩu đến:              ││
│  │                                 ││
│  │  student@gmail.com              ││
│  │                                 ││
│  │  📌 Vui lòng:                   ││
│  │  1. Kiểm tra hộp thư đến        ││
│  │  2. Nếu không thấy, kiểm tra    ││
│  │     thư mục Spam                ││
│  │  3. Nhấn vào link trong email   ││
│  │  4. Đặt mật khẩu mới            ││
│  │                                 ││
│  │  ⏰ Link có hiệu lực trong 1h   ││
│  │                                 ││
│  │  ┌───────────────────────────┐ ││
│  │  │    OK, Đã Hiểu 👈 [CLICK] │ ││
│  │  └───────────────────────────┘ ││
│  └─────────────────────────────────┘│
└─────────────────────────────────────┘

ACTION: Click "OK, Đã Hiểu"
RESULT: Quay về màn hình Login
```

---

### Scene 5: Email Client (Gmail)
```
┌─────────────────────────────────────────────────────────┐
│  Gmail                                 🔍 Search    ☰   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  📧 Inbox                                               │
│                                                         │
│  ┌───────────────────────────────────────────────────┐ │
│  │ noreply@librarymanagement-2c326...  Just now     │ │
│  │ Reset your password for Library Management       │ │
│  │                                                   │ │
│  │ Hello,                                            │ │
│  │                                                   │ │
│  │ Follow this link to reset your Library           │ │
│  │ Management password for your student@gmail.com   │ │
│  │ account.                                          │ │
│  │                                                   │ │
│  │ ┌─────────────────────────────────────────────┐  │ │
│  │ │        RESET PASSWORD 👈 [CLICK]           │  │ │
│  │ └─────────────────────────────────────────────┘  │ │
│  │                                                   │ │
│  │ If you didn't ask to reset your password,        │ │
│  │ you can ignore this email.                       │ │
│  │                                                   │ │
│  │ Thanks,                                           │ │
│  │ Your Library Management team                     │ │
│  └───────────────────────────────────────────────────┘ │
│                                                         │
└─────────────────────────────────────────────────────────┘

ACTION: Click nút "RESET PASSWORD"
RESULT: Mở trình duyệt với trang Firebase
```

---

### Scene 6: Firebase Reset Password Page (Browser)
```
┌─────────────────────────────────────────────────────────┐
│  🔥 Firebase                                            │
│  https://librarymanagement-2c326.firebaseapp.com/...   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Reset your password                                    │
│                                                         │
│  ┌───────────────────────────────────────────────────┐ │
│  │ New password                                      │ │
│  │ newpassword123 👈 [NHẬP]                          │ │
│  └───────────────────────────────────────────────────┘ │
│                                                         │
│  ┌───────────────────────────────────────────────────┐ │
│  │ Confirm new password                              │ │
│  │ newpassword123 👈 [NHẬP LẠI]                      │ │
│  └───────────────────────────────────────────────────┘ │
│                                                         │
│  ℹ️ Password must be at least 6 characters             │
│                                                         │
│  ┌─────────────────┐                                   │
│  │   Save 👈 [CLICK]│                                   │
│  └─────────────────┘                                   │
│                                                         │
└─────────────────────────────────────────────────────────┘

ACTION: 
1. Nhập mật khẩu mới: newpassword123
2. Nhập lại: newpassword123
3. Click "Save"
```

---

### Scene 7: Success Message (Browser)
```
┌─────────────────────────────────────────────────────────┐
│  🔥 Firebase                                            │
├─────────────────────────────────────────────────────────┤
│                                                         │
│                 ✅                                      │
│                                                         │
│         Password has been changed                       │
│                                                         │
│  Your password has been successfully updated.           │
│  You can now sign in with your new password.            │
│                                                         │
│  ┌─────────────────┐                                   │
│  │   Continue      │                                   │
│  └─────────────────┘                                   │
│                                                         │
└─────────────────────────────────────────────────────────┘

ACTION: Click "Continue" hoặc đóng browser
RESULT: Quay lại app để login
```

---

### Scene 8: Login Với Mật Khẩu Mới
```
┌─────────────────────────────────────┐
│  📚 LIBRARY MANAGEMENT              │
│                                     │
│  ┌───────────────────────────────┐  │
│  │ Email                         │  │
│  │ student@gmail.com 👈 [NHẬP]   │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │ Password                      │  │
│  │ newpassword123 👈 [NHẬP MỚI]  │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌─────────────────────────────────┐│
│  │      ĐĂNG NHẬP 👈 [CLICK]       ││
│  └─────────────────────────────────┘│
│                                     │
└─────────────────────────────────────┘

ACTION:
1. Email: student@gmail.com
2. Password: newpassword123 (mật khẩu mới)
3. Click "ĐĂNG NHẬP"
```

---

### Scene 9: Login Thành Công
```
┌─────────────────────────────────────┐
│  📚 Library Management              │
│                                     │
│  👤 Welcome, Student Name!          │
│                                     │
│  📚 Sách   📋 Mượn   👤 Profile     │
│  ════════                           │
│                                     │
│  ┌─────────────────────────────────┐│
│  │ 📖 Book 1                       ││
│  │ Author: John Doe                ││
│  │ Available: 5                    ││
│  └─────────────────────────────────┘│
│                                     │
│  ┌─────────────────────────────────┐│
│  │ 📖 Book 2                       ││
│  │ Author: Jane Smith              ││
│  │ Available: 3                    ││
│  └─────────────────────────────────┘│
│                                     │
└─────────────────────────────────────┘

✅ THÀNH CÔNG! Vào được app với mật khẩu mới
```

---

## 🎥 Script Demo Video

### Opening (0:00 - 0:05)
```
Narrator: "Hôm nay mình sẽ demo tính năng Quên Mật Khẩu 
          của ứng dụng Library Management"
```

### Part 1: Forgot Password (0:05 - 0:15)
```
Narrator: "Đầu tiên, ở màn hình đăng nhập, 
          mình click vào 'Quên mật khẩu'"

Action: Click "Quên mật khẩu?"

Narrator: "Nhập email đã đăng ký: student@gmail.com"

Action: Type "student@gmail.com"

Narrator: "Sau đó bấm 'Gửi mã xác thực'"

Action: Click "GỬI MÃ XÁC THỰC"
```

### Part 2: Success Dialog (0:15 - 0:25)
```
Narrator: "App hiển thị thông báo email đã được gửi,
          và hướng dẫn chi tiết các bước tiếp theo"

Action: Show dialog

Narrator: "Mình sẽ kiểm tra email ngay"

Action: Click "OK, Đã Hiểu"
```

### Part 3: Check Email (0:25 - 0:35)
```
Narrator: "Mở Gmail, mình thấy email từ Firebase"

Action: Open Gmail app/browser

Narrator: "Email có nút Reset Password"

Action: Show email content

Narrator: "Click vào đây"

Action: Click "RESET PASSWORD"
```

### Part 4: Reset Password (0:35 - 0:50)
```
Narrator: "Browser mở trang Firebase với giao diện đẹp"

Action: Show Firebase page

Narrator: "Nhập mật khẩu mới hai lần"

Action: Type "newpassword123" (2 times)

Narrator: "Click Save"

Action: Click "Save"

Narrator: "Thành công! Mật khẩu đã được đổi"

Action: Show success message
```

### Part 5: Login (0:50 - 1:00)
```
Narrator: "Quay lại app, đăng nhập với mật khẩu mới"

Action: Return to app

Narrator: "Email: student@gmail.com"

Action: Type email

Narrator: "Password: mật khẩu mới vừa đặt"

Action: Type "newpassword123"

Narrator: "Bấm Đăng Nhập"

Action: Click "ĐĂNG NHẬP"

Narrator: "Và vào được app thành công!"

Action: Show home screen

Narrator: "Vậy là xong! Rất đơn giản và nhanh chóng"
```

### Closing (1:00 - 1:10)
```
Narrator: "Tính năng này sử dụng Firebase Authentication,
          hoàn toàn miễn phí, không cần Cloud Functions.
          Link tài liệu chi tiết ở description nhé.
          Thanks for watching!"
```

---

## 📸 Screenshots Checklist

Để demo đầy đủ, bạn cần chụp các màn hình sau:

### Screenshots cần có:

- [ ] **01_login_screen.png** - Màn hình login với nút "Quên mật khẩu?"
- [ ] **02_forgot_password_screen.png** - Màn hình nhập email
- [ ] **03_forgot_password_filled.png** - Đã nhập email
- [ ] **04_loading.png** - Đang gửi email (loading)
- [ ] **05_success_dialog.png** - Dialog thông báo thành công
- [ ] **06_email_inbox.png** - Gmail inbox với email từ Firebase
- [ ] **07_email_content.png** - Nội dung email với nút Reset
- [ ] **08_firebase_reset_page.png** - Trang Firebase reset password
- [ ] **09_firebase_reset_filled.png** - Đã nhập mật khẩu mới
- [ ] **10_firebase_success.png** - Thông báo đổi mật khẩu thành công
- [ ] **11_login_new_password.png** - Login với mật khẩu mới
- [ ] **12_home_screen.png** - Vào app thành công

### Screenshots lỗi (cho test cases):

- [ ] **error_01_invalid_email.png** - Email không hợp lệ
- [ ] **error_02_email_not_exist.png** - Email không tồn tại
- [ ] **error_03_no_internet.png** - Không có mạng
- [ ] **error_04_weak_password.png** - Mật khẩu yếu (trên Firebase page)
- [ ] **error_05_expired_link.png** - Link hết hạn

---

## 🎬 GIF Animation Ideas

### GIF 1: Full Flow (10 seconds)
```
1. Login screen
2. Click "Quên mật khẩu"
3. Enter email
4. Success dialog
5. Check email
6. Reset password
7. Login success
```

### GIF 2: Error Cases (5 seconds)
```
1. Invalid email → Error message
2. Email not exist → Toast
3. Network error → Toast
```

### GIF 3: Firebase Reset Page (3 seconds)
```
1. Enter new password
2. Confirm password
3. Click Save
4. Success message
```

---

## 📱 App Screenshots Best Practices

### Chuẩn bị:
1. **Device:** Dùng emulator hoặc thiết bị thật
2. **Resolution:** 1080x1920 (Full HD) hoặc cao hơn
3. **Clean UI:** Không có notification bar lộn xộn
4. **Demo data:** Dùng dữ liệu mẫu đẹp, có ý nghĩa

### Chụp màn hình:
- **Android Studio:** Tools → AVD Manager → Emulator → Camera icon
- **Physical device:** Power + Volume Down
- **ADB:** `adb exec-out screencap -p > screenshot.png`

### Edit (optional):
- Thêm khung điện thoại
- Highlight các phần quan trọng
- Thêm mũi tên chỉ dẫn
- Thêm text giải thích

---

## 🎨 Presentation Ideas

### PowerPoint/Google Slides:

**Slide 1: Title**
```
Tính Năng Quên Mật Khẩu
Library Management App
```

**Slide 2: Overview**
```
- Sử dụng Firebase Authentication
- Hoàn toàn miễn phí
- Không cần Cloud Functions
- Email được gửi tự động
```

**Slide 3: User Flow**
```
[Flow diagram với screenshots]
```

**Slide 4: Screenshots Demo**
```
[4-6 screenshots chính]
```

**Slide 5: Email Template**
```
[Screenshot email]
```

**Slide 6: Firebase Reset Page**
```
[Screenshot trang Firebase]
```

**Slide 7: Success**
```
[Screenshot login thành công]
```

**Slide 8: Technical Details**
```
- Code snippet
- Firebase configuration
- Security features
```

---

## 🎤 Demo Live Script

### Chuẩn bị trước:
1. App đã build và cài trên emulator/device
2. Gmail đã login và mở sẵn
3. Internet kết nối tốt
4. Clear data nếu cần

### Demo steps:

**Step 1:** "Giả sử user quên mật khẩu..."

**Step 2:** "Click vào đây để vào màn hình forgot password"

**Step 3:** "Nhập email đã đăng ký"

**Step 4:** "Hệ thống sẽ kiểm tra email và gửi link reset"

**Step 5:** "Email đã được gửi, kiểm tra gmail"

**Step 6:** "Đây là email từ Firebase, click reset password"

**Step 7:** "Trang Firebase hiển thị form đẹp, nhập mật khẩu mới"

**Step 8:** "Mật khẩu đã được đổi thành công"

**Step 9:** "Quay lại app, login với mật khẩu mới"

**Step 10:** "Thành công! User vào được app"

---

## ✅ Checklist Trước Khi Demo

### App:
- [ ] Build thành công
- [ ] Cài đặt trên device/emulator
- [ ] Test forgot password flow 1 lần
- [ ] Clear data nếu cần demo từ đầu

### Firebase:
- [ ] Authentication enabled
- [ ] Test email account đã đăng ký
- [ ] Email template đã setup (nếu có custom)

### Email:
- [ ] Gmail/email client đã login
- [ ] Inbox đã clear (để demo rõ ràng)
- [ ] Check spam folder (biết đâu email vào đó)

### Recording (nếu quay video):
- [ ] Screen recorder đã cài đặt
- [ ] Microphone test
- [ ] Resolution đủ cao (1080p+)
- [ ] Tắt notification/DND mode

### Presentation:
- [ ] Screenshots đã chụp đầy đủ
- [ ] Slides đã chuẩn bị
- [ ] Script đã thuộc
- [ ] Backup plan nếu internet chậm

---

## 🎓 Tổng Kết

Demo tính năng Quên Mật Khẩu rất đơn giản:

1. **Visual:** Chụp screenshots đầy đủ các màn hình
2. **Video:** Quay video demo hoặc tạo GIF
3. **Presentation:** Chuẩn bị slides với screenshots
4. **Live demo:** Tập script và test trước

**Lưu ý quan trọng:**
- Kiểm tra internet tốt
- Test flow 1-2 lần trước
- Có backup plan (screenshots) nếu live demo lỗi
- Giải thích rõ tại sao dùng Firebase Auth (miễn phí, dễ, bảo mật)

**Chúc bạn demo thành công! 🎉**

---

_Created: 02/11/2025_


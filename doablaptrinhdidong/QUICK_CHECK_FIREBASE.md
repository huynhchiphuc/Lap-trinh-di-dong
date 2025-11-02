# ⚡ QUICK CHECK - Firebase Console

## 🔥 Kiểm Tra Firebase Ngay (3 phút)

### ✅ Checklist Quan Trọng Nhất

#### 1. Enable Email/Password Authentication ⭐⭐⭐

```
🔗 LINK: https://console.firebase.google.com/project/librarymanagement-2c326/authentication/providers

BƯỚC LÀM:
1. Click link trên
2. Tìm "Email/Password" trong danh sách
3. Click vào để mở
4. Bật ON cho "Email/Password"
5. Click "Save"

✅ KẾT QUẢ ĐÚNG: Status hiển thị "Enabled" (màu xanh)
❌ NẾU LỖI: Email sẽ không gửi được!
```

#### 2. Kiểm Tra User Test Đã Đăng Ký ⭐⭐⭐

```
🔗 LINK: https://console.firebase.google.com/project/librarymanagement-2c326/authentication/users

BƯỚC LÀM:
1. Click link trên
2. Xem danh sách users
3. Tìm email muốn test (ví dụ: student@gmail.com)

✅ KẾT QUẢ ĐÚNG: Thấy user trong list
❌ NẾU KHÔNG THẤY: 
   - Đăng ký user mới bằng cách click "Add user"
   - Hoặc đăng ký qua app trước
```

#### 3. Kiểm Tra API Enabled ⭐⭐

```
🔗 LINK: https://console.cloud.google.com/apis/library/identitytoolkit.googleapis.com?project=librarymanagement-2c326

BƯỚC LÀM:
1. Click link trên
2. Xem status của "Identity Toolkit API"

✅ KẾT QUẢ ĐÚNG: Status "Enabled" (màu xanh)
❌ NẾU CHƯA ENABLE: Click "Enable" button
```

---

## 🧪 Test Nhanh Trong 5 Phút

### Option 1: Test Qua Firebase Console

```
1. Vào: https://console.firebase.google.com/project/librarymanagement-2c326/authentication/users

2. Click "Add user"

3. Nhập:
   Email: test@gmail.com (email thật của bạn)
   Password: 123456

4. Click "Add user"

5. Click vào user vừa tạo

6. Click "⋮" (3 dots) → "Send password reset email"

7. Check email test@gmail.com

✅ NẾU NHẬN ĐƯỢC EMAIL: Firebase hoạt động tốt, lỗi ở app
❌ NẾU KHÔNG NHẬN: Vấn đề ở Firebase config
```

### Option 2: Test Qua App

```
1. Run app

2. Vào màn hình Login

3. Click "Quên mật khẩu"

4. Nhập email đã test ở trên: test@gmail.com

5. Click "Gửi"

6. Mở Android Studio → Logcat

7. Filter: "ForgotPassword"

8. Xem logs:
   ✅ Có "SUCCESS! Email sent" → Kiểm tra Spam folder
   ❌ Có "FAILED!" → Đọc error message
```

---

## 📱 Xem Logs Trong Android Studio

### Mở Logcat:

```
Android Studio → Bottom bar → Logcat tab
```

### Filter logs:

```
1. Trong ô search, gõ: "ForgotPassword"
2. Hoặc: "Firebase"
```

### Logs quan trọng:

```
✅ GOOD LOGS (Success):
========== START FORGOT PASSWORD ==========
Email entered: test@gmail.com
✓ Email validation passed
Checking email in Firestore...
✓ Firestore query success
Documents found: 1
✓ User found. ID: xxx
Calling Firebase sendPasswordResetEmail...
========================================
✅ SUCCESS! Email sent to: test@gmail.com
Please check inbox and spam folder
========================================

❌ ERROR LOGS (Failed):
========================================
❌ FAILED! Error sending email
Error class: com.google.firebase.FirebaseException
Error message: [Error details here]
========================================
```

---

## 🔧 Common Fixes

### Fix 1: Email/Password Not Enabled

```
Problem: Authentication method not enabled
Solution:
  1. https://console.firebase.google.com/project/librarymanagement-2c326/authentication/providers
  2. Enable "Email/Password"
  3. Save
  4. Test lại
```

### Fix 2: User Not Registered

```
Problem: Email không tồn tại trong Firebase Auth
Solution:
  1. Đăng ký user qua app
  2. Hoặc add user qua Console
  3. Email trong Firestore và Auth phải giống nhau
```

### Fix 3: API Key Issues

```
Problem: API key không có quyền
Solution:
  1. Download lại google-services.json từ Firebase
  2. Copy vào app/ folder
  3. Rebuild app: gradlew.bat clean assembleDebug
```

### Fix 4: Internet Issues

```
Problem: Không có kết nối internet
Solution:
  1. Check wifi/mobile data
  2. Ping google.com để test
  3. Check firewall không block
```

### Fix 5: Email In Spam

```
Problem: Email vào spam folder
Solution:
  1. Check Spam/Junk folder
  2. Mark as "Not Spam"
  3. Add sender vào whitelist
  4. Email từ: noreply@librarymanagement-2c326.firebaseapp.com
```

---

## 📧 Email Checklist

Sau khi gửi thành công, check email:

### Gmail:
- [ ] Check Inbox
- [ ] Check Spam folder
- [ ] Check Promotions tab
- [ ] Check Updates tab
- [ ] Đợi 1-5 phút

### Email Content:
```
From: noreply@librarymanagement-2c326.firebaseapp.com
Subject: Reset your password for Library Management
Body: Có button "RESET PASSWORD"
```

### Nếu không thấy:
1. **Đợi thêm** - Email có thể delay 5-10 phút
2. **Check Spam** - 80% trường hợp vào đây
3. **Search email** - Tìm "librarymanagement" trong Gmail
4. **Check filters** - Gmail Settings → Filters

---

## 🎯 Decision Tree

```
Email không gửi được?
│
├─ App có crash không?
│  ├─ Có → Fix crash trước
│  └─ Không → Tiếp tục
│
├─ Logcat có log "SUCCESS!" không?
│  ├─ Có → Email đã gửi, check Spam folder
│  └─ Không → Tiếp tục
│
├─ Logcat có log "FAILED!" không?
│  ├─ Có → Đọc error message
│  │        ├─ "DEVELOPER_ERROR" → Check Firebase Console
│  │        ├─ "USER_NOT_FOUND" → Đăng ký user trước
│  │        ├─ "network" → Check internet
│  │        └─ Khác → Google error message
│  └─ Không có log gì → Firebase chưa init
│
└─ Email/Password enabled chưa?
   ├─ Chưa → Enable ngay!
   └─ Rồi → Test lại từ đầu
```

---

## 📞 Cần Help?

### 1. Copy Logs

```
Android Studio → Logcat → Select all logs → Copy
Paste vào file text để share
```

### 2. Screenshot Errors

```
Screenshot:
- Toast error message
- Logcat errors
- Firebase Console config
```

### 3. Check Firebase Status

```
https://status.firebase.google.com/
Xem Firebase có down không
```

---

## ✅ Summary

**3 điều PHẢI CHECK trước tiên:**

1. **Email/Password ENABLED** trong Firebase Console
   → 90% lỗi ở đây!

2. **User đã đăng ký** trong Firebase Auth
   → Không có user thì không gửi được!

3. **Logs trong Logcat** để biết lỗi gì
   → Đọc logs để biết fix gì!

**Sau khi check 3 điều trên:**
- 99% trường hợp sẽ work ✅
- Email có thể vào Spam ✅
- Đợi 1-5 phút ✅

**Good luck! 🍀**

---

_Quick reference - 02/11/2025_


# ✅ SỬ DỤNG CHỨC NĂNG QUÊN MẬT KHẨU - TEST MODE

## 🎯 Không cần deploy Cloud Functions!

App đã được cấu hình để hoạt động ngay cả khi **CHƯA deploy Cloud Functions**.

---

## 🚀 Cách sử dụng TEST MODE

### Bước 1: Quên mật khẩu
```
1. Mở app
2. Màn hình Login → Nhấn "Quên mật khẩu"
3. Nhập email đã đăng ký
4. Nhấn "GỬI MÃ XÁC THỰC"
```

### Bước 2: Nhận mã xác thực
```
→ Sẽ hiện Dialog:
┌────────────────────────────────────┐
│ 📧 TEST MODE - Mã Xác Thực        │
├────────────────────────────────────┤
│                                    │
│ ⚠️ Cloud Function chưa deploy!    │
│                                    │
│ 📋 Mã xác thực của bạn là:        │
│                                    │
│ ━━━━━━━━━━━━━━━                   │
│      1 2 3 4 5 6                   │
│ ━━━━━━━━━━━━━━━                   │
│                                    │
│ ✏️ Vui lòng ghi nhớ hoặc copy mã  │
│                                    │
│ [OK, Đã copy mã] [Tiếp tục]       │
└────────────────────────────────────┘

→ Nhấn "OK, Đã copy mã" để tự động copy
   hoặc "Tiếp tục" để ghi nhớ mã
```

### Bước 3: Nhập mã và đổi mật khẩu
```
1. Nhập mã 6 số vừa nhận
2. Nhập mật khẩu mới (>= 6 ký tự)
3. Xác nhận mật khẩu mới
4. Nhấn "ĐẶT LẠI MẬT KHẨU"
```

### Bước 4: Hoàn tất
```
→ Mật khẩu đã được đặt lại!
→ Tự động quay về màn hình Login sau 3 giây
→ Đăng nhập với mật khẩu mới
✅ Thành công!
```

---

## 📝 Ví dụ thực tế

```
1. Email: student@gmail.com
2. Gửi mã → Dialog hiện mã: 456789
3. Copy mã: 456789
4. Mật khẩu mới: password123
5. Xác nhận: password123
6. Đặt lại → Thành công!
7. Login với password123 → OK!
```

---

## 🎨 Tính năng Dialog mới

### ✅ Cải tiến:
- **AlertDialog** thay vì Toast (dễ đọc hơn)
- **Định dạng mã rõ ràng** với viền
- **Nút "OK, Đã copy mã"** tự động copy vào clipboard
- **Hiển thị lâu hơn** (không tự động mất như Toast)
- **Cảnh báo rõ ràng** về TEST MODE

### 📋 Auto-copy vào Clipboard:
Khi nhấn "OK, Đã copy mã":
- Mã tự động được copy vào clipboard
- Có thể paste trực tiếp vào ô nhập mã
- Tiện lợi hơn nhiều!

---

## 🔄 So sánh TEST MODE vs PRODUCTION

| Tính năng | TEST MODE | PRODUCTION |
|-----------|-----------|------------|
| **Deploy Cloud Functions** | ❌ Không cần | ✅ Cần |
| **Gửi email thật** | ❌ Không | ✅ Có |
| **Hiển thị mã** | ✅ Dialog | ❌ Không (gửi email) |
| **Thời gian setup** | 0 phút | 6 phút |
| **Testing** | ✅ Hoàn hảo | N/A |
| **User experience** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## ⚠️ Lưu ý

### 1. Mã vẫn hết hạn sau 10 phút
```
→ Nếu chờ quá 10 phút
→ Phải gửi lại mã mới
→ Countdown 60s giữa các lần gửi
```

### 2. Mã chỉ dùng được 1 lần
```
→ Sau khi reset password thành công
→ Mã tự động bị xóa
→ Không thể dùng lại
```

### 3. Dialog không tự động đóng
```
→ Phải nhấn "OK" hoặc "Tiếp tục"
→ Đảm bảo user thấy mã
→ Không bị mất mã như Toast
```

---

## 🚀 Nâng cấp lên PRODUCTION

Khi muốn gửi email THẬT:

```bash
# Follow hướng dẫn trong QUICK_START_EMAIL.md
1. Tạo Gmail App Password (2 phút)
2. Init Firebase Functions (1 phút)
3. Copy code functions/index.js (30s)
4. npm install nodemailer (30s)
5. Set config Gmail (30s)
6. firebase deploy --only functions (1 phút)
7. Rebuild app → Done!

→ Email sẽ được gửi thật qua Gmail
→ Dialog TEST MODE sẽ không hiện nữa
→ User nhận email chuyên nghiệp!
```

---

## 🎯 Kết luận

### TEST MODE hiện tại:
✅ **Hoạt động ngay** không cần setup
✅ **Dialog rõ ràng** với mã dễ đọc
✅ **Auto-copy** vào clipboard
✅ **Hoàn hảo cho testing**
✅ **Demo được cho giảng viên**

### Để chuyển sang Production:
- Follow **QUICK_START_EMAIL.md** (6 phút)
- Deploy Cloud Functions
- Rebuild app
- Email gửi thật!

---

## 📸 Screenshots Flow

```
Login Screen
    ↓ [Quên mật khẩu]
    
Forgot Password - Step 1
├─ Email: [student@gmail.com]
└─ [GỬI MÃ XÁC THỰC]
    ↓
    
Dialog: TEST MODE
┌────────────────────────┐
│ 📧 TEST MODE          │
│                        │
│ Mã của bạn: 123456    │
│                        │
│ [OK, Đã copy] [Next]  │
└────────────────────────┘
    ↓
    
Forgot Password - Step 2
├─ Mã: [123456] ← Paste từ clipboard
├─ Mật khẩu mới: [••••••]
├─ Xác nhận: [••••••]
└─ [ĐẶT LẠI MẬT KHẨU]
    ↓
    
Success Toast
"✅ Mật khẩu đã đổi thành công!"
    ↓
    
Login Screen (auto return)
Login với password mới → Success!
```

---

**✅ TEST MODE hoạt động hoàn hảo! Không cần deploy Cloud Functions!** 🎉


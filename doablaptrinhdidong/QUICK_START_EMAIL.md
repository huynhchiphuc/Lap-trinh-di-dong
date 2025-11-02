# HƯỚNG DẪN NHANH: GỬI EMAIL THẬT QUA GMAIL

## 🚀 Quick Start (5 phút)

### Bước 1: Chuẩn bị Gmail App Password (2 phút)

1. **Bật 2-Step Verification:**
   - Truy cập: https://myaccount.google.com/security
   - Tìm "2-Step Verification" → Bật lên

2. **Tạo App Password:**
   - Truy cập: https://myaccount.google.com/apppasswords
   - App: **Mail**
   - Device: **Other** → Nhập "LibraryApp"
   - **LƯU MẬT KHẨU 16 KÝ TỰ** (ví dụ: `abcd efgh ijkl mnop`)

---

### Bước 2: Cài đặt Firebase CLI (1 phút)

```bash
# Cài Firebase CLI
npm install -g firebase-tools

# Đăng nhập
firebase login
```

---

### Bước 3: Init Firebase Functions (1 phút)

```bash
cd D:\do_an_mon\Lap-trinh-di-dong\doablaptrinhdidong

firebase init functions
```

**Chọn:**
- ✅ Use existing project → Chọn project Firebase của bạn
- ✅ JavaScript
- ✅ ESLint: No (hoặc Yes tùy ý)
- ✅ Install dependencies: Yes

---

### Bước 4: Copy Code Cloud Function (30 giây)

**File: `functions/index.js`** (tự động tạo sau bước 3)

Xóa hết nội dung và paste code này:

```javascript
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');

admin.initializeApp();

// Lấy config từ Firebase
const gmailEmail = functions.config().gmail.email;
const gmailPassword = functions.config().gmail.password;

const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: gmailEmail,
    pass: gmailPassword
  }
});

// Function gửi mã xác thực
exports.sendVerificationCode = functions.https.onCall(async (data) => {
  const { email, code } = data;

  const mailOptions = {
    from: `Thư Viện App <${gmailEmail}>`,
    to: email,
    subject: 'Mã Xác Thực Đặt Lại Mật Khẩu',
    html: `
      <div style="font-family: Arial; padding: 20px; max-width: 600px; margin: 0 auto;">
        <h1 style="color: #1976D2;">🔐 Đặt Lại Mật Khẩu</h1>
        <p>Mã xác thực của bạn là:</p>
        <div style="background: #E3F2FD; padding: 20px; text-align: center; border-radius: 8px;">
          <h1 style="color: #1976D2; letter-spacing: 5px;">${code}</h1>
        </div>
        <p style="margin-top: 20px;">⏰ Mã có hiệu lực trong <strong>10 phút</strong></p>
        <p style="color: red;">⚠️ Không chia sẻ mã này với bất kỳ ai!</p>
      </div>
    `
  };

  await transporter.sendMail(mailOptions);
  return { success: true };
});

// Function đổi mật khẩu trực tiếp
exports.resetPasswordWithCode = functions.https.onCall(async (data) => {
  const { email, code, newPassword } = data;
  
  const userRecord = await admin.auth().getUserByEmail(email);
  const userId = userRecord.uid;
  
  const codeDoc = await admin.firestore()
    .collection('verification_codes')
    .doc(userId)
    .get();
  
  if (!codeDoc.exists || codeDoc.data().code !== code) {
    throw new functions.https.HttpsError('invalid-argument', 'Mã không hợp lệ');
  }
  
  if (Date.now() > codeDoc.data().expiryTime) {
    throw new functions.https.HttpsError('deadline-exceeded', 'Mã đã hết hạn');
  }
  
  await admin.auth().updateUser(userId, { password: newPassword });
  await codeDoc.ref.delete();
  
  return { success: true };
});
```

---

### Bước 5: Cài Nodemailer (30 giây)

```bash
cd functions
npm install nodemailer
```

---

### Bước 6: Lưu Gmail credentials vào Firebase (30 giây)

```bash
firebase functions:config:set gmail.email="huynhchiphuchcp@gmail.com"
firebase functions:config:set gmail.password="ahns ircy shef pyng"
```

**Thay:**
- `your-email@gmail.com` → Email Gmail của bạn
- `abcd efgh ijkl mnop` → App Password từ bước 1

**Kiểm tra:**
```bash
firebase functions:config:get
```

Output:
```json
{
  "gmail": {
    "email": "your-email@gmail.com",
    "password": "abcd efgh ijkl mnop"
  }
}
```

---

### Bước 7: Deploy Functions (1 phút)

```bash
firebase deploy --only functions
```

**Đợi deploy xong** (khoảng 1-2 phút), sẽ thấy:
```
✔  functions[sendVerificationCode(us-central1)]
✔  functions[resetPasswordWithCode(us-central1)]

Deploy complete!
```

---

### Bước 8: Test trong App Android

1. **Sync Gradle** trong Android Studio
2. **Rebuild project**
3. **Chạy app**
4. **Test:**
   - Vào màn hình Login
   - Nhấn "Quên mật khẩu"
   - Nhập email
   - Nhấn "Gửi mã"
   - **Kiểm tra email** → Sẽ nhận được mã xác thực!

---

## ✅ Xong! Email sẽ được gửi thật!

---

## 🐛 Troubleshooting

### Lỗi: "Invalid login"
```bash
# Kiểm tra lại config
firebase functions:config:get

# Nếu sai, set lại
firebase functions:config:set gmail.email="email-dung@gmail.com"
firebase functions:config:set gmail.password="mat-khau-app-dung"

# Deploy lại
firebase deploy --only functions
```

### Lỗi: "NOT_FOUND"
- Functions chưa deploy hoặc deploy lỗi
- Chạy lại: `firebase deploy --only functions`

### Lỗi: "Permission denied"
- Kiểm tra Firestore Rules
- Firestore Rules phải cho phép read/write `verification_codes`

### Email vào Spam
- Bình thường, Gmail miễn phí dễ bị đánh dấu spam
- User cần kiểm tra folder Spam
- Hoặc nâng cấp lên SendGrid/AWS SES

---

## 🔄 Cách chuyển sang đổi mật khẩu trực tiếp

Trong `ForgotPasswordActivity.java`, dòng 308:

```java
// Hiện tại: Gửi email reset password (mặc định)
changePasswordViaEmail();

// Đổi thành: Reset password trực tiếp (sau khi deploy Cloud Function)
changePasswordViaCloudFunction(newPassword);
```

**Lợi ích:**
- User không cần click link trong email
- Đổi mật khẩu ngay lập tức
- Trải nghiệm mượt mà hơn

---

## 💰 Chi phí

### Gmail (Free)
- ✅ Miễn phí
- ⚠️ Limit: 500 emails/ngày
- ⚠️ Có thể bị spam

### Firebase Functions (Free Tier)
- ✅ 2 triệu calls/tháng
- ✅ 400,000 GB-seconds
- ✅ 200,000 CPU-seconds

**→ Đủ cho app nhỏ và vừa!**

---

## 📊 So sánh giải pháp

| Giải pháp | Chi phí | Độ tin cậy | Dễ setup | Khuyến nghị |
|-----------|---------|------------|----------|-------------|
| **Gmail** | Free | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ✅ Testing & Demo |
| **SendGrid** | $15/tháng | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ✅ Production |
| **AWS SES** | $0.10/1000 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ✅ Scale lớn |

---

## 🎯 Tóm tắt

```
1. Tạo Gmail App Password (2 phút)
2. Cài Firebase CLI (1 phút)  
3. Init Functions (1 phút)
4. Copy code vào functions/index.js (30s)
5. npm install nodemailer (30s)
6. Set config Gmail (30s)
7. Deploy functions (1 phút)
8. Test app → Email sẽ được gửi thật! ✅
```

**Tổng thời gian: ~6 phút**

---

## 📞 Support

Nếu gặp vấn đề:
1. Kiểm tra Firebase Console → Functions → Logs
2. Kiểm tra `firebase functions:config:get`
3. Test local: `firebase emulators:start`
4. Xem log deploy: `firebase functions:log`

---

**Done! Giờ app đã có thể gửi email thật rồi! 🎉**


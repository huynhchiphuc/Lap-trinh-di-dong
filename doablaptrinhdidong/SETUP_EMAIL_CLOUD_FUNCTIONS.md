# Setup Firebase Cloud Functions để Gửi Email

## Bước 1: Cài đặt Firebase CLI

```bash
# Cài đặt Firebase CLI
npm install -g firebase-tools

# Đăng nhập Firebase
firebase login

# Khởi tạo Functions trong project
cd d:\do_an_mon\Lap-trinh-di-dong\doablaptrinhdidong
firebase init functions
```

Chọn:
- Sử dụng existing project
- Chọn JavaScript hoặc TypeScript
- Install dependencies

## Bước 2: Cấu hình Gmail App Password

### 2.1. Tạo App Password cho Gmail:

1. Truy cập: https://myaccount.google.com/security
2. Bật **2-Step Verification** (bắt buộc)
3. Vào **App passwords**: https://myaccount.google.com/apppasswords
4. Chọn app: **Mail**, device: **Other** → Nhập tên "Library App"
5. Copy password 16 ký tự (VD: `abcd efgh ijkl mnop`)

### 2.2. Lưu credentials vào Firebase Config:

```bash
firebase functions:config:set gmail.email="your-email@gmail.com"
firebase functions:config:set gmail.password="abcd efgh ijkl mnop"
```

Kiểm tra:
```bash
firebase functions:config:get
```

## Bước 3: Code Cloud Function

Tạo file `functions/index.js`:

```javascript
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');

admin.initializeApp();

// Cấu hình Gmail transporter
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
exports.sendVerificationCode = functions.https.onCall(async (data, context) => {
  const { email, code } = data;

  if (!email || !code) {
    throw new functions.https.HttpsError('invalid-argument', 'Email và mã xác thực là bắt buộc');
  }

  const mailOptions = {
    from: `Thư Viện App <${gmailEmail}>`,
    to: email,
    subject: 'Mã Xác Thực Đặt Lại Mật Khẩu',
    html: `
      <!DOCTYPE html>
      <html>
      <head>
        <style>
          body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            padding: 20px;
          }
          .container {
            max-width: 600px;
            margin: 0 auto;
            background-color: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
          }
          .header {
            text-align: center;
            color: #1976D2;
            margin-bottom: 20px;
          }
          .code-box {
            background-color: #E3F2FD;
            border: 2px dashed #1976D2;
            border-radius: 8px;
            padding: 20px;
            text-align: center;
            margin: 20px 0;
          }
          .code {
            font-size: 32px;
            font-weight: bold;
            color: #1976D2;
            letter-spacing: 5px;
          }
          .warning {
            color: #F44336;
            margin-top: 20px;
            padding: 10px;
            background-color: #FFEBEE;
            border-radius: 5px;
          }
          .footer {
            margin-top: 30px;
            text-align: center;
            color: #666;
            font-size: 12px;
          }
        </style>
      </head>
      <body>
        <div class="container">
          <div class="header">
            <h1>🔐 Đặt Lại Mật Khẩu</h1>
          </div>
          
          <p>Xin chào,</p>
          <p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình tại <strong>Thư Viện App</strong>.</p>
          
          <div class="code-box">
            <p style="margin: 0; font-size: 14px; color: #666;">Mã xác thực của bạn là:</p>
            <p class="code">${code}</p>
          </div>
          
          <p style="text-align: center;">
            ⏰ Mã này có hiệu lực trong <strong>10 phút</strong>
          </p>
          
          <div class="warning">
            <strong>⚠️ Lưu ý bảo mật:</strong>
            <ul style="margin: 10px 0;">
              <li>Không chia sẻ mã này với bất kỳ ai</li>
              <li>Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này</li>
            </ul>
          </div>
          
          <div class="footer">
            <p>Email này được gửi tự động từ Thư Viện App</p>
            <p>Nếu bạn cần hỗ trợ, vui lòng liên hệ: support@library-app.com</p>
          </div>
        </div>
      </body>
      </html>
    `
  };

  try {
    await transporter.sendMail(mailOptions);
    console.log('Email sent successfully to:', email);
    return { success: true, message: 'Email đã được gửi thành công' };
  } catch (error) {
    console.error('Error sending email:', error);
    throw new functions.https.HttpsError('internal', 'Không thể gửi email: ' + error.message);
  }
});

// Function reset password với code verification
exports.resetPasswordWithCode = functions.https.onCall(async (data, context) => {
  const { email, code, newPassword } = data;

  if (!email || !code || !newPassword) {
    throw new functions.https.HttpsError('invalid-argument', 'Thiếu thông tin bắt buộc');
  }

  try {
    // 1. Tìm user ID từ email
    const userRecord = await admin.auth().getUserByEmail(email);
    const userId = userRecord.uid;

    // 2. Lấy mã xác thực từ Firestore
    const codeDoc = await admin.firestore()
      .collection('verification_codes')
      .doc(userId)
      .get();

    if (!codeDoc.exists) {
      throw new functions.https.HttpsError('not-found', 'Mã xác thực không tồn tại');
    }

    const savedData = codeDoc.data();

    // 3. Kiểm tra mã
    if (savedData.code !== code) {
      throw new functions.https.HttpsError('invalid-argument', 'Mã xác thực không đúng');
    }

    // 4. Kiểm tra hết hạn
    if (Date.now() > savedData.expiryTime) {
      throw new functions.https.HttpsError('deadline-exceeded', 'Mã xác thực đã hết hạn');
    }

    // 5. Cập nhật mật khẩu bằng Admin SDK
    await admin.auth().updateUser(userId, {
      password: newPassword
    });

    // 6. Xóa mã đã sử dụng
    await codeDoc.ref.delete();

    // 7. Gửi email thông báo
    await sendPasswordChangedEmail(email);

    console.log('Password reset successfully for:', email);
    return { success: true, message: 'Mật khẩu đã được đặt lại thành công' };

  } catch (error) {
    console.error('Error resetting password:', error);
    throw error;
  }
});

// Helper function: Gửi email thông báo đã đổi mật khẩu
async function sendPasswordChangedEmail(email) {
  const mailOptions = {
    from: `Thư Viện App <${gmailEmail}>`,
    to: email,
    subject: 'Mật khẩu đã được thay đổi',
    html: `
      <div style="font-family: Arial, sans-serif; padding: 20px;">
        <h2 style="color: #4CAF50;">✅ Mật khẩu đã được thay đổi thành công</h2>
        <p>Mật khẩu tài khoản của bạn đã được cập nhật.</p>
        <p>Nếu bạn không thực hiện thay đổi này, vui lòng liên hệ với chúng tôi ngay lập tức.</p>
        <p style="margin-top: 30px; color: #666; font-size: 12px;">
          Email này được gửi tự động từ Thư Viện App
        </p>
      </div>
    `
  };

  await transporter.sendMail(mailOptions);
}
```

### Cài đặt dependencies:

```bash
cd functions
npm install nodemailer
```

File `functions/package.json`:
```json
{
  "name": "functions",
  "description": "Cloud Functions for Firebase",
  "scripts": {
    "serve": "firebase emulators:start --only functions",
    "shell": "firebase functions:shell",
    "start": "npm run shell",
    "deploy": "firebase deploy --only functions",
    "logs": "firebase functions:log"
  },
  "engines": {
    "node": "18"
  },
  "main": "index.js",
  "dependencies": {
    "firebase-admin": "^11.8.0",
    "firebase-functions": "^4.3.1",
    "nodemailer": "^6.9.7"
  }
}
```

## Bước 4: Deploy Functions

```bash
firebase deploy --only functions
```

Output sẽ có URL của functions:
```
✔  functions[sendVerificationCode]: https://us-central1-your-project.cloudfunctions.net/sendVerificationCode
✔  functions[resetPasswordWithCode]: https://us-central1-your-project.cloudfunctions.net/resetPasswordWithCode
```

## Bước 5: Test Functions (Local)

```bash
# Chạy emulator
firebase emulators:start

# Functions sẽ chạy tại: http://localhost:5001
```

## Bước 6: Cấu hình Firestore Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Cho phép Cloud Functions ghi vào verification_codes
    match /verification_codes/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## Troubleshooting

### Lỗi: "Less secure app access"
- Giải pháp: Sử dụng App Password thay vì password thường

### Lỗi: "Invalid login"
- Kiểm tra email và App Password đúng
- Chạy: `firebase functions:config:get` để verify

### Lỗi: "Daily sending limit exceeded"
- Gmail free có giới hạn: 500 emails/ngày
- Xem xét nâng cấp hoặc dùng SendGrid

### Test gửi email:
```bash
# Trong Firebase Console
firebase functions:shell

# Gọi function
sendVerificationCode({email: 'test@gmail.com', code: '123456'})
```

## Giá cả

### Gmail (Free):
- ✅ Miễn phí
- ⚠️ Giới hạn: 500 emails/ngày
- ⚠️ Có thể bị đánh dấu spam

### Firebase Cloud Functions:
- ✅ Free tier: 2 triệu invocations/tháng
- ✅ 400,000 GB-seconds/tháng
- ✅ 200,000 CPU-seconds/tháng

Đủ cho app nhỏ và testing!

## Lưu ý bảo mật

1. **KHÔNG commit** Gmail App Password vào Git
2. **Sử dụng** Firebase config để lưu credentials
3. **Xác thực** người dùng trước khi gọi functions
4. **Rate limit** để chống spam
5. **Log** mọi request để audit

## Kết luận

Sau khi setup xong, email sẽ được gửi THỰC SỰ qua Gmail của bạn với template đẹp mắt!


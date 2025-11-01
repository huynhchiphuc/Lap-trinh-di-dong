# Tổng kết: Tính năng Quên Mật Khẩu

## Tóm tắt
Đã implement chức năng **Quên mật khẩu** với quy trình:
1. ✅ Nhập email
2. ✅ Hệ thống gửi mã xác thực 6 số
3. ✅ Nhập mã xác thực + Mật khẩu mới
4. ✅ Xác thực mã và gửi link reset password qua email

## Mục đích
Cho phép người dùng lấy lại quyền truy cập tài khoản khi quên mật khẩu một cách an toàn và bảo mật.

## Luồng hoạt động

### Bước 1: Nhập Email
```
User nhấn "Quên mật khẩu" từ màn hình đăng nhập
    ↓
Nhập email đã đăng ký
    ↓
Nhấn "GỬI MÃ XÁC THỰC"
    ↓
Hệ thống kiểm tra email có tồn tại không
    ├─ Không tồn tại → Thông báo lỗi
    └─ Tồn tại → Tiếp tục
```

### Bước 2: Tạo và Lưu Mã Xác Thực
```
Hệ thống tạo mã 6 số ngẫu nhiên (VD: 123456)
    ↓
Lưu vào Firestore collection "verification_codes":
    {
        code: "123456",
        email: "user@example.com",
        timestamp: 1234567890,
        expiryTime: 1234567890 + 10 phút
    }
    ↓
Hiển thị mã trong Toast (để test)
    ↓
Chuyển sang màn hình nhập mã
```

### Bước 3: Nhập Mã và Mật Khẩu Mới
```
User nhập:
    - Mã xác thực (6 số)
    - Mật khẩu mới
    - Xác nhận mật khẩu mới
    ↓
Nhấn "ĐẶT LẠI MẬT KHẨU"
    ↓
Hệ thống validate:
    ├─ Mã có đúng không?
    ├─ Mã có hết hạn không? (>10 phút)
    ├─ Mật khẩu >= 6 ký tự?
    └─ Mật khẩu xác nhận có khớp không?
```

### Bước 4: Đặt Lại Mật Khẩu
```
Mã hợp lệ
    ↓
Firebase Auth gửi email reset password
    ↓
User nhận email với link đặt lại mật khẩu
    ↓
User click link → Đặt mật khẩu mới
    ↓
Hoàn tất! Đăng nhập với mật khẩu mới
```

## Chi tiết kỹ thuật

### 1. Layout - `activity_forgot_password.xml`

#### Cấu trúc:
```xml
LinearLayout (vertical)
    ├─ ImageView (icon khóa)
    ├─ TextView (tiêu đề)
    ├─ TextView (hướng dẫn - thay đổi theo bước)
    │
    ├─ layoutEmailStep (Bước 1)
    │   ├─ EditText (email)
    │   └─ Button (Gửi mã)
    │
    ├─ layoutVerifyStep (Bước 2)
    │   ├─ EditText (mã 6 số)
    │   ├─ EditText (mật khẩu mới)
    │   ├─ EditText (xác nhận mật khẩu)
    │   ├─ Button (Đặt lại mật khẩu)
    │   └─ Button (Gửi lại mã)
    │
    ├─ ProgressBar
    └─ TextView (Quay lại đăng nhập)
```

#### Visibility logic:
- **Mặc định**: `layoutEmailStep` visible, `layoutVerifyStep` gone
- **Sau khi gửi mã**: `layoutEmailStep` gone, `layoutVerifyStep` visible

### 2. Activity - `ForgotPasswordActivity.java`

#### Fields chính:
```java
private String userEmail;          // Email người dùng
private String generatedCode;      // Mã xác thực đã tạo
private String userId;             // Firebase User ID
private CountDownTimer resendTimer; // Timer cho nút gửi lại
```

#### Methods chính:

##### `sendVerificationCode()`
```java
- Validate email
- Kiểm tra email có tồn tại trong Firestore không
- Tạo mã 6 số ngẫu nhiên
- Lưu mã vào Firestore với thời gian hết hạn
- Hiển thị mã (giả lập gửi email)
- Chuyển sang bước nhập mã
```

##### `generateVerificationCode()`
```java
Random random = new Random();
int code = 100000 + random.nextInt(900000); // 100000-999999
return String.valueOf(code);
```

##### `saveVerificationCode()`
```java
Map<String, Object> verificationData = {
    "code": generatedCode,
    "email": userEmail,
    "timestamp": currentTimeMillis,
    "expiryTime": currentTimeMillis + 10 phút
};
db.collection("verification_codes").document(userId).set(data);
```

##### `showVerificationStep()`
```java
- Ẩn layoutEmailStep
- Hiện layoutVerifyStep
- Cập nhật hướng dẫn
- Bắt đầu countdown 60s cho nút "Gửi lại mã"
```

##### `startResendTimer()`
```java
CountDownTimer 60 giây:
    - Disable nút "Gửi lại mã"
    - Hiển thị countdown: "GỬI LẠI MÃ (59s)"
    - Khi hết: Enable lại nút
```

##### `resetPassword()`
```java
- Validate input (mã, mật khẩu mới)
- Gọi verifyCodeAndResetPassword()
```

##### `verifyCodeAndResetPassword()`
```java
1. Lấy mã đã lưu từ Firestore
2. So sánh với mã user nhập vào
3. Kiểm tra thời gian hết hạn
4. Nếu hợp lệ → Gọi changePassword()
5. Nếu không hợp lệ → Hiển thị lỗi
```

##### `changePassword()`
```java
- Gọi Firebase Auth sendPasswordResetEmail()
- Xóa mã xác thực khỏi Firestore
- Hiển thị thông báo thành công
- Đợi 3s rồi quay về màn hình login
```

### 3. Firestore Collection: `verification_codes`

#### Document Structure:
```javascript
{
  "code": "123456",              // String - Mã 6 số
  "email": "user@example.com",   // String - Email người dùng
  "timestamp": 1234567890000,    // Long - Thời điểm tạo mã
  "expiryTime": 1234567890600    // Long - Thời điểm hết hạn (10 phút)
}
```

#### Document ID:
- Sử dụng `userId` làm document ID
- Mỗi user chỉ có 1 mã active tại 1 thời điểm

#### Lifecycle:
```
Tạo mã → Lưu vào Firestore
    ↓
User xác thực thành công → Xóa document
    ↓
Hoặc: Hết hạn 10 phút → Document vẫn tồn tại (cần cleanup)
```

### 4. Security Rules (Firestore)

Khuyến nghị cấu hình:
```javascript
match /verification_codes/{userId} {
  // Chỉ allow read/write cho authenticated users
  allow read, write: if request.auth != null && request.auth.uid == userId;
  
  // Hoặc: Chỉ cho phép từ server-side
  allow read, write: if false; // Use admin SDK only
}
```

## Validation & Error Handling

### Email Step:
| Điều kiện | Lỗi |
|-----------|-----|
| Email trống | "Vui lòng nhập email" |
| Email không hợp lệ | "Email không hợp lệ" |
| Email không tồn tại | "Email không tồn tại trong hệ thống" |

### Verification Step:
| Điều kiện | Lỗi |
|-----------|-----|
| Mã trống | "Vui lòng nhập mã xác thực" |
| Mã != 6 số | "Mã xác thực phải có 6 số" |
| Mã sai | "Mã xác thực không đúng" |
| Mã hết hạn | "Mã xác thực đã hết hạn. Vui lòng gửi lại mã mới." |
| Mật khẩu trống | "Vui lòng nhập mật khẩu mới" |
| Mật khẩu < 6 ký tự | "Mật khẩu phải có ít nhất 6 ký tự" |
| Mật khẩu không khớp | "Mật khẩu xác nhận không khớp" |

## Tính năng đặc biệt

### 1. Countdown Timer cho "Gửi lại mã"
```java
- Sau khi gửi mã, disable nút trong 60 giây
- Hiển thị: "GỬI LẠI MÃ (59s)" → "GỬI LẠI MÃ (58s)" → ...
- Sau 60s: Enable lại nút
- Mục đích: Chống spam
```

### 2. Mã hết hạn sau 10 phút
```java
expiryTime = currentTime + (10 * 60 * 1000); // 10 phút
if (currentTime > expiryTime) {
    // Mã hết hạn
}
```

### 3. Giả lập gửi email (Testing)
```java
Toast.makeText(this, 
    "📧 Mã xác thực đã được gửi!\n\n" +
    "Mã của bạn là: " + code + "\n\n" +
    "(Trong ứng dụng thực tế, mã này sẽ được gửi qua email)", 
    Toast.LENGTH_LONG).show();
```

### 4. Tự động quay về Login sau khi thành công
```java
new Handler().postDelayed(this::finish, 3000); // 3 giây
```

## Hạn chế hiện tại & Cải tiến

### ⚠️ Hạn chế:

1. **Không gửi email thực tế**
   - Hiện tại chỉ hiển thị mã trong Toast
   - Cần tích hợp email service để production

2. **Không thay đổi mật khẩu trực tiếp**
   - Vẫn phải dùng Firebase sendPasswordResetEmail
   - User phải click link trong email để đổi mật khẩu

3. **Không tự động cleanup mã hết hạn**
   - Mã cũ vẫn tồn tại trong Firestore
   - Cần Cloud Function để cleanup

### ✅ Cải tiến đề xuất:

#### 1. Tích hợp gửi email thực
**Option A: Firebase Cloud Functions + Nodemailer**
```javascript
// functions/index.js
const functions = require('firebase-functions');
const nodemailer = require('nodemailer');

exports.sendVerificationEmail = functions.https.onCall(async (data, context) => {
  const { email, code } = data;
  
  let transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
      user: 'your-email@gmail.com',
      pass: 'your-app-password'
    }
  });
  
  await transporter.sendMail({
    from: 'Library App <your-email@gmail.com>',
    to: email,
    subject: 'Mã xác thực đặt lại mật khẩu',
    html: `
      <h2>Mã xác thực của bạn là:</h2>
      <h1 style="color: blue;">${code}</h1>
      <p>Mã này có hiệu lực trong 10 phút.</p>
    `
  });
  
  return { success: true };
});
```

**Option B: SendGrid API**
```java
// Android
implementation 'com.sendgrid:sendgrid-java:4.9.3'

// Code
Email from = new Email("noreply@libraryapp.com");
Email to = new Email(userEmail);
String subject = "Mã xác thực đặt lại mật khẩu";
Content content = new Content("text/html", 
    "<h1>Mã của bạn: " + code + "</h1>");
Mail mail = new Mail(from, subject, to, content);

SendGrid sg = new SendGrid(SENDGRID_API_KEY);
Request request = new Request();
request.setMethod(Method.POST);
request.setEndpoint("mail/send");
request.setBody(mail.build());
sg.api(request);
```

#### 2. Đổi mật khẩu trực tiếp (Custom Implementation)

**Yêu cầu:**
- User phải re-authenticate trước khi đổi mật khẩu
- Hoặc sử dụng Admin SDK từ Cloud Functions

**Giải pháp: Cloud Function**
```javascript
exports.resetPasswordWithCode = functions.https.onCall(async (data, context) => {
  const { email, code, newPassword } = data;
  
  // 1. Verify code
  const codeDoc = await admin.firestore()
    .collection('verification_codes')
    .doc(userId)
    .get();
    
  if (!codeDoc.exists || codeDoc.data().code !== code) {
    throw new functions.https.HttpsError('invalid-argument', 'Mã không hợp lệ');
  }
  
  // 2. Check expiry
  if (Date.now() > codeDoc.data().expiryTime) {
    throw new functions.https.HttpsError('deadline-exceeded', 'Mã đã hết hạn');
  }
  
  // 3. Update password using Admin SDK
  await admin.auth().updateUser(userId, {
    password: newPassword
  });
  
  // 4. Delete used code
  await codeDoc.ref.delete();
  
  return { success: true };
});
```

#### 3. Auto-cleanup mã hết hạn

**Cloud Scheduler + Cloud Function:**
```javascript
exports.cleanupExpiredCodes = functions.pubsub
  .schedule('every 1 hours')
  .onRun(async (context) => {
    const now = Date.now();
    const snapshot = await admin.firestore()
      .collection('verification_codes')
      .where('expiryTime', '<', now)
      .get();
    
    const batch = admin.firestore().batch();
    snapshot.docs.forEach(doc => batch.delete(doc.ref));
    await batch.commit();
    
    console.log(`Deleted ${snapshot.size} expired codes`);
  });
```

#### 4. Rate Limiting (Chống spam)

**Lưu số lần gửi mã:**
```java
// Lưu vào Firestore
Map<String, Object> rateLimitData = new HashMap<>();
rateLimitData.put("email", email);
rateLimitData.put("attemptCount", 1);
rateLimitData.put("lastAttempt", System.currentTimeMillis());
rateLimitData.put("resetTime", System.currentTimeMillis() + (60 * 60 * 1000)); // 1 giờ

// Kiểm tra trước khi gửi
if (attemptCount >= 5 && currentTime < resetTime) {
    Toast.makeText(this, "Bạn đã gửi quá nhiều lần. Vui lòng thử lại sau 1 giờ.", 
        Toast.LENGTH_LONG).show();
    return;
}
```

#### 5. SMS Verification (Thay vì email)

**Sử dụng Firebase Phone Auth:**
```java
PhoneAuthProvider.getInstance().verifyPhoneNumber(
    phoneNumber,
    60, TimeUnit.SECONDS,
    this,
    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // Auto-verify
        }
        
        @Override
        public void onCodeSent(String verificationId, 
                               PhoneAuthProvider.ForceResendingToken token) {
            // Mã đã gửi qua SMS
        }
    }
);
```

## Files đã tạo/sửa

### Created:
1. ✅ `app/src/main/res/layout/activity_forgot_password.xml`
   - Layout 2 bước: Email → Verification
   - Progress bar, countdown timer

2. ✅ `app/src/main/java/com/example/do_an/activities/ForgotPasswordActivity.java`
   - Logic gửi mã, xác thực, đặt lại mật khẩu
   - Countdown timer 60s
   - Email validation

### Modified:
3. ✅ `app/src/main/java/com/example/do_an/activities/LoginActivity.java`
   - Thêm Intent mở ForgotPasswordActivity

4. ✅ `app/src/main/AndroidManifest.xml`
   - Đăng ký ForgotPasswordActivity

## Test Cases

### Test 1: Luồng thành công
- [ ] Nhập email đúng → Gửi mã
- [ ] Kiểm tra Toast hiển thị mã
- [ ] Nhập mã đúng + mật khẩu mới
- [ ] Nhấn "Đặt lại mật khẩu"
- [ ] Kiểm tra email nhận được link reset password
- [ ] Click link → Đặt mật khẩu mới
- [ ] Đăng nhập với mật khẩu mới → Thành công

### Test 2: Email không tồn tại
- [ ] Nhập email chưa đăng ký
- [ ] Nhấn gửi mã
- [ ] Kiểm tra thông báo lỗi: "Email không tồn tại trong hệ thống"

### Test 3: Mã sai
- [ ] Gửi mã thành công
- [ ] Nhập mã sai (VD: 111111)
- [ ] Kiểm tra thông báo: "Mã xác thực không đúng"

### Test 4: Mã hết hạn
- [ ] Gửi mã
- [ ] Đợi > 10 phút
- [ ] Nhập mã (đúng nhưng đã hết hạn)
- [ ] Kiểm tra thông báo: "Mã xác thực đã hết hạn"

### Test 5: Gửi lại mã
- [ ] Gửi mã lần 1
- [ ] Kiểm tra nút "Gửi lại mã" bị disable
- [ ] Đợi countdown hết 60s
- [ ] Nhấn "Gửi lại mã"
- [ ] Kiểm tra mã mới được tạo

### Test 6: Mật khẩu không khớp
- [ ] Nhập mã đúng
- [ ] Mật khẩu mới: "password123"
- [ ] Xác nhận: "password456"
- [ ] Kiểm tra lỗi: "Mật khẩu xác nhận không khớp"

### Test 7: Mật khẩu quá ngắn
- [ ] Nhập mật khẩu < 6 ký tự
- [ ] Kiểm tra lỗi: "Mật khẩu phải có ít nhất 6 ký tự"

### Test 8: Quay lại đăng nhập
- [ ] Nhấn "Quay lại đăng nhập" ở bất kỳ bước nào
- [ ] Kiểm tra quay về LoginActivity

## Security Best Practices

### ✅ Đã implement:
1. ✅ Mã hết hạn sau 10 phút
2. ✅ Mã chỉ dùng được 1 lần (xóa sau khi dùng)
3. ✅ Validate email tồn tại trước khi gửi mã
4. ✅ Countdown 60s giữa các lần gửi
5. ✅ Document ID = userId (chỉ 1 mã active/user)

### ⚠️ Cần thêm:
1. Rate limiting nghiêm ngặt hơn (max 5 lần/giờ)
2. Log attempt history
3. CAPTCHA cho bot detection
4. Two-factor authentication
5. Security questions
6. IP blocking cho suspicious activity

## Tổng kết

### ✅ Đã hoàn thành:
- Giao diện 2 bước đẹp mắt
- Logic gửi mã 6 số ngẫu nhiên
- Lưu trữ mã trong Firestore với expiry
- Xác thực mã và kiểm tra hết hạn
- Countdown timer cho nút "Gửi lại"
- Tích hợp Firebase sendPasswordResetEmail
- Toast thông báo mã (để testing)

### 🚀 Sẵn sàng production sau khi:
1. Tích hợp email service thực tế
2. Deploy Cloud Functions cho reset password trực tiếp
3. Implement rate limiting
4. Add auto-cleanup expired codes
5. Security audit & testing

### 📝 Ghi chú:
- Hiện tại đã đủ để demo và testing
- Mã được hiển thị trong Toast để tiện test
- Production cần gửi email thực qua SMTP/SendGrid/SES


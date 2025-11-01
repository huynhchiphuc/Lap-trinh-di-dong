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
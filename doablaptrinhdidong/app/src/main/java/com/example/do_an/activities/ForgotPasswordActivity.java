package com.example.do_an.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.do_an.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * ForgotPasswordActivity: Màn hình đặt lại mật khẩu (Quên mật khẩu)
 * Chức năng:
 * 1. Người dùng nhập email để nhận mã xác thực
 * 2. Gửi mã xác thực 6 số qua email (sử dụng Cloud Function)
 * 3. Người dùng nhập mã xác thực và mật khẩu mới
 * 4. Xác thực mã và đặt lại mật khẩu
 * 5. Hỗ trợ TEST MODE khi Cloud Function chưa deploy (hiển thị mã trong dialog)
 * 6. Có tính năng gửi lại mã với countdown 60 giây
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    // ============ Khai báo các UI views cho bước 1 (Nhập email) ============
    private EditText edtEmail;                      // Trường nhập email
    private Button btnSendCode;                     // Nút gửi mã xác thực
    private LinearLayout layoutEmailStep;           // Container chứa form nhập email

    // ============ Khai báo các UI views cho bước 2 (Xác thực mã + Đặt mật khẩu) ============
    private EditText edtVerificationCode;           // Trường nhập mã xác thực
    private EditText edtNewPassword;                // Trường nhập mật khẩu mới
    private EditText edtConfirmNewPassword;         // Trường xác nhận mật khẩu mới
    private Button btnResetPassword;                // Nút đặt lại mật khẩu
    private Button btnResendCode;                   // Nút gửi lại mã (với countdown)
    private TextView tvInstruction;                 // Text hướng dẫn
    private LinearLayout layoutVerifyStep;          // Container chứa form xác thực

    // ============ Khai báo các UI views chung ============
    private TextView tvBackToLogin;                 // Liên kết quay lại login
    private ProgressBar progressBar;                // Thanh tiến trình

    // ============ Khai báo Firebase instances ============
    private FirebaseAuth mAuth;                     // Firebase Authentication
    private FirebaseFirestore db;                   // Firestore Database
    private FirebaseFunctions functions;            // Firebase Cloud Functions

    // ============ Khai báo biến để lưu trạng thái ============
    private String userEmail;                       // Email của người dùng
    private String generatedCode;                   // Mã xác thực được tạo
    private String userId;                          // UID của người dùng
    private CountDownTimer resendTimer;             // Timer để đếm ngược lại nút gửi lại

    /**
     * onCreate: Gọi khi Activity được tạo
     * - Khởi tạo Firebase
     * - Ràng buộc các UI views
     * - Thiết lập listeners cho các nút
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // ========== Khởi tạo Firebase Instances ==========
        mAuth = FirebaseAuth.getInstance();           // Khởi tạo Firebase Auth
        db = FirebaseFirestore.getInstance();         // Khởi tạo Firestore DB
        functions = FirebaseFunctions.getInstance();  // Khởi tạo Firebase Cloud Functions

        // ========== Ràng buộc UI Views từ Layout ==========
        edtEmail = findViewById(R.id.edtEmail);
        edtVerificationCode = findViewById(R.id.edtVerificationCode);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnResendCode = findViewById(R.id.btnResendCode);
        tvInstruction = findViewById(R.id.tvInstruction);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        layoutEmailStep = findViewById(R.id.layoutEmailStep);
        layoutVerifyStep = findViewById(R.id.layoutVerifyStep);
        progressBar = findViewById(R.id.progressBar);

        // ========== Thiết lập Listeners cho các nút ==========
        btnSendCode.setOnClickListener(v -> sendVerificationCode());
        btnResetPassword.setOnClickListener(v -> resetPassword());
        btnResendCode.setOnClickListener(v -> resendVerificationCode());
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    /**
     * sendVerificationCode: Gửi mã xác thực qua email
     * Bước 1: Lấy email từ EditText
     * Bước 2: Validate email không trống và định dạng hợp lệ
     * Bước 3: Kiểm tra email có tồn tại trong Firestore không
     * Bước 4: Tạo mã xác thực 6 số ngẫu nhiên
     * Bước 5: Lưu mã vào Firestore
     * Bước 6: Gửi email qua Cloud Function
     */
    private void sendVerificationCode() {
        // ========== Lấy email từ EditText ==========
        userEmail = edtEmail.getText().toString().trim();

        // ========== Kiểm tra Email không trống ==========
        if (TextUtils.isEmpty(userEmail)) {
            edtEmail.setError("Vui lòng nhập email");
            return;
        }

        // ========== Kiểm tra Email có định dạng hợp lệ ==========
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            edtEmail.setError("Email không hợp lệ");
            return;
        }

        // ========== Hiển thị ProgressBar và disable nút ==========
        progressBar.setVisibility(View.VISIBLE);
        btnSendCode.setEnabled(false);

        // ========== Kiểm tra Email tồn tại trong Firestore ==========
        db.collection("users")
                .whereEqualTo("email", userEmail)  // Tìm tài khoản với email này
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // Email không tồn tại
                        progressBar.setVisibility(View.GONE);
                        btnSendCode.setEnabled(true);
                        Toast.makeText(this, "Email không tồn tại trong hệ thống", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ========== Lấy userId từ Firestore ==========
                    DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                    userId = document.getId(); // Lấy document ID (chính là UID)

                    // ========== Tạo mã xác thực 6 số ngẫu nhiên ==========
                    generatedCode = generateVerificationCode();

                    // ========== Lưu mã vào Firestore ==========
                    saveVerificationCode(generatedCode);
                })
                .addOnFailureListener(e -> {
                    // Lỗi khi truy vấn Firestore
                    progressBar.setVisibility(View.GONE);
                    btnSendCode.setEnabled(true);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * generateVerificationCode: Tạo mã xác thực 6 số ngẫu nhiên
     * @return Chuỗi 6 số từ 100000 đến 999999
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Tạo số từ 100000 đến 999999
        return String.valueOf(code);
    }

    /**
     * saveVerificationCode: Lưu mã xác thực vào Firestore
     * Bước 1: Tạo Map chứa mã, email, timestamp, thời gian hết hạn
     * Bước 2: Lưu vào collection "verification_codes" với document ID = userId
     * Bước 3: Gửi email thông qua Cloud Function
     *
     * @param code Mã xác thực
     */
    private void saveVerificationCode(String code) {
        // ========== Tạo dữ liệu xác thực ==========
        Map<String, Object> verificationData = new HashMap<>();
        verificationData.put("code", code);                                        // Mã xác thực
        verificationData.put("email", userEmail);                                  // Email
        verificationData.put("timestamp", System.currentTimeMillis());             // Thời gian tạo
        verificationData.put("expiryTime", System.currentTimeMillis() + (10 * 60 * 1000)); // Hết hạn sau 10 phút

        // ========== Lưu vào Firestore ==========
        db.collection("verification_codes").document(userId)
                .set(verificationData)
                .addOnSuccessListener(aVoid -> {
                    // Lưu thành công - Gửi email
                    sendEmailViaCloudFunction(code);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSendCode.setEnabled(true);
                    Toast.makeText(this, "Lỗi lưu mã: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * sendEmailViaCloudFunction: Gửi email xác thực thông qua Cloud Function
     * Nếu Cloud Function chưa deploy, sẽ hiển thị mã trong dialog (TEST MODE)
     *
     * @param code Mã xác thực cần gửi
     */
    private void sendEmailViaCloudFunction(String code) {
        // ========== Chuẩn bị dữ liệu cho Cloud Function ==========
        Map<String, Object> data = new HashMap<>();
        data.put("email", userEmail);  // Email người nhận
        data.put("code", code);         // Mã xác thực

        // ========== Gọi Cloud Function "sendVerificationCode" ==========
        functions
                .getHttpsCallable("sendVerificationCode")  // Tên Cloud Function
                .call(data)  // Gọi với dữ liệu
                .addOnSuccessListener(result -> {
                    // ===== Gửi email thành công =====
                    progressBar.setVisibility(View.GONE);
                    btnSendCode.setEnabled(true);

                    Toast.makeText(this,
                            "✅ Mã xác thực đã được gửi đến email của bạn!\n\n" +
                            "Vui lòng kiểm tra hộp thư đến (hoặc spam).",
                            Toast.LENGTH_LONG).show();

                    // ===== Chuyển sang bước 2: Xác thực mã =====
                    showVerificationStep();
                })
                .addOnFailureListener(e -> {
                    // ===== Gửi email thất bại (Cloud Function chưa deploy) =====
                    progressBar.setVisibility(View.GONE);
                    btnSendCode.setEnabled(true);

                    String errorMsg = e.getMessage();

                    // ===== Kiểm tra lỗi để hiển thị thích hợp =====
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setTitle("📧 TEST MODE - Mã Xác Thực");

                    if (errorMsg != null && (errorMsg.contains("NOT_FOUND") || errorMsg.contains("UNAUTHENTICATED"))) {
                        // Cloud Function chưa deploy
                        builder.setMessage(
                                "⚠️ Cloud Function chưa được deploy!\n\n" +
                                "📋 Mã xác thực của bạn là:\n\n" +
                                "━━━━━━━━━━━━━━━\n" +
                                "     " + code + "\n" +
                                "━━━━━━━━━━━━━━━\n\n" +
                                "✏️ Vui lòng ghi nhớ hoặc copy mã này.\n\n" +
                                "💡 Để gửi email thật, hãy deploy Cloud Functions theo hướng dẫn trong QUICK_START_EMAIL.md"
                        );
                    } else {
                        // Lỗi khác
                        builder.setMessage(
                                "⚠️ Lỗi kết nối email service\n\n" +
                                "📋 Mã xác thực của bạn là:\n\n" +
                                "━━━━━━━━━━━━━━━\n" +
                                "     " + code + "\n" +
                                "━━━━━━━━━━━━━━━\n\n" +
                                "Lỗi: " + errorMsg
                        );
                    }

                    builder.setPositiveButton("OK, Đã copy mã", (dialog, which) -> {
                        // ===== Copy mã vào clipboard =====
                        android.content.ClipboardManager clipboard =
                            (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData.newPlainText("Verification Code", code);
                        clipboard.setPrimaryClip(clip);

                        Toast.makeText(this, "✅ Đã copy mã: " + code, Toast.LENGTH_SHORT).show();
                        showVerificationStep();
                    });

                    builder.setNegativeButton("Tiếp tục", (dialog, which) -> {
                        showVerificationStep();
                    });

                    builder.setCancelable(false);
                    builder.show();
                });
    }

    /**
     * showVerificationStep: Chuyển từ bước 1 (nhập email) sang bước 2 (xác thực mã + đặt mật khẩu)
     */
    private void showVerificationStep() {
        layoutEmailStep.setVisibility(View.GONE);         // Ẩn form nhập email
        layoutVerifyStep.setVisibility(View.VISIBLE);     // Hiển thị form xác thực mã
        tvInstruction.setText("Nhập mã xác thực đã được gửi đến " + userEmail);

        // Bắt đầu countdown cho nút gửi lại mã
        startResendTimer();
    }

    /**
     * startResendTimer: Bắt đầu countdown 60 giây cho nút "GỬI LẠI MÃ"
     * Nút sẽ bị disable trong 60 giây, sau đó mới có thể gửi lại
     */
    private void startResendTimer() {
        btnResendCode.setEnabled(false);  // Disable nút

        resendTimer = new CountDownTimer(60000, 1000) { // 60 giây, cập nhật mỗi 1 giây
            @Override
            public void onTick(long millisUntilFinished) {
                // Cập nhật text nút với số giây còn lại
                btnResendCode.setText("GỬI LẠI MÃ (" + millisUntilFinished / 1000 + "s)");
            }

            @Override
            public void onFinish() {
                // Countdown hoàn thành - Enable nút lại
                btnResendCode.setText("GỬI LẠI MÃ");
                btnResendCode.setEnabled(true);
            }
        }.start();
    }

    /**
     * resendVerificationCode: Gửi lại mã xác thực
     * Gọi lại sendVerificationCode() để tạo và gửi mã mới
     */
    private void resendVerificationCode() {
        sendVerificationCode();
    }

    /**
     * resetPassword: Xác thực mã xác thực và đặt lại mật khẩu
     * Bước 1: Lấy mã xác thực, mật khẩu mới, xác nhận mật khẩu
     * Bước 2: Validate dữ liệu
     * Bước 3: Xác thực mã
     * Bước 4: Đặt lại mật khẩu
     */
    private void resetPassword() {
        // ========== Lấy dữ liệu từ EditText ==========
        String inputCode = edtVerificationCode.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmNewPassword.getText().toString().trim();

        // ========== Validate Mã xác thực không trống ==========
        if (TextUtils.isEmpty(inputCode)) {
            edtVerificationCode.setError("Vui lòng nhập mã xác thực");
            return;
        }

        // ========== Validate Mã xác thực có đúng 6 ký tự ==========
        if (inputCode.length() != 6) {
            edtVerificationCode.setError("Mã xác thực phải có 6 số");
            return;
        }

        // ========== Validate Mật khẩu mới không trống ==========
        if (TextUtils.isEmpty(newPassword)) {
            edtNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return;
        }

        // ========== Validate Mật khẩu mới ít nhất 6 ký tự ==========
        if (newPassword.length() < 6) {
            edtNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        // ========== Validate Xác nhận mật khẩu khớp ==========
        if (!newPassword.equals(confirmPassword)) {
            edtConfirmNewPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        // ========== Hiển thị ProgressBar ==========
        progressBar.setVisibility(View.VISIBLE);
        btnResetPassword.setEnabled(false);

        // ========== Xác thực mã và đặt lại mật khẩu ==========
        verifyCodeAndResetPassword(inputCode, newPassword);
    }

    /**
     * verifyCodeAndResetPassword: Xác thực mã xác thực từ Firestore
     * Bước 1: Lấy mã được lưu từ Firestore
     * Bước 2: Kiểm tra mã nhập vào có khớp không
     * Bước 3: Kiểm tra mã có hết hạn không
     * Bước 4: Nếu hợp lệ, gọi changePassword để đặt lại mật khẩu
     *
     * @param inputCode Mã xác thực người dùng nhập vào
     * @param newPassword Mật khẩu mới
     */
    private void verifyCodeAndResetPassword(String inputCode, String newPassword) {
        // ========== Truy vấn mã xác thực từ Firestore ==========
        db.collection("verification_codes").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // ===== Kiểm tra document có tồn tại không =====
                    if (!documentSnapshot.exists()) {
                        progressBar.setVisibility(View.GONE);
                        btnResetPassword.setEnabled(true);
                        Toast.makeText(this, "Mã xác thực không tồn tại", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ===== Lấy mã đã lưu và thời gian hết hạn =====
                    String savedCode = documentSnapshot.getString("code");
                    Long expiryTime = documentSnapshot.getLong("expiryTime");

                    // ===== Kiểm tra mã có khớp không =====
                    if (!inputCode.equals(savedCode)) {
                        progressBar.setVisibility(View.GONE);
                        btnResetPassword.setEnabled(true);
                        Toast.makeText(this, "Mã xác thực không đúng", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ===== Kiểm tra mã có hết hạn không =====
                    if (expiryTime != null && System.currentTimeMillis() > expiryTime) {
                        progressBar.setVisibility(View.GONE);
                        btnResetPassword.setEnabled(true);
                        Toast.makeText(this, "Mã xác thực đã hết hạn. Vui lòng gửi lại mã mới.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ===== Mã hợp lệ - Đặt lại mật khẩu =====
                    changePassword(newPassword);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);
                    Toast.makeText(this, "Lỗi xác thực: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * changePassword: Chọn cách đặt lại mật khẩu
     * Có 2 tùy chọn:
     * - Cloud Function (nếu đã deploy) - gọi changePasswordViaCloudFunction
     * - Firebase Email Reset - gọi changePasswordViaEmail (mặc định hiện tại)
     *
     * @param newPassword Mật khẩu mới
     */
    private void changePassword(String newPassword) {
        // Option 1: Sử dụng Cloud Function để đổi mật khẩu trực tiếp (khuyến nghị)
        // Uncomment dòng dưới sau khi deploy Cloud Function "resetPasswordWithCode"
        // changePasswordViaCloudFunction(newPassword);

        // Option 2: Sử dụng Firebase sendPasswordResetEmail (mặc định hiện tại)
        changePasswordViaEmail();
    }

    /**
     * changePasswordViaCloudFunction: Đặt lại mật khẩu thông qua Cloud Function
     * (Sử dụng khi Cloud Function "resetPasswordWithCode" đã được deploy)
     *
     * @param newPassword Mật khẩu mới
     */
    private void changePasswordViaCloudFunction(String newPassword) {
        // ========== Chuẩn bị dữ liệu cho Cloud Function ==========
        Map<String, Object> data = new HashMap<>();
        data.put("email", userEmail);
        data.put("code", edtVerificationCode.getText().toString().trim());
        data.put("newPassword", newPassword);

        // ========== Gọi Cloud Function "resetPasswordWithCode" ==========
        functions
                .getHttpsCallable("resetPasswordWithCode")
                .call(data)
                .addOnSuccessListener(result -> {
                    // ===== Đặt lại mật khẩu thành công =====
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);

                    Toast.makeText(this,
                            "✅ Mật khẩu đã được đặt lại thành công!\n\n" +
                            "Bạn có thể đăng nhập với mật khẩu mới ngay bây giờ.",
                            Toast.LENGTH_LONG).show();

                    // ===== Quay về login sau 2 giây =====
                    new android.os.Handler().postDelayed(this::finish, 2000);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);

                    String errorMessage = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định";
                    Toast.makeText(this, "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                });
    }

    /**
     * changePasswordViaEmail: Đặt lại mật khẩu thông qua Firebase sendPasswordResetEmail
     * Phương pháp này gửi link reset password đến email của người dùng
     * Người dùng sẽ phải click link để đặt lại mật khẩu mới
     */
    private void changePasswordViaEmail() {
        // ========== Gửi link đặt lại mật khẩu ==========
        mAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);

                    if (task.isSuccessful()) {
                        // ===== Gửi email thành công =====
                        // Xóa mã xác thực đã dùng khỏi Firestore
                        db.collection("verification_codes").document(userId).delete();

                        Toast.makeText(this,
                            "✅ Xác thực thành công!\n\n" +
                            "Link đặt lại mật khẩu đã được gửi đến email của bạn.\n" +
                            "Vui lòng kiểm tra email và làm theo hướng dẫn.",
                            Toast.LENGTH_LONG).show();

                        // ===== Đợi 3 giây rồi quay về login =====
                        new android.os.Handler().postDelayed(this::finish, 3000);
                    } else {
                        // ===== Gửi email thất bại =====
                        String errorMessage = task.getException() != null
                            ? task.getException().getMessage()
                            : "Lỗi không xác định";
                        Toast.makeText(this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * onDestroy: Gọi khi Activity bị hủy
     * Hủy timer nếu còn chạy để tránh memory leak
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resendTimer != null) {
            resendTimer.cancel(); // Hủy countdown timer
        }
    }
}


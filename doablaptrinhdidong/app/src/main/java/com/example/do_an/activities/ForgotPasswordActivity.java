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

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail, edtVerificationCode, edtNewPassword, edtConfirmNewPassword;
    private Button btnSendCode, btnResetPassword, btnResendCode;
    private TextView tvInstruction, tvBackToLogin;
    private LinearLayout layoutEmailStep, layoutVerifyStep;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseFunctions functions;

    private String userEmail;
    private String generatedCode;
    private String userId;
    private CountDownTimer resendTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        functions = FirebaseFunctions.getInstance();

        // Initialize views
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

        // Set click listeners
        btnSendCode.setOnClickListener(v -> sendVerificationCode());
        btnResetPassword.setOnClickListener(v -> resetPassword());
        btnResendCode.setOnClickListener(v -> resendVerificationCode());
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void sendVerificationCode() {
        userEmail = edtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            edtEmail.setError("Vui lòng nhập email");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            edtEmail.setError("Email không hợp lệ");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSendCode.setEnabled(false);

        // Kiểm tra xem email có tồn tại trong hệ thống không
        db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        btnSendCode.setEnabled(true);
                        Toast.makeText(this, "Email không tồn tại trong hệ thống", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Lấy userId
                    DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                    userId = document.getId();

                    // Tạo mã xác thực 6 số
                    generatedCode = generateVerificationCode();

                    // Lưu mã vào Firestore với thời gian hết hạn
                    saveVerificationCode(generatedCode);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSendCode.setEnabled(true);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Tạo số từ 100000 đến 999999
        return String.valueOf(code);
    }

    private void saveVerificationCode(String code) {
        Map<String, Object> verificationData = new HashMap<>();
        verificationData.put("code", code);
        verificationData.put("email", userEmail);
        verificationData.put("timestamp", System.currentTimeMillis());
        verificationData.put("expiryTime", System.currentTimeMillis() + (10 * 60 * 1000)); // Hết hạn sau 10 phút

        db.collection("verification_codes").document(userId)
                .set(verificationData)
                .addOnSuccessListener(aVoid -> {
                    // Gửi email thật qua Cloud Function
                    sendEmailViaCloudFunction(code);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSendCode.setEnabled(true);
                    Toast.makeText(this, "Lỗi lưu mã: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendEmailViaCloudFunction(String code) {
        // Chuẩn bị data cho Cloud Function
        Map<String, Object> data = new HashMap<>();
        data.put("email", userEmail);
        data.put("code", code);

        // Gọi Cloud Function
        functions
                .getHttpsCallable("sendVerificationCode")
                .call(data)
                .addOnSuccessListener(result -> {
                    progressBar.setVisibility(View.GONE);
                    btnSendCode.setEnabled(true);

                    Toast.makeText(this,
                            "✅ Mã xác thực đã được gửi đến email của bạn!\n\n" +
                            "Vui lòng kiểm tra hộp thư đến (hoặc spam).",
                            Toast.LENGTH_LONG).show();

                    // Chuyển sang bước 2
                    showVerificationStep();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSendCode.setEnabled(true);

                    // Cloud Function chưa deploy - dùng Toast để hiển thị mã
                    String errorMsg = e.getMessage();

                    // Hiển thị mã trong Toast (TEST MODE)
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setTitle("📧 TEST MODE - Mã Xác Thực");

                    if (errorMsg != null && (errorMsg.contains("NOT_FOUND") || errorMsg.contains("UNAUTHENTICATED"))) {
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
                        // Copy mã vào clipboard
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

    // Backup method: Hiển thị mã trong Toast (cho testing khi chưa có Cloud Function)
    private void simulateSendEmail(String code) {
        Toast.makeText(this,
            "📧 Mã xác thực (TEST MODE)\n\n" +
            "Mã của bạn là: " + code + "\n\n" +
            "(Deploy Cloud Functions để gửi email thật)",
            Toast.LENGTH_LONG).show();
    }

    private void showVerificationStep() {
        layoutEmailStep.setVisibility(View.GONE);
        layoutVerifyStep.setVisibility(View.VISIBLE);
        tvInstruction.setText("Nhập mã xác thực đã được gửi đến " + userEmail);

        // Bắt đầu countdown cho nút gửi lại
        startResendTimer();
    }

    private void startResendTimer() {
        btnResendCode.setEnabled(false);

        resendTimer = new CountDownTimer(60000, 1000) { // 60 giây
            @Override
            public void onTick(long millisUntilFinished) {
                btnResendCode.setText("GỬI LẠI MÃ (" + millisUntilFinished / 1000 + "s)");
            }

            @Override
            public void onFinish() {
                btnResendCode.setText("GỬI LẠI MÃ");
                btnResendCode.setEnabled(true);
            }
        }.start();
    }

    private void resendVerificationCode() {
        sendVerificationCode();
    }

    private void resetPassword() {
        String inputCode = edtVerificationCode.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmNewPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(inputCode)) {
            edtVerificationCode.setError("Vui lòng nhập mã xác thực");
            return;
        }

        if (inputCode.length() != 6) {
            edtVerificationCode.setError("Mã xác thực phải có 6 số");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            edtNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (newPassword.length() < 6) {
            edtNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            edtConfirmNewPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnResetPassword.setEnabled(false);

        // Xác thực mã
        verifyCodeAndResetPassword(inputCode, newPassword);
    }

    private void verifyCodeAndResetPassword(String inputCode, String newPassword) {
        db.collection("verification_codes").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        progressBar.setVisibility(View.GONE);
                        btnResetPassword.setEnabled(true);
                        Toast.makeText(this, "Mã xác thực không tồn tại", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String savedCode = documentSnapshot.getString("code");
                    Long expiryTime = documentSnapshot.getLong("expiryTime");

                    // Kiểm tra mã có đúng không
                    if (!inputCode.equals(savedCode)) {
                        progressBar.setVisibility(View.GONE);
                        btnResetPassword.setEnabled(true);
                        Toast.makeText(this, "Mã xác thực không đúng", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Kiểm tra mã có hết hạn không
                    if (expiryTime != null && System.currentTimeMillis() > expiryTime) {
                        progressBar.setVisibility(View.GONE);
                        btnResetPassword.setEnabled(true);
                        Toast.makeText(this, "Mã xác thực đã hết hạn. Vui lòng gửi lại mã mới.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Mã hợp lệ - Đổi mật khẩu
                    changePassword(newPassword);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);
                    Toast.makeText(this, "Lỗi xác thực: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void changePassword(String newPassword) {
        // Option 1: Sử dụng Cloud Function để đổi mật khẩu trực tiếp (khuyến nghị)
        // Uncomment dòng dưới sau khi deploy Cloud Function "resetPasswordWithCode"
        // changePasswordViaCloudFunction(newPassword);

        // Option 2: Sử dụng Firebase sendPasswordResetEmail (mặc định)
        changePasswordViaEmail();
    }

    private void changePasswordViaCloudFunction(String newPassword) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", userEmail);
        data.put("code", edtVerificationCode.getText().toString().trim());
        data.put("newPassword", newPassword);

        functions
                .getHttpsCallable("resetPasswordWithCode")
                .call(data)
                .addOnSuccessListener(result -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);

                    Toast.makeText(this,
                            "✅ Mật khẩu đã được đặt lại thành công!\n\n" +
                            "Bạn có thể đăng nhập với mật khẩu mới ngay bây giờ.",
                            Toast.LENGTH_LONG).show();

                    // Quay về login sau 2 giây
                    new android.os.Handler().postDelayed(this::finish, 2000);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);

                    String errorMessage = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định";
                    Toast.makeText(this, "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                });
    }

    private void changePasswordViaEmail() {
        // Sử dụng Firebase sendPasswordResetEmail
        // Sau khi xác thực mã thành công, gửi link reset password
        mAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Xóa mã xác thực đã dùng
                        db.collection("verification_codes").document(userId).delete();

                        Toast.makeText(this,
                            "✅ Xác thực thành công!\n\n" +
                            "Link đặt lại mật khẩu đã được gửi đến email của bạn.\n" +
                            "Vui lòng kiểm tra email và làm theo hướng dẫn.",
                            Toast.LENGTH_LONG).show();

                        // Đợi 3 giây rồi quay về login
                        new android.os.Handler().postDelayed(this::finish, 3000);
                    } else {
                        String errorMessage = task.getException() != null
                            ? task.getException().getMessage()
                            : "Lỗi không xác định";
                        Toast.makeText(this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resendTimer != null) {
            resendTimer.cancel();
        }
    }
}


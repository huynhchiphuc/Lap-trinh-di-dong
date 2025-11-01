package com.example.do_an.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail, edtVerificationCode, edtNewPassword, edtConfirmNewPassword;
    private Button btnSendCode, btnResetPassword, btnResendCode;
    private TextView tvInstruction, tvBackToLogin;
    private LinearLayout layoutEmailStep, layoutVerifyStep;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String userEmail;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void sendVerificationCode() {
        userEmail = edtEmail.getText().toString().trim();

        Log.d("ForgotPassword", "========== START FORGOT PASSWORD ==========");
        Log.d("ForgotPassword", "Email entered: " + userEmail);

        if (TextUtils.isEmpty(userEmail)) {
            Log.e("ForgotPassword", "Error: Email is empty!");
            edtEmail.setError("Vui lòng nhập email");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Log.e("ForgotPassword", "Error: Email format invalid: " + userEmail);
            edtEmail.setError("Email không hợp lệ");
            return;
        }

        Log.d("ForgotPassword", "✓ Email validation passed");

        progressBar.setVisibility(View.VISIBLE);
        btnSendCode.setEnabled(false);

        Log.d("ForgotPassword", "Checking email in Firestore...");
        // Kiểm tra xem email có tồn tại trong hệ thống không
        db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("ForgotPassword", "✓ Firestore query success");
                    Log.d("ForgotPassword", "Documents found: " + queryDocumentSnapshots.size());

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.e("ForgotPassword", "Error: Email not found in Firestore!");
                        progressBar.setVisibility(View.GONE);
                        btnSendCode.setEnabled(true);
                        Toast.makeText(this, "❌ Email không tồn tại trong hệ thống", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Lấy userId
                    DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                    userId = document.getId();
                    Log.d("ForgotPassword", "✓ User found. ID: " + userId);

                    Log.d("ForgotPassword", "Calling Firebase sendPasswordResetEmail...");
                    // GỬI EMAIL ĐẶT LẠI MẬT KHẨU TRỰC TIẾP QUA FIREBASE AUTH
                    mAuth.sendPasswordResetEmail(userEmail)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("ForgotPassword", "========================================");
                                Log.d("ForgotPassword", "✅ SUCCESS! Email sent to: " + userEmail);
                                Log.d("ForgotPassword", "Please check inbox and spam folder");
                                Log.d("ForgotPassword", "========================================");

                                progressBar.setVisibility(View.GONE);
                                btnSendCode.setEnabled(true);

                                // Hiển thị dialog thành công
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
                                                "⏰ Link có hiệu lực trong 1 giờ\n\n" +
                                                "💡 Email từ: noreply@librarymanagement-2c326.firebaseapp.com"
                                        )
                                        .setPositiveButton("OK, Đã Hiểu", (dialog, which) -> {
                                            finish(); // Quay về màn hình login
                                        })
                                        .setCancelable(false)
                                        .show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ForgotPassword", "========================================");
                                Log.e("ForgotPassword", "❌ FAILED! Error sending email");
                                Log.e("ForgotPassword", "Error class: " + e.getClass().getName());
                                Log.e("ForgotPassword", "Error message: " + e.getMessage());
                                if (e.getCause() != null) {
                                    Log.e("ForgotPassword", "Error cause: " + e.getCause().getMessage());
                                }
                                Log.e("ForgotPassword", "========================================");

                                progressBar.setVisibility(View.GONE);
                                btnSendCode.setEnabled(true);

                                String errorMessage = e.getMessage();
                                if (errorMessage != null && errorMessage.contains("network")) {
                                    Toast.makeText(this, "❌ Lỗi kết nối mạng. Vui lòng kiểm tra internet!", Toast.LENGTH_LONG).show();
                                } else if (errorMessage != null && errorMessage.contains("DEVELOPER_ERROR")) {
                                    Toast.makeText(this, "❌ Lỗi cấu hình Firebase. Check console!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(this, "❌ Lỗi: " + errorMessage + "\n\nCheck Logcat để biết chi tiết!", Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("ForgotPassword", "❌ Firestore query FAILED!");
                    Log.e("ForgotPassword", "Error: " + e.getMessage());

                    progressBar.setVisibility(View.GONE);
                    btnSendCode.setEnabled(true);
                    Toast.makeText(this, "❌ Lỗi Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}


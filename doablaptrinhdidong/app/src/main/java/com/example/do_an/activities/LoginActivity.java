package com.example.do_an.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.do_an.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * LoginActivity: Màn hình đăng nhập
 * Chức năng:
 * 1. Cho phép người dùng đăng nhập với email và mật khẩu
 * 2. Kiểm tra trạng thái tài khoản (pending, approved, rejected)
 * 3. Kiểm tra vai trò (student/admin) và điều hướng đến màn hình tương ứng
 * 4. Cung cấp liên kết đến RegisterActivity (tạo tài khoản) và ForgotPasswordActivity (quên mật khẩu)
 */
public class LoginActivity extends AppCompatActivity {

    // ============ Khai báo các UI views ============
    private EditText edtEmail;              // Trường nhập email
    private EditText edtPassword;           // Trường nhập mật khẩu
    private Button btnLogin;                // Nút đăng nhập
    private TextView tvRegister;            // Liên kết đến màn hình đăng ký
    private TextView tvForgotPassword;      // Liên kết đến màn hình quên mật khẩu
    private ProgressBar progressBar;        // Thanh tiến trình khi đang đăng nhập

    // ============ Khai báo Firebase instances ============
    private FirebaseAuth mAuth;             // Firebase Authentication
    private FirebaseFirestore db;           // Firestore Database (lưu trữ vai trò và trạng thái)

    /**
     * onCreate: Gọi khi Activity được tạo
     * - Khởi tạo Firebase
     * - Ràng buộc các UI views
     * - Kiểm tra xem người dùng đã đăng nhập hay chưa
     * - Thiết lập listeners cho các nút và liên kết
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ========== Khởi tạo Firebase Instances ==========
        mAuth = FirebaseAuth.getInstance();          // Khởi tạo Firebase Auth
        db = FirebaseFirestore.getInstance();        // Khởi tạo Firestore DB

        // ========== Ràng buộc UI Views từ Layout ==========
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar);

        // ========== Kiểm tra xem người dùng đã đăng nhập chưa ==========
        // Nếu có người dùng hiện tại, kiểm tra vai trò và điều hướng
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserRoleAndNavigate(currentUser.getUid());
        }

        // ========== Thiết lập Listener cho nút Đăng nhập ==========
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(); // Gọi method để xử lý đăng nhập
            }
        });

        // ========== Thiết lập Listener cho liên kết Đăng ký ==========
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở RegisterActivity để người dùng tạo tài khoản mới
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // ========== Thiết lập Listener cho liên kết Quên mật khẩu ==========
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở ForgotPasswordActivity để người dùng đặt lại mật khẩu
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    /**
     * loginUser: Xử lý quá trình đăng nhập
     * Bước 1: Lấy email và mật khẩu từ EditText
     * Bước 2: Kiểm tra email có trống không
     * Bước 3: Kiểm tra mật khẩu có trống không
     * Bước 4: Gọi Firebase signInWithEmailAndPassword để xác thực
     * Bước 5: Nếu thành công, gọi checkUserRoleAndNavigate để kiểm tra vai trò
     * Bước 6: Nếu thất bại, hiển thị thông báo lỗi
     */
    private void loginUser() {
        // ========== Lấy giá trị từ các EditText ==========
        String email = edtEmail.getText().toString().trim();      // Loại bỏ khoảng trắng
        String password = edtPassword.getText().toString().trim();

        // ========== Kiểm tra Email không trống ==========
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email");  // Hiển thị lỗi trên EditText
            return; // Dừng execution nếu email trống
        }

        // ========== Kiểm tra Mật khẩu không trống ==========
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            return; // Dừng execution nếu mật khẩu trống
        }

        // ========== Hiển thị ProgressBar ==========
        progressBar.setVisibility(View.VISIBLE); // Bắt đầu hiển thị loading

        // ========== Gọi Firebase signInWithEmailAndPassword ==========
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE); // Ẩn ProgressBar

                        if (task.isSuccessful()) {
                            // ===== Đăng nhập thành công =====
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Kiểm tra vai trò và trạng thái tài khoản
                                checkUserRoleAndNavigate(user.getUid());
                            }
                        } else {
                            // ===== Đăng nhập thất bại =====
                            // Hiển thị thông báo lỗi từ Firebase (vd: email không tồn tại, mật khẩu sai)
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * checkUserRoleAndNavigate: Kiểm tra vai trò và trạng thái tài khoản, sau đó điều hướng
     * Bước 1: Truy vấn Firestore để lấy thông tin người dùng (role, status)
     * Bước 2: Kiểm tra trạng thái (pending, approved, rejected)
     * Bước 3: Nếu trạng thái là pending hoặc rejected, logout và hiển thị thông báo
     * Bước 4: Nếu trạng thái là approved, điều hướng dựa trên vai trò:
     *         - Nếu admin -> AdminMainActivity
     *         - Nếu student -> StudentMainActivity
     *
     * @param uid UID của người dùng từ Firebase Authentication
     */
    private void checkUserRoleAndNavigate(String uid) {
        // ========== Truy vấn Firestore để lấy thông tin người dùng ==========
        db.collection("users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // ===== Lấy role và status từ Firestore =====
                            String role = task.getResult().getString("role");      // "student" hoặc "admin"
                            String status = task.getResult().getString("status");  // "pending", "approved", "rejected"

                            // ========== Xử lý trạng thái Null ==========
                            // Các tài khoản cũ có thể không có trường status
                            // Gán mặc định là "approved" nếu không có giá trị
                            if (status == null) {
                                status = "approved"; // Tài khoản cũ tự động approved
                            }

                            // ========== Kiểm tra nếu tài khoản chờ duyệt ==========
                            if ("pending".equals(status)) {
                                // Logout người dùng (vì tài khoản chưa được duyệt)
                                mAuth.signOut();

                                // Hiển thị thông báo khác nhau tùy vai trò
                                String message = "admin".equals(role)
                                    ? "Tài khoản Quản lý của bạn đang chờ Super Admin duyệt. Vui lòng thử lại sau!"
                                    : "Tài khoản của bạn đang chờ admin duyệt. Vui lòng thử lại sau!";
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                                return; // Dừng lại, không cho đăng nhập
                            }

                            // ========== Kiểm tra nếu tài khoản bị từ chối ==========
                            if ("rejected".equals(status)) {
                                // Logout người dùng (vì tài khoản bị từ chối)
                                mAuth.signOut();

                                // Hiển thị thông báo khác nhau tùy vai trò
                                String message = "admin".equals(role)
                                    ? "Tài khoản Quản lý của bạn đã bị từ chối. Vui lòng liên hệ Super Admin!"
                                    : "Tài khoản của bạn đã bị từ chối. Vui lòng liên hệ admin!";
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                                return; // Dừng lại, không cho đăng nhập
                            }

                            // ========== Tài khoản đã được duyệt - Cho phép đăng nhập ==========
                            Intent intent;
                            if ("admin".equals(role)) {
                                // Nếu là admin, mở AdminMainActivity
                                intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                            } else {
                                // Nếu là student, mở StudentMainActivity
                                intent = new Intent(LoginActivity.this, StudentMainActivity.class);
                            }

                            // Thiết lập flags để xóa Activity stack (không quay lại màn hình login bằng back button)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish(); // Đóng LoginActivity
                        } else {
                            // Không tìm thấy thông tin người dùng trong Firestore
                            Toast.makeText(LoginActivity.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


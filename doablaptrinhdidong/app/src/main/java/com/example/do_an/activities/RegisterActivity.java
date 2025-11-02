package com.example.do_an.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.do_an.R;
import com.example.do_an.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * RegisterActivity: Màn hình đăng ký tài khoản
 * Chức năng:
 * 1. Cho phép người dùng tạo tài khoản mới với email, mật khẩu, tên và chọn vai trò
 * 2. Validate dữ liệu (tên, email, mật khẩu, xác nhận mật khẩu, vai trò)
 * 3. Tạo tài khoản trên Firebase Authentication
 * 4. Lưu thông tin người dùng vào Firestore Database
 * 5. Tất cả tài khoản mới được thiết lập trạng thái "pending" (chờ duyệt)
 * 6. Logout ngay sau khi đăng ký để bắt buộc chờ duyệt
 */
public class RegisterActivity extends AppCompatActivity {

    // ============ Khai báo các UI views ============
    private EditText edtName;               // Trường nhập tên đầy đủ
    private EditText edtEmail;              // Trường nhập email
    private EditText edtPassword;           // Trường nhập mật khẩu
    private EditText edtConfirmPassword;    // Trường xác nhận mật khẩu
    private RadioGroup rgRole;              // Group radio button để chọn vai trò (Student/Admin)
    private Button btnRegister;             // Nút đăng ký
    private TextView tvLogin;               // Liên kết quay lại màn hình login
    private ProgressBar progressBar;        // Thanh tiến trình khi đang đăng ký

    // ============ Khai báo Firebase instances ============
    private FirebaseAuth mAuth;             // Firebase Authentication
    private FirebaseFirestore db;           // Firestore Database (lưu trữ thông tin người dùng)

    /**
     * onCreate: Gọi khi Activity được tạo
     * - Khởi tạo Firebase
     * - Ràng buộc các UI views
     * - Thiết lập listeners cho các nút và liên kết
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // ========== Khởi tạo Firebase Instances ==========
        mAuth = FirebaseAuth.getInstance();          // Khởi tạo Firebase Auth
        db = FirebaseFirestore.getInstance();        // Khởi tạo Firestore DB

        // ========== Ràng buộc UI Views từ Layout ==========
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        rgRole = findViewById(R.id.rgRole);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);

        // ========== Thiết lập Listener cho nút Đăng ký ==========
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(); // Gọi method để xử lý đăng ký
            }
        });

        // ========== Thiết lập Listener cho liên kết Quay lại Login ==========
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng Activity để quay lại LoginActivity
            }
        });
    }

    /**
     * registerUser: Xử lý quá trình đăng ký tài khoản
     * Bước 1: Lấy tất cả dữ liệu từ các EditText và RadioGroup
     * Bước 2: Validate tên không trống
     * Bước 3: Validate email không trống
     * Bước 4: Validate mật khẩu không trống
     * Bước 5: Validate mật khẩu ít nhất 6 ký tự
     * Bước 6: Validate xác nhận mật khẩu khớp với mật khẩu
     * Bước 7: Validate vai trò được chọn
     * Bước 8: Tạo tài khoản trên Firebase Auth
     * Bước 9: Lưu thông tin vào Firestore
     * Bước 10: Logout ngay lập tức để bắt buộc chờ duyệt
     */
    private void registerUser() {
        // ========== Lấy giá trị từ các EditText ==========
        String name = edtName.getText().toString().trim();                      // Loại bỏ khoảng trắng
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // ========== Kiểm tra Tên không trống ==========
        if (TextUtils.isEmpty(name)) {
            edtName.setError("Vui lòng nhập họ tên");
            return; // Dừng execution nếu tên trống
        }

        // ========== Kiểm tra Email không trống ==========
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email");
            return; // Dừng execution nếu email trống
        }

        // ========== Kiểm tra Mật khẩu không trống ==========
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            return; // Dừng execution nếu mật khẩu trống
        }

        // ========== Kiểm tra Mật khẩu ít nhất 6 ký tự ==========
        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return; // Dừng execution nếu mật khẩu quá ngắn
        }

        // ========== Kiểm tra Xác nhận mật khẩu khớp ==========
        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return; // Dừng execution nếu mật khẩu xác nhận không khớp
        }

        // ========== Kiểm tra vai trò được chọn ==========
        int selectedRoleId = rgRole.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            // Không có radio button nào được chọn
            Toast.makeText(this, "Vui lòng chọn vai trò", Toast.LENGTH_SHORT).show();
            return; // Dừng execution nếu chưa chọn vai trò
        }

        // ========== Lấy vai trò từ radio button được chọn ==========
        RadioButton rbSelectedRole = findViewById(selectedRoleId);
        // Kiểm tra text của radio button: "Sinh viên" -> "student", ngược lại -> "admin"
        String role = rbSelectedRole.getText().toString().equals("Sinh viên") ? "student" : "admin";

        // ========== Hiển thị ProgressBar ==========
        progressBar.setVisibility(View.VISIBLE); // Bắt đầu hiển thị loading

        // ========== Gọi Firebase createUserWithEmailAndPassword ==========
        // Tạo tài khoản trong Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // ===== Tạo tài khoản thành công =====
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid(); // Lấy UID từ Firebase Auth

                                // ===== Tất cả tài khoản mới được thiết lập trạng thái "pending" =====
                                // Điều này buộc user phải chờ admin/SuperAdmin duyệt trước khi có thể sử dụng
                                String status = "pending";

                                // ===== Tạo đối tượng User =====
                                User user = new User(uid, name, email, role, status);

                                // ========== Lưu thông tin người dùng vào Firestore ==========
                                db.collection("users").document(uid)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // ===== Lưu thành công =====
                                                progressBar.setVisibility(View.GONE);

                                                // ===== Logout ngay lập tức =====
                                                // Điều này bắt buộc user không được login cho đến khi được duyệt
                                                mAuth.signOut();

                                                // ===== Hiển thị thông báo thành công =====
                                                String message = role.equals("admin")
                                                    ? "Đăng ký tài khoản Quản lý thành công! Vui lòng chờ Super Admin duyệt tài khoản."
                                                    : "Đăng ký thành công! Vui lòng chờ admin duyệt tài khoản.";
                                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                                                // ===== Đóng RegisterActivity =====
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // ===== Lỗi khi lưu vào Firestore =====
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(RegisterActivity.this, "Lỗi lưu thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // ===== Tạo tài khoản thất bại =====
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


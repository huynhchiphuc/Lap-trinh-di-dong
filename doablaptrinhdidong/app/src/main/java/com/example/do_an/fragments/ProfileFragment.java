package com.example.do_an.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.do_an.R;
import com.example.do_an.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * ProfileFragment: Fragment hiển thị hồ sơ cá nhân người dùng
 * Chức năng:
 * 1. Hiển thị thông tin: tên, email, vai trò (sinh viên/quản trị viên)
 * 2. Nút Logout: đăng xuất và quay về LoginActivity
 */
public class ProfileFragment extends Fragment {

    // ============ Khai báo các UI views ============
    private TextView tvName;                 // Hiển thị tên người dùng
    private TextView tvEmail;                // Hiển thị email
    private TextView tvRole;                 // Hiển thị vai trò
    private Button btnLogout;                // Nút đăng xuất

    // ============ Khai báo Firebase instances ============
    private FirebaseAuth mAuth;              // Firebase Authentication
    private FirebaseFirestore db;            // Firestore Database

    /**
     * onCreateView: Gọi khi Fragment được tạo
     * - Khởi tạo Firebase
     * - Ràng buộc các UI views
     * - Load thông tin người dùng
     * - Setup listener cho nút Logout
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // ========== Khởi tạo Firebase ==========
        mAuth = FirebaseAuth.getInstance();    // Khởi tạo Firebase Auth
        db = FirebaseFirestore.getInstance();  // Khởi tạo Firestore DB

        // ========== Ràng buộc UI views ==========
        tvName = view.findViewById(R.id.tvName);    // TextView tên
        tvEmail = view.findViewById(R.id.tvEmail);  // TextView email
        tvRole = view.findViewById(R.id.tvRole);    // TextView vai trò
        btnLogout = view.findViewById(R.id.btnLogout);  // Nút logout

        // ========== Load thông tin người dùng ==========
        loadUserInfo();

        // ========== Thiết lập listener cho nút Logout ==========
        btnLogout.setOnClickListener(v -> {
            // ===== Đăng xuất =====
            mAuth.signOut();

            // ===== Quay về LoginActivity =====
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            // FLAG_ACTIVITY_NEW_TASK: Tạo activity mới
            // FLAG_ACTIVITY_CLEAR_TASK: Xóa activity stack
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    /**
     * loadUserInfo: Tải thông tin người dùng từ Firestore
     * Bước 1: Kiểm tra người dùng đã login
     * Bước 2: Lấy UID từ Firebase Auth
     * Bước 3: Truy vấn document người dùng từ Firestore
     * Bước 4: Hiển thị thông tin (tên, email, vai trò)
     * Bước 5: Convert role "admin" thành "Quản trị viên", còn lại "Sinh viên"
     */
    private void loadUserInfo() {
        // ========== Kiểm tra người dùng đã login ==========
        if (mAuth.getCurrentUser() == null) {
            // Chưa login - hiển thị "---"
            tvName.setText("---");
            tvEmail.setText("---");
            tvRole.setText("---");
            return;
        }

        // ========== Lấy UID người dùng ==========
        String userId = mAuth.getCurrentUser().getUid();

        // ========== Truy vấn thông tin từ Firestore ==========
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // ===== Kiểm tra document có tồn tại =====
                    if (documentSnapshot.exists()) {
                        // ===== Lấy dữ liệu từ document =====
                        String name = documentSnapshot.getString("name");         // Tên
                        String email = documentSnapshot.getString("email");       // Email
                        String role = documentSnapshot.getString("role");         // Vai trò

                        // ===== Hiển thị thông tin =====
                        tvName.setText(name != null ? name : "---");
                        tvEmail.setText(email != null ? email : "---");

                        // ===== Convert role =====
                        if (role != null && role.equals("admin")) {
                            tvRole.setText("Quản trị viên");
                        } else {
                            tvRole.setText("Sinh viên");
                        }
                    } else {
                        // Document không tồn tại - hiển thị "---"
                        tvName.setText("---");
                        tvEmail.setText("---");
                        tvRole.setText("---");
                    }
                })
                .addOnFailureListener(e -> {
                    // ===== Lỗi khi tải =====
                    android.widget.Toast.makeText(getContext(), "Lỗi tải thông tin: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();

                    // Hiển thị "---" nếu lỗi
                    tvName.setText("---");
                    tvEmail.setText("---");
                    tvRole.setText("---");
                });
    }
}


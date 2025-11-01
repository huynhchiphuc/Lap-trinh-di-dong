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

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvRole;
    private Button btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRole = view.findViewById(R.id.tvRole);
        btnLogout = view.findViewById(R.id.btnLogout);

        loadUserInfo();

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserInfo() {
        // Kiểm tra user đã đăng nhập chưa
        if (mAuth.getCurrentUser() == null) {
            tvName.setText("---");
            tvEmail.setText("---");
            tvRole.setText("---");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String role = documentSnapshot.getString("role");

                        tvName.setText(name != null ? name : "---");
                        tvEmail.setText(email != null ? email : "---");
                        tvRole.setText(role != null && role.equals("admin") ? "Quản trị viên" : "Sinh viên");
                    } else {
                        tvName.setText("---");
                        tvEmail.setText("---");
                        tvRole.setText("---");
                    }
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast.makeText(getContext(), "Lỗi tải thông tin: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    tvName.setText("---");
                    tvEmail.setText("---");
                    tvRole.setText("---");
                });
    }
}


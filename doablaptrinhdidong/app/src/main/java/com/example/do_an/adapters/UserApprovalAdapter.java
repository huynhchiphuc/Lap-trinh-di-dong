package com.example.do_an.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.do_an.R;
import com.example.do_an.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserApprovalAdapter extends RecyclerView.Adapter<UserApprovalAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private FirebaseFirestore db;
    private Runnable onDataChanged;

    public UserApprovalAdapter(Context context, List<User> userList, Runnable onDataChanged) {
        this.context = context;
        this.userList = userList;
        this.db = FirebaseFirestore.getInstance();
        this.onDataChanged = onDataChanged;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_approval, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvRole.setText(user.isAdmin() ? "Quản trị viên" : "Sinh viên");

        holder.btnApprove.setOnClickListener(v -> showApproveDialog(user));
        holder.btnReject.setOnClickListener(v -> showRejectDialog(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void showApproveDialog(User user) {
        new AlertDialog.Builder(context)
                .setTitle("Duyệt tài khoản")
                .setMessage("Bạn có muốn duyệt tài khoản của " + user.getName() + " không?")
                .setPositiveButton("Duyệt", (dialog, which) -> approveUser(user))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showRejectDialog(User user) {
        new AlertDialog.Builder(context)
                .setTitle("Từ chối tài khoản")
                .setMessage("Bạn có muốn từ chối tài khoản của " + user.getName() + " không?")
                .setPositiveButton("Từ chối", (dialog, which) -> rejectUser(user))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void approveUser(User user) {
        db.collection("users").document(user.getUid())
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Đã duyệt tài khoản!", Toast.LENGTH_SHORT).show();
                    if (onDataChanged != null) {
                        onDataChanged.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void rejectUser(User user) {
        db.collection("users").document(user.getUid())
                .update("status", "rejected")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Đã từ chối tài khoản!", Toast.LENGTH_SHORT).show();
                    if (onDataChanged != null) {
                        onDataChanged.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole;
        Button btnApprove, btnReject;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}


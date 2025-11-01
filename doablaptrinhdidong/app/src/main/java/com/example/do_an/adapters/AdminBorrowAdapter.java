package com.example.do_an.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.do_an.R;
import com.example.do_an.models.Borrow;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminBorrowAdapter extends RecyclerView.Adapter<AdminBorrowAdapter.AdminBorrowViewHolder> {

    private Context context;
    private List<Borrow> borrowList;
    private SimpleDateFormat dateFormat;
    private Runnable onDataChanged;

    public AdminBorrowAdapter(Context context, List<Borrow> borrowList, Runnable onDataChanged) {
        this.context = context;
        this.borrowList = borrowList;
        this.onDataChanged = onDataChanged;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public AdminBorrowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_borrow, parent, false);
        return new AdminBorrowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBorrowViewHolder holder, int position) {
        Borrow borrow = borrowList.get(position);

        holder.tvUserName.setText("Người mượn: " + borrow.getUserName());
        holder.tvBookTitle.setText("Sách: " + borrow.getBookTitle());
        holder.tvBorrowDate.setText("Ngày mượn: " + dateFormat.format(borrow.getBorrowDate().toDate()));
        holder.tvDueDate.setText("Hạn trả: " + dateFormat.format(borrow.getDueDate().toDate()));
        holder.tvStatus.setText("Trạng thái: " + borrow.getStatus());

        // Màu status
        if (borrow.getStatus().equals("Đang mượn")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else if (borrow.getStatus().equals("Đã trả")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        if (borrow.getReturnDate() != null) {
            holder.tvReturnDate.setVisibility(View.VISIBLE);
            holder.tvReturnDate.setText("Ngày trả: " + dateFormat.format(borrow.getReturnDate().toDate()));
        } else {
            holder.tvReturnDate.setVisibility(View.GONE);
        }

        // Hiện nút duyệt/từ chối nếu status = Chờ duyệt
        if (borrow.getStatus().equals("Chờ duyệt")) {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
            holder.btnApprove.setOnClickListener(v -> showApproveDialog(borrow, position));
            holder.btnReject.setOnClickListener(v -> showRejectDialog(borrow, position));
        } else {
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return borrowList.size();
    }

    private void showApproveDialog(Borrow borrow, int position) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Duyệt yêu cầu mượn sách")
                .setMessage("Bạn có muốn duyệt yêu cầu mượn sách \"" + borrow.getBookTitle() + "\" của " + borrow.getUserName() + " không?")
                .setPositiveButton("Duyệt", (dialog, which) -> approveBorrow(borrow, position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showRejectDialog(Borrow borrow, int position) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Từ chối yêu cầu")
                .setMessage("Bạn có muốn từ chối yêu cầu mượn sách \"" + borrow.getBookTitle() + "\" của " + borrow.getUserName() + " không?")
                .setPositiveButton("Từ chối", (dialog, which) -> rejectBorrow(borrow, position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void approveBorrow(Borrow borrow, int position) {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        // Cập nhật status = Đang mượn
        db.collection("borrows").document(borrow.getBorrowId())
                .update("status", "Đang mượn")
                .addOnSuccessListener(aVoid -> {
                    // Giảm quantity sách
                    db.collection("books").document(borrow.getBookId()).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Long quantityLong = documentSnapshot.getLong("quantity");
                                    if (quantityLong != null && quantityLong > 0) {
                                        db.collection("books").document(borrow.getBookId())
                                                .update("quantity", quantityLong - 1)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    android.widget.Toast.makeText(context, "Đã duyệt yêu cầu mượn sách!", android.widget.Toast.LENGTH_SHORT).show();
                                                    borrow.setStatus("Đang mượn");
                                                    notifyItemChanged(position);
                                                    if (onDataChanged != null) {
                                                        onDataChanged.run();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    android.widget.Toast.makeText(context, "Lỗi cập nhật số lượng: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        android.widget.Toast.makeText(context, "Sách đã hết!", android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast.makeText(context, "Lỗi: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                });
    }

    private void rejectBorrow(Borrow borrow, int position) {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        db.collection("borrows").document(borrow.getBorrowId())
                .update("status", "Từ chối")
                .addOnSuccessListener(aVoid -> {
                    android.widget.Toast.makeText(context, "Đã từ chối yêu cầu!", android.widget.Toast.LENGTH_SHORT).show();
                    borrow.setStatus("Từ chối");
                    notifyItemChanged(position);
                    if (onDataChanged != null) {
                        onDataChanged.run();
                    }
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast.makeText(context, "Lỗi: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                });
    }

    static class AdminBorrowViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvBookTitle, tvBorrowDate, tvDueDate, tvReturnDate, tvStatus;
        android.widget.Button btnApprove, btnReject;

        public AdminBorrowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBorrowDate = itemView.findViewById(R.id.tvBorrowDate);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvReturnDate = itemView.findViewById(R.id.tvReturnDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}


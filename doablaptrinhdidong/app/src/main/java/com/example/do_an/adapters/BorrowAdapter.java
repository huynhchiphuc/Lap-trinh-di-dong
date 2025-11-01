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
import com.example.do_an.models.Borrow;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BorrowAdapter extends RecyclerView.Adapter<BorrowAdapter.BorrowViewHolder> {

    private Context context;
    private List<Borrow> borrowList;
    private FirebaseFirestore db;
    private SimpleDateFormat dateFormat;

    public BorrowAdapter(Context context, List<Borrow> borrowList) {
        this.context = context;
        this.borrowList = borrowList;
        this.db = FirebaseFirestore.getInstance();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public BorrowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_borrow, parent, false);
        return new BorrowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowViewHolder holder, int position) {
        Borrow borrow = borrowList.get(position);

        holder.tvBookTitle.setText(borrow.getBookTitle());
        holder.tvBorrowDate.setText("Ngày mượn: " + dateFormat.format(borrow.getBorrowDate().toDate()));
        holder.tvDueDate.setText("Hạn trả: " + dateFormat.format(borrow.getDueDate().toDate()));
        holder.tvStatus.setText("Trạng thái: " + borrow.getStatus());

        // Màu status
        if (borrow.getStatus().equals("Chờ duyệt")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light));
        } else if (borrow.getStatus().equals("Đang mượn") || borrow.getStatus().equals("Đã duyệt")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
        } else if (borrow.getStatus().equals("Đã trả")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if (borrow.getStatus().equals("Từ chối")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        // Hiện nút trả sách nếu đang mượn (đã được duyệt)
        if (borrow.getStatus().equals("Đang mượn") || borrow.getStatus().equals("Đã duyệt")) {
            holder.btnReturn.setVisibility(View.VISIBLE);
            holder.btnReturn.setOnClickListener(v -> showReturnConfirmDialog(borrow, position));
        } else {
            holder.btnReturn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return borrowList.size();
    }

    private void showReturnConfirmDialog(Borrow borrow, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận trả sách")
                .setMessage("Bạn có muốn trả sách \"" + borrow.getBookTitle() + "\" không?")
                .setPositiveButton("Trả", (dialog, which) -> returnBook(borrow, position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void returnBook(Borrow borrow, int position) {
        // Cập nhật borrow record
        db.collection("borrows").document(borrow.getBorrowId())
                .update(
                        "returnDate", Timestamp.now(),
                        "status", "Đã trả"
                )
                .addOnSuccessListener(aVoid -> {
                    // Tăng số lượng sách
                    db.collection("books").document(borrow.getBookId()).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Long quantityLong = documentSnapshot.getLong("quantity");
                                    if (quantityLong != null) {
                                        int currentQuantity = quantityLong.intValue();
                                        db.collection("books").document(borrow.getBookId())
                                                .update("quantity", currentQuantity + 1)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    Toast.makeText(context, "Trả sách thành công!", Toast.LENGTH_SHORT).show();
                                                    borrow.setStatus("Đã trả");
                                                    borrow.setReturnDate(Timestamp.now());
                                                    notifyItemChanged(position);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(context, "Lỗi cập nhật số lượng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(context, "Lỗi: Không tìm thấy số lượng sách", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Lỗi: Không tìm thấy sách", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Lỗi tải thông tin sách: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi trả sách: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    static class BorrowViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookTitle, tvBorrowDate, tvDueDate, tvStatus;
        Button btnReturn;

        public BorrowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBorrowDate = itemView.findViewById(R.id.tvBorrowDate);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnReturn = itemView.findViewById(R.id.btnReturn);
        }
    }
}


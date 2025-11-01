package com.example.do_an.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.do_an.R;
import com.example.do_an.models.Book;
import com.example.do_an.models.Borrow;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public BookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText("Tác giả: " + book.getAuthor());
        holder.tvCategory.setText("Thể loại: " + book.getCategory());
        holder.tvQuantity.setText("Còn lại: " + book.getQuantity());

        // Load image với Glide
        Glide.with(context)
                .load(book.getImageUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imgBook);

        holder.btnBorrow.setOnClickListener(v -> {
            if (book.getQuantity() > 0) {
                showBorrowConfirmDialog(book);
            } else {
                Toast.makeText(context, "Sách đã hết!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            showBookDetailsDialog(book);
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    private void showBorrowConfirmDialog(Book book) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận mượn sách")
                .setMessage("Bạn có muốn mượn sách \"" + book.getTitle() + "\" không?")
                .setPositiveButton("Mượn", (dialog, which) -> borrowBook(book))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void borrowBook(Book book) {
        // Kiểm tra user đã đăng nhập chưa
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(context, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra book có đầy đủ thông tin không
        if (book == null || book.getId() == null || book.getId().isEmpty()) {
            Toast.makeText(context, "Lỗi: Thông tin sách không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            Toast.makeText(context, "Lỗi: Sách không có tên!", Toast.LENGTH_SHORT).show();
            return;
        }

        final String userId = mAuth.getCurrentUser().getUid();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(context, "Lỗi: Không xác định được người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thông tin user
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = "Người dùng"; // default value
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        if (name != null && !name.isEmpty()) {
                            userName = name;
                        }
                    }
                    final String finalUserName = userName; // Make effectively final for lambda

                    // Tạo borrow record với ID an toàn
                    String borrowId;
                    try {
                        borrowId = db.collection("borrows").document().getId();
                        if (borrowId == null || borrowId.isEmpty()) {
                            borrowId = "B_" + UUID.randomUUID().toString();
                        }
                    } catch (Exception ex) {
                        borrowId = "B_" + UUID.randomUUID().toString();
                    }
                    final String finalBorrowId = borrowId; // Make effectively final

                    Calendar calendar = Calendar.getInstance();
                    Timestamp borrowDate = Timestamp.now();

                    calendar.add(Calendar.DAY_OF_MONTH, 14); // 14 ngày mượn
                    Timestamp dueDate = new Timestamp(calendar.getTime());

                    Borrow borrow = new Borrow(
                            finalBorrowId,
                            userId,
                            finalUserName,
                            book.getId(),
                            book.getTitle(),
                            borrowDate,
                            dueDate,
                            null,
                            "Chờ duyệt" // Trạng thái chờ admin duyệt
                    );

                    // Lưu borrow record
                    try {
                        db.collection("borrows").document(finalBorrowId).set(borrow)
                                .addOnSuccessListener(aVoid -> {
                                    // KHÔNG giảm quantity ngay, đợi admin duyệt
                                    Toast.makeText(context, "✅ Đã gửi yêu cầu mượn sách!\n\nVui lòng chờ admin duyệt.", Toast.LENGTH_LONG).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "❌ Lỗi mượn sách: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } catch (IllegalArgumentException iae) {
                        // document(...) nhận null hoặc chuỗi rỗng
                        Toast.makeText(context, "❌ Lỗi nội bộ: không thể tạo yêu cầu mượn (ID không hợp lệ).", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "❌ Lỗi mượn sách: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi tải thông tin user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showBookDetailsDialog(Book book) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(book.getTitle());
        builder.setMessage(
                "Tác giả: " + book.getAuthor() + "\n\n" +
                "Thể loại: " + book.getCategory() + "\n\n" +
                "Mô tả: " + book.getDescription() + "\n\n" +
                "Số lượng còn lại: " + book.getQuantity()
        );
        builder.setPositiveButton("Đóng", null);
        builder.show();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBook;
        TextView tvTitle, tvAuthor, tvCategory, tvQuantity;
        Button btnBorrow;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgBook);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnBorrow = itemView.findViewById(R.id.btnBorrow);
        }
    }
}

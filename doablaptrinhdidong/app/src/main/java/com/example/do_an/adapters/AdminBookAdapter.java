package com.example.do_an.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.do_an.R;
import com.example.do_an.models.Book;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminBookAdapter extends RecyclerView.Adapter<AdminBookAdapter.AdminBookViewHolder> {

    private Context context;
    private List<Book> bookList;
    private FirebaseFirestore db;
    private Runnable onDataChanged;

    public AdminBookAdapter(Context context, List<Book> bookList, Runnable onDataChanged) {
        this.context = context;
        this.bookList = bookList;
        this.db = FirebaseFirestore.getInstance();
        this.onDataChanged = onDataChanged;
    }

    @NonNull
    @Override
    public AdminBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_book, parent, false);
        return new AdminBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText("Tác giả: " + book.getAuthor());
        holder.tvCategory.setText("Thể loại: " + book.getCategory());
        holder.tvQuantity.setText("Số lượng: " + book.getQuantity());

        Glide.with(context)
                .load(book.getImageUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imgBook);

        holder.btnEdit.setOnClickListener(v -> {
            // Mở EditBookActivity
            android.content.Intent intent = new android.content.Intent(context, com.example.do_an.activities.EditBookActivity.class);
            intent.putExtra("bookId", book.getId());
            intent.putExtra("title", book.getTitle());
            intent.putExtra("author", book.getAuthor());
            intent.putExtra("category", book.getCategory());
            intent.putExtra("quantity", book.getQuantity());
            intent.putExtra("imageUrl", book.getImageUrl());
            intent.putExtra("description", book.getDescription());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            showDeleteConfirmDialog(book, position);
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    private void showDeleteConfirmDialog(Book book, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa sách \"" + book.getTitle() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteBook(book, position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteBook(Book book, int position) {
        if (book == null || book.getId() == null || book.getId().isEmpty()) {
            Toast.makeText(context, "ID sách không hợp lệ, không thể xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("books").document(book.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Xóa sách thành công!", Toast.LENGTH_SHORT).show();
                    bookList.remove(position);
                    notifyItemRemoved(position);
                    // Nếu có callback để reload dữ liệu, gọi nó để đảm bảo trạng thái nhất quán
                    if (onDataChanged != null) {
                        try {
                            onDataChanged.run();
                        } catch (Exception ignored) {
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    static class AdminBookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBook;
        TextView tvTitle, tvAuthor, tvCategory, tvQuantity;
        ImageButton btnEdit, btnDelete;

        public AdminBookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgBook);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

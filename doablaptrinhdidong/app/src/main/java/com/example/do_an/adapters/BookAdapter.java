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

/**
 * BookAdapter: Adapter để hiển thị danh sách sách trong RecyclerView
 * Chức năng:
 * 1. Hiển thị thông tin sách (ảnh, tên, tác giả, thể loại, số lượng)
 * 2. Xử lý sự kiện mượn sách
 * 3. Hiển thị chi tiết sách khi click
 * 4. Tạo yêu cầu mượn và lưu vào Firestore
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    // ============ Khai báo biến ============
    private Context context;                // Context của Activity/Fragment
    private List<Book> bookList;            // Danh sách sách
    private FirebaseFirestore db;           // Firestore Database
    private FirebaseAuth mAuth;             // Firebase Authentication

    /**
     * Constructor: Khởi tạo BookAdapter
     * @param context Context từ Activity
     * @param bookList Danh sách sách cần hiển thị
     */
    public BookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    /**
     * onCreateViewHolder: Tạo ViewHolder cho mỗi item
     * @param parent ViewGroup chứa items
     * @param viewType Loại view
     * @return BookViewHolder mới
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ========== Inflate layout item_book ==========
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    /**
     * onBindViewHolder: Gắn dữ liệu sách vào ViewHolder
     * @param holder ViewHolder cần gắn dữ liệu
     * @param position Vị trí item trong danh sách
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        // ========== Lấy sách tại vị trí hiện tại ==========
        Book book = bookList.get(position);

        // ========== Gắn dữ liệu vào các TextView ==========
        holder.tvTitle.setText(book.getTitle());                           // Tên sách
        holder.tvAuthor.setText("Tác giả: " + book.getAuthor());          // Tác giả
        holder.tvCategory.setText("Thể loại: " + book.getCategory());     // Thể loại
        holder.tvQuantity.setText("Còn lại: " + book.getQuantity());      // Số lượng còn

        // ========== Load ảnh bìa sách bằng Glide ==========
        Glide.with(context)
                .load(book.getImageUrl())
                .placeholder(R.drawable.ic_launcher_foreground)  // Ảnh placeholder khi đang tải
                .into(holder.imgBook);

        // ========== Thiết lập listener cho nút Mượn ==========
        holder.btnBorrow.setOnClickListener(v -> {
            // Kiểm tra xem còn sách không
            if (book.getQuantity() > 0) {
                // Có sách - hiển thị dialog xác nhận
                showBorrowConfirmDialog(book);
            } else {
                // Hết sách - hiển thị thông báo
                Toast.makeText(context, "Sách đã hết!", Toast.LENGTH_SHORT).show();
            }
        });

        // ========== Thiết lập listener cho click item để xem chi tiết ==========
        holder.itemView.setOnClickListener(v -> {
            // Hiển thị dialog chi tiết sách
            showBookDetailsDialog(book);
        });
    }

    /**
     * getItemCount: Trả về số lượng item
     * @return Kích thước danh sách sách
     */
    @Override
    public int getItemCount() {
        return bookList.size();
    }

    /**
     * showBorrowConfirmDialog: Hiển thị dialog xác nhận mượn sách
     * @param book Sách cần mượn
     */
    private void showBorrowConfirmDialog(Book book) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận mượn sách")
                .setMessage("Bạn có muốn mượn sách \"" + book.getTitle() + "\" không?")
                .setPositiveButton("Mượn", (dialog, which) -> borrowBook(book))  // Nút xác nhận
                .setNegativeButton("Hủy", null)  // Nút hủy
                .show();
    }

    /**
     * borrowBook: Xử lý yêu cầu mượn sách
     * Bước 1: Kiểm tra người dùng đã login
     * Bước 2: Validate thông tin sách
     * Bước 3: Lấy thông tin người dùng
     * Bước 4: Tạo record mượn với trạng thái "Chờ duyệt"
     * Bước 5: Lưu vào Firestore
     *
     * @param book Sách cần mượn
     */
    private void borrowBook(Book book) {
        // ========== Kiểm tra người dùng đã login ==========
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(context, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ========== Validate thông tin sách ==========
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


        // ========== Lấy thông tin người dùng từ Firestore ==========
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {

                    // ===== Lấy tên người dùng =====
                    String userName = null;
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        userName = documentSnapshot.getString("name");
                    }
                    // Nếu không tìm thấy tên, dùng tên mặc định
                    if (userName == null || userName.isEmpty()) {
                        userName = "Người dùng";

                    final String finalUserName = userName; // Make effectively final for lambda


                    // Tạo borrow record với ID an toàn
                    String borrowId;

                    try {
                        borrowId = db.collection("borrows").document().getId();
                        if (borrowId == null || borrowId.isEmpty()) {
                            borrowId = "B_" + UUID.randomUUID().toString();
                        }
                    } catch (Exception ex) {

                        borrowId = null;
                    }

                    // ===== Nếu ID null, tạo ID từ UUID =====
                    if (borrowId == null || borrowId.isEmpty()) {

                        borrowId = "B_" + UUID.randomUUID().toString();
                    }
                    final String finalBorrowId = borrowId; // Make effectively final

                    // ===== Tạo ngày mượn và ngày hết hạn =====
                    Calendar calendar = Calendar.getInstance();
                    Timestamp borrowDate = Timestamp.now();  // Ngày mượn = hôm nay

                    calendar.add(Calendar.DAY_OF_MONTH, 14);  // Cộng thêm 14 ngày
                    Timestamp dueDate = new Timestamp(calendar.getTime());  // Ngày hết hạn

                    // ===== Tạo đối tượng Borrow =====
                    Borrow borrow = new Borrow(
                            finalBorrowId,
                            userId,
                            finalUserName,
                            book.getId(),
                            book.getTitle(),
                            borrowDate,
                            dueDate,
                            null,  // Chưa trả => null
                            "Chờ duyệt"  // Trạng thái chờ admin duyệt
                    );

                    // ===== Lưu yêu cầu mượn vào Firestore =====
                    try {
                        db.collection("borrows").document(finalBorrowId).set(borrow)
                                .addOnSuccessListener(aVoid -> {

                                    // KHÔNG giảm số lượng sách ngay, đợi admin duyệt
                                    Toast.makeText(context, "Đã gửi yêu cầu mượn sách! Vui lòng chờ admin duyệt.", Toast.LENGTH_LONG).show();

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

    /**
     * showBookDetailsDialog: Hiển thị dialog chi tiết thông tin sách
     * @param book Sách cần hiển thị chi tiết
     */
    private void showBookDetailsDialog(Book book) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(book.getTitle());  // Tiêu đề = tên sách
        builder.setMessage(
                "Tác giả: " + book.getAuthor() + "\n\n" +
                "Thể loại: " + book.getCategory() + "\n\n" +
                "Mô tả: " + book.getDescription() + "\n\n" +
                "Số lượng còn lại: " + book.getQuantity()
        );
        builder.setPositiveButton("Đóng", null);
        builder.show();
    }

    /**
     * BookViewHolder: Inner class để giữ reference các view trong item
     */
    static class BookViewHolder extends RecyclerView.ViewHolder {
        // ============ Khai báo các view trong item_book.xml ============
        ImageView imgBook;           // Ảnh bìa sách
        TextView tvTitle;            // Tên sách
        TextView tvAuthor;           // Tác giả
        TextView tvCategory;         // Thể loại
        TextView tvQuantity;         // Số lượng còn
        Button btnBorrow;            // Nút mượn sách

        /**
         * Constructor: Khởi tạo BookViewHolder
         * @param itemView View của item
         */
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            // ========== Ràng buộc các view ==========
            imgBook = itemView.findViewById(R.id.imgBook);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnBorrow = itemView.findViewById(R.id.btnBorrow);
        }
    }
}

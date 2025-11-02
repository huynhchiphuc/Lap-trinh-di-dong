package com.example.do_an.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.do_an.R;
import com.example.do_an.models.Book;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * AddBookActivity: Màn hình thêm sách mới
 * Chức năng:
 * 1. Cho phép admin nhập thông tin sách (tên, tác giả, thể loại, số lượng, ảnh bìa, mô tả)
 * 2. Validate dữ liệu nhập vào
 * 3. Tạo ID sách tự động dựa trên số lượng sách hiện có (B001, B002, ...)
 * 4. Lưu thông tin sách vào Firestore Database
 */
public class AddBookActivity extends AppCompatActivity {

    // ============ Khai báo các UI views ============
    private EditText edtTitle;              // Trường nhập tên sách
    private EditText edtAuthor;             // Trường nhập tác giả
    private EditText edtCategory;           // Trường nhập thể loại
    private EditText edtQuantity;           // Trường nhập số lượng
    private EditText edtImageUrl;           // Trường nhập URL ảnh bìa
    private EditText edtDescription;        // Trường nhập mô tả sách
    private Button btnSave;                 // Nút lưu sách
    private Button btnCancel;               // Nút hủy / quay lại
    private ProgressBar progressBar;        // Thanh tiến trình khi đang lưu

    // ============ Khai báo Firebase instances ============
    private FirebaseFirestore db;           // Firestore Database

    /**
     * onCreate: Gọi khi Activity được tạo
     * - Khởi tạo Firebase
     * - Ràng buộc các UI views
     * - Thiết lập listeners cho các nút
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // ========== Khởi tạo Firebase ==========
        db = FirebaseFirestore.getInstance(); // Khởi tạo Firestore DB

        // ========== Ràng buộc UI Views từ Layout ==========
        edtTitle = findViewById(R.id.edtTitle);
        edtAuthor = findViewById(R.id.edtAuthor);
        edtCategory = findViewById(R.id.edtCategory);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtImageUrl = findViewById(R.id.edtImageUrl);
        edtDescription = findViewById(R.id.edtDescription);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        // ========== Thiết lập Listeners cho nút Lưu ==========
        btnSave.setOnClickListener(v -> saveBook()); // Gọi saveBook khi bấm nút Lưu

        // ========== Thiết lập Listeners cho nút Hủy ==========
        btnCancel.setOnClickListener(v -> finish()); // Đóng Activity khi bấm nút Hủy
    }

    /**
     * saveBook: Xử lý quá trình thêm sách mới
     * Bước 1: Lấy tất cả dữ liệu từ các EditText
     * Bước 2: Validate tên sách không trống
     * Bước 3: Validate tác giả không trống
     * Bước 4: Validate thể loại không trống
     * Bước 5: Validate số lượng hợp lệ (>=0)
     * Bước 6: Nếu URL ảnh trống, dùng ảnh placeholder
     * Bước 7: Nếu mô tả trống, ghi "Chưa có mô tả"
     * Bước 8: Gọi generateBookId để tạo ID và lưu sách
     */
    private void saveBook() {
        // ========== Lấy dữ liệu từ các EditText ==========
        String title = edtTitle.getText().toString().trim();            // Tên sách
        String author = edtAuthor.getText().toString().trim();          // Tác giả
        String category = edtCategory.getText().toString().trim();      // Thể loại
        String quantityStr = edtQuantity.getText().toString().trim();   // Số lượng (string)
        String imageUrl = edtImageUrl.getText().toString().trim();      // URL ảnh bìa
        String description = edtDescription.getText().toString().trim(); // Mô tả

        // ========== Validate Tên sách không trống ==========
        if (TextUtils.isEmpty(title)) {
            edtTitle.setError("Vui lòng nhập tên sách");
            return; // Dừng execution
        }

        // ========== Validate Tác giả không trống ==========
        if (TextUtils.isEmpty(author)) {
            edtAuthor.setError("Vui lòng nhập tác giả");
            return; // Dừng execution
        }

        // ========== Validate Thể loại không trống ==========
        if (TextUtils.isEmpty(category)) {
            edtCategory.setError("Vui lòng nhập thể loại");
            return; // Dừng execution
        }

        // ========== Validate Số lượng không trống ==========
        if (TextUtils.isEmpty(quantityStr)) {
            edtQuantity.setError("Vui lòng nhập số lượng");
            return; // Dừng execution
        }

        // ========== Parse và Validate Số lượng ==========
        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr); // Chuyển string sang số nguyên
            if (quantity < 0) {
                // Số lượng không được âm
                edtQuantity.setError("Số lượng phải >= 0");
                return; // Dừng execution
            }
        } catch (NumberFormatException e) {
            // Parse thất bại - nhập không phải số
            edtQuantity.setError("Số lượng không hợp lệ");
            return; // Dừng execution
        }

        // ========== Xử lý URL ảnh ==========
        // Nếu trống, dùng placeholder
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = "https://via.placeholder.com/300x400?text=Book";
        }

        // ========== Xử lý Mô tả ==========
        // Nếu trống, ghi mô tả mặc định
        if (TextUtils.isEmpty(description)) {
            description = "Chưa có mô tả";
        }

        // ========== Hiển thị ProgressBar và disable nút Lưu ==========
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        // ========== Tạo ID sách và lưu ==========
        generateBookId(title, author, category, quantity, imageUrl, description);
    }

    /**
     * generateBookId: Tạo ID sách tự động và lưu vào Firestore
     * Bước 1: Lấy tất cả sách hiện có từ Firestore
     * Bước 2: Đếm số lượng để tạo ID (B001, B002, ...)
     * Bước 3: Tạo đối tượng Book
     * Bước 4: Lưu vào Firestore collection "books"
     * Bước 5: Hiển thị thông báo thành công và quay lại
     *
     * @param title Tên sách
     * @param author Tác giả
     * @param category Thể loại
     * @param quantity Số lượng
     * @param imageUrl URL ảnh bìa
     * @param description Mô tả
     */
    private void generateBookId(String title, String author, String category, int quantity, String imageUrl, String description) {
        // ========== Lấy tất cả sách hiện có ==========
        db.collection("books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // ===== Đếm số lượng sách hiện có =====
                    int bookCount = queryDocumentSnapshots.size();

                    // ===== Tạo ID sách =====
                    // VD: Nếu có 5 sách, ID mới là "B006"
                    String bookId = "B" + String.format("%03d", bookCount + 1);

                    // ===== Tạo đối tượng Book =====
                    Book book = new Book(bookId, title, author, category, quantity, imageUrl, description);

                    // ===== Lưu vào Firestore =====
                    db.collection("books").document(bookId)
                            .set(book)
                            .addOnSuccessListener(aVoid -> {
                                // ===== Lưu thành công =====
                                progressBar.setVisibility(View.GONE);
                                btnSave.setEnabled(true);
                                Toast.makeText(AddBookActivity.this, "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
                                finish(); // Đóng Activity
                            })
                            .addOnFailureListener(e -> {
                                // ===== Lỗi khi lưu =====
                                progressBar.setVisibility(View.GONE);
                                btnSave.setEnabled(true);
                                Toast.makeText(AddBookActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // ===== Lỗi khi lấy danh sách sách =====
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(AddBookActivity.this, "Lỗi tạo ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}


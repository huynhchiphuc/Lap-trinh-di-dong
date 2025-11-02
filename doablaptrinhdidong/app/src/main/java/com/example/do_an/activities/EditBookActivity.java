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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * EditBookActivity: Màn hình chỉnh sửa thông tin sách
 * Chức năng:
 * 1. Nhận thông tin sách từ Activity gọi nó (qua Intent)
 * 2. Hiển thị thông tin sách hiện tại trong các EditText
 * 3. Cho phép admin chỉnh sửa thông tin sách
 * 4. Validate dữ liệu
 * 5. Update thông tin sách vào Firestore Database
 */
public class EditBookActivity extends AppCompatActivity {

    // ============ Khai báo các UI views ============
    private EditText edtTitle;              // Trường nhập tên sách
    private EditText edtAuthor;             // Trường nhập tác giả
    private EditText edtCategory;           // Trường nhập thể loại
    private EditText edtQuantity;           // Trường nhập số lượng
    private EditText edtImageUrl;           // Trường nhập URL ảnh bìa
    private EditText edtDescription;        // Trường nhập mô tả sách
    private Button btnUpdate;               // Nút cập nhật sách
    private Button btnCancel;               // Nút hủy / quay lại
    private ProgressBar progressBar;        // Thanh tiến trình khi đang cập nhật

    // ============ Khai báo Firebase instances ============
    private FirebaseFirestore db;           // Firestore Database

    // ============ Khai báo biến ============
    private String bookId;                  // ID của sách cần chỉnh sửa

    /**
     * onCreate: Gọi khi Activity được tạo
     * - Khởi tạo Firebase
     * - Ràng buộc các UI views
     * - Lấy dữ liệu sách từ Intent
     * - Điền dữ liệu hiện tại vào các EditText
     * - Thiết lập listeners cho các nút
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        // ========== Khởi tạo Firebase ==========
        db = FirebaseFirestore.getInstance(); // Khởi tạo Firestore DB

        // ========== Ràng buộc UI Views từ Layout ==========
        edtTitle = findViewById(R.id.edtTitle);
        edtAuthor = findViewById(R.id.edtAuthor);
        edtCategory = findViewById(R.id.edtCategory);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtImageUrl = findViewById(R.id.edtImageUrl);
        edtDescription = findViewById(R.id.edtDescription);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        // ========== Lấy dữ liệu sách từ Intent ==========
        // Dữ liệu được truyền từ Activity gọi nó (thường là BookListFragment)
        bookId = getIntent().getStringExtra("bookId");
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String category = getIntent().getStringExtra("category");
        int quantity = getIntent().getIntExtra("quantity", 0);
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String description = getIntent().getStringExtra("description");

        // ========== Điền dữ liệu hiện tại vào EditText ==========
        if (bookId != null) {
            edtTitle.setText(title);                        // Tên sách hiện tại
            edtAuthor.setText(author);                      // Tác giả hiện tại
            edtCategory.setText(category);                  // Thể loại hiện tại
            edtQuantity.setText(String.valueOf(quantity));  // Số lượng hiện tại
            edtImageUrl.setText(imageUrl);                  // URL ảnh bìa hiện tại
            edtDescription.setText(description);            // Mô tả hiện tại
        }

        // ========== Thiết lập Listeners cho nút Cập nhật ==========
        btnUpdate.setOnClickListener(v -> updateBook()); // Gọi updateBook khi bấm nút Cập nhật

        // ========== Thiết lập Listeners cho nút Hủy ==========
        btnCancel.setOnClickListener(v -> finish()); // Đóng Activity khi bấm nút Hủy
    }

    /**
     * updateBook: Xử lý quá trình cập nhật sách
     * Bước 1: Lấy tất cả dữ liệu chỉnh sửa từ các EditText
     * Bước 2: Validate tất cả dữ liệu
     * Bước 3: Tạo HashMap chứa các thay đổi
     * Bước 4: Update vào Firestore
     * Bước 5: Hiển thị thông báo và quay lại
     */
    private void updateBook() {
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

        // ========== Hiển thị ProgressBar và disable nút Cập nhật ==========
        progressBar.setVisibility(View.VISIBLE);
        btnUpdate.setEnabled(false);

        // ========== Tạo HashMap chứa các trường cần update ==========
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);           // Cập nhật tên sách
        updates.put("author", author);         // Cập nhật tác giả
        updates.put("category", category);     // Cập nhật thể loại
        updates.put("quantity", quantity);     // Cập nhật số lượng
        updates.put("imageUrl", imageUrl);     // Cập nhật URL ảnh bìa
        updates.put("description", description); // Cập nhật mô tả

        // ========== Update vào Firestore ==========
        db.collection("books").document(bookId)
                .update(updates)  // Sử dụng update thay vì set để chỉ cập nhật các trường cần thay đổi
                .addOnSuccessListener(aVoid -> {
                    // ===== Cập nhật thành công =====
                    progressBar.setVisibility(View.GONE);
                    btnUpdate.setEnabled(true);
                    Toast.makeText(EditBookActivity.this, "Cập nhật sách thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng Activity
                })
                .addOnFailureListener(e -> {
                    // ===== Lỗi khi cập nhật =====
                    progressBar.setVisibility(View.GONE);
                    btnUpdate.setEnabled(true);
                    Toast.makeText(EditBookActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}


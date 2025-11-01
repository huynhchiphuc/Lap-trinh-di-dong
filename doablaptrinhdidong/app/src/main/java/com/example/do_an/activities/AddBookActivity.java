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

public class AddBookActivity extends AppCompatActivity {

    private EditText edtTitle, edtAuthor, edtCategory, edtQuantity, edtImageUrl, edtDescription;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        edtTitle = findViewById(R.id.edtTitle);
        edtAuthor = findViewById(R.id.edtAuthor);
        edtCategory = findViewById(R.id.edtCategory);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtImageUrl = findViewById(R.id.edtImageUrl);
        edtDescription = findViewById(R.id.edtDescription);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        btnSave.setOnClickListener(v -> saveBook());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveBook() {
        String title = edtTitle.getText().toString().trim();
        String author = edtAuthor.getText().toString().trim();
        String category = edtCategory.getText().toString().trim();
        String quantityStr = edtQuantity.getText().toString().trim();
        String imageUrl = edtImageUrl.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(title)) {
            edtTitle.setError("Vui lòng nhập tên sách");
            return;
        }

        if (TextUtils.isEmpty(author)) {
            edtAuthor.setError("Vui lòng nhập tác giả");
            return;
        }

        if (TextUtils.isEmpty(category)) {
            edtCategory.setError("Vui lòng nhập thể loại");
            return;
        }

        if (TextUtils.isEmpty(quantityStr)) {
            edtQuantity.setError("Vui lòng nhập số lượng");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                edtQuantity.setError("Số lượng phải >= 0");
                return;
            }
        } catch (NumberFormatException e) {
            edtQuantity.setError("Số lượng không hợp lệ");
            return;
        }

        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = "https://via.placeholder.com/300x400?text=Book";
        }

        if (TextUtils.isEmpty(description)) {
            description = "Chưa có mô tả";
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        // Generate book ID
        generateBookId(title, author, category, quantity, imageUrl, description);
    }

    private void generateBookId(String title, String author, String category, int quantity, String imageUrl, String description) {
        // Lấy số lượng sách hiện có để tạo ID
        db.collection("books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int bookCount = queryDocumentSnapshots.size();
                    String bookId = "B" + String.format("%03d", bookCount + 1);

                    // Tạo book object
                    Book book = new Book(bookId, title, author, category, quantity, imageUrl, description);

                    // Lưu vào Firestore
                    db.collection("books").document(bookId)
                            .set(book)
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.GONE);
                                btnSave.setEnabled(true);
                                Toast.makeText(AddBookActivity.this, "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                btnSave.setEnabled(true);
                                Toast.makeText(AddBookActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(AddBookActivity.this, "Lỗi tạo ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}


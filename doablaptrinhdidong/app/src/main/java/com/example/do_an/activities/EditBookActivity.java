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

public class EditBookActivity extends AppCompatActivity {

    private EditText edtTitle, edtAuthor, edtCategory, edtQuantity, edtImageUrl, edtDescription;
    private Button btnUpdate, btnCancel;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        edtTitle = findViewById(R.id.edtTitle);
        edtAuthor = findViewById(R.id.edtAuthor);
        edtCategory = findViewById(R.id.edtCategory);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtImageUrl = findViewById(R.id.edtImageUrl);
        edtDescription = findViewById(R.id.edtDescription);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        // Get data from intent
        bookId = getIntent().getStringExtra("bookId");
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String category = getIntent().getStringExtra("category");
        int quantity = getIntent().getIntExtra("quantity", 0);
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String description = getIntent().getStringExtra("description");

        // Pre-fill data
        if (bookId != null) {
            edtTitle.setText(title);
            edtAuthor.setText(author);
            edtCategory.setText(category);
            edtQuantity.setText(String.valueOf(quantity));
            edtImageUrl.setText(imageUrl);
            edtDescription.setText(description);
        }

        btnUpdate.setOnClickListener(v -> updateBook());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void updateBook() {
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
        btnUpdate.setEnabled(false);

        // Update book in Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("author", author);
        updates.put("category", category);
        updates.put("quantity", quantity);
        updates.put("imageUrl", imageUrl);
        updates.put("description", description);

        db.collection("books").document(bookId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    btnUpdate.setEnabled(true);
                    Toast.makeText(EditBookActivity.this, "Cập nhật sách thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnUpdate.setEnabled(true);
                    Toast.makeText(EditBookActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}


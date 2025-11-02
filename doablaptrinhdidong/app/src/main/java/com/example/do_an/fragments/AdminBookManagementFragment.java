package com.example.do_an.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.do_an.R;
import com.example.do_an.adapters.AdminBookAdapter;
import com.example.do_an.models.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * AdminBookManagementFragment: Fragment cho admin quản lý danh sách sách
 * Chức năng:
 * 1. Hiển thị danh sách tất cả sách
 * 2. Tìm kiếm sách theo tên, tác giả, thể loại (không phân biệt hoa thường)
 * 3. Chỉnh sửa sách (via AdminBookAdapter)
 * 4. Xóa sách (via AdminBookAdapter)
 * 5. Thêm sách mới (FAB button mở AddBookActivity)
 */
public class AdminBookManagementFragment extends Fragment {

    // ============ Khai báo các UI views ============
    private RecyclerView recyclerView;               // RecyclerView hiển thị danh sách sách
    private FloatingActionButton fabAdd;             // Nút + để thêm sách mới
    private EditText etSearchBook;                   // EditText tìm kiếm sách

    // ============ Khai báo Adapter và List ============
    private AdminBookAdapter adapter;                // Adapter với chức năng edit/delete
    private List<Book> bookList;                     // Danh sách tất cả sách
    private List<Book> filteredBookList;             // Danh sách sách sau filter

    // ============ Khai báo Firebase ============
    private FirebaseFirestore db;                    // Firestore Database

    /**
     * onCreateView: Gọi khi Fragment được tạo
     * - Inflate layout
     * - Khởi tạo RecyclerView, FAB, EditText
     * - Setup search listener
     * - Load danh sách sách
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_book_management, container, false);

        // ========== Khởi tạo Firebase ==========
        db = FirebaseFirestore.getInstance();

        // ========== Khởi tạo danh sách ==========
        bookList = new ArrayList<>();
        filteredBookList = new ArrayList<>();

        // ========== Ràng buộc UI views ==========
        recyclerView = view.findViewById(R.id.recyclerView);
        fabAdd = view.findViewById(R.id.fabAdd);
        etSearchBook = view.findViewById(R.id.etSearchBook);

        // ========== Setup RecyclerView ==========
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tạo Adapter với callback loadBooks để refresh list sau khi xóa
        adapter = new AdminBookAdapter(getContext(), filteredBookList, this::loadBooks);
        recyclerView.setAdapter(adapter);

        // ========== Thiết lập listener cho FAB (nút +) ==========
        fabAdd.setOnClickListener(v -> {
            // ===== Mở AddBookActivity =====
            android.content.Intent intent = new android.content.Intent(getActivity(), com.example.do_an.activities.AddBookActivity.class);
            startActivity(intent);
        });

        // ========== Setup listeners ==========
        setupSearchListener();
        loadBooks();

        return view;
    }

    /**
     * setupSearchListener: Thiết lập listener để tìm kiếm khi người dùng nhập
     */
    private void setupSearchListener() {
        etSearchBook.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Gọi trước khi text thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ========== Filter danh sách khi text thay đổi ==========
                filterBooks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Gọi sau khi text thay đổi
            }
        });
    }

    /**
     * filterBooks: Lọc danh sách sách theo query
     * Tìm kiếm không phân biệt hoa thường
     *
     * @param query Chuỗi tìm kiếm
     */
    private void filterBooks(String query) {
        // ========== Xóa danh sách filter ==========
        filteredBookList.clear();

        // ========== Nếu query rỗng, hiển thị tất cả ==========
        if (query.isEmpty()) {
            filteredBookList.addAll(bookList);
        } else {
            // ========== Query không rỗng, filter danh sách ==========
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Book book : bookList) {
                if (book.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                    book.getAuthor().toLowerCase().contains(lowerCaseQuery) ||
                    book.getCategory().toLowerCase().contains(lowerCaseQuery)) {
                    filteredBookList.add(book);
                }
            }
        }

        // ========== Cập nhật UI ==========
        adapter.notifyDataSetChanged();
    }

    /**
     * onResume: Gọi khi Fragment quay lại từ Activity khác
     * Refresh danh sách sách (vì có thể đã thêm/sửa sách)
     */
    @Override
    public void onResume() {
        super.onResume();
        // ========== Refresh danh sách khi quay lại ==========
        loadBooks();
    }

    /**
     * loadBooks: Tải danh sách sách từ Firestore
     * Chuyển đổi từng document thành đối tượng Book
     * Filter theo query tìm kiếm hiện tại
     */
    private void loadBooks() {
        db.collection("books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // ========== Xóa danh sách cũ ==========
                    bookList.clear();

                    // ========== Duyệt từng document ==========
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);

                        // QUAN TRỌNG: Set ID từ document ID nếu chưa có
                        if (book.getId() == null || book.getId().isEmpty()) {
                            book.setId(document.getId());
                        }

                        bookList.add(book);
                    }

                    // ========== Filter theo query tìm kiếm hiện tại ==========
                    filterBooks(etSearchBook.getText().toString());
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast.makeText(getContext(), "Lỗi tải sách: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                });
    }
}


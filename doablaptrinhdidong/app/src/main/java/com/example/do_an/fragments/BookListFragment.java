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
import com.example.do_an.adapters.BookAdapter;
import com.example.do_an.models.Book;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * BookListFragment: Fragment hiển thị danh sách sách với chức năng tìm kiếm
 * Chức năng:
 * 1. Hiển thị danh sách tất cả sách từ Firestore
 * 2. Tìm kiếm sách theo tên, tác giả, thể loại (không phân biệt hoa thường)
 * 3. Cập nhật danh sách real-time khi người dùng nhập trong ô tìm kiếm
 */
public class BookListFragment extends Fragment {

    // ============ Khai báo các UI views ============
    private RecyclerView recyclerView;               // RecyclerView hiển thị danh sách sách
    private EditText etSearchBook;                   // EditText tìm kiếm sách

    // ============ Khai báo Adapter và List ============
    private BookAdapter adapter;                     // Adapter để bind dữ liệu vào RecyclerView
    private List<Book> bookList;                     // Danh sách tất cả sách từ Firestore
    private List<Book> filteredBookList;             // Danh sách sách sau khi filter (tìm kiếm)

    // ============ Khai báo Firebase ============
    private FirebaseFirestore db;                    // Firestore Database

    /**
     * onCreateView: Gọi khi Fragment được tạo
     * - Inflate layout
     * - Khởi tạo RecyclerView, EditText
     * - Setup search listener
     * - Load danh sách sách từ Firestore
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        // ========== Khởi tạo Firebase ==========
        db = FirebaseFirestore.getInstance();

        // ========== Khởi tạo danh sách ==========
        bookList = new ArrayList<>();               // Danh sách gốc
        filteredBookList = new ArrayList<>();        // Danh sách filter

        // ========== Ràng buộc UI views ==========
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));  // Linear layout

        etSearchBook = view.findViewById(R.id.etSearchBook);  // Trường tìm kiếm

        // ========== Tạo và gắn Adapter ==========
        adapter = new BookAdapter(getContext(), filteredBookList);
        recyclerView.setAdapter(adapter);

        // ========== Setup listeners ==========
        setupSearchListener();  // Thiết lập listener tìm kiếm
        loadBooks();           // Tải danh sách sách từ Firestore

        return view;
    }

    /**
     * setupSearchListener: Thiết lập listener để tìm kiếm khi người dùng nhập
     * Sử dụng TextWatcher để lắng nghe sự thay đổi trong EditText
     * Gọi filterBooks khi text thay đổi
     */
    private void setupSearchListener() {
        etSearchBook.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Gọi trước khi text thay đổi (không cần xử lý)
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ========== Gọi khi text thay đổi ==========
                filterBooks(s.toString());  // Filter danh sách theo text được nhập
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Gọi sau khi text thay đổi (không cần xử lý)
            }
        });
    }

    /**
     * filterBooks: Lọc danh sách sách theo query tìm kiếm
     * Tìm kiếm không phân biệt hoa thường (case-insensitive)
     * Tìm kiếm theo: tên sách, tác giả, thể loại
     *
     * @param query Chuỗi tìm kiếm từ EditText
     */
    private void filterBooks(String query) {
        // ========== Xóa danh sách filter ==========
        filteredBookList.clear();

        // ========== Nếu query rỗng, hiển thị tất cả sách ==========
        if (query.isEmpty()) {
            filteredBookList.addAll(bookList);  // Thêm tất cả sách vào filtered list
        } else {
            // ========== Query không rỗng, filter danh sách ==========
            String lowerCaseQuery = query.toLowerCase().trim();  // Chuyển thành lowercase để so sánh

            // ========== Duyệt từng sách và kiểm tra ==========
            for (Book book : bookList) {
                // Kiểm tra xem tên, tác giả, hoặc thể loại có chứa query không (không phân biệt hoa thường)
                if (book.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                    book.getAuthor().toLowerCase().contains(lowerCaseQuery) ||
                    book.getCategory().toLowerCase().contains(lowerCaseQuery)) {
                    // Nếu trùng, thêm vào filtered list
                    filteredBookList.add(book);
                }
            }
        }

        // ========== Thông báo cho Adapter cập nhật UI ==========
        adapter.notifyDataSetChanged();
    }

    /**
     * loadBooks: Tải danh sách sách từ Firestore
     * Bước 1: Lấy tất cả documents trong collection "books"
     * Bước 2: Chuyển đổi từng document thành đối tượng Book
     * Bước 3: Set ID nếu chưa có
     * Bước 4: Thêm vào bookList
     * Bước 5: Filter theo query tìm kiếm hiện tại
     */
    private void loadBooks() {
        db.collection("books")  // Truy vấn collection "books"
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // ========== Xóa danh sách cũ ==========
                    bookList.clear();

                    // ========== Duyệt từng document ==========
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Chuyển đổi document thành đối tượng Book
                        Book book = document.toObject(Book.class);

                        // QUAN TRỌNG: Set ID từ document ID nếu chưa có
                        // Document ID trong Firestore có thể không được lưu trong object
                        if (book.getId() == null || book.getId().isEmpty()) {
                            book.setId(document.getId());
                        }

                        // Thêm vào danh sách
                        bookList.add(book);
                    }

                    // ========== Filter theo query hiện tại ==========
                    // Nếu người dùng đang tìm kiếm, cập nhật filtered list
                    filterBooks(etSearchBook.getText().toString());
                })
                .addOnFailureListener(e -> {
                    // ========== Lỗi khi tải ==========
                    android.widget.Toast.makeText(getContext(), "Lỗi tải danh sách sách: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                });
    }
}


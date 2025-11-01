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

public class AdminBookManagementFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminBookAdapter adapter;
    private List<Book> bookList;
    private List<Book> filteredBookList;
    private FloatingActionButton fabAdd;
    private FirebaseFirestore db;
    private EditText etSearchBook;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_book_management, container, false);

        db = FirebaseFirestore.getInstance();
        bookList = new ArrayList<>();
        filteredBookList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        fabAdd = view.findViewById(R.id.fabAdd);
        etSearchBook = view.findViewById(R.id.etSearchBook);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminBookAdapter(getContext(), filteredBookList, this::loadBooks);
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            // Mở AddBookActivity
            android.content.Intent intent = new android.content.Intent(getActivity(), com.example.do_an.activities.AddBookActivity.class);
            startActivity(intent);
        });

        setupSearchListener();
        loadBooks();

        return view;
    }

    private void setupSearchListener() {
        etSearchBook.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBooks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterBooks(String query) {
        filteredBookList.clear();

        if (query.isEmpty()) {
            filteredBookList.addAll(bookList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Book book : bookList) {
                if (book.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                    book.getAuthor().toLowerCase().contains(lowerCaseQuery) ||
                    book.getCategory().toLowerCase().contains(lowerCaseQuery)) {
                    filteredBookList.add(book);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh danh sách khi quay lại từ Add/Edit
        loadBooks();
    }

    private void loadBooks() {
        db.collection("books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        // QUAN TRỌNG: Set ID từ document ID
                        if (book.getId() == null || book.getId().isEmpty()) {
                            book.setId(document.getId());
                        }
                        bookList.add(book);
                    }
                    filterBooks(etSearchBook.getText().toString());
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast.makeText(getContext(), "Lỗi tải sách: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                });
    }
}


package com.example.do_an.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private FloatingActionButton fabAdd;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_book_management, container, false);

        db = FirebaseFirestore.getInstance();
        bookList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        fabAdd = view.findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminBookAdapter(getContext(), bookList, this::loadBooks);
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            // Mở AddBookActivity
            android.content.Intent intent = new android.content.Intent(getActivity(), com.example.do_an.activities.AddBookActivity.class);
            startActivity(intent);
        });

        loadBooks();

        return view;
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
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast.makeText(getContext(), "Lỗi tải sách: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                });
    }
}


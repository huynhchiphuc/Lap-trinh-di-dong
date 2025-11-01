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
import com.example.do_an.adapters.BorrowAdapter;
import com.example.do_an.models.Borrow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyBorrowsFragment extends Fragment {

    private RecyclerView recyclerView;
    private BorrowAdapter adapter;
    private List<Borrow> borrowList;
    private List<Borrow> filteredBorrowList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText etSearchBorrow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_borrows, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        borrowList = new ArrayList<>();
        filteredBorrowList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        etSearchBorrow = view.findViewById(R.id.etSearchBorrow);

        adapter = new BorrowAdapter(getContext(), filteredBorrowList);
        recyclerView.setAdapter(adapter);

        setupSearchListener();
        loadMyBorrows();

        return view;
    }

    private void setupSearchListener() {
        etSearchBorrow.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBorrows(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterBorrows(String query) {
        filteredBorrowList.clear();

        if (query.isEmpty()) {
            filteredBorrowList.addAll(borrowList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Borrow borrow : borrowList) {
                if (borrow.getBookTitle().toLowerCase().contains(lowerCaseQuery) ||
                    borrow.getStatus().toLowerCase().contains(lowerCaseQuery)) {
                    filteredBorrowList.add(borrow);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void loadMyBorrows() {
        // Kiểm tra user đã đăng nhập chưa
        if (mAuth.getCurrentUser() == null) {
            android.widget.Toast.makeText(getContext(), "Vui lòng đăng nhập lại!", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        db.collection("borrows")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    borrowList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Borrow borrow = document.toObject(Borrow.class);
                        borrowList.add(borrow);
                    }
                    filterBorrows(etSearchBorrow.getText().toString());
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast.makeText(getContext(), "Lỗi tải phiếu mượn: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                });
    }
}


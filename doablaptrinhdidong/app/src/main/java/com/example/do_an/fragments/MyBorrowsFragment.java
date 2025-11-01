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
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_borrows, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        borrowList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BorrowAdapter(getContext(), borrowList);
        recyclerView.setAdapter(adapter);

        loadMyBorrows();

        return view;
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
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast.makeText(getContext(), "Lỗi tải phiếu mượn: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                });
    }
}


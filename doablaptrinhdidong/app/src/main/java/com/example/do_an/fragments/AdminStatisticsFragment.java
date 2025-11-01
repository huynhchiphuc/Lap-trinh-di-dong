package com.example.do_an.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.do_an.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminStatisticsFragment extends Fragment {

    private TextView tvTotalBooks, tvTotalBorrows, tvActiveBorrows;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_statistics, container, false);

        db = FirebaseFirestore.getInstance();

        tvTotalBooks = view.findViewById(R.id.tvTotalBooks);
        tvTotalBorrows = view.findViewById(R.id.tvTotalBorrows);
        tvActiveBorrows = view.findViewById(R.id.tvActiveBorrows);

        loadStatistics();

        return view;
    }

    private void loadStatistics() {
        // Đếm tổng số sách
        db.collection("books").get().addOnSuccessListener(queryDocumentSnapshots -> {
            int totalBooks = queryDocumentSnapshots.size();
            tvTotalBooks.setText(String.valueOf(totalBooks));
        });

        // Đếm tổng số phiếu mượn
        db.collection("borrows").get().addOnSuccessListener(queryDocumentSnapshots -> {
            int totalBorrows = queryDocumentSnapshots.size();
            tvTotalBorrows.setText(String.valueOf(totalBorrows));
        });

        // Đếm số phiếu mượn đang hoạt động
        db.collection("borrows")
                .whereEqualTo("status", "Đang mượn")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int activeBorrows = queryDocumentSnapshots.size();
                    tvActiveBorrows.setText(String.valueOf(activeBorrows));
                });
    }
}


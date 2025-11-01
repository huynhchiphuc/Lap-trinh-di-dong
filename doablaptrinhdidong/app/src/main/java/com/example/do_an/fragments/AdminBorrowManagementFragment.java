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
import com.example.do_an.adapters.AdminBorrowAdapter;
import com.example.do_an.models.Borrow;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminBorrowManagementFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminBorrowAdapter adapter;
    private List<Borrow> borrowList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_borrow_management, container, false);

        db = FirebaseFirestore.getInstance();
        borrowList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AdminBorrowAdapter(getContext(), borrowList, this::loadBorrows);
        recyclerView.setAdapter(adapter);

        loadBorrows();

        return view;
    }

    private void loadBorrows() {
        db.collection("borrows")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    borrowList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Borrow borrow = document.toObject(Borrow.class);
                        borrowList.add(borrow);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}


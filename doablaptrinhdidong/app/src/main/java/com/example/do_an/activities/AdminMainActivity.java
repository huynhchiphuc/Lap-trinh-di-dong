package com.example.do_an.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.do_an.R;
import com.example.do_an.fragments.AdminBookManagementFragment;
import com.example.do_an.fragments.AdminBorrowManagementFragment;
import com.example.do_an.fragments.AdminStatisticsFragment;
import com.example.do_an.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminBookManagementFragment())
                    .commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_manage_books) {
                        selectedFragment = new AdminBookManagementFragment();
                    } else if (itemId == R.id.nav_manage_borrows) {
                        selectedFragment = new AdminBorrowManagementFragment();
                    } else if (itemId == R.id.nav_approve_users) {
                        selectedFragment = new com.example.do_an.fragments.AdminUserApprovalFragment();
                    } else if (itemId == R.id.nav_statistics) {
                        selectedFragment = new AdminStatisticsFragment();
                    } else if (itemId == R.id.nav_profile) {
                        selectedFragment = new ProfileFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();
                    }

                    return true;
                }
            };
}


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

/**
 * AdminMainActivity: Màn hình chính cho Admin
 * Chức năng:
 * 1. Hiển thị Bottom Navigation View với 5 menu chính
 * 2. Quản lý sách (thêm, sửa, xóa sách)
 * 3. Quản lý mượn sách (duyệt, từ chối, theo dõi mượn trả)
 * 4. Phê duyệt tài khoản mới
 * 5. Xem thống kê
 * 6. Xem hồ sơ cá nhân
 */
public class AdminMainActivity extends AppCompatActivity {

    // ============ Khai báo các UI views ============
    private BottomNavigationView bottomNav;  // Bottom Navigation View cho menu chính

    /**
     * onCreate: Gọi khi Activity được tạo
     * - Thiết lập layout
     * - Khởi tạo Bottom Navigation
     * - Load Fragment mặc định (AdminBookManagementFragment)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // ========== Ràng buộc Bottom Navigation View ==========
        bottomNav = findViewById(R.id.bottom_navigation);

        // ========== Thiết lập listener cho Bottom Navigation ==========
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // ========== Load Fragment mặc định ==========
        // Chỉ load nếu Activity vừa được tạo lần đầu (không phải từ savedInstanceState)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminBookManagementFragment())
                    .commit();
        }
    }

    /**
     * navListener: Listener xử lý khi người dùng chọn item trong Bottom Navigation
     * Bước 1: Lấy item ID từ menu được chọn
     * Bước 2: Xác định Fragment tương ứng
     * Bước 3: Replace fragment hiện tại bằng fragment mới
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // ========== Khởi tạo biến lưu trữ Fragment được chọn ==========
                    Fragment selectedFragment = null;

                    // ========== Lấy ID của item được chọn ==========
                    int itemId = item.getItemId();

                    // ========== Xác định Fragment dựa trên ID ==========
                    if (itemId == R.id.nav_manage_books) {
                        // Menu "Quản lý sách"
                        selectedFragment = new AdminBookManagementFragment();
                    } else if (itemId == R.id.nav_manage_borrows) {
                        // Menu "Quản lý mượn sách"
                        selectedFragment = new AdminBorrowManagementFragment();
                    } else if (itemId == R.id.nav_approve_users) {
                        // Menu "Phê duyệt tài khoản"
                        selectedFragment = new com.example.do_an.fragments.AdminUserApprovalFragment();
                    } else if (itemId == R.id.nav_statistics) {
                        // Menu "Thống kê"
                        selectedFragment = new AdminStatisticsFragment();
                    } else if (itemId == R.id.nav_profile) {
                        // Menu "Hồ sơ cá nhân"
                        selectedFragment = new ProfileFragment();
                    }

                    // ========== Replace Fragment nếu có Fragment được chọn ==========
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)  // Replace fragment hiện tại
                                .commit();  // Thực hiện thay đổi
                    }

                    return true; // Báo cáo rằng item được xử lý
                }
            };
}


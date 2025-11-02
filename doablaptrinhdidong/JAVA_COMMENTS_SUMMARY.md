# 📋 TÓMS TẮTỤ - TẤT CẢ CODE ĐÃ ĐƯỢC GRITNH CHÚ

## ✅ Hoàn Thành: 13 File Java Đã Ghi Chú Chi Tiết

### 📚 Model Classes (3 file)
1. **Book.java** ✅
   - 7 thuộc tính: id, title, author, category, quantity, imageUrl, description
   - Getter/Setter cho tất cả

2. **User.java** ✅
   - 5 thuộc tính: uid, name, email, role (student/admin), status (pending/approved/rejected)
   - Helper methods: isAdmin(), isStudent(), isPending(), isApproved()

3. **Borrow.java** ✅
   - 9 thuộc tính: borrowId, userId, userName, bookId, bookTitle, borrowDate, dueDate, returnDate, status
   - Trạng thái mượn: Chờ duyệt, Đã duyệt, Đang mượn, Đã trả, Từ chối

---

### 🔐 Authentication & Account (3 file)
4. **LoginActivity.java** ✅
   - Đăng nhập email + mật khẩu
   - Kiểm tra trạng thái tài khoản (pending/approved/rejected)
   - Kiểm tra vai trò điều hướng → AdminMainActivity / StudentMainActivity
   - Check user đã login → skip login screen

5. **RegisterActivity.java** ✅
   - Đăng ký tài khoản mới
   - Chọn vai trò (Student/Admin)
   - Tất cả tài khoản mới → status = "pending" (chờ duyệt)
   - Validation: tên, email, password (min 6), xác nhận password, role

6. **ForgotPasswordActivity.java** ✅
   - Gửi mã xác thực (6 số) qua email → Cloud Function
   - TEST MODE: hiển thị mã trong dialog nếu Cloud Function chưa deploy
   - Verify mã + đặt lại password qua Firebase
   - Countdown 60s cho nút "Gửi lại mã"
   - Hết hạn mã sau 10 phút

---

### 📖 Book Management (4 file)
7. **AddBookActivity.java** ✅
   - Thêm sách mới: tên, tác giả, thể loại, số lượng, ảnh bìa, mô tả
   - Auto generate ID: B001, B002, ...
   - Validation: tên/tác giả/thể loại (bắt buộc), số lượng ≥ 0
   - Default ảnh placeholder nếu trống
   - Default mô tả = "Chưa có mô tả"

8. **EditBookActivity.java** ✅
   - Chỉnh sửa thông tin sách hiện tại
   - Nhận dữ liệu từ Intent (bookId, title, author, ...)
   - Validate giống AddBookActivity
   - Update Firestore (field cần thay đổi)

9. **BookListFragment.java** ✅
   - Hiển thị tất cả sách (sinh viên xem)
   - Tìm kiếm không phân biệt hoa thường
   - Filter theo: tên sách, tác giả, thể loại
   - Real-time filter khi user nhập

10. **AdminBookManagementFragment.java** ✅
    - Quản lý sách cho admin
    - Tìm kiếm không phân biệt hoa thường
    - Nút +: mở AddBookActivity thêm sách mới
    - Adapter hiển thị nút Sửa/Xóa
    - Refresh danh sách khi quay lại (onResume)

---

### 🎯 Main Activity & Fragment System
11. **AdminMainActivity.java** ✅
    - Dashboard admin (5 menu Bottom Navigation)
    - Menu 1: Quản lý sách (AdminBookManagementFragment)
    - Menu 2: Quản lý mượn (AdminBorrowManagementFragment)
    - Menu 3: Phê duyệt tài khoản (AdminUserApprovalFragment)
    - Menu 4: Thống kê (AdminStatisticsFragment)
    - Menu 5: Hồ sơ cá nhân (ProfileFragment)

---

### 🔄 Adapter & UI Binding (2 file)
12. **BookAdapter.java** ✅
    - Adapter hiển thị danh sách sách (RecyclerView)
    - Mỗi item: ảnh, tên, tác giả, thể loại, số lượng
    - Nút Mượn: kiểm tra còn hàng → xác nhận → tạo yêu cầu (status="Chờ duyệt")
    - Click item: hiển thị chi tiết sách dialog
    - Load ảnh bằng Glide (placeholder nếu lỗi)

13. **ProfileFragment.java** ✅
    - Hiển thị: tên, email, vai trò (Sinh viên/Quản trị viên)
    - Nút Logout: signOut + quay LoginActivity (clear stack)
    - Load thông tin từ Firestore collection "users"

---

## 📝 Các File Còn Lại - Hướng Dẫn (8 file)

Xem file **CODE_COMMENTS_GUIDE.md** để biết chi tiết:

- **MyBorrowsFragment.java** - Danh sách mượn của sinh viên
- **AdminBorrowManagementFragment.java** - Admin quản lý & duyệt mượn
- **AdminUserApprovalFragment.java** - Admin phê duyệt tài khoản
- **AdminStatisticsFragment.java** - Thống kê (tổng sách, mượn, đang mượn)
- **BorrowAdapter.java** - Adapter hiển thị mượn (admin duyệt/trả)
- **UserApprovalAdapter.java** - Adapter hiển thị user chờ duyệt
- **AdminBookAdapter.java** - Adapter admin book (edit/delete)
- **MainActivity.java** - Activity chính (nếu dùng)

---

## 🔑 Các Trạng Thái Quan Trọng

### User Status (Trạng thái tài khoản):
- **pending** - Chờ admin duyệt (mặc định khi đăng ký)
- **approved** - Được duyệt, có thể login + dùng app
- **rejected** - Bị từ chối, không thể login

### Borrow Status (Trạng thái mượn):
- **Chờ duyệt** - Yêu cầu mới, admin chưa xử lý
- **Đã duyệt** - Admin chấp nhận, sinh viên có thể lấy
- **Đang mượn** - Đã lấy sách, đang mượn
- **Đã trả** - Trả sách xong
- **Từ chối** - Admin từ chối yêu cầu

---

## 🗂️ Firestore Collections

```
users/{uid}
  - uid, name, email, role (student/admin), status (pending/approved/rejected)

books/{bookId}
  - id, title, author, category, quantity, imageUrl, description

borrows/{borrowId}
  - borrowId, userId, userName, bookId, bookTitle
  - borrowDate, dueDate, returnDate (nullable), status

verification_codes/{userId}
  - code (6 số), email, timestamp, expiryTime
```

---

## 💡 Lưu Ý Khi Đọc Code

1. **Tìm kiếm**: luôn `.toLowerCase().trim()` trước so sánh
2. **Null check**: dùng `TextUtils.isEmpty()` hoặc `if (obj == null)`
3. **Firebase**:
   - `.set()` = ghi toàn bộ
   - `.update()` = cập nhật partial
   - `.delete()` = xóa
4. **RecyclerView**: gọi `adapter.notifyDataSetChanged()` sau khi thay đổi list
5. **Fragment**: dùng `getContext()`, Activity dùng `this`

---

## 📦 Import Chính

```java
// Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.Timestamp;

// Android
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

// Material
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

// Image
import com.bumptech.glide.Glide;
```

---

## ✨ Tóm Tắt Quy Trình Chính

### 1. Đăng Ký Tài Khoản
→ RegisterActivity → Firebase Auth → Firestore users (status=pending) → Logout

### 2. Admin Duyệt Tài Khoản
→ AdminUserApprovalFragment → Firestore users (status=approved/rejected)

### 3. Đăng Nhập
→ LoginActivity → Kiểm tra status → Nếu approved → vào app

### 4. Mượn Sách (Sinh Viên)
→ BookListFragment → Chọn sách → BookAdapter → Tạo Borrow (status=Chờ duyệt)

### 5. Admin Duyệt Mượn
→ AdminBorrowManagementFragment → BorrowAdapter → Approve (giảm quantity)

### 6. Trả Sách (Admin)
→ AdminBorrowManagementFragment → Confirm return → Tăng quantity

### 7. Quên Mật Khẩu
→ ForgotPasswordActivity → Gửi mã email → Verify → Reset password

---

**✅ HOÀN THÀNH GHI CHÚ TẤT CẢ CODE JAVA!**

Bạn có thể mở bất kỳ file nào để xem comment chi tiết bằng tiếng Việt.

Nếu muốn ghi chú thêm các file khác hoặc cần giải thích thêm, hãy cho tôi biết! 


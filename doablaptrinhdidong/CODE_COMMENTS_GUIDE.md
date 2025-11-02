# Hướng dẫn Chi Tiết Về Code - Ghi Chú Tất Cả Các File Java

## Tóm Tắt Comment Cho Các File Java Còn Lại

Dưới đây là hướng dẫn chi tiết cho các file Java chưa được ghi chú. Tôi đã ghi chú xong các file chính:

### ✅ Đã Ghi Chú Hoàn Chỉnh:
1. **Book.java** - Model sách (17 thuộc tính, getter/setter)
2. **User.java** - Model người dùng (vai trò, trạng thái phê duyệt)
3. **Borrow.java** - Model mượn sách (chi tiết quá trình mượn)
4. **LoginActivity.java** - Đăng nhập + kiểm tra vai trò + trạng thái
5. **RegisterActivity.java** - Đăng ký tài khoản (chờ duyệt)
6. **ForgotPasswordActivity.java** - Quên mật khẩu (gửi mã qua email + verify)
7. **AddBookActivity.java** - Thêm sách mới (validation + lưu Firestore)
8. **EditBookActivity.java** - Chỉnh sửa sách (update Firestore)
9. **AdminMainActivity.java** - Dashboard admin (Bottom Navigation 5 menu)
10. **BookAdapter.java** - Adapter hiển thị danh sách sách (mượn + chi tiết)
11. **BookListFragment.java** - Danh sách sách sinh viên (tìm kiếm không phân biệt hoa thường)
12. **AdminBookManagementFragment.java** - Quản lý sách cho admin (tìm kiếm + thêm/sửa/xóa)

---

## Các File Còn Lại - Hướng Dẫn Comment:

### **MyBorrowsFragment.java**
```
Chức năng: Hiển thị danh sách mượn sách của sinh viên hiện tại
- Lấy uid từ FirebaseAuth.getCurrentUser()
- Truy vấn collection "borrows" với whereEqualTo("userId", uid)
- Hiển thị RecyclerView danh sách mượn
- Phân loại theo trạng thái (Chờ duyệt, Đã duyệt, Đang mượn, Đã trả, Từ chối)
- Có tính năng lọc/tìm kiếm nếu cần

Các method chính:
- onCreateView(): Khởi tạo fragment
- loadMyBorrows(): Tải danh sách mượn của user
- setupRecyclerView(): Setup RecyclerView
```

### **AdminBorrowManagementFragment.java**
```
Chức năng: Admin quản lý tất cả yêu cầu mượn sách
- Load tất cả documents trong "borrows" collection
- Hiển thị RecyclerView với BorrowAdapter
- Duyệt yêu cầu mượn (update status -> "Đã duyệt" + giảm quantity sách)
- Từ chối yêu cầu (update status -> "Từ chối")
- Xác nhận trả sách (update status -> "Đã trả" + tăng quantity sách)
- Tìm kiếm theo tên sinh viên, tên sách

Các method chính:
- onCreateView(): Khởi tạo fragment
- loadAllBorrows(): Tải tất cả yêu cầu mượn
- setupSearchListener(): Tìm kiếm
- filterBorrows(): Filter danh sách
```

### **AdminUserApprovalFragment.java**
```
Chức năng: Admin phê duyệt tài khoản mới (pending)
- Load tất cả users với status = "pending"
- Hiển thị danh sách chờ duyệt
- Duyệt tài khoản: update status -> "approved" 
- Từ chối tài khoản: update status -> "rejected"
- Phân biệt loại tài khoản (student/admin) -> thông báo khác nhau

Các method chính:
- onCreateView(): Khởi tạo
- loadPendingUsers(): Tải users pending
- approveUser(uid): Duyệt user
- rejectUser(uid): Từ chối user
```

### **AdminStatisticsFragment.java** (Đã ghi chú cơ bản)
```
Chức năng: Hiển thị thống kê
- Tổng số sách: db.collection("books").get().size()
- Tổng mượn: db.collection("borrows").get().size()
- Mượn đang: db.collection("borrows").whereEqualTo("status", "Đang mượn")

Các TextView:
- tvTotalBooks: Hiển thị tổng sách
- tvTotalBorrows: Hiển thị tổng mượn
- tvActiveBorrows: Hiển thị mượn đang
```

### **ProfileFragment.java**
```
Chức năng: Hiển thị hồ sơ người dùng + logout
- Lấy thông tin user từ Firestore dựa vào uid hiện tại
- Hiển thị: tên, email, role, trạng thái
- Nút Logout: mAuth.signOut() + quay về LoginActivity

Các method chính:
- onCreateView(): Khởi tạo
- loadUserProfile(): Lấy profile từ Firestore
- setupLogoutButton(): Logout
```

### **BorrowAdapter.java**
```
Chức năng: Adapter hiển thị danh sách mượn
- Mỗi item là: thông tin sách + sinh viên + ngày + trạng thái
- Hiển thị nút duyệt/từ chối (nếu là admin)
- Hiển thị nút xác nhận trả (nếu admin, status = "Đang mượn")
- Click item -> hiển thị chi tiết mượn

ViewHolder fields:
- tvBookTitle, tvStudentName, tvBorrowDate, tvDueDate, tvReturnDate, tvStatus
- btnApprove, btnReject, btnReturn
```

### **UserApprovalAdapter.java**
```
Chức năng: Adapter hiển thị danh sách user chờ duyệt
- Mỗi item là: tên, email, role (Student/Admin), status
- Nút Duyệt (Approve): update status -> "approved"
- Nút Từ chối (Reject): update status -> "rejected"

ViewHolder fields:
- tvName, tvEmail, tvRole, tvStatus
- btnApprove, btnReject
```

### **AdminBookAdapter.java**
```
Chức năng: Adapter admin quản lý sách (với edit/delete)
- Hiển thị sách + số lượng
- Nút Sửa (Edit): startActivity(EditBookActivity) với dữ liệu book
- Nút Xóa (Delete): db.collection("books").document(id).delete()
- Xóa thành công -> gọi callback loadBooks để refresh

ViewHolder fields:
- tvTitle, tvAuthor, tvCategory, tvQuantity
- btnEdit, btnDelete
```

### **MainActivity.java**
```
Chức năng: Activity chính (nếu chưa xóa)
- Nếu còn dùng: chỉ khởi tạo layout cơ bản
- Có thể dùng như launcher screen
```

---

## Cấu Trúc Comment Tiêu Chuẩn Cho Tất Cả Files:

### Trên Đầu Class:
```java
/**
 * TênClass: Mô tả chức năng chính
 * Chức năng:
 * 1. ...
 * 2. ...
 * 3. ...
 */
```

### Trước Mỗi Method:
```java
/**
 * tênMethod: Mô tả ngắn
 * Bước 1: ...
 * Bước 2: ...
 * @param paramName Mô tả tham số
 * @return Mô tả giá trị trả về
 */
```

### Trong Method:
```java
// ========== Tiêu đề phần logic ==========
// Mô tả chi tiết những gì code làm
```

---

## Danh Sách ID Resource Quan Trọng:

### Các EditText (Login/Register):
- `edtEmail`: Email input
- `edtPassword`: Password input
- `edtName`: Tên người dùng
- `edtConfirmPassword`: Xác nhận mật khẩu

### Các Button:
- `btnLogin`: Nút đăng nhập
- `btnRegister`: Nút đăng ký
- `btnSave`: Lưu sách
- `btnUpdate`: Cập nhật sách
- `btnCancel`: Hủy

### Các RecyclerView:
- `recyclerView`: Danh sách chính

### Các Nút FAB:
- `fabAdd`: Nút thêm mới (+)

---

## Firestore Collections Structure:

```
users/
  {uid}
    - uid: String (user id từ Firebase)
    - name: String
    - email: String
    - role: "student" | "admin"
    - status: "pending" | "approved" | "rejected"

books/
  {bookId} (e.g., "B001")
    - id: String
    - title: String
    - author: String
    - category: String
    - quantity: int
    - imageUrl: String
    - description: String

borrows/
  {borrowId}
    - borrowId: String
    - userId: String
    - userName: String
    - bookId: String
    - bookTitle: String
    - borrowDate: Timestamp
    - dueDate: Timestamp
    - returnDate: Timestamp (nullable)
    - status: "Chờ duyệt" | "Đã duyệt" | "Đang mượn" | "Đã trả" | "Từ chối"

verification_codes/
  {userId}
    - code: String (6 số)
    - email: String
    - timestamp: Long
    - expiryTime: Long
```

---

## Các Trạng Thái Quan Trọng:

### User Status:
- **pending**: Chờ admin duyệt (mặc định khi đăng ký)
- **approved**: Được duyệt, có thể dùng app
- **rejected**: Bị từ chối, không thể dùng

### Borrow Status:
- **Chờ duyệt**: Yêu cầu mới, chờ admin phê duyệt
- **Đã duyệt**: Admin chấp nhận, sinh viên có thể lấy sách
- **Đang mượn**: Sinh viên đã lấy sách, đang mượn
- **Đã trả**: Sinh viên đã trả sách xong
- **Từ chối**: Admin từ chối yêu cầu

---

## Tips Khi Đọc Code:

1. **Tìm kiếm không phân biệt hoa thường**: 
   - Luôn convert sang `.toLowerCase().trim()` trước khi so sánh

2. **Validate dữ liệu**:
   - Kiểm tra `TextUtils.isEmpty()` trước khi dùng
   - Kiểm tra `null` trước khi gọi method

3. **Firebase Operations**:
   - `.set()` = ghi toàn bộ document
   - `.update()` = cập nhật một số field
   - `.delete()` = xóa document
   - `.get()` = lấy dữ liệu

4. **RecyclerView**:
   - `adapter.notifyDataSetChanged()` = cập nhật UI

5. **Fragment vs Activity**:
   - Fragment: sử dụng `getContext()` để lấy context
   - Activity: sử dụng `this` hoặc `getApplicationContext()`

---

Tôi đã ghi chú hoàn chỉnh **12 file chính** và cung cấp hướng dẫn chi tiết cho **8 file còn lại**.
Tất cả các file sau đã sẵn sàng để bạn hiểu rõ từng phần code!


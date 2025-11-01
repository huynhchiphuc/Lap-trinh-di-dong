# 📖 TÀI LIỆU CODE - GIẢI THÍCH CHI TIẾT

## 🏗️ Kiến trúc tổng quan

```
┌─────────────────────────────────────────────┐
│           USER INTERFACE (Activities)       │
│  LoginActivity | StudentMain | AdminMain    │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│           FRAGMENTS (Views)                 │
│  BookList | MyBorrows | Profile | Admin...  │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│           ADAPTERS (Data Binding)           │
│  BookAdapter | BorrowAdapter | Admin...     │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│           MODELS (Data Classes)             │
│  Book | User | Borrow                       │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│           FIREBASE (Backend)                │
│  Authentication | Firestore | Storage       │
└─────────────────────────────────────────────┘
```

## 📁 Giải thích từng thành phần

### 1️⃣ MODELS (Các class dữ liệu)

#### Book.java
```java
// Đại diện cho 1 quyển sách trong thư viện
public class Book {
    String id;           // Mã sách (B001, B002...)
    String title;        // Tên sách
    String author;       // Tác giả
    String category;     // Thể loại
    int quantity;        // Số lượng còn lại
    String imageUrl;     // Link ảnh bìa
    String description;  // Mô tả sách
}
```

**Chức năng:**
- Lưu trữ thông tin sách
- Mapping với Firestore collection "books"
- Sử dụng trong RecyclerView

#### User.java
```java
// Đại diện cho người dùng (sinh viên hoặc admin)
public class User {
    String uid;     // ID từ Firebase Auth
    String name;    // Họ tên
    String email;   // Email đăng nhập
    String role;    // "student" hoặc "admin"
}
```

**Chức năng:**
- Phân biệt quyền user
- Lưu thông tin cá nhân
- Mapping với Firestore collection "users"

#### Borrow.java
```java
// Đại diện cho 1 phiếu mượn sách
public class Borrow {
    String borrowId;       // Mã phiếu mượn
    String userId;         // ID người mượn
    String userName;       // Tên người mượn
    String bookId;         // Mã sách
    String bookTitle;      // Tên sách
    Timestamp borrowDate;  // Ngày mượn
    Timestamp dueDate;     // Hạn trả
    Timestamp returnDate;  // Ngày trả (null nếu chưa trả)
    String status;         // "Đang mượn" / "Đã trả"
}
```

**Chức năng:**
- Theo dõi việc mượn/trả
- Lưu lịch sử
- Mapping với Firestore collection "borrows"

---

### 2️⃣ ACTIVITIES (Các màn hình chính)

#### LoginActivity.java
**Mô tả:** Màn hình đăng nhập đầu tiên khi mở app

**Chức năng:**
- Nhập email + password
- Đăng nhập với Firebase Auth
- Kiểm tra role user → Chuyển đến màn hình phù hợp
  - Admin → AdminMainActivity
  - Student → StudentMainActivity
- Link đến RegisterActivity

**Code chính:**
```java
mAuth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            checkUserRoleAndNavigate(userId);
        }
    });
```

#### RegisterActivity.java
**Mô tả:** Màn hình đăng ký tài khoản mới

**Chức năng:**
- Nhập: Họ tên, Email, Password, Vai trò
- Tạo account với Firebase Auth
- Lưu thông tin user vào Firestore collection "users"
- Quay lại LoginActivity

**Code chính:**
```java
mAuth.createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            User user = new User(uid, name, email, role);
            db.collection("users").document(uid).set(user);
        }
    });
```

#### StudentMainActivity.java
**Mô tả:** Màn hình chính cho sinh viên (sau khi login)

**Chức năng:**
- Hiển thị Bottom Navigation với 3 tabs
- Fragment container để swap giữa các màn hình
- Tab 1: BookListFragment (Danh sách sách)
- Tab 2: MyBorrowsFragment (Phiếu mượn)
- Tab 3: ProfileFragment (Tài khoản)

**Layout:** `activity_student_main.xml`

#### AdminMainActivity.java
**Mô tả:** Màn hình chính cho admin

**Chức năng:**
- Bottom Navigation với 4 tabs
- Tab 1: AdminBookManagementFragment (Quản lý sách)
- Tab 2: AdminBorrowManagementFragment (Quản lý mượn)
- Tab 3: AdminStatisticsFragment (Thống kê)
- Tab 4: ProfileFragment (Tài khoản)

---

### 3️⃣ FRAGMENTS (Các tab/màn hình con)

#### BookListFragment.java
**Ai dùng:** Sinh viên
**Chức năng:**
- Load tất cả sách từ Firestore collection "books"
- Hiển thị trong RecyclerView
- Mỗi item có nút "Mượn sách"
- Click item → Xem chi tiết sách

**Code chính:**
```java
db.collection("books").get()
    .addOnSuccessListener(snapshots -> {
        for (DocumentSnapshot doc : snapshots) {
            Book book = doc.toObject(Book.class);
            bookList.add(book);
        }
        adapter.notifyDataSetChanged();
    });
```

#### MyBorrowsFragment.java
**Ai dùng:** Sinh viên
**Chức năng:**
- Load phiếu mượn của user hiện tại
- Query: `where("userId", "==", currentUserId)`
- Hiển thị: Tên sách, ngày mượn, hạn trả, status
- Nút "Trả sách" nếu status = "Đang mượn"

#### ProfileFragment.java
**Ai dùng:** Cả sinh viên và admin
**Chức năng:**
- Hiển thị thông tin: Họ tên, Email, Vai trò
- Nút "Đăng xuất" → FirebaseAuth.signOut()
- Quay về LoginActivity

#### AdminBookManagementFragment.java
**Ai dùng:** Admin
**Chức năng:**
- Xem tất cả sách trong thư viện
- FloatingActionButton "+" để thêm sách (TODO)
- Mỗi item có nút Edit và Delete
- Xóa sách → Update Firestore

#### AdminBorrowManagementFragment.java
**Ai dùng:** Admin
**Chức năng:**
- Xem TẤT CẢ phiếu mượn (của tất cả sinh viên)
- Hiển thị: Người mượn, Sách, Ngày mượn, Hạn trả, Status
- Theo dõi ai đang mượn sách nào

#### AdminStatisticsFragment.java
**Ai dùng:** Admin
**Chức năng:**
- Đếm tổng số sách
- Đếm tổng lượt mượn
- Đếm số phiếu đang mượn
- Hiển thị dạng cards với số to

**Code chính:**
```java
db.collection("books").get()
    .addOnSuccessListener(snapshot -> {
        int totalBooks = snapshot.size();
        tvTotalBooks.setText(String.valueOf(totalBooks));
    });
```

---

### 4️⃣ ADAPTERS (Kết nối dữ liệu với UI)

#### BookAdapter.java
**Mục đích:** Hiển thị danh sách sách cho sinh viên

**Chức năng:**
- Bind data: Book → item_book.xml
- Load ảnh với Glide
- Click "Mượn sách" → Tạo Borrow record
  - Tự động giảm quantity sách
  - Tạo phiếu mượn mới
  - Due date = borrowDate + 14 ngày

**Code chính (Mượn sách):**
```java
private void borrowBook(Book book) {
    String borrowId = db.collection("borrows").document().getId();
    Timestamp dueDate = // +14 days
    
    Borrow borrow = new Borrow(
        borrowId, userId, userName, 
        bookId, bookTitle,
        Timestamp.now(), dueDate, 
        null, "Đang mượn"
    );
    
    db.collection("borrows").document(borrowId).set(borrow);
    db.collection("books").document(bookId)
        .update("quantity", quantity - 1);
}
```

#### BorrowAdapter.java
**Mục đích:** Hiển thị phiếu mượn của sinh viên

**Chức năng:**
- Bind data: Borrow → item_borrow.xml
- Hiển thị ngày tháng với SimpleDateFormat
- Màu status:
  - Đang mượn → Orange
  - Đã trả → Green
- Click "Trả sách" → Update status
  - Tự động tăng quantity sách
  - Set returnDate = now
  - Status = "Đã trả"

#### AdminBookAdapter.java
**Mục đích:** Quản lý sách cho admin

**Chức năng:**
- Bind data: Book → item_admin_book.xml
- Nút Edit → Mở dialog chỉnh sửa (TODO)
- Nút Delete → Xóa khỏi Firestore
- Confirmation dialog trước khi xóa

#### AdminBorrowAdapter.java
**Mục đích:** Xem tất cả phiếu mượn cho admin

**Chức năng:**
- Bind data: Borrow → item_admin_borrow.xml
- Hiển thị thêm: Tên người mượn
- Không có nút action (chỉ xem)
- Màu status tương tự BorrowAdapter

---

## 🔄 FLOW CHÍNH CỦA ỨNG DỤNG

### Flow 1: Sinh viên mượn sách
```
1. StudentMainActivity → Tab "Sách"
2. BookListFragment load books từ Firestore
3. BookAdapter hiển thị trong RecyclerView
4. User click "Mượn sách"
5. BookAdapter.borrowBook():
   - Tạo Borrow record → Firestore "borrows"
   - Giảm quantity → Firestore "books"
6. Toast: "Mượn sách thành công!"
7. Quantity cập nhật trên UI
```

### Flow 2: Sinh viên trả sách
```
1. StudentMainActivity → Tab "Phiếu mượn"
2. MyBorrowsFragment load borrows where userId = current
3. BorrowAdapter hiển thị list
4. User click "Trả sách"
5. BorrowAdapter.returnBook():
   - Update status = "Đã trả" → Firestore
   - Set returnDate = now
   - Tăng quantity sách
6. Toast: "Trả sách thành công!"
7. Item refresh với status mới
```

### Flow 3: Admin xóa sách
```
1. AdminMainActivity → Tab "Quản lý sách"
2. AdminBookManagementFragment load all books
3. AdminBookAdapter hiển thị
4. Admin click icon Delete
5. Show confirmation dialog
6. AdminBookAdapter.deleteBook():
   - db.collection("books").document(id).delete()
7. Remove khỏi list và notifyItemRemoved()
8. Toast: "Xóa sách thành công!"
```

---

## 🔥 FIREBASE OPERATIONS

### Authentication
```java
// Đăng ký
FirebaseAuth.getInstance()
    .createUserWithEmailAndPassword(email, password);

// Đăng nhập
FirebaseAuth.getInstance()
    .signInWithEmailAndPassword(email, password);

// Đăng xuất
FirebaseAuth.getInstance().signOut();

// Get current user
FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
String uid = user.getUid();
```

### Firestore - READ
```java
// Get all documents
db.collection("books").get()
    .addOnSuccessListener(querySnapshot -> {
        for (QueryDocumentSnapshot doc : querySnapshot) {
            Book book = doc.toObject(Book.class);
        }
    });

// Get with filter
db.collection("borrows")
    .whereEqualTo("userId", userId)
    .get();

// Get single document
db.collection("users").document(uid).get()
    .addOnSuccessListener(doc -> {
        String name = doc.getString("name");
    });
```

### Firestore - WRITE
```java
// Create (auto ID)
String id = db.collection("borrows").document().getId();
Borrow borrow = new Borrow(...);
db.collection("borrows").document(id).set(borrow);

// Update
db.collection("books").document(bookId)
    .update("quantity", newQuantity);

// Delete
db.collection("books").document(bookId).delete();
```

---

## 📊 DỮ LIỆU FLOW

### Khi mượn sách:
```
1. Check: book.quantity > 0
2. Create: Borrow document
   - borrowId: auto
   - userId: current user
   - bookId: selected book
   - borrowDate: now
   - dueDate: now + 14 days
   - returnDate: null
   - status: "Đang mượn"
3. Update: book.quantity - 1
```

### Khi trả sách:
```
1. Update Borrow:
   - returnDate: now
   - status: "Đã trả"
2. Update Book:
   - quantity + 1
```

---

## 🎨 UI COMPONENTS

### Layouts
- **activity_*.xml**: Full screen activities
- **fragment_*.xml**: Fragment containers với RecyclerView
- **item_*.xml**: RecyclerView item layouts (CardView)

### Bottom Navigation
- **bottom_nav_menu_student.xml**: 3 items
- **bottom_nav_menu_admin.xml**: 4 items

### Key Components
- `RecyclerView`: Danh sách scrollable
- `CardView`: Item container với shadow
- `FloatingActionButton`: Nút "+" thêm sách
- `TextInputLayout`: Form inputs
- `ProgressBar`: Loading indicator

---

## 🔐 SECURITY & RULES

### Firestore Rules (Test Mode)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2025, 12, 31);
    }
  }
}
```

**⚠️ Cảnh báo:** Đây là test mode, production cần rules chặt chẽ hơn!

---

## 📝 CODING CONVENTIONS

### Naming
- **Activity**: `LoginActivity`, `StudentMainActivity`
- **Fragment**: `BookListFragment`, `ProfileFragment`
- **Adapter**: `BookAdapter`, `BorrowAdapter`
- **Model**: `Book`, `User`, `Borrow`
- **Layout**: `activity_login.xml`, `item_book.xml`
- **ID**: `btnLogin`, `tvTitle`, `edtEmail`

### Package Structure
```
com.example.do_an/
├── activities/    → Các Activity
├── fragments/     → Các Fragment
├── adapters/      → RecyclerView Adapters
└── models/        → Data classes
```

---

**Tài liệu này giải thích toàn bộ cấu trúc code của ứng dụng! 📚**


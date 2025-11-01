# 🐛 KHẮC PHỤC LỖI: Thoát ra màn hình Login khi Mượn/Trả sách

## ❌ VẤN ĐỀ

Khi sinh viên bấm **"Mượn sách"** hoặc **"Trả sách"**, app tự động crash và thoát ra.

## 🔍 NGUYÊN NHÂN

### Nguyên nhân 1: Firebase Auth Session bị null
**Firebase Auth Session bị null** - Khi gọi `mAuth.getCurrentUser()` mà không kiểm tra null, app sẽ crash với `NullPointerException`.

### Nguyên nhân 2: book.getId() trả về null ⚠️ QUAN TRỌNG!
```
java.lang.NullPointerException: Provided document path must not be null.
at com.example.do_an.adapters.BookAdapter.borrowBook()
```

**Nguyên nhân:** Khi load sách từ Firestore bằng `toObject(Book.class)`, field `id` không được tự động map từ Document ID.

### Các tình huống xảy ra:
1. **Session hết hạn**: Firebase token expire
2. **User bị đăng xuất tự động**: Lỗi network
3. **App restart**: Activity bị kill bởi system
4. **Chưa đăng nhập**: Truy cập trực tiếp vào Fragment
5. **Book ID null**: Firestore không map Document ID vào field `id` ⚠️

## ✅ GIẢI PHÁP ĐÃ THỰC HIỆN

### 1. Thêm Null Check trong BookAdapter
```java
private void borrowBook(Book book) {
    // ✅ KIỂM TRA NULL TRƯỚC
    if (mAuth.getCurrentUser() == null) {
        Toast.makeText(context, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
        return;
    }
    
    String userId = mAuth.getCurrentUser().getUid();
    // ... tiếp tục logic mượn sách
}
```

### 2. Thêm Null Check trong MyBorrowsFragment
```java
private void loadMyBorrows() {
    // ✅ KIỂM TRA NULL TRƯỚC
    if (mAuth.getCurrentUser() == null) {
        Toast.makeText(getContext(), "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
        return;
    }
    
    String userId = mAuth.getCurrentUser().getUid();
    // ... load phiếu mượn
}
```

### 3. Thêm Error Handling
```java
// ✅ BẮT LỖI CHO TẤT CẢ FIREBASE OPERATIONS
db.collection("books").get()
    .addOnSuccessListener(snapshot -> {
        // Xử lý thành công
    })
    .addOnFailureListener(e -> {
        // ✅ HIỂN THỊ LỖI CHO USER
        Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    });
```

### 4. Thêm Null Check trong ProfileFragment
```java
private void loadUserInfo() {
    // ✅ KIỂM TRA NULL
    if (mAuth.getCurrentUser() == null) {
        tvName.setText("---");
        tvEmail.setText("---");
        tvRole.setText("---");
        return;
    }
    
    // Load user info...
}
```

### 5. FIX LỖI book.getId() NULL ⚠️ QUAN TRỌNG!
```java
// BookListFragment.java & AdminBookManagementFragment.java
private void loadBooks() {
    db.collection("books").get()
        .addOnSuccessListener(snapshots -> {
            bookList.clear();
            for (QueryDocumentSnapshot document : snapshots) {
                Book book = document.toObject(Book.class);
                
                // ✅ QUAN TRỌNG: Set ID từ document ID
                if (book.getId() == null || book.getId().isEmpty()) {
                    book.setId(document.getId());
                }
                
                bookList.add(book);
            }
            adapter.notifyDataSetChanged();
        });
}
```

### 6. Thêm Validation trong BookAdapter.borrowBook()
```java
private void borrowBook(Book book) {
    // ✅ KIỂM TRA AUTH
    if (mAuth.getCurrentUser() == null) {
        Toast.makeText(context, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
        return;
    }
    
    // ✅ KIỂM TRA BOOK VALID
    if (book == null || book.getId() == null || book.getId().isEmpty()) {
        Toast.makeText(context, "Lỗi: Thông tin sách không hợp lệ!", Toast.LENGTH_SHORT).show();
        return;
    }
    
    if (book.getTitle() == null || book.getTitle().isEmpty()) {
        Toast.makeText(context, "Lỗi: Sách không có tên!", Toast.LENGTH_SHORT).show();
        return;
    }
    
    // Continue with borrow logic...
}
```

## 📝 CÁC FILE ĐÃ SỬA

### ✅ Đã cập nhật:
1. **BookAdapter.java** ⭐ QUAN TRỌNG
   - Thêm null check trong `borrowBook()` cho `getCurrentUser()`
   - Thêm validation cho `book.getId()` và `book.getTitle()`
   - Thêm error handling cho tất cả Firebase operations
   
2. **BorrowAdapter.java**
   - Thêm null check trong `returnBook()`
   - Thêm error handling chi tiết

3. **MyBorrowsFragment.java**
   - Thêm null check trong `loadMyBorrows()`
   - Thêm error handling

4. **ProfileFragment.java**
   - Thêm null check trong `loadUserInfo()`
   - Thêm error handling

5. **BookListFragment.java** ⭐ QUAN TRỌNG
   - **Fix lỗi book.getId() null**: Set ID từ document ID
   - Thêm error handling trong `loadBooks()`

6. **AdminBookManagementFragment.java** ⭐ QUAN TRỌNG
   - **Fix lỗi book.getId() null**: Set ID từ document ID
   - Thêm error handling

## 🧪 CÁCH KIỂM TRA

### Test Case 1: Mượn sách bình thường
```
1. Đăng nhập sinh viên
2. Vào tab "Sách"
3. Click "Mượn sách"
✅ Kỳ vọng: Mượn thành công, không bị thoát
```

### Test Case 2: Trả sách bình thường
```
1. Đăng nhập sinh viên
2. Vào tab "Phiếu mượn"
3. Click "Trả sách"
✅ Kỳ vọng: Trả thành công, không bị thoát
```

### Test Case 3: Session hết hạn
```
1. Đăng nhập
2. Đợi lâu (hoặc force logout trong code)
3. Thử mượn sách
✅ Kỳ vọng: Hiện toast "Vui lòng đăng nhập lại", không crash
```

## 🔐 BẢO VỆ SESSION

### Kiểm tra Auth trong mọi Fragment
```java
@Override
public void onResume() {
    super.onResume();
    
    // Kiểm tra user còn đăng nhập không
    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
        // Redirect về login
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
```

### Thêm vào StudentMainActivity (Optional)
```java
@Override
protected void onResume() {
    super.onResume();
    
    // Check auth khi resume
    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
```

## 🚀 LỢI ÍCH SAU KHI SỬA

### Trước khi sửa:
- ❌ App crash khi session hết hạn
- ❌ Không biết lỗi gì
- ❌ User bị thoát ra đột ngột

### Sau khi sửa:
- ✅ App không crash
- ✅ Hiện thông báo lỗi rõ ràng
- ✅ User biết phải làm gì (đăng nhập lại)
- ✅ UX tốt hơn

## 📊 PATTERN ÁP DỤNG

### Pattern: Check-Then-Execute
```java
// ✅ LUÔN LÀM THẾ NÀY:
if (mAuth.getCurrentUser() == null) {
    // Handle null case
    return;
}

String userId = mAuth.getCurrentUser().getUid();
// Execute logic...
```

### Pattern: Try-Catch for Firebase
```java
// ✅ LUÔN CÓ onFailureListener:
db.collection("books").get()
    .addOnSuccessListener(snapshot -> {
        // Success logic
    })
    .addOnFailureListener(e -> {
        // Handle error - QUAN TRỌNG!
        Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    });
```

## 🎯 BEST PRACTICES

### 1. Null Safety
```java
// ✅ TỐT
String name = doc.getString("name");
tvName.setText(name != null ? name : "---");

// ❌ TỆ
String name = doc.getString("name");
tvName.setText(name); // Có thể null!
```

### 2. Error Messages
```java
// ✅ TỐT - Thông báo cụ thể
Toast.makeText(context, "Lỗi mượn sách: " + e.getMessage(), ...);

// ❌ TỆ - Không rõ ràng
Toast.makeText(context, "Có lỗi xảy ra", ...);
```

### 3. User Experience
```java
// ✅ TỐT - Hướng dẫn user
if (mAuth.getCurrentUser() == null) {
    Toast.makeText(context, "Vui lòng đăng nhập lại!", ...);
    // Có thể redirect về login
    return;
}

// ❌ TỆ - Crash im lặng
String userId = mAuth.getCurrentUser().getUid(); // NullPointerException!
```

## 🔄 TƯƠNG LAI

### Cải tiến thêm (Optional):
1. **Session Management Service**
   - Tự động refresh token
   - Detect session expire
   - Auto re-login

2. **Offline Mode**
   - Firestore offline persistence
   - Queue operations
   - Sync khi online

3. **Loading States**
   - ProgressBar khi load
   - Disable buttons khi processing
   - Prevent double-click

## 📞 HỖ TRỢ

Nếu vẫn gặp lỗi thoát ra màn hình login:

### Bước 1: Check Logcat
```
Android Studio → Logcat → Search "NullPointerException"
Xem dòng nào gây lỗi
```

### Bước 2: Verify Auth State
```java
// Thêm log để debug
Log.d("AUTH", "Current user: " + (mAuth.getCurrentUser() != null ? "Logged in" : "Null"));
```

### Bước 3: Check Firebase Console
```
Firebase Console → Authentication → Users
Xem user có tồn tại không
```

### Bước 4: Clean & Rebuild
```
Build → Clean Project
Build → Rebuild Project
Run lại app
```

## ✅ CHECKLIST SAU KHI SỬA

- [x] Đã thêm null check ở BookAdapter
- [x] Đã thêm null check ở BorrowAdapter
- [x] Đã thêm null check ở MyBorrowsFragment
- [x] Đã thêm null check ở ProfileFragment
- [x] Đã thêm error handling cho tất cả Firebase operations
- [x] Test mượn sách → OK
- [x] Test trả sách → OK
- [x] Test khi chưa đăng nhập → Show toast
- [x] Test khi session hết hạn → Show toast

---

**VẤN ĐỀ ĐÃ ĐƯỢC KHẮC PHỤC! ✅**

App giờ sẽ không bị thoát ra nữa, thay vào đó sẽ hiện thông báo lỗi rõ ràng cho user.

*Last updated: November 2025*


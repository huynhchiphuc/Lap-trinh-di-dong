# 📚 HƯỚNG DẪN SỬ DỤNG TÍNH NĂNG QUẢN LÝ SÁCH

## ✅ ĐÃ THÊM CÁC TÍNH NĂNG MỚI

### 1. ➕ THÊM SÁCH MỚI (Admin)
### 2. ✏️ SỬA THÔNG TIN SÁCH (Admin)
### 3. 🗑️ XÓA SÁCH (Admin) - Đã có sẵn

---

## 📦 CÁC FILE ĐÃ TẠO

### Java Classes (2 files)
- ✅ `AddBookActivity.java` - Màn hình thêm sách
- ✅ `EditBookActivity.java` - Màn hình chỉnh sửa sách

### XML Layouts (2 files)
- ✅ `activity_add_book.xml` - Layout thêm sách
- ✅ `activity_edit_book.xml` - Layout sửa sách

### Files Đã Sửa
- ✅ `AdminBookAdapter.java` - Thêm chức năng Edit
- ✅ `AdminBookManagementFragment.java` - Thêm onResume để refresh
- ✅ `AndroidManifest.xml` - Đăng ký 2 Activity mới

---

## 🚀 CÁCH SỬ DỤNG

### ➕ THÊM SÁCH MỚI

**Bước 1:** Đăng nhập với tài khoản Admin
```
Email: admin@gmail.com
Password: admin123456
```

**Bước 2:** Vào tab "Quản lý sách"

**Bước 3:** Click nút **"+"** (FAB) ở góc dưới bên phải

**Bước 4:** Điền thông tin sách:
- **Tên sách** (bắt buộc): VD: "Lập trình Python"
- **Tác giả** (bắt buộc): VD: "Nguyễn Văn B"
- **Thể loại** (bắt buộc): VD: "Công nghệ thông tin"
- **Số lượng** (bắt buộc): VD: 10
- **Link ảnh** (tùy chọn): VD: https://via.placeholder.com/300x400?text=Python
- **Mô tả** (tùy chọn): VD: "Sách học Python từ cơ bản đến nâng cao"

**Bước 5:** Click nút **"LƯU"**

✅ **Kết quả:**
- Toast hiện: "Thêm sách thành công!"
- Sách mới xuất hiện trong danh sách
- Book ID tự động tạo (B004, B005...)

---

### ✏️ SỬA THÔNG TIN SÁCH

**Bước 1:** Vào tab "Quản lý sách"

**Bước 2:** Tìm sách muốn sửa

**Bước 3:** Click icon **Edit** (✏️) bên phải sách

**Bước 4:** Thay đổi thông tin cần sửa
- Tên sách
- Tác giả
- Thể loại
- Số lượng
- Link ảnh
- Mô tả

**Bước 5:** Click nút **"CẬP NHẬT"**

✅ **Kết quả:**
- Toast hiện: "Cập nhật sách thành công!"
- Thông tin sách được cập nhật
- Quay lại danh sách với dữ liệu mới

---

### 🗑️ XÓA SÁCH

**Bước 1:** Vào tab "Quản lý sách"

**Bước 2:** Tìm sách muốn xóa

**Bước 3:** Click icon **Delete** (🗑️) bên phải sách

**Bước 4:** Click **"Xóa"** trong dialog xác nhận

✅ **Kết quả:**
- Toast hiện: "Xóa sách thành công!"
- Sách biến mất khỏi danh sách
- Sách bị xóa khỏi Firestore

---

## 🧪 TEST CÁC TÍNH NĂNG

### Test 1: Thêm sách mới
```
1. Click nút "+" trong tab "Quản lý sách"
2. Nhập:
   - Tên: "Học Machine Learning"
   - Tác giả: "Trần Văn C"
   - Thể loại: "AI/ML"
   - Số lượng: 3
3. Click "LƯU"

✅ Kỳ vọng:
   - Toast "Thêm sách thành công!"
   - Sách xuất hiện trong danh sách
   - Book ID tự động (VD: B006)
```

### Test 2: Sửa thông tin sách
```
1. Click icon Edit (✏️) của sách vừa thêm
2. Thay đổi số lượng từ 3 → 5
3. Click "CẬP NHẬT"

✅ Kỳ vọng:
   - Toast "Cập nhật sách thành công!"
   - Số lượng hiển thị 5
   - Quay lại danh sách
```

### Test 3: Xóa sách
```
1. Click icon Delete (🗑️) của sách test
2. Click "Xóa" trong dialog

✅ Kỳ vọng:
   - Toast "Xóa sách thành công!"
   - Sách biến mất khỏi danh sách
```

---

## 🔧 BUILD & RUN

### Bước 1: Sync Gradle
```
File → Sync Project with Gradle Files
Đợi sync xong
```

### Bước 2: Clean & Rebuild
```
Build → Clean Project
Build → Rebuild Project
```

### Bước 3: Run App
```
Click Run (▶️)
Chọn emulator/device
```

### Bước 4: Test với tài khoản Admin
```
Đăng nhập admin → Tab "Quản lý sách"
Test thêm/sửa/xóa sách
```

---

## 📋 VALIDATION RULES

### Thêm/Sửa Sách:

#### Bắt buộc nhập:
- ✅ Tên sách (không được rỗng)
- ✅ Tác giả (không được rỗng)
- ✅ Thể loại (không được rỗng)
- ✅ Số lượng (phải là số >= 0)

#### Tùy chọn:
- 📷 Link ảnh (mặc định: placeholder nếu không nhập)
- 📝 Mô tả (mặc định: "Chưa có mô tả")

#### Số lượng:
- Phải là số nguyên
- Phải >= 0
- Không được nhập chữ

---

## 🎨 GIAO DIỆN

### Màn hình Thêm Sách:
```
┌─────────────────────────────┐
│     THÊM SÁCH MỚI          │
├─────────────────────────────┤
│ [Tên sách           ]      │
│ [Tác giả            ]      │
│ [Thể loại           ]      │
│ [Số lượng           ]      │
│ [Link ảnh (tùy chọn)]      │
│ [Mô tả (tùy chọn)   ]      │
│                             │
│  [  HỦY  ]  [  LƯU  ]      │
└─────────────────────────────┘
```

### Màn hình Sửa Sách:
```
┌─────────────────────────────┐
│     CHỈNH SỬA SÁCH         │
├─────────────────────────────┤
│ [Tên sách (pre-filled)]    │
│ [Tác giả (pre-filled) ]    │
│ [Thể loại (pre-filled)]    │
│ [Số lượng (pre-filled)]    │
│ [Link ảnh (pre-filled)]    │
│ [Mô tả (pre-filled)   ]    │
│                             │
│  [  HỦY  ]  [CẬP NHẬT]     │
└─────────────────────────────┘
```

---

## 🔍 TROUBLESHOOTING

### Lỗi 1: Cannot resolve symbol 'activity_edit_book'
**Giải pháp:**
```
1. Build → Clean Project
2. Build → Rebuild Project
3. File → Invalidate Caches → Restart
```

### Lỗi 2: Không thấy nút "+"
**Giải pháp:**
```
- Kiểm tra đã đăng nhập admin chưa
- Vào đúng tab "Quản lý sách"
- Scroll xuống để thấy FAB button
```

### Lỗi 3: Thêm sách bị trùng ID
**Giải pháp:**
```
- Code tự động tạo ID unique
- Check Firestore xem có ID trùng không
- Xóa document trùng nếu có
```

### Lỗi 4: Sau khi thêm/sửa không thấy thay đổi
**Giải pháp:**
```
- onResume() sẽ tự động refresh
- Nếu không refresh, pull down to refresh
- Hoặc thoát ra và vào lại
```

---

## 📊 FLOW HOẠT ĐỘNG

### Flow Thêm Sách:
```
1. Admin click nút "+"
2. Mở AddBookActivity
3. Nhập thông tin
4. Click "LƯU"
5. Validate input
6. Generate Book ID (B00X)
7. Create Book object
8. Save to Firestore "books" collection
9. Show toast "Thêm thành công"
10. Finish activity
11. AdminBookManagementFragment.onResume()
12. Refresh danh sách → Hiển thị sách mới
```

### Flow Sửa Sách:
```
1. Admin click icon Edit
2. Mở EditBookActivity với pre-filled data
3. Thay đổi thông tin
4. Click "CẬP NHẬT"
5. Validate input
6. Create updates Map
7. Update Firestore document
8. Show toast "Cập nhật thành công"
9. Finish activity
10. AdminBookManagementFragment.onResume()
11. Refresh danh sách → Hiển thị thay đổi
```

### Flow Xóa Sách:
```
1. Admin click icon Delete
2. Show confirmation dialog
3. Click "Xóa"
4. Delete from Firestore
5. Remove from local list
6. Notify adapter
7. Show toast "Xóa thành công"
```

---

## 🎯 TÍNH NĂNG ĐÃ HOÀN THÀNH

### Admin:
- [x] Xem danh sách sách
- [x] **Thêm sách mới** ⭐ MỚI
- [x] **Sửa thông tin sách** ⭐ MỚI
- [x] Xóa sách
- [x] Xem phiếu mượn
- [x] Thống kê

### Còn lại để cải tiến:
- [ ] Upload ảnh từ device
- [ ] Tìm kiếm sách
- [ ] Lọc theo thể loại
- [ ] Sắp xếp (theo tên, tác giả, số lượng)
- [ ] Batch delete (xóa nhiều sách)

---

## 📝 CHECKLIST

- [x] Tạo AddBookActivity.java
- [x] Tạo EditBookActivity.java
- [x] Tạo activity_add_book.xml
- [x] Tạo activity_edit_book.xml
- [x] Sửa AdminBookAdapter - thêm Edit
- [x] Sửa AdminBookManagementFragment - thêm FAB handler
- [x] Đăng ký Activities trong AndroidManifest
- [x] Thêm validation
- [x] Thêm error handling
- [ ] Test thêm sách → Bạn test
- [ ] Test sửa sách → Bạn test
- [ ] Test xóa sách → Bạn test

---

## 🎉 KẾT LUẬN

**Ứng dụng giờ đã có đầy đủ tính năng CRUD:**
- ✅ **C**reate - Thêm sách mới
- ✅ **R**ead - Xem danh sách sách
- ✅ **U**pdate - Sửa thông tin sách
- ✅ **D**elete - Xóa sách

**Hãy build và test ngay! 🚀**

---

*Last updated: November 1, 2025*
*Features: Add, Edit, Delete Books*


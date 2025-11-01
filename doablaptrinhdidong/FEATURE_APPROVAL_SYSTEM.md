# ✅ TÍNH NĂNG DUYỆT - APPROVAL SYSTEM

## 🎉 ĐÃ THÊM TÍNH NĂNG MỚI

### 1️⃣ DUYỆT TÀI KHOẢN USER (Admin)
- ✅ User mới đăng ký → Status = "pending" (chờ duyệt)
- ✅ Admin duyệt → Status = "approved" (cho phép login)
- ✅ Admin từ chối → Status = "rejected" (không cho login)

### 2️⃣ DUYỆT YÊU CẦU MƯỢN SÁCH (Admin)
- ✅ Sinh viên mượn sách → Status = "Chờ duyệt"
- ✅ Admin duyệt → Status = "Đang mượn" + giảm quantity
- ✅ Admin từ chối → Status = "Từ chối"

---

## 📦 FILES ĐÃ TẠO/SỬA

### Mới tạo (4 files):
1. `AdminUserApprovalFragment.java` - Fragment duyệt user
2. `UserApprovalAdapter.java` - Adapter hiển thị user chờ duyệt
3. `fragment_admin_user_approval.xml` - Layout fragment
4. `item_user_approval.xml` - Layout item user

### Đã sửa (10 files):
5. `User.java` - Thêm field `status`
6. `Borrow.java` - Cập nhật comment status
7. `RegisterActivity.java` - Set status = pending cho user mới
8. `LoginActivity.java` - Kiểm tra status trước khi login
9. `BookAdapter.java` - Mượn sách tạo status = Chờ duyệt
10. `BorrowAdapter.java` - Cập nhật màu cho các status mới
11. `AdminBorrowAdapter.java` - Thêm nút duyệt/từ chối
12. `AdminMainActivity.java` - Thêm tab duyệt user
13. `bottom_nav_menu_admin.xml` - Thêm menu item
14. `item_admin_borrow.xml` - Thêm nút duyệt/từ chối

---

## 🚀 CÁCH SỬ DỤNG

### 📋 DUYỆT TÀI KHOẢN USER

#### Sinh viên đăng ký:
```
1. Mở app → Đăng ký
2. Chọn vai trò "Sinh viên"
3. Click "Đăng ký"
✅ Thông báo: "Đăng ký thành công! Vui lòng chờ admin duyệt tài khoản."
❌ KHÔNG thể đăng nhập được (status = pending)
```

#### Admin duyệt:
```
1. Đăng nhập Admin
2. Tab "Duyệt user" (tab thứ 3)
3. Xem danh sách user chờ duyệt
4. Click "Duyệt" hoặc "Từ chối"
✅ User được duyệt → Có thể đăng nhập
❌ User bị từ chối → Không thể đăng nhập
```

#### Sinh viên đăng nhập sau khi được duyệt:
```
1. Mở app → Đăng nhập
2. Nhập email/password
3. Click "Đăng nhập"
✅ Nếu approved → Vào được app
⏳ Nếu pending → "Tài khoản đang chờ admin duyệt"
❌ Nếu rejected → "Tài khoản đã bị từ chối"
```

---

### 📚 DUYỆT YÊU CẦU MƯỢN SÁCH

#### Sinh viên tạo yêu cầu:
```
1. Đăng nhập sinh viên
2. Tab "Sách" → Chọn sách
3. Click "Mượn sách" → Xác nhận
✅ Thông báo: "Đã gửi yêu cầu mượn sách! Vui lòng chờ admin duyệt."
⏳ Status = "Chờ duyệt"
❌ Chưa giảm quantity (chờ admin duyệt)
```

#### Admin duyệt yêu cầu:
```
1. Đăng nhập Admin
2. Tab "Mượn trả"
3. Tìm yêu cầu có status "Chờ duyệt"
4. Click "Duyệt" hoặc "Từ chối"

✅ Nếu duyệt:
   - Status → "Đang mượn"
   - Quantity giảm 1
   - Sinh viên có thể trả sách

❌ Nếu từ chối:
   - Status → "Từ chối"
   - Quantity KHÔNG thay đổi
   - Sinh viên không mượn được
```

#### Sinh viên kiểm tra:
```
1. Tab "Phiếu mượn"
2. Xem status:
   ⏳ "Chờ duyệt" → Chờ admin
   ✅ "Đang mượn" → Đã duyệt, có nút trả sách
   ❌ "Từ chối" → Yêu cầu bị từ chối
```

---

## 🔄 FLOW HOẠT ĐỘNG

### Flow 1: Đăng ký → Duyệt → Đăng nhập

```
[Sinh viên]
1. Đăng ký tài khoản
   ↓
2. Firebase Auth tạo user
   ↓
3. Firestore lưu user với status = "pending"
   ↓
4. Thông báo: "Chờ admin duyệt"

[Admin]
5. Vào tab "Duyệt user"
   ↓
6. Xem danh sách pending
   ↓
7. Click "Duyệt" → Update status = "approved"

[Sinh viên]
8. Đăng nhập
   ↓
9. LoginActivity check status
   ↓
10. Status = "approved" → Cho vào app ✅
    Status = "pending" → Signout + thông báo ⏳
    Status = "rejected" → Signout + thông báo ❌
```

### Flow 2: Mượn sách → Duyệt → Đang mượn

```
[Sinh viên]
1. Click "Mượn sách"
   ↓
2. Tạo Borrow với status = "Chờ duyệt"
   ↓
3. KHÔNG giảm quantity
   ↓
4. Thông báo: "Đã gửi yêu cầu"

[Admin]
5. Vào tab "Mượn trả"
   ↓
6. Xem yêu cầu "Chờ duyệt"
   ↓
7. Click "Duyệt"
   ↓
8. Update status = "Đang mượn"
   ↓
9. Giảm quantity sách

[Sinh viên]
10. Vào "Phiếu mượn"
    ↓
11. Thấy status = "Đang mượn" ✅
    ↓
12. Có nút "Trả sách"
```

---

## 🎨 GIAO DIỆN

### Admin - Tab "Duyệt user":
```
┌─────────────────────────────┐
│     DUYỆT TÀI KHOẢN         │
├─────────────────────────────┤
│ ┌─────────────────────────┐ │
│ │ Nguyễn Văn A            │ │
│ │ student@gmail.com       │ │
│ │ Vai trò: Sinh viên      │ │
│ │ [Từ chối] [Duyệt]       │ │
│ └─────────────────────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ Trần Thị B              │ │
│ │ student2@gmail.com      │ │
│ │ Vai trò: Sinh viên      │ │
│ │ [Từ chối] [Duyệt]       │ │
│ └─────────────────────────┘ │
└─────────────────────────────┘
```

### Admin - Tab "Mượn trả" (có yêu cầu chờ duyệt):
```
┌─────────────────────────────┐
│    QUẢN LÝ MƯỢN TRẢ         │
├─────────────────────────────┤
│ ┌─────────────────────────┐ │
│ │ Người mượn: Nguyễn Văn A│ │
│ │ Sách: Lập trình Java    │ │
│ │ Ngày mượn: 01/11/2025   │ │
│ │ Hạn trả: 15/11/2025     │ │
│ │ Trạng thái: Chờ duyệt   │ │
│ │ [Từ chối] [Duyệt]       │ │
│ └─────────────────────────┘ │
└─────────────────────────────┘
```

---

## 🎯 STATUS STATES

### User Status:
- 🟡 **pending** - Chờ admin duyệt (mặc định khi đăng ký)
- 🟢 **approved** - Đã duyệt, cho phép login
- 🔴 **rejected** - Bị từ chối, không cho login

### Borrow Status:
- 🟡 **Chờ duyệt** - Yêu cầu mới tạo, chờ admin
- 🔵 **Đang mượn** - Đã duyệt, đang mượn sách
- 🟢 **Đã trả** - Đã trả sách xong
- 🔴 **Từ chối** - Yêu cầu bị admin từ chối

---

## 🧪 TEST SCENARIOS

### Test 1: Đăng ký → Chờ duyệt
```
1. Đăng ký tài khoản sinh viên mới
✅ Toast: "Vui lòng chờ admin duyệt"

2. Thử đăng nhập ngay
❌ Toast: "Tài khoản đang chờ admin duyệt"
✅ Bị logout ra
```

### Test 2: Admin duyệt user
```
1. Đăng nhập admin
2. Tab "Duyệt user"
✅ Thấy danh sách user pending

3. Click "Duyệt" cho user
✅ Toast: "Đã duyệt tài khoản"
✅ User biến mất khỏi danh sách

4. User login lại
✅ Vào được app
```

### Test 3: Mượn sách → Chờ duyệt
```
1. Sinh viên mượn sách
✅ Toast: "Đã gửi yêu cầu... chờ admin duyệt"

2. Tab "Phiếu mượn"
✅ Status: "Chờ duyệt" (màu cam)
❌ KHÔNG có nút "Trả sách"

3. Kiểm tra quantity sách
✅ Quantity KHÔNG thay đổi (chưa giảm)
```

### Test 4: Admin duyệt mượn sách
```
1. Admin → Tab "Mượn trả"
✅ Thấy yêu cầu "Chờ duyệt"
✅ Có nút Duyệt/Từ chối

2. Click "Duyệt"
✅ Toast: "Đã duyệt yêu cầu"
✅ Status → "Đang mượn"
✅ Quantity giảm 1

3. Sinh viên kiểm tra
✅ Status: "Đang mượn" (màu xanh)
✅ CÓ nút "Trả sách"
```

---

## 📊 THỐNG KÊ THAY ĐỔI

**Trước:**
- User đăng ký → Đăng nhập ngay ❌
- Mượn sách → Giảm quantity ngay ❌
- Không kiểm soát được ❌

**Sau:**
- User đăng ký → Chờ admin duyệt ✅
- Mượn sách → Chờ admin duyệt ✅
- Admin kiểm soát toàn bộ ✅
- Tránh spam/lạm dụng ✅

---

## 🔐 BẢO MẬT

### Tài khoản Admin:
- Admin tự động `status = "approved"` khi đăng ký
- KHÔNG cần chờ duyệt
- Đăng nhập ngay được

### Tài khoản Sinh viên:
- Mặc định `status = "pending"`
- PHẢI chờ admin duyệt
- Không thể login nếu pending/rejected

---

## 🎉 KẾT LUẬN

**App giờ có hệ thống duyệt hoàn chỉnh:**
- ✅ Duyệt tài khoản user
- ✅ Duyệt yêu cầu mượn sách
- ✅ Kiểm soát toàn bộ hoạt động
- ✅ Admin có quyền quản lý tuyệt đối

**Build và test ngay! 🚀**

---

*Last updated: November 1, 2025*
*Features: Approval System Complete*


# 📝 TODO LIST - CÁC TÍNH NĂNG CẦN BỔ SUNG

## 🔥 Ưu tiên CAO (Cần làm ngay)

### 1. ✅ Thêm sách mới (Admin) - HOÀN THÀNH
**File đã tạo:**
- ✅ `AddBookActivity.java`
- ✅ `activity_add_book.xml`

**Chức năng:**
- Form nhập: Tên sách, Tác giả, Thể loại, Số lượng, Mô tả
- Upload ảnh từ device
- Save vào Firestore collection "books"
- Generate bookId tự động (B00X)

**Code mẫu:**
```java
// AdminBookManagementFragment.java
fabAdd.setOnClickListener(v -> {
    Intent intent = new Intent(getActivity(), AddBookActivity.class);
    startActivity(intent);
});

// AddBookActivity.java
private void saveBook() {
    String bookId = "B" + String.format("%03d", nextId);
    Book book = new Book(bookId, title, author, category, quantity, imageUrl, description);
    
    db.collection("books").document(bookId).set(book)
        .addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });
}
```

---

### 2. ✅ Sửa thông tin sách (Admin) - HOÀN THÀNH
**File đã tạo:**
- ✅ `EditBookActivity.java`
- ✅ `activity_edit_book.xml`

**Chức năng:**
- Pre-fill data từ sách đã chọn
- Cho phép edit tất cả fields
- Update vào Firestore

**Code mẫu:**
```java
// AdminBookAdapter.java
btnEdit.setOnClickListener(v -> {
    Intent intent = new Intent(context, EditBookActivity.class);
    intent.putExtra("bookId", book.getId());
    intent.putExtra("title", book.getTitle());
    // ... other fields
    context.startActivity(intent);
});

// EditBookActivity.java
private void updateBook() {
    Map<String, Object> updates = new HashMap<>();
    updates.put("title", newTitle);
    updates.put("author", newAuthor);
    // ... other fields
    
    db.collection("books").document(bookId).update(updates)
        .addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });
}
```

---

### 3. Upload ảnh sách ⭐⭐
**Thư viện cần thêm:**
```kotlin
// gradle/libs.versions.toml
[versions]
imagePicker = "2.1"

[libraries]
imagePicker = { group = "com.github.dhaval2404", name = "imagepicker", version.ref = "imagePicker" }
```

**Chức năng:**
- Chọn ảnh từ Gallery
- Chụp ảnh mới
- Upload lên Firebase Storage
- Lấy URL và lưu vào Firestore

**Code mẫu:**
```java
// AddBookActivity.java
private void selectImage() {
    ImagePicker.with(this)
        .crop()
        .compress(1024)
        .maxResultSize(1080, 1080)
        .start();
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
        Uri imageUri = data.getData();
        uploadImageToFirebase(imageUri);
    }
}

private void uploadImageToFirebase(Uri imageUri) {
    StorageReference ref = FirebaseStorage.getInstance()
        .getReference("books/" + bookId + ".jpg");
    
    ref.putFile(imageUri)
        .addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                imageUrl = uri.toString();
                // Save to Firestore
            });
        });
}
```

---

## 🟡 Ưu tiên TRUNG BÌNH

### 4. Tìm kiếm sách ⭐⭐
**File cần sửa:**
- `fragment_book_list.xml` - Thêm SearchView
- `BookListFragment.java` - Implement search logic

**Chức năng:**
- Search theo tên sách
- Search theo tác giả
- Hiển thị kết quả real-time

**Code mẫu:**
```java
// BookListFragment.java
SearchView searchView = view.findViewById(R.id.searchView);
searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
    @Override
    public boolean onQueryTextChange(String newText) {
        filter(newText);
        return true;
    }
});

private void filter(String text) {
    List<Book> filteredList = new ArrayList<>();
    for (Book book : bookList) {
        if (book.getTitle().toLowerCase().contains(text.toLowerCase()) ||
            book.getAuthor().toLowerCase().contains(text.toLowerCase())) {
            filteredList.add(book);
        }
    }
    adapter.filterList(filteredList);
}
```

---

### 5. Lọc theo thể loại ⭐⭐
**File cần tạo:**
- Menu dropdown hoặc Chips

**Chức năng:**
- Hiển thị danh sách thể loại
- Click để lọc sách theo thể loại
- "Tất cả" để reset filter

**Code mẫu:**
```java
// BookListFragment.java
Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String category = parent.getItemAtPosition(position).toString();
        if (category.equals("Tất cả")) {
            adapter.updateList(bookList);
        } else {
            filterByCategory(category);
        }
    }
});

private void filterByCategory(String category) {
    List<Book> filtered = new ArrayList<>();
    for (Book book : bookList) {
        if (book.getCategory().equals(category)) {
            filtered.add(book);
        }
    }
    adapter.updateList(filtered);
}
```

---

### 6. Quên mật khẩu ⭐
**File cần tạo:**
- `ForgotPasswordActivity.java`
- `activity_forgot_password.xml`

**Chức năng:**
- Nhập email
- Gửi email reset password qua Firebase
- Hiển thị thông báo thành công

**Code mẫu:**
```java
// ForgotPasswordActivity.java
private void sendResetEmail() {
    String email = edtEmail.getText().toString().trim();
    
    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        .addOnSuccessListener(aVoid -> {
            Toast.makeText(this, 
                "Email đặt lại mật khẩu đã được gửi!", 
                Toast.LENGTH_SHORT).show();
            finish();
        })
        .addOnFailureListener(e -> {
            Toast.makeText(this, 
                "Lỗi: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        });
}
```

---

## 🟢 Tính năng NÂNG CAO

### 7. Thông báo sắp hết hạn ⭐⭐⭐
**Thư viện cần thêm:**
```kotlin
// Firebase Cloud Messaging
firebase-messaging = { group = "com.google.firebase", name = "firebase-messaging" }
```

**Chức năng:**
- Check phiếu mượn sắp hết hạn (< 3 ngày)
- Gửi notification cho user
- Background service hoặc Cloud Function

**Code mẫu:**
```java
// NotificationService.java
private void checkOverdueBorrows() {
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    
    db.collection("borrows")
        .whereEqualTo("userId", userId)
        .whereEqualTo("status", "Đang mượn")
        .get()
        .addOnSuccessListener(snapshots -> {
            for (QueryDocumentSnapshot doc : snapshots) {
                Borrow borrow = doc.toObject(Borrow.class);
                long daysLeft = calculateDaysLeft(borrow.getDueDate());
                
                if (daysLeft <= 3 && daysLeft >= 0) {
                    sendNotification("Sách sắp hết hạn", 
                        "Sách \"" + borrow.getBookTitle() + 
                        "\" sẽ hết hạn trong " + daysLeft + " ngày!");
                }
            }
        });
}

private void sendNotification(String title, String message) {
    NotificationCompat.Builder builder = 
        new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH);
    
    NotificationManager manager = 
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    manager.notify(NOTIFICATION_ID, builder.build());
}
```

---

### 8. QR Code mượn sách ⭐⭐⭐
**Thư viện cần thêm:**
```kotlin
[versions]
zxing = "4.3.0"

[libraries]
zxing = { group = "com.google.zxing", name = "core", version.ref = "zxing" }
```

**Chức năng:**
- Generate QR code cho mỗi sách (chứa bookId)
- Scan QR để mượn nhanh
- Camera permission

**Code mẫu:**
```java
// QRScannerActivity.java
private void scanQRCode() {
    IntentIntegrator integrator = new IntentIntegrator(this);
    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
    integrator.setPrompt("Quét mã QR của sách");
    integrator.setCameraId(0);
    integrator.setBeepEnabled(true);
    integrator.initiateScan();
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    if (result != null && result.getContents() != null) {
        String bookId = result.getContents();
        borrowBookByQR(bookId);
    }
}
```

---

### 9. Biểu đồ thống kê ⭐⭐⭐
**Thư viện cần thêm:**
```kotlin
[versions]
mpandroidchart = "v3.1.0"

[libraries]
mpandroidchart = { group = "com.github.PhilJay", name = "MPAndroidChart", version.ref = "mpandroidchart" }
```

**Chức năng:**
- Biểu đồ cột: Số lượt mượn theo tháng
- Biểu đồ tròn: Phân bổ theo thể loại
- Biểu đồ đường: xu hướng mượn sách

**Code mẫu:**
```java
// AdminStatisticsFragment.java
private void setupBarChart() {
    BarChart barChart = view.findViewById(R.id.barChart);
    
    ArrayList<BarEntry> entries = new ArrayList<>();
    // Load data từ Firestore theo tháng
    entries.add(new BarEntry(1, borrowsInJan));
    entries.add(new BarEntry(2, borrowsInFeb));
    // ...
    
    BarDataSet dataSet = new BarDataSet(entries, "Lượt mượn theo tháng");
    dataSet.setColor(Color.BLUE);
    
    BarData data = new BarData(dataSet);
    barChart.setData(data);
    barChart.invalidate();
}
```

---

### 10. Export báo cáo ⭐⭐
**Chức năng:**
- Export danh sách mượn ra PDF
- Export thống kê ra Excel
- Share qua email

**Code mẫu:**
```java
// AdminStatisticsFragment.java
private void exportToPDF() {
    PdfDocument document = new PdfDocument();
    PdfDocument.PageInfo pageInfo = 
        new PdfDocument.PageInfo.Builder(595, 842, 1).create();
    PdfDocument.Page page = document.startPage(pageInfo);
    
    Canvas canvas = page.getCanvas();
    Paint paint = new Paint();
    
    // Vẽ nội dung lên canvas
    canvas.drawText("BÁO CÁO THỐNG KÊ", 100, 100, paint);
    // ... more content
    
    document.finishPage(page);
    
    // Save file
    File file = new File(getExternalFilesDir(null), "report.pdf");
    try {
        document.writeTo(new FileOutputStream(file));
        Toast.makeText(this, "Export thành công!", Toast.LENGTH_SHORT).show();
    } catch (IOException e) {
        e.printStackTrace();
    }
    document.close();
}
```

---

## 🔵 Cải tiến UI/UX

### 11. Loading states
- Hiển thị ProgressBar khi load data
- Skeleton screens
- Pull to refresh

### 12. Empty states
- Thông báo khi không có sách
- Thông báo khi không có phiếu mượn
- Hướng dẫn user action tiếp theo

### 13. Error handling
- Try-catch cho tất cả operations
- Hiển thị lỗi user-friendly
- Retry button khi lỗi network

### 14. Animations
- Transition giữa các màn hình
- RecyclerView item animations
- Button ripple effects

---

## 🟣 Tối ưu Performance

### 15. Pagination
- Load sách theo batch (20 items/page)
- Infinite scroll
- Giảm tải Firestore queries

### 16. Caching
- Cache ảnh với Glide
- Firestore offline persistence
- SharedPreferences cho settings

### 17. Database optimization
- Index Firestore fields
- Composite queries
- Batch writes

---

## 📊 PRIORITY MATRIX

```
│ Urgent & Important     │ Important but Not Urgent │
│ 1. Add book (Admin)    │ 4. Search books          │
│ 2. Edit book (Admin)   │ 5. Filter by category    │
│ 3. Upload images       │ 6. Forgot password       │
├────────────────────────┼──────────────────────────┤
│ Urgent but Less Imp    │ Nice to Have             │
│ 11. Loading states     │ 7. Notifications         │
│ 12. Empty states       │ 8. QR code               │
│ 13. Error handling     │ 9. Charts                │
```

---

## 🎯 ROADMAP

### Sprint 1 (Tuần 1)
- [x] Setup project
- [x] Firebase integration
- [x] Authentication
- [x] Basic CRUD

### Sprint 2 (Tuần 2)
- [ ] Add/Edit book
- [ ] Upload images
- [ ] Search & filter

### Sprint 3 (Tuần 3)
- [ ] Notifications
- [ ] QR code
- [ ] UI improvements

### Sprint 4 (Tuần 4)
- [ ] Charts & statistics
- [ ] Export reports
- [ ] Performance optimization
- [ ] Testing & bug fixes

---

## 📝 NOTES

- Mỗi feature nên tạo branch riêng
- Test kỹ trước khi merge
- Update documentation sau khi hoàn thành
- Screenshot các features mới

---

**Last updated: November 2025**


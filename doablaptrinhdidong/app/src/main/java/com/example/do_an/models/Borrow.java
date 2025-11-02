package com.example.do_an.models;

import com.google.firebase.Timestamp;

/**
 * Model class: Borrow (Mượn sách)
 * Lớp này đại diện cho một yêu cầu mượn sách
 * Ghi lại tất cả thông tin liên quan đến quá trình mượn: người mượn, sách, ngày mượn, ngày hết hạn, v.v.
 *
 * Các trạng thái mượn:
 * - "Chờ duyệt": Yêu cầu mới, chờ admin phê duyệt
 * - "Đã duyệt": Admin đã chấp nhận, học sinh có thể lấy sách
 * - "Đang mượn": Học sinh đã lấy sách, đang mượn
 * - "Đã trả": Học sinh trả sách thành công
 * - "Từ chối": Admin từ chối yêu cầu mượn
 */
public class Borrow {
    // ============ Các thuộc tính của yêu cầu mượn ============
    private String borrowId;    // ID duy nhất cho yêu cầu mượn
    private String userId;      // ID của người mượn (từ Firebase Auth)
    private String userName;    // Tên của người mượn
    private String bookId;      // ID của sách được mượn
    private String bookTitle;   // Tên sách được mượn
    private Timestamp borrowDate;  // Ngày yêu cầu mượn / ngày bắt đầu mượn
    private Timestamp dueDate;     // Ngày hết hạn mượn (ngày phải trả)
    private Timestamp returnDate;  // Ngày thực tế trả sách (null nếu chưa trả)
    private String status;      // Trạng thái: "Chờ duyệt", "Đã duyệt", "Đang mượn", "Đã trả", "Từ chối"

    /**
     * Constructor rỗng - bắt buộc cho Firebase Firestore
     * Firebase cần constructor này để chuyển đổi dữ liệu từ Firestore về đối tượng Java
     */
    public Borrow() {
    }

    /**
     * Constructor đầy đủ - khởi tạo yêu cầu mượn với tất cả thông tin
     * @param borrowId ID duy nhất của yêu cầu mượn
     * @param userId ID người mượn
     * @param userName Tên người mượn
     * @param bookId ID sách được mượn
     * @param bookTitle Tên sách được mượn
     * @param borrowDate Ngày mượn
     * @param dueDate Ngày hết hạn
     * @param returnDate Ngày trả (có thể null nếu chưa trả)
     * @param status Trạng thái mượn
     */
    public Borrow(String borrowId, String userId, String userName, String bookId, String bookTitle,
                  Timestamp borrowDate, Timestamp dueDate, Timestamp returnDate, String status) {
        this.borrowId = borrowId;
        this.userId = userId;
        this.userName = userName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // ============ Getter và Setter cho ID mượn ============
    /**
     * Lấy ID duy nhất của yêu cầu mượn
     * @return ID mượn
     */
    public String getBorrowId() {
        return borrowId;
    }

    /**
     * Thiết lập ID mượn
     * @param borrowId ID mượn
     */
    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    // ============ Getter và Setter cho ID người dùng ============
    /**
     * Lấy ID người mượn
     * @return ID người dùng
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Thiết lập ID người mượn
     * @param userId ID người dùng
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // ============ Getter và Setter cho Tên người dùng ============
    /**
     * Lấy tên người mượn
     * @return Tên người dùng
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Thiết lập tên người mượn
     * @param userName Tên người dùng
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    // ============ Getter và Setter cho ID sách ============
    /**
     * Lấy ID sách được mượn
     * @return ID sách
     */
    public String getBookId() {
        return bookId;
    }

    /**
     * Thiết lập ID sách được mượn
     * @param bookId ID sách
     */
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    // ============ Getter và Setter cho Tên sách ============
    /**
     * Lấy tên sách được mượn
     * @return Tên sách
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Thiết lập tên sách được mượn
     * @param bookTitle Tên sách
     */
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    // ============ Getter và Setter cho Ngày mượn ============
    /**
     * Lấy ngày mượn (ngày yêu cầu hoặc ngày bắt đầu mượn)
     * @return Timestamp ngày mượn
     */
    public Timestamp getBorrowDate() {
        return borrowDate;
    }

    /**
     * Thiết lập ngày mượn
     * @param borrowDate Timestamp ngày mượn
     */
    public void setBorrowDate(Timestamp borrowDate) {
        this.borrowDate = borrowDate;
    }

    // ============ Getter và Setter cho Ngày hết hạn ============
    /**
     * Lấy ngày hết hạn (ngày phải trả sách)
     * @return Timestamp ngày hết hạn
     */
    public Timestamp getDueDate() {
        return dueDate;
    }

    /**
     * Thiết lập ngày hết hạn
     * @param dueDate Timestamp ngày hết hạn
     */
    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    // ============ Getter và Setter cho Ngày trả ============
    /**
     * Lấy ngày trả thực tế
     * @return Timestamp ngày trả (hoặc null nếu chưa trả)
     */
    public Timestamp getReturnDate() {
        return returnDate;
    }

    /**
     * Thiết lập ngày trả
     * @param returnDate Timestamp ngày trả
     */
    public void setReturnDate(Timestamp returnDate) {
        this.returnDate = returnDate;
    }

    // ============ Getter và Setter cho Trạng thái ============
    /**
     * Lấy trạng thái của yêu cầu mượn
     * @return Trạng thái: "Chờ duyệt", "Đã duyệt", "Đang mượn", "Đã trả", "Từ chối"
     */
    public String getStatus() {
        return status;
    }

    /**
     * Thiết lập trạng thái của yêu cầu mượn
     * @param status Trạng thái mượn
     */
    public void setStatus(String status) {
        this.status = status;
    }
}


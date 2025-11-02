package com.example.do_an.models;

/**
 * Model class: Book (Sách)
 * Lớp này đại diện cho một cuốn sách trong thư viện
 * Được sử dụng để lưu trữ và truyền dữ liệu sách giữa các thành phần của ứng dụng
 */
public class Book {
    // ============ Các thuộc tính của sách ============
    private String id;           // ID duy nhất của sách (khóa chính trong Firestore)
    private String title;        // Tên sách
    private String author;       // Tác giả sách
    private String category;     // Thể loại sách (ví dụ: Khoa học, Văn học, v.v.)
    private int quantity;        // Số lượng sách có sẵn trong thư viện
    private String imageUrl;     // URL của ảnh bìa sách
    private String description;  // Mô tả chi tiết về sách

    /**
     * Constructor rỗng - bắt buộc cho Firebase Firestore
     * Firebase cần constructor này để chuyển đổi dữ liệu từ Firestore về đối tượng Java
     */
    public Book() {
    }

    /**
     * Constructor đầy đủ - khởi tạo Book với tất cả các thông tin
     * @param id ID duy nhất của sách
     * @param title Tên sách
     * @param author Tác giả sách
     * @param category Thể loại sách
     * @param quantity Số lượng có sẵn
     * @param imageUrl URL của ảnh bìa
     * @param description Mô tả sách
     */
    public Book(String id, String title, String author, String category, int quantity, String imageUrl, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    // ============ Getter và Setter cho ID ============
    /**
     * Lấy ID sách
     * @return ID duy nhất của sách
     */
    public String getId() {
        return id;
    }

    /**
     * Thiết lập ID sách
     * @param id ID duy nhất của sách
     */
    public void setId(String id) {
        this.id = id;
    }

    // ============ Getter và Setter cho Tên sách ============
    /**
     * Lấy tên sách
     * @return Tên sách
     */
    public String getTitle() {
        return title;
    }

    /**
     * Thiết lập tên sách
     * @param title Tên sách
     */
    public void setTitle(String title) {
        this.title = title;
    }

    // ============ Getter và Setter cho Tác giả ============
    /**
     * Lấy tác giả sách
     * @return Tác giả sách
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Thiết lập tác giả sách
     * @param author Tác giả sách
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    // ============ Getter và Setter cho Thể loại ============
    /**
     * Lấy thể loại sách
     * @return Thể loại sách
     */
    public String getCategory() {
        return category;
    }

    /**
     * Thiết lập thể loại sách
     * @param category Thể loại sách
     */
    public void setCategory(String category) {
        this.category = category;
    }

    // ============ Getter và Setter cho Số lượng ============
    /**
     * Lấy số lượng sách có sẵn
     * @return Số lượng sách
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Thiết lập số lượng sách
     * @param quantity Số lượng sách
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // ============ Getter và Setter cho URL ảnh ============
    /**
     * Lấy URL của ảnh bìa sách
     * @return URL ảnh bìa
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Thiết lập URL của ảnh bìa sách
     * @param imageUrl URL ảnh bìa
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // ============ Getter và Setter cho Mô tả ============
    /**
     * Lấy mô tả của sách
     * @return Mô tả chi tiết về sách
     */
    public String getDescription() {
        return description;
    }

    /**
     * Thiết lập mô tả của sách
     * @param description Mô tả chi tiết về sách
     */
    public void setDescription(String description) {
        this.description = description;
    }
}


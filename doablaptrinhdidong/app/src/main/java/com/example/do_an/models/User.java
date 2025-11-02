package com.example.do_an.models;

/**
 * Model class: User (Người dùng)
 * Lớp này đại diện cho một người dùng trong ứng dụng thư viện
 * Bao gồm thông tin cá nhân và vai trò (Student/Admin)
 * Có hệ thống phê duyệt: pending (chờ), approved (đã duyệt), rejected (từ chối)
 */
public class User {
    // ============ Các thuộc tính của người dùng ============
    private String uid;        // User ID duy nhất từ Firebase Authentication
    private String name;       // Tên đầy đủ của người dùng
    private String email;      // Email của người dùng
    private String role;       // Vai trò: "student" (học sinh) hoặc "admin" (quản trị viên)
    private String status;     // Trạng thái phê duyệt: "pending" (chờ), "approved" (đã duyệt), "rejected" (từ chối)

    /**
     * Constructor rỗng - bắt buộc cho Firebase Firestore
     * Firebase cần constructor này để chuyển đổi dữ liệu từ Firestore về đối tượng Java
     */
    public User() {
    }

    /**
     * Constructor khi tạo tài khoản mới (3 tham số)
     * Mặc định status = "pending" (chờ phê duyệt)
     * @param uid User ID từ Firebase
     * @param name Tên người dùng
     * @param email Email người dùng
     * @param role Vai trò (student/admin)
     */
    public User(String uid, String name, String email, String role) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = "pending"; // Mặc định chờ duyệt khi tạo tài khoản
    }

    /**
     * Constructor đầy đủ với trạng thái phê duyệt (5 tham số)
     * @param uid User ID từ Firebase
     * @param name Tên người dùng
     * @param email Email người dùng
     * @param role Vai trò (student/admin)
     * @param status Trạng thái phê duyệt
     */
    public User(String uid, String name, String email, String role, String status) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    // ============ Getter và Setter cho UID ============
    /**
     * Lấy User ID
     * @return User ID duy nhất
     */
    public String getUid() {
        return uid;
    }

    /**
     * Thiết lập User ID
     * @param uid User ID duy nhất
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    // ============ Getter và Setter cho Tên ============
    /**
     * Lấy tên người dùng
     * @return Tên đầy đủ
     */
    public String getName() {
        return name;
    }

    /**
     * Thiết lập tên người dùng
     * @param name Tên đầy đủ
     */
    public void setName(String name) {
        this.name = name;
    }

    // ============ Getter và Setter cho Email ============
    /**
     * Lấy email người dùng
     * @return Email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Thiết lập email người dùng
     * @param email Email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    // ============ Getter và Setter cho Role ============
    /**
     * Lấy vai trò của người dùng
     * @return "student" hoặc "admin"
     */
    public String getRole() {
        return role;
    }

    /**
     * Thiết lập vai trò của người dùng
     * @param role "student" hoặc "admin"
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Kiểm tra xem người dùng có phải là admin không
     * @return true nếu role == "admin", ngược lại false
     */
    public boolean isAdmin() {
        return "admin".equals(role);
    }

    /**
     * Kiểm tra xem người dùng có phải là sinh viên không
     * @return true nếu role == "student", ngược lại false
     */
    public boolean isStudent() {
        return "student".equals(role);
    }

    // ============ Getter và Setter cho Status (Trạng thái phê duyệt) ============
    /**
     * Lấy trạng thái phê duyệt của tài khoản
     * @return "pending" (chờ), "approved" (đã duyệt), hoặc "rejected" (từ chối)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Thiết lập trạng thái phê duyệt
     * @param status "pending", "approved", hoặc "rejected"
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Kiểm tra xem tài khoản đang chờ phê duyệt không
     * @return true nếu status == "pending", ngược lại false
     */
    public boolean isPending() {
        return "pending".equals(status);
    }

    /**
     * Kiểm tra xem tài khoản đã được phê duyệt chưa
     * @return true nếu status == "approved", ngược lại false
     */
    public boolean isApproved() {
        return "approved".equals(status);
    }
}


package com.hsf302.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;

// TODO 18: Ánh xạ entity với bảng SQL Server
@Entity
@Table(name = "Students")
public class Student {

    // TODO 18
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // TODO 1: Thêm validation
    @NotBlank(message = "Mã sinh viên không được để trống")
    @Pattern(regexp = "^(SE|SS|SA|HE)\\d{6}$",
             message = "Mã SV phải có dạng SE/SS/SA/HE + 6 chữ số (VD: SE180001)")
    @Column(name = "student_code", nullable = false, unique = true, length = 8)
    private String studentCode;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 50, message = "Họ tên phải từ 2 đến 50 ký tự")
    @Nationalized // NVARCHAR – lưu tiếng Việt
    @Column(name = "full_name", nullable = false, length = 50)
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    // TODO 2: Thêm validation
    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @NotNull(message = "GPA không được để trống")
    @DecimalMin(value = "0.0", message = "GPA tối thiểu là 0")
    @DecimalMax(value = "10.0", message = "GPA tối đa là 10")
    @Column(name = "gpa", nullable = false)
    private Double gpa;

    @NotBlank(message = "Vui lòng chọn ngành")
    @Column(name = "major", nullable = false, length = 2)
    private String major;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public Student() {
    }

    public Student(Long id, String studentCode, String fullName, String email,
                   LocalDate dateOfBirth, Double gpa, String major, boolean active) {
        this.id = id;
        this.studentCode = studentCode;
        this.fullName = fullName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gpa = gpa;
        this.major = major;
        this.active = active;
    }

    // TODO 3: Cài đặt 2 thuộc tính tính toán
    public int getAge() {
        if (dateOfBirth == null) return 0;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public String getRank() {
        if (gpa == null) return "Chưa xếp loại";
        if (gpa >= 9.0) return "Xuất sắc";
        if (gpa >= 8.0) return "Giỏi";
        if (gpa >= 6.5) return "Khá";
        if (gpa >= 5.0) return "Trung bình";
        return "Yếu";
    }

    // TODO 0: Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

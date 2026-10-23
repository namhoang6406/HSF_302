package com.hsf302.student.repository;

import com.hsf302.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // TODO 19: Khai báo các derived query sau (KHÔNG cần viết thân hàm):
    //  19.1. Tìm theo mã SV HOẶC họ tên chứa từ khóa, không phân biệt hoa thường, sắp xếp theo id tăng dần
    List<Student> findByStudentCodeContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrderByIdAsc(String code, String name);

    //  19.2. Kiểm tra mã SV đã tồn tại (dùng khi THÊM MỚI)
    boolean existsByStudentCodeIgnoreCase(String studentCode);

    //  19.3. Kiểm tra mã SV đã tồn tại ở sinh viên KHÁC (dùng khi CẬP NHẬT – bỏ qua chính nó)
    boolean existsByStudentCodeIgnoreCaseAndIdNot(String studentCode, Long id);
}

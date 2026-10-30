package com.example.data.repository;

import com.example.data.pojo.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findAllByOrderByIdAsc();

    Optional<Student> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query("SELECT s FROM Student s " +
           "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(s.email) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "ORDER BY s.id")
    List<Student> search(@Param("kw") String keyword);
}

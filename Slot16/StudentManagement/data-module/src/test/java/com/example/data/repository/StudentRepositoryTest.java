package com.example.data.repository;

import com.example.data.pojo.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    private Student an, binh, cuong;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        an    = studentRepository.save(new Student("Nguyen Van An", "an@fpt.edu.vn", 20));
        binh  = studentRepository.save(new Student("Tran Thi Binh", "binh@gmail.com", 21));
        cuong = studentRepository.save(new Student("Le Van Cuong", "cuong@fpt.edu.vn", 22));
    }

    @Test
    void save_assignsGeneratedId() {
        assertNotNull(an.getId());
        assertEquals(3, studentRepository.count());
    }

    @Test
    void saveAndFlush_duplicateEmail_throws() {
        assertThrows(DataIntegrityViolationException.class,
                () -> studentRepository.saveAndFlush(new Student("An 2", "an@fpt.edu.vn", 25)));
    }

    @Test
    void findAllByOrderByIdAsc_returnsSortedById() {
        List<Student> all = studentRepository.findAllByOrderByIdAsc();
        assertEquals(List.of(an.getId(), binh.getId(), cuong.getId()),
                all.stream().map(Student::getId).toList());
    }

    @Test
    void findByEmailIgnoreCase_works() {
        assertTrue(studentRepository.findByEmailIgnoreCase("BINH@GMAIL.COM").isPresent());
        assertTrue(studentRepository.findByEmailIgnoreCase("none@x.com").isEmpty());
    }

    @Test
    void existsByEmailIgnoreCase_works() {
        assertTrue(studentRepository.existsByEmailIgnoreCase("An@Fpt.Edu.Vn"));
        assertFalse(studentRepository.existsByEmailIgnoreCase("zzz@fpt.edu.vn"));
    }

    @Test
    void existsByEmailIgnoreCaseAndIdNot_excludesItself() {
        assertFalse(studentRepository.existsByEmailIgnoreCaseAndIdNot("an@fpt.edu.vn", an.getId()));
        assertTrue(studentRepository.existsByEmailIgnoreCaseAndIdNot("an@fpt.edu.vn", binh.getId()));
    }

    @Test
    void search_byName_ignoreCase() {
        assertEquals(1, studentRepository.search("BINH").size());
        assertEquals(2, studentRepository.search("van").size());
    }

    @Test
    void search_byEmail() {
        List<Student> result = studentRepository.search("fpt.edu");
        assertEquals(2, result.size());
        assertEquals(an.getId(), result.get(0).getId());   // ORDER BY id
    }

    @Test
    void search_noMatch_returnsEmpty() {
        assertTrue(studentRepository.search("zzz").isEmpty());
    }

    @Test
    void deleteById_removesStudent() {
        studentRepository.deleteById(binh.getId());
        assertFalse(studentRepository.existsById(binh.getId()));
        assertEquals(2, studentRepository.count());
    }
}

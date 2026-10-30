package com.example.data.service;

import com.example.data.exception.StudentNotFoundException;
import com.example.data.exception.ValidationException;
import com.example.data.pojo.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(StudentServiceImpl.class)
class StudentServiceImplTest {

    @Autowired
    private StudentService studentService;   // inject qua INTERFACE

    @Test
    void create_valid_normalizesAndSaves() {
        Student s = studentService.create(new Student("  Nguyen An  ", "  AN@FPT.EDU.VN ", 20));
        assertNotNull(s.getId());
        assertEquals("Nguyen An", s.getName());
        assertEquals("an@fpt.edu.vn", s.getEmail());
    }

    @Test
    void create_blankName_throws() {
        var ex = assertThrows(ValidationException.class,
                () -> studentService.create(new Student("   ", "a@x.com", 20)));
        assertEquals("Name is required", ex.getMessage());
    }

    @Test
    void create_nameTooLong_throws() {
        var ex = assertThrows(ValidationException.class,
                () -> studentService.create(new Student("x".repeat(101), "a@x.com", 20)));
        assertEquals("Name must be at most 100 characters", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "a@b", "@x.com", "a b@x.com"})
    void create_invalidEmail_throws(String email) {
        var ex = assertThrows(ValidationException.class,
                () -> studentService.create(new Student("An", email, 20)));
        assertEquals("Invalid email", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {17, 61})
    void create_ageOutOfRange_throws(int age) {
        var ex = assertThrows(ValidationException.class,
                () -> studentService.create(new Student("An", "an@x.com", age)));
        assertEquals("Age must be between 18 and 60", ex.getMessage());
    }

    @Test
    void create_boundaryAges_ok() {
        assertNotNull(studentService.create(new Student("A", "a18@x.com", 18)).getId());
        assertNotNull(studentService.create(new Student("B", "b60@x.com", 60)).getId());
    }

    @Test
    void create_duplicateEmail_ignoreCase_throws() {
        studentService.create(new Student("An", "an@x.com", 20));
        var ex = assertThrows(ValidationException.class,
                () -> studentService.create(new Student("An 2", "AN@X.COM", 21)));
        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void update_sameEmailOfItself_ok() {
        Student s = studentService.create(new Student("An", "an@x.com", 20));
        Student edit = new Student(s.getId(), "An Updated", "an@x.com", 25);
        Student updated = studentService.update(edit);
        assertEquals("An Updated", updated.getName());
        assertEquals(25, studentService.findById(s.getId()).getAge());
    }

    @Test
    void update_emailOfAnotherStudent_throws() {
        studentService.create(new Student("An", "an@x.com", 20));
        Student b = studentService.create(new Student("Binh", "binh@x.com", 21));
        Student edit = new Student(b.getId(), "Binh", "an@x.com", 21);
        var ex = assertThrows(ValidationException.class, () -> studentService.update(edit));
        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void findById_notFound_throws() {
        var ex = assertThrows(StudentNotFoundException.class, () -> studentService.findById(999L));
        assertEquals("Student not found: 999", ex.getMessage());
    }

    @Test
    void delete_existing_and_notFound() {
        Student s = studentService.create(new Student("An", "an@x.com", 20));
        studentService.delete(s.getId());
        assertThrows(StudentNotFoundException.class, () -> studentService.findById(s.getId()));
        assertThrows(StudentNotFoundException.class, () -> studentService.delete(999L));
    }

    @Test
    void search_blank_returnsAll_and_trimsKeyword() {
        studentService.create(new Student("An", "an@x.com", 20));
        studentService.create(new Student("Binh", "binh@x.com", 21));
        assertEquals(2, studentService.search("   ").size());
        assertEquals(2, studentService.search(null).size());
        assertEquals(1, studentService.search(" binh ").size());
    }
}

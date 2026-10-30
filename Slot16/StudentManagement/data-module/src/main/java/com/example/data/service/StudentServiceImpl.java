package com.example.data.service;

import com.example.data.exception.StudentNotFoundException;
import com.example.data.exception.ValidationException;
import com.example.data.pojo.Student;
import com.example.data.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$");

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public Student create(Student s) {
        normalize(s);
        validate(s);
        if (studentRepository.existsByEmailIgnoreCase(s.getEmail())) {
            throw new ValidationException("Email already exists");
        }
        s.setId(null);
        return studentRepository.save(s);
    }

    @Override
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAllByOrderByIdAsc();
    }

    @Override
    @Transactional
    public Student update(Student s) {
        Student existing = findById(s.getId());
        normalize(s);
        validate(s);
        if (studentRepository.existsByEmailIgnoreCaseAndIdNot(s.getEmail(), s.getId())) {
            throw new ValidationException("Email already exists");
        }
        existing.setName(s.getName());
        existing.setEmail(s.getEmail());
        existing.setAge(s.getAge());
        return studentRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    public List<Student> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return findAll();
        return studentRepository.search(keyword.trim());
    }

    private void normalize(Student s) {
        if (s.getName() != null) s.setName(s.getName().trim());
        if (s.getEmail() != null) s.setEmail(s.getEmail().trim().toLowerCase());
    }

    private void validate(Student s) {
        if (s.getName() == null || s.getName().isBlank())
            throw new ValidationException("Name is required");
        if (s.getName().length() > 100)
            throw new ValidationException("Name must be at most 100 characters");
        if (s.getEmail() == null || !EMAIL.matcher(s.getEmail()).matches())
            throw new ValidationException("Invalid email");
        if (s.getAge() == null || s.getAge() < 18 || s.getAge() > 60)
            throw new ValidationException("Age must be between 18 and 60");
    }
}

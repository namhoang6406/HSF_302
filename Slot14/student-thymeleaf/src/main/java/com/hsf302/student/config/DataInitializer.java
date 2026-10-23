package com.hsf302.student.config;

import com.hsf302.student.entity.Student;
import com.hsf302.student.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;

    public DataInitializer(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) {
        if (studentRepository.count() > 0) {
            return;
        }
        studentRepository.saveAll(List.of(
            new Student(null, "SE180001", "Nguyễn Văn An",  "annv@fpt.edu.vn",    LocalDate.of(2004, 5, 12),  8.75, "SE", true),
            new Student(null, "SE180002", "Trần Thị Bình",  "binhtt@fpt.edu.vn",  LocalDate.of(2004, 9, 3),   9.20, "AI", true),
            new Student(null, "SS180003", "Lê Hoàng Cường", "cuonglh@fpt.edu.vn", LocalDate.of(2003, 1, 25),  6.80, "IA", true),
            new Student(null, "SA180004", "Phạm Minh Dũng", "dungpm@fpt.edu.vn",  LocalDate.of(2004, 11, 30), 4.50, "GD", false),
            new Student(null, "HE180005", "Võ Thị Em",      "emvt@fpt.edu.vn",    LocalDate.of(2005, 2, 14),  8.35, "SE", true)
        ));
    }
}

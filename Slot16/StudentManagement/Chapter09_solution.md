# Chapter 09 — ĐÁP ÁN THAM KHẢO
## Student Management System — JavaFX + Spring Data JPA (Repository · Service · ServiceImpl)

> Đi kèm đề `Chapter09.md`.
> **Môi trường:** JDK 21 · Maven 3.9+ · Spring Boot 4.1.1 (Hibernate 7, Jakarta Persistence 3.2) · JavaFX 21.0.5 · SQL Server · H2 (test)

---

## Mục lục
1. [Cấu trúc project hoàn chỉnh](#1-cấu-trúc-project-hoàn-chỉnh)
2. [Bài 0 — POM & .gitignore](#2-bài-0--pom--gitignore)
3. [TODO #1 — Entity](#3-todo-1--entity)
4. [TODO #2 — Repository](#4-todo-2--repository)
5. [TODO #3 — Exception, Service, ServiceImpl](#5-todo-3--exception-service-serviceimpl)
6. [Test (cho sẵn)](#6-test-cho-sẵn)
7. [TODO #4 — Spring Boot + JavaFX bootstrap](#7-todo-4--spring-boot--javafx-bootstrap)
8. [TODO #5 + #6 — StudentController hoàn chỉnh](#8-todo-5--6--studentcontroller-hoàn-chỉnh)
9. [TODO #7 — CSS & README](#9-todo-7--css--readme)
10. [Chuỗi lệnh Git chuẩn (9 commit)](#10-chuỗi-lệnh-git-chuẩn-9-commit)
11. [Kết quả mong đợi khi chạy](#11-kết-quả-mong-đợi-khi-chạy)
12. [Gợi ý chấm điểm & lỗi sinh viên hay mắc](#12-gợi-ý-chấm-điểm--lỗi-sinh-viên-hay-mắc)
13. [Đáp án Bonus](#13-đáp-án-bonus)

---

## 1. Cấu trúc project hoàn chỉnh

```
student-management/
├── .gitignore
├── pom.xml
├── README.md
├── docs/screenshots/            (01-list.png, 02-add.png, 03-validation.png, 04-delete-confirm.png)
├── data-module/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/example/data/
│       │   ├── pojo/Student.java
│       │   ├── repository/StudentRepository.java
│       │   ├── exception/ValidationException.java
│       │   ├── exception/StudentNotFoundException.java
│       │   ├── service/StudentService.java
│       │   └── service/StudentServiceImpl.java
│       └── test/java/com/example/data/
│           ├── DataTestApplication.java
│           ├── repository/StudentRepositoryTest.java
│           └── service/StudentServiceImplTest.java
└── ui-module/
    ├── pom.xml
    └── src/main/
        ├── java/com/example/
        │   ├── StudentManagementApplication.java
        │   └── ui/
        │       ├── JavaFxApplication.java
        │       ├── Launcher.java
        │       └── controller/StudentController.java
        └── resources/
            ├── application.properties
            ├── student-view.fxml
            └── style.css
```

> ✅ **Không có:** `StudentRepositoryImpl`, `persistence.xml`, `JpaUtil`, `module-info.java`.

---

## 2. Bài 0 — POM & .gitignore

#### `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.1</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>student-management</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
        <module>data-module</module>
        <module>ui-module</module>
    </modules>

    <properties>
        <java.version>21</java.version>
        <javafx.version>21.0.5</javafx.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.example</groupId>
                <artifactId>data-module</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-controls</artifactId>
                <version>${javafx.version}</version>
            </dependency>
            <dependency>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-fxml</artifactId>
                <version>${javafx.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

#### `data-module/pom.xml`
> Không khai báo `spring-boot-maven-plugin` ở đây (data-module là thư viện, không có `main`).
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.example</groupId>
        <artifactId>student-management</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>data-module</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.microsoft.sqlserver</groupId>
            <artifactId>mssql-jdbc</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

#### `ui-module/pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.example</groupId>
        <artifactId>student-management</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>ui-module</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>data-module</artifactId>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <mainClass>com.example.ui.Launcher</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

#### `.gitignore`

```gitignore
target/
.idea/
*.iml
out/
*.log
.DS_Store
```

---

## 3. TODO #1 — Entity

#### `data-module/src/main/java/com/example/data/pojo/Student.java`
> `@Nationalized` → cột `name` kiểu `NVARCHAR` trên SQL Server (lưu đúng tiếng Việt).
```java
package com.example.data.pojo;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private Integer age;

    public Student() {}

    public Student(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public Student(Long id, String name, String email, Integer age) {
        this(name, email, age);
        this.id = id;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    @Override
    public String toString() {
        return id + " | " + name + " | " + email + " | " + age;
    }
}
```

---

## 4. TODO #2 — Repository

#### `data-module/src/main/java/com/example/data/repository/StudentRepository.java`

```java
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
```

**Giải thích:**

| Method | Loại | Dùng ở đâu |
|---|---|---|
| `save`, `findById`, `existsById`, `deleteById`, `count` | Kế thừa từ `JpaRepository` | ServiceImpl, test |
| `findAllByOrderByIdAsc()` | Derived query | `StudentServiceImpl.findAll()` |
| `findByEmailIgnoreCase(email)` | Derived query | (dự phòng / test) |
| `existsByEmailIgnoreCase(email)` | Derived query | `create` — chặn trùng email |
| `existsByEmailIgnoreCaseAndIdNot(email, id)` | Derived query | `update` — chặn trùng email với student **khác** |
| `search(kw)` | `@Query` JPQL | `StudentServiceImpl.search()` |

---

## 5. TODO #3 — Exception, Service, ServiceImpl

#### `data-module/src/main/java/com/example/data/exception/ValidationException.java`

```java
package com.example.data.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
}
```

#### `data-module/src/main/java/com/example/data/exception/StudentNotFoundException.java`

```java
package com.example.data.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(Long id) { super("Student not found: " + id); }
}
```

#### `data-module/src/main/java/com/example/data/service/StudentService.java`

```java
package com.example.data.service;

import com.example.data.pojo.Student;
import java.util.List;

public interface StudentService {
    Student create(Student s);
    Student findById(Long id);
    List<Student> findAll();
    Student update(Student s);
    void delete(Long id);
    List<Student> search(String keyword);
}
```

#### `data-module/src/main/java/com/example/data/service/StudentServiceImpl.java`

```java
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
```

**Các điểm then chốt:**
- `@Transactional(readOnly = true)` ở class → mọi method đọc chạy trong transaction chỉ đọc; method ghi ghi đè bằng `@Transactional`.
- `create`: `s.setId(null)` đảm bảo luôn **insert** (tránh client truyền id làm `save` thành update).
- `update`: lấy entity đang được quản lý (`existing`), copy field rồi `save` → không ghi đè cột ngoài ý muốn; `findById` ném `StudentNotFoundException` nếu id không tồn tại.
- Thứ tự validate **name → email → age** quyết định message trả về khi có nhiều lỗi cùng lúc.

---

## 6. Test (cho sẵn)

> Giống hệt đề. Kết quả mong đợi: `StudentRepositoryTest` 10/10, `StudentServiceImplTest` 16 lần chạy (12 method).

#### `data-module/src/test/java/com/example/data/DataTestApplication.java`

```java
package com.example.data;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Cấu hình Spring Boot CHỈ dùng cho test của data-module
 *  (data-module là thư viện, không có class main riêng). */
@SpringBootApplication
public class DataTestApplication {
}
```

#### `data-module/src/test/java/com/example/data/repository/StudentRepositoryTest.java`

```java
package com.example.data.repository;

import com.example.data.pojo.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
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
```

#### `data-module/src/test/java/com/example/data/service/StudentServiceImplTest.java`

```java
package com.example.data.service;

import com.example.data.exception.StudentNotFoundException;
import com.example.data.exception.ValidationException;
import com.example.data.pojo.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
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
```

---

## 7. TODO #4 — Spring Boot + JavaFX bootstrap

#### `ui-module/src/main/resources/application.properties`
> Sửa `username/password` theo máy chạy.
```properties
spring.application.name=student-management
spring.main.web-application-type=none

spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=12345
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

#### `ui-module/src/main/java/com/example/StudentManagementApplication.java`

```java
package com.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Đặt ở package gốc com.example để Spring quét được cả
 *  com.example.data (entity, repository, service) và com.example.ui (controller). */
@SpringBootApplication
public class StudentManagementApplication {
}
```

#### `ui-module/src/main/java/com/example/ui/JavaFxApplication.java`

```java
package com.example.ui;

import com.example.StudentManagementApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(StudentManagementApplication.class)
                .web(WebApplicationType.NONE)
                .headless(false)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student-view.fxml"));
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setTitle("Student Management System");
        stage.setMinWidth(900);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }
}
```

#### `ui-module/src/main/java/com/example/ui/Launcher.java`

```java
package com.example.ui;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }
}
```

#### `ui-module/src/main/resources/student-view.fxml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.control.cell.PropertyValueFactory?>
<?import javafx.scene.layout.*?>

<BorderPane xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="com.example.ui.controller.StudentController">
    <top>
        <VBox spacing="10">
            <padding><Insets top="10" right="10" bottom="10" left="10"/></padding>
            <Label text="Student Management System" styleClass="header"/>
            <HBox spacing="10">
                <TextField fx:id="searchField" promptText="Search by name or email..."
                           prefWidth="320" onAction="#handleSearch"/>
                <Button text="Search" onAction="#handleSearch"/>
                <Button text="Refresh" onAction="#handleRefresh"/>
            </HBox>
        </VBox>
    </top>

    <center>
        <TableView fx:id="tableView">
            <columnResizePolicy><TableView fx:constant="CONSTRAINED_RESIZE_POLICY"/></columnResizePolicy>
            <placeholder><Label text="No students found"/></placeholder>
            <columns>
                <TableColumn text="ID" prefWidth="60">
                    <cellValueFactory><PropertyValueFactory property="id"/></cellValueFactory>
                </TableColumn>
                <TableColumn text="Name" prefWidth="240">
                    <cellValueFactory><PropertyValueFactory property="name"/></cellValueFactory>
                </TableColumn>
                <TableColumn text="Email" prefWidth="260">
                    <cellValueFactory><PropertyValueFactory property="email"/></cellValueFactory>
                </TableColumn>
                <TableColumn text="Age" prefWidth="80">
                    <cellValueFactory><PropertyValueFactory property="age"/></cellValueFactory>
                </TableColumn>
            </columns>
        </TableView>
    </center>

    <right>
        <VBox spacing="8" prefWidth="300">
            <padding><Insets top="10" right="10" bottom="10" left="10"/></padding>
            <Label text="Student Form" styleClass="subheader"/>
            <Label text="ID:"/>
            <TextField fx:id="idField" editable="false" promptText="(auto)"/>
            <Label text="Name:"/>
            <TextField fx:id="nameField" promptText="Full name"/>
            <Label text="Email:"/>
            <TextField fx:id="emailField" promptText="name@fpt.edu.vn"/>
            <Label text="Age:"/>
            <TextField fx:id="ageField" promptText="18 - 60"/>
            <HBox spacing="8">
                <Button text="Add" onAction="#handleAdd" styleClass="btn-primary"/>
                <Button fx:id="btnUpdate" text="Update" onAction="#handleUpdate" styleClass="btn-warning"/>
                <Button fx:id="btnDelete" text="Delete" onAction="#handleDelete" styleClass="btn-danger"/>
                <Button text="Clear" onAction="#handleClear"/>
            </HBox>
            <Label fx:id="messageLabel" wrapText="true" styleClass="msg"/>
        </VBox>
    </right>

    <bottom>
        <HBox>
            <padding><Insets top="5" right="10" bottom="5" left="10"/></padding>
            <Label fx:id="statusLabel" text="Ready"/>
        </HBox>
    </bottom>
</BorderPane>
```

---

## 8. TODO #5 + #6 — StudentController hoàn chỉnh

#### `ui-module/src/main/java/com/example/ui/controller/StudentController.java`

```java
package com.example.ui.controller;

import com.example.data.exception.StudentNotFoundException;
import com.example.data.exception.ValidationException;
import com.example.data.pojo.Student;
import com.example.data.service.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class StudentController {

    @FXML private TableView<Student> tableView;
    @FXML private TextField searchField, idField, nameField, emailField, ageField;
    @FXML private Button btnUpdate, btnDelete;
    @FXML private Label messageLabel, statusLabel;

    private final StudentService studentService;
    private final ObservableList<Student> data = FXCollections.observableArrayList();

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @FXML
    public void initialize() {
        tableView.setItems(data);
        tableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) fillForm(newSel);
                });
        var noSelection = tableView.getSelectionModel().selectedItemProperty().isNull();
        btnUpdate.disableProperty().bind(noSelection);
        btnDelete.disableProperty().bind(noSelection);
        loadData();
    }

    private void loadData() {
        try {
            data.setAll(studentService.findAll());
            statusLabel.setText("Loaded " + data.size() + " students");
        } catch (Exception e) {
            showError("Cannot load data: " + e.getMessage());
            statusLabel.setText("Database error");
        }
    }

    @FXML
    private void handleSearch() {
        try {
            data.setAll(studentService.search(searchField.getText()));
            statusLabel.setText("Found " + data.size() + " students");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadData();
    }

    private Student readForm() {
        String ageText = ageField.getText() == null ? "" : ageField.getText().trim();
        if (ageText.isEmpty()) throw new ValidationException("Age is required");
        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            throw new ValidationException("Age must be a number");
        }
        return new Student(nameField.getText(), emailField.getText(), age);
    }

    @FXML
    private void handleAdd() {
        try {
            Student saved = studentService.create(readForm());
            showInfo("Added: " + saved.getName());
            handleClear();
            loadData();
        } catch (ValidationException | StudentNotFoundException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("System error: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Student selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a student");
            return;
        }
        try {
            Student edit = readForm();
            edit.setId(selected.getId());
            Student updated = studentService.update(edit);
            showInfo("Updated: " + updated.getName());
            handleClear();
            loadData();
        } catch (ValidationException | StudentNotFoundException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("System error: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Student selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a student");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete student \"" + selected.getName() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText("Confirm delete");
        confirm.initOwner(tableView.getScene().getWindow());
        confirm.showAndWait()
               .filter(rs -> rs == ButtonType.OK)
               .ifPresent(rs -> {
                   try {
                       studentService.delete(selected.getId());
                       showInfo("Deleted: " + selected.getName());
                       handleClear();
                       loadData();
                   } catch (StudentNotFoundException e) {
                       showError(e.getMessage());
                       loadData();
                   } catch (Exception e) {
                       showError("System error: " + e.getMessage());
                   }
               });
    }

    @FXML
    private void handleClear() {
        idField.clear();
        nameField.clear();
        emailField.clear();
        ageField.clear();
        tableView.getSelectionModel().clearSelection();
        nameField.requestFocus();
    }

    private void fillForm(Student s) {
        idField.setText(String.valueOf(s.getId()));
        nameField.setText(s.getName());
        emailField.setText(s.getEmail());
        ageField.setText(String.valueOf(s.getAge()));
    }

    private void showInfo(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: green;");
    }

    private void showError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: red;");
    }
}
```

**Phân chia theo TODO:**

| TODO | Thành phần trong controller |
|---|---|
| #4 (khung) | `@Component`, `@FXML` fields, constructor `StudentController(StudentService)` |
| #5 | `data`, `initialize()`, `loadData()`, `handleSearch()`, `handleRefresh()`, `fillForm()`, `showInfo()`, `showError()` |
| #6 | `readForm()`, `handleAdd()`, `handleUpdate()`, `handleDelete()`, `handleClear()` |

> Nếu chấm theo commit: ở commit #5 (TODO #4) các method `handle...` để rỗng; commit #6 thêm phần TODO #5; commit #7 thêm phần TODO #6.

---

## 9. TODO #7 — CSS & README

#### `ui-module/src/main/resources/style.css`

```css
.root        { -fx-font-family: "Segoe UI"; -fx-font-size: 13; }
.header      { -fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #2c3e50; }
.subheader   { -fx-font-size: 16; -fx-font-weight: bold; }
.btn-primary { -fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; }
.btn-warning { -fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; }
.btn-danger  { -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; }
.button:disabled { -fx-opacity: 0.45; }
.table-view  { -fx-border-color: #bdc3c7; }
.msg         { -fx-padding: 5; -fx-font-style: italic; }
```

#### `README.md` (mẫu)
````markdown
# Student Management System (JavaFX + Spring Data JPA)

## Yêu cầu môi trường
- JDK 21, Maven 3.9+
- SQL Server (TCP/IP port 1433 bật, tài khoản `sa`)
- IntelliJ IDEA, Gluon Scene Builder (tuỳ chọn)

## Cài đặt
1. Tạo database:
   ```sql
   IF DB_ID('StudentDB') IS NULL CREATE DATABASE StudentDB;
   ```
2. Sửa `ui-module/src/main/resources/application.properties`
   (`spring.datasource.username`, `spring.datasource.password`).

## Build & Test
```bash
mvn clean install
```
Test dùng H2 in-memory, không cần SQL Server.

## Chạy ứng dụng
- IntelliJ: Run `com.example.ui.Launcher`
- Terminal:
  ```bash
  mvn -q clean install -DskipTests
  mvn -f ui-module/pom.xml spring-boot:run
  ```

## Kiến trúc
StudentController → StudentService (interface) → StudentServiceImpl → StudentRepository (JpaRepository) → SQL Server

| Module | Nội dung |
|---|---|
| data-module | Entity, Repository (Spring Data JPA), Service + ServiceImpl |
| ui-module | JavaFX (FXML, CSS), Controller, cấu hình Spring Boot |

## Ảnh chụp
![List](docs/screenshots/01-list.png)
![Add](docs/screenshots/02-add.png)
![Validation](docs/screenshots/03-validation.png)
![Delete](docs/screenshots/04-delete-confirm.png)
````

---

## 10. Chuỗi lệnh Git chuẩn (9 commit)

```bash
# Commit 1 — Bài 0
mvn -q clean install
git init
git branch -M main
git add .
git commit -m "chore: init multi-module spring boot project for JDK 21"

# Commit 2 — TODO #1
git add data-module/src/main/java/com/example/data/pojo
git commit -m "feat(data): add Student entity"

# Commit 3 — TODO #2 (+ test repository + DataTestApplication)
git add data-module/src/main/java/com/example/data/repository data-module/src/test
git commit -m "feat(data): add StudentRepository extending JpaRepository"

# Commit 4 — TODO #3 (+ test service)
git add data-module
git commit -m "feat(data): add StudentService and StudentServiceImpl"

# Commit 5 — TODO #4
git add ui-module
git commit -m "feat(ui): bootstrap Spring context in JavaFX app"

# Commit 6 — TODO #5
git add ui-module
git commit -m "feat(ui): load, search and select students"

# Commit 7 — TODO #6
git add ui-module
git commit -m "feat(ui): add, update, delete and clear student"

# Commit 8 — TODO #7 (CSS)
git add ui-module/src/main/resources/style.css
git commit -m "style(ui): add stylesheet and polish UX"

# Commit 9 — TODO #7 (README)
git add README.md docs
git commit -m "docs: add README with setup and run guide"

git log --oneline --reverse
```
> Trước **mỗi** commit chạy `mvn -q clean install` → `BUILD SUCCESS`.

---

## 11. Kết quả mong đợi khi chạy

**`mvn clean install`:**
```
[INFO] Running com.example.data.repository.StudentRepositoryTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.data.service.StudentServiceImplTest
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] Tests run: 26, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] student-management ................................. SUCCESS
[INFO] data-module ........................................ SUCCESS
[INFO] ui-module .......................................... SUCCESS
[INFO] BUILD SUCCESS
```

**Chạy `Launcher` lần đầu** — console có:
```
Started StudentManagementApplication in ... seconds
Hibernate:
    create table students (
        id bigint identity not null,
        age int not null,
        email varchar(150) not null,
        name nvarchar(100) not null,
        primary key (id)
    )
```

**Đáp án manual test:** như bảng TC01–TC14 trong đề (cột "Kết quả mong đợi").

---

## 12. Gợi ý chấm điểm & lỗi sinh viên hay mắc

| TODO | Điểm | Trừ điểm khi |
|---|---|---|
| #1 Entity (10) | 10 | Thiếu `@Nationalized` (−2) · `id` kiểu `long` (−2) · thiếu constructor rỗng (−5) · thiếu `unique` email (−2) |
| #2 Repository (15) | 15 | Test fail: −1,5/test · tự viết `StudentRepositoryImpl` → **0** · nối chuỗi keyword vào JPQL (−5) |
| #3 Service (20) | 20 | Test fail: −1,5/method · `@Autowired` trên field thay vì constructor (−2) · thiếu `@Transactional` ở method ghi (−3) · validate trong Controller thay vì Service (−5) |
| #4 Bootstrap (15) | 15 | `new StudentController()` / không dùng `setControllerFactory` (−5) · `@SpringBootApplication` sai package (app không chạy, −10) · không đóng context ở `stop()` (−2) |
| #5 Load/Search/Select (10) | 10 | Mỗi TC01–TC04 sai −2,5 |
| #6 CUD (15) | 15 | `handleUpdate` sửa trực tiếp `selected` (−3) · Delete không confirm (−3) · TC05–TC14 sai −1/TC |
| #7 CSS + README (5) | 5 | Thiếu README (−3) · thiếu ảnh (−2) |
| Git (10) | 10 | Sai format message −1/commit · gộp nhiều TODO 1 commit −2 · commit `target/` hoặc `.idea/` −3 · < 9 commit −1/commit thiếu |

**Lệnh kiểm tra nhanh khi chấm:**
```bash
git log --oneline --reverse                      # 9 commit, đúng message
git ls-files | findstr /i "target .idea .iml"    # phải rỗng
findstr /s /i "StudentRepositoryImpl EntityManager persistence.xml" *.java *.xml   # phải rỗng
findstr /s "new StudentServiceImpl" *.java       # phải rỗng
mvn clean install                                # BUILD SUCCESS, 26 test pass
```

---

## 13. Đáp án Bonus

### 13.1 Phân trang bằng `Pageable`
```java
// StudentService
Page<Student> findPage(int page, int size);

// StudentServiceImpl
@Override
public Page<Student> findPage(int page, int size) {
    return studentRepository.findAll(PageRequest.of(page, size, Sort.by("id")));
}
```
```java
// Test bổ sung trong StudentServiceImplTest
@Test
void findPage_returnsCorrectSlice() {
    for (int i = 1; i <= 12; i++) {
        studentService.create(new Student("S" + i, "s" + i + "@x.com", 20));
    }
    Page<Student> p = studentService.findPage(1, 10);
    assertEquals(12, p.getTotalElements());
    assertEquals(2, p.getTotalPages());
    assertEquals(2, p.getContent().size());
}
```
```java
// StudentController — thêm @FXML private Pagination pagination; (FXML: đặt dưới TableView)
private static final int PAGE_SIZE = 10;

private void loadPage(int pageIndex) {
    Page<Student> page = studentService.findPage(pageIndex, PAGE_SIZE);
    data.setAll(page.getContent());
    pagination.setPageCount(Math.max(1, page.getTotalPages()));
    statusLabel.setText("Page " + (pageIndex + 1) + "/" + Math.max(1, page.getTotalPages())
            + " - total " + page.getTotalElements() + " students");
}
// trong initialize():
// pagination.currentPageIndexProperty().addListener((o, ov, nv) -> loadPage(nv.intValue()));
// loadPage(0);
```
Import: `org.springframework.data.domain.Page`, `PageRequest`, `Sort`.

### 13.2 Async loading với `Task`
```java
private void runAsync(Supplier<List<Student>> query, String prefix) {
    Task<List<Student>> task = new Task<>() {
        @Override protected List<Student> call() { return query.get(); }
    };
    statusLabel.setText("Loading...");
    searchField.setDisable(true);
    task.setOnSucceeded(e -> {
        data.setAll(task.getValue());
        statusLabel.setText(prefix + " " + data.size() + " students");
        searchField.setDisable(false);
    });
    task.setOnFailed(e -> {
        showError("Cannot load data: " + task.getException().getMessage());
        statusLabel.setText("Database error");
        searchField.setDisable(false);
    });
    Thread t = new Thread(task, "db-loader");
    t.setDaemon(true);
    t.start();
}

private void loadData()       { runAsync(studentService::findAll, "Loaded"); }
@FXML private void handleSearch() {
    String kw = searchField.getText();
    runAsync(() -> studentService.search(kw), "Found");
}
```
Import: `javafx.concurrent.Task`, `java.util.List`, `java.util.function.Supplier`.

### 13.3 Export CSV
```java
@FXML
private void handleExport() {
    FileChooser fc = new FileChooser();
    fc.setInitialFileName("students.csv");
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
    File file = fc.showSaveDialog(tableView.getScene().getWindow());
    if (file == null) return;                       // user bấm Cancel
    try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8)) {
        pw.print('\uFEFF');                         // BOM cho Excel
        pw.println("ID,Name,Email,Age");
        for (Student s : data) {
            pw.printf("%d,%s,%s,%d%n", s.getId(), csv(s.getName()), csv(s.getEmail()), s.getAge());
        }
        showInfo("Exported " + data.size() + " rows to " + file.getName());
    } catch (IOException e) {
        showError("Export failed: " + e.getMessage());
    }
}

private static String csv(String v) {
    if (v == null) return "";
    return (v.contains(",") || v.contains("\"")) ? "\"" + v.replace("\"", "\"\"") + "\"" : v;
}
```
FXML: thêm `<Button text="Export CSV" onAction="#handleExport"/>` vào HBox ở top.
Import: `javafx.stage.FileChooser`, `java.io.*`, `java.nio.charset.StandardCharsets`.

### 13.4 Soft delete
```java
// Student
@Column(nullable = false)
private boolean active = true;
public boolean isActive() { return active; }
public void setActive(boolean active) { this.active = active; }
```
```java
// StudentRepository
List<Student> findAllByActiveTrueOrderByIdAsc();
boolean existsByIdAndActiveTrue(Long id);

@Query("SELECT s FROM Student s WHERE s.active = true AND (" +
       "LOWER(s.name) LIKE LOWER(CONCAT('%', :kw, '%')) " +
       "OR LOWER(s.email) LIKE LOWER(CONCAT('%', :kw, '%'))) ORDER BY s.id")
List<Student> search(@Param("kw") String keyword);
```
```java
// StudentServiceImpl
@Override
public List<Student> findAll() { return studentRepository.findAllByActiveTrueOrderByIdAsc(); }

@Override
@Transactional
public void delete(Long id) {
    Student s = studentRepository.findById(id)
            .filter(Student::isActive)
            .orElseThrow(() -> new StudentNotFoundException(id));
    s.setActive(false);
    studentRepository.save(s);
}
```


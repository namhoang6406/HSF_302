# Chapter 09 — BÀI TẬP: Student Management System
## JavaFX + Spring Data JPA (Repository · Service · ServiceImpl) — Multi-module Maven

> **Môi trường:** JDK 21 · Maven 3.9+ · Spring Boot **3.5.x** · JavaFX **21.0.5** · IntelliJ IDEA · Gluon Scene Builder · SQL Server (chạy app) · H2 (chạy test)
> **Đầu ra:** 1 Git repository gồm 2 module (`data-module`, `ui-module`), **9 commit** đúng quy ước, toàn bộ test pass, ứng dụng JavaFX CRUD chạy được.

---

## Mục lục

0. [Tổng quan & Rubric](#0-tổng-quan--rubric)
1. [Quy ước Git & Commit (BẮT BUỘC)](#1-quy-ước-git--commit-bắt-buộc)
2. [Bài 0 — Khởi tạo project multi-module Spring Boot](#bài-0--khởi-tạo-project-multi-module-spring-boot)
3. [Bài 1 — TODO #1: Entity `Student`](#bài-1--todo-1-entity-student)
4. [Bài 2 — TODO #2: `StudentRepository extends JpaRepository`](#bài-2--todo-2-studentrepository-extends-jparepository)
5. [Bài 3 — TODO #3: `StudentService` + `StudentServiceImpl`](#bài-3--todo-3-studentservice--studentserviceimpl)
6. [Bài 4 — TODO #4: Tích hợp Spring Boot vào JavaFX + FXML](#bài-4--todo-4-tích-hợp-spring-boot-vào-javafx--fxml)
7. [Bài 5 — TODO #5: Controller — Load / Search / Select](#bài-5--todo-5-controller--load--search--select)
8. [Bài 6 — TODO #6: Controller — Add / Update / Delete / Clear](#bài-6--todo-6-controller--add--update--delete--clear)
9. [Bài 7 — TODO #7: CSS + README](#bài-7--todo-7-css--readme)
10. [Bài 8 — Nâng cao (Bonus)](#bài-8--nâng-cao-bonus)
11. [Kịch bản kiểm thử thủ công](#kịch-bản-kiểm-thử-thủ-công)
12. [Checklist nộp bài](#checklist-nộp-bài)
13. [Xử lý lỗi thường gặp](#xử-lý-lỗi-thường-gặp)

---

## 0. Tổng quan & Rubric

### 0.1 Kiến trúc N-Layer

```
┌────────────────────────── ui-module ──────────────────────────┐
│  student-view.fxml + style.css            (View)              │
│  StudentController  @Component            (Controller) TODO #5,#6
│  JavaFxApplication / Launcher / StudentManagementApplication  │  TODO #4
│  application.properties                   (cấu hình DB)       │
└───────────────┬───────────────────────────────────────────────┘
                │ chỉ phụ thuộc INTERFACE StudentService
┌───────────────▼─────────── data-module ───────────────────────┐
│  StudentService            (interface)                        │  TODO #3
│  StudentServiceImpl        @Service @Transactional            │  TODO #3
│        │ constructor injection                                │
│  StudentRepository extends JpaRepository<Student, Long>       │  TODO #2
│        │ Spring Data JPA tự sinh implementation               │
│  Student  @Entity                                             │  TODO #1
└───────────────┬───────────────────────────────────────────────┘
                ▼
        SQL Server (StudentDB)   |   H2 in-memory (test)
```

```
student-management/
├── pom.xml                                   (parent = spring-boot-starter-parent)
├── data-module/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/example/data/
│       │   ├── pojo/Student.java                         ← TODO #1
│       │   ├── repository/StudentRepository.java         ← TODO #2
│       │   ├── exception/ValidationException.java        ← TODO #3
│       │   ├── exception/StudentNotFoundException.java   ← TODO #3
│       │   ├── service/StudentService.java               ← TODO #3
│       │   └── service/StudentServiceImpl.java           ← TODO #3
│       └── test/java/com/example/data/
│           ├── DataTestApplication.java                  (cho sẵn)
│           ├── repository/StudentRepositoryTest.java     (cho sẵn)
│           └── service/StudentServiceImplTest.java       (cho sẵn)
└── ui-module/
    ├── pom.xml
    └── src/main/
        ├── java/com/example/
        │   ├── StudentManagementApplication.java         ← TODO #4
        │   └── ui/
        │       ├── JavaFxApplication.java                ← TODO #4
        │       ├── Launcher.java                         ← TODO #4
        │       └── controller/StudentController.java     ← TODO #5, #6
        └── resources/
            ├── application.properties                    ← TODO #4
            ├── student-view.fxml                         ← TODO #4
            └── style.css                                 ← TODO #7
```


### 0.3 Rubric (100 điểm + 10 bonus)

| TODO | Nội dung | Điểm | Cách kiểm tra |
|---|---|---|---|
| #1 | Entity `Student` | 10 | Code review + test TODO #2 chạy được |
| #2 | `StudentRepository` (JpaRepository + derived query + `@Query`) | 15 | `StudentRepositoryTest` — **10/10 pass** |
| #3 | `StudentService` + `StudentServiceImpl` | 20 | `StudentServiceImplTest` — **12/12 method pass** |
| #4 | Spring Boot + JavaFX bootstrap, `application.properties`, FXML | 15 | App mở được, kết nối SQL Server |
| #5 | Load / Search / Select | 10 | Manual TC01–TC04 |
| #6 | Add / Update / Delete / Clear | 15 | Manual TC05–TC14 |
| #7 | CSS + README + screenshots | 5 | Review |
| Git | Lịch sử commit đúng quy ước | 10 | `git log --oneline` |
| Bonus | Bài 8 | +10 | Demo |

> ⚠️ **0 điểm cho TODO** nếu: code không compile, sửa/xoá test được cung cấp, hoặc tự viết class `StudentRepositoryImpl` thay vì dùng `JpaRepository`.

---

## 1. Quy ước Git & Commit (BẮT BUỘC)

### 1.1 Quy tắc
1. **Mỗi TODO = đúng 1 commit** (code + test của TODO đó). Không gộp, không commit vụn ("fix", "fix again").
2. **Format [Conventional Commits](https://www.conventionalcommits.org/):** `<type>(<scope>): <mô tả tiếng Anh, thể mệnh lệnh, ≤ 72 ký tự>`
   - `type`: `chore` · `feat` · `test` · `fix` · `style` · `docs` · `refactor`
   - `scope`: `data` hoặc `ui` (bỏ trống với commit khởi tạo / README)
3. **Trước mỗi commit** chạy ở thư mục gốc: `mvn -q clean install` → phải `BUILD SUCCESS`.
4. **Không commit:** `target/`, `.idea/`, `*.iml`, `out/`, `*.log`.
5. Nhánh `main`; bonus làm trên nhánh `feature/...` rồi merge.

### 1.2 Bảng commit bắt buộc (đúng thứ tự)

| # | Sau khi xong | Commit message |
|---|---|---|
| 1 | Bài 0 | `chore: init multi-module spring boot project for JDK 21` |
| 2 | TODO #1 | `feat(data): add Student entity` |
| 3 | TODO #2 | `feat(data): add StudentRepository extending JpaRepository` |
| 4 | TODO #3 | `feat(data): add StudentService and StudentServiceImpl` |
| 5 | TODO #4 | `feat(ui): bootstrap Spring context in JavaFX app` |
| 6 | TODO #5 | `feat(ui): load, search and select students` |
| 7 | TODO #6 | `feat(ui): add, update, delete and clear student` |
| 8 | TODO #7 | `style(ui): add stylesheet and polish UX` |
| 9 | TODO #7 | `docs: add README with setup and run guide` |

### 1.3 Kiểm tra
```bash
git log --oneline --reverse
```
```
a1b2c3d chore: init multi-module spring boot project for JDK 21
b2c3d4e feat(data): add Student entity
c3d4e5f feat(data): add StudentRepository extending JpaRepository
d4e5f6a feat(data): add StudentService and StudentServiceImpl
e5f6a7b feat(ui): bootstrap Spring context in JavaFX app
f6a7b8c feat(ui): load, search and select students
a7b8c9d feat(ui): add, update, delete and clear student
b8c9d0e style(ui): add stylesheet and polish UX
c9d0e1f docs: add README with setup and run guide
```
> 💡 Sửa message commit cuối (chưa push): `git commit --amend -m "..."`. Quên file: `git add <file>` → `git commit --amend --no-edit`.

---

## Bài 0 — Khởi tạo project multi-module Spring Boot

### 🎯 Mục tiêu
Parent project `student-management` (packaging `pom`) kế thừa `spring-boot-starter-parent`, có 2 module con, build bằng JDK 21.

### 📋 TODO 0
- **0.1** Tạo `student-management/pom.xml` (parent).
- **0.2** Tạo `data-module/pom.xml`, `ui-module/pom.xml`.
- **0.3** Tạo cây thư mục, `.gitignore`, `git init`, commit #1.

### 🪜 Hướng dẫn từng bước

**Bước 1.** Kiểm tra: `java -version` và `mvn -version` đều là **21.x**. Nếu Maven dùng JDK khác → đặt `JAVA_HOME` về JDK 21.

**Bước 2.** IntelliJ → `New Project` → **Maven**, JDK 21, GroupId `com.example`, ArtifactId `student-management`, đặt trong thư mục `SpringJPA_javafx`. Xoá thư mục `src/` của project cha.

**Bước 3.** `student-management/pom.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.16</version>
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
> 📌 Spring Boot parent đã quản lý version của Hibernate, Spring Data JPA, mssql-jdbc, H2, JUnit 5 → các module con **không ghi `<version>`** cho những thư viện này. Có thể dùng bất kỳ bản **3.5.x** nào (Spring Boot 4.x đổi package của test annotation — xem mục [Xử lý lỗi](#xử-lý-lỗi-thường-gặp)).

**Bước 4.** `data-module/pom.xml`:
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

        <!-- Test -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <!-- KHÔNG khai báo spring-boot-maven-plugin ở đây: data-module là thư viện, không có main -->
</project>
```

**Bước 5.** `ui-module/pom.xml`:
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

**Bước 6.** Tạo cây thư mục (theo sơ đồ mục 0.1) và `.gitignore`:
```gitignore
target/
.idea/
*.iml
out/
*.log
.DS_Store
```

**Bước 7.** Build & commit:
```bash
cd student-management
mvn -q clean install
git init
git branch -M main
git add .
git commit -m "chore: init multi-module spring boot project for JDK 21"
```

### ✅ Checklist Bài 0
- [ ] `java -version`, `mvn -version` → 21
- [ ] Parent kế thừa `spring-boot-starter-parent` 3.5.x, `<packaging>pom</packaging>`, `<java.version>21</java.version>`
- [ ] 2 `<module>`: `data-module`, `ui-module`
- [ ] `data-module` có `spring-boot-starter-data-jpa`, `mssql-jdbc` (runtime), `h2` + `spring-boot-starter-test` (test)
- [ ] `data-module` **không** có `spring-boot-maven-plugin`; `ui-module` **có**, `mainClass = com.example.ui.Launcher`
- [ ] Không ghi `<version>` cho dependency do Spring Boot quản lý
- [ ] `mvn clean install` → `BUILD SUCCESS` cả 3 project
- [ ] Commit #1 đúng message

---

## Bài 1 — TODO #1: Entity `Student`

### 📋 Yêu cầu
**File:** `data-module/src/main/java/com/example/data/pojo/Student.java`

| Column | Field | Kiểu DB | Ràng buộc |
|---|---|---|---|
| `id` | `Long id` | BIGINT | PK, IDENTITY |
| `name` | `String name` | NVARCHAR(100) | NOT NULL |
| `email` | `String email` | VARCHAR(150) | NOT NULL, UNIQUE |
| `age` | `Integer age` | INT | NOT NULL |

### 🧩 Skeleton
```java
package com.example.data.pojo;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

// TODO 1.1: @Entity, @Table(name = "students")
public class Student {

    // TODO 1.2: @Id + @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 1.3: @Nationalized + @Column(nullable = false, length = 100)
    private String name;

    // TODO 1.4: @Column(nullable = false, unique = true, length = 150)
    private String email;

    // TODO 1.5: @Column(nullable = false)
    private Integer age;

    // TODO 1.6: constructor rỗng + (name, email, age) + (id, name, email, age)
    // TODO 1.7: getter/setter đủ 4 field
    // TODO 1.8: toString() "id | name | email | age"
}
```

### 🪜 Hướng dẫn
1. Tạo package `com.example.data.pojo`, class `Student`, thêm annotation theo bảng.
2. `Alt + Insert` → Constructor / Getter and Setter / toString.
3. `mvn -q -pl data-module compile`.
4. Commit:
   ```bash
   mvn -q clean install
   git add .
   git commit -m "feat(data): add Student entity"
   ```

### ✅ Checklist TODO #1
- [ ] `@Entity`, `@Table(name = "students")`
- [ ] `id` kiểu `Long`, `GenerationType.IDENTITY`
- [ ] `name` có `@Nationalized`, `nullable=false`, `length=100`
- [ ] `email` `unique=true`, `nullable=false`, `length=150`
- [ ] `age` kiểu `Integer`, `nullable=false`
- [ ] Có constructor **không tham số**; getter chuẩn JavaBean (`getName()`…) — `PropertyValueFactory` của JavaFX cần
- [ ] Commit #2 đúng message

---

## Bài 2 — TODO #2: `StudentRepository extends JpaRepository`

### 🎯 Mục tiêu
Hiểu và dùng 3 cách truy vấn của Spring Data JPA:
1. **Method kế thừa** từ `JpaRepository` (không cần viết gì).
2. **Derived query** — Spring sinh JPQL từ **tên method**.
3. **`@Query`** — tự viết JPQL khi điều kiện phức tạp.

### 📖 Kiến thức cần nắm

**Method có sẵn khi `extends JpaRepository<Student, Long>`:**

| Method | Ý nghĩa |
|---|---|
| `save(entity)` | Insert (id null) hoặc update (id có giá trị) |
| `saveAndFlush(entity)` | Save + đẩy SQL xuống DB ngay |
| `findById(id)` | `Optional<Student>` |
| `findAll()` / `findAll(Sort)` / `findAll(Pageable)` | Lấy tất cả / có sắp xếp / phân trang |
| `existsById(id)` | `boolean` |
| `deleteById(id)` | Xoá theo id |
| `count()` | Đếm số dòng |

**Quy tắc đặt tên derived query:** `find|exists|count|delete` + `By` + `<Field><Từ khoá>` (+ `And|Or` …) + `OrderBy<Field>Asc|Desc`

| Tên method | JPQL Spring sinh ra (tương đương) |
|---|---|
| `findAllByOrderByIdAsc()` | `SELECT s FROM Student s ORDER BY s.id ASC` |
| `findByEmailIgnoreCase(String e)` | `... WHERE UPPER(s.email) = UPPER(?1)` |
| `existsByEmailIgnoreCase(String e)` | `SELECT COUNT(s) > 0 ... WHERE UPPER(s.email) = UPPER(?1)` |
| `existsByEmailIgnoreCaseAndIdNot(String e, Long id)` | `... WHERE UPPER(s.email) = UPPER(?1) AND s.id <> ?2` |

> ⚠️ Tên field trong method phải **trùng tên thuộc tính trong Entity** (`Email` ↔ `email`). Viết sai (`findByMail`) → app **lỗi ngay khi khởi động** với `No property 'mail' found for type 'Student'`.

### 📋 Yêu cầu
**File:** `data-module/src/main/java/com/example/data/repository/StudentRepository.java`

| TODO | Method | Cách làm |
|---|---|---|
| 2.1 | Interface | `@Repository public interface StudentRepository extends JpaRepository<Student, Long>` |
| 2.2 | `List<Student> findAllByOrderByIdAsc()` | Derived query |
| 2.3 | `Optional<Student> findByEmailIgnoreCase(String email)` | Derived query |
| 2.4 | `boolean existsByEmailIgnoreCase(String email)` | Derived query (dùng khi **create**) |
| 2.5 | `boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id)` | Derived query (dùng khi **update**) |
| 2.6 | `List<Student> search(String keyword)` | `@Query` JPQL: tên **hoặc** email chứa keyword, không phân biệt hoa thường, `ORDER BY s.id`; tham số `@Param("kw")` |

> ❌ **KHÔNG** tạo class `StudentRepositoryImpl`. Spring Data tự tạo implementation (proxy) lúc chạy.

### 🧩 Skeleton
```java
package com.example.data.repository;

import com.example.data.pojo.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// TODO 2.1: @Repository, extends JpaRepository<Student, Long>
public interface StudentRepository {

    // TODO 2.2

    // TODO 2.3

    // TODO 2.4

    // TODO 2.5

    // TODO 2.6: @Query("SELECT s FROM Student s WHERE ... LIKE LOWER(CONCAT('%', :kw, '%')) ...")
}
```

### 🧪 Test được cung cấp (copy nguyên văn — KHÔNG sửa)

**File 1:** `data-module/src/test/java/com/example/data/DataTestApplication.java`
```java
package com.example.data;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Cấu hình Spring Boot CHỈ dùng cho test của data-module
 *  (data-module là thư viện, không có class main riêng). */
@SpringBootApplication
public class DataTestApplication {
}
```

**File 2:** `data-module/src/test/java/com/example/data/repository/StudentRepositoryTest.java`
```java
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
```
> `@DataJpaTest` tự thay DataSource bằng **H2 in-memory**, tự tạo bảng (`create-drop`) và **rollback sau mỗi test** → không cần SQL Server, không cần `application.properties` trong data-module.

### 🪜 Hướng dẫn
1. Tạo 2 file test ở trên → `mvn -pl data-module test` → lỗi compile (chưa có method) — đúng.
2. Làm TODO 2.1 → 2.6.
3. `mvn -pl data-module test` → `Tests run: 10, Failures: 0, Errors: 0`.
4. Commit:
   ```bash
   mvn -q clean install
   git add .
   git commit -m "feat(data): add StudentRepository extending JpaRepository"
   ```

### ✅ Checklist TODO #2
- [ ] `StudentRepository` là **interface**, `extends JpaRepository<Student, Long>`, có `@Repository`
- [ ] **Không** có class `StudentRepositoryImpl` — chỉ dùng method của `JpaRepository`, derived query và `@Query`
- [ ] 4 derived query đúng tên, đúng kiểu trả về
- [ ] `search` dùng `@Query` + `@Param("kw")`, **không nối chuỗi** keyword vào JPQL
- [ ] `search` tìm cả name lẫn email, không phân biệt hoa thường, có `ORDER BY s.id`
- [ ] `StudentRepositoryTest`: **10/10 pass**, không sửa test
- [ ] Commit #3 đúng message

---

## Bài 3 — TODO #3: `StudentService` + `StudentServiceImpl`

### 🎯 Mục tiêu
- **Service interface** = hợp đồng mà UI sử dụng.
- **ServiceImpl** = chứa business rule, quản lý transaction, gọi Repository.
- UI **chỉ biết interface** `StudentService` → sau này đổi sang web (Spring MVC) vẫn tái sử dụng nguyên data-module.

### 📋 Quy tắc nghiệp vụ (message phải đúng từng ký tự)

| Rule | Ném | Message |
|---|---|---|
| `name` null/blank | `ValidationException` | `Name is required` |
| `name` > 100 ký tự (sau trim) | `ValidationException` | `Name must be at most 100 characters` |
| `email` null / sai regex `^[\w.+-]+@[\w-]+\.[\w.-]+$` | `ValidationException` | `Invalid email` |
| `age` null / ngoài [18, 60] | `ValidationException` | `Age must be between 18 and 60` |
| create: email đã tồn tại (không phân biệt hoa thường) | `ValidationException` | `Email already exists` |
| update: email trùng **student khác** | `ValidationException` | `Email already exists` |
| findById / update / delete: id không tồn tại | `StudentNotFoundException` | `Student not found: <id>` |

- **Chuẩn hoá trước khi validate:** `name.trim()`, `email.trim().toLowerCase()`.
- **Thứ tự validate:** name → email → age.
- **`findAll()`** trả về theo id tăng dần.
- **`search(keyword)`:** null/blank → `findAll()`; ngược lại `search(keyword.trim())`.
- **Transaction:** class `@Transactional(readOnly = true)`; các method ghi (`create`, `update`, `delete`) đánh `@Transactional`.
- **`update`:** lấy entity đang quản lý (`findById`), copy `name/email/age` từ tham số vào, rồi `save`.

### 🧩 Code cho sẵn

```java
// data-module/src/main/java/com/example/data/exception/ValidationException.java
package com.example.data.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
}
```
```java
// data-module/src/main/java/com/example/data/exception/StudentNotFoundException.java
package com.example.data.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(Long id) { super("Student not found: " + id); }
}
```
```java
// data-module/src/main/java/com/example/data/service/StudentService.java
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

### 🧩 Skeleton `StudentServiceImpl`
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

// TODO 3.1: @Service + @Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$");

    // TODO 3.2: field final StudentRepository + constructor injection (KHÔNG dùng @Autowired trên field)

    @Override
    // TODO 3.3: @Transactional — normalize → validate → existsByEmailIgnoreCase → save
    public Student create(Student s) { throw new UnsupportedOperationException("TODO 3.3"); }

    @Override
    // TODO 3.4: findById(...).orElseThrow(() -> new StudentNotFoundException(id))
    public Student findById(Long id) { throw new UnsupportedOperationException("TODO 3.4"); }

    @Override
    // TODO 3.5: findAllByOrderByIdAsc()
    public List<Student> findAll() { throw new UnsupportedOperationException("TODO 3.5"); }

    @Override
    // TODO 3.6: @Transactional — findById (entity managed) → normalize → validate
    //           → existsByEmailIgnoreCaseAndIdNot → copy field → save
    public Student update(Student s) { throw new UnsupportedOperationException("TODO 3.6"); }

    @Override
    // TODO 3.7: @Transactional — !existsById → StudentNotFoundException; ngược lại deleteById
    public void delete(Long id) { throw new UnsupportedOperationException("TODO 3.7"); }

    @Override
    // TODO 3.8
    public List<Student> search(String keyword) { throw new UnsupportedOperationException("TODO 3.8"); }

    private void normalize(Student s) {
        // TODO 3.9
    }

    private void validate(Student s) {
        // TODO 3.10
    }
}
```

### 🧪 Test được cung cấp (copy nguyên văn — KHÔNG sửa)

**File:** `data-module/src/test/java/com/example/data/service/StudentServiceImplTest.java`
```java
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
```
> 12 method test; 2 method `@ParameterizedTest` chạy 6 lần → Maven báo `Tests run: 16` cho class này.

### 🪜 Hướng dẫn
1. Tạo 2 exception + `StudentService` (copy).
2. Tạo `StudentServiceImpl` từ skeleton, copy file test.
3. Làm theo thứ tự: **3.1 → 3.2 → 3.9 normalize → 3.10 validate → 3.3 create** (7 test đầu xanh) → 3.4 → 3.5 → 3.6 → 3.7 → 3.8.
4. `mvn -pl data-module test` → 26 lần chạy (10 + 16) đều pass.
5. Commit:
   ```bash
   mvn -q clean install
   git add .
   git commit -m "feat(data): add StudentService and StudentServiceImpl"
   ```

### ✅ Checklist TODO #3
- [ ] `StudentService` là interface; `StudentServiceImpl implements StudentService` có `@Service`
- [ ] Repository inject qua **constructor**, field `private final`
- [ ] `@Transactional(readOnly = true)` ở class, `@Transactional` ở create/update/delete
- [ ] Service chỉ truy cập dữ liệu qua `StudentRepository` được inject (không `new`)
- [ ] Validate đúng thứ tự & đúng message; email lưu chữ thường, đã trim
- [ ] Kiểm tra trùng email khi create **và** update (bỏ qua chính nó)
- [ ] Ném `StudentNotFoundException` khi id không tồn tại (findById/update/delete)
- [ ] Toàn bộ test data-module pass; không còn `UnsupportedOperationException`
- [ ] Commit #4 đúng message

---

## Bài 4 — TODO #4: Tích hợp Spring Boot vào JavaFX + FXML

### 🎯 Mục tiêu
- Spring Boot khởi động **bên trong** vòng đời JavaFX (tài liệu mục 8.5).
- `FXMLLoader` lấy controller từ Spring (`setControllerFactory(context::getBean)`) → Controller được **inject `StudentService`**.
- Cấu hình SQL Server bằng `application.properties`.

### 📖 Vòng đời

```
Launcher.main()
   └─► Application.launch(JavaFxApplication)
          ├─ init()   : khởi động Spring → tạo DataSource (kết nối SQL Server),
          │             StudentRepository (proxy), StudentServiceImpl, StudentController
          ├─ start()  : FXMLLoader + controllerFactory(context::getBean) → hiện cửa sổ
          └─ stop()   : context.close() (đóng kết nối DB) → Platform.exit()
```

### 📋 TODO 4
- **4.1** `ui-module/src/main/resources/application.properties`
- **4.2** `com.example.StudentManagementApplication` — `@SpringBootApplication` đặt ở **package gốc `com.example`**
- **4.3** `com.example.ui.JavaFxApplication extends Application` — `init/start/stop`
- **4.4** `com.example.ui.Launcher`
- **4.5** `student-view.fxml` (Scene Builder)
- **4.6** `StudentController` khung: `@Component`, constructor nhận `StudentService`, khai báo `@FXML` field + method rỗng
- **4.7** `style.css` rỗng

### 🪜 Hướng dẫn từng bước

**Bước 1 — Database.** SSMS:
```sql
IF DB_ID('StudentDB') IS NULL CREATE DATABASE StudentDB;
```
Bật TCP/IP port 1433 (SQL Server Configuration Manager) và đăng nhập được bằng `sa`.

**Bước 2 — `application.properties`** (TODO 4.1):
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
> Sửa `username/password` theo máy. Hibernate 6 tự nhận dialect SQL Server — **không cần** `hibernate.dialect`.

**Bước 3 — `StudentManagementApplication`** (TODO 4.2):
```java
package com.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Đặt ở package gốc com.example để Spring quét được cả
 *  com.example.data (entity, repository, service) và com.example.ui (controller). */
@SpringBootApplication
public class StudentManagementApplication {
}
```
> ⚠️ Nếu đặt class này trong `com.example.ui` → Spring **không** quét thấy `com.example.data` → lỗi `No qualifying bean of type 'StudentService'` / `Not a managed type: Student`.

**Bước 4 — `JavaFxApplication` skeleton** (TODO 4.3):
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
        // TODO 4.3a: context = new SpringApplicationBuilder(StudentManagementApplication.class)
        //                .web(WebApplicationType.NONE).headless(false).run(...)
    }

    @Override
    public void start(Stage stage) throws Exception {
        // TODO 4.3b: FXMLLoader "/student-view.fxml" + loader.setControllerFactory(context::getBean)
        // TODO 4.3c: Scene 1000x600 + "/style.css"; title "Student Management System"; min 900x500; show
    }

    @Override
    public void stop() {
        // TODO 4.3d: context.close(); Platform.exit();
    }
}
```

**Bước 5 — `Launcher`** (TODO 4.4):
```java
package com.example.ui;

import javafx.application.Application;

/** Chạy class này (không chạy trực tiếp JavaFxApplication) để tránh lỗi
 *  "JavaFX runtime components are missing" khi chạy trên classpath. */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }
}
```

**Bước 6 — Scene Builder.** `File → Settings → Languages & Frameworks → JavaFX → Path to SceneBuilder` → chọn `SceneBuilder.exe`. Tạo `ui-module/src/main/resources/student-view.fxml` → chuột phải → **Open in SceneBuilder**:
1. Root **BorderPane**; panel *Controller* nhập `com.example.ui.controller.StudentController`.
2. **TOP:** VBox (spacing 10, padding 10): Label `Student Management System` (styleClass `header`) + HBox: TextField `searchField` (On Action `#handleSearch`), Button `Search` (`#handleSearch`), Button `Refresh` (`#handleRefresh`).
3. **CENTER:** TableView `tableView`, 4 cột ID/Name/Email/Age.
4. **RIGHT:** VBox (prefWidth 300): Label `Student Form`, 4 cặp Label + TextField (`idField` editable=false, `nameField`, `emailField`, `ageField`), HBox 4 nút `Add`, `Update` (fx:id `btnUpdate`), `Delete` (fx:id `btnDelete`), `Clear`; Label `messageLabel` (wrapText).
5. **BOTTOM:** Label `statusLabel` text `Ready`.
6. Lưu; mở file trong IntelliJ thêm `cellValueFactory` cho 4 cột (Scene Builder không có ô nhập).

FXML cần đạt:
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

**Bước 7 — Controller khung** (TODO 4.6):
```java
package com.example.ui.controller;

import com.example.data.pojo.Student;
import com.example.data.service.StudentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class StudentController {

    @FXML private TableView<Student> tableView;
    @FXML private TextField searchField, idField, nameField, emailField, ageField;
    @FXML private Button btnUpdate, btnDelete;
    @FXML private Label messageLabel, statusLabel;

    private final StudentService studentService;   // INTERFACE, không phải StudentServiceImpl

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @FXML private void handleSearch()  { }
    @FXML private void handleRefresh() { }
    @FXML private void handleAdd()     { }
    @FXML private void handleUpdate()  { }
    @FXML private void handleDelete()  { }
    @FXML private void handleClear()   { }
}
```

**Bước 8 — `style.css`** để trống (TODO 4.7).

**Bước 9 — Chạy thử:**
- IntelliJ: chuột phải `Launcher` → **Run**. Console phải có `Started StudentManagementApplication` và Hibernate in `create table students` (lần đầu).
- Hoặc terminal:
  ```bash
  mvn -q clean install -DskipTests
  mvn -f ui-module/pom.xml spring-boot:run
  ```
  > ⚠️ Không chạy `spring-boot:run` ở thư mục gốc (Maven sẽ chạy cả parent và data-module → lỗi).

**Bước 10 — Commit:**
```bash
mvn -q clean install
git add .
git commit -m "feat(ui): bootstrap Spring context in JavaFX app"
```

### ✅ Checklist TODO #4
- [ ] `application.properties` nằm trong **ui-module** (data-module không chứa cấu hình DB)
- [ ] `@SpringBootApplication` ở package `com.example`
- [ ] Spring khởi động trong `init()`, `WebApplicationType.NONE`
- [ ] `loader.setControllerFactory(context::getBean)` — controller **do Spring tạo**, không `new`
- [ ] `StudentController` có `@Component`, nhận `StudentService` qua constructor
- [ ] `stop()` gọi `context.close()` — đóng cửa sổ thì process Java kết thúc
- [ ] Chạy bằng `Launcher` mở được cửa sổ, bảng `students` được tạo trong `StudentDB` (kiểm tra bằng SSMS: cột `name` kiểu **nvarchar**)
- [ ] `fx:id` / `onAction` khớp 100% với controller
- [ ] Commit #5 đúng message

---

## Bài 5 — TODO #5: Controller — Load / Search / Select

### 🎯 Mục tiêu
- Mở app → bảng hiển thị toàn bộ sinh viên, status `Loaded N students`.
- Search bằng nút hoặc **Enter**; Refresh xoá ô search và tải lại.
- Chọn dòng → form tự điền; **Update/Delete disable** khi chưa chọn.

### 📋 TODO 5
- **5.1** Field `ObservableList<Student> data = FXCollections.observableArrayList()`.
- **5.2** `@FXML public void initialize()`: `tableView.setItems(data)`; listener selection → `fillForm`; bind `disableProperty` của `btnUpdate/btnDelete` với `selectedItemProperty().isNull()`; `loadData()`.
- **5.3** `loadData()`: `data.setAll(studentService.findAll())`; lỗi → `showError("Cannot load data: ...")`, status `Database error`.
- **5.4** `handleSearch()` (status `Found N students`), `handleRefresh()`.
- **5.5** `fillForm`, `showInfo` (chữ xanh), `showError` (chữ đỏ).

### 🪜 Hướng dẫn
1. Chạy app 1 lần để Hibernate tạo bảng, rồi thêm dữ liệu mẫu:
   ```sql
   USE StudentDB;
   INSERT INTO students(name, email, age) VALUES
   (N'Nguyễn Văn An', 'an.nv@fpt.edu.vn',   20),
   (N'Trần Thị Bình', 'binh.tt@fpt.edu.vn', 21),
   (N'Lê Văn Cường',  'cuong.lv@gmail.com', 22),
   (N'Phạm Thị Dung', 'dung.pt@fpt.edu.vn', 19),
   (N'Hoàng Văn Em',  'em.hv@gmail.com',    23);
   ```
2. Làm TODO 5.1 → 5.5; chạy TC01–TC04.
3. Commit:
   ```bash
   mvn -q clean install
   git add .
   git commit -m "feat(ui): load, search and select students"
   ```

### ✅ Checklist TODO #5
- [ ] Logic khởi tạo đặt trong `initialize()`, **không** trong constructor (lúc đó `@FXML` field còn `null`)
- [ ] Một `ObservableList` duy nhất, cập nhật bằng `setAll`
- [ ] Controller chỉ gọi `studentService`, **không** gọi `StudentRepository`
- [ ] Tiếng Việt hiển thị đúng; search theo email hoạt động; Enter = Search
- [ ] Update/Delete disable khi chưa chọn dòng
- [ ] Commit #6 đúng message

---

## Bài 6 — TODO #6: Controller — Add / Update / Delete / Clear

### 📋 TODO 6
- **6.1** `readForm()`: age trống → `ValidationException("Age is required")`; không phải số → `ValidationException("Age must be a number")`; trả về `new Student(name, email, age)`.
- **6.2** `handleAdd()`: `studentService.create(readForm())` → `Added: <name>` → clear → reload.
- **6.3** `handleUpdate()`: **không** sửa object đang chọn; `Student edit = readForm(); edit.setId(selected.getId());` → `studentService.update(edit)` → `Updated: <name>` → clear → reload.
- **6.4** `handleDelete()`: `Alert CONFIRMATION` `Delete student "<name>"?` (OK/Cancel) → OK: `studentService.delete(id)` → `Deleted: <name>`.
- **6.5** `handleClear()`: xoá 4 ô, bỏ chọn dòng, focus `nameField`.
- **6.6** Bắt lỗi: `ValidationException | StudentNotFoundException` → hiện message nguyên văn; `Exception` khác → `System error: ...`. (import `com.example.data.exception.ValidationException`, `com.example.data.exception.StudentNotFoundException`)

### 🪜 Hướng dẫn
1. `readForm()` → `handleClear()` → `handleAdd()` → chạy TC05–TC08.
2. `handleUpdate()` → TC09–TC10 (nhập email sai rồi Update: bảng **vẫn hiện email cũ**).
3. `handleDelete()` → TC11–TC12.
4. Commit:
   ```bash
   mvn -q clean install
   git add .
   git commit -m "feat(ui): add, update, delete and clear student"
   ```

### ✅ Checklist TODO #6
- [ ] Controller **không** chứa rule nghiệp vụ (name/email/age range nằm ở ServiceImpl); chỉ kiểm tra "age là số"
- [ ] `handleUpdate` không gọi `selected.setXxx(...)`
- [ ] Delete luôn hỏi xác nhận; Cancel không xoá
- [ ] Thành công: chữ xanh + form trống + bảng reload; Lỗi: chữ đỏ + giữ nguyên dữ liệu đang nhập
- [ ] Không có exception chưa bắt nào in ra console khi chạy TC05–TC12
- [ ] Commit #7 đúng message

---

## Bài 7 — TODO #7: CSS + README

### 📋 TODO 7
- **7.1** `style.css`:
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
  Commit: `style(ui): add stylesheet and polish UX`
- **7.2** `README.md` ở thư mục gốc: môi trường (JDK 21, Maven, SQL Server), tạo DB, sửa `application.properties`, lệnh build/test/run, sơ đồ kiến trúc, 4 ảnh trong `docs/screenshots/` (`01-list.png`, `02-add.png`, `03-validation.png`, `04-delete-confirm.png`).
  Commit: `docs: add README with setup and run guide`

### ✅ Checklist TODO #7
- [ ] CSS chỉ dùng thuộc tính `-fx-...`
- [ ] README đủ mục, có 4 ảnh chụp
- [ ] 2 commit riêng (#8, #9) đúng message

---

## Bài 8 — Nâng cao (Bonus)

> Mỗi bài làm trên nhánh riêng rồi merge:
> ```bash
> git checkout -b feature/pagination
> # ... code + commit ...
> git checkout main
> git merge --no-ff feature/pagination -m "merge: feature/pagination"
> ```

### 8.1 Phân trang bằng `Pageable` (+3đ)
**TODO:** thêm vào `StudentService` method `Page<Student> findPage(int page, int size)`; Impl dùng `studentRepository.findAll(PageRequest.of(page, size, Sort.by("id")))` (method **có sẵn** của `JpaRepository`). UI dùng control `Pagination` 10 dòng/trang. Viết thêm 1 test `@DataJpaTest`.
**Commit:** `feat: paginate student table with Pageable`

### 8.2 Async loading với `Task` (+3đ)
**TODO:** `loadData()` / `handleSearch()` chạy trên background thread; hiển thị `Loading...`, disable nút Search/Refresh trong lúc tải.
```java
Task<List<Student>> task = new Task<>() {
    @Override protected List<Student> call() { return studentService.findAll(); }
};
task.setOnSucceeded(e -> data.setAll(task.getValue()));
task.setOnFailed(e -> showError(task.getException().getMessage()));
Thread t = new Thread(task, "db-loader");
t.setDaemon(true);
t.start();
```
**Checklist:** [ ] không cập nhật UI trong `call()` · [ ] thread daemon · [ ] lỗi DB hiện chữ đỏ
**Commit:** `feat(ui): load students asynchronously with Task`

### 8.3 Export CSV (+2đ)
**TODO:** nút `Export CSV` + `FileChooser`; ghi UTF-8 có BOM (`﻿`) để Excel đọc đúng tiếng Việt; escape `,` và `"`.
**Commit:** `feat(ui): export students to CSV`

### 8.4 Soft delete (+2đ)
**TODO:** thêm `boolean active = true`; `delete` chỉ đặt `active = false`; repository đổi sang `findAllByActiveTrueOrderByIdAsc()` và thêm `AND s.active = true` vào `@Query`. Cập nhật test cho phù hợp (được phép sửa test **chỉ** trong bài bonus này, ghi rõ trong README).
**Commit:** `feat(data): soft delete students`

---

## Kịch bản kiểm thử thủ công

> Dữ liệu mẫu ở Bài 5. Đánh dấu ✅/❌; TC có 📸 thì chụp ảnh.

| TC | Bước | Kết quả mong đợi | ✅/❌ |
|---|---|---|---|
| TC01 📸 | Mở app | 5 dòng, tiếng Việt đúng dấu, status `Loaded 5 students`, Update/Delete disable | |
| TC02 | Gõ `bình` → Enter | 1 dòng (Trần Thị Bình), `Found 1 students` | |
| TC03 | Gõ `GMAIL` → Search | 2 dòng (theo email, không phân biệt hoa thường) | |
| TC04 | Refresh, click dòng 2 | Ô search trống, đủ 5 dòng; form điền đúng; Update/Delete sáng | |
| TC05 📸 | Add `Võ Thị Giang`, `giang@fpt.edu.vn`, `20` | Xanh `Added: Võ Thị Giang`, bảng 6 dòng | |
| TC06 📸 | Add tên trống | Đỏ `Name is required`, dữ liệu nhập vẫn giữ | |
| TC07 | Add email `abc`, age `abc` | Đỏ `Age must be a number` | |
| TC08 | Add tên `X`, email `AN.NV@FPT.EDU.VN`, age `20` | Đỏ `Email already exists` | |
| TC09 | Chọn Dung → age `25` → Update | Xanh `Updated: Phạm Thị Dung`, bảng hiện 25 | |
| TC10 | Chọn Dung → email `abc` → Update | Đỏ `Invalid email`; bảng **vẫn hiện email cũ** | |
| TC11 📸 | Chọn Em → Delete → Cancel | Không xoá | |
| TC12 | Chọn Em → Delete → OK | Xanh `Deleted: Hoàng Văn Em`, bảng giảm 1 dòng | |
| TC13 | Tắt service SQL Server → Refresh | Đỏ `Cannot load data: ...`, app không đóng | |
| TC14 | Đóng cửa sổ | Process Java kết thúc, không treo trong IntelliJ | |

---

## Checklist nộp bài

**Build & Test**
- [ ] `mvn clean install` ở thư mục gốc → `BUILD SUCCESS` (JDK 21)
- [ ] `StudentRepositoryTest` 10/10, `StudentServiceImplTest` 12/12 method (16 lần chạy) pass
- [ ] Không sửa/xoá test cho sẵn; không còn `UnsupportedOperationException`

**Kiến trúc Repository – Service – ServiceImpl**
- [ ] `StudentRepository extends JpaRepository<Student, Long>`; không có `StudentRepositoryImpl`, không có `persistence.xml`
- [ ] `StudentService` (interface) ← `StudentServiceImpl` (`@Service`, `@Transactional`, constructor injection)
- [ ] `StudentController` (`@Component`) chỉ phụ thuộc `StudentService`
- [ ] `data-module` không import JavaFX; `ui-module` không import `jakarta.persistence` hay `StudentRepository`
- [ ] Không có từ khoá `new StudentServiceImpl` / `new StudentController` trong code

**Chức năng:** [ ] 14 TC ✅ · [ ] 4 ảnh trong `docs/screenshots/`

**Git**
- [ ] `git log --oneline --reverse` → 9 commit đúng thứ tự & message mục 1.2
- [ ] Không có `target/`, `.idea/`, `*.iml` trong repo (`git ls-files | findstr /i "target .idea .iml"` → rỗng)
- [ ] Push:
  ```bash
  git remote add origin <url-repo>
  git push -u origin main
  ```

---

## Xử lý lỗi thường gặp

| Lỗi | Nguyên nhân | Cách sửa |
|---|---|---|
| `JavaFX runtime components are missing` | Chạy trực tiếp `JavaFxApplication` | Chạy `Launcher` |
| `No qualifying bean of type 'StudentService'` | `@SpringBootApplication` không ở `com.example`, hoặc thiếu `@Service` | Chuyển class về package `com.example`; thêm `@Service` cho Impl |
| `Not a managed type: class com.example.data.pojo.Student` | Spring không quét tới package entity | Như trên |
| `No property 'mail' found for type 'Student'` | Tên derived query sai tên field | Tên method phải khớp field: `findByEmail...` |
| `Unable to find main class` khi build data-module | Khai báo `spring-boot-maven-plugin` trong data-module | Xoá plugin khỏi `data-module/pom.xml` |
| `Unable to find a @SpringBootConfiguration` khi chạy test | Thiếu `DataTestApplication` trong `src/test/java/com/example/data` | Tạo file đúng package |
| `package org.springframework.boot.test.autoconfigure.orm.jpa does not exist` | Đang dùng **Spring Boot 4.x** (annotation chuyển package) | Dùng Spring Boot **3.5.x** như đề, hoặc với Boot 4 thêm `spring-boot-data-jpa-test` và import `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest` |
| `Could not resolve dependencies ... data-module:1.0.0` | Chưa install data-module | `mvn clean install` ở thư mục gốc |
| `The TCP/IP connection to the host localhost, port 1433 has failed` | TCP/IP tắt / SQL Server chưa chạy | Bật TCP/IP, restart service |
| `PKIX path building failed` | SSL | URL thêm `encrypt=false;trustServerCertificate=true` |
| Tên hiện `Nguy?n V?n An` | Cột `varchar` | `@Nationalized`; nếu bảng đã tạo: `DROP TABLE students` rồi chạy lại; INSERT dùng `N'...'` |
| `javafx.fxml.LoadException` | `fx:id` / `#handleXxx` không khớp controller, hoặc thiếu `setControllerFactory` | So khớp tên; nếu thiếu controllerFactory, FXMLLoader gọi constructor rỗng → lỗi vì controller cần `StudentService` |
| `NullPointerException` tại `tableView` | Logic đặt trong constructor | Chuyển vào `initialize()` |
| Bảng có dòng nhưng ô trống | `PropertyValueFactory` sai tên / thiếu getter | `property="name"` ↔ `getName()` |
| `Location is not set` | Sai đường dẫn FXML | File ở `src/main/resources/student-view.fxml`, load `"/student-view.fxml"` |
| App đóng nhưng process vẫn chạy | Không `context.close()` / thread không daemon | Sửa `stop()`; `setDaemon(true)` |
| `Not on FX application thread` | Sửa UI trong `Task.call()` | Cập nhật UI trong `setOnSucceeded` / `Platform.runLater` |

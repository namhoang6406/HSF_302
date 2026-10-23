# BÀI TẬP THYMELEAF – VARIABLE EXPRESSIONS VỚI LỚP STUDENT

> **Môn:** HSF302 – Spring Boot
> **Chủ đề:** Thymeleaf Variable Expressions `${...}`, Validation, Layout Dialect, Bootstrap offline, Spring Data JPA + SQL Server
> **Thời lượng gợi ý:** 3 – 4 tiết (135 – 180 phút)
> **Mức độ:** Cơ bản
> **Môi trường:** Spring Boot **4.1.1** · Spring Framework 7 · Java **21** (tối thiểu 17) · Jakarta EE 11 · Spring Data JPA (Hibernate 7) · **SQL Server** 2019/2022 · Thymeleaf 3.1 · Layout Dialect 3.4 · Bootstrap 5.3 (offline)

---

## MỤC LỤC

- [Mục tiêu](#mục-tiêu)
- [Phần A – Lý thuyết](#phần-a--lý-thuyết)
- [Phần B – Bài tập step-by-step](#phần-b--bài-tập-step-by-step)
- [Phần C – Kiểm thử](#phần-c--kiểm-thử)
- [Phần D – Checklist hoàn thành](#phần-d--checklist-hoàn-thành)
- [Phần E – Thang điểm](#phần-e--thang-điểm)
- [Phần F – Lỗi thường gặp](#phần-f--lỗi-thường-gặp)
- [Phần G – Bài tập mở rộng](#phần-g--bài-tập-mở-rộng)
- [Phần H – Đáp án tham khảo](#phần-h--đáp-án-tham-khảo)

---

## MỤC TIÊU

Sau bài này sinh viên có thể:

1. Hiểu và sử dụng thành thạo **Variable Expression `${...}`** để hiển thị dữ liệu từ `Model`.
2. Sử dụng các toán tử trong expression: số học, so sánh, logic, ternary, Elvis `?:`, safe navigation `?.`, literal substitution `|...|`.
3. Sử dụng **Expression Utility Objects**: `#strings`, `#numbers`, `#temporals`, `#lists`, `#fields`, `#ids`.
4. Duyệt danh sách với `th:each` và biến trạng thái (`iterStat`).
5. Validate dữ liệu form với **Bean Validation** (`@NotBlank`, `@Email`, `@Pattern`, ...) và hiển thị lỗi bằng Thymeleaf.
6. Tổ chức giao diện bằng **Thymeleaf Layout Dialect** (`layout:decorate`, `layout:fragment`).
7. Sử dụng **Bootstrap 5 offline** (không phụ thuộc CDN/Internet).
8. Ánh xạ lớp `Student` thành **JPA Entity** (`@Entity`, `@Table`, `@Id`, `@Column`, ...) và lưu dữ liệu vào **SQL Server** thông qua **Spring Data JPA** (`JpaRepository`, derived query).

---

# PHẦN A – LÝ THUYẾT

## A1. Thymeleaf là gì?

**Thymeleaf** là một *server-side template engine* cho Java, được Spring khuyến nghị dùng với Spring MVC / Spring Boot.

- **Natural template:** file `.html` vẫn là HTML hợp lệ, mở trực tiếp bằng trình duyệt vẫn xem được (dữ liệu mẫu), khi chạy qua server thì dữ liệu thật thay thế vào.
- Thymeleaf xử lý các thuộc tính có tiền tố `th:` (vd: `th:text`, `th:each`).
- Khai báo namespace ở thẻ `<html>`:

```html
<html xmlns:th="http://www.thymeleaf.org">
```

**Luồng dữ liệu Controller → View:**

```
Controller                         Template (Thymeleaf)
──────────                         ────────────────────
model.addAttribute("student", s)   →   ${student}
model.addAttribute("students", l)  →   ${students}
return "students/list";            →   templates/students/list.html
```

Mọi attribute trong `Model` trở thành **biến ngữ cảnh (context variable)** và được truy cập bằng `${tênBiến}`.

## A2. Năm loại Standard Expression

| Cú pháp | Tên | Dùng để | Ví dụ |
|---|---|---|---|
| `${...}` | **Variable Expression** | Truy cập biến trong Model/context | `${student.fullName}` |
| `*{...}` | Selection Expression | Truy cập thuộc tính của object đã chọn bằng `th:object` | `*{fullName}` |
| `#{...}` | Message Expression | Lấy message i18n từ `messages.properties` | `#{student.title}` |
| `@{...}` | Link (URL) Expression | Tạo URL có context path | `@{/students/{id}(id=${s.id})}` |
| `~{...}` | Fragment Expression | Chèn/tham chiếu fragment | `~{layout/main}` |

> Bài này tập trung vào **`${...}`**, có kết hợp `*{...}`, `@{...}`, `~{...}` ở mức cơ bản.

## A3. Variable Expression `${...}` chi tiết

Trong ứng dụng Spring, biểu thức bên trong `${...}` được đánh giá bằng **SpEL (Spring Expression Language)**.

### A3.1. Truy cập thuộc tính (property) – gọi getter

```html
<p th:text="${student.fullName}">Tên mẫu</p>          <!-- gọi getFullName() -->
<p th:text="${student.active}">true</p>               <!-- gọi isActive() với boolean -->
<p th:text="${student['email']}">a@b.com</p>          <!-- cú pháp ngoặc vuông -->
```

> `${student.rank}` sẽ gọi `getRank()` – **không bắt buộc** phải có field `rank`, chỉ cần có getter. Đây là cách tạo *thuộc tính tính toán* (computed property).

### A3.2. Gọi phương thức

```html
<p th:text="${student.getFullName().toUpperCase()}"></p>
<p th:text="${student.fullName.length()}"></p>
```

### A3.3. Truy cập List, Map, mảng

```html
<p th:text="${students[0].fullName}"></p>      <!-- phần tử đầu của List -->
<p th:text="${majors['SE']}"></p>              <!-- Map với key 'SE' -->
<p th:text="${majors[student.major]}"></p>     <!-- key lấy từ biến khác -->
```

### A3.4. Các biến có sẵn khác

```html
<p th:text="${param.keyword}"></p>     <!-- request parameter ?keyword=... -->
<p th:text="${session.username}"></p>  <!-- session attribute -->
```

> Từ **Thymeleaf 3.1** (dùng trong Spring Boot 3 và Spring Boot 4), các object `#request`, `#session`, `#servletContext`, `#response` **đã bị loại bỏ**. Hãy truyền dữ liệu qua `Model`, hoặc dùng `${param.x}`, `${session.x}`.

### A3.5. Literal (hằng) và nối chuỗi

```html
<p th:text="'Xin chào ' + ${student.fullName} + '!'"></p>   <!-- nối chuỗi bằng + -->
<p th:text="|Xin chào ${student.fullName}!|"></p>           <!-- literal substitution: gọn hơn -->
```

| Loại literal | Ví dụ |
|---|---|
| Chuỗi | `'Đang học'` |
| Số | `10`, `8.5` |
| Boolean | `true`, `false` |
| Null | `null` |

### A3.6. Toán tử

| Nhóm | Toán tử | Dạng chữ (nên dùng trong HTML) |
|---|---|---|
| Số học | `+ - * / %` | `div`, `mod` |
| So sánh | `> < >= <=` | `gt lt ge le` |
| Bằng | `== !=` | `eq ne` |
| Logic | `and or ! not` | |
| Điều kiện | `(cond) ? a : b`, `(cond) ? a` | |
| Mặc định (Elvis) | `${x} ?: 'giá trị mặc định'` | |
| Không làm gì | `_` (no-op – giữ nguyên nội dung mẫu) | |

```html
<span th:text="${student.gpa ge 8.0} ? 'Giỏi' : 'Chưa giỏi'"></span>
<span th:text="${student.email} ?: 'Chưa cập nhật'"></span>
<span th:text="${student.nickname} ?: _">Không có biệt danh</span>
<tr th:classappend="${s.gpa lt 5} ? 'table-danger'"></tr>
```

> Nên dùng `lt`, `gt`, `le`, `ge` thay cho `<`, `>` bên trong thuộc tính HTML để tránh lỗi parse.

### A3.7. Safe navigation `?.` (SpEL)

```html
<p th:text="${student?.fullName}"></p>   <!-- student null → không ném lỗi, trả về null -->
```

Không có `?.`, nếu `student` là `null` sẽ gặp lỗi `EL1007E: Property or field 'fullName' cannot be found on null`.

### A3.8. Collection selection & projection (SpEL – nâng cao)

```html
<!-- Lọc: các sinh viên đang học -->
<p th:text="${#lists.size(students.?[active])}"></p>
<!-- Chiếu: danh sách GPA -->
<p th:text="${students.![gpa]}"></p>
```

## A4. Các thuộc tính `th:*` hay dùng

| Thuộc tính | Ý nghĩa | Ví dụ |
|---|---|---|
| `th:text` | Gán nội dung text (**có escape HTML**, an toàn XSS) | `th:text="${s.fullName}"` |
| `th:utext` | Gán nội dung **không escape** (render HTML) – cẩn thận XSS | `th:utext="${html}"` |
| `th:value` | Gán thuộc tính `value` | `th:value="${keyword}"` |
| `th:href`, `th:src`, `th:action` | URL | `th:href="@{/students}"` |
| `th:each` | Lặp | `th:each="s, stat : ${students}"` |
| `th:if` / `th:unless` | Hiển thị có điều kiện | `th:if="${#lists.isEmpty(students)}"` |
| `th:switch` / `th:case` | Rẽ nhánh nhiều trường hợp | `th:case="'Giỏi'"`, `th:case="*"` |
| `th:class` / `th:classappend` | Gán / nối thêm class CSS | `th:classappend="${ok} ? 'active'"` |
| `th:object` | Chọn object cho form | `th:object="${student}"` |
| `th:field` | Bind input với field (sinh `id`, `name`, `value`) | `th:field="*{fullName}"` |
| `th:errors` | Hiển thị message lỗi của field | `th:errors="*{fullName}"` |
| `th:errorclass` | Thêm class khi field có lỗi | `th:errorclass="is-invalid"` |

### Biến trạng thái vòng lặp `th:each`

```html
<tr th:each="s, stat : ${students}">
    <td th:text="${stat.count}">1</td>
</tr>
```

| Thuộc tính | Ý nghĩa |
|---|---|
| `stat.index` | Chỉ số, bắt đầu từ **0** |
| `stat.count` | Số thứ tự, bắt đầu từ **1** |
| `stat.size` | Tổng số phần tử |
| `stat.even` / `stat.odd` | Dòng chẵn / lẻ |
| `stat.first` / `stat.last` | Phần tử đầu / cuối |
| `stat.current` | Phần tử hiện tại |

> Nếu không khai báo `stat`, Thymeleaf tự tạo biến tên `sStat` (tên biến + `Stat`).

## A5. Expression Utility Objects

Dùng bên trong `${...}`, bắt đầu bằng `#`:

| Object | Ví dụ | Kết quả |
|---|---|---|
| `#strings` | `${#strings.toUpperCase(s.fullName)}` | `NGUYỄN VĂN AN` |
| | `${#strings.isEmpty(keyword)}` | `true` nếu null hoặc rỗng |
| | `${#strings.abbreviate(s.email, 15)}` | `annv@fpt.ed...` |
| `#numbers` | `${#numbers.formatDecimal(s.gpa, 1, 2)}` | `8.75` |
| | `${#numbers.formatDecimal(1234567.5, 1, 'COMMA', 2, 'POINT')}` | `1,234,567.50` |
| `#temporals` | `${#temporals.format(s.dateOfBirth, 'dd/MM/yyyy')}` | `12/05/2004` |
| | `${#temporals.year(#temporals.createNow())}` | Năm hiện tại |
| `#lists` | `${#lists.size(students)}` | Số phần tử |
| | `${#lists.isEmpty(students)}` | `true/false` |
| `#bools` | `${#bools.isTrue(s.active)}` | |
| `#aggregates` | `${#aggregates.avg(students.![gpa])}` | Trung bình |
| `#fields` | `${#fields.hasErrors('fullName')}` | Field có lỗi? |
| `#ids` | `${#ids.prev('active')}` | id của checkbox vừa sinh |

> `#temporals` dùng cho `java.time` (`LocalDate`, `LocalDateTime`). `#dates` dùng cho `java.util.Date` (cũ).

## A6. `${...}` và `*{...}` – khác nhau thế nào?

```html
<!-- Cách 1: dùng ${...} đầy đủ -->
<div>
    <p th:text="${student.fullName}"></p>
    <p th:text="${student.email}"></p>
</div>

<!-- Cách 2: chọn object bằng th:object, rồi dùng *{...} -->
<div th:object="${student}">
    <p th:text="*{fullName}"></p>
    <p th:text="*{email}"></p>
</div>
```

Hai cách cho **cùng kết quả**. `*{...}` đánh giá trên object đã chọn bằng `th:object`; nếu không có `th:object`, `*{...}` hoạt động giống `${...}`.

## A7. Validation với Bean Validation + Thymeleaf

### A7.1. Các annotation thường dùng (`jakarta.validation.constraints`)

| Annotation | Áp dụng | Ý nghĩa |
|---|---|---|
| `@NotNull` | Mọi kiểu | Không được `null` |
| `@NotBlank` | `String` | Không null, không rỗng, không toàn khoảng trắng |
| `@NotEmpty` | String, Collection | Không null, không rỗng |
| `@Size(min, max)` | String, Collection | Độ dài trong khoảng |
| `@Email` | `String` | Đúng định dạng email |
| `@Pattern(regexp)` | `String` | Khớp biểu thức chính quy |
| `@Min`, `@Max` | Số nguyên | Giá trị nhỏ nhất/lớn nhất |
| `@DecimalMin`, `@DecimalMax` | Số thực | Giá trị nhỏ nhất/lớn nhất (dạng chuỗi) |
| `@Past`, `@Future` | Ngày tháng | Trong quá khứ / tương lai |
| `@Positive` | Số | Lớn hơn 0 |

### A7.2. Controller

```java
@PostMapping("/save")
public String save(@Valid @ModelAttribute("student") Student student,
                   BindingResult result) {   // BindingResult PHẢI đứng NGAY SAU tham số @Valid
    if (result.hasErrors()) {
        return "students/form";              // trả lại form, KHÔNG redirect
    }
    ...
    return "redirect:/students";            // thành công → redirect (PRG pattern)
}
```

### A7.3. Hiển thị lỗi trong Thymeleaf

```html
<form th:object="${student}" ...>
    <input th:field="*{fullName}" class="form-control" th:errorclass="is-invalid">
    <div class="invalid-feedback"
         th:if="${#fields.hasErrors('fullName')}"
         th:errors="*{fullName}"></div>

    <!-- Tất cả lỗi -->
    <ul th:if="${#fields.hasErrors('*')}">
        <li th:each="err : ${#fields.errors('*')}" th:text="${err}"></li>
    </ul>
</form>
```

### A7.4. Lỗi chuyển kiểu (type mismatch)

Khi người dùng nhập `abc` vào ô GPA (kiểu `Double`), Spring báo lỗi chuyển kiểu với message rất dài và khó hiểu. Ta ghi đè message bằng `messages.properties`:

```properties
typeMismatch.java.lang.Double=Giá trị phải là số
typeMismatch.student.gpa=GPA phải là số thực từ 0 đến 10
```

## A8. Thymeleaf Layout Dialect

**Vấn đề:** mọi trang đều có chung `<head>`, navbar, footer. Copy vào từng file → khó bảo trì.

**Giải pháp:** dùng **Layout Dialect** – tạo 1 file layout (khung), các trang con chỉ khai báo phần nội dung riêng.

```
templates/layout/main.html   (Layout – khung chung)
┌──────────────────────────────┐
│ <head> CSS Bootstrap         │
│ NAVBAR                       │
│ ┌──────────────────────────┐ │
│ │ layout:fragment="content"│ │  ← trang con điền vào đây
│ └──────────────────────────┘ │
│ FOOTER                       │
│ JS Bootstrap                 │
└──────────────────────────────┘
```

**Layout (`layout/main.html`):**

```html
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <title layout:title-pattern="$CONTENT_TITLE | $LAYOUT_TITLE">Student Manager</title>
</head>
<body>
    <nav>...</nav>
    <main layout:fragment="content"></main>
    <footer>...</footer>
</body>
</html>
```

**Trang con (`students/list.html`):**

```html
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/main}">
<head><title>Danh sách sinh viên</title></head>
<body>
    <section layout:fragment="content">
        ... nội dung riêng ...
    </section>
</body>
</html>
```

| Thuộc tính | Đặt ở | Ý nghĩa |
|---|---|---|
| `layout:decorate="~{layout/main}"` | `<html>` trang con | Trang này dùng layout nào |
| `layout:fragment="content"` | Cả layout và trang con | Vùng được thay thế |
| `layout:title-pattern` | `<title>` của layout | Ghép tiêu đề: `$CONTENT_TITLE` (trang con), `$LAYOUT_TITLE` (layout) |

> **So sánh:** Thymeleaf thuần có `th:insert` / `th:replace` (trang con *kéo* fragment vào). Layout Dialect ngược lại: trang con *được bọc* bởi layout → phù hợp làm khung trang hơn.

## A9. Bootstrap offline

**Online (CDN)** – cần Internet:

```html
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
```

**Offline** – tải file về đặt trong `src/main/resources/static`:

```
static/
├── css/bootstrap.min.css
└── js/bootstrap.bundle.min.js     (bundle = đã gồm Popper.js)
```

```html
<link rel="stylesheet" th:href="@{/css/bootstrap.min.css}">
<script th:src="@{/js/bootstrap.bundle.min.js}"></script>
```

> Spring Boot phục vụ file trong `static/` từ gốc URL → đường dẫn là `/css/...`, **không có** `/static`.
> Dùng `@{...}` để URL tự thêm context path (nếu có).

## A10. Ánh xạ Entity với SQL Server bằng Spring Data JPA

**Kiến trúc tầng dữ liệu:**

```
Controller  →  Service  →  StudentRepository (JpaRepository)  →  Hibernate  →  JDBC (mssql-jdbc)  →  SQL Server
                                                                                                   bảng dbo.Students
```

### A10.1. Các annotation ánh xạ (`jakarta.persistence`)

| Annotation | Đặt ở | Ý nghĩa |
|---|---|---|
| `@Entity` | Lớp | Đánh dấu lớp là entity – được Hibernate quản lý, ánh xạ tới 1 bảng |
| `@Table(name = "Students")` | Lớp | Tên bảng (mặc định = tên lớp) |
| `@Id` | Field | Khóa chính |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Field `id` | Khóa tự tăng – SQL Server sinh bằng `IDENTITY(1,1)` |
| `@Column(name, nullable, length, unique)` | Field | Tên cột, `NOT NULL`, độ dài `VARCHAR(n)`, ràng buộc `UNIQUE` |
| `@Nationalized` (`org.hibernate.annotations`) | Field `String` | Dùng `NVARCHAR` thay vì `VARCHAR` → lưu được **tiếng Việt có dấu** |
| `@Transient` | Field / getter | Không ánh xạ xuống cột nào |

**Bảng SQL Server tương ứng với entity `Student`** (Hibernate tự sinh khi `ddl-auto=update`):

| Field Java | Kiểu Java | Cột SQL Server | Kiểu SQL Server |
|---|---|---|---|
| `id` | `Long` | `id` | `BIGINT IDENTITY(1,1)` – PK |
| `studentCode` | `String` | `student_code` | `VARCHAR(8) NOT NULL UNIQUE` |
| `fullName` | `String` | `full_name` | `NVARCHAR(50) NOT NULL` |
| `email` | `String` | `email` | `VARCHAR(100) NOT NULL` |
| `dateOfBirth` | `LocalDate` | `date_of_birth` | `DATE NOT NULL` |
| `gpa` | `Double` | `gpa` | `FLOAT NOT NULL` |
| `major` | `String` | `major` | `VARCHAR(2) NOT NULL` |
| `active` | `boolean` | `active` | `BIT NOT NULL` |

> **Bean Validation ≠ ràng buộc database.** `@NotBlank`, `@Size`... kiểm tra dữ liệu ở tầng Java (trước khi lưu, hiển thị lỗi lên form). `@Column(nullable = false, unique = true, length = ...)` tạo ràng buộc ở tầng database (lớp bảo vệ cuối cùng). Một entity tốt nên có **cả hai**.

> `getAge()`, `getRank()` **không có field** tương ứng. Vì `@Id` đặt trên field nên Hibernate dùng *field access* – chỉ ánh xạ các **field**, getter tính toán không bị ánh xạ thành cột. Không cần `@Transient`.

### A10.2. Spring Data JPA Repository

Chỉ cần khai báo **interface** kế thừa `JpaRepository<Entity, KiểuKhóaChính>`, Spring tự tạo class cài đặt:

```java
public interface StudentRepository extends JpaRepository<Student, Long> {
}
```

| Phương thức có sẵn | SQL tương ứng |
|---|---|
| `findAll()` / `findAll(Sort.by("id"))` | `SELECT * FROM Students [ORDER BY id]` |
| `findById(id)` → `Optional<Student>` | `SELECT ... WHERE id = ?` |
| `save(student)` | `id == null` → `INSERT`; `id != null` → `UPDATE` |
| `deleteById(id)` | `DELETE FROM Students WHERE id = ?` |
| `count()` | `SELECT COUNT(*) FROM Students` |

**Derived query** – Spring sinh câu truy vấn từ **tên phương thức**:

| Tên phương thức | Câu WHERE sinh ra |
|---|---|
| `findByMajor(String major)` | `WHERE major = ?` |
| `findByGpaGreaterThanEqual(Double gpa)` | `WHERE gpa >= ?` |
| `findByFullNameContainingIgnoreCase(String kw)` | `WHERE UPPER(full_name) LIKE UPPER('%' + ? + '%')` |
| `existsByStudentCodeIgnoreCase(String code)` | `SELECT 1 ... WHERE UPPER(student_code) = UPPER(?)` → `boolean` |
| `existsByStudentCodeIgnoreCaseAndIdNot(String code, Long id)` | `... WHERE UPPER(student_code) = UPPER(?) AND id <> ?` |
| `findBy...OrderByIdAsc(...)` | thêm `ORDER BY id ASC` |

### A10.3. `spring.jpa.hibernate.ddl-auto`

| Giá trị | Hành vi | Dùng khi |
|---|---|---|
| `create` | Xóa bảng cũ, tạo lại mỗi lần chạy (**mất dữ liệu**) | Thử nghiệm |
| `create-drop` | Như `create`, xóa bảng khi tắt app | Test |
| `update` | Tạo bảng/cột còn thiếu, **giữ dữ liệu** | **Bài tập này** / phát triển |
| `validate` | Chỉ kiểm tra entity khớp với bảng, sai → lỗi khi khởi động | Production |
| `none` | Không làm gì | Production (dùng Flyway/Liquibase) |

---

# PHẦN B – BÀI TẬP STEP-BY-STEP

## B0. Đề bài

Xây dựng ứng dụng web **Student Manager** quản lý sinh viên, dữ liệu **lưu trong SQL Server** (database `StudentDB`, bảng `Students`) thông qua **Spring Data JPA**, với các chức năng:

| # | Chức năng | URL | Method |
|---|---|---|---|
| 1 | Danh sách sinh viên + tìm kiếm + thống kê | `/students?keyword=...` | GET |
| 2 | Xem chi tiết sinh viên | `/students/{id}` | GET |
| 3 | Form thêm mới | `/students/new` | GET |
| 4 | Form cập nhật | `/students/{id}/edit` | GET |
| 5 | Lưu (thêm/cập nhật) có validation | `/students/save` | POST |
| 6 | Xóa sinh viên | `/students/{id}/delete` | POST |
| 7 | Trang chủ chuyển hướng về danh sách | `/` | GET |

**Entity `Student`** (ánh xạ bảng `Students` trong SQL Server):

| Field | Kiểu | Cột SQL Server | Ràng buộc |
|---|---|---|---|
| `id` | `Long` | `id BIGINT IDENTITY` – PK | Tự sinh (identity) |
| `studentCode` | `String` | `student_code VARCHAR(8)` | Bắt buộc; dạng `SE`/`SS`/`SA`/`HE` + 6 chữ số (vd: `SE180001`); **UNIQUE** – không trùng |
| `fullName` | `String` | `full_name NVARCHAR(50)` | Bắt buộc; 2 – 50 ký tự; tiếng Việt có dấu |
| `email` | `String` | `email VARCHAR(100)` | Bắt buộc; đúng định dạng email |
| `dateOfBirth` | `LocalDate` | `date_of_birth DATE` | Bắt buộc; phải là ngày trong quá khứ |
| `gpa` | `Double` | `gpa FLOAT` | Bắt buộc; từ 0.0 đến 10.0 |
| `major` | `String` | `major VARCHAR(2)` | Bắt buộc; một trong `SE`, `AI`, `IA`, `GD` |
| `active` | `boolean` | `active BIT` | Mặc định `true` |

**Thuộc tính tính toán** (chỉ có getter, không có field):

- `getAge()`: tuổi tính từ `dateOfBirth`.
- `getRank()`: xếp loại theo GPA – `>= 9` Xuất sắc, `>= 8` Giỏi, `>= 6.5` Khá, `>= 5` Trung bình, còn lại Yếu.

**Giao diện mong đợi – trang danh sách:**

```
┌───────────────────────────────────────────────────────────────────────┐
│ Student Manager      Danh sách   Thêm mới                             │
├───────────────────────────────────────────────────────────────────────┤
│ ✔ Thêm sinh viên Nguyễn Văn An thành công!                       [x]  │
│                                                                       │
│ Danh sách sinh viên                               [+ Thêm sinh viên]  │
│ [ Tìm theo mã SV hoặc họ tên...      ] [Tìm kiếm]                     │
│                                                                       │
│ ┌ Tổng SV ─┐ ┌ GPA TB ──┐ ┌ Đang học ┐                                │
│ │    5     │ │   7.52   │ │    4     │                                │
│ └──────────┘ └──────────┘ └──────────┘                                │
│ # | Mã SV    | Họ tên        | ... | GPA  | Xếp loại | Trạng thái | ⚙ │
│ 1 | SE180001 | NGUYỄN VĂN AN | ... | 8.75 | [Giỏi]   | [Đang học] |   │
│ 2 | ...                                                               │
├───────────────────────────────────────────────────────────────────────┤
│                  © 2026 HSF302 – Student Manager                      │
└───────────────────────────────────────────────────────────────────────┘
```

## B1. Cấu trúc project

```
student-thymeleaf/
├── pom.xml
└── src/main/
    ├── java/com/hsf302/student/
    │   ├── StudentThymeleafApplication.java
    │   ├── entity/
    │   │   └── Student.java              (@Entity → bảng Students)
    │   ├── repository/
    │   │   └── StudentRepository.java    (JpaRepository)
    │   ├── config/
    │   │   └── DataInitializer.java      (thêm 5 SV mẫu khi bảng rỗng)
    │   ├── service/
    │   │   └── StudentService.java
    │   └── controller/
    │       ├── HomeController.java
    │       └── StudentController.java
    └── resources/
        ├── application.properties
        ├── messages.properties
        ├── static/
        │   ├── css/
        │   │   ├── bootstrap.min.css
        │   │   └── style.css
        │   └── js/
        │       └── bootstrap.bundle.min.js
        └── templates/
            ├── layout/
            │   └── main.html
            └── students/
                ├── list.html
                ├── detail.html
                └── form.html
```

---

## STEP 1 – Tạo project (Spring Boot 4.1.1)

1. Vào **https://start.spring.io** (hoặc IntelliJ → *New Project* → *Spring Boot*).
2. Cấu hình:
   - Project: **Maven**, Language: **Java**, Spring Boot: **4.1.1**
   - Group: `com.hsf302`, Artifact: `student-thymeleaf`, Package: `com.hsf302.student`
   - Java: **21** (Spring Boot 4 hỗ trợ Java 17 trở lên; khuyến nghị LTS 21 hoặc 25)
3. Dependencies: **Spring Web**, **Thymeleaf**, **Validation**, **Spring Data JPA**, **MS SQL Server Driver**, **Spring Boot DevTools**.
4. Mở `pom.xml`, kiểm tra phần `parent` / `java.version` và **thêm thủ công** Layout Dialect (Initializr không có):

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.1.1</version>
    <relativePath/>
</parent>

<properties>
    <java.version>21</java.version>
</properties>

<dependencies>
    <!-- Spring Boot 4: starter "web" đã đổi tên thành "webmvc" -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webmvc</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Spring Data JPA (Hibernate 7) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <!-- JDBC driver SQL Server: version được Spring Boot quản lý -->
    <dependency>
        <groupId>com.microsoft.sqlserver</groupId>
        <artifactId>mssql-jdbc</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Thymeleaf Layout Dialect: version được Spring Boot quản lý -->
    <dependency>
        <groupId>nz.net.ultraq.thymeleaf</groupId>
        <artifactId>thymeleaf-layout-dialect</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

> Nếu Maven báo thiếu version cho `thymeleaf-layout-dialect`, thêm `<version>3.4.0</version>`.
> Spring Boot **tự cấu hình** `LayoutDialect` khi thư viện có trên classpath – không cần tạo `@Bean`.

5. Bấm **Reload Maven Project** (IntelliJ) để tải thư viện.

---

## STEP 2 – Cài Bootstrap offline

1. Vào **https://getbootstrap.com** → *Download* → **Compiled CSS and JS** → tải `bootstrap-5.3.x-dist.zip`.
2. Giải nén, copy:

| File trong zip | Copy vào |
|---|---|
| `css/bootstrap.min.css` | `src/main/resources/static/css/` |
| `css/bootstrap.min.css.map` *(tùy chọn – tránh cảnh báo 404 trong DevTools)* | `src/main/resources/static/css/` |
| `js/bootstrap.bundle.min.js` | `src/main/resources/static/js/` |
| `js/bootstrap.bundle.min.js.map` *(tùy chọn)* | `src/main/resources/static/js/` |

3. Tạo file `static/css/style.css`:

```css
body { background-color: #f5f7fa; }
.navbar-brand { font-weight: 700; }
.stat-card .value { font-size: 1.8rem; font-weight: 700; }
footer { font-size: .9rem; }
```

> **Cách khác:** dùng WebJars (`org.webjars:bootstrap`) – cũng offline sau khi Maven tải về lần đầu. Trong bài này dùng cách copy file để hiểu rõ cơ chế static resources.

---

## STEP 3 – Chuẩn bị SQL Server

1. Mở **SQL Server Configuration Manager** → *SQL Server Network Configuration* → *Protocols for MSSQLSERVER* → **TCP/IP = Enabled** → tab *IP Addresses* → mục **IPAll**: `TCP Port = 1433` → **Restart** service *SQL Server (MSSQLSERVER)*.
2. Đảm bảo SQL Server bật **SQL Server and Windows Authentication mode** (SSMS → chuột phải server → *Properties* → *Security*) và tài khoản `sa` đã được *Enable* + có mật khẩu.
3. Mở **SSMS**, tạo database (chỉ tạo database, **bảng do Hibernate tự sinh**):

```sql
CREATE DATABASE StudentDB;
GO
```

> Bảng `Students` sẽ được Hibernate tạo tự động khi chạy ứng dụng lần đầu (`ddl-auto=update`). Câu lệnh tương đương mà Hibernate sinh ra (để tham khảo – **không cần chạy**):
>
> ```sql
> CREATE TABLE Students (
>     id             BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
>     student_code   VARCHAR(8)    NOT NULL UNIQUE,
>     full_name      NVARCHAR(50)  NOT NULL,
>     email          VARCHAR(100)  NOT NULL,
>     date_of_birth  DATE          NOT NULL,
>     gpa            FLOAT         NOT NULL,
>     major          VARCHAR(2)    NOT NULL,
>     active         BIT           NOT NULL
> );
> ```

---

## STEP 4 – Cấu hình `application.properties` và `messages.properties`

**`src/main/resources/application.properties`**

```properties
spring.application.name=student-thymeleaf
server.port=8080

# ===== SQL Server =====
# encrypt=true là mặc định của mssql-jdbc → cần trustServerCertificate=true khi chạy local
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=12345
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# ===== JPA / Hibernate =====
# update: tự tạo/cập nhật bảng theo entity, giữ nguyên dữ liệu
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
# Tắt Open Session In View (tránh cảnh báo khi khởi động)
spring.jpa.open-in-view=false
# Không cần khai báo dialect – Hibernate 7 tự nhận diện SQLServerDialect

# Tắt cache để sửa HTML không cần restart
spring.thymeleaf.cache=false

# Message cho validation / type mismatch
spring.messages.basename=messages
spring.messages.encoding=UTF-8
```

> Đổi `username` / `password` theo SQL Server trên máy của bạn. Nếu dùng instance có tên (vd `SQLEXPRESS`) mà không cố định port: `jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=StudentDB;...` (cần bật dịch vụ *SQL Server Browser*).

**`src/main/resources/messages.properties`**

```properties
# TODO 17: Viết message tiếng Việt cho lỗi chuyển kiểu (type mismatch)
#  - Khi nhập chữ vào ô GPA (Double)
#  - Khi ngày sinh không hợp lệ (LocalDate)
```

> ⚠ **IntelliJ:** *Settings → Editor → File Encodings → Default encoding for properties files* = **UTF-8**, nếu không tiếng Việt sẽ bị lỗi font `?`.

---

## STEP 5 – Entity `Student` (TODO 18, TODO 1 → 3)

**`entity/Student.java`**

```java
package com.hsf302.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;

// TODO 18: Ánh xạ entity với bảng SQL Server
//  - Lớp     : @Entity, @Table(name = "Students")
//  - id      : @Id + @GeneratedValue(strategy = GenerationType.IDENTITY)
//  - Mỗi field: @Column(name = "...", nullable = false, length = ...) theo bảng ở B0
//      + studentCode: length = 8, unique = true
//      + fullName   : length = 50 và thêm @Nationalized (NVARCHAR – lưu tiếng Việt)
//      + email      : length = 100
//      + major      : length = 2
//      + dateOfBirth, gpa, active: nullable = false
public class Student {

    private Long id;

    // TODO 1: Thêm validation
    //  - studentCode: bắt buộc; regex ^(SE|SS|SA|HE)\d{6}$
    //                 message: "Mã SV phải có dạng SE/SS/SA/HE + 6 chữ số (VD: SE180001)"
    //  - fullName   : bắt buộc; độ dài 2 - 50
    //  - email      : bắt buộc; đúng định dạng email
    private String studentCode;
    private String fullName;
    private String email;

    // TODO 2: Thêm validation
    //  - dateOfBirth: bắt buộc; phải là ngày trong quá khứ;
    //                 thêm @DateTimeFormat(pattern = "yyyy-MM-dd") để bind với <input type="date">
    //  - gpa        : bắt buộc; 0.0 <= gpa <= 10.0
    //  - major      : bắt buộc (chọn ngành)
    private LocalDate dateOfBirth;
    private Double gpa;
    private String major;

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
    //  - getAge(): trả về số tuổi (dùng Period.between); dateOfBirth null → 0
    //  - getRank(): >=9 "Xuất sắc", >=8 "Giỏi", >=6.5 "Khá", >=5 "Trung bình", còn lại "Yếu";
    //               gpa null → "Chưa xếp loại"
    public int getAge() {
        return 0;
    }

    public String getRank() {
        return null;
    }

    // TODO 0: Generate Getters & Setters cho tất cả field (IntelliJ: Alt + Insert)
    //  Lưu ý: field boolean "active" → getter là isActive()
}
```

> ⚠ JPA **bắt buộc** entity có **constructor không tham số** (`public Student() {}`) – Hibernate dùng nó để tạo object khi đọc dữ liệu từ bảng.

---

## STEP 6 – Repository (TODO 19)

**`repository/StudentRepository.java`**

```java
package com.hsf302.student.repository;

import com.hsf302.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // TODO 19: Khai báo các derived query sau (KHÔNG cần viết thân hàm):
    //  19.1. Tìm theo mã SV HOẶC họ tên chứa từ khóa, không phân biệt hoa thường, sắp xếp theo id tăng dần
    //        → findByStudentCodeContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrderByIdAsc(String code, String name)
    //  19.2. Kiểm tra mã SV đã tồn tại (dùng khi THÊM MỚI)
    //        → existsByStudentCodeIgnoreCase(String studentCode)
    //  19.3. Kiểm tra mã SV đã tồn tại ở sinh viên KHÁC (dùng khi CẬP NHẬT – bỏ qua chính nó)
    //        → existsByStudentCodeIgnoreCaseAndIdNot(String studentCode, Long id)
}
```

---

## STEP 7 – Service + dữ liệu mẫu (CHO SẴN – không cần sửa)

**`service/StudentService.java`** – gọi `StudentRepository` thay vì lưu trong `List`:

```java
package com.hsf302.student.service;

import com.hsf302.student.entity.Student;
import com.hsf302.student.repository.StudentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll(Sort.by("id"));
    }

    public List<Student> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String kw = keyword.trim();
        return studentRepository
                .findByStudentCodeContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrderByIdAsc(kw, kw);
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    /** id == null → INSERT, id != null → UPDATE */
    @Transactional
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    /** Kiểm tra trùng mã SV, bỏ qua chính sinh viên đang sửa (excludeId) */
    public boolean isStudentCodeDuplicated(String code, Long excludeId) {
        if (code == null || code.isBlank()) return false;
        String c = code.trim();
        return excludeId == null
                ? studentRepository.existsByStudentCodeIgnoreCase(c)
                : studentRepository.existsByStudentCodeIgnoreCaseAndIdNot(c, excludeId);
    }

    public double averageGpa(List<Student> list) {
        return list.stream()
                .map(Student::getGpa)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }
}
```

**`config/DataInitializer.java`** – thêm 5 sinh viên mẫu vào SQL Server **chỉ khi bảng đang rỗng** (chạy lại app không bị thêm trùng):

```java
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
```

> `DataInitializer` chỉ biên dịch được khi đã làm xong **TODO 18 – 19**. Nếu muốn reset dữ liệu mẫu: `DELETE FROM Students;` trong SSMS rồi chạy lại ứng dụng.

---

## STEP 8 – Controller (TODO 4 → 7)

**`controller/HomeController.java`** (cho sẵn)

```java
package com.hsf302.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/students";
    }
}
```

**`controller/StudentController.java`**

```java
package com.hsf302.student.controller;

import com.hsf302.student.entity.Student;
import com.hsf302.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /** Có sẵn trong Model của MỌI request trong controller này → ${majors} */
    @ModelAttribute("majors")
    public Map<String, String> majors() {
        Map<String, String> majors = new LinkedHashMap<>();
        majors.put("SE", "Kỹ thuật phần mềm");
        majors.put("AI", "Trí tuệ nhân tạo");
        majors.put("IA", "An toàn thông tin");
        majors.put("GD", "Thiết kế đồ họa");
        return majors;
    }

    // TODO 4: GET /students?keyword=...
    //  Đưa vào Model:
    //   - "students"      : danh sách sau khi search(keyword)
    //   - "keyword"       : từ khóa (để giữ lại trên ô tìm kiếm)
    //   - "totalStudents" : số sinh viên trong danh sách
    //   - "avgGpa"        : GPA trung bình (studentService.averageGpa)
    //   - "activeMenu"    : "list"  (để tô sáng menu trên navbar)
    //  Trả về view "students/list"
    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        return "students/list";
    }

    // TODO 5: GET /students/{id}
    //  - Tìm thấy: đưa "student" vào Model, "activeMenu" = "list", trả về "students/detail"
    //  - Không thấy: flash attribute "errorMessage" = "Không tìm thấy sinh viên có id = ..."
    //                rồi redirect:/students
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return "students/detail";
    }

    // TODO 6: Hiển thị form
    //  - GET /students/new      : Model "student" = new Student(), "activeMenu" = "new"
    //  - GET /students/{id}/edit: Model "student" = sinh viên tìm được (không thấy → như TODO 5)
    //  Cả 2 trả về view "students/form"
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        return "students/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return "students/form";
    }

    // TODO 7: POST /students/save
    //  - Thêm @Valid và BindingResult (đặt NGAY SAU tham số student)
    //  - Nếu mã SV bị trùng → result.rejectValue("studentCode", "duplicate", "Mã sinh viên đã tồn tại")
    //  - Có lỗi → trả lại "students/form" (KHÔNG redirect)
    //  - Hợp lệ → lưu, flash "successMessage" = "Thêm/Cập nhật sinh viên <tên> thành công!"
    //             rồi redirect:/students
    @PostMapping("/save")
    public String save(@ModelAttribute("student") Student student,
                       Model model, RedirectAttributes ra) {
        return "redirect:/students";
    }

    // Cho sẵn
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        studentService.deleteById(id);
        ra.addFlashAttribute("successMessage", "Đã xóa sinh viên có id = " + id);
        return "redirect:/students";
    }
}
```

---

## STEP 9 – Layout chung (TODO 8)

**`templates/layout/main.html`**

```html
<!DOCTYPE html>
<html lang="vi"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- TODO 8.1: Dùng layout:title-pattern để tiêu đề tab có dạng:
                   "<Tiêu đề trang con> | Student Manager" -->
    <title>Student Manager</title>

    <!-- TODO 8.2: Link Bootstrap OFFLINE + style.css bằng th:href="@{...}" -->
</head>
<body class="d-flex flex-column min-vh-100">

<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="#">Student Manager</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav ms-auto">
                <!-- TODO 8.3: th:href tới /students và /students/new
                               th:classappend="... ? 'active'" dựa vào ${activeMenu} ('list' / 'new') -->
                <li class="nav-item"><a class="nav-link" href="#">Danh sách</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Thêm mới</a></li>
            </ul>
        </div>
    </div>
</nav>

<main class="container my-4 flex-grow-1">
    <!-- TODO 8.4: Hiển thị flash message ${successMessage} (alert-success)
                   và ${errorMessage} (alert-danger), chỉ khi có giá trị (th:if),
                   có nút đóng (btn-close, data-bs-dismiss="alert") -->

    <!-- TODO 8.5: Vùng nội dung để trang con thay thế: layout:fragment="content" -->
</main>

<footer class="bg-dark text-white-50 text-center py-3">
    <!-- TODO 8.6: Hiển thị năm hiện tại bằng #temporals -->
    &copy; <span>2026</span> HSF302 – Student Manager
</footer>

<!-- TODO 8.7: Script Bootstrap OFFLINE (bootstrap.bundle.min.js) -->
</body>
</html>
```

---

## STEP 10 – Trang danh sách (TODO 9 → 13)

**`templates/students/list.html`**

```html
<!DOCTYPE html>
<html lang="vi"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<!-- TODO 9.1: Thêm layout:decorate="~{layout/main}" vào thẻ <html> -->
<head>
    <title>Danh sách sinh viên</title>
</head>
<body>
<section layout:fragment="content">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="mb-0">Danh sách sinh viên</h2>
        <a href="#" class="btn btn-primary">+ Thêm sinh viên</a>  <!-- TODO 9.2: th:href -->
    </div>

    <!-- TODO 9.3: Form tìm kiếm GET tới /students; ô input name="keyword" giữ lại giá trị ${keyword} -->
    <form class="row g-2 mb-3">
        <div class="col-md-6">
            <input type="text" name="keyword" class="form-control"
                   placeholder="Tìm theo mã SV hoặc họ tên...">
        </div>
        <div class="col-auto">
            <button type="submit" class="btn btn-outline-primary">Tìm kiếm</button>
        </div>
    </form>
    <!-- TODO 9.4: Nếu keyword không rỗng (#strings.isEmpty) → hiển thị:
                   "Tìm thấy X sinh viên cho từ khóa: <keyword>" (dùng literal substitution |...|) -->

    <!-- TODO 10: 3 thẻ thống kê
         - Tổng số sinh viên : ${totalStudents}
         - GPA trung bình    : #numbers.formatDecimal(avgGpa, 1, 2)
         - Đang học          : #lists.size(students.?[active])  (collection selection) -->
    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="card stat-card text-bg-primary"><div class="card-body">
                <div>Tổng số sinh viên</div><div class="value">0</div>
            </div></div>
        </div>
        <div class="col-md-4">
            <div class="card stat-card text-bg-success"><div class="card-body">
                <div>GPA trung bình</div><div class="value">0.00</div>
            </div></div>
        </div>
        <div class="col-md-4">
            <div class="card stat-card text-bg-info"><div class="card-body">
                <div>Đang học</div><div class="value">0</div>
            </div></div>
        </div>
    </div>

    <!-- TODO 13.1: Nếu danh sách rỗng → alert-warning "Không có sinh viên nào."
                    và ẩn bảng (th:if / th:unless + #lists.isEmpty) -->
    <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle bg-white">
            <thead class="table-dark">
            <tr>
                <th>#</th><th>Mã SV</th><th>Họ tên</th><th>Email</th><th>Ngày sinh</th>
                <th>Tuổi</th><th>Ngành</th><th>GPA</th><th>Xếp loại</th><th>Trạng thái</th><th>Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <!-- TODO 11: th:each="s, stat : ${students}"
                 - #            : stat.count
                 - Mã SV        : s.studentCode
                 - Họ tên       : IN HOA bằng #strings.toUpperCase
                 - Email        : s.email
                 - Ngày sinh    : #temporals.format(..., 'dd/MM/yyyy')
                 - Tuổi         : s.age  (thuộc tính tính toán)
                 - GPA          : #numbers.formatDecimal(..., 1, 2)
                 TODO 12:
                 - Ngành        : tra Map → ${majors[s.major]}
                 - Xếp loại     : th:switch="${s.rank}" → badge màu
                                  Xuất sắc=success, Giỏi=primary, Khá=info, Trung bình=warning, * = danger
                 - Trạng thái   : ternary → 'Đang học' (badge success) / 'Bảo lưu' (badge secondary)
                 - Dòng có GPA < 5 (dùng 'lt') → th:classappend 'table-danger'
                 TODO 13.2:
                 - Nút Xem / Sửa: th:href="@{/students/{id}(id=${s.id})}" và ".../{id}/edit"
                 - Nút Xóa      : form POST th:action="@{/students/{id}/delete(id=${s.id})}" -->
            <tr>
                <td>1</td><td>SE180001</td><td>NGUYỄN VĂN A</td><td>a@fpt.edu.vn</td>
                <td>01/01/2004</td><td>22</td><td>Kỹ thuật phần mềm</td><td>8.00</td>
                <td><span class="badge text-bg-primary">Giỏi</span></td>
                <td><span class="badge text-bg-success">Đang học</span></td>
                <td>
                    <a href="#" class="btn btn-sm btn-outline-info">Xem</a>
                    <a href="#" class="btn btn-sm btn-outline-warning">Sửa</a>
                    <button class="btn btn-sm btn-outline-danger">Xóa</button>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</section>
</body>
</html>
```

> 💡 Dữ liệu mẫu trong `<td>` (vd `SE180001`) là **natural template** – mở file trực tiếp trên trình duyệt vẫn thấy; khi chạy qua Thymeleaf, `th:text` sẽ thay thế.

---

## STEP 11 – Trang chi tiết (TODO 14)

**`templates/students/detail.html`**

```html
<!DOCTYPE html>
<html lang="vi"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/main}">
<head>
    <title>Chi tiết sinh viên</title>
</head>
<body>
<section layout:fragment="content">
    <div class="card shadow-sm">
        <div class="card-header d-flex justify-content-between align-items-center">
            <!-- TODO 14.1: Tiêu đề "<studentCode> - <fullName>" bằng literal substitution -->
            <h4 class="mb-0">SE180001 - Nguyễn Văn A</h4>
            <!-- TODO 14.2: Badge trạng thái: th:classappend + ternary -->
            <span class="badge text-bg-success">Đang học</span>
        </div>
        <div class="card-body">
            <dl class="row mb-0">
                <!-- TODO 14.3: Hiển thị bằng ${student.xxx}:
                     - Mã SV, Họ tên
                     - Email: link mailto (th:href="|mailto:${...}|"); null → 'Chưa cập nhật' (Elvis ?:)
                     - Ngày sinh: "dd/MM/yyyy (X tuổi)"
                     - Ngành: "SE - Kỹ thuật phần mềm" (mã + tra Map majors)
                     - GPA: 2 chữ số thập phân + " / 10"
                     - Xếp loại: ${student.rank}
                     - Học bổng: GPA >= 8.0 (dùng 'ge') VÀ đang học → "Đủ điều kiện xét học bổng",
                                 ngược lại "Không đủ điều kiện" -->
                <dt class="col-sm-3">Mã sinh viên</dt>
                <dd class="col-sm-9">SE180001</dd>
            </dl>

            <!-- TODO 14.4 (bonus): Viết lại "Họ tên" và "Email" bằng th:object + *{...}
                                    để so sánh với ${...} -->
        </div>
        <div class="card-footer">
            <!-- TODO 14.5: th:href cho 2 nút -->
            <a href="#" class="btn btn-secondary">← Quay lại</a>
            <a href="#" class="btn btn-warning">Sửa</a>
        </div>
    </div>
</section>
</body>
</html>
```

---

## STEP 12 – Form thêm/sửa có validation (TODO 15 → 16)

**`templates/students/form.html`**

```html
<!DOCTYPE html>
<html lang="vi"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/main}">
<head>
    <!-- TODO 15.1: Tiêu đề động: id null → 'Thêm sinh viên', ngược lại 'Cập nhật sinh viên' -->
    <title>Form sinh viên</title>
</head>
<body>
<section layout:fragment="content">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow-sm">
                <div class="card-header">
                    <h4 class="mb-0">Thêm sinh viên mới</h4> <!-- TODO 15.2: tiêu đề động -->
                </div>
                <div class="card-body">

                    <!-- TODO 15.3: th:action="@{/students/save}" th:object="${student}" method="post"
                                    (thêm novalidate để tắt validate HTML5 → thấy rõ validate server) -->
                    <form>
                        <!-- TODO 15.4: hidden id: th:field="*{id}" -->

                        <!-- TODO 16.1: Khối tổng hợp lỗi: th:if="${#fields.hasErrors('*')}"
                                        liệt kê bằng th:each="err : ${#fields.errors('*')}" -->

                        <div class="row g-3">
                            <!-- TODO 15.5 + 16.2: Với MỖI ô nhập:
                                 - th:field="*{tênField}"
                                 - th:errorclass="is-invalid"
                                 - <div class="invalid-feedback" th:if="${#fields.hasErrors('tênField')}"
                                        th:errors="*{tênField}"></div> -->
                            <div class="col-md-6">
                                <label for="studentCode" class="form-label">Mã sinh viên <span class="text-danger">*</span></label>
                                <input type="text" id="studentCode" class="form-control" placeholder="VD: SE180001">
                            </div>
                            <div class="col-md-6">
                                <label for="fullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
                                <input type="text" id="fullName" class="form-control">
                            </div>
                            <div class="col-md-6">
                                <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                                <input type="email" id="email" class="form-control">
                            </div>
                            <div class="col-md-6">
                                <label for="dateOfBirth" class="form-label">Ngày sinh <span class="text-danger">*</span></label>
                                <input type="date" id="dateOfBirth" class="form-control">
                            </div>
                            <div class="col-md-6">
                                <label for="gpa" class="form-label">GPA (0 - 10) <span class="text-danger">*</span></label>
                                <!-- dùng type="text" để có thể test lỗi type mismatch (nhập chữ) -->
                                <input type="text" id="gpa" class="form-control">
                            </div>
                            <div class="col-md-6">
                                <label for="major" class="form-label">Ngành <span class="text-danger">*</span></label>
                                <!-- TODO 15.6: option lặp từ Map ${majors}: value = key, text = "key - value" -->
                                <select id="major" class="form-select">
                                    <option value="">-- Chọn ngành --</option>
                                </select>
                            </div>
                            <div class="col-12">
                                <!-- TODO 15.7: checkbox th:field="*{active}";
                                                label th:for="${#ids.prev('active')}" -->
                                <div class="form-check form-switch">
                                    <input class="form-check-input" type="checkbox">
                                    <label class="form-check-label">Đang học</label>
                                </div>
                            </div>
                        </div>

                        <div class="mt-4 d-flex gap-2">
                            <!-- TODO 15.8: text nút động: 'Thêm mới' / 'Lưu thay đổi' -->
                            <button type="submit" class="btn btn-primary">Lưu</button>
                            <a href="#" class="btn btn-secondary">Hủy</a>  <!-- th:href="@{/students}" -->
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</section>
</body>
</html>
```

---

## STEP 13 – Chạy ứng dụng

1. Bật SQL Server, chắc chắn đã có database `StudentDB` (STEP 3).
2. Chạy `StudentThymeleafApplication` (hoặc `mvn spring-boot:run`).
3. Quan sát console: Hibernate in câu `create table Students (...)` (lần chạy đầu) và các câu `insert into Students ...` của `DataInitializer`.
4. Mở SSMS kiểm tra: `SELECT * FROM StudentDB.dbo.Students;` → có 5 dòng, cột `full_name` hiển thị đúng tiếng Việt.
5. Mở **http://localhost:8080** → tự chuyển tới `/students`.
6. Kiểm tra Bootstrap offline: mở **http://localhost:8080/css/bootstrap.min.css** – phải thấy nội dung CSS.
7. **Ngắt mạng (tắt Wi-Fi)** → `Ctrl + F5` → giao diện vẫn có style Bootstrap ⇒ đạt yêu cầu offline (SQL Server chạy local nên không bị ảnh hưởng).
8. Thực hiện các test case ở **Phần C**.

---

# PHẦN C – KIỂM THỬ

## C1. Test validation (form thêm mới `/students/new`)

| # | Dữ liệu nhập | Kết quả mong đợi |
|---|---|---|
| 1 | Để trống tất cả, bấm Lưu | Hiện lỗi ở: Mã SV, Họ tên, Email, Ngày sinh, GPA, Ngành; khối tổng hợp lỗi xuất hiện |
| 2 | Mã SV = `ABC123` | "Mã SV phải có dạng SE/SS/SA/HE + 6 chữ số..." |
| 3 | Mã SV = `SE180001` (đã có) | "Mã sinh viên đã tồn tại" |
| 4 | Họ tên = `A` | Lỗi độ dài 2 – 50 |
| 5 | Email = `abc` | Lỗi định dạng email |
| 6 | Ngày sinh = ngày mai | Lỗi "phải là ngày trong quá khứ" |
| 7 | GPA = `11` | Lỗi GPA tối đa 10 |
| 8 | GPA = `abc` | "GPA phải là số thực từ 0 đến 10" (từ `messages.properties`) |
| 9 | Nhập sai rồi bấm Lưu | Các giá trị đã nhập **được giữ lại** trên form |
| 10 | Dữ liệu hợp lệ | Redirect về danh sách, hiện alert xanh "Thêm sinh viên ... thành công!" |
| 11 | F5 sau khi thêm thành công | **Không** bị thêm trùng (nhờ redirect – PRG), alert biến mất |

## C2. Test hiển thị

| # | Thao tác | Kết quả mong đợi |
|---|---|---|
| 1 | Vào `/students` | 5 sinh viên mẫu; tên IN HOA; ngày `dd/MM/yyyy`; GPA 2 số lẻ |
| 2 | Xem dòng `SA180004` (GPA 4.5) | Dòng màu đỏ (`table-danger`), badge "Yếu" đỏ, trạng thái "Bảo lưu" |
| 3 | Thẻ thống kê | Tổng 5, GPA TB `7.52`, Đang học 4 |
| 4 | Tìm `nguyễn` | Còn 1 dòng, ô tìm kiếm giữ chữ `nguyễn`, hiện "Tìm thấy 1 sinh viên..." |
| 5 | Tìm `xyz` | Hiện "Không có sinh viên nào.", bảng bị ẩn |
| 6 | Bấm "Xem" | Trang chi tiết, URL `/students/{id}` |
| 7 | Gõ tay `/students/999` | Quay về danh sách với alert đỏ "Không tìm thấy sinh viên có id = 999" |
| 8 | Bấm "Sửa" | Form có sẵn dữ liệu (kể cả ngày sinh, ngành, checkbox), tiêu đề "Cập nhật sinh viên" |
| 9 | Sửa giữ nguyên mã SV, bấm Lưu | Lưu thành công (không báo trùng với chính nó) |
| 10 | Tiêu đề tab trình duyệt | "Danh sách sinh viên \| Student Manager" |
| 11 | Menu navbar | Mục đang đứng được tô sáng (`active`) |
| 12 | Footer | Hiện năm hiện tại |

> Các số liệu ở C2 tính trên **5 sinh viên mẫu**. Nếu đã thêm/xóa dữ liệu khi test C1, chạy `DELETE FROM Students;` rồi khởi động lại app để `DataInitializer` nạp lại dữ liệu mẫu.

## C3. Test lưu trữ SQL Server

| # | Thao tác | Kết quả mong đợi |
|---|---|---|
| 1 | Khởi động app lần đầu | Console có `create table Students`; SSMS thấy bảng `dbo.Students` với đúng tên cột, kiểu (`NVARCHAR(50)` cho `full_name`, `BIT` cho `active`...) |
| 2 | `SELECT * FROM Students` | 5 dòng mẫu, tiếng Việt hiển thị đúng (không bị `?`) |
| 3 | Thêm 1 sinh viên có tên tiếng Việt trên web | SSMS thấy dòng mới, `id` tự tăng, tên có dấu đúng |
| 4 | Sửa GPA 1 sinh viên trên web | Console có câu `update Students set ...`; SSMS thấy giá trị mới |
| 5 | Xóa 1 sinh viên trên web | Console có câu `delete from Students ...`; SSMS không còn dòng đó |
| 6 | **Tắt rồi chạy lại app** | Dữ liệu vẫn còn (không mất như bản in-memory), **không** bị thêm trùng 5 dòng mẫu |
| 7 | Tìm `se18` | Câu SQL sinh ra có `like`; kết quả gồm các SV có mã chứa `SE18` (không phân biệt hoa thường) |
| 8 | Chạy trong SSMS: `INSERT INTO Students(student_code, full_name, email, date_of_birth, gpa, major, active) VALUES ('SE180001', N'Test', 'a@b.c', '2004-01-01', 5, 'SE', 1)` | Lỗi *Violation of UNIQUE KEY constraint* – ràng buộc `unique = true` hoạt động |

---

# PHẦN D – CHECKLIST HOÀN THÀNH

### 1. Cấu hình project
- [ ] Có đủ dependency: `webmvc`, `thymeleaf`, `validation`, `data-jpa`, `mssql-jdbc`, `thymeleaf-layout-dialect`
- [ ] `application.properties` có `spring.datasource.*` (url, username, password) và `spring.jpa.hibernate.ddl-auto=update`
- [ ] `spring.thymeleaf.cache=false`
- [ ] `messages.properties` lưu UTF-8, có message `typeMismatch` (TODO 17)

### 2. Bootstrap offline
- [ ] `bootstrap.min.css` nằm trong `static/css/`
- [ ] `bootstrap.bundle.min.js` nằm trong `static/js/`
- [ ] Không còn link CDN nào trong project
- [ ] Ngắt mạng vẫn hiển thị đúng giao diện
- [ ] Nút đóng alert / navbar toggler hoạt động (JS đã load)

### 3. Model & Validation
- [ ] TODO 1: `@NotBlank`, `@Pattern`, `@Size`, `@Email` cho `studentCode`, `fullName`, `email`
- [ ] TODO 2: `@NotNull`, `@Past`, `@DateTimeFormat`, `@DecimalMin`, `@DecimalMax`, `@NotBlank` cho `dateOfBirth`, `gpa`, `major`
- [ ] TODO 3: `getAge()` và `getRank()` đúng
- [ ] Có đủ getter/setter (`isActive()` cho boolean)

### 3b. Entity & Repository (SQL Server)
- [ ] Đã tạo database `StudentDB`; ứng dụng kết nối SQL Server thành công
- [ ] TODO 18: `@Entity`, `@Table(name = "Students")`, `@Id` + `@GeneratedValue(IDENTITY)`
- [ ] TODO 18: `@Column` đúng tên cột, `nullable = false`, `length`; `studentCode` có `unique = true`
- [ ] TODO 18: `fullName` có `@Nationalized` → cột `NVARCHAR`, lưu tiếng Việt đúng
- [ ] Entity có constructor không tham số
- [ ] TODO 19: `StudentRepository extends JpaRepository<Student, Long>` với 3 derived query
- [ ] Dữ liệu còn nguyên sau khi khởi động lại ứng dụng

### 4. Controller
- [ ] TODO 4: `list()` đưa đủ 5 attribute vào Model
- [ ] TODO 5: `detail()` xử lý trường hợp không tìm thấy (flash + redirect)
- [ ] TODO 6: form thêm mới và form sửa dùng chung view `students/form`
- [ ] TODO 7: `@Valid` + `BindingResult` đặt đúng vị trí; kiểm tra trùng mã SV bằng `rejectValue`
- [ ] Lỗi → trả về form; thành công → `redirect:` + flash message

### 5. Layout
- [ ] TODO 8: `layout/main.html` có navbar, vùng `layout:fragment="content"`, footer
- [ ] `layout:title-pattern` ghép tiêu đề đúng
- [ ] Menu `active` theo `${activeMenu}`
- [ ] Flash `successMessage` / `errorMessage` hiển thị ở layout
- [ ] Năm ở footer dùng `#temporals`
- [ ] Tất cả trang con dùng `layout:decorate="~{layout/main}"`, không lặp lại navbar/footer

### 6. Variable expressions – trang danh sách
- [ ] TODO 9: ô tìm kiếm giữ lại `${keyword}`; thông báo kết quả bằng `|...|`
- [ ] TODO 10: thẻ thống kê dùng `${totalStudents}`, `#numbers.formatDecimal`, `#lists.size(...?[active])`
- [ ] TODO 11: `th:each` có `stat.count`; dùng `#strings`, `#temporals`, `#numbers`; hiển thị `${s.age}`
- [ ] TODO 12: tra Map `${majors[s.major]}`; `th:switch` cho xếp loại; ternary cho trạng thái; `lt` + `th:classappend`
- [ ] TODO 13: `@{/students/{id}(id=${s.id})}`; thông báo danh sách rỗng bằng `#lists.isEmpty`

### 7. Variable expressions – trang chi tiết
- [ ] TODO 14: literal substitution, Elvis `?:`, `mailto`, `ge` + `and` cho điều kiện học bổng
- [ ] (Bonus) so sánh `${...}` với `th:object` + `*{...}`

### 8. Form
- [ ] TODO 15: `th:object`, `th:field` cho mọi input (kể cả hidden `id`, select, checkbox)
- [ ] Tiêu đề và nút submit thay đổi theo chế độ thêm/sửa
- [ ] TODO 16: `th:errorclass="is-invalid"` + `th:errors` dưới từng ô; khối tổng hợp lỗi
- [ ] Giữ lại dữ liệu đã nhập khi có lỗi
- [ ] Form sửa hiển thị đúng ngày sinh (`<input type="date">`) và ngành đã chọn

### 9. Kiểm thử
- [ ] Pass 11 test case validation (C1)
- [ ] Pass 12 test case hiển thị (C2)
- [ ] Pass 8 test case lưu trữ SQL Server (C3)
- [ ] Console không có exception khi thao tác

---

# PHẦN E – THANG ĐIỂM

| Hạng mục | TODO | Điểm |
|---|---|---|
| Entity ánh xạ SQL Server + Repository + cấu hình datasource | 18 – 19 | 1.5 |
| Validation + thuộc tính tính toán | 1 – 3, 17 | 1.5 |
| Controller (Model, flash, validate, trùng mã) | 4 – 7 | 1.5 |
| Layout + Bootstrap offline | 8 | 1.0 |
| Trang danh sách (variable expressions, utility objects) | 9 – 13 | 2.0 |
| Trang chi tiết | 14 | 1.0 |
| Form + hiển thị lỗi | 15 – 16 | 1.5 |
| **Tổng** | | **10.0** |

---

# PHẦN F – LỖI THƯỜNG GẶP

| Lỗi / Hiện tượng | Nguyên nhân | Cách sửa |
|---|---|---|
| `TemplateInputException: Error resolving template [students/list]` | Sai tên view hoặc sai vị trí file | File phải ở `templates/students/list.html`, return `"students/list"` (không có `.html`, không có `/` đầu) |
| Trang không có navbar/footer | Thiếu dependency Layout Dialect, sai namespace `xmlns:layout`, hoặc sai `~{layout/main}` | Kiểm tra `pom.xml`, namespace `http://www.ultraq.net.nz/thymeleaf/layout` |
| Không có style Bootstrap, CSS 404 | Đặt sai thư mục hoặc ghi `/static/css/...` | File trong `static/css/`, link `@{/css/bootstrap.min.css}` |
| `Neither BindingResult nor plain target object for bean name 'student'` | Quên `model.addAttribute("student", ...)` khi mở form | Thêm `new Student()` vào Model ở `showCreateForm` |
| Gặp trang lỗi 400 thay vì hiện lỗi validate | `BindingResult` không đứng ngay sau tham số `@Valid` | Đặt `BindingResult result` ngay sau `Student student` |
| Validate không chạy | Thiếu `spring-boot-starter-validation` hoặc thiếu `@Valid` | Thêm dependency / annotation |
| `EL1007E: Property or field 'xxx' cannot be found on null` | Object trong Model là `null` | Kiểm tra controller, hoặc dùng `?.` |
| `EL1008E: Property or field 'rank' cannot be found` | Thiếu getter `getRank()` / sai tên | Tên getter phải là `get` + tên viết hoa chữ đầu |
| Ô ngày sinh trống khi sửa | Thiếu `@DateTimeFormat(pattern = "yyyy-MM-dd")` | Thêm annotation vào `dateOfBirth` |
| Message tiếng Việt thành `?????` | File `.properties` không lưu UTF-8 | Đổi encoding trong IDE sang UTF-8 |
| F5 sau khi thêm bị thêm trùng | Return view thay vì redirect khi thành công | Dùng `return "redirect:/students";` |
| Flash message không hiện | Dùng `model.addAttribute` trước khi redirect | Dùng `RedirectAttributes.addFlashAttribute` |
| `GPA trung bình` hiện `7.519999...` | Không format số | `#numbers.formatDecimal(avgGpa, 1, 2)` |
| `The TCP/IP connection to the host localhost, port 1433 has failed` | SQL Server chưa bật TCP/IP hoặc không nghe port 1433 | SQL Server Configuration Manager → bật TCP/IP, IPAll port `1433` → restart service |
| `PKIX path building failed` / `SSL ... encryption` | Driver mặc định `encrypt=true`, SQL Server local dùng chứng chỉ tự ký | Thêm `;encrypt=true;trustServerCertificate=true` vào URL |
| `Login failed for user 'sa'` | Sai mật khẩu, `sa` bị disable, hoặc server chỉ cho Windows Authentication | Bật *SQL Server and Windows Authentication mode*, enable `sa`, đặt lại mật khẩu, restart service |
| `Cannot open database "StudentDB" requested by the login` | Chưa tạo database | `CREATE DATABASE StudentDB;` trong SSMS |
| `Not a managed type: class ...Student` / `No identifier specified for entity` | Thiếu `@Entity` hoặc thiếu `@Id` | Thêm đủ annotation (TODO 18) |
| `No default constructor for entity` | Entity không có constructor rỗng | Thêm `public Student() {}` |
| `No property 'xxx' found for type 'Student'` khi khởi động | Sai tên field trong tên phương thức derived query | Tên trong method phải khớp **tên field Java** (`studentCode`), không phải tên cột (`student_code`) |
| Tên tiếng Việt trong SQL Server thành `?` | Cột kiểu `VARCHAR` | Thêm `@Nationalized` cho field `fullName` → `NVARCHAR`; nếu bảng đã tạo sai: `DROP TABLE Students` rồi chạy lại |
| Sửa `@Column` (length, nullable...) nhưng bảng không đổi | `ddl-auto=update` **không** sửa cột đã tồn tại, chỉ thêm cột mới | Drop bảng để Hibernate tạo lại (dữ liệu mẫu sẽ được nạp lại) |
| `Violation of UNIQUE KEY constraint` (trang lỗi 500) | Bỏ qua kiểm tra trùng mã trong controller, DB chặn ở tầng cuối | Gọi `isStudentCodeDuplicated` + `rejectValue` trước khi `save` (TODO 7) |

---

# PHẦN G – BÀI TẬP MỞ RỘNG

1. **Sắp xếp:** thêm tham số `?sort=gpa&dir=desc`; click tiêu đề cột để đổi chiều sắp xếp (dùng `${param.sort}`, `${param.dir}` để tô mũi tên ▲▼).
2. **Lọc theo ngành:** thêm `<select name="major">` vào form tìm kiếm, giữ lại lựa chọn bằng `th:selected="${m.key == param.major}"`.
3. **Phân trang** bằng Spring Data: `Page<Student> findAll(Pageable pageable)` với `PageRequest.of(page, 3, Sort.by("id"))`; dùng `${page.totalPages}`, `${page.number}` và `#numbers.sequence(1, page.totalPages)` để sinh nút trang.
4. **Fragment tái sử dụng:** tách badge xếp loại thành fragment `fragments/badges :: rank(rank)` và gọi bằng `th:replace`.
5. **Đa ngôn ngữ (i18n):** chuyển các label sang `messages.properties` / `messages_en.properties`, dùng `#{...}`.
6. **Thống kê theo ngành:** bảng đếm số SV theo từng ngành (`Map<String, Long>` từ controller), duyệt Map bằng `th:each="e : ${stats}"` → `${e.key}`, `${e.value}`.
7. **Validation nâng cao:** tự viết annotation `@UniqueStudentCode` (custom `ConstraintValidator` inject `StudentRepository`) thay cho `rejectValue` trong controller.
8. **Thống kê bằng JPQL:** viết `@Query("SELECT s.major, COUNT(s), AVG(s.gpa) FROM Student s GROUP BY s.major")` trong `StudentRepository` và hiển thị thành bảng.
9. **Quan hệ entity:** tách ngành thành entity `Major` (bảng `Majors`), `Student` dùng `@ManyToOne @JoinColumn(name = "major_id")`; form chọn ngành lấy từ database thay cho `Map` cố định.

---

# PHẦN H – ĐÁP ÁN THAM KHẢO

> ⚠ Phần dành cho giảng viên / tự kiểm tra sau khi làm xong. **Hãy tự làm trước khi xem.**

<details>
<summary><b>TODO 18 + TODO 1 – 3: Student.java (Entity)</b></summary>

```java
package com.hsf302.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;

// TODO 18
@Entity
@Table(name = "Students")
public class Student {

    // TODO 18
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // TODO 1
    @NotBlank(message = "Mã sinh viên không được để trống")
    @Pattern(regexp = "^(SE|SS|SA|HE)\\d{6}$",
             message = "Mã SV phải có dạng SE/SS/SA/HE + 6 chữ số (VD: SE180001)")
    @Column(name = "student_code", nullable = false, unique = true, length = 8)
    private String studentCode;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 50, message = "Họ tên phải từ 2 đến 50 ký tự")
    @Nationalized                                           // NVARCHAR – lưu tiếng Việt
    @Column(name = "full_name", nullable = false, length = 50)
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    // TODO 2
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

    // TODO 3
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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Double getGpa() { return gpa; }
    public void setGpa(Double gpa) { this.gpa = gpa; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
```
</details>

<details>
<summary><b>TODO 17: messages.properties</b></summary>

```properties
typeMismatch.java.lang.Double=Giá trị phải là số (VD: 8.5)
typeMismatch.java.time.LocalDate=Ngày không hợp lệ
typeMismatch.student.gpa=GPA phải là số thực từ 0 đến 10
typeMismatch.student.dateOfBirth=Ngày sinh không hợp lệ
```
</details>

<details>
<summary><b>TODO 4 – 7: StudentController.java</b></summary>

```java
package com.hsf302.student.controller;

import com.hsf302.student.entity.Student;
import com.hsf302.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @ModelAttribute("majors")
    public Map<String, String> majors() {
        Map<String, String> majors = new LinkedHashMap<>();
        majors.put("SE", "Kỹ thuật phần mềm");
        majors.put("AI", "Trí tuệ nhân tạo");
        majors.put("IA", "An toàn thông tin");
        majors.put("GD", "Thiết kế đồ họa");
        return majors;
    }

    // TODO 4
    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        List<Student> students = studentService.search(keyword);
        model.addAttribute("students", students);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalStudents", students.size());
        model.addAttribute("avgGpa", studentService.averageGpa(students));
        model.addAttribute("activeMenu", "list");
        return "students/list";
    }

    // TODO 5
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Student> opt = studentService.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Không tìm thấy sinh viên có id = " + id);
            return "redirect:/students";
        }
        model.addAttribute("student", opt.get());
        model.addAttribute("activeMenu", "list");
        return "students/detail";
    }

    // TODO 6
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("activeMenu", "new");
        return "students/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Student> opt = studentService.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Không tìm thấy sinh viên có id = " + id);
            return "redirect:/students";
        }
        model.addAttribute("student", opt.get());
        model.addAttribute("activeMenu", "list");
        return "students/form";
    }

    // TODO 7
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("student") Student student,
                       BindingResult result,
                       Model model,
                       RedirectAttributes ra) {

        if (studentService.isStudentCodeDuplicated(student.getStudentCode(), student.getId())) {
            result.rejectValue("studentCode", "duplicate", "Mã sinh viên đã tồn tại");
        }

        if (result.hasErrors()) {
            model.addAttribute("activeMenu", student.getId() == null ? "new" : "list");
            return "students/form";
        }

        boolean isNew = student.getId() == null;
        studentService.save(student);
        ra.addFlashAttribute("successMessage",
                (isNew ? "Thêm" : "Cập nhật") + " sinh viên " + student.getFullName() + " thành công!");
        return "redirect:/students";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        studentService.deleteById(id);
        ra.addFlashAttribute("successMessage", "Đã xóa sinh viên có id = " + id);
        return "redirect:/students";
    }
}
```
</details>

<details>
<summary><b>TODO 8: layout/main.html</b></summary>

```html
<!DOCTYPE html>
<html lang="vi"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- 8.1 -->
    <title layout:title-pattern="$CONTENT_TITLE | $LAYOUT_TITLE">Student Manager</title>

    <!-- 8.2 -->
    <link rel="stylesheet" th:href="@{/css/bootstrap.min.css}">
    <link rel="stylesheet" th:href="@{/css/style.css}">
</head>
<body class="d-flex flex-column min-vh-100">

<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" th:href="@{/students}">Student Manager</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav ms-auto">
                <!-- 8.3 -->
                <li class="nav-item">
                    <a class="nav-link" th:href="@{/students}"
                       th:classappend="${activeMenu == 'list'} ? 'active'">Danh sách</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" th:href="@{/students/new}"
                       th:classappend="${activeMenu == 'new'} ? 'active'">Thêm mới</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<main class="container my-4 flex-grow-1">
    <!-- 8.4 -->
    <div th:if="${successMessage}" class="alert alert-success alert-dismissible fade show" role="alert">
        <span th:text="${successMessage}">Thành công</span>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <div th:if="${errorMessage}" class="alert alert-danger alert-dismissible fade show" role="alert">
        <span th:text="${errorMessage}">Lỗi</span>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>

    <!-- 8.5 -->
    <section layout:fragment="content">
        <p>Nội dung trang con sẽ thay thế ở đây.</p>
    </section>
</main>

<footer class="bg-dark text-white-50 text-center py-3">
    <!-- 8.6 -->
    &copy; <span th:text="${#temporals.year(#temporals.createNow())}">2026</span> HSF302 – Student Manager
</footer>

<!-- 8.7 -->
<script th:src="@{/js/bootstrap.bundle.min.js}"></script>
</body>
</html>
```
</details>

<details>
<summary><b>TODO 9 – 13: students/list.html</b></summary>

```html
<!DOCTYPE html>
<html lang="vi"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/main}">
<head>
    <title>Danh sách sinh viên</title>
</head>
<body>
<section layout:fragment="content">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="mb-0">Danh sách sinh viên</h2>
        <a th:href="@{/students/new}" class="btn btn-primary">+ Thêm sinh viên</a>
    </div>

    <!-- TODO 9.3 -->
    <form th:action="@{/students}" method="get" class="row g-2 mb-3">
        <div class="col-md-6">
            <input type="text" name="keyword" class="form-control"
                   placeholder="Tìm theo mã SV hoặc họ tên..."
                   th:value="${keyword}">
        </div>
        <div class="col-auto">
            <button type="submit" class="btn btn-outline-primary">Tìm kiếm</button>
        </div>
        <div class="col-auto" th:unless="${#strings.isEmpty(keyword)}">
            <a th:href="@{/students}" class="btn btn-outline-secondary">Xóa lọc</a>
        </div>
    </form>

    <!-- TODO 9.4 -->
    <p class="text-muted" th:unless="${#strings.isEmpty(keyword)}"
       th:text="|Tìm thấy ${totalStudents} sinh viên cho từ khóa: ${keyword}|">
        Tìm thấy 1 sinh viên cho từ khóa: abc
    </p>

    <!-- TODO 10 -->
    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="card stat-card text-bg-primary"><div class="card-body">
                <div>Tổng số sinh viên</div>
                <div class="value" th:text="${totalStudents}">0</div>
            </div></div>
        </div>
        <div class="col-md-4">
            <div class="card stat-card text-bg-success"><div class="card-body">
                <div>GPA trung bình</div>
                <div class="value" th:text="${#numbers.formatDecimal(avgGpa, 1, 2)}">0.00</div>
            </div></div>
        </div>
        <div class="col-md-4">
            <div class="card stat-card text-bg-info"><div class="card-body">
                <div>Đang học</div>
                <div class="value" th:text="${#lists.size(students.?[active])}">0</div>
            </div></div>
        </div>
    </div>

    <!-- TODO 13.1 -->
    <div th:if="${#lists.isEmpty(students)}" class="alert alert-warning">
        Không có sinh viên nào.
    </div>

    <div class="table-responsive" th:unless="${#lists.isEmpty(students)}">
        <table class="table table-bordered table-hover align-middle bg-white">
            <thead class="table-dark">
            <tr>
                <th>#</th><th>Mã SV</th><th>Họ tên</th><th>Email</th><th>Ngày sinh</th>
                <th>Tuổi</th><th>Ngành</th><th>GPA</th><th>Xếp loại</th><th>Trạng thái</th><th>Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <!-- TODO 11 + 12 -->
            <tr th:each="s, stat : ${students}"
                th:classappend="${s.gpa lt 5} ? 'table-danger'">
                <td th:text="${stat.count}">1</td>
                <td th:text="${s.studentCode}">SE180001</td>
                <td th:text="${#strings.toUpperCase(s.fullName)}">NGUYỄN VĂN A</td>
                <td th:text="${s.email}">a@fpt.edu.vn</td>
                <td th:text="${#temporals.format(s.dateOfBirth, 'dd/MM/yyyy')}">01/01/2004</td>
                <td th:text="${s.age}">22</td>
                <td th:text="${majors[s.major]}">Kỹ thuật phần mềm</td>
                <td th:text="${#numbers.formatDecimal(s.gpa, 1, 2)}">8.00</td>
                <td th:switch="${s.rank}">
                    <span th:case="'Xuất sắc'"   class="badge text-bg-success" th:text="${s.rank}"></span>
                    <span th:case="'Giỏi'"       class="badge text-bg-primary" th:text="${s.rank}"></span>
                    <span th:case="'Khá'"        class="badge text-bg-info"    th:text="${s.rank}"></span>
                    <span th:case="'Trung bình'" class="badge text-bg-warning" th:text="${s.rank}"></span>
                    <span th:case="*"            class="badge text-bg-danger"  th:text="${s.rank}"></span>
                </td>
                <td>
                    <span class="badge"
                          th:classappend="${s.active} ? 'text-bg-success' : 'text-bg-secondary'"
                          th:text="${s.active} ? 'Đang học' : 'Bảo lưu'">Đang học</span>
                </td>
                <!-- TODO 13.2 -->
                <td class="text-nowrap">
                    <a th:href="@{/students/{id}(id=${s.id})}" class="btn btn-sm btn-outline-info">Xem</a>
                    <a th:href="@{/students/{id}/edit(id=${s.id})}" class="btn btn-sm btn-outline-warning">Sửa</a>
                    <form th:action="@{/students/{id}/delete(id=${s.id})}" method="post" class="d-inline"
                          onsubmit="return confirm('Bạn chắc chắn muốn xóa sinh viên này?');">
                        <button type="submit" class="btn btn-sm btn-outline-danger">Xóa</button>
                    </form>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</section>
</body>
</html>
```
</details>

<details>
<summary><b>TODO 14: students/detail.html</b></summary>

```html
<!DOCTYPE html>
<html lang="vi"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/main}">
<head>
    <title>Chi tiết sinh viên</title>
</head>
<body>
<section layout:fragment="content">
    <div class="card shadow-sm">
        <div class="card-header d-flex justify-content-between align-items-center">
            <!-- 14.1 -->
            <h4 class="mb-0" th:text="|${student.studentCode} - ${student.fullName}|">SE180001 - Nguyễn Văn A</h4>
            <!-- 14.2 -->
            <span class="badge"
                  th:classappend="${student.active} ? 'text-bg-success' : 'text-bg-secondary'"
                  th:text="${student.active} ? 'Đang học' : 'Bảo lưu'">Đang học</span>
        </div>
        <div class="card-body">
            <!-- 14.3 -->
            <dl class="row mb-0">
                <dt class="col-sm-3">Mã sinh viên</dt>
                <dd class="col-sm-9" th:text="${student.studentCode}">SE180001</dd>

                <dt class="col-sm-3">Họ tên</dt>
                <dd class="col-sm-9" th:text="${student.fullName}">Nguyễn Văn A</dd>

                <dt class="col-sm-3">Email</dt>
                <dd class="col-sm-9">
                    <a th:href="|mailto:${student.email}|"
                       th:text="${student.email} ?: 'Chưa cập nhật'">a@fpt.edu.vn</a>
                </dd>

                <dt class="col-sm-3">Ngày sinh</dt>
                <dd class="col-sm-9"
                    th:text="|${#temporals.format(student.dateOfBirth, 'dd/MM/yyyy')} (${student.age} tuổi)|">
                    01/01/2004 (22 tuổi)
                </dd>

                <dt class="col-sm-3">Ngành</dt>
                <dd class="col-sm-9"
                    th:text="${student.major} + ' - ' + (${majors[student.major]} ?: 'Không xác định')">
                    SE - Kỹ thuật phần mềm
                </dd>

                <dt class="col-sm-3">GPA</dt>
                <dd class="col-sm-9" th:text="${#numbers.formatDecimal(student.gpa, 1, 2)} + ' / 10'">8.00 / 10</dd>

                <dt class="col-sm-3">Xếp loại</dt>
                <dd class="col-sm-9" th:text="${student.rank}">Giỏi</dd>

                <dt class="col-sm-3">Học bổng</dt>
                <dd class="col-sm-9"
                    th:text="${student.gpa ge 8.0 and student.active}
                             ? 'Đủ điều kiện xét học bổng' : 'Không đủ điều kiện'">
                    Đủ điều kiện xét học bổng
                </dd>
            </dl>

            <!-- 14.4 (bonus): cùng kết quả nhưng dùng th:object + *{...} -->
            <hr>
            <div th:object="${student}" class="small text-muted">
                <span>Selection expression → </span>
                <span th:text="*{fullName}">Nguyễn Văn A</span> –
                <span th:text="*{email}">a@fpt.edu.vn</span>
            </div>
        </div>
        <div class="card-footer">
            <!-- 14.5 -->
            <a th:href="@{/students}" class="btn btn-secondary">← Quay lại</a>
            <a th:href="@{/students/{id}/edit(id=${student.id})}" class="btn btn-warning">Sửa</a>
        </div>
    </div>
</section>
</body>
</html>
```
</details>

<details>
<summary><b>TODO 15 – 16: students/form.html</b></summary>

```html
<!DOCTYPE html>
<html lang="vi"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/main}">
<head>
    <!-- 15.1 -->
    <title th:text="${student.id == null} ? 'Thêm sinh viên' : 'Cập nhật sinh viên'">Form sinh viên</title>
</head>
<body>
<section layout:fragment="content">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow-sm">
                <div class="card-header">
                    <!-- 15.2 -->
                    <h4 class="mb-0"
                        th:text="${student.id == null} ? 'Thêm sinh viên mới'
                                 : 'Cập nhật sinh viên: ' + ${student.studentCode}">
                        Thêm sinh viên mới
                    </h4>
                </div>
                <div class="card-body">

                    <!-- 15.3 -->
                    <form th:action="@{/students/save}" th:object="${student}" method="post" novalidate>

                        <!-- 15.4 -->
                        <input type="hidden" th:field="*{id}">

                        <!-- 16.1 -->
                        <div th:if="${#fields.hasErrors('*')}" class="alert alert-danger">
                            <strong>Vui lòng sửa các lỗi sau:</strong>
                            <ul class="mb-0">
                                <li th:each="err : ${#fields.errors('*')}" th:text="${err}">Lỗi</li>
                            </ul>
                        </div>

                        <div class="row g-3">
                            <!-- 15.5 + 16.2 -->
                            <div class="col-md-6">
                                <label for="studentCode" class="form-label">Mã sinh viên <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" placeholder="VD: SE180001"
                                       th:field="*{studentCode}" th:errorclass="is-invalid">
                                <div class="invalid-feedback" th:if="${#fields.hasErrors('studentCode')}"
                                     th:errors="*{studentCode}"></div>
                            </div>

                            <div class="col-md-6">
                                <label for="fullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
                                <input type="text" class="form-control"
                                       th:field="*{fullName}" th:errorclass="is-invalid">
                                <div class="invalid-feedback" th:if="${#fields.hasErrors('fullName')}"
                                     th:errors="*{fullName}"></div>
                            </div>

                            <div class="col-md-6">
                                <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                                <input type="email" class="form-control"
                                       th:field="*{email}" th:errorclass="is-invalid">
                                <div class="invalid-feedback" th:if="${#fields.hasErrors('email')}"
                                     th:errors="*{email}"></div>
                            </div>

                            <div class="col-md-6">
                                <label for="dateOfBirth" class="form-label">Ngày sinh <span class="text-danger">*</span></label>
                                <input type="date" class="form-control"
                                       th:field="*{dateOfBirth}" th:errorclass="is-invalid">
                                <div class="invalid-feedback" th:if="${#fields.hasErrors('dateOfBirth')}"
                                     th:errors="*{dateOfBirth}"></div>
                            </div>

                            <div class="col-md-6">
                                <label for="gpa" class="form-label">GPA (0 - 10) <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" placeholder="VD: 8.5"
                                       th:field="*{gpa}" th:errorclass="is-invalid">
                                <div class="invalid-feedback" th:if="${#fields.hasErrors('gpa')}"
                                     th:errors="*{gpa}"></div>
                            </div>

                            <div class="col-md-6">
                                <label for="major" class="form-label">Ngành <span class="text-danger">*</span></label>
                                <!-- 15.6 -->
                                <select class="form-select" th:field="*{major}" th:errorclass="is-invalid">
                                    <option value="">-- Chọn ngành --</option>
                                    <option th:each="m : ${majors}"
                                            th:value="${m.key}"
                                            th:text="|${m.key} - ${m.value}|">SE - Kỹ thuật phần mềm</option>
                                </select>
                                <div class="invalid-feedback" th:if="${#fields.hasErrors('major')}"
                                     th:errors="*{major}"></div>
                            </div>

                            <div class="col-12">
                                <!-- 15.7 -->
                                <div class="form-check form-switch">
                                    <input class="form-check-input" type="checkbox" th:field="*{active}">
                                    <label class="form-check-label" th:for="${#ids.prev('active')}">Đang học</label>
                                </div>
                            </div>
                        </div>

                        <div class="mt-4 d-flex gap-2">
                            <!-- 15.8 -->
                            <button type="submit" class="btn btn-primary"
                                    th:text="*{id == null} ? 'Thêm mới' : 'Lưu thay đổi'">Lưu</button>
                            <a th:href="@{/students}" class="btn btn-secondary">Hủy</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</section>
</body>
</html>
```
</details>

---

## TÓM TẮT KIẾN THỨC (CHEAT SHEET)

```text
${student.fullName}                         → gọi getFullName()
${student.rank}                             → gọi getRank() (computed property)
${majors[s.major]}                          → tra Map theo key
${s.gpa lt 5} ? 'table-danger'              → ternary không có else
${s.active} ? 'Đang học' : 'Bảo lưu'        → ternary đầy đủ
${student.email} ?: 'Chưa cập nhật'         → Elvis (giá trị mặc định)
${student?.fullName}                        → safe navigation
|Xin chào ${student.fullName}|              → literal substitution
${#strings.toUpperCase(...)}                → utility object chuỗi
${#numbers.formatDecimal(x, 1, 2)}          → định dạng số
${#temporals.format(d, 'dd/MM/yyyy')}       → định dạng LocalDate
${#lists.isEmpty(list)} / #lists.size(...)  → kiểm tra List
${students.?[active]}                       → lọc collection (SpEL)
th:each="s, stat : ${students}"             → stat.count, stat.index, stat.odd...
th:object="${student}" + *{field}           → selection expression
${#fields.hasErrors('field')} + th:errors   → hiển thị lỗi validation
layout:decorate="~{layout/main}"            → dùng layout
layout:fragment="content"                   → vùng nội dung
@{/css/bootstrap.min.css}                   → Bootstrap offline trong static/
```

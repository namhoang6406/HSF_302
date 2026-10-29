# Chapter 08 — Bài Tập JavaFX Intro (Cơ Bản & Trung Bình)

> **Dựa trên:** Chapter 08 — Introduction to JavaFX
> **Môi trường:** JDK 21 · JavaFX 21 · Scene Builder 23 · Maven · IntelliJ IDEA
> **Cấu trúc mỗi bài:** Mục tiêu → Kiến thức (mục trong Chapter 08) → Mô tả → Step-by-step → TODOs → Validation → Checklist
> **Bản final:** bổ sung B1–B3 làm quen controls, layout, xử lý sự kiện, form đăng nhập/đăng ký (viết UI bằng code); B4 làm lại form đăng nhập bằng FXML

---

## Mục Lục

- [0. Chuẩn bị project dùng chung](#0-chuẩn-bị-project-dùng-chung)
- [Phần A — Bài tập cơ bản (B1–B6)](#phần-a--bài-tập-cơ-bản)
  - B1. Làm quen Controls & Xử lý sự kiện (Đặt đồ uống) — *code thuần*
  - B2. Layout thông dụng & Form đăng nhập — *code thuần*
  - B3. Form đăng ký + Validate + chuyển sang Đăng nhập — *code thuần*
  - B4. FXML + Controller + CSS (Form đăng nhập bằng Scene Builder)
  - B5. Layout components (Layout Gallery)
  - B6. Controls — Phiếu khảo sát
- [Phần B — Bài tập trung bình (T1–T6)](#phần-b--bài-tập-trung-bình)
  - T1. Quản lý sinh viên: TreeView + TableView + Menu + FileChooser
  - T2. Shape Editor: 2D Graphics, Mouse/Key event, Transform, Effect, Animation
  - T3. Concurrency: Task, ProgressBar, Cancel, Platform.runLater
  - T4. Charts Dashboard cập nhật real-time
  - T5. Mini Browser & Media Player (WebView + Media)
  - T6. 3D Viewer (bonus)
- [Phần C — Lưu ý quan trọng](#phần-c--lưu-ý-quan-trọng)
- [Phần D — Checklist kiểm tra tổng](#phần-d--checklist-kiểm-tra-tổng)

---

## 0. Chuẩn bị project dùng chung

Tạo **1 Maven project** `ch08-javafx`, mỗi bài là 1 package riêng. Đổi `mainClass` trong plugin (hoặc Run trực tiếp từ IDE) để chạy từng bài.

### 0.1. `pom.xml`

```xml
<properties>
    <maven.compiler.release>21</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <javafx.version>21</javafx.version>
</properties>

<dependencies>
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
    <!-- T5: WebView + Media -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-web</artifactId>
        <version>${javafx.version}</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-media</artifactId>
        <version>${javafx.version}</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-maven-plugin</artifactId>
            <version>0.0.8</version>
            <configuration>
                <!-- Đổi theo bài đang chạy: com.fu.ch08/com.fu.ch08.b1.DrinkOrderApp -->
                <mainClass>com.fu.ch08/com.fu.ch08.b1.DrinkOrderApp</mainClass>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 0.2. Cấu trúc project

```
ch08-javafx/
├─ pom.xml
└─ src/main/
   ├─ java/
   │  ├─ module-info.java
   │  └─ com/fu/ch08/
   │     ├─ b1/ ... b3/        # Bài cơ bản — UI viết bằng code (không FXML)
   │     ├─ b4/ ... b6/        # Bài cơ bản — FXML + Controller
   │     ├─ t1/ ... t6/        # Bài trung bình
   │     └─ common/AlertUtil.java
   └─ resources/com/fu/ch08/
      ├─ b4/login-view.fxml, login.css
      ├─ b5/main-layout.fxml
      ├─ b6/survey-view.fxml
      ├─ t1/student-view.fxml
      └─ ...                   # FXML/CSS đặt CÙNG đường dẫn package với class
```

### 0.3. `module-info.java`

```java
module com.fu.ch08 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;      // T5
    requires javafx.media;    // T5

    // FXMLLoader cần reflection vào controller (@FXML private field/method)
    opens com.fu.ch08.b4 to javafx.fxml;
    opens com.fu.ch08.b5 to javafx.fxml;
    opens com.fu.ch08.b6 to javafx.fxml;
    opens com.fu.ch08.t1 to javafx.fxml, javafx.base;   // javafx.base: PropertyValueFactory (TableView)
    opens com.fu.ch08.t4 to javafx.fxml, javafx.base;

    // Application class phải được export để javafx.graphics khởi tạo được
    // b1–b3 viết UI bằng code (không FXML) → chỉ cần exports, không cần opens
    exports com.fu.ch08.b1;
    exports com.fu.ch08.b2;
    exports com.fu.ch08.b3;
    exports com.fu.ch08.b4;
    exports com.fu.ch08.b5;
    exports com.fu.ch08.b6;
    exports com.fu.ch08.t1;
    exports com.fu.ch08.t2;
    exports com.fu.ch08.t3;
    exports com.fu.ch08.t4;
    exports com.fu.ch08.t5;
    exports com.fu.ch08.t6;
}
```

> Thêm package nào thì nhớ thêm `exports`/`opens` tương ứng — đây là nguyên nhân lỗi phổ biến nhất (xem Phần C).

### 0.4. Tiện ích dùng chung

```java
package com.fu.ch08.common;

public final class AlertUtil {
    private AlertUtil() {}

    public static void info(String msg)  { show(Alert.AlertType.INFORMATION, msg); }
    public static void warn(String msg)  { show(Alert.AlertType.WARNING, msg); }
    public static void error(String msg) { show(Alert.AlertType.ERROR, msg); }

    public static boolean confirm(String msg) {
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO)
                .showAndWait().filter(b -> b == ButtonType.YES).isPresent();
    }

    private static void show(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
```
> Nhớ `exports com.fu.ch08.common;` nếu dùng ở package khác (cùng module thì không bắt buộc).

---

## Phần A — Bài tập cơ bản

## B1 — Làm Quen Controls & Xử Lý Sự Kiện (Đặt Đồ Uống)

### Mục tiêu
- Tạo và cấu hình các control cơ bản **bằng code Java** (chưa dùng FXML): `Label`, `TextField`, `Button`, `CheckBox`, `RadioButton` + `ToggleGroup`, `ComboBox`, `TextArea`
- Đọc giá trị từ từng loại control: `getText()`, `getValue()`, `isSelected()`, `getSelectedToggle()`
- Đăng ký xử lý sự kiện theo **3 cách**: anonymous class, lambda, method reference
- Phân biệt **ActionEvent**, **KeyEvent**, **MouseEvent** và **ChangeListener** (lắng nghe giá trị thay đổi)
- Dùng `event.getSource()` để 1 handler phục vụ nhiều control; `consume()` để chặn sự kiện

### Kiến thức (Chapter 08)
§4 JavaFX Parts · §14 JavaFX Controls · Xử lý sự kiện (`EventHandler`, `setOnXxx`, `addEventFilter`)

### Mô tả
Form "Đặt đồ uống" một cửa sổ, toàn bộ UI viết bằng code. Phía dưới có **nhật ký sự kiện** (`TextArea`): mỗi thao tác của người dùng ghi 1 dòng, giúp **thấy được** sự kiện nào xảy ra và khi nào.

| Field | Control | Ghi chú |
|---|---|---|
| Tên khách | `TextField` | Bắt buộc; Enter → chuyển focus sang ComboBox |
| Đồ uống | `ComboBox<String>` | Cà phê sữa 25.000 · Trà sữa 30.000 · Nước cam 28.000 · Sinh tố bơ 35.000 |
| Size | `RadioButton` × 3 + `ToggleGroup` | S (+0) · M (+5.000, mặc định) · L (+10.000) |
| Tùy chọn | `CheckBox` × 3 | Trân châu (+5.000) · Thạch (+5.000) · Ít đường (0) |
| Số lượng | `TextField` | Chỉ cho gõ số, 1–20 |
| — | `Button` Tính tiền / Làm lại | Enter = Tính tiền, ESC = Làm lại |
| Kết quả | `Label` | Xanh nếu hợp lệ, đỏ nếu lỗi |
| Nhật ký | `TextArea` (chỉ đọc) | Double-click để xóa nhật ký |

**Công thức:** `đơn giá = giá đồ uống + phụ thu size + 5.000 × số topping`; `tổng = đơn giá × số lượng`; mua **≥ 5 ly giảm 10%**.

### Step-by-step
**Bước 1:** Tạo package `com.fu.ch08.b1`, class `DrinkOrderApp extends Application`. Không cần FXML → `module-info` chỉ cần `exports com.fu.ch08.b1;` (không cần `opens`).
**Bước 2:** Khai báo control làm **field** của class để mọi method đều truy cập được.
**Bước 3:** Trong `start()`: cấu hình control (prompt text, items, ToggleGroup, userData) → xếp vào `GridPane` + `HBox` + `VBox`.
**Bước 4:** Viết `registerEvents()` — đăng ký sự kiện theo 3 cách, handler dùng chung, listener.
**Bước 5:** Viết `handleCalc()` — đọc giá trị, validate, tính tiền; `reset()` — đưa form về ban đầu.
**Bước 6:** Thêm event filter ESC ở `Scene`, sự kiện chuột cho nút/nhật ký. Chạy thử, quan sát nhật ký sau mỗi thao tác.

### TODOs

```java
package com.fu.ch08.b1;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class DrinkOrderApp extends Application {

    // Bảng giá — LinkedHashMap giữ đúng thứ tự khi đổ vào ComboBox
    private static final Map<String, Integer> MENU = new LinkedHashMap<>();
    static {
        MENU.put("Cà phê sữa", 25_000);
        MENU.put("Trà sữa", 30_000);
        MENU.put("Nước cam", 28_000);
        MENU.put("Sinh tố bơ", 35_000);
    }
    private static final int TOPPING_PRICE = 5_000;

    // TODO 1: Khai báo control là field
    private final TextField tfName = new TextField();
    private final ComboBox<String> cbDrink = new ComboBox<>();
    private final ToggleGroup tgSize = new ToggleGroup();
    private final RadioButton rbS = new RadioButton("S");
    private final RadioButton rbM = new RadioButton("M (+5.000)");
    private final RadioButton rbL = new RadioButton("L (+10.000)");
    private final CheckBox chkPearl = new CheckBox("Trân châu (+5.000)");
    private final CheckBox chkJelly = new CheckBox("Thạch (+5.000)");
    private final CheckBox chkLessSugar = new CheckBox("Ít đường");
    private final TextField tfQty = new TextField("1");
    private final Button btnCalc = new Button("Tính tiền");
    private final Button btnClear = new Button("Làm lại");
    private final Label lblResult = new Label("Chưa có đơn hàng");
    private final TextArea taLog = new TextArea();

    @Override
    public void start(Stage stage) {
        // TODO 2: Cấu hình control
        tfName.setPromptText("Nhập tên khách hàng");
        cbDrink.getItems().addAll(MENU.keySet());
        cbDrink.setPromptText("-- Chọn đồ uống --");
        cbDrink.setMaxWidth(Double.MAX_VALUE);

        rbS.setToggleGroup(tgSize);
        rbM.setToggleGroup(tgSize);
        rbL.setToggleGroup(tgSize);
        rbS.setUserData(0);                 // userData: gắn dữ liệu tùy ý vào control (ở đây: phụ thu size)
        rbM.setUserData(5_000);
        rbL.setUserData(10_000);
        tgSize.selectToggle(rbM);

        tfQty.setPrefColumnCount(4);
        taLog.setEditable(false);
        taLog.setPrefRowCount(8);
        taLog.setTooltip(new Tooltip("Double-click để xóa nhật ký"));
        btnCalc.setDefaultButton(true);     // Enter = bấm "Tính tiền"

        // TODO 3: Xếp layout (B2 sẽ học kỹ hơn)
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Tên khách:"), tfName);
        form.addRow(1, new Label("Đồ uống:"), cbDrink);
        form.addRow(2, new Label("Size:"), new HBox(15, rbS, rbM, rbL));
        form.addRow(3, new Label("Tùy chọn:"), new HBox(15, chkPearl, chkJelly, chkLessSugar));
        form.addRow(4, new Label("Số lượng:"), tfQty);

        HBox buttons = new HBox(10, btnCalc, btnClear);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Label title = new Label("ĐẶT ĐỒ UỐNG");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        VBox root = new VBox(12, title, form, buttons, lblResult, new Label("Nhật ký sự kiện:"), taLog);
        root.setPadding(new Insets(16));

        registerEvents();

        Scene scene = new Scene(root, 540, 580);

        // TODO 9: Event FILTER trên Scene — chạy ở pha capturing, TRƯỚC khi control con nhận phím
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                reset();
                log("ESC → làm lại form");
                e.consume();                // chặn, không cho sự kiện đi tiếp
            }
        });

        stage.setScene(scene);
        stage.setTitle("B1 - Controls & Sự kiện");
        stage.show();
        tfName.requestFocus();
    }

    private void registerEvents() {
        // TODO 4: Cách 1 — method reference (gọn khi logic dài, tách ra method riêng)
        btnCalc.setOnAction(this::handleCalc);

        // TODO 5: Cách 2 — lambda (phổ biến nhất)
        btnClear.setOnAction(e -> {
            reset();
            log("Bấm Làm lại");
        });

        // TODO 6: Cách 3 — anonymous class (viết "dài" để hiểu bản chất: EventHandler là interface 1 method handle)
        cbDrink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String d = cbDrink.getValue();
                if (d != null) {            // reset() gọi setValue(null) cũng kích hoạt onAction → phải kiểm tra null
                    log("Chọn đồ uống: %s (%,d đ)".formatted(d, MENU.get(d)));
                }
            }
        });

        // TextField: nhấn Enter → ActionEvent. Vì tfName có onAction nên Enter KHÔNG kích hoạt defaultButton
        tfName.setOnAction(e -> cbDrink.requestFocus());

        // TODO 7: Một handler dùng chung cho nhiều CheckBox — phân biệt bằng getSource()
        EventHandler<ActionEvent> onCheck = e -> {
            CheckBox src = (CheckBox) e.getSource();
            log("%s: %s".formatted(src.getText(), src.isSelected() ? "chọn" : "bỏ chọn"));
        };
        chkPearl.setOnAction(onCheck);
        chkJelly.setOnAction(onCheck);
        chkLessSugar.setOnAction(onCheck);

        // TODO 8: ChangeListener — lắng nghe GIÁ TRỊ thay đổi (do người dùng hoặc do code)
        tgSize.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT != null) log("Đổi size → " + ((RadioButton) newT).getText());
        });
        tfQty.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d{0,2}")) tfQty.setText(oldV);   // chỉ cho gõ tối đa 2 chữ số
        });

        // TODO 10: MouseEvent
        btnCalc.setOnMouseEntered(e -> btnCalc.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white;"));
        btnCalc.setOnMouseExited(e -> btnCalc.setStyle(""));
        taLog.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) taLog.clear();
        });
    }

    // TODO 11: Xử lý chính — đọc giá trị, validate, tính tiền
    private void handleCalc(ActionEvent e) {
        String name = tfName.getText().trim();
        String drink = cbDrink.getValue();
        String qtyText = tfQty.getText();

        if (name.isEmpty()) { showError("Vui lòng nhập tên khách"); tfName.requestFocus(); return; }
        if (drink == null) { showError("Vui lòng chọn đồ uống"); cbDrink.requestFocus(); return; }
        if (tgSize.getSelectedToggle() == null) { showError("Vui lòng chọn size"); return; }
        int qty = qtyText.isEmpty() ? 0 : Integer.parseInt(qtyText);   // an toàn vì listener chỉ cho gõ số
        if (qty < 1 || qty > 20) { showError("Số lượng từ 1 đến 20"); tfQty.requestFocus(); return; }

        RadioButton size = (RadioButton) tgSize.getSelectedToggle();
        int unit = MENU.get(drink) + (int) size.getUserData();
        if (chkPearl.isSelected()) unit += TOPPING_PRICE;
        if (chkJelly.isSelected()) unit += TOPPING_PRICE;
        int total = unit * qty;
        boolean discount = qty >= 5;
        if (discount) total = total * 90 / 100;

        lblResult.setStyle("-fx-text-fill: #188038; -fx-font-weight: bold;");
        lblResult.setText("%s: %d × %s size %s%s → %,d đ%s".formatted(
                name, qty, drink, size.getText().charAt(0),
                chkLessSugar.isSelected() ? " (ít đường)" : "",
                total, discount ? " (đã giảm 10%)" : ""));
        log("Tính tiền: %,d đ".formatted(total));
    }

    private void showError(String msg) {
        lblResult.setStyle("-fx-text-fill: #d93025;");
        lblResult.setText(msg);
        log("Lỗi: " + msg);
    }

    // TODO 12: Đưa form về trạng thái ban đầu
    private void reset() {
        tfName.clear();
        cbDrink.setValue(null);
        tgSize.selectToggle(rbM);
        chkPearl.setSelected(false);
        chkJelly.setSelected(false);
        chkLessSugar.setSelected(false);
        tfQty.setText("1");
        lblResult.setStyle("");
        lblResult.setText("Chưa có đơn hàng");
        tfName.requestFocus();
    }

    private void log(String msg) {
        taLog.appendText("[%s] %s%n".formatted(
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), msg));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

> **Chạy:** đổi `mainClass` trong `pom.xml` thành `com.fu.ch08/com.fu.ch08.b1.DrinkOrderApp` rồi `mvn javafx:run`, hoặc Run trực tiếp class trong IDE.

### Validation cần kiểm tra
- Tên khách không rỗng (sau `trim()`); phải chọn đồ uống và size
- Số lượng: không gõ được chữ, tối đa 2 chữ số; giá trị phải trong 1–20
- Ví dụ kiểm tra: Trà sữa, size L, Trân châu + Thạch, SL 5 → `(30.000 + 10.000 + 10.000) × 5 × 90% = 225.000 đ`

### Checklist hoàn thành
- [ ] UI viết hoàn toàn bằng code, không có FXML
- [ ] 3 RadioButton chung 1 `ToggleGroup` → chỉ chọn được 1
- [ ] Có đủ 3 cách đăng ký handler: anonymous class, lambda, method reference
- [ ] 3 CheckBox dùng **chung 1 handler**, nhật ký ghi đúng tên CheckBox (`getSource()`)
- [ ] Đổi size → nhật ký ghi lại (listener); đổi đồ uống → nhật ký ghi lại (onAction)
- [ ] Enter ở ô Tên → chuyển sang ComboBox; Enter ở ô Số lượng → Tính tiền (`defaultButton`)
- [ ] ESC ở bất kỳ ô nào → làm lại form (event filter + `consume()`)
- [ ] Rê chuột vào nút "Tính tiền" → đổi màu; rời ra → về màu cũ
- [ ] Double-click nhật ký → xóa nhật ký
- [ ] Giải thích được khác nhau giữa **event handler** và **ChangeListener**

---

## B2 — Layout Thông Dụng & Form Đăng Nhập (Code Thuần)

### Mục tiêu
- Dùng 5 layout hay gặp nhất: `BorderPane` (khung app), `HBox`, `VBox`, `GridPane` (form), `StackPane` (căn giữa)
- Nắm các thuộc tính layout: `padding`, `spacing`, `alignment`, `hgap/vgap`, `ColumnConstraints`, `Priority` (grow), `maxWidth`
- Kỹ thuật **spacer** (`Region` + `HBox.setHgrow/VBox.setVgrow`) để đẩy node sang phải/xuống đáy
- Xử lý sự kiện form đăng nhập; **chuyển màn hình** bằng `scene.setRoot(...)`
- `defaultButton` (Enter) / `cancelButton` (ESC)

### Kiến thức (Chapter 08)
§13 Layout Components · §14 JavaFX Controls · Xử lý sự kiện

### Mô tả
Ứng dụng có 2 màn hình, chuyển qua lại bằng cách **đổi root của Scene**:

**Màn hình 1 — Đăng nhập** (`BorderPane`)
```
┌──────────────────────────────────────────────┐
│ [HSF] Hệ thống quản lý sinh viên        v1.0 │  ← top: HBox (spacer đẩy "v1.0" sang phải)
├──────────────────────────────────────────────┤
│                                              │
│          ┌──────── ĐĂNG NHẬP ────────┐       │  ← center: StackPane căn giữa "card" VBox
│          │ Username [______________] │       │     trong card: GridPane 2 cột
│          │ Password [______________] │       │
│          │          ☐ Ghi nhớ        │       │
│          │ (thông báo lỗi)           │       │
│          │ [ Đăng nhập ] [  Thoát  ] │       │  ← HBox, 2 nút giãn đều
│          └───────────────────────────┘       │
├──────────────────────────────────────────────┤
│ ☐ Hiện viền layout (debug)    © 2026 HSF302  │  ← bottom: HBox
└──────────────────────────────────────────────┘
```

**Màn hình 2 — Trang chủ** (`BorderPane`): top = header dùng lại; left = `VBox` menu (4 nút + nút "Đăng xuất" bị spacer đẩy xuống đáy); center = lời chào.

| Username | Password |
|---|---|
| `admin` | `Admin@123` |
| `student` | `Student@123` |

- Tích **"Ghi nhớ"** → sau khi đăng xuất, ô Username được điền sẵn.
- Tích **"Hiện viền layout"** → mọi container hiện viền đỏ nét đứt, giúp nhìn rõ từng layout chiếm vùng nào khi resize.

### Step-by-step
**Bước 1:** Tạo package `com.fu.ch08.b2`, class `LoginLayoutApp`. Giữ `Scene` làm field để đổi root.
**Bước 2:** Viết `buildHeader()` (HBox + spacer), `buildFooter()` (HBox).
**Bước 3:** Viết `buildCenter()`: GridPane form → VBox card → StackPane căn giữa.
**Bước 4:** Ghép vào `BorderPane` trong `buildLoginView()`.
**Bước 5:** Xử lý sự kiện: Đăng nhập, Thoát (xác nhận), xóa thông báo lỗi khi gõ lại.
**Bước 6:** Viết `buildHomeView()`; Đăng nhập thành công → `scene.setRoot(home)`; Đăng xuất → `scene.setRoot(login)`.
**Bước 7:** Resize cửa sổ, bật viền debug để quan sát hành vi từng layout.

### TODOs

```java
package com.fu.ch08.b2;

import com.fu.ch08.common.AlertUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginLayoutApp extends Application {

    private static final Map<String, String> ACCOUNTS = Map.of(
            "admin", "Admin@123",
            "student", "Student@123");

    private static final Border DEBUG_BORDER = new Border(new BorderStroke(
            Color.RED, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(1)));

    private Scene scene;
    private String rememberedUser;                              // "Ghi nhớ đăng nhập"
    private final List<Region> containers = new ArrayList<>();  // để bật/tắt viền debug

    @Override
    public void start(Stage stage) {
        scene = new Scene(buildLoginView(), 680, 480);
        stage.setScene(scene);
        stage.setTitle("B2 - Layout & Form đăng nhập");
        stage.setMinWidth(480);
        stage.setMinHeight(400);
        stage.show();
    }

    // TODO 1: Khung chính — BorderPane 3 vùng top / center / bottom
    private Parent buildLoginView() {
        containers.clear();
        BorderPane root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(buildCenter());
        root.setBottom(buildFooter());
        return track(root);
    }

    // TODO 2: Header — HBox: logo, tiêu đề, SPACER giãn, version bên phải
    private Node buildHeader() {
        Label logo = new Label("HSF");
        logo.setStyle("-fx-background-color: #f27024; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 4 8;");
        Label title = new Label("Hệ thống quản lý sinh viên");
        title.setStyle("-fx-font-size: 16; -fx-text-fill: white;");
        Label version = new Label("v1.0");
        version.setStyle("-fx-text-fill: #cfd8dc;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);     // spacer chiếm hết chỗ trống → "đẩy" version sang phải

        HBox header = new HBox(10, logo, title, spacer, version);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 16, 10, 16));
        header.setStyle("-fx-background-color: #263238;");
        return track(header);
    }

    // TODO 3: Center — GridPane (form) → VBox (card) → StackPane (căn giữa)
    private Node buildCenter() {
        Label lblTitle = new Label("ĐĂNG NHẬP");
        lblTitle.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        TextField tfUser = new TextField(rememberedUser == null ? "" : rememberedUser);
        tfUser.setPromptText("Username");
        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Password");
        CheckBox chkRemember = new CheckBox("Ghi nhớ đăng nhập");
        chkRemember.setSelected(rememberedUser != null);
        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #d93025;");
        lblError.setWrapText(true);

        // 3a. GridPane: cột 0 cố định 80px, cột 1 giãn theo chiều ngang
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Username"), 0, 0);
        grid.add(tfUser, 1, 0);
        grid.add(new Label("Password"), 0, 1);
        grid.add(pfPass, 1, 1);
        grid.add(chkRemember, 1, 2);
        ColumnConstraints c0 = new ColumnConstraints(80);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c0, c1);

        // 3b. HBox 2 nút giãn đều: maxWidth = MAX_VALUE + hgrow ALWAYS
        Button btnLogin = new Button("Đăng nhập");
        Button btnExit = new Button("Thoát");
        btnLogin.setDefaultButton(true);            // Enter
        btnExit.setCancelButton(true);              // ESC
        for (Button b : List.of(btnLogin, btnExit)) {
            b.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(b, Priority.ALWAYS);
        }
        HBox actions = new HBox(10, btnLogin, btnExit);

        // 3c. VBox card: rộng tối đa 380, cao vừa nội dung
        VBox card = new VBox(14, lblTitle, grid, lblError, actions);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(24));
        card.setMaxWidth(380);
        card.setMaxHeight(Region.USE_PREF_SIZE);    // không bị StackPane kéo giãn theo chiều cao
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0, 0, 3);");

        // 3d. StackPane mặc định căn giữa node con
        StackPane center = new StackPane(card);
        center.setPadding(new Insets(20));
        center.setStyle("-fx-background-color: #eceff1;");

        // TODO 4: Xử lý sự kiện
        btnLogin.setOnAction(e -> {
            String user = tfUser.getText().trim().toLowerCase();
            String pass = pfPass.getText();
            if (user.isEmpty() || pass.isEmpty()) {
                lblError.setText("Vui lòng nhập đầy đủ username và password");
                (user.isEmpty() ? tfUser : pfPass).requestFocus();
                return;
            }
            if (!pass.equals(ACCOUNTS.get(user))) {
                lblError.setText("Sai username hoặc mật khẩu");
                pfPass.clear();
                pfPass.requestFocus();
                return;
            }
            rememberedUser = chkRemember.isSelected() ? user : null;
            scene.setRoot(buildHomeView(user));     // CHUYỂN MÀN HÌNH: đổi root, giữ nguyên Stage & Scene
        });

        btnExit.setOnAction(e -> {
            if (AlertUtil.confirm("Bạn muốn thoát ứng dụng?")) Platform.exit();
        });

        // Gõ lại → xóa thông báo lỗi
        tfUser.textProperty().addListener((obs, o, n) -> lblError.setText(""));
        pfPass.textProperty().addListener((obs, o, n) -> {
            if (!n.isEmpty()) lblError.setText("");
        });

        Platform.runLater(() -> (tfUser.getText().isEmpty() ? tfUser : pfPass).requestFocus());

        track(grid);
        track(actions);
        track(card);
        return track(center);
    }

    // TODO 5: Footer — HBox: CheckBox debug | spacer | bản quyền
    private Node buildFooter() {
        CheckBox chkDebug = new CheckBox("Hiện viền layout (debug)");
        chkDebug.setOnAction(e -> containers.forEach(
                r -> r.setBorder(chkDebug.isSelected() ? DEBUG_BORDER : null)));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox footer = new HBox(10, chkDebug, spacer, new Label("© 2026 HSF302 — Chapter 08"));
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(6, 16, 6, 16));
        footer.setStyle("-fx-background-color: #cfd8dc;");
        return track(footer);
    }

    // TODO 6: Trang chủ — BorderPane: top header, left menu VBox, center lời chào
    private Parent buildHomeView(String user) {
        containers.clear();
        Label lblHello = new Label("Xin chào, " + user + "!");
        lblHello.setStyle("-fx-font-size: 22;");

        VBox menu = new VBox(8);
        for (String item : List.of("Trang chủ", "Sinh viên", "Môn học", "Điểm")) {
            Button b = new Button(item);
            b.setMaxWidth(Double.MAX_VALUE);        // VBox: nút giãn bằng chiều rộng menu
            b.setOnAction(e -> lblHello.setText("Bạn đang ở: " + item));
            menu.getChildren().add(b);
        }
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);     // đẩy nút Đăng xuất xuống đáy
        Button btnLogout = new Button("Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> scene.setRoot(buildLoginView()));
        menu.getChildren().addAll(spacer, btnLogout);
        menu.setPadding(new Insets(10));
        menu.setPrefWidth(160);
        menu.setStyle("-fx-background-color: #37474f;");

        BorderPane root = new BorderPane(new StackPane(lblHello));  // BorderPane(center)
        root.setTop(buildHeader());
        root.setLeft(menu);
        return root;
    }

    private <T extends Region> T track(T region) {
        containers.add(region);
        return region;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

> **Câu hỏi suy nghĩ:** B4 sẽ làm lại form đăng nhập này bằng **FXML + Scene Builder + CSS**. Sau khi làm xong B4, so sánh: phần nào của B2 được chuyển sang FXML/CSS, phần nào vẫn phải ở Java?

### Validation cần kiểm tra
- Username/password rỗng → báo lỗi, focus vào ô còn thiếu
- Sai tài khoản → báo lỗi, xóa password; username không phân biệt hoa thường
- Gõ lại → thông báo lỗi biến mất
- Thoát → hỏi xác nhận; chọn No thì không thoát

### Checklist hoàn thành
- [ ] Màn hình đăng nhập đủ 3 vùng `BorderPane` (top / center / bottom)
- [ ] Header: "v1.0" luôn nằm sát phải khi kéo rộng cửa sổ (spacer)
- [ ] Card đăng nhập luôn ở giữa, không bị kéo giãn khi phóng to cửa sổ
- [ ] GridPane: cột ô nhập giãn ra, cột nhãn giữ 80px
- [ ] 2 nút Đăng nhập / Thoát rộng bằng nhau và giãn theo card
- [ ] Enter = Đăng nhập, ESC = Thoát (có xác nhận)
- [ ] Đăng nhập đúng → sang Trang chủ **cùng cửa sổ**; Đăng xuất → về màn hình đăng nhập
- [ ] Menu trái: nút "Đăng xuất" luôn ở đáy khi resize chiều cao
- [ ] "Ghi nhớ" → đăng xuất xong username được điền sẵn, focus ở ô password
- [ ] Bật viền debug → thấy rõ ranh giới từng container
- [ ] Giải thích được: khi nào dùng `HBox/VBox`, `GridPane`, `StackPane`, `BorderPane`

---

## B3 — Form Đăng Ký Tài Khoản + Validate + Chuyển Sang Đăng Nhập

### Mục tiêu
- Thiết kế form nhiều loại input: `TextField`, `PasswordField`, `RadioButton`, `DatePicker`, `ComboBox`, `CheckBox`, `Hyperlink`, `ProgressBar`
- **Validate từng field** với thông báo lỗi ngay dưới ô (inline error): khi **mất focus** và khi bấm **Đăng ký**
- Cập nhật giao diện **theo từng phím gõ** (độ mạnh mật khẩu) bằng `textProperty().addListener`
- Bind đơn giản: nút Đăng ký chỉ bật khi tích "Đồng ý điều khoản"
- Lưu tài khoản vào bộ nhớ (`Map`) → chuyển sang form đăng nhập và đăng nhập bằng tài khoản vừa tạo

### Kiến thức (Chapter 08)
§11 Properties (listener, bind cơ bản) · §13 Layout Components · §14 JavaFX Controls · Xử lý sự kiện

### Mô tả

| Field | Control | Ràng buộc |
|---|---|---|
| Họ tên * | `TextField` | 3–50 ký tự, chỉ chữ cái (có dấu tiếng Việt) và khoảng trắng |
| Email * | `TextField` | Đúng định dạng email |
| Username * | `TextField` | `[a-z0-9_]{3,20}`, **chưa tồn tại** |
| Mật khẩu * | `PasswordField` + `ProgressBar` + `Label` | ≥ 8 ký tự, có chữ hoa, chữ thường, số, ký tự đặc biệt; hiện độ mạnh khi gõ |
| Xác nhận MK * | `PasswordField` | Trùng mật khẩu |
| Giới tính * | `RadioButton` × 3 + `ToggleGroup` | Bắt buộc chọn |
| Ngày sinh * | `DatePicker` | Không ở tương lai, đủ **16 tuổi** |
| Ngành * | `ComboBox<String>` | Bắt buộc chọn |
| Sở thích | `CheckBox` × 4 trong `FlowPane` | Không bắt buộc |
| — | `CheckBox` "Tôi đồng ý điều khoản" | Chưa tích → nút Đăng ký bị disable |
| — | `Button` Đăng ký / Làm lại, `Hyperlink` "Đã có tài khoản? Đăng nhập" | — |

**Luồng:** Đăng ký hợp lệ → lưu vào `UserStore` → `Alert` thành công → chuyển sang **form đăng nhập** (điền sẵn username) → đăng nhập → `Alert` "Xin chào {họ tên}". Từ form đăng nhập có Hyperlink quay lại đăng ký. Tài khoản mẫu có sẵn: `admin` / `Admin@123`.

### Step-by-step
**Bước 1:** Tạo package `com.fu.ch08.b3`: `User` (record), `UserStore` (lưu tài khoản trong `Map`), `RegisterApp`.
**Bước 2:** `setupControls()` — prompt text, ToggleGroup, items ComboBox, bind nút Đăng ký.
**Bước 3:** `buildRegisterView()` — GridPane 2 cột; mỗi ô nhập nằm trong `VBox(input, errorLabel)` (helper `addField`). Bọc form trong `ScrollPane`.
**Bước 4:** Viết các hàm validator `vXxx()` — mỗi hàm kiểm tra 1 field, ghi lỗi vào label riêng, trả về `boolean`.
**Bước 5:** `registerEvents()` — gắn validator vào sự kiện mất focus / giá trị đổi; listener độ mạnh mật khẩu; nút Đăng ký / Làm lại.
**Bước 6:** `handleRegister()` → lưu → chuyển màn hình; `buildLoginView()` kiểm tra với `UserStore`.

### TODOs

```java
// TODO 1: Model & "database" trong bộ nhớ
package com.fu.ch08.b3;

public record User(String fullName, String email, String username, String password,
                   String gender, LocalDate dob, String major, List<String> hobbies) {}
```

```java
package com.fu.ch08.b3;

public final class UserStore {
    private static final Map<String, User> USERS = new HashMap<>();

    static {    // tài khoản mẫu
        add(new User("Quản trị viên", "admin@example.com", "admin", "Admin@123",
                "Khác", LocalDate.of(2000, 1, 1), "Kỹ thuật phần mềm", List.of()));
    }

    private UserStore() {}

    public static boolean exists(String username) {
        return USERS.containsKey(username.trim().toLowerCase());
    }

    public static void add(User u) {
        USERS.put(u.username(), u);
    }

    public static Optional<User> login(String username, String password) {
        return Optional.ofNullable(USERS.get(username.trim().toLowerCase()))
                .filter(u -> u.password().equals(password));
    }
}
// Lưu ý: app thật phải lưu HASH mật khẩu (BCrypt...), không lưu plain text.
```

```java
package com.fu.ch08.b3;

import com.fu.ch08.common.AlertUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

public class RegisterApp extends Application {

    private static final String STRONG_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$";
    private static final String[] LEVEL_TEXT  = {"Rất yếu", "Yếu", "Trung bình", "Khá", "Mạnh"};
    private static final String[] LEVEL_COLOR = {"#d93025", "#d93025", "#f29900", "#1a73e8", "#188038"};

    private Scene scene;
    private Parent registerView;        // tạo 1 lần, dùng lại khi quay về từ màn hình đăng nhập

    // ===== Control form đăng ký =====
    private final TextField tfFullName = new TextField();
    private final TextField tfEmail = new TextField();
    private final TextField tfUsername = new TextField();
    private final PasswordField pfPassword = new PasswordField();
    private final PasswordField pfConfirm = new PasswordField();
    private final ToggleGroup tgGender = new ToggleGroup();
    private final RadioButton rbMale = new RadioButton("Nam");
    private final RadioButton rbFemale = new RadioButton("Nữ");
    private final RadioButton rbOther = new RadioButton("Khác");
    private final DatePicker dpDob = new DatePicker();
    private final ComboBox<String> cbMajor = new ComboBox<>();
    private final CheckBox chkSport = new CheckBox("Thể thao");
    private final CheckBox chkMusic = new CheckBox("Âm nhạc");
    private final CheckBox chkGame = new CheckBox("Game");
    private final CheckBox chkReading = new CheckBox("Đọc sách");
    private final CheckBox chkAgree = new CheckBox("Tôi đồng ý với điều khoản sử dụng");
    private final ProgressBar pbStrength = new ProgressBar(0);
    private final Label lblStrength = new Label();
    private final Button btnRegister = new Button("Đăng ký");
    private final Button btnReset = new Button("Làm lại");

    // Label lỗi riêng cho từng field
    private final Label errFullName = err(), errEmail = err(), errUsername = err(), errPassword = err(),
                        errConfirm = err(), errGender = err(), errDob = err(), errMajor = err();

    @Override
    public void start(Stage stage) {
        setupControls();
        registerEvents();
        registerView = buildRegisterView();
        scene = new Scene(registerView, 580, 660);
        stage.setScene(scene);
        stage.setTitle("B3 - Đăng ký & Đăng nhập");
        stage.show();
    }

    // TODO 2: Cấu hình control
    private void setupControls() {
        tfFullName.setPromptText("Nguyễn Văn An");
        tfEmail.setPromptText("abc@example.com");
        tfUsername.setPromptText("3–20 ký tự: a-z, 0-9, _");
        pfPassword.setPromptText("≥ 8 ký tự, có hoa, thường, số, ký tự đặc biệt");
        pfConfirm.setPromptText("Nhập lại mật khẩu");

        rbMale.setToggleGroup(tgGender);
        rbFemale.setToggleGroup(tgGender);
        rbOther.setToggleGroup(tgGender);

        dpDob.setPromptText("Chọn ngày sinh");
        dpDob.setEditable(false);           // chỉ chọn từ lịch → tránh lỗi gõ sai định dạng
        dpDob.setMaxWidth(Double.MAX_VALUE);

        cbMajor.getItems().addAll("Kỹ thuật phần mềm", "Trí tuệ nhân tạo", "An toàn thông tin", "Thiết kế đồ họa");
        cbMajor.setPromptText("-- Chọn ngành --");
        cbMajor.setMaxWidth(Double.MAX_VALUE);

        pbStrength.setPrefWidth(140);

        // Nút Đăng ký chỉ bật khi đã tích "Đồng ý" — bind thay vì if/else trong handler
        btnRegister.disableProperty().bind(chkAgree.selectedProperty().not());
        btnRegister.setDefaultButton(true);
    }

    // TODO 3: Giao diện — GridPane 2 cột, mỗi ô nhập kèm label lỗi bên dưới
    private Parent buildRegisterView() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(6);
        int r = 0;
        addField(grid, r++, "Họ tên *", tfFullName, errFullName);
        addField(grid, r++, "Email *", tfEmail, errEmail);
        addField(grid, r++, "Username *", tfUsername, errUsername);
        HBox strength = new HBox(8, pbStrength, lblStrength);
        strength.setAlignment(Pos.CENTER_LEFT);
        addField(grid, r++, "Mật khẩu *", new VBox(4, pfPassword, strength), errPassword);
        addField(grid, r++, "Xác nhận MK *", pfConfirm, errConfirm);
        addField(grid, r++, "Giới tính *", new HBox(15, rbMale, rbFemale, rbOther), errGender);
        addField(grid, r++, "Ngày sinh *", dpDob, errDob);
        addField(grid, r++, "Ngành *", cbMajor, errMajor);
        addField(grid, r++, "Sở thích", new FlowPane(12, 6, chkSport, chkMusic, chkGame, chkReading), null);

        ColumnConstraints c0 = new ColumnConstraints(110);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c0, c1);

        Label title = new Label("ĐĂNG KÝ TÀI KHOẢN");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #1a73e8;");

        HBox actions = new HBox(10, btnRegister, btnReset);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Hyperlink linkLogin = new Hyperlink("Đã có tài khoản? Đăng nhập");
        linkLogin.setOnAction(e -> scene.setRoot(buildLoginView("")));

        VBox form = new VBox(12, title, grid, chkAgree, actions, linkLogin);
        form.setPadding(new Insets(20));

        ScrollPane sp = new ScrollPane(form);   // cửa sổ nhỏ vẫn cuộn được, không bị cắt
        sp.setFitToWidth(true);
        return sp;
    }

    private void addField(GridPane grid, int row, String label, Node input, Label error) {
        Label lbl = new Label(label);
        lbl.setPadding(new Insets(4, 0, 0, 0));
        GridPane.setValignment(lbl, VPos.TOP);  // nhãn canh theo dòng đầu (ô nhập), không canh giữa cả ô
        grid.add(lbl, 0, row);
        grid.add(error == null ? input : new VBox(2, input, error), 1, row);
    }

    private static Label err() {
        Label l = new Label();
        l.setStyle("-fx-text-fill: #d93025; -fx-font-size: 11;");
        l.setWrapText(true);
        return l;
    }

    // TODO 4: Đăng ký sự kiện
    private void registerEvents() {
        // 4a. Ô gõ chữ: validate khi RỜI KHỎI ô (mất focus) — không làm phiền khi đang gõ
        onFocusLost(tfFullName, this::vFullName);
        onFocusLost(tfEmail, this::vEmail);
        onFocusLost(tfUsername, this::vUsername);
        onFocusLost(pfPassword, this::vPassword);
        onFocusLost(pfConfirm, this::vConfirm);

        // 4b. Control dạng chọn: validate ngay khi giá trị đổi
        tgGender.selectedToggleProperty().addListener((obs, o, n) -> vGender());
        dpDob.valueProperty().addListener((obs, o, n) -> vDob());
        cbMajor.valueProperty().addListener((obs, o, n) -> vMajor());

        // 4c. Độ mạnh mật khẩu — cập nhật theo TỪNG PHÍM gõ
        pfPassword.textProperty().addListener((obs, o, n) -> updateStrength(n));

        // 4d. Nút
        btnRegister.setOnAction(e -> handleRegister());
        btnReset.setOnAction(e -> {
            if (AlertUtil.confirm("Xóa toàn bộ dữ liệu đã nhập?")) resetForm();
        });
    }

    private void onFocusLost(Node node, BooleanSupplier validator) {
        node.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) validator.getAsBoolean();
        });
    }

    // TODO 5: Validator — kiểm tra 1 field, ghi/xóa lỗi, trả về true nếu hợp lệ
    private boolean check(Label err, boolean ok, String msg) {
        err.setText(ok ? "" : msg);
        return ok;
    }

    private boolean vFullName() {
        // \p{L}: chữ cái mọi ngôn ngữ; \p{M}: dấu kết hợp (bộ gõ "Unicode tổ hợp")
        return check(errFullName, tfFullName.getText().trim().matches("[\\p{L}\\p{M} ]{3,50}"),
                "Họ tên 3–50 ký tự, chỉ gồm chữ cái và khoảng trắng");
    }

    private boolean vEmail() {
        return check(errEmail, tfEmail.getText().trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$"),
                "Email không hợp lệ (VD: abc@example.com)");
    }

    private boolean vUsername() {
        String u = tfUsername.getText().trim().toLowerCase();
        if (!u.matches("[a-z0-9_]{3,20}"))
            return check(errUsername, false, "Username 3–20 ký tự: chữ thường, số, dấu _");
        return check(errUsername, !UserStore.exists(u), "Username đã tồn tại");
    }

    private boolean vPassword() {
        return check(errPassword, pfPassword.getText().matches(STRONG_PASSWORD),
                "Mật khẩu ≥ 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt");
    }

    private boolean vConfirm() {
        String c = pfConfirm.getText();
        return check(errConfirm, !c.isEmpty() && c.equals(pfPassword.getText()), "Mật khẩu xác nhận không khớp");
    }

    private boolean vGender() {
        return check(errGender, tgGender.getSelectedToggle() != null, "Vui lòng chọn giới tính");
    }

    private boolean vDob() {
        LocalDate d = dpDob.getValue();
        if (d == null) return check(errDob, false, "Vui lòng chọn ngày sinh");
        if (d.isAfter(LocalDate.now())) return check(errDob, false, "Ngày sinh không được ở tương lai");
        return check(errDob, Period.between(d, LocalDate.now()).getYears() >= 16, "Phải đủ 16 tuổi trở lên");
    }

    private boolean vMajor() {
        return check(errMajor, cbMajor.getValue() != null, "Vui lòng chọn ngành");
    }

    // TODO 6: Độ mạnh mật khẩu (0–4 điểm)
    private void updateStrength(String s) {
        int score = 0;
        if (s.length() >= 8) score++;
        if (s.matches(".*[a-z].*") && s.matches(".*[A-Z].*")) score++;
        if (s.matches(".*\\d.*")) score++;
        if (s.matches(".*[^\\w\\s].*")) score++;

        pbStrength.setProgress(score / 4.0);
        pbStrength.setStyle("-fx-accent: " + LEVEL_COLOR[score] + ";");   // -fx-accent = màu thanh progress
        lblStrength.setText(s.isEmpty() ? "" : LEVEL_TEXT[score]);
        lblStrength.setStyle("-fx-text-fill: " + LEVEL_COLOR[score] + ";");
    }

    // TODO 7: Đăng ký — dùng & (KHÔNG dùng &&) để chạy HẾT validator → hiện mọi lỗi cùng lúc
    private void handleRegister() {
        boolean ok = vFullName() & vEmail() & vUsername() & vPassword()
                   & vConfirm() & vGender() & vDob() & vMajor();
        if (!ok) {
            AlertUtil.warn("Vui lòng sửa các ô đang báo lỗi (chữ đỏ).");
            return;
        }

        List<String> hobbies = Stream.of(chkSport, chkMusic, chkGame, chkReading)
                .filter(CheckBox::isSelected).map(CheckBox::getText).toList();
        User u = new User(
                tfFullName.getText().trim(),
                tfEmail.getText().trim(),
                tfUsername.getText().trim().toLowerCase(),
                pfPassword.getText(),
                ((RadioButton) tgGender.getSelectedToggle()).getText(),
                dpDob.getValue(),
                cbMajor.getValue(),
                hobbies);
        UserStore.add(u);

        AlertUtil.info("Đăng ký thành công!\nUsername: %s\nNgành: %s\nSở thích: %s".formatted(
                u.username(), u.major(), hobbies.isEmpty() ? "(không)" : String.join(", ", hobbies)));
        resetForm();
        scene.setRoot(buildLoginView(u.username()));    // sang đăng nhập, điền sẵn username
    }

    // TODO 8: Làm lại form
    private void resetForm() {
        Stream.of(tfFullName, tfEmail, tfUsername, pfPassword, pfConfirm).forEach(TextInputControl::clear);
        tgGender.selectToggle(null);
        dpDob.setValue(null);
        cbMajor.setValue(null);
        Stream.of(chkSport, chkMusic, chkGame, chkReading, chkAgree).forEach(c -> c.setSelected(false));
        // KHÔNG gọi btnRegister.setDisable(true): property đã bind → RuntimeException (Phần C.15).
        // Bỏ tích chkAgree là đủ, binding tự disable nút.

        // Xóa lỗi SAU CÙNG: các listener ở 4b vừa chạy khi reset giá trị và đã ghi lỗi "Vui lòng chọn..."
        Stream.of(errFullName, errEmail, errUsername, errPassword, errConfirm, errGender, errDob, errMajor)
                .forEach(l -> l.setText(""));
        tfFullName.requestFocus();
    }

    // TODO 9: Form đăng nhập — kiểm tra với UserStore
    private Parent buildLoginView(String prefillUser) {
        Label title = new Label("ĐĂNG NHẬP");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #1a73e8;");

        TextField tfUser = new TextField(prefillUser);
        tfUser.setPromptText("Username");
        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Password");
        Label lblMsg = new Label();
        lblMsg.setStyle("-fx-text-fill: #d93025;");

        Button btnLogin = new Button("Đăng nhập");
        btnLogin.setDefaultButton(true);
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        Hyperlink linkRegister = new Hyperlink("Chưa có tài khoản? Đăng ký");

        btnLogin.setOnAction(e -> {
            if (tfUser.getText().isBlank() || pfPass.getText().isEmpty()) {
                lblMsg.setText("Nhập đầy đủ username và password");
                return;
            }
            UserStore.login(tfUser.getText(), pfPass.getText()).ifPresentOrElse(
                    u -> {
                        lblMsg.setText("");
                        AlertUtil.info("Xin chào %s!\nNgành: %s".formatted(u.fullName(), u.major()));
                    },
                    () -> {
                        lblMsg.setText("Sai username hoặc mật khẩu");
                        pfPass.clear();
                    });
        });
        linkRegister.setOnAction(e -> scene.setRoot(registerView));   // quay lại form đăng ký (giữ dữ liệu đang nhập)

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Username"), tfUser);
        grid.addRow(1, new Label("Password"), pfPass);
        ColumnConstraints c0 = new ColumnConstraints(80);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c0, c1);

        VBox box = new VBox(12, title, grid, lblMsg, btnLogin, linkRegister);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(340);

        StackPane root = new StackPane(box);
        root.setPadding(new Insets(20));

        Node focus = prefillUser.isEmpty() ? tfUser : pfPass;
        Platform.runLater(focus::requestFocus);
        return root;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

> Import cho `User`/`UserStore`: `java.time.LocalDate`, `java.util.*`. Nhớ `exports com.fu.ch08.b3;` (đã có trong `module-info` mẫu). Không dùng FXML nên không cần `opens`.

### Validation cần kiểm tra
- Họ tên `[\p{L}\p{M} ]{3,50}` — gõ "Nguyễn Văn An" hợp lệ, "An123" không hợp lệ
- Email đúng định dạng; username `[a-z0-9_]{3,20}` và **chưa tồn tại** (thử đăng ký lại `admin`)
- Mật khẩu mạnh: `abc` → Rất yếu/Yếu, `Abcdefg1` → Khá, `Abcdefg1@` → Mạnh và hợp lệ
- Xác nhận mật khẩu trùng khớp
- Giới tính, ngày sinh (không tương lai, ≥ 16 tuổi), ngành là bắt buộc
- Chưa tích "Đồng ý điều khoản" → nút Đăng ký bị mờ, không bấm được

### Checklist hoàn thành
- [ ] Form dùng đủ: TextField, PasswordField, RadioButton, DatePicker, ComboBox, CheckBox, ProgressBar, Button, Hyperlink
- [ ] Lỗi hiện **ngay dưới ô** tương ứng, không dùng Alert cho từng lỗi
- [ ] Rời khỏi ô nhập sai → hiện lỗi; sửa đúng rồi rời ô → lỗi biến mất
- [ ] Bấm Đăng ký khi form trống → hiện **tất cả** lỗi cùng lúc (nhờ `&`)
- [ ] Thanh độ mạnh mật khẩu đổi màu + chữ theo từng phím gõ
- [ ] Nút Đăng ký chỉ bật khi tích "Đồng ý điều khoản" (binding)
- [ ] Làm lại → hỏi xác nhận; form trống, không còn chữ đỏ nào
- [ ] Đăng ký thành công → sang form đăng nhập, username điền sẵn, focus ở ô password
- [ ] Đăng nhập bằng tài khoản vừa tạo → "Xin chào {họ tên}"; sai → báo lỗi
- [ ] Đăng ký trùng username vừa tạo → báo "Username đã tồn tại"
- [ ] Từ đăng nhập bấm "Chưa có tài khoản?" → quay lại form đăng ký
- [ ] Cửa sổ thu nhỏ → form cuộn được (ScrollPane), không bị cắt

---

## B4 — FXML + Controller + CSS (Form Đăng Nhập)

### Mục tiêu
- Thiết kế giao diện bằng **Scene Builder**, lưu FXML đúng package
- Liên kết FXML ↔ Controller: `fx:controller`, `fx:id`, `onAction="#..."`, `initialize()`
- Lấy controller bằng `loader.getController()` để truyền dữ liệu
- Tách style ra file CSS, dùng `styleClass`, pseudo-class `:hover`, `:focused`

### Kiến thức (Chapter 08)
§4 JavaFX Parts · §5 Scene Builder · §12 FXML · §21 Demo step-by-step

### Mô tả
> Đây là phiên bản **FXML** của form đăng nhập ở B2. Làm xong hãy so sánh lượng code Java giữa 2 cách.

Form đăng nhập: Username, Password (có CheckBox "Hiện mật khẩu"), ComboBox vai trò (Sinh viên / Giảng viên — khai báo items **trong FXML**), nút Đăng nhập, Hyperlink "Quên mật khẩu?". Tài khoản hợp lệ cố định trong code. Đăng nhập thành công → mở **cửa sổ chào mừng** (FXML thứ 2) và truyền tên người dùng sang controller của cửa sổ đó.

| Username | Password | Vai trò |
|---|---|---|
| `student` | `Student@123` | Sinh viên |
| `teacher` | `Teacher@123` | Giảng viên |

### Step-by-step
**Bước 1:** Mở Scene Builder → tạo `GridPane` → kéo thả Label, TextField, PasswordField, CheckBox, ComboBox, Button, Hyperlink → đặt `fx:id` (tab *Code*) → lưu `login-view.fxml` vào `resources/com/fu/ch08/b4/`.
**Bước 2:** Trong Scene Builder, mục *Controller* → nhập `com.fu.ch08.b4.LoginController`; *View → Show Sample Controller Skeleton* để copy khung controller.
**Bước 3:** Tạo `login.css`, gắn qua `stylesheets="@login.css"` trong FXML.
**Bước 4:** Viết `LoginController`: `initialize()`, `handleLogin`, `handleForgot`, toggle hiện mật khẩu.
**Bước 5:** Tạo `welcome-view.fxml` + `WelcomeController` có method `setUser(String, String)`.
**Bước 6:** Tạo `LoginApp` load FXML; nhớ `opens com.fu.ch08.b4 to javafx.fxml;`.

### TODOs

```xml
<!-- TODO 1: resources/com/fu/ch08/b4/login-view.fxml -->
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.collections.FXCollections?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import java.lang.String?>

<GridPane xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
          fx:controller="com.fu.ch08.b4.LoginController"
          hgap="10" vgap="12" styleClass="login-pane" stylesheets="@login.css">
    <padding><Insets top="24" right="28" bottom="24" left="28"/></padding>
    <columnConstraints>
        <ColumnConstraints minWidth="90"/>
        <ColumnConstraints hgrow="ALWAYS" minWidth="200"/>
    </columnConstraints>

    <Label text="ĐĂNG NHẬP" styleClass="title" GridPane.columnSpan="2"/>

    <Label text="Username" GridPane.rowIndex="1"/>
    <TextField fx:id="tfUsername" promptText="student" GridPane.rowIndex="1" GridPane.columnIndex="1"/>

    <Label text="Password" GridPane.rowIndex="2"/>
    <StackPane GridPane.rowIndex="2" GridPane.columnIndex="1">
        <PasswordField fx:id="pfPassword"/>
        <TextField fx:id="tfPasswordVisible" visible="false" managed="false"/>
    </StackPane>
    <CheckBox fx:id="chkShow" text="Hiện mật khẩu" GridPane.rowIndex="3" GridPane.columnIndex="1"/>

    <Label text="Vai trò" GridPane.rowIndex="4"/>
    <ComboBox fx:id="cbRole" promptText="Chọn vai trò" maxWidth="Infinity"
              GridPane.rowIndex="4" GridPane.columnIndex="1">
        <items>
            <FXCollections fx:factory="observableArrayList">
                <String fx:value="Sinh viên"/>
                <String fx:value="Giảng viên"/>
            </FXCollections>
        </items>
    </ComboBox>

    <Label fx:id="lblError" styleClass="error" wrapText="true" GridPane.rowIndex="5" GridPane.columnSpan="2"/>

    <HBox spacing="12" alignment="CENTER_LEFT" GridPane.rowIndex="6" GridPane.columnIndex="1">
        <Button fx:id="btnLogin" text="Đăng nhập" styleClass="primary-btn"
                defaultButton="true" onAction="#handleLogin"/>
        <Hyperlink text="Quên mật khẩu?" onAction="#handleForgot"/>
    </HBox>
</GridPane>
```

```css
/* TODO 2: resources/com/fu/ch08/b4/login.css */
.login-pane { -fx-background-color: linear-gradient(to bottom, #ffffff, #e8f0fe); -fx-font-family: "Segoe UI"; }
.title      { -fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #1a73e8; }
.error      { -fx-text-fill: #d93025; }
.text-field:focused, .password-field:focused { -fx-border-color: #1a73e8; -fx-border-radius: 3; }
.primary-btn {
    -fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-weight: bold;
    -fx-padding: 8 18; -fx-background-radius: 5; -fx-cursor: hand;
}
.primary-btn:hover    { -fx-background-color: #1558b0; }
.primary-btn:disabled { -fx-opacity: 0.5; }
```

```java
package com.fu.ch08.b4;

public class LoginController {

    // TODO 3: @FXML field — tên trùng fx:id
    @FXML private TextField tfUsername, tfPasswordVisible;
    @FXML private PasswordField pfPassword;
    @FXML private CheckBox chkShow;
    @FXML private ComboBox<String> cbRole;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    private static final Map<String, String[]> ACCOUNTS = Map.of(
            "student", new String[]{"Student@123", "Sinh viên"},
            "teacher", new String[]{"Teacher@123", "Giảng viên"});
    private int failedCount = 0;

    // TODO 4: initialize() — chạy SAU khi các @FXML đã được inject (constructor thì chưa!)
    @FXML
    public void initialize() {
        // Hai ô password đồng bộ nội dung
        tfPasswordVisible.textProperty().bindBidirectional(pfPassword.textProperty());
        // Hiện/ẩn: visible + managed (managed=false để node không chiếm chỗ trong layout)
        tfPasswordVisible.visibleProperty().bind(chkShow.selectedProperty());
        tfPasswordVisible.managedProperty().bind(chkShow.selectedProperty());
        pfPassword.visibleProperty().bind(chkShow.selectedProperty().not());
        pfPassword.managedProperty().bind(chkShow.selectedProperty().not());

        btnLogin.disableProperty().bind(tfUsername.textProperty().isEmpty()
                .or(pfPassword.textProperty().isEmpty()).or(cbRole.valueProperty().isNull()));
        Platform.runLater(tfUsername::requestFocus);
    }

    // TODO 5: Xử lý đăng nhập
    @FXML
    private void handleLogin(ActionEvent e) {
        String user = tfUsername.getText().trim().toLowerCase();
        String pass = pfPassword.getText();
        String role = cbRole.getValue();

        if (failedCount >= 3) { lblError.setText("Sai quá 3 lần. Vui lòng khởi động lại ứng dụng."); return; }
        if (!user.matches("[a-z0-9_]{3,20}")) { lblError.setText("Username 3–20 ký tự, chỉ chữ thường/số/_"); return; }

        String[] acc = ACCOUNTS.get(user);
        if (acc == null || !acc[0].equals(pass)) {
            failedCount++;
            lblError.setText("Sai username hoặc mật khẩu (còn %d lần)".formatted(3 - failedCount));
            pfPassword.clear();
            return;
        }
        if (!acc[1].equals(role)) { lblError.setText("Vai trò không đúng với tài khoản"); return; }

        lblError.setText("");
        openWelcome(user, role, (Stage) btnLogin.getScene().getWindow());
    }

    // TODO 6: Mở FXML thứ 2 và TRUYỀN DỮ LIỆU qua controller
    private void openWelcome(String user, String role, Stage loginStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("welcome-view.fxml"));
            Parent root = loader.load();
            WelcomeController ctrl = loader.getController();
            ctrl.setUser(user, role);

            Stage stage = new Stage();
            stage.setTitle("Chào mừng");
            stage.setScene(new Scene(root, 360, 200));
            stage.show();
            loginStage.close();
        } catch (IOException ex) {
            AlertUtil.error("Không mở được màn hình chào mừng: " + ex.getMessage());
        }
    }

    @FXML
    private void handleForgot() {
        TextInputDialog d = new TextInputDialog();
        d.setHeaderText("Nhập email để lấy lại mật khẩu");
        d.showAndWait().ifPresent(email -> {
            if (email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) AlertUtil.info("Đã gửi hướng dẫn tới " + email);
            else AlertUtil.warn("Email không hợp lệ");
        });
    }
}
```

```java
// TODO 7: Controller màn hình 2
public class WelcomeController {
    @FXML private Label lblWelcome;
    public void setUser(String user, String role) {
        lblWelcome.setText("Xin chào %s (%s)!".formatted(user, role));
    }
    @FXML private void handleLogout(ActionEvent e) { ((Stage) lblWelcome.getScene().getWindow()).close(); }
}
// welcome-view.fxml: VBox với <Label fx:id="lblWelcome"/> và <Button text="Đăng xuất" onAction="#handleLogout"/>

// TODO 8: Application
public class LoginApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Đường dẫn TƯƠNG ĐỐI → tìm trong resources/com/fu/ch08/b4/
        FXMLLoader loader = new FXMLLoader(LoginApp.class.getResource("login-view.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("B4 - Đăng nhập");
        stage.setResizable(false);
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}
```

### Validation cần kiểm tra
- Username 3–20 ký tự `[a-z0-9_]`; password và vai trò bắt buộc (nút disable khi thiếu)
- Sai tài khoản → báo lỗi, xóa password, đếm số lần; sai 3 lần → khóa
- Vai trò phải khớp tài khoản; email quên mật khẩu đúng định dạng

### Checklist hoàn thành
- [ ] FXML được tạo/chỉnh bằng Scene Builder và nằm đúng thư mục package
- [ ] Không có control nào tạo bằng `new` trong `LoginController`
- [ ] ComboBox items khai báo trong FXML (`FXCollections fx:factory`)
- [ ] CSS ở file riêng, `:hover` của nút hoạt động
- [ ] Tích "Hiện mật khẩu" → thấy mật khẩu, bỏ tích → ẩn; nội dung 2 ô luôn giống nhau
- [ ] Nút Đăng nhập disable khi thiếu dữ liệu; Enter = bấm Đăng nhập (`defaultButton`)
- [ ] Đăng nhập đúng → mở cửa sổ chào mừng có tên người dùng, cửa sổ login đóng
- [ ] Hiểu vì sao code setup đặt trong `initialize()` chứ không đặt trong constructor
- [ ] Đổi layout trong FXML (VD đổi màu, vị trí) không cần sửa Java

---

## B5 — Layout Components

### Mục tiêu
- Kết hợp các layout: `BorderPane`, `VBox`, `HBox`, `GridPane`, `FlowPane`, `TilePane`, `StackPane`, `AnchorPane`, `SplitPane`, `ScrollPane`, `TabPane`, `Accordion`
- Hiểu `Priority` (grow), `alignment`, `padding`, `spacing`, `managed`
- Quan sát hành vi resize của từng layout

### Kiến thức (Chapter 08)
§13 Layout Components

### Mô tả
Ứng dụng "Layout Gallery" dạng khung ứng dụng thật (`BorderPane`, viết bằng FXML):
- **top:** `MenuBar` (File → Thoát; View → Ẩn/hiện sidebar phải)
- **left:** `Accordion` gồm 2 `TitledPane` "Cơ bản" và "Nâng cao", mỗi pane chứa các nút điều hướng
- **center:** `StackPane` — đổi nội dung theo nút được chọn: demo `FlowPane`, `TilePane`, `GridPane`, `AnchorPane`, `SplitPane`, `ScrollPane`
- **right:** `VBox` sidebar hiển thị mô tả layout đang xem
- **bottom:** `HBox` status bar: tên layout + kích thước center (cập nhật khi resize)

### Step-by-step
**Bước 1:** Thiết kế `main-layout.fxml` (BorderPane) trong Scene Builder.
**Bước 2:** Viết `LayoutController` với method `buildFlow()`, `buildTile()`, `buildGrid()`, `buildAnchor()`, `buildSplit()`, `buildScroll()` trả về `Node`.
**Bước 3:** Mỗi nút điều hướng gọi `show(name, node, description)` → `centerPane.getChildren().setAll(node)`.
**Bước 4:** Status bar bind với `widthProperty()`/`heightProperty()` của center.
**Bước 5:** Menu View → toggle sidebar phải bằng `CheckMenuItem` (bind `visible` + `managed`).

### TODOs

```xml
<!-- TODO 1: resources/com/fu/ch08/b5/main-layout.fxml -->
<BorderPane xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="com.fu.ch08.b5.LayoutController" prefWidth="1000" prefHeight="640">
    <top>
        <MenuBar>
            <Menu text="File"><MenuItem text="Thoát" onAction="#handleExit" accelerator="Shortcut+Q"/></Menu>
            <Menu text="View"><CheckMenuItem fx:id="miSidebar" text="Hiện sidebar" selected="true"/></Menu>
        </MenuBar>
    </top>
    <left>
        <Accordion fx:id="accordion" prefWidth="180">
            <panes>
                <TitledPane fx:id="tpBasic" text="Cơ bản">
                    <VBox spacing="6">
                        <Button text="FlowPane" maxWidth="Infinity" onAction="#showFlow"/>
                        <Button text="TilePane" maxWidth="Infinity" onAction="#showTile"/>
                        <Button text="GridPane" maxWidth="Infinity" onAction="#showGrid"/>
                    </VBox>
                </TitledPane>
                <TitledPane text="Nâng cao">
                    <VBox spacing="6">
                        <Button text="AnchorPane" maxWidth="Infinity" onAction="#showAnchor"/>
                        <Button text="SplitPane" maxWidth="Infinity" onAction="#showSplit"/>
                        <Button text="ScrollPane" maxWidth="Infinity" onAction="#showScroll"/>
                    </VBox>
                </TitledPane>
            </panes>
        </Accordion>
    </left>
    <center>
        <StackPane fx:id="centerPane" style="-fx-padding: 10; -fx-background-color: #fafafa;"/>
    </center>
    <right>
        <VBox fx:id="sidebar" prefWidth="220" spacing="8" style="-fx-padding: 10; -fx-background-color: #eef3fb;">
            <Label text="Mô tả" style="-fx-font-weight: bold;"/>
            <Label fx:id="lblDescription" wrapText="true"/>
        </VBox>
    </right>
    <bottom>
        <HBox spacing="20" style="-fx-padding: 4 10; -fx-background-color: #ddd;">
            <Label fx:id="lblStatus"/>
            <Label fx:id="lblSize"/>
        </HBox>
    </bottom>
</BorderPane>
```

```java
package com.fu.ch08.b5;

public class LayoutController {
    @FXML private StackPane centerPane;
    @FXML private VBox sidebar;
    @FXML private Label lblDescription, lblStatus, lblSize;
    @FXML private CheckMenuItem miSidebar;
    @FXML private Accordion accordion;
    @FXML private TitledPane tpBasic;

    @FXML
    public void initialize() {
        // TODO 2: Toggle sidebar — managed=false để center chiếm luôn phần trống
        sidebar.visibleProperty().bind(miSidebar.selectedProperty());
        sidebar.managedProperty().bind(miSidebar.selectedProperty());

        // TODO 3: Status bar cập nhật khi resize
        lblSize.textProperty().bind(Bindings.format("Center: %.0f x %.0f",
                centerPane.widthProperty(), centerPane.heightProperty()));

        accordion.setExpandedPane(tpBasic);
        showFlow();
    }

    private void show(String name, Node content, String desc) {
        centerPane.getChildren().setAll(content);   // setAll: node cũ bị gỡ khỏi scene graph
        lblStatus.setText("Đang xem: " + name);
        lblDescription.setText(desc);
    }

    // TODO 4: FlowPane — tự xuống dòng; thử resize cửa sổ
    @FXML private void showFlow() {
        FlowPane fp = new FlowPane(8, 8);
        for (int i = 1; i <= 30; i++) fp.getChildren().add(box("Item " + i, 60 + (i % 4) * 20, 40));
        show("FlowPane", fp, "Xếp node theo hàng, hết chiều rộng thì xuống dòng. Kích thước node khác nhau.");
    }

    // TODO 5: TilePane — mọi ô có kích thước bằng nhau
    @FXML private void showTile() {
        TilePane tp = new TilePane(8, 8);
        tp.setPrefColumns(5);
        for (int i = 1; i <= 20; i++) tp.getChildren().add(box("Tile " + i, 60 + (i % 4) * 20, 40));
        show("TilePane", tp, "Giống FlowPane nhưng mọi ô bằng nhau (theo node lớn nhất).");
    }

    // TODO 6: GridPane — form 2 cột, cột 2 giãn theo chiều ngang, colspan/rowspan
    @FXML private void showGrid() {
        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10);
        gp.addRow(0, new Label("Họ tên"), new TextField());
        gp.addRow(1, new Label("Email"), new TextField());
        TextArea ta = new TextArea(); ta.setPrefRowCount(4);
        gp.add(new Label("Ghi chú"), 0, 2);
        gp.add(ta, 1, 2, 1, 2);                                    // rowspan = 2
        Button btn = new Button("Lưu"); btn.setMaxWidth(Double.MAX_VALUE);
        gp.add(btn, 0, 4, 2, 1);                                   // colspan = 2
        ColumnConstraints c1 = new ColumnConstraints(80), c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        gp.getColumnConstraints().addAll(c1, c2);
        gp.setGridLinesVisible(true);                              // debug: hiện đường lưới
        show("GridPane", gp, "Lưới hàng/cột; dùng ColumnConstraints để cột giãn. Bật gridLinesVisible để debug.");
    }

    // TODO 7: AnchorPane — neo theo cạnh
    @FXML private void showAnchor() {
        Button tl = new Button("Top-Left"), br = new Button("Bottom-Right");
        TextArea stretch = new TextArea("Neo cả 4 cạnh cách 50px → giãn theo cửa sổ");
        AnchorPane.setTopAnchor(tl, 10.0); AnchorPane.setLeftAnchor(tl, 10.0);
        AnchorPane.setBottomAnchor(br, 10.0); AnchorPane.setRightAnchor(br, 10.0);
        for (var side : List.<BiConsumer<Node, Double>>of(AnchorPane::setTopAnchor, AnchorPane::setBottomAnchor,
                AnchorPane::setLeftAnchor, AnchorPane::setRightAnchor)) side.accept(stretch, 50.0);
        show("AnchorPane", new AnchorPane(stretch, tl, br), "Neo node theo khoảng cách tới các cạnh.");
    }

    // TODO 8: SplitPane — kéo thanh chia
    @FXML private void showSplit() {
        ListView<String> list = new ListView<>(FXCollections.observableArrayList("Một", "Hai", "Ba"));
        Label detail = new Label("Chọn một mục");
        list.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> detail.setText("Đã chọn: " + b));
        SplitPane sp = new SplitPane(list, new StackPane(detail));
        sp.setDividerPositions(0.3);
        show("SplitPane", sp, "Chia vùng có thể kéo thay đổi kích thước (master-detail).");
    }

    // TODO 9: ScrollPane — nội dung lớn hơn vùng hiển thị
    @FXML private void showScroll() {
        VBox content = new VBox(5);
        for (int i = 1; i <= 100; i++) content.getChildren().add(new Label("Dòng " + i));
        ScrollPane sc = new ScrollPane(content);
        sc.setFitToWidth(true);
        show("ScrollPane", sc, "Bọc nội dung lớn, tự hiện thanh cuộn. fitToWidth để nội dung giãn ngang.");
    }

    @FXML private void handleExit() { Platform.exit(); }

    private static Label box(String text, double w, double h) {
        Label l = new Label(text);
        l.setPrefSize(w, h); l.setAlignment(Pos.CENTER);
        l.setStyle("-fx-background-color: hsb(%d, 40%%, 95%%); -fx-border-color: #888;".formatted((int) (w * 3) % 360));
        return l;
    }
}
```

### Validation / quan sát cần kiểm tra
- Resize cửa sổ: FlowPane xuống dòng; GridPane cột 2 giãn; AnchorPane TextArea giãn; ScrollPane hiện thanh cuộn
- Ẩn sidebar: center giãn ra chiếm chỗ (nhờ `managed=false`)

### Checklist hoàn thành
- [ ] Khung ứng dụng đủ 5 vùng BorderPane, viết bằng FXML
- [ ] Accordion 2 TitledPane, mở sẵn pane "Cơ bản"
- [ ] Hiển thị được đủ 6 layout demo trong center
- [ ] Status bar hiển thị kích thước center, đổi khi resize
- [ ] Menu View ẩn/hiện sidebar; File → Thoát và phím tắt Ctrl+Q hoạt động
- [ ] Giải thích được khác biệt FlowPane ↔ TilePane, `visible` ↔ `managed`
- [ ] GridPane có colspan và rowspan

---

## B6 — Controls: Phiếu Khảo Sát Sinh Viên

### Mục tiêu
- Sử dụng đầy đủ nhóm control phổ biến trong 1 form FXML
- Đọc giá trị từ từng loại control; reset form
- `ListView` thêm/xóa phần tử; `ContextMenu`; `Tooltip`

### Kiến thức (Chapter 08)
§14 JavaFX Controls

### Mô tả
Form "Khảo sát môn học HSF302":

| Field | Control | Ràng buộc |
|---|---|---|
| MSSV | `TextField` | Bắt buộc, dạng `SE` + 6 số (VD `SE123456`) |
| Họ tên | `TextField` | Bắt buộc, ≥ 3 ký tự |
| Ngành | `ChoiceBox<String>` | SE, AI, IA, GD — bắt buộc |
| Kỳ học | `Spinner<Integer>` | 1–9 |
| Giới tính | `RadioButton` × 3 + `ToggleGroup` | Bắt buộc |
| Ngày khảo sát | `DatePicker` | Không ở tương lai |
| Mức hài lòng | `Slider` 1–5 (snapToTicks) | Label hiển thị "Rất hài lòng"... |
| Phần thích nhất | `CheckBox` × 4 (Spring, JPA, JavaFX, Testing) | Chọn ≥ 1 |
| Màu giao diện yêu thích | `ColorPicker` | — |
| Ngôn ngữ đã biết | `ComboBox` editable + nút Thêm → `ListView` | Không trùng, không rỗng |
| Góp ý | `TextArea` | Tối đa 300 ký tự, hiện bộ đếm |
| — | `ProgressBar` | Tỷ lệ field bắt buộc đã điền |
| — | `Button` Gửi / Làm lại, `Hyperlink` "Xem đề cương" | — |

Bấm **Gửi** → `Alert` hiển thị toàn bộ câu trả lời.

### Step-by-step
**Bước 1:** Thiết kế `survey-view.fxml` (`ScrollPane` → `GridPane`) bằng Scene Builder. `fx:id` phải trùng tên các field trong `SurveyController` bên dưới; khai báo `<fx:define><ToggleGroup fx:id="tgGender"/></fx:define>` và gán `toggleGroup="$tgGender"` cho 3 RadioButton; các nút gọi `#handleAddLanguage`, `#handleSubmit`, `#handleReset`, Hyperlink gọi `#handleSyllabus`.
**Bước 2:** `initialize()`: nạp items cho ChoiceBox/ComboBox; cấu hình Spinner, Slider (`majorTickUnit=1`, `snapToTicks=true`), DatePicker chặn ngày tương lai.
**Bước 3:** ListView: nút Thêm lấy giá trị ComboBox; `ContextMenu` "Xóa"; phím Delete.
**Bước 4:** `TextFormatter` giới hạn 300 ký tự cho TextArea; label đếm ký tự bind.
**Bước 5:** ProgressBar hoàn thành: `Bindings.createDoubleBinding` theo các field bắt buộc.
**Bước 6:** `handleSubmit()` validate → hiện kết quả; `handleReset()`.

### TODOs

```java
package com.fu.ch08.b6;

public class SurveyController {
    @FXML private TextField tfStudentId, tfFullName;
    @FXML private ChoiceBox<String> cbMajor;
    @FXML private Spinner<Integer> spSemester;
    @FXML private ToggleGroup tgGender;
    @FXML private DatePicker dpDate;
    @FXML private Slider slSatisfaction;
    @FXML private Label lblSatisfaction, lblCharCount;
    @FXML private CheckBox chkSpring, chkJpa, chkFx, chkTesting;
    @FXML private ColorPicker cpColor;
    @FXML private ComboBox<String> cbLanguage;
    @FXML private ListView<String> lvLanguages;
    @FXML private TextArea taComment;
    @FXML private ProgressBar pbComplete;

    private static final String[] LEVELS = {"", "Rất không hài lòng", "Không hài lòng", "Bình thường", "Hài lòng", "Rất hài lòng"};

    @FXML
    public void initialize() {
        // TODO 1: Items & cấu hình
        cbMajor.getItems().addAll("SE", "AI", "IA", "GD");
        spSemester.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9, 5));
        cbLanguage.getItems().addAll("Java", "C#", "Python", "JavaScript", "Kotlin");
        cbLanguage.setEditable(true);

        dpDate.setValue(LocalDate.now());
        dpDate.setDayCellFactory(p -> new DateCell() {
            @Override public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                setDisable(empty || d.isAfter(LocalDate.now()));
            }
        });

        // TODO 2: Slider 1..5 + label mô tả
        slSatisfaction.setMin(1); slSatisfaction.setMax(5); slSatisfaction.setValue(3);
        slSatisfaction.setMajorTickUnit(1); slSatisfaction.setMinorTickCount(0);
        slSatisfaction.setSnapToTicks(true); slSatisfaction.setShowTickLabels(true);
        lblSatisfaction.textProperty().bind(Bindings.createStringBinding(
                () -> LEVELS[(int) Math.round(slSatisfaction.getValue())], slSatisfaction.valueProperty()));

        // TODO 3: TextArea tối đa 300 ký tự + bộ đếm
        taComment.setTextFormatter(new TextFormatter<String>(c -> c.getControlNewText().length() <= 300 ? c : null));
        lblCharCount.textProperty().bind(taComment.textProperty().length().asString("%d/300"));

        // TODO 4: ListView — ContextMenu + phím Delete
        MenuItem miDelete = new MenuItem("Xóa");
        miDelete.setOnAction(e -> removeSelectedLanguage());
        lvLanguages.setContextMenu(new ContextMenu(miDelete));
        lvLanguages.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.DELETE) removeSelectedLanguage(); });
        lvLanguages.setPlaceholder(new Label("Chưa có ngôn ngữ nào"));

        // TODO 5: Tooltip
        tfStudentId.setTooltip(new Tooltip("Định dạng: SE + 6 chữ số, VD SE123456"));

        // TODO 6: Progress hoàn thành (6 nhóm bắt buộc)
        pbComplete.progressProperty().bind(Bindings.createDoubleBinding(() -> {
            int done = 0;
            if (!tfStudentId.getText().isBlank()) done++;
            if (!tfFullName.getText().isBlank()) done++;
            if (cbMajor.getValue() != null) done++;
            if (tgGender.getSelectedToggle() != null) done++;
            if (dpDate.getValue() != null) done++;
            if (chkSpring.isSelected() || chkJpa.isSelected() || chkFx.isSelected() || chkTesting.isSelected()) done++;
            return done / 6.0;
        }, tfStudentId.textProperty(), tfFullName.textProperty(), cbMajor.valueProperty(),
           tgGender.selectedToggleProperty(), dpDate.valueProperty(),
           chkSpring.selectedProperty(), chkJpa.selectedProperty(), chkFx.selectedProperty(), chkTesting.selectedProperty()));
    }

    // TODO 7: Thêm ngôn ngữ — editable ComboBox: đọc từ editor nếu người dùng gõ
    @FXML
    private void handleAddLanguage() {
        String lang = cbLanguage.getEditor().getText().trim();
        if (lang.isEmpty()) { AlertUtil.warn("Nhập hoặc chọn ngôn ngữ"); return; }
        boolean dup = lvLanguages.getItems().stream().anyMatch(l -> l.equalsIgnoreCase(lang));
        if (dup) { AlertUtil.warn("Ngôn ngữ đã có trong danh sách"); return; }
        lvLanguages.getItems().add(lang);
        cbLanguage.getEditor().clear();
        cbLanguage.setValue(null);
    }

    private void removeSelectedLanguage() {
        String sel = lvLanguages.getSelectionModel().getSelectedItem();
        if (sel != null) lvLanguages.getItems().remove(sel);
    }

    // TODO 8: Gửi
    @FXML
    private void handleSubmit() {
        List<String> errors = new ArrayList<>();
        if (!tfStudentId.getText().trim().toUpperCase().matches("SE\\d{6}")) errors.add("MSSV phải có dạng SE + 6 số");
        if (tfFullName.getText().trim().length() < 3) errors.add("Họ tên ≥ 3 ký tự");
        if (cbMajor.getValue() == null) errors.add("Chọn ngành");
        if (tgGender.getSelectedToggle() == null) errors.add("Chọn giới tính");
        if (dpDate.getValue() == null || dpDate.getValue().isAfter(LocalDate.now())) errors.add("Ngày khảo sát không hợp lệ");
        List<String> liked = Stream.of(chkSpring, chkJpa, chkFx, chkTesting)
                .filter(CheckBox::isSelected).map(CheckBox::getText).toList();
        if (liked.isEmpty()) errors.add("Chọn ít nhất 1 phần thích nhất");

        if (!errors.isEmpty()) { AlertUtil.warn("Vui lòng sửa:\n• " + String.join("\n• ", errors)); return; }

        String summary = """
                MSSV: %s | Họ tên: %s
                Ngành: %s | Kỳ: %d | Giới tính: %s
                Ngày: %s
                Hài lòng: %s
                Thích nhất: %s
                Màu yêu thích: %s
                Ngôn ngữ: %s
                Góp ý: %s""".formatted(
                tfStudentId.getText().trim().toUpperCase(), tfFullName.getText().trim(),
                cbMajor.getValue(), spSemester.getValue(), ((RadioButton) tgGender.getSelectedToggle()).getText(),
                dpDate.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                lblSatisfaction.getText(), String.join(", ", liked), cpColor.getValue(),
                lvLanguages.getItems().isEmpty() ? "(không)" : String.join(", ", lvLanguages.getItems()),
                taComment.getText().isBlank() ? "(không)" : taComment.getText());
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("Cảm ơn bạn đã gửi khảo sát!");
        a.getDialogPane().setContent(new TextArea(summary) {{ setEditable(false); setWrapText(true); }});
        a.showAndWait();
    }

    @FXML
    private void handleReset() {
        tfStudentId.clear(); tfFullName.clear(); cbMajor.setValue(null);
        spSemester.getValueFactory().setValue(5); tgGender.selectToggle(null);
        dpDate.setValue(LocalDate.now()); slSatisfaction.setValue(3);
        Stream.of(chkSpring, chkJpa, chkFx, chkTesting).forEach(c -> c.setSelected(false));
        cpColor.setValue(Color.WHITE); lvLanguages.getItems().clear(); taComment.clear();
    }

    @FXML
    private void handleSyllabus() {
        AlertUtil.info("Đề cương HSF302: Spring Core, Spring Data JPA, JavaFX...");
        // Mở trình duyệt: getHostServices().showDocument(url) — HostServices lấy từ Application
    }
}
```

### Validation cần kiểm tra
- MSSV đúng `SE\d{6}` (không phân biệt hoa thường); họ tên ≥ 3 ký tự
- Bắt buộc: ngành, giới tính, ngày (không tương lai), ≥ 1 checkbox
- Ngôn ngữ thêm vào không rỗng, không trùng (không phân biệt hoa thường)
- Góp ý ≤ 300 ký tự (không gõ thêm được)

### Checklist hoàn thành
- [ ] Form dùng đủ: TextField, ChoiceBox, Spinner, RadioButton, DatePicker, Slider, CheckBox, ColorPicker, ComboBox, ListView, TextArea, ProgressBar, Button, Hyperlink
- [ ] Slider chỉ dừng ở 1–5, label mô tả đổi theo
- [ ] DatePicker không chọn được ngày tương lai
- [ ] Thêm/xóa ngôn ngữ (nút, chuột phải, phím Delete); chặn trùng
- [ ] Bộ đếm ký tự góp ý, không gõ quá 300
- [ ] ProgressBar tăng dần khi điền field bắt buộc
- [ ] Gửi khi thiếu → liệt kê **tất cả** lỗi; gửi đủ → hiện tóm tắt
- [ ] Làm lại → form về trạng thái ban đầu
- [ ] Form dài đặt trong ScrollPane, không bị cắt khi cửa sổ nhỏ

---

## Phần B — Bài tập trung bình

---

## T1 — Quản Lý Sinh Viên: TreeView + TableView + Menu + FileChooser

### Mục tiêu
- Kết hợp `TreeView` (cây Khoa → Lớp) và `TableView` (danh sách sinh viên của lớp)
- Model JavaBean dùng JavaFX Property; `ObservableList` → `FilteredList` → `SortedList`
- `MenuBar`, `ToolBar`, `Accelerator`; CRUD với form; **Import/Export CSV** bằng `FileChooser`
- Cấu hình `module-info` cho `PropertyValueFactory` (`opens ... to javafx.base`)

### Kiến thức (Chapter 08)
§11 Properties · §12 FXML · §13 BorderPane/SplitPane · §14 TreeView, TableView, Menu, ToolBar · §23 FileChooser

### Mô tả
- **left:** `TreeView<String>` gốc "FPT University" → Khoa "Kỹ thuật phần mềm", "Trí tuệ nhân tạo" → các Lớp (SE1801, SE1802, AI1801…). Chọn **Khoa** → hiện SV toàn khoa; chọn **Lớp** → SV của lớp; chọn gốc → tất cả.
- **center:** ô tìm kiếm + `TableView` (MSSV, Họ tên, Lớp, GPA, Xếp loại) + label "x sinh viên".
- **bottom:** form MSSV / Họ tên / Lớp (ComboBox) / GPA + nút Thêm, Cập nhật, Xóa, Làm mới.
- **top:** `MenuBar` File (Import CSV `Ctrl+O`, Export CSV `Ctrl+S`, Thoát) · Edit (Xóa `Delete`) và `ToolBar`.

### Step-by-step
**Bước 1:** Tạo `Student` (Property + getter/setter/xxxProperty), `grade` là **computed** từ `gpa`.
**Bước 2:** Thiết kế `student-view.fxml`: `BorderPane` → top `VBox(MenuBar, ToolBar)`, center `SplitPane(TreeView, VBox(search, table))`, bottom form `GridPane`.
**Bước 3:** `initialize()`: dựng cây, cấu hình cột `PropertyValueFactory`, chuỗi `FilteredList`/`SortedList`.
**Bước 4:** Predicate kết hợp **node cây đang chọn + từ khóa tìm kiếm**.
**Bước 5:** CRUD: click dòng → điền form; validate; MSSV không trùng; xóa có xác nhận.
**Bước 6:** Import CSV (bỏ qua dòng lỗi, báo số dòng lỗi), Export CSV (UTF-8).

### TODOs

```java
package com.fu.ch08.t1;

// TODO 1: Model — PropertyValueFactory("studentId") sẽ tìm studentIdProperty() hoặc getStudentId()
public class Student {
    private final StringProperty studentId = new SimpleStringProperty();
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty className = new SimpleStringProperty();
    private final DoubleProperty gpa = new SimpleDoubleProperty();
    private final ReadOnlyStringWrapper grade = new ReadOnlyStringWrapper();

    public Student(String id, String name, String cls, double gpa) {
        setStudentId(id); setFullName(name); setClassName(cls); setGpa(gpa);
        grade.bind(Bindings.createStringBinding(() -> classify(this.gpa.get()), this.gpa));
    }

    public static String classify(double g) {
        if (g >= 9) return "Xuất sắc";
        if (g >= 8) return "Giỏi";
        if (g >= 6.5) return "Khá";
        if (g >= 5) return "Trung bình";
        return "Yếu";
    }

    public String getStudentId() { return studentId.get(); }  public void setStudentId(String v) { studentId.set(v); }
    public StringProperty studentIdProperty() { return studentId; }
    public String getFullName() { return fullName.get(); }    public void setFullName(String v) { fullName.set(v); }
    public StringProperty fullNameProperty() { return fullName; }
    public String getClassName() { return className.get(); }  public void setClassName(String v) { className.set(v); }
    public StringProperty classNameProperty() { return className; }
    public double getGpa() { return gpa.get(); }              public void setGpa(double v) { gpa.set(v); }
    public DoubleProperty gpaProperty() { return gpa; }
    public String getGrade() { return grade.get(); }
    public ReadOnlyStringProperty gradeProperty() { return grade.getReadOnlyProperty(); }
}
```

```xml
<!-- TODO 2: student-view.fxml (rút gọn — thiết kế bằng Scene Builder) -->
<BorderPane xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="com.fu.ch08.t1.StudentController" prefWidth="1000" prefHeight="620">
    <top>
        <VBox>
            <MenuBar>
                <Menu text="File">
                    <MenuItem text="Import CSV..." accelerator="Shortcut+O" onAction="#handleImport"/>
                    <MenuItem text="Export CSV..." accelerator="Shortcut+S" onAction="#handleExport"/>
                    <SeparatorMenuItem/>
                    <MenuItem text="Thoát" onAction="#handleExit"/>
                </Menu>
                <Menu text="Edit">
                    <MenuItem text="Xóa sinh viên" accelerator="Delete" onAction="#handleDelete"/>
                </Menu>
            </MenuBar>
            <ToolBar>
                <Button text="➕ Thêm" onAction="#handleAdd"/>
                <Button fx:id="btnUpdate" text="✏ Cập nhật" onAction="#handleUpdate"/>
                <Button fx:id="btnDelete" text="🗑 Xóa" onAction="#handleDelete"/>
                <Separator/>
                <Button text="📂 Import" onAction="#handleImport"/>
                <Button text="💾 Export" onAction="#handleExport"/>
            </ToolBar>
        </VBox>
    </top>
    <center>
        <SplitPane dividerPositions="0.25">
            <TreeView fx:id="tvFaculty"/>
            <VBox spacing="6" style="-fx-padding: 8;">
                <TextField fx:id="tfSearch" promptText="Tìm theo MSSV hoặc họ tên..."/>
                <TableView fx:id="tblStudents" VBox.vgrow="ALWAYS">
                    <columns>
                        <TableColumn fx:id="colId" text="MSSV" prefWidth="100"/>
                        <TableColumn fx:id="colName" text="Họ tên" prefWidth="220"/>
                        <TableColumn fx:id="colClass" text="Lớp" prefWidth="90"/>
                        <TableColumn fx:id="colGpa" text="GPA" prefWidth="70"/>
                        <TableColumn fx:id="colGrade" text="Xếp loại" prefWidth="100"/>
                    </columns>
                </TableView>
                <Label fx:id="lblCount"/>
            </VBox>
        </SplitPane>
    </center>
    <bottom>
        <GridPane hgap="10" vgap="8" style="-fx-padding: 10;">
            <Label text="MSSV"/>    <TextField fx:id="tfId" GridPane.columnIndex="1"/>
            <Label text="Họ tên" GridPane.columnIndex="2"/> <TextField fx:id="tfName" GridPane.columnIndex="3"/>
            <Label text="Lớp" GridPane.rowIndex="1"/> <ComboBox fx:id="cbClass" GridPane.rowIndex="1" GridPane.columnIndex="1"/>
            <Label text="GPA" GridPane.rowIndex="1" GridPane.columnIndex="2"/>
            <TextField fx:id="tfGpa" promptText="0 - 10" GridPane.rowIndex="1" GridPane.columnIndex="3"/>
            <Button text="Làm mới" onAction="#handleClear" GridPane.rowIndex="1" GridPane.columnIndex="4"/>
        </GridPane>
    </bottom>
</BorderPane>
```

```java
public class StudentController {
    @FXML private TreeView<String> tvFaculty;
    @FXML private TextField tfSearch, tfId, tfName, tfGpa;
    @FXML private TableView<Student> tblStudents;
    @FXML private TableColumn<Student, String> colId, colName, colClass, colGrade;
    @FXML private TableColumn<Student, Double> colGpa;
    @FXML private ComboBox<String> cbClass;
    @FXML private Label lblCount;
    @FXML private Button btnUpdate, btnDelete;

    // Khoa → danh sách lớp
    private static final Map<String, List<String>> FACULTIES = new LinkedHashMap<>();
    static {
        FACULTIES.put("Kỹ thuật phần mềm", List.of("SE1801", "SE1802", "SE1803"));
        FACULTIES.put("Trí tuệ nhân tạo", List.of("AI1801", "AI1802"));
    }

    // Extractor: sửa lớp/tên của 1 SV cũng làm FilteredList lọc lại
    private final ObservableList<Student> students = FXCollections.observableArrayList(
            s -> new Observable[]{s.studentIdProperty(), s.fullNameProperty(), s.classNameProperty()});
    { students.addAll(
            new Student("SE180001", "Nguyễn Văn An", "SE1801", 8.2),
            new Student("SE180002", "Trần Thị Bình", "SE1801", 9.1),
            new Student("SE180010", "Lê Văn Cường", "SE1802", 6.4),
            new Student("AI180003", "Phạm Thu Dung", "AI1801", 7.5)); }
    private final FilteredList<Student> filtered = new FilteredList<>(students);

    @FXML
    public void initialize() {
        // TODO 3: Dựng cây
        TreeItem<String> root = new TreeItem<>("FPT University");
        FACULTIES.forEach((fac, classes) -> {
            TreeItem<String> facItem = new TreeItem<>(fac);
            classes.forEach(c -> facItem.getChildren().add(new TreeItem<>(c)));
            facItem.setExpanded(true);
            root.getChildren().add(facItem);
        });
        root.setExpanded(true);
        tvFaculty.setRoot(root);
        cbClass.getItems().setAll(FACULTIES.values().stream().flatMap(List::stream).toList());

        // TODO 4: Cột — PropertyValueFactory cần package được opens tới javafx.base
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colClass.setCellValueFactory(new PropertyValueFactory<>("className"));
        colGpa.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        SortedList<Student> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tblStudents.comparatorProperty());
        tblStudents.setItems(sorted);
        tblStudents.setPlaceholder(new Label("Không có sinh viên"));
        lblCount.textProperty().bind(Bindings.size(filtered).asString("%d sinh viên"));

        // TODO 5: Predicate kết hợp cây + tìm kiếm
        filtered.predicateProperty().bind(Bindings.createObjectBinding(() -> {
            TreeItem<String> node = tvFaculty.getSelectionModel().getSelectedItem();
            String kw = tfSearch.getText() == null ? "" : tfSearch.getText().trim().toLowerCase();
            Set<String> allowedClasses = classesOf(node);
            return (Predicate<Student>) s -> (allowedClasses == null || allowedClasses.contains(s.getClassName()))
                    && (kw.isEmpty() || s.getStudentId().toLowerCase().contains(kw) || s.getFullName().toLowerCase().contains(kw));
        }, tvFaculty.getSelectionModel().selectedItemProperty(), tfSearch.textProperty()));

        // TODO 6: Chọn dòng → điền form; nút disable khi chưa chọn
        tblStudents.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s == null) return;
            tfId.setText(s.getStudentId()); tfName.setText(s.getFullName());
            cbClass.setValue(s.getClassName()); tfGpa.setText(String.valueOf(s.getGpa()));
        });
        BooleanBinding noSel = tblStudents.getSelectionModel().selectedItemProperty().isNull();
        btnUpdate.disableProperty().bind(noSel);
        btnDelete.disableProperty().bind(noSel);
    }

    /** null = không lọc (gốc hoặc chưa chọn) */
    private Set<String> classesOf(TreeItem<String> node) {
        if (node == null || node.getParent() == null) return null;      // gốc
        if (node.isLeaf()) return Set.of(node.getValue());             // lớp
        return node.getChildren().stream().map(TreeItem::getValue).collect(Collectors.toSet());   // khoa
    }

    // TODO 7: Validate form — trả về Student hoặc null
    private Student readForm(Student editing) {
        String id = tfId.getText().trim().toUpperCase();
        String name = tfName.getText().trim();
        String cls = cbClass.getValue();
        if (!id.matches("(SE|AI)\\d{6}")) { AlertUtil.warn("MSSV dạng SE/AI + 6 số"); return null; }
        if (name.length() < 3) { AlertUtil.warn("Họ tên ≥ 3 ký tự"); return null; }
        if (cls == null) { AlertUtil.warn("Chọn lớp"); return null; }
        if (!id.startsWith(cls.substring(0, 2))) { AlertUtil.warn("MSSV và lớp không cùng ngành"); return null; }
        double gpa;
        try {
            gpa = Double.parseDouble(tfGpa.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) { AlertUtil.warn("GPA phải là số"); return null; }
        if (gpa < 0 || gpa > 10) { AlertUtil.warn("GPA từ 0 đến 10"); return null; }
        boolean dup = students.stream().anyMatch(s -> s != editing && s.getStudentId().equalsIgnoreCase(id));
        if (dup) { AlertUtil.warn("MSSV đã tồn tại"); return null; }
        return new Student(id, name, cls, gpa);
    }

    // TODO 8: CRUD
    @FXML private void handleAdd() {
        Student s = readForm(null);
        if (s != null) { students.add(s); handleClear(); }
    }
    @FXML private void handleUpdate() {
        Student sel = tblStudents.getSelectionModel().getSelectedItem();
        Student s = readForm(sel);
        if (sel == null || s == null) return;
        sel.setStudentId(s.getStudentId()); sel.setFullName(s.getFullName());
        sel.setClassName(s.getClassName()); sel.setGpa(s.getGpa());       // Property → bảng tự cập nhật
        // Đổi lớp → nhờ extractor, FilteredList tự lọc lại (SV có thể "rơi" khỏi lớp đang xem).
        // KHÔNG gọi filtered.setPredicate(...) vì predicateProperty đang bind → lỗi "A bound value cannot be set"
    }
    @FXML private void handleDelete() {
        Student sel = tblStudents.getSelectionModel().getSelectedItem();
        if (sel != null && AlertUtil.confirm("Xóa " + sel.getFullName() + "?")) students.remove(sel);
    }
    @FXML private void handleClear() {
        tfId.clear(); tfName.clear(); tfGpa.clear(); cbClass.setValue(null);
        tblStudents.getSelectionModel().clearSelection();
    }

    // TODO 9: Import CSV: studentId,fullName,className,gpa
    @FXML private void handleImport() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import sinh viên");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File f = fc.showOpenDialog(tblStudents.getScene().getWindow());
        if (f == null) return;
        int ok = 0;
        List<String> errors = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {                      // bỏ dòng tiêu đề
                String[] p = lines.get(i).split(",");
                try {
                    if (p.length != 4) throw new IllegalArgumentException("thiếu cột");
                    String id = p[0].trim().toUpperCase();
                    double gpa = Double.parseDouble(p[3].trim());
                    if (!id.matches("(SE|AI)\\d{6}") || gpa < 0 || gpa > 10) throw new IllegalArgumentException("dữ liệu sai");
                    if (students.stream().anyMatch(s -> s.getStudentId().equals(id))) throw new IllegalArgumentException("trùng MSSV");
                    students.add(new Student(id, p[1].trim(), p[2].trim(), gpa));
                    ok++;
                } catch (IllegalArgumentException ex) {                    // NumberFormatException là con của nó
                    errors.add("Dòng " + (i + 1) + ": " + ex.getMessage());
                }
            }
            AlertUtil.info("Import %d dòng thành công, %d dòng lỗi%s".formatted(ok, errors.size(),
                    errors.isEmpty() ? "" : "\n" + String.join("\n", errors.subList(0, Math.min(10, errors.size())))));
        } catch (IOException e) {
            AlertUtil.error("Không đọc được file: " + e.getMessage());
        }
    }

    // TODO 10: Export CSV những SV đang hiển thị
    @FXML private void handleExport() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("students.csv");
        File f = fc.showSaveDialog(tblStudents.getScene().getWindow());
        if (f == null) return;
        List<String> lines = new ArrayList<>(List.of("studentId,fullName,className,gpa"));
        tblStudents.getItems().forEach(s -> lines.add(String.join(",", s.getStudentId(), s.getFullName(),
                s.getClassName(), String.valueOf(s.getGpa()))));
        try {
            Files.write(f.toPath(), lines, StandardCharsets.UTF_8);
            AlertUtil.info("Đã xuất " + (lines.size() - 1) + " sinh viên");
        } catch (IOException e) { AlertUtil.error(e.getMessage()); }
    }

    @FXML private void handleExit() { Platform.exit(); }
}
```

### Validation cần kiểm tra
- MSSV `(SE|AI)\d{6}`, không trùng; tiền tố MSSV khớp ngành của lớp
- Họ tên ≥ 3 ký tự; bắt buộc chọn lớp; GPA là số trong [0, 10]
- Import: bỏ qua & báo dòng lỗi (thiếu cột, sai số, trùng MSSV) — không dừng cả file
- FileChooser trả về `null` khi bấm Cancel → không làm gì

### Checklist hoàn thành
- [ ] Cây 3 cấp hiển thị đúng, mở rộng sẵn
- [ ] Chọn gốc / khoa / lớp → bảng lọc đúng; kết hợp được với ô tìm kiếm
- [ ] Sort theo cột vẫn hoạt động khi đang lọc
- [ ] Cột Xếp loại tự đổi khi sửa GPA
- [ ] Thêm/Cập nhật/Xóa hoạt động, có xác nhận khi xóa
- [ ] Menu + ToolBar + phím tắt Ctrl+O / Ctrl+S / Delete hoạt động
- [ ] Import file CSV có dòng lỗi → báo đúng dòng lỗi, các dòng đúng vẫn được thêm
- [ ] Export ra file mở được bằng Notepad/Excel, tiếng Việt không lỗi
- [ ] Giải thích được vì sao phải `opens com.fu.ch08.t1 to javafx.base`

---

## T2 — Shape Editor: 2D Graphics, Mouse/Key Event, Transform, Effect, Animation

### Mục tiêu
- Tạo shape (`Rectangle`, `Circle`, `Line`, `Polygon`) động theo thao tác chuột
- Xử lý `MouseEvent` (pressed / dragged / released / clicked) và `KeyEvent`
- Áp dụng transform (`rotate`, `scale`, `translate`), effect (`DropShadow`, `GaussianBlur`), animation (`RotateTransition`, `FadeTransition`, `Timeline`)

### Kiến thức (Chapter 08)
§2 Animations & Effects · §10 Node properties · §15 2D Graphics · `javafx.scene.input`, `javafx.scene.effect`, `javafx.animation`

### Mô tả
- **ToolBar:** `ToggleButton` chế độ **Chọn / Hình chữ nhật / Hình tròn / Đường thẳng / Tam giác**; `ColorPicker` màu tô & màu viền; `Slider` độ dày viền; nút **Xóa tất cả**
- **Vùng vẽ (`Pane`):** nhấn-kéo-thả để vẽ shape theo kích thước kéo; ở chế độ Chọn: click chọn shape (viền nét đứt + DropShadow), kéo để di chuyển, phím `Delete` xóa, chuột phải → ContextMenu (Đưa lên trên, Đưa xuống dưới, Nhân bản, Xóa)
- **Panel phải:** Slider **Xoay**, **Phóng to**, **Opacity**, CheckBox **Blur** cho shape đang chọn; nút **Xoay 360°**, **Nhấp nháy**, **Đổi màu liên tục**
- **Status bar:** tọa độ chuột, số shape, shape đang chọn

### Step-by-step
**Bước 1:** Dựng layout `BorderPane` (top ToolBar, center Pane, right VBox, bottom status). `drawPane.setClip(...)` để shape không tràn ra ngoài.
**Bước 2:** `onMousePressed`: nếu chế độ vẽ → tạo shape tại điểm nhấn; nếu chế độ chọn → xác định shape bị click (`e.getTarget()`), lưu offset.
**Bước 3:** `onMouseDragged`: chế độ vẽ → cập nhật kích thước; chế độ chọn → `translate`.
**Bước 4:** `onMouseReleased`: shape quá nhỏ (< 5px) → bỏ.
**Bước 5:** Chọn shape → **gỡ binding cũ, bind slider với shape mới** (không để 2 shape cùng bị bind).
**Bước 6:** Animation dùng `RotateTransition`, `FadeTransition`, `Timeline` + `KeyFrame` + `KeyValue`.

### TODOs

```java
package com.fu.ch08.t2;

public class ShapeEditorApp extends Application {

    enum Tool { SELECT, RECT, CIRCLE, LINE, TRIANGLE }

    private final Pane drawPane = new Pane();
    private final ObjectProperty<Tool> tool = new SimpleObjectProperty<>(Tool.SELECT);
    private final ObjectProperty<Shape> selected = new SimpleObjectProperty<>();
    private final ColorPicker cpFill = new ColorPicker(Color.web("#64b5f6"));
    private final ColorPicker cpStroke = new ColorPicker(Color.web("#0d47a1"));
    private final Slider slStroke = new Slider(0, 10, 2);
    private final Slider slRotate = new Slider(0, 360, 0), slScale = new Slider(0.2, 3, 1), slOpacity = new Slider(0, 1, 1);
    private final CheckBox chkBlur = new CheckBox("Blur");
    private final Label lblStatus = new Label();

    private double startX, startY, dragOffsetX, dragOffsetY;
    private Shape drawing;              // shape đang được vẽ

    @Override
    public void start(Stage stage) {
        // TODO 1: Toolbar chế độ
        ToggleGroup tg = new ToggleGroup();
        HBox modeBox = new HBox(4);
        for (Tool t : Tool.values()) {
            ToggleButton b = new ToggleButton(switch (t) {
                case SELECT -> "🖱 Chọn"; case RECT -> "▭ Chữ nhật"; case CIRCLE -> "◯ Tròn";
                case LINE -> "╱ Đường"; case TRIANGLE -> "△ Tam giác";
            });
            b.setToggleGroup(tg); b.setUserData(t);
            if (t == Tool.SELECT) b.setSelected(true);
            modeBox.getChildren().add(b);
        }
        tg.selectedToggleProperty().addListener((o, a, n) -> {
            if (n == null) a.setSelected(true);          // không cho bỏ chọn hết
            else tool.set((Tool) n.getUserData());
        });
        Button btnClear = new Button("Xóa tất cả");
        btnClear.setOnAction(e -> { if (AlertUtil.confirm("Xóa toàn bộ hình?")) { drawPane.getChildren().clear(); selected.set(null); } });
        ToolBar toolbar = new ToolBar(modeBox, new Separator(), new Label("Tô"), cpFill,
                new Label("Viền"), cpStroke, new Label("Độ dày"), slStroke, new Separator(), btnClear);

        // TODO 2: Vùng vẽ — clip theo kích thước
        drawPane.setStyle("-fx-background-color: white;");
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(drawPane.widthProperty());
        clip.heightProperty().bind(drawPane.heightProperty());
        drawPane.setClip(clip);
        drawPane.setOnMousePressed(this::onPressed);
        drawPane.setOnMouseDragged(this::onDragged);
        drawPane.setOnMouseReleased(this::onReleased);
        drawPane.setOnMouseMoved(e -> updateStatus(e.getX(), e.getY()));

        // TODO 3: Panel thuộc tính & animation
        Button btnSpin = new Button("Xoay 360°"), btnBlink = new Button("Nhấp nháy"), btnColor = new Button("Đổi màu liên tục");
        btnSpin.setOnAction(e -> spin());
        btnBlink.setOnAction(e -> blink());
        btnColor.setOnAction(e -> colorCycle());
        VBox right = new VBox(8, new Label("Xoay"), slRotate, new Label("Phóng to"), slScale,
                new Label("Opacity"), slOpacity, chkBlur, new Separator(), btnSpin, btnBlink, btnColor);
        right.setPadding(new Insets(10)); right.setPrefWidth(200);
        right.disableProperty().bind(selected.isNull());      // chưa chọn shape → panel disable

        // TODO 4: Khi đổi shape được chọn → chuyển binding
        selected.addListener((o, oldS, newS) -> onSelectionChanged(oldS, newS));

        BorderPane root = new BorderPane(drawPane, toolbar, right, new HBox(lblStatus), null);
        Scene scene = new Scene(root, 1100, 700);

        // TODO 5: Phím tắt
        scene.setOnKeyPressed(e -> {
            Shape s = selected.get();
            if (s == null) return;
            switch (e.getCode()) {
                case DELETE, BACK_SPACE -> { drawPane.getChildren().remove(s); selected.set(null); }
                case ESCAPE -> selected.set(null);
                case LEFT -> s.setTranslateX(s.getTranslateX() - 5);
                case RIGHT -> s.setTranslateX(s.getTranslateX() + 5);
                case UP -> s.setTranslateY(s.getTranslateY() - 5);
                case DOWN -> s.setTranslateY(s.getTranslateY() + 5);
                default -> { }
            }
            updateStatus(0, 0);
        });

        stage.setTitle("T2 - Shape Editor");
        stage.setScene(scene);
        stage.show();
    }

    // TODO 6: Mouse pressed
    private void onPressed(MouseEvent e) {
        if (e.getButton() != MouseButton.PRIMARY) return;
        startX = e.getX(); startY = e.getY();
        if (tool.get() == Tool.SELECT) {
            if (e.getTarget() instanceof Shape s && s.getParent() == drawPane) {
                selected.set(s);
                dragOffsetX = e.getX() - s.getTranslateX();
                dragOffsetY = e.getY() - s.getTranslateY();
            } else {
                selected.set(null);                             // click vùng trống → bỏ chọn
            }
            return;
        }
        drawing = switch (tool.get()) {
            case RECT -> new Rectangle(startX, startY, 0, 0);
            case CIRCLE -> new Circle(startX, startY, 0);
            case LINE -> new Line(startX, startY, startX, startY);
            case TRIANGLE -> new Polygon(startX, startY, startX, startY, startX, startY);
            default -> null;
        };
        if (!(drawing instanceof Line)) drawing.setFill(cpFill.getValue());
        drawing.setStroke(cpStroke.getValue());
        drawing.setStrokeWidth(Math.max(1, slStroke.getValue()));
        installContextMenu(drawing);
        drawPane.getChildren().add(drawing);
    }

    // TODO 7: Mouse dragged
    private void onDragged(MouseEvent e) {
        double x = e.getX(), y = e.getY();
        updateStatus(x, y);
        if (tool.get() == Tool.SELECT) {
            Shape s = selected.get();
            if (s != null) { s.setTranslateX(x - dragOffsetX); s.setTranslateY(y - dragOffsetY); }
            return;
        }
        switch (drawing) {
            case Rectangle r -> {
                r.setX(Math.min(startX, x)); r.setY(Math.min(startY, y));
                r.setWidth(Math.abs(x - startX)); r.setHeight(Math.abs(y - startY));
            }
            case Circle c -> c.setRadius(Math.hypot(x - startX, y - startY));
            case Line l -> { l.setEndX(x); l.setEndY(y); }
            case Polygon p -> p.getPoints().setAll(startX + (x - startX) / 2, startY, startX, y, x, y);
            case null, default -> { }
        }
    }

    // TODO 8: Mouse released — bỏ shape quá nhỏ (click nhầm)
    private void onReleased(MouseEvent e) {
        if (drawing == null) return;
        Bounds b = drawing.getLayoutBounds();
        if (b.getWidth() < 5 && b.getHeight() < 5) drawPane.getChildren().remove(drawing);
        drawing = null;
        updateStatus(e.getX(), e.getY());
    }

    // TODO 9: Chuyển binding sang shape mới
    private final DropShadow selectionEffect = new DropShadow(12, Color.ORANGE);
    private void onSelectionChanged(Shape oldS, Shape newS) {
        if (oldS != null) {
            oldS.rotateProperty().unbind(); oldS.scaleXProperty().unbind(); oldS.scaleYProperty().unbind();
            oldS.opacityProperty().unbind();
            oldS.getStrokeDashArray().clear();
            oldS.setEffect(Boolean.TRUE.equals(oldS.getProperties().get("blur")) ? new GaussianBlur(6) : null);
        }
        if (newS != null) {
            // Đưa slider về giá trị hiện tại của shape TRƯỚC khi bind
            slRotate.setValue(newS.getRotate()); slScale.setValue(newS.getScaleX()); slOpacity.setValue(newS.getOpacity());
            chkBlur.setSelected(Boolean.TRUE.equals(newS.getProperties().get("blur")));
            newS.rotateProperty().bind(slRotate.valueProperty());
            newS.scaleXProperty().bind(slScale.valueProperty());
            newS.scaleYProperty().bind(slScale.valueProperty());
            newS.opacityProperty().bind(slOpacity.valueProperty());
            newS.getStrokeDashArray().setAll(8.0, 4.0);
            newS.setEffect(selectionEffect);
        }
        updateStatus(0, 0);
    }
    {   // instance initializer: blur lưu vào properties của node, effect hiển thị khi bỏ chọn
        chkBlur.selectedProperty().addListener((o, a, on) -> {
            if (selected.get() != null) selected.get().getProperties().put("blur", on);
        });
    }

    // TODO 10: ContextMenu cho từng shape
    private void installContextMenu(Shape s) {
        MenuItem front = new MenuItem("Đưa lên trên"), back = new MenuItem("Đưa xuống dưới"),
                 dup = new MenuItem("Nhân bản"), del = new MenuItem("Xóa");
        front.setOnAction(e -> s.toFront());
        back.setOnAction(e -> s.toBack());
        del.setOnAction(e -> { drawPane.getChildren().remove(s); if (selected.get() == s) selected.set(null); });
        dup.setOnAction(e -> duplicate(s));
        ContextMenu cm = new ContextMenu(front, back, dup, del);
        s.setOnContextMenuRequested(e -> cm.show(s, e.getScreenX(), e.getScreenY()));
    }

    private void duplicate(Shape s) {
        Shape copy = switch (s) {
            case Rectangle r -> new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
            case Circle c -> new Circle(c.getCenterX(), c.getCenterY(), c.getRadius());
            case Line l -> new Line(l.getStartX(), l.getStartY(), l.getEndX(), l.getEndY());
            case Polygon p -> { Polygon q = new Polygon(); q.getPoints().setAll(p.getPoints()); yield q; }
            default -> null;
        };
        if (copy == null) return;
        copy.setFill(s.getFill()); copy.setStroke(s.getStroke()); copy.setStrokeWidth(s.getStrokeWidth());
        copy.setTranslateX(s.getTranslateX() + 20); copy.setTranslateY(s.getTranslateY() + 20);
        copy.setRotate(s.getRotate()); copy.setScaleX(s.getScaleX()); copy.setScaleY(s.getScaleY());
        installContextMenu(copy);
        drawPane.getChildren().add(copy);
    }

    // TODO 11: Animation — rotate đang bind với slider → animate SLIDER
    private void spin() {
        Timeline t = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(slRotate.valueProperty(), 0)),
                new KeyFrame(Duration.seconds(1.2), new KeyValue(slRotate.valueProperty(), 360, Interpolator.EASE_BOTH)));
        t.play();
    }

    private void blink() {
        Shape s = selected.get();
        s.opacityProperty().unbind();                         // FadeTransition cần set opacity trực tiếp
        FadeTransition ft = new FadeTransition(Duration.millis(250), s);
        ft.setFromValue(1); ft.setToValue(0.1); ft.setCycleCount(6); ft.setAutoReverse(true);
        ft.setOnFinished(e -> { if (selected.get() == s) s.opacityProperty().bind(slOpacity.valueProperty()); });
        ft.play();
    }

    private void colorCycle() {
        Shape s = selected.get();
        Paint original = s.getFill();
        Timeline t = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(s.fillProperty(), Color.RED)),
                new KeyFrame(Duration.seconds(1), new KeyValue(s.fillProperty(), Color.YELLOW)),
                new KeyFrame(Duration.seconds(2), new KeyValue(s.fillProperty(), Color.LIMEGREEN)));
        t.setCycleCount(2); t.setAutoReverse(true);
        t.setOnFinished(e -> s.setFill(original));
        t.play();
    }

    private void updateStatus(double x, double y) {
        Shape s = selected.get();
        lblStatus.setText("  Chuột: (%.0f, %.0f) | Số hình: %d | Đang chọn: %s".formatted(
                x, y, drawPane.getChildren().size(), s == null ? "—" : s.getClass().getSimpleName()));
    }
}
```

### Validation cần kiểm tra
- Bỏ qua shape có kích thước < 5px; chuột phải không vẽ
- Không thể bỏ chọn hết các chế độ trong ToggleGroup
- Panel thuộc tính disable khi chưa chọn shape; animation không chạy khi chưa chọn

### Checklist hoàn thành
- [ ] Vẽ được 4 loại shape bằng nhấn-kéo-thả; kéo ngược chiều (lên/trái) vẫn đúng với hình chữ nhật
- [ ] Màu tô, màu viền, độ dày viền áp dụng cho shape mới
- [ ] Chế độ Chọn: click chọn (viền nét đứt + bóng), kéo di chuyển, click vùng trống bỏ chọn
- [ ] Slider xoay/phóng to/opacity chỉ tác động lên shape đang chọn
- [ ] Đổi shape được chọn → shape cũ giữ nguyên trạng thái, không bị đổi theo slider
- [ ] ContextMenu: lên trên / xuống dưới / nhân bản / xóa
- [ ] Delete, ESC, phím mũi tên hoạt động
- [ ] 3 animation chạy mượt, kết thúc đúng trạng thái
- [ ] Shape không vẽ tràn ra ngoài vùng vẽ (clip)
- [ ] Dùng được pattern matching `switch (shape)` của Java 21

---

## T3 — Concurrency: Task, ProgressBar, Cancel, Platform.runLater

### Mục tiêu
- Thấy rõ hiện tượng **UI đơ** khi chạy tác vụ nặng trên FX thread
- Dùng `Task<V>`: `updateProgress`, `updateMessage`, `updateValue`, `isCancelled`, `setOnSucceeded/Failed/Cancelled`
- Bind `ProgressBar`/`Label` với `Task`; chạy nhiều Task song song bằng `ExecutorService`
- So sánh `Platform.runLater` + Thread với `Timeline` cho đồng hồ

### Kiến thức (Chapter 08)
§14 ProgressBar · §20 Concurrency · §22 lưu ý 4

### Mô tả
Ứng dụng "Xử lý dữ liệu sinh viên" gồm:
1. **Đồng hồ** góc trên — cập nhật mỗi giây (2 cách: Thread + `Platform.runLater` và `Timeline`)
2. Nút **"Chạy trên UI thread (sai)"** → `Thread.sleep(5000)` trên FX thread → cửa sổ đơ, đồng hồ dừng
3. Khu vực **Xử lý 1 file**: nhập số bản ghi (100–10.000), nút **Bắt đầu** / **Hủy**; `ProgressBar`, `%`, message "Đang xử lý bản ghi x/N", `ListView` hiển thị bản ghi lỗi **trong lúc chạy**; kết thúc → Alert tổng kết
4. Khu vực **Xử lý song song**: nút "Chạy 3 file" → 3 `ProgressBar` chạy đồng thời với tốc độ khác nhau; nút "Hủy tất cả"
5. Mô phỏng lỗi: CheckBox "Gây lỗi ở 50%" → Task ném exception → `setOnFailed`

### Step-by-step
**Bước 1:** Tạo đồng hồ bằng `Timeline` (KeyFrame 1s, `INDEFINITE`). Thêm đồng hồ thứ 2 bằng daemon Thread + `Platform.runLater` để so sánh.
**Bước 2:** Nút "sai": sleep trên FX thread → quan sát.
**Bước 3:** Viết `ProcessTask extends Task<Summary>`: vòng lặp N bản ghi, mỗi bản ghi sleep vài ms, kiểm tra `isCancelled()`, `updateProgress`, `updateMessage`; bản ghi lỗi đẩy lên UI bằng `Platform.runLater`.
**Bước 4:** Bind UI với task; nút Bắt đầu disable khi `task.running`; nút Hủy gọi `task.cancel()`.
**Bước 5:** Dùng `ExecutorService` (3 thread) chạy 3 task; `shutdownNow()` trong `stop()`.
**Bước 6:** Xử lý `setOnFailed` hiển thị lỗi, `setOnCancelled` hiển thị "Đã hủy".

### TODOs

```java
package com.fu.ch08.t3;

// TODO 1: Kết quả trả về
public record Summary(int total, int processed, int errors, long millis) {}

// TODO 2: Task — CHỈ dùng updateXxx(), KHÔNG đụng trực tiếp control trong call()
public class ProcessTask extends Task<Summary> {
    private final int total;
    private final int delayMs;
    private final boolean failHalfway;
    private final Consumer<String> onErrorRecord;    // callback sẽ được gọi trên FX thread

    public ProcessTask(int total, int delayMs, boolean failHalfway, Consumer<String> onErrorRecord) {
        this.total = total; this.delayMs = delayMs; this.failHalfway = failHalfway; this.onErrorRecord = onErrorRecord;
    }

    @Override
    protected Summary call() throws Exception {
        long start = System.currentTimeMillis();
        int errors = 0, processed = 0;
        Random rnd = new Random();
        for (int i = 1; i <= total; i++) {
            if (isCancelled()) {                       // TODO 2a: kiểm tra hủy mỗi vòng
                updateMessage("Đã hủy tại bản ghi " + i);
                break;
            }
            if (failHalfway && i == total / 2) throw new IllegalStateException("Dữ liệu hỏng tại bản ghi " + i);
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {          // cancel() ngắt thread đang sleep
                if (isCancelled()) { updateMessage("Đã hủy tại bản ghi " + i); break; }
                throw e;
            }
            if (rnd.nextInt(100) < 3) {                // ~3% bản ghi lỗi
                errors++;
                String msg = "Bản ghi #" + i + ": email không hợp lệ";
                Platform.runLater(() -> onErrorRecord.accept(msg));   // đẩy cập nhật UI về FX thread
            }
            processed = i;
            updateProgress(i, total);
            updateMessage("Đang xử lý %d/%d".formatted(i, total));
        }
        // Không đọc getProgress() ở đây: property được cập nhật bất đồng bộ trên FX thread
        return new Summary(total, processed, errors, System.currentTimeMillis() - start);
    }
}
```

```java
public class ConcurrencyApp extends Application {

    private final ExecutorService pool = Executors.newFixedThreadPool(3, r -> {
        Thread t = new Thread(r, "worker"); t.setDaemon(true); return t;   // daemon: không giữ JVM khi thoát
    });
    private ProcessTask currentTask;
    private final List<ProcessTask> parallelTasks = new ArrayList<>();
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void start(Stage stage) {
        // TODO 3: Đồng hồ 1 — Timeline (chạy trên FX thread, không cần runLater)
        Label clock1 = new Label();
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1),
                e -> clock1.setText("⏰ Timeline: " + LocalTime.now().format(TIME))));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        // TODO 4: Đồng hồ 2 — Thread + Platform.runLater
        Label clock2 = new Label();
        Thread clockThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                String now = LocalTime.now().format(TIME);
                Platform.runLater(() -> clock2.setText("🧵 Thread+runLater: " + now));
                try { Thread.sleep(1000); } catch (InterruptedException e) { return; }
            }
        }, "clock");
        clockThread.setDaemon(true);
        clockThread.start();

        // TODO 5: Ví dụ SAI — chặn FX thread
        Button btnFreeze = new Button("⚠ Chạy trên UI thread (sai)");
        btnFreeze.setOnAction(e -> {
            try { Thread.sleep(5000); } catch (InterruptedException ignored) { }
            AlertUtil.info("Xong sau 5 giây — trong lúc đó cửa sổ bị đơ, đồng hồ đứng yên");
        });

        // TODO 6: Khu vực xử lý 1 file
        TextField tfCount = new TextField("1000");
        CheckBox chkFail = new CheckBox("Gây lỗi ở 50%");
        Button btnStart = new Button("Bắt đầu"), btnCancel = new Button("Hủy");
        ProgressBar pb = new ProgressBar(0); pb.setPrefWidth(400);
        Label lblPercent = new Label(), lblMessage = new Label();
        ListView<String> lvErrors = new ListView<>(); lvErrors.setPrefHeight(140);
        lvErrors.setPlaceholder(new Label("Chưa có bản ghi lỗi"));
        btnCancel.setDisable(true);

        btnStart.setOnAction(e -> {
            int n;
            try { n = Integer.parseInt(tfCount.getText().trim()); }
            catch (NumberFormatException ex) { AlertUtil.warn("Số bản ghi phải là số nguyên"); return; }
            if (n < 100 || n > 10_000) { AlertUtil.warn("Số bản ghi từ 100 đến 10.000"); return; }

            lvErrors.getItems().clear();
            currentTask = new ProcessTask(n, 2, chkFail.isSelected(), lvErrors.getItems()::add);

            // TODO 6a: Binding UI ↔ Task
            pb.progressProperty().bind(currentTask.progressProperty());
            lblPercent.textProperty().bind(currentTask.progressProperty().multiply(100).asString("%.0f%%"));
            lblMessage.textProperty().bind(currentTask.messageProperty());
            btnStart.disableProperty().bind(currentTask.runningProperty());
            btnCancel.disableProperty().bind(currentTask.runningProperty().not());
            tfCount.disableProperty().bind(currentTask.runningProperty());

            // TODO 6b: Callback — chạy trên FX thread
            currentTask.setOnSucceeded(ev -> {
                Summary s = currentTask.getValue();
                AlertUtil.info("Hoàn tất: %d/%d bản ghi, %d lỗi, %.1f giây".formatted(
                        s.processed(), s.total(), s.errors(), s.millis() / 1000.0));
            });
            currentTask.setOnFailed(ev -> AlertUtil.error("Lỗi: " + currentTask.getException().getMessage()));
            currentTask.setOnCancelled(ev -> AlertUtil.warn("Đã hủy xử lý"));

            pool.submit(currentTask);
        });
        btnCancel.setOnAction(e -> { if (currentTask != null) currentTask.cancel(); });

        // TODO 7: Xử lý song song 3 file
        VBox parallelBox = new VBox(6);
        Button btnParallel = new Button("Chạy 3 file song song"), btnCancelAll = new Button("Hủy tất cả");
        btnParallel.setOnAction(e -> {
            parallelBox.getChildren().clear();
            parallelTasks.clear();
            int[] speeds = {1, 3, 6};
            for (int i = 0; i < 3; i++) {
                ProcessTask t = new ProcessTask(800, speeds[i], false, msg -> { });
                ProgressBar p = new ProgressBar(); p.setPrefWidth(300);
                p.progressProperty().bind(t.progressProperty());
                Label l = new Label(); l.textProperty().bind(t.messageProperty());
                parallelBox.getChildren().add(new HBox(10, new Label("File " + (i + 1)), p, l));
                parallelTasks.add(t);
                pool.submit(t);
            }
        });
        btnCancelAll.setOnAction(e -> parallelTasks.forEach(Task::cancel));

        VBox root = new VBox(12,
                new HBox(30, clock1, clock2), btnFreeze, new Separator(),
                new Label("Xử lý 1 file"), new HBox(8, new Label("Số bản ghi"), tfCount, chkFail, btnStart, btnCancel),
                new HBox(10, pb, lblPercent), lblMessage, new Label("Bản ghi lỗi:"), lvErrors, new Separator(),
                new HBox(8, btnParallel, btnCancelAll), parallelBox);
        root.setPadding(new Insets(16));

        stage.setTitle("T3 - Concurrency");
        stage.setScene(new Scene(root, 760, 640));
        stage.show();
    }

    // TODO 8: Dọn dẹp thread khi thoát
    @Override
    public void stop() {
        pool.shutdownNow();
    }
}
```

### Validation cần kiểm tra
- Số bản ghi là số nguyên trong [100, 10.000]
- Không thể bấm Bắt đầu khi đang chạy (tránh 2 task cùng lúc)
- Hủy hoạt động cả khi task đang `sleep` (InterruptedException)

### Checklist hoàn thành
- [ ] Bấm nút "sai" → cửa sổ không phản hồi 5 giây, 2 đồng hồ đứng yên → hiểu được vì sao cần Task
- [ ] Khi Task chạy, cửa sổ vẫn kéo/thả, đồng hồ vẫn chạy
- [ ] ProgressBar, %, message cập nhật liên tục; bản ghi lỗi xuất hiện dần trong ListView
- [ ] Nút Bắt đầu/Hủy/ô nhập tự bật/tắt theo trạng thái task (binding `runningProperty`)
- [ ] Hủy giữa chừng → Alert "Đã hủy"; tích "Gây lỗi" → Alert lỗi từ `setOnFailed`
- [ ] 3 progress bar song song chạy với tốc độ khác nhau; "Hủy tất cả" dừng cả 3
- [ ] Trong `call()` không có dòng nào gọi trực tiếp `setText`/`getItems().add` của control
- [ ] Đóng cửa sổ khi task đang chạy → ứng dụng thoát hẳn (daemon thread + `shutdownNow`)

---

## T4 — Charts Dashboard Cập Nhật Real-time

### Mục tiêu
- Dùng `PieChart`, `BarChart`, `LineChart`, `AreaChart`/`StackedBarChart`
- Đồng bộ chart với `ObservableList` dữ liệu qua `ListChangeListener`
- Tooltip, click vào phần biểu đồ, đổi loại biểu đồ, ẩn/hiện series

### Kiến thức (Chapter 08)
§19 Charts · §11 Observable · §14 TableView

### Mô tả
"Bảng điểm lớp SE1801" — bên trái: form nhập (Họ tên, điểm Java, điểm Spring, điểm JavaFX) + `TableView` điểm (có nút xóa). Bên phải: `TabPane`:
1. **PieChart** phân bố xếp loại (theo điểm trung bình); click vào lát → Alert danh sách SV thuộc loại đó
2. **BarChart** điểm trung bình từng môn; `ComboBox` đổi sang `StackedBarChart` theo từng sinh viên
3. **LineChart** điểm từng sinh viên theo 3 môn (mỗi môn 1 series); `CheckBox` ẩn/hiện từng series

Thêm/xóa/sửa điểm → **mọi chart cập nhật ngay**.

### Step-by-step
**Bước 1:** Model `ScoreRow` (name, java, spring, javafx — Property) + method `average()`, `grade()`.
**Bước 2:** Form nhập + TableView (`setCellValueFactory` bằng lambda); TableView **editable** cột điểm (`TextFieldTableCell` + `DoubleStringConverter`).
**Bước 3:** Tạo chart 1 lần; viết `refreshCharts()` dựng lại data.
**Bước 4:** `scores.addListener((ListChangeListener) ...)` + listener trên property điểm của từng dòng → gọi `refreshCharts()`. Dùng *extractor* để list báo thay đổi khi property con đổi.
**Bước 5:** Tooltip & click trên node của data (lấy được sau khi data đã add vào chart).
**Bước 6:** Tắt animation cho chart dùng `CategoryAxis` khi thay dữ liệu.

### TODOs

```java
package com.fu.ch08.t4;

// TODO 1: Model
public class ScoreRow {
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty java = new SimpleDoubleProperty(), spring = new SimpleDoubleProperty(), javafx = new SimpleDoubleProperty();

    public ScoreRow(String n, double j, double s, double f) { name.set(n); java.set(j); spring.set(s); javafx.set(f); }

    public double average() { return Math.round((java.get() + spring.get() + javafx.get()) / 3 * 100) / 100.0; }
    public String grade() {
        double a = average();
        return a >= 8.5 ? "Giỏi" : a >= 7 ? "Khá" : a >= 5 ? "Trung bình" : "Yếu";
    }
    /** extractor: các property mà khi đổi thì ObservableList phát sự kiện "update" */
    public Observable[] observables() { return new Observable[]{name, java, spring, javafx}; }

    public StringProperty nameProperty() { return name; }
    public DoubleProperty javaProperty() { return java; }
    public DoubleProperty springProperty() { return spring; }
    public DoubleProperty javafxProperty() { return javafx; }
    public String getName() { return name.get(); }
}
```

```java
public class ScoreDashboardApp extends Application {

    // TODO 2: ObservableList có EXTRACTOR → sửa điểm 1 dòng cũng phát sự kiện thay đổi
    private final ObservableList<ScoreRow> scores = FXCollections.observableArrayList(ScoreRow::observables);

    private final PieChart pie = new PieChart();
    private final CategoryAxis barX = new CategoryAxis();
    private final NumberAxis barY = new NumberAxis(0, 10, 1);
    private final BarChart<String, Number> bar = new BarChart<>(barX, barY);
    private final StackedBarChart<String, Number> stacked = new StackedBarChart<>(new CategoryAxis(), new NumberAxis(0, 30, 5));
    private final LineChart<String, Number> line = new LineChart<>(new CategoryAxis(), new NumberAxis(0, 10, 1));
    private final StackPane barHolder = new StackPane(bar);
    private final Map<String, CheckBox> seriesToggles = new LinkedHashMap<>();

    @Override
    public void start(Stage stage) {
        scores.addAll(new ScoreRow("An", 8, 9, 7.5), new ScoreRow("Bình", 6, 5.5, 7),
                      new ScoreRow("Cường", 9.5, 9, 9), new ScoreRow("Dung", 4, 5, 4.5));

        // TODO 3: Bảng điểm editable
        TableView<ScoreRow> table = new TableView<>(scores);
        table.setEditable(true);
        TableColumn<ScoreRow, String> colName = new TableColumn<>("Họ tên");
        colName.setCellValueFactory(c -> c.getValue().nameProperty());
        table.getColumns().add(colName);
        addScoreColumn(table, "Java", ScoreRow::javaProperty);
        addScoreColumn(table, "Spring", ScoreRow::springProperty);
        addScoreColumn(table, "JavaFX", ScoreRow::javafxProperty);
        TableColumn<ScoreRow, String> colAvg = new TableColumn<>("TB");
        colAvg.setCellValueFactory(c -> Bindings.createStringBinding(
                () -> String.valueOf(c.getValue().average()), c.getValue().observables()));
        table.getColumns().add(colAvg);

        // TODO 4: Form thêm
        TextField tfName = new TextField(), tfJava = new TextField(), tfSpring = new TextField(), tfFx = new TextField();
        tfName.setPromptText("Họ tên"); tfJava.setPromptText("Java"); tfSpring.setPromptText("Spring"); tfFx.setPromptText("JavaFX");
        Button btnAdd = new Button("Thêm"), btnDel = new Button("Xóa dòng chọn");
        btnAdd.setOnAction(e -> {
            try {
                double j = score(tfJava), s = score(tfSpring), f = score(tfFx);
                String n = tfName.getText().trim();
                if (n.isEmpty()) throw new IllegalArgumentException("Nhập họ tên");
                if (scores.stream().anyMatch(r -> r.getName().equalsIgnoreCase(n))) throw new IllegalArgumentException("Tên đã tồn tại");
                scores.add(new ScoreRow(n, j, s, f));
                List.of(tfName, tfJava, tfSpring, tfFx).forEach(TextField::clear);
            } catch (IllegalArgumentException ex) { AlertUtil.warn(ex.getMessage()); }
        });
        btnDel.setOnAction(e -> scores.remove(table.getSelectionModel().getSelectedItem()));
        btnDel.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        // TODO 5: Chart config
        pie.setTitle("Phân bố xếp loại");
        bar.setTitle("Điểm trung bình theo môn"); bar.setAnimated(false); bar.setLegendVisible(false);
        stacked.setTitle("Tổng điểm từng sinh viên"); stacked.setAnimated(false);
        line.setTitle("Điểm từng sinh viên"); line.setAnimated(false);
        ComboBox<String> cbBarType = new ComboBox<>(FXCollections.observableArrayList("Bar - TB môn", "Stacked - từng SV"));
        cbBarType.setValue("Bar - TB môn");
        cbBarType.valueProperty().addListener((o, a, v) -> barHolder.getChildren().setAll(v.startsWith("Bar") ? bar : stacked));

        HBox toggles = new HBox(10);
        for (String subject : List.of("Java", "Spring", "JavaFX")) {
            CheckBox cb = new CheckBox(subject); cb.setSelected(true);
            cb.selectedProperty().addListener((o, a, b) -> refreshCharts());
            seriesToggles.put(subject, cb);
            toggles.getChildren().add(cb);
        }

        // TODO 6: Lắng nghe thay đổi dữ liệu (thêm/xóa/sửa)
        scores.addListener((ListChangeListener<ScoreRow>) change -> refreshCharts());
        refreshCharts();

        TabPane tabs = new TabPane(new Tab("Xếp loại", pie), new Tab("Theo môn", new VBox(6, cbBarType, barHolder)),
                new Tab("Từng SV", new VBox(6, toggles, line)));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        VBox left = new VBox(8, new HBox(5, tfName, tfJava, tfSpring, tfFx), new HBox(5, btnAdd, btnDel), table);
        left.setPadding(new Insets(10)); left.setPrefWidth(470);
        stage.setScene(new Scene(new SplitPane(left, tabs), 1150, 600));
        stage.setTitle("T4 - Charts Dashboard (double-click ô điểm để sửa)");
        stage.show();
    }

    private void addScoreColumn(TableView<ScoreRow> t, String title, Function<ScoreRow, DoubleProperty> prop) {
        TableColumn<ScoreRow, Double> col = new TableColumn<>(title);
        col.setCellValueFactory(c -> prop.apply(c.getValue()).asObject());
        // DoubleStringConverter ném NumberFormatException khi gõ chữ → bọc lại, trả null để báo lỗi
        col.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter() {
            @Override public Double fromString(String s) {
                try { return super.fromString(s == null ? null : s.trim().replace(',', '.')); }
                catch (NumberFormatException e) { return null; }
            }
        }));
        col.setOnEditCommit(e -> {
            Double v = e.getNewValue();
            if (v == null || v < 0 || v > 10) { AlertUtil.warn("Điểm từ 0 đến 10"); t.refresh(); return; }
            prop.apply(e.getRowValue()).set(v);
        });
        t.getColumns().add(col);
    }

    private static double score(TextField tf) {
        try {
            double v = Double.parseDouble(tf.getText().trim());
            if (v < 0 || v > 10) throw new IllegalArgumentException(tf.getPromptText() + ": điểm từ 0 đến 10");
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(tf.getPromptText() + ": phải là số");
        }
    }

    // TODO 7: Dựng lại dữ liệu cho mọi chart
    private void refreshCharts() {
        // Pie
        Map<String, List<String>> byGrade = scores.stream().collect(Collectors.groupingBy(ScoreRow::grade,
                TreeMap::new, Collectors.mapping(ScoreRow::getName, Collectors.toList())));
        pie.getData().setAll(byGrade.entrySet().stream()
                .map(en -> new PieChart.Data(en.getKey() + " (" + en.getValue().size() + ")", en.getValue().size())).toList());
        for (PieChart.Data d : pie.getData()) {          // node đã tồn tại sau khi add vào chart
            String grade = d.getName().substring(0, d.getName().indexOf(" ("));
            double pct = d.getPieValue() * 100 / scores.size();
            Tooltip.install(d.getNode(), new Tooltip("%s: %.1f%%".formatted(d.getName(), pct)));
            d.getNode().setOnMouseClicked(e -> AlertUtil.info(grade + ": " + String.join(", ", byGrade.get(grade))));
        }

        // Bar — TB từng môn
        XYChart.Series<String, Number> avg = new XYChart.Series<>();
        avg.getData().add(new XYChart.Data<>("Java", scores.stream().mapToDouble(r -> r.javaProperty().get()).average().orElse(0)));
        avg.getData().add(new XYChart.Data<>("Spring", scores.stream().mapToDouble(r -> r.springProperty().get()).average().orElse(0)));
        avg.getData().add(new XYChart.Data<>("JavaFX", scores.stream().mapToDouble(r -> r.javafxProperty().get()).average().orElse(0)));
        bar.getData().setAll(List.of(avg));

        // Stacked & Line — mỗi môn 1 series, trục X = tên SV
        stacked.getData().clear(); line.getData().clear();
        Map<String, Function<ScoreRow, DoubleProperty>> subjects = new LinkedHashMap<>();
        subjects.put("Java", ScoreRow::javaProperty);
        subjects.put("Spring", ScoreRow::springProperty);
        subjects.put("JavaFX", ScoreRow::javafxProperty);
        subjects.forEach((subject, prop) -> {
            XYChart.Series<String, Number> s1 = new XYChart.Series<>(), s2 = new XYChart.Series<>();
            s1.setName(subject); s2.setName(subject);
            scores.forEach(r -> {
                s1.getData().add(new XYChart.Data<>(r.getName(), prop.apply(r).get()));
                s2.getData().add(new XYChart.Data<>(r.getName(), prop.apply(r).get()));
            });
            stacked.getData().add(s1);
            if (seriesToggles.isEmpty() || seriesToggles.get(subject).isSelected()) line.getData().add(s2);
        });
    }
}
```

### Validation cần kiểm tra
- Họ tên không rỗng, không trùng; 3 điểm là số trong [0, 10]
- Sửa trực tiếp trong bảng: giá trị không hợp lệ → cảnh báo, giữ giá trị cũ
- Danh sách rỗng → chart rỗng, không chia cho 0

### Checklist hoàn thành
- [ ] 3 tab chart hiển thị đúng dữ liệu mẫu
- [ ] Thêm / xóa / **sửa ô điểm** → cả 3 chart cập nhật ngay (nhờ extractor + ListChangeListener)
- [ ] PieChart có tooltip %; click lát → hiện danh sách SV
- [ ] Đổi Bar ↔ Stacked hoạt động
- [ ] Ẩn/hiện series trên LineChart bằng CheckBox
- [ ] Không bị chồng nhãn trục khi thay dữ liệu (đã tắt animation)
- [ ] Cột TB trong bảng tự cập nhật khi sửa điểm

---

## T5 — Mini Browser & Media Player (WebView + Media)

### Mục tiêu
- Dùng `WebView`/`WebEngine`: load URL, HTML string, history, trạng thái tải, gọi JavaScript, xử lý `alert()`
- Dùng `Media`/`MediaPlayer`/`MediaView`: mở file, play/pause/stop, tua, âm lượng, xử lý lỗi định dạng
- Cấu hình dependency `javafx-web`, `javafx-media` và `module-info`

### Kiến thức (Chapter 08)
§17 Audio & Video · §18 WebView · §22 lưu ý 10

### Mô tả
`TabPane` 2 tab:
1. **🌐 Trình duyệt:** nút ◀ ▶ ⟳ 🏠, ô URL (Enter để đi), `ProgressBar` tải trang, tiêu đề cửa sổ = tiêu đề trang; nút **"Trang HTML nội bộ"** load HTML string có nút bấm gọi `alert()`; nút **"Đếm thẻ <a>"** chạy JavaScript trả kết quả về Java.
2. **🎬 Media Player:** nút **Mở file** (mp4, mp3, wav), Play/Pause, Stop, `Slider` thời gian (kéo để tua), `Slider` âm lượng, Label `mm:ss / mm:ss`, CheckBox Lặp lại.

### Step-by-step
**Bước 1:** Thêm dependency `javafx-web`, `javafx-media`; `requires javafx.web; requires javafx.media;`.
**Bước 2:** Browser: chuẩn hóa URL (tự thêm `https://`), bind progress với `engine.getLoadWorker().progressProperty()`.
**Bước 3:** History: `engine.getHistory()` — `go(-1)`, `go(1)`; disable nút theo `currentIndex`.
**Bước 4:** `engine.setOnAlert(...)` để hiện `alert()` của JS bằng `Alert` của JavaFX; `executeScript` lấy giá trị trả về.
**Bước 5:** Media: `FileChooser` → `new Media(file.toURI().toString())`; **dispose** player cũ trước khi tạo mới; `setOnError`.
**Bước 6:** Slider thời gian: cập nhật theo `currentTimeProperty` **trừ khi người dùng đang kéo**; tua bằng `seek()` khi thả chuột.

### TODOs

```java
package com.fu.ch08.t5;

public class BrowserMediaApp extends Application {

    private static final String HOME = "https://openjfx.io";
    private MediaPlayer player;
    private final MediaView mediaView = new MediaView();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(new Tab("🌐 Trình duyệt", buildBrowser(stage)), new Tab("🎬 Media Player", buildPlayer(stage)));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        stage.setScene(new Scene(tabs, 1100, 720));
        stage.show();
    }

    // ================= BROWSER =================
    private Node buildBrowser(Stage stage) {
        WebView web = new WebView();
        WebEngine engine = web.getEngine();
        WebHistory history = engine.getHistory();

        Button btnBack = new Button("◀"), btnFwd = new Button("▶"), btnReload = new Button("⟳"), btnHome = new Button("🏠");
        TextField tfUrl = new TextField(HOME);
        HBox.setHgrow(tfUrl, Priority.ALWAYS);
        Button btnLocal = new Button("Trang HTML nội bộ"), btnCount = new Button("Đếm thẻ <a>");
        ProgressBar pb = new ProgressBar(); pb.setMaxWidth(Double.MAX_VALUE);

        // TODO 1: Điều hướng
        tfUrl.setOnAction(e -> {
            String url = normalize(tfUrl.getText());
            if (url == null) { AlertUtil.warn("URL không hợp lệ"); return; }
            engine.load(url);
        });
        btnBack.setOnAction(e -> history.go(-1));
        btnFwd.setOnAction(e -> history.go(1));
        btnReload.setOnAction(e -> engine.reload());
        btnHome.setOnAction(e -> engine.load(HOME));

        // TODO 2: Binding trạng thái
        btnBack.disableProperty().bind(history.currentIndexProperty().lessThanOrEqualTo(0));
        btnFwd.disableProperty().bind(Bindings.createBooleanBinding(
                () -> history.getCurrentIndex() >= history.getEntries().size() - 1,
                history.currentIndexProperty(), history.getEntries()));
        pb.progressProperty().bind(engine.getLoadWorker().progressProperty());
        pb.visibleProperty().bind(engine.getLoadWorker().runningProperty());
        engine.locationProperty().addListener((o, a, loc) -> tfUrl.setText(loc));
        stage.titleProperty().bind(Bindings.createStringBinding(
                () -> "T5 - " + (engine.getTitle() == null ? "Mini Browser" : engine.getTitle()), engine.titleProperty()));

        // TODO 3: Lỗi tải trang
        engine.getLoadWorker().stateProperty().addListener((o, a, st) -> {
            if (st == Worker.State.FAILED) {
                engine.loadContent("<h2 style='color:#c00'>Không tải được trang</h2><p>"
                        + engine.getLoadWorker().getException() + "</p>");
            }
        });

        // TODO 4: alert() trong JavaScript → mặc định KHÔNG hiển thị gì, phải tự xử lý
        engine.setOnAlert(ev -> AlertUtil.info("[JavaScript] " + ev.getData()));

        // TODO 5: Load HTML string + gọi JS
        btnLocal.setOnAction(e -> engine.loadContent("""
                <html><body style="font-family:Segoe UI">
                  <h1>Xin chào từ HTML nội bộ</h1>
                  <a href="https://openjfx.io">OpenJFX</a> | <a href="https://gluonhq.com">Gluon</a>
                  <p><button onclick="alert('Hi from JavaScript!')">Gọi alert()</button></p>
                </body></html>"""));
        btnCount.setOnAction(e -> {
            if (engine.getLoadWorker().getState() != Worker.State.SUCCEEDED) { AlertUtil.warn("Trang chưa tải xong"); return; }
            Object n = engine.executeScript("document.getElementsByTagName('a').length");   // trả về Integer
            AlertUtil.info("Trang có " + n + " liên kết");
        });

        engine.load(HOME);
        HBox bar = new HBox(6, btnBack, btnFwd, btnReload, btnHome, tfUrl, btnLocal, btnCount);
        bar.setPadding(new Insets(6));
        VBox box = new VBox(bar, pb, web);
        VBox.setVgrow(web, Priority.ALWAYS);
        return box;
    }

    /** Thêm https:// nếu thiếu; null nếu không hợp lệ */
    static String normalize(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String url = raw.trim();
        if (!url.matches("(?i)^(https?|file)://.*")) url = "https://" + url;
        try {
            URI uri = new URI(url);
            return uri.getHost() == null && !url.startsWith("file") ? null : uri.toString();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    // ================= MEDIA PLAYER =================
    private Node buildPlayer(Stage stage) {
        Button btnOpen = new Button("📂 Mở file"), btnPlay = new Button("▶ Play"), btnStop = new Button("⏹ Stop");
        Slider slTime = new Slider(0, 1, 0), slVolume = new Slider(0, 1, 0.7);
        Label lblTime = new Label("00:00 / 00:00");
        CheckBox chkLoop = new CheckBox("Lặp lại");
        HBox.setHgrow(slTime, Priority.ALWAYS);
        HBox controls = new HBox(8, btnPlay, btnStop, slTime, lblTime, new Label("🔊"), slVolume, chkLoop);
        controls.setDisable(true);

        StackPane screen = new StackPane(mediaView, new Label("Chưa mở file"));
        screen.setStyle("-fx-background-color: black;");
        mediaView.fitWidthProperty().bind(screen.widthProperty());
        mediaView.fitHeightProperty().bind(screen.heightProperty());
        mediaView.setPreserveRatio(true);

        // TODO 6: Mở file
        btnOpen.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Media", "*.mp4", "*.mp3", "*.wav", "*.m4a"));
            File f = fc.showOpenDialog(stage);
            if (f == null) return;
            if (player != null) {                                  // giải phóng player cũ
                player.volumeProperty().unbindBidirectional(slVolume.valueProperty());
                player.dispose();
            }

            final Media media;                                     // khai báo NGOÀI try để các lambda bên dưới dùng được
            try {
                media = new Media(f.toURI().toString());
                player = new MediaPlayer(media);
            } catch (MediaException ex) {
                AlertUtil.error("Không hỗ trợ định dạng: " + ex.getMessage());
                return;
            }
            mediaView.setMediaPlayer(player);
            screen.getChildren().setAll(mediaView);
            controls.setDisable(false);

            // TODO 7: Gắn sự kiện cho player mới
            player.setOnError(() -> AlertUtil.error("Lỗi phát: " + player.getError().getMessage()));
            player.setOnReady(() -> {
                slTime.setMax(media.getDuration().toSeconds());
                btnOpen.setText("📂 " + f.getName());   // không setTitle: titleProperty đang bind ở tab Browser
            });
            player.currentTimeProperty().addListener((o, a, t) -> {
                if (!slTime.isValueChanging()) slTime.setValue(t.toSeconds());   // không giật khi đang kéo
                lblTime.setText(fmt(t) + " / " + fmt(media.getDuration()));
            });
            player.volumeProperty().bindBidirectional(slVolume.valueProperty());  // cùng kiểu DoubleProperty
            player.cycleCountProperty().bind(Bindings.when(chkLoop.selectedProperty())
                    .then(MediaPlayer.INDEFINITE).otherwise(1));
            player.statusProperty().addListener((o, a, st) ->
                    btnPlay.setText(st == MediaPlayer.Status.PLAYING ? "⏸ Pause" : "▶ Play"));
            player.setOnEndOfMedia(() -> { if (!chkLoop.isSelected()) { player.stop(); } });
            player.play();
        });

        // TODO 8: Điều khiển
        btnPlay.setOnAction(e -> {
            if (player == null) return;
            if (player.getStatus() == MediaPlayer.Status.PLAYING) player.pause(); else player.play();
        });
        btnStop.setOnAction(e -> { if (player != null) player.stop(); });
        slTime.valueChangingProperty().addListener((o, wasChanging, changing) -> {
            if (!changing && player != null) player.seek(Duration.seconds(slTime.getValue()));   // thả chuột → tua
        });
        slTime.setOnMouseClicked(e -> { if (player != null) player.seek(Duration.seconds(slTime.getValue())); });

        VBox box = new VBox(8, btnOpen, screen, controls);
        box.setPadding(new Insets(8));
        VBox.setVgrow(screen, Priority.ALWAYS);
        return box;
    }

    private static String fmt(Duration d) {
        if (d == null || d.isUnknown()) return "--:--";
        int s = (int) d.toSeconds();
        return "%02d:%02d".formatted(s / 60, s % 60);
    }

    // TODO 9: Giải phóng tài nguyên
    @Override
    public void stop() {
        if (player != null) player.dispose();
    }
}
```

### Validation cần kiểm tra
- URL rỗng/sai cú pháp → cảnh báo; thiếu giao thức → tự thêm `https://`
- Tải trang thất bại (mất mạng, sai domain) → hiện trang báo lỗi thay vì trắng
- Chỉ đếm thẻ `<a>` khi trang tải xong
- File media không hỗ trợ / hỏng → Alert lỗi, không crash; Cancel FileChooser → không làm gì

### Checklist hoàn thành
- [ ] Gõ `openjfx.io` + Enter → tải được trang (tự thêm https)
- [ ] ◀ ▶ disable đúng lúc; ⟳, 🏠 hoạt động; ô URL cập nhật khi click link
- [ ] Thanh progress hiện khi đang tải, ẩn khi xong; tiêu đề cửa sổ = tiêu đề trang
- [ ] Trang nội bộ: bấm nút → Alert JavaFX hiển thị nội dung `alert()`
- [ ] "Đếm thẻ <a>" trả về số đúng
- [ ] Mở mp4 có hình, mp3 có tiếng; Play/Pause đổi nhãn; Stop về đầu
- [ ] Kéo slider tua được, slider tự chạy theo thời gian phát; âm lượng hoạt động
- [ ] Mở file mới → file cũ dừng hẳn (đã `dispose`)
- [ ] Đã khai báo `javafx-web`, `javafx-media` trong pom và `requires` trong module-info

---

## T6 — 3D Viewer (Bonus)

### Mục tiêu
- Dùng `Box`, `Sphere`, `Cylinder`, `PhongMaterial`, `PerspectiveCamera`, `SubScene` (depth buffer)
- Xoay mô hình bằng chuột (`Rotate` transform), zoom bằng con lăn, ánh sáng `PointLight`/`AmbientLight`

### Kiến thức (Chapter 08)
§16 3D Graphics · §10 Transform

### Mô tả
`SubScene` 3D ở giữa chứa 3 khối (hộp, cầu, trụ). Panel trái: `ComboBox` chọn khối, `ColorPicker` màu, `Slider` kích thước, CheckBox **Wireframe**, CheckBox **Tự xoay**. Kéo chuột để xoay cả nhóm, lăn chuột để zoom, phím `R` reset góc nhìn.

### TODOs

```java
package com.fu.ch08.t6;

public class Viewer3DApp extends Application {
    private final Rotate rx = new Rotate(-20, Rotate.X_AXIS), ry = new Rotate(30, Rotate.Y_AXIS);
    private double lastX, lastY;

    @Override
    public void start(Stage stage) {
        // TODO 1: Kiểm tra hỗ trợ 3D
        if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
            AlertUtil.error("Máy không hỗ trợ 3D (kiểm tra driver card đồ họa)");
            Platform.exit(); return;
        }

        // TODO 2: Khối 3D + vật liệu
        Box box = new Box(100, 100, 100);          box.setTranslateX(-160);
        Sphere sphere = new Sphere(60);
        Cylinder cyl = new Cylinder(40, 130);      cyl.setTranslateX(160);
        Map<String, Shape3D> shapes = new LinkedHashMap<>();
        shapes.put("Hộp", box); shapes.put("Cầu", sphere); shapes.put("Trụ", cyl);
        Color[] colors = {Color.CRIMSON, Color.DODGERBLUE, Color.SEAGREEN};
        int i = 0;
        for (Shape3D s : shapes.values()) s.setMaterial(new PhongMaterial(colors[i++]));

        Group model = new Group(box, sphere, cyl);
        model.getTransforms().addAll(rx, ry);

        // TODO 3: Ánh sáng + camera + SubScene có depth buffer
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(-300); light.setTranslateY(-300); light.setTranslateZ(-400);
        Group world = new Group(model, light, new AmbientLight(Color.gray(0.3)));
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1); camera.setFarClip(5000); camera.setTranslateZ(-700);
        SubScene sub = new SubScene(world, 800, 600, true, SceneAntialiasing.BALANCED);
        sub.setFill(Color.web("#202530"));
        sub.setCamera(camera);

        // TODO 4: Panel điều khiển
        ComboBox<String> cbShape = new ComboBox<>(FXCollections.observableArrayList(shapes.keySet()));
        cbShape.setValue("Hộp");
        ColorPicker cp = new ColorPicker(colors[0]);
        Slider slSize = new Slider(0.3, 2, 1);
        CheckBox chkWire = new CheckBox("Wireframe"), chkAuto = new CheckBox("Tự xoay");
        cbShape.valueProperty().addListener((o, a, n) -> {
            Shape3D s = shapes.get(n);
            cp.setValue(((PhongMaterial) s.getMaterial()).getDiffuseColor());
            slSize.setValue(s.getScaleX());
            chkWire.setSelected(s.getDrawMode() == DrawMode.LINE);
        });
        cp.setOnAction(e -> ((PhongMaterial) shapes.get(cbShape.getValue()).getMaterial()).setDiffuseColor(cp.getValue()));
        slSize.valueProperty().addListener((o, a, v) -> {
            Shape3D s = shapes.get(cbShape.getValue());
            s.setScaleX(v.doubleValue()); s.setScaleY(v.doubleValue()); s.setScaleZ(v.doubleValue());
        });
        chkWire.setOnAction(e -> shapes.get(cbShape.getValue()).setDrawMode(chkWire.isSelected() ? DrawMode.LINE : DrawMode.FILL));

        // TODO 5: Tự xoay bằng Timeline
        Timeline spin = new Timeline(new KeyFrame(Duration.millis(16), e -> ry.setAngle(ry.getAngle() + 0.5)));
        spin.setCycleCount(Animation.INDEFINITE);
        chkAuto.selectedProperty().addListener((o, a, on) -> { if (on) spin.play(); else spin.pause(); });

        // TODO 6: Chuột xoay, con lăn zoom (giới hạn khoảng cách camera)
        sub.setOnMousePressed(e -> { lastX = e.getSceneX(); lastY = e.getSceneY(); });
        sub.setOnMouseDragged(e -> {
            ry.setAngle(ry.getAngle() + (e.getSceneX() - lastX) * 0.4);
            rx.setAngle(Math.max(-90, Math.min(90, rx.getAngle() - (e.getSceneY() - lastY) * 0.4)));
            lastX = e.getSceneX(); lastY = e.getSceneY();
        });
        sub.setOnScroll(e -> camera.setTranslateZ(Math.max(-2000, Math.min(-200, camera.getTranslateZ() + e.getDeltaY()))));

        VBox panel = new VBox(8, new Label("Khối"), cbShape, new Label("Màu"), cp, new Label("Kích thước"), slSize, chkWire, chkAuto,
                new Label("Kéo chuột: xoay\nLăn chuột: zoom\nR: reset"));
        panel.setPadding(new Insets(10)); panel.setPrefWidth(180);
        BorderPane root = new BorderPane(sub, null, null, null, panel);
        sub.widthProperty().bind(root.widthProperty().subtract(panel.prefWidthProperty()));
        sub.heightProperty().bind(root.heightProperty());

        Scene scene = new Scene(root, 1000, 640);
        scene.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.R) { rx.setAngle(-20); ry.setAngle(30); camera.setTranslateZ(-700); } });
        stage.setTitle("T6 - 3D Viewer");
        stage.setScene(scene);
        stage.show();
    }
}
```

### Checklist hoàn thành
- [ ] Hiển thị 3 khối 3D có ánh sáng, che khuất đúng (depth buffer = `true`)
- [ ] Kéo chuột xoay, lăn chuột zoom có giới hạn, phím R reset
- [ ] Đổi màu / kích thước / wireframe cho khối đang chọn
- [ ] Tự xoay bật/tắt được
- [ ] Máy không hỗ trợ 3D → thông báo thay vì crash

---

## Phần C — Lưu ý quan trọng

### C.1. Môi trường & cấu hình

1. **JavaFX không có sẵn trong JDK ≥ 11** → thêm dependency `org.openjfx` (Maven tự tải đúng bản theo hệ điều hành). Các module cần: `javafx-controls`, `javafx-fxml`; thêm `javafx-web` (WebView), `javafx-media` (Media) khi dùng.
2. **`module-info.java`:**
   - `requires` đủ module đang dùng (`javafx.web`, `javafx.media` không tự có).
   - `opens <package> to javafx.fxml` cho mọi package chứa **controller** (FXMLLoader dùng reflection).
   - `opens <package> to javafx.base` cho package chứa **model** dùng `PropertyValueFactory` trong TableView. Thiếu → cột trống + warning `Can not retrieve property`.
   - `exports <package>` cho package chứa class `Application`.
3. **Không dùng module-info** (classpath mode)? Khi chạy class extends `Application` trực tiếp sẽ gặp *"JavaFX runtime components are missing"* → tạo class `Launcher` có `main()` gọi `Application.launch(App.class, args)`, hoặc dùng `mvn javafx:run`.
4. **Scene Builder 23 + JavaFX 21:** FXML do Scene Builder 23 tạo có `xmlns="http://javafx.com/javafx/23"` → khi chạy với JavaFX 21 hiện warning *"Loading FXML document with JavaFX API of version 23 by JavaFX runtime of version 21"*. Không gây lỗi, nhưng nên sửa thành `/javafx/21` và **không dùng control/thuộc tính chỉ có ở JavaFX 22+**.
5. **Đường dẫn resource:** `getClass().getResource("x.fxml")` (tương đối → cùng package với class) khác `getResource("/x.fxml")` (tuyệt đối → gốc `resources`). Lỗi `Location is not set` = sai đường dẫn; nhớ **Rebuild** để resource được copy vào `target/classes`.

### C.2. FXML & Controller

6. `fx:id` phải **trùng tên** field `@FXML`; `onAction="#ten"` phải trùng tên method. Method/field `private` dùng được nếu có `@FXML` (và package đã `opens`).
7. **Không dùng `@FXML` field trong constructor** của controller (lúc đó còn `null`) → đặt code khởi tạo trong `initialize()`.
8. `fx:controller` chỉ khai báo ở **root** của FXML. Muốn truyền dữ liệu sang màn hình khác → `loader.getController()` rồi gọi setter (B4).
9. Mỗi lần `loader.load()` cần **một `FXMLLoader` mới** (không load lại cùng loader).

### C.3. Scene graph, Stage, Node

10. **Một node chỉ có một parent:** add node đang nằm ở parent A vào parent B → tự bị gỡ khỏi A; add trùng 2 lần vào cùng `children` → `IllegalArgumentException: duplicate children added`.
11. `initStyle()`, `initModality()`, `initOwner()` phải gọi **trước** `show()` và chỉ một lần. `TRANSPARENT` cần thêm `scene.setFill(Color.TRANSPARENT)` và root không có background.
12. `setOnCloseRequest` chỉ chạy khi người dùng bấm nút đóng cửa sổ — **không** chạy khi gọi `stage.close()` hay `Platform.exit()`. Dọn dẹp tài nguyên nên đặt trong `Application.stop()`.
13. Đóng cửa sổ cuối cùng → app thoát (do `Platform.isImplicitExit()` = true). Muốn chạy ẩn (system tray) → `Platform.setImplicitExit(false)`.
14. `visible=false` vẫn chiếm chỗ trong layout; muốn không chiếm chỗ → thêm `managed=false` (B4, B5).

### C.4. Properties & Binding

15. Property đã `bind()` thì **không `set()` được** (`RuntimeException: A bound value cannot be set`) → thay đổi nguồn, hoặc `unbind()` trước (B3 reset nút Đăng ký, T2 animation).
16. `bindBidirectional` yêu cầu **cùng kiểu**: `Slider.valueProperty()` (`DoubleProperty`) ≠ `Spinner.valueProperty()` (`ObjectProperty<Double>`) → dùng `asObject()` và **giữ tham chiếu** vào field, vì bidirectional binding dùng weak reference. Ví dụ trong Chapter 08 `slider.valueProperty().bindBidirectional(spinner.getValueFactory().valueProperty())` sẽ **không biên dịch được**.
17. Một property chỉ bind 1 chiều với **một** nguồn tại một thời điểm; bind nguồn mới sẽ thay thế nguồn cũ.
18. `ObservableList` thông thường chỉ báo khi thêm/xóa phần tử. Muốn báo cả khi **property bên trong phần tử đổi** → tạo list với **extractor** `FXCollections.observableArrayList(item -> new Observable[]{...})` (T4).

### C.5. Concurrency

19. **Chỉ FX Application Thread được sửa scene graph.** Trong `Task.call()` chỉ dùng `updateProgress/updateMessage/updateValue`; cần cập nhật control khác → `Platform.runLater` (T3).
20. `setOnSucceeded/Failed/Cancelled` **đã chạy trên FX thread** — không cần `runLater` thêm.
21. **Luôn đăng ký `setOnFailed`**: exception trong `call()` bị Task "nuốt" mất nếu không xử lý.
22. `Task` chỉ chạy **1 lần**; muốn chạy lại → tạo Task mới hoặc dùng `Service<V>` (`restart()`).
23. `task.cancel()` chỉ đặt cờ + interrupt thread → trong `call()` phải tự kiểm tra `isCancelled()` và xử lý `InterruptedException`.
24. Thread tự tạo nên `setDaemon(true)` hoặc `shutdownNow()` executor trong `stop()` để app thoát hẳn.
25. Animation/đồng hồ định kỳ → ưu tiên `Timeline` (chạy trên FX thread) thay vì Thread + `sleep` + `runLater`.

### C.6. Controls, Chart, Media, Web

26. `ComboBox` **editable**: giá trị gõ tay nằm ở `getEditor().getText()` cho tới khi commit (Enter/mất focus) (B6).
27. `Spinner` editable: giá trị gõ tay chỉ cập nhật `getValue()` khi nhấn Enter.
28. Chart dùng `CategoryAxis` mà thay dữ liệu liên tục → `setAnimated(false)` để tránh nhãn trục chồng/lệch. `Data.getNode()` chỉ có sau khi data đã được add vào chart.
29. `PropertyValueFactory` không dùng được với `record` (accessor `name()` không phải `getName()`) → dùng lambda `c -> new SimpleStringProperty(c.getValue().name())`.
30. `WebView`: `alert()`/`confirm()` của JS mặc định không hiện gì → `engine.setOnAlert(...)`, `setConfirmHandler(...)`. `executeScript` chỉ gọi khi trang đã `SUCCEEDED`.
31. `Media` hỗ trợ: MP4 (H.264/AAC), MP3, WAV, AIFF, M4A, FLV… **không** hỗ trợ MKV, AVI, WMV. Luôn `dispose()` `MediaPlayer` cũ.
32. CSS JavaFX: thuộc tính có tiền tố `-fx-`; thứ tự ưu tiên: inline `setStyle()` > stylesheet của Scene/Parent > `setXxx()` trong code > Modena (mặc định). Dùng `styleClass` + file `.css` thay vì inline để dễ bảo trì.

### C.7. Xử lý sự kiện & form

33. `setOnXxx(...)` chỉ giữ **một** handler — gán lần 2 sẽ **thay thế** lần 1. Muốn nhiều handler cho cùng loại sự kiện → `addEventHandler(ActionEvent.ACTION, ...)` (B1).
34. Sự kiện đi 2 pha: **capturing** (event filter, từ Stage → node đích) rồi **bubbling** (event handler, từ node đích → Stage). `event.consume()` dừng lan truyền. Muốn bắt phím ở cấp cửa sổ trước khi TextField "nuốt" mất → `scene.addEventFilter(KeyEvent.KEY_PRESSED, ...)` (B1 phím ESC).
35. **Event** ≠ **ChangeListener**: event là hành động của người dùng (click, gõ phím); listener chạy mỗi khi **giá trị property đổi — kể cả do code gọi `setValue()`/`clear()`**. Vì vậy reset form sẽ kích hoạt listener validate → phải xóa label lỗi **sau cùng** (B3). `ComboBox.setOnAction` cũng chạy khi code gọi `setValue(null)` → luôn kiểm tra `null` (B1).
36. `TextField` có `onAction` thì Enter **không** kích hoạt `defaultButton` nữa (B1 ô Tên khách). Không gán `onAction` → Enter "chuyển tiếp" tới nút default.
37. Validate nhiều field: dùng `&` (không phải `&&`) để **chạy hết** các validator và hiện mọi lỗi cùng lúc; `&&` dừng ở lỗi đầu tiên (B3).
38. Validate khi mất focus (`focusedProperty().addListener`) — **không** mở `Alert` trong listener này: Alert lấy focus → ô nhập lại mất focus → lặp vô hạn. Dùng label lỗi inline (B3).
39. Chuyển màn hình trong cùng cửa sổ: `scene.setRoot(newRoot)` (B2, B3) — nhẹ hơn tạo `Scene`/`Stage` mới. Một node chỉ thuộc **một** parent: muốn dùng lại cả màn hình → giữ tham chiếu root (B3 `registerView`) thay vì add lại từng control vào layout mới.

---

## Phần D — Checklist kiểm tra tổng

### D.1. Cấu hình project
- [ ] JDK 21, JavaFX 21 trong `pom.xml`; `mvn clean javafx:run` chạy được
- [ ] `module-info.java`: đủ `requires`, `opens` (javafx.fxml / javafx.base), `exports`
- [ ] FXML, CSS nằm trong `src/main/resources/<cùng đường dẫn package>`
- [ ] Mỗi bài 1 package, đặt tên class rõ ràng (`XxxApp`, `XxxController`)

### D.2. Kiến thức cốt lõi Chapter 08
- [ ] Giải thích được Stage → Scene → Scene Graph → Node; root node; layout vs control
- [ ] Nêu đúng thread của `init()`, `start()`, `stop()`
- [ ] Phân biệt `show()` / `showAndWait()`; 5 `StageStyle`; `Modality`
- [ ] Phân biệt `layoutBounds` / `boundsInLocal` / `boundsInParent`
- [ ] Viết được model class theo JavaFX Property convention
- [ ] Dùng được bind 1 chiều, 2 chiều, `NumberBinding`, `Bindings.when`, `createXxxBinding`
- [ ] Đăng ký sự kiện bằng anonymous class / lambda / method reference; phân biệt handler, filter, listener
- [ ] Xây form đăng nhập/đăng ký bằng code: validate inline, chuyển màn hình bằng `scene.setRoot()`
- [ ] Liên kết được FXML ↔ Controller (`fx:controller`, `fx:id`, `#handler`, `initialize`)
- [ ] Chọn đúng layout cho từng tình huống (BorderPane khung, GridPane form, FlowPane/TilePane lưới…)
- [ ] Dùng được ≥ 15 loại control
- [ ] Vẽ shape 2D, áp transform/effect/animation; dựng cảnh 3D cơ bản
- [ ] Hiển thị chart từ dữ liệu động
- [ ] Nhúng WebView, phát Media
- [ ] Chạy tác vụ nền bằng `Task`; cập nhật UI an toàn

### D.3. Chất lượng giao diện & xử lý lỗi
- [ ] Mọi input đều được validate; thông báo lỗi rõ ràng, tiếng Việt
- [ ] Không có exception/stack trace hiện ra console khi thao tác bình thường
- [ ] Nút hành động disable khi chưa đủ điều kiện (ưu tiên binding)
- [ ] Hộp thoại xác nhận trước thao tác xóa/thoát
- [ ] `FileChooser` / `Dialog` xử lý cả trường hợp Cancel (`null` / `Optional.empty()`)
- [ ] Cửa sổ resize không vỡ layout; có kích thước tối thiểu hợp lý
- [ ] Style đặt trong file CSS, không lạm dụng `setStyle()`

### D.4. Concurrency & tài nguyên
- [ ] Không có tác vụ > 100ms chạy trên FX thread
- [ ] Không cập nhật control từ background thread (không có `IllegalStateException: Not on FX application thread`)
- [ ] Có `setOnFailed` cho mọi Task
- [ ] Đóng app → process kết thúc hoàn toàn (không còn thread/MediaPlayer treo)

### D.5. Bảng theo dõi tiến độ

| Bài | Nội dung | Hoàn thành | Ghi chú |
|---|---|:---:|---|
| B1 | Controls & Xử lý sự kiện (Đặt đồ uống) | ☐ | |
| B2 | Layout thông dụng & Form đăng nhập (code) | ☐ | |
| B3 | Form đăng ký + Validate + Đăng nhập (code) | ☐ | |
| B4 | FXML + Controller + CSS (Đăng nhập) | ☐ | |
| B5 | Layout components | ☐ | |
| B6 | Controls — Khảo sát | ☐ | |
| T1 | TreeView + TableView + CSV | ☐ | |
| T2 | Shape Editor | ☐ | |
| T3 | Concurrency | ☐ | |
| T4 | Charts Dashboard | ☐ | |
| T5 | WebView + Media | ☐ | |
| T6 | 3D Viewer (bonus) | ☐ | |

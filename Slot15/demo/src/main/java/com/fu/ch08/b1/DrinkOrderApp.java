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


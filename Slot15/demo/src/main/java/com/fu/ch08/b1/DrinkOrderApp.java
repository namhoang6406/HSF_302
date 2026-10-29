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

        // TODO 9: Event FILTER trên Scene — chạy ở pha capturing, TRƯỚC khi control con nhận phím
    }

    private void registerEvents() {
        // TODO 4: Cách 1 — method reference (gọn khi logic dài, tách ra method riêng)

        // TODO 5: Cách 2 — lambda (phổ biến nhất)

        // TODO 6: Cách 3 — anonymous class (viết "dài" để hiểu bản chất: EventHandler là interface 1 method handle)

        // TODO 7: Một handler dùng chung cho nhiều CheckBox — phân biệt bằng getSource()

        // TODO 8: ChangeListener — lắng nghe GIÁ TRỊ thay đổi (do người dùng hoặc do code)

        // TODO 10: MouseEvent
    }

    // TODO 11: Xử lý chính — đọc giá trị, validate, tính tiền
    private void handleCalc(ActionEvent e) {
    }

    private void showError(String msg) {
        lblResult.setStyle("-fx-text-fill: #d93025;");
        lblResult.setText(msg);
        log("Lỗi: " + msg);
    }

    // TODO 12: Đưa form về trạng thái ban đầu
    private void reset() {
    }

    private void log(String msg) {
        taLog.appendText("[%s] %s%n".formatted(
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), msg));
    }

    public static void main(String[] args) {
        launch(args);
    }
}


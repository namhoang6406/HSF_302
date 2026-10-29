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
    }

    // TODO 3: Giao diện — GridPane 2 cột, mỗi ô nhập kèm label lỗi bên dưới
    private Parent buildRegisterView() {
        return new Region();
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

    private boolean vFullName() { return true; }
    private boolean vEmail() { return true; }
    private boolean vUsername() { return true; }
    private boolean vPassword() { return true; }
    private boolean vConfirm() { return true; }
    private boolean vGender() { return true; }
    private boolean vDob() { return true; }
    private boolean vMajor() { return true; }

    // TODO 6: Độ mạnh mật khẩu (0–4 điểm)
    private void updateStrength(String s) {
    }

    // TODO 7: Đăng ký
    private void handleRegister() {
    }

    // TODO 8: Làm lại form
    private void resetForm() {
    }

    // TODO 9: Form đăng nhập — kiểm tra với UserStore
    private Parent buildLoginView(String prefillUser) {
        return new Region();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

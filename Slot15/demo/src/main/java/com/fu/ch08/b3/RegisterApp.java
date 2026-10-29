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


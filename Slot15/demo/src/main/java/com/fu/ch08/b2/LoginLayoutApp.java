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


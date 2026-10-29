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
    // TODO 4: Xử lý sự kiện đăng nhập, thoát, listener
    private Node buildCenter() {
        return new Region();
    }

    // TODO 5: Footer — HBox: CheckBox debug | spacer | bản quyền
    private Node buildFooter() {
        return new Region();
    }

    // TODO 6: Trang chủ — BorderPane: top header, left menu VBox, center lời chào
    private Parent buildHomeView(String user) {
        return new Region();
    }

    private <T extends Region> T track(T region) {
        containers.add(region);
        return region;
    }

    public static void main(String[] args) {
        launch(args);
    }
}


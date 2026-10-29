package fu.de200319.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberCheckBox;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Hyperlink registerLink;

    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean remember = rememberCheckBox.isSelected();

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Remember me: " + remember);

        // Xử lý logic đăng nhập tại đây
        // Kiểm tra thông tin đăng nhập
        if ("admin".equals(username) && "1234567".equals(password)) {
            showSuccessModal(username, rememberCheckBox.isSelected());
        } else {
            showErrorAlert();
        }
    }

    private void showSuccessModal(String username, boolean remember) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Đăng nhập thành công");
        alert.setHeaderText("Xin chào, " + username + "!");
        alert.setContentText("Bạn đã đăng nhập thành công!" + (remember ? "\nTrạng thái: Đã ghi nhớ đăng nhập." : ""));
        alert.showAndWait();
    }

    private void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText("Đăng nhập thất bại");
        alert.setContentText("Tên đăng nhập hoặc mật khẩu không chính xác!");
        alert.showAndWait();
    }

    @FXML
    void handleForgotPassword(ActionEvent event) {
        System.out.println("Chuyển tới màn hình Quên mật khẩu");
    }

    @FXML
    void handleRegister(ActionEvent event) {
        System.out.println("Chuyển tới màn hình Đăng ký");
    }
}

package fu.de200319.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WelcomeController {

    @FXML
    private Label lblWelcomeMessage;

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblRole;

    @FXML
    private Label lblLoginTime;

    @FXML
    private Button btnLogout;

    public void setSessionData(String username, String role) {
        lblWelcomeMessage.setText("Chào mừng " + username + " đã đăng nhập!");
        lblUsername.setText(username);
        lblRole.setText(role);
        lblLoginTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")));
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Stage currentStage = (Stage) btnLogout.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 480, 580);
            currentStage.setTitle("Hệ thống đăng nhập");
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

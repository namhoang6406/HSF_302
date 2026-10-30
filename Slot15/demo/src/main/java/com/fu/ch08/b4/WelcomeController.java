package com.fu.ch08.b4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WelcomeController {
    @FXML private Label lblWelcome;

    public void setUser(String user, String role) {
        lblWelcome.setText("Xin chào %s (%s)!".formatted(user, role));
    }

    @FXML
    private void handleLogout(ActionEvent e) {
        ((Stage) lblWelcome.getScene().getWindow()).close();
    }
}

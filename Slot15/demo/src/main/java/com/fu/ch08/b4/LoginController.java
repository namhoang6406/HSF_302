package com.fu.ch08.b4;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Map;

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
}

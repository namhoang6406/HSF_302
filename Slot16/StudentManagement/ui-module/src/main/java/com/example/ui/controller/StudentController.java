package com.example.ui.controller;

import com.example.data.pojo.Student;
import com.example.data.service.StudentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class StudentController {

    @FXML private TableView<Student> tableView;
    @FXML private TextField searchField, idField, nameField, emailField, ageField;
    @FXML private Button btnUpdate, btnDelete;
    @FXML private Label messageLabel, statusLabel;

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @FXML
    public void initialize() {
        // Will be completed in TODO #5
    }

    @FXML
    private void handleSearch() {}

    @FXML
    private void handleRefresh() {}

    @FXML
    private void handleAdd() {}

    @FXML
    private void handleUpdate() {}

    @FXML
    private void handleDelete() {}

    @FXML
    private void handleClear() {}
}

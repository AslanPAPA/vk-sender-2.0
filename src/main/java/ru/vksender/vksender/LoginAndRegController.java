package ru.vksender.vksender;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginAndRegController {

    @FXML
    private Button signin_close;

    @FXML
    private Hyperlink signin_createAccount;

    @FXML
    private AnchorPane signin_form;

    @FXML
    private Button signin_loginBtn;

    @FXML
    private Button signin_minimize;

    @FXML
    private PasswordField signin_password;

    @FXML
    private TextField signup_username;

    @FXML
    private TextField signup_password;

    @FXML
    private TextField signin_username;

    @FXML
    private Hyperlink signup_alrdyHaveAcc;

    @FXML
    private Button signup_btn;

    @FXML
    private Button signup_close;

    @FXML
    private TextField signup_email;

    @FXML
    private AnchorPane signup_form;

    @FXML
    private Button signup_minimize;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    public boolean validEmail() {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+([.][a-zA-Z]+)+");

        Matcher match = pattern.matcher(signup_email.getText());

        Alert alert;

        if (match.find() && match.group().matches(signup_email.getText())) {
            return true;
        } else {

            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Введите адрес электронной почты в формате someone@example.com");
            alert.showAndWait();
            return false;
        }
    }

    public void signup() {
        String sql = "INSERT INTO admin (email,username,password) VALUES (?,?,?)";

        try (Connection connect = Database.connectDb();
             PreparedStatement prepare = connect.prepareStatement(sql);
             PreparedStatement prepareCheckName = connect.prepareStatement(
                     "SELECT username FROM admin WHERE username = ?")) {

            Alert alert;

            if (signup_email.getText().isEmpty() || signup_username.getText().isEmpty()
                    || signup_password.getText().isEmpty()) {
                showAlert(AlertType.ERROR, "Ошибка", "Пожалуйста, заполните все поля.");
                return;
            }

            if (signup_password.getText().length() < 8) {
                showAlert(AlertType.ERROR, "Ошибка", "Длина пароля должна быть не менее 8 символов!");
                return;
            }

            if (!validEmail()) {
                return;
            }

            prepareCheckName.setString(1, signup_username.getText());
            try (ResultSet result = prepareCheckName.executeQuery()) {
                if (result.next()) {
                    showAlert(AlertType.ERROR, "ОШИБКА", "Логин " + signup_username.getText() + " уже существует!");
                    return;
                }
            }

            prepare.setString(1, signup_email.getText());
            prepare.setString(2, signup_username.getText());
            prepare.setString(3, signup_password.getText());
            prepare.executeUpdate();

            showAlert(AlertType.INFORMATION, "Информация", "Аккаунт успешно создан!");
            signup_email.clear();
            signup_username.clear();
            signup_password.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void signin() {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";

        try (Connection connect = Database.connectDb();
             PreparedStatement prepare = connect.prepareStatement(sql)) {

            if (connect == null) {
                showAlert(AlertType.ERROR, "Ошибка", "Не удалось подключиться к базе данных.");
                return;
            }

            if (signin_username.getText().isEmpty() || signin_password.getText().isEmpty()) {
                showAlert(AlertType.ERROR, "Ошибка", "Пожалуйста, заполните все поля.");
                return;
            }

            prepare.setString(1, signin_username.getText());
            prepare.setString(2, signin_password.getText());

            try (ResultSet result = prepare.executeQuery()) {
                if (result.next()) {
                    getData.username = signin_username.getText();
                    if ("aslADMIN".equalsIgnoreCase(signin_username.getText())) {
                        getData.isAdmin = true;
                        showAlert(AlertType.INFORMATION, "Информация", "Вход выполнен как АДМИНИСТРАТОР!");
                    } else {
                        getData.isAdmin = false;
                        showAlert(AlertType.INFORMATION, "Информация", "Успешный вход!");
                    }


                    signin_loginBtn.getScene().getWindow().hide();

                    Parent root = FXMLLoader.load(getClass().getResource("/ru/vksender/vksender/Dashboard.fxml"));
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.initStyle(StageStyle.TRANSPARENT);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    showAlert(AlertType.ERROR, "Ошибка", "Неправильный логин или пароль.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void switchForm(ActionEvent event) {
        if (event.getSource() == signin_createAccount) {
            signin_form.setVisible(false);
            signup_form.setVisible(true);
        } else if (event.getSource() == signup_alrdyHaveAcc) {
            signup_form.setVisible(false);
        }
        signin_form.setVisible(true);
    }

    public void signInClose() {
        System.exit(0);
    }

    public void signInMinimize() {
        Stage stage = (Stage) signin_form.getScene().getWindow();
        stage.setIconified(true);
    }

    public void signUpClose() {
        System.exit(0);
    }

    public void signUpMinimize() {
        Stage stage = (Stage) signup_form.getScene().getWindow();
        stage.setIconified(true);
    }

    public void initialize() {

    }
}

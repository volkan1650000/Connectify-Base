package com.example.socialmedia.UI.Controllers;

import com.example.socialmedia.DataBaseUtility.Database;
import com.example.socialmedia.Models.Users;
import com.example.socialmedia.UI.Inside.Dashboard;
import com.example.socialmedia.UI.Inside.MessagesUI;
import com.example.socialmedia.UI.Inside.Profile;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    private final Scene scene;
    private final Stage stage;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorMessage;

    public LoginController(Scene scene, Stage stage) {
        this.scene = scene;
        this.stage = stage;
        createLogin();
    }

    public void createLogin() {
        VBox root = new VBox(10);
        root.setPrefSize(300, 200);
        root.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Login Page");
        usernameField = new TextField();
        usernameField.setPromptText("Username or Email");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button loginButton = new Button("Login");
        Button createAccountButton = new Button("Create Account");

        // Set action for the Create Account button
        createAccountButton.setOnAction(e -> createAccount());

        loginButton.setOnAction(this::handleLogin);

        errorMessage = new Label(); // Initialize the errorMessage

        root.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton, createAccountButton, errorMessage);
        scene.setRoot(root);
    }

    private void handleLogin(ActionEvent event) {
        Database db = new Database();
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();
            PreparedStatement ps = db.getConnection().prepareStatement("select * from users where password = ? and (username = ? or email = ?)");
            ps.setString(1,password);
            ps.setString(2,username);
            ps.setString(3,username);
            ResultSet rs = ps.executeQuery();
            boolean authenticationSuccessful = rs.next();
            if (authenticationSuccessful) {
                System.out.println("Successfully logged in");

                Profile.username = rs.getString(1);
                Profile.email = rs.getString(2);
                Profile.password = rs.getString(3);
                Profile.profilePictureUrl = rs.getString(4);
                Profile.bio = rs.getString(5);

                Dashboard dashboardController = new Dashboard(scene, stage);
                dashboardController.createDashboard();
                MessagesUI.initialize();
            } else {
                errorMessage.setText("Invalid username/email or password");
                errorMessage.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
    }

    private void createAccount(){
        VBox root = new VBox(10);
        root.setPrefSize(300, 250);
        root.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Create Account");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        // Labels for error messages
        Label usernameErrorLabel = new Label();
        Label passwordErrorLabel = new Label();
        Label emailErrorLabel = new Label();

        Button createAccountButton = new Button("Create Account");
        createAccountButton.setOnAction(e -> handleCreateAccount(usernameField.getText(), passwordField.getText(), emailField.getText(), usernameErrorLabel, passwordErrorLabel, emailErrorLabel));

        root.getChildren().addAll(titleLabel, usernameField, usernameErrorLabel, passwordField, passwordErrorLabel, emailField, emailErrorLabel, createAccountButton);
        scene.setRoot(root);

    }

    private void handleCreateAccount(String username, String password, String email, Label usernameErrorLabel, Label passwordErrorLabel, Label emailErrorLabel) {
        boolean okUsername = true;
        boolean okPassword = true;
        boolean okEmail = true;

        if (!isNameUsable(username)) {
            usernameErrorLabel.setText("Username is taken");
            okUsername = false;
        }
        if (username.length() > 30) {
            usernameErrorLabel.setText("Username can't be longer than 30 characters");
            okUsername = false;
        }
        if (username.length() < 5) {
            usernameErrorLabel.setText("Username can't be less than 5 characters");
            okUsername = false;
        }
        if(okUsername){
            usernameErrorLabel.setText("");
        }


        if (password.replaceAll("[^A-Z]","").length()==0) {
            passwordErrorLabel.setText("Password must contain at least one uppercase letter");
            okPassword = false;
        }
        if (password.replaceAll("[^a-z]","").length()==0) {
            passwordErrorLabel.setText("Password must contain at least one lowercase letter");
            okPassword = false;
        }
        if (password.replaceAll("[^0-9]","").length()==0) {
            passwordErrorLabel.setText("Password must contain at least one digit");
            okPassword = false;
        }
        if (password.length() < 7) {
            passwordErrorLabel.setText("Password can't be less than 7 characters");
            okPassword = false;
        }else if (password.length() > 50) {
            passwordErrorLabel.setText("Password can't be longer than 50 characters");
            okPassword = false;
        }
        if(okPassword){
            passwordErrorLabel.setText("");
        }

        if (!email.contains("@") || !email.endsWith(".com")) {
            emailErrorLabel.setText("Enter a valid email");
            okEmail = false;
        }
        if (email.length() > 50) {
            emailErrorLabel.setText("Email can't be longer than 50 characters");
            okEmail = false;
        }
        if(okEmail){
            emailErrorLabel.setText("");
        }
        boolean okToGo = okEmail && okPassword && okUsername;
        if(okToGo){
            Users user = new Users(username,email,password);
            user.addUser();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Account Created");
            alert.setHeaderText(null);
            alert.setContentText("Your account has been created successfully! Click OK to return to the login page.");
            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButtonType);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == okButtonType) {
                    createLogin(); // This needs to be called with the LoginController context
                }
            });
        }
    }

    public static boolean isNameUsable(String username) {
        Database db = new Database();
        try (PreparedStatement ps = db.getConnection().prepareStatement("select username from users where username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ERROR");
            return false;
        }
        db.close();
        return true;
    }
}


package com.example.socialmedia.UI.Inside;

import com.example.socialmedia.Models.Users;
import com.example.socialmedia.UI.Controllers.LoginController;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class Dashboard {
    private final Scene scene;
    private final Stage stage;


    public Dashboard(Scene scene, Stage stage) {
        this.scene = scene;
        this.stage = stage;
    }

    public void createDashboard() {
        BorderPane root = new BorderPane();
        root.setPrefSize(600, 400); // Set your preferred size

        // Top area for Search functionality, Messages button, and Logout button
        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.TOP_RIGHT); // Align to the top-right corner

        TextField searchField = new TextField();
        searchField.setPromptText("Enter username to search");
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String searchedWord = searchField.getText();
            List<String> foundUsersList = Users.search(searchedWord);
            VBox resultBox = new VBox(10);
            for (String username : foundUsersList) {
                Button userButton = new Button(username);
                userButton.setOnAction(event -> Profile.showProfile(username));
                resultBox.getChildren().add(userButton);
            }
            root.setCenter(resultBox);
        });

        Button messagesButton = new Button("Messages");
        messagesButton.setOnAction(e -> MessagesUI.showMessages()); // Call your showMessages method here

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(this::handleLogout);

        topBox.getChildren().addAll(searchField, searchButton, messagesButton, logoutButton);
        root.setTop(topBox);

        // Bottom area for the "My Profile" button
        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.BOTTOM_RIGHT); // Align to the bottom-right corner

        Button myProfileButton = new Button("My Profile");
        myProfileButton.setOnAction(e -> Profile.showProfile(Profile.username)); // Handle button click to show profile

        bottomBox.getChildren().add(myProfileButton);
        root.setBottom(bottomBox);

        scene.setRoot(root);
    }



    private void handleLogout(ActionEvent event) {
        LoginController loginController = new LoginController(scene, stage);
        loginController.createLogin();
    }
}

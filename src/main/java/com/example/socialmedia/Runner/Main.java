package com.example.socialmedia.Runner;
import com.example.socialmedia.UI.Controllers.LoginController;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends javafx.application.Application{
    @Override
    public void start(Stage primaryStage) {
        // Create a simple layout for the login screen
        VBox root = new VBox();
        Scene loginScene = new Scene(root, 400, 300); // Creating a Scene with a VBox as the root

        // Create a LoginController instance with the Scene and Stage
        new LoginController(loginScene, primaryStage);

        // Set the title for the stage and show the login scene
        primaryStage.setTitle("Login Screen");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

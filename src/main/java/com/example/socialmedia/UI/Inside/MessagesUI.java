package com.example.socialmedia.UI.Inside;

import com.example.socialmedia.Models.Messages;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.List;

import static com.example.socialmedia.Models.Messages.thoseWeHaveMessagesWith;


public class MessagesUI {
    private static Stage messageStage;
    private static VBox messageBox;

    public static void initialize() {
        messageStage = null;
        messageBox = null;
    }

    public static void showMessages() {
        // Retrieve the list of users with whom messages were exchanged
        LinkedHashSet<String> usersWithMessages = thoseWeHaveMessagesWith(Profile.username);

        // Create a new stage for the messages tab
        Stage messagesStage = new Stage();
        messagesStage.setTitle("Messages");

        // Create a VBox to hold the buttons displaying usernames
        VBox usersBox = new VBox(10);
        usersBox.setPadding(new Insets(10));

        // Populate the VBox with clickable buttons for each user
        for (String username : usersWithMessages) {
            Button userButton = new Button(username);
            userButton.setOnAction(event -> showMessageWithUser(username)); // Replace with your showMessageWithUser method
            usersBox.getChildren().add(userButton);
        }

        // Create a scroll pane in case the list of users is too long
        ScrollPane scrollPane = new ScrollPane(usersBox);
        scrollPane.setFitToWidth(true);

        // Set the content of the stage as the scroll pane
        Scene scene = new Scene(scrollPane, 300, 400);
        messagesStage.setScene(scene);

        // Show the messages tab
        messagesStage.show();
    }

    public static void showMessageWithUser(String username) {
        if (messageStage == null) {
            messageStage = new Stage();
            messageStage.setTitle("Messages with " + username);

            messageBox = new VBox(10);
            messageBox.setPadding(new Insets(10));

            ScrollPane scrollPane = new ScrollPane(messageBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(320, 400);

            TextField messageInput = new TextField();
            Button sendButton = new Button("Send");
            sendButton.setOnAction(e -> {
                String content = messageInput.getText();
                Messages newMessage = new Messages(Profile.username, username, content);
                newMessage.addMessage();
                refreshMessages(username);
                messageInput.clear(); // Clear the text field after sending the message
            });

            HBox messageSenderBox = new HBox(10);
            messageSenderBox.setPadding(new Insets(10));
            messageSenderBox.getChildren().addAll(messageInput, sendButton);

            BorderPane messagePane = new BorderPane();
            messagePane.setCenter(scrollPane); // Scrollable messages
            messagePane.setBottom(messageSenderBox); // Input field and send button

            Scene scene = new Scene(messagePane);
            messageStage.setScene(scene);
        }

        refreshMessages(username);
        messageStage.show();
    }

    private static void refreshMessages(String username) {
        messageBox.getChildren().clear();

        List<Integer> messageIDs = Messages.getMessageIDs(Profile.username, username);

        for (Integer messageID : messageIDs) {
            String content = Messages.getContent(messageID);
            String from = Messages.getUsername(messageID);
            if (from == null || content == null) {
                continue;
            }

            Label messageLabel = new Label(content);
            messageLabel.setWrapText(true);

            if (from.equals(Profile.username)) {
                messageLabel.setStyle("-fx-background-color: lightblue;");
                messageLabel.setAlignment(Pos.CENTER_RIGHT);
            } else {
                messageLabel.setStyle("-fx-background-color: lightgreen;");
                messageLabel.setAlignment(Pos.CENTER_LEFT);
            }

            double width = Math.min(content.length() * 8, 300);
            messageLabel.setMaxWidth(width);
            messageLabel.setMinWidth(Region.USE_PREF_SIZE);

            messageBox.getChildren().add(messageLabel);
        }
    }
}
package com.example.socialmedia.UI.Inside;

import com.example.socialmedia.DataBaseUtility.Database;
import com.example.socialmedia.Models.Comments;
import com.example.socialmedia.Models.Likes;
import com.example.socialmedia.Models.Posts;
import com.example.socialmedia.UI.Controllers.LoginController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.socialmedia.Models.Comments.*;


public class Profile {
    public static String username;
    public static String email;
    public static String password;
    public static String profilePictureUrl;
    public static String bio;

    public static void showProfile(String username) {
        BorderPane profilePane = new BorderPane();
        String profilePictureUrl;
        String bio;
        Database db = new Database();
        try (PreparedStatement ps = db.getConnection().prepareStatement("select * from users where username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("NO PROFILE FOUND (Should be written on the app)");
                return;
            }
            profilePictureUrl = rs.getString("profile_picture_path");
            bio = rs.getString("bio");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Left section for profile picture
        ImageView profilePicture = new ImageView(profilePictureUrl); // Load user profile picture
        profilePicture.setFitWidth(200);
        profilePicture.setFitHeight(200);
        profilePicture.setPreserveRatio(true); // Maintain aspect ratio if needed

        // Center section for username, bio, and edit profile button
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.TOP_LEFT); // Align contents to the top-left corner

        Label usernameLabel = new Label(username); // Replace with actual username from SQL
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 30px;"); // Style the username label

        Label bioLabel = new Label(bio); // Replace with actual user bio from SQL
        bioLabel.setWrapText(true);
        bioLabel.setMaxWidth(200); // Adjust width as needed
        bioLabel.setStyle("-fx-font-size: 16px;"); // Style for smaller text
        boolean owner = username.equals(Profile.username);
        if (owner) {
            Button editProfileButton = new Button("Edit Profile");
            Button createPostButton = new Button("Create Post");
            createPostButton.setOnAction(e -> Profile.createPost());
            editProfileButton.setOnAction(e -> Profile.editProfile());
            centerBox.getChildren().addAll(usernameLabel, bioLabel, editProfileButton, createPostButton);
        } else {
            Button sendMessageButton = new Button("Send message");
            MessagesUI.initialize();
            sendMessageButton.setOnAction(e -> MessagesUI.showMessageWithUser(username));
            centerBox.getChildren().addAll(usernameLabel, bioLabel,sendMessageButton);
        }

        // Add profile picture to the left and user info to the center of the profile pane
        profilePane.setLeft(profilePicture);
        profilePane.setCenter(centerBox);

        VBox postContents = new VBox(10); // Create VBox to hold posts
        postContents.setAlignment(Pos.CENTER_LEFT); // Align contents to the left

        ScrollPane scrollPane = new ScrollPane(); // Create ScrollPane to hold postContent
        scrollPane.setContent(postContents); // Set postContent inside ScrollPane
        scrollPane.setFitToWidth(true); // Enable horizontal scrolling if needed

        // Add scrollPane to the right section of profilePane
        profilePane.setRight(scrollPane);

        // Display the profile view
        Scene profileScene = new Scene(profilePane, 600, 400); // Set your preferred size
        Stage profileStage = new Stage();
        profileStage.setScene(profileScene);
        profileStage.show();

        // Fetching posts in a separate thread
        new Thread(() -> {
            List<Integer> postIDs = Posts.getAllPostID(username);
            List<Node> postNodes = new ArrayList<>();

            for (Integer postID : postIDs) {
                String postContent = Posts.getContent(postID);
                int likeCount = Posts.getLikesCount(postID);
                int commentCount = Posts.getCommentCount(postID);
                String pictureUrl = Posts.getPicture(postID);

                Label contentLabel = new Label(postContent);
                Label likeCountLabel = new Label("Likes: " + likeCount);
                Label commentCountLabel = new Label("Comments: " + commentCount);
                likeCountLabel.setTextFill(Color.BLUE); // Set text color to differentiate it as a link
                commentCountLabel.setTextFill(Color.BLUE); // Set text color to differentiate it as a link
                likeCountLabel.setOnMouseClicked(e -> {
                    List<String> likedBy = Likes.showThoseWhoLiked(postID);
                   showLikesDialog(likedBy);
                });

                commentCountLabel.setOnMouseClicked(e -> {
                    List<Integer> commentIds = Comments.getCommentsIDs(postID);
                    showCommentsDialog(commentIds,postID);
                });

                // Create a VBox to hold text content and counts
                VBox textInfo = new VBox(5);
                textInfo.getChildren().addAll(contentLabel, commentCountLabel, likeCountLabel);

                ImageView imageView = null;
                try {
                    if (pictureUrl != null && !pictureUrl.isEmpty()) {
                        imageView = new ImageView(new Image(pictureUrl));
                        imageView.setFitWidth(100); // Adjust width as needed
                        imageView.setPreserveRatio(true); // Maintain aspect ratio
                    }
                } catch (Exception ignored) {
                    // If an exception occurs while loading the image, set imageView to null
                    imageView = null;
                }

                // Create an HBox to hold the image and text content side by side
                HBox postBox = new HBox(10);
                postBox.setAlignment(Pos.CENTER_LEFT);

                // Add image and text to the post box if imageView is not null
                if (imageView != null) {
                    postBox.getChildren().addAll(imageView, textInfo);
                } else {
                    // If there's no image, or it failed to load, only add the text info
                    postBox.getChildren().add(textInfo);
                }

                boolean alreadyLiked = Likes.isLiked(Profile.username,postID);

                // Create a like button or like symbol based on whether the user has liked the post
                if (!alreadyLiked) {
                    Button likeButton = new Button("Like");
                    likeButton.setOnAction(e -> {
                        Likes like = new Likes(Profile.username, postID);
                        like.addLike();
                    });
                    postBox.getChildren().add(likeButton);
                } else {
                    Button takeLikeBackButton = new Button("Take Like Back");
                    takeLikeBackButton.setOnAction(e -> Likes.deleteLike(Profile.username,postID));
                    postBox.getChildren().add(takeLikeBackButton);
                }

                Separator separator = new Separator();
                separator.setPrefHeight(10); // Add space between posts if needed

                // Check if the current user has permission to delete the post
                if (owner) {
                    Button deleteButton = new Button("Delete");
                    deleteButton.setOnAction(e -> Posts.deletePost(postID));
                    postBox.getChildren().add(deleteButton);
                }

                postNodes.add(postBox);
                postNodes.add(separator);
            }

            Platform.runLater(() -> {
                // Add fetched posts to postContents
                postContents.getChildren().addAll(postNodes);
            });
        }).start();
    }

    private static void showCommentsDialog(List<Integer> commentIds, int postId) {
        // Create a new stage to display comments
        Stage commentsStage = new Stage();
        commentsStage.setTitle("Comments");

        // Create a VBox to hold the comments
        VBox commentsBox = new VBox(10);
        commentsBox.setPadding(new Insets(10));

        // Loop through the comment IDs and fetch/comment content and usernames
        for (int commentId : commentIds) {
            String content = getContent(commentId);
            String username = getUsername(commentId);

            if (content != null && username != null) {
                String commentText = username + ": " + content;

                Label commentLabel = new Label(commentText);
                commentLabel.setWrapText(true); // Enable wrapping for long comments

                HBox commentBox = new HBox(10); // Create an HBox to hold the comment and delete button

                // Add the comment label to the HBox
                commentBox.getChildren().add(commentLabel);

                // Check if the comment belongs to the current user
                if (username.equals(Profile.username)) {
                    Button deleteButton = new Button("Delete");
                    deleteButton.setOnAction(e -> deleteComment(commentId)); // Call deleteComment with the comment ID

                    // Add the delete button to the HBox
                    commentBox.getChildren().add(deleteButton);
                }

                // Add the comment HBox to the VBox
                commentsBox.getChildren().add(commentBox);
            }
        }


        // Create a TextField for the current user to add a comment
        TextField commentInput = new TextField();
        Button addCommentButton = new Button("Add Comment");
        addCommentButton.setOnAction(e -> {
            String commentText = commentInput.getText();
            if (!commentText.isEmpty()) {
                Comments newComment = new Comments(Profile.username, postId, commentText);
                newComment.addComment();
            }
        });

        // Create an HBox to hold the TextField and Add Comment button
        HBox commentAdderBox = new HBox(10);
        commentAdderBox.setPadding(new Insets(10));
        commentAdderBox.getChildren().addAll(commentInput, addCommentButton);

        // Add the comment adder HBox to the VBox
        commentsBox.getChildren().add(commentAdderBox);

        // Create a ScrollPane to handle scrolling if needed
        ScrollPane scrollPane = new ScrollPane(commentsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(320, 400); // Set preferred size

        // Set the ScrollPane as the root of the scene
        Scene scene = new Scene(scrollPane);

        // Set the scene and show the stage
        commentsStage.setScene(scene);
        commentsStage.show();
    }



    private static void showLikesDialog(List<String> likedBy) {
        // Create a dialog or a new window to display the list of users who liked the post
        // You can use Alert, Dialog, or a new stage as per your UI design

        // For example, using an Alert
        Alert likesDialog = new Alert(Alert.AlertType.INFORMATION);
        likesDialog.setTitle("Likes");
        likesDialog.setHeaderText("Users who liked this post:");

        // Convert the list of users to a string representation
        StringBuilder users = new StringBuilder();
        for (String user : likedBy) {
            users.append(user).append("\n");
        }

        TextArea textArea = new TextArea(users.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        likesDialog.getDialogPane().setContent(textArea);
        likesDialog.showAndWait();
    }



    public static void createPost() {
        Stage createPostStage = new Stage();
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        TextArea postContent = new TextArea();
        postContent.setPromptText("Enter your post...");

        CheckBox includePicture = new CheckBox("Include picture");
        TextField pictureUrlField = new TextField();
        pictureUrlField.setPromptText("Enter picture URL (optional)");
        pictureUrlField.setVisible(false); // Initially hide the picture URL field

        includePicture.setOnAction(e -> {
            if (includePicture.isSelected()) {
                // Show picture URL field when the checkbox is selected
                pictureUrlField.setVisible(true);
            } else {
                // Hide picture URL field when the checkbox is unselected
                pictureUrlField.setVisible(false);
                pictureUrlField.clear(); // Clear the URL field content
            }
        });

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        Button postButton = new Button("Post");
        postButton.setOnAction(e -> {
            String content = postContent.getText();
            String pictureUrl = includePicture.isSelected() ? pictureUrlField.getText() : "";

            // Check if either content or picture URL is provided
            if (!content.isEmpty() || !pictureUrl.isEmpty()) {
                Posts newPost = new Posts(content, Profile.username, pictureUrl);
                newPost.addPost();
                createPostStage.close();
            } else {
                // Show an error message indicating the need for content or picture
                errorLabel.setText("Please provide content or picture URL");
                errorLabel.setVisible(true);
            }
        });

        root.getChildren().addAll(postContent, includePicture, pictureUrlField, errorLabel, postButton);

        Scene scene = new Scene(root, 300, 250);
        createPostStage.setScene(scene);
        createPostStage.show();
    }

    public static void editProfile(){
        // Code to display the UI for selecting the action - changing username, password, or email
        // This could involve a separate UI window or elements added to the existing profile view

        // For instance, let's assume a dialog box or a separate UI where the user selects an option
        // Displaying a simple Alert dialog for the sake of an example:
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Edit Profile");
        alert.setHeaderText("Choose an action:");
        alert.close();

        ButtonType changeUsernameButton = new ButtonType("Change Username");
        ButtonType changePasswordButton = new ButtonType("Change Password");
        ButtonType changeEmailButton = new ButtonType("Change Email");
        ButtonType changePfpButton = new ButtonType("Change Profile Picture");
        ButtonType changeBioButton = new ButtonType("Change Bio");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(changeUsernameButton, changePasswordButton, changeEmailButton,changePfpButton,changeBioButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == changeUsernameButton) {
                changeUsername();
            } else if (result.get() == changePasswordButton) {
                changePassword();
            } else if (result.get() == changeEmailButton) {
                changeEmail();
            } else if (result.get() == changePfpButton){
                changePfp();
            } else if (result.get() == changeBioButton){
                changeBio();
            }
        }
    }

    public static void changeUsername(){
        Label usernameErrorLabel = new Label();
        usernameErrorLabel.setTextFill(Color.RED);

        Stage changeUsernameStage = new Stage();
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        TextField newUsernameField = new TextField();
        newUsernameField.setPromptText("Enter new username");

        Button updateUsernameButton = new Button("Update Username");
        updateUsernameButton.setOnAction(e -> {
            String newUsername = newUsernameField.getText();
            boolean isValidUsername = true;
            if (!LoginController.isNameUsable(newUsername)) {
                usernameErrorLabel.setText("Username is already taken"); // Display username taken message
                isValidUsername = false;
            }else if (newUsername.length() > 30) {
                usernameErrorLabel.setText("Username can't be longer than 30 characters");
                isValidUsername = false;
            } else if (newUsername.length() < 5) {
                usernameErrorLabel.setText("Username can't be less than 5 characters");
                isValidUsername = false;
            }
            if(isValidUsername){
                Database db = new Database();
                try(PreparedStatement ps = db.getConnection().prepareStatement("update users set username = ? where username = ?")){
                    ps.setString(1,newUsername);
                    ps.setString(2,username);
                    int x =  ps.executeUpdate();
                    if(x==0){
                        System.out.println("Failed to change the username");
                    }else{
                        System.out.println("Successfully changed the username");
                        username = newUsername;

                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                changeUsernameStage.close();
            }else{
                if (!root.getChildren().contains(usernameErrorLabel)) {
                    root.getChildren().add(usernameErrorLabel);
                }
            }
        });

        root.getChildren().addAll(newUsernameField, updateUsernameButton);

        Scene scene = new Scene(root, 300, 200);
        changeUsernameStage.setScene(scene);
        changeUsernameStage.show();
    }
    public static void changePassword(){
        Label validPasswordLabel = new Label();
        validPasswordLabel.setTextFill(Color.RED);
        Stage changePasswordStage = new Stage();
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");

        Button updatePasswordButton = new Button("Update Password");
        updatePasswordButton.setOnAction(e -> {
            String newPassword = newPasswordField.getText();
            boolean isValidPassword = true;
            if (newPassword.replaceAll("[^A-Z]", "").length() == 0) {
                validPasswordLabel.setText("New password must contain at least one uppercase");
                isValidPassword = false;
            } else if (newPassword.replaceAll("[^a-z]", "").length() == 0) {
                validPasswordLabel.setText("New password must contain at least one lowercase");
                isValidPassword = false;
            } else if (newPassword.replaceAll("[^0-9]", "").length() == 0) {
                validPasswordLabel.setText("New password must contain at least one digit");
                isValidPassword = false;
            } else if (newPassword.length() < 7) {
                validPasswordLabel.setText("New password must be at least 7 characters");
                isValidPassword = false;
            } else if (newPassword.length() > 50) {
                validPasswordLabel.setText("New password must be less than 50 characters");
                isValidPassword = false;
            }

            // Check if the password is valid before attempting to update it
            if (isValidPassword) {
                Database db = new Database();
                try (PreparedStatement ps = db.getConnection().prepareStatement("update users set password = ? where username = ?")) {
                    ps.setString(1, newPassword);
                    ps.setString(2, username);
                    int x = ps.executeUpdate();
                    if (x == 0) {
                        System.out.println("Failed to change the password");
                    } else {
                        System.out.println("Successfully changed the password");
                        password = newPassword;
                        validPasswordLabel.setText(""); // Clear the validation message if password update is successful
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                changePasswordStage.close();
            } else {
                if (!root.getChildren().contains(validPasswordLabel)) {
                    root.getChildren().add(validPasswordLabel);
                }
            }
        });
        root.getChildren().addAll(newPasswordField, updatePasswordButton);

        Scene scene = new Scene(root, 300, 200);
        changePasswordStage.setScene(scene);
        changePasswordStage.show();
    }

    public static void changeEmail(){
        Label emailErrorLabel = new Label();
        emailErrorLabel.setTextFill(Color.RED);
        Stage changeEmailStage = new Stage();
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        TextField newEmailField = new TextField();
        newEmailField.setPromptText("Enter new email");

        Button updateEmailButton = new Button("Update Email");
        updateEmailButton.setOnAction(e -> {
            String newEmail = newEmailField.getText();
            boolean okEmail = true;

            if (!newEmail.contains("@") || !newEmail.endsWith(".com")) {
                emailErrorLabel.setText("Enter a valid email");
                okEmail = false;
            }
            if (newEmail.length() > 50) {
                emailErrorLabel.setText("Email can't be longer than 50 characters");
                okEmail = false;
            }

            // Check if the email is valid before attempting to update it
            if (okEmail) {
                Database db = new Database();
                try(PreparedStatement ps = db.getConnection().prepareStatement("update users set email = ? where username = ?")){
                    ps.setString(1, newEmail);
                    ps.setString(2, username);
                    int x = ps.executeUpdate();
                    if(x == 0){
                        System.out.println("Failed to change the email");
                    } else {
                        System.out.println("Successfully changed the email");
                        email = newEmail;
                        emailErrorLabel.setText(""); // Clear the validation message if email update is successful
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                changeEmailStage.close();
            } else {
                if (!root.getChildren().contains(emailErrorLabel)) {
                    root.getChildren().add(emailErrorLabel);
                }
            }
        });

        root.getChildren().addAll(newEmailField, updateEmailButton);

        Scene scene = new Scene(root, 300, 200);
        changeEmailStage.setScene(scene);
        changeEmailStage.show();
    }
    public static void changePfp(){
        Label pfpErrorLabel = new Label();
        pfpErrorLabel.setTextFill(Color.RED);
        Stage changePfpStage = new Stage();
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        TextField newEmailField = new TextField();
        newEmailField.setPromptText("Enter the url of the new pfp");

        Button updatePfpButton = new Button("Update the pfp");
        updatePfpButton.setOnAction(e -> {
            String newPfp = newEmailField.getText();
            if(newPfp.length()>255){
                pfpErrorLabel.setText("Bio can't be longer than 255 characters");
            }else{
                Database db = new Database();
                try(PreparedStatement ps = db.getConnection().prepareStatement("update users set profile_picture_path = ? where username = ?")){
                    ps.setString(1,newPfp);
                    ps.setString(2,username);
                    int x =  ps.executeUpdate();
                    if(x==0){
                        System.out.println("Failed to change the profile picture");
                    }else{
                        System.out.println("Successfully changed the profile picture");
                        profilePictureUrl = newPfp;
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                changePfpStage.close();
                db.close();
            }
            if (!root.getChildren().contains(pfpErrorLabel)) {
                root.getChildren().add(pfpErrorLabel);
            }
        });

        root.getChildren().addAll(newEmailField, updatePfpButton);

        Scene scene = new Scene(root, 300, 200);
        changePfpStage.setScene(scene);
        changePfpStage.show();
    }
    public static void changeBio(){
        Label bioErrorLabel = new Label();
        bioErrorLabel.setTextFill(Color.RED);
        Stage changeBioStage = new Stage();
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        TextArea newBioField = new TextArea();
        newBioField.setPromptText("Enter new bio");

        Button updateBioButton = new Button("Update Bio");
        updateBioButton.setOnAction(e -> {
            String newBio = newBioField.getText();

            if (newBio.length() > 255) {
                bioErrorLabel.setText("Bio can't be longer than 255 characters");
            } else {
                Database db = new Database();
                try(PreparedStatement ps = db.getConnection().prepareStatement("update users set bio = ? where username = ?")){
                    ps.setString(1, newBio);
                    ps.setString(2, username);
                    int x = ps.executeUpdate();
                    if(x == 0){
                        System.out.println("Failed to change the bio");
                    } else {
                        System.out.println("Successfully changed the bio");
                        bio = newBio;
                        bioErrorLabel.setText(""); // Clear the validation message if bio update is successful
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                changeBioStage.close();
            }

            // Ensure the label is visible by adding it to the root if there's a bio length error
            if (!root.getChildren().contains(bioErrorLabel)) {
                root.getChildren().add(bioErrorLabel);
            }
        });

        root.getChildren().addAll(newBioField, updateBioButton);

        Scene scene = new Scene(root, 300, 200);
        changeBioStage.setScene(scene);
        changeBioStage.show();
    }
}

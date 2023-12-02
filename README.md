## SocialMediaApp

# Overview
The SocialMediaApp is an Instagram-like platform that enables users to create profiles, share posts, comment on content, like posts, and send messages to other users. It includes features such as profile editing, post creation, liking, commenting, and real-time messaging functionalities.

# User Authentication and Profiles :
-Login/Logout: Users can log in and out of the application.
-Profile Management: Users can view and edit their profiles.
-Change Profile Details: Including username, password, email, profile picture, and bio.
Social Interactions:
-Messaging: Users can send and receive messages to/from other users.
-View Messages: Users can view and interact with their messages and conversations.
-Likes and Comments: Users can like posts and comment on them.
Posts and Feeds:
-Create Posts: Users can create and share posts, optionally with pictures.
-View Posts: Users can see their own posts and posts from others in a feed-like manner.
-Interact with Posts: Like, comment, and delete their own posts.
-Search Functionality: Users can search for other users by their usernames.
-Responsive UI: The app has a user-friendly interface for navigation and interaction.


# Usage
To use the app, you need to create these tables or simply copy paste these on a SQL (You don't need to add the column created_at, its just for the security for now, the post-message-user-like-comment creation date might be brought in the app in the upcoming updates) :

create table users(
	username varchar(30) primary key,
	email varchar(50), password varchar(50),
	profile_picture_path varchar(255),
	bio varchar(255),
	created_at timestamp default CURRENT_TIMESTAMP
);

create table posts(
  post_id serial primary key,
  content text,
  created_at timestamp default CURRENT_TIMESTAMP,
  likes_count integer,
  comments_count integer,
  username varchar(30) references users(username),
  picture varchar(255)
);

create table messages(
  from_username varchar(30) references users(username),
  to_username varchar(30) references users(username),
  content text,
  created_at timestamp default CURRENT_TIMESTAMP,
  message_id serial primary key
);

create table likes(
  like_id serial primary key,
  post_id integer references posts(post_id),
  created_at timestamp default CURRENT_TIMESTAMP,
  username varchar(30) references users(username)
);

create table comments(
  comment_id serial primary key,
  post_id integer references posts(post_id), 
  comment_text text,
  created_at timestamp default CURRENT_TIMESTAMP,
  username varchar(30) references users(username)
);

To use the app after these creations:

modify these parts in the Database class in the DataBaseUtility package, based on your database's ID = 

Class.forName("org.postgresql.Driver");
this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/SocialMedia", "postgres", "p");

and then simply go to the runner package and Run the program through the Main class :=)


# Technologies Used
-Java 11
-JavaFX 11.0.2
-SQL (PostgreSQL)
-JDBC (Java Database Connectivity)

# Author: Volkan


I developed the core functionalities, database integration, and backend logic for this project, aiming to showcase my expertise in Java, SQL, JDBC; backend development in simple words. The JavaFX-related things like locating the buttons, setting the size, changing the background color and etc were created with guidance and support from an AI assistant (ChatGPT), I handled the backend within the UI package too.


# Common problems

1-The profile tab needs to be closed and reopened after editing something on it (the changes in the like and comments count on the posts after commenting or liking, don't show up on the post unless you refresh the tab the same way I said above).

2-In order for the comments to appear after commenting on a post, you need to close the comment and reopen it like the profile tab.



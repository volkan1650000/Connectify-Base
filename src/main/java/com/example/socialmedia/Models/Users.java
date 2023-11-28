package com.example.socialmedia.Models;

import com.example.socialmedia.DataBaseUtility.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Users {
    String username;
    String email;
    String password;
    String profilePictureUrl;
    String bio;

    public Users(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePictureUrl = "https://via.placeholder.com/200";
        this.bio = "No bio";
    }

    public void addUser(){
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("insert into users(username, email, password, profile_picture_path, bio) values (?, ?, ?, ?, ?)")){
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3,password);
            ps.setString(4,profilePictureUrl);
            ps.setString(5,bio);
            int x = ps.executeUpdate();
            if(x == 0){
                System.out.println("Failed to add the user");
            }else{
                System.out.println("Successfully added the user");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
    }

    public static List<String> search(String searchedWord){
        List<String> result = new LinkedList<>();
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select username from users where username like ?")){
            ps.setString(1,"%" + searchedWord + "%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result.add(rs.getString(1));
            }
            rs.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return result;
    }
}

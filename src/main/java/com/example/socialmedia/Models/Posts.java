package com.example.socialmedia.Models;

import com.example.socialmedia.DataBaseUtility.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Posts {
    String content;
    String picture;
    int likesCount;
    int commentsCount;
    String username;

    public Posts(String content, String username,String picture) {
        this.content = content;
        this.likesCount = 0;
        this.commentsCount = 0;
        this.picture = picture;
        this.username = username;
    }
    public void addPost(){
        Database db  = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("insert into posts(content,likes_count, comments_count, username, picture) values(?,?,?,?,?)")){
            ps.setString(1,content);
            ps.setInt(2,likesCount);
            ps.setInt(3,commentsCount);
            ps.setString(4,username);
            ps.setString(5,picture);
            int x = ps.executeUpdate();
            if(x==0){
                System.out.println("Failed to add the posts");
            }else{
                System.out.println("Successfully added the posts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
    }
    public static void deletePost(int postId){
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("delete from posts where post_id = ?")){
            ps.setInt(1,postId);
            int x = ps.executeUpdate();
            if(x==0){
                System.out.println("Failed to delete the post");
            }else{
                System.out.println("Successfully deleted the post");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
    }
    public static List<Integer> getAllPostID(String username){
        List<Integer> result = new LinkedList<>();
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select post_id from posts where username = ?")){
            ps.setString(1,username);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result.add(rs.getInt(1));
            }
            rs.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return result;
    }

    public static int getLikesCount(int postID) {
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select likes_count from posts where post_id = ?")){
            ps.setInt(1,postID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int rtn = rs.getInt(1);
                db.close();
                rs.close();
                return rtn;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Likes error");
        }
        db.close();
        return 0;
    }

    public static int getCommentCount(int postID) {
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select comments_count from posts where post_id = ?")){
            ps.setInt(1,postID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int rtn = rs.getInt(1);
                db.close();
                rs.close();
                return rtn;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Comment error");
        }
        db.close();
        return 0;
    }
    public static String getContent(int postID) {
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select content from posts where post_id = ?")){
            ps.setInt(1,postID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String rtn = rs.getString(1);
                db.close();
                rs.close();
                return rtn;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Content error");
        }
        db.close();
        return null;
    }
    public static String getPicture(int postID) {
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select picture from posts where post_id = ?")){
            ps.setInt(1,postID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String rtn = rs.getString(1);
                db.close();
                rs.close();
                return rtn;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Comment error");
        }
        db.close();
        return null;
    }
}

package com.example.socialmedia.Models;

import com.example.socialmedia.DataBaseUtility.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Comments {
    String username;
    int postId;
    String commentText;

    public Comments(String username, int postId, String commentText) {
        this.username = username;
        this.postId = postId;
        this.commentText = commentText;
    }
    public void addComment() {
        Database db = new Database();
        try (PreparedStatement ps2 = db.getConnection().prepareStatement("insert into comments (post_id, comment_text, username) values (?,?,?)");
             PreparedStatement ps3 = db.getConnection().prepareStatement("update posts set comments_count = comments_count+1 where post_id  = ?")) {
            ps2.setInt(1, postId);
            ps2.setString(2,commentText);
            ps2.setString(3, username);
            ps3.setInt(1, postId);
            int y = ps3.executeUpdate();
            int x = ps2.executeUpdate();
            if (x == 0 || y == 0) {
                System.out.println("Failed to comment");
            } else {
                System.out.println("Successfully commented");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            db.close();
        }
        db.close();
    }

    public static List<Integer> getCommentsIDs(int postId){
        List<Integer>result = new LinkedList<>();
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select comment_id from comments where post_id = ?")){
            ps.setInt(1,postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                result.add(rs.getInt(1));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return result;
    }

    public static String getContent(int comment_id){
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select comment_text from comments where comment_id = ?")){
            ps.setInt(1,comment_id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String content = rs.getString(1);
                rs.close();
                return content;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return null;
    }

    public static String getUsername(int comment_id){
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select username from comments where comment_id = ?")){
            ps.setInt(1,comment_id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String username = rs.getString(1);
                rs.close();
                return username;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return null;
    }

    public static void deleteComment(int comment_id){
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select post_id from comments where comment_id = ?");
            PreparedStatement ps2 = db.getConnection().prepareStatement("delete from comments where comment_id = ?");
            PreparedStatement ps3 = db.getConnection().prepareStatement("update posts set comments_count = comments_count-1 where post_id  = ?")
            ){
            ps.setInt(1,comment_id);
            ResultSet rs = ps.executeQuery();
            int postId = 0;
            while(rs.next()){
                postId = rs.getInt(1);
                break;
            }
            ps2.setInt(1,comment_id);
            int x = ps2.executeUpdate();
            ps3.setInt(1,postId);
            int y = ps3.executeUpdate();
            if(x==0 || y==0){
                System.out.println("Failed to delete the comment");
            }else{
                rs.close();
                System.out.println("Successfully deleted the comment");
            }
        } catch (SQLException e) {
            db.close();
            e.printStackTrace();
        }
        db.close();
    }
}

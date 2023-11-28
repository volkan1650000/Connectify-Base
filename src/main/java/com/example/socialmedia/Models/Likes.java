package com.example.socialmedia.Models;

import com.example.socialmedia.DataBaseUtility.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Likes {
    String username;
    int postId;

    public Likes(String username, int postId) {
        this.username = username;
        this.postId = postId;
    }

    public void addLike() {
        Database db = new Database();
        try (PreparedStatement ps2 = db.getConnection().prepareStatement("insert into likes (post_id, username) values (?,?)");
             PreparedStatement ps3 = db.getConnection().prepareStatement("update posts set likes_count = likes_count+1 where post_id  = ?")) {
            ps2.setInt(1, postId);
            ps2.setString(2, username);
            ps3.setInt(1, postId);
            int y = ps3.executeUpdate();
            int x = ps2.executeUpdate();
            if (x == 0 || y == 0) {
                System.out.println("Failed to like");
            } else {
                System.out.println("Successfully liked");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
    }

    public static void deleteLike(String username, int postId){
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("delete from likes where username = ? and post_id = ? ");
            PreparedStatement ps2 = db.getConnection().prepareStatement("update posts set likes_count = likes_count-1 where post_id  = ?")){
            ps.setString(1,username);
            ps.setInt(2,postId);
            ps2.setInt(1,postId);
            int x = ps.executeUpdate();
            int y = ps2.executeUpdate();
            if(x==0 || y==0){
                System.out.println("Failed to delete the like");
            }else{
                System.out.println("Successfully deleted the like");
            }
        } catch (SQLException e) {
            db.close();
            e.printStackTrace();
        }
        db.close();
    }

    public static boolean isLiked(String username, int postId){
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select * from likes where username = ? and post_id = ? ")){
            ps.setString(1,username);
            ps.setInt(2,postId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                db.close();
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            db.close();
            e.printStackTrace();
            return true;
        }
        db.close();
        return false;
    }

    public static List<String> showThoseWhoLiked(int postId){
        List<String> result = new LinkedList<>();
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select username from likes where post_id  = ?")){
            ps.setInt(1,postId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result.add(rs.getString(1));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return result;
    }
}

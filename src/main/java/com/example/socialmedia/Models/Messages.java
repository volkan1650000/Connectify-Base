package com.example.socialmedia.Models;

import com.example.socialmedia.DataBaseUtility.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class Messages {
    String fromUsername;
    String toUsername;
    String content;

    public Messages(String fromUsername, String toUsername, String content) {
        this.fromUsername = fromUsername;
        this.toUsername = toUsername;
        this.content = content;
    }

    public void addMessage(){
        Database db = new Database();
        try(PreparedStatement ps =  db.getConnection().prepareStatement("insert into messages(from_username, to_username, content) values(?,?,?)")){
            ps.setString(1,fromUsername);
            ps.setString(2,toUsername);
            ps.setString(3,content);
            int x = ps.executeUpdate();
            if(x==0){
                System.out.println("Failed to add the message");
            }else{
                System.out.println("Successfully added the message");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
    }

    public static List<Integer> getMessageIDs(String from, String to){
        List<Integer>result = new LinkedList<>();
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select message_id from messages where from_username in(?,?) and to_username in(?,?)")){
            ps.setString(1,from);
            ps.setString(2,to);
            ps.setString(3,from);
            ps.setString(4,to);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result.add(rs.getInt(1));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return result;
    }

    public static String getUsername(int messageId){
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select from_username from messages where message_id = ?")){
            ps.setInt(1,messageId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String username = rs.getString(1);
                db.close();
                rs.close();
                return username;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getContent(int messageId){
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select content from messages where message_id = ?")){
            ps.setInt(1,messageId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String content = rs.getString(1);
                db.close();
                rs.close();
                return content;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LinkedHashSet<String> thoseWeHaveMessagesWith(String profileUsername){
        LinkedHashSet<String> result = new LinkedHashSet<>();
        Database db = new Database();
        try(PreparedStatement ps = db.getConnection().prepareStatement("select to_username from messages where from_username = ?");
            PreparedStatement ps1 = db.getConnection().prepareStatement("select from_username from messages where to_username = ?")){
            ps.setString(1,profileUsername);
            ps1.setString(1,profileUsername);
            ResultSet rs = ps.executeQuery();
            ResultSet rs2 = ps1.executeQuery();
            while(rs.next()){
                result.add(rs.getString(1));
            }
            while(rs2.next()){
                result.add(rs2.getString(1));
            }
            rs.close();
            rs2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return result;
    }
}

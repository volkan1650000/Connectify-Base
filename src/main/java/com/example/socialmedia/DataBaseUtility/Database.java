package com.example.socialmedia.DataBaseUtility;
import java.sql.*;

public class Database {
    private Connection connection;

    //No need for statement instance since prepared statement was always used which was by default closed within
    //the try-catch with resources block

    public Database() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/SocialMedia", "postgres", "p");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("User not found");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Class not found");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close(){
        try{
            if(connection != null){
                connection.close();
            }
        }catch (SQLException e) {
            System.out.println("Failed to close the connection");
        }
    }
}

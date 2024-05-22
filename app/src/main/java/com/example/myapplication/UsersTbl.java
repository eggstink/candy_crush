package com.example.myapplication;

import android.widget.EditText;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsersTbl {
    public static void createTable() {
        try(Connection c = MySQLConnection.getConnection();
            Statement statement = c.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "firstname VARCHAR(50) NOT NULL," +
                    "lastname VARCHAR(50) NOT NULL," +
                    "email VARCHAR(50) NOT NULL," +
                    "username VARCHAR(50) NOT NULL," +
                    "password VARCHAR(50) NOT NULL)";
            statement.execute(query);
            System.out.println("Table created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertData(String fname, String lname, String email, String uname, String pass) {
        try(Connection c = MySQLConnection.getConnection();
            PreparedStatement statement = c.prepareStatement(
                    "INSERT INTO users (firstname, lastname, email, username, password) VALUES (?, ?, ?, ?, ?)"
            )) {
            statement.setString(1, fname);
            statement.setString(2, lname);
            statement.setString(3, email);
            statement.setString(4, uname);
            statement.setString(5, pass);
            int rows = statement.executeUpdate();
            System.out.println("Rows inserted: " + rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void readData(String currUname, EditText fname, EditText lname, EditText email, EditText uname) {
        try(Connection c = MySQLConnection.getConnection();
            Statement statement = c.createStatement()) {
            String query = "SELECT * FROM users WHERE username='"+currUname+"'";
            ResultSet res = statement.executeQuery(query);

            while(res.next()) {
                String firstname = res.getString("firstname");
                String lastname = res.getString("lastname");
                String emailAdd = res.getString("email");
                String username = res.getString("username");

                fname.setText(firstname);
                lname.setText(lastname);
                email.setText(emailAdd);
                uname.setText(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateData(String currUname, String fname, String lname, String email, String uname) {
        try(Connection c = MySQLConnection.getConnection();
            PreparedStatement statement = c.prepareStatement(
                    "UPDATE users SET firstname=?, lastname=?, email=?, username=? WHERE username=?"
            )) {

            statement.setString(1, fname);
            statement.setString(2, lname);
            statement.setString(3, email);
            statement.setString(4, uname);
            statement.setString(5, currUname);

            int rows = statement.executeUpdate();
            System.out.println("Rows updated: " + rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteData(String[] args) {
        try(Connection c = MySQLConnection.getConnection();
            PreparedStatement statement = c.prepareStatement(
                    "DELETE FROM users WHERE id=? RETURNING *"
            )) {
            int id = 1;
            statement.setInt(1, id);
            int rows = statement.executeUpdate();

            ResultSet res = statement.getResultSet();
            if(res.next()) {
                System.out.println("Name: " + res.getString("name"));
                System.out.println("Email Address: " + res.getString("email"));
                System.out.println("Data deleted successfully!");
            }
            System.out.println("Rows deleted: " + rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

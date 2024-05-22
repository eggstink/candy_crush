package com.example.myapplication;

import java.sql.*;

public class MySQLConnection {
    public static final String URL = "jdbc:mysql://localhost:3306/dbcustomcrush";
    public static final String username = "root";
    public static final String password = "";

    static Connection getConnection() {
        Connection c = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection(URL, username, password);
            System.out.println("Database Connected!");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static void main(String[] args) {
        getConnection();
    }


}

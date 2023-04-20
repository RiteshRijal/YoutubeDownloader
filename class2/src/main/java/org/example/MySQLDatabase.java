package org.example;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/downloaddb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection(){

        Connection connection;
        try {
            connection= DriverManager.getConnection(URL,USERNAME,PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}

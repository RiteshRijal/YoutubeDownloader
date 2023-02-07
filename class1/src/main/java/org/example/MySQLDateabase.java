package org.example;

import java.sql.Connection;
import java.sql.DriverManager;

public  class MySQLDateabase {
    private static final String URL="jdbc:mysql://localhost:3306/ems ?useSSL=false";
    private static final String username="root";
    private static final String password="root";


    public static Connection getConnection(){

        Connection connection;
        try{
            connection= DriverManager.getConnection(URL,username,password);


        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return connection;
    }
}

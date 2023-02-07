package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class DatabaseUtil {
   //docker docker pull mysql
    //driver 0>
    public static void saveToDatabase(String downlaodUrl, StringBuffer downloadPath, LocalDateTime now) {
        Connection connection=MySQLDateabase.getConnection();
        Statement statement= null;
        try {
            statement = connection.createStatement();
            statement.execute("select *from employee ");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




    }
}

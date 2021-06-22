package main;

import java.sql.*;

/* Provided class in assignment template.
 */
public class SQLConnection {

    private static Connection connection;

    public SQLConnection(){
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:assignment.db");
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public static Connection getConnection(){return connection;}
}

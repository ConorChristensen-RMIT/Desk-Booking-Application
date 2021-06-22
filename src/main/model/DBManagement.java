package main.model;

import main.SQLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/* DBManagement class.
 * Singleton Class pattern used. Only one instance required to be made.
 * Facade pattern used - used to peform common DB functions across most model classes.
 */
public class DBManagement {
    private SQLConnection sql = new SQLConnection();

    private final static DBManagement INSTANCE = new DBManagement();

    private DBManagement () {}

    public static DBManagement getInstance() {return INSTANCE;}

    /* finallyTableEditOnly(Statement statement)
     * Closes an inputted statement after table is edited. Contains try and catch blocks and cleans up calling class
     * code.
     */
    public void finallyTableEditOnly(Statement statement){
        try{
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /* finallyTableEditOnly(Resultset results, Statement statement)
     * Closes an inputted resultset and statement after table is read. Contains try and catch blocks and cleans up
     * calling class code.
     */
    public void finallyTableRead(ResultSet results, Statement statement){
        try {
            results.close();
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /* connect().
     * Creates connection and sets internal variable.
     * This way only one connection is made for whole session.
     */
    public Connection connect() {
        Connection connection = sql.getConnection();
        if (connection == null){
            System.exit(1);
        }
        return connection;
    }

    /* getStatement()
     * gets statement from connection variable. Cleans up caller class code by avoiding try/catch blocks.
     */
    public Statement getStatement(){
        Statement statement = null;
        try {
            statement = sql.getConnection().createStatement();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return statement;
    }

}

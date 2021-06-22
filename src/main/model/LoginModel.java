package main.model;

import main.SQLConnection;
import org.sqlite.SQLiteConnection;

import java.sql.*;
import java.time.LocalDate;

/* Model class for LandingPageController class login functions.
 * Uses Singleton Facade DBManagement.
 * Mostly provided code in project template
 * Logs in user, and deletes all unconfirmed bookings for the next 24hrs.
 */
public class LoginModel {
    DBManagement db = DBManagement.getInstance();
    Connection connection;
    Statement statement;

    public LoginModel(){
        connection = db.connect();
        statement = db.getStatement();
    }

    public Boolean isDbConnected(){
        try {
            return !connection.isClosed();
        }
        catch(Exception e){
            return false;
        }
    }

    public Boolean isLogin(String user, String pass) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "select * from Profile where username = ? and password= ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, pass);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                String firstname = resultSet.getString(2);
                String lastname = resultSet.getString(3);
                String role = resultSet.getString(4);
                String username = resultSet.getString(5);
                String password = resultSet.getString(6);
                boolean isAdmin = (resultSet.getInt(7) == 1);
                String secret_qn = resultSet.getString(8);
                String secret_ans = resultSet.getString(9);
                int enabled = resultSet.getInt(10);
                if (enabled == 0){
                    db.finallyTableRead(resultSet, preparedStatement);
                    return false;
                }
                if (isAdmin) {
                    ProfileModel admin = new AdministratorModel(username, firstname, lastname, password, id, role, secret_qn, secret_ans);
                    ProfileHolder PH = ProfileHolder.getInstance();
                    PH.setProfile(admin);
                } else {
                    EmployeeModel employee = new EmployeeModel(username, firstname, lastname, password, id, role, secret_qn, secret_ans);
                    ProfileHolder PH = ProfileHolder.getInstance();
                    PH.setProfile(employee);
                }
                db.finallyTableRead(resultSet, preparedStatement);
                flushNextDayBookings();
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /* flushNextDayBookings()
     * Method called whenever any user is loggin in, and deletes all unconfirmed bookings in the coming 24Hrs
     * (today and tomorrow).
     */
    public void flushNextDayBookings(){
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate today = LocalDate.now();
        String query = "DELETE FROM Bookings WHERE confirmed = 0 AND (date = '"+today.toString()+"' OR date = '"+tomorrow.toString()+"');";
        try{
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableEditOnly(statement);
    }

}

package main.model;

import javafx.beans.property.SimpleStringProperty;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/* Booking class.
 * Class implements Facade Singleton DBManagement to assist with DB Communication.
 * Class uses Simple String properties to assist populating tableviews in UI. Getter setters are made for this.
 *
 */
public class Booking {
    Connection connection;
    Statement statement;
    DBManagement db = DBManagement.getInstance();

    private int bookingID;
    private int deskID;
    private LocalDate date;
    private EmployeeModel employee;
    private boolean confirmed;

    private SimpleStringProperty date_str;
    private SimpleStringProperty deskID_str;
    private SimpleStringProperty confirmed_str;
    private SimpleStringProperty employeeID_str;

    /* Constructor sets all priovate variables on construction.
     *
     */
    public Booking(int bookingID, int deskID, LocalDate date, EmployeeModel employee, boolean confirmed){
        this.bookingID = bookingID;
        this.deskID = deskID;
        this.employee = employee;
        this.date = date;
        this.confirmed = confirmed;
        this.date_str = new SimpleStringProperty(date.toString());
        this.deskID_str = new SimpleStringProperty(String.valueOf(deskID));
        if (confirmed) {this.confirmed_str = new SimpleStringProperty(this,"confirmed_str","Yes");}
        else {this.confirmed_str = new SimpleStringProperty(this,"confirmed_str", "No");}
        this.employeeID_str = new SimpleStringProperty(String.valueOf(employee.getEmployeeID()));
        connection = db.connect();
        statement = db.getStatement();
    }
    /* START SIMPLE STRING GET/SET METHODS
     * -----------------------------
     */
    public String getDate_str(){return date_str.get();}

    public String getDeskID_str(){return deskID_str.get();}

    public String getConfirmed_str(){return confirmed_str.get();}

    public String getEmployeeID_str(){return employeeID_str.get();}
    /* -----------------------------
     * END SIMPLE STRING GETSET METHODS
     */

    private void setDeskID(int deskID){
        this.deskID = deskID;
        deskID_str.setValue(String.valueOf(this.deskID));
    }

    private void setDate(LocalDate date){
        this.date = date;
        date_str.setValue(date.toString());
    }

    private void setConfirmed (boolean confirmed){
        this.confirmed = confirmed;
        if (confirmed) {confirmed_str.setValue("Yes");}
        else {confirmed_str.setValue("No");}
    }

    public EmployeeModel getEmployee() {
        return employee;
    }

    public boolean getConfirmed(){
        return confirmed;
    }

    public int getBookingID() {
        return bookingID;
    }

    public int getDeskID() {
        return deskID;
    }

    public LocalDate getDate(){
        return date;
    }

    public void saveToDB() {
        int confirmed_int;
        if (confirmed) {
            confirmed_int = 1;
        } else {
            confirmed_int = 0;
        }
        String query = "INSERT into Bookings VALUES (" + String.valueOf(bookingID) + ", " + String.valueOf(deskID) +
                ", '" + date.toString() + "', " + String.valueOf(employee.getEmployeeID()) + ", " + String.valueOf(confirmed_int) + ");";
        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.finallyTableEditOnly(statement);
    }

    public void deleteBooking(){
        String query = "DELETE FROM Bookings WHERE bookingID = " + this.getBookingID() + ";";
        try{
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableEditOnly(statement);
    }

    public void updateBooking(int deskID, LocalDate date){
        String query = "UPDATE Bookings SET deskID = "+String.valueOf(deskID)+", date = '"+date.toString()+"' WHERE bookingID = " + this.getBookingID() + ";";
        setDate(date);
        setDeskID(deskID);
        try{
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableEditOnly(statement);
    }

    public void confirm(){
        String query = "UPDATE Bookings SET confirmed = 1 WHERE bookingID = "+String.valueOf(this.bookingID)+";";
        setConfirmed(true);
        try{
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableEditOnly(statement);
    }

}


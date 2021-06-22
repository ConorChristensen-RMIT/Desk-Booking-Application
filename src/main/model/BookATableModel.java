package main.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

/* Model class for BookATableController class
 * Uses Singleton Facade DBManagement and Singleton ProfileHolder.
 * Class controls the data processing and database entries for booking tables
 */
public class BookATableModel {

    Connection connection;
    Statement statement;
    ResultSet results;
    ProfileHolder PH = ProfileHolder.getInstance();
    DBManagement db = DBManagement.getInstance();

    /* construct class with database connections.
     * Same connection is used throughout application use using DBManagement
     */
    public BookATableModel() {
        connection = db.connect();
        statement = db.getStatement();
    }

    /* checkIfEmpAlreadyBookedForDate(LocalDate date) - OVERLOADED method
     * if no profile ID is provided, the other checkIfEmpAlreadyBookedForDate method is called and prodile ID is given
     * as argument from the singleton ProfileHolder
     */
    public boolean checkIfEmpAlreadyBookedForDate(LocalDate date){
        return checkIfEmpAlreadyBookedForDate(date, PH.getProfile().getEmployeeID());
    }

    public boolean checkIfEmpAlreadyBookedForDate(LocalDate date, int empID){
        boolean booked = false;
        String query = "SELECT employeeID FROM Bookings where date = '"+date.toString()+"';";
        try{
            results = statement.executeQuery(query);
            while(results.next()){
                if (empID == results.getInt(1)){
                    booked = true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return booked;
    }

    /* getDeskAvailability(LocalDate date)
     * returns a boolean array with availability for desk with ID corresponding to array index. Availability is false
     * for all unavailable circumstances including bookings and locked desk. It is not within the employees purview to
     * understand why a table is unavailable so this information is ommitted.
     */
    public boolean[] getDeskAvailability(LocalDate date){
        ArrayList<Integer> locked_deskIDs = getLockedDeskIDs(date);
        boolean[] desks = new boolean[12];
        Arrays.fill(desks, true);
        for (int i=0 ; i < desks.length ; i++){
            if (locked_deskIDs.contains(i)){
                desks[i] = false;
            }
        }
        String query = "SELECT DeskID FROM Bookings WHERE date = '" + date.toString() + "';";
        try{
            results = statement.executeQuery(query);
            while (results.next()){
                int desk_ID = results.getInt(1);
                desks[desk_ID] = false;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        db.finallyTableRead(results, statement);
        return desks;
    }

    /* getLockedDeskIDs(LocalDate date)
     * Called when finding desk availabilities, and by controllers finding the locked desks for a particular date.
     */
    public ArrayList<Integer> getLockedDeskIDs(LocalDate date){
        ArrayList<Integer> locked_deskIDs = new ArrayList <Integer>();
        String query = "SELECT DeskID FROM LockedDesks WHERE date = '" + date.toString() + "';";
        try{
            results = statement.executeQuery(query);
            while (results.next()){
                int desk_ID = results.getInt(1);
                locked_deskIDs.add(desk_ID);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        db.finallyTableRead(results, statement);
        return locked_deskIDs;
    }

    private int generateBookingID(){
        int bookingID = 1;
        String query = "SELECT MAX(bookingID) FROM Bookings;";
        try{
            results = statement.executeQuery(query);
            if (results.next()){
                bookingID = results.getInt(1) + 1;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        db.finallyTableRead(results, statement);
        return bookingID;
    }

    /* bookTable(int deskID, LocalDate date)
     * creates a new booking object with the relevant details, then calls the object method saveToDB() to write to db.
     */
    public void bookTable(int deskID, LocalDate date){
        EmployeeModel employee = (EmployeeModel) PH.getProfile();
        Booking booking = new Booking(generateBookingID(), deskID, date, employee, false);
        booking.saveToDB();
    }


}

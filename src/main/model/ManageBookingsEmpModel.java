package main.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/* Controller class for ManageBookingsEmpController EXTENDS ManageBookingsAdminModel.
 * Child class overrides getBookings() method to ensure that employee recieves the restricted information intended when managing
 * their bookings.
 * Uses Singleton Facade DBManagement and Singleton ProfileHolder.
 * Class controls db entries for when an employee is managing their bookings.
 */
public class ManageBookingsEmpModel extends ManageBookingsAdminModel{
    DBManagement db = DBManagement.getInstance();
    Connection connection;
    Statement statement;
    ResultSet results;
    ProfileHolder PH = ProfileHolder.getInstance();

    public ManageBookingsEmpModel() {
        connection = db.connect();
        statement = db.getStatement();
    }

    /* getBookings()
     * Override of ManageBookingsAdmin getBookings()
     * Only concerned with current employee's bookings, therefore singleton ProfileHolder is used for empID
     */
    public ArrayList<Booking> getBookings(){
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        String query = "SELECT * FROM Bookings WHERE employeeID = " + PH.getProfile().getEmployeeID() + ";";
        try{
            results = statement.executeQuery(query);
            while (results.next()){
                int booking_ID = results.getInt(1);
                int desk_ID = results.getInt(2);
                LocalDate date = LocalDate.parse(results.getString(3));
                int employee_ID = results.getInt(4);
                EmployeeModel employee = new EmployeeModel(employee_ID);
                boolean confirmed = (results.getInt(5)==1);
                Booking booking = new Booking(booking_ID, desk_ID, date, employee, confirmed);
                bookings.add(booking);
            }
        }
        catch (SQLException | EmployeeDoesNotExist e){
            e.printStackTrace();
        }
        return bookings;
    }



}

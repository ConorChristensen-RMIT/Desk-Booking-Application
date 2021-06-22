package main.model;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

/* Model class for GenerateReportsModel class
 * Uses Singleton Facade DBManagement.
 * Class generates CSV reports on specified dates or employees.
 * Class also accesses database to get relevant bookings using two overloaded getBookings() methods.
 */
public class GenerateReportsModel {
    DBManagement db = DBManagement.getInstance();
    Connection connection;
    Statement statement;
    ResultSet results;

    public GenerateReportsModel() {
        connection = db.connect();
        statement = db.getStatement();
    }

    public void generateDateReport(LocalDate date){
        ArrayList<Booking> bookings = getBookings(date);
        try{
            FileWriter csv_write = new FileWriter("Reports/"+date.toString()+".csv");
            Iterator<Booking> iter = bookings.iterator();
            csv_write.append("BookingID, desk ID, Employee ID\n");
            while (iter.hasNext()){
                Booking booking = iter.next();
                csv_write.append(booking.getBookingID()+","+booking.getDeskID_str()+","+booking.getEmployee().getEmployeeID()+"\n");
            }
            csv_write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateEmployeeReport(int empID) throws EmployeeDoesNotExist {
        EmployeeModel employee = new EmployeeModel(empID);
        try {
            FileWriter csv_write = new FileWriter("Reports/"+employee.getEmployeeID()+".csv");
            csv_write.append("EmpID, First Name, Last Name, Role, Username, Password, Secret Question, Secret Answer\n");
            csv_write.append(employee.getEmployeeID() + "," + employee.getFirst_name()+","+employee.getLast_name()+","
            + employee.getRole() + ","+employee.getUsername()+","+employee.getPassword()+","+employee.getSecretQn()+","+
                    employee.getSecretAns()+"\n\n");
            csv_write.append("Date, DeskID, BookingID\n");
            ArrayList<Booking> bookings = getBookings(empID);
            Iterator<Booking> iter = bookings.iterator();
            while (iter.hasNext()){
                Booking booking = iter.next();
                csv_write.append(booking.getDate().toString()+","+booking.getDeskID_str()+","+booking.getBookingID()+"\n");
            }
            csv_write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* getBookings(int empID) - OVERLOADED method
     * Returns ArrayList of bookings for a specified employee
     * Catches employee does not exist exception, as empID is not user input and must exist to be inputted in the fist
     * place (for this context).
     */
    private ArrayList<Booking> getBookings(int empID){
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        String query = "SELECT * FROM Bookings WHERE employeeID = " + empID + ";";
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
        db.finallyTableRead(results, statement);
        return bookings;
    }

    /* getBookings(LocalDate date) - OVERLOADED method
     * Returns ArrayList of bookings for a specified date
     */
    private ArrayList<Booking> getBookings(LocalDate date){
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        String query = "SELECT * FROM Bookings WHERE date = '" + date.toString() + "';";
        try{
            results = statement.executeQuery(query);
            while (results.next()){
                int booking_ID = results.getInt(1);
                int desk_ID = results.getInt(2);
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
        db.finallyTableRead(results, statement);
        return bookings;
    }

}

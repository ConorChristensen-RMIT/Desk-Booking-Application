package main.model;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Iterator;


/* Model class for ManageBookingsAdminController class
 * Uses Singleton Facade DBManagement.
 * Class controls the data processing and database entries for an admin managing bookings
 */
public class ManageBookingsAdminModel{
    DBManagement db = DBManagement.getInstance();
    Connection connection;
    Statement statement;
    ResultSet results;

    public ManageBookingsAdminModel() {
        connection = db.connect();
        statement = db.getStatement();
    }

    /* getBookings() - OVERLOADED method
     * Returns ArrayList of bookings for all bookings.
     * Catches employee does not exist exception, as empID is not user input and must exist to be inputted in the fist
     * place (for this context).
     */
    public ArrayList<Booking> getBookings(){
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        String query = "SELECT * FROM Bookings;";
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


    /* getBookings(Object object, String search_param) - OVERLOADED method
     * Returns ArrayList of bookings for a specified search parameter. Method handles different object types as input and
     * adjusts the SELECT query to the DB accordingly.
     * Catches employee does not exist exception, as empID is not user input and must exist to be inputted in the fist
     * place (for this context).
     */
    public ArrayList<Booking> getBookings(Object object, String search_param){
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        String query = "";
        if (object.getClass().equals(Integer.class)){
            int obj_int = (Integer) object;
            query = "SELECT * FROM Bookings WHERE "+search_param+" = " + obj_int + ";";
        }
        else if (object.getClass().equals(String.class)){
            String obj_string = (String) object;
            query = "SELECT * FROM Bookings WHERE "+search_param+" = '" + obj_string + "';";
        }
        else {
            boolean obj_boolean = (boolean) object;
            int confirmed_int = 0;
            if (obj_boolean) {confirmed_int = 1;}
            query = "SELECT * FROM Bookings WHERE "+search_param+" = " + confirmed_int + ";";
        }
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
        }
        db.finallyTableRead(results, statement);
        return bookings;
    }

    /* getUpcomingBookings(Object object, String search_param) - OVERLOADED method
     * Returns all bookings in the future for a specified search parameter. Uses getBookings() method to get all bookings,
     * then retrurns only bookings in the future.
     */
    public ArrayList<Booking> getUpcomingBookings(Object object, String search_param){
        ArrayList<Booking> upcoming_bookings = new ArrayList<Booking>();
        ArrayList<Booking> all_bookings = getBookings(object, search_param);
        Iterator<Booking> i = all_bookings.iterator();
        while (i.hasNext()) {
            Booking booking = i.next();
            if (booking.getDate().isAfter(LocalDate.now().minus(Period.ofDays(1)))) {
                upcoming_bookings.add(booking);
            }
        }
        return upcoming_bookings;
    }

    /* getUpcomingBookings() - OVERLOADED method
     * Returns all bookings in the future for all upcoming bookings. Uses getBookings() method to get all bookings,
     * then retrurns only bookings in the future.
     */
    public ArrayList<Booking> getUpcomingBookings(){
        ArrayList<Booking> upcoming_bookings = new ArrayList<Booking>();
        ArrayList<Booking> all_bookings = getBookings();
        Iterator<Booking> i = all_bookings.iterator();
        while (i.hasNext()) {
            Booking booking = i.next();
            if (booking.getDate().isAfter(LocalDate.now().minus(Period.ofDays(1)))) {
                upcoming_bookings.add(booking);
            }
        }
        return upcoming_bookings;
    }

    /* getPreviousBookings()
     * Returns all bookings in the past for all upcoming bookings. Uses getBookings() method to get all bookings,
     * then returns only bookings in the past.
     */
    public ArrayList<Booking> getPreviousBookings(){
        ArrayList<Booking> previous_bookings = new ArrayList<Booking>();
        ArrayList<Booking> all_bookings = getBookings();
        Iterator<Booking> i = all_bookings.iterator();
        while (i.hasNext()) {
            Booking booking = i.next();
            if (booking.getDate().isBefore(LocalDate.now())) {
                previous_bookings.add(booking);
            }
        }
        return previous_bookings;
    }

    /* getBookingSearchParams()
     * Gets table_info from the SQLite table and returns an array list of searchable parameters within that table.
     */
    public ArrayList<String> getBookingSearchParams(){
        ArrayList<String> search_params = new ArrayList<String>();
        String query = "PRAGMA table_info (Bookings)";
        try{
            results = statement.executeQuery(query);
            while (results.next()){
                search_params.add(results.getString(2));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableRead(results, statement);
        return search_params;
    }

    /* getBookingSearchParamTypes()
     * Gets table_info from the SQLite table and returns an array list of searchable parameters types (TEXT, INTEGER etc.)
     * within that table.
     */
    public ArrayList<String> getBookingSearchParamTypes(){
        ArrayList<String> search_param_types = new ArrayList<String>();
        String query = "PRAGMA table_info (Bookings)";
        try{
            results = statement.executeQuery(query);
            while (results.next()){
                search_param_types.add(results.getString(3));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableRead(results, statement);
        return search_param_types;
    }

    public void confirmBooking(Booking booking){
        booking.confirm();
    }


    public void changeBooking(int deskID, LocalDate date, Booking booking){
        booking.updateBooking(deskID, date);
    }

    /* getLockdownLevelStrings()
     * returns a String ArrayList containing the levels of lockdown which are used by the calling class to populate a
     * choicebox.
     *
     */
    public ArrayList<String> getLockdownLevelStrings(){
        ArrayList<String> lockdown_levels = new ArrayList<String>();
        lockdown_levels.add("Level 0 - Business as usual");
        lockdown_levels.add("Level 1 - Lockdown every 2nd desk");
        lockdown_levels.add("Level 2 - No more bookings");
        return lockdown_levels;
    }

    /* engageLockdown(String level, LocalDate start, LocalDate end)
     * Manage all DB entries relevent to staging a certain level of lockdown specified by user input.
     */
    public void engageLockdown(String level, LocalDate start, LocalDate end){
        ArrayList<String> levels_arr = getLockdownLevelStrings();
        ArrayList<String> indiv_dates = new ArrayList<>();
        //Create array list of dates converted to strings with DB Format.
        for (LocalDate date = start ; date.isBefore(end) ; date = date.plusDays(1)){
            indiv_dates.add(date.toString());
        }
        Iterator<String> iter = indiv_dates.iterator();
        int LL = levels_arr.indexOf(level);
        if (LL == 0){
            enforceLevel0(iter);
        }
        else if (LL == 1) {
            //Lockdown every second desk
            int[] L1_deskIDs = new int[]{1, 3, 4, 6, 9, 11};
            enforceLevel1or2(iter, L1_deskIDs);
        }
        else if (LL == 2){
            //Lockdown all desks
            int[] L2_deskIDs = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
            enforceLevel1or2(iter, L2_deskIDs);
        }
        return;
    }

    /* enforceLevel1or2(Iterator<String> iter, int[] deskIDs)
     * Manage all DB entries relevent to locking down a given array of desk numbers.
     */
    private void enforceLevel1or2(Iterator<String> iter, int[] deskIDs){
        while (iter.hasNext()){
            String date = iter.next();
            for (int i = 0; i < deskIDs.length; i++) {
                try {
                    String query = "DELETE FROM Bookings WHERE date = '" + date + "' AND deskID = " + deskIDs[i] + ";";
                    statement.executeUpdate(query);
                    db.finallyTableEditOnly(statement);

                    query = "INSERT into LockedDesks (deskID, date) VALUES (" + deskIDs[i] + ", '" + date + "');";
                    statement.executeUpdate(query);
                    db.finallyTableEditOnly(statement);
                } catch (SQLException throwables){
                    // Primary Key Constraint exception expected if desk already locked which is okay. Do nothing and continue
                    // iterating. Bookings will have been deleted first before exception.
                }
            }
        }
    }

    /* enforceLevel0(Iterator<String> iter)
     * ease all lockdown restrictions.
     */
    private void enforceLevel0(Iterator<String> iter){
        while (iter.hasNext()){
            String query = "DELETE from LockedDesks WHERE date = '" + iter.next() + "';";
            try{
                statement.executeUpdate(query);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            db.finallyTableEditOnly(statement);
        }
    }

    public boolean checkIfDeskLockedDown(int desk_ID, LocalDate date){
        boolean is_locked = false;
        String query = "SELECT deskID FROM LockedDesks WHERE deskID = "+desk_ID+" AND date = '"+date.toString()+"';";
        try{
            results = statement.executeQuery(query);
            if (results.next()){
                is_locked = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return is_locked;
    }

    public void lockdownDesk(int deskID, LocalDate date){
        String query = "INSERT INTO LockedDesks (deskID, date) VALUES ("+deskID+", '"+date.toString()+"');";
        try{
            statement.executeUpdate(query);
            db.finallyTableEditOnly(statement);
            query = "SELECT bookingID FROM Bookings WHERE deskID = "+deskID+" and date = '"+date.toString()+"';";
            results = statement.executeQuery(query);
            if (results.next()){
                query = "DELETE FROM Bookings where bookingID = "+results.getInt(1)+";";
                statement.executeUpdate(query);
            }
            db.finallyTableRead(results, statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void unlockDesk(int deskID, LocalDate date){
        String query = "DELETE FROM LockedDesks WHERE deskID = "+deskID+" AND date = '"+date+"';";
        try{
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableEditOnly(statement);
    }



}

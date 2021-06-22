package main.model;
import javafx.beans.property.SimpleStringProperty;
import java.sql.SQLException;

/* EmployeeModel class EXTENDS ProfileModel.
 * Class implements Facade Singleton DBManagement to assist with DB Communication.
 * Class uses Simple String properties to assist populating tableviews in UI. Getter setter methods are utilised for this.
 * Uses update methods to update employee information within the object and within the DB.
 */
public class EmployeeModel extends ProfileModel{

    private SimpleStringProperty first_name_ss;
    private SimpleStringProperty last_name_ss;
    private SimpleStringProperty username_ss;
    private SimpleStringProperty employeeID_ss;
    private SimpleStringProperty role_ss;
    private SimpleStringProperty enabled_ss;

    /* Constructor for new Employeel
     * Implements parent constructor and sets Simple String properties
     */
    public EmployeeModel(String username, String first_name, String last_name, String password, int employeeID, String role, String secret_qn, String secret_ans){
        super(username, first_name, last_name, password, employeeID, role, secret_qn, secret_ans);
        setEmployee();
        this.first_name_ss = new SimpleStringProperty(first_name);
        this.last_name_ss = new SimpleStringProperty(last_name);
        this.username_ss = new SimpleStringProperty(username);
        this.employeeID_ss = new SimpleStringProperty(String.valueOf(employeeID));
        this.role_ss = new SimpleStringProperty(role);
        setEnabled(true);
        this.enabled_ss = new SimpleStringProperty(getEnabledString());
    }

    /* Constructor for existing Employee in database. Throws EmployeeDoesNotExist exception to be handled by caller.
     * Implements parent constructor and sets Simple String properties
     */
    public EmployeeModel(int employeeID) throws EmployeeDoesNotExist {
        //Pulls profile information from database using parent class constructor.
        super(employeeID);
        //Set all simple string properties.
        this.first_name_ss = new SimpleStringProperty(getFirst_name());
        this.last_name_ss = new SimpleStringProperty(getLast_name());
        this.username_ss = new SimpleStringProperty(getUsername());
        this.employeeID_ss = new SimpleStringProperty(String.valueOf(getEmployeeID()));
        this.role_ss = new SimpleStringProperty(getRole());
        this.enabled_ss = new SimpleStringProperty(getEnabledString());
        //Set employee boolean
        setEmployee();
    }

    /* registerEmployee() throws DuplicateIDException, DuplicateUsernameException
     * Saves new employee information to database.
     * Throws exceptions to be handled by caller class indicating a Primary Key clash with an existing employee in the
     * DB.
     */
    public void registerEmployee() throws DuplicateIDException, DuplicateUsernameException{
        String query = "SELECT username FROM Profile where username = '"+getUsername()+"';";
        try {
            results = statement.executeQuery(query);
            if (results.next()){
                throw new DuplicateUsernameException();
            }
            query = "SELECT id FROM Profile WHERE id = "+getEmployeeID()+";";
            results = statement.executeQuery(query);
            if (results.next()){
                throw new DuplicateIDException();
            }
            else {
                query = "INSERT INTO Profile (id, firstname, lastname, role, username, password, isAdmin, secretQuestion, " +
                        "secretAnswer, enabled) VALUES ("+getEmployeeID()+", '"+getFirst_name()+"', '"+getLast_name()+"', '"
                        +getRole()+"' , '"+getUsername()+"', '"+getPassword()+"', 0, '"+getSecretQn()+"', '"+getSecretAns()+"', 1);";
                statement.executeUpdate(query);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableRead(results, statement);
    }

    /* disableAccount()
     * Account can no longer log in.Method updates DB and current object information.
     */
    public void disableAccount(){
        this.enabled_ss.setValue("No");
        this.setEnabled(false);
        String query = "UPDATE Profile SET enabled = 0 WHERE id = "+getEmployeeID()+";";
        try{
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableEditOnly(statement);
    }

    /* enableAccount()
     * Account can log in.Method updates DB and current object information.
     */
    public void enableAccount(){
        this.enabled_ss.setValue("Yes");
        this.setEnabled(true);
        String query = "UPDATE Profile SET enabled = 1 WHERE id = "+getEmployeeID()+";";
        try{
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableEditOnly(statement);
    }

    /* deleteAccount()
     * Deletes entry of account within the DB and all bookings made by this account.
     */
    public void deleteAccount(){
        String query = "DELETE FROM Profile WHERE id = "+getEmployeeID()+";";
        try{
            statement.executeUpdate(query);
            query = "DELETE FROM Bookings WHERE employeeID = "+getEmployeeID()+";";
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableEditOnly(statement);
    }

    public void updateUsername(String name){
        setUsername(name);
        String query = "UPDATE Profile SET username = '"+getUsername()+"' WHERE id = "+getEmployeeID()+";";
        try{
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableEditOnly(statement);
    }

    public void updateFirstName(String name){
        setFirstName(name);
        String query = "UPDATE Profile SET firstname = '"+getFirst_name()+"' WHERE id = "+getEmployeeID()+";";
        try{
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableEditOnly(statement);
    }

    public void updateLastName(String name){
        setLastName(name);
        String query = "UPDATE Profile SET lastname = '"+getLast_name()+"' WHERE id = "+getEmployeeID()+";";
        try{
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableEditOnly(statement);
    }

    public void updateRole(String role){
        setRole(role);
        String query = "UPDATE Profile SET role = '"+getRole()+"' WHERE id = "+getEmployeeID()+";";
        try{
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableEditOnly(statement);
    }

    /* START SIMPLE STRING GET/SET METHODS
     * -----------------------------------
     */
    public String getFirst_name_ss(){return first_name_ss.get();}

    public String getLast_name_ss(){return last_name_ss.get();}

    public String getUsername_ss(){return username_ss.get();}

    public String getEmployeeID_ss(){return employeeID_ss.get();}

    public String getRole_ss(){return role_ss.get();}

    public String getEnabled_ss(){return enabled_ss.get();}
    /* ----------------------------------
     * END SIMPLE STRING GET/SET METHODS
     */
}

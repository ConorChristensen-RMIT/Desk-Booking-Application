package main.model;

import main.SQLConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ProfileModel {
    private String username;
    private String first_name;
    private String last_name;
    private String password;
    private int employeeID;
    private String role;
    private boolean is_admin;
    private String secret_qn;
    private String secret_ans;
    private boolean enabled = true;
    DBManagement db = DBManagement.getInstance();

    Connection connection;
    Statement statement;
    ResultSet results;

    /* Basic constructor, used for profile regdistration. Requires all private variables to be inputted as argument.
     */
    public ProfileModel(String username, String first_name, String last_name, String password, int employeeID, String role, String secret_qn, String secret_ans){
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.employeeID = employeeID;
        this.role = role;
        this.secret_qn = secret_qn;
        this.secret_ans = secret_ans;
        this.connection = db.connect();
        this.statement = db.getStatement();
        setEnabled(true);
    }

    /* Constructor for ProfileModel when employee exists within database. Use Primary key variable to then pull information
     * from the db to build class. Throws EmployeeDoesNotExist exception which must be handled by caller.
     */
    public ProfileModel(int employeeID) throws EmployeeDoesNotExist {
        connection = db.connect();
        if (connection == null)
            System.exit(1);
        else{
            try {
                statement = db.getStatement();
                String query = "SELECT * FROM Profile where id = " + String.valueOf(employeeID) + ";";
                results = statement.executeQuery(query);
                if (results.next()){
                    this.username = results.getString(5);
                    this.first_name = results.getString(2);
                    this.last_name = results.getString(3);
                    this.password = results.getString(6);
                    this.employeeID = results.getInt(1);
                    this.role = results.getString(4);
                    this.secret_qn = results.getString(8);
                    this.secret_ans = results.getString(9);
                    if (results.getInt(7)==1){ setAdmin();}
                    else if (results.getInt(7)==0) { setEmployee();}
                    if (results.getInt(10) == 1) {setEnabled(true);}
                    else if (results.getInt(10)==0) {setEnabled(false);}
                }
                else {
                    throw new EmployeeDoesNotExist();
                }
                db.finallyTableRead(results, statement);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    public String getEnabledString(){
        String enabled = "Yes";
        if (!getEnabled()) {enabled = "No";}
        return enabled;
    }

    public int getEnabledInt(){
        int enabled = 1;
        if (!getEnabled()){ enabled = 0;}
        return enabled;
    }

    public boolean getEnabled(){
        return this.enabled;
    }

    public String getFirst_name() {
        return this.first_name;
    }

    public String getLast_name() {
        return this.last_name;
    }

    public int getEmployeeID() {return this.employeeID;}

    public String getUsername() {
        return this.username;
    }

    public boolean getIsAdmin(){return this.is_admin;}

    public int getIsAdminInt(){
        int return_val = 0;
        if (is_admin){
            return_val = 1;
        }
        return return_val;
    }

    public void setAdmin(){
        is_admin = true;
    }

    public void setEmployee(){
        is_admin = false;
    }

    public String getSecretQn(){
        return this.secret_qn;
    }

    public String getSecretAns(){
        return this.secret_ans;
    }

    public String getRole(){
        return this.role;
    }

    public String getPassword(){
        return this.password;
    }

    public void resetPassword(String new_pass){
        String query = "UPDATE Profile SET password = '"+new_pass+"' WHERE id = "+getEmployeeID()+";";
        try{
            statement.executeUpdate(query);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setUsername(String name){
        this.username = name;
    }

    public void setFirstName(String name){
        this.first_name = name;
    }

    public void setLastName(String name){
        this.first_name = name;
    }

    public void setRole(String name){
        this.first_name = name;
    }


}

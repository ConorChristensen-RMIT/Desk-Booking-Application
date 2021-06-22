package main.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/* Model class for ManageAccountsController class
 * Uses Singleton Facade DBManagement.
 * Generates and sets passwords as required in the Landing Page.
 * Gets secret question and answers as required in the Landing Page
 */
public class ManageAccountsModel {
    DBManagement db = DBManagement.getInstance();
    Connection connection;
    Statement statement;
    ResultSet results;

    public ManageAccountsModel(){
        connection = db.connect();
        statement = db.getStatement();
    }

    public ArrayList<EmployeeModel> getCurrentEmployees(){
        ArrayList<EmployeeModel> current_employees = new ArrayList<>();
        String query = "SELECT id FROM Profile WHERE isAdmin = 0;";
        try{
            results = statement.executeQuery(query);
            while (results.next()){
                EmployeeModel employee = new EmployeeModel(results.getInt(1));
                current_employees.add(employee);
            }
        } catch (SQLException | EmployeeDoesNotExist throwables) {
            throwables.printStackTrace();
        }
        db.finallyTableRead(results, statement);
        return current_employees;
    }

}

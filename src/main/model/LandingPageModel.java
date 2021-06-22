package main.model;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Statement;

/* Model class for LandingPageModel class
 * Uses Singleton Facade DBManagement.
 * Generates and sets passwords as required in the Landing Page.
 * Gets secret question and answers as required in the Landing Page
 */
public class LandingPageModel {
    DBManagement db = DBManagement.getInstance();
    Connection connection;
    Statement statement;

    public LandingPageModel(){
        connection = db.connect();
        statement = db.getStatement();
    }

    public String[] getSecrets(int input_id) throws EmployeeDoesNotExist {
        String[] return_tuple = new String[2];
        EmployeeModel employee = new EmployeeModel(input_id);
        return_tuple[0] = employee.getSecretQn();
        return_tuple[1] = employee.getSecretAns();
        return return_tuple;
    }

    public String setNewPassword(int emp_id){
        String new_pass = "";
        try{
            EmployeeModel employee = new EmployeeModel(emp_id);
            new_pass = generateRandomPassword();
            employee.resetPassword(new_pass);

        } catch (EmployeeDoesNotExist employeeDoesNotExist) {
            employeeDoesNotExist.printStackTrace();
        }
        return new_pass;
    }

    /* generateRandomPassword() - internally called private method
     * Code developed from example provided at https://www.techiedelight.com/generate-random-alphanumeric-password-java/
     * returns a new random password string.
     */
    private String generateRandomPassword(){
        String available_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        SecureRandom rand_int = new SecureRandom();
        StringBuilder new_pass = new StringBuilder();
        int next_char_index;
        for (int i = 0 ; i < 10 ; i++){
            next_char_index = rand_int.nextInt(available_chars.length()-1);
            new_pass.append(available_chars.charAt(next_char_index));
        }
        return new_pass.toString();
    }

}

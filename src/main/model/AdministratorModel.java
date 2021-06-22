package main.model;


/* AdministratorModel extends Profile Class as child.
 * Is a Profile with admin boolean variable set using method setAdmin during construction. No extra methods needed in
 * this implementation.
 */
public class AdministratorModel extends ProfileModel{

    public AdministratorModel(String username, String first_name, String last_name, String password, int employeeID, String role, String secret_qn, String secret_ans){
        super(username, first_name, last_name, password, employeeID, role, secret_qn, secret_ans);
        setAdmin();
    }



}

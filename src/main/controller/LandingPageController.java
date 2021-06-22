package main.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.model.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/* Controller class for LandingPage.fxml
 * Uses Singleton Facades with SceneSwitcher and PaneManager to help control UI.
 * Class controls UI for log in and register functions.
 */
public class LandingPageController implements Initializable {
    private LoginModel loginModel = new LoginModel();
    private PaneManager pc = PaneManager.getInstance();
    private SceneSwitcher sw = SceneSwitcher.getInstance();
    private LandingPageModel model = new LandingPageModel();
    private String[] reco_secretQA;
    String default_choice;
    ObservableList<String> secret_qn_options = FXCollections.observableArrayList("What was the make of your first car?", "What was the name of your first pet?", "What was the name of your favourite teacher?",
            "What primary school did you attend?", "What was the name of your childhood best friend?");

    @FXML private Label is_Connected;
    @FXML private TextField txt_Username;
    @FXML private TextField txt_Password;
    @FXML Button back_to_landing_button;
    @FXML private TextField emp_id_in;
    @FXML private TextField username_in;
    @FXML private TextField first_name_in;
    @FXML private TextField last_name_in;
    @FXML private TextField role_in;
    @FXML private TextField password_in;
    @FXML private TextField secret_ans_in;
    @FXML private ChoiceBox secret_qn_choice;
    @FXML private Label fields_warning1;
    @FXML private Label fields_warning2;
    @FXML private Label register_warning_label;
    @FXML private TextField recover_emp_id_check;
    @FXML private Label recover_secret_qn_label;
    @FXML private TextField secret_ans_check;
    @FXML private Button secret_ans_check_button;
    @FXML private Label check_emp_id_warning_label;
    @FXML private Label incorrect_ans_label;
    @FXML private Label new_pass_label;
    @FXML private Text new_pass_text;

    /* Override initialize(URL, ResourceBundle) from Initializable.
     * Set items in choice box, and default value.
     * Display db Connection Status
     * Uses model class LandingPageModel to handle data processing and db communications.
     * Uses LoginModel class provided by project template to log users in.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        secret_qn_choice.setItems(secret_qn_options);
        default_choice = "Please select";
        secret_qn_choice.setValue(default_choice);
        if (loginModel.isDbConnected()){
            is_Connected.setText("Connected");
        }else{
            is_Connected.setText("Not Connected");
        }

    }
    /* Login(ActionEvent event) handles button event to request log in.
     * Calls loginModel to check login details. Continues to MainMenu stage if correct, sets warning text if incorrect.
     */
    public void Login(ActionEvent event){
        try {
            if (loginModel.isLogin(txt_Username.getText(),txt_Password.getText())){
                is_Connected.setText("Logged in successfully");
                createMainMenu();
                final Node source = (Node) event.getSource();
                final Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            }
            else{
                is_Connected.setText("username and password is incorrect, or account is disabled");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* createMainMenu() is internally called private event.
     * Creates main menu stage. Called before landing page stage is closed
     * Checks ProfileHolder singleton for profile logging in. Directs to respective main menu fxml if administrator or
     * employee.
     */
    private void createMainMenu() {
        ProfileHolder PH = ProfileHolder.getInstance();
        Stage main_menu_stage = new Stage();
        Parent root;
        FXMLLoader loader;
        try{
            if (PH.getProfile().getIsAdmin()) {
                loader = new FXMLLoader(getClass().getResource("../ui/MainMenuAdmin.fxml"));
            }
            else{
                loader = new FXMLLoader(getClass().getResource("../ui/MainMenuEmp.fxml"));
            }
            root = loader.load();
            Scene scene = new Scene(root);
            main_menu_stage.setScene(scene);
            main_menu_stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* register(Event event) handles button event to start registration process.
     * Changes pane to beginning of registration. UI only
     */
    public void register(Event event){
        pc.goForward(event, 2);
    }

    /* backToRegister(Event event) handles button event to move back to first registration pane.
     * Changes pane to beginning of registration. UI only. Initial pane will retain previously inputted values for user
     * reference.
     */
    public void backToRegister1(Event event){
        pc.goBack(event, 1);
    }

    /* toRegister2(Event event) handles button event to move forward to second registration pane.
     * Changes pane to last pane of registration. UI only. Initial pane will retain previously inputted values for user
     * reference. Checks all fields in first pane have been filled.
     */
    public void toRegister2(Event event){
        fields_warning1.setText("");
        boolean fields_not_filled = ((emp_id_in.getText().equals("")) | (first_name_in.getText().equals("")) | (last_name_in.getText().equals(""))
                | (role_in.getText().equals("")));
        if (fields_not_filled){
            fields_warning1.setText("Please fill in all the fields!");
        }
        else {
            pc.goForward(event, 1);
        }

    }

    /* finishRegistering(Event event) handles button event to complete registration.
     * Checks fields have all been filled. Exceptions are thrown for registering an existing employee or incorrect
     * field values. Warning labels are set if necessary, otherwise model is called to register profile with inputted
     * values.
     */
    public void finishRegistering(Event event){
        fields_warning2.setText("");
        boolean fields_not_filled = ((password_in.getText().equals("")) | (secret_qn_choice.getValue().toString().equals(default_choice)) |
                (secret_ans_in.getText().equals("")));
        if (fields_not_filled){
            fields_warning2.setText("Please fill in all the fields!");
        }
        else {
            try{
                EmployeeModel employee = new EmployeeModel(username_in.getText(), first_name_in.getText(), last_name_in.getText(),
                        password_in.getText(), Integer.valueOf(emp_id_in.getText()), role_in.getText(), secret_qn_choice.getValue().toString(),
                        secret_ans_in.getText());
                employee.registerEmployee();
                backToLanding(event);
            } catch (DuplicateIDException e) {
                register_warning_label.setText("The employee ID entered is already registered.");
                pc.goForward(event, 1);
            } catch (DuplicateUsernameException e) {
                register_warning_label.setText("The username entered is already registered.");
                pc.goForward(event, 1);
            }
        }
    }

    /* confirmEmpID(Event event) handles button event to confirm empID in password recovery routine.
     * Handles exceptions thrown by model class and will set warning labels accordingly.
     * If input is valid, model class is called to retrieve secret questions and answers and populate the global list
     * variable reco_sectretQA with the values.
     */
    public void confirmEmpId(Event event){
        secret_ans_check_button.setDisable(true);
        secret_ans_check.setDisable(true);
        secret_ans_check.setText("");
        check_emp_id_warning_label.setText("");
        new_pass_label.setText("");
        try{
            reco_secretQA = model.getSecrets(Integer.valueOf(recover_emp_id_check.getText()));
            recover_secret_qn_label.setText(reco_secretQA[0]);
            secret_ans_check_button.setDisable(false);
            secret_ans_check.setDisable(false);
        }
        catch (NumberFormatException e){
            check_emp_id_warning_label.setText("Invalid: Employee ID must be numeric only");
        }
        catch (EmployeeDoesNotExist em){
            check_emp_id_warning_label.setText("The Employee ID is not registered");
        }
    }

    /* checkAnswer(Event event) handles button event to check inputted secret anser.
     * If input is valid, diaplayNewPassword() is called to show user new password.
     * Warning label used if input not valid.
     */
    public void checkAnswer(Event event){
        incorrect_ans_label.setText("");
        String answer = secret_ans_check.getText();
        new_pass_label.setText("");
        if (answer.toLowerCase().equals(reco_secretQA[1].toLowerCase())){
            displayNewPassword();
        }
        else{
            incorrect_ans_label.setText("Incorrect answer. Try again.");
        }
    }

    /* displayNewPassword() is an internally called private method.
     * Sets new password label to show new password generated by the model class.
     */
    private void displayNewPassword(){
        new_pass_label.setText("");
        new_pass_text.setVisible(true);
        int empID = Integer.valueOf(recover_emp_id_check.getText());
        String new_pass = model.setNewPassword(empID);
        new_pass_label.setText("'"+new_pass+"'");
    }

    /* warningPressOkay(Event event) handles button event when warning is read and exited.
     * Moves UI back to landing page using backToLanding() method
     */
    public void warningPressOkay(Event event){
        backToLanding(event);
    }

    /* forgotPassword(Event event) handles button event for selecting forgotten password.
     * Moves UI back to forgotten password pane
     */
    public void forgotPassword(Event event){
        pc.goForward(event, 4);
    }

    /* selectLogin(Event event) handles button event for selecting login.
     * Moves UI back to login pane
     */
    public void selectLogin(Event event){
        pc.goForward(event, 1);
    }

    /* backToLanding(Event event) handles button event for moving back to landing page. Also called internally.
     * Moves UI back to landing pane by reloading scene. Ensures UI behaviour.
     */
    public void backToLanding(Event event){
        sw.switchScene(event, "../ui/LandingPage.fxml");
    }


}

package main.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import main.model.*;
import java.net.URL;
import java.util.ResourceBundle;

/* Controller class for ManageAccounts.fxml
 * Uses Singleton Facades with SceneSwitcher and PaneManager.
 * Class controls UI for switching scenes depending on Main Menu interaction.
 */
public class ManageAccountsController implements Initializable {
    LandingPageModel LPmodel = new LandingPageModel();
    ManageAccountsModel model = new ManageAccountsModel();
    PaneManager pc = PaneManager.getInstance();
    SceneSwitcher sw = SceneSwitcher.getInstance();
    EmployeeModel selected_account;
    String default_choice;
    ObservableList<String> secret_qn_options = FXCollections.observableArrayList("What was the make of your first car?", "What was the name of your first pet?", "What was the name of your favourite teacher?",
            "What primary school did you attend?", "What was the name of your childhood best friend?");

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
    @FXML Button delete_account_button;
    @FXML Button enable_account_button;
    @FXML Button disable_account_button;
    @FXML TableView<EmployeeModel> current_account_tableview;
    @FXML TableColumn<EmployeeModel, String> emp_id_column;
    @FXML TableColumn<EmployeeModel, String> first_name_column;
    @FXML TableColumn<EmployeeModel, String> last_name_column;
    @FXML TableColumn<EmployeeModel, String> username_column;
    @FXML TableColumn<EmployeeModel, String> role_column;
    @FXML TableColumn<EmployeeModel, String> enabled_column;

    /* Override initialize(URL, ResourceBundle) from Initializable.
     * Set items in choice box, and default value.
     * populate current employees table by calling populateCurrentEmployees() method. Create listener for employee tableview
     * using createAccountListener() method.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        secret_qn_choice.setItems(secret_qn_options);
        default_choice = "Please select";
        secret_qn_choice.setValue(default_choice);
        populateCurrentEmployees();
        // Code for listener was developed from Stack Exchange URL - "https://stackoverflow.com/questions/41323945/javafx-combobox-add-listener-for-selected-item-value"
        // Accessed 01/06/2021 - Conor Christensen
        createCurrentAccountListener();
    }

    /* createCurrentAccountListener() creates a listener that updates UI button usability and account selection variable
     * depending on selection of account from the tableview.
     */
    private void createCurrentAccountListener(){
        current_account_tableview.getSelectionModel().selectedItemProperty().addListener((n, oldaccount, newaccount) -> {
            selected_account = newaccount;
            if (newaccount == null) {
                enable_account_button.setDisable(true);
                disable_account_button.setDisable(true);
                delete_account_button.setDisable(true);
            } else {
                if (newaccount.getEnabled()) {
                    enable_account_button.setDisable(true);
                    disable_account_button.setDisable(false);
                    delete_account_button.setDisable(false);
                } else {
                    enable_account_button.setDisable(false);
                    disable_account_button.setDisable(true);
                    delete_account_button.setDisable(false);
                }
            }
        });
    }

    /* backToMainMenu(ActionEvent) Handles 'back' button event and uses SceneSwitcher to return to the main menu.
     */
    public void backToMainMenu(Event event){
        sw.switchScene(event, "../ui/MainMenuAdmin.fxml");
    }

    /* addAccount(MousEvent event) handles mouse event to move to add account menu.
     * Changes scene. UI only
     */
    public void addAccount(MouseEvent event){
        pc.goForward(event, 1);
    }

    /* warningPressOkay(Event event) handles button press event.
     * Called when warning message is acknowledged. Returns UI back to previous menu.
     */
    public void warningPressOkay(Event event){
        backToAccountManager(event);
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
                backToAccountManager(event);
            } catch (DuplicateIDException e) {
                register_warning_label.setText("The employee ID entered is already registered.");
                pc.goForward(event, 1);
            } catch (DuplicateUsernameException e) {
                register_warning_label.setText("The username entered is already registered.");
                pc.goForward(event, 1);
            }
        }
    }

    /* backToAccountManager(Event event) handles button event for moving back to account manager menu.
     * Moves UI back to account manager menu by reloading scene. Ensures UI behaviour.
     */
    public void backToAccountManager(Event event){
        sw.switchScene(event, "../ui/ManageAccounts.fxml");
    }

    /* currentAccounts(MouseEvent event) handles mouse click event for moving forward to current account menu.
     * Moves UI forward by changing panes. Populates current account table when navigating.
     */
    public void currentAccounts(MouseEvent event){
        populateCurrentEmployees();
        pc.goForward(event, 4);

    }

    /* populateCurrentEmployees() - internally called private method.
     * Populates the columns with values from employees retrieved with model class.
     */
    private void populateCurrentEmployees(){
        ObservableList<EmployeeModel> employees = FXCollections.observableArrayList(model.getCurrentEmployees());
        setColumnValues(employees);

    }

    /* setColumnValues(ObservableList) - internally called private method.
     * Used to set column values with setCellValueFactory() method.
     */
    private void setColumnValues(ObservableList<EmployeeModel> employees){
        first_name_column.setCellValueFactory(new PropertyValueFactory<>("first_name_ss"));
        last_name_column.setCellValueFactory(new PropertyValueFactory<>("last_name_ss"));
        emp_id_column.setCellValueFactory(new PropertyValueFactory<>("employeeID_ss"));
        role_column.setCellValueFactory(new PropertyValueFactory<>("role_ss"));
        username_column.setCellValueFactory(new PropertyValueFactory<>("username_ss"));
        enabled_column.setCellValueFactory(new PropertyValueFactory<>("enabled_ss"));
        current_account_tableview.setItems(employees);
        current_account_tableview.getSortOrder().add(emp_id_column);
        first_name_column.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    public void deleteAccount(Event event){
        selected_account.deleteAccount();
        populateCurrentEmployees();
    }

    public void disableAccount(Event event){
        selected_account.disableAccount();
        populateCurrentEmployees();
    }

    public void enableAccount(Event event){
        selected_account.enableAccount();
        populateCurrentEmployees();
    }


    public void updateUsername(TableColumn.CellEditEvent<EmployeeModel, String> event){
        selected_account.updateUsername(event.getNewValue());
        populateCurrentEmployees();
    }

    public void updateFirstName(TableColumn.CellEditEvent<EmployeeModel, String> event){
        selected_account.updateFirstName(event.getNewValue());
        populateCurrentEmployees();
    }

    public void updateLastName(TableColumn.CellEditEvent<EmployeeModel, String> event){
        selected_account.updateLastName(event.getNewValue());
        populateCurrentEmployees();
    }

    public void updateRole(TableColumn.CellEditEvent<EmployeeModel, String> event){
        selected_account.updateRole(event.getNewValue());
        populateCurrentEmployees();
    }
}

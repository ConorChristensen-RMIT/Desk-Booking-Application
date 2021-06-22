package main.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.model.EmployeeDoesNotExist;
import main.model.GenerateReportsModel;

/* Controller class for GenerateReports.fxml
 * Uses Singleton Facade with SceneSwitcher to help control UI.
 * Class controls UI for admin report creation process.
 * Uses model class GenerateReportsModel() to handle data processing and db communications.
 */
public class GenerateReportsController {
    SceneSwitcher sw = SceneSwitcher.getInstance();
    GenerateReportsModel model = new GenerateReportsModel();

    @FXML DatePicker report_date_picker;
    @FXML Label date_warning_label;
    @FXML Label id_warning_label;
    @FXML TextField report_emp_id_in;

    /* generateBookingReport(Event) - event handler for selecting booking report generation.
     * Checks date picker for date selection. Calls model to generate report.
     */
    public void generateBookingReport(Event event){ ;
        date_warning_label.setText("");
        if (report_date_picker.getValue() == null){
            date_warning_label.setText("You must select a date!");
        }
        else {
            model.generateDateReport(report_date_picker.getValue());
        }
    }

    /* generateEmployeeReport(Event) - event handler for selecting Employee report generation.
     * Checks date picker for date selection. Calls model to generate report.
     */
    public void generateEmployeeReport(Event event){
        id_warning_label.setText("");
        if (report_emp_id_in.getText().equals("")){
            id_warning_label.setText("Please enter in an ID");
        }
        try{
            model.generateEmployeeReport(Integer.valueOf(report_emp_id_in.getText()));
        } catch (NumberFormatException e){
            id_warning_label.setText("Employee ID must be \nnumeric.");
        } catch (EmployeeDoesNotExist n) {
            id_warning_label.setText("Employee does not exist");
        }
    }

    /* backToMainMenu(ActionEvent) Handles 'back' button event and uses SceneSwitcher to return to the main menu.
     */
    public void backToMainMenu(Event event){
        sw.switchScene(event, "../ui/MainMenuAdmin.fxml");
    }
}

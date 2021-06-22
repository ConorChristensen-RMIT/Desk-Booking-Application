package main.controller;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import main.model.*;
import java.net.URL;
import java.util.ResourceBundle;

/* Controller class for ManageBookingsEmp.fxml. EXTENDS ManageBookingsAdminController.
 * This child class implements many methods from parent called by the FXML UI. All methods with lesser or restricted
 * functionality for employees are overriden.
 * Uses Singleton Facades with SceneSwitcher and PaneManager.
 * Class controls UI for switching scenes depending on Main Menu interaction.
 * Uses controller class to process data and manage DB entries.
 */
public class ManageBookingsEmpController extends ManageBookingsAdminController implements Initializable{


    /* Override initialize(URL, ResourceBundle) from Initializable.
     * Create listener for upcoming booking tableview using createBookingSelectionListener() method.
     * Reallocate the model used to the Employee model class. This way only employee functions can be accessed.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        // Code for listener was developed from Stack Exchange URL - "https://stackoverflow.com/questions/41323945/javafx-combobox-add-listener-for-selected-item-value"
        // Accessed 01/06/2021 - Conor Christensen
        model = new ManageBookingsEmpModel();
        empID = ProfileHolder.getInstance().getProfile().getEmployeeID();
        createBookingSelectionListener();
    }

    //Override from parent class. Different UI controls.
    private void createBookingSelectionListener() {
        upcoming_booking_table.getSelectionModel().selectedItemProperty().addListener((n, oldbooking, newbooking) -> {
            selected_booking = newbooking;
            try {
                change_date = selected_booking.getDate();
                current_desk_id = selected_booking.getDeskID();
            } catch (NullPointerException e) {
                change_date = null;
                current_desk_id = -1;
            }
            if (newbooking == null) {
                edit_booking_button.setDisable(true);
                cancel_booking_button.setDisable(true);
            } else {
                if (newbooking.getConfirmed()) {
                    edit_booking_button.setDisable(true);
                    cancel_booking_button.setDisable(false);
                } else {
                    edit_booking_button.setDisable(false);
                    cancel_booking_button.setDisable(false);
                }
            }
        });
    }

    //Override from parent class. Return to Employee Menu, not admin.
    public void backToManageBookings(Event event){
        sw.switchScene(event, "../ui/ManageBookingsEmp.fxml");
    }

    //Override from parent class. Different columns in the child table require handling.
    public void setColumnValues(ObservableList<Booking> bookings){
        upc_date_column.setCellValueFactory(new PropertyValueFactory<>("date_str"));
        upc_desk_number_column.setCellValueFactory(new PropertyValueFactory<>("deskID_str"));
        upc_confirmed_column.setCellValueFactory(new PropertyValueFactory<>("confirmed_str"));
        upcoming_booking_table.setItems(bookings);
        upcoming_booking_table.getSortOrder().add(upc_date_column);
    }

    //Override from parent class. Return to Employee Menu, not admin.
    public void backToMainMenu(Event event){
        sw.switchScene(event, "../ui/MainMenuEmp.fxml");
    }
}

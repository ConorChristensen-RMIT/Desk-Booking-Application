package main.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import main.model.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

/* Controller class for ManageBookingsAdmin.fxml
 * Uses Singleton Facades with SceneSwitcher and PaneManager.
 * Class controls UI for switching scenes depending on Main Menu interaction.
 * Uses controller class to process data and manage DB entries.
 */
public class ManageBookingsAdminController implements Initializable {
    public ManageBookingsAdminModel model = new ManageBookingsAdminModel();
    SceneSwitcher sw = SceneSwitcher.getInstance();
    PaneManager pc = PaneManager.getInstance();
    LocalDate change_date;
    int new_desk_ID;
    int current_desk_id;
    boolean[] desk_availability_for_date;
    BookATableModel btm = new BookATableModel();
    Booking selected_booking;
    String current_search_selection;
    int empID;
    String current_level_selection;
    ObservableList<Booking> upcoming_bookings;

    @FXML TableView<Booking> upcoming_booking_table;
    @FXML TableView<Booking> previous_booking_table;
    @FXML TableColumn<Booking, String> upc_date_column;
    @FXML TableColumn<Booking, String> upc_desk_number_column;
    @FXML TableColumn<Booking, String> upc_confirmed_column;
    @FXML TableColumn<Booking, String> prev_date_column;
    @FXML TableColumn<Booking, String> prev_desk_number_column;
    @FXML Button back_button;
    @FXML Button back_button1;
    @FXML Button back_to_main;
    @FXML Button edit_booking_button;
    @FXML Button cancel_booking_button;
    @FXML Rectangle desk_0;
    @FXML Rectangle desk_1;
    @FXML Rectangle desk_2;
    @FXML Rectangle desk_3;
    @FXML Rectangle desk_4;
    @FXML Rectangle desk_5;
    @FXML Rectangle desk_6;
    @FXML Rectangle desk_7;
    @FXML Rectangle desk_8;
    @FXML Rectangle desk_9;
    @FXML Rectangle desk_10;
    @FXML Rectangle desk_11;
    @FXML Rectangle desk_L0;
    @FXML Rectangle desk_L1;
    @FXML Rectangle desk_L2;
    @FXML Rectangle desk_L3;
    @FXML Rectangle desk_L4;
    @FXML Rectangle desk_L5;
    @FXML Rectangle desk_L6;
    @FXML Rectangle desk_L7;
    @FXML Rectangle desk_L8;
    @FXML Rectangle desk_L9;
    @FXML Rectangle desk_L10;
    @FXML Rectangle desk_L11;
    @FXML Label lockdown_desk_selection_warning;
    @FXML Label desk_selection_warning;
    @FXML Label confirm_table_number_label;
    @FXML Label confirm_date_label;
    @FXML Label date_selection_warning;
    @FXML Button confirm_change_button;
    @FXML Label booking_edit_warning_label;
    @FXML Group edit_booking_group;
    @FXML DatePicker new_date_selected;
    @FXML Label new_desk_ID_label;
    @FXML Label new_date_label;
    @FXML Button see_floorplan_button;
    @FXML DatePicker search_date_picker;
    @FXML TextField search_string;
    @FXML ChoiceBox<String> search_options_choicebox;
    @FXML Button go_search_button;
    @FXML TableColumn<Booking, String> upc_emp_ID_column;
    @FXML CheckBox confirmed_booking_search_checkbox;
    @FXML Label search_warning_label;
    @FXML Button confirm_booking_button;
    @FXML DatePicker desk_lockdown_datepicker;
    @FXML Button desk_lockdown_floorplan_button;
    @FXML ChoiceBox<String> lockdown_level_choicebox;
    @FXML Button engage_level_button;
    @FXML DatePicker lockdown_start_datepicker;
    @FXML DatePicker lockdown_end_datepicker;
    @FXML Label lockdown_protocol_warning;
    @FXML ProgressIndicator engage_lockdown_indicator;
    @FXML Label lockdown_see_floorplan_warning;

    /* Override initialize(URL, ResourceBundle) from Initializable.
     * Populate search parameters choicebox by calling populatesearchParams() method.
     * Populate lockdown leves choicebox by calling populateLockdownLevels() method.
     * Create listener for upcoming booking tableview using createBookingSelectionListener() method.
     * create listener for both choiceboxes using createSearchOptionsChoiceboxListener() and
     * createLockdownChoicebocListener() methods.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateSearchParams();
        populateLockdownLevels();
        // Code for listeners was developed from Stack Exchange URL - "https://stackoverflow.com/questions/41323945/javafx-combobox-add-listener-for-selected-item-value"
        // Accessed 01/06/2021 - Conor Christensen
        createBookingSelectionListener();
        createSearchOptionsChoiceboxListener();
        createLockdownChoiceboxListener();
    }

    /* createBookingsSelectionListener() - internally called private method.
     * Creates listener for selected booking from tableview and sets UI controls and selected_booking variable accordingly.
     */
    private void createBookingSelectionListener(){
        upcoming_booking_table.getSelectionModel().selectedItemProperty().addListener((n, oldbooking, newbooking) -> {
            selected_booking = newbooking;
            try {
                empID = selected_booking.getEmployee().getEmployeeID();
                change_date = selected_booking.getDate();
                current_desk_id = selected_booking.getDeskID();
            } catch (NullPointerException e) {
                change_date = null;
                current_desk_id = -1;
            }
            if (newbooking != oldbooking) {
                edit_booking_group.setVisible(false);
                restoreEditBooking();
            }
            if (newbooking == null) {
                edit_booking_group.setVisible(false);
                restoreEditBooking();
                edit_booking_button.setDisable(true);
                cancel_booking_button.setDisable(true);
            } else {
                if (newbooking.getConfirmed()) {
                    confirm_booking_button.setDisable(true);
                    edit_booking_button.setDisable(true);
                    cancel_booking_button.setDisable(false);
                } else {
                    confirm_booking_button.setDisable(false);
                    edit_booking_button.setDisable(false);
                    cancel_booking_button.setDisable(false);
                }
            }
        });
    }

    /* createSearchOptionsChoiceboxListener() - internally called private method.
     * Creates listener for selected option from choicebox and sets UI controls and selection variable accordingly.
     */
    private void createSearchOptionsChoiceboxListener(){
        search_options_choicebox.getSelectionModel().selectedItemProperty().addListener((m, old_choice, new_choice) -> {
            try{
                current_search_selection = new_choice;
            }
            catch (NullPointerException e){
                current_search_selection = "";
            }
            if (new_choice != null){
                go_search_button.setDisable(false);
                search_string.setDisable(false);
                if (new_choice != old_choice) {
                    if (new_choice.equals("confirmed")){
                        search_string.setVisible(false);
                        search_string.setText("");
                        search_date_picker.setVisible(false);
                        search_date_picker.setValue(null);
                        confirmed_booking_search_checkbox.setVisible(true);
                    }
                    else if (new_choice.equals("date")){
                        search_string.setVisible(false);
                        search_string.setText("");
                        search_date_picker.setVisible(true);
                        confirmed_booking_search_checkbox.setVisible(false);
                        confirmed_booking_search_checkbox.setSelected(false);
                    }
                    else {
                        search_string.setVisible(true);
                        search_date_picker.setVisible(false);
                        search_date_picker.setValue(null);
                        confirmed_booking_search_checkbox.setVisible(false);
                        confirmed_booking_search_checkbox.setSelected(false);
                    }
                }
            }
        });
    }

    /* createLockdownChoiceboxListener() - internally called private method.
     * Creates listener for selected option from choicebox and sets UI controls and selection variable accordingly.
     */
    private void createLockdownChoiceboxListener(){
        lockdown_level_choicebox.getSelectionModel().selectedItemProperty().addListener((m, old_choice, new_choice) -> {
            try{
                current_level_selection = new_choice;
            }
            catch (NullPointerException e){
                current_level_selection = "";
            }
            if (new_choice != null){
                engage_level_button.setDisable(false);
            }
        });
    }

    public void backToManageBookings(Event event){
        sw.switchScene(event, "../ui/ManageBookingsAdmin.fxml");
    }

    /* goSearch() - called by search button in UI
     * Takes user inputs of search parameters for upcoming bookings and handles the inputs. Calls Model function to
     * find matching bookings. Sets warning labels if necessary. Uses selected option checks to call model class with
     * method arguments.
     */
    public void goSearch(){
        search_warning_label.setText("");
        Object search_obj = null;
        if ((search_string.getText().equals("")) & ((!current_search_selection.equals("date"))&(!current_search_selection.equals("confirmed")))){
            search_warning_label.setText("Please fill the \nsearch box");
        }
        else if ((current_search_selection.equals("date"))&(search_date_picker.getValue()==null)){
            search_warning_label.setText("Please select a date");
        }
        else if (search_options_choicebox.getValue() == null){
            search_warning_label.setText("Please choose a \nvaiable to search for");
        }
        else{
            ArrayList<String> options = model.getBookingSearchParams();
            ArrayList<String> option_types = model.getBookingSearchParamTypes();
            String selection_type_str = option_types.get(options.indexOf(search_options_choicebox.getValue()));
            try {
                if (search_options_choicebox.getValue().equals("confirmed")){
                    search_obj = (boolean) confirmed_booking_search_checkbox.isSelected();
                }
                else if (search_options_choicebox.getValue().equals("date")){
                    search_obj = (String) search_date_picker.getValue().toString();
                }
                else if (selection_type_str.equals("INTEGER")) {
                    search_obj = (Integer) Integer.valueOf(search_string.getText());
                } else {
                    search_obj = (String) search_string.getText();
                }
                populateUpcomingBookings(search_obj, search_options_choicebox.getValue());
            }
            catch (NumberFormatException e){
                search_warning_label.setText("Invalid: ID field must \nbe numeric only");
            }
        }
    }

    /* populateSearchParams() - internally called private method
     * Populates the search parameter choicebox by retrieving data from model class
     */
    private void populateSearchParams(){
        ObservableList<String> params_OL =  FXCollections.observableArrayList(model.getBookingSearchParams());
        search_options_choicebox.setItems(params_OL);
    }

    /* clearSearch(Event event) - Handles button press event
     * Clears all search parameters and re-populates the table with upcoming bookings with populateUpcomingBookings() method.
     */
    public void clearSearch(Event event){
        search_string.setText("");
        search_string.setDisable(true);
        search_string.setVisible(true);
        search_date_picker.setVisible(false);
        search_date_picker.setValue(null);
        confirmed_booking_search_checkbox.setVisible(false);
        confirmed_booking_search_checkbox.setSelected(false);
        go_search_button.setDisable(true);
        search_date_picker.setValue(null);
        search_date_picker.setVisible(false);
        confirmed_booking_search_checkbox.setVisible(false);
        confirmed_booking_search_checkbox.setSelected(false);
        search_options_choicebox.setValue(null);
        populateUpcomingBookings();
    }

    /* populateUpcomingBookings(Object object, String search_param) - internally called OVERLOADED method
     * populates upcoming bookings by sourcing bookings from the model class that fit the search parameters
     */
    private void populateUpcomingBookings(Object object, String search_param){
        upcoming_bookings = FXCollections.observableArrayList(model.getUpcomingBookings(object, search_param));
        setColumnValues(upcoming_bookings);
    }

    /* populateUpcomingBookings() - internally called OVERLOADED method
     * populates upcoming bookings by sourcing all upcoming bookings from the model class.
     */
    public void populateUpcomingBookings(){
        upcoming_bookings = FXCollections.observableArrayList(model.getUpcomingBookings());
        setColumnValues(upcoming_bookings);
    }

    /* setColumnValues(ObservableList) - internally called private method.
     * Used to set column values with setCellValueFactory() method.
     */
    public void setColumnValues(ObservableList<Booking> bookings){
        upc_date_column.setCellValueFactory(new PropertyValueFactory<>("date_str"));
        upc_desk_number_column.setCellValueFactory(new PropertyValueFactory<>("deskID_str"));
        upc_confirmed_column.setCellValueFactory(new PropertyValueFactory<>("confirmed_str"));
        upc_emp_ID_column.setCellValueFactory(new PropertyValueFactory<>("employeeID_str"));
        upcoming_booking_table.setItems(bookings);
        upcoming_booking_table.getSortOrder().add(upc_date_column);
    }

    /* refreshUpcomingBookings() - internally called private method
     * Refreshes the column values in the Tableview.
     */
    private void refreshUpcomingBookings(){
        upcoming_booking_table.refresh();
    }

    public void confirmBooking(Event event){
        model.confirmBooking(selected_booking);
        refreshUpcomingBookings();
    }

    public void goToUpcomingBookings(MouseEvent click){
        pc.goForward(click, 1);
        upcoming_booking_table.setEditable(false);
        populateUpcomingBookings();
    }

    public void goToPreviousBookings(MouseEvent click){
        pc.goForward(click, 2);
        previous_booking_table.setEditable(false);
        populatePreviousBookings();
    }


    public void populatePreviousBookings() {
        ObservableList<Booking> bookings = FXCollections.observableArrayList();         //Should do this herre to avoid any FXML work in model class
        ArrayList<Booking> previous_booking_arr = model.getPreviousBookings();          //Might be a nicer way of doing this?
        Iterator<Booking> iter = previous_booking_arr.iterator();                       //I think I want to figure out fx collections
        while (iter.hasNext()) {
            bookings.add(iter.next());
        }
        prev_date_column.setCellValueFactory(new PropertyValueFactory<Booking, String>("date_str"));
        prev_desk_number_column.setCellValueFactory(new PropertyValueFactory<Booking, String>("deskID_str"));
        previous_booking_table.setItems(bookings);
    }

    /* backToMainMenu(ActionEvent) Handles 'back' button event and uses SceneSwitcher to return to the main menu.
     */
    public void backToMainMenu(Event event){
        sw.switchScene(event, "../ui/MainMenuAdmin.fxml");
    }

    public void cancelBooking(Event event){
        pc.overlay(event, 4);
    }

    public void confirmDeleteBooking(Event event){
        selected_booking.deleteBooking();
        upcoming_bookings.remove(selected_booking);
        upcoming_booking_table.refresh();
        pc.exitOverlay(event, 4);
        restoreEditBooking();
    }


    public void cancelDeleteBooking(Event event){
        pc.exitOverlay(event, 4);
        restoreEditBooking();
    }

    public void restoreEditBooking(){
        new_date_label.setText("");
        new_desk_ID_label.setText("");
        new_date_selected.setValue(null);
        edit_booking_group.setVisible(false);
        confirm_change_button.setDisable(true);
    }

    public void editBooking(Event event){
        new_desk_ID_label.setText("");
        new_date_label.setText("");
        edit_booking_group.setVisible(true);
        new_desk_ID = current_desk_id;
    }

    public void setDeskColours(){
        colourDesks(change_date);
    }

    public void setDeskColours(LocalDate date){
        colourDesks(date);
    }

    public void colourDesks(LocalDate date){
        desk_availability_for_date = btm.getDeskAvailability(date);
        String[] colours = new String[12];
        for (int i=0 ; i < desk_availability_for_date.length ; i++){
            if (i == current_desk_id) {colours[i] = "Yellow";}
            else if (desk_availability_for_date[i]) {colours[i] = "Green";}
            else {colours[i] = "Red";}
        }
        desk_0.setFill(Paint.valueOf(colours[0]));
        desk_1.setFill(Paint.valueOf(colours[1]));
        desk_2.setFill(Paint.valueOf(colours[2]));
        desk_3.setFill(Paint.valueOf(colours[3]));
        desk_4.setFill(Paint.valueOf(colours[4]));
        desk_5.setFill(Paint.valueOf(colours[5]));
        desk_6.setFill(Paint.valueOf(colours[6]));
        desk_7.setFill(Paint.valueOf(colours[7]));
        desk_8.setFill(Paint.valueOf(colours[8]));
        desk_9.setFill(Paint.valueOf(colours[9]));
        desk_10.setFill(Paint.valueOf(colours[10]));
        desk_11.setFill(Paint.valueOf(colours[11]));
    }

    public void tableSelected(MouseEvent click){
        Node desk_selected = click.getPickResult().getIntersectedNode();
        String fxml_id = desk_selected.getId();
        int desk_ID = Integer.valueOf(fxml_id.split("_")[1]);
        if (!desk_availability_for_date[desk_ID]){
            desk_selection_warning.setText("This desk is unavailable! Try again.");
        }
        else if (desk_ID == current_desk_id){
            desk_selection_warning.setText("This is the desk for your current booking!");
        }
        else {
            pc.overlay(click);
            confirm_table_number_label.setText("Table Number: " + Integer.valueOf(desk_ID));
            confirm_date_label.setText("Date: " + change_date);
            new_desk_ID = desk_ID;
        }
    }

    public void seeFloorplan(Event event){
        booking_edit_warning_label.setText("");
        Node button = (Node) event.getSource();
        Parent button_pane = button.getParent();
        if (new_date_selected.getValue()!=null) { setDeskColours(new_date_selected.getValue()); }
        else {setDeskColours();}
        pc.goForward(button_pane.getParent(), 2);
    }

    public void backToUpcomingBookings(Event event){
        pc.goBack(event, 2);
    }

    public void bookingConfirmResponse(ActionEvent event){
        Node selection = (Node) event.getSource();
        if (!selection.getId().equals("confirm_yes_button")){
            new_desk_ID = selected_booking.getDeskID();
        }
        else{
            confirm_change_button.setDisable(false);
            new_desk_ID_label.setText("New desk ID selected: " + Integer.toString(new_desk_ID));
        }
        pc.exitOverlay(event);
        pc.goBack(pc.goBack(event, 1), 2);
    }

    public void selectingDate(ActionEvent event){
        see_floorplan_button.setDisable(true);
        booking_edit_warning_label.setText("");
        new_date_label.setText("");
        while (new_date_selected.getValue() == null){}
        if (btm.checkIfEmpAlreadyBookedForDate(new_date_selected.getValue(), empID) & (!new_date_selected.getValue().isEqual(selected_booking.getDate()))){
            booking_edit_warning_label.setText("You have a different booking \nfor that date already");
        }
        else if (new_date_selected.getValue().isBefore(LocalDate.now())){
            booking_edit_warning_label.setText("Invalid date:\nIs in the past.");
        }
        else{
            confirm_change_button.setDisable(false);
            see_floorplan_button.setDisable(false);
            new_date_label.setText("New date: "+new_date_selected.getValue().toString());
        }
    }

    public void confirmChange(Event event){
        if (new_date_selected.getValue() != null) {
            change_date = new_date_selected.getValue();
            if(btm.checkIfEmpAlreadyBookedForDate(change_date) & (new_desk_ID == current_desk_id)){
                booking_edit_warning_label.setText("You are already booked \nfor that desk on \nthat day!");
            }
            else if (!btm.getDeskAvailability(change_date)[new_desk_ID]){       //If the desk isn't available for the selected date
                booking_edit_warning_label.setText("You must select an available \ndesk for the chosen date");
            }
            else{
                model.changeBooking(new_desk_ID, change_date, selected_booking);
                refreshUpcomingBookings();
                edit_booking_group.setVisible(false);
            }
        }
        else{
            model.changeBooking(new_desk_ID, change_date, selected_booking);
            refreshUpcomingBookings();
            edit_booking_group.setVisible(false);
        }
    }

    public void goToCovidLockdown(MouseEvent event){
        pc.goForward(event, 6);
    }

    private void populateLockdownLevels(){
        ObservableList<String> levels =  FXCollections.observableArrayList(model.getLockdownLevelStrings());
        lockdown_level_choicebox.setItems(levels);
    }

    public void engageLockdownLevel(Event event){
        lockdown_protocol_warning.setText("");
        if ((lockdown_start_datepicker.getValue() != null) & (lockdown_end_datepicker.getValue() != null)){
            String level = lockdown_level_choicebox.getValue();
            LocalDate start = lockdown_start_datepicker.getValue();
            LocalDate end = lockdown_end_datepicker.getValue();
            engage_lockdown_indicator.setVisible(true);
            engage_level_button.setDisable(true);
            model.engageLockdown(level, start, end);
            engage_lockdown_indicator.setVisible(false);
            engage_level_button.setDisable(false);

        }
        else{
            lockdown_protocol_warning.setText("You must specify a date window for the lockdown protocol.");
        }
    }

    public void backToCovidLockdown(Event event){
        pc.goBack(event, 1);
    }

    public void seeLockdownFloorplan(Event event){ ;
        lockdown_see_floorplan_warning.setText("");
        if (desk_lockdown_datepicker.getValue() == null){
            lockdown_see_floorplan_warning.setText("You must select a date!");
        }
        else if (desk_lockdown_datepicker.getValue().isBefore(LocalDate.now())){
            lockdown_see_floorplan_warning.setText("You cannot select a date \nin the past");
        }
        else {
            colourLockdownDesks(desk_lockdown_datepicker.getValue());
            pc.goForward(event, 1);
        }
    }

    /* colourLockdownDesks(LocalDate date)
     * Colours the FXML shapes to reflect the state of the desk. Green = available, red = booked, orange = locked down.
     */
    public void colourLockdownDesks(LocalDate date){
        lockdown_desk_selection_warning.setText("");
        desk_availability_for_date = btm.getDeskAvailability(date);
        String[] colours = new String[12];
        ArrayList<Integer> locked_deskIDs = btm.getLockedDeskIDs(date);
        for (int i=0 ; i < desk_availability_for_date.length ; i++){
            if (locked_deskIDs.contains(i)) {colours[i] = "Orange";}
            else if (desk_availability_for_date[i]) {colours[i] = "Green";}
            else {colours[i] = "Red";}
        }
        desk_L0.setFill(Paint.valueOf(colours[0]));
        desk_L1.setFill(Paint.valueOf(colours[1]));
        desk_L2.setFill(Paint.valueOf(colours[2]));
        desk_L3.setFill(Paint.valueOf(colours[3]));
        desk_L4.setFill(Paint.valueOf(colours[4]));
        desk_L5.setFill(Paint.valueOf(colours[5]));
        desk_L6.setFill(Paint.valueOf(colours[6]));
        desk_L7.setFill(Paint.valueOf(colours[7]));
        desk_L8.setFill(Paint.valueOf(colours[8]));
        desk_L9.setFill(Paint.valueOf(colours[9]));
        desk_L10.setFill(Paint.valueOf(colours[10]));
        desk_L11.setFill(Paint.valueOf(colours[11]));
    }

    /* tableLockedDown(MouseEvent event)
     * Handles the FXML rectabngle table selection, and toggles the lockdown on that table.
     * Re-colours all the desks accordingly.
     * Toggle action used to lock and unlock tables.
     */
    public void tableLockedDown(MouseEvent event){
        LocalDate picked_date = desk_lockdown_datepicker.getValue();
        lockdown_desk_selection_warning.setText("");
        Node desk_selected = event.getPickResult().getIntersectedNode();
        String fxml_id = desk_selected.getId();
        int desk_ID = Integer.valueOf(fxml_id.split("_L")[1]);
        if (model.checkIfDeskLockedDown(desk_ID, picked_date))
            model.unlockDesk(desk_ID, picked_date);
        else {
            model.lockdownDesk(desk_ID, picked_date);
        }
        colourLockdownDesks(picked_date);
    }
}

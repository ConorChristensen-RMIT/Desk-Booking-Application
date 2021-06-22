package main.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import main.model.*;
import java.time.LocalDate;

/* Controller class for BookATable.fxml
 * Uses Singleton Facades with SceneSwitcher and PaneManager to help control UI.
 * Class controls UI for employee table booking process.
 * Uses model class BookATableModel() to handle data processing and db communications.
 */
public class BookATableController {
    SceneSwitcher sw = SceneSwitcher.getInstance();
    PaneManager pc = PaneManager.getInstance();
    LocalDate date;
    int desk_ID;
    BookATableModel model = new BookATableModel();
    boolean[] desk_availability_for_date;
    
    @FXML private DatePicker date_selected;
    @FXML Label date_selection_warning;
    @FXML Label desk_selection_warning;
    @FXML Label confirm_table_number_label;
    @FXML Label confirm_date_label;
    @FXML Button confirm_cancel_button;
    @FXML Button confirm_yes_button;
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

    /* selectingDate() called when datepicker selected (event). Resets warnings as they may not apply to new selection.
     */
    public void selectingDate(ActionEvent event){
        date_selection_warning.setText("");
    }

    /* selectDeskButton(Event) called when desk selected from floorplan.
     * Resets and delivers necessary selection warnings as they apply to new selection.
     * Displays Overlay for booking confirmation.
     */
    public void selectDeskButton(ActionEvent event){
        date = date_selected.getValue();
        if (date == null) {date_selection_warning.setText("Please select a date!"); }
        else if (date.isBefore(LocalDate.now())) {date_selection_warning.setText("Cannot select a date in the past! Try again.");}
        else if (date.isEqual(LocalDate.now())) {date_selection_warning.setText("Cannot select today's date! Try again.");}
        else if (model.checkIfEmpAlreadyBookedForDate(date)) {date_selection_warning.setText("You have already booked for this date.\n" +
                "To manage future bookings, use 'Manage Bookings'\nfrom the main menu.");}
        else {
            setDeskColours();
            pc.goForward(event, 1);
        }

    }

    /* setDeskColours() an internally called private method.
     * Sets the colours of the desks on floorplan. Unavailable desks are red despite reasoning (lockdown or booked) on
     * purpose.
     */
    private void setDeskColours(){
        desk_availability_for_date = model.getDeskAvailability(date);
        String[] colours = new String[12];
        for (int i=0 ; i < desk_availability_for_date.length ; i++){
            if (desk_availability_for_date[i]) {colours[i] = "Green";}
            else {colours[i] = "Red";}
        }
        desk_0.setFill(Paint.valueOf(colours[0]));  //This is disgusting. Help! haha
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

    /* tableSelected(MouseEvent) - event handler for selecting a table.
     * Retrieves selected node. Handles selection warnings. Will proceed to booking routine with overlay if desk
     * available.
     */
    public void tableSelected(MouseEvent click){
        Node desk = click.getPickResult().getIntersectedNode();
        String fxml_id = desk.getId();
        desk_ID = Integer.valueOf(fxml_id.split("_")[1]);
        if (!desk_availability_for_date[desk_ID]){
            desk_selection_warning.setText("This desk is unavailable! Try again.");
        }
        else {
            pc.overlay(click);
            confirm_table_number_label.setText("Table Number: " + Integer.valueOf(desk_ID));
            confirm_date_label.setText("Date: " + date);
        }
    }

    /* backToMainMenu(ActionEvent) Handles 'back' button event and uses SceneSwitcher to return to the main menu.
     */
    public void backToMain(ActionEvent event){
        sw.switchScene(event, "../ui/MainMenuEmp.fxml");
    }

    /* backToSelectedDate(ActionEvent) Handles 'back' button event and uses PaneManager to return to date selection pane.
     */
    public void backToSelectDate(ActionEvent event) {
        pc.goBack(event, 1);
    }

    /* bookingConfirmResponse(ActionEvent event) handles button event.
     * Uses model to make booking if confirmed. Always returns the UI to Main Menu.
     */
    public void bookingConfirmResponse(ActionEvent event){
        Node selection = (Node) event.getSource();
        if (selection.getId().equals("confirm_yes_button")){
            model.bookTable(desk_ID, date);
        }
        backToMain(event);
    }



}

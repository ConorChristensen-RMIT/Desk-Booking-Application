package main.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.model.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/* Controller class for MainMenuEmp.fxml
 * Uses Singleton Facade with SceneSwitcher.
 * Class controls UI for switching scenes depending on Main Menu interaction.
 */
public class MainMenuEmpController implements Initializable{
    SceneSwitcher sw = SceneSwitcher.getInstance();
    ProfileHolder PH = ProfileHolder.getInstance();

    @FXML
    private Label welcome_message;

    /* Override Initializable initialize(URL, ResourceBundle)
     * sets welcome text to user's name. This show's correct user has logged in
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        welcome_message.setText("Welcome: " + PH.getProfile().getFirst_name() + " " + PH.getProfile().getLast_name());
    }

    /* manageBookings(Mousevent event) handles mouse event to go to manage bookings menus.
     * Changes scene. UI only
     */
    public void manageBookings(MouseEvent mouse_release){
        sw.switchScene(mouse_release, "../ui/ManageBookingsEmp.fxml");
    }

    /* bookTable(Mousevent event) handles mouse event to go to Book A Table menus.
     * Changes scene. UI only
     */
    public void bookTable(MouseEvent mouse_release){
        sw.switchScene(mouse_release, "../ui/BookATable.fxml");
    }

    /* logout(Event event) handles button event to go back to the Landing page and logout
     * Changes stage by opening new and closing current.
     */
    public void logout(Event event){
        Stage landing_page_stage = new Stage();
        Parent root;
        FXMLLoader loader;
        try{
            loader = new FXMLLoader(getClass().getResource("../ui/LandingPage.fxml"));
            root = loader.load();
            Scene scene = new Scene(root);
            landing_page_stage.setScene(scene);
            landing_page_stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }


}

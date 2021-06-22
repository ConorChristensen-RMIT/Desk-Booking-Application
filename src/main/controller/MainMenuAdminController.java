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
import main.model.ProfileHolder;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/* Controller class for MainMenuAdmin.fxml
 * Uses Singleton Facade with SceneSwitcher.
 * Class controls UI for switching scenes depending on Main Menu interaction.
 */
public class MainMenuAdminController implements Initializable{

    @FXML Label welcome_message;
    SceneSwitcher sw = SceneSwitcher.getInstance();
    ProfileHolder PH = ProfileHolder.getInstance();

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
    public void manageBookings(MouseEvent event){
        sw.switchScene(event, "../ui/ManageBookingsAdmin.fxml");
    }

    /* manageAccounts(Mousevent event) handles mouse event to go to manage accounts menus.
     * Changes scene. UI only
     */
    public void manageAccounts(MouseEvent event){
        sw.switchScene(event, "../ui/ManageAccounts.fxml");

    }

    /* generateReports(Mousevent event) handles mouse event to go to generate reports menus.
     * Changes scene. UI only
     */
    public void generateReports(MouseEvent event){
        sw.switchScene(event, "../ui/GenerateReports.fxml");
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

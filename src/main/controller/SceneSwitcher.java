package main.controller;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/* SceneSwitcher singleton facade class.
 * Class is singleton because it is repeatedly called, and only one instance is required.
 * Manages current scene open within stage. Class is a facade because most controllers use it to control UI behaviour.
 */
public class SceneSwitcher {

    FXMLLoader loader;
    Parent root;

    private final static SceneSwitcher INSTANCE = new SceneSwitcher();

    private SceneSwitcher () {}

    public static SceneSwitcher getInstance() {return INSTANCE;}

    public void switchScene(Event event, String fxml_page){
        try{
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            loader = new FXMLLoader(getClass().getResource(fxml_page));
            root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

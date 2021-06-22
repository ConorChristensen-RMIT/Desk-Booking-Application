package main.controller;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import java.util.List;

/* PaneManager singleton facade class.
 * Class is singleton because it is repeatedly called, and only one instance is required.
 * Manages stack pane visibility in UI. Class is a facade because most controllers use it to control UI behaviour.
 */
public class PaneManager {

    private final static PaneManager INSTANCE = new PaneManager();

    private PaneManager() {}

    public static PaneManager getInstance() {return INSTANCE;}

    public void goForward(Node current_pane, int number_of_panes){
        Parent stack_pane = current_pane.getParent();
        List<Node> panes = stack_pane.getChildrenUnmodifiable();
        int current_pane_index = panes.indexOf(current_pane);
        panes.get(current_pane_index + number_of_panes).setVisible(true);
        current_pane.setVisible(false);
    }

    public void goForward(Event event, int number_of_panes){
        Node node = (Node) event.getSource();
        Parent current_pane = node.getParent();
        Parent stack_pane = current_pane.getParent();
        List<Node> panes = stack_pane.getChildrenUnmodifiable();
        int current_pane_index = panes.indexOf(current_pane);
        panes.get(current_pane_index + number_of_panes).setVisible(true);
        current_pane.setVisible(false);
    }

    public void goForward(MouseEvent click, int number_of_panes){
        Node node = click.getPickResult().getIntersectedNode();
        Parent current_pane = node.getParent();
        Parent stack_pane = current_pane.getParent();
        List<Node> panes = stack_pane.getChildrenUnmodifiable();
        int current_pane_index = panes.indexOf(current_pane);
        panes.get(current_pane_index + number_of_panes).setVisible(true);
        current_pane.setVisible(false);
    }

    public void goBack(Node current_pane, int number_of_panes){
        Parent stack_pane = current_pane.getParent();
        List<Node> panes = stack_pane.getChildrenUnmodifiable();
        int current_pane_index = panes.indexOf(current_pane);
        panes.get(current_pane_index - number_of_panes).setVisible(true);
        current_pane.setVisible(false);
    }

    public Node goBack(Event event, int number_of_panes){
        Node node = (Node) event.getSource();
        Parent current_pane = node.getParent();
        Parent stack_pane = current_pane.getParent();
        List<Node> panes = stack_pane.getChildrenUnmodifiable();
        int current_pane_index = panes.indexOf(current_pane);
        Node next_pane = panes.get(current_pane_index - number_of_panes);
        next_pane.setVisible(true);
        current_pane.setVisible(false);
        return next_pane;
    }

    public void overlay(MouseEvent click){
        Node node = click.getPickResult().getIntersectedNode();
        Parent current_pane = node.getParent();
        Parent stack_pane = current_pane.getParent();
        List<Node> panes = stack_pane.getChildrenUnmodifiable();
        int current_pane_index = panes.indexOf(current_pane);
        panes.get(current_pane_index + 1).setVisible(true);
        current_pane.setDisable(true);
    }

    public void overlay(Event event, int num_of_panes){
        Node node = (Node) event.getSource();
        Parent current_pane = node.getParent();
        Parent stack_pane = current_pane.getParent();
        List<Node> panes = stack_pane.getChildrenUnmodifiable();
        int current_pane_index = panes.indexOf(current_pane);
        panes.get(current_pane_index + num_of_panes).setVisible(true);
        current_pane.setDisable(true);
    }



    public void exitOverlay(Event event){
        Node node = (Node) event.getSource();
        Parent current_pane = node.getParent();
        Parent stack_pane = current_pane.getParent();
        List<Node> panes = stack_pane.getChildrenUnmodifiable();
        int current_pane_index = panes.indexOf(current_pane);
        panes.get(current_pane_index - 1).setDisable(false);
        current_pane.setVisible(false);
    }

    public void exitOverlay(Event event, int num_of_panes){
        Node node = (Node) event.getSource();
        Parent current_pane = node.getParent();
        Parent stack_pane = current_pane.getParent();
        List<Node> panes = stack_pane.getChildrenUnmodifiable();
        int current_pane_index = panes.indexOf(current_pane);
        panes.get(current_pane_index - num_of_panes).setDisable(false);
        current_pane.setVisible(false);
    }
}

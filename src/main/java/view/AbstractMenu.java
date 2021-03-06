package view;

import controller.GameController;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import java.io.File;
import javafx.scene.paint.Color;

/**
 * Created by RuYiMarone on 11/12/2016.
 */
public class AbstractMenu {
    public static final int PREFWIDTH = 140;
    private Button exploreButton = new Button("Explore");
    private Button endTurnButton = new Button("End Turn");
    private Text terrain = new Text();
    private Text unitStatus = new Text();
    private VBox menu = new VBox(10, terrain, unitStatus,
            exploreButton, endTurnButton);

    public AbstractMenu() {
        terrain.setFill(Color.WHITE);
        unitStatus.setFill(Color.WHITE);

        menu.setPrefWidth(PREFWIDTH);
        unitStatus.setWrappingWidth(120);

        exploreButton.setOnMousePressed(e -> {
                GameController.getCivilization().explore();
                playSFX("AbstractMenu_explore");
                if (endTurn()) {
                    Alert newAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    newAlert.setHeaderText("Congratulations");
                    newAlert.setTitle("You Won!");
                    newAlert.showAndWait();
                    System.exit(0);
                }
            });

        endTurnButton.setOnAction(event -> {
                playSFX("AbstractMenu_end");
                if (endTurn()) {
                    Alert newAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    newAlert.setHeaderText("Congratulations");
                    newAlert.setTitle("You Won!");
                    newAlert.showAndWait();
                    System.exit(0);
                }
            });
        menu.setPrefWidth(PREFWIDTH);
        updateItems();
    }
    /**
     * This method updates the items and return the vbox as
     * the menu
     */
    public VBox getRootNode() {
        menu.getStylesheets().add(AbstractMenu.class.
            getResource("style3.css").toExternalForm());
        updateItems();
        return menu;
    }
    /**
     * This method takes in a node and added the node as
     * a child of the vbox menu
     */
    public void addMenuItem(Node node) {
        menu.getChildren().add(node);
    }
    /**
     * ends the player's turn and check for winning condition
     */
    public boolean endTurn() {
        GameController.setLastClicked(null);
        GameController.tick();
        GameController.ai();
        GridFX.update();
        GameController.updateResourcesBar();
        if (GameController.getCivilization().getNumSettlements() <= 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Your last settlement has been destroyed!");
            alert.setTitle("Game Over");
            alert.showAndWait();
            System.exit(0);
        }
        return GameController.getCivilization()
                .getStrategy().conqueredTheWorld()
                || GameController.getCivilization()
                .getTechnology().hasTechnologyWin();
    }

    private void updateItems() {
        unitStatus.setText("");
        if (GameController.getLastClicked() != null) {
            terrain.setText(GameController.getLastClicked()
                    .getTile().getType().toString());
            if (!GameController.getLastClicked().getTile().isEmpty()) {
                unitStatus.setText(GameController.getLastClicked()
                        .getTile().getOccupant().getStatusString());
            }
        }
    }

    public void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void playSFX(String fileName) {
        try {
            String s = "src/main/java/view/sfx/" + fileName + ".wav";
            File file = new File("src/main/java/view/sfx/" + fileName + ".wav");
            Media sound = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Throwable e) {
            System.out.print("");
        }
    }

}

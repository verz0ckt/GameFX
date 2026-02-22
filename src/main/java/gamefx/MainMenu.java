package gamefx;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MainMenu {
    //made using ChatGPT
    @FXML
    private TextField nameField;

    @FXML
    private TextField hostPortField;

    @FXML
    private VBox page1Box;       // first page
    @FXML
    private VBox multiplayerBox; // second page

    // ------------------- Page Navigation -------------------

    @FXML
    private void handleSingleplayer() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) return;

        Main.startGame(name);
    }

    @FXML
    private void handleMultiplayer() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) return;
        // Hide first page, show multiplayer page
        page1Box.setVisible(false);
        page1Box.setManaged(false);

        multiplayerBox.setVisible(true);
        multiplayerBox.setManaged(true);
    }

    @FXML
    private void handleReturn() {
        // Hide multiplayer page, show first page
        multiplayerBox.setVisible(false);
        multiplayerBox.setManaged(false);

        page1Box.setVisible(true);
        page1Box.setManaged(true);
    }

    // ------------------- Multiplayer Actions -------------------

    @FXML
    private void handleMultiplayerAction(boolean isHost) {
        String name = nameField.getText().trim();
        String hostPort = hostPortField.getText().trim();

        if (hostPort.isEmpty()) return;

        Main.startMultiplayer(name, hostPort, isHost);
    }

    @FXML
    private void handleHost() {
        handleMultiplayerAction(true);
    }

    @FXML
    private void handleJoin() {
        handleMultiplayerAction(false);
    }

    // ------------------- Close -------------------

    @FXML
    private void handleClose() {
        Main.setClose();
        Main.tryClose();
    }
}
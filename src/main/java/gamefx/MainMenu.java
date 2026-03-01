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

    }

    @FXML
    private void handleHost() {
        String name = nameField.getText().trim();
        String portString = hostPortField.getText().trim();

        if (portString.isEmpty()) return;
        try {
            int port = Integer.parseInt(portString.replace(":","").trim());
            Main.startHost(name, port);
        } catch (NumberFormatException e) {
            return;
        }
    }

    @FXML
    private void handleJoin() {
        String name = nameField.getText().trim();
        String hostPort = hostPortField.getText().trim();

        if (hostPort.isEmpty()) return;
        String[] parts = hostPort.split(":");
        if (parts.length != 2) return;
        try {
            int port = Integer.parseInt(parts[1].trim());
            Main.startClient(name, parts[0].trim(), port);
        } catch (NumberFormatException _) {
            return;
        }
    }

    // ------------------- Close -------------------

    @FXML
    private void handleClose() {
        Main.setClose();
        Main.tryClose();
    }
}
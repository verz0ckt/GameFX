package gamefx;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class MainMenu extends Scene {
    VBox root;
    ObservableList<Node> children;
    public MainMenu() {
        super(new VBox());
        root = (VBox) getRoot();
        root.setAlignment(Pos.BASELINE_CENTER);
        root.setSpacing(10);
        root.setPrefSize(1200,600);
        children = root.getChildren();
        Button startButton = new Button("Start");
        startButton.setOnAction(_ -> Main.getInstance().startGame());
        children.add(startButton);
        Button endButton = new Button("CLOSE");
        endButton.setOnAction(_ -> {
            Main.getInstance().setClose();
            Main.getInstance().tryClose();
        });
        children.add(endButton);
    }
}

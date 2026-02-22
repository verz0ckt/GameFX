package gamefx.multiplayer;

import gamefx.Game;
import javafx.stage.Stage;
import java.util.ArrayList;

public class Host extends Game {
    ArrayList<Integer> dirty;
    public Host(Stage stage, int port) {
        super(stage);
        dirty = new ArrayList<>(8);
    }

    @Override
    public void init(String name) {
        super.init(name);
    }

    @Override
    public void update() {

    }
}

package gamefx.multiplayer;

import gamefx.Game;
import gamefx.Main;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Host extends Game {
    ArrayList<Integer> dirty;
    private ServerSocket TCPSocket;
    private DatagramSocket socket;
    public Host(Stage stage, int port) {
        super(stage);
        dirty = new ArrayList<>(8);
        try {
            TCPSocket = new ServerSocket(port);
            socket = new DatagramSocket(port);
        } catch (IOException _) {
            Main.tryClose();
        }

    }

    @Override
    public void init(String name) {
        super.init(name);
    }

    @Override
    public void update() {
        super.update();

    }
}

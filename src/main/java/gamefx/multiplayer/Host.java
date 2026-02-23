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
    private final ClientPlayer[] otherPlayers;
    private char playerNum;
    private ServerSocket TCPSocket;
    private DatagramSocket socket;
    public Host(Stage stage, int port) {
        super(stage);
        otherPlayers = new ClientPlayer[8];
        playerNum = 0;
        dirty = new ArrayList<>(8);
        try {
            TCPSocket = new ServerSocket(port);
            socket = new DatagramSocket(port);
        } catch (IOException _) {
            Main.tryClose();
        }

    }

    public boolean addOtherPlayer(ClientPlayer player){
        if(playerNum < 8) {
            otherPlayers[playerNum++] = player;
            return true;
        }else {
            return false;
        }
    }
    public ClientPlayer popOtherPlayer(){
        ClientPlayer p = otherPlayers[--playerNum];
        otherPlayers[playerNum] = null;
        return p;
    }
    public ClientPlayer removeOtherPlayer(int index) {
        if (playerNum <= index) {
            return null;
        }
        ClientPlayer p = otherPlayers[index];
        playerNum--;
        while (index < playerNum) {
            otherPlayers[index] = otherPlayers[++index];
        }
        otherPlayers[playerNum] = null;
        return p;
    }
    public void removeOtherPlayer(ClientPlayer p) {
        int i = 0;
        --playerNum;
        while (i<=playerNum && p.equals(otherPlayers[i++]));
        while (i<=playerNum){
            otherPlayers[i-1] =  otherPlayers[i++];
        }
        otherPlayers[playerNum] = null;
    }

    @Override
    public void init(String name) {
        new Thread(() -> {
            while (!stop) {
                try {
                    if(playerNum >= 8) {
                        addOtherPlayer(new ClientPlayer(playerNum,TCPSocket.accept()));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        super.init(name);
    }

    @Override
    public void update(){
        super.update();
    }
}

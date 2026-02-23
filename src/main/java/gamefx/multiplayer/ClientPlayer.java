package gamefx.multiplayer;

import gamefx.objects.Player;

import java.io.IOException;
import java.net.Socket;

public class ClientPlayer {
    private char id;
    private Socket tcpSocket;
    private Player player;
    public ClientPlayer(char id,Socket s){
        this.id = id;
        this.tcpSocket = s;
    }
    public void send(byte[] bytes){
        try {
            tcpSocket.getOutputStream().write(bytes);
            tcpSocket.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}

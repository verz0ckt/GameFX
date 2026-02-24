package gamefx.multiplayer;

import gamefx.objects.Player;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientPlayer {
    private char id;
    private Socket tcpSocket;
    private SocketAddress addr;
    private Player player;
    public ClientPlayer(char id,Socket s){
        this.id = id;
        this.tcpSocket = s;
        this.addr = tcpSocket.getRemoteSocketAddress();
    }
    public void send(byte[] bytes){
        try {
            tcpSocket.getOutputStream().write(bytes);
            tcpSocket.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public char getId() {
        return id;
    }

    public SocketAddress getAddr() {
        return addr;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}

package gamefx.multiplayer;

import gamefx.objects.Player;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientPlayer {
    private char id;
    public AtomicBoolean isWriting;
    private Socket tcpSocket;
    private SocketAddress addr;
    private Player player;
    public ClientPlayer(char id,Socket s){
        this.id = id;
        this.tcpSocket = s;
        this.addr = tcpSocket.getRemoteSocketAddress();
        isWriting = new AtomicBoolean(false);
    }
    public boolean send(byte[] bytes) {
        if (!isWriting.getAndSet(true)){
            try {
                tcpSocket.getOutputStream().write(bytes);
                tcpSocket.getOutputStream().flush();
                isWriting.set(false);
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        return false;
    }
    //returns false if unsuccessful
    public boolean setWriting(){
        return !isWriting.getAndSet(true);
    }
    public boolean write(byte[] bytes,int offset,int size) throws IOException {
        if(!isWriting.get())return false;
        tcpSocket.getOutputStream().write(bytes,offset,size);
        return true;
    }
    public boolean write(byte[] bytes) throws IOException {
        if(!isWriting.get())return false;
        tcpSocket.getOutputStream().write(bytes);
        return true;
    }
    public boolean flush() throws IOException {
        if(!isWriting.get())return false;
        tcpSocket.getOutputStream().flush();
        isWriting.set(false);
        return true;
    }
    //logoutSystem and TCP read

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

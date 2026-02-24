package gamefx.multiplayer;

import gamefx.Game;
import gamefx.Main;
import gamefx.objects.Player;
import gamefx.util.Quaternion;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class Host extends Game {
    private final ClientPlayer[] otherPlayers;
    private char playerNum;
    private ServerSocket TCPSocket;
    private DatagramSocket socket;
    public Host(Stage stage, int port) {
        super(stage);
        otherPlayers = new ClientPlayer[7];
        playerNum = 0;
        sendBuffer = new byte[8*Player.BYTESIZEFORNEW];
        sendPacket = new DatagramPacket(sendBuffer,sendBuffer.length);
        lastUpdateTime = System.nanoTime();
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
    private byte[] sendBuffer;
    private DatagramPacket sendPacket;
    private void sendMovement(){
        int offset = addPlayerToBuffer(mainPlayer, (char) 0,sendBuffer,0);
        for(ClientPlayer p : otherPlayers){
            offset = addPlayerToBuffer(p.getPlayer(),p.getId(),sendBuffer, offset);
        }
        for(ClientPlayer p : otherPlayers){
            sendPacket.setSocketAddress(p.getAddr());
            try {
                socket.send(sendPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public int addPlayerToBuffer(Player p,char id,byte[] buffer,int offset){
        buffer[offset++] = (byte)id;
        double[] pos = p.getPos();
        offset = addDoubletoBuffer(pos[0],buffer,offset);
        offset = addDoubletoBuffer(pos[1],buffer,offset);
        offset = addDoubletoBuffer(pos[2],buffer,offset);
        Quaternion rot = p.getRot();
        offset = addDoubletoBuffer(rot.getW(),buffer,offset);
        offset = addDoubletoBuffer(rot.getI(),buffer,offset);
        offset = addDoubletoBuffer(rot.getJ(),buffer,offset);
        offset = addDoubletoBuffer(rot.getK(),buffer,offset);
        return offset;
    }
    public int addDoubletoBuffer(double d,byte[] buffer, int offset){
        long value = Double.doubleToLongBits(d);
        buffer[offset++] = (byte) (value>>>56);
        buffer[offset++] = (byte) (value>>>48);
        buffer[offset++] = (byte) (value>>>40);
        buffer[offset++] = (byte) (value>>>32);
        buffer[offset++] = (byte) (value>>>24);
        buffer[offset++] = (byte) (value>>>16);
        buffer[offset++] = (byte) (value>>>8);
        buffer[offset++] = (byte) (value);
        return offset;
    }
    private long lastUpdateTime;
    @Override
    public void update(){
        super.update();
        lastUpdateTime += lastUpdateTime;
        if(lastUpdateTime-System.nanoTime() >= 20_000_000){
            sendMovement();
            lastUpdateTime += 20_000_000;
        }
    }
}

package gamefx.multiplayer;

import gamefx.Game;
import gamefx.GameKey;
import gamefx.Main;
import gamefx.objects.Object;
import gamefx.objects.Player;
import gamefx.util.Quaternion;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Host extends Game {
    private final ClientPlayer[] otherPlayers;
    private char playerNum;
    public static final char MAXPLAYERS = 7;
    private ServerSocket tcpSocket;
    private DatagramSocket socket;
    int[] otherPressed;
    public Host(Stage stage, int port) {
        super(stage);
        otherPlayers = new ClientPlayer[MAXPLAYERS];
        playerNum = 0;
        otherPressed = new int[MAXPLAYERS];
        sendBuffer = new byte[(MAXPLAYERS+1)*Player.BYTESIZEFORNEW];
        sendPacket = new DatagramPacket(sendBuffer,sendBuffer.length);
        tcpBuffer = new byte[512];
        lastUpdateTime = System.nanoTime();
        try {
            tcpSocket = new ServerSocket(port);
            socket = new DatagramSocket(port);
        } catch (IOException _) {
            Main.tryClose();
        }
    }
    private void movePlayer(ClientPlayer p){
        int pressed = otherPressed[p.getId()];
        Player player = p.getPlayer();
        double x = (((pressed >>> GameKey.Inputs.FORWARD.ordinal()) & 1) - ((pressed >>> GameKey.Inputs.BACKWARDS.ordinal()) & 1)) * deltatime * 100;
        double z = (((pressed >>> GameKey.Inputs.LEFT.ordinal()) & 1) - ((pressed >>> GameKey.Inputs.RIGHT.ordinal()) & 1)) * deltatime * 100;
        if ((x != 0) || (z != 0)) {
            player.move(x, 0, z);
        }
        int rotz = (((pressed >>> GameKey.Inputs.UP.ordinal()) & 1) - ((pressed >>> GameKey.Inputs.DOWN.ordinal()) & 1));
        int roty = (((pressed >>> GameKey.Inputs.TRIGHT.ordinal()) & 1) - ((pressed >>> GameKey.Inputs.TLEFT.ordinal()) & 1));
        if (roty != 0) {
            player.rotateAngle(deltatime, 0, roty, 0);
        }
        if (rotz != 0) {
            Quaternion rot = player.head.getRot();
            rot.multiply(Quaternion.fromAngle(deltatime, 0, 0, rotz));
            rot.setW(Math.max(rot.getW(), 0.7071067812));
            rot.setK(Math.max(Math.min(rot.getK(), 0.7071067812), -0.7071067812));
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
                        Socket s = tcpSocket.accept();
                        if(s.getInputStream().read() == 0x01) {
                            s.close();
                            continue;
                        }
                        String otherName = new String(s.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                        OutputStream output = s.getOutputStream();
                        if(otherName.isBlank()){
                            output.write(new byte[]{(byte) 0xff,(byte) 0xff});
                            output.flush();
                            s.close();
                            return;
                        }
                        ClientPlayer p = new ClientPlayer(playerNum,s);
                        p.setPlayer(new Player(otherName,new double[3]));
                        addOtherPlayer(p);
                        sendAll(p, (byte) 0x01, (byte) p.getId());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        super.init(name);
    }
    private final byte[] sendBuffer;
    private final DatagramPacket sendPacket;
    private void sendPlayer(){
        int offset = addObjectToBuffer(mainPlayer, (char) MAXPLAYERS,sendBuffer,0);
        for(ClientPlayer p : otherPlayers){
            offset = addObjectToBuffer(p.getPlayer(),p.getId(),sendBuffer, offset);
        }
        if(offset < sendBuffer.length){
            sendBuffer[offset] = (byte)-1;
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
    @Deprecated
    public static int addObjectToBuffer(Object o,char id,byte[] buffer,int offset){
        //TODO: turn back to addPlayerToBuffer and add new func for each type;
        buffer[offset++] = (byte)id;
        double[] pos = o.getPos();
        offset = addDoubleToBuffer(pos[0],buffer,offset);
        offset = addDoubleToBuffer(pos[1],buffer,offset);
        offset = addDoubleToBuffer(pos[2],buffer,offset);
        Quaternion rot = o.getRot();
        offset = addDoubleToBuffer(rot.getW(),buffer,offset);
        offset = addDoubleToBuffer(rot.getI(),buffer,offset);
        offset = addDoubleToBuffer(rot.getJ(),buffer,offset);
        offset = addDoubleToBuffer(rot.getK(),buffer,offset);
        return offset;
    }
    public static int addDoubleToBuffer(double d, byte[] buffer, int offset){
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
    private final byte[] tcpBuffer;
    /*
        0b0001_xxxx = Player
        0b0010_xxxx = Block
        0b0011_xxxx = PlaneObj
     */
    public boolean sendAll(ClientPlayer cp,byte... sendType){
        if(!cp.setWriting())return false;
        try {
            if(sendType != null && sendType.length > 0) cp.write(sendType);

            for (Object o : objects) {
                if (o instanceof Player) {
                    tcpBuffer[0] = (byte) o.getId();
                    byte[] name = ((Player) o).getName().getBytes(StandardCharsets.UTF_8);
                    System.arraycopy(name, 0, tcpBuffer, 1, name.length);
                    tcpBuffer[name.length+1] = 0;
                    continue;
                }
                int i = addObjectToBuffer(o, o.getId(), tcpBuffer, 0);
                cp.write(tcpBuffer, 0, i);
            }
            cp.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


    private long lastUpdateTime;
    @Override
    public void update(){
        super.update();
        for(int i = 0;i<playerNum;i++){
            movePlayer(otherPlayers[i]);
        }
        if(lastUpdateTime-System.nanoTime() >= 20_000_000){
            sendPlayer();
            lastUpdateTime += 20_000_000;
        }
    }
}

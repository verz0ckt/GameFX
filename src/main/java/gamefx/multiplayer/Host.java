package gamefx.multiplayer;

import gamefx.Game;
import gamefx.GameKey;
import gamefx.Main;
import gamefx.objects.Block;
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
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import static gamefx.multiplayer.BufferHelper.*;

public class Host extends Game {
    private final ClientPlayer[] otherPlayers;
    private char playerNum;
    private Semaphore slots;
    public static final char MAXPLAYERS = 7;
    private ServerSocket tcpSocket;
    private DatagramSocket socket;
    int[] otherPressed;
    public Host(Stage stage, int port) {
        super(stage);
        slots = new Semaphore(MAXPLAYERS);
        otherPlayers = new ClientPlayer[MAXPLAYERS];
        playerNum = 0;
        otherPressed = new int[MAXPLAYERS];
        sendBuffer = new byte[(MAXPLAYERS+1)* playerUpdateSize];
        sendPacket = new DatagramPacket(sendBuffer,sendBuffer.length);
        receiveBuffer = new byte[9];
        receivePacket = new DatagramPacket(receiveBuffer,receiveBuffer.length);
        tcpBuffer = new byte[512];
        objectsToUpdate = new ArrayList<>();
        lastUpdateTime = System.nanoTime();
        try {
            tcpSocket = new ServerSocket(port);
            socket = new DatagramSocket(port);
        } catch (IOException _) {
            Main.tryClose();
        }
        assert tcpSocket != null;
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
        slots.release();
        otherPlayers[playerNum] = null;
    }

    private byte[] receiveBuffer;
    private DatagramPacket receivePacket;
    @Override
    public void init(String name) {
        new Thread(() -> {
            while (!stop) {
                try {
                    slots.acquire();
                }catch(InterruptedException _){}
                try {
                    Socket s = tcpSocket.accept();
                    int fbyte = s.getInputStream().read();
                    System.out.println("fbyte: "+fbyte);
                    if (fbyte != loginByte) {
                        s.close();
                        slots.release();
                        continue;
                    }
                    System.out.println("Done");

                    byte[] nameBytes = new byte[16];
                    int count = s.getInputStream().read(nameBytes);
                    String otherName = new String(nameBytes,0,count, StandardCharsets.UTF_8);
                    System.out.println("Name: "+otherName);
                    OutputStream output = s.getOutputStream();
                    if (otherName.isBlank()) {
                        output.write(forceStopSequence);
                        output.flush();
                        s.close();
                        slots.release();
                        continue;
                    }
                    ClientPlayer p = new ClientPlayer(playerNum, s);
                    p.setPlayer(new Player(otherName, new double[3]));
                    addOtherPlayer(p);
                    objects.add(p.getPlayer());
                    //TODO: send to other players
                    sendAll(p, loginByte, (byte) p.getId());
                    System.out.println("finished");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },"Player Accept").start();
        new Thread(() ->{
            while (!stop){
                try {
                    socket.receive(receivePacket);
                    int id = receiveBuffer[0];
                    otherPressed[id] = (((int)receiveBuffer[1] & 0xff)<<24)+(((int)receiveBuffer[2] & 0xff)<<16)+(((int)receiveBuffer[3] & 0xff)<<8)+((int)receiveBuffer[4] & 0xff);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },"GetMovement").start();
        super.init(name);
        System.out.println("init complete");
    }
    private final byte[] sendBuffer;
    private final DatagramPacket sendPacket;
    private void sendPlayer(){
        int offset = addUpdateToBuffer(mainPlayer, (char) MAXPLAYERS,sendBuffer,0);
        for(int i = 0;i<playerNum;i++){
            ClientPlayer p = otherPlayers[i];
            offset = addUpdateToBuffer(p.getPlayer(),p.getId(),sendBuffer, offset);
        }
        if(offset < sendBuffer.length){
            sendBuffer[offset] = endSequence[0];
            sendBuffer[offset+1] = endSequence[1];
        }
        for(int i = 0;i<playerNum;i++){
            sendPacket.setSocketAddress(otherPlayers[i].getAddr());
            try {
                socket.send(sendPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
            int i = addNewObjectToBuffer(mainPlayer,tcpBuffer,0);
            cp.write(tcpBuffer, 0, i);
            for (Object o : objects) {
                i = addNewObjectToBuffer(o, tcpBuffer, 0);
                cp.write(tcpBuffer, 0, i);
            }
            cp.write(endSequence);
            cp.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    private final ArrayList<Integer> objectsToUpdate;
    private void sendObjectUpdate(){
        if(objectsToUpdate.isEmpty())return;
        tcpBuffer[0] = updateByte;
        int offset = 1;
        for(int i : objectsToUpdate){
            switch (i&0x03){
                case addFlag -> {
                    tcpBuffer[offset++] = addFlag;
                    offset = addNewObjectToBuffer(objects.get(i>>>2),tcpBuffer,offset);
                }
                case removeFlag -> {
                    tcpBuffer[offset++] = removeFlag;
                    offset = addIntToBuffer(i>>>2,tcpBuffer,offset);
                }
                default -> {
                    tcpBuffer[offset++] = updateFlag;
                    offset = addIntToBuffer(i>>>2,tcpBuffer,offset);
                    offset = addObjectUpdateToBuffer(objects.get(i>>>2),tcpBuffer,offset);
                }
            }
        }

        if(offset >= tcpBuffer.length){
            System.out.println("TCP BUFFER TOO SHORT");
        }
        for (int i = 0; i<playerNum;i++){
            ClientPlayer cp = otherPlayers[i];
            if(!cp.setWriting()){
                throw new RuntimeException("Couldn't sent to Player:"+(int)cp.getId()+";"+ cp.getPlayer());
            }
            try {
                cp.write(tcpBuffer,0,offset);
                cp.write(endSequence);
                cp.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        objectsToUpdate.clear();
    }


    private long lastUpdateTime;
    @Override
    public void update(){
        long oldtime = timeNano;
        timeNano = System.nanoTime();
        deltatime = (timeNano - oldtime) / 1_000_000_000.0;
        //TODO: finish key recognision
        int pressed = gameKey.getPressed();
        int released = gameKey.getReleased();
        {
            double x = (((pressed >>> GameKey.Inputs.FORWARD.ordinal()) & 1) - ((pressed >>> GameKey.Inputs.BACKWARDS.ordinal()) & 1)) * deltatime * 100;
            double y = 0;
            double z = (((pressed >>> GameKey.Inputs.LEFT.ordinal()) & 1) - ((pressed >>> GameKey.Inputs.RIGHT.ordinal()) & 1)) * deltatime * 100;
            if ((x != 0) || (y != 0) || (z != 0)) {
                getMainPlayer().move(x, y, z);
            }
        }{
            int rotz = (((pressed >>> GameKey.Inputs.UP.ordinal()) & 1) - ((pressed >>> GameKey.Inputs.DOWN.ordinal()) & 1));
            int roty = (((pressed >>> GameKey.Inputs.TRIGHT.ordinal()) & 1) - ((pressed >>> GameKey.Inputs.TLEFT.ordinal()) & 1));
            if (roty != 0) {
                getMainPlayer().rotateAngle(deltatime, 0, roty, 0);
            }
            if (rotz != 0) {
                Quaternion rot = getMainPlayer().head.getRot();
                rot.multiply(Quaternion.fromAngle(deltatime, 0, 0, rotz));
                rot.setW(Math.max(rot.getW(), 0.7071067812));
                rot.setK(Math.max(Math.min(rot.getK(), 0.7071067812), -0.7071067812));
            }
        }
        if((released&1<< GameKey.Inputs.FULLSCREEN.ordinal()) != 0){
            toggleFullscreen();
            gameKey.unset(GameKey.Inputs.FULLSCREEN);
        }
        if((released&1<< GameKey.Inputs.PERSPECTIVE.ordinal()) != 0){
            System.out.println(cam.togglePerspective());
            gameKey.unset(GameKey.Inputs.PERSPECTIVE);
        }
        if((released&1<< GameKey.Inputs.SPAWN.ordinal()) != 0){
            double[] pos = new double[]{200,0,0};
            Quaternion q = mainPlayer.getRot().copy();
            q.multiply(mainPlayer.head.getRot());
            q.apply(pos);
            double[] playerPos = getCam().getAbsPos();
            pos[0] += playerPos[0];
            pos[1] += playerPos[1];
            pos[2] += playerPos[2];
            Block b = new Block(pos,q,32);
            int index = objects.size();
            objects.add(b);
            if(index+1 != objects.size()){
                index = objects.indexOf(b);
            }
            objectsToUpdate.add(addFlagToIndex(index,addFlag));
            gameKey.unset(GameKey.Inputs.SPAWN);
        }
        //TODO: remove{
        int blockUP = ((pressed >>> GameKey.Inputs.B1.ordinal())&1)-((pressed >>>GameKey.Inputs.B2.ordinal())&1);
        if(blockUP != 0){
            int index = objects.size()-1;
            Object last = objects.getLast();
            if(last instanceof Player)return;
            if(index != objects.size() - 1){
                index = objects.indexOf(last);
            }
            Quaternion q = mainPlayer.getRot().getConjugate();
            q.multiplyGlobal(Quaternion.fromEuler(deltatime,0,0,blockUP));
            q.multiplyGlobal(mainPlayer.getRot());
            last.getRot().multiplyGlobal(q);
            objectsToUpdate.add(addFlagToIndex(index,updateFlag));
        }
        int blockSide =((pressed >>> GameKey.Inputs.B3.ordinal())&1)-((pressed >>>GameKey.Inputs.B4.ordinal())&1);
        if(blockSide != 0){
            int index = objects.size()-1;
            Object last = objects.getLast();
            if(last instanceof Player)return;
            if(index != objects.size() - 1){
                index = objects.indexOf(last);
            }
            last.getRot().multiplyGlobal(Quaternion.fromEuler(deltatime,0,blockSide,0));
            objectsToUpdate.add(addFlagToIndex(index,updateFlag));
        }
        //} only for testing

        for(int i = 0;i<playerNum;i++){
            movePlayer(otherPlayers[i]);
        }
        if(System.nanoTime()-lastUpdateTime >= 20_000_000){
            if(playerNum > 0) {
                sendPlayer();
                sendObjectUpdate();
            }
            lastUpdateTime += 20_000_000;
        }
    }

    @Override
    public void stop() {

        try {
            //kick all players
            tcpSocket.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.stop();
    }
}

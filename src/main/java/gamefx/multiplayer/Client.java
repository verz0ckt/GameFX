package gamefx.multiplayer;

import gamefx.Game;
import gamefx.GameKey;
import gamefx.Main;
import gamefx.objects.*;
import gamefx.objects.Object;
import gamefx.util.Quaternion;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static gamefx.multiplayer.BufferHelper.*;

public class Client extends Game {

    private ArrayList<Player> otherPlayers;
    char id;
    public Client(Stage stage,String host,int port) {
        super(stage);
        try {
            //tcpSocket = new Socket(host,port,null,port+1);
            tcpSocket = new Socket(host,port);
            tcpSocket.setKeepAlive(true);
            socket = new DatagramSocket(tcpSocket.getLocalSocketAddress());//(port)
            inputToSend = new byte[9];
            receiveBuffer = new byte[(Host.MAXPLAYERS+1)* playerUpdateSize];
            tcpBuffer = new byte[512];
            sendPacket = new DatagramPacket(inputToSend, inputToSend.length,tcpSocket.getRemoteSocketAddress());
            receivePacket = new DatagramPacket(receiveBuffer,receiveBuffer.length);
        } catch (IOException e) {
            System.out.println("Fail");
            throw new RuntimeException(e);
            //Main.tryClose();
        }
        assert tcpSocket != null;
        assert tcpBuffer != null;
        assert socket != null;
        otherPlayers = new ArrayList<>(7);
        System.out.println("test");
    }

    @Override
    public void init(String name) {
        stage.setOnCloseRequest(windowEvent -> {
            setStop();
            Main.setClose();
            windowEvent.consume();
        });
        gameKey.addHandlers();
        mainPlayer = login(name);
        cam = new Cam(mainPlayer.head,new double[]{0,8,0});
        System.out.println("init finished");
    }

    @Override
    public void start() {
        startFetch();
        startTCPFetch();
        super.start();
    }

    private Socket tcpSocket;
    private DatagramSocket socket;
    public Player login(String name) {
        try {
            tcpSocket.getOutputStream().write(0x1);
            tcpSocket.getOutputStream().write(name.getBytes(StandardCharsets.UTF_8));
            tcpSocket.getOutputStream().flush();
            int type = tcpSocket.getInputStream().read();
            assert type != -1;
            if (type == 1) {
                readLogin(tcpSocket.getInputStream());
            }else{
                Main.tryClose();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new Player(name, new double[3]);
    }
    private byte[] tcpBuffer;
    public Player readLogin(InputStream stream){
        assert tcpBuffer != null;
        try {
            int notEnd = 0;
            id = (char) stream.read();
            System.out.println("ID: "+(int)id);
            stream.readNBytes(tcpBuffer,0,1);
            int pSize = (tcpBuffer[0]&0x0F)+9;
            stream.readNBytes(tcpBuffer,1,pSize);
            Player hostPlayer = (Player) createObjectFromBuffer(tcpBuffer,0);
            Player main = null;
            while (true){
                stream.readNBytes(tcpBuffer,0,1);
                if(tcpBuffer[0] == endSequence[0]){
                    stream.readNBytes(tcpBuffer,1,1);
                    if(tcpBuffer[1] == endSequence[1]){
                        break;
                    }
                    notEnd = 1;
                }
                stream.readNBytes(tcpBuffer,1+notEnd,getBufferSizeOfNew((char) tcpBuffer[0])+notEnd-1);
                Object o = createObjectFromBuffer(tcpBuffer,0);
                if(o instanceof Player){
                    if(otherPlayers.size() == this.id){
                        main = (Player) o;
                        o = hostPlayer;
                    }
                    otherPlayers.add((Player) o);
                }
                objects.add(o);
                notEnd = 0;
            }
            assert main != null;
            return main;
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void startFetch(){
        new Thread(() -> {
            while (!stop){
                try {
                    socket.receive(receivePacket);
                    receiveBufferNew = true;
                    sendInput();
                } catch (IOException e) {
                    if(!stop) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
    private byte[] inputToSend;
    private byte[] receiveBuffer;
    private boolean receiveBufferNew;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    public void sendInput(){
        int pressed = gameKey.getPressed();
        int released = gameKey.getReleased();
        inputToSend[0] = (byte) id;
        inputToSend[1] = (byte) (pressed>>>24);
        inputToSend[2] = (byte) (pressed>>>16);
        inputToSend[3] = (byte) (pressed>>>8);
        inputToSend[4] = (byte) (pressed);
        inputToSend[5] = (byte) (released>>>24);
        inputToSend[6] = (byte) (released>>>16);
        inputToSend[7] = (byte) (released>>>8);
        inputToSend[8] = (byte) (released);
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update() {
        long oldtime = timeNano;
        timeNano = System.nanoTime();
        deltatime = (timeNano - oldtime) / 1_000_000_000.0;

        if(receiveBufferNew){
            int i = 0;
            while(i < receiveBuffer.length && (receiveBuffer[i] != endSequence[0] || receiveBuffer[i+1] != endSequence[1])){
                int playerID = receiveBuffer[i++];
                if(playerID == this.id){
                    i = updatePlayerFromBuffer(mainPlayer,receiveBuffer,i);
                }else if(playerID == Host.MAXPLAYERS){
                    i = updatePlayerFromBuffer(otherPlayers.get(this.id),receiveBuffer,i);
                }else {
                    i = updatePlayerFromBuffer(otherPlayers.get(playerID), receiveBuffer, i);
                }
            }
            receiveBufferNew = false;
        }

        movementPrediction();

        int released = gameKey.getReleased();
        if((released&1<< GameKey.Inputs.FULLSCREEN.ordinal()) != 0){
            toggleFullscreen();
            gameKey.unset(GameKey.Inputs.FULLSCREEN);
        }
        if((released&1<< GameKey.Inputs.PERSPECTIVE.ordinal()) != 0){
            System.out.println(cam.togglePerspective());
            gameKey.unset(GameKey.Inputs.PERSPECTIVE);
        }
    }
    private void startTCPFetch(){
        InputStream stream;
        try {
            stream = tcpSocket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            while (!stop) {
                try {
                    int type = stream.read();
                    switch (type){
                        case updateByte ->{
                            while (true) {
                                stream.readNBytes(tcpBuffer, 0, 2);
                                if (tcpBuffer[0] == endSequence[0] && tcpBuffer[1] == endSequence[1]) {
                                    break;
                                }
                                switch (tcpBuffer[0]){
                                    case addFlag -> {
                                        stream.readNBytes(tcpBuffer, 2, getBufferSizeOfNew((char) tcpBuffer[1])- 1);
                                        Object o = createObjectFromBuffer(tcpBuffer, 1);
                                        if (o instanceof Player) {
                                            if(((Player) o).getName().matches(mainPlayer.getName()))break;
                                            otherPlayers.add((Player) o);
                                        }
                                        objects.add(o);
                                    }
                                    case removeFlag -> {
                                        stream.readNBytes(tcpBuffer, 2, 3);
                                        int index = getIntfromBuffer(tcpBuffer,1);
                                        //TODO: make safe
                                        // unsafe
                                        objects.remove(index);
                                    }
                                    case updateFlag -> {
                                        stream.readNBytes(tcpBuffer, 2, basicUpdateSize-1);
                                        int index = getIntfromBuffer(tcpBuffer,1);
                                        updateObjectFromBuffer(objects.get(index),tcpBuffer,5);
                                    }
                                    default -> throw new IllegalStateException("Unexpected value: " + tcpBuffer[1]);
                                }
                            }
                        }
                        case logoutByte -> {

                        }
                        default -> throw new IllegalStateException("Unexpected value: " + type);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }
    private void movementPrediction(){
        int pressed = gameKey.getPressed();
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
    }


    @Override
    public void stop() {
        try {
            tcpSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        socket.close();
        System.out.println("noo");
        super.stop();
    }
}

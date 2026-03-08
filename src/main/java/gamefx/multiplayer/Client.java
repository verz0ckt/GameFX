package gamefx.multiplayer;

import gamefx.Game;
import gamefx.GameKey;
import gamefx.Main;
import gamefx.objects.*;
import gamefx.objects.Object;
import gamefx.util.Quaternion;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
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
            inputToSend = new byte[13];
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
        int mouseX = gameKey.getMovedX();
        int mouseY = gameKey.getMovedY();
        inputToSend[0] = (byte) id;
        inputToSend[1] = (byte) (pressed>>>24);
        inputToSend[2] = (byte) (pressed>>>16);
        inputToSend[3] = (byte) (pressed>>>8);
        inputToSend[4] = (byte) (pressed);
        inputToSend[5] = (byte) (mouseX>>>24);
        inputToSend[6] = (byte) (mouseX>>>16);
        inputToSend[7] = (byte) (mouseX>>>8);
        inputToSend[8] = (byte) (mouseX);
        inputToSend[9] = (byte) (mouseY>>>24);
        inputToSend[10] = (byte) (mouseY>>>16);
        inputToSend[11] = (byte) (mouseY>>>8);
        inputToSend[12] = (byte) (mouseY);
        gameKey.resetMouse();
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
        //prediction
        //movePlayer(getMainPlayer(), gameKey.getPressed(),0,0);
        processReleased();
        handlePause();
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
    @Override
    protected void handlePause() {
        if((gameKey.getReleased()&1<< GameKey.Inputs.PAUSE.ordinal()) != 0){
            gameKey.unset(GameKey.Inputs.PAUSE);
            gameKey.removeHandlers();
            renderer.addEventFilter(KeyEvent.KEY_RELEASED,pauseHandler);
            renderer.setCursor(Cursor.DEFAULT);

        }
    }
}

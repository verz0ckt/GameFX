package gamefx.multiplayer;

import gamefx.Game;
import gamefx.GameKey;
import gamefx.Main;
import gamefx.objects.Cam;
import gamefx.objects.Player;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;

public class Client extends Game {

    char id;
    public Client(Stage stage,String host,int port) {
        super(stage);
        try {
            TCPSocket = new Socket(host,port,null,port);
            TCPSocket.setKeepAlive(true);
            socket = new DatagramSocket(port);
            inputToSend = new byte[9];
            receiveBuffer = new byte[1024];
            InetSocketAddress addr = new InetSocketAddress(host,port);
            sendPacket = new DatagramPacket(inputToSend, inputToSend.length,addr);
            receivePacket = new DatagramPacket(receiveBuffer,receiveBuffer.length);
        } catch (IOException _) {
            Main.tryClose();
        }
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
    }

    @Override
    public void start() {
        super.start();
        startFetch();
    }

    private Socket TCPSocket;
    private DatagramSocket socket;
    public Player login(String name){
        objects.add(new Player("Main",new double[3]));
        id = 1;
        return new Player(name,new double[3]);
    }
    public void startFetch(){
        new Thread(() -> {
            while (!stop){
                try {
                    socket.receive(receivePacket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    private byte[] inputToSend;
    private byte[] receiveBuffer;
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
    public void fetch(){
        try {
            socket.receive(receivePacket);
        } catch (IOException e) {
            if(!stop) {
                throw new RuntimeException(e);
            }
        }
        // work with packet
    }

    @Override
    public void update() {
        int released = gameKey.getReleased();
        if((released&1<< GameKey.Inputs.FULLSCREEN.ordinal()) != 0){
            toggleFullscreen();
            gameKey.unset(GameKey.Inputs.FULLSCREEN);
        }
        if((released&1<< GameKey.Inputs.PERSPECTIVE.ordinal()) != 0){
            System.out.println(cam.togglePerspective());
            gameKey.unset(GameKey.Inputs.PERSPECTIVE);
        }
        sendInput();
    }

    @Override
    public void stop() {
        try {
            TCPSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        socket.close();
        super.stop();
    }
}

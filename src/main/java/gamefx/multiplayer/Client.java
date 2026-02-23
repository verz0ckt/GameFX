package gamefx.multiplayer;

import gamefx.Game;
import gamefx.GameKey;
import gamefx.Main;
import gamefx.objects.Cam;
import gamefx.objects.Player;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class Client extends Game {

    public Client(Stage stage,String host,int port) {
        super(stage);
        try {
            TCPSocket = new Socket(host, port,null,port);
            TCPSocket.setKeepAlive(true);
            socket = new DatagramSocket(port);
        } catch (IOException _) {
            Main.tryClose();
        }
        inputToSend = new byte[8];
        sendPacket = new DatagramPacket(inputToSend,8);
        receiveBuffer = new byte[1024];
        receivePacket = new DatagramPacket(receiveBuffer,1024);

    }

    @Override
    public void init(String name) {
        stage.setOnCloseRequest(windowEvent -> {
            setStop();
            Main.setClose();
            windowEvent.consume();
        });
        gameKey.addHandlers();
        mainPlayer = startFetch(name);
        cam = new Cam(mainPlayer.head,new double[]{0,8,0});
    }
    private Socket TCPSocket;
    private DatagramSocket socket;
    public Player startFetch(String name){

        addOtherPlayer(new Player("Main",new double[3]));
        return new Player(name,new double[3]);
    }
    private byte[] inputToSend;
    private byte[] receiveBuffer;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    public void sendInput(){
        int pressed = gameKey.getPressed();
        int released = gameKey.getReleased();
        inputToSend[0] = (byte) (pressed>>>24);
        inputToSend[1] = (byte) (pressed>>>16);
        inputToSend[2] = (byte) (pressed>>>8);
        inputToSend[3] = (byte) (pressed);
        inputToSend[4] = (byte) (released>>>24);
        inputToSend[5] = (byte) (released>>>16);
        inputToSend[6] = (byte) (released>>>8);
        inputToSend[7] = (byte) (released);
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
}

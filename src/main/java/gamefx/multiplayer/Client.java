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

public class Client extends Game {

    private ArrayList<Player> otherPlayers;
    char id;
    public Client(Stage stage,String host,int port) {
        super(stage);
        try {
            tcpSocket = new Socket(host,port,null,port);
            tcpSocket.setKeepAlive(true);
            socket = new DatagramSocket(port);
            inputToSend = new byte[9];
            receiveBuffer = new byte[8*(Player.BYTESIZEFORNEW+1)];
            InetSocketAddress addr = new InetSocketAddress(host,port);
            sendPacket = new DatagramPacket(inputToSend, inputToSend.length,addr);
            receivePacket = new DatagramPacket(receiveBuffer,receiveBuffer.length);
        } catch (IOException _) {
            Main.tryClose();
        }
        assert tcpSocket != null;
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
    }

    @Override
    public void start() {
        startFetch();
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
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new Player(name, new double[3]);
    }
    //0x01,id,8*pos*3,8*rot*4,
    public Player readLogin(InputStream stream){
        try {

            assert stream.read() == 0x01;
            id = (char) stream.read();
            while (true){
                Object o;
                switch(stream.read()){
                    case Player.ID ->{

                        continue;
                    }
                    case Block.ID ->{
                        o = new Block(new double[3],32);
                    }
                    case PlaneObj.ID ->{
                        o = new PlaneObj(new double[3],500);
                    }
                    default -> throw new RuntimeException();
                }
                updateObjectFromBytes(o,);
            }
            //return new Player("", new double[3]);
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
        int released = gameKey.getReleased();
        if((released&1<< GameKey.Inputs.FULLSCREEN.ordinal()) != 0){
            toggleFullscreen();
            gameKey.unset(GameKey.Inputs.FULLSCREEN);
        }
        if((released&1<< GameKey.Inputs.PERSPECTIVE.ordinal()) != 0){
            System.out.println(cam.togglePerspective());
            gameKey.unset(GameKey.Inputs.PERSPECTIVE);
        }
        if(receiveBufferNew){
            int i = 0;
            while(i < receiveBuffer.length && receiveBuffer[i] != -1 ){
                int playerID = receiveBuffer[i++];
                if(playerID == this.id){
                    i = updateObjectFromBytes(mainPlayer,receiveBuffer,i);
                }else if(playerID == Host.MAXPLAYERS){
                    i = updateObjectFromBytes(otherPlayers.get(this.id),receiveBuffer,i);
                }else {
                    i = updateObjectFromBytes(otherPlayers.get(playerID), receiveBuffer, i);
                }
            }
            receiveBufferNew = false;
        }
    }
    public int updateObjectFromBytes(Object o, byte[] bytes, int offset){
        double x = getDoublefromBytes(bytes,offset);
        offset+= 8;
        double y = getDoublefromBytes(bytes,offset);
        offset+=8;
        double z = getDoublefromBytes(bytes,offset);
        offset+=8;
        o.setPos(x,y,z);
        Quaternion rot = o.getRot();
        rot.setW(getDoublefromBytes(bytes,offset));
        offset+=8;
        rot.setI(getDoublefromBytes(bytes,offset));
        offset+=8;
        rot.setJ(getDoublefromBytes(bytes,offset));
        offset+=8;
        rot.setK(getDoublefromBytes(bytes,offset));
        offset+=8;
        return offset;
    }
    public double getDoublefromBytes(byte[] buffer, int offset){
        return (buffer[offset++]<<56)+(buffer[offset++]<<48)+(buffer[offset++]<<40)+(buffer[offset++]<<32)+(buffer[offset++]<<24)+(buffer[offset++]<<16)+(buffer[offset++]<<8)+buffer[offset];
    }

    @Override
    public void stop() {
        try {
            tcpSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        socket.close();
        super.stop();
    }
}

package gamefx;

import gamefx.objects.*;
import gamefx.objects.Object;
import gamefx.rendering.Renderer;
import gamefx.util.Quaternion;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

public class Game
{
    protected Renderer renderer;
    protected Stage stage;
    protected GameKey gameKey;
    protected AnimationTimer clock;
    protected boolean stop = false;
    protected Player mainPlayer;
    protected Cam cam;
    protected Player[] otherPlayers;
    protected int playerNum = 0;
    public Player getMainPlayer() {
        return mainPlayer;
    }
    public PlaneObj plane;
    public ArrayList<Object> objects;


    public Stage getStage() {
        return stage;
    }

    public Game(Stage stage) {
       System.out.println("Start");
        this.stage = stage;
        renderer = new Renderer(this);
        //input
        gameKey = new GameKey(renderer);
        clock = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update();
                renderer.repaint();
                if(stop){
                    Game.this.stop();
                }
            }
        };
        //objects
        objects = new ArrayList<>();
        otherPlayers = new Player[8];
    }
    public void init(String name){
        stage.setOnCloseRequest(windowEvent -> {
            setStop();
            Main.setClose();
            windowEvent.consume();
        });
        gameKey.addHandlers();
        mainPlayer = new Player(name,new double[3]);
        cam = new Cam(mainPlayer.head,new double[]{0,8,0});
        //testobjects
        plane = new PlaneObj(new double[]{0,0,0},500);
        addOtherPlayer(new Player("other",new double[3]));
        objects.add(new Block(new double[]{100, 16, 0}, 32));
        objects.add(new Block(new double[]{200, 32, 0}, 64));
        //stressInit2(objects,1000);

    }
    public boolean addOtherPlayer(Player player){
        if(playerNum < 8) {
            otherPlayers[playerNum] = player;
            playerNum++;
            return true;
        }else {
            return false;
        }
    }
    public Player popOtherPlayer(){
        playerNum--;
        Player p = otherPlayers[playerNum];
        otherPlayers[playerNum] = null;
        return p;
    }
    public Player removeOtherPlayer(int index){
        if(playerNum <= index){
            return null;
        }
        Player p = otherPlayers[index];
        playerNum--;
        while (index < playerNum){
            otherPlayers[index] = otherPlayers[++index];
        }
        otherPlayers[playerNum] = null;
        return p;
    }
    private void stressInit2(ArrayList<Object> objects, int max){
        for(int i = 0;i<max;i++) {
            objects.add(new Block(new double[]{200, 0,0}, 10));
        }
    }
    private void stressInit(ArrayList<Object> objects){
        for(int i = 0;i<100;i++) {
            for(int j = 0;j<10;j++) {
                objects.add(new Block(new double[]{200+(j*22), 0, (22*i)-(50*22)}, 10));
            }
        }
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public Cam getCam() {
        return cam;
    }

    public Player[] getOtherPlayers() {
        return otherPlayers;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public void start(){
        clock.start();
    }

    public double getDeltatime() {
        return deltatime;
    }

    protected double deltatime;
    public long timeNano;
    public void update() {
        long oldtime = timeNano;
        timeNano = System.nanoTime();
        deltatime = (timeNano - oldtime) / 1_000_000_000.0;
        //TODO: finish key recognision
        int pressed = gameKey.getPressed();
        int released = gameKey.getReleased();
        {
            double x = (((pressed >>> GameKey.Inputs.FORWARD.ordinal()) & 1) - ((pressed >>> GameKey.Inputs.BACKWARDS.ordinal()) & 1)) * deltatime * 30;
            double y = 0;
            double z = (((pressed >>> GameKey.Inputs.LEFT.ordinal()) & 1) - ((pressed >>> GameKey.Inputs.RIGHT.ordinal()) & 1)) * deltatime * 30;
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
            objects.add(new Block(pos,q,30));
            gameKey.unset(GameKey.Inputs.SPAWN);
        }
        int blockUP = ((pressed >>> GameKey.Inputs.B1.ordinal())&1)-((pressed >>>GameKey.Inputs.B2.ordinal())&1);
        if(blockUP != 0){
            Quaternion q = mainPlayer.getRot().getConjugate();
            q.multiplyGlobal(Quaternion.fromEuler(deltatime,0,0,blockUP));
            q.multiplyGlobal(mainPlayer.getRot());
            objects.getLast().getRot().multiplyGlobal(q);
        }
        int blockSide =((pressed >>> GameKey.Inputs.B3.ordinal())&1)-((pressed >>>GameKey.Inputs.B4.ordinal())&1);
        if(blockSide != 0){
            objects.getLast().getRot().multiplyGlobal(Quaternion.fromEuler(deltatime,0,blockSide,0));
        }

    }
    public void stop(){
        clock.stop();
        System.out.println("Stopped");
        stage.setScene(null);
        Main.tryClose();
    }
    public void setStop(){
        stop = true;
    }
    public void setFullscreen(boolean x){
        stage.setFullScreen(x);
    }
    public void toggleFullscreen(){
        stage.setFullScreen(!stage.isFullScreen());
    }

}

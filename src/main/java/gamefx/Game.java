package gamefx;

import gamefx.multiplayer.Host;
import gamefx.objects.*;
import gamefx.objects.Object;
import gamefx.rendering.Renderer;
import gamefx.util.Quaternion;
import javafx.animation.AnimationTimer;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

public class Game
{
    protected Renderer renderer;
    protected Stage stage;
    protected GameKey gameKey;
    protected Clock clock;
    protected volatile boolean stop = false;
    protected Player mainPlayer;
    protected Cam cam;
    public Player getMainPlayer() {
        return mainPlayer;
    }
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
        clock = new Clock(this);
        //objects
        objects = new ArrayList<>();
    }
    public void init(String name){
        stage.setOnCloseRequest(windowEvent -> {
            setStop();
            Main.setClose();
            windowEvent.consume();
            if(!clock.isRunning()){
                stop();
            }
        });
        gameKey.addHandlers();
        mainPlayer = new Player(name,new double[3]);
        cam = new Cam(mainPlayer.head,new double[]{0,8,0});
        //testobjects
        objects.add(new PlaneObj(new double[]{0,0,0},500));
        objects.add(new Block(new double[]{200, 32, 0}, 64));
        objects.add(new Block(new double[]{100, 16, 0}, 32));
        //stressInit2(objects,1000);

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


    public void start(){
        renderer.setCursor(Cursor.NONE);
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
        movePlayer(getMainPlayer(),pressed,gameKey.getMovedX(),gameKey.getMovedY());
        gameKey.resetMouse();
        processReleased();
        processTestBlocks(pressed, gameKey.getReleased());
        handlePause();
    }
    public void stop(){
        clock.stop();
        System.out.println("Stopped");
        stage.setScene(null);
        Main.tryClose();
    }
    protected void movePlayer(Player player,int pressed,int mX,int mY){
        double x = (((pressed >>> GameKey.Inputs.FORWARD.ordinal()) & 1) - ((pressed >>> GameKey.Inputs.BACKWARDS.ordinal()) & 1)) * deltatime * 100;
        double y = 0;
        double z = (((pressed >>> GameKey.Inputs.LEFT.ordinal()) & 1) - ((pressed >>> GameKey.Inputs.RIGHT.ordinal()) & 1)) * deltatime * 100;
        if ((x != 0) || (y != 0) || (z != 0)) {
            player.move(x, y, z);
        }
        player.rotateAngle(mX*0.001,0,1,0);
        if (mY != 0) {
            Quaternion rot = player.head.getRot();
            rot.multiply(Quaternion.fromAngle(mY*0.001, 0, 0, -1));
            rot.setW(Math.max(rot.getW(), 0.7071067812));
            rot.setK(Math.max(Math.min(rot.getK(), 0.7071067812), -0.7071067812));
        }
    }
    protected void processReleased(){
        if((gameKey.getReleased()&1<< GameKey.Inputs.FULLSCREEN.ordinal()) != 0){
            toggleFullscreen();
            gameKey.unset(GameKey.Inputs.FULLSCREEN);
        }
        if((gameKey.getReleased()&1<< GameKey.Inputs.PERSPECTIVE.ordinal()) != 0){
            System.out.println(cam.togglePerspective());
            gameKey.unset(GameKey.Inputs.PERSPECTIVE);
        }
    }
    protected void processTestBlocks(int pressed,int released){
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
            objects.add(b);
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
    protected final EventHandler<KeyEvent> pauseHandler = new EventHandler<>() {
        @Override
        public void handle(KeyEvent e) {
            if (GameKey.Inputs.PAUSE == gameKey.getInput(e.getCode())) {
                renderer.removeEventFilter(KeyEvent.KEY_RELEASED, this);
                e.consume();
                gameKey.addHandlers();
                clock.start();
                renderer.setCursor(Cursor.NONE);
            }
        }
    };
    protected void handlePause(){
        if((gameKey.getReleased()&1<< GameKey.Inputs.PAUSE.ordinal()) != 0){
            gameKey.unset(GameKey.Inputs.PAUSE);
            gameKey.removeHandlers();
            renderer.addEventFilter(KeyEvent.KEY_RELEASED,pauseHandler);
            clock.stop();
            renderer.setCursor(Cursor.DEFAULT);

        }
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

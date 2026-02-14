package gamefx;

import javafx.animation.AnimationTimer;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Game
{
    private Renderer renderer;
    private Stage stage;
    private GameKey gameKey;
    private AnimationTimer clock;
    private boolean stop = false;

    private static Game Instance;
    public static Game getInstance(){
        if(Instance == null){
        Instance = new Game();
        }
        return Instance;
    }
    private Player mainPlayer;
    public Player getMainPlayer() {
        return mainPlayer;
    }
    public PlaneObj plane;
    public ArrayList<Object> objects;


    public Stage getStage() {
        return stage;
    }

    private Game() {
       System.out.println("Start");
    }
    public void init(Stage stage){
        this.stage = stage;
        renderer = new Renderer(new StackPane());
        //input
        gameKey = new GameKey();
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setOnCloseRequest(windowEvent -> {
            stop();
            windowEvent.consume();
        });
        clock = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update();
                renderer.repaint();
                if(stop){
                    clock.stop();
                    System.out.println("Stopped");
                    System.exit(0);
                }
            }
        };
        //objects
        objects = new ArrayList<>();
        mainPlayer = new Player(new double[3]);
        //testobjects
        plane = new PlaneObj(new double[]{0,30,0},500);
        objects.add(new Block(new double[]{200, 0, 0}, 30));
        objects.add(new Block(new double[]{100, 0, 0}, 30));
        stressInit2(objects,1000);

    }

    private void stressInit2(ArrayList<Object> objects,int max){
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
    public void start(){
        clock.start();
        renderer.addEventHandler(InputEvent.ANY,gameKey);
    }

    public double getDeltatime() {
        return deltatime;
    }

    private double deltatime;
    public long timeNano;
    public void update() {
        long oldtime = timeNano;
        timeNano = System.nanoTime();
        deltatime = (timeNano - oldtime) / 1_000_000_000.0;
        //TODO: finish key recognision
        int pressed = gameKey.getPressed();
        int released = gameKey.getReleased();
        double x = (((pressed >>> GameKey.Inputs.FORWARD.ordinal())&1)-((pressed >>>GameKey.Inputs.BACKWARDS.ordinal())&1))*deltatime*100;
        double y = 0;
        double z = (((pressed >>>GameKey.Inputs.RIGHT.ordinal())&1)-((pressed >>>GameKey.Inputs.LEFT.ordinal())&1))*deltatime*100;
        if((x != 0) || (y != 0) || (z != 0)){
            getMainPlayer().move(x,y,z);
        }
        double rotz =(((pressed >>> GameKey.Inputs.DOWN.ordinal())&1)-((pressed >>>GameKey.Inputs.UP.ordinal())&1));
        double rotx = 0;
        double roty =(((pressed >>> GameKey.Inputs.TLEFT.ordinal())&1)-((pressed >>>GameKey.Inputs.TRIGHT.ordinal())&1));
        if((rotz != 0) || (rotx != 0) || (roty != 0)){
            getMainPlayer().rotateAngle(deltatime*5,rotx,roty,rotz);
        }
        if((released&1<< GameKey.Inputs.FULLSCREEN.ordinal()) != 0){
            toggleFullscreen();
            gameKey.unset(GameKey.Inputs.FULLSCREEN);
        }
        if((released&1<< GameKey.Inputs.SPAWN.ordinal()) != 0){
            //TODO: Spawn Block in front of the Player copying player rotation
            gameKey.unset(GameKey.Inputs.SPAWN);
        }

    }
    public void stop(){
        if(!stop) {
            System.out.println("Stopping...");
            stop = true;
        }
    }
    public void setFullscreen(boolean x){
        stage.setFullScreen(x);
    }
    public void toggleFullscreen(){
        stage.setFullScreen(!stage.isFullScreen());
    }

}

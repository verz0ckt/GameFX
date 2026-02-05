package gamefx;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;

public class Game
{
    private Renderer renderer;
    private Stage stage;
    private GameKey gameKey;
    private boolean stop = false;

    private static Game Instance = new Game();
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
    private PlaneObj plane;
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
        //objects
        objects = new ArrayList<>();
        mainPlayer = new Player(new double[3]);
        //testobjects
        plane = new PlaneObj(new double[]{0,30,0},500);
        objects.add(plane);
        objects.add(new Block(new double[]{200, 0, 0}, 30));
        //stressInit2(objects);

    }

    private void stressInit2(ArrayList<Object> objects){
        for(int i = 0;i<10000;i++) {
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
        new Thread(() -> {
            timeNano = System.nanoTime();
            while (!stop){
                update();
            }
            System.out.println("Stopped");
            System.exit(0);
        }).start();
        renderer.addEventHandler(InputEvent.ANY,gameKey);
        renderer.startRendering();
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
        int triggered = gameKey.getTriggered();
        double x = (((triggered>>> GameKey.Inputs.FORWARD.ordinal())&1)-((triggered>>>GameKey.Inputs.BACKWARDS.ordinal())&1))*deltatime*100;
        double y = 0;
        double z = (((triggered>>>GameKey.Inputs.RIGHT.ordinal())&1)-((triggered>>>GameKey.Inputs.LEFT.ordinal())&1))*deltatime*100;
        if((x != 0) || (y != 0) || (z != 0)){
            getMainPlayer().move(x,y,z);
        }
        double rotz =(((triggered>>> GameKey.Inputs.DOWN.ordinal())&1)-((triggered>>>GameKey.Inputs.UP.ordinal())&1))*deltatime*5;
        double rotx = 0;
        double roty =(((triggered>>> GameKey.Inputs.TLEFT.ordinal())&1)-((triggered>>>GameKey.Inputs.TRIGHT.ordinal())&1))*deltatime*5;
        if((rotz != 0) || (rotx != 0) || (roty != 0)){
            getMainPlayer().rotate(rotx,roty,rotz);
        }
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    public void stop(){
        if(!stop) {
            System.out.println("Stopping...");
            renderer.stopRendering();
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

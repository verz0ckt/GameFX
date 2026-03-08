package gamefx;



import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.robot.Robot;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;


public class GameKey implements EventHandler<InputEvent> {
    private Scene scene;
    private Robot robot;

    public enum Inputs{
        FORWARD,
        BACKWARDS,
        LEFT,
        RIGHT,
        SPAWN,
        FULLSCREEN,
        PERSPECTIVE,
        PAUSE,
        B1,B2,B3,B4,
        OTHER;
    }
    public GameKey(Scene s){
        this.scene = s;
        this.robot = new Robot();
        initKeybinds();
    }
    public void addHandlers(){
        //TODO: separate handlers
        scene.addEventHandler(InputEvent.ANY,this);
    }
    public void removeHandlers(){
        scene.removeEventHandler(InputEvent.ANY,this);
    }

    public final HashMap<Integer, Inputs> KEYBINDS = new HashMap<>();

    private void initKeybinds(){
        KEYBINDS.put(getCode(KeyCode.W),Inputs.FORWARD);
        KEYBINDS.put(getCode(KeyCode.S),Inputs.BACKWARDS);
        KEYBINDS.put(getCode(KeyCode.A),Inputs.LEFT);
        KEYBINDS.put(getCode(KeyCode.D),Inputs.RIGHT);
        KEYBINDS.put(getCode(KeyCode.F11),Inputs.FULLSCREEN);
        KEYBINDS.put(getCode(KeyCode.F3),Inputs.PERSPECTIVE);
        KEYBINDS.put(getCode(KeyCode.ESCAPE),Inputs.PAUSE);
        KEYBINDS.put(getCode(MouseButton.PRIMARY),Inputs.SPAWN);
        KEYBINDS.put(getCode(KeyCode.UP),Inputs.B1);
        KEYBINDS.put(getCode(KeyCode.DOWN),Inputs.B2);
        KEYBINDS.put(getCode(KeyCode.LEFT),Inputs.B3);
        KEYBINDS.put(getCode(KeyCode.RIGHT),Inputs.B4);
    }
    private static int getCode(MouseButton m){
        return m.ordinal()+600;
    }
    private static int getCode(KeyCode k){
        return k.getCode();
    }
    public Inputs getInput(MouseButton m){
        return KEYBINDS.getOrDefault(getCode(m),Inputs.OTHER);
    }
    public Inputs getInput(KeyCode k){
        return KEYBINDS.getOrDefault(getCode(k),Inputs.OTHER);
    }


    private int pressed = 0;
    private int released = 0;
    private int movedX = 0;
    private int movedY = 0;

    public int getReleased() {
        return released;
    }

    public int getPressed() {
        return pressed;
    }

    public int getMovedX() {
        return movedX;
    }

    public int getMovedY() {
        return movedY;
    }
    public void resetMouse(){
        movedY = movedX = 0;
    }

    public void unset(Inputs input){
        released &= ~(1 << input.ordinal());
    }

    @Override
    public void handle(InputEvent event) {
        //System.out.println(event);
        switch (event.getEventType().getName()){
            case "KEY_PRESSED"->{
                KeyEvent keyEvent = (KeyEvent) event;
                pressed |= 1 << KEYBINDS.getOrDefault(getCode(keyEvent.getCode()),Inputs.OTHER).ordinal();
            }
            case "KEY_RELEASED"->{
                KeyEvent keyEvent = (KeyEvent) event;
                pressed &= ~(1 << KEYBINDS.getOrDefault(getCode(keyEvent.getCode()),Inputs.OTHER).ordinal());
                released |= 1 << KEYBINDS.getOrDefault(getCode(keyEvent.getCode()),Inputs.OTHER).ordinal();
            }
            case "MOUSE_PRESSED"->{
                MouseEvent mouseEvent = (MouseEvent) event;
                pressed |= 1 << KEYBINDS.getOrDefault(getCode(mouseEvent.getButton()),Inputs.OTHER).ordinal();
            }
            case "MOUSE_RELEASED"->{
                MouseEvent mouseEvent = (MouseEvent) event;
                pressed &= ~(1 << KEYBINDS.getOrDefault(getCode(mouseEvent.getButton()),Inputs.OTHER).ordinal());
                released |= 1 << KEYBINDS.getOrDefault(getCode(mouseEvent.getButton()),Inputs.OTHER).ordinal();
            }
            case "MOUSE_MOVED","MOUSE_DRAGGED"->{
                MouseEvent mouseEvent = (MouseEvent) event;
                double midX = scene.getWidth()/2;
                double midY = scene.getHeight()/2;
                movedX += (int) (mouseEvent.getSceneX()-midX);
                movedY += (int) (mouseEvent.getSceneY()-midY);
                robot.mouseMove(scene.getX()+scene.getWindow().getX()+midX,scene.getY()+scene.getWindow().getY()+midY);

            }
            default -> {
                event.consume();
            }
        }
    }
}

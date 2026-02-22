package gamefx;



import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.*;

import java.util.HashMap;


public class GameKey implements EventHandler<InputEvent> {
    private Scene scene;

    public enum Inputs{
        FORWARD,
        BACKWARDS,
        LEFT,
        RIGHT,
        UP,
        DOWN,
        TLEFT,
        TRIGHT,
        SPAWN,
        FULLSCREEN,
        PERSPECTIVE,
        B1,B2,B3,B4,
        OTHER;
    }
    public GameKey(Scene s){
        this.scene = s;
        initKeybinds();
    }
    public void addHandlers(){
        //TODO: separate handlers
        scene.addEventHandler(InputEvent.ANY,this);
    }

    public final HashMap<Integer, Inputs> KEYBINDS = new HashMap<>();

    private void initKeybinds(){
        KEYBINDS.put(getCode(KeyCode.W),Inputs.FORWARD);
        KEYBINDS.put(getCode(KeyCode.S),Inputs.BACKWARDS);
        KEYBINDS.put(getCode(KeyCode.A),Inputs.LEFT);
        KEYBINDS.put(getCode(KeyCode.D),Inputs.RIGHT);
        KEYBINDS.put(getCode(KeyCode.UP),Inputs.UP);
        KEYBINDS.put(getCode(KeyCode.DOWN),Inputs.DOWN);
        KEYBINDS.put(getCode(KeyCode.LEFT),Inputs.TLEFT);
        KEYBINDS.put(getCode(KeyCode.RIGHT),Inputs.TRIGHT);
        KEYBINDS.put(getCode(KeyCode.F11),Inputs.FULLSCREEN);
        KEYBINDS.put(getCode(KeyCode.F3),Inputs.PERSPECTIVE);
        KEYBINDS.put(getCode(MouseButton.PRIMARY),Inputs.SPAWN);
        KEYBINDS.put(getCode(KeyCode.I),Inputs.B1);
        KEYBINDS.put(getCode(KeyCode.K),Inputs.B2);
        KEYBINDS.put(getCode(KeyCode.J),Inputs.B3);
        KEYBINDS.put(getCode(KeyCode.L),Inputs.B4);
    }
    public static int getCode(MouseButton m){
        return m.ordinal()+600;
    }
    public static int getCode(KeyCode k){
        return k.getCode();
    }

    private int pressed = 0;
    private int released = 0;

    public int getReleased() {
        return released;
    }

    public int getPressed() {
        return pressed;
    }

    public void unset(Inputs input){
        released &= ~(1 << input.ordinal());
    }

    @Override
    public void handle(InputEvent event) {
        //System.out.println(event.getEventType().getName());
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
            default -> {
                event.consume();
            }
        }
    }
}

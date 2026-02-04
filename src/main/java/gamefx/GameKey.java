package gamefx;



import javafx.event.EventHandler;
import javafx.scene.input.*;

import java.util.HashMap;
import java.util.Map;


public class GameKey implements EventHandler<InputEvent> {

    public enum Inputs{
        FORWARD,
        BACKWARDS,
        LEFT,
        RIGHT,
        FULLSCREEN,
        UP,
        DOWN,
        TLEFT,
        TRIGHT,
        SPAWN,
        OTHER;
    }
    public GameKey(){
    }

    public final HashMap<Integer, Inputs> KEYBINDS = new HashMap<>(Map.of(getCode(KeyCode.W),Inputs.FORWARD, getCode(KeyCode.S),Inputs.BACKWARDS,
            getCode(KeyCode.A),Inputs.LEFT,getCode(KeyCode.D),Inputs.RIGHT,getCode(KeyCode.F11),Inputs.FULLSCREEN,
            getCode(KeyCode.UP),Inputs.UP,getCode(KeyCode.DOWN),Inputs.DOWN,
            getCode(KeyCode.LEFT),Inputs.TLEFT,getCode(KeyCode.RIGHT),Inputs.TRIGHT,
            getCode(MouseButton.PRIMARY),Inputs.SPAWN));

    public static int getCode(MouseButton m){
        return m.ordinal()+600;
    }
    public static int getCode(KeyCode k){
        return k.getCode();
    }

    private int triggered = 0;


    public int getTriggered() {
        return triggered;
    }

    @Override
    public void handle(InputEvent event) {
        System.out.println(event.getEventType().getName());
        switch (event.getEventType().getName()){
            case "KEY_PRESSED"->{
                KeyEvent keyEvent = (KeyEvent) event;
                triggered |= 1 << KEYBINDS.getOrDefault(getCode(keyEvent.getCode()),Inputs.OTHER).ordinal();
            }
            case "KEY_RELEASED"->{
                KeyEvent keyEvent = (KeyEvent) event;
                triggered &= ~(1 << KEYBINDS.getOrDefault(getCode(keyEvent.getCode()),Inputs.OTHER).ordinal());
            }
            case "MOUSE_PRESSED"->{
                MouseEvent mouseEvent = (MouseEvent) event;
                triggered |= 1 << KEYBINDS.getOrDefault(getCode(mouseEvent.getButton()),Inputs.OTHER).ordinal();
            }
            case "MOUSE_RELEASED"->{
                MouseEvent mouseEvent = (MouseEvent) event;
                triggered &= ~(1 << KEYBINDS.getOrDefault(getCode(mouseEvent.getButton()),Inputs.OTHER).ordinal());
            }
            default -> {
                event.consume();
            }
        }
    }
}

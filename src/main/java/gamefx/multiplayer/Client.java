package gamefx.multiplayer;

import gamefx.Game;
import gamefx.Main;
import gamefx.objects.Cam;
import gamefx.objects.Player;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;

public class Client extends Game {


    public Client(Stage stage,String host,int port) {
        super(stage);
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
    public Player startFetch(String name){
        addOtherPlayer(new Player("Main",new double[3]));
        return new Player(name,new double[3]);
    }

    @Override
    public void update() {
        super.update();
    }
}

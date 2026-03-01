package gamefx;

import gamefx.multiplayer.Client;
import gamefx.multiplayer.Host;
import gamefx.objects.Block;
import gamefx.objects.Object;
import gamefx.util.Quaternion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class Main extends Application {
    private static Main instance;
    static Game game;
    private static boolean close = false;
    private static Stage mainStage;

    /// debug
    //0= normal;1= server; 2= client;
    private void testStart(int mode){
        switch (mode){
            case 1->startHost("main",42069);
            case 2->startClient("other","localhost",42069);
            default -> startGame("main");
        }
    }
    public static int debug = -1;
    public static void main(String[] args) {
        debug = Integer.parseInt(args[0].trim());
        launch();
    }
    /// debug end

    public static Main getInstance() {
        return instance;
    }

    public static Game getGame() {
        return game;
    }

    @Override
    public void start(Stage stage) throws Exception {
        instance = this;
        mainStage = stage;
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.show();

        if(debug == -1){ //debug
            startMainMenu();
        }else {
            testStart(debug); //debug
        }
    }
    protected static void startMainMenu() throws Exception{
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainMenu.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 600);
        mainStage.setScene(scene);
    }
    protected static void startHost(String name,int port){
        if(name.length() >16){
            name = name.substring(0,16);
        }
        game = new Host(mainStage,port);
        game.init(name);
        mainStage.setScene(game.getRenderer());
        game.start();
    }
    protected static void startClient(String name,String hostName,int port){
        if(name.length() >20){
            name = name.substring(0,20);
        }
        game = new Client(mainStage,hostName,port);
        game.init(name);
        mainStage.setScene(game.getRenderer());
        game.start();
    }
    protected static void startGame(String name) {
        game = new Game(mainStage);
        game.init(name);
        mainStage.setScene(game.getRenderer());
        game.start();
        mainStage.close();
    }

    public static void tryClose() {
        if (close) {
            System.out.println("closing");
            mainStage.close();
            return;
        }
        try {
            startMainMenu();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setClose() {
        close = true;
    }
}

package gamefx;

import gamefx.multiplayer.Client;
import gamefx.multiplayer.Host;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class Main extends Application {
    private static Main instance;
    private static Game game;
    private static boolean close = false;
    private static Stage mainStage;

    /// debug
    private final int MODE = 1; //0= normal;1= server; 2= client;
    private void testStart(){
        switch (MODE){
            case 1->startMultiplayer("main","localhost:42069",false);
            case 2->startMultiplayer("other",":42069",true);
            default -> startGame("main");
        }
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
        startMainMenu();
        testStart(); //debug
    }
    protected static void startMainMenu() throws Exception{
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainMenu.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 600);
        mainStage.setScene(scene);
    }
    protected static void startMultiplayer(String name,String host,boolean isHost){
        //parse hostName
        String hostName = "localhost";
        int port = 42069;
        if(isHost){
            game = new Host(mainStage,port);
        }else {
            game = new Client(mainStage,hostName,port);
        }
        game.init(name);
        mainStage.setScene(game.getRenderer());
        game.start();
    }
    protected static void startGame(String name) {
        game = new Game(mainStage);
        game.init(name);
        mainStage.setScene(game.getRenderer());
        game.start();
    }

    public static void tryClose() {
        if (close) {
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

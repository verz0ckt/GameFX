package gamefx;

import javafx.application.Application;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class Main extends Application {
    private static Main instance;
    private static Game game;
    private static boolean close = false;
    private static Stage mainStage;


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
        stage.setScene(new MainMenu());
        stage.show();
    }

    protected static void startGame() {
        game = new Game();
        game.init(mainStage);
        mainStage.setScene(game.getRenderer());
        //mainStage.show();
        game.start();
    }

    protected static void tryClose() {
        if (close) {
            mainStage.close();
            return;
        }
        mainStage.setScene(new MainMenu());

    }

    protected static void setClose() {
        close = true;
    }
}

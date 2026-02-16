package gamefx;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private static Main instance;
    private boolean close = false;
    private Stage mainStage;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) throws Exception {
        instance = this;
        mainStage = stage;
        stage.setScene(new MainMenu());
        stage.show();
    }

    public void startGame() {
        Game g = Game.create();
        g.init(mainStage);
        mainStage.setScene(g.getRenderer());
        mainStage.show();
        g.start();
    }

    public void tryClose() {
        if (close) {
            mainStage.close();
        }
    }

    public void setClose() {
        close = true;
    }
}

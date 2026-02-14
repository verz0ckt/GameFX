package gamefx;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Game g = Game.getInstance();
        g.init(stage);
        stage.setScene(g.getRenderer());
        stage.show();
        g.start();
    }

    public static void main(String[] args) {
        launch(args);
        System.out.println("Done");
    }
}

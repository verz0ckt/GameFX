package gamefx;

import javafx.animation.AnimationTimer;

public class Clock extends AnimationTimer {
    private volatile boolean running = false;
    private Game game;

    public Clock(Game game) {
        this.game = game;
    }

    @Override
    public void start() {
        super.start();
        running = true;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void handle(long l) {
        game.update();
        game.renderer.repaint();
        if(game.stop){
            game.stop();
        }
    }

    @Override
    public void stop() {
        super.stop();
        running = false;
    }
}

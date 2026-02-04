package gamefx;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;


public class Renderer extends Scene {
    private Canvas canvas;
    private AnimationTimer clock;
    private double fow = 300;//500
	private int near = 1;
    private int far = 500;

    public double midX;
    public double midY;
    public double sizeMultX;
    public double sizeMultY;

    public Canvas getCanvas() {
        return canvas;
    }

    public Renderer(StackPane root) {

        super(root);
        canvas = new Canvas();
        canvas.widthProperty().bind(
                root.widthProperty());
        canvas.heightProperty().bind(
                root.heightProperty());
        root.getChildren().add(canvas);
        clock = new AnimationTimer() {
            @Override
            public void handle(long l) {
                repaint();
            }
        };
    }

    public int getNear() {
        return near;
    }

    public int getFar() {
        return far;
    }

    public double distanceFromViewPort(double[] point) {
        double dx = point[0] - Game.getInstance().getMainPlayer().getPos()[0];
        double dy = point[1] - Game.getInstance().getMainPlayer().getPos()[1];
        double dz = point[2] - Game.getInstance().getMainPlayer().getPos()[2];
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public void setFow(double fow) {
        this.fow = fow;
    }

    public double getFow() {
        return fow;
    }

    public void repaint() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        setMid();
        Game.getInstance().getMainPlayer().getModel().draw(g);
        for (Object o : Game.getInstance().objects) {
            o.getModel().draw(g);
        }
    }
    public void getRotation(double[] rotation,double[] point) {
        double Z = point[2];
        double Y = point[1];
        double X = point[0];
        double sinZ = Math.sin(rotation[2]);
        double sinY = Math.sin(rotation[1]);
        double sinX = Math.sin(rotation[0]);

        double cosZ = Math.cos(rotation[2]);
        double cosY = Math.cos(rotation[1]);
        double cosX = Math.cos(rotation[0]);
        point[2] = cosY * (sinX * Y + cosX * Z) - sinY * X;
        double v = cosY * X + sinY * (sinX * Y + cosX * Z);
        point[1] = sinZ * v + cosZ * (cosX * Y - sinX * Z);
        point[0] = cosZ * v - sinZ * (cosX * Y - sinX * Z);
    }
    public void getNegRot(double[] rotation,double[] point) {
        double Z = point[2];
        double Y = point[1];
        double X = point[0];
        double sinZ = Math.sin(-rotation[2]);
        double sinY = Math.sin(-rotation[1]);
        double sinX = Math.sin(-rotation[0]);

        double cosZ = Math.cos(-rotation[2]);
        double cosY = Math.cos(-rotation[1]);
        double cosX = Math.cos(-rotation[0]);
        point[2] = cosY * (sinX * Y + cosX * Z) - sinY * X;
        double v = cosY * X + sinY * (sinX * Y + cosX * Z);
        point[1] = sinZ * v + cosZ * (cosX * Y - sinX * Z);
        point[0] = cosZ * v - sinZ * (cosX * Y - sinX * Z);
    }
    public void setRelToCam(double[] point){
        point[0] = point[0] - Game.getInstance().getMainPlayer().getPos()[0];
        point[1] = point[1] - Game.getInstance().getMainPlayer().getPos()[1];
        point[2] = point[2] - Game.getInstance().getMainPlayer().getPos()[2];
    }
    public double[] getProjection(double[] point,double fow){
        double[] proj = new double[2];
        proj[0] = fow/point[0]*point[2];
        proj[1] = fow/point[0]*point[1];
        return proj;
    }
    public void adjustToScreen(double[] v){
        v[0] = v[0]*sizeMultX+midX;
        v[1] = v[1]*sizeMultY+midY;
    }
    public double[] getCutLine(Point p1, Point p2,double cut) {
        double[] cord1 = p1.getCampos();
        double[] cord2 = p2.getCampos();

        double t = (cut - cord1[0]) / (cord2[0] - cord1[0]);
        double z = cord1[2] + t * (cord2[2] - cord1[2]);
        double y = cord1[1] + t * (cord2[1] - cord1[1]);
        return new double[]{cut,y, z};
    }
    public void setMid() {
        midX = getWidth()/2;
        midY = getHeight()/2;
        Game.getInstance().getRenderer().sizeMultX = 1;
        Game.getInstance().getRenderer().sizeMultY = 1;
    }
    public void startRendering(){
        clock.start();
    }
    public void stopRendering(){
        clock.stop();
    }
}
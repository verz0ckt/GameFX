package gamefx;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;


public class Renderer extends Scene {
    private Canvas canvas;
    private double fow = 300;//500
	private int near = 1;
    private int far = 500;
    private Game game;

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
        game = Game.getInstance();
        canvas.getGraphicsContext2D().setImageSmoothing(false);

    }
    private Quaternion camrot = Quaternion.fromEuler(0,0,0,0);

    public Quaternion getCamrot() {
        return camrot;
    }

    public int getNear() {
        return near;
    }

    public int getFar() {
        return far;
    }

    public double distanceFromViewPort(double[] point) {
        double[] pos = game.getMainPlayer().getPos();
        double dx = point[0] - pos[0];
        double dy = point[1] - pos[1];
        double dz = point[2] - pos[2];
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    public double distanceFromViewPortSqared(double[] point) {
        double[] pos = game.getMainPlayer().getPos();
        double dx = point[0] - pos[0];
        double dy = point[1] - pos[1];
        double dz = point[2] - pos[2];
        return dx * dx + dy * dy + dz * dz;
    }

    public void setFow(double fow) {
        this.fow = fow;
    }

    public double getFow() {
        return fow;
    }

    public void repaint() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        setMid();
        Quaternion playerrot = game.getMainPlayer().getRot();
        camrot.i = -playerrot.i;
        camrot.j = -playerrot.j;
        camrot.k = -playerrot.k;
        camrot.w = playerrot.w;
        camrot.multiplyGlobal(Quaternion.fromAngle(0,0,-1,game.getMainPlayer().pitch));
        camrot.tryNormalize();
        game.plane.getModel().draw(gc);
        sortObjects(game.objects);
        for (Object o : game.objects) {
            o.getModel().draw(gc);
        }
        game.getMainPlayer().getModel().draw(gc);
        System.out.println("done");
    }
    public void sortObjects(ArrayList<Object> objects){
        for(Object o: objects){
            o.distance = distanceFromViewPortSqared(o.pos);
        }
        objects.sort((o1,o2)-> (o1.distance<o2.distance)?1:-1);
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
}
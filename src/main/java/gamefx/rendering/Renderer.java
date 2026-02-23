package gamefx.rendering;

import gamefx.Game;
import gamefx.Main;
import gamefx.util.Matrix;
import gamefx.objects.Object;
import gamefx.util.Quaternion;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

import java.util.ArrayList;


public class Renderer extends Scene {
    private final Canvas canvas;
    private double focalLength = -850;
	private double near = 1;
    private double far = 500;
    private final Game game;

    private double midX;
    private double midY;
    private double maxHeight;
    private double maxWidth;
    private double sizeMultHeight;
    private double sizeMultWidth;

    public Canvas getCanvas() {
        return canvas;
    }

    public Renderer(Game game) {
        super(new StackPane());
        this.game = game;
        StackPane root = (StackPane) getRoot();
        canvas = new Canvas();
        canvas.widthProperty().bind(
                root.widthProperty());
        canvas.heightProperty().bind(
                root.heightProperty());
        root.getChildren().add(canvas);
        canvas.getGraphicsContext2D().setImageSmoothing(false);
        maxHeight = Screen.getPrimary().getBounds().getHeight();
        maxWidth = Screen.getPrimary().getBounds().getWidth();
    }
    //projection
    private final Quaternion camRot = Quaternion.zeroRot();
    private final double[] camPos = new double[3];
    private final Matrix camRotationMatrix = new Matrix();

    public Quaternion getCamRot() {
        return camRot;
    }
    public Matrix getCamRotationMatrix() {
        return camRotationMatrix;
    }

    public double getNear() {
        return near;
    }

    public double getFar() {
        return far;
    }


    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength;
    }

    public double getFocalLength() {
        return focalLength;
    }

    public void repaint() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        setMid();
        game.getCam().update();
        camRot.conjugateOf(game.getCam().getRenderingQuaternion());
        camRot.tryNormalize();
        camRotationMatrix.fromQuaternion(camRot);
        double[] camOffset = game.getCam().getAbsPos();
        camPos[0] = camOffset[0];
        camPos[1] = camOffset[1];
        camPos[2] = camOffset[2];
        game.plane.getModel().draw(gc);
        sortObjects(game.objects);
        for (Object o : game.objects) {
            o.getModel().draw(gc);
        }
        if(game.getCam().getPerspective() >1){
            game.getMainPlayer().getModel().draw(gc);
        }
    }
    @Deprecated
    public double distanceFromViewPortSqared(double[] point) {
        double dx = point[0] - camPos[0];
        double dy = point[1] - camPos[1];
        double dz = point[2] - camPos[2];
        return dx * dx + dy * dy + dz * dz;
    }

    @Deprecated
    public void sortObjects(ArrayList<Object> objects){
        //TODO: sort Planes instead
        for(Object o: objects){
            o.distance = distanceFromViewPortSqared(o.getPos());
        }
        objects.sort((o1,o2)-> (o1.distance<o2.distance)?1:-1);
    }
    public void setRelToCam(double[] point){
        point[0] -= camPos[0];
        point[1] -= camPos[1];
        point[2] -= camPos[2];
    }
    public void getProjection(double[] proj,double[] point,double fov){
        proj[0] = fov/point[0]*point[2];
        proj[1] = fov/point[0]*point[1];
    }
    public void adjustToScreen(double[] v){
        v[0] = v[0]* sizeMultWidth +midX;
        v[1] = v[1]* sizeMultHeight +midY;
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
        sizeMultHeight = getHeight()/maxHeight;
        sizeMultWidth = getWidth()/maxWidth;
    }
}
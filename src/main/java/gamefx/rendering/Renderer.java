package gamefx.rendering;

import gamefx.Game;
import gamefx.util.Matrix;
import gamefx.objects.Object;
import gamefx.util.Quaternion;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;


public class Renderer extends Scene {
    private final WritableImage image;
    private final PixelBuffer<ByteBuffer> pixelBuffer;
    private final ByteBuffer buffer;
    private final ByteBuffer zbuffer;
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

    public WritableImage getImage() {
        return image;
    }

    public Renderer(Game game) {
        super(new StackPane());
        this.game = game;
        StackPane root = (StackPane) getRoot();
        maxHeight = Screen.getPrimary().getBounds().getHeight();
        maxWidth = Screen.getPrimary().getBounds().getWidth();
        int size = ((int)maxHeight)*((int)maxWidth);
        buffer = ByteBuffer.allocateDirect(size*4);
        pixelBuffer = new PixelBuffer<>((int)maxWidth,(int)maxHeight,buffer, PixelFormat.getByteBgraPreInstance());
        zbuffer = ByteBuffer.allocateDirect(size);
        image = new WritableImage(pixelBuffer);
        ImageView view = new ImageView(image);
        view.fitWidthProperty().bind(
                root.widthProperty());
        view.fitHeightProperty().bind(
                root.heightProperty());
        root.getChildren().add(view);
        view.setSmooth(false);
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
        for (int i = 0; i < buffer.capacity(); i += 4) {
            buffer.put(i, (byte) 0);
            buffer.put(i + 1, (byte) 0);
            buffer.put(i + 2, (byte) 0);
            buffer.put(i + 3, (byte) 255);
        }

        game.getCam().update();
        camRot.conjugateOf(game.getCam().getRenderingQuaternion());
        camRot.tryNormalize();
        camRotationMatrix.fromQuaternion(camRot);
        double[] camOffset = game.getCam().getAbsPos();
        camPos[0] = camOffset[0];
        camPos[1] = camOffset[1];
        camPos[2] = camOffset[2];
        if (game.getClass() == Game.class) {
            sortObjects(game.objects);
        }
        for (Object o : game.objects) {
            o.getModel().draw(buffer);
        }
        if (game.getCam().getPerspective() > 1) {
            game.getMainPlayer().getModel().draw(buffer);
        }
        pixelBuffer.updateBuffer(_ -> null);
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
    public void getCutLine(Point p1, Point p2,double cut,double[] out) {
        double[] cord1 = p1.getCampos();
        double[] cord2 = p2.getCampos();

        double t = (cut - cord1[0]) / (cord2[0] - cord1[0]);
        out[0] = cut;
        out[1] = cord1[1] + t * (cord2[1] - cord1[1]);
        out[2] = cord1[2] + t * (cord2[2] - cord1[2]);
    }
    public void setMid() {
        midX = getWidth()/2;
        midY = getHeight()/2;
        sizeMultHeight = getHeight()/maxHeight;
        sizeMultWidth = getWidth()/maxWidth;
    }
}
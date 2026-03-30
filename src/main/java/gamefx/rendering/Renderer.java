package gamefx.rendering;

import gamefx.Game;
import gamefx.util.Matrix;
import gamefx.objects.Object;
import gamefx.util.Quaternion;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;

import java.nio.IntBuffer;


public class Renderer extends Scene {
    private final WritableImage image;
    private final PixelBuffer<IntBuffer> pixelBuffer;
    private final IntBuffer buffer;
    private final short[] zbuffer;
    public final int SIZE;
    private double focalLength = -850;
    public final double mY;
    public final double mZ;
	private static final double near = 1;
    private static final double far = 500;
    private final Game game;
    private final Text timeField;

    private final double midX;
    private final double midY;
    public final int maxHeight;
    public final int maxWidth;

    public WritableImage getImage() {
        return image;
    }

    public Renderer(Game game) {
        super(new StackPane());
        this.game = game;
        StackPane root = (StackPane) getRoot();
        timeField = new Text();
        maxHeight = (int) Screen.getPrimary().getBounds().getHeight();
        maxWidth = (int) Screen.getPrimary().getBounds().getWidth();
        midX = (double) maxWidth /2;
        midY = (double) maxHeight /2;
        mY = maxHeight/(focalLength*2);
        mZ = maxWidth/(focalLength*2);
        SIZE = maxHeight*maxWidth;
        buffer = IntBuffer.allocate(SIZE);
        pixelBuffer = new PixelBuffer<>(maxWidth,maxHeight, buffer, PixelFormat.getIntArgbPreInstance());
        zbuffer = new short[SIZE];
        image = new WritableImage(pixelBuffer);
        ImageView view = new ImageView(image);
        view.fitWidthProperty().bind(
                root.widthProperty());
        view.fitHeightProperty().bind(
                root.heightProperty());
        root.getChildren().add(view);

        root.getChildren().add(timeField);
        StackPane.setAlignment(timeField, Pos.TOP_LEFT);
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
        int[] buffer = this.buffer.array();
        timeField.setText(String.valueOf(game.getDeltatime()*1_000_000.0));
        game.getCam().update();
        camRot.conjugateOf(game.getCam().getRenderingQuaternion());
        camRot.tryNormalize();
        camRotationMatrix.fromQuaternion(camRot);
        double[] camOffset = game.getCam().getAbsPos();
        camPos[0] = camOffset[0];
        camPos[1] = camOffset[1];
        camPos[2] = camOffset[2];
        for (int i = 0; i < SIZE; i ++) {
            buffer[i] = 0xFFDDDDDD;
            zbuffer[i] = Short.MIN_VALUE;
        }

        for (Object o : game.objects) {
            o.getModel().draw(buffer,zbuffer);
        }
        if (game.getCam().getPerspective() > 1) {
            game.getMainPlayer().getModel().draw(buffer, zbuffer);
        }

        pixelBuffer.updateBuffer(_->null);
    }

    public void setRelToCam(double[] point){
        point[0] -= camPos[0];
        point[1] -= camPos[1];
        point[2] -= camPos[2];
    }
    public static final double zOffset = Short.MIN_VALUE+far;
    public void getProjection(double[] proj,double[] point,double fov){
        proj[0] = fov/point[0]*point[2];//x
        proj[1] = fov/point[0]*point[1];//y
        proj[2] = -point[0]+zOffset;
    }
    public void adjustToScreen(double[] v){
        v[0] += midX;
        v[1] += midY;
    }
    public void getCutLine(Point p1, Point p2,double cut,double[] out) {
        double[] cord1 = p1.getCampos();
        double[] cord2 = p2.getCampos();

        double t = (cut - cord1[0]) / (cord2[0] - cord1[0]);
        out[0] = cut;
        out[1] = cord1[1] + t * (cord2[1] - cord1[1]);
        out[2] = cord1[2] + t * (cord2[2] - cord1[2]);
    }
}
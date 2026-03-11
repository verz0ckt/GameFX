package gamefx.rendering;

import gamefx.Game;
import gamefx.util.Matrix;
import gamefx.objects.Object;
import gamefx.util.Quaternion;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;


public class Renderer extends Scene {
    private final WritableImage image;
    private volatile PixelBuffer<IntBuffer> activePixelBuffer;
    private volatile PixelBuffer<IntBuffer> renderingPixelBuffer;
    private final ByteBuffer zbuffer;
    private double focalLength = -850;
	private double near = 1;
    private double far = 500;
    private final Game game;

    private double midX;
    private double midY;
    public final int maxHeight;
    public final int maxWidth;

    public WritableImage getImage() {
        return image;
    }

    public Renderer(Game game) {
        super(new StackPane());
        this.game = game;
        StackPane root = (StackPane) getRoot();
        maxHeight = (int) Screen.getPrimary().getBounds().getHeight();
        maxWidth = (int) Screen.getPrimary().getBounds().getWidth();
        midX = (double) maxWidth /2;
        midY = (double) maxHeight /2;
        int size = maxHeight*maxWidth;
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(size*4);
        byteBuffer1.order(ByteOrder.nativeOrder());
        activePixelBuffer = new PixelBuffer<>(maxWidth,maxHeight, byteBuffer1.asIntBuffer(), PixelFormat.getIntArgbPreInstance());
        ByteBuffer byteBuffer2 = ByteBuffer.allocateDirect(size*4);
        byteBuffer2.order(ByteOrder.nativeOrder());
        renderingPixelBuffer = new PixelBuffer<>(maxWidth,maxHeight,byteBuffer2.asIntBuffer(), PixelFormat.getIntArgbPreInstance());
        zbuffer = ByteBuffer.allocateDirect(size);
        image = new WritableImage(activePixelBuffer);
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

    private void switchBuffer(){
        PixelBuffer<IntBuffer> temp = renderingPixelBuffer;
        renderingPixelBuffer = activePixelBuffer;
        activePixelBuffer = temp;
    }

    public void repaint() {
        IntBuffer buffer = activePixelBuffer.getBuffer();
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

        for (int i = 0; i < buffer.capacity(); i ++) {
            buffer.put(i, 0xFFDDDDDD);
        }
        for(int i = 0; i < 200; i ++) {
            buffer.put(i+i*maxWidth,0xFFFF0000);
        }

        for (Object o : game.objects) {
            o.getModel().draw(buffer);
        }
        if (game.getCam().getPerspective() > 1) {
            game.getMainPlayer().getModel().draw(buffer);
        }

        switchBuffer();
        renderingPixelBuffer.updateBuffer(_->null);
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
        proj[0] = fov/point[0]*point[2];//x
        proj[1] = fov/point[0]*point[1];//y
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
package gamefx.objects;


import gamefx.Game;
import gamefx.Main;
import gamefx.util.Matrix;
import gamefx.util.Quaternion;
import gamefx.rendering.Drawable;
import gamefx.rendering.Point;
import gamefx.rendering.Renderer;
import javafx.scene.canvas.GraphicsContext;

import java.util.Arrays;

public abstract class Object {
    protected ObjectModel model;
    protected Object parent;
    private Object[] children;
    public ObjectModel getModel() {
        return model;
    }
    protected double[] pos;
    protected Quaternion rot;
    public double size;
    //only use in rendering;
    protected Quaternion renderingQuaterion;
    protected Matrix rotMatrix;
    protected double[] offset;
    //dont use
    public double distance;

    public Object(Object parent, double[] pos, double size) {
        this.parent = parent;
        this.pos = pos;
        this.rot = Quaternion.zeroRot();
        this.size = size;
        init();
    }
    public Object(Object parent, double[] pos, Quaternion rot, double size) {
        this.parent = parent;
        this.pos = pos;
        this.rot = rot;
        this.size = size;
        init();
    }
    public Object(double[] pos, Quaternion rot, double size) {
        this.pos = pos;
        this.rot = rot;
        this.size = size;
        init();
    }
    public Object(double[] pos,double size) {
        this.pos = pos;
        this.rot = Quaternion.zeroRot();
        this.size = size;
        init();
    }

    public Object[] getChildren() {
        return children;
    }
    public void setChildren(Object[] children) {
        this.children = children;
    }

    private void init(){
        offset = new double[3];
        rotMatrix = new Matrix();
        renderingQuaterion = Quaternion.zeroRot();
    }


    public void addPos(double[] pos) {
        this.pos[0] += pos[0];
        this.pos[1] += pos[1];
        this.pos[2] += pos[2];
    }
    public void addPos(double x, double y,double z) {
        this.pos[0] += x;
        this.pos[1] += y;
        this.pos[2] += z;
    }

    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public double[] getPos() {
        return pos;
    }

    public void setPos(double[] pos) {
        this.pos = pos;
    }

    public Matrix getRenderingRotationMatrix() {
        return rotMatrix;
    }

    public Quaternion getRot() {
        return rot;
    }

    @Deprecated
    public void rotateAngle(double amount,double x,double y, double z){
        rot.multiply(Quaternion.fromAngle(amount,x,y,z));
        rot.tryNormalize();
    }
    public void rotate(double x,double y,double z){
        //TODO: add rotate then delete this
    }

    public void applyOffsetToPoint(double[] pos){
        pos[0] += offset[0];
        pos[1] += offset[1];
        pos[2] += offset[2];
    }

    public abstract class ObjectModel{
        protected Point[] points;
        protected Drawable[] drawable;
        protected Renderer renderer;

        public ObjectModel() {
            this.renderer = Main.getGame().getRenderer();
            
        }
        public void offset(double x,double y,double z){
            for(Point p : points){
                double[] pos = p.getPosition();
                pos[0] += x;
                pos[1] += y;
                pos[2] += z;
            }
        }
        public Object getObject(){
            return Object.this;
        }
        public void draw(GraphicsContext gc){
             if(points != null || children != null) {
                 renderingQuaterion.replaceWith(rot);
                 if (parent == null) {
                     renderingQuaterion.multiplyGlobal(renderer.getCamRot());
                     rotMatrix.fromQuaternion(renderingQuaterion);
                     offset[0] = pos[0];
                     offset[1] = pos[1];
                     offset[2] = pos[2];
                     renderer.setRelToCam(offset);
                     System.out.println(Arrays.toString(offset));
                     renderer.getCamRotationMatrix().apply(offset);

                 } else {
                     renderingQuaterion.multiplyGlobal(parent.renderingQuaterion);
                     rotMatrix.fromQuaternion(renderingQuaterion);
                     offset[0] = pos[0];
                     offset[1] = pos[1];
                     offset[2] = pos[2];
                     parent.renderingQuaterion.apply(offset);
                     parent.applyOffsetToPoint(offset);
                 }
                 if(points!= null){
                    for (Point p : points) {
                         p.project(renderer);
                    }
                 }
                 if(drawable != null){
                    for (Drawable d : drawable) {
                       d.draw(gc);
                    }
                 }
                 if (children != null) {
                     for (Object c : children) {
                         c.getModel().draw(gc);
                     }
                 }
             }
        }
    }

    public String toString() {
        return "Object{" +
                ", pos=" + Arrays.toString(pos) +
                '}';
    }
}

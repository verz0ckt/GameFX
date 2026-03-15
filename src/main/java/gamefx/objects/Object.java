package gamefx.objects;


import gamefx.Main;
import gamefx.util.Matrix;
import gamefx.util.Quaternion;
import gamefx.rendering.Drawable;
import gamefx.rendering.Point;
import gamefx.rendering.Renderer;

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
    protected double size;
    //only use in rendering;
    protected Quaternion renderingQuaterion;
    protected Matrix rotMatrix;
    protected double[] offset;
    //dont use
    @Deprecated
    public double distance;

    //Multiplayer
    public static final char ID = 0;

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
    public Object(double size) {
        this.pos = new double[3];
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
        this.pos[0] = pos[0];
        this.pos[1] = pos[1];
        this.pos[2] = pos[2];
    }
    public void setPos(double x, double y, double z) {
        this.pos[0] = x;
        this.pos[1] = y;
        this.pos[2] = z;
    }

    public Matrix getRenderingRotationMatrix() {
        return rotMatrix;
    }

    public Quaternion getRot() {
        return rot;
    }


    public void rotateAngle(double amount,double v1,double v2, double v3){
        rot.multiply(Quaternion.fromAngle(amount,v1,v2,v3));
        rot.tryNormalize();
    }
    public void rotate(double x,double y,double z){
        Quaternion rot = Quaternion.fromEuler(x,1,0,0);
        rot.multiply(Quaternion.fromEuler(y,0,1,0));
        rot.multiply(Quaternion.fromEuler(z,0,0,1));
        this.rot.multiply(rot);
    }

    public void applyOffsetToPoint(double[] pos){
        pos[0] += offset[0];
        pos[1] += offset[1];
        pos[2] += offset[2];
    }
    //TODO:apply size at the end
    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        double scalar = size/this.size;
        model.scale(scalar,scalar,scalar);
        this.size = size;
    }

    public abstract class ObjectModel{
        protected Point[] points;
        protected Drawable[] drawable;
        protected Renderer renderer;
        protected double maxOffset = -1;

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
            calcMaxOffset();
        }
        public void scale(double x,double y,double z){
            for(Point p : points){
                double[] pos = p.getPosition();
                pos[0] *= x;
                pos[1] *= y;
                pos[2] *= z;
            }
            calcMaxOffset();
        }
        public Object getObject(){
            return Object.this;
        }
        public void calcMaxOffset() {
            double max = 0;
            if(points == null) return;
            for(Point p : points){
                double[] pos = p.getPosition();
                double dist = pos[0]*pos[0]+pos[1]*pos[1]+pos[2]*pos[2];
                if(max < dist){
                    max = dist;
                }
            }
            maxOffset = Math.sqrt(max);
        }
        public void draw(int[] buffer){
             if(points != null || children != null) {
                 renderingQuaterion.replaceWith(rot);
                 if (parent == null) {
                     renderingQuaterion.multiplyGlobal(renderer.getCamRot());
                     rotMatrix.fromQuaternion(renderingQuaterion);
                     offset[0] = pos[0];
                     offset[1] = pos[1];
                     offset[2] = pos[2];
                     renderer.setRelToCam(offset);
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
                 assert maxOffset != -1;
                 if((offset[0]+maxOffset < renderer.getNear()) || offset[0]-maxOffset > renderer.getFar()) {
                     //improve clipping
                     if (children != null) {
                         for (Object c : children) {
                             c.getModel().draw(buffer);
                         }
                     }
                     return;
                 };
                 if(points!= null){
                    for (Point p : points) {
                         p.project(renderer);
                    }
                 }
                 if(drawable != null){
                     drawDrawables(buffer);
                 }
                 if (children != null) {
                     for (Object c : children) {
                         c.getModel().draw(buffer);
                     }
                 }
             }
        }
        protected abstract void drawDrawables(int[] buffer);
    }

    @Override
    public String toString() {
        if(parent != null){
            return "Object{" +
                    "pos=" + Arrays.toString(pos) +
                    ", rot=" + rot +
                    ", size=" + size +
                    ", parent=" + parent +
                    '}';
        }
        return "Object{" +
                "pos=" + Arrays.toString(pos) +
                ", rot=" + rot +
                ", size=" + size +
                '}';
    }

    public abstract char getId();
}

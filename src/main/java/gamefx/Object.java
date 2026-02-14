package gamefx;


import javafx.scene.canvas.GraphicsContext;

import java.util.Arrays;

public abstract class Object {
    private ObjectModel model;
    public ObjectModel getModel() {
        return model;
    }
    protected double[] pos;
    protected Quaternion rot;
    //only use in rendering;
    protected Quaternion globalRot;
    protected double[] offset;
    //dont use
    public double distance;

    protected abstract ObjectModel createModel();

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

    public double[] getPos() {
        return pos;
    }

    public void setPos(double[] pos) {
        this.pos = pos;
    }



    public Quaternion getRot() {
        return rot;
    }

    public void rotateAngle(double amount,double x,double y, double z){
        rot.multiply(Quaternion.fromAngle(amount,x,y,z));
        rot.tryNormalize();
    }
    public void rotate(double x,double y,double z){
        //TODO
    }

    public int size;

    public Object(double[] pos,Quaternion rot,int size) {
        this.pos = pos;
        this.rot = rot;
        this.size = size;
        init();
    }
    public Object(double[] pos,int size) {
        this.pos = pos;
        this.rot = Quaternion.fromEuler(0,0,0,0);
        this.size = size;
        init();
    }
    public void init(){
        offset = new double[3];
        globalRot = Quaternion.fromEuler(0,0,0,0);
        model = createModel();
    }

    public abstract class ObjectModel{
        protected Point[] points;
        protected Drawable[] drawable;
        protected Renderer renderer;

        public ObjectModel() {
            this.renderer = Game.getInstance().getRenderer();
            
        }
        public Object getObject(){
            return Object.this;
        }
        public void draw(GraphicsContext gc){
             if(drawable != null && drawable.length > 0) {
                 globalRot.replaceWith(rot);
                 globalRot.multiplyGlobal(renderer.getCamrot());
                 offset[0] = pos[0];
                 offset[1] = pos[1];
                 offset[2] = pos[2];
                 renderer.setRelToCam(offset);
                 renderer.getCamrot().apply(offset);
                for(Point p: points){
                    p.project(renderer);
                }
                for (Drawable d : drawable) {
                    d.draw(gc);
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

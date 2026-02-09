package gamefx;


import javafx.scene.canvas.GraphicsContext;

import java.util.Arrays;

public abstract class Object {
    private ObjectModel model;
    public ObjectModel getModel() {
        return model;
    }
    protected double[] pos;
    protected double[] rot;
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



    public double[] getRot() {
        return rot;
    }

    public void rotate(double roll,double yaw,double pitch){
        rot[0] += roll;
        rot[1] += yaw;
        rot[2] += pitch;
    }
    public void rotateX(double degree){
        rot[0] += degree;
    }
    public void rotateY(double degree){
        rot[1] += degree;
    }
    public void rotateZ(double degree){
        rot[2] += degree;
    }

    public int size;

    public Object(double[] pos,double[] rot,int size) {
        this.pos = pos;
        this.rot = rot;
        this.size = size;
        model = createModel();
    }
    public Object(double[] pos,int size) {
        this.pos = pos;
        this.rot = new double[]{0,0,0};
        this.size = size;
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
        public void draw(GraphicsContext g){
             if(drawable != null && drawable.length > 0) {
                for(Point p: points){
                    p.project(renderer);
                }
                for (Drawable d : drawable) {
                    d.draw(g);
                }
            }
        }
    }

    public String toString() {
        return "Object{" +
                "rot=" + Arrays.toString(rot) +
                ", pos=" + Arrays.toString(pos) +
                '}';
    }
}

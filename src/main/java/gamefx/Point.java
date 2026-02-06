package gamefx;


import java.util.Arrays;

public class Point {

    private Object object;

    private double[] projection = new double[2];

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    private double[] position = new double[3];

    public double[] getCampos() {
        return campos;
    }

    private double[] campos = new double[3];

    Renderer ren;

    Point(Object object, double x, double y, double z){
        this.ren= Game.getInstance().getRenderer();
        this.object = object;
        position[0] = x;
        position[1] = y;
        position[2] = z;
    }

    public double getX() {
        return position[0];
    }

    public void setX(double x) {
        position[0] = x;
    }

    public double getY() {
        return position[1];
    }

    public void setY(double y) {
        position[1] = y;
    }

    public double getZ() {
        return position[2];
    }

    public void setZ(double z) {
        position[2] = z;
    }

    public double[] getProjection() {
        return projection;
    }
    public double getProjX() {
        return projection[0];
    }
    public double getProjY() {
        return projection[1];
    }


    private int visibility = 0;

    public int visibility() {
        return visibility;
    }

    public Object getObject() {
        return object;
    }

    public void project(double fow){
        Renderer ren = Game.getInstance().getRenderer();
        double[] vertex= position.clone();
        ren.getRotation(object.getRot(), vertex);
        vertex[0] = vertex[0] +object.getPos()[0];
        vertex[1] = vertex[1] +object.getPos()[1];
        vertex[2] = vertex[2] +object.getPos()[2];
        ren.setRelToCam(vertex);
        ren.getNegRot(ren.getCamrot(),vertex);
        campos = vertex;
        if(vertex[0] < ren.getNear()){
            visibility = -1;
            return;
        }else if(vertex[0] > ren.getFar()){
            visibility = 1;
            return;
        }
        visibility = 0;
        projection = ren.getProjection(vertex,fow);
        ren.adjustToScreen(projection);
    }

    public double[] getCut(Point other) {
        double[] alt;
        if(this.visibility() == -1){
            alt = ren.getProjection(ren.getCutLine(this, other,ren.getNear()), ren.getFow());
        }else{
            alt = ren.getProjection(ren.getCutLine(other,this,ren.getFar()), ren.getFow());
        }
        ren.adjustToScreen(alt);
        return alt;
    }

    @Override
    public String toString() {
        return "Point{" +
                "projection=" + Arrays.toString(projection) +
                ", position=" + Arrays.toString(position) +
                ", campos=" + Arrays.toString(campos) +
                ", visibility=" + visibility +
                '}';
    }
}

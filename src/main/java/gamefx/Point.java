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

    public void project(Renderer ren){
        campos[0] = position[0];
        campos[1] = position[1];
        campos[2] = position[2];
        object.globalRot.apply(campos);
        campos[0]+=  object.offset[0];
        campos[1]+=  object.offset[1];
        campos[2]+=  object.offset[2];
        if(campos[0] < ren.getNear()){
            visibility = -1;
            return;
        }else if(campos[0] > ren.getFar()){
            visibility = 1;
            return;
        }
        visibility = 0;
        ren.getProjection(projection,campos, ren.getFow());
        ren.adjustToScreen(projection);
    }

    public double[] getCut(Point other) {
        double[] alt = new double[2];
        if(this.visibility() == -1){
            ren.getProjection(alt,ren.getCutLine(this, other,ren.getNear()), ren.getFow());
        }else{
            ren.getProjection(alt,ren.getCutLine(other,this,ren.getFar()), ren.getFow());
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

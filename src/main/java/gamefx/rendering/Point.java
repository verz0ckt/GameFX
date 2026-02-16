package gamefx.rendering;


import gamefx.Game;
import gamefx.objects.Object;

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

    public Point(Object object, double x, double y, double z){
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
        object.getRenderingRotationMatrix().apply(position,campos);
        object.applyOffset(campos);
        if(campos[0] < ren.getNear()){
            visibility = -1;
            return;
        }else if(campos[0] > ren.getFar()){
            visibility = 1;
            return;
        }
        visibility = 0;
        ren.getProjection(projection,campos, ren.getFov());
        ren.adjustToScreen(projection);
    }

    public double[] getCut(Point other) {
        double[] alt = new double[2];
        if(this.visibility() == -1){
            ren.getProjection(alt,ren.getCutLine(this, other,ren.getNear()), ren.getFov());
        }else{
            ren.getProjection(alt,ren.getCutLine(other,this,ren.getFar()), ren.getFov());
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

package gamefx.rendering;


import gamefx.Main;
import gamefx.objects.Object;

import java.util.Arrays;

public class Point {

    private final Object object;

    private final double[] projection = new double[3];

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

    private final double[] campos = new double[3];

    Renderer ren;

    public Point(Object object, double x, double y, double z){
        this.ren= Main.getGame().getRenderer();
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
    public double getProjZ() {
        return projection[2];
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
        object.applyOffsetToPoint(campos);
        if(campos[0] < ren.getNear()){
            visibility = -1;
            return;
        }else if(campos[0] > ren.getFar()){
            visibility = 1;
            return;
        }
        visibility = 0;
        ren.getProjection(projection,campos, ren.getFocalLength());
        ren.adjustToScreen(projection);
    }
    //only use while rendering
    private static final double[] tempPoint = new double[3];
    public void getCut(Point other,double[] alt) {
        if(this.visibility() == -1){
            ren.getCutLine(this, other,ren.getNear(), tempPoint);
            ren.getProjection(alt,tempPoint, ren.getFocalLength());
        }else{
            ren.getCutLine(other,this,ren.getFar(), tempPoint);
            ren.getProjection(alt,tempPoint, ren.getFocalLength());
        }
        ren.adjustToScreen(alt);
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

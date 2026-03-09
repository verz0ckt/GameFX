package gamefx.rendering;


import javafx.scene.paint.Color;

import java.nio.ByteBuffer;

public class Plane extends Drawable {
    public Plane(Point... corners) {
        super("plane",Color.LIGHTSKYBLUE,corners );
    }

    public Plane(Color color, Point... corners) {
        super("plane",color,corners);
    }

    private double[] x = new double[4];
    private double[] y = new double[4];
    @Override
    public void draw(ByteBuffer buffer) {
        int index = 0;
        for (int i = 0;i<corners.length;i++){
            if(corners[i].visibility() != 0){
                {
                    int imin = i - 1;
                    if (imin < 0) {
                        imin = corners.length - 1;
                    }
                    if (corners[imin].visibility() != corners[i].visibility()) {
                        corners[i].getCut(corners[imin],alt);
                        if(index >= x.length){
                            x = new double[index+1];
                            y = new double[index+1];
                            //TODO: make better
                            return;
                        }
                        x[index] = alt[0];
                        y[index] = alt[1];
                        index++;
                    }
                }{
                    int imax = i + 1;
                    if (imax > corners.length - 1) {
                        imax = 0;
                    }
                    if (corners[imax].visibility() != corners[i].visibility()) {
                        corners[i].getCut(corners[imax],alt);
                        if(index >= x.length){
                            x = new double[index+1];
                            y = new double[index+1];
                            //TODO: make better
                            return;
                        }
                        x[index] = alt[0];
                        y[index] = alt[1];
                        index++;
                    }
                }
                continue;
            }
            double[] p = corners[i].getProjection();
            if(index >= x.length){
                x = new double[index+1];
                y = new double[index+1];
                //TODO: make better
                return;
            }
            x[index] = p[0];
            y[index] = p[1];
            index++;
        }
        if(index >1) {
            /*if(gc.getFill() != paint){
                gc.setFill(paint);
            }
            gc.fillPolygon(x, y, index);
             */
        }
    }
}

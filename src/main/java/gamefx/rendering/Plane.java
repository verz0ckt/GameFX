package gamefx.rendering;



import java.nio.IntBuffer;
import java.util.Arrays;

public class Plane extends Drawable {
    public Plane(Point... corners) {
        super("plane",0xff87cefa,corners );
    }

    public Plane(int color, Point... corners) {
        super("plane",color,corners);
    }

    private double[] x = new double[4];
    private double[] y = new double[4];
    @Override
    public void draw(IntBuffer buffer) {
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
            int topX = (int) findMin(x,index);
            int topY = (int) findMin(y,index);
            int bottomX = (int) findMax(x,index);
            int bottomY = (int) findMax(y,index);
            for(int i = topX; i <= bottomX; i++){
                for(int j = topY; j <= bottomY; j++){
                    if(i >= ren.maxWidth || i<0 || j >= ren.maxHeight || j < 0){
                        continue;
                    }
                    buffer.put(i+j*ren.maxWidth,color);
                }
            }

        }
    }
    public static double findMin(double[] array,int size){
        double min = Integer.MAX_VALUE;
        for(int i = 0; i < size; i++){
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;

    }
    public static double findMax(double[] array,int size){
        double max = Integer.MIN_VALUE;
        for(int i = 0; i < size; i++){
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;

    }
}

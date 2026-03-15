package gamefx.rendering;


public class Triangle extends Drawable {
    public Triangle(Point... corners) {
        super("plane",0xff87cefa,corners );
    }

    public Triangle(int color, Point... corners) {
        super("plane",color,corners);
    }

    private final double[] x = new double[3];
    private final double[] y = new double[3];
    @Override
    public void draw(int[] buffer) {
        int index = 0;
        /*
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
        }*/
        //while testing
        for(Point c : corners){
            if (c.visibility() != 0){
                return;
            }
        }
        double[] p = corners[0].getProjection();
        x[index] = p[0];
        y[index++] = p[1];
        p = corners[1].getProjection();
        x[index] = p[0];
        y[index++] = p[1];
        p = corners[2].getProjection();
        x[index] = p[0];
        y[index++] = p[1];
        if(index >1) {
            int topX = Math.max((int) findMin(x,index),0);
            int topY = Math.max((int) findMin(y,index),0);
            int bottomX = Math.min((int) findMax(x,index), ren.maxWidth-1);
            int bottomY = Math.min((int) findMax(y,index), ren.maxHeight-1);
            for(int j = topY; j <= bottomY; j++){
                for(int i = topX; i <= bottomX; i++){
                    buffer[i+j* ren.maxWidth] = color;
                }
            }
        }
    }
    public void drawTopTri(){

    }
    public void drawBottomTri(){

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

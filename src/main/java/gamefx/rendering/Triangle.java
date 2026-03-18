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
    public void draw(int[] buffer, short[] zbuffer) {
        int xt = (int) corners[0].getProjX();
        int yt = (int) corners[0].getProjY();
        int zt = (int) corners[0].getProjZ();
        int xm = (int) corners[1].getProjX();
        int ym = (int) corners[1].getProjY();
        int zm = (int) corners[1].getProjZ();
        int xb = (int) corners[2].getProjX();
        int yb = (int) corners[2].getProjY();
        int zb = (int) corners[2].getProjZ();

        if(xb < xm){
            int tmp = xb;
            xb = xm;
            xm = tmp;

            tmp = yb;
            yb = ym;
            ym = tmp;

            tmp = zb;
            zb = zm;
            zm = tmp;
        }
        if(xm < xt){
            int tmp = xm;
            xm = xt;
            xt = tmp;

            tmp = ym;
            ym = yt;
            yt = tmp;

            tmp = zm;
            zm = zt;
            zt = tmp;
        }
        if(xb < xm){
            int tmp = xb;
            xb = xm;
            xm = tmp;

            tmp = yb;
            yb = ym;
            ym = tmp;

            tmp = zb;
            zb = zm;
            zm = tmp;
        }

        //while testing
        for (Point c : corners) {
            if (c.visibility() != 0) {
                return;
            }
        }
        if(xt == xm){
            drawBottomTri();
            return;
        }else if(xb == xm){
            drawTopTri();
            return;
        }

        drawTopTri();
        drawBottomTri();


        /*
        double[] p = corners[0].getProjection();
        x[0] = p[0];
        y[0] = p[1];
        int zs = (int) p[2];
        p = corners[1].getProjection();
        x[1] = p[0];
        y[1] = p[1];
        zs += (int) p[2];
        p = corners[2].getProjection();
        x[2] = p[0];
        y[2] = p[1];
        zs += (int) p[2];
        int z = zs / 3;
        int topX = Math.max((int) findMin(x), 0);
        int topY = Math.max((int) findMin(y), 0);
        int bottomX = Math.min((int) findMax(x), ren.maxWidth - 1);
        int bottomY = Math.min((int) findMax(y), ren.maxHeight - 1);
        for (int j = topY; j <= bottomY; j++) {
            for (int i = topX; i <= bottomX; i++) {
                int pos = i + j * ren.maxWidth;
                if (z > zbuffer[pos]) {
                    buffer[pos] = color;
                    zbuffer[pos] = (short) z;
                }
            }
        }*/
    }
    public void drawTopTri(int[] buffer,int x1,int y1,int c1, int m1,int c2,int m2,int x2,int y2){
        for(int y = y1; y < y2; y++){
            for(int x = x1; x < x2; x++){
                int l1 = -x*m1-c1+y;
                int l2 = x*m2+c2-y;
                if(l1 >= 0 && l2 >= 0){
                    buffer[x+y* ren.maxWidth] = color;
                }
            }
        }
    }
    public void drawBottomTri(){

    }
    public static double findMin(double[] array){
        double min = Integer.MAX_VALUE;
        for (double v : array) {
            if (v < min) {
                min = v;
            }
        }
        return min;

    }
    public static double findMax(double[] array){
        double max = Integer.MIN_VALUE;
        for (double v : array) {
            if (v > max) {
                max = v;
            }
        }
        return max;

    }
}

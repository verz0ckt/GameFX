package gamefx.rendering;


public class Triangle extends Drawable {
    public Triangle(Point... corners) {
        super("plane",0xff87cefa,corners );
    }

    public Triangle(int color, Point... corners) {
        super("plane",color,corners);
    }

    @Deprecated
    private int z;

    @Override
    public void draw(int[] buffer, short[] zbuffer) {
        int xl = (int) corners[0].getProjX();
        int yl = (int) corners[0].getProjY();
        int zl = (int) corners[0].getProjZ();
        int xm = (int) corners[1].getProjX();
        int ym = (int) corners[1].getProjY();
        int zm = (int) corners[1].getProjZ();
        int xr = (int) corners[2].getProjX();
        int yr = (int) corners[2].getProjY();
        int zr = (int) corners[2].getProjZ();

        if(xr < xm){
            int tmp = xr;
            xr = xm;
            xm = tmp;

            tmp = yr;
            yr = ym;
            ym = tmp;

            tmp = zr;
            zr = zm;
            zm = tmp;
        }
        if(xm < xl){
            int tmp = xm;
            xm = xl;
            xl = tmp;

            tmp = ym;
            ym = yl;
            yl = tmp;

            tmp = zm;
            zm = zl;
            zl = tmp;
        }
        if(xr < xm){
            int tmp = xr;
            xr = xm;
            xm = tmp;

            tmp = yr;
            yr = ym;
            ym = tmp;

            tmp = zr;
            zr = zm;
            zm = tmp;
        }

        //TODO: bounding box cases

        //while testing
        for (Point c : corners) {
            if (c.visibility() != 0) {
                return;
            }
        }
        int maxZ = Math.max(Math.max(zl,zm),zr);
        z = (zl+zm+zr-maxZ)/2;//TODO:remove


        //TODO: clap min to 0 and max to maxWidth/maxHeight

        boolean flip =  yl+yr < ym*2;
        if(xl == xm){//draw Right
            double mlr = (double) (yr - yl) /(xr-xl);
            double mrm = (double) (ym - yr) /(xm-xr);
            int yMidMax;
            int yMidMin;
            if(ym > yl){
                yMidMax = ym;
                yMidMin = yl;
            }else {
                yMidMax = yl;
                yMidMin = ym;
            }
            int ymax = Math.max(yMidMax,yr);
            int ymin = Math.min(yMidMin,yr);
            if(flip) {
                drawTri(buffer, zbuffer, xr, yr, xm, xr, ymin, ymax, mrm, mlr);
            }else {
                drawTri(buffer, zbuffer, xr, yr, xm, xr, ymin, ymax, mlr, mrm);
            }
            return;
        }else if(xr == xm){//draw left
            double mlr = (double) (yr - yl) /(xr-xl);
            double mlm = (double) (ym - yl) /(xm-xl);
            int yMidMax;
            int yMidMin;
            if(ym > yr){
                yMidMax = ym;
                yMidMin = yr;
            }else {
                yMidMax = yr;
                yMidMin = ym;
            }
            int ymax = Math.max(yMidMax,yl);
            int ymin = Math.min(yMidMin,yl);
            if(flip) {
                drawTri(buffer, zbuffer, xl, yl, xl, xm, ymin, ymax,mlm,mlr);
            }else {
                drawTri(buffer, zbuffer, xl, yl, xl, xm, ymin, ymax, mlr,mlm);
            }
            return;
        }
        double mlr = (double) (yr - yl) /(xr-xl);
        int ysplit = (int) (mlr*(xm-xl)+yl);
        double mlm = (double) (ym - yl) /(xm-xl);
        double mrm = (double) (ym - yr) /(xm-xr);

        int yMidMax;
        int yMidMin;
        if(ym > ysplit){
            yMidMax = ym;
            yMidMin = ysplit;
        }else {
            yMidMax = ysplit;
            yMidMin = ym;
        }
        int ymax = Math.max(yMidMax,yr);
        int ymin = Math.min(yMidMin,yr);
        if(flip){
            drawTri(buffer, zbuffer, xr,yr,xm,xr,ymin,ymax,mrm,mlr);
        }else{
            drawTri(buffer, zbuffer, xr,yr,xm,xr,ymin,ymax,mlr,mrm);
        }
        ymax = Math.max(yMidMax,yl);
        ymin = Math.min(yMidMin,yl);
        if(flip){
            drawTri(buffer, zbuffer, xl,yl,xl,xm,ymin,ymax,mlm,mlr);
        }else{
            drawTri(buffer, zbuffer, xl,yl,xl,xm,ymin,ymax,mlr,mlm);
        }
    }
    public void drawTri(int[] buffer,short[] zbuffer,
                        final int xstart,final int ystart,
                        final int xmin,final int xmax,final int ymin, int ymax,
                        final double m1,final double m2){
        int dx = xmin-xstart;
        int dy = ymin-ystart;
        double e1 = m1*dx-dy;
        double e2 = -m2*dx+dy;
        ymax *= ren.maxWidth;
        for(int y = ymin* ren.maxWidth;y<=ymax;y += ren.maxWidth){
            double te1 = e1;
            double te2 = e2;
            boolean hasdrawn = false;
            for(int x = xmin; x<= xmax;x++){
                te1 += m1;
                te2 -= m2;
                if(te1<0 || te2 <0){
                    if(hasdrawn){
                        break;
                    }
                    continue;
                }
                int pos = x+y;
                if(pos <0 || pos >= buffer.length){
                    continue;
                }
                hasdrawn = true;
                if(z > zbuffer[pos]){
                    buffer[pos] = color;
                    zbuffer[pos] = (short) z;
                }
            }
            e1--;
            e2++;
        }
    }
}

















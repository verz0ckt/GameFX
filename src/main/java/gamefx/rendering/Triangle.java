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

        if(xl == xm){//draw only Right
            final int dxrl = xl- xr;
            final int dyrl = yl-yr;
            final int dxrm = xm-xr;
            final int dyrm = ym-yr;
            if(ym > yl){
                int ymax = Math.max(ym,yr);
                int ymin = Math.min(yl,yr);
                drawTri(buffer, zbuffer, xr,yr,xm,xr,ymin,ymax,dxrl,dyrl,dxrm,dyrm);
                return;
            }else {
                int ymax = Math.max(yl, yr);
                int ymin = Math.min(ym, yr);
                drawTri(buffer, zbuffer, xr, yr, xm, xr, ymin, ymax, dxrm, dyrm, dxrl, dyrl);
                return;
            }
        }else if(xr == xm){//draw only left
            final int dxlr = xr-xl;
            final int dylr = yr-yl;
            final int dxlm = xm-xl;
            final int dylm = ym-yl;
            if(ym > yr){
                int ymax = Math.max(ym,yl);
                int ymin = Math.min(yr,yl);
                drawTri(buffer, zbuffer, xl,yl,xl,xm,ymin,ymax,dxlm,dylm,dxlr,dylr);
                return;
            }else {
                int ymax = Math.max(yr, yl);
                int ymin = Math.min(ym, yl);
                drawTri(buffer, zbuffer, xl, yl, xl, xm, ymin, ymax, dxlr, dylr, dxlm, dylm);
                return;
            }
        }//draw both
        final int dxlr = xr-xl;
        final int dylr = yr-yl;
        final int dxrm = xm-xr;
        final int dyrm = ym-yr;
        final int dxlm = xm-xl;
        final int dylm = ym-yl;
        int ysplit =  (yr - yl)*(xm-xl)/(xr-xl)+yl;
        if(ym > ysplit){
            ysplit--;
            int ymax = Math.max(ym,yr);
            int ymin = Math.min(ysplit,yr);
            drawTri(buffer, zbuffer, xr,yr,xm,xr,ymin,ymax,-dxlr,-dylr,dxrm,dyrm);
            ymax = Math.max(ym,yl);
            ymin = Math.min(ysplit,yl);
            drawTri(buffer, zbuffer, xl,yl,xl,xm,ymin,ymax,dxlm,dylm,dxlr,dylr);
        }else {
            ysplit++;
            int ymax = Math.max(ysplit,yr);
            int ymin = Math.min(ym,yr);
            drawTri(buffer, zbuffer, xr,yr,xm,xr,ymin,ymax,dxrm,dyrm,-dxlr,-dylr);
            ymax = Math.max(ysplit,yl);
            ymin = Math.min(ym,yl);
            drawTri(buffer, zbuffer, xl,yl,xl,xm,ymin,ymax,dxlr,dylr,dxlm,dylm);
        }
    }
    public void drawTri(int[] buffer,short[] zbuffer,
                        final int xstart,final int ystart,
                        int xmin,int xmax,int ymin,int ymax,
                        final int dx1,final int dy1,final int dx2,final int dy2){
        xmin = Math.max(xmin, 0);
        xmax = Math.min(xmax, ren.maxWidth-1);
        ymin = Math.max(ymin, 0);
        ymax = Math.min(ymax, ren.maxHeight-1);
        int dx = xmin-xstart;
        int dy = ymin-ystart;
        int e1 = dy1*dx-dx1*dy;
        int e2 = -dy2*dx+dx2*dy;
        ymax *= ren.maxWidth;
        for(int y = ymin* ren.maxWidth;y<=ymax;y += ren.maxWidth){
            int te1 = e1;
            int te2 = e2;
            final int posmax = xmax+y;
            for(int pos = xmin+y; pos<= posmax;pos++){
                te1 += dy1;
                te2 -= dy2;
                if(te1<0 || te2<0 || zbuffer[pos] >= z){
                    continue;
                }
                buffer[pos] = color;
                zbuffer[pos] = (short) z;
            }
            e1 -= dx1;
            e2 +=dx2;
        }
    }
}

















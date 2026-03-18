package gamefx.rendering;


public class Line extends Drawable
{
    public Line(Point... corners)
    {
        super("line",0xFF000000,corners);

    }
    public Line(int color, Point... corners)
    {
        super("line",color,corners);
    }

    @Override
    public void draw(int[] buffer, short[] zbuffer) {
        double x1 = corners[0].getProjX();
        double y1 = corners[0].getProjY();
        double z1 = corners[0].getProjZ();
        double x2 = corners[1].getProjX();
        double y2 = corners[1].getProjY();
        double z2 = corners[1].getProjZ();

        if(corners[0].visibility() != 0){
            if(corners[1].visibility() == corners[0].visibility()){
                return;
            }
            corners[0].getCut(corners[1],alt);
            x1 = alt[0];
            y1 = alt[1];
            z1 = alt[2];
        }
        if(corners[1].visibility()  != 0){
            corners[1].getCut(corners[0],alt);
            x2 = alt[0];
            y2 = alt[1];
            z2 = alt[2];
        }
        int dx = Math.abs((int)x2-(int)x1);
        int dy = Math.abs((int)y2-(int)y1);
        int dz = Math.abs((int)z2-(int)z1);
        if(dx>dy){
            int x = (int) x1;
            int xEnd = (int) x2;
            int y = (int) y1;
            int z = (int) z1;
            int signY = (int) Math.signum(y2-y1);
            int signZ = (int) Math.signum(z2-z1);
            if(x1 > x2){
                x = (int) x2;
                xEnd = (int) x1;
                y = (int) y2;
                signY *=-1;
                z = (int) z2;
                signZ *=-1;
            }
            y*= ren.maxWidth;
            signY *= ren.maxWidth;
            dy <<= 1;
            dz <<= 1;
            int error = dy-dx;
            int errorZ = dz-dx;
            dx = dx<<1;
            if(x < ren.maxWidth && x >= 0 && y < ren.SIZE && y >= 0) {
                int pos = x+y;
                if(z >= zbuffer[pos]){
                    buffer[pos] = color;
                    zbuffer[pos] = (short) z;
                }
            }
            while(x <= xEnd){
                x++;
                error += dy;
                errorZ += dz;
                if(error > 0){
                    error -= dx;
                    y += signY;
                }
                if(errorZ > 0){
                    errorZ -= dx;
                    z += signZ;
                }
                if(x >= ren.maxWidth || x<0 || y >= ren.SIZE || y < 0){
                    if(x >= ren.maxWidth || (y >= ren.SIZE && signY >= 0) || (y < 0 && signY <= 0) ){
                        break;
                    }
                    continue;
                }
                int pos = x+y;
                if(z >= zbuffer[pos]){
                    buffer[pos] = color;
                    zbuffer[pos] = (short) z;
                }
            }
        }else {
            int x = (int) x1;
            int y = (int) y1;
            int z = (int) z1;
            int yEnd = (int) y2;
            int signX = (int) Math.signum(x2-x1);
            int signZ = (int) Math.signum(z2-z1);
            if(y1 > y2){
                x = (int) x2;
                yEnd = (int) y1;
                y = (int) y2;
                signX *=-1;
                z = (int) z2;
                signZ *=-1;
            }
            y*= ren.maxWidth;
            yEnd *= ren.maxWidth;
            dx <<= 1;
            dz <<= 1;
            int error = dx-dy;
            int errorZ = dz-dy;
            dy <<= 1;
            if(x < ren.maxWidth && x >= 0 && y < ren.SIZE && y >= 0) {
                int pos = x+y;
                if(z >= zbuffer[pos]){
                    buffer[pos] = color;
                    zbuffer[pos] = (short) z;
                }
            }
            while(y <= yEnd){
                y+= ren.maxWidth;
                error += dx;
                errorZ += dz;
                if(error > 0){
                    error -= dy;
                    x += signX;
                }
                if(errorZ > 0){
                    errorZ -= dy;
                    z += signZ;
                }
                if(x >= ren.maxWidth || x < 0 || y >= ren.SIZE || y < 0){
                    if(y >= ren.SIZE || (x >= ren.maxWidth && signX >= 0) || (x < 0 && signX <= 0) ){
                        break;
                    }
                    continue;
                }
                int pos = x+y;
                if(z >= zbuffer[pos]){
                    buffer[pos] = color;
                    zbuffer[pos] = (short) z;
                }
            }
        }
    }

}

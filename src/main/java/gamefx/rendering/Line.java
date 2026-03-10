package gamefx.rendering;


import java.nio.IntBuffer;

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
    public void draw(IntBuffer buffer) {
        double x1 = corners[0].getProjection()[0];
        double y1 = corners[0].getProjection()[1];
        double x2 = corners[1].getProjection()[0];
        double y2 = corners[1].getProjection()[1];

        if(corners[0].visibility() != 0){
            if(corners[1].visibility() == corners[0].visibility()){
                return;
            }
            corners[0].getCut(corners[1],alt);
            x1 = alt[0];
            y1 = alt[1];
        }
        if(corners[1].visibility()  != 0){
            corners[1].getCut(corners[0],alt);
            x2 = alt[0];
            y2 = alt[1];
        }
        int dx = Math.abs((int)x2-(int)x1);
        int dy = Math.abs((int)y2-(int)y1);

        if(dx>dy){
            int x = (int) x1;
            int xEnd = (int) x2;
            int y = (int) y1;
            int signY = (int) Math.signum(y2-y1);
            if(x1 > x2){
                x = (int) x2;
                xEnd = (int) x1;
                y = (int) y2;
                signY *=-1;
            }
            y*= ren.maxWidth;
            signY *= ren.maxWidth;
            dy <<= 1;
            int error = dy-dx;
            dx = dx<<1;
            if(x >= ren.maxWidth && x < 0 && y >= buffer.capacity() && y < 0) {
                int pos = x+y;
                buffer.put(pos,color);
            }
            while(x <= xEnd){
                x++;
                error += dy;
                if(error > 0){
                    error -= dx;
                    y += signY;
                }
                if(x >= ren.maxWidth || x < 0 || y >= buffer.capacity() || y < 0) {
                    continue;
                }
                int pos = x+y;
                buffer.put(pos,color);
            }
        }else {
            int x = (int) x1;
            int y = (int) y1;
            int yEnd = (int) y2;
            int signX = (int) Math.signum(x2-x1);
            if(y1 > y2){
                x = (int) x2;
                yEnd = (int) y1;
                y = (int) y2;
                signX *=-1;
            }
            y*= ren.maxWidth;
            yEnd *= ren.maxWidth;
            dx <<= 1;
            int error = dx-dy;
            dy <<= 1;
            if(x >= ren.maxWidth && x < 0 && y >= buffer.capacity() && y < 0) {
                int pos = x+y;
                buffer.put(pos,color);
            }
            while(y <= yEnd){
                y+= ren.maxWidth;
                error += dx;
                if(error > 0){
                    error -= dy;
                    x += signX;
                }
                if(x >= ren.maxWidth || x < 0 || y >= buffer.capacity() || y < 0) continue;
                int pos = x+y;
                buffer.put(pos,color);
            }
        }

        //gc.setStroke(paint);
        //gc.strokeLine(x1,y1,x2,y2);
    }

}

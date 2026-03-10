package gamefx.rendering;


import javafx.scene.paint.Color;

import java.nio.ByteBuffer;

public class Line extends Drawable
{
    public Line(Point... corners)
    {
        super("line",Color.BLACK,corners);

    }
    public Line(Color color, Point... corners)
    {
        super("line",color,corners);
    }

    @Override
    public void draw(ByteBuffer buffer) {
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
        int dx = (int) Math.abs((int)x2-(int)x1)<<2;
        int dy = (int) Math.abs((int)y2-(int)y1)<<2;

        if(dx>dy){

            int x = ((int) x1)<<2;
            int xEnd = ((int) x2)<<2;
            int y = ((int) y1)<<2;
            int signY = (int) Math.signum(y2-y1)*4;
            if(x1 > x2){
                x = ((int) x2)<<2;
                xEnd = ((int) x1)<<2;
                y = ((int) y2)<<2;
                signY *=-1;
            }
            dy <<= 1;
            int error = dy-dx;
            dx = dx<<1;
            /*
            if(x >= ren.maxWidth && x < 0 && y >= ren.maxHeight && y < 0) {
                Color col = (Color) paint;
                int pos = (x + y * ren.maxWidth) * 4;
                buffer.put(pos++, (byte) (col.getBlue() * 255));
                buffer.put(pos++, (byte) (col.getGreen() * 255));
                buffer.put(pos++, (byte) (col.getRed() * 255));
                buffer.put(pos, (byte) (col.getOpacity() * 255));
            }*/
            while(x <= xEnd){
                Color col = (Color) paint;
                x += 4;
                error += dy;
                if(error > 0){
                    error -= dx;
                    y += signY;
                }
                if(x >= ren.maxWidth || x < 0 || y >= ren.maxHeight || y < 0) System.out.println("g");
                int pos = x+y*ren.maxWidth;
                buffer.put(pos++,(byte) (col.getBlue()*255));
                buffer.put(pos++,(byte) (col.getGreen()*255));
                buffer.put(pos++,(byte) (col.getRed()*255));
                buffer.put(pos,(byte) (col.getOpacity()*255));
            }
        }else{
            //add shifting
            int x = (int) x1;
            int y = (int) y1;
            int yEnd = (int) y2;
            int signX = (int) Math.signum(x2-x1);
            if(y1 > y2){
                x = (int) x2;
                y = (int) y2;
                yEnd = (int) y1;
                signX *=-1;
            }
            dx = dx<<1;
            int error = dx-dy;
            dy = dy<<1;
            if(x >= ren.maxWidth && x < 0 && y >= ren.maxHeight && y < 0) {
                Color col = (Color) paint;
                int pos = (x + y * ren.maxWidth) * 4;
                buffer.put(pos++, (byte) (col.getBlue() * 255));
                buffer.put(pos++, (byte) (col.getGreen() * 255));
                buffer.put(pos++, (byte) (col.getRed() * 255));
                buffer.put(pos, (byte) (col.getOpacity() * 255));
            }
            while(y <= yEnd){
                Color col = (Color) paint;
                y += 1;
                error += dx;
                if(error > 0){
                    error -= dy;
                    x += signX;
                }
                if(x >= ren.maxWidth || x < 0 || y >= ren.maxHeight || y < 0) continue;
                int pos = (x+y*ren.maxWidth)*4;
                buffer.put(pos++,(byte) (col.getBlue()*255));
                buffer.put(pos++,(byte) (col.getGreen()*255));
                buffer.put(pos++,(byte) (col.getRed()*255));
                buffer.put(pos,(byte) (col.getOpacity()*255));
            }
        }

        //gc.setStroke(paint);
        //gc.strokeLine(x1,y1,x2,y2);
    }
    public static void drawLine(ByteBuffer buffer,int x1,int dx,int dy, int y1, int x2, int y2, Color paint){


    }

}

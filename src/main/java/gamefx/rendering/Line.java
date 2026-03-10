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

        //gc.setStroke(paint);
        //gc.strokeLine(x1,y1,x2,y2);
    }
    private void drawLine(int v1,int u1,int v2,int u2,ByteBuffer buffer){
        int dV = Math.abs(v2-v1);
        int dU = Math.abs(u2-u1);
        int v = (int) v1;//v1
        int u = (int) u1;//v2
        if(v1> v2){
            v = (int) v2;
            u = (int) u2;
        }
        //fix direction
        double m = 0;
        if(dV != 0){
            m = (double) dU / dV;
        }

        double error = 0;
        for(int i = 0; i< dV; i++){
            Color col = (Color)paint;
            v += 1;
            error += m;
            if(error >= 1){
                error--;
                u +=1;
            }
            int pos = (v+u*ren.maxWidth)*4;
            buffer.put(pos++,(byte) (col.getBlue()*255));
            buffer.put(pos++,(byte) (col.getGreen()*255));
            buffer.put(pos++,(byte) (col.getRed()*255));
            buffer.put(pos,(byte) (col.getOpacity()*255));
        }
    }

}

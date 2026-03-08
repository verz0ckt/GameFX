package gamefx.rendering;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
    public void draw(GraphicsContext gc) {
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
        gc.setStroke(paint);
        gc.strokeLine(x1,y1,x2,y2);
    }

}

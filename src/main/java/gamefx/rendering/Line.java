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
        double[] proj1 = corners[0].getProjection();
        double[] proj2 = corners[1].getProjection();

        if(corners[0].visibility() != 0){
            if(corners[1].visibility() == corners[0].visibility()){
                return;
            }
            proj1 = corners[0].getCut(corners[1]);
        }
        if(corners[1].visibility()  != 0){
            proj2 = corners[1].getCut(corners[0]);
        }
        gc.setStroke(paint);
        gc.strokeLine(proj1[0], proj1[1], proj2[0],proj2[1]);
    }

}

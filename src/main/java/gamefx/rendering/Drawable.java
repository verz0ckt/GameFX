package gamefx.rendering;


import gamefx.Game;
import gamefx.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public abstract class Drawable
{
    public Point[] getCorners() {
        return corners;
    }
    protected Point[] corners;

    public String getName() {
        return name;
    }

    protected String name;
    protected Renderer ren;
    protected Paint paint;

    //only use while rendering
    protected static double[] alt = new double[2];
    public Drawable(String name, Paint paint, Point[] corners){
       this.corners = corners;
       this.name = name;
       this.paint = paint;
       ren = Main.getGame().getRenderer();
    }
    public abstract void draw( GraphicsContext gc);
}

package gamefx;


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
    public Drawable(String name, Paint paint, Point[] corners){
       this.corners = corners;
       this.name = name;
       this.paint = paint;
       ren = Game.getInstance().getRenderer();
    }
    public abstract void draw( GraphicsContext gc);
}

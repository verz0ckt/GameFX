package gamefx;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
    protected Color color;
    public Drawable(String name, Color color, Point[] corners){
       this.corners = corners;
       this.name = name;
       this.color = color;
       ren = Game.getInstance().getRenderer();
    }
    public abstract void draw( GraphicsContext g);
}

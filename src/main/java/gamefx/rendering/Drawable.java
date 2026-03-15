package gamefx.rendering;


import gamefx.Main;

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
    protected int color;

    //only use while rendering
    protected static double[] alt = new double[2];
    public Drawable(String name, int color, Point[] corners){
       this.corners = corners;
       this.name = name;
       this.color = color;
       ren = Main.getGame().getRenderer();
    }
    public abstract void draw(int[] buffer);
}

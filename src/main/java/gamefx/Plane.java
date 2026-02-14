package gamefx;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;

public class Plane extends Drawable {
    public Plane(Point... corners) {
        super("plane",Color.RED,corners );
    }

    public Plane(Color color, Point... corners) {
        super("plane",color,corners);
    }

    private final ArrayList<double[]> proj =  new ArrayList<>();
    @Override
    public void draw(GraphicsContext g) {
        proj.clear();
        for (int i = 0;i<corners.length;i++){
            if(corners[i].visibility() != 0){
                {
                    int imin = i - 1;
                    if (imin < 0) {
                        imin = corners.length - 1;
                    }
                    if (corners[imin].visibility() != corners[i].visibility()) {
                        proj.add(corners[i].getCut(corners[imin]));
                    }
                }{
                    int imax = i + 1;
                    if (imax > corners.length - 1) {
                        imax = 0;
                    }
                    if (corners[imax].visibility() != corners[i].visibility()) {
                        proj.add(corners[i].getCut(corners[imax]));
                    }
                }
                continue;
            }
            proj.add(corners[i].getProjection());
        }

        double[] x = new double[proj.size()];
        double[] y = new double[proj.size()];
        for(int i = 0;i< proj.size();i++){

            x[i] = proj.get(i)[0];
            y[i] = proj.get(i)[1];
        }
        if(!proj.isEmpty()) {
            if(g.getFill() != paint){
                g.setFill(paint);
            }
            g.fillPolygon(x, y, proj.size());
        }
    }
}

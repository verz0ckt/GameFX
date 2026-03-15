package gamefx.objects;


import gamefx.util.Quaternion;
import gamefx.rendering.Drawable;
import gamefx.rendering.Line;
import gamefx.rendering.Triangle;
import gamefx.rendering.Point;
@Deprecated
public class Plane extends Object{


        //Multiplayer
        public static final char ID = (char) 0b0011_0000;

        public Plane(double[] pos, Quaternion rot, double size)
        {
            super(pos,rot,size);
            model = new PlaneModel();

        }
        public Plane(double[] pos, double size)
        {
            super(pos,size);
            model = new PlaneModel();
        }


    public class PlaneModel extends ObjectModel {
        public PlaneModel() {
            super();
            int imax = ((int) size - 10) / 5;
            points = new Point[(imax + 1) * 4 + 4];
            drawable = new Drawable[(imax + 1) * 2 + 2];
            for (int i = 0; i <= imax; i += 1) {
                points[i * 4] = new Point(getObject(), i * 10 - size + 10, 0, size);
                points[i * 4 + 1] = new Point(getObject(), i * 10 - size + 10, 0, -size);
                drawable[i * 2 + 2] = new Line(points[i * 4], points[i * 4 + 1]);
                points[i * 4 + 2] = new Point(getObject(), size, 0, i * 10 - size + 10);
                points[i * 4 + 3] = new Point(getObject(), -size, 0, i * 10 - size + 10);
                drawable[i * 2 + 3] = new Line(points[i * 4 + 2], points[i * 4 + 3]);
            }
            points[(imax + 1) * 4] = new Point(getObject(), size, 0, size);
            points[(imax + 1) * 4 + 1] = new Point(getObject(), -size, 0, size);
            points[(imax + 1) * 4 + 2] = new Point(getObject(), -size, 0, -size);
            points[(imax + 1) * 4 + 3] = new Point(getObject(), size, 0, -size);
            drawable[0] = new Triangle(0xffff69b4, points[(imax + 1) * 4], points[(imax + 1) * 4 + 1], points[(imax + 1) * 4 + 2]);
            drawable[1] = new Triangle(0xffff69b4, points[(imax + 1) * 4 + 1], points[(imax + 1) * 4 + 2], points[(imax + 1) * 4 + 3]);
            calcMaxOffset();
        }
        protected void drawDrawables(int[] buffer) {
            boolean isFacingAway = false;
            for(Drawable d : drawable){
                if(d instanceof Triangle && isFacingAway)continue;
                d.draw(buffer);
            }
        }
    }

    @Override
    public char getId() {
        return ID;
    }
}

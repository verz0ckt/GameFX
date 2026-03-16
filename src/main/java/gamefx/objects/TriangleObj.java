package gamefx.objects;

import gamefx.rendering.Drawable;
import gamefx.rendering.Line;
import gamefx.rendering.Triangle;
import gamefx.rendering.Point;
import gamefx.util.Quaternion;

public class TriangleObj extends Object{

    public TriangleObj(double[] pos, Quaternion rot, double size) {
        super(pos, rot, size);
        model = new TriangleModel();
    }

    public TriangleObj(double size) {
        super(size);
        model = new TriangleModel();
    }
    public class TriangleModel extends ObjectModel{
        public TriangleModel() {
            super();
            double width = Math.tan(Math.PI/6)*size;
            points = new Point[3];
            points[0] = new Point(TriangleObj.this,0,size,0);
            points[1] = new Point(TriangleObj.this,0,0,-width);
            points[2] = new Point(TriangleObj.this,0,0,width);
            drawable = new Drawable[4];
            drawable[0] = new Triangle(0xFFFF00FF,points[0],points[1],points[2]);
            drawable[1] = new Line(0xFF000000,points[0],points[1]);
            drawable[2] = new Line(0xFF000000,points[1],points[2]);
            drawable[3] = new Line(0xFF000000,points[2],points[0]);
            calcMaxOffset();

        }


        protected void drawDrawables(int[] buffer) {
            tempPoint[0] = offset[0];
            tempPoint[1] = offset[1];
            tempPoint[2] = offset[2];
            renderingQuaterion.getConjugate().apply(tempPoint);

            if(tempPoint[0] >= 0){
                System.out.println(renderingQuaterion);
                drawable[0].draw(buffer);
                drawable[1].draw(buffer);
                drawable[2].draw(buffer);
                drawable[3].draw(buffer);
            }
        }
    }


    @Override
    public char getId() {
        return 0;
    }
}

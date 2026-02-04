package gamefx;

public class Block extends Object
{

        @Override
        protected ObjectModel createModel() {
                return new BlockModel();
        }

        public Block(double[] pos, double[] rot,int size)
        {
                super(pos,rot,size);

        }
        public Block(double[] pos, int size)
        {
                super(pos,size);

        }
    public class BlockModel extends ObjectModel {
            public BlockModel() {
                    super();
                    points = new Point[]{
                            new Point(getObject(),-size,-size,-size),
                            new Point(getObject(), size,-size,-size),
                            new Point(getObject(), size, size,-size),
                            new Point(getObject(),-size, size,-size),
                            new Point(getObject(),-size,-size, size),
                            new Point(getObject(), size,-size, size),
                            new Point( getObject(),size, size, size),
                            new Point(getObject(),-size, size, size)
                    };
                    boolean planes = true;
                    drawable = new Drawable[planes?24:12];  //24
                    drawable[0] = new Line(points[0],points[1]);
                    drawable[1] = new Line(points[1],points[2]);
                    drawable[2] = new Line(points[2],points[3]);
                    drawable[3] = new Line(points[3],points[0]);
                    drawable[4] = new Line(points[4],points[5]);
                    drawable[5] = new Line(points[5],points[6]);
                    drawable[6] = new Line(points[6],points[7]);
                    drawable[7] = new Line(points[7],points[4]);

                    for(int i = 0;i<4;i++) {
                            drawable[8 + i] = new Line(points[i], points[4 + i]);
                    }
                    if(planes) {
                            drawable[12] = new Plane(points[0], points[1], points[2]);
                            drawable[13] = new Plane(points[0], points[3], points[2]);
                            drawable[14] = new Plane(points[4], points[5], points[6]);
                            drawable[15] = new Plane(points[4], points[7], points[6]);
                            drawable[16] = new Plane(points[0], points[4], points[5]);
                            drawable[17] = new Plane(points[0], points[1], points[5]);
                            drawable[18] = new Plane(points[1], points[5], points[6]);
                            drawable[19] = new Plane(points[1], points[2], points[6]);
                            drawable[20] = new Plane(points[2], points[6], points[7]);
                            drawable[21] = new Plane(points[2], points[3], points[7]);
                            drawable[22] = new Plane(points[3], points[7], points[4]);
                            drawable[23] = new Plane(points[3], points[0], points[4]);
                    }
            }
    }
}

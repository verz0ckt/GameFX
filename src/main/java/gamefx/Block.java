package gamefx;

public class Block extends Object
{

        @Override
        protected ObjectModel createModel() {
                return new BlockModel();
        }

        public Block(double[] pos, Quaternion rot,int size)
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
                    boolean lines = true;
                    drawable = new Drawable[(planes?12:0)+(lines?12:0)];  //24
                    
                    if(planes) {
                            drawable[0] = new Plane(points[0], points[1], points[2]);
                            drawable[1] = new Plane(points[0], points[3], points[2]);
                            drawable[2] = new Plane(points[4], points[5], points[6]);
                            drawable[3] = new Plane(points[4], points[7], points[6]);
                            drawable[4] = new Plane(points[0], points[4], points[5]);
                            drawable[5] = new Plane(points[0], points[1], points[5]);
                            drawable[6] = new Plane(points[1], points[5], points[6]);
                            drawable[7] = new Plane(points[1], points[2], points[6]);
                            drawable[8] = new Plane(points[2], points[6], points[7]);
                            drawable[9] = new Plane(points[2], points[3], points[7]);
                            drawable[10] = new Plane(points[3], points[7], points[4]);
                            drawable[11] = new Plane(points[3], points[0], points[4]);
                    }
                    if(lines) {
                            int offset = lines?12:0;
                            drawable[offset+0] = new Line(points[0], points[1]);
                            drawable[offset+1] = new Line(points[1], points[2]);
                            drawable[offset+2] = new Line(points[2], points[3]);
                            drawable[offset+3] = new Line(points[3], points[0]);
                            drawable[offset+4] = new Line(points[4], points[5]);
                            drawable[offset+5] = new Line(points[5], points[6]);
                            drawable[offset+6] = new Line(points[6], points[7]);
                            drawable[offset+7] = new Line(points[7], points[4]);

                            for (int i = 0; i < 4; i++) {
                                    drawable[offset+8 + i] = new Line(points[i], points[4 + i]);
                            }
                    }
            }
    }
}

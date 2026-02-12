package gamefx;


import javafx.scene.paint.Color;

public class PlaneObj extends Object{

        protected ObjectModel createModel() {
        return new PlaneObj.PlaneModel();
    }

        public PlaneObj(double[] pos, Quaternion rot,int size)
        {
            super(pos,rot,size);

        }
        public PlaneObj(double[] pos,int size)
        {
            super(pos,size);

        }

    public class PlaneModel extends ObjectModel {
            public PlaneModel() {
                super();
                int imax = (size-10)/5;
                points = new Point[(imax+1)*4+4];
                drawable = new Drawable[(imax+1)*2+1];
                for(int i = 0;i <= imax;i+=1){
                    points[i*4] = new Point(getObject(),i*10-size+10,0,size);
                    points[i*4+1] = new Point(getObject(),i*10-size+10,0,-size);
                    drawable[i*2+1] = new Line(points[i*4],points[i*4+1]);
                    points[i*4+2] = new Point(getObject(),size,0,i*10-size+10);
                    points[i*4+3] = new Point(getObject(),-size,0,i*10-size+10);
                    drawable[i*2+2] = new Line(points[i*4+2],points[i*4+3]);
                }
                points[(imax+1)*4]= new Point(getObject(),size,0,size);
                points[(imax+1)*4+1]= new Point(getObject(),-size,0,size);
                points[(imax+1)*4+2]= new Point(getObject(),-size,0,-size);
                points[(imax+1)*4+3]= new Point(getObject(),size,0,-size);
                drawable[0] = new Plane(Color.BLUE,points[(imax+1)*4],points[(imax+1)*4+1],points[(imax+1)*4+2],points[(imax+1)*4+3]);

            }
        }
    }

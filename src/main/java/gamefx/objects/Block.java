package gamefx.objects;

import gamefx.util.Quaternion;
import gamefx.rendering.Drawable;
import gamefx.rendering.Line;
import gamefx.rendering.Triangle;
import gamefx.rendering.Point;

public class Block extends Object{

        private double height;
        private double length;
        private double width;

        //Multiplayer
        public static final char ID = (char) 0b0010_0000;

        public Block(double[] pos, Quaternion rot, double size) {
                super(pos,rot,size);
                height = length = width = 1;
                model = new BlockModel();
        }
        public Block(Object parent, double[] pos, Quaternion rot, double size) {
                super(parent, pos, rot, size);
                height = length = width = 1;
                model = new BlockModel();
        }
        public Block(Object parent, double[] pos, Quaternion rot, double size,double height,double length,double width) {
                super(parent, pos, rot, size);
                this.height = height;
                this.length = length;
                this.width = width;
                model = new BlockModel();
        }
        public Block(double[] pos, Quaternion rot, double size,double height,double length,double width) {
                super( pos, rot, size);
                this.height = height;
                this.length = length;
                this.width = width;
                model = new BlockModel();
        }

        public Block(double[] pos, int size) {
                super(pos,size);
                height = width = length = 1;
                model = new BlockModel();
        }

        public double getHeight() {
                return height;
        }

        public double getLength() {
                return length;
        }

        public double getWidth() {
                return width;
        }

        public class BlockModel extends ObjectModel {
                public BlockModel() {
                        super();
                        double halfSize = size / 2;
                        points = new Point[]{
                                new Point(getObject(), -halfSize * length, -halfSize * height, -halfSize * width),
                                new Point(getObject(), halfSize * length, -halfSize * height, -halfSize * width),
                                new Point(getObject(), halfSize * length, halfSize * height, -halfSize * width),
                                new Point(getObject(), -halfSize * length, halfSize * height, -halfSize * width),
                                new Point(getObject(), -halfSize * length, -halfSize * height, halfSize * width),
                                new Point(getObject(), halfSize * length, -halfSize * height, halfSize * width),
                                new Point(getObject(), halfSize * length, halfSize * height, halfSize * width),
                                new Point(getObject(), -halfSize * length, halfSize * height, halfSize * width)
                        };
                        boolean lines = false;
                        drawable = new Drawable[12 + (lines ? 12 : 0)];  //24
                                drawable[0] = new Triangle(0xfff08080, points[3], points[7], points[4]);
                                drawable[1] = new Triangle(0xfff08080, points[3], points[0], points[4]);
                                drawable[2] = new Triangle(points[1], points[5], points[6]);
                                drawable[3] = new Triangle(points[1], points[2], points[6]);

                                drawable[4] = new Triangle(points[0], points[4], points[5]);
                                drawable[5] = new Triangle(points[0], points[1], points[5]);
                                drawable[6] = new Triangle(points[2], points[6], points[7]);
                                drawable[7] = new Triangle(points[2], points[3], points[7]);

                                drawable[8] = new Triangle(points[0], points[1], points[2]);
                                drawable[9] = new Triangle(points[0], points[3], points[2]);
                                drawable[10] = new Triangle(points[4], points[5], points[6]);
                                drawable[11] = new Triangle(points[4], points[7], points[6]);
                        if (lines) {
                                int offset = lines ? 12 : 0;
                                drawable[offset + 0] = new Line(points[0], points[1]);
                                drawable[offset + 1] = new Line(points[1], points[2]);
                                drawable[offset + 2] = new Line(points[2], points[3]);
                                drawable[offset + 3] = new Line(points[3], points[0]);
                                drawable[offset + 4] = new Line(points[4], points[5]);
                                drawable[offset + 5] = new Line(points[5], points[6]);
                                drawable[offset + 6] = new Line(points[6], points[7]);
                                drawable[offset + 7] = new Line(points[7], points[4]);

                                for (int i = 0; i < 4; i++) {
                                        drawable[offset + 8 + i] = new Line(points[i], points[4 + i]);
                                }
                        }
                        calcMaxOffset();
                }
                protected void drawDrawables(int[] buffer, short[] zbuffer) {
                        tempPoint[0] = offset[0];
                        tempPoint[1] = offset[1];
                        tempPoint[2] = offset[2];
                        renderingQuaterion.getConjugate().apply(tempPoint);
                        if(tempPoint[0] >= 0){
                                drawable[0].draw(buffer,zbuffer);
                                drawable[1].draw(buffer,zbuffer);
                        }else{
                                drawable[2].draw(buffer,zbuffer);
                                drawable[3].draw(buffer,zbuffer);
                        }
                        if(tempPoint[1] > 0){
                                drawable[4].draw(buffer,zbuffer);
                                drawable[5].draw(buffer,zbuffer);
                        }else{
                                drawable[6].draw(buffer,zbuffer);
                                drawable[7].draw(buffer,zbuffer);
                        }
                        if(tempPoint[2] > 0){
                                drawable[8].draw(buffer,zbuffer);
                                drawable[9].draw(buffer,zbuffer);
                        }else{
                                drawable[10].draw(buffer,zbuffer);
                                drawable[11].draw(buffer,zbuffer);
                        }
                        for(int i = 12; i < drawable.length;i++){
                                if(drawable[i] == null)continue;
                                drawable[i].draw(buffer,zbuffer);
                        }
                }
        }

        @Override
        public char getId() {
                return ID;
        }
}

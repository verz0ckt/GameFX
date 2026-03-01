package gamefx.objects;

import gamefx.Main;
import gamefx.util.Quaternion;

public class Cam extends Object{


    public Cam(double[] pos) {
        super(pos, 0);
        absPos = new double[3];
    }
    public Cam(Object parent, double[] pos) {
        super(parent,pos, 0);
        absPos = new double[3];
    }
    public Cam(double[] pos, Quaternion rot) {
        super(pos, rot, 0);
        absPos = new double[3];
    }
    public Cam(Object parent, double[] pos, Quaternion rot) {
        super(parent, pos, rot, 0);
        absPos = new double[3];
    }


    public Quaternion getRenderingQuaternion(){
        return renderingQuaterion;
    }
    private double[] absPos;
    public double[] getAbsPos() {
        return absPos;
    }

    public void update(){
        absPos[0] = pos[0];
        absPos[1] = pos[1];
        absPos[2] = pos[2];
        renderingQuaterion.replaceWith(rot);
        Object temp = parent;
        while(temp != null){
            renderingQuaterion.multiplyGlobal(temp.rot);
            temp.getRot().apply(absPos);
            absPos[0] += temp.pos[0];
            absPos[1] += temp.pos[1];
            absPos[2] += temp.pos[2];
            temp = temp.parent;
        }
    }
    private int perspective = 1;

    public int togglePerspective(){
        if(++perspective > 3){
            perspective = 1;
        }
        setPerspective(perspective);
        return perspective;
    }

    public int getPerspective() {
        return perspective;
    }

    public void setPerspective(int perspective) {
        this.perspective = perspective;
        switch(perspective){
            case 1 -> pos[0] = 0;
            case 2 ->{
                rot.multiply(Quaternion.fromAngle(Math.PI,0,1,0));
                pos[0] = Main.getGame().getMainPlayer().size*2;
            }
            case 3 -> {
                rot = Quaternion.zeroRot();
                pos[0] = -Main.getGame().getMainPlayer().size*2;
            }
        }
    }


    @Override
    public char getId() {
        return ID;
    }
}

package gamefx.objects;

import gamefx.util.Quaternion;

import java.util.Arrays;

/**
 * Beschreiben Sie hier die Klasse Player.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Player extends Object
{
    @Override
    protected ObjectModel createModel() {
        return new PlayerModel();
    }
    public double pitch = 0;


    public void rotateZ(double pitch){
        this.pitch += pitch;
    }

    public Player(double[] pos, Quaternion rot)
    {
        super(pos,rot,1);

    }
    public Player(double[] pos)
    {
        super(pos,1);

    }
    public void move(double[] pos){
        rot.apply(pos);
        this.pos[0]+= pos[0];
        this.pos[1]+= pos[1];
        this.pos[2]+= pos[2];
    }
    public void move(double x,double y,double z){
        //TODO: add extra  func in Quaternion
        move(new double[]{x,y,z});
    }

    @Override
    @Deprecated
    public void rotateAngle(double amount, double x, double y, double z) {
        //TODO: add rotate then delete this
        super.rotateAngle(amount,x,y,0);
        this.pitch+= z*amount;
    }
    @Override
    public void rotate(double x, double y, double z){

    }

    public class PlayerModel extends ObjectModel {
        public PlayerModel(){
          super();

        }
    }

    @Override
    public String toString() {
        return "Player{" +
                "pos=" + Arrays.toString(pos) +
                '}';
    }
}

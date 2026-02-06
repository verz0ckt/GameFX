package gamefx;

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

    @Override
    public void rotate(double roll,double yaw,double pitch){
        rot[0] += roll;
        rot[1] += yaw;
        this.pitch += pitch;
    }
    public void rotateZ(double pitch){
        this.pitch += pitch;
    }

    public Player(double[] pos, double[] rot)
    {
        super(pos,rot,1);

    }
    public Player(double[] pos)
    {
        super(pos,1);

    }
    public void move(double[] pos){
        move(pos[0],pos[1],pos[2]);
    }
    public void move(double x,double y,double z){
        double sinY = Math.sin(getRot()[1]);
        double cosY = Math.cos(getRot()[1]);
        this.pos[0] += cosY * x +sinY * z;
        this.pos[1] += y;
        this.pos[2] += -sinY * x + cosY * z;
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
                ", rot=" + Arrays.toString(rot) +
                '}';
    }
}

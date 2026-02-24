package gamefx.objects;

import gamefx.util.Quaternion;

import java.util.Arrays;

public class Player extends Object
{

    public String name;



    public Player(String name, double[] pos, Quaternion rot) {
        super(pos,rot,32);
        this.name = name;
        model = new PlayerModel();

    }
    public Player(String name, double[] pos) {
        super(pos, 32);
        this.name = name;
        model = new PlayerModel();
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
    public Block head;


    public class PlayerModel extends ObjectModel {
        public PlayerModel(){
          super();


          Object[] children = new Object[6];
          Block legl = new Block(Player.this,new double[]{0,0.75*size,0.125*size},Quaternion.zeroRot(),size, 0.75, 0.25,0.25);
          legl.getModel().offset(0,-0.325*size, 0);
          Block legr = new Block(Player.this,new double[]{0,0.75*size,-0.125*size},Quaternion.zeroRot(),size,0.75,0.25,0.25);
          legr.getModel().offset(0,-0.325*size, 0);
          Block body = new Block(Player.this,new double[]{0, 1.125*size,0},Quaternion.zeroRot(),size,0.75,0.5,0.25);
          Block arml = new Block(Player.this,new double[]{0, 1.375*size,0.25*size},Quaternion.zeroRot(),size,0.75,0.25,0.25);
          arml.getModel().offset(0,-0.25*size,0.125*size);
          Block armr = new Block(Player.this,new double[]{0,1.375*size,-0.25*size},Quaternion.zeroRot(),size,0.75,0.25,0.25);
          armr.getModel().offset(0,-0.25*size,-0.125*size);
          head = new Block(Player.this,new double[]{0,1.5*size,0},Quaternion.zeroRot(),0.5*size);
          head.getModel().offset(0,0.25*size,0);


          children[0] = head;
          children[1] = legl;
          children[2] = legr;
          children[3] = body;
          children[4] = arml;
          children[5] = armr;
          setChildren(children);
        }
    }


    @Override
    public String toString() {
        return "Player{" +
                "pos=" + Arrays.toString(pos) +
                '}';
    }
}

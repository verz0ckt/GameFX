package gamefx.multiplayer;

import gamefx.objects.Block;
import gamefx.objects.Object;
import gamefx.objects.Plane;
import gamefx.objects.Player;
import gamefx.util.Quaternion;

import java.nio.charset.StandardCharsets;

public class BufferHelper {
    public static final byte[] endSequence = new byte[]{0x00, (byte) 0xFF};
    public static final byte[] forceStopSequence = new byte[]{(byte) 0xFF, (byte) 0xFF};
    public static final byte loginByte = 0x01;
    public static final byte logoutByte = 0x2;
    public static final byte updateByte = 0x03;

    //0x00 is start of endsequence
    public static final byte addFlag = 0x01;
    public static final byte removeFlag = 0x02;
    public static final byte updateFlag = 0x03;


    public static final int bufferSizeNewObject = 8*Double.BYTES+1;
    public static final int bufferSizeNewPlayer = Double.BYTES+16+1;
    public static final int bufferSizeNewBlock = 11*Double.BYTES+1;

    public static final int basicUpdateSize = 8*Double.BYTES+4;
    public static final int playerUpdateSize = 12*Double.BYTES+1;


    public static int addFlagToIndex(int index,byte flag){
        return  (index << 2)+(flag & 0x3);
    }

    public static int getBufferSizeOfNew(Object o){
        switch (o){
            case Player _ -> {
                return bufferSizeNewPlayer;
            }
            case Block _ -> {
                return bufferSizeNewBlock;
            }
            default -> {
                return bufferSizeNewObject;
            }
        }
    }
    public static int getBufferSizeOfNew(char id){
        switch (id&0xF0){
            case Player.ID -> {
                return (id&0x0F)+9+1;
            }
            case Block.ID -> {
                return bufferSizeNewBlock;
            }
            default -> {
                return bufferSizeNewObject;
            }
        }
    }

    public static Object createObjectFromBuffer(byte[] bytes, int offset){
        char id = (char) (bytes[offset] & 0xF0);
        offset++;
        if(id == Player.ID){
            int strLen = (bytes[offset-1] & 0x0F)+1;
            double size = getDoublefromBuffer(bytes,offset);
            String name = new String(bytes,offset+8,strLen, StandardCharsets.UTF_8);
            return new Player(name,size);
        }
        double x = getDoublefromBuffer(bytes,offset);
        offset+= 8;
        double y = getDoublefromBuffer(bytes,offset);
        offset+=8;
        double z = getDoublefromBuffer(bytes,offset);
        offset+=8;
        double w = getDoublefromBuffer(bytes,offset);
        offset+=8;
        double i = getDoublefromBuffer(bytes,offset);
        offset+=8;
        double j = getDoublefromBuffer(bytes,offset);
        offset+=8;
        double k = getDoublefromBuffer(bytes,offset);
        offset+=8;
        double size = getDoublefromBuffer(bytes,offset);
        switch (id){
            case Block.ID -> {
                offset+=8;
                double height = getDoublefromBuffer(bytes,offset);
                offset+=8;
                double length = getDoublefromBuffer(bytes,offset);
                offset+=8;
                double width = getDoublefromBuffer(bytes,offset);
                return new Block(new double[]{x,y,z},new Quaternion(w,i,j,k),size,height,length,width);
            }
            case Plane.ID -> {
                return new Plane(new double[]{x,y,z},new Quaternion(w,i,j,k),size);
            }
            default -> throw new IllegalStateException("Unexpected value: " + id);
        }
    }
    public static int updatePlayerFromBuffer(Player p, byte[] bytes, int offset){
        double x = getDoublefromBuffer(bytes,offset);
        offset+= 8;
        double y = getDoublefromBuffer(bytes,offset);
        offset+=8;
        double z = getDoublefromBuffer(bytes,offset);
        offset+=8;
        p.setPos(x,y,z);
        Quaternion rot = p.getRot();
        rot.setW(getDoublefromBuffer(bytes,offset));
        offset+=8;
        rot.setI(getDoublefromBuffer(bytes,offset));
        offset+=8;
        rot.setJ(getDoublefromBuffer(bytes,offset));
        offset+=8;
        rot.setK(getDoublefromBuffer(bytes,offset));
        offset+=8;
        rot = p.head.getRot();
        rot.setW(getDoublefromBuffer(bytes,offset));
        offset+=8;
        rot.setI(getDoublefromBuffer(bytes,offset));
        offset+=8;
        rot.setJ(getDoublefromBuffer(bytes,offset));
        offset+=8;
        rot.setK(getDoublefromBuffer(bytes,offset));
        offset+=8;
        return offset;
    }
    public static int updateObjectFromBuffer(Object o, byte[] bytes, int offset){
        double x = getDoublefromBuffer(bytes,offset);
        offset+= 8;
        double y = getDoublefromBuffer(bytes,offset);
        offset+=8;
        double z = getDoublefromBuffer(bytes,offset);
        offset+=8;
        o.setPos(x,y,z);
        Quaternion rot = o.getRot();
        rot.setW(getDoublefromBuffer(bytes,offset));
        offset+=8;
        rot.setI(getDoublefromBuffer(bytes,offset));
        offset+=8;
        rot.setJ(getDoublefromBuffer(bytes,offset));
        offset+=8;
        rot.setK(getDoublefromBuffer(bytes,offset));
        offset+=8;
        return offset;
    }
    public static double getDoublefromBuffer(byte[] buffer, int offset){
        long value = ((((long) buffer[offset++])&0xffL) <<56)
                +((((long) buffer[offset++]) & 0xffL) <<48)
                +((((long) buffer[offset++]) & 0xffL) <<40)
                +((((long) buffer[offset++]) & 0xffL) <<32)
                +((((long) buffer[offset++]) & 0xffL) <<24)
                +((((long) buffer[offset++]) & 0xffL) <<16)
                +((((long) buffer[offset++]) & 0xffL) <<8)
                +(((long) buffer[offset]) & 0xffL);
        return Double.longBitsToDouble(value);
    }
    public static int getIntfromBuffer(byte[] buffer,int offset){
        return  (((int)buffer[offset++] & 0xff)<<24)+(((int)buffer[offset++] & 0xff)<<16)+(((int)buffer[offset++] & 0xff)<<8)+((int)buffer[offset] & 0xff);
    }




    public static int addNewObjectToBuffer(Object o, byte[] buffer, int offset) {
        buffer[offset++] = (byte) o.getId();
        if(o instanceof Player){
            System.out.println("ho");
            byte[] name = ((Player) o).getName().getBytes(StandardCharsets.UTF_8);
            buffer[offset-1] |= (byte) (name.length-1);
            offset = addDoubleToBuffer(o.getSize(), buffer, offset);
            System.arraycopy(name, 0, buffer, offset, name.length);
            return offset+name.length;
        }
        System.out.println("hi");
        double[] pos = o.getPos();
        offset = addDoubleToBuffer(pos[0], buffer, offset);
        offset = addDoubleToBuffer(pos[1], buffer, offset);
        offset = addDoubleToBuffer(pos[2], buffer, offset);
        Quaternion rot = o.getRot();
        offset = addDoubleToBuffer(rot.getW(), buffer, offset);
        offset = addDoubleToBuffer(rot.getI(), buffer, offset);
        offset = addDoubleToBuffer(rot.getJ(), buffer, offset);
        offset = addDoubleToBuffer(rot.getK(), buffer, offset);
        offset = addDoubleToBuffer(o.getSize(),buffer,offset);
        switch (o){
            case Block b ->{
                offset = addDoubleToBuffer(b.getHeight(), buffer, offset);
                offset = addDoubleToBuffer(b.getLength(), buffer, offset);
                offset = addDoubleToBuffer(b.getWidth(), buffer, offset);
                return offset;
            }
            default -> {
                return offset;
            }
        }
    }
    public static int addObjectUpdateToBuffer(Object o, byte[] buffer, int offset) {
        double[] pos = o.getPos();
        offset = addDoubleToBuffer(pos[0], buffer, offset);
        offset = addDoubleToBuffer(pos[1], buffer, offset);
        offset = addDoubleToBuffer(pos[2], buffer, offset);
        Quaternion rot = o.getRot();
        offset = addDoubleToBuffer(rot.getW(), buffer, offset);
        offset = addDoubleToBuffer(rot.getI(), buffer, offset);
        offset = addDoubleToBuffer(rot.getJ(), buffer, offset);
        offset = addDoubleToBuffer(rot.getK(), buffer, offset);
        offset = addDoubleToBuffer(o.getSize(),buffer,offset);
        return offset;
    }
    public static int addUpdateToBuffer(Player player,char id,byte[] buffer,int offset){
        buffer[offset++] = (byte)id;
        double[] pos = player.getPos();
        offset = addDoubleToBuffer(pos[0],buffer,offset);
        offset = addDoubleToBuffer(pos[1],buffer,offset);
        offset = addDoubleToBuffer(pos[2],buffer,offset);
        Quaternion rot = player.getRot();
        offset = addDoubleToBuffer(rot.getW(),buffer,offset);
        offset = addDoubleToBuffer(rot.getI(),buffer,offset);
        offset = addDoubleToBuffer(rot.getJ(),buffer,offset);
        offset = addDoubleToBuffer(rot.getK(),buffer,offset);
        rot = player.head.getRot();
        offset = addDoubleToBuffer(rot.getW(),buffer,offset);
        offset = addDoubleToBuffer(rot.getI(),buffer,offset);
        offset = addDoubleToBuffer(rot.getJ(),buffer,offset);
        offset = addDoubleToBuffer(rot.getK(),buffer,offset);
        return offset;
    }

    public static int addDoubleToBuffer(double d, byte[] buffer, int offset){
        long value = Double.doubleToLongBits(d);
        buffer[offset++] = (byte) (value>>>56);
        buffer[offset++] = (byte) (value>>>48);
        buffer[offset++] = (byte) (value>>>40);
        buffer[offset++] = (byte) (value>>>32);
        buffer[offset++] = (byte) (value>>>24);
        buffer[offset++] = (byte) (value>>>16);
        buffer[offset++] = (byte) (value>>>8);
        buffer[offset++] = (byte) (value);
        return offset;
    }
    public static int addIntToBuffer(int i,byte[] buffer, int offset){
        buffer[offset++] = (byte) (i>>>24);
        buffer[offset++] = (byte) (i>>>16);
        buffer[offset++] = (byte) (i>>>8);
        buffer[offset++] = (byte) (i);
        return offset;
    }
}

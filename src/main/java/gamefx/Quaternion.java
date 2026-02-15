package gamefx;

public class Quaternion {
    private double w,i,j,k;

    private Quaternion(double w, double i, double j, double k) {
        this.w = w;
        this.i = i;
        this.j = j;
        this.k = k;
    }

    public double getW() {
        return w;
    }

    public double getI() {
        return i;
    }

    public double getJ() {
        return j;
    }

    public double getK() {
        return k;
    }

    public static Quaternion fromEuler(double theta,double x, double y, double z){
        double sinTheta = Math.sin(theta/2);
        return new Quaternion(Math.cos(theta/2),x*sinTheta,y*sinTheta,z*sinTheta);
    }
    public static Quaternion fromAngle(double theta,double x,double y,double z){
        double sinTheta = Math.sin(theta/2);
        return new Quaternion(Math.cos(theta/2),x*sinTheta,y*sinTheta,z*sinTheta).normalize();
    }
    public static Quaternion zeroRot(){
        return new Quaternion(1,0,0,0);
    }
    public Quaternion copy(){
        return new Quaternion(w,i,j,k);
    }
    public Quaternion getConjugate(){
        return new Quaternion(w,-i,-j,-k);
    }
    public Quaternion normalize(){
        double length = Math.sqrt(w*w+i*i+j*j+k*k);
        w= w/length;
        i = i/length;
        j= j/length;
        k= k/length;
        return this;
    }
    public Quaternion tryNormalize(){

        double length = w*w+i*i+j*j+k*k;
        if(length == 1){
            return this;
        }
        length = Math.sqrt(length);
        w= w/length;
        i = i/length;
        j= j/length;
        k= k/length;
        return this;
    }

    
    public void apply(double[] vec){
        double x = vec[0];
        double y = vec[1];
        double z = vec[2];
        double r20 = i+i;
        double r21 = j+j;
        double r22 = k+k;
        double z0 = j* z-k* y+w* x;
        double z1 = k* x-i* z+w* y;
        double z2 = i* y-j* x+w* z;
        vec[0] += r21*z2-r22*z1;
        vec[1] += r22*z0-r20*z2;
        vec[2] += r20*z1-r21*z0;

        //double[] vNew = v + (r+r) x (r x v + w*v);

    }
    public void multiply(Quaternion q2){
        /*
        q' =(i1+j1+k1+w1)*(i2+j2+k2+w2)
        i2*i1 = -1, i2*j1=k, i2*k1=-j, i2*w1=i,
        j2*i1=-k, j2*j1=-1, j2*k1=i, j2*w1=j,
        k2*i1=j, k2*j1=-i, k2*k1=-1, k2*w1=k,
        w2*i1=i, w2*j1=j, w2*k1=k, w2*w1=1,
        */
        final double w1 = this.w;
        final double i1 = this.i;
        final double j1 = this.j;
        final double k1 = this.k;
        final double w2 = q2.getW();
        final double i2 = q2.getI();
        final double j2 = q2.getJ();
        final double k2 = q2.getK();
        w = -i1 * i2 - j1 * j2 - k1 * k2 + w1 * w2;
        i = i1 * w2 + j1 * k2 - k1 * j2 + w1 * i2;
        j = -i1 * k2 + j1 * w2 + k1 * i2 + w1 * j2;
        k = i1 * j2 - j1 * i2 + k1 * w2 + w1 * k2;

    }
    public void multiplyGlobal(Quaternion q2) {
        final double w1 = this.w;
        final double i1 = this.i;
        final double j1 = this.j;
        final double k1 = this.k;
        final double w2 = q2.getW();
        final double i2 = q2.getI();
        final double j2 = q2.getJ();
        final double k2 = q2.getK();
        w = -i2 * i1 - j2 * j1 - k2 * k1 + w2 * w1;
        i = i2 * w1 + j2 * k1 - k2 * j1 + w2 * i1;
        j = -i2 * k1 + j2 * w1 + k2 * i1 + w2 * j1;
        k = i2 * j1 - j2 * i1 + k2 * w1 + w2 * k1;
    }
    public void replaceWith(double w,double i,double j,double k){
        this.w = w;
        this.i = i;
        this.j = j;
        this.k = k;
    }
    public void conjugateOf(Quaternion q2){
        this.w = q2.w;
        this.i = -q2.i;
        this.j = -q2.j;
        this.k = -q2.k;
    }
    public void replaceWith(Quaternion q2){
        this.w = q2.w;
        this.i = q2.i;
        this.j = q2.j;
        this.k = q2.k;
    }

}

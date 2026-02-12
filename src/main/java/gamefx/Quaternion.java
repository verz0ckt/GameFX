package gamefx;

public class Quaternion {
    public double i,j,k,w;

    private Quaternion(double w, double i, double j, double k) {
        this.i = i;
        this.j = j;
        this.k = k;
        this.w = w;
    }
    public static Quaternion fromEuler(double x,double y,double z,double theta){
        double sinTheta = Math.sin(theta/2);
        return new Quaternion(Math.cos(theta/2),x*sinTheta,y*sinTheta,z*sinTheta);
    }
    public static Quaternion fromAngle(double x,double y,double z,double theta){
        double sinTheta = Math.sin(theta/2);
        return new Quaternion(Math.cos(theta/2),x*sinTheta,y*sinTheta,z*sinTheta).normalize();
    }
    public Quaternion normalize(){
        double length = Math.sqrt(i*i+j*j+k*k+w*w);
        i = i/length;
        j= j/length;
        k= k/length;
        w= w/length;
        return this;
    }
    public Quaternion tryNormalize(){

        double length = i*i+j*j+k*k+w*w;
        if(length == 1){
            return this;
        }
        length = Math.sqrt(length);
        i = i/length;
        j= j/length;
        k= k/length;
        w= w/length;
        return this;
    }

    public void apply(double[] vec){
        double[] r = new double[]{i,j,k};
        double r20 = i+i;
        double r21 = j+j;
        double r22 = k+k;
        double z0 = r[1]* vec[2]-r[2]* vec[1]+w* vec[0];
        double z1 = r[2]* vec[0]-r[0]* vec[2]+w* vec[1];
        double z2 = r[0]* vec[1]-r[1]* vec[0]+w* vec[2];
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
        double i=this.i*q2.w+this.j*q2.k-this.k*q2.j+this.w*q2.i;
        double j= -this.i*q2.k+this.j*q2.w+this.k*q2.i+this.w*q2.j;
        double k= this.i*q2.j-this.j*q2.i+this.k*q2.w+this.w*q2.k;
        this.w=-this.i*q2.i-this.j*q2.j-this.k*q2.k+this.w*q2.w;
        this.i = i;
        this.j = j;
        this.k = k;
    }
    public void multiplyGlobal(Quaternion q2) {
        double i = q2.i * this.w + q2.j * this.k - q2.k * this.j + q2.w * this.i;
        double j = -q2.i * this.k + q2.j * this.w + q2.k * this.i + q2.w * this.j;
        double k = q2.i * this.j - q2.j * this.i + q2.k * this.w + q2.w * this.k;
        this.w = -q2.i * this.i - q2.j * this.j - q2.k * this.k + q2.w * this.w;
        this.i = i;
        this.j = j;
        this.k = k;
    }
}

package gamefx;


public class Matrix {
    private double m00,m01,m02,
                    m10,m11,m12,
                    m20,m21,m22;

    public Matrix(double[][] values){
        m00 = values[0][0];
        m01 = values[0][1];
        m02 = values[0][2];
        m10 = values[1][0];
        m11 = values[1][1];
        m12 = values[1][2];
        m20 = values[2][0];
        m21 = values[2][1];
        m22 = values[2][2];


    }

    public Matrix() {
        m00 = 1;
        m11 = 1;
        m22 = 1;
    }

    public Matrix(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22) {
        this.m00 = m00;this.m01 = m01;this.m02 = m02;
        this.m10 = m10;this.m11 = m11;this.m12 = m12;
        this.m20 = m20;this.m21 = m21;this.m22 = m22;
    }
    public void apply(double[] in, double[] out){
        final double v0 = in[0];
        final double v1 = in[1];
        final double v2 = in[2];
        out[0]= m00*v0+m01*v1+m02*v2;
        out[1]= m10*v0+m11*v1+m12*v2;
        out[2]= m20*v0+m21*v1+m22*v2;
    }
    public void apply(double[] vec){
        final double v0 = vec[0];
        final double v1 = vec[1];
        final double v2 = vec[2];
        vec[0]= m00*v0+m01*v1+m02*v2;
        vec[1]= m10*v0+m11*v1+m12*v2;
        vec[2]= m20*v0+m21*v1+m22*v2;
    }
    public void multiply(Matrix m2) {
        double a00 = m00, a01 = m01, a02 = m02;
        double a10 = m10, a11 = m11, a12 = m12;
        double a20 = m20, a21 = m21, a22 = m22;

        double b00 = m2.m00, b01 = m2.m01, b02 = m2.m02;
        double b10 = m2.m10, b11 = m2.m11, b12 = m2.m12;
        double b20 = m2.m20, b21 = m2.m21, b22 = m2.m22;

        m00 = a00*b00 + a01*b10 + a02*b20; m01 = a00*b01 + a01*b11 + a02*b21; m02 = a00*b02 + a01*b12 + a02*b22;
        m10 = a10*b00 + a11*b10 + a12*b20; m11 = a10*b01 + a11*b11 + a12*b21; m12 = a10*b02 + a11*b12 + a12*b22;
        m20 = a20*b00 + a21*b10 + a22*b20; m21 = a20*b01 + a21*b11 + a22*b21; m22 = a20*b02 + a21*b12 + a22*b22;
    }

    public void fromQuaternion(Quaternion q){
        final double w = q.getW();
        final double i = q.getI();
        final double j = q.getJ();
        final double k = q.getK();

        final double iw = i*w;
        final double ii = i*i;
        final double ij = i*j;
        final double ik = i*k;
        final double jw = j*w;
        final double jj = j*j;
        final double jk = j*k;
        final double kw = k*w;
        final double kk = k*k;

        m00 = 1-2*(jj+kk); m01 = 2*(ij-kw); m02 = 2*(ik+jw);
        m10 = 2*(ij+kw); m11 = 1- 2*(ii+kk); m12 = 2*(jk-iw);
        m20 = 2*(ik-jw); m21 = 2*(jk+iw); m22 = 1-2*(ii+jj);
    }
    public void add(double x){
        m00 += x;m01 += x;m02 += x;
        m10 += x;m11 += x;m12 += x;
        m20 += x;m21 += x;m22 += x;
    }
    public void subtract(double x){
        m00 -= x;m01 -= x;m02 -= x;
        m10 -= x;m11 -= x;m12 -= x;
        m20 -= x;m21 -= x;m22 -= x;
    }
    public void multiply(double x){
        m00 *= x;m01 *= x;m02 *= x;
        m10 *= x;m11 *= x;m12 *= x;
        m20 *= x;m21 *= x;m22 *= x;
    }
    public void divide(double x){
        m00 /= x;m01 /= x;m02 /= x;
        m10 /= x;m11 /= x;m12 /= x;
        m20 /= x;m21 /= x;m22 /= x;
    }
}

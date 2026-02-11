package gamefx;

public class Matrix {
    private double[][] matrix;
    public Matrix(int height,int width){
        matrix = new double[height][width];
    }
    public Matrix(double[][] values){
        this.matrix = values;
    }
    public double[] apply(double[] vec){
        if(vec.length != matrix[0].length)
            throw new IllegalArgumentException();
        double[] newVec = new double[vec.length];
        for(int row = 0; row < matrix.length;row++){
            for(int i = 0;i< matrix[i].length;i++){
                newVec[row]+= matrix[row][i]*vec[i];
            }
        }
        return newVec;
    }
    public void add(int x){
        for(int row = 0; row < matrix.length;row++){
            for(int i = 0;i< matrix[i].length;i++){
                matrix[row][i]+= x;
            }
        }
    }
    public void multiply(int x){
        for(int row = 0; row < matrix.length;row++){
            for(int i = 0;i< matrix[i].length;i++){
                matrix[row][i]*= x;
            }
        }
    }
}

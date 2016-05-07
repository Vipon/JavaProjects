package Matrix;

public class Matrix {
    
    private int hight;
    private	int	length;
    private double[][] matrix;
    
    public static Matrix randM(int hight, int length) {
    
        double[][] rand = new double[hight][length];
        
        for (int i = 0; i < hight; i++)
            for (int j = 0; j < length; j++)
                rand[i][j] = 1000*Math.random();
        return new Matrix(rand);
    }
    
    public Matrix(int hight, int length) {
    
        this.hight = hight;
        this.length = length;
        matrix = new double[hight][length];
    }
    
    public Matrix(double[][] data) {
    
        hight = data.length;
        length = data[0].length;
        matrix = new double[hight][length];
        
        for (int i = 0; i < hight; i++)
            for (int j = 0; j < length; j++)
                matrix[i][j] = data[i][j];
    }
    
    public double[] getArray() {
        
        double[] result = new double[hight*length];
        for (int i = 0; i < hight; i++)
            for (int j = 0; j < length; j++)
                result[i*length + j] = matrix[i][j];
        return result;
    }
    
    public double[][] getDarray() {
        
        return matrix;
    }
    
    public void setDarray(double[][] m) {
        
        matrix = m;
    }
    
    public int getHeight() {
        
        return hight;
    }
    
    public int getWigth() {
        
        return length;
    }
    
    public Matrix transpone() {
    
        Matrix M = new Matrix(hight, length);
        
        for (int i = 0; i < hight; i++)
            for (int j = 0; j < length; j++)
                M.matrix[j][i] = matrix[i][j];
        return M;
    }
    
    public Matrix add(Matrix M) {
    
        if ((hight != M.hight) || (length != M.length))
            throw new RuntimeException("ERROR: Illegal matrix dimensions.");
        
        Matrix result = new Matrix(hight, length);
        
        for (int i = 0; i < hight; i++)
            for (int j = 0; j < length; j++)
                result.matrix[i][j] = matrix[i][j] + M.matrix[i][j];
        return result;
    }
    
    public Matrix sub(Matrix M) {
    
        if ((hight != M.hight) || (length != M.length))
            throw new RuntimeException("ERROR: Illegal matrix dimensions.");
        
        Matrix result = new Matrix(hight, length);
        
        for (int i = 0; i < hight; i++)
            for (int j = 0; j < length; j++)
                result.matrix[i][j] = matrix[i][j] - M.matrix[i][j];
        return result;
    }
    
    public Matrix mul(Matrix M) {
    
        if (length != M.hight)
            throw new RuntimeException("ERROR: Illegal matrix dimensions.");
        
        Matrix result = new Matrix(hight, M.length);
        
        for (int i = 0; i < result.hight; i++)
            for (int j = 0; j < result.length; j++)
                for (int k = 0; k < length; k++)
                    result.matrix[i][j] += matrix[i][k] * M.matrix[k][j];
        return result;
    }
    
    public void print(){
    
        for (int i = 0; i < hight; i++) {
            for (int j = 0; j < length; j++)
                System.out.printf("%9.4f ", matrix[i][j]);
            System.out.println();
        }
    }
}

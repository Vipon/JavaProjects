package Affine;

import Matrix.*;

public class Affine{
    
    public enum Mode {NEAREST_NEIGHBOR, ARITHMETIC_AVERAGE};
    private Mode mode;
    
    private int curWidth;
    private int curHeight;
    private int newWidth;
    private int newHeight;
    
    private double rad;
    private double koeff;
    
    private Matrix rotateMatrix;
    private Matrix arotateMatrix;
    
    public Affine(double angle, double koeff, Mode mode) {
        
        //this.angle = angle;
        this.koeff = koeff;
        this.mode = mode;
        
        rad = Math.toRadians(angle);
        double[][] rotM = new double[2][2];
        rotM[0][0] = Math.cos(rad);
        rotM[0][1] = -Math.sin(rad);
        rotM[1][0] = Math.sin(rad);
        rotM[1][1] = Math.cos(rad);
        rotateMatrix = new Matrix(rotM);
        
        rotM[0][1] = Math.sin(rad);
        rotM[1][0] = -Math.sin(rad);
        arotateMatrix = new Matrix(rotM);
    }
    
    public Matrix transform(Matrix m) {
        
        curWidth = m.getWigth();
        curHeight = m.getHeight();
        
        double[][] Old = m.getDarray();
        newBounds(Old);
        double[][] New = new double[newHeight][newWidth];
       
        switch(mode) {
        
        case NEAREST_NEIGHBOR:
            nearestNeighbor(Old, New);
            break;
        case ARITHMETIC_AVERAGE:
            arithmeticAverage(Old, New);
            break;
        default:
            break;
        }
        return new Matrix(New);
    }
    
    private void newBounds(double[][] Old) {
        
        Matrix oldPoint = new Matrix(2,1);
        Matrix newPoint = new Matrix(2,1);
        double[][] Point = new double[2][1];
        
        double[] p;
        double x;
        double y;
        double maxX = 0;
        double maxY = 0;
        //left-up
        Point[0][0] = -curWidth/2;
        Point[1][0] = curHeight/2 - 1;
        oldPoint.setDarray(Point);
        newPoint = rotateMatrix.mul(oldPoint);
        p = newPoint.getArray();
        x = p[0];
        y = p[1];
        if(Math.abs(y) > maxY) maxY = Math.abs(y);
        if(Math.abs(x) > maxX) maxX = Math.abs(x);
        //right-up
        Point[0][0] = curWidth/2 - 1;
        Point[1][0] = curHeight/2 - 1;
        oldPoint.setDarray(Point);
        newPoint = rotateMatrix.mul(oldPoint);
        p = newPoint.getArray();
        x = p[0];
        y = p[1];
        if(Math.abs(y) > maxY) maxY = Math.abs(y);
        if(Math.abs(x) > maxX) maxX = Math.abs(x);
        //left-down
        Point[0][0] = -curWidth/2;
        Point[1][0] = -curHeight/2;
        oldPoint.setDarray(Point);
        newPoint = rotateMatrix.mul(oldPoint);
        p = newPoint.getArray();
        x = p[0];
        y = p[1];
        if(Math.abs(y) > maxY) maxY = Math.abs(y);
        if(Math.abs(x) > maxX) maxX = Math.abs(x);
        //right-down
        Point[0][0] = curWidth/2 - 1;
        Point[1][0] = -curHeight/2;
        oldPoint.setDarray(Point);
        newPoint = rotateMatrix.mul(oldPoint);
        p = newPoint.getArray();
        x = p[0];
        y = p[1];
        if(Math.abs(y) > maxY) maxY = Math.abs(y);
        if(Math.abs(x) > maxX) maxX = Math.abs(x);
        
        newWidth = 1 + (int) Math.ceil(2*maxX);
        newHeight = 1 + (int) Math.ceil(2*maxY);
        newWidth *= koeff;
        newHeight *= koeff;
    }
    
    private void nearestNeighbor(double[][] Old, double[][] New) {
        
        int oldX = 0;
        int oldY = 0;
        double[] p;
        double[][] Point = new double[2][1];
        Matrix oldPoint = new Matrix(2,1);
        Matrix newPoint = new Matrix(2,1);
        
        for(int i = 0; i < newHeight; ++i)
            for(int j = 0; j < newWidth; ++j)  
                if(New[i][j] == 0) {
                    
                    Point[0][0] = (j - newWidth/2)/koeff;
                    Point[1][0] = (i - newHeight/2)/koeff;
                    newPoint.setDarray(Point);
                    oldPoint = arotateMatrix.mul(newPoint);
                    p = oldPoint.getArray();
                    
                    oldX = (int)(p[0] + curWidth/2);
                    oldY = (int)(p[1] + curHeight/2);
                    if((oldX < curWidth) && (oldX >= 0) && (oldY < curHeight) && (oldY >= 0))                         
                        New[i][j] = Old[oldY][oldX];
                }
    }
    
    private void arithmeticAverage(double[][] Old, double[][] New) {
        
        int oldX1 = 0;
        int oldY1 = 0;
        int oldX2 = 0;
        int oldY2 = 0;
        
        double[] p;
        double[][] Point = new double[2][1];
        Matrix oldPoint = new Matrix(2,1);
        Matrix newPoint = new Matrix(2,1);
        
        for(int i = 0; i < newHeight; ++i)
            for(int j = 0; j < newWidth; ++j)  
                if(New[i][j] == 0) {
                    
                    Point[0][0] = ((double)j - (double)newWidth/2)/koeff;
                    Point[1][0] = ((double)i - (double)newHeight/2)/koeff;
                    newPoint.setDarray(Point);
                    oldPoint = arotateMatrix.mul(newPoint);
                    p = oldPoint.getArray();
                    
                    double x = (p[0] + (double)curWidth/2);
                    double y = (p[1] + (double)curHeight/2);
                    oldX1 = (int)(p[0] + (double)curWidth/2);
                    oldY1 = (int)(p[1] + (double)curHeight/2);
                    oldX2 = (int)Math.ceil((p[0] + (double)curWidth/2));
                    oldY2 = (int)Math.ceil((p[1] + (double)curHeight/2));
                    
                   /*System.out.println(oldX1);
                    System.out.println(oldX2);*/
                    int R, G, B;
                    int R0, G0, B0;
                    int R1, G1, B1;
                    int R2, G2, B2;
                    int R3, G3, B3;
                    int Head = 0xFF;
                    if ((oldX2 == oldX1) && (oldX2 < curWidth) && (oldX1 >= 0) && (oldY2 < curHeight) && (oldY1 >= 0)) {
                        
                        B0 = ((int)(Old[oldY1][oldX1])) & 0x000000FF;
                        B2 = ((int)(Old[oldY2][oldX1])) & 0x000000FF;
                        
                        G0 = (((int)(Old[oldY1][oldX1])) >> 8) & 0x000000FF;
                        G2 = (((int)(Old[oldY2][oldX1])) >> 8) & 0x000000FF;
                        
                        R0 = (((int)(Old[oldY1][oldX1])) >> 16) & 0x000000FF;
                        R2 = (((int)(Old[oldY2][oldX1])) >> 16) & 0x000000FF;
                        
                        B =  (int)(B0*((double)oldY2 - y) + B2*(y - (double)oldY1)) & 0x000000FF;
                        G =  (int)(G0*((double)oldY2 - y) + G2*(y - (double)oldY1)) & 0x000000FF;
                        R =  (int)(R0*((double)oldY2 - y) + R2*(y - (double)oldY1)) & 0x000000FF;
                        
                        New[i][j] = ((Head<<24) + (R<<16) + (G<<8) + B);
                    } else if ((oldY1 == oldY2) && (oldX2 < curWidth) && (oldX1 >= 0) && (oldY2 < curHeight) && (oldY1 >= 0)) {
                        
                        B0 = ((int)(Old[oldY1][oldX1])) & 0x000000FF;
                        B1 = ((int)(Old[oldY1][oldX2])) & 0x000000FF;
                        
                        G0 = (((int)(Old[oldY1][oldX1])) >> 8) & 0x000000FF;
                        G1 = (((int)(Old[oldY1][oldX1])) >> 8) & 0x000000FF;
                        
                        R0 = (((int)(Old[oldY1][oldX1])) >> 16) & 0x000000FF;
                        R1 = (((int)(Old[oldY1][oldX2])) >> 16) & 0x000000FF;
                        
                        B =  (int)(B0*((double)oldX2 - x) + B1*(x - (double)oldX1)) & 0x000000FF;
                        G =  (int)(G0*((double)oldX2 - x) + G1*(x - (double)oldX1)) & 0x000000FF;
                        R =  (int)(R0*((double)oldX2 - x) + R1*(x - (double)oldX1)) & 0x000000FF;
                        
                        New[i][j] = ((Head<<24) + (R<<16) + (G<<8) + B);
                    } else if((oldX2 < curWidth) && (oldX1 >= 0) && (oldY2 < curHeight) && (oldY1 >= 0)) {
                        
                        B0 = ((int)(Old[oldY1][oldX1])) & 0x000000FF;
                        B1 = ((int)(Old[oldY1][oldX2])) & 0x000000FF;
                        B2 = ((int)(Old[oldY2][oldX1])) & 0x000000FF;
                        B3 = ((int)(Old[oldY2][oldX2])) & 0x000000FF;
                        
                        G0 = (((int)(Old[oldY1][oldX1])) >> 8) & 0x000000FF;
                        G1 = (((int)(Old[oldY1][oldX2])) >> 8) & 0x000000FF;
                        G2 = (((int)(Old[oldY2][oldX1])) >> 8) & 0x000000FF;
                        G3 = (((int)(Old[oldY2][oldX2])) >> 8) & 0x000000FF;
                        
                        R0 = (((int)(Old[oldY1][oldX1])) >> 16) & 0x000000FF;
                        R1 = (((int)(Old[oldY1][oldX2])) >> 16) & 0x000000FF;
                        R2 = (((int)(Old[oldY2][oldX1])) >> 16) & 0x000000FF;
                        R3 = (((int)(Old[oldY2][oldX2])) >> 16) & 0x000000FF;
                        
                        B =  (int)((B0*((double)oldX2 - x)*((double)oldY2 - y) +
                                    B1*(x - (double)oldX1)*((double)oldY2 - y) +
                                    B2*((double)oldX2 - x)*(y - (double)oldY1) +
                                    B3*(x - (double)oldX1)*(y - (double)oldY1))) & 0x000000FF;
                        G =  (int)((G0*((double)oldX2 - x)*((double)oldY2 - y) +
                                    G1*(x - (double)oldX1)*((double)oldY2 - y) +
                                    G2*((double)oldX2 - x)*(y - (double)oldY1) +
                                    G3*(x - (double)oldX1)*(y - (double)oldY1))) & 0x000000FF;
                        R =  (int)((R0*((double)oldX2 - x)*((double)oldY2 - y) +
                                    R1*(x - (double)oldX1)*((double)oldY2 - y) +
                                    R2*((double)oldX2 - x)*(y - (double)oldY1) +
                                    R3*(x - (double)oldX1)*(y - (double)oldY1))) & 0x000000FF;
                       
                        New[i][j] = ((Head<<24) + (R<<16) + (G<<8) + B);
                    }
                }
               
    }
}
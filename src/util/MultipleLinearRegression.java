package util;

/*************************************************************************
 *  Compilation:  javac -classpath jama.jar:. MultipleLinearRegression.java
 *  Execution:    java  -classpath jama.jar:. MultipleLinearRegression
 *  Dependencies: jama.jar
 *  
 *  Compute least squares solution to X beta = y using Jama library.
 *  Assumes X has full column rank.
 *  
 *       http://math.nist.gov/javanumerics/jama/
 *       http://math.nist.gov/javanumerics/jama/Jama-1.0.1.jar
 *
 *************************************************************************/

import Jama.Matrix;
import Jama.QRDecomposition;

public class MultipleLinearRegression {
    private final int N;        // number of 
//    private final int p;        // number of dependent variables
    private final Matrix beta;  // regression coefficients
    private final Matrix residuals;  // residuals
    private double SSE;         // sum of squared
    private double SST;         // sum of squared

    public MultipleLinearRegression(double[][] x, double[] y) {
        if (x.length != y.length) throw new RuntimeException("dimensions don't agree");
        N = y.length;
  //      p = x[0].length;

        Matrix X = new Matrix(x);

        // create matrix from vector
        Matrix Y = new Matrix(y, N);

        // find least squares solution
        QRDecomposition qr = new QRDecomposition(X);
        beta = qr.solve(Y);


        // mean of y[] values
        double sum = 0.0;
        for (int i = 0; i < N; i++)
            sum += y[i];
        double mean = sum / N;
        
        System.out.println("mean " + mean);
        
        // total variation to be accounted for
        for (int i = 0; i < N; i++) {
            double dev = y[i] - mean;
            SST += dev*dev;
        }

        System.out.println("SST " + SST);
        // variation not accounted for
        residuals = X.times(beta).minus(Y);
        SSE = residuals.norm2() * residuals.norm2();
       
        System.out.println("SSE " + SSE);
        System.out.println("R2 " + (1.0 - SSE/SST));
    }

    public double beta(int j) {
        return beta.get(j, 0);
    }
    
    public double residuals(int j) {
        return residuals.get(j, 0);
    }

    public double R2() {
        return 1.0 - SSE/SST;
    }

//    public static void main(String[] args) {
//        double[][] x = { {  1,  10,  20 },
//                         {  1,  20,  40 },
//                         {  1,  40,  15 },
//                         {  1,  80, 100 },
//                         {  1, 160,  23 },
//                         {  1, 200,  18 } };
//        double[] y = { 243, 483, 508, 1503, 1764, 2129 };
//        MultipleLinearRegression regression = new MultipleLinearRegression(x, y);
//
//        System.out.printf("%.2f + %.2f beta1 + %.2f beta2  (R^2 = %.2f)\n",
//                      regression.beta(0), regression.beta(1), regression.beta(2), regression.R2());
//    }
}
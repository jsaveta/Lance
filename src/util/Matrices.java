package util;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class Matrices {
 /**
  * The difference between 1 and the smallest exactly representable number
  * greater than one. Gives an upper bound on the relative error due to
  * rounding of floating point numbers.
  */
	
 /** MachineEpsilon */
 public static double MACH_EPSILON =  2.220446049250313E-32;
 
 
 /**
  * Updates MACHEPS for the executing machine.
  */
 public static void updateMacheps() {
	 MACH_EPSILON = 1;
  do
	  MACH_EPSILON /= 2;
  while (1 + MACH_EPSILON / 2 != 1);
 }

 
	/**
	 * Compute the pseudo inverse of a Matrix.
	 * 
	 * if USV* is the SingularValueDecomposition of M, then the pseudo inverse
	 * is V(S+)U* where S+ is the pseudo inverse of S. S being diagonal, its pseudo
	 * inverse is computed by taking the inverse of the element of its diagonal, 
	 * except for elements smaller that MACH_EPSI*max_size(m)*max(S)
	 * (see http://en.wikipedia.org/wiki/Moore–Penrose_pseudoinverse#Construction)
	 * 
	 * @param m Matrix
	 * @return its pseudo inverse (m+)
	 */
	static public Matrix pinv( Matrix m) {
		SingularValueDecomposition svdM = m.svd();
		
		Matrix S = svdM.getS();
		Matrix U = svdM.getU();
		Matrix V = svdM.getV();
//		System.out.println("U=\n"+JamaU.matToString(U));
//		System.out.println("S=\n"+JamaU.matToString(S));
//		System.out.println("V=\n"+JamaU.matToString(V));
		
		// Compute inverse of a diagonal matrix : inverse element of diagonal.
		// Only elements greater than MACH_EPSI*max_size(m)*max(S) are inversed
		double theta = MACH_EPSILON * Math.max(S.getColumnDimension(),S.getRowDimension());
		double maxS = -Double.MAX_VALUE;
		for (int row = 0; row < S.getRowDimension(); row++) {
			for (int col = 0; col < S.getColumnDimension(); col++) {
				if (S.get(row, col)>maxS) {
					maxS = S.get(row, col);
				}
			}
		}
		theta = theta * maxS;
		// inverse
		for (int i = 0; i < Math.min(S.getRowDimension(), S.getColumnDimension()); i++) {
			double val = S.get(i, i);
			if (val > theta) {
				S.set(i, i, 1.0/val);
			}
			else {
				S.set(i, i, 0);
			}
		}
//		System.out.println("S+=\n"+JamaU.matToString(S));
//		Matrix invSS = S.transpose().times(svdM.getS());
//		System.out.println("invSS =\n"+JamaU.matToString(invSS));
		
		Matrix pinvM = V.times(S.transpose().times(U.transpose()));
//		System.out.println("pinvM =\n"+JamaU.matToString(pinvM));
		// Pseudo Inverse

		//System.out.println("pinvM cols "+pinvM.getColumnDimension());
		//System.out.println("pinvM rows "+pinvM.getRowDimension());
		
//		for (int i = 0; i < pinvM.getRowDimension(); i++) {
//		    for (int j = 0; j < pinvM.getColumnDimension(); j++) {
//		        System.out.print(pinvM.getArray()[i][j] + " ");
//		    }
//		    System.out.print("\n");
//		}
		return pinvM;
	}
	
	/**
	  * Computes the Moore–Penrose pseudoinverse using the SVD method.
	  * 
	  * Modified version of the original implementation by Kim van der Linde.
	  */
	public static Matrix pinv2(Matrix x) {
		if (x.rank() < 1)
			return null;
		if (x.getColumnDimension() > x.getRowDimension())
			return pinv(x.transpose()).transpose();
		
		SingularValueDecomposition svdX = new SingularValueDecomposition(x);
		double[] singularValues = svdX.getSingularValues();
		double tol = Math.max(x.getColumnDimension(), x.getRowDimension()) * singularValues[0] * MACH_EPSILON;
		double[] singularValueReciprocals = new double[singularValues.length];
		for (int i = 0; i < singularValues.length; i++)
			singularValueReciprocals[i] = Math.abs(singularValues[i]) < tol ? 0 : (1.0 / singularValues[i]);
//		for (int i = 0; i < singularValues.length; i++)
//			System.out.println("sv["+i+"]="+singularValueReciprocals[i]);
		double[][] u = svdX.getU().getArray();
		double[][] v = svdX.getV().getArray();
		int min = Math.min(x.getColumnDimension(), u[0].length);
		
		double[][] inverse = new double[x.getColumnDimension()][x.getRowDimension()];
		for (int i = 0; i < x.getColumnDimension(); i++)
			for (int j = 0; j < u.length; j++)
				for (int k = 0; k < min; k++)
					inverse[i][j] += v[i][k] * singularValueReciprocals[k] * u[j][k];
		
		//System.out.println("--inverse rows "+new Matrix(inverse).getRowDimension());
		//System.out.println("--inverse cols "+new Matrix(inverse).getColumnDimension());
		
//		for (int i = 0; i < new Matrix(inverse).getRowDimension(); i++) {
//		    for (int j = 0; j < new Matrix(inverse).getColumnDimension(); j++) {
//		        System.out.print(inverse[i][j] + " ");
//		    }
//		    System.out.print("\n");
//		}
		return new Matrix(inverse);
	}
 
}
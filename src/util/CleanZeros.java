package util;

import java.util.ArrayList;

public class CleanZeros {
public static ArrayList<Integer> indexes = new ArrayList<Integer>();
//TODO FIX THIS! array index out of bounds!!! maybe because of zeros?!?!
//so we cannot calculate specific weights etc...
    public static double[][] cleanZeroRows(double[][] matrix) {
        //first find those that need to delete
    	// System.out.println("matrix[0].length  "+matrix[0].length);
       //  System.out.println("matrix.length "+matrix.length);

        boolean[] deleteThese = new boolean[matrix.length];
        int count = 0;

        int rows = matrix.length;
        int cols = matrix[0].length;
        for(int i=0;i<rows;i++){
            boolean allZeros = true;
            for(int j=0;j<cols;j++){
                if (matrix[i][j] != 0){
                    allZeros = false;
                    break;
                }
            }           
            deleteThese[i] = allZeros;
            if (allZeros){
                count++;
            }
        }

        if (count == 0){
            return matrix;
        }else if (count == matrix.length){
            return new double[0][0];
        }else{
            double[][] newMatrix = new double[rows - count][cols];
            int idx = -1;
            for(int i=0;i<rows;i++){
                if (!deleteThese[i]){
                	idx++;
                    newMatrix[idx] = matrix[i];
                }
                else{
                	indexes.add(i);
                }
               
            }
  //          System.out.println("newMatrix[0].length  "+newMatrix[0].length);
  //          System.out.println("newMatrix.length "+newMatrix.length);
            return newMatrix;
        }
    }

//    /**
//     * expects N\nM\ne1 e2 e3 e4 e5 e6 e7...
//     * @param in 
//     * @return
//     */
//    private static double[][] read(InputStream in) {
//        Scanner scan = new Scanner(in);
//        int rows = scan.nextInt();
//        int cols = scan.nextInt();
//        double[][] matrix = new double[rows][cols];
//        for(int i=0;i<rows;i++){
//            for(int j=0;j<cols;j++){
//                matrix[i][j] = scan.nextDouble();
//            }           
//        }
//        return matrix;
//    }
//
    private static void write(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                System.out.print(matrix[i][j]);
                System.out.print(" ");
            }           
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    public static double[][] transpose(double[][] m) {
    	
        int r = m.length;
        int c = m[0].length;
        double[][] t = new double[c][r];
        for (int i = 0; i < r; ++i) {
            for (int j = 0; j < c; ++j) {
                t[j][i] = m[i][j];
            }
        }
        return t;
    }

    /**
     * @param args
     * @throws UnsupportedEncodingException 
     */
//    public static void main(String[] args) throws UnsupportedEncodingException {
////      double[][] matrix = readFromConsole(System.in);
//        String input = "3\n3\n1 0 2\n0 0 0\n3 0 4\n";
//        double[][] matrix = read(new ByteArrayInputStream(input.getBytes("UTF-8")));
//
//        write(matrix,System.out);
//        double[][] rowsCleaned = cleanZeroRows(matrix);
//        write(rowsCleaned,System.out);
//        double[][] transposed = transpose(rowsCleaned);
//        write(transposed,System.out);
//        double[][] colsCleaned = cleanZeroRows(transposed);
//        write(colsCleaned,System.out);
//        double[][] transposedBack = transpose(colsCleaned);
//        write(transposedBack,System.out);
//    }

}
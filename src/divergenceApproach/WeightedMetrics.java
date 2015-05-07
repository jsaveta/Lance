package divergenceApproach;
import java.util.ArrayList;


public class WeightedMetrics {
	public static double calculateArithmeticMean(ArrayList<Double> w){
		double sum = 0;
	    for(int i = 0; i < w.size(); i++)
	    {
	        sum += w.get(i);
	    }
	    return sum/w.size();
	}
	
	public static double calculateStandardDeviation(ArrayList<Double> w, double mean){
		double squareSum = 0;
		for (int i = 0; i < w.size(); i++) {
			squareSum += Math.pow(w.get(i) - mean, 2);
		}
		return Math.sqrt((squareSum) / (w.size() - 1));
	}
	

}

package divergenceApproach;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class KeepOnlyTransformedINWeighted {

	private static ArrayList<String> instanceToRemove;
	private static ArrayList<String> mappings;
	private static ArrayList<Double> weights;
	
	public static void deleteNonInstanceMatchings(File file){
		String str = null;
	    try
	    {
	        BufferedReader in = new BufferedReader(new FileReader(file));   
	        File fout = new File(file.getPath().replace(".txt", "")+"Weighted.txt");
	    	FileOutputStream fos = new FileOutputStream(fout);
	    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	    	
	        while ((str = in.readLine()) != null)
	        {	
				/*customize this*/
	        	str = str.replace("|", " ");
	        	String[] parts = str.split(" ");
	           	bw.write(parts[0]+" "+parts[1]+"\n");
	        }
	        in.close();
	        bw.close();
	    }
	        catch (Exception e) {
	    }
	}
	
	public static void deleteWeightsFromMatchingsLDIMBENCH(File file){
		String str = null;
		instanceToRemove= new ArrayList<String> ();
		mappings = new ArrayList<String>();
		weights = new ArrayList<Double>();
    	
	    try
	    {
	        BufferedReader in = new BufferedReader(new FileReader(file));
	        
	        File fout = new File(file.getPath().replace(".txt", "")+"Weighted.txt");
	    	FileOutputStream fos = new FileOutputStream(fout);
	    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	        
	        while ((str = in.readLine()) != null)
	        {   
			/*Customize here if you want to keep only transformed (w = 1) etc*/
	        	String[] parts = str.split(" ");
				mappings.add(parts[0] +" "+parts[1]);
				weights.add(Double.parseDouble(parts[2])); 
				bw.write(parts[0]+" "+parts[1]+"\n");
	        	
	        }
	        in.close();
	        bw.close();
	    }
	        catch (Exception e) {
	    }
	}
	
	public static ArrayList<Double> getWeightArrayList(){
		return weights;
	}
	
	public static ArrayList<String> getMappings(){
		return mappings;
	}
	
	
 /*   public static void main(String[] args) {   
    	File file2 = new File("D:\\datasetsForExperimentsFromSPIMBENCH_NEW_100_MERGED\\10K\\structure\\mappings.txt");
    	deleteWeightsFromMatchingsLDIMBENCH(file2);
    	
    	File file1 = new File("D:\\datasetsForExperimentsFromSPIMBENCH_NEW_100_MERGED\\10K\\structure\\OtOMappings.txt");
    	deleteNonInstanceMatchings(file1);
    }*/
}








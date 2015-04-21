package rescal.turtleRescalTripleFinder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import main.TestDriver;
import properties.Configurations;
import util.RdfUtils;

public class RescalStarter {
	private static Map<String, Map<String, Double>> gsWeighted= new HashMap<String, Map<String, Double>>();
	
	public RescalStarter(String source, String target, String gold_standard) {
		try{
           Map<String, ArrayList<Double>> uriMatrixD1 = runProcess(source);
          //System.out.println("uriMatrixD1 size: " + uriMatrixD1.size() +" source : " + source);
           Map<String, ArrayList<Double>> uriMatrixD2 = runProcess(target);
          //System.out.println("uriMatrixD2 size: " + uriMatrixD2.size()+" target : " + target);
           Map<String, Map<String, Double>> gs = createGSMap(gold_standard);
          //System.out.println("gs size: " + gs.size());
           @SuppressWarnings("unused")
		Map<String, Map<String, Double>> gsWeightedTemp = calculateCosine(uriMatrixD1, uriMatrixD2, gs);
          //System.out.println("gsWeighted size: " + gsWeightedTemp.size());
           
        }catch(Exception e){
        	System.err.println(e.getMessage());
        	System.err.println("Problem with java-python bridge occured. Please clean your datasets (broken URIs)");
        }
    }
    
    
  
    public static Map<String, Map<String,Double>> createGSMap(String file) throws IOException{
    	Map<String, Map<String, Double>> gs = new HashMap<String, Map<String, Double>>();
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(file));
    	String line = null;
    	while ((line = reader.readLine()) != null) {
    		String[] temp = line.split(" ");
    		Map <String,Double> value= new HashMap<String,Double> ();
		    value.put(temp[1], Double.parseDouble("0.0"));
    		gs.put(temp[0], value);
    	}
		return gs;
    }
    
    public static Map<String, ArrayList<Double>> runProcess(String file) throws IOException{
     	String decimalPositivePattern = "([0-9]*)\\.([0-9]*)";  
     	String decimalNegativePattern = "-([0-9]*)\\.([0-9]*)";
    	try {
    	  PrintWriter configWriter = new PrintWriter("config.ini", "UTF-8");
  	      configWriter.println("[paths]");
  	      configWriter.println("pathToRescal: "+ "../src/rescal/rescal.py/rescal/rescal.py");
  	      configWriter.println("pathToFile: "+file);
  	      configWriter.println("format: "+RdfUtils.findContentType(TestDriver.getConfigurations().getString(Configurations.GENERATED_DATA_FORMAT)));
  	      configWriter.close();
  	   }catch (IOException e) {
  	   System.err.println("Problem writing to the file config.ini");
  	   }

     String threshold = "1.0";
 	 String rank = TestDriver.getConfigurations().getString(Configurations.RESCAL_RANK);
 	 Process p = Runtime.getRuntime().exec("python ../src/rescal/turtleRescalTripleFinder/fileReader.py "+threshold +" "+rank);
     BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream())); 
     String response = "";
     String line = "";
     Map <String, ArrayList<Double>> uriMatrix = new HashMap <String, ArrayList<Double>>();
     ArrayList <Double> matrix = new ArrayList <Double>();
     while ((line = in.readLine()) != null) {
    	 response += line;
     }	 

     //System.out.println("python response: "+response);
     
	 String uri = "" ;
     String[] temp = response.split(" ");
     for(int l = 0 ; l < temp.length ; l++){
     //System.out.println("temp[l] : " + temp[l]);
	 if(!temp[l].equals(null) &&!temp[l].equals("") &&!temp[l].equals(".") && temp[l].substring(0,1).matches("[a-zA-Z]") ){
		 	if(temp[l].startsWith("http")){
				if(!matrix.isEmpty() && !uriMatrix.containsKey(uri)){
					uriMatrix.put(uri, matrix);
					matrix = new ArrayList <Double>();
					uri = temp[l];
					
				}
			}
      }
      else if( Pattern.matches(decimalPositivePattern, temp[l]) || Pattern.matches(decimalNegativePattern, temp[l])){
			double d = Double.parseDouble(temp[l]);
			matrix.add(Math.log(Math.abs(d)));//matrix.add(Math.log(d)); //matrix.add(d);
 
       }
	}
    in.close();

// iterate and display values
//     for (Entry<String, ArrayList<Double>> entry : uriMatrix.entrySet()) {
//    	 String key = entry.getKey();
//    	 ArrayList<Double> values = entry.getValue();
//    	 System.out.println("Key = " + key);
//    	 System.out.println("Values = " + values );
//     
//     }
     
     
     return uriMatrix;  
    }
    
    
    public static Map<String, Map<String, Double>> calculateCosine(Map<String, ArrayList<Double>> uriMatrixD1,Map<String, ArrayList<Double>> 
    uriMatrixD2,Map<String, Map<String, Double>> gs){
   
    ArrayList<Double> uA = null;
    ArrayList<Double> uPrimeA = null;
    Map<String,Double> value = null;
    Iterator<String> iteratorGS = gs.keySet().iterator();  
    while (iteratorGS.hasNext()) {  
       String key = iteratorGS.next().toString();
       //System.out.println("key " +key);
       if(uriMatrixD1.containsKey(key)){
      	 uA = uriMatrixD1.get(key);
         value = gs.get(key);
          
	         Iterator<String> internalIteratorGS = value.keySet().iterator();
	         while (internalIteratorGS.hasNext()) {  
	            String internalKey = internalIteratorGS.next().toString();
	            //System.out.println("internalKey " +internalKey);
	            if(uriMatrixD2.containsKey(internalKey)){
			            uPrimeA = uriMatrixD2.get(internalKey);
			            //System.out.println("uA " +uA.toString());
			            //System.out.println("uPrimeA " +uPrimeA.toString());
			            double cosine = cosineSimilarity(uA,uPrimeA);
			            //System.out.println("cosine " +cosine);
			            Map<String, Double> temp = new HashMap<String, Double>();
			            temp.put(internalKey, cosine);
			            gsWeighted.put(key, temp);
	            }
	         }
       }
    } 
  return gsWeighted;    	
  }
  
  public static double cosineSimilarity(ArrayList<Double> vectorA, ArrayList<Double> vectorB) {
	  	double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.size(); i++) {
	//    	if(!CosineUtil.isInfinite(vectorA.get(i)) && !CosineUtil.isInfinite(vectorB.get(i))){
	    		dotProduct += vectorA.get(i) * vectorB.get(i);
	    		normA += Math.pow(vectorA.get(i), 2);
	    		normB += Math.pow(vectorB.get(i), 2);
	//    	}
	    }   
	    if ((Math.sqrt(normA) * Math.sqrt(normB)) != 0.0)
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	    else return 0.0;
  }
  	        
    public static Map<String, Map<String, Double>> getGsWeighted(){
    	return Collections.synchronizedMap(gsWeighted); //new TreeMap<String, Map<String, Double>>(gsWeighted);
    }

    
    public static ArrayList<Double> getSArrayList(){
    	ArrayList<Double> S = new ArrayList<Double>();
    	
    	Iterator<String> iteratorGS = getGsWeighted().keySet().iterator();
    	while (iteratorGS.hasNext()) {
			 String key = iteratorGS.next().toString();
		     //System.out.println("key : " + key);
		     Map<String,Double> value = getGsWeighted().get(key);
		     
		     Iterator<String> internalIteratorGS = value.keySet().iterator();
		     String internalKey = internalIteratorGS.next().toString();
		     double cos = value.get(internalKey);
		     //System.out.println("cos : " + cos);
		     if(cos > 0.0){S.add(cos);}
    	}
    	return S;
    }
	    
    public static void main(String[] args) {
    	
	   	 try{
	   		new RescalStarter("generatedCreativeWorks-0008.ttl", "generatedCreativeWorksD2-0008.ttl", "generatedCreativeWorksGS-0008_final.txt");
	          
	       }catch(Exception e){
	       	System.err.println(e.getMessage());
	       	System.err.println("Problem with java-python bridge occured.");
	       }
	   }    
}

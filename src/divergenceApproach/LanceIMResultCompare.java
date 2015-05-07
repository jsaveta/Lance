package divergenceApproach;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class LanceIMResultCompare {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File file2 = new File("D:\\datasetsForExperimentsFromSPIMBENCH_NEW_100_MERGED\\50K\\value\\mappings.txt");
		KeepOnlyTransformedIMWeighted.deleteWeightsFromMatchingsLDIMBENCH(file2);
    	
    	File file1 = new File("D:\\datasetsForExperimentsFromSPIMBENCH_NEW_100_MERGED\\50K\\value\\IMMappings.txt");
    	KeepOnlyTransformedIMWeighted.deleteNonInstanceMatchings(file1);
    	
    	ArrayList<String> mappingsGS = KeepOnlyTransformedIMWeighted.getMappings();
		//System.out.println("mappings size "+mappingsGS.size());
		List<Double> weightsGS = KeepOnlyTransformedIMWeighted.getWeightArrayList();
		//System.out.println("weights size "+weightsGS.size());
		
		ArrayList<String> TPmappings = new ArrayList<String>();		
		ArrayList<Double> TPweights = new ArrayList<Double>();
		
		String path = "D:\\datasetsForExperimentsFromSPIMBENCH_NEW_100_MERGED\\50K\\value\\";
		LinkedList <String> M1 = new LinkedList<String>() ;
 		LinkedList <String> M2 = new LinkedList<String>() ;
 		File file_true_positive = new File(path+"IM-TRUE-POSITIVE_WEIGHTED.txt");
 		File file_false_positive = new File(path+"IM-FALSE-POSITIVE_WEIGHTED.txt");
 		File file_false_negative = new File(path+"IM-FALSE-NEGATIVE_WEIGHTED.txt");
		
		BufferedWriter output1 = null;
		BufferedWriter output2 = null;
		BufferedWriter output3 = null;
		        
		        try {
		         
		          output1 = new BufferedWriter(new FileWriter(file_true_positive));
		          output2 = new BufferedWriter(new FileWriter(file_false_positive));
		          output3 = new BufferedWriter(new FileWriter(file_false_negative));
		       
		        } catch ( IOException e ) {
		           e.printStackTrace();
		        }
		    
		
		
		 FileInputStream fstream1 = null;
		 FileInputStream fstream2 = null;
		try {
			fstream2 = new FileInputStream(path+"mappingsWeighted.txt"); 
			fstream1 = new FileInputStream(path+"IMMappingsWeighted.txt");
		} catch (FileNotFoundException e1) {
		
			e1.printStackTrace();
		}
 		  // Get the object of DataInputStream
 		  DataInputStream in1 = new DataInputStream(fstream1);
 		  DataInputStream in2 = new DataInputStream(fstream2);
 		  BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
 		  BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
 		  String SystemString ="";
 		  String MatchingString ="";
 		 
 		  String strLine1;
 		  String strLine2;
 		 LinkedList <String> Matchinglines = new LinkedList<String>() ;
 		 LinkedList <String> Resultlines = new LinkedList<String>() ; 
 		//create other ArrayList in order to save there each matching because readLine Method does not read each line of the file
 		 String[] Results = null ;
 		 String[] Matchings= null ;
		 String[] regExpSplit1;
		 String[] regExpSplit2;
 		  
 		  boolean found=false;
 		  
 		 //read all lines from file a and store it in MatchingLines
					
				while ((strLine1 = br1.readLine()) != null)   
					Resultlines.add(strLine1);
		
 		  
 		  //read all lines from file b and store it in MatchingLines
 		  
				while ((strLine2 = br2.readLine()) != null)   
					Matchinglines.add(strLine2);

				br2.close();
				br1.close();
				
				 
 		  for (int j = 0; j < Resultlines.size(); j++) {
 			  
 			regExpSplit1=LanceIMResultCompare.return_splitedarrays(Resultlines.get(j));
 			Results=LanceIMResultCompare.combineArrays(Results,regExpSplit1);
 		  }
 		  
 		 for (int i = 0; i < Matchinglines.size(); i++) {
 			 regExpSplit2=LanceIMResultCompare.return_splitedarrays(Matchinglines.get(i));
 			 Matchings=LanceIMResultCompare.combineArrays(Matchings,regExpSplit2);
 		 }
 		
 	
 		
 		for (int i = 0; i < Matchings.length; i++) {
 			String[] mac = Matchings[i].split(" ");
 			M1.add(mac[0]);
 			M2.add(mac[1]);
		 }

		 for (int t = 0; t < Results.length; t++) {
 			found=false;				  
 			SystemString=Results[t].trim();    
 	 		for (int p=0; p<Matchings.length; p++ ){	
 				if (SystemString.contains(Matchings[p].trim())){
					output1.write(Results[t]+"\n");
					TPweights.add(weightsGS.get(mappingsGS.indexOf(Results[t])));
					TPmappings.add(Results[t]);
					found=true;
					break;
 				}
 	 		}
 		    if (found==false){
 				String[] res = Results[t].split(" ");
 			 	LinkedList <String> R1 = new LinkedList<String>() ;
 			 	LinkedList <String> R2 = new LinkedList<String>() ;
 		 		R1.add(res[0]);
 		 		if(res.length>1)R2.add(res[1]); 
 		 		else R2.add(res[0]);
				if((M1.contains(R1.get(0)) && !M2.contains(R2.get(0))) || (!M1.contains(R1.get(0)) && M2.contains(R2.get(0)))){
 					output2.write(Results[t]+"\n");
 				}
 		 	}
 		}
 		output1.close();
 		output2.close();
			for (int k=0; k<Matchings.length; k++ ){
				found=false;
				MatchingString=Matchings[k].trim();
				for (int l = 0; l < Results.length; l++) {
 				String result=Results[l].trim();	
 				if (MatchingString.contains(result)){
					found=true;
					break;
 				}
			} 
 			if (found==false){output3.write(Matchings[k]+"\n");}
		}
			
	 	output3.close();
	 	
	 	LineNumberReader TPlineNumberReader = new LineNumberReader(new FileReader(file_true_positive));
 	 	TPlineNumberReader.skip(Long.MAX_VALUE);
 	 	int TPlines = TPlineNumberReader.getLineNumber();
 	 	
 	 	LineNumberReader FPlineNumberReader = new LineNumberReader(new FileReader(file_false_positive));
 	 	FPlineNumberReader.skip(Long.MAX_VALUE);
 	 	int FPlines = FPlineNumberReader.getLineNumber();
	 	
		 	
		LineNumberReader FNlineNumberReader = new LineNumberReader(new FileReader(file_false_negative));
	 	FNlineNumberReader.skip(Long.MAX_VALUE);
	 	int FNlines = FNlineNumberReader.getLineNumber();
	 	
 	
 	
 	
 	File file = new File(path+"IM-COUNTS_WEIGHTED.txt");
 	BufferedWriter lineCounts = new BufferedWriter(new FileWriter(file));
 	lineCounts.write("TRUE POSITIVE # : "+TPlines+"\n");
 	lineCounts.write("FALSE POSITIVE # : "+FPlines+"\n");
 	lineCounts.write("FALSE NEGATIVE # : "+FNlines+"\n");
 	lineCounts.close();

  	double TP_AM = WeightedMetrics.calculateArithmeticMean(TPweights); //arithmetic mean for TP
	System.out.println("Arithmetic Mean for TP "+TP_AM);
	
	double TP_SD = WeightedMetrics.calculateStandardDeviation(TPweights,TP_AM); //standard deviation for TP
	System.out.println("Standard Deviation for TP "+TP_SD);
	
	double AM = WeightedMetrics.calculateArithmeticMean((ArrayList<Double>)weightsGS); //arithmetic mean
	System.out.println("Arithmetic Mean "+AM);
	
	double SD = WeightedMetrics.calculateStandardDeviation(TPweights,AM); //standard deviation
	System.out.println("Standard Deviation"+SD);
	
}

	
	public static String[] return_splitedarrays(String subjectString){ 
	String[] splitArray = null;
	try {
		Pattern regex = Pattern.compile("> *?\\.", Pattern.DOTALL);
		splitArray = regex.split(subjectString);
	} catch (Exception ex) {
		// Syntax error in the regular expression
	}
	return splitArray;
	}
	
	public static String[] combineArrays(String[] arg1, String[] arg2){ 
		String[] result;
		if (arg1==null)
			 result =arg2;
		else if (arg2==null){
			result =arg1;
			}
		else {
			result = new String[arg1.length + arg2.length];
		    System.arraycopy(arg1, 0, result, 0, arg1.length);
		    System.arraycopy(arg2, 0, result, arg1.length, arg2.length);
		}
		return result;
		
	}

}
	
				













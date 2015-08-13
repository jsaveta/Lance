package transformations.goldStandard;

import generators.data.sesamemodelbuilders.SesameBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.turtle.TurtleParser;

import Jama.Matrix;
import properties.Configurations;
import main.TestDriver;
import rescal.turtleRescalTripleFinder.RescalStarter;
import util.CleanZeros;
import util.FileUtils;
import util.Matrices;
import util.SesameUtils;


public class CreateFinalGS {
	 private static ArrayList<ArrayList<Object>> GS = new ArrayList<ArrayList<Object>>();
	 private static ArrayList<ArrayList<Object>> finalGS = new ArrayList<ArrayList<Object>>();
	 private static String exactmatch = "http://www.w3.org/2004/02/skos/core#exactMatch";
	 public static double MACHEPS = 2E-16;
	 private static ArrayList<String> transformationsArrayList;
	 private static String tsvfile = "weights.csv";
	 private static Map<String, Integer> weightFrequence = new HashMap<String, Integer>();
	 private static BufferedWriter tsvBw;

		
	 
	 public CreateFinalGS(){}
	 
public static List<ArrayList<Object>> GSScores(String file_) throws IOException, RDFHandlerException, RDFParseException {
	String GSfile = file_;	
	String u = "";
	String uPrime = "";
	String source = null, target = null, gold_standard = null;
	//System.out.println("file_ "+file_);
	if ( GSfile.startsWith("goldStandard") && !GSfile.endsWith(".txt")){
		//System.out.println("GSfile "+GSfile);
		try {
			File file = new File("GoldStandards\\"+GSfile.toString().replace(".", "").replace( FileUtils.getFileExtension(GSfile), "")+"_final"+ ".txt");	 //replace meta tin teleia oxi ttl
			@SuppressWarnings("resource")
			BufferedWriter simpleGSfile = new BufferedWriter(new FileWriter(file));
			
			
			RDFParser rdfParser = new TurtleParser();
		    FileInputStream fileinputstream = new FileInputStream("GoldStandards/"+GSfile);
			
		   
			InputStreamReader inputstreamreader  = new InputStreamReader(fileinputstream);
			StatementCollector handler = new StatementCollector();
			rdfParser.setRDFHandler(handler);
			rdfParser.parse(inputstreamreader, "");
			Collection<Statement> col = handler.getStatements();
			ArrayList<Object> IdTemp = new ArrayList<Object>();
			ArrayList<Object> TypeTemp = initializeTypeTempArray();
			for(Iterator<Statement> it = col.iterator(); it.hasNext();){
				Statement st = it.next();
			    if(st.getPredicate().toString().equals(exactmatch) && !u.equals(st.getSubject().toString())){
			    	if(!u.equals("")){
			    		if((Integer)TypeTemp.get(transformationsArrayList.indexOf("DisjointWith")+1) == 0 && (Integer)TypeTemp.get(transformationsArrayList.indexOf("DisjointProperty")+1) == 0 && !IdTemp.isEmpty()){ //check for disjoint and do not add it 
				    		GS.add(IdTemp);
				    		GS.add(TypeTemp);
				    	}
			    		TypeTemp = initializeTypeTempArray();
			    		IdTemp = new ArrayList<Object>();
			    	}
			    	u = st.getSubject().toString();
					uPrime = st.getObject().toString();
			    }
			    
			    if(st.getPredicate().toString().equals("http://www.type")){
			    	int indexToReplace = transformationsArrayList.indexOf(st.getObject().stringValue())+1;
			    	int transf = (Integer)TypeTemp.get(indexToReplace) +1;
			    	TypeTemp.remove(indexToReplace);
			    	TypeTemp.add(indexToReplace,transf);
			    }
			    if(st.getObject().stringValue().equals("NotTransformed")){
			    	IdTemp.add(u);
			    	IdTemp.add(uPrime);
			    	try {
						simpleGSfile.write(u+" "+uPrime+"\n");
					} catch ( IOException e ) {
						throw new IOException("Failed to calculate scores for gs file : " + e.getMessage(), e);
					}
			    }
			    
			    
			}
			simpleGSfile.close();
			
			//call rescal
			source = "SourceDatasets\\"+GSfile.toString().replace("goldStandard", "source"); 
			target = "TargetDatasets\\"+GSfile.toString().replace("goldStandard", "target");
			gold_standard = file.toString();
			Rescal(source, target, gold_standard);

		}
		 catch (IOException e) {
			  throw new IOException("Failed to read file : " + e.getMessage(), e);
		 }
		 catch (RDFParseException e) {
			throw new RDFParseException("Failed to parse file: "+ e.getMessage());
         }
         catch (UnsupportedRDFormatException e) {
        	 throw new UnsupportedRDFormatException("UnsupportedRDFormatException : "+ e.getMessage());
         }
    }
	return Collections.synchronizedList(GS);
}



public static ArrayList<Double> calculateSpecificTransfWeights(ArrayList<ArrayList<Object>>finalGS_, Map<String, Map<String, Double>> S) throws RDFParseException, RDFHandlerException, IOException{
	tsvBw = new BufferedWriter(new FileWriter(new File(tsvfile),true));
	//initialize weightFrequence
	weightFrequence.put("0.00 - 0.10", 0);
	weightFrequence.put("0.11 - 0.20", 0);
	weightFrequence.put("0.21 - 0.30", 0);
	weightFrequence.put("0.31 - 0.40", 0);
	weightFrequence.put("0.41 - 0.50", 0);
	weightFrequence.put("0.51 - 0.60", 0);
	weightFrequence.put("0.61 - 0.70", 0);
	weightFrequence.put("0.71 - 0.80", 0);
	weightFrequence.put("0.81 - 0.90", 0);
	weightFrequence.put("0.91 - 1.00", 0);
	
	
	
	ArrayList<Double> Y = new ArrayList<Double>();
	double[][] M = new double[RescalStarter.getSArrayList().size()][40];
	int j = 0;
	double cos = 0.0;
	Iterator<String> iteratorGS = S.keySet().iterator();
	while (iteratorGS.hasNext()) {
		 String key = iteratorGS.next().toString();
	     //System.out.println("key : " + key);
	    	 for(int i = 0; i < finalGS_.size(); i++) {   
	    			ArrayList<Object> in = finalGS_.get(i);
	    			if(in.contains(key)){
		     	 		//System.out.println("in key " + in.toString());

			     		Map<String,Double> value = S.get(key);
					    Iterator<String> internalIteratorGS = value.keySet().iterator();
					    String internalKey = internalIteratorGS.next().toString();
					    cos = value.get(internalKey);
			     	}
	     			if(cos > 0.0 && in.size() == 40){
		        		for (int k = 0; k < in.size(); k++) {
		        			 M[j][k] = (Double) ((Integer)in.get(k)*1.0);
		           		}
		        		j++;
		        	//System.out.println("cos " + cos);
		     		Y.add(cos);
				    cos = 0.0;
	     			}
			   	}
	    	 }
    	
    	
   /*clean M from columns that contain only zeros*/
    double[][] transposed = CleanZeros.transpose(M);
    double[][] colsCleaned = CleanZeros.cleanZeroRows(transposed);
    double[][] transposedBack = CleanZeros.transpose(colsCleaned);
   
    /*Create Marray*/
    double[][] Marray = new double[Y.size()][transposedBack[0].length];
	for (int x = 0; x < Marray.length; x++)
		  for (int y = 0; y < Marray[0].length; y++)
		    Marray[x][y] = transposedBack[x][y];
	
	/*Create Yarray*/
	double[][] Yarray = new double[Y.size()][1];
	double[] Ytemp = new double[Y.size()];
	for (int k = 0; k < Yarray.length; k++){   
		Yarray[k][0] = Y.get(k);
		Ytemp[k] = Y.get(k);
	}
	/*Convert Y from array to matrix*/
	Matrix Ymatrix = new Matrix(Yarray);
	
	/*pseudo inverse Marray*/
	Matrix InverseMarray = Matrices.pinv2(new Matrix(Marray));
	//Matrix InverseMarray = new Matrix(Marray).inverse();

//	double [][] transposedM = CleanZeros.transpose(InverseMarray.getArray());
//	MultipleLinearRegression regression = new MultipleLinearRegression(transposedM, Ytemp); 
//
//	System.out.println("Y size " + Y.size());
//	for(int b = 0; b<transposedM[0].length; b++){ //////////////////////  divide with sum? what is R2?
//		System.out.println("beta "+b+" :" + regression.beta(b));
//		System.out.println("residuals "+b+" :" + regression.residuals(b));
//	}
//	
//	System.out.println("R2 "+ regression.R2());
//
//	
//	OLSMultipleLinearRegression  regression = new OLSMultipleLinearRegression();
//	regression.newSampleData(Ytemp, transposedM);
//	regression.setNoIntercept(false);
//    regression.newSampleData(Ytemp, transposedM);
//    double[] beta = regression.estimateRegressionParameters();   //////
//    double[] c = regression.estimateRegressionParametersStandardErrors();
//    double[] d = regression.estimateResiduals();
//	double[][] var = regression.estimateRegressionParametersVariance();
//    RealMatrix hat = regression.calculateHat();
//    
//    for(int b = 0; b<beta.length; b++){
//		System.out.println("beta "+b+" :" + beta[b]);
//		System.out.println("c "+b+" :" + c[b]);
//		System.out.println("d "+b+" :" + d[b]);
//
//	}
//
//     for(int b = 1; b<hat.getData()[0].length; b++){
//    	 System.out.println("hat: "+ hat.getData()[0][b]); //////////
//     }    
//     System.out.print("\nVAR\n");
//     for (int i = 0; i < var.length; i++) {
// 	    for (int j1 = 0; j1 < var[0].length; j1++) {
// 	        System.out.print(var[i][j1] + " ");
// 	    }
// 	    System.out.print("\n");
// 	}
	
	/*Calculate final weight*/
	Matrix T = InverseMarray.times(Ymatrix); 
	double[] T_array = T.getRowPackedCopy();//row or column, contain the same nums
	
//	System.out.print("\nT\n");
//	for (int i = 0; i < T_array.length; i++) {
//		System.out.print(T_array[i] + " ");
//	}
//	System.out.print("\n");
	
	ArrayList<Double> specificWeights = new ArrayList<Double>();
	specificWeights.add(0,0.0);
	specificWeights.add(1,0.0);
	int zeroIndexes = 0;
	for (int i = 2; i < 40; i++){
		double weight = 0.0;
		if(CleanZeros.indexes.contains(i)){ 
			weight = 0.0;
			zeroIndexes ++;
		} 
		else{
			weight = T_array[i - zeroIndexes];//regression.residuals(i - zeroIndexes);   <-------------
		}
		
		Double w = Math.abs(weight);
		specificWeights.add(w);
		
		
	}
//	for(int i = 0; i < specificWeights.size(); i++) {   
//	    System.out.println("i: "+ i+ "  specificWeight: "+specificWeights.get(i));
//	}
//	
//	
//	System.out.print("Marray \n");
//	for (int i = 0; i < Marray.length; i++) {
//	    for (int j1 = 0; j1 < Marray[0].length; j1++) {
//	        System.out.print(Marray[i][j1] + " ");
//	    }
//	    System.out.print("\n");
//	}
//
//	System.out.println("Marray len " + Marray.length);
//	System.out.println("Y len " + Y.size());
//
//	System.out.println("Marray columns : " + Marray.length);
//	System.out.println("Marray rows : " + Marray[0].length);
//
//	System.out.println("Y matrix columns : " + Ymatrix.getColumnDimension());
//	System.out.println("Y matrix rows : " + Ymatrix.getRowDimension());
//
//	System.out.println("InverseMarray columns : " + InverseMarray.getColumnDimension());
//	System.out.println("InverseMarray rows : " + InverseMarray.getRowDimension());

	return specificWeights;
}


public static void writeFinalGSFiles() throws IOException, RDFParseException, RDFHandlerException{
	//write here detailed gs(ttl) and also simple one (txt) both wighted
	//after finishing delete previous detailes GSs with the wrong weights
	ArrayList<Double> weight_per_trans_ =  calculateSpecificTransfWeights(getFinalGS(),RescalStarter.getGsWeighted());
//	 
//	for(int i = 0; i < weight_per_trans_.size(); i++) {   
//	    System.out.println("i: "+ i+ "  weight: "+weight_per_trans_.get(i));
//	} 
	File folder = new File("GoldStandards\\");
	File[] listOfFiles = folder.listFiles();
		for (File file_ : listOfFiles) {
		    if ( !file_.getName().endsWith("txt") && !file_.getName().contains("DETAILED")) {
		    	String GSfile = file_.getName();	
		    	//System.out.println("GSfile "+GSfile);
		    	String u = "";
		    	String uPrime = "";
		    	int indexToReplace = 0;
		    	
		    	try {
		    		File file = new File("GoldStandards\\"+GSfile.toString().replace(".", "").replace( FileUtils.getFileExtension(GSfile), "")+"_final"+ ".txt");	
		    		//oaei
		    		OAEIRDFAlignmentFormat oaeiRDF = null;
		    		OAEIAlignmentOutput oaei = null;
		    		try {
		    			oaeiRDF = new OAEIRDFAlignmentFormat("OAEIRDFGoldStandards\\"+GSfile.toString().replace( FileUtils.getFileExtension(GSfile), "rdf"), GSfile.toString().replace("goldStandard", "source"), GSfile.toString().replace("goldStandard", "target"));
		    			oaei = new OAEIAlignmentOutput(GSfile.toString().replace(".", "").replace( FileUtils.getFileExtension(GSfile), ""), GSfile.toString().replace("goldStandard", "source"), GSfile.toString().replace("goldStandard", "target"));
		    		} catch (Exception e1) {
						e1.printStackTrace();
					}
		    		

		    		BufferedWriter simpleGSfile = new BufferedWriter(new FileWriter(file));
		    		
		    		Model detailedGS = new LinkedHashModel();
		    		String detailedFileName = "GoldStandards\\"+GSfile.toString().replace(".", "").replace(FileUtils.getFileExtension(GSfile), "")+"_DETAILED."+  FileUtils.getFileExtension(GSfile);
		    		FileOutputStream fos_det = new FileOutputStream(detailedFileName);	
		    		
		    		RDFParser rdfParser = new TurtleParser();
		    		FileInputStream fileinputstream = new FileInputStream("GoldStandards/"+GSfile);
		    		InputStreamReader inputstreamreader  = new InputStreamReader(fileinputstream);
		    		StatementCollector handler = new StatementCollector();
		    		rdfParser.setRDFHandler(handler);
		    		rdfParser.parse(inputstreamreader, "");
		    		
		    		Collection<Statement> col = handler.getStatements();
		    		ArrayList<Object> IdTemp = new ArrayList<Object>();
		    		ArrayList<Object> ResTemp = new ArrayList<Object>();
		    		ArrayList<Object> TypeTemp = initializeTypeTempArray();
		    		double u_uPrime_weight = 0d;
			    	
		    		for(Iterator<Statement> it = col.iterator(); it.hasNext();){
					Statement st = it.next();
					
				    if(st.getPredicate().toString().equals(exactmatch) && !u.equals(st.getSubject().toString())){
				    	if(!u.equals("")){ //check disjointness again
				    		if((Integer)TypeTemp.get(transformationsArrayList.indexOf("DisjointWith")+1) == 0 && (Integer)TypeTemp.get(transformationsArrayList.indexOf("DisjointProperty")+1) == 0 && !IdTemp.isEmpty()){ //check for disjoint and do not add it 
					    		finalGS.add(IdTemp);
					    		finalGS.add(ResTemp);
					    		finalGS.add(TypeTemp);
					    		
						    	try {
						    		Double finalWeight = 1.0 - u_uPrime_weight;//(1.0 - u_uPrime_weight);
						    		if(finalWeight > 1.0){
						    			//System.out.println("The weight is greater than 1.0 " + finalWeight);
						    			finalWeight = 1.0;
						    		}
						    		if(finalWeight < 0.0){ //we need this
						    			finalWeight = 0.0;
						    		}
						    		//System.out.println(u_uPrime_weight + "       ------------         " + finalWeight);
									
									simpleGSfile.write(IdTemp.get(0)+" "+IdTemp.get(1)+" "+finalWeight+"\n"); //made it 1- weight 
									//tsv file with weight frequence
									writeTSVFile(tsvBw,finalWeight.toString());
									
									
									if(finalWeight <= 0.10){
										weightFrequence.put("0.00 - 0.10", weightFrequence.get("0.00 - 0.10")+1);
									}
									else if(finalWeight <= 0.20){
										weightFrequence.put("0.11 - 0.20", weightFrequence.get("0.11 - 0.20")+1);
									}
									else if(finalWeight <= 0.30){
										weightFrequence.put("0.21 - 0.30", weightFrequence.get("0.21 - 0.30")+1);
									}
									else if(finalWeight <= 0.40){
										weightFrequence.put("0.31 - 0.40", weightFrequence.get("0.31 - 0.40")+1);
									}
									else if(finalWeight <= 0.50){
										weightFrequence.put("0.41 - 0.50", weightFrequence.get("0.41 - 0.50")+1);
									}
									else if(finalWeight <= 0.60){
										weightFrequence.put("0.51 - 0.60", weightFrequence.get("0.51 - 0.60")+1);
									}
									else if(finalWeight <= 0.70){
										weightFrequence.put("0.61 - 0.70", weightFrequence.get("0.61 - 0.70")+1);
									}
									else if(finalWeight <= 0.80){
										weightFrequence.put("0.71 - 0.80", weightFrequence.get("0.71 - 0.80")+1);
									}
									else if(finalWeight <= 0.90){
										weightFrequence.put("0.81 - 0.90", weightFrequence.get("0.81 - 0.90")+1);
									}
									else if(finalWeight <= 1.0){
										weightFrequence.put("0.91 - 1.00", weightFrequence.get("0.91 - 1.00")+1);
									}
									
									
									
									//oaei
									try {
										oaeiRDF.addMapping2Output(IdTemp.get(0).toString(), IdTemp.get(1).toString(), 0, finalWeight);
										oaei.addMapping2Output(IdTemp.get(0).toString(), IdTemp.get(1).toString(), 0, finalWeight);
										u_uPrime_weight = 0d; /////
										
										
									} catch (Exception e) {
										e.printStackTrace();
									}
									
								} catch ( IOException e ) {
									   e.printStackTrace();
								}
					    	}
				    		
				    		TypeTemp = initializeTypeTempArray();
				    		IdTemp = new ArrayList<Object>();
				    		ResTemp = new ArrayList<Object>(); 
				    		
				    	}
				    	u = st.getSubject().toString();
						uPrime = st.getObject().toString();
				    }
				    
				    if(st.getPredicate().toString().equals("http://www.type")){
				    	indexToReplace = transformationsArrayList.indexOf(st.getObject().stringValue())+1;
				    	int transf = (Integer)TypeTemp.get(indexToReplace) +1;
				    	TypeTemp.remove(indexToReplace);
				    	TypeTemp.add(indexToReplace,transf);
				    	
				    }
				    if(st.getObject().stringValue().equals("NotTransformed")){
				    	IdTemp.add(u);
				    	IdTemp.add(uPrime);
				    	ResTemp.add(1);
				    }
					  //check weight here
					if(st.getPredicate().toString().equals("http://www.weight")){
						double weight = weight_per_trans_.get((indexToReplace));
						detailedGS.add(st.getSubject(),st.getPredicate(),SesameBuilder.sesameValueFactory.createLiteral(weight),st.getContext());
						//keep sum here for final gs
						u_uPrime_weight += weight;
						//System.out.println("u_uPrime_weight " + u_uPrime_weight);
					}
					else{detailedGS.add(st);}
				    
				    
				}
		    		
		    	RDFFormat rdfFormat = SesameUtils.parseRdfFormat(TestDriver.getConfigurations().getString(Configurations.GENERATED_DATA_FORMAT)); 
		    	//RDFFormat rdfFormat = SesameUtils.parseRdfFormat("turtle"); 
		    		 
		    	Rio.write(detailedGS, fos_det, rdfFormat);
		    	
		    	try {
					oaeiRDF.saveOutputFile();
					oaei.saveOutputFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
	
				simpleGSfile.close();	
				fileinputstream.close();
		    	inputstreamreader.close();
		    }
			catch (UnsupportedRDFormatException e) {
				throw new UnsupportedRDFormatException("UnsupportedRDFormatException : "+ e.getMessage());
			}
		    file_.delete();
		   
		    }

	}
	
	TreeMap<String, Integer>weightFrequenceTree = new TreeMap<String, Integer>(weightFrequence);
	writeTSVFile(tsvBw,"name,frequency");
	for (String key : weightFrequenceTree.keySet()) {
	    //System.out.println("Key: " + key + ", Value: " + weightFrequenceTree.get(key));
	    writeTSVFile(tsvBw, key + "	" + weightFrequenceTree.get(key));
	}
	tsvBw.close();	

}


public static  Map<String, Map<String, Double>> Rescal(String source, String target, String gold_standard){
Map<String, Map<String, Double>> map = null ;
try{

	new RescalStarter( source,  target,  gold_standard);
	map = RescalStarter.getGsWeighted();  
}catch(Exception e){
	System.err.println(e.getMessage());
	System.err.println("Problem with java-python bridge occured.");
}
return map;
}


public void sortListOfLists(ArrayList < ArrayList < String >> listOfLists) {

// now sort by comparing the first string of each inner list using a comparator
Collections.sort(listOfLists, new ListOfStringsComparator());
}

public static ArrayList<Object> initializeTypeTempArray(){
	ArrayList<Object> TypeTemp = new ArrayList<Object>();
	TypeTemp.add(1); //extra static constant
//so i have to calculate the T and add them at index i+1
	for(int i=0; i < 39; i++){
		TypeTemp.add(0);
	}
	return TypeTemp;		
}

public static ArrayList<ArrayList<Object>> getFinalGS(){
	//Collections.synchronizedList(finalGS);
	return GS; //sort this 
}

public static void setTransformationsArrayList(){
	transformationsArrayList = new ArrayList<String>();
	transformationsArrayList.add("NotTransformed");
	// 1 - 18 are value transformations
	transformationsArrayList.add("BlankCharsAddition");
	transformationsArrayList.add("BlankCharsDeletion");
	transformationsArrayList.add("RandomCharsAddition");
	transformationsArrayList.add("RandomCharsDeletion");
	transformationsArrayList.add("RandomCharsModifier");
	transformationsArrayList.add("TokenAddition");
	transformationsArrayList.add("TokenDeletion");
	transformationsArrayList.add("TokenShuffle");
	transformationsArrayList.add("NameStyleAbbreviation");
	transformationsArrayList.add("CountryNameAbbreviation");
	transformationsArrayList.add("ChangeSynonym");
	transformationsArrayList.add("ChangeAntonym"); 
	transformationsArrayList.add("ChangeNumber"); 
	transformationsArrayList.add("ChangeDateFormat");
	transformationsArrayList.add("ChangeLanguage");
	transformationsArrayList.add("ChangeBooleanValue"); 
	transformationsArrayList.add("ChangeGenderFormat");
	transformationsArrayList.add("StemWord"); 
	transformationsArrayList.add("ChangePoint");  
	
	// 19 - 22 are structure transformations
	transformationsArrayList.add("AddProperty"); 
	transformationsArrayList.add("DeleteProperty");
	transformationsArrayList.add("ExtractProperty");
	transformationsArrayList.add("AggregateProperties");
	
	//23 - 35 are semanticsAware transformations 
	//For instances
	transformationsArrayList.add("SameAs");
	transformationsArrayList.add("SameAsOnExistingInstances");
	transformationsArrayList.add("DifferentFrom");
	//For classes
	transformationsArrayList.add("SubClassOf");
	transformationsArrayList.add("EquivalentClass");
	transformationsArrayList.add("DisjointWith"); 
	transformationsArrayList.add("UnionOf");
	transformationsArrayList.add("IntersectionOf");
	//For properties
	transformationsArrayList.add("SubPropertyOf"); 
	transformationsArrayList.add("EquivalentProperty");
	transformationsArrayList.add("DisjointProperty"); 
	transformationsArrayList.add("FunctionalProperty"); 
	transformationsArrayList.add("InverseFunctionalProperty");
	//transformationsArrayList.add("InverseOf");
}


	private static void writeTSVFile(BufferedWriter bw, String finalWeight) throws IOException{
		bw.write(finalWeight.toString());
		bw.write("\t");
		//bw.close();
	}

}


    

final class ListOfStringsComparator implements Comparator < ArrayList < String >> {

    @Override
    public int compare(ArrayList < String > o1, ArrayList < String > o2) {
    	try{// do other error checks here as well... such as null. outofbounds, etc
            return o1.get(2).compareTo(o2.get(2));
         }
    	 catch(IndexOutOfBoundsException e){
    	    System.err.println("IndexOutOfBoundsException: " + e.getMessage());
         }
		return 0;
        
    }

}




package util;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import main.TestDriver;

import org.openrdf.model.Statement;

import properties.Configurations;

/**
 * @author jsaveta, Foundation for Research and Technology-Hellas (FORTH)
 *
 * @date Mar 30, 2015
 */
public class TransformationsMeasurements {

	private long startTime;
    private long endTime;
    private static long loadingTime;
    private static long generationTransformationTime;
    private static long retrievingInfoTime;
    private static long samplingTime;
    private static long weightCalculationTime;
    
    public static int valueSuccess;
    public static int valueFailure;
    
    public static int structureSuccess;
    public static int structureFailure;
    
    public static int semanticsSuccess;
    public static int semanticsFailure;
    
    public static int complexSuccess;
    public static int complexFailure;
    
    public TransformationsMeasurements(){}
    
    public long getDuration() {
        return endTime - startTime;
    }
    
    public void start() {
        startTime = System.nanoTime();
    }
    
    public void stop() {
         endTime = System.nanoTime();
    }
    
	/**
	 * Time for loading ontologies and datasets into triple store in nano sec
	 */
	public static void setLoadingTime(long time){
		loadingTime = time;
	} 
	
	public static Long getLoadingTime(){
		return loadingTime;
	} 
	
	/**
	 * Time for retrieving schema info from triple store in nano sec
	 */
	public void setRetrievingInfoTime(long time){
		retrievingInfoTime = time;
	}
	
	public static Long getRetrievingInfoTime(){
		return retrievingInfoTime;
	}
	
	/**
	 * Time for generation and transformation source, target ang gs files in nano sec
	 */
	public static void setGenerationTransformationTime(long time){ //TODO check this subtr
		generationTransformationTime = (time - retrievingInfoTime);
	} 
	
	public static Long getGenerationTransformationTime(){
		return generationTransformationTime;
	} 
	
	/**
	 * Time for sampling in nano sec
	 * */
	public static void setSamplingTime(long time){
		samplingTime = time;
	}
	
	public static Long getSamplingTime(){
		return samplingTime;
	} 
	
	/**
	 * Time for weight calculation in nano sec
	 * */
	public static void setWeightCalculationTime(long time){
		weightCalculationTime = time;
	}
	
	public static Long getWeightCalculationTime(){
		return weightCalculationTime;
	} 
	
	/**
	 * convert nanoSeconds to Seconds
	 * @param nano
	 * @return
	 */
	public Long nanoSecToSec(long nano){
		return TimeUnit.SECONDS.convert(nano, TimeUnit.NANOSECONDS);
	}
	/**
	 * Success rate of value transformations, given a percentage of triples to transform how many were actually transformed?
	 * */
	public static void valueSuccessRate(String source, String target){
		if(!source.toString().equals(target.toString())){valueSuccess++;}
		else{
			valueFailure++;
		}
	}

	
	/**
	 * Success rate of semantics transformations, given a percentage of triples to transform how many were actually transformed?
	 * */
	public static void semanticsSuccessRate(Statement source, Statement target){
		
		if(!source.getPredicate().stringValue().equals(target.getPredicate().stringValue()) || !source.getObject().stringValue().equals(target.getObject().stringValue())){semanticsSuccess++;}
		else{
			semanticsFailure++;
		}
	}

	/**
	 * Success rate of complex transformations, given a percentage of triples to transform how many were actually transformed?
	 * */
	public static void complexSuccessRate(Statement source, Statement target){
		if(!source.toString().equals(target.toString())){complexSuccess++;}
		else{complexFailure++;}
	}
	
	/**
	 * Final success rate, given num of triples that failed to be transformed and num of triples that succeeded
	 * @param succeeded
	 * @param failed
	 * @return success percentage
	 */
	public String finalSuccessRate(int succeeded,int failed){
		DecimalFormat df = new DecimalFormat("#.####");
		if((succeeded+failed) == 0){return Double.toString(0.0);}
		return df.format(((double)succeeded/((double)succeeded+(double)failed))*100.0); 
	}
	/**
	 * Percentage of triples to be transformed
	 * @param succeeded
	 * @param failed
	 * @param all
	 * @return
	 */
	public String transformedTriplesPercentage(int succeeded, int all){
		DecimalFormat df = new DecimalFormat("#.####");
		if((succeeded) == 0){return Double.toString(0.0);}
		return df.format(((double)succeeded/(double)all)*100.0); 
	}
	
	/**
	 * Sum num of triples that were given to be transformed
	 * @param succeeded
	 * @param failed
	 * @return
	 */
	public int totalGivenTriples(int succeeded,int failed){
		return succeeded+failed; 
	}
	
	/**
	 * Write log file with all times and rates
	 */
	public void writeLogFile(){
		new Logger();
		Logger.write("The time in nanosecs needed to load ontologies and datasets was: "+getLoadingTime()+" , "+nanoSecToSec(getLoadingTime())+" (sec)");
		Logger.write("The time in nanosecs needed to retrieve schema information was: "+getRetrievingInfoTime()+" , "+nanoSecToSec(getRetrievingInfoTime())+" (sec)");
		Logger.write("The time in nanosecs needed to retrieve source files and generate target files was: "+getGenerationTransformationTime()+" , "+nanoSecToSec(getGenerationTransformationTime())+" (sec)");
		Logger.write("The time in nanosecs needed to calculate the sampling files number was: "+getSamplingTime()+" , "+nanoSecToSec(getSamplingTime())+" (sec)");
		Logger.write("The time in nanosecs needed to calculate the weights was: "+getWeightCalculationTime()+" , "+nanoSecToSec(getWeightCalculationTime())+" (sec)");
		
		Logger.write("\n-----------------------------------------\n");
		
		Logger.write("Given "+totalGivenTriples(valueSuccess,valueFailure)+" triples to transform with a value test case. "+valueSuccess+" of them were transformed.\n"
				+ " This corresponds to "+finalSuccessRate(valueSuccess, valueFailure)+" per cent of triples to be transformed and "+transformedTriplesPercentage(valueSuccess, TestDriver.getConfigurations().getInt(Configurations.TOTAL_TRIPLES))+" per cent of all triples. ");
		Logger.write("Given "+totalGivenTriples(structureSuccess,structureFailure)+" triples to transform with a structure test case. "+structureSuccess+" of them were transformed.\n"
				+ " This corresponds to "+finalSuccessRate(structureSuccess, structureFailure)+" per cent of triples to be transformed and "+transformedTriplesPercentage(structureSuccess, TestDriver.getConfigurations().getInt(Configurations.TOTAL_TRIPLES))+" per cent of all triples. ");
		Logger.write("Given "+totalGivenTriples(semanticsSuccess,semanticsFailure)+" triples to transform with a semantics test case.  "+semanticsSuccess+" of them were transformed.\n"
				+ " This corresponds to "+finalSuccessRate(semanticsSuccess, semanticsFailure)+" per cent of triples to be transformed and "+transformedTriplesPercentage(semanticsSuccess, TestDriver.getConfigurations().getInt(Configurations.TOTAL_TRIPLES))+" per cent of all triples. ");
		Logger.write("Given "+totalGivenTriples(complexSuccess,complexFailure)+" triples to transform with a complex combination test case. "+complexSuccess+" of them were transformed.\n"
				+ " This corresponds to "+finalSuccessRate(complexSuccess, complexFailure)+" per cent of triples to be transformed and "+transformedTriplesPercentage(complexSuccess, TestDriver.getConfigurations().getInt(Configurations.TOTAL_TRIPLES))+" per cent of all triples. ");
		
		Logger.write("\n-----------------------------------------\n");
		
		//tsv 
		Logger.writeTsv("The time in nanosecs needed to load ontologies and datasets was: "+getLoadingTime()+" , "+nanoSecToSec(getLoadingTime())+" (sec)");
		Logger.writeTsv("The time in nanosecs needed to retrieve schema information was: "+getRetrievingInfoTime()+" , "+nanoSecToSec(getRetrievingInfoTime())+" (sec)");
		Logger.writeTsv("The time in nanosecs needed to retrieve source files and generate target files was: "+getGenerationTransformationTime()+" , "+nanoSecToSec(getGenerationTransformationTime())+" (sec)");
		Logger.writeTsv("The time in nanosecs needed to calculate the sampling files number was: "+getSamplingTime()+" , "+nanoSecToSec(getSamplingTime())+" (sec)");
		Logger.writeTsv("The time in nanosecs needed to calculate the weights was: "+getWeightCalculationTime()+" , "+nanoSecToSec(getWeightCalculationTime())+" (sec)");
		
		Logger.writeTsv("\n-----------------------------------------\n");
		
		Logger.writeTsv("Given "+totalGivenTriples(valueSuccess,valueFailure)+" triples to transform with a value test case. "+valueSuccess+" of them were transformed.\n"
				+ " This corresponds to "+finalSuccessRate(valueSuccess, valueFailure)+" per cent of triples to be transformed and "+transformedTriplesPercentage(valueSuccess, TestDriver.getConfigurations().getInt(Configurations.TOTAL_TRIPLES))+" per cent of all triples. ");
		Logger.writeTsv("Given "+totalGivenTriples(structureSuccess,structureFailure)+" triples to transform with a structure test case. "+structureSuccess+" of them were transformed.\n"
				+ " This corresponds to "+finalSuccessRate(structureSuccess, structureFailure)+" per cent of triples to be transformed and "+transformedTriplesPercentage(structureSuccess, TestDriver.getConfigurations().getInt(Configurations.TOTAL_TRIPLES))+" per cent of all triples. ");
		Logger.writeTsv("Given "+totalGivenTriples(semanticsSuccess,semanticsFailure)+" triples to transform with a semantics test case.  "+semanticsSuccess+" of them were transformed.\n"
				+ " This corresponds to "+finalSuccessRate(semanticsSuccess, semanticsFailure)+" per cent of triples to be transformed and "+transformedTriplesPercentage(semanticsSuccess, TestDriver.getConfigurations().getInt(Configurations.TOTAL_TRIPLES))+" per cent of all triples. ");
		Logger.writeTsv("Given "+totalGivenTriples(complexSuccess,complexFailure)+" triples to transform with a complex combination test case. "+complexSuccess+" of them were transformed.\n"
				+ " This corresponds to "+finalSuccessRate(complexSuccess, complexFailure)+" per cent of triples to be transformed and "+transformedTriplesPercentage(complexSuccess, TestDriver.getConfigurations().getInt(Configurations.TOTAL_TRIPLES))+" per cent of all triples. ");
		
		Logger.writeTsv("\n-----------------------------------------\n");
				
		
//		Logger.write(" valueSuccess "+valueSuccess);
//		Logger.write(" valueFailure "+valueFailure);
//		Logger.write(" structureSuccess "+structureSuccess);
//		Logger.write(" structureFailure "+structureFailure);
//		Logger.write(" semanticsSuccess "+semanticsSuccess);
//		Logger.write(" semanticsFailure "+semanticsFailure);
//		Logger.write(" complexSuccess "+complexSuccess); 
//		Logger.write(" complexFailure "+complexFailure);
	}
}

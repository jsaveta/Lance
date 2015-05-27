package generators.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import properties.Configurations;
import properties.Definitions;
import transformations.TransformationsCall;
import transformations.goldStandard.CreateFinalGS;
import util.CosineUtil;
import util.DivergenceUtil;
import util.FileUtils;
import util.TransformationsMeasurements;

/**
 * The class responsible for managing data generation for the benchmark.
 * It is the entry point for any data generation related process.  
 *
 */
public class DataGenerator {
	private Configurations configuration;
	@SuppressWarnings("unused")
	private Definitions definitions;
	private int generatorThreads = 1;
	private long triplesPerFile;
	private AtomicLong filesCount = new AtomicLong(0);
	private AtomicLong triplesGeneratedSoFar = new AtomicLong(0);
	private String destinationPath;
	private String serializationFormat;
	private static final long AWAIT_PERIOD_HOURS = 96; 
	private Object syncLock;

	public DataGenerator(Configurations configuration, Definitions definitions, int generatorThreads, long triplesPerFile, String destinationPath, String serializationFormat) {
		this.configuration = configuration;
		this.definitions = definitions;
		this.generatorThreads = generatorThreads;
		this.triplesPerFile = triplesPerFile;
		this.destinationPath = destinationPath;
		this.serializationFormat = serializationFormat;
		this.syncLock = this;
	}
	
	public void produceData() throws InterruptedException, IOException, RDFHandlerException, RDFParseException, RepositoryException, MalformedQueryException, QueryEvaluationException {
		produceData(false);		
	}
	
	@SuppressWarnings("static-access")
	public void produceData(boolean silent) throws InterruptedException, IOException, RDFHandlerException, RDFParseException, RepositoryException, MalformedQueryException, QueryEvaluationException  {
		Map <String,ArrayList<Double>> Fmap = new HashMap<String,ArrayList<Double>>();
		
		//create destination directory
		FileUtils.makeDirectories(this.destinationPath);
		ExecutorService executorService = Executors.newFixedThreadPool(generatorThreads);
		
		TransformationsMeasurements timer = new TransformationsMeasurements();
		timer.start(); //measure generation-transformation time, start
		
		new TransformationsCall();
		for (int i = 0; i < generatorThreads; i++) {				
			Worker worker = new Worker(syncLock, filesCount, triplesPerFile, triplesGeneratedSoFar, destinationPath, serializationFormat, silent);
			executorService.execute(worker);
			Fmap = worker.getFtransformations();
		}
		executorService.shutdown();
		executorService.awaitTermination(AWAIT_PERIOD_HOURS, TimeUnit.HOURS);		
		

		timer.stop(); //measure generation-transformation time, stop
		timer.setGenerationTransformationTime(timer.getDuration());
		
		// map that contains file as key and u,u', rescal and transf score as value

		timer.start(); //measure sampling time, start
		
		ArrayList<Double> E = new ArrayList<Double>();
		for(int i = 0 ; i < 38; i++){
			E.add(0.0);
		}
		for (Entry<String, ArrayList<Double>> entry : Fmap.entrySet()) {
            ArrayList<Double> value = (ArrayList<Double>)entry.getValue();
            for(int i = 0 ; i < value.size(); i++){
        		double array_value = E.get(i) + value.get(i);
        		E.remove(i);
             	E.add(i, array_value);
            }
        }

		double sumE = 0d;
		for(int i = 0 ; i < E.size(); i++){
			double average_value = E.get(i)/(Fmap.size()-1);
			sumE += average_value;
			E.remove(i);
         	E.add(i, average_value);
		}

		//calculate square cosine between E[] and Fi[]
		CosineUtil cos = new CosineUtil();
		Map<String,Double> square_cos = cos.squareCosineSimilarityEF(Fmap,E);

		//calculate  Ek an average of the transformations for k files
		ArrayList<Double> Ek = new ArrayList<Double>();
		for(int i = 0 ; i < 38; i++){
			Ek.add(0.0);
		}
		
		if(Integer.parseInt(configuration.getString(Configurations.FILES_FOR_RESCAL_SAMPLING)) > 0){ //TODO also check if k is too low
			List<String> list = new ArrayList<String>();
			for (Entry<String, Double> cos_entry : square_cos.entrySet()) {
				if (list.size() > Integer.parseInt(configuration.getString(Configurations.FILES_FOR_RESCAL_SAMPLING)) -1) break;
				    else{ 
				    	for (Entry<String, ArrayList<Double>> entry : Fmap.entrySet()) {
				    		if(entry.getKey().endsWith(cos_entry.getKey().replace("goldStandard", "source"))){
					            ArrayList<Double> value = (ArrayList<Double>)entry.getValue();
					            for(int i = 0 ; i < value.size(); i++){
					        		double array_value = Ek.get(i) + value.get(i);
					        		Ek.remove(i);
					             	Ek.add(i, array_value);
					            }
					        	list.add(entry.getKey());
				    		}
				        }
				    }
			}
		}
		else{
			System.out.println("\t Please give as k a positive number as rescalSampling.");		
		}
		double sumEk = 0d;
		for(int i = 0 ; i < Ek.size(); i++){
			double average_value = Ek.get(i)/Integer.parseInt(configuration.getString(Configurations.FILES_FOR_RESCAL_SAMPLING));
			sumEk += average_value;
			Ek.remove(i);
         	Ek.add(i, average_value);
		}

		ArrayList<Double> ProbabilisticE = new ArrayList<Double>();
		for(int i = 0 ; i < E.size(); i++){
			ProbabilisticE.add(E.get(i)/sumE);
		}
		
		ArrayList<Double> ProbabilisticEk = new ArrayList<Double>();
		for(int i = 0 ; i < Ek.size(); i++){
			ProbabilisticEk.add(Ek.get(i)/sumEk);
		}
		ArrayList<Double> ProbabilisticEandEkAverage = new ArrayList<Double>();
		for(int i = 0 ; i < Ek.size(); i++){
			ProbabilisticEandEkAverage.add((ProbabilisticEk.get(i) + ProbabilisticE.get(i))/2);
		}
		
		double js = DivergenceUtil.jsDivergence(ProbabilisticEk, ProbabilisticE, ProbabilisticEandEkAverage);
		
		/*delete empty files first*/
		FileUtils.deleteEmptyFiles("./SourceDatasets");
		FileUtils.deleteEmptyFiles("./TargetDatasets");
		FileUtils.deleteEmptyFiles("./GoldStandards");
		
		timer.stop(); //measure sampling time, stop
		timer.setSamplingTime(timer.getDuration());
		
		timer.start(); //measure rescal time, start
		System.out.println("\n\tIs suggested to select more than 2 files for rescalSampling.\n");	
		if(Integer.parseInt(configuration.getString(Configurations.FILES_FOR_RESCAL_SAMPLING)) > 0){ 
			int files = new File("./SourceDatasets").list().length;
			double k = js*files + Integer.parseInt(configuration.getString(Configurations.FILES_FOR_RESCAL_SAMPLING)); //TODO fix this!
			
			//System.out.println("js : " +js);
			//System.out.println("k : " +k);
			int times = Integer.parseInt(configuration.getString(Configurations.FILES_FOR_RESCAL_SAMPLING));
			
			if((int)k <= Integer.parseInt(configuration.getString(Configurations.FILES_FOR_RESCAL_SAMPLING))){
				System.out.println("\tThe rescalSampling you chose is satisfactory.");
			}
			else{
				if(k>files) k = files;
				System.out.println("\tThe suggested rescalSampling is : " + (int)k); 
				times = (int)k; 
			}
			List<String> list = new ArrayList<String>();
			if(Integer.parseInt(configuration.getString(Configurations.FILES_FOR_RESCAL_SAMPLING)) > files){
				times = files;
			}
			CreateFinalGS.setTransformationsArrayList();
			for (Entry<String, Double> entry : square_cos.entrySet()) {
			  if (list.size() >= (times-1)) {
				  CreateFinalGS.writeFinalGSFiles(); 
				  timer.stop(); //measure rescal time, stop
				  timer.setWeightCalculationTime(timer.getDuration());
				  break;
			  }
			  else{  
				  list.add(entry.getKey());
				  try {
					  CreateFinalGS.GSScores(entry.getKey().replace("./SourceDatasets\\source", ""));
				  } catch (RDFParseException e) {
					throw new RDFParseException("RDFParseException " + e.getMessage());
				}
			  }
			}
		}
		else{
			System.out.println("\n\tPlease give a number greater than 2 for rescalSampling.\n");
			System.exit(0); //exit program
		}
	
	//	System.out.println("\tcompleted! Total Creative Works created : " + String.format("%,d", (DataManager.creativeWorksNextId.get() - creativeWorksInDatabase)) + ". Time : " + (System.currentTimeMillis() - currentTime) + " ms");		
			
		
	}
	
}

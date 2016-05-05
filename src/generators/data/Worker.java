package generators.data;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResults;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;

import main.TestDriver;
import properties.Configurations;
import transformations.Transformation;
import transformations.TransformationsCall;
import util.SesameUtils;

/**
 * A class for generating instances using components for serializing from the Sesame. 
 *
 */
public class Worker extends AbstractAsynchronousWorker{
	
	protected long targetTriples;
	protected long triplesPerFile;
	protected long totalTriplesForWorker;
	protected String destinationPath;
	protected String serializationFormat;
	protected AtomicLong filesCount;
	protected AtomicLong triplesGeneratedSoFar;
	protected Object lock;
	protected boolean silent;
	private static String endpointUrl = TestDriver.getConfigurations().getString(Configurations.ENDPOINT_URL);
	protected HashMap <String, String> URIMapping = new HashMap<String, String>(); /*as key we have the initial URI and as value the corresponding one (new or not)*/
	protected ArrayList<Double> FTransfArray;
	protected Map <String, Transformation> TransformationsConfiguration; //key: predicates value: type of transformation(if so)
	protected TransformationsCall tr; //key: id, value: every transformation(lexical, structural or logical)
	protected static RDFFormat rdfFormat;
	protected static Model sourceSesameModel;
	protected static Model targetSesameModel;
	protected static Model gsSesameModel;
	protected static  ArrayList <Model> sourceSesameModelArrayList;
	protected static  ArrayList <Model> targetSesameModelArrayList;
	protected String sourceFileName;
	protected FileOutputStream gsFos;
	private static long offset = 0;
	private static ArrayList<Long> offsetArray = new ArrayList<Long>();

	public Worker(Object lock, AtomicLong filesCount,long triplesPerFile, AtomicLong triplesGeneratedSoFar, String destinationPath, String serializationFormat, boolean silent) {
		this.lock = lock;
		this.filesCount = filesCount;
		this.triplesPerFile = triplesPerFile;
		this.triplesGeneratedSoFar = triplesGeneratedSoFar;
		this.destinationPath = destinationPath;
		this.serializationFormat = serializationFormat;
		this.silent = silent;
		sourceSesameModelArrayList = new ArrayList<Model>();
		targetSesameModelArrayList = new ArrayList<Model>();
	
	}
	public Worker() {} 

	@Override
	public void execute() throws Exception {
		FileOutputStream sourceFos = null;
		FileOutputStream targetFos = null;
		gsFos = null;
		rdfFormat = SesameUtils.parseRdfFormat(serializationFormat);

		long currentFilesCount = filesCount.incrementAndGet();
		String targetDestination = "TargetDatasets"; // main location for uploads
		String goldStandardDestination = "GoldStandards"; // main location for uploads
        File theFile = new File(targetDestination); 
        theFile.mkdirs();// will create a folder for the transformed data if not exists
        File theFilegs = new File(goldStandardDestination); 
        theFilegs.mkdirs();// will create a folder for the transformed data if not exists

		sourceFileName = String.format(FILENAME_SOURCE_FORMAT + rdfFormat.getDefaultFileExtension(), destinationPath, File.separator, currentFilesCount);
		String targetFileName = String.format(FILENAME_TARGET_FORMAT + rdfFormat.getDefaultFileExtension(), targetDestination, File.separator, currentFilesCount);
		String gsFileName = String.format(FILENAME_GS_FORMAT + rdfFormat.getDefaultFileExtension(), goldStandardDestination, File.separator, currentFilesCount);
	
		initializeFMapEntry();
		
		int instancesInFileCount = 0;
		int currentTriplesCount = 0;
		boolean stop = false;
		boolean stopClasses = false;

		RepositoryConnection conn = ConnectToRepository();
		if(conn.size() < TestDriver.getConfigurations().getInt(Configurations.TOTAL_TRIPLES)){
			targetTriples = conn.size(); 
		}
		else{
			targetTriples = TestDriver.getConfigurations().getInt(Configurations.TOTAL_TRIPLES); //or target size that user gave
		}
		CloseConnection(conn);
		
		//skip data generation if targetTriples size has already been reached 
		if (triplesGeneratedSoFar.get() >= targetTriples) {
			System.out.println(Thread.currentThread().getName() + " :: generated triples so far: " + String.format("%,d", triplesGeneratedSoFar.get()) + " have reached the targeted triples size: " + String.format("%,d", targetTriples) + ". Generating is cancelled");
			return;
		}
		//loop until the generated triples have reached the targeted totalTriples size
		while (true) {
			if (triplesGeneratedSoFar.get() >= targetTriples) {					
				break;
			}
			if(stop){
				break;
			}
			
			try {
				sourceFos = new FileOutputStream(sourceFileName);	
				targetFos = new FileOutputStream(targetFileName);
				gsFos = new FileOutputStream(gsFileName);
				Model resultsModel = new LinkedHashModel();
				ArrayList<String> classes = new ArrayList<String>();
				classes.addAll(TestDriver.getConfigurations().getArray(Configurations.TRANSFORM_CLASS_INSTANCES));
				
				while (true) {
					
					if (currentTriplesCount >= (triplesPerFile-(triplesPerFile*0.1))) { /*let the triples per file be in a range from triplesPerFile-(triplesPerFile*0.1) (10% less than accepted)*/
						break;
					}					

					if (triplesGeneratedSoFar.get() >= targetTriples) {					
						break;
					}
					synchronized(lock) {	
					
					RepositoryConnection con = ConnectToRepository();
					GraphQueryResult graphResult;
					if(!TestDriver.getConfigurations().getArray(Configurations.TRANSFORM_CLASS_INSTANCES).isEmpty()){
							
						if(!offsetArray.contains(offset)){
							for (String c : classes){
								graphResult = con.prepareGraphQuery(QueryLanguage.SPARQL, 
										"CONSTRUCT {?s ?p ?o}"
										 + "FROM <"+endpointUrl+"/datasets> " 
										 + "WHERE {?s  a  <"+c+"> ."
										 		+ "?s ?p ?o . + "
										 		+ "FILTER (!isBlank(?s) && !isBlank(?o))"
										 	+ "}"
										 + "ORDER BY ASC (?s)"
										 + "LIMIT "+TestDriver.getConfigurations().getInt(Configurations.TRIPLES_PER_FILE)/classes.size()
										 + "OFFSET "+(TestDriver.getConfigurations().getInt(Configurations.TRIPLES_PER_FILE)*offset)/classes.size()).evaluate();
								
								int sizeBefore = resultsModel.size();
								resultsModel.addAll(QueryResults.asModel(graphResult));
								int sizeAfter = resultsModel.size();
								if(sizeBefore == sizeAfter){stopClasses=true;resultsModel = new LinkedHashModel(); break;}
							}
							offsetArray.add(offset);
							offset++;
						}
					}
					else{
					graphResult = con.prepareGraphQuery(QueryLanguage.SPARQL, 
					   "CONSTRUCT {?s ?p ?o}"
					   + "FROM <"+endpointUrl+"/datasets> " 
					   + "WHERE {?s ?p ?o . "
					   + "FILTER (!isBlank(?s) && !isBlank(?o))"
					   + "}"
					   + "ORDER BY ASC (?s)"
					   + "LIMIT "+TestDriver.getConfigurations().getInt(Configurations.TRIPLES_PER_FILE)
					   + "OFFSET "+(TestDriver.getConfigurations().getInt(Configurations.TRIPLES_PER_FILE)*(currentFilesCount-1))).evaluate();
					
					resultsModel = QueryResults.asModel(graphResult);
					}
			   		
					try{
					    sourceSesameModel = new LinkedHashModel();
				   		Iterator<Statement> statement = resultsModel.iterator();
						Statement previousStatement = null;
							while(statement.hasNext()){
								Statement thisStatement = statement.next();		
								if(previousStatement == null || previousStatement.getSubject().stringValue().equals(thisStatement.getSubject().stringValue())){
									sourceSesameModel.add(thisStatement);
								}
								else{
									Rio.write(sourceSesameModel, sourceFos, rdfFormat); 
									//call transf here
									gsSesameModel = new LinkedHashModel();
									
									targetSesameModel =  CreateTargetModel(this,gsSesameModel,gsFos); 
									//System.out.println("sourceSesameModel size : " +sourceSesameModel.size()); 
									//System.out.println("targetSesameModel size : " +targetSesameModel.size()); 
									Rio.write(targetSesameModel, targetFos, rdfFormat);
									
									instancesInFileCount++;
									currentTriplesCount += sourceSesameModel.size();		
									triplesGeneratedSoFar.addAndGet(sourceSesameModel.size());
									
									sourceSesameModel = new LinkedHashModel();
									sourceSesameModel.add(thisStatement);
								}
								previousStatement = thisStatement;				
							}
						}
						finally {
							CloseConnection(con);
						}
					if(resultsModel.isEmpty() || stopClasses){
						stop = true;
						break;
					}
					
					}
				}
				
				sourceSesameModelArrayList = new ArrayList<Model>();
				targetSesameModelArrayList = new ArrayList<Model>();

				flushClose(sourceFos);
				flushClose(targetFos);
				flushClose(gsFos);

				if (!silent && instancesInFileCount > 0) {
					System.out.println(Thread.currentThread().getName() + " :: Saving file #" + currentFilesCount + " with " + String.format("%,d", instancesInFileCount) + " instances. Generated triples so far: " + String.format("%,d", triplesGeneratedSoFar.get()));
				}

				instancesInFileCount = 0;
				currentTriplesCount = 0;

				currentFilesCount = filesCount.incrementAndGet();
				sourceFileName = String.format(FILENAME_SOURCE_FORMAT + rdfFormat.getDefaultFileExtension(), destinationPath, File.separator, currentFilesCount);
				targetFileName = String.format(FILENAME_TARGET_FORMAT + rdfFormat.getDefaultFileExtension(), targetDestination, File.separator, currentFilesCount);
				gsFileName = String.format(FILENAME_GS_FORMAT + rdfFormat.getDefaultFileExtension(), goldStandardDestination, File.separator, currentFilesCount);		
				
				initializeFMapEntry(); //do not remove this
			
			} catch (RDFHandlerException e) {
				flushClose(sourceFos);
				flushClose(targetFos);
				flushClose(gsFos);
				
				throw new IOException("A problem occurred while generating RDF data: " + e.getMessage());
			}
		}
	}
	
	protected synchronized void flushClose(FileOutputStream fos) throws IOException {
		if (fos != null) {
			fos.flush();
			fos.close();
		}
	}
	
	public RepositoryConnection ConnectToRepository() throws RepositoryException{
		Repository repository = null;
        try{
        	repository = new HTTPRepository(endpointUrl);
        	repository.initialize();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
		return repository.getConnection();
	}
	
	public void CloseConnection(RepositoryConnection con) throws RepositoryException{
		con.close();
	}
	
	@SuppressWarnings("static-access")
	public void initializeFMapEntry(){
		if(!getFtransformations().containsKey(sourceFileName)){
			FTransfArray = new ArrayList<Double>();
			for(int i=0; i <38; i++){ //TODO check num
				FTransfArray.add(0.0);
			}	
			this.getFtransformations().put(sourceFileName, FTransfArray);	
		}
	}
	
	@Override
	public Model getSourceSesameModel() {
		return sourceSesameModel;
	}

	@Override
	public Model getTargetSesameModel() {
		return targetSesameModel;
	}
	@Override
	public Model getGSSesameModel() {
		return gsSesameModel;
	}
	
	@Override
	public FileOutputStream getGSFileOutputStream() {
		return gsFos;
	}
	
	@Override
	public ArrayList<Model> getSourceSesameModelArrayList() {
		return sourceSesameModelArrayList;
	}
	@Override
	public ArrayList<Model> getTargetSesameModelArrayList() {
		return targetSesameModelArrayList;
	}	
	@Override
	public HashMap<String, String> getURIMapping() {
		return URIMapping;
	}
	@Override
	public String getSourceFileName() {
		return sourceFileName;
	}

	@Override
	public Map<String, Transformation> getTransformationConfiguration() {
		return TransformationsConfiguration;
	}

	
}


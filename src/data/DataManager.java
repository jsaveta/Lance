
package data;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import properties.Configurations;
import properties.Definitions;
import util.TransformationsMeasurements;
import endpoint.SparqlQueryExecuteManager;
import generators.data.DataGenerator;
import main.TestDriver;
//import eu.ldbc.semanticpublishing.refdataset.DataManager;

/**
 * @author jsaveta, Foundation for Research and Technology-Hellas (FORTH)
 *
 * @date Dec 11, 2014
 */
public class DataManager {

	private final AtomicBoolean inBenchmarkState = new AtomicBoolean(false);
	private final Definitions definitions = new Definitions();
	protected SparqlQueryExecuteManager queryExecuteManager;
	protected Repository repository;
	
    

	public DataManager() {		
	
		queryExecuteManager = new SparqlQueryExecuteManager(inBenchmarkState,
				TestDriver.getConfigurations().getString(Configurations.ENDPOINT_URL),
				TestDriver.getConfigurations().getString(Configurations.ENDPOINT_UPDATE_URL),
				TestDriver.getConfigurations().getInt(Configurations.QUERY_TIMEOUT_SECONDS) * 1000,
				TestDriver.getConfigurations().getInt(Configurations.SYSTEM_QUERY_TIMEOUT_SECONDS) * 1000,
				TestDriver.getConfigurations().getBoolean(Configurations.VERBOSE));

	}
	/**
	 * Call the execution phases
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	public void executePhases() throws Exception {

		TransformationsMeasurements timer = new TransformationsMeasurements();
		timer.start(); //measure loading time, start
		
		new LoadOntologies(TestDriver.getConfigurations().getBoolean(Configurations.LOAD_ONTOLOGIES));
		new LoadDatasets(TestDriver.getConfigurations().getBoolean(Configurations.LOAD_REFERENCE_DATASETS));
		
		timer.stop(); //measure loading time, stop
		timer.setLoadingTime(timer.getDuration()); //measured loading time
		
		generateData(); 
		new ClearDatabase(TestDriver.getConfigurations().getBoolean(Configurations.CLEAR_DATABASE));
		
		System.out.println("END OF BENCHMARK RUN, all agents shut down...");
		timer.writeLogFile();
		
		System.exit(0);
	}

	private void generateData() throws IOException, InterruptedException, RDFHandlerException, RDFParseException, RepositoryException, MalformedQueryException, QueryEvaluationException {
		System.out.println("Retrieving source data files...");
		long triplesPerFile = TestDriver.getConfigurations().getLong(Configurations.TRIPLES_PER_FILE);
		String destinationPath = TestDriver.getConfigurations().getString(Configurations.SOURCE_PATH);
		String serializationFormat = TestDriver.getConfigurations().getString(Configurations.GENERATED_DATA_FORMAT);	
		int generatorThreads = TestDriver.getConfigurations().getInt(Configurations.WORKERS);
		DataGenerator dataGenerator = new DataGenerator(TestDriver.getConfigurations(), definitions, generatorThreads, triplesPerFile, destinationPath, serializationFormat);
		dataGenerator.produceData();
	}

}

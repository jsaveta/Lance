/**
 * 
 */
package properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;


/**
 * A holder for all the benchmark configuration parameters.
 * 
 * A client is expected to instantiate this class, which will provide values
 * (defaults or blank) for all configuration parameters, and then to save this
 * to a file (to create a template configuration file) or to load it from a file
 * (which is the usual case).
 * 
 */
public class Configurations {
	public static final String QUERY_TIMEOUT_SECONDS = "queryTimeoutSeconds";
	public static final String SYSTEM_QUERY_TIMEOUT_SECONDS = "systemQueryTimeoutSeconds";
	public static final String TRIPLES_PER_FILE = "triplesPerFile";
	public static final String TOTAL_TRIPLES = "totalTriples";
	public static final String WORKERS = "workers";
	public static final String ENDPOINT_URL = "endpointURL";
	public static final String ENDPOINT_UPDATE_URL = "endpointUpdateURL";
	public static final String ONTOLOGIES_PATH = "ontologiesPath";
	public static final String REFERENCE_DATASETS_PATH = "referenceDatasetsPath";
	public static final String DEFINITIONS_PATH = "definitionsPath";
	public static final String SOURCE_PATH = "sourcePath";
	public static final String GENERATED_DATA_FORMAT = "generatedDataFormat";
	public static final String WORDNET_PATH = "wordnetPath";
	public static final String RESCAL_RANK = "rescalRank";
	public static final String FILES_FOR_RESCAL_SAMPLING = "rescalSampling";
	public static final String VALUE_SEVERITY = "valueSeverity";
	public static final String VALUE_TOKEN = "valueToken";
	public static final String VALUE_ABBREVIATION = "valueAbbreviation";
	public static final String OUTPUT_LANGUAGE = "outputLanguage";
	public static final String DATE_FORMAT = "dateFormat";
	public static final String NEW_DATE_FORMAT = "newDateFormat";
	public static final String EXTRACT_PROPERTY = "extractProperty";
	public static final String INFERENSE_SUBCLASS_SUBPROPERTY = "inferenceSubClassSubProperty";
	public static final String CHANGE_URIS = "changeURIs";
	public static final String NEW_URI_NAMESPACE = "newURInamespace";
	public static final String TRANSFORM_CLASS_INSTANCES = "transformClassInstances";
	public static final String VERBOSE = "verbose";
	public static final String LOAD_ONTOLOGIES = "loadOntologies";
	public static final String LOAD_REFERENCE_DATASETS = "loadReferenceDatasets";
	public static final String CLEAR_DATABASE = "clearDatabase";
	
	/**
	 * Initialise and set default values for parameters that make sense.
	 */
	public Configurations() {
		properties.setProperty(QUERY_TIMEOUT_SECONDS, "90" );
		properties.setProperty(SYSTEM_QUERY_TIMEOUT_SECONDS, "3600" );
		properties.setProperty(TRIPLES_PER_FILE, "1000" );
		properties.setProperty(TOTAL_TRIPLES, "10000" );
		properties.setProperty(WORKERS, "1");
		properties.setProperty(ENDPOINT_URL, "" );
		properties.setProperty(ENDPOINT_UPDATE_URL, "" );
		properties.setProperty(ONTOLOGIES_PATH, "./datasets_and_ontologies/ontologies");
		properties.setProperty(REFERENCE_DATASETS_PATH, "./datasets_and_ontologies/datasets");
		properties.setProperty(DEFINITIONS_PATH, "./definitions.properties");
		properties.setProperty(SOURCE_PATH, "./SourceDatasets");
		properties.setProperty(GENERATED_DATA_FORMAT,"turtle");
		properties.setProperty(WORDNET_PATH, "C:/Program Files/WordNet/2.1/dict/");
		properties.setProperty(RESCAL_RANK, "10");
		properties.setProperty(FILES_FOR_RESCAL_SAMPLING, "5");
		properties.setProperty(VALUE_SEVERITY, "0.5");
		properties.setProperty(VALUE_TOKEN, "a");
		properties.setProperty(VALUE_ABBREVIATION, "0"); //NDOTS = 0; SCOMMANDOT = 1; ALLDOTS = 2;
		properties.setProperty(OUTPUT_LANGUAGE, "GREEK");
		properties.setProperty(DATE_FORMAT, "yyyy-MM-dd");
		properties.setProperty(NEW_DATE_FORMAT, "1");
		properties.setProperty(EXTRACT_PROPERTY, "2");
		properties.setProperty(INFERENSE_SUBCLASS_SUBPROPERTY, "true");
		properties.setProperty(CHANGE_URIS, "true");
		properties.setProperty(NEW_URI_NAMESPACE, "http://www.ldbc.eu/");
		properties.setProperty(TRANSFORM_CLASS_INSTANCES, ""); // if empty transform all instances, split with ,
		properties.setProperty(VERBOSE, "false" );
		properties.setProperty(LOAD_ONTOLOGIES, "true");
		properties.setProperty(LOAD_REFERENCE_DATASETS, "true");
		properties.setProperty(CLEAR_DATABASE, "false");
	}
	
	/**
	 * Load the configuration from the given file (java properties format).
	 * @param filename A readable file on the file system.
	 * @throws IOException
	 */
	public void loadFromFile(String filename) throws IOException {
		
		InputStream input = new FileInputStream(filename);
		try {
			properties.load(input);
		}
		finally {
			input.close();
		}
	}
	
	/**
	 * Save the configuration to a text file (java properties format).
	 * @param filename
	 * @throws IOException
	 */
	public void saveToFile(String filename) throws IOException {
		OutputStream output = new FileOutputStream(filename);
		try {
			properties.store(output, "");
		}
		finally {
			output.close();
		}
	}
	
	/**
	 * Read a configuration parameter's value as a string
	 * @param key
	 * @return
	 */
	public String getString( String key) {
		String value = properties.getProperty(key);
		
		if(value == null) {
			throw new IllegalStateException( "Missing configuration parameter: " + key);
		}
		return value;
	}

	/**
	 * Read a configuration parameter's value as a boolean
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key) {
		String value = getString(key);
		
		if(value.equalsIgnoreCase("true") || value.equals("1") || value.equalsIgnoreCase("y") ) {
			return true;
		}
		if(value.equalsIgnoreCase("false") || value.equals("0") || value.equalsIgnoreCase("n") ) {
			return false;
		}
		throw new IllegalStateException( "Illegal value for boolean configuration parameter: " + key);
	}

	/**
	 * Read a configuration parameter's value as an int
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		String value = getString(key);
		
		try {
			return Integer.parseInt(value);
		}
		catch( NumberFormatException e ) {
			throw new IllegalStateException( "Illegal value for integer configuration parameter: " + key);
		}
	}
	/**
	 * Read a configuration parameter's value as an double
	 * @param key
	 * @return
	 */
	public double getDouble(String key) {
		String value = getString(key);
		
		try {
			return Double.parseDouble(value);
		}
		catch( NumberFormatException e ) {
			throw new IllegalStateException( "Illegal value for double configuration parameter: " + key);
		}
	}

	/**
	 * Read a configuration parameter's value as a long
	 * @param key
	 * @return
	 */
	public long getLong(String key) {
		String value = getString(key);
		
		try {
			return Long.parseLong(value);
		}
		catch( NumberFormatException e ) {
			throw new IllegalStateException( "Illegal value for long integer configuration parameter: " + key);
		}
	}
	
	/**
	 * Read a configuration parameter's value as a array
	 * @param key
	 * @return
	 */
	public Collection<? extends String> getArray(String key) {
		String value = properties.getProperty(key);
		ArrayList <String> array = new ArrayList<String>();
		value = value.replace(" ", "");
		if(value.equals("")) {
			//System.out.println("We will transform all classes. ");
		}else{
			//System.out.println("We will transform those classes: " +value);
			Collections.addAll(array, value.split(","));
		}
		return array;
	}
	
	private final Properties properties = new Properties();
	
	public static void main(String[] args) throws IOException  {
		Configurations c = new Configurations();
		c.saveToFile("default_config.properties");
	}
}


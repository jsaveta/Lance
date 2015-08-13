package properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import util.AllocationsUtil;

/**
 * A holder of the benchmark definitions of allocation values.
 * Client is expected to initialize from file definitions.properties first all allocation values. 
 * 
 */
public class Definitions {
	public static final String TRANSFORMATION_ALLOCATION = "transformationAllocation";
	public static final String VALUE_ALLOCATION = "valueAllocation";
	public static final String STRUCTURE_ALLOCATION = "structureAllocation";
	public static final String SEMANTICS_AWARE_ALLOCATION = "semanticsAwareAllocation";
	public static final String SIMPLE_COMBINATION_ALLOCATION = "simpleCombinationAllocation";
	public static final String COMPLEX_COMBINATION_ALLOCATION = "complexCombinationAllocation";
	public static final String COMPLEX_COMBINATION_SEMANTICS_ALLOCATION = "complexCombinationForSemanticsAwareAllocation";
	
	
	//Determines allocation of transformation
	public static AllocationsUtil transformationAllocation;
	
	//Determines allocation of lexical transformation
	public static AllocationsUtil valueAllocation;
	
	//Determines allocation of structural transformation
	public static AllocationsUtil structureAllocation;
	
	//Determines allocation of logical transformation
	public static AllocationsUtil semanticsAwareAllocation;
	
	//Determines allocation of simple combination transformation
	public static AllocationsUtil simpleCombinationAllocation;
	
	//Determines allocation of complex combination transformation
	public static AllocationsUtil complexCombinationAllocation;
	
	//Determines allocation of semantics aware transformations in orded to be user for complex transformations
	public static AllocationsUtil complexCombinationForSemanticsAwareAllocation;
	
	private static final Properties definitionsProperties = new Properties();
	
	private boolean verbose = false;
	
	/**
	 * Load the configuration from the given file (java properties format).
	 * @param filename A readable file on the file system.
	 * @throws IOException
	 */
	public void loadFromFile(String filename, boolean verbose) throws IOException {
		
		InputStream input = new FileInputStream(filename);
		try {
			definitionsProperties.load(input);
		}
		finally {
			input.close();
		}
		this.verbose = verbose;
	}
	
	/**
	 * Read a definition parameter's value as a string
	 * @param key
	 * @return
	 */
	private String getString( String key) {
		String value = definitionsProperties.getProperty(key);
		
		if(value == null) {
			throw new IllegalStateException( "Missing definitions parameter: " + key);
		}
		return value;
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
	 * Read a configuration parameter's value as a Double
	 * @param key
	 * @return
	 */
	public double getDouble(String key) {
		String value = getString(key);
		
		try {
			return Double.parseDouble(value);
		}
		catch( NumberFormatException e ) {
			throw new IllegalStateException( "Illegal value for long integer configuration parameter: " + key);
		}
	}	
	
	public void setLong(String key, long value) {
		definitionsProperties.setProperty(key, Long.toString(value));
	}
	
	public void initializeAllocations(Random random) {
		if (verbose) {
			System.out.println("Initializing allocations...");
		}
		
		initializeAllocation(TRANSFORMATION_ALLOCATION, random);
		initializeAllocation(VALUE_ALLOCATION, random);
		initializeAllocation(STRUCTURE_ALLOCATION, random);
		initializeAllocation(SEMANTICS_AWARE_ALLOCATION, random);
		initializeAllocation(SIMPLE_COMBINATION_ALLOCATION, random);
		initializeAllocation(COMPLEX_COMBINATION_ALLOCATION, random);
		initializeAllocation(COMPLEX_COMBINATION_SEMANTICS_ALLOCATION, random);
	}
	
	public static void reconfigureAllocations(Random random) {
		transformationAllocation.setRandom(random);
		valueAllocation.setRandom(random);
		structureAllocation.setRandom(random);
		semanticsAwareAllocation.setRandom(random);
		simpleCombinationAllocation.setRandom(random);
		complexCombinationAllocation.setRandom(random);
		complexCombinationForSemanticsAwareAllocation.setRandom(random);
	}
	
	/**
	 * Initialize allocations depending on allocationProperty name
	 */
	private void initializeAllocation(String allocationPropertyName, Random random) {
		String allocations = getString(allocationPropertyName);
		String[] allocationsAsStrings = allocations.split(",");
		double[] allocationsAsDoubles = new double[allocationsAsStrings.length];
		
		for (int i = 0; i < allocationsAsDoubles.length; i++) {
			allocationsAsDoubles[i] = Double.parseDouble(allocationsAsStrings[i]);
		}
		
		if (allocationPropertyName.equals(TRANSFORMATION_ALLOCATION)) {
			transformationAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		} else if (allocationPropertyName.equals(VALUE_ALLOCATION)) {
			valueAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		} else if (allocationPropertyName.equals(STRUCTURE_ALLOCATION)) {
			structureAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		} else if (allocationPropertyName.equals(SEMANTICS_AWARE_ALLOCATION)) {
			semanticsAwareAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		}
		else if (allocationPropertyName.equals(SIMPLE_COMBINATION_ALLOCATION)) {
			simpleCombinationAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		}
		else if (allocationPropertyName.equals(COMPLEX_COMBINATION_ALLOCATION)) {
			complexCombinationAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		}
		else if (allocationPropertyName.equals(COMPLEX_COMBINATION_SEMANTICS_ALLOCATION)) {
			complexCombinationForSemanticsAwareAllocation = new AllocationsUtil(allocationsAsDoubles, random);
		}
		if (verbose) {
			System.out.println(String.format("\t%-33s : {%s}", allocationPropertyName, allocations));
		}
	}
	
}


package main;
import java.util.Random;

import properties.Configurations;
import properties.Definitions;
import data.DataManager;



/**
 * The start point of SPIMBENCH. Initializes and runs all parts of the benchmark.
 */
public class TestDriver {
	public static Configurations configurations = new Configurations();
	public static Definitions definitions = new Definitions();
	public DataManager manageData = new DataManager();
	protected Random randomGenerator = new Random();
	
	public TestDriver(String[] args) throws Exception {

		if( args.length < 1) {
			throw new IllegalArgumentException("Missing parameter - the configuration file must be specified");
		}
		configurations.loadFromFile(args[0]);
		definitions.loadFromFile(configurations.getString(Configurations.DEFINITIONS_PATH), configurations.getBoolean(Configurations.VERBOSE));	
		definitions.initializeAllocations(randomGenerator);
		manageData.executePhases();

	}
	public static Configurations getConfigurations(){
		return configurations;
	}
	
	public static Definitions getDefinitions(){
		return definitions;
	}
	
	public static void main(String[] args) throws Exception {
		new TestDriver(args);
	}
}


package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;



import main.TestDriver;
import properties.Configurations;
import util.FileUtils;
import util.StringUtil;

/**
 * @author jsaveta, Foundation for Research and Technology-Hellas (FORTH)
 *
 * @date Dec 11, 2014
 */
public class LoadDatasets extends DataManager{
	/**
	 * Read datasets (source) from REFERENCE_DATASETS_PATH and load them into ENDPOINT_URL.
	 * @param enable
	 * @throws IOException
	 */
	static int totalLines = 0;
	public LoadDatasets(boolean enable) throws IOException{
			String endpointUrl = TestDriver.getConfigurations().getString(Configurations.ENDPOINT_URL);
		        try{
		        	this.repository = new HTTPRepository(endpointUrl);
		        	this.repository.initialize();
		        }catch(RepositoryException ex){
		            ex.printStackTrace();
		        }
			 if (enable) {	
		        System.out.println("\n\nLoading datasets...");
				String ontologiesPath = StringUtil.normalizePath(TestDriver.getConfigurations().getString(Configurations.REFERENCE_DATASETS_PATH));
				List<File> collectedFiles = new ArrayList<File>();
				FileUtils.collectFilesList2(ontologiesPath, collectedFiles, "*", true);
				Collections.sort(collectedFiles);
			    URI schemaContext=this.repository.getValueFactory().createURI(TestDriver.getConfigurations().getString(Configurations.ENDPOINT_URL)+"/datasets");
		        try{
		            RepositoryConnection repoConn=this.repository.getConnection();
			          for(File file : collectedFiles){
			        	  System.out.println(file.getName());
			        	  repoConn.add(file, schemaContext.toString(), RDFFormat.forMIMEType(file.getName()), schemaContext); //forMIMEType finds the file format in order to read more than one rdfformats 
			          }
		            repoConn.close();
		        }catch(IOException | RDFParseException | RepositoryException ex){
		            ex.printStackTrace();
		        }
		 	}
		}
	
	
	/**
	 * Counts num of triples in given (source) dataset in order to apply the correct percentage of transformations later and create the target dataset.
	 * @return num of triples
	 */
	public static int getNumOfTriplesInSourceDataset(File file){
		int fileLines = countLinesNumber(file);
		totalLines += fileLines;
//		System.out.println("File lines " + fileLines);
//		System.out.println("Total lines " + totalLines);
	return totalLines;
	}
	
	public static int countLinesNumber(File file) {
		  int lines = 0;
		  try {
			   LineNumberReader lineNumberReader = new LineNumberReader(
			     new FileReader(file));
			   lineNumberReader.skip(Long.MAX_VALUE);
			   lines = lineNumberReader.getLineNumber();
			   lineNumberReader.close();
			   
		  } catch (FileNotFoundException e) {
			  System.out.println("FileNotFoundException Occured"+ e.getMessage());
		  } catch (IOException e) {
			  System.out.println("IOException Occured" + e.getMessage());
		  }

    return lines;
	}
}





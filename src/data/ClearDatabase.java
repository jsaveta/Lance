/**
 * 
 */
package data;

import java.io.IOException;

import endpoint.SparqlQueryConnection.QueryType;

/**
 * @author jsaveta, Foundation for Research and Technology-Hellas (FORTH)
 *
 * @date Dec 11, 2014
 */
public class ClearDatabase extends DataManager{

	
	public ClearDatabase(boolean enable) throws IOException {
				
		if (enable) {		
			System.out.println("Cleaning up database ...");
			queryExecuteManager.executeQuery("SERVICE-DELETE", " CLEAR ALL ", QueryType.DELETE);
		}
	}

}

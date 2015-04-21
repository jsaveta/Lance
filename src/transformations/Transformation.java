/**
 * 
 */
package transformations;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

/**
 * @author Alfio Ferrara, Universita` degli Studi di Milano
 * @date 18/mag/2010
 * Represents a generic transformation. The method transform takes an object and transforms it into another object.
 */
public interface Transformation {
	
	public abstract Object execute(Object arg);
	
	public String print();

	public abstract Model executeStatement(Statement statement);


	
}

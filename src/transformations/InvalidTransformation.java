/**
 * 
 */
package transformations;


/**
 * @author Alfio Ferrara, Universita` degli Studi di Milano
 * @date 18/mag/2010
 */
public class InvalidTransformation extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5441815029013851904L;

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return "Wrong type of object transformation";
	}

	
}

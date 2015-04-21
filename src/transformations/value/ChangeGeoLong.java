
package transformations.value;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import transformations.DataValueTransformation;

/**
 * @author jsaveta, Foundation for Research and Technology-Hellas (FORTH)
 *
 * @date Jan 15, 2015
 */
public class ChangeGeoLong implements DataValueTransformation{

	public ChangeGeoLong()  {
		//what happens when we change a geopoint? is like a typo or like a disjoint?

	}

	/* (non-Javadoc)
	 * @see transformations.Transformation#execute(java.lang.Object)
	 */
	@Override
	public Object execute(Object arg) {
		double longitude = Math.random() * Math.PI * 2;
		return longitude;
	}

	/* (non-Javadoc)
	 * @see transformations.Transformation#print()
	 */
	@Override
	public String print() {
		return null;
	}

	/* (non-Javadoc)
	 * @see transformations.Transformation#executeStatement(org.openrdf.model.Statement)
	 */
	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}

}


package transformations.value;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import transformations.DataValueTransformation;
import transformations.InvalidTransformation;

/**
 * @author Alfio Ferrara, Universita` degli Studi di Milano
 * @date 18/mag/2010
 * Changes a female or male string into a F or M and viceversa
 */
public class ChangeGenderFormat implements DataValueTransformation {
	
	public String print(){
		String name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1);
		return name;
	}

	/* (non-Javadoc)
	 * @see it.unimi.dico.islab.iimb.transfom.Transformation#execute(java.lang.Object)
	 */
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		String f = (String)arg;
		if(arg instanceof String){
			if(f.toLowerCase().equals("female")){
				f = "F";
			}else if(f.toLowerCase().equals("f")){
				f = "Female";
			}else if(f.toLowerCase().equals("male")){
				f = "M";
			}else if(f.toLowerCase().equals("m")){
				f = "Male";
			}
		}else{
			try {
				throw new InvalidTransformation();
			} catch (InvalidTransformation e) {
				e.printStackTrace();
			}finally{
				return arg;
			}
		}
		return f;
	}

	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}

}

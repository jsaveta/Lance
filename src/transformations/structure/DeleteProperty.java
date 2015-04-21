package transformations.structure;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import transformations.InvalidTransformation;
import transformations.Transformation;

public class DeleteProperty  implements Transformation{
	private Model model;
	public DeleteProperty(){
		model = new LinkedHashModel();
	}
	@SuppressWarnings("finally")
	public Model executeStatement(Statement arg) {
	if(arg instanceof Statement){}
	else{
		try {
			throw new InvalidTransformation();
		} catch (InvalidTransformation e) {
			e.printStackTrace();
		}finally{
			return model;
		}
	}
	return model;
	}
	@Override
	public String print() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object execute(Object arg) {
		// TODO Auto-generated method stub
		return null;
	}
}
package transformations.structure;

import generators.data.sesamemodelbuilders.SesameBuilder;

import java.lang.Object;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;

import properties.Configurations;
import transformations.InvalidTransformation;
import transformations.Transformation;
import util.RandomUtil;
import main.TestDriver;



public class AddProperty implements Transformation {
	public AddProperty(){}
	
	@SuppressWarnings("finally")
	public Model executeStatement(Statement arg) {
	Model model = new LinkedHashModel();
	if(arg instanceof Statement){
		RandomUtil ru = new RandomUtil(); 
		URI predicate = SesameBuilder.sesameValueFactory.createURI(TestDriver.getConfigurations().getString(Configurations.NEW_URI_NAMESPACE) + ru.randomChars(20)); 
		Value object = SesameBuilder.sesameValueFactory.createLiteral(ru.randomChars(30));
		
		model.add(arg);
		model.add(arg.getSubject(),predicate ,object);
		
	}
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

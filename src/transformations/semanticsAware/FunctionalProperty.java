package transformations.semanticsAware;


import generators.data.AbstractAsynchronousWorker;
import generators.data.sesamemodelbuilders.SesameBuilder;

import java.io.IOException;
import java.util.Collection;

import main.TestDriver;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import properties.Configurations;
import data.LoadOntologies;
import transformations.InvalidTransformation;
import transformations.Transformation;
import transformations.TransformationsCall;
import util.RandomUtil;

public class FunctionalProperty implements Transformation{
	private Collection<String> PropertyFunct; 
	LoadOntologies ontology;
	RandomUtil ru = new RandomUtil();
	public FunctionalProperty(AbstractAsynchronousWorker worker){ 
		try {
			ontology = new LoadOntologies(false);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	@Override
	public Object execute(Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String print() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("finally")
	@Override
	public Model executeStatement(Statement arg) {
		//System.out.println("FunctionalProperty");
		Statement s = (Statement)arg;
		PropertyFunct = TransformationsCall.functionalProperty;
		Model model = new LinkedHashModel();
		String newObject;
		if(arg instanceof Statement){
	    	if(PropertyFunct.contains(s.getPredicate().stringValue())){
	    		if(!(s.getObject() instanceof Literal)){
	    			newObject = TestDriver.getConfigurations().getString(Configurations.NEW_URI_NAMESPACE) + ru.randomChars(30);
					model.add(s.getSubject(),s.getPredicate(),SesameBuilder.sesameValueFactory.createURI(newObject),s.getContext());
	    		}
//	    		else{
//	    			newObject = ru.randomChars(30);
//	    			model.add(s.getSubject(),s.getPredicate(),SesameBuilder.sesameValueFactory.createLiteral(newObject),s.getContext());
//	    		}
	    		
	    		
	    		//do not change subject in sesamemodel 
	    		
//	    		String[] new_obj;
//	    		if(s.getObject().stringValue().contains("#")){
//	    			 new_obj = s.getObject().stringValue().split("#"); 
//	    		}
//	    		else{
//	    			 new_obj = s.getObject().stringValue().split("/"); 
//	    		}
//				String lastOne = new_obj[new_obj.length-1];
			}
//	    	else{
//	    		model.add(s);
//	    	}
  		
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

}

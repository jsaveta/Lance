package transformations.semanticsAware;


import generators.data.AbstractAsynchronousWorker;
import generators.data.sesamemodelbuilders.SesameBuilder;
import main.TestDriver;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import properties.Configurations;
import transformations.InvalidTransformation;
import transformations.Transformation;
import transformations.TransformationsCall;
import util.RandomUtil;



public class InverseFunctionalProperty implements Transformation{
	
	public InverseFunctionalProperty(AbstractAsynchronousWorker worker){ }
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
		//System.out.println("InverseFunctionalProperty");
		Statement s = (Statement)arg;
		Model model = new LinkedHashModel();
				
	    if(arg instanceof Statement){
	    	if(TransformationsCall.inverseFunctionalProperty.contains(s.getPredicate().stringValue())){
	    		if(!(s.getObject() instanceof Literal)){
//		    		int index = (int)(Math.random()*(TransformationsCall.inverseFunctionalProperty.get(s.getPredicate().stringValue()).toArray().length-1));
//		    		model.add(SesameBuilder.sesameValueFactory.createURI(s.getObject().stringValue()), SesameBuilder.sesameValueFactory.createURI(TransformationsCall.inverseFunctionalPropertyMap.get(s.getPredicate().stringValue()).toArray()[index].toString()),SesameBuilder.sesameValueFactory.createURI(s.getSubject().stringValue()),s.getContext());
	    			RandomUtil ru = new RandomUtil();
		    		String newObject = TestDriver.getConfigurations().getString(Configurations.NEW_URI_NAMESPACE) + ru.randomChars(30);
					model.add(SesameBuilder.sesameValueFactory.createURI(s.getObject().stringValue()),s.getPredicate(),SesameBuilder.sesameValueFactory.createURI(newObject),s.getContext());
	    		}
	    	}
//	    	else{
//	    		model.add(s);
//			}
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
		
//		Statement s = (Statement)arg;
//		PropertyInverseFunct = ontology.getInverseFunctionalProperties(s.getPredicate().stringValue());
//		Model model = new LinkedHashModel(); 
//		if(arg instanceof Statement){
//			if(!PropertyInverseFunct.isEmpty()){
//				//Generate number between 0-PropertyInverseFunct size
//				int index = (int)(Math.random()*(PropertyInverseFunct.size()-1));		
//				model.add(SesameBuilder.sesameValueFactory.createURI(s.getObject().stringValue()), SesameBuilder.sesameValueFactory.createURI(PropertyInverseFunct.toArray()[index].toString()),s.getSubject(),s.getContext());
//				//need to change subject in sesamemodel 
//	    	}
////			else{
////				model.add(s);
////			}
//	    }
//	    else{
//			try {
//				throw new InvalidTransformation();
//			} catch (InvalidTransformation e) {
//				e.printStackTrace();
//			}finally{
//				return model;
//			}
//		}
//	return model;
	}

}

package transformations.semanticsAware;


import generators.data.AbstractAsynchronousWorker;
import generators.data.sesamemodelbuilders.SesameBuilder;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import transformations.InvalidTransformation;
import transformations.Transformation;
import transformations.TransformationsCall;



public class SubPropertyOf implements Transformation{

	public SubPropertyOf(AbstractAsynchronousWorker worker){ }
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
		//System.out.println("SubPropertyOf");
		Statement s = (Statement)arg;
		Model model = new LinkedHashModel();
				
	    if(arg instanceof Statement){
	    	if(TransformationsCall.subPropertyOfMap.containsKey(s.getPredicate().stringValue())){
	    		int index = (int)(Math.random()*(TransformationsCall.subPropertyOfMap.get(s.getPredicate().stringValue()).toArray().length-1));
	    		model.add(s.getSubject(), SesameBuilder.sesameValueFactory.createURI(TransformationsCall.subPropertyOfMap.get(s.getPredicate().stringValue()).toArray()[index].toString()),s.getObject(),s.getContext());
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
//		PropertyHierarchy = ontology.getSuperProperties(s.getPredicate().stringValue(),TestDriver.getConfigurations().getBoolean(Configurations.INFERENSE_SUBCLASS_SUBPROPERTY));
//		Model model = new LinkedHashModel(); 
//		if(arg instanceof Statement){
//			if(!PropertyHierarchy.isEmpty()){
//				//Generate number between 0-PropertyHierarchy size
//				int index = (int)(Math.random()*(PropertyHierarchy.size()-1));		
//				model.add(s.getSubject(), SesameBuilder.sesameValueFactory.createURI(PropertyHierarchy.toArray()[index].toString()),s.getObject(),s.getContext());
//	
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

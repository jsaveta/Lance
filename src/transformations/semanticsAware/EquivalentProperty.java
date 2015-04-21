package transformations.semanticsAware;


import generators.data.AbstractAsynchronousWorker;
import generators.data.sesamemodelbuilders.SesameBuilder;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import transformations.InvalidTransformation;
import transformations.Transformation;
import transformations.TransformationsCall;



public class EquivalentProperty implements Transformation{
	

	public EquivalentProperty(AbstractAsynchronousWorker worker){	}
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
		//System.out.println("EquivalentProperty");
		Statement s = (Statement)arg;
		Model model = new LinkedHashModel();
				
	    if(arg instanceof Statement){
	    	if(TransformationsCall.equivalentPropertyMap.containsKey(s.getPredicate().stringValue())){
	    		int index = (int)(Math.random()*(TransformationsCall.equivalentPropertyMap.get(s.getPredicate().stringValue()).toArray().length-1));
	    		model.add(s.getSubject(), SesameBuilder.sesameValueFactory.createURI(TransformationsCall.equivalentPropertyMap.get(s.getPredicate().stringValue()).toArray()[index].toString()),s.getObject(),s.getContext());
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
//		PropertyEquivalence = ontology.getEquivalentProperties(s.getPredicate().stringValue());
//		Model model = new LinkedHashModel(); 
//		if(arg instanceof Statement){
//			if(!PropertyEquivalence.isEmpty()){
//				//Generate number between 0-PropertyEquivalence size
//				int index = (int)(Math.random()*(PropertyEquivalence.size()-1));		
//				model.add(s.getSubject(), SesameBuilder.sesameValueFactory.createURI(PropertyEquivalence.toArray()[index].toString()),s.getObject(),s.getContext());
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

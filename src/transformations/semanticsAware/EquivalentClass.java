package transformations.semanticsAware;


import generators.data.AbstractAsynchronousWorker;
import generators.data.sesamemodelbuilders.SesameBuilder;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import transformations.InvalidTransformation;
import transformations.Transformation;
import transformations.TransformationsCall;



public class EquivalentClass implements Transformation{

	public EquivalentClass(AbstractAsynchronousWorker worker){}
	
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
		//System.out.println("EquivalentClass");
		Statement s = (Statement)arg;
		Model model = new LinkedHashModel();
		if(arg instanceof Statement){
			if(TransformationsCall.equivalentClassMap.containsKey(s.getObject().stringValue())){
				int index = (int)(Math.random()*(TransformationsCall.equivalentClassMap.get(s.getObject().stringValue()).toArray().length -1));
				model.add(s.getSubject(), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(TransformationsCall.equivalentClassMap.get(s.getObject().stringValue()).toArray()[index].toString()),s.getContext());
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
 	
//		Statement s = (Statement)arg;
//		ClassEquivalence = ontology.getEquivalentClasses(s.getObject().stringValue());
//		Model model = new LinkedHashModel(); 
//		if(arg instanceof Statement){
//			if(!ClassEquivalence.isEmpty()){
//				//Generate number between 0-ClassEquivalence size
//				int index = (int)(Math.random()*(ClassEquivalence.size()-1));		
//				model.add(s.getSubject(), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(ClassEquivalence.toArray()[index].toString()),s.getContext());
//
//			}
//			else{
//				model.add(s);
//			}
//		}
//		else{
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

package transformations.semanticsAware;


import generators.data.AbstractAsynchronousWorker;
import generators.data.sesamemodelbuilders.SesameBuilder;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import transformations.InvalidTransformation;
import transformations.Transformation;
import transformations.TransformationsCall;



public class IntersectionOf implements Transformation{
	
	public IntersectionOf(AbstractAsynchronousWorker worker){	}
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
		//System.out.println("IntersectionOf");
		Statement s = (Statement)arg;
		Model model = new LinkedHashModel();
		if(arg instanceof Statement){
			if(TransformationsCall.intersectionOfMap.containsKey(s.getObject().stringValue())){
				int index = (int)(Math.random()*(TransformationsCall.intersectionOfMap.get(s.getObject().stringValue()).toArray().length -1));
				model.add(s.getSubject(), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(TransformationsCall.intersectionOfMap.get(s.getObject().stringValue()).toArray()[index].toString()),s.getContext());
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
//		try {
//			ClassIntersection = ontology.getIntersectionOf(s.getObject().stringValue());
//		} catch (RepositoryException | MalformedQueryException
//				| QueryEvaluationException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		Model model = new LinkedHashModel(); 
//		if(arg instanceof Statement){
//			if(!ClassIntersection.isEmpty()){
//				//Generate number between 0-ClassIntersection size
//				int index = (int)(Math.random()*(ClassIntersection.size()-1));		
//				model.add(s.getSubject(), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(ClassIntersection.toArray()[index].toString()),s.getContext());
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

package transformations.semanticsAware;


import generators.data.AbstractAsynchronousWorker;
import generators.data.sesamemodelbuilders.SesameBuilder;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import transformations.InvalidTransformation;
import transformations.Transformation;
import transformations.TransformationsCall;



public class DisjointProperty implements Transformation{

	public DisjointProperty(AbstractAsynchronousWorker worker){	}
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
		//System.out.println("DisjointProperty");
		Statement s = (Statement)arg;
		Model model = new LinkedHashModel();
				
	    if(arg instanceof Statement){
	    	if(TransformationsCall.disjointPropertyMap.containsKey(s.getPredicate().stringValue())){
	    		int index = (int)(Math.random()*(TransformationsCall.disjointPropertyMap.get(s.getPredicate().stringValue()).toArray().length-1));
	    		model.add(s.getSubject(), SesameBuilder.sesameValueFactory.createURI(TransformationsCall.disjointPropertyMap.get(s.getPredicate().stringValue()).toArray()[index].toString()),s.getObject(),s.getContext());
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
	
//	   Statement s = (Statement)arg;
//	   try {
//		PropertyDisjointness = ontology.getDisjointProperties(s.getPredicate().stringValue());
//		PropertyDisjointness.addAll(ontology.getAllDisjointProperties(s.getPredicate().stringValue()));
//	} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
//		e.printStackTrace();
//	}
//	   
//	 Model model = new LinkedHashModel(); 
//	 if(arg instanceof Statement){
//		if(!PropertyDisjointness.isEmpty()){
//			//Generate number between 0-PropertyDisjointness size
//			int index = (int)(Math.random()*(PropertyDisjointness.size()-1));		
//			model.add(s.getSubject(), SesameBuilder.sesameValueFactory.createURI(PropertyDisjointness.toArray()[index].toString()),s.getObject(),s.getContext());
//
//    	}
////		else{
////			model.add(s);
////		}
//    }
//    else{
//		try {
//			throw new InvalidTransformation();
//		} catch (InvalidTransformation e) {
//			e.printStackTrace();
//		}finally{
//			return model;
//		}
//	}
//	return model;
	}

}

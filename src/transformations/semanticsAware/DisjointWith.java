package transformations.semanticsAware;


import generators.data.AbstractAsynchronousWorker;
import generators.data.sesamemodelbuilders.SesameBuilder;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import transformations.InvalidTransformation;
import transformations.Transformation;
import transformations.TransformationsCall;



public class DisjointWith implements Transformation{

	public DisjointWith(AbstractAsynchronousWorker worker){ 

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
		//System.out.println("DisjointWith");
		Statement s = (Statement)arg;
		Model model = new LinkedHashModel();
		if(arg instanceof Statement){
			if(TransformationsCall.disjointWithMap.containsKey(s.getObject().stringValue())){
				int index = (int)(Math.random()*(TransformationsCall.disjointWithMap.get(s.getObject().stringValue()).toArray().length -1));
				model.add(s.getSubject(), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(TransformationsCall.disjointWithMap.get(s.getObject().stringValue()).toArray()[index].toString()),s.getContext());
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
		
//	   Statement s = (Statement)arg;
//	   try {
//		ClassDisjointness = ontology.getDisjointClasses(s.getObject().stringValue());
//		ClassDisjointness.addAll(ontology.getAllDisjointClasses(s.getObject().stringValue()));
//	} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
//		e.printStackTrace();
//	}
//	   
//	 Model model = new LinkedHashModel(); 
//	 if(arg instanceof Statement){
//		if(!ClassDisjointness.isEmpty()){
//		//Generate number between 0-ClassDisjointness size
//			int index = (int)(Math.random()*(ClassDisjointness.size()-1));		
//			model.add(s.getSubject(), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(ClassDisjointness.toArray()[index].toString()),s.getContext());
//    	}
//		else{
//			model.add(s);
//		}
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

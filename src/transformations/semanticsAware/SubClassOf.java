package transformations.semanticsAware;

import generators.data.AbstractAsynchronousWorker;
import generators.data.sesamemodelbuilders.SesameBuilder;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import transformations.InvalidTransformation;
import transformations.Transformation;
import transformations.TransformationsCall;



public class SubClassOf implements Transformation{

	public SubClassOf(AbstractAsynchronousWorker worker){ }
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
		//System.out.println("SubClassOf");
		Statement s = (Statement)arg;
		Model model = new LinkedHashModel();
		if(arg instanceof Statement){
			if(TransformationsCall.subClassesOfMap.containsKey(s.getObject().stringValue())){
				int index = (int)(Math.random()*(TransformationsCall.subClassesOfMap.get(s.getObject().stringValue()).toArray().length -1));
				model.add(s.getSubject(), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(TransformationsCall.subClassesOfMap.get(s.getObject().stringValue()).toArray()[index].toString()),s.getContext());
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
//		ClassHierarchy = ontology.getSuperClasses(s.getObject().stringValue(),TestDriver.getConfigurations().getBoolean(Configurations.INFERENSE_SUBCLASS_SUBPROPERTY));
//		Model model = new LinkedHashModel(); 
//		if(arg instanceof Statement){
//			if(!ClassHierarchy.isEmpty()){
//				//Generate number between 0-ClassHierarchy size
//				int index = (int)(Math.random()*(ClassHierarchy.size()-1));		
//				model.add(s.getSubject(), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(ClassHierarchy.toArray()[index].toString()),s.getContext());
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

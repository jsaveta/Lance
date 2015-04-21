package transformations.semanticsAware;

import generators.data.AbstractAsynchronousWorker;
import generators.data.sesamemodelbuilders.SesameBuilder;

import java.util.Iterator;
import java.util.Random;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;

import transformations.Transformation;

public class SameAsOnExistingInstances implements Transformation{
	private static final String owlSameAs = "http://www.w3.org/2002/07/owl#sameAs";
	
	AbstractAsynchronousWorker worker;
	Random random = new Random();
	
	public SameAsOnExistingInstances(AbstractAsynchronousWorker worker){
		this.worker = worker;
		
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

@Override
public Model executeStatement(Statement statement) {
	Model model = new LinkedHashModel();
	Value sourceObject1 = statement.getObject(), sourceObject2 = statement.getObject(), targetObject1 = statement.getObject(), targetObject2 = statement.getObject();
	Resource sourceSubject1 = statement.getSubject(), sourceSubject2 = statement.getSubject(), targetSubject1 = statement.getSubject(), targetSubject2 = statement.getSubject();
	Model sourceModel1 = new LinkedHashModel();
	Model sourceModel2 = new LinkedHashModel();
	Model targetModel1 = new LinkedHashModel();
	Model targetModel2 = new LinkedHashModel();
	int index1 = 0, index2 = 0, times =0;
	//too much effort to find 4 instances with same type! suggested to call this transformation with small percentage! 
	if(!worker.getSourceSesameModelArrayList().isEmpty() && worker.getSourceSesameModelArrayList().size() >= 2){		
		do{
			times ++; //need times in order to stop the loop when we tried many times to find pairs of instances, many times means size of modelarraylist
			index1 = random.nextInt(worker.getSourceSesameModelArrayList().size() - 1); 
			index2 = random.nextInt(worker.getSourceSesameModelArrayList().size() - 1); 
			
//			System.out.println("index1 "+index1 +", index2 " + index2);
//			System.out.println("worker.getSourceSesameModelArrayList().size() "+worker.getSourceSesameModelArrayList().size());
//			System.out.println("worker.getTargetSesameModelArrayList().size() "+worker.getTargetSesameModelArrayList().size());
			
			/*Pick two instances from source model*/
			sourceModel1 = worker.getSourceSesameModelArrayList().get(index1);
			sourceModel2 = worker.getSourceSesameModelArrayList().get(index2);
			
			/*Pick two instances from target model*/
			targetModel1 = worker.getTargetSesameModelArrayList().get(index1);
			targetModel2 = worker.getTargetSesameModelArrayList().get(index2);
			
			/*Keep subjects and objects from the first triple of the instances in order to check and return*/
			Iterator<Statement> sourceIt1 = sourceModel1.iterator();
			while(sourceIt1.hasNext()){	
				Statement st = sourceIt1.next();
				sourceSubject1 = st.getSubject();
				sourceObject1 = st.getObject();
				break;
			}
			
			Iterator<Statement> sourceIt2 = sourceModel2.iterator();
			while(sourceIt2.hasNext()){
				Statement st = sourceIt2.next();
				sourceSubject2 = st.getSubject();
				sourceObject2 = st.getObject();
				break;
			}
			
			Iterator<Statement> targetIt1 = targetModel1.iterator();
			while(targetIt1.hasNext()){
				Statement st = targetIt1.next();
				targetSubject1 = st.getSubject();
				targetObject1 = st.getObject();
				break;
			}
			
			Iterator<Statement> targetIt2 = targetModel2.iterator();
			while(targetIt2.hasNext()){
				Statement st = targetIt2.next();
				targetSubject2 = st.getSubject();
				targetObject2 = st.getObject();
				break;
			}
			
		}
		while((times <  worker.getSourceSesameModelArrayList().size()) 
				&& (sourceSubject1.stringValue().equals(sourceSubject2.stringValue())
				|| (sourceObject1 instanceof Literal) || (sourceObject2 instanceof Literal) 
				|| (targetObject1 instanceof Literal) || (targetObject2 instanceof Literal) 
				|| !sourceObject1.stringValue().equals(sourceObject2.stringValue()) 
				|| !targetObject1.stringValue().equals(targetObject2.stringValue()) 
				|| !sourceObject1.stringValue().equals(targetObject1.stringValue())));
		
		//System.out.println("sourceObject1: "+sourceObject1.stringValue() +", sourceObject2: "+sourceObject2.stringValue() +", targetObject1: "+targetObject1.stringValue() +", targetObject2: "+targetObject2.stringValue());
		if(!targetSubject1.stringValue().equals(statement.getSubject().stringValue())  && !targetSubject2.stringValue().equals(statement.getSubject().stringValue())){
		    this.worker.getGS().WriteGSAsTriples(sourceSubject1.stringValue(), targetSubject2.stringValue(),0.0,"SameAsOnExistingInstances",statement.getObject().stringValue(),this.worker.getGSSesameModel(),this.worker.getGSFileOutputStream());
			this.worker.getGS().WriteGSAsTriples(sourceSubject2.stringValue(), targetSubject1.stringValue(),0.0,"SameAsOnExistingInstances",statement.getObject().stringValue(),this.worker.getGSSesameModel(),this.worker.getGSFileOutputStream());
			
			model.add(targetSubject1, SesameBuilder.sesameValueFactory.createURI(owlSameAs), targetSubject2, (Resource)null);
		}
	}
	//System.out.println("statement.toString() "+statement.toString());
	model.add(SesameBuilder.sesameValueFactory.createURI(worker.getURIMapping().get(statement.getSubject().stringValue())),statement.getPredicate(), statement.getObject(), statement.getContext());
	
	return model;
}


}

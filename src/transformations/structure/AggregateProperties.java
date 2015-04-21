package transformations.structure;

import java.util.Iterator;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;

import generators.data.AbstractAsynchronousWorker;
import generators.data.sesamemodelbuilders.SesameBuilder;
import transformations.InvalidTransformation;
import transformations.Transformation;


public class AggregateProperties implements Transformation{
	private AbstractAsynchronousWorker worker; 
	
	public AggregateProperties(AbstractAsynchronousWorker worker) {
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
	@SuppressWarnings("finally")
	public Model executeStatement(Statement arg) { 
	Model model = new LinkedHashModel();
	 if(arg instanceof Statement){
		Model temp = worker.getSourceSesameModel(); 
		Iterator<Statement> it = temp.iterator();
		while(it.hasNext())
		{	
			Statement st = it.next();
			if(st.getPredicate().toString().equals(arg.getPredicate().toString())){
				if(it.hasNext()){
					st = it.next();
					if(!arg.getPredicate().stringValue().equals(st.getPredicate().stringValue())){
		    			Value o = SesameBuilder.sesameValueFactory.createLiteral( arg.getObject().stringValue()+ " " + st.getObject().stringValue());
		    			URI p =  SesameBuilder.sesameValueFactory.createURI( arg.getPredicate().stringValue()+ st.getPredicate().stringValue().replace(st.getPredicate().getNamespace(), ""));
		    			model.add(st.getSubject(), (URI)p,(Value)o, st.getContext());
					}
				}
			}
		}
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
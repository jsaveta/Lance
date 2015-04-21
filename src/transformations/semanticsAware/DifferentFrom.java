
package transformations.semanticsAware;

import generators.data.AbstractAsynchronousWorker;
import generators.data.sesamemodelbuilders.SesameBuilder;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;

import transformations.Transformation;
import transformations.TransformationsCall;
import util.RandomUtil;

public class DifferentFrom implements Transformation{
	AbstractAsynchronousWorker worker;
	private static final String owldifferentFrom = "http://www.w3.org/2002/07/owl#differentFrom";
	Random random = new Random();
	
	public DifferentFrom(AbstractAsynchronousWorker worker){
		this.worker = worker;
		
	}

@Override
	public Object execute(Object arg) {
	return null;
	}

	@Override
	public String print() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model executeStatement(Statement st) {
		Model targetModel = new LinkedHashModel();
		RandomUtil ru = new RandomUtil();
		
		Resource newSubject = SesameBuilder.sesameValueFactory.createURI(ru.randomUniqueURI());
		Resource sourceSubject = SesameBuilder.sesameValueFactory.createURI(worker.getURIMapping().get(st.getSubject().stringValue()));
		
		TransformationsCall tr = new TransformationsCall(worker);
		tr.setTransformationConfigurations();
		Map <String, Transformation> transformationConfigurations = tr.getTransformationConfigurations();
		
		Iterator<Statement> it = this.worker.getSourceSesameModel().iterator();
		while(it.hasNext())
		{
			Statement statement = it.next();
			
			/*Value transformations*/
			if(!transformationConfigurations.isEmpty() && transformationConfigurations.containsKey(statement.getPredicate().stringValue())){
				if(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getPackage().getName().equals("transformations.value")){
					Object temp = transformationConfigurations.get(statement.getPredicate().stringValue()).execute(statement.getObject().stringValue());
					if(!(temp.toString().equals(statement.getObject().stringValue()))){
						if (temp instanceof Literal){
							targetModel.add(newSubject, statement.getPredicate(),(Value)temp, statement.getContext());
						}
						else if(temp instanceof String){
							targetModel.add(newSubject, statement.getPredicate(),SesameBuilder.sesameValueFactory.createLiteral(temp.toString()), statement.getContext());
						}
					}else{
						targetModel.add(newSubject,statement.getPredicate(),statement.getObject(), statement.getContext());
					}
				} 
				/*Structure transformations*/ 
				else if(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getPackage().getName().equals("transformations.structure")){
					Model tempModel = transformationConfigurations.get(statement.getPredicate().stringValue()).executeStatement(statement);
					if(!tempModel.isEmpty()){ 
						for (Statement tempStatement : tempModel){
							targetModel.add(newSubject,tempStatement.getPredicate(),tempStatement.getObject(),statement.getContext());											
						}	
						if(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getName().equals("AggregateProperties")){
							if(it.hasNext())statement = it.next();
						}
						
					}
				}
				/*Semantics aware transformations for properties*/
				else if(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getPackage().getName().equals("transformations.semanticsAware")){
					Model tempModel = transformationConfigurations.get(statement.getPredicate().stringValue()).executeStatement(statement);
					if(!tempModel.isEmpty()){ 
						for (Statement tempStatement : tempModel){
							targetModel.add(newSubject,tempStatement.getPredicate(),tempStatement.getObject(),statement.getContext());
						}
					}
					else{
						targetModel.add(newSubject,statement.getPredicate(),statement.getObject(),statement.getContext());
					}
				}
			}
			else{
				targetModel.add(newSubject,statement.getPredicate(),statement.getObject(), statement.getContext());
			}
		}
		
		Model model = new LinkedHashModel();
		model.addAll(targetModel);
		model.add(newSubject, SesameBuilder.sesameValueFactory.createURI(owldifferentFrom), sourceSubject); 
		
		Iterator<Statement> itNew = this.worker.getSourceSesameModel().iterator();
		while(itNew.hasNext()){
			Statement stS = itNew.next();
			model.add(sourceSubject,stS.getPredicate(), stS.getObject(), stS.getContext());
		}

		this.worker.getGS().WriteGSAsTriples(st.getSubject().stringValue(), sourceSubject.stringValue(),0.0,"DifferentFrom",st.getObject().stringValue(),this.worker.getGSSesameModel(),this.worker.getGSFileOutputStream());
		return model;
	}
}

package transformations.goldStandard;

import java.io.FileOutputStream;
import java.util.ArrayList;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;

import generators.data.AbstractAsynchronousWorker;
import generators.data.Worker;
import generators.data.sesamemodelbuilders.SesameBuilder;
import util.SesameUtils;


public class WriteIntermediateGS extends Worker {
	Model GSModel = null;
	private String exactmatch;
	private static int count; 
	static Throwable t = new Throwable(); 
	static StackTraceElement[] elements = t.getStackTrace(); 

	private AbstractAsynchronousWorker worker_; 
	private String value1;String value2; Double weight;String transformation;String property;
	private ArrayList <String> transformationsArrayList;
	
	
	public WriteIntermediateGS(AbstractAsynchronousWorker worker){
		super();
		exactmatch = "http://www.w3.org/2004/02/skos/core#exactMatch";
		this.worker_ = worker;
		transformationsArrayList = new ArrayList<String>();
		transformationsArrayList.add("NotTransformed");
		// 1 - 18 are value transformations
		transformationsArrayList.add("BlankCharsAddition");
		transformationsArrayList.add("BlankCharsDeletion");
		transformationsArrayList.add("RandomCharsAddition");
		transformationsArrayList.add("RandomCharsDeletion");
		transformationsArrayList.add("RandomCharsModifier");
		transformationsArrayList.add("TokenAddition");
		transformationsArrayList.add("TokenDeletion");
		transformationsArrayList.add("TokenShuffle");
		transformationsArrayList.add("NameStyleAbbreviation");
		transformationsArrayList.add("CountryNameAbbreviation");
		transformationsArrayList.add("ChangeSynonym");
		transformationsArrayList.add("ChangeAntonym"); 
		transformationsArrayList.add("ChangeNumber"); 
		transformationsArrayList.add("ChangeDateFormat");
		transformationsArrayList.add("ChangeLanguage");
		transformationsArrayList.add("ChangeBooleanValue"); 
		transformationsArrayList.add("ChangeGenderFormat");
		transformationsArrayList.add("StemWord"); 
		
		// 19 - 22 are structure transformations
		transformationsArrayList.add("AddProperty"); 
		transformationsArrayList.add("DeleteProperty");
		transformationsArrayList.add("ExtractProperty");
		transformationsArrayList.add("AggregateProperties");
		
		//23 - 35 are semanticsAware transformations 
		//For instances
		transformationsArrayList.add("SameAs");
		transformationsArrayList.add("SameAsOnExistingInstances");
		transformationsArrayList.add("DifferentFrom");
		//For classes
		transformationsArrayList.add("SubClassOf");
		transformationsArrayList.add("EquivalentClass");
		transformationsArrayList.add("DisjointWith"); 
		transformationsArrayList.add("UnionOf");
		transformationsArrayList.add("IntersectionOf");
		//For properties
		transformationsArrayList.add("SubPropertyOf"); 
		transformationsArrayList.add("EquivalentProperty");
		transformationsArrayList.add("DisjointProperty"); 
		transformationsArrayList.add("FunctionalProperty"); 
		transformationsArrayList.add("InverseFunctionalProperty");
		transformationsArrayList.add("InverseOf");
	}

	@SuppressWarnings("static-access")
	public void WriteGSAsTriples(String value1,String value2, Double weight,String transformation,String property, Model GsSesameModel,FileOutputStream gsFos) {
		RDFFormat rdfFormat = SesameUtils.parseRdfFormat("turtle");
		//RDFFormat rdfFormat = SesameUtils.parseRdfFormat(TestDriver.getConfigurations().getString(Configurations.GENERATED_DATA_FORMAT));
		GsSesameModel = new LinkedHashModel();
		this.value1 = value1;
		this.value2 = value2;
		this.weight = weight;
		this.transformation = transformation;
		this.property = property;
		Resource subject = SesameBuilder.sesameValueFactory.createURI(this.value1);
		URI predicate = SesameBuilder.sesameValueFactory.createURI(exactmatch);
		Value object = SesameBuilder.sesameValueFactory.createURI(this.value2);
		GsSesameModel.add(subject,predicate,object); //value1 equals value2
		
		URI transf_u = SesameBuilder.sesameValueFactory.createURI("http://www.trans/"+ Long.toString(count)); //atomiclong
		Resource transf_r = SesameBuilder.sesameValueFactory.createURI("http://www.trans/"+ Long.toString(count)); //atomiclong
		URI numOfTrans = SesameBuilder.sesameValueFactory.createURI("http://www.transf_num");
		URI weight_ = SesameBuilder.sesameValueFactory.createURI("http://www.weight");
		URI type_ = SesameBuilder.sesameValueFactory.createURI("http://www.type");
		URI prop_ = SesameBuilder.sesameValueFactory.createURI("http://www.prop");

		Value transformation_ = SesameBuilder.sesameValueFactory.createLiteral(this.transformation);
		GsSesameModel.add(subject,numOfTrans,transf_u); // value1 has trans_num transf
		GsSesameModel.add(transf_r,type_,transformation_); //transf has transfrormation type a number from the map

		if(this.worker_.getFtransformations().containsKey(this.worker_.getSourceFileName())){
			ArrayList<Double> values = this.worker_.getFtransformations().get(this.worker_.getSourceFileName()); // first, copy out the existing values
			
			values.set(transformationsArrayList.indexOf(this.transformation) ,values.get(transformationsArrayList.indexOf(this.transformation))+ 1);
		    this.worker_.getFtransformations().remove(this.worker_.getSourceFileName());
		    this.worker_.getFtransformations().put(this.worker_.getSourceFileName(), values);
		}

		if(this.property  != null && !this.property.equals("")){
			if(property.startsWith("http")) {
				URI prop_name_u = SesameBuilder.sesameValueFactory.createURI(property); 
				GsSesameModel.add(transf_r,prop_, prop_name_u);
			}
			else {
				Value prop_name_v = SesameBuilder.sesameValueFactory.createLiteral(property); 
				GsSesameModel.add(transf_r,prop_, prop_name_v);
			}
		}
		Value weight_num = SesameBuilder.sesameValueFactory.createLiteral(this.weight.toString());
		GsSesameModel.add(transf_r,weight_,weight_num); // the above equality has weight weight_num
		try {
			Rio.write(GsSesameModel, gsFos, rdfFormat);
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
		count++;
	}


}

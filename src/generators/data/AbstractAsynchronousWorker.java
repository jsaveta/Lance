package generators.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFHandlerException;

import generators.data.sesamemodelbuilders.SesameBuilder;
import transformations.goldStandard.WriteIntermediateGS;
import properties.Configurations;
import transformations.Transformation;
import transformations.TransformationsCall;
import util.RandomUtil;
import util.StringUtil;
import util.TransformationsMeasurements;
import main.TestDriver;

public abstract class AbstractAsynchronousWorker extends Thread{
	
	protected static final String FILENAME_SOURCE_FORMAT = "%s%ssource-%04d.";
	protected static final String FILENAME_TARGET_FORMAT = "%s%starget-%04d.";
	protected static final String FILENAME_GS_FORMAT = "%s%sgoldStandard-%04d.";
	//Treeset for keeping the properties that are going to be added to the ontology.
	private static final TreeSet <String> extendOntologyProps = new TreeSet<String>();
		

	public static void addToextendOntologyProps(String p){
		extendOntologyProps.add(p);
	}
	
	public static TreeSet<String> getExtendOntologyProps(){
		return extendOntologyProps;
	}
	
	
	@Override
	public void run() {
		try {
			execute();
		} catch (Exception e) {
			System.out.println("Exception caught by : " + Thread.currentThread().getName() + " : " + e.getMessage());
		}
	}	
	/**
	 * This method will be called for execution of a concrete task
	 */
	public abstract void execute() throws Exception;
		
	public abstract String getSourceFileName();
	public abstract Model getSourceSesameModel();
	public abstract Model getTargetSesameModel();
	public abstract Model getGSSesameModel();
	public abstract ArrayList<Model> getTargetSesameModelArrayList();
	public abstract ArrayList<Model> getSourceSesameModelArrayList();
	public abstract HashMap<String, String> getURIMapping();
	public abstract FileOutputStream getGSFileOutputStream();
	public abstract Map <String, Transformation> getTransformationConfiguration();
	private static Map <String,ArrayList<Double>> Ftransformations = new HashMap <String,ArrayList<Double>>();
	private Map <String, Transformation> transformationConfigurations;
	private Map <String, List<Transformation>> complexTransformationConfigurations;
	private AbstractAsynchronousWorker worker;
	private WriteIntermediateGS writegs;
	
	public static Map <String,ArrayList<Double>> getFtransformations() {
		return Ftransformations;
	}

	public static void setFtransformations(Map <String,ArrayList<Double>> ftransformations) {
		Ftransformations = ftransformations;
	}
	
	public WriteIntermediateGS getGS(){
		return this.writegs;
	}
	
	
	public Model CreateTargetModel(AbstractAsynchronousWorker worker_, Model model, FileOutputStream fos) throws RDFHandlerException, IOException{
		Model gsSesameModel = model;
		FileOutputStream gsFos = fos;
		this.worker = worker_;
		TransformationsCall tr = new TransformationsCall(worker);
		writegs = new WriteIntermediateGS(worker);
		tr.setTransformationConfigurations();
		transformationConfigurations = tr.getTransformationConfigurations();
		complexTransformationConfigurations = tr.getComplexTransformationConfigurations();
		Model tempTargetSesameModel = new LinkedHashModel();
		RandomUtil ru = new RandomUtil();
		Resource subjectFromMap = null;
		/*loop for every triple in source sesamemodel in order to transform and create target sesamemodel*/
		Iterator<Statement> it = this.worker.getSourceSesameModel().iterator();
		boolean equivalent = false;
		boolean disjoint = false;
		
		boolean functional  = false;
		boolean inverseFunctional = false;
		while(it.hasNext())
		{

			Statement statement = it.next();
			try{
			/*Check if first triple of model*/
			if(!this.worker.getURIMapping().containsKey(statement.getSubject().stringValue())){
				/*Check if the user wants to change the uris*/
				if(TestDriver.getConfigurations().getBoolean(Configurations.CHANGE_URIS)){
					this.worker.getURIMapping().put(statement.getSubject().stringValue(), ru.randomUniqueURI());
					writegs.WriteGSAsTriples(statement.getSubject().stringValue(), this.worker.getURIMapping().get(statement.getSubject().stringValue()),0.0,"NotTransformed","URIchange",gsSesameModel,gsFos);
				}
				else{
					this.worker.getURIMapping().put(statement.getSubject().stringValue(), statement.getSubject().stringValue());
					writegs.WriteGSAsTriples(statement.getSubject().stringValue(), this.worker.getURIMapping().get(statement.getSubject().stringValue()),0.0,"NotTransformed","noURIchange",gsSesameModel,gsFos);
				}
				
				subjectFromMap = SesameBuilder.sesameValueFactory.createURI(this.worker.getURIMapping().get(statement.getSubject().stringValue()));	
				/*Semantics aware transformations for classes*/
				if(!transformationConfigurations.isEmpty() && transformationConfigurations.containsKey(statement.getObject().stringValue())){
					if(transformationConfigurations.get(statement.getObject().stringValue()).getClass().getPackage().getName().equals("transformations.semanticsAware")){
						Model tempModel = transformationConfigurations.get(statement.getObject().stringValue()).executeStatement(statement);
						if(StringUtil.getClassName(transformationConfigurations.get(statement.getObject().stringValue()).getClass().getName()).equals("SameAs") ||
								StringUtil.getClassName(transformationConfigurations.get(statement.getObject().stringValue()).getClass().getName()).equals("DifferentFrom") ||
								StringUtil.getClassName(transformationConfigurations.get(statement.getObject().stringValue()).getClass().getName()).equals("SameAsOnExistingInstances")){
							for (Statement st : tempModel){
								tempTargetSesameModel.add(st);
							}
						}
						else{
							if(!tempModel.isEmpty()){
								writegs.WriteGSAsTriples(statement.getSubject().stringValue(), this.worker.getURIMapping().get(statement.getSubject().stringValue()),0.0,StringUtil.getClassName(transformationConfigurations.get(statement.getObject().stringValue()).getClass().getName()), statement.getObject().stringValue(),gsSesameModel,gsFos);
								for (Statement st : tempModel){
									TransformationsMeasurements.semanticsSuccessRate(st,statement); //success rate
									
									tempTargetSesameModel.add(subjectFromMap, st.getPredicate(),st.getObject(), st.getContext());
								}
							}
							else{
								tempTargetSesameModel.add(subjectFromMap, statement.getPredicate(),statement.getObject(), statement.getContext());
							}
							if(StringUtil.getClassName(transformationConfigurations.get(statement.getObject().stringValue()).getClass().getName()).equals("EquivalentClass")){ equivalent = true;} 
							if(StringUtil.getClassName(transformationConfigurations.get(statement.getObject().stringValue()).getClass().getName()).equals("DisjointWith")){ disjoint = true;} 
						}
					}
				}
				else{
					tempTargetSesameModel.add(subjectFromMap,statement.getPredicate(),statement.getObject(), statement.getContext());
				}
			}	
			else{
				if(subjectFromMap == null){subjectFromMap = statement.getSubject();} 
				/*Value transformations*/
				if(!transformationConfigurations.isEmpty() && transformationConfigurations.containsKey(statement.getPredicate().stringValue())){
					//value and structure here
					if(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getPackage().getName().equals("transformations.value")){
						Object temp = transformationConfigurations.get(statement.getPredicate().stringValue()).execute(statement.getObject().stringValue());
						if(!(temp.toString().equals("")) && (temp.toString()!=null) && !(temp.toString().equals(statement.getObject().stringValue()))){
							tempTargetSesameModel.add(subjectFromMap, statement.getPredicate(),SesameBuilder.sesameValueFactory.createLiteral(temp.toString()),(Resource)statement.getContext());
							writegs.WriteGSAsTriples(statement.getSubject().stringValue(), this.worker.getURIMapping().get(statement.getSubject().stringValue()),0.0,StringUtil.getClassName(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getName()),statement.getPredicate().stringValue(),gsSesameModel,gsFos);
						}else {
							TransformationsMeasurements.valueFailure++;
							tempTargetSesameModel.add(subjectFromMap, statement.getPredicate(),statement.getObject(), statement.getContext());
						}

						TransformationsMeasurements.valueSuccessRate(statement.getObject().stringValue(), temp.toString()); //success rate
					} 
					/*Structure trasformations*/ 
					else if(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getPackage().getName().equals("transformations.structure")){
						Model tempModel = transformationConfigurations.get(statement.getPredicate().stringValue()).executeStatement(statement);
						if(!tempModel.isEmpty()){ 
							for (Statement tempStatement : tempModel){
								if(subjectFromMap!= null && (URI)tempStatement.getPredicate()!= null && (Value)tempStatement.getObject()!= null)
								tempTargetSesameModel.add(subjectFromMap, (URI)tempStatement.getPredicate(),(Value)tempStatement.getObject(),(Resource)tempStatement.getContext());		
							}	

							writegs.WriteGSAsTriples(statement.getSubject().stringValue(), this.worker.getURIMapping().get(statement.getSubject().stringValue()),0.0,StringUtil.getClassName(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getName()),statement.getPredicate().stringValue(),gsSesameModel,gsFos);
							if(StringUtil.getClassName(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getName()).equals("AggregateProperties")){
								if(it.hasNext()){
									statement = it.next();
								}
							}
							TransformationsMeasurements.structureSuccess++;
						}
						else if(StringUtil.getClassName(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getName()).equals("DeleteProperty")){
							writegs.WriteGSAsTriples(statement.getSubject().stringValue(), this.worker.getURIMapping().get(statement.getSubject().stringValue()),0.0,StringUtil.getClassName(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getName()),statement.getPredicate().stringValue(),gsSesameModel,gsFos);
							TransformationsMeasurements.structureSuccess++;
						}
						else{
							TransformationsMeasurements.structureFailure++;
						}
						
					}
					/*Semantics aware transformations for properties*/
					else if(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getPackage().getName().equals("transformations.semanticsAware")){
						Model tempModel = new LinkedHashModel();
						if(StringUtil.getClassName(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getName()).equals("DisjointProperty")){
							if(!equivalent){ tempModel = transformationConfigurations.get(statement.getPredicate().stringValue()).executeStatement(statement); }
							
						}
						else if(StringUtil.getClassName(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getName()).equals("EquivalentProperty")){
							 if(!disjoint){ tempModel = transformationConfigurations.get(statement.getPredicate().stringValue()).executeStatement(statement);}
							
						}
						else{tempModel = transformationConfigurations.get(statement.getPredicate().stringValue()).executeStatement(statement);}
						
						if(!tempModel.isEmpty()){ 
							 if(StringUtil.getClassName(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getName()).equals("FunctionalProperty")){
								 functional  = true;
							 }
							 if(StringUtil.getClassName(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getName()).equals("InverseFunctionalProperty")){
								 inverseFunctional = true;
							 }
							for (Statement tempStatement : tempModel){
								TransformationsMeasurements.semanticsSuccessRate(tempStatement,statement); //success rate
								
								tempTargetSesameModel.add(subjectFromMap,(URI)tempStatement.getPredicate(),(Value)tempStatement.getObject(),(Resource)tempStatement.getContext());
								writegs.WriteGSAsTriples(statement.getSubject().stringValue(), this.worker.getURIMapping().get(statement.getSubject().stringValue()),0.0,StringUtil.getClassName(transformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getName()),statement.getPredicate().stringValue(),gsSesameModel,gsFos);
							}
						}
						else{
							TransformationsMeasurements.semanticsFailure++; //success rate
							tempTargetSesameModel.add(subjectFromMap,statement.getPredicate(),statement.getObject(),statement.getContext());
						}
					}
				}
				/*Complex transformations*/ 
				else if (complexTransformationConfigurations.containsKey(statement.getPredicate().stringValue())){
					Model complexModel = new LinkedHashModel();
					String valueTransformation = null;
					List<Transformation> value = complexTransformationConfigurations.get(statement.getPredicate().stringValue());//entry.getValue();
					complexModel = new LinkedHashModel();
					for (int e = 0; e < value.size(); e++) {
						Transformation transformation = (Transformation) value.get(e);
						if(transformation.toString().startsWith("transformations.value")){
							Object temp = transformation.execute(statement.getObject().stringValue());
							if(!(temp.toString().equals("")) && (temp!=null) && !(temp.toString().equals(statement.getObject().stringValue()))){
								complexModel.add(subjectFromMap, statement.getPredicate(),SesameBuilder.sesameValueFactory.createLiteral(temp.toString()), statement.getContext());
								valueTransformation = transformation.getClass().getName().toString().replace("transformations.value.", "");
							}
						}
						else if(transformation.toString().startsWith("transformations.semanticsAware")){
							Model temp = new LinkedHashModel();
							if(!complexModel.isEmpty()){
								for (Statement st : complexModel){
									temp = transformation.executeStatement(st);
									if(!temp.isEmpty()){
										
										 if(transformation.toString().contains("InverseFunctionalProperty")){
											 inverseFunctional = true;
										 }
										 else if(transformation.toString().contains("FunctionalProperty")){
											 functional  = true;
										 }
										for (Statement st1 : temp){
											if(!st1.toString().equals(st.toString())){
												if(valueTransformation != null){
													writegs.WriteGSAsTriples(statement.getSubject().stringValue(),subjectFromMap.stringValue(),0.0,valueTransformation,statement.getPredicate().stringValue(),gsSesameModel,gsFos);
												}
												String semanticsAwareTransformation = transformation.getClass().getName().toString().replace("transformations.semanticsAware.", "");
												writegs.WriteGSAsTriples(statement.getSubject().stringValue(),subjectFromMap.stringValue(),0.0,semanticsAwareTransformation,statement.getPredicate().stringValue(),gsSesameModel,gsFos);
											}
											complexModel = new LinkedHashModel();
											TransformationsMeasurements.complexSuccessRate(statement, st1); //success rate
											complexModel.add(subjectFromMap,st1.getPredicate(),st1.getObject(), st1.getContext());
										} 
									}
								} 
							}else{
								temp = transformation.executeStatement(statement);
								if(!temp.isEmpty()){
									complexModel = new LinkedHashModel();
									for (Statement st1 : temp){
										TransformationsMeasurements.semanticsSuccessRate(statement, st1); //success rate
										complexModel.add(subjectFromMap,st1.getPredicate(),st1.getObject(),st1.getContext());
									} 
								}
								else{
									TransformationsMeasurements.complexFailure++;
								}
							} 
						}
						else if(transformation.toString().startsWith("transformations.structure")){
						Model temp = new LinkedHashModel();	
						if(!complexModel.isEmpty()){
							for (Statement st : complexModel){
								temp = transformation.executeStatement(st);
								complexModel = new LinkedHashModel();
								if(!temp.isEmpty()){
									for (Statement st1 : temp){
										complexModel.add(subjectFromMap,st1.getPredicate(),st1.getObject(),st1.getContext());
										TransformationsMeasurements.complexSuccessRate(statement, st1); //success rate
									} 

									if(valueTransformation != null){
										writegs.WriteGSAsTriples(statement.getSubject().stringValue(),subjectFromMap.stringValue(),0.0,valueTransformation,statement.getPredicate().stringValue(),gsSesameModel,gsFos);
									}
									String structureTransformation = transformation.getClass().getName().toString().replace("transformations.structure.", "");
									writegs.WriteGSAsTriples(statement.getSubject().stringValue(),subjectFromMap.stringValue(),0.0,structureTransformation,statement.getPredicate().stringValue(),gsSesameModel,gsFos);
									if(StringUtil.getClassName(complexTransformationConfigurations.get(statement.getPredicate().stringValue()).getClass().getName()).equals("AggregateProperties")){
										if(it.hasNext()){
											statement = it.next();
										}
									}
								}
							}
						}
						else{
							if(!temp.isEmpty()){
								complexModel = new LinkedHashModel();
								for (Statement st1 : temp){
									complexModel.add(subjectFromMap,st1.getPredicate(),st1.getObject(), st1.getContext());
								} 
							}
							else{
								TransformationsMeasurements.complexFailure++;
							}
						} 
					}
				}
				for (Statement st : complexModel){
					tempTargetSesameModel.add(st);
				}
				}
				/*no changes in triple*/
				else{
					tempTargetSesameModel.add(subjectFromMap,statement.getPredicate(), statement.getObject(),(Resource)statement.getContext());
				}
			}
			gsSesameModel = new LinkedHashModel();
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
			}
		}
		getSourceSesameModelArrayList().add(this.worker.getSourceSesameModel()); //add every instance from the source sesame model in an arraylist as models	
		getTargetSesameModelArrayList().add(tempTargetSesameModel); //add every instance from the target sesame model in an arraylist as models
		
		
		//check if there was a functional or inversefunctional property in order to transform or not the uris! 
		Iterator<Statement> iter = tempTargetSesameModel.iterator();
		Model modelForFunctionals = new LinkedHashModel();
		if(functional){ //do not change uri
			while(iter.hasNext())
			{
				Statement st = iter.next();
				String notTransformedURI = RandomUtil.getKey(this.worker.getURIMapping(),subjectFromMap.toString());
				if(notTransformedURI!=null){
					modelForFunctionals.add(SesameBuilder.sesameValueFactory.createURI(notTransformedURI), st.getPredicate(), st.getObject(), st.getContext());
				}
			}
		}
		else if (inverseFunctional){ //change uri 
			while(iter.hasNext())
			{	
				Statement st = iter.next();
				if(subjectFromMap.toString().equals(st.getSubject().toString())){
					this.worker.getURIMapping().put(st.getSubject().stringValue(), ru.randomUniqueURI());
					modelForFunctionals.add(SesameBuilder.sesameValueFactory.createURI(this.worker.getURIMapping().get(st.getSubject().stringValue())), st.getPredicate(), st.getObject(), st.getContext());
				}
			}
		}
		if(!modelForFunctionals.isEmpty()){return modelForFunctionals;}
		return tempTargetSesameModel;
	}
	
	
}
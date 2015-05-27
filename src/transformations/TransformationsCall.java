package transformations;

import generators.data.AbstractAsynchronousWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import main.TestDriver;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import properties.Configurations;
import properties.Definitions;
import util.StructureUtil;
import util.TransformationsMeasurements;
import data.LoadOntologies;
/**
 * We map every transformation with a unique number for reasons of space on the GoldStandard file.
 *
 */
public class TransformationsCall {
	public static Map <String, String> transformationsMap;
	private Map <String, Transformation> predicatesObjectsMap;
	private Map<String, List<Transformation>> complexPredicatesObjectsMap;
	private static TreeSet <String> stringLiteralCollection;
	private static TreeSet <String> numericLiteralCollection;
	private static TreeSet <String> booleanLiteralCollection;
	private static TreeSet <String> dateLiteralCollection;
	private static TreeSet <String> geoLatCollection;
	private static TreeSet <String> geoLongCollection;
	private static TreeSet <String> datatypePropertiesCollection;
	private static TreeSet <String> objectPropertiesCollection;
	private static TreeSet <String> rdfPropertiesCollection;
	private static TreeSet <String> classesCollection;
	

	private static TreeSet <String> valueTree;
	private static TreeSet <String> structureTree;
	private static TreeSet <String> semanticsAwareTree;
	private static TreeSet <String> classSemanticsAwareTree;
	private static TreeSet <String> complexValueStructureTree;
	private static TreeSet <String> complexValueSemanticsTree;
	
	public static Map<String, Collection<String>> subClassesOfMap;
	public static Map<String, Collection<String>> equivalentClassMap;
	public static Map<String, Collection<String>> disjointWithMap;
	public static Map<String, Collection<String>> unionOfMap;
	public static Map<String, Collection<String>> intersectionOfMap;
	public static Map<String, Collection<String>> equivalentPropertyMap;
	public static Map<String, Collection<String>> disjointPropertyMap;
	public static Map<String, Collection<String>> subPropertyOfMap;
	public static Map<String, Collection<String>> inverseFunctionalPropertyMap;
	public static TreeSet<String> functionalProperty;
	
	private Tranformation transfPerc = Tranformation.VALUE;
	private Value valPerc = Value.BLANKCHARSADDITION;
	private Structure structPerc = Structure.ADDPROPERTY;
	private SemanticsAware semPerc = SemanticsAware.SAMEAS;
	private ComplexSemanticsAware semCompPerc = ComplexSemanticsAware.EQUIVALENTPROPERTY;
	private SimpleCombination simplePerc = SimpleCombination.VALUE;
	private ComplexCombination compvalPerc = ComplexCombination.VALUE_STRUCTURE;

	private AbstractAsynchronousWorker worker;
	private LoadOntologies loadOntologies;
	private final Configurations configuration = new Configurations();
	
	public TransformationsCall() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		
		TransformationsMeasurements timer = new TransformationsMeasurements();
		timer.start(); //measure retrieving needed schema info time, start
		
		predicatesObjectsMap = new HashMap<String, Transformation>(); 
		complexPredicatesObjectsMap = new HashMap<String, List<Transformation>>();
		stringLiteralCollection = new TreeSet<String>();
		numericLiteralCollection = new TreeSet<String>();
		booleanLiteralCollection = new TreeSet<String>();
		dateLiteralCollection = new TreeSet<String>();
		geoLatCollection = new TreeSet<String>();
		geoLongCollection = new TreeSet<String>();
		datatypePropertiesCollection = new TreeSet<String>();
		objectPropertiesCollection = new TreeSet<String>();
		rdfPropertiesCollection = new TreeSet<String>();
		classesCollection = new TreeSet<String>();

		subClassesOfMap = new HashMap<String,Collection<String>>();
		equivalentClassMap = new HashMap<String,Collection<String>>();
		disjointWithMap = new HashMap<String,Collection<String>>();
		unionOfMap = new HashMap<String,Collection<String>>();
		intersectionOfMap = new HashMap<String,Collection<String>>();
		equivalentPropertyMap = new HashMap<String,Collection<String>>();
		disjointPropertyMap = new HashMap<String,Collection<String>>();
		subPropertyOfMap = new HashMap<String,Collection<String>>();
		functionalProperty = new TreeSet<String>();
		inverseFunctionalPropertyMap = new HashMap<String,Collection<String>>();
		
		valueTree = new TreeSet<String>();
		structureTree = new TreeSet<String>();
		semanticsAwareTree = new TreeSet<String>();
		classSemanticsAwareTree = new TreeSet<String>(); 
		complexValueStructureTree = new TreeSet<String>();
		complexValueSemanticsTree = new TreeSet<String>();
		
        try {
			loadOntologies = new LoadOntologies(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        setClassesCollection(); //need to be first!
        setDatatypePropertiesCollection();
		setObjectPropertiesCollection();
		setRdfPropertiesCollection();
        setStringLiteralCollection();
        setBooleanLiteralCollection();
        setDateLiteralCollection();
        setGeoLatCollection();
        setGeoLongCollection();
        setNumericLiteralCollection();
		setValueCollection();
		setStructureCollection();
		setClassSemanticsAwareCollection();
		setSemanticsAwareCollection();
		setComplexValueStructureTransformationsList();
		setComplexValueSemanticsTransformationsList();
		
		if(Definitions.transformationAllocation.getAllocationsArray().get(2) != 0.0 || Definitions.transformationAllocation.getAllocationsArray().get(3) != 0.0 || Definitions.transformationAllocation.getAllocationsArray().get(4) != 0.0){
		/*classes*/
		for(String c : getClassesCollection()){
			/*subclassof map*/
			if( Definitions.semanticsAwareAllocation.getAllocationsArray().get(3) != 0.0){//System.out.println("subclassof");
				Collection<String>  subClassesOf = loadOntologies.getSuperClasses(c,TestDriver.getConfigurations().getBoolean(Configurations.INFERENSE_SUBCLASS_SUBPROPERTY));
				if(!subClassesOf.isEmpty()) subClassesOfMap.put(c, subClassesOf);
			}
			
			/*equivalentclass map*/
			if( Definitions.semanticsAwareAllocation.getAllocationsArray().get(4) != 0.0){//System.out.println("equivalentclass");
				Collection<String>  equivalentClass = loadOntologies.getEquivalentClasses(c);
				if(!equivalentClass.isEmpty()) equivalentClassMap.put(c, equivalentClass);
			}
			
			/*disjointwith map*/
			if( Definitions.semanticsAwareAllocation.getAllocationsArray().get(5) != 0.0){//System.out.println("disjointwith");
				Collection<String>  disjointWith = loadOntologies.getDisjointClasses(c); 
				if(!disjointWith.isEmpty()) disjointWithMap.put(c, disjointWith);
			}
			
			/*unionOf map*/
			if( Definitions.semanticsAwareAllocation.getAllocationsArray().get(6) != 0.0){//System.out.println("unionOf");
				Collection<String> unionOf = null;
				try {
					unionOf = loadOntologies.getUnionOf(c);
				} catch (RepositoryException | MalformedQueryException	| QueryEvaluationException e) {
					e.printStackTrace();
				}
				if(!unionOf.isEmpty()) unionOfMap.put(c, unionOf);
			}
			
			/*intersectionOf map*/
			if( Definitions.semanticsAwareAllocation.getAllocationsArray().get(7) != 0.0){//System.out.println("intersectionOf");
				Collection<String> intersectionOf = null;
				try {
					intersectionOf = loadOntologies.getIntersectionOf(c);
				} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
					e.printStackTrace();
				} 
				if(!intersectionOf.isEmpty()) intersectionOfMap.put(c, intersectionOf);
			}
		}
			
		/*properties*/
		ArrayList<String> classPropertiesArrayList = new ArrayList<String>();
		System.out.println("is ClassesCollection empty?  " + getClassesCollection().isEmpty());
		for(String c : getClassesCollection()){
			try {
				classPropertiesArrayList.addAll(loadOntologies.getClassProperties(c));
			} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) { e.printStackTrace();} 
		}
			for(String p : classPropertiesArrayList){
				/*subPropertyOf map*/
				if( Definitions.semanticsAwareAllocation.getAllocationsArray().get(8) != 0.0){//System.out.println("subPropertyOf");
					Collection<String> subPropertyOf = new HashSet<>();
					try { 
						subPropertyOf = loadOntologies.getSuperProperties(p,TestDriver.getConfigurations().getBoolean(Configurations.INFERENSE_SUBCLASS_SUBPROPERTY));
					} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
						e.printStackTrace();
					}
					if(!subPropertyOfMap.containsKey(p) && !subPropertyOf.isEmpty()) subPropertyOfMap.put(p, subPropertyOf);
				}
				
				/*equivalentProperty map*/
				if( Definitions.semanticsAwareAllocation.getAllocationsArray().get(9) != 0.0){//System.out.println("equivalentProperty");
					Collection<String> equivalentProperty = new HashSet<>();
					try {
						equivalentProperty = loadOntologies.getEquivalentProperties(p);
					} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
						e.printStackTrace();
					}
					if(!equivalentPropertyMap.containsKey(p)  && !equivalentProperty.isEmpty()) equivalentPropertyMap.put(p, equivalentProperty);
				}
				
				/*disjointProperty map*/
				if( Definitions.semanticsAwareAllocation.getAllocationsArray().get(10) != 0.0){//System.out.println("disjointProperty");
					Collection<String>  disjointProperty = new HashSet<>();
					try {
						disjointProperty = loadOntologies.getDisjointProperties(p);
						disjointProperty.addAll(loadOntologies.getAllDisjointProperties(p));
					} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {	e.printStackTrace();}
					if(!disjointPropertyMap.containsKey(p)  && !disjointProperty.isEmpty()) disjointPropertyMap.put(p, disjointProperty);
				}
				
				
				/*functionalProperty*/
				if( Definitions.semanticsAwareAllocation.getAllocationsArray().get(11) != 0.0){//System.out.println("functionalProperty");
					functionalProperty.addAll(loadOntologies.getFunctionalProperties());
				}
				
				/*inverseFunctionalProperty map*/
				if( Definitions.semanticsAwareAllocation.getAllocationsArray().get(12) != 0.0){//System.out.println("inverseFunctionalProperty");
					Collection<String> inverseFunctionalProperty = new HashSet<>();
					try {
						inverseFunctionalProperty = loadOntologies.getInverseFunctionalProperties(p);
					} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
						e.printStackTrace();
					}
					if(!inverseFunctionalPropertyMap.containsKey(p)  && !inverseFunctionalProperty.isEmpty()) inverseFunctionalPropertyMap.put(p, inverseFunctionalProperty);
				}
			  }
			}
		
		System.out.println("I have initialized everything");
		timer.stop(); //measure retrieving needed schema info time, stop
		timer.setRetrievingInfoTime(timer.getDuration());
	}
	
	public TransformationsCall(AbstractAsynchronousWorker worker_){
		this.worker = worker_;
		predicatesObjectsMap = new HashMap<String, Transformation>(); 
		complexPredicatesObjectsMap = new HashMap<String, List<Transformation>>();
	}
	
	
	private static enum Tranformation { 
		VALUE , STRUCTURE ,SEMANTICS_AWARE , SIMPLECOMBINATION ,COMPLEXCOMBINATION, NOTRANSFORMATION 
	}
	
	private static enum Value {  
		 BLANKCHARSADDITION , BLANKCHARSDELETION ,RANDOMCHARSADDITION , RANDOMCHARSDELETION , RANDOMCHARSMODIFIER, TOKENADDITION, 
		 TOKENDELETION, TOKENSHUFFLE, NAMESTYLEABBREVIATION, COUNTRYNAMEABBREVIATION, CHANGESYNONYM, CHANGEANTONYM, CHANGENUMBER, 
		 CHANGEDATEFORMAT, CHANGELANGUAGE, CHANGEBOOLEAN, CHANGEGENDERFORMAT, STEMWORD, CHANGEPOINT, NOTRANSFORMATION
	}
	
	private static enum Structure { 
		ADDPROPERTY, DELETEPROPERTY, EXTRACTPROPERTY, AGGREGATEPROPERTY, NOTRANSFORMATION
	}
	
	private static enum SemanticsAware { 
		SAMEAS, SAMEASONEXISTINGINSTANCE, DIFFERENTFROM, SUBCLASSOF, EQUIVALENTCLASS, DISJOINTWITH, UNIONOF, 
		INTERSECTIONOF, SUBPROPERTYOF, EQUIVALENTPROPERTY, DISJOINTPROPERTY, FUNCTIONALPROPERTY, INVERSEFUNCTIONALPROPERTY,
	    NOTRANSFORMATION
	}
	private static enum SimpleCombination {
		VALUE , STRUCTURE ,SEMANTICS_AWARE , NOTRANSFORMATION 
	}
	
	private static enum ComplexCombination {
		VALUE_STRUCTURE, VALUE_SEMANTICS_AWARE, NOTRANSFORMATION 
	}

	private static enum ComplexSemanticsAware { 
		SUBPROPERTYOF, EQUIVALENTPROPERTY, DISJOINTPROPERTY, FUNCTIONALPROPERTY, INVERSEFUNCTIONALPROPERTY,
	    NOTRANSFORMATION

	}
	
	private void initializeTransformationsEntity() {
		try {			
			switch (Definitions.transformationAllocation.getAllocation()) {
				case 0 : //System.out.println("VALUE");
					this.transfPerc = Tranformation.VALUE;
					break;
				case 1 : //System.out.println("STRUCTURE");
					this.transfPerc = Tranformation.STRUCTURE;
					break;
				case 2 : //System.out.println("SEMANTICS_AWARE");
					this.transfPerc = Tranformation.SEMANTICS_AWARE;
					break;	
				case 3 : //System.out.println("SIMPLECOMBINATION");
					this.transfPerc = Tranformation.SIMPLECOMBINATION;
					break;
				case 4 : //System.out.println("COMPLEXCOMBINATION");
					this.transfPerc = Tranformation.COMPLEXCOMBINATION;
					break;
				case 5 : //System.out.println("NOTRANSFORMATION");
					this.transfPerc = Tranformation.NOTRANSFORMATION;
					break;
			}
		} catch (IllegalArgumentException iae) {
				System.err.println("Check transformation percentages");
		}
	}
	
	private void initializeValueEntity() {
		try {			

			switch (Definitions.valueAllocation.getAllocation()) {
				case 0 :
					this.valPerc = Value.BLANKCHARSADDITION;
					break;
				case 1 :
					this.valPerc = Value.BLANKCHARSDELETION;
					break;
				case 2 :
					this.valPerc = Value.RANDOMCHARSADDITION;
					break;	
				case 3 :
					this.valPerc = Value.RANDOMCHARSDELETION;
					break;
				case 4 :
					this.valPerc = Value.RANDOMCHARSMODIFIER;
					break;
				case 5 :
					this.valPerc = Value.TOKENADDITION;
					break;
				case 6 :
					this.valPerc = Value.TOKENDELETION;
					break;
				case 7 :
					this.valPerc = Value.TOKENSHUFFLE;
					break;
				case 8 :
					this.valPerc = Value.NAMESTYLEABBREVIATION;
					break;
				case 9 :
					this.valPerc = Value.COUNTRYNAMEABBREVIATION;
					break;
				case 10 :
					this.valPerc = Value.CHANGESYNONYM;
					break;
				case 11 :
					this.valPerc = Value.CHANGEANTONYM;
					break;
				case 12 :
					this.valPerc = Value.CHANGENUMBER;
					break;
				case 13 :
					this.valPerc = Value.CHANGEDATEFORMAT;
					break;
				case 14 :
					this.valPerc = Value.CHANGELANGUAGE;
					break;
				case 15 :
					this.valPerc = Value.CHANGEBOOLEAN;
					break;
				case 16 :
					this.valPerc = Value.CHANGEGENDERFORMAT;
					break;
				case 17 :
					this.valPerc = Value.STEMWORD;
					break;
				case 18 :
					this.valPerc = Value.CHANGEPOINT;
					break;
				case 19 :
					this.valPerc = Value.NOTRANSFORMATION;
					break;	
			}

			
		} catch (IllegalArgumentException iae) {
				System.err.println("Check value transformation percentages");
		}
	}
	private void initializeStructureEntity() {
		try {			
			switch (Definitions.structureAllocation.getAllocation()) {
				case 0 :
					this.structPerc = Structure.ADDPROPERTY;
					break;
				case 1 :
					this.structPerc = Structure.DELETEPROPERTY;
					break;
				case 2 :
					this.structPerc = Structure.EXTRACTPROPERTY;
					break;	
				case 3 :
					this.structPerc = Structure.AGGREGATEPROPERTY;
					break;
				case 4 :
					this.structPerc = Structure.NOTRANSFORMATION;
					break;
				
			}

			
		} catch (IllegalArgumentException iae) {
				System.err.println("Check structure transformation percentages");
		}
	}
	private void initializeSemanticsAwareEntity() {
		try {	
			switch (Definitions.semanticsAwareAllocation.getAllocation()) {
				case 0 :
					this.semPerc = SemanticsAware.SAMEAS;
					break;
				case 1 :
					this.semPerc = SemanticsAware.SAMEASONEXISTINGINSTANCE;
					break;
				case 2 :
					this.semPerc = SemanticsAware.DIFFERENTFROM;
					break;	
				case 3 :
					this.semPerc = SemanticsAware.SUBCLASSOF;
					break;
				case 4 :
					this.semPerc = SemanticsAware.EQUIVALENTCLASS;
					break;
				case 5 :
					this.semPerc = SemanticsAware.DISJOINTWITH;
					break;
				case 6 :
					this.semPerc = SemanticsAware.UNIONOF;
					break;
				case 7 :
					this.semPerc = SemanticsAware.INTERSECTIONOF;
					break;
				case 8 :
					this.semPerc = SemanticsAware.SUBPROPERTYOF;
					break;
				case 9 :
					this.semPerc = SemanticsAware.EQUIVALENTPROPERTY;
					break;
				case 10 :
					this.semPerc = SemanticsAware.DISJOINTPROPERTY;
					break;
				case 11 :
					this.semPerc = SemanticsAware.FUNCTIONALPROPERTY;
					break;
				case 12 :
					this.semPerc = SemanticsAware.INVERSEFUNCTIONALPROPERTY;
					break;
				case 13 :
					this.semPerc = SemanticsAware.NOTRANSFORMATION;
					break;
					//add here every time you implement + on configuration file + cases below 
			}
		} catch (IllegalArgumentException iae) {
				System.err.println("Check semanticsAware transformation percentages");
		}
	}
	
	private void initializeComplexSemanticsAwareEntity() {
		try {	
			switch (Definitions.semanticsAwareAllocation.getAllocation()) {
				case 0 :
					this.semCompPerc = ComplexSemanticsAware.SUBPROPERTYOF;
					break;
				case 1 :
					this.semCompPerc = ComplexSemanticsAware.EQUIVALENTPROPERTY;
					break;
				case 2 :
					this.semCompPerc = ComplexSemanticsAware.DISJOINTPROPERTY;
					break;
				case 3 :
					this.semCompPerc = ComplexSemanticsAware.FUNCTIONALPROPERTY;
					break;
				case 4 :
					this.semCompPerc = ComplexSemanticsAware.INVERSEFUNCTIONALPROPERTY;
					break;
				case 5 :
					this.semCompPerc = ComplexSemanticsAware.NOTRANSFORMATION;
					break;
			}
		} catch (IllegalArgumentException iae) {
				System.err.println("Check semanticsAware transformation percentages");
		}
	}
	
	private void initializeSimpleCombinationEntity() {
		try {			
			
			switch (Definitions.simpleCombinationAllocation.getAllocation()) {
				case 0 :
					this.simplePerc = SimpleCombination.VALUE;
					break;
				case 1 :
					this.simplePerc = SimpleCombination.STRUCTURE;
					break;
				case 2 :
					this.simplePerc = SimpleCombination.SEMANTICS_AWARE;
					break;	
				case 3 :
					this.simplePerc = SimpleCombination.NOTRANSFORMATION;
					break;
			}

			
		} catch (IllegalArgumentException iae) {
				System.err.println("Check simple combination percentages");
		}
	}
	
	private void initializeComplexCombinationEntity() {
		try {			
			
			switch (Definitions.complexCombinationAllocation.getAllocation()) {
				case 0 :
					this.compvalPerc = ComplexCombination.VALUE_STRUCTURE;
					break;
				case 1 :
					this.compvalPerc = ComplexCombination.VALUE_SEMANTICS_AWARE;
					break;
				case 2 :
					this.compvalPerc = ComplexCombination.NOTRANSFORMATION;
					break;
			}

			
		} catch (IllegalArgumentException iae) {
				System.err.println("Check simple combination percentages");
		}
	}
	

	/*setters*/
	public void setStringLiteralCollection(){
		for (String c : getClassesCollection()){
			try {
				stringLiteralCollection.addAll(loadOntologies.getPredicatesOfStringObjects(c));
			} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setNumericLiteralCollection(){
		numericLiteralCollection.addAll(loadOntologies.getPredicatesOfNumericObjects());
	}
	
	public void setBooleanLiteralCollection(){
		for (String c : getClassesCollection()){
			try {
				booleanLiteralCollection.addAll(loadOntologies.getPredicatesOfBooleanObjects(c));
			} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setDateLiteralCollection(){
		for (String c : getClassesCollection()){
			try {
				dateLiteralCollection.addAll(loadOntologies.getPredicatesOfDateObjects(c));
			} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setGeoLatCollection(){
		geoLatCollection.addAll(loadOntologies.getPredicatesOfGeoLatObjects());
		
	}
	
	public void setGeoLongCollection(){
		geoLongCollection.addAll(loadOntologies.getPredicatesOfGeoLongObjects());
		
	}
	
	public void setDatatypePropertiesCollection() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		datatypePropertiesCollection.addAll(loadOntologies.getAllRdfDatatypeProperties());
		datatypePropertiesCollection.addAll(loadOntologies.getAllDatatypeProperties());
		datatypePropertiesCollection.remove("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	}
	
	public void setObjectPropertiesCollection() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		objectPropertiesCollection.addAll(loadOntologies.getAllRdfObjectProperties());
		objectPropertiesCollection.addAll(loadOntologies.getAllObjectProperties());
		objectPropertiesCollection.remove("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	}
	
	public void setRdfPropertiesCollection(){
		ArrayList<String> classes = new ArrayList<String>();
		classes.addAll(TestDriver.getConfigurations().getArray(Configurations.TRANSFORM_CLASS_INSTANCES));
		if(classes.isEmpty()) classes.addAll(getClassesCollection());
		for (String c : classes){
			try {
				rdfPropertiesCollection.addAll(loadOntologies.getClassProperties(c));
				rdfPropertiesCollection.remove("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
			} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setClassesCollection(){
		if(!TestDriver.getConfigurations().getArray(Configurations.TRANSFORM_CLASS_INSTANCES).isEmpty()){
			ArrayList<String> classes = new ArrayList<String>();
			classes.addAll(TestDriver.getConfigurations().getArray(Configurations.TRANSFORM_CLASS_INSTANCES));
			for (String c : classes){
				classesCollection.add(c);
			}
		}
		else{
			classesCollection.addAll(loadOntologies.getAllClasses());
		}
	}
	
	@SuppressWarnings("static-access")
	public void setValueCollection(){
		this.valueTree.addAll(getStringLiteralCollection());
		this.valueTree.addAll(getNumericLiteralCollection());
		this.valueTree.addAll(getBooleanLiteralCollection());
		this.valueTree.addAll(getDateLiteralCollection());
		this.valueTree.addAll(getGeoLatCollection());
		this.valueTree.addAll(getGeoLongCollection());
	}

	@SuppressWarnings("static-access")
	public void setStructureCollection(){
		this.structureTree.addAll(getRdfPropertiesCollection());
		this.structureTree.addAll(getDatatypePropertiesCollection());
		this.structureTree.addAll(getObjectPropertiesCollection());
	}
	
	@SuppressWarnings("static-access")
	public void setSemanticsAwareCollection(){
		this.semanticsAwareTree.addAll(getClassesCollection());
		this.semanticsAwareTree.addAll(getRdfPropertiesCollection());
		this.semanticsAwareTree.addAll(getDatatypePropertiesCollection());
		this.semanticsAwareTree.addAll(getObjectPropertiesCollection());
	}
	
	@SuppressWarnings("static-access")
	public void setClassSemanticsAwareCollection(){
		this.classSemanticsAwareTree.addAll(getClassesCollection());
	}

	@SuppressWarnings("static-access")
	public void setComplexValueStructureTransformationsList(){
		this.complexValueStructureTree = StructureUtil.intersection(this.valueTree, this.structureTree); 

	}
	
	@SuppressWarnings("static-access")
	public void setComplexValueSemanticsTransformationsList(){
		this.complexValueSemanticsTree =  StructureUtil.intersection(this.valueTree, this.semanticsAwareTree);
	}
	
	/*getters*/
	@SuppressWarnings("static-access")
	public Collection<String> getStringLiteralCollection(){
		return this.stringLiteralCollection;
	}
	@SuppressWarnings("static-access")
	public Collection<String> getNumericLiteralCollection(){
		return this.numericLiteralCollection;
	}
	@SuppressWarnings("static-access")
	public Collection<String> getBooleanLiteralCollection(){
		return this.booleanLiteralCollection;
	}
	@SuppressWarnings("static-access")
	public Collection<String> getDateLiteralCollection(){
		return this.dateLiteralCollection;
	}
	@SuppressWarnings("static-access")
	public Collection<String> getGeoLatCollection(){
		return this.geoLatCollection;
	}
	@SuppressWarnings("static-access")
	public Collection<String> getGeoLongCollection(){
		return this.geoLongCollection;
	}
	@SuppressWarnings("static-access")
	public TreeSet<String> getDatatypePropertiesCollection(){
		return this.datatypePropertiesCollection; 
	}
	@SuppressWarnings("static-access")
	public TreeSet<String> getObjectPropertiesCollection(){
		return this.objectPropertiesCollection; 
	}
	@SuppressWarnings("static-access")
	public TreeSet<String> getRdfPropertiesCollection(){
		return this.rdfPropertiesCollection; 
	}
	@SuppressWarnings("static-access")
	public TreeSet<String> getClassesCollection(){
		return this.classesCollection; 
	}
	@SuppressWarnings("static-access")
	public TreeSet<String> getSemanticsAwareCollection(){
		return this.semanticsAwareTree; 
	}
	@SuppressWarnings("static-access")
	public TreeSet<String> getClassSemanticsAwareCollection(){
		return this.classSemanticsAwareTree;
	}
	@SuppressWarnings("static-access")
	public TreeSet<String> getValueCollection(){
		return this.valueTree; 
	}
	@SuppressWarnings("static-access")
	public TreeSet<String> getStructureCollection(){
		return this.structureTree; 
	}
	@SuppressWarnings("static-access")
	public TreeSet<String> getComplexValueStructureTree(){
		return this.complexValueStructureTree; 
	}
	@SuppressWarnings("static-access")
	public TreeSet<String> getComplexValueSemanticsTree(){
		return this.complexValueSemanticsTree; 
	}
	
	
	public Map <String, Transformation> valueCases(TreeSet<String> treeSet){
		ArrayList<String> value = new ArrayList<String>(treeSet);
		//this.valueArrayList = valueArrayList;
		Map <String, Transformation> predicatesObjectsMap = new HashMap<String, Transformation>();
		
		for(int i = 0; i < value.size(); i++){
			initializeValueEntity();	
			switch (valPerc) {
			
				case BLANKCHARSADDITION : //System.out.println("BLANKCHARSADDITION");
					if(getStringLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.addRANDOMBLANKS(TestDriver.getConfigurations().getDouble(Configurations.VALUE_SEVERITY)));
					}
					else{
						i--;
					}
					break;
				case BLANKCHARSDELETION : //System.out.println("BLANKCHARSDELETION");
					if(getStringLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.deleteRANDOMBLANKS(TestDriver.getConfigurations().getDouble(Configurations.VALUE_SEVERITY)));
					}
					else{
						i--;
					}
					break;
				case RANDOMCHARSADDITION : //System.out.println("RANDOMCHARSADDITION");
					if(getStringLiteralCollection().contains(value.get(i))){	
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.addRANDOMCHARS(TestDriver.getConfigurations().getDouble(Configurations.VALUE_SEVERITY)));
					}
					else{
						i--;
					}
					break;
				case RANDOMCHARSDELETION : //System.out.println("RANDOMCHARSDELETION");
					if(getStringLiteralCollection().contains(value.get(i))){	
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.deleteRANDOMCHARS(TestDriver.getConfigurations().getDouble(Configurations.VALUE_SEVERITY)));
					}
					else{
						i--;
					}
					break;
				case RANDOMCHARSMODIFIER : //System.out.println("RANDOMCHARSMODIFIER");
					if(getStringLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.substituteRANDOMCHARS(TestDriver.getConfigurations().getDouble(Configurations.VALUE_SEVERITY)));
					}
					else{
						i--;
					}
					break;
				case TOKENADDITION : //System.out.println("TOKENADDITION");
					if(getStringLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.addTOKENS(TestDriver.getConfigurations().getString(Configurations.VALUE_TOKEN), TestDriver.getConfigurations().getDouble(Configurations.VALUE_SEVERITY)));
					}
					else{
						i--;
					}
					break;
				case TOKENDELETION : //System.out.println("TOKENDELETION");
					if(getStringLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.deleteTOKENS(TestDriver.getConfigurations().getString(Configurations.VALUE_TOKEN), TestDriver.getConfigurations().getDouble(Configurations.VALUE_SEVERITY)));
					}
					else{
						i--;
					}
					break;
				case TOKENSHUFFLE : //System.out.println("TOKENSHUFFLE");
					if(getStringLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.shuffleTOKENS(TestDriver.getConfigurations().getString(Configurations.VALUE_TOKEN), TestDriver.getConfigurations().getDouble(Configurations.VALUE_SEVERITY)));
					}
					else{
						i--;
					}
					break;
				case NAMESTYLEABBREVIATION : //System.out.println("NAMESTYLEABBREVIATION");
					if(getStringLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i),TransformationConfiguration.abbreviateNAME(TestDriver.getConfigurations().getInt(Configurations.VALUE_ABBREVIATION))); //NDOTS = 0; SCOMMANDOT = 1; ALLDOTS = 2;
					}
					else{
						i--;
					}
					break;
				case COUNTRYNAMEABBREVIATION : //System.out.println("COUNTRYNAMEABBREVIATION");
					if(getStringLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.abbreviateCOUNTRY());
					}
					else{
						i--;
					}
					break;
				case CHANGESYNONYM : //System.out.println("CHANGESYNONYM");
					if(getStringLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i),TransformationConfiguration.changeSYNONYMS(configuration.getString(Configurations.WORDNET_PATH),TestDriver.getConfigurations().getDouble(Configurations.VALUE_SEVERITY)));	
					}
					else{
						i--;
					}
					break;
				case CHANGEANTONYM : //System.out.println("CHANGEANTONYM");
					if(getStringLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.changeANTONYM(configuration.getString(Configurations.WORDNET_PATH),TestDriver.getConfigurations().getDouble(Configurations.VALUE_SEVERITY)));	
					}
					else{
						i--;
					}
					break;
				case CHANGENUMBER : //System.out.println("CHANGENUMBER");
					if(getNumericLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i),TransformationConfiguration.numberFORMAT(10, TestDriver.getConfigurations().getDouble(Configurations.VALUE_SEVERITY)));
					}
					else{
						i--;
					}
					break;
				case CHANGEDATEFORMAT : //System.out.println("CHANGEDATEFORMAT");
					if(getDateLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.dateFORMAT(TestDriver.getConfigurations().getString(Configurations.DATE_FORMAT), TestDriver.getConfigurations().getInt(Configurations.NEW_DATE_FORMAT))); //SHORT/MEDIUM/LONG
					}
					else{
						i--;
					}
					break;
				case CHANGELANGUAGE :  //System.out.println("CHANGELANGUAGE");
					if(getStringLiteralCollection().contains(value.get(i))){
							predicatesObjectsMap.put(value.get(i), TransformationConfiguration.changeLANGUAGE());
					}
					else{
						i--;
					}
					break;
				case CHANGEBOOLEAN : //System.out.println("CHANGEBOOLEAN");
					if(getBooleanLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.changeBOOLEAN());
					}
					else{
					 i--;
					}
					break; 
				case CHANGEGENDERFORMAT : //System.out.println("CHANGEGENDERFORMAT");
					if(getStringLiteralCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.genderFORMAT());
					}
					else{
						i--;
					}
					break;
				case STEMWORD : //System.out.println("STEMWORD");
					if(getStringLiteralCollection().contains(value.get(i))){
							predicatesObjectsMap.put(value.get(i), TransformationConfiguration.STEMWORD());
					}
					else{
						i--;
					}
					break;
				case CHANGEPOINT : //System.out.println("CHANGEPOINT");
					if(getGeoLatCollection().contains(value.get(i))){
							predicatesObjectsMap.put(value.get(i), TransformationConfiguration.CHANGELAT());
					}
					else if(getGeoLongCollection().contains(value.get(i))){
						predicatesObjectsMap.put(value.get(i), TransformationConfiguration.CHANGELONG());
					}
					else{
						i--;
					}
					break;
				case NOTRANSFORMATION: //System.out.println("NOTRANSFORMATION - VALUE");
					break;
				}
			}
		return predicatesObjectsMap;
	}
	

	public Map <String, Transformation> structureCases(TreeSet<String> treeSet){
		//this.structureArrayList = structureArrayList;
		ArrayList<String> structure = new ArrayList<String>(treeSet);
		Map <String, Transformation> predicatesObjectsMap = new HashMap<String, Transformation>();
		for(int i = 0; i < structure.size(); i++){
			initializeStructureEntity();

			switch(structPerc){
			case ADDPROPERTY: //System.out.println("ADDPROPERTY");
					predicatesObjectsMap.put(structure.get(i),TransformationConfiguration.addPROPERTY());
				break;
			case DELETEPROPERTY: //System.out.println("DELETEPROPERTY");
					predicatesObjectsMap.put(structure.get(i), TransformationConfiguration.deletePROPERTY());
				break;
			case EXTRACTPROPERTY: //System.out.println("EXTRACTPROPERTY");
				//if(!getObjectPropertiesCollection().contains(structure.get(i))){
					predicatesObjectsMap.put(structure.get(i), TransformationConfiguration.extractPROPERTY(TestDriver.getConfigurations().getInt(Configurations.EXTRACT_PROPERTY)));
				//}
				//else{
				//	i--;
				//}
				break;
			case AGGREGATEPROPERTY: //System.out.println("AGGREGATEPROPERTY");
				//if(!getObjectPropertiesCollection().contains(structure.get(i))){
					//System.out.println("AGGREGATEPROPERTY");
					predicatesObjectsMap.put(structure.get(i), TransformationConfiguration.aggregatePROPERTIES(worker));
				//}
				//else{
				//	i--;
				//}
				break;
			case NOTRANSFORMATION:
				break;
			}
		}
		return predicatesObjectsMap;
	}
	
	
	public Map <String, Transformation> semanticsAwareCases(TreeSet<String> treeSet){
		//this.semanticsAwareArrayList = semanticsAwareArrayList;
		ArrayList<String> semantics = new ArrayList<String>(treeSet);
		
		Map <String, Transformation> predicatesObjectsMap = new HashMap<String, Transformation>();
		for(int i = 0; i < semantics.size(); i++){
			initializeSemanticsAwareEntity();
			switch(semPerc){
			case SAMEAS: //System.out.println("SAMEAS");
				if(getClassesCollection().contains(semantics.get(i)) && getComplexTransformationConfigurations().isEmpty()){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.SAMEAS(worker));
				}
				break;
			case SAMEASONEXISTINGINSTANCE: //System.out.println("SAMEASONEXISTINGINSTANCE");
				if(getClassesCollection().contains(semantics.get(i)) && getComplexTransformationConfigurations().isEmpty()){
					predicatesObjectsMap.put(semantics.get(i),TransformationConfiguration.SAMEASONEXISTINGINSTANCES(worker));
				}
				break;
			case DIFFERENTFROM: //System.out.println("DIFFERENTFROM");
				if(getClassesCollection().contains(semantics.get(i)) && getComplexTransformationConfigurations().isEmpty()){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.DIFFERENTFROM(worker));
				}
				break;
			case SUBCLASSOF: //System.out.println("SUBCLASSOF");
				if(getClassesCollection().contains(semantics.get(i))){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.SUBCLASSOF(worker));
				}
				else{
				 i--;
				}
				break;
			case EQUIVALENTCLASS: //System.out.println("EQUIVALENTCLASS");
				if(equivalentClassMap.containsKey(semantics.get(i))){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.EQUIVALENTCLASS(worker));
				}
				else{
				 i--;
				}
				break;
			case DISJOINTWITH: //System.out.println("DISJOINTWITH");
				if(disjointWithMap.containsKey(semantics.get(i))){
						predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.DISJOINTWITH(worker));
				}
				else{
				 i--;
				}
				break;
			case UNIONOF: //System.out.println("UNIONOF");
				if(unionOfMap.containsKey(semantics.get(i))){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.UNIONOF(worker));
				}
				else{
					i--;
				}
				break;
			case INTERSECTIONOF: //System.out.println("INTERSECTIONOF");
				if(intersectionOfMap.containsKey(semantics.get(i))){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.INTERSECTIONOF(worker));
				}
				else{
					i--;
				}
				break;				
			case SUBPROPERTYOF: //System.out.println("SUBPROPERTYOF ");
				if(subPropertyOfMap.containsKey(semantics.get(i))){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.SUBPROPERTY(worker));
				}
				else{
				 i--;
				}
				
				break; 			
			case EQUIVALENTPROPERTY: //System.out.println("EQUIVALENTPROPERTY ");
				if(equivalentPropertyMap.containsKey(semantics.get(i))){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.EQUIVALENTPROPERTY(worker));
				}
				else{
				 i--;
				}
				break;
			case DISJOINTPROPERTY: //System.out.println("DISJOINTPROPERTY ");
				if(disjointPropertyMap.containsKey(semantics.get(i))){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.DISJOINTPROPERTY(worker));
				}
				else{
					i--;
				}
				break;  
			case FUNCTIONALPROPERTY: //System.out.println("FUNCTIONALPROPERTY ");
				if(functionalProperty.contains(semantics.get(i))){
				 predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.FUNCTIONALPROPERTY(worker));
				}
				else{
					i--;
				}
				break;
			case INVERSEFUNCTIONALPROPERTY: //System.out.println("INVERSEFUNCTIONALPROPERTY ");
				if(inverseFunctionalPropertyMap.containsKey(semantics.get(i))){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.INVERSEFUNCTIONALPROPERTY(worker));
				}
				else{
					i--;
				}
				break;
			case NOTRANSFORMATION:
				break;
			}
		}
		return predicatesObjectsMap;
	}
	
	public Map <String, Transformation> complexSemanticsAwareCases(TreeSet<String> treeSet){
		//this.semanticsAwareArrayList = semanticsAwareArrayList;
		ArrayList<String> semantics = new ArrayList<String>(treeSet);
		
		Map <String, Transformation> predicatesObjectsMap = new HashMap<String, Transformation>();
		for(int i = 0; i < semantics.size(); i++){
			initializeComplexSemanticsAwareEntity();
			switch(semCompPerc){			
			case SUBPROPERTYOF: //System.out.println("SUBPROPERTYOF ");
				if(subPropertyOfMap.containsKey(semantics.get(i))){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.SUBPROPERTY(worker));
				}
				else{
				 i--;
				}
				
				break; 			
			case EQUIVALENTPROPERTY: //System.out.println("EQUIVALENTPROPERTY ");
				if(equivalentPropertyMap.containsKey(semantics.get(i))){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.EQUIVALENTPROPERTY(worker));
				}
				else{
				 i--;
				}
				break;
			case DISJOINTPROPERTY: //System.out.println("DISJOINTPROPERTY ");
				if(disjointPropertyMap.containsKey(semantics.get(i))){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.DISJOINTPROPERTY(worker));
				}
				else{
					i--;
				}
				break;  
			case FUNCTIONALPROPERTY: //System.out.println("FUNCTIONALPROPERTY ");
				if(functionalProperty.contains(semantics.get(i))){
				 predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.FUNCTIONALPROPERTY(worker));
				}
				else{
					i--;
				}
				break;
			case INVERSEFUNCTIONALPROPERTY: //System.out.println("INVERSEFUNCTIONALPROPERTY ");
				if(inverseFunctionalPropertyMap.containsKey(semantics.get(i))){
					predicatesObjectsMap.put(semantics.get(i), TransformationConfiguration.INVERSEFUNCTIONALPROPERTY(worker));
				}
				else{
					i--;
				}
				break;
			case NOTRANSFORMATION:
				break;
			}
		}
		return predicatesObjectsMap;
	}
	
	public void setTransformationConfigurations(){	
		initializeTransformationsEntity();
		switch (transfPerc) { 
		case VALUE :
			//System.out.println("VALUE");
			predicatesObjectsMap = valueCases(getValueCollection());
			break;
		
		case STRUCTURE :
			//System.out.println("STRUCTURE");
			predicatesObjectsMap = structureCases(getStructureCollection());
			break;
			
		case SEMANTICS_AWARE:
			//System.out.println("SEMANTICS_AWARE");
			predicatesObjectsMap = semanticsAwareCases(getSemanticsAwareCollection());
			break;
			
		case SIMPLECOMBINATION :
			//System.out.println("SIMPLECOMBINATION");
			initializeSimpleCombinationEntity();
			switch(simplePerc){
			case VALUE :
				predicatesObjectsMap = valueCases(getValueCollection());
				break;
			
			case STRUCTURE :
				predicatesObjectsMap = structureCases(getStructureCollection());
				break;
				
			case SEMANTICS_AWARE:
				predicatesObjectsMap = semanticsAwareCases(getSemanticsAwareCollection());
				break;
				
			case NOTRANSFORMATION:
				break;
			}
			
			break;

		//N transformations per triple N >= 2 && N <= 3 	
		case COMPLEXCOMBINATION :
			//System.out.println("COMPLEXCOMBINATION");
			initializeComplexCombinationEntity();
			initializeComplexSemanticsAwareEntity();
			List <Transformation> transformation;
			Transformation value = null;
			Transformation structure = null;
			Transformation semanticsAware = null;
			Map <String, Transformation> predicatesObjectsMapTemp;
			complexPredicatesObjectsMap = new HashMap<String, List<Transformation>>(); 
				switch(compvalPerc){
				
				case VALUE_STRUCTURE:
					//System.out.println("VALUE_STRUCTURE");
					ArrayList<String> listVST = new ArrayList<String>();
					listVST.addAll(getComplexValueStructureTree());
					for(int i = 0; i < listVST.size(); i++){
						transformation = new ArrayList<Transformation>();	
						predicatesObjectsMapTemp = valueCases(getComplexValueStructureTree()); 
						if(!predicatesObjectsMapTemp.values().isEmpty()) value = (Transformation) predicatesObjectsMapTemp.values().toArray()[0];
						transformation.add(value);
						//System.out.println("value "+value.toString());
						do{
							predicatesObjectsMapTemp = structureCases(getComplexValueStructureTree());
							if(!predicatesObjectsMapTemp.values().isEmpty()){
								structure = (Transformation) predicatesObjectsMapTemp.values().toArray()[0];
								//System.out.println("structure "+structure.toString());
								transformation.add(structure);
							}
						}while(structure==null || !structure.getClass().getName().contains("Extract") && !structure.getClass().getName().contains("Aggregate"));
						if(transformation.size() == 2) complexPredicatesObjectsMap.put(listVST.get(i), transformation);
					}
					break;
				case VALUE_SEMANTICS_AWARE:
//					System.out.println("VALUE_SEMANTICS_AWARE");
					ArrayList<String> listVSE = new ArrayList<String>();
					listVSE.addAll(getComplexValueSemanticsTree());
					
					for(int i = 0; i < listVSE.size(); i++){
						transformation = new ArrayList<Transformation>();
						predicatesObjectsMapTemp = valueCases(getComplexValueSemanticsTree()); 
						if(!predicatesObjectsMapTemp.values().isEmpty()) value = (Transformation) predicatesObjectsMapTemp.values().toArray()[0];
						transformation.add(value);  
						//System.out.println("value "+value.toString());
						do{
							predicatesObjectsMapTemp = complexSemanticsAwareCases(getComplexValueSemanticsTree()); 
							if(!predicatesObjectsMapTemp.values().isEmpty()){
								semanticsAware = (Transformation) predicatesObjectsMapTemp.values().toArray()[0];
								//System.out.println("semanticsAware "+semanticsAware.toString());
								transformation.add(semanticsAware);
							}
						}while(semanticsAware==null /*|| semanticsAware.getClass().getName().contains("SameAs") || semanticsAware.getClass().getName().contains("DifferentFrom") || semanticsAware.getClass().getName().contains("SameAsOnExistingInstances")*/);
						if(transformation.size() == 2) complexPredicatesObjectsMap.put(listVSE.get(i), transformation);		
					}
					break;
				case NOTRANSFORMATION:
					break;
				}
			predicatesObjectsMap = semanticsAwareCases(getClassSemanticsAwareCollection()); //call semantics aware transformations for class except of complex transformation of properties
			break;
			
		case NOTRANSFORMATION :
			//System.out.println("NOTRANSFORMATION");
			break;
		
		}
	}
	//return a map that as key contains every predicate and as value the type of transformation
	//that will be done o n its object. The key might also denote that the object will not change
	public HashMap<String, Transformation> getTransformationConfigurations(){
		return (HashMap<String, Transformation>) predicatesObjectsMap;
	}
	
	public HashMap<String, List<Transformation>> getComplexTransformationConfigurations(){
		return (HashMap<String, List<Transformation>>) complexPredicatesObjectsMap;
	}
	
	
}

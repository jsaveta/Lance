package data;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import properties.Configurations;


public class Model {
    private Repository repository;
    private URI schemaContext;
     
    public static Configurations configurations = new Configurations();
    
    private final String typeOfUriAsText = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    private final String classUriAsText = "http://www.w3.org/2000/01/rdf-schema#Class";
    private final String disjointClassUriAsText = "http://www.w3.org/2002/07/owl#disjointWith";
    private final String equivalentClassUriAsText = "http://www.w3.org/2002/07/owl#equivalentClass"; 
    private final String disjointPropertyUriAsText = "http://www.w3.org/2002/07/owl#disjointWith"; //disjointProperty
    private final String equivalentPropertyUriAsText = "http://www.w3.org/2002/07/owl#equivalentProperty";//equivalentProperty
    private final String functionalPropertyUriAsText = "http://www.w3.org/2002/07/owl#FunctionalProperty";//functionalProperty
    private final String inverseFunctionalPropertyUriAsText = "http://www.w3.org/2002/07/owl#InverseFunctionalProperty";//inverseFunctionalProperty
    //owl:sameAs and owl:differentFrom will use the getClassInstances method in order to get the instances of the class we need
    
    //RDFXML
    private final String subClassOfUriAsText = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
    private final String subPropertyOfUriAsText = "http://www.w3.org/2000/01/rdf-schema#subPropertyOf";
    //private final String propertyUriAsText="http://www.w3.org/1999/02/22-rdf-syntax-ns#Property";
    private final String literalAsText = "http://www.w3.org/2000/01/rdf-schema#Literal";
    private final String rangeAsText = "http://www.w3.org/2000/01/rdf-schema#range";
    
    //OWL
    private final String owlClassUriAsText = "http://www.w3.org/2002/07/owl#Class";
    //might not need at datatype and object properties 
    private final String owlObjectProperyUriAsText = "http://www.w3.org/2002/07/owl#ObjectProperty";
    private final String owlDatatypeProperyUriAsText = "http://www.w3.org/2002/07/owl#DatatypeProperty";
    private final String owlInstanceUriAsText = "http://www.w3.org/1999/02/22-rdf-syntax-ns#about";
    
  //TODO   
  //Make sure that ALL the methods below will be able to recognize a equiv/disjoint b with starting class a and b (2 cases!) 
   // see if there is a better way for the queries instead of copy-paste and change the subject and object only! 
    
    
    //conect to repository
    public Model(){
    	//TODO kanonika thelei to endpointurl, alla an den to trexw me to ant diavazei ayta pou uparxoun ston kodika kai einai null tora!!! allakse to! 
    	//anti gia split valto sto config file? Alla afou einai mono sesame tha mporouse na meinei ki etsi 
    	String endpointUrl = "http://localhost:8080/openrdf-sesame/repositories/spimbench";//TestDriver.getConfigurations().getString(Configurations.ENDPOINT_URL);
        try{
        	this.repository = new HTTPRepository(endpointUrl);
        	this.repository.initialize();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
        
    }
    
    //add ontologies to repository
    public Model(Collection<File> rdfFilesToImport){
        this();
        this.schemaContext=this.repository.getValueFactory().createURI("http://localhost/context");
        //this.schemaContext=this.repository.getValueFactory().createURI(TestDriver.getConfigurations().getString(Configurations.ENDPOINT_URL));
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
	          for(File file : rdfFilesToImport){
	        	  System.out.println("Adding file: " +file.getName());
	        	  repoConn.add(file, this.schemaContext.toString(), RDFFormat.forMIMEType(file.getName()), this.schemaContext); //forMIMEType finds the file format in order to read more than one rdfformats 
	        	  System.out.println("FILE ADDED");
	          }
            repoConn.close();
        }catch(IOException | RDFParseException | RepositoryException ex){
            ex.printStackTrace();
        }
    }
   
    /************************CLASSES*************************/
    public Collection<String> getAllClasses(){
        Set<String> allClasses=new HashSet<>();
        URI classUri=this.repository.getValueFactory().createURI(classUriAsText);
        URI typeOfUri=this.repository.getValueFactory().createURI(typeOfUriAsText);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, typeOfUri, classUri, false);
            while(results.hasNext()){
            	Statement tempResult = results.next();
            	if(!(tempResult.getSubject() instanceof BNode)){
            		allClasses.add(tempResult.getSubject().stringValue());
            	}
            }
            repoConn.close();
        }catch(RepositoryException ex){
            
        }
        
        URI owlClassUri=this.repository.getValueFactory().createURI(owlClassUriAsText);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, typeOfUri, owlClassUri, false);
            while(results.hasNext()){
            	Statement tempResult = results.next();
            	if(!(tempResult.getSubject() instanceof BNode)){
            		allClasses.add(tempResult.getSubject().stringValue());
            	}
            }
            repoConn.close();
        }catch(RepositoryException ex){
            
        }
        return allClasses;
    }

    public Collection<String> getSubClassesOf(String startingClass, boolean useInference){
        Set<String> subClasses=new HashSet<>();
        URI subClassUri=this.repository.getValueFactory().createURI(this.subClassOfUriAsText);
        URI classUri=this.repository.getValueFactory().createURI(startingClass);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, subClassUri, classUri, useInference);
            while(results.hasNext()){
            	Statement tempResult = results.next();
            	if(!(tempResult.getSubject() instanceof BNode)){
            		subClasses.add(tempResult.getSubject().stringValue());
            	}
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
    return subClasses;
    }
    
    public Collection<String> getSuperClasses(String startingClass, boolean useInference){
        Set<String> superClasses=new HashSet<>();
        URI subClassUri=this.repository.getValueFactory().createURI(this.subClassOfUriAsText);
        URI classUri=this.repository.getValueFactory().createURI(startingClass);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(classUri, subClassUri,null , useInference);
            while(results.hasNext()){
            	Statement tempResult = results.next();
            	if(!(tempResult.getObject() instanceof BNode)){
            		superClasses.add(tempResult.getObject().stringValue());
            	}
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
    return superClasses;
    }
  
    public Collection<String> getDisjointClasses(String startingClass){
        Set<String> disjointClasses = new HashSet<>();
        URI disjointClassUri = this.repository.getValueFactory().createURI(this.disjointClassUriAsText);
	    URI classUri = this.repository.getValueFactory().createURI(startingClass);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, disjointClassUri, classUri,false);
            while(results.hasNext()){
            	disjointClasses.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
        
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(classUri, disjointClassUri,null ,false);
            while(results.hasNext()){
            	disjointClasses.add(results.next().getObject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
    return disjointClasses;
    }
    
    
    public Collection<String> getAllDisjointClasses(String startingClass) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
    	Set<String> allDisjointClasses = new HashSet<>();
		RepositoryConnection con = this.repository.getConnection();
		try{
			  TupleQuery allDisjointClassesQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?items  "
			  		+ "WHERE { ?y a owl:AllDisjointClasses ."
			  		+ "?y owl:members ?members ."
			  		+ "?members rdf:rest*/rdf:first <"+startingClass+"> ."
			  		+ "?members rdf:rest*/rdf:first ?items ."
			  		+ "FILTER (?items != <"+startingClass+">) .}");
			  		
			  
			   TupleQueryResult allDisjointClassesResult = allDisjointClassesQuery.evaluate();
			   try {
			      while (allDisjointClassesResult.hasNext()) {
			         BindingSet bindingSet = allDisjointClassesResult.next();
			         Value name = bindingSet.getValue("items");
			         allDisjointClasses.add(name.toString());
			      }
			   }
			   finally {
				   allDisjointClassesResult.close();
			   }
			}
			finally {
			   con.close();
			}
	    return allDisjointClasses;
    }
    
    public Collection<String> getEquivalentClasses(String startingClass){
        Set<String> equivalentClasses = new HashSet<>();
        URI equivalentClassUri = this.repository.getValueFactory().createURI(this.equivalentClassUriAsText);
	    URI classUri = this.repository.getValueFactory().createURI(startingClass);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, equivalentClassUri, classUri,false);
            while(results.hasNext()){
            	equivalentClasses.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
        
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(classUri, equivalentClassUri,null ,false);
            while(results.hasNext()){
            	equivalentClasses.add(results.next().getObject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
    return equivalentClasses;
    }
   
    
    public Collection<String> getUnionOf(String startingClass) throws RepositoryException, MalformedQueryException, QueryEvaluationException{    
		Set<String> unionOfClasses = new HashSet<>();
		RepositoryConnection con = this.repository.getConnection();
		try{
			   TupleQuery unionOfQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,"SELECT ?c WHERE {  ?c owl:unionOf ?l . "
			   		+ " ?l rdf:rest*/rdf:first <"+startingClass+"> .}");
			   // Evaluate the first query to get results
			   TupleQueryResult unionOfResult = unionOfQuery.evaluate();
			   try {
			      while (unionOfResult.hasNext()) {
			         BindingSet bindingSet = unionOfResult.next();
			         Value name = bindingSet.getValue("c");
			    	 unionOfClasses.add(name.toString());
			         //System.out.println("union Ofs : " + name);
			      }
			   }
			   finally {
				   unionOfResult.close();
			   }
			}
			finally {
			   con.close();
			}
	    return unionOfClasses;
    }
    
    
    public Collection<String> getIntersectionOf(String startingClass) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
    	Set<String> intersectionOfClasses = new HashSet<>();
		RepositoryConnection con = this.repository.getConnection();
		try{
			   TupleQuery intersectionOfQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,"SELECT ?c WHERE {  ?c owl:intersectionOf ?l .  "
			   		+ "?l rdf:rest*/rdf:first <"+startingClass+"> .}");
			   TupleQueryResult intersectionOfResult = intersectionOfQuery.evaluate();
			   try {
			      while (intersectionOfResult.hasNext()) {
			         BindingSet bindingSet = intersectionOfResult.next();
			         Value name = bindingSet.getValue("c");
			         intersectionOfClasses.add(name.toString());
			         //System.out.println("intersections Ofs : " + name);
			      }
			   }
			   finally {
				   intersectionOfResult.close();
			   }
			}
			finally {
			   con.close();
			}
	    return intersectionOfClasses;
    }
    
    
    
    /************************PROPERTIES*************************/
    public Collection<String> getAllObjectProperties(){
        Set<String> allObjectProperties=new HashSet<>();
        //check if property is object property
        URI rangeOfUri = this.repository.getValueFactory().createURI(rangeAsText);

        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, rangeOfUri, null, false);
            while(results.hasNext()){
            	Statement tempResult = results.next();
            	//if range is not rdf:resource literal means that is an object property
            	if(!tempResult.getObject().stringValue().equals(literalAsText)){
            		allObjectProperties.add(tempResult.getSubject().stringValue());
            	}
            }
            repoConn.close();
        }catch(RepositoryException ex){
            
        }
        
        URI typeOfUri=this.repository.getValueFactory().createURI(typeOfUriAsText);
		URI objectProperyUri=this.repository.getValueFactory().createURI(owlObjectProperyUriAsText);
		try{
		    RepositoryConnection repoConn=this.repository.getConnection();
		    RepositoryResult<Statement> results=repoConn.getStatements(null, typeOfUri, objectProperyUri, false);
		    while(results.hasNext()){
		    	allObjectProperties.add(results.next().getSubject().stringValue());
		    }
		    repoConn.close();
		}catch(RepositoryException ex){
		    
		}
		return allObjectProperties;
    }
    
    public Collection<String> getAllDatatypeProperties(){

        Set<String> allDatatypeProperties=new HashSet<>();
    	//check if property is datatype property (literal)
        URI rangeOfUri = this.repository.getValueFactory().createURI(rangeAsText);
        URI literalUri = this.repository.getValueFactory().createURI(literalAsText);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, rangeOfUri, literalUri, false);
            while(results.hasNext()){
            	allDatatypeProperties.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            
        }

        URI typeOfUri=this.repository.getValueFactory().createURI(typeOfUriAsText);
        URI datatypeProperyUri=this.repository.getValueFactory().createURI(owlDatatypeProperyUriAsText);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, typeOfUri, datatypeProperyUri, false);
            while(results.hasNext()){
            	allDatatypeProperties.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            
        }
        return allDatatypeProperties;
    }
    
    public Collection<String> getSubPropertyOf(String startingProperty, boolean useInference){
        Set<String> subProperties=new HashSet<>();
        URI subPropertyUri=this.repository.getValueFactory().createURI(this.subPropertyOfUriAsText);
        URI propertyUri=this.repository.getValueFactory().createURI(startingProperty);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, subPropertyUri, propertyUri, useInference);
            while(results.hasNext()){
            	String subProperty = results.next().getSubject().stringValue();
            	if(!subProperty.equals(startingProperty)) subProperties.add(subProperty);
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
        return subProperties;
    }
    
  //TODO : check this method (haven't check this due to the poor test ontology) 
    public Collection<String> getDisjointProperties(String startingProperty){
        Set<String> disjointProperties = new HashSet<>();
        URI disjointPropertyUri = this.repository.getValueFactory().createURI(this.disjointPropertyUriAsText);
	    URI propertyUri = this.repository.getValueFactory().createURI(startingProperty);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, disjointPropertyUri, propertyUri,false);
            while(results.hasNext()){
            	disjointProperties.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
        
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(propertyUri, disjointPropertyUri,null ,false);
            while(results.hasNext()){
            	disjointProperties.add(results.next().getObject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
    return disjointProperties;
    }
    
    public Collection<String> getAllDisjointProperties(String startingProperty) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
    	Set<String> allDisjointProperties = new HashSet<>();
		RepositoryConnection con = this.repository.getConnection();
		try{
			  TupleQuery allDisjointPropertiesQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?items  "
			  		+ "WHERE { ?y a owl:AllDisjointProperties ."
			  		+ "?y owl:members ?members ."
			  		+ "?members rdf:rest*/rdf:first <"+startingProperty+"> ."
			  		+ "?members rdf:rest*/rdf:first ?items ."
			  		+ "FILTER (?items != <"+startingProperty+">) .}");
			  		
			  
			   TupleQueryResult allDisjointPropertiesResult = allDisjointPropertiesQuery.evaluate();
			   try {
			      while (allDisjointPropertiesResult.hasNext()) {
			         BindingSet bindingSet = allDisjointPropertiesResult.next();
			         Value name = bindingSet.getValue("items");
			         allDisjointProperties.add(name.toString());
			      }
			   }
			   finally {
				   allDisjointPropertiesResult.close();
			   }
			}
			finally {
			   con.close();
			}
	    return allDisjointProperties;
    }
    
    
  //TODO : check this method (haven't check this due to  the poor test ontology) 
    public Collection<String> getEquivalentProperties(String startingProperty){
        Set<String> equivalentProperties = new HashSet<>();
        URI equivalentPropertyUri = this.repository.getValueFactory().createURI(this.equivalentPropertyUriAsText);
	    URI propertyUri = this.repository.getValueFactory().createURI(startingProperty);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, equivalentPropertyUri, propertyUri,false);
            while(results.hasNext()){
            	equivalentProperties.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
        
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(propertyUri, equivalentPropertyUri, null,false);
            while(results.hasNext()){
            	equivalentProperties.add(results.next().getObject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
    return equivalentProperties;
    }
    
  //check this and the below one, if we need the subject or the object!!!!!!!!!!
  //TODO : check this method (haven't check this due to  the poor test ontology) 
    public Collection<String> getFunctionalProperties(String startingProperty){
        Set<String> functionalProperties = new HashSet<>();
        URI functionalPropertyUri = this.repository.getValueFactory().createURI(this.functionalPropertyUriAsText);
	    URI propertyUri = this.repository.getValueFactory().createURI(startingProperty);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, functionalPropertyUri, propertyUri,false);
            while(results.hasNext()){
            	functionalProperties.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
    return functionalProperties;
    }
    
  //TODO : check this method (haven't check this due to  the poor test ontology) 
    public Collection<String> getInverseFunctionalProperties(String startingProperty){
        Set<String> inverseFunctionalProperties = new HashSet<>();
        URI inverseFunctionalPropertyUri = this.repository.getValueFactory().createURI(this.inverseFunctionalPropertyUriAsText);
	    URI propertyUri = this.repository.getValueFactory().createURI(startingProperty);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, inverseFunctionalPropertyUri, propertyUri,false);
            while(results.hasNext()){
            	inverseFunctionalProperties.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
    return inverseFunctionalProperties;
    }
    
    /************************INSTANCES*************************/
   
    public Collection<String> getClassInstances(String className){
        Set<String> retInstances=new HashSet<>();
        URI typeOfUri=this.repository.getValueFactory().createURI(typeOfUriAsText);
        URI classUri=this.repository.getValueFactory().createURI(className);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, typeOfUri, classUri, false);
            while(results.hasNext()){
                retInstances.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
        
        URI owlTypeOfUri=this.repository.getValueFactory().createURI(owlInstanceUriAsText);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, owlTypeOfUri, classUri, false);
            while(results.hasNext()){
                retInstances.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
        return retInstances;
    }
    
    public Collection<String> getAllTriples(){
        Set<String> all=new HashSet<>();
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, null, null, false);
            while(results.hasNext()){
            	Statement tempResult = results.next();
            	all.add(tempResult.toString());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
    return all;
    }

    
    public static void main(String[] args) throws QueryEvaluationException, MalformedQueryException, RepositoryException{
//    	System.out.println("Read ontologies from Model.java");
//		String ontologiesPath = StringUtil.normalizePath(TestDriver.getConfigurations().getString(Configurations.ONTOLOGIES_PATH));
//		List<File> collectedFiles = new ArrayList<File>();
//		try {
//			FileUtils.collectFilesList2(ontologiesPath, collectedFiles, "*", true);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Collections.sort(collectedFiles);
//		
//      Model model=new Model(collectedFiles);  
       
    	
        Model model=new Model(Arrays.asList(new File("./datasets_and_ontologies/ontologies/ldbc.ttl"))); //c26.owl cidoc_crm_v5.1.rdfs 
        
//        System.out.println("All Classes");
//        for(String cl : model.getAllClasses())
//            System.out.println("\t"+cl);

//	      System.out.println("All unionof Classes");
//	      for(String cl : model.getAllClasses())
//	        	 for(String scl : model.getUnionOf(cl))
//	        		 System.out.println(scl+"\tunionof\t"+cl);

	      System.out.println("All intersectionOf Classes");
	      for(String cl : model.getAllClasses())
	        	 for(String scl : model.getIntersectionOf(cl))
	        		 System.out.println(scl+"\tintersectionOf\t"+cl);
//        System.out.println("All Triples");
//        for(String scl : model.getAllTriples())
//  		 System.out.println(scl);
//
//        System.out.println("All Disjoint Classes");
//        for(String cl : model.getAllClasses())
//       	 for(String scl : model.getAllDisjointClasses(cl))
//       		 System.out.println(scl+"\tAll disjoint classes\t"+cl);
//        
//       
//        System.out.println("All direct subclasses from all classes");
//        for(String cl : model.getAllClasses())
//        	 for(String scl : model.getSubClassesOf(cl,false))
//        		 System.out.println(scl+"\tsubclassOf\t"+cl);
//        
//        System.out.println("All direct superclasses from all classes");
//        for(String cl : model.getAllClasses())
//        	 for(String scl : model.getSuperClasses(cl,false))
//        		 System.out.println(scl+"\tsuperclass Of\t"+cl);
//
//        System.out.println("All inferred subclasses from all classes");
//        for(String cl : model.getAllClasses())
//        	 for(String scl : model.getSubClassesOf("http://www.ldbc.eu/NINA",true))
//                 System.out.println("Class: "+cl+"\t SubClass: "+scl);
//        
//        System.out.println("All datatype properties");
//        for(String pr : model.getAllDatatypeProperties())
//            System.out.println("\t"+pr);
//        
//        System.out.println("All direct subproperties from all datatype properties");
//        for(String pr : model.getAllDatatypeProperties())
//        	 for(String spr : model.getSubPropertyOf(pr,false))
//        		 System.out.println("DatatypeProperty: "+pr+"\t Subproperty: "+spr);
//        
//        System.out.println("All inferred subproperties from all datatype properties");
//        for(String pr : model.getAllDatatypeProperties())
//        	 for(String spr : model.getSubPropertyOf(pr,true))
//                 System.out.println("DatatypeProperty: "+pr+"\t Subproperty: "+spr);
//        
//        System.out.println("All object properties");
//        for(String pr : model.getAllObjectProperties())
//            System.out.println("\t"+pr);
//        
//        System.out.println("All direct subproperties from all object properties");
//        for(String pr : model.getAllObjectProperties())
//        	 for(String spr : model.getSubPropertyOf(pr,false))
//        		 System.out.println("ObjectProperty: "+pr+"\t Subproperty: "+spr);
//        
//        System.out.println("All inferred subproperties from all object properties");
//        for(String pr : model.getAllObjectProperties())
//        	 for(String spr : model.getSubPropertyOf(pr,true))
//                 System.out.println("ObjectProperty: "+pr+"\t Subproperty: "+spr);
//         
//        System.out.println("All pairs of disjoint classes (disjointWith)");
//        for(String cl : model.getAllClasses())
//        	for(String dcl : model.getDisjointClasses(cl))
//          	System.out.println("Class: "+cl+"\t DijointClass: "+dcl);
//        
//  	  	 System.out.println("All equivalent classes");
//  	  	 for(String cl : model.getAllClasses())
//  	  		 for(String ecl : model.getEquivalentClasses(cl))
//  	  			 System.out.println("Class: "+cl+"\t EquivalentClass: "+ecl);
    
//	  	 System.out.println("All unions of");
//	  	 for(String cl : model.getAllClasses())
//	  		 for(String ecl : model.getUnionOf(cl))
//	  			 System.out.println(ecl+"\t union of: " +cl);
 		 
//      System.out.println("Direct subClasses of E7");
//      for(String cl : model.getSubClassesOf("http://www.cidoc-crm.org/cidoc-crm/E7_Activity",false))
//          System.out.println("\t"+cl);
//      
//      System.out.println("Direct + Inferred subClasses of E7");
//      for(String cl : model.getSubClassesOf("http://www.cidoc-crm.org/cidoc-crm/E7_Activity",true))
//          System.out.println("\t"+cl); 
//        //Add some Instances under E7
//        model.addClassInstance("http://localhost/swimming","http://www.cidoc-crm.org/cidoc-crm/E7_Activity");
//        model.addClassInstance("http://localhost/running","http://www.cidoc-crm.org/cidoc-crm/E7_Activity");
//        model.addClassInstance("http://localhost/reading","http://www.cidoc-crm.org/cidoc-crm/E7_Activity");       
//    
//        System.out.println("Instances of all classes");
//        for(String cl : model.getAllClasses())
//        	 for(String inst : model.getClassInstances(cl))
//                 System.out.println("Class: "+cl+"\t Instance: "+inst);
	  	 
	  	 

    }
}
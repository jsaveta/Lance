package data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
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
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import properties.Configurations;
import util.FileUtils;
import util.StringUtil;
import main.TestDriver;


/**
 * @author jsaveta, Foundation for Research and Technology-Hellas (FORTH)
 *
 * @date Dec 10, 2014
 */
public class LoadOntologies extends DataManager{
	
	/**
	 * Read ontologies from ONTOLOGIES_PATH and load them into ENDPOINT_URL.
	 * @param enable, boolean parameter that determines if the ontologies are going to be loaded
	 * @throws IOException
	 */
	
	//TriG, TriX, N-Triples, N-Quads, N3, RDF/XML, RDF/JSON, Turtle
	
	private final String typeOfUriAsText = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    private final String classUriAsText = "http://www.w3.org/2000/01/rdf-schema#Class";
    private final String disjointClassUriAsText = "http://www.w3.org/2002/07/owl#disjointWith";
    private final String equivalentClassUriAsText = "http://www.w3.org/2002/07/owl#equivalentClass"; 
    private final String disjointPropertyUriAsText = "http://www.w3.org/2002/07/owl#disjointWith"; //disjointProperty
    private final String equivalentPropertyUriAsText = "http://www.w3.org/2002/07/owl#equivalentProperty";//equivalentProperty
    private final String functionalPropertyUriAsText = "http://www.w3.org/2002/07/owl#FunctionalProperty";//functionalProperty
    private final String inverseFunctionalPropertyUriAsText = "http://www.w3.org/2002/07/owl#InverseFunctionalProperty";//inverseFunctionalProperty
    //RDFXML
    private final String subClassOfUriAsText = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
    private final String subPropertyOfUriAsText = "http://www.w3.org/2000/01/rdf-schema#subPropertyOf";
    private final String literalAsText = "http://www.w3.org/2000/01/rdf-schema#Literal";
    private final String stringAsText = "http://www.w3.org/2000/01/rdf-schema#string";
    private final String rangeAsText = "http://www.w3.org/2000/01/rdf-schema#range";
    //OWL
    private final String owlClassUriAsText = "http://www.w3.org/2002/07/owl#Class";
    private final String owlObjectProperyUriAsText = "http://www.w3.org/2002/07/owl#ObjectProperty";
    private final String owlDatatypeProperyUriAsText = "http://www.w3.org/2002/07/owl#DatatypeProperty";
    private final String rdfProperyUriAsText = "http://www.w3.org/1999/02/22-rdf-syntax-ns#Property";
    private final String owlInstanceUriAsText = "http://www.w3.org/1999/02/22-rdf-syntax-ns#about";
    private final String xsdAsText = "http://www.w3.org/2001/XMLSchema#";
    private final String geoPointAsText = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    private String endpointUrl = TestDriver.getConfigurations().getString(Configurations.ENDPOINT_URL);
    private URI schemaContext;
	
	 public LoadOntologies(boolean enable) throws IOException{
		   
		 Properties props = new Properties();
         props.setProperty("log4j.rootLogger", "DEBUG, R");
         props.setProperty("log4j.appender.R.layout", "org.apache.log4j.PatternLayout");
         props.setProperty("log4j.appender.R.layout.ConversionPattern", "%p-> %m%n");
         props.setProperty("log4j.appender.R", "org.apache.log4j.FileAppender");
         props.setProperty("log4j.appender.R.File", "app.log");
         PropertyConfigurator.configure(props);
            
         try{
	        	this.repository = new HTTPRepository(endpointUrl);
	        	this.repository.initialize();
	            schemaContext=this.repository.getValueFactory().createURI(TestDriver.getConfigurations().getString(Configurations.ENDPOINT_URL)+"/ontologies");
	     		
	        }catch(RepositoryException ex){
	            ex.printStackTrace();
	        }
		 if (enable) {	
	        System.out.println("\n\nLoading ontologies...");
			String ontologiesPath = StringUtil.normalizePath(TestDriver.getConfigurations().getString(Configurations.ONTOLOGIES_PATH));
			List<File> collectedFiles = new ArrayList<File>();
			FileUtils.collectFilesList2(ontologiesPath, collectedFiles, "*", true);
			Collections.sort(collectedFiles);
			
			try{
	            RepositoryConnection repoConn=this.repository.getConnection();
		          for(File file : collectedFiles){
		        	  System.out.println(file.getName());
		        	  repoConn.add(file, schemaContext.toString(), RDFFormat.forMIMEType(file.getName()), schemaContext); //forMIMEType finds the file format in order to read more than one rdfformats 
		          }
	            repoConn.close();
	        }catch(IOException | RDFParseException | RepositoryException ex){
	            ex.printStackTrace();
	        }
	 	}
	}
	 /************************CLASSES*************************/
	 public Collection<String> getAllClasses(){
        Set<String> allClasses=new HashSet<>();
        URI classUri=this.repository.getValueFactory().createURI(classUriAsText);
        URI typeOfUri=this.repository.getValueFactory().createURI(typeOfUriAsText);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, typeOfUri, classUri, false,schemaContext);
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
            RepositoryResult<Statement> results=repoConn.getStatements(null, typeOfUri, owlClassUri, false,schemaContext);
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
            RepositoryResult<Statement> results=repoConn.getStatements(null, subClassUri, classUri, useInference,schemaContext);
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
            RepositoryResult<Statement> results=repoConn.getStatements(classUri, subClassUri,null , useInference,schemaContext);
            while(results.hasNext()){
            	Statement tempResult = results.next();
            	if(!(tempResult.getObject() instanceof BNode) && !tempResult.getObject().stringValue().equals(startingClass)){
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
            RepositoryResult<Statement> results=repoConn.getStatements(null, disjointClassUri, classUri,false,schemaContext);
            while(results.hasNext()){
            	Statement tempResult = results.next();
            	if(!(tempResult.getObject() instanceof BNode)) disjointClasses.add(tempResult.getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
        
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(classUri, disjointClassUri,null ,false,schemaContext);
            while(results.hasNext()){
            	Statement tempResult = results.next();
            	if(!(tempResult.getObject() instanceof BNode))disjointClasses.add(tempResult.getObject().stringValue());
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
			  TupleQuery allDisjointClassesQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?items FROM <"+endpointUrl+"/ontologies> "
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
			         if(!(name instanceof BNode)) allDisjointClasses.add(name.toString());
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
            RepositoryResult<Statement> results=repoConn.getStatements(null, equivalentClassUri, classUri,false,schemaContext);
            while(results.hasNext()){
            	Statement tempResult = results.next();
            	if(!(tempResult.getObject() instanceof BNode)) equivalentClasses.add(tempResult.getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
        
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(classUri, equivalentClassUri,null ,false,schemaContext);
            while(results.hasNext()){
            	Statement tempResult = results.next();
            	if(!(tempResult.getObject() instanceof BNode)) equivalentClasses.add(tempResult.getObject().stringValue());
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
			   TupleQuery unionOfQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,"SELECT ?c FROM <"+endpointUrl+"/ontologies> "
			   		+ "WHERE {  ?c owl:unionOf ?l .  ?l rdf:rest*/rdf:first <"+startingClass+"> .}");
			   // Evaluate the first query to get results
			   TupleQueryResult unionOfResult = unionOfQuery.evaluate();
			   try {
			      while (unionOfResult.hasNext()) {
			         BindingSet bindingSet = unionOfResult.next();
			         Value name = bindingSet.getValue("c");
		            	if(!(name instanceof BNode)) unionOfClasses.add(name.toString());
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
			   TupleQuery intersectionOfQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,"SELECT ?c FROM <"+endpointUrl+"/ontologies>"
			   		+ "WHERE {  ?c owl:intersectionOf ?l .  ?l rdf:rest*/rdf:first <"+startingClass+"> .}");
			   TupleQueryResult intersectionOfResult = intersectionOfQuery.evaluate();
			   try {
			      while (intersectionOfResult.hasNext()) {
			         BindingSet bindingSet = intersectionOfResult.next();
			         Value name = bindingSet.getValue("c");
			         if(!(name instanceof BNode)) intersectionOfClasses.add(name.toString());
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
    
    public Collection<String> getClassProperties(String startingClass) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
    	Set<String> allClassProperties = new HashSet<>();
		RepositoryConnection con = this.repository.getConnection();
		try{
			  TupleQuery allClassPropertiesQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?p "
					  	+"WHERE {?p  rdfs:domain  <"+startingClass+"> .}");
				 		
			  		
			   TupleQueryResult allClassPropertiesResult = allClassPropertiesQuery.evaluate();
			   try {
			      while (allClassPropertiesResult.hasNext()) {
			         BindingSet bindingSet = allClassPropertiesResult.next();
			         Value name = bindingSet.getValue("p");
			         allClassProperties.add(name.toString());
			      }
			   }
			   finally {
				   allClassPropertiesResult.close();
			   }
			}
			finally {
			   con.close();
			}
	    return allClassProperties;
    }
    
    public Collection<String> getAllRdfObjectProperties(){
        Set<String> allRdfProperties=new HashSet<>();
        //check if property is object property
        URI rangeOfUri = this.repository.getValueFactory().createURI(rangeAsText);

        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, rangeOfUri, null, false,schemaContext);
            while(results.hasNext()){
            	Statement tempResult = results.next();
            	//if range is not rdf:resource literal means that is an object property
            	if(!tempResult.getObject().stringValue().equals(literalAsText) && !tempResult.getObject().stringValue().equals(stringAsText)){
            		allRdfProperties.add(tempResult.getSubject().stringValue());
            	}
            }
            repoConn.close();
        }catch(RepositoryException ex){
            
        }
        
        URI typeOfUri=this.repository.getValueFactory().createURI(typeOfUriAsText);
		URI rdfProperyUri=this.repository.getValueFactory().createURI(rdfProperyUriAsText);
		try{
		    RepositoryConnection repoConn=this.repository.getConnection();
		    RepositoryResult<Statement> results=repoConn.getStatements(null, typeOfUri, rdfProperyUri, false,schemaContext);
		    while(results.hasNext()){
		    	allRdfProperties.add(results.next().getSubject().stringValue());
		    }
		    repoConn.close();
		}catch(RepositoryException ex){
		    
		}
		return allRdfProperties;
    }
    
    public Collection<String> getAllRdfDatatypeProperties() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
        Set<String> allRdfProperties=new HashSet<>();
        
		RepositoryConnection con = this.repository.getConnection();
		try{
			  TupleQuery allClassPropertiesQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, 
					  "SELECT ?p "
					  	+"WHERE {?p  a  owl:DatatypeProperty .}");
				 		
			  		
			   TupleQueryResult allClassPropertiesResult = allClassPropertiesQuery.evaluate();
			   try {
			      while (allClassPropertiesResult.hasNext()) {
			         BindingSet bindingSet = allClassPropertiesResult.next();
			         Value name = bindingSet.getValue("p");
			         allRdfProperties.add(name.toString());
			      }
			   }
			   finally {
				   allClassPropertiesResult.close();
			   }
			}
			finally {
			   con.close();
			}
	return allRdfProperties;

    }
    
    public Collection<String> getAllObjectProperties() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
    	 Set<String> allObjectProperties=new HashSet<>();
         
 		RepositoryConnection con = this.repository.getConnection();
 		try{
 			  TupleQuery allClassPropertiesQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, 
 					  "SELECT ?p "
 					  	+"WHERE {?p  a  owl:ObjectProperty .}");
 				 		
 			  		
 			   TupleQueryResult allClassPropertiesResult = allClassPropertiesQuery.evaluate();
 			   try {
 			      while (allClassPropertiesResult.hasNext()) {
 			         BindingSet bindingSet = allClassPropertiesResult.next();
 			         Value name = bindingSet.getValue("p");
 			        allObjectProperties.add(name.toString());
 			      }
 			   }
 			   finally {
 				   allClassPropertiesResult.close();
 			   }
 			}
 			finally {
 			   con.close();
 			}
 	    return allObjectProperties;
    }
    
    public Collection<String> getAllDatatypeProperties() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
    	Set<String> allDatatypeProperties=new HashSet<>();
        
 		RepositoryConnection con = this.repository.getConnection();
 		try{
 			  TupleQuery allClassPropertiesQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, 
 					  "SELECT ?p "
 					  	+"WHERE {?p  a  owl:DatatypeProperty  .}");
 				 		
 			  		
 			   TupleQueryResult allClassPropertiesResult = allClassPropertiesQuery.evaluate();
 			   try {
 			      while (allClassPropertiesResult.hasNext()) {
 			         BindingSet bindingSet = allClassPropertiesResult.next();
 			         Value name = bindingSet.getValue("p");
 			        allDatatypeProperties.add(name.toString());
 			      }
 			   }
 			   finally {
 				   allClassPropertiesResult.close();
 			   }
 			}
 			finally {
 			   con.close();
 			}
 	    return allDatatypeProperties;
    }
    
    public Collection<String> getSubPropertyOf( String startingProperty, boolean useInference) {
    	
        Set<String> subProperties=new HashSet<>();
        URI subPropertyUri=this.repository.getValueFactory().createURI(this.subPropertyOfUriAsText);
        URI propertyUri=this.repository.getValueFactory().createURI(startingProperty);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, subPropertyUri, propertyUri, useInference,schemaContext);
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
    
    public Collection<String> getSuperProperties(String startingProperty, boolean useInference) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
    	Set<String> superProperties = new HashSet<>();
		RepositoryConnection con = this.repository.getConnection();
		try{
			  TupleQuery allClassPropertiesQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?p FROM <"+endpointUrl+"/ontologies>"
					  	+"WHERE {?p <"+subPropertyOfUriAsText+"> <"+startingProperty+">   .}");
			 
			   TupleQueryResult superPropertiesResult = allClassPropertiesQuery.evaluate();
			   try {
			      while (superPropertiesResult.hasNext()) {
			         BindingSet bindingSet = superPropertiesResult.next();
			         Value name = bindingSet.getValue("p");
			         superProperties.add(name.stringValue());
			      }
			   }
			   finally {
				   superPropertiesResult.close();
			   }
			}
			finally {
			   con.close();
			}
	    return superProperties;
    }
    
    public Collection<String> getDisjointProperties(String startingProperty) throws RepositoryException, QueryEvaluationException, MalformedQueryException{
    	Set<String> disjointProperties = new HashSet<>();
    	RepositoryConnection con = this.repository.getConnection();
		try{
			  TupleQuery disjointPropertiesQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?p  FROM <"+endpointUrl+"/ontologies>"
					  	+"WHERE {?p <"+disjointPropertyUriAsText+"> <"+startingProperty+">   .}");
			  
			   TupleQueryResult disjointPropertiesResult = disjointPropertiesQuery.evaluate();
			   try {
			      while (disjointPropertiesResult.hasNext()) {
			         BindingSet bindingSet = disjointPropertiesResult.next();
			         Value name = bindingSet.getValue("p");
			         disjointProperties.add(name.toString());
			      }
			   }
			   finally {
				   disjointPropertiesResult.close();
			   }
			}
			finally {
			   con.close();
			}
		
		con = this.repository.getConnection();
		try{
			  TupleQuery disjointPropertiesQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?p  FROM <"+endpointUrl+"/ontologies>"
					  	+"WHERE { <"+startingProperty+"> <"+disjointPropertyUriAsText+"> ?p  .}");
			  
			   TupleQueryResult disjointPropertiesResult = disjointPropertiesQuery.evaluate();
			   try {
			      while (disjointPropertiesResult.hasNext()) {
			         BindingSet bindingSet = disjointPropertiesResult.next();
			         Value name = bindingSet.getValue("p");
			         disjointProperties.add(name.toString());
			      }
			   }
			   finally {
				   disjointPropertiesResult.close();
			   }
			}
			finally {
			   con.close();
			}
	    return disjointProperties;

    }
    
    
    public Collection<String> getAllDisjointProperties(String startingProperty) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
    	Set<String> allDisjointProperties = new HashSet<>();
		RepositoryConnection con = this.repository.getConnection();
		try{
			  TupleQuery allDisjointPropertiesQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?items  FROM <"+endpointUrl+"/ontologies>"
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
    
    

    public Collection<String> getEquivalentProperties(String startingProperty) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
    	Set<String> equivalentProperties = new HashSet<>();
    	RepositoryConnection con = this.repository.getConnection();
		try{
			  TupleQuery equivalentPropertiesQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?p  FROM <"+endpointUrl+"/ontologies>"
					  	+"WHERE {?p <"+equivalentPropertyUriAsText+"> <"+startingProperty+">   .}");
			  
			   TupleQueryResult equivalentPropertiesResult = equivalentPropertiesQuery.evaluate();
			   try {
			      while (equivalentPropertiesResult.hasNext()) {
			         BindingSet bindingSet = equivalentPropertiesResult.next();
			         Value name = bindingSet.getValue("p");
			         equivalentProperties.add(name.toString());
			      }
			   }
			   finally {
				   equivalentPropertiesResult.close();
			   }
			}
			finally {
			   con.close();
			}
		
		con = this.repository.getConnection();
		try{
			  TupleQuery equivalentPropertiesQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?p  FROM <"+endpointUrl+"/ontologies>"
					  	+"WHERE { <"+startingProperty+"> <"+equivalentPropertyUriAsText+"> ?p  .}");
			  
			   TupleQueryResult equivalentPropertiesResult = equivalentPropertiesQuery.evaluate();
			   try {
			      while (equivalentPropertiesResult.hasNext()) {
			         BindingSet bindingSet = equivalentPropertiesResult.next();
			         Value name = bindingSet.getValue("p");
			         equivalentProperties.add(name.toString());
			      }
			   }
			   finally {
				   equivalentPropertiesResult.close();
			   }
			}
			finally {
			   con.close();
			}
	    return equivalentProperties;
    }
    
 
    public Collection<String> getFunctionalProperties(){
        Set<String> functionalProperties = new HashSet<>();
        URI functionalPropertyUri = this.repository.getValueFactory().createURI(this.functionalPropertyUriAsText);
        URI typeOfUri=this.repository.getValueFactory().createURI(typeOfUriAsText);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, typeOfUri, functionalPropertyUri,false,schemaContext);
            while(results.hasNext()){
            	functionalProperties.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
    return functionalProperties;
    }
  public Collection<String> getInverseFunctionalProperties() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
        Set<String> inverseFunctionalProperties = new HashSet<>();
        URI inverseFunctionalPropertyUri = this.repository.getValueFactory().createURI(this.inverseFunctionalPropertyUriAsText);
        URI typeOfUri=this.repository.getValueFactory().createURI(typeOfUriAsText);
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, typeOfUri, inverseFunctionalPropertyUri,false,schemaContext);
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
            RepositoryResult<Statement> results=repoConn.getStatements(null, typeOfUri, classUri, false,schemaContext);
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
            RepositoryResult<Statement> results=repoConn.getStatements(null, owlTypeOfUri, classUri, false,schemaContext);
            while(results.hasNext()){
                retInstances.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            ex.printStackTrace();
        }
        return retInstances;
    }
    
    /*************************For value transformations******************************/
  
    public Collection<String> getPredicatesOfStringObjects(String startingClass) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
     	Set<String> allStringProperties=new HashSet<>();
        URI rangeOfUri = this.repository.getValueFactory().createURI(rangeAsText);
        URI literalUri = this.repository.getValueFactory().createURI(xsdAsText+"string");
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, rangeOfUri, literalUri, false,schemaContext);
            while(results.hasNext()){
            	allStringProperties.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            
        }
	    return allStringProperties;
    }
    public Collection<String> getPredicatesOfBooleanObjects(String startingClass) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
    	Set<String> allBooleanProperties=new HashSet<>();
        URI rangeOfUri = this.repository.getValueFactory().createURI(rangeAsText);
        URI literalUri = this.repository.getValueFactory().createURI(xsdAsText+"boolean");
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, rangeOfUri, literalUri, false,schemaContext);
            while(results.hasNext()){
            	allBooleanProperties.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            
        }
	    return allBooleanProperties;
    }
    
    
    public Collection<String> getPredicatesOfDateObjects(String startingClass) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
    	Set<String> allDateProperties=new HashSet<>();
        URI rangeOfUri = this.repository.getValueFactory().createURI(rangeAsText);
        URI dateUri = this.repository.getValueFactory().createURI(xsdAsText+"date");
        URI dateTimeUri = this.repository.getValueFactory().createURI(xsdAsText+"dateTime");
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            //date
            RepositoryResult<Statement> results=repoConn.getStatements(null, rangeOfUri, dateUri, false,schemaContext);
            while(results.hasNext()){
            	allDateProperties.add(results.next().getSubject().stringValue());
            }
            //dateTime
            results=repoConn.getStatements(null, rangeOfUri, dateTimeUri, false,schemaContext);
            while(results.hasNext()){
            	allDateProperties.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            
        }
	    return allDateProperties;
    }
    
    public Collection<String> getPredicatesOfGeoLatObjects() {
    	Set<String> geoPointProperties=new HashSet<>();
        URI rangeOfUri = this.repository.getValueFactory().createURI(rangeAsText);
        URI latUri = this.repository.getValueFactory().createURI(geoPointAsText+"lat");
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            RepositoryResult<Statement> results=repoConn.getStatements(null, rangeOfUri, latUri, false,schemaContext);
            while(results.hasNext()){
            	geoPointProperties.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            
        }
	    return geoPointProperties;
    }
    
    public Collection<String> getPredicatesOfGeoLongObjects() {
    	Set<String> geoPointProperties=new HashSet<>();
        URI rangeOfUri = this.repository.getValueFactory().createURI(rangeAsText);
        URI longUri = this.repository.getValueFactory().createURI(geoPointAsText+"long");
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            
            RepositoryResult<Statement> results=repoConn.getStatements(null, rangeOfUri, longUri, false,schemaContext);
            while(results.hasNext()){
            	geoPointProperties.add(results.next().getSubject().stringValue());
            }
            repoConn.close();
        }catch(RepositoryException ex){
            
        }
	    return geoPointProperties;
    }
    
    public Collection<String> getPredicatesOfNumericObjects(){
        Set<String> allDatatypeProperties=new HashSet<>();
        URI rangeOfUri = this.repository.getValueFactory().createURI(rangeAsText);
        URI doubleUri = this.repository.getValueFactory().createURI(xsdAsText+"double");
        URI intUri = this.repository.getValueFactory().createURI(xsdAsText+"integer");
        URI floatUri = this.repository.getValueFactory().createURI(xsdAsText+"float");
        URI decimalUri = this.repository.getValueFactory().createURI(xsdAsText+"decimal");
        URI nonNegativeIntegerUri = this.repository.getValueFactory().createURI(xsdAsText+"nonNegativeInteger");
        URI negativeIntegerUri = this.repository.getValueFactory().createURI(xsdAsText+"negativeInteger");
        URI nonPositiveIntegerUri = this.repository.getValueFactory().createURI(xsdAsText+"nonPositiveInteger");
        URI positiveIntegerUri = this.repository.getValueFactory().createURI(xsdAsText+"positiveInteger");
        try{
            RepositoryConnection repoConn=this.repository.getConnection();
            //double
            RepositoryResult<Statement> results=repoConn.getStatements(null, rangeOfUri, doubleUri, false,schemaContext);
            while(results.hasNext()){
            	allDatatypeProperties.add(results.next().getSubject().stringValue());
            }
            //integer
            results=repoConn.getStatements(null, rangeOfUri, intUri, false,schemaContext);
            while(results.hasNext()){
            	allDatatypeProperties.add(results.next().getSubject().stringValue());
            }
            //float
            results=repoConn.getStatements(null, rangeOfUri, floatUri, false,schemaContext);
            while(results.hasNext()){
            	allDatatypeProperties.add(results.next().getSubject().stringValue());
            }
            //decimal
            results=repoConn.getStatements(null, rangeOfUri, decimalUri, false,schemaContext);
            while(results.hasNext()){
            	allDatatypeProperties.add(results.next().getSubject().stringValue());
            }
            //nonNegativeInteger
            results=repoConn.getStatements(null, rangeOfUri, nonNegativeIntegerUri, false,schemaContext);
            while(results.hasNext()){
            	allDatatypeProperties.add(results.next().getSubject().stringValue());
            }
            //negativeInteger 
            results=repoConn.getStatements(null, rangeOfUri, negativeIntegerUri, false,schemaContext);
            while(results.hasNext()){
            	allDatatypeProperties.add(results.next().getSubject().stringValue());
            }
            //nonPositiveInteger 
            results=repoConn.getStatements(null, rangeOfUri, nonPositiveIntegerUri, false,schemaContext);
            while(results.hasNext()){
            	allDatatypeProperties.add(results.next().getSubject().stringValue());
            }
            //positiveInteger
            results=repoConn.getStatements(null, rangeOfUri, positiveIntegerUri, false,schemaContext);
            while(results.hasNext()){
            	allDatatypeProperties.add(results.next().getSubject().stringValue());
            }
            
            repoConn.close();
        }catch(RepositoryException ex){
            
        }
        return allDatatypeProperties;
    }

}




![LDBC Logo](ldbc_logo.png)
LANCE
-----------------------------

###Description

The Linked Data Instance Matching Benchmark (LANCE), is a
benchmark for the assessment of Instance Matching techniques and
systems for Linked Data data that are accompanied by a schema.
Essentially, LANCE implements:

(i) a set of test cases based on structural, semantic and value
transformations

(ii) a gold standard documenting the matches that IM systems should
discover, and

(iii) evaluation metrics. 

LANCE accepts as input ontologies and source datasets expressed in
different formats (is able to load all RDF serialization formats that the triplestore supports
such as RDF/XML, N3, NT, TTL, etc.) and generates a target dataset and gold standard
files (in any preselected format, as well as the format specified by OAEI).

###Build

Apache Ant build tool is required. Use the following task : 

```
#to build the benchmark:
ant build

```

Result of the build process is saved to the distribution folder (dist/) : 
* lance-base.jar
* lance-base.zip
* definitions.properties
* test.properties
* readme.txt

###Install

Required dependencies for RESCAL : 
* ***Numpy >= 1.3***
* ***SciPy >= 0.7***

* ***WordNet*** - is also required for value-based transformations

Required configuration files : 

* ***test.properties*** - contains configuration parameters for configuring the benchmark driver
* ***definitions.properties*** - contains values of pre-allocated parameters used by the benchmark. Not to be modified by the regular benchmark user

###Configure

* RDF Repository configuration
  * Use RDFS rule-set

* Benchmark driver configuration. All configuration parameters are stored in properties file (test.properties)

  * ***totalTriples*** - Total triples we want to transform
  * ***triplesPerFile*** - Triples per file
  * ***workers*** - Number of workers
  * ***definitionsPath*** - Path for definitions.properties file (e.g. ./definitions.properties)
  * ***sourcePath*** - Path for source datasets (e.g. ./SourceDatasets)
  * ***generatedDataFormat*** - Generated files format (e.g. turtle) , supported: n3, turtle, n-triples
  * ***wordnetPath*** - WordNet path (e.g. C:/Program Files/WordNet/2.1/dict/) , you can use different version of WordNet
  * ***rescalRank*** - Size of rescal matrix (e.g. 10), the time increases if you increase this size
  * ***rescalSampling - Number of files we are going to use for sampling reasons
  * ***valueSeverity*** - Severity for value-based test cases (e.g. 0.3)
  * ***valueToken*** - Token to use in value test cases (e.g. a)
  * ***valueAbbreviation*** - Type of abbreviation (NDOTS = 0, SCOMMANDOT = 1, ALLDOTS = 2)
  * ***outputLanguage*** - Output language for multilingual value transformation (e.g. el)
  * ***dateFormat*** -	Date format on input dates we want to transform (e.g. yyyy-MM-dd)
  * ***newDateFormat*** - Output date format (SHORT = 3, MEDIUM = 2, LONG = 1, FULL = 0)
  * ***extractProperty*** - Number of properties we want to extract the given one for structure transformation (e.g. 3)
  * ***inferenceSubClassSubProperty*** -Boolean variable in order to use or not inference for subclass and subproperty transformations
  * ***changeURIs*** - Boolean variable in order to change the given URIs or not. We do not follow this in the case of functional and inverse functional property transformations.
  * ***newURInamespace*** - Namespace for new URIs (e.g http://www.ldbc.eu/)
  * ***transformClassInstances*** - Instances of classes we want to retrieve from database (e.g. http://dbpedia.org/ontology/Event, http://dbpedia.org/ontology/Place,http://dbpedia.org/ontology/Organisation). Separate classes with comma.If this remains empty random instances will be retrieved until totalTriples.
  * ***endpointURL*** - Endpoint URL for OWLIM (e.g. http://localhost:8080/openrdf-sesame/repositories/lance)
  * ***endpointUpdateURL*** - Endpoint update URL for OWLIM (e.g. http://localhost:8080/openrdf-sesame/repositories/lance/statements)
  * ***loadOntologies*** - Load ontologies (from the 'datasets_and_ontologies/ontologies' folder (ontologiesPath)) into database
  * ***loadReferenceDatasets*** - Load datasets (from the 'datasets_and_ontologies/datasets' folder (referenceDatasetsPath)) into database
  * ***clearDatabase*** - The benchmark can be set to clear all data from database. Note : all data will be erased from repository
  
  
  * Benchmark driver configuration. All configuration parameters are stored in properties file (definitions.properties)

  * ***transformationAllocation*** - Defines the allocation amount of transformations: VALUE , STRUCTURAL ,LOGICAL , SIMPLECOMBINATION ,COMPLEXCOMBINATION, NOTRANSFORMATION, NOTRANSFORMATION must NOT be zero,(e.g. transformationAllocation = 0.3, 0.1, 0.2, 0.0, 0.2, 0.2)
  * ***valueAllocation*** - Defines the allocation amount of value transformations: BLANKCHARSADDITION , BLANKCHARSDELETION ,RANDOMCHARSADDITION , RANDOMCHARSDELETION ,RANDOMCHARSMODIFIER, TOKENADDITION, TOKENDELETION, TOKENSHUFFLE, NAMESTYLEABBREVIATION, COUNTRYNAMEABBREVIATION, CHANGESYNONYM, CHANGEANTONYM, CHANGENUMBER, CHANGEDATEFORMAT, CHANGELANGUAGE, CHANGEBOOLEAN, CHANGEGENDERFORMAT, STEMWORD, CHANGEPOINT, NOTRANSFORMATION, NOTRANSFORMATION must NOT be zero, (e.g. valueAllocation = 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.0, 0.1)
  * ***structureAllocation*** - Defines the allocation amount of structural transformations: ADDPROPERTY, DELETEPROPERTY, EXTRACTPROPERTY, AGGREGATEPROPERTY, NOTRANSFORMATION, NOTRANSFORMATION must NOT be zero, (e.g. structureAllocation = 0.2, 0.2, 0.2, 0.2, 0.2)
  * ***semanticsAwareAllocation*** - Defines the allocation amount of semantics aware transformations: SAMEAS, SAMEASONEXISTINGINSTANCE, DIFFERENTFROM, SUBCLASSOF, EQUIVALENTCLASS, DISJOINTWITH, UNIONOF, INTERSECTIONOF, SUBPROPERTYOF, EQUIVALENTPROPERTY, DISJOINTPROPERTY, FUNCTIONALPROPERTY, INVERSEFUNCTIONALPROPERTY, NOTRANSFORMATION, NOTRANSFORMATION must NOT be zero, (e.g. semanticsAwareAllocation = 0.05, 0.05, 0.05, 0.05 ,0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.15, 0.15, 0.1, 0.1)
  * ***simpleCombinationAllocation*** - Defines the allocation amount of simple combinations: VALUE , STRUCTURE ,SEMANTICS AWARE,NOTRANSFORMATION, NOTRANSFORMATION must NOT be zero,(e.g. simpleCombinationAllocation = 0.3, 0.3, 0.3, 0.1)
  * ***complexCombinationAllocation*** - Defines the allocation amount of complex combinations: VALUE and STRUCTURE, VALUE and SEMANTICS AWARE, NOTRANSFORMATION, NOTRANSFORMATION must NOT be zero,(e.g. complexCombinationAllocation =  0.4, 0.4, 0.2)
  * ***complexCombinationForSemanticsAwareAllocation*** - Defines the allocation amount of semantics aware transformations in order to be used for complex transformations: SUBPROPERTYOF, EQUIVALENTPROPERTY, DISJOINTPROPERTY, FUNCTIONALPROPERTY, 
INVERSEFUNCTIONALPROPERTY, NOTRANSFORMATION, (e.g. complexCombinationForSemanticsAwareAllocation = 0.2 ,0.2, 0.2, 0.1, 0.1, 0.2)
###Run

```sh
java -jar lance-base.jar test.properties
```
*Note: appropriate value for java maximum heap size may be required, e.g. -Xmx8G*

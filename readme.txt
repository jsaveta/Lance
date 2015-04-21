LANCE

Description : 

-------------------------------------------------------------------------

The Linked Data Instance Matching Benchmark (LANCE), is a
benchmark for the assessment of Instance Matching techniques and
systems for Linked Data data that are accompanied by a schema.
Essentially, LANCE implements:

(i) a set of test cases based on structure, semantic and value
transformations

(ii) a gold standard documenting the matches that IM systems should
discover, and

(iii) evaluation metrics. 

LANCE accepts as input ontologies and source datasets expressed in
different formats (is able to load all RDF serialization formats that the triplestore supports
such as RDF/XML, N3, NT, TTL, etc.) and generates a target dataset and gold standard
files (in any preselected format, as well as the format specified by OAEI).

Distribution : 
-------------------------------------------------------------------------

The benchmark test driver is distributed as a jar file : lance-base.jar.

In the datasets_and_ontologies/datasets folder the user must place
the reference datasets and in the datasets_and_ontologies/ontologies
folder the ontologies that are going to be used in order to create the
target and gold standard files.

The required configuration files are: test.properties and
definitions.properties (can also be found in the jar file)

How to build the benchmark driver :
-------------------------------------------------------------------------

  Use the Ant with build.xml script. The default ant task builds the
  jar and saves it to the 'dist' folder.  e.g.  > ant build
  
How to install the benchmark driver :
-------------------------------------------------------------------------

Save the distribution jar and reference knowledge data files to a
folder of choice, then extract from both following items :

    - folder datasets_and_ontologies/ from reference knowledge data
      file - contains required ontologies and reference data

    - file test.properties - configuration parameters for running the
      benchmark, found in distribution folder (also in the benchmark
      jar file)

    - file definitions.properties - configuration parameters on the
      benchmark generator, found in distribution folder (also in the
      benchmark jar file)


Benchmark Phases : 
-------------------------------------------------------------------------

  * The Semantic Publishing Benchmark can be configured to run through these phases ordered by the sequence they should be run : 

- totalTriples: Total triples we want to transform

- triplesPerFile: Triples per file

- workers: Number of workers

- definitionsPath: Path for definitions.properties file
  (e.g. ./definitions.properties)

- sourcePath: Path for source datasets (e.g. ./SourceDatasets)

- generatedDataFormat: Generated files format (e.g. turtle) ,
  supported: n3, turtle, n-triples

- wordnetPath: the path where WordNet is located (e.g. C:/Program
 Files/WordNet/2.1/dict/), different versions of WordNet can be used

- rescalRank: Size of rescal matrix (e.g. 10), the time increases if
  you increase this size

- rescalSampling: Number of files we are going to use for sampling
  reasons

- valueSeverity: Severity for value test cases (e.g. 0.3)

- valueToken: Token to use in value test cases (e.g. a)

- valueAbbreviation: Type of abbreviation (NDOTS = 0, SCOMMANDOT = 1,
  ALLDOTS = 2)

- outputLanguage: Output language for multilingual value
  transformation (e.g. el)

- dateFormat: Date format on input dates we want to transform
  (e.g. yyyy-MM-dd)

- newDateFormat: Output date format (SHORT = 3, MEDIUM = 2, LONG = 1,
  FULL = 0)


- extractProperty: Number of properties we want to extract the given
  one for structure transformation (e.g. 3)

- inferenceSubClassSubProperty: Boolean variable in order to use or
  not inference for subclass and subproperty transformations

- changeURIs: Boolean variable in order to change the given URIs or
  not. We do not follow this in the case of functional and inverse
  functional property transformations.

- newURInamespace: Namespace for new URIs (e.g http://www.ldbc.eu/)

- transformClassInstances: Instances of classes we want to retrieve
  from database
  (e.g. http://dbpedia.org/ontology/Event,http://dbpedia.org/ontology/Place,http://dbpedia.org/ontology/Organisation). Separate
  classes with comma.
													  If this field remains empty random instances will be retrieved until totalTriples.

- endpointURL: Endpoint URL for OWLIM
  (e.g. http://localhost:8080/openrdf-sesame/repositories/lance)

- endpointUpdateURL: Endpoint update URL for OWLIM
  (e.g. http://localhost:8080/openrdf-sesame/repositories/lance/statements)

- loadOntologies: load ontologies (from the
  'datasets_and_ontologies/ontologies' folder (ontologiesPath)) into
  database

- loadReferenceDatasets: load datasets (from the
  'datasets_and_ontologies/datasets' folder (referenceDatasetsPath))
  into database

- clearDatabase: optional, the benchmark can be set to clear all data
  from database.  Note : all data will be erased from the repository
  
Each of those phases can be configured to run independently or in a
sequence by setting appropriate property value in file :
test.properties.
 
How to run the benchmark : 
-------------------------------------------------------------------------

  * Prepare and start a new RDF repository. 
  
    - Use rule-set : RDFS
    - Enable context indexing if available
    - Enable geo-spatial indexing if available
  
  * Configure the benchmark driver
  
      Edit file : test.properties, set values for :
  
  * definitions.properties

- transformationAllocation: Defines the allocation amount of
transformations VALUE , STRUCTURAL ,LOGICAL , SIMPLECOMBINATION
,COMPLEXCOMBINATION, NOTRANSFORMATION, NOTRANSFORMATION must NOT be
zero (e.g. transformationAllocation = 0.3, 0.1, 0.2, 0.0, 0.2, 0.2)

- valueAllocation: Defines the allocation amount of value
 transformations

BLANKCHARSADDITION, BLANKCHARSDELETION, RANDOMCHARSADDITION,
RANDOMCHARSDELETION, RANDOMCHARSMODIFIER, TOKENADDITION,
TOKENDELETION, TOKENSHUFFLE, NAMESTYLEABBREVIATION,
COUNTRYNAMEABBREVIATION, CHANGESYNONYM, CHANGEANTONYM, CHANGENUMBER,
CHANGEDATEFORMAT, CHANGELANGUAGE, CHANGEBOOLEAN, CHANGEGENDERFORMAT,
STEMWORD, CHANGEPOINT, NOTRANSFORMATION.

NOTRANSFORMATION: must NOT be zero (e.g. valueAllocation = 0.05, 0.05,
0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05,
0.05, 0.05, 0.05, 0.05, 0.05, 0.0, 0.1) 

- structureAllocation : Defines the allocation amount of structural
transformations 
ADDPROPERTY, DELETEPROPERTY, EXTRACTPROPERTY,
AGGREGATEPROPERTY,NOTRANSFORMATION 

NOTRANSFORMATION must NOT be zero (e.g. structureAllocation = 0.2,
0.2, 0.2, 0.2, 0.2)

- semanticsAwareAllocation: Defines the allocation amount of semantics
 aware transformations SAMEAS, SAMEASONEXISTINGINSTANCE,
 DIFFERENTFROM, SUBCLASSOF, EQUIVALENTCLASS, DISJOINTWITH, UNIONOF,
 INTERSECTIONOF, SUBPROPERTYOF, EQUIVALENTPROPERTY, DISJOINTPROPERTY,
 FUNCTIONALPROPERTY, INVERSEFUNCTIONALPROPERTY, NOTRANSFORMATION

NOTRANSFORMATION must NOT be zero e.g. semanticsAwareAllocation =
0.05, 0.05, 0.05, 0.05 ,0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.15,
0.15, 0.1, 0.1)
	
- simpleCombinationAllocation: Defines the allocation amount of simple
  combinations VALUE , STRUCTURE ,SEMANTICS AWARE,NOTRANSFORMATION
												  
NOTRANSFORMATION must NOT be zero (e.g. simpleCombinationAllocation =
0.3, 0.3, 0.3, 0.1)

- complexCombinationAllocation: Defines the allocation amount of
complex combinations VALUE and STRUCTURE,VALUE and SEMANTICS
AWARE,NOTRANSFORMATION

NOTRANSFORMATION must NOT be zero (e.g. complexCombinationAllocation =
0.4, 0.4, 0.2)

- complexCombinationForSemanticsAwareAllocation: Defines the allocation amount of semantics aware transformations in order
to be used for complex transformations (SUBPROPERTYOF, EQUIVALENTPROPERTY, DISJOINTPROPERTY, FUNCTIONALPROPERTY, 
INVERSEFUNCTIONALPROPERTY, NOTRANSFORMATION)
NOTRANSFORMATION must NOT be zero (e.g. complexCombinationForSemanticsAwareAllocation = 0.2 ,0.2, 0.2, 0.1, 0.1, 0.2)

Sample definitions.properties file can be found in the distribution
jar file.

  * Example benchmark run command : 
  	  java -jar lance-base.jar test.properties
  	  Note: appropriate value for java maximum heap size may be required, e.g. -Xmx8G

RESCAL:
-------------------------------------------------------------------------

The required dependencies for RESCAL are Numpy >= 1.3, SciPy >= 0.7 and Python

LANCE generates : n3, turtle, n-triples

The semantics-aware transformations that are done when complex transformations are chosen refer to class changes.

Supported output languages for "Change Language" transformation (Input language: English):
	AFRIKAANS = af
	ALBANIAN = sq
	ARABIC = ar
	ARMENIAN = hy
	AZERBAIJANI = az
	BASQUE = eu
	BELARUSIAN = be
	BENGALI = bn
	BULGARIAN = bg
	CATALAN = ca
	CHINESE = zh-CN
	CROATIAN = hr
	CZECH = cs
	DANISH = da
	DUTCH = nl
	ENGLISH = en
	ESTONIAN = et
	FILIPINO = tl
	FINNISH = fi
	FRENCH = fr
	GALICIAN = gl
	GEORGIAN = ka
	GERMAN = de
	GREEK = el
	GUJARATI = gu
	HAITIAN_CREOLE = ht
	HEBREW = iw
	HINDI = hi
	HUNGARIAN = hu
	ICELANDIC = is
	INDONESIAN = id
	IRISH = ga
	ITALIAN = it
	JAPANESE = ja
	KANNADA = kn
	KOREAN = ko
	LATIN = la
	LATVIAN = lv
	LITHUANIAN = lt
	MACEDONIAN = mk
	MALAY = ms
	MALTESE = mt
	NORWEGIAN = no
	PERSIAN = fa
	POLISH = pl
	PORTUGUESE = pt
	ROMANIAN = ro
	RUSSIAN = ru
	SERBIAN = sr
	SLOVAK = sk
	SLOVENIAN = sl
	SPANISH = es
	SWAHILI = sw
	SWEDISH = sv
	TAMIL = ta
	TELUGU = te
	THAI = th
	TURKISH = tr
	UKRAINIAN = uk
	URDU = ur
	VIETNAMESE = vi
	WELSH = cy
	YIDDISH = yi
	CHINESE_SIMPLIFIED = zh-CN
	CHINESE_TRADITIONAL = zh-TW

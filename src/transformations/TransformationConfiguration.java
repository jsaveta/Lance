package transformations;


import generators.data.AbstractAsynchronousWorker;
import transformations.value.BlankCharsAddition;
import transformations.value.BlankCharsDeletion;
import transformations.value.ChangeAntonym;
import transformations.value.ChangeBooleanValue;
import transformations.value.ChangeDateFormat;
import transformations.value.ChangeGenderFormat;
import transformations.value.ChangeGeoLat;
import transformations.value.ChangeGeoLong;
import transformations.value.ChangeLanguage;
import transformations.value.ChangeNumber;
import transformations.value.CountryNameAbbreviation;
import transformations.value.StemWord;
import transformations.value.NameStyleAbbreviation;
import transformations.value.RandomCharsAddition;
import transformations.value.RandomCharsDeletion;
import transformations.value.RandomCharsModifier;
import transformations.value.ChangeSynonym;
import transformations.value.TokenAddition;
import transformations.value.TokenDeletion;
import transformations.value.TokenShuffle;
import transformations.semanticsAware.DifferentFrom;
import transformations.semanticsAware.DisjointProperty;
import transformations.semanticsAware.DisjointWith;
import transformations.semanticsAware.EquivalentClass;
import transformations.semanticsAware.EquivalentProperty;
import transformations.semanticsAware.FunctionalProperty;
import transformations.semanticsAware.IntersectionOf;
import transformations.semanticsAware.InverseFunctionalProperty;
import transformations.semanticsAware.SameAs;
import transformations.semanticsAware.SameAsOnExistingInstances;
import transformations.semanticsAware.SubClassOf;
import transformations.semanticsAware.SubPropertyOf;
import transformations.semanticsAware.UnionOf;
import transformations.structure.AddProperty;
import transformations.structure.AggregateProperties;
//import transformations.semanticsAware.CWDifferentFrom;
//import transformations.semanticsAware.CWSameAs;
//import transformations.semanticsAware.DisjointProperty;
//import transformations.semanticsAware.DisjointWith;
//import transformations.semanticsAware.EquivalentClass;
//import transformations.semanticsAware.EquivalentProperty;
//import transformations.semanticsAware.FunctionalProperty;
//import transformations.semanticsAware.IntersectionOf;
//import transformations.semanticsAware.InverseFunctionalProperty;
//import transformations.semanticsAware.OneOf;
//import transformations.semanticsAware.SameAsOnExistingCW;
//import transformations.semanticsAware.SubClassOf;
//import transformations.semanticsAware.SubPropertyOf;
//import transformations.semanticsAware.UnionOf;
//import transformations.structure.AddProperty;
//import transformations.structure.AggregateProperties;
import transformations.structure.DeleteProperty;
//import transformations.structure.ExtractProperty;
import transformations.structure.ExtractProperty;


public class TransformationConfiguration {
	
	//value transformations
	public static RandomCharsModifier substituteRANDOMCHARS(double severity){
		return new RandomCharsModifier(severity);
	}
	
	public static BlankCharsDeletion deleteRANDOMBLANKS(double severity){
		return new BlankCharsDeletion(severity);
	}

	public static BlankCharsAddition addRANDOMBLANKS(double severity){
		return new BlankCharsAddition(severity);
	}

	public static RandomCharsDeletion deleteRANDOMCHARS(double severity){
		return new RandomCharsDeletion(severity);
	}

	public static RandomCharsAddition addRANDOMCHARS(double severity){
		return new RandomCharsAddition(severity);
	}

	public static TokenShuffle shuffleTOKENS(String splitter, double severity){
		return new TokenShuffle(splitter,severity);
	}

	public static TokenDeletion deleteTOKENS(String splitter, double severity){
		return new TokenDeletion(splitter,severity);
	}
	
	public static TokenAddition addTOKENS(String splitter, double severity){
		return new TokenAddition(splitter,severity);
	}
	
	public static ChangeSynonym changeSYNONYMS(String wndict, double severity){
		return new ChangeSynonym(wndict, severity);
	}
	public static ChangeAntonym changeANTONYM(String wndict, double severity){
		return new ChangeAntonym(wndict, severity);
	}

	public static ChangeDateFormat dateFORMAT(String sourceFormat, int format){
		return new ChangeDateFormat(sourceFormat, format);
	}
	
	public static ChangeGenderFormat genderFORMAT(){
		return new ChangeGenderFormat();
	}
	
	public static ChangeNumber numberFORMAT(int add, double severity){
		return new ChangeNumber(add, severity);
	}
	
	public static NameStyleAbbreviation abbreviateNAME(int format){
		return new NameStyleAbbreviation(format);
	}
	public static CountryNameAbbreviation abbreviateCOUNTRY(){
		return new CountryNameAbbreviation();
	}
	public static ChangeLanguage changeLANGUAGE(){
		return new ChangeLanguage();
	}
	
	public static ChangeBooleanValue changeBOOLEAN(){
		return new ChangeBooleanValue();
	}
	
	public static StemWord STEMWORD(){
		return new StemWord();
	}
	
	public static ChangeGeoLat CHANGELAT(){
		return new ChangeGeoLat();
	}
	public static ChangeGeoLong CHANGELONG(){
		return new ChangeGeoLong();
	}
	
	//structure transformations
	public static DeleteProperty deletePROPERTY(){
		return new DeleteProperty();
	}
	public static AddProperty addPROPERTY(){
		return new AddProperty();
	}
	public static ExtractProperty extractPROPERTY(int piecies){
		return new ExtractProperty(piecies);
	}
	public static AggregateProperties aggregatePROPERTIES(AbstractAsynchronousWorker worker){
		return new AggregateProperties(worker);
	}

	//logical transformations
	public static SameAs SAMEAS(AbstractAsynchronousWorker worker){
		return new SameAs(worker);
	}
	public static DifferentFrom DIFFERENTFROM(AbstractAsynchronousWorker worker){
		return new DifferentFrom(worker);
	}
	public static SameAsOnExistingInstances SAMEASONEXISTINGINSTANCES(AbstractAsynchronousWorker worker){
		return new SameAsOnExistingInstances(worker);
	}
	public static SubClassOf SUBCLASSOF(AbstractAsynchronousWorker worker){
		return new SubClassOf(worker);
	}
	public static EquivalentClass EQUIVALENTCLASS(AbstractAsynchronousWorker worker){
		return new EquivalentClass(worker);
	}
	public static EquivalentProperty EQUIVALENTPROPERTY(AbstractAsynchronousWorker worker){
		return new EquivalentProperty(worker);
	}	
	public static DisjointWith DISJOINTWITH(AbstractAsynchronousWorker worker){
		return new DisjointWith(worker);
	}
	public static SubPropertyOf SUBPROPERTY(AbstractAsynchronousWorker worker){
		return new SubPropertyOf(worker);
	}

	public static DisjointProperty DISJOINTPROPERTY(AbstractAsynchronousWorker worker){
		return new DisjointProperty(worker);
	}
	
	public static FunctionalProperty FUNCTIONALPROPERTY(AbstractAsynchronousWorker worker){
		return new FunctionalProperty(worker);
	}
	
	public static InverseFunctionalProperty INVERSEFUNCTIONALPROPERTY(AbstractAsynchronousWorker worker){
		return new InverseFunctionalProperty(worker);
	}

	public static IntersectionOf INTERSECTIONOF(AbstractAsynchronousWorker worker){
		return new IntersectionOf(worker);
	}
	public static UnionOf UNIONOF(AbstractAsynchronousWorker worker){
		return new UnionOf(worker);
	}
	
	
	
	
	
	
}

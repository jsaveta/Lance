package transformations.value;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.tartarus.snowball.ext.EnglishStemmer;

import transformations.DataValueTransformation;
import transformations.InvalidTransformation;

public class StemWord implements DataValueTransformation{

	private String splitter;
		
	public StemWord(){
		this.splitter = "[\\s|\n|\t]";
	}
	/*  1.Danish-DanishStemmer
		2.Dutch-DutchStemmer
		3.English-EnglishStemmer
		4.Finnish-FinnishStemmer
		5.French-FrenchStemmer
		6.German-GermanStemmer
		7.Hungarian-HungarianStemmer
		8.Italian-ItalianStemmer
		9.Norwegian-NorwegianStemmer
		10.Portuguese-PortugueseStemmer
		11.Romanian-RomanianStemmer
		12.Russian-RussianStemmer
		13.Spanish-SpanishStemmer
		14.Swedish-SwedishStemmer
		15.Turkish-TurkishStemmer*/
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		//System.out.println("StemWord");
		String f = (String)arg;
		
		if(arg instanceof String){
			String[] tokens = f.split(this.splitter);
			f = "";
			for(int i = 0; i < tokens.length; i++){
				String syn = tokens[i];
					//System.out.println("syn :" + syn);
					//PorterStemmer stemmer = new PorterStemmer();
					EnglishStemmer stemmer = new EnglishStemmer();
					stemmer.setCurrent(syn);
					if(stemmer.stem())
					{
						 syn = stemmer.getCurrent();
						 //System.out.println("syn transf: " + syn);
					}
				f += syn + " ";
			}
			f = f.trim();
		}else{
			try {
				throw new InvalidTransformation();
			} catch (InvalidTransformation e) {
				e.printStackTrace();
			}finally{
				return arg;
			}
		}
		//System.out.println("f : " + f.toString());
		return f;
	}

	@Override
	public String print() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}  
	
	
}


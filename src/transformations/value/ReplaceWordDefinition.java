package transformations.value;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import properties.Configurations;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.PropertyNames;
import transformations.DataValueTransformation;
import transformations.InvalidTransformation;

public class ReplaceWordDefinition implements DataValueTransformation{

	private WordNetDatabase database = WordNetDatabase.getFileInstance();
	private String splitter;
	private double severity;
	private final Configurations configuration = new Configurations();
		
	public ReplaceWordDefinition(double severity){
		this.splitter = "[\\s|\n|\t]";
		this.severity = severity;
	}

	@SuppressWarnings("finally")
	public Object execute(Object arg) {
		String f = (String)arg;
		if(arg instanceof String){
			System.setProperty(PropertyNames.DATABASE_DIRECTORY, configuration.getString(Configurations.WORDNET_PATH));
			WordNetDatabase database = WordNetDatabase.getFileInstance(); 
			
			String[] tokens = f.split(this.splitter);
			f = "";
			Random coin = new Random();
				for(int i = 0; i < tokens.length; i++){
					String syn = tokens[i];
					Synset[] synset_list = database.getSynsets(syn);
					//System.out.println("\n\n** Process word: " + syn);
					
					if(coin.nextDouble() <= this.severity){
						for (int k = 0; k < synset_list.length; k++)
						{
							syn = synset_list[k].getDefinition();
							System.out.println(": " + synset_list[k].getDefinition());
						}
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
		return f;
	}

	
	public List<Synset> getSynsets(String word) {
		return Arrays.asList(database.getSynsets(word));
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

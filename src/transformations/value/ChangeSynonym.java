
package transformations.value;

import transformations.DataValueTransformation;
import transformations.InvalidTransformation;

import java.util.Random;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;

/**
 * @author Alfio Ferrara, Universita` degli Studi di Milano
 * @date 19/mag/2010
 * 
 * Use WordNet in order to change words with WN synonyms (if they exists)
 * 
 */
public class ChangeSynonym  implements DataValueTransformation {
	private String splitter;
	private double severity;
	private String wn;
	
	public ChangeSynonym(String wndict, double severity){
		this.splitter = "[\\s|\n|\t]";
		this.severity = severity;
		this.wn = wndict;
	}

	public String print(){
		String name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1);
		return name + "\t" + this.severity;
	}

	/* (non-Javadoc)
	 * @see it.unimi.dico.islab.iimb.transfom.Transformation#execute(java.lang.Object)
	 */
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		String f = (String)arg;
		
		if(arg instanceof String){
			System.setProperty("wordnet.database.dir", this.wn);
			WordNetDatabase database = WordNetDatabase.getFileInstance(); 
			String[] tokens = f.split(this.splitter);
			f = "";
			Random coin = new Random();
			for(int i = 0; i < tokens.length; i++){
				String syn = tokens[i];
				if(coin.nextDouble() <= this.severity){
					Synset[] syns = database.getSynsets(syn);
					for(int j = 0; j < syns.length; j++){
						Synset s = syns[j];
						for(String nstr: s.getWordForms()){
							if(!(syn.equals(nstr))){
								syn = nstr;
								break;
							}
						}
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

	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}
}

package transformations.value;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import transformations.DataValueTransformation;
import transformations.InvalidTransformation;


/**
 * @author Alfio Ferrara, Universita` degli Studi di Milano
 * @date 19/mag/2010
 * Changes a string as if it was a name of form First Second Surname into the following supported formats:
 * - NDOTS => F.S. Surname
 * - SCOMMANDOT => Surname, F.S.
 * - ALLDOTS => F.S. S.
 */
public class NameStyleAbbreviation implements DataValueTransformation {
	
	public static int NDOTS = 0;
	public static int SCOMMANDOT = 1;
	public static int ALLDOTS = 2;
	
	private int format;
	
	private int surnames;
	
	/*
	 * surnames is the number of surnames that is foreseen
	 */
	public NameStyleAbbreviation(int format){
		this.format = format;
	}

	public String print(){
		String name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1);
		return name + "\t" + this.surnames + "\t" + this.format;
	}

	/* (non-Javadoc)
	 * @see it.unimi.dico.islab.iimb.transfom.Transformation#execute(java.lang.Object)
	 */
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		String f = (String)arg;
		while(f.startsWith(" ")) f = f.substring(1, f.length());
		if(arg instanceof String){
			//Do the job
			String[] tokens = f.split(" ");
			if(tokens.length > 1){
				String buffer = "";
				if(this.format == NDOTS){
					buffer = this.ndots(buffer, tokens);
				}else if(this.format == SCOMMANDOT){
					buffer = this.scommadots(buffer, tokens);
				}else if(this.format == ALLDOTS){
					buffer = this.alldots(buffer, tokens);
				}else{
					buffer = this.ndots(buffer, tokens);
				}
				f = buffer;
			}
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
	
	private String ndots(String buffer, String[] tokens){
		int stop = 1;
		if(this.surnames <= tokens.length){
			stop = this.surnames;
		}
		for(int i = 0; i < tokens.length - stop; i++){
			buffer += tokens[i].charAt(0);
			buffer += ".";
			if(i == tokens.length - stop - 1){
				buffer += " ";
			}
		}
		while(stop > 0){
			buffer += tokens[tokens.length - stop--] + " ";
		}
		return buffer.trim();
	}

	private String scommadots(String buffer, String[] tokens){
		int stop = 1;
		if(this.surnames <= tokens.length){
			stop = this.surnames;
		}
		for(int i = 0; i < tokens.length - stop; i++){
			buffer += tokens[i].charAt(0);
			buffer += ".";
		}
		String surnames = "";
		while(stop > 0){
			surnames += tokens[tokens.length - stop--] + " ";
		}
		buffer = surnames.trim() + ", " + buffer;
		return buffer;
	}

	private String alldots(String buffer, String[] tokens){
		for(int i = 0; i < tokens.length; i++){
			buffer += tokens[i].charAt(0);
			buffer += ".";
			if(i != tokens.length - 1){
				buffer += " ";
			}
		}
		return buffer;
	}

	/* (non-Javadoc)
	 * @see transformations.Transformation#executeStatement(org.openrdf.model.Statement)
	 */
	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}

}

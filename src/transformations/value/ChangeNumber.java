package transformations.value;


import java.util.Random;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import transformations.DataValueTransformation;
import transformations.InvalidTransformation;

/**
 * @author Alfio Ferrara, Universita` degli Studi di Milano
 * @date 19/mag/2010
 * 
 * This takes a String and changes numbers in it with randomly generated numbers
 * If severity check is passed also the specified number of digits are added to the string
 * 
 */
public class ChangeNumber implements DataValueTransformation {
	
	private int digittoadd;
	private double severity;

	public ChangeNumber(int digittoadd, double severity){
		this.digittoadd = digittoadd;
		this.severity = severity;
	}

	public String print(){
		String name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1);
		return name + "\t" + this.severity + "\t" + this.digittoadd;
	}

	/* (non-Javadoc)
	 * @see it.unimi.dico.islab.iimb.transfom.Transformation#execute(java.lang.Object)
	 */
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		String f = (String)arg;
		if(arg instanceof String){
			//Do the job
			Random coin = new Random();
			String buffer = "";
			for(char c: f.toCharArray()){
				if(coin.nextDouble() <= this.severity && Character.isDigit(c)){
					Integer i = coin.nextInt(10);
					buffer += i.toString();
				}
				else{
					buffer += c;
				}
			}
			String prefix = "";
			String ending = "";
			if(coin.nextDouble() <= this.severity){
				for(int i = 0; i < this.digittoadd; i++){
					Integer add = coin.nextInt(10);
					prefix += add.toString();
				}
			}
			if(coin.nextDouble() <= this.severity){
				for(int i = 0; i < this.digittoadd; i++){
					Integer add = coin.nextInt(10);
					ending += add.toString();
				}
			}
			f = prefix + buffer + ending;
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

	/* (non-Javadoc)
	 * @see transformations.Transformation#executeStatement(org.openrdf.model.Statement)
	 */
	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}

}

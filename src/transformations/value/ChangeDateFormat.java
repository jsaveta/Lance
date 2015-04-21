package transformations.value;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import transformations.DataValueTransformation;

/**
 * @author Alfio Ferrara, Universita` degli Studi di Milano
 * @date 19/mag/2010
 * 
 * This takes a Date as input and returns a string representing the date according to various formats
 * 
 */
public class ChangeDateFormat implements DataValueTransformation {
	//private static ValueFactory sesameValueFactory = ValueFactoryImpl.getInstance();
	private int format; //SHORT = 3 /MEDIUM = 2 /LONG = 1 /FULL = 0 ...
	private DateFormat sourceFormat;

	public ChangeDateFormat(String sourceFormat, int format){
		this.format = format;
		this.sourceFormat = new SimpleDateFormat(sourceFormat, Locale.US);
	}

	public String print(){
		String name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1);
		return name + "\t" + format;
	}

	/* (non-Javadoc)
	 * @see it.unimi.dico.islab.iimb.transfom.Transformation#execute(java.lang.Object)
	 */
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		Locale.setDefault(Locale.ENGLISH);
		String f = arg.toString();
		try {
			Date d = this.sourceFormat.parse(f);
			DateFormat df = DateFormat.getDateInstance(this.format);
			f = df.format(d);
		} catch (ParseException e) {
		} finally{
			return f;
		}
	}

	/* (non-Javadoc)
	 * @see transformations.Transformation#executeStatement(org.openrdf.model.Statement)
	 */
	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}
	
//	 public static void main(String args[]) {
//	        // Create Date object.
//	        Date date = new Date();
//	        // Specify the desired date format
//	        String DATE_FORMAT = "yyyy/MM/dd";
//	        // Create object of SimpleDateFormat and pass the desired date format.
//	        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
//	        /*
//	         * Use format method of SimpleDateFormat class to format the date.
//	         */
//	        System.out.println("Today is " + sdf.format(date));
//	        Object arg = sesameValueFactory.createLiteral(sdf.format(date),XMLSchema.DATETIME);
//	        System.out.println("arg " + arg.toString());
//	        
//	        Locale.setDefault(Locale.ENGLISH);
//			String f = arg.toString();
//			DateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//			try {
//				Date d = sourceFormat.parse(f);
//				DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
//				f = df.format(d);
//			} catch (ParseException e) {
//			} 
//			
//			  System.out.println("f " + f.toString());
//	 }
}

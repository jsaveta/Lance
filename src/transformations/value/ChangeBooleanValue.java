package transformations.value;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;

import transformations.InvalidTransformation;
import transformations.Transformation;

public class ChangeBooleanValue implements Transformation {
	private static ValueFactory sesameValueFactory = ValueFactoryImpl.getInstance();
	public ChangeBooleanValue(){}
	
	public String print(){
		String name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1);
		return name;
	}

	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		String f = (String)arg;
		if(arg instanceof String){
			if(f.equals("false")){
				f = "true";
				return sesameValueFactory.createLiteral(f,XMLSchema.BOOLEAN);
			}else if(f.equals("true")){
				f = "false";
				return sesameValueFactory.createLiteral(f,XMLSchema.BOOLEAN);
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

	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}
//	public static void main(String[] args) throws Exception {
//		ValueFactory sesameValueFactory = ValueFactoryImpl.getInstance();
//		Object arg = sesameValueFactory.createLiteral("false",XMLSchema.BOOLEAN);
//		Value v = (Value)arg;
//		String str = v.stringValue();
//		System.out.println(str); 
//
//	}
}

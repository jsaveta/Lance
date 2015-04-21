package transformations.structure;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;

import transformations.InvalidTransformation;
import transformations.Transformation;

public class ExtractProperty  implements Transformation{
		private int N;
	    private static ValueFactory sesameValueFactory = ValueFactoryImpl.getInstance();	
	    
		public ExtractProperty(int N){			
			this.N = N;		
		}
		@SuppressWarnings("finally")
		public Model executeStatement(Statement arg) { 
			Model model = new LinkedHashModel();
			
		    if(arg instanceof Statement){
				if(arg.getObject() instanceof Literal){
		    		String s = arg.getObject().stringValue();
			    	int base = s.length() / N;
				    int remainder = s.length() % N;
		
				    String[] parts = new String[N];
				    for (int i = 0; i < N; i++) {
				        int length = base + (i < remainder ? 1 : 0);
				        parts[i] = s.substring(0, length);
				        s = s.substring(length);
				        
				        URI predicate = sesameValueFactory.createURI((arg.getPredicate().stringValue() + Integer.toString(i)));
						Value object = sesameValueFactory.createLiteral(parts[i]);
						model.add(arg.getSubject(), predicate ,object,arg.getContext());
						
				    }
		    	}
		    	//else{ model.add(st);}
		    }
			else{
				try {
					throw new InvalidTransformation();
				} catch (InvalidTransformation e) {
					e.printStackTrace();
				}finally{
					return model;
				}
			}
			return model;
		}

		@Override
		public String print() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object execute(Object arg) {
			// TODO Auto-generated method stub
			return null;
		}
	}



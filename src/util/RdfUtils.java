package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Utility class for configuring the POST http request with different serialization content types.
 */
public class RdfUtils {
	
	private static final int READ_BUFFER_SIZE_BYTES = 128 * 1024;
	
	public static final String CONTENT_TYPE_NQUADS = "application/n-quads";
	public static final String CONTENT_TYPE_SESAME_NQUADS = "text/x-nquads";
	public static final String CONTENT_TYPE_TRIG = "application/x-trig";
	public static final String CONTENT_TYPE_TURTLE = "application/x-turtle";

	public static void postStatements(String endpoint, String contentType, InputStream input) throws IOException {
		
		URL url = new URL(endpoint);
		HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
		httpUrlConnection.setDefaultUseCaches(false);
		httpUrlConnection.setUseCaches(false);
		httpUrlConnection.setDoOutput(input != null);

		httpUrlConnection.setRequestMethod("POST");
		httpUrlConnection.setRequestProperty("Content-Type", contentType);

		if(input != null) {
			OutputStream outStream = httpUrlConnection.getOutputStream();
			
			try {
				int b; 
				byte[] buffer = new byte[READ_BUFFER_SIZE_BYTES];
				while((b = input.read(buffer)) >= 0) {
					outStream.write(buffer, 0, b);
				}
				outStream.flush();
			}
			finally {
				input.close();
				outStream.close();
			}
		}
		
		int code = httpUrlConnection.getResponseCode();
		if (code < 200 || code >= 300) {
			throw new IOException("Posting statements received error code : " + code + " from server.");
		}
		
		httpUrlConnection.getInputStream().close();
	}
	
	/**
	 * Returns content type of file format
	 * TriG, TriX, N-Triples, N-Quads, N3, RDF/XML, RDF/JSON, Turtle
	 * xml, n3, turtle, nt, pretty-xml, trix are built in. + None
	 */
	public static String findContentType(String type){
		if( type.toLowerCase().equals("n-triples")) {
			return "nt";
		}
//		if( type.toLowerCase().equals("trig")) {
//			return RdfUtils.CONTENT_TYPE_SESAME_NQUADS;
//		}
//		else if( type.toLowerCase().equals("trix")) {
//			return "trix";
//		}
//		else if( type.toLowerCase().equals("n-quads")) {
//			return RdfUtils.CONTENT_TYPE_NQUADS;
//		}
//		else if( type.toLowerCase().equals("rdf/xml")) {
//			return "application/rdf+xml";
//		}
		else{ //turtle, n3, 
			return type.toLowerCase();	
		}
	}

}

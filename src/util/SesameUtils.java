package util;

import java.io.File;

import org.openrdf.rio.RDFFormat;

/**
 * Utility class for providing functionality specific to the Sesame RDF Framework
 *
 */
public class SesameUtils {
	public static RDFFormat parseRdfFormat(String serializationFormat) {
		RDFFormat rdfFormat = RDFFormat.NQUADS;
		
		if (serializationFormat.equalsIgnoreCase("BinaryRDF")) {
			rdfFormat = RDFFormat.BINARY;
		} else if (serializationFormat.equalsIgnoreCase("TriG")) {
			rdfFormat = RDFFormat.TRIG;
		} else if (serializationFormat.equalsIgnoreCase("TriX")) {
			rdfFormat = RDFFormat.TRIX;
		} else if (serializationFormat.equalsIgnoreCase("N-Triples")) {
			rdfFormat = RDFFormat.NTRIPLES;
		} else if (serializationFormat.equalsIgnoreCase("N-Quads")) {	
			rdfFormat = RDFFormat.NQUADS;
		} else if (serializationFormat.equalsIgnoreCase("N3")) {
			rdfFormat = RDFFormat.N3;
		} else if (serializationFormat.equalsIgnoreCase("RDF/XML")) {
			rdfFormat = RDFFormat.RDFXML;
		} else if (serializationFormat.equalsIgnoreCase("RDF/JSON")) {
			rdfFormat = RDFFormat.RDFJSON;
		} else if (serializationFormat.equalsIgnoreCase("Turtle")) {
			rdfFormat = RDFFormat.TURTLE;
		} else {
			throw new IllegalArgumentException("Warning : unknown serialization format : " + serializationFormat + ", defaulting to N-Quads");
		}		
		
		return rdfFormat;
	}
	
	public static String findContentType(File file){
		if( file.getName().endsWith(".trig")) {
			return RdfUtils.CONTENT_TYPE_SESAME_NQUADS;
		}
		else if( file.getName().endsWith(".trix")) {
			return "application/trix+xml";
		}
		else if( file.getName().endsWith(".nt")) {
			return "text/plain";
		}
		else if( file.getName().endsWith(".nq")) {
			return RdfUtils.CONTENT_TYPE_NQUADS;
		}
		else if( file.getName().endsWith(".n3")) {
			return "text/n3";
		}
		else if( file.getName().endsWith(".rdfs") || file.getName().endsWith(".owl") ||file.getName().endsWith(".rdf")) {
			return "application/rdf+xml";
		}
		else if( file.getName().endsWith(".json")) {
			return "application/rdf+json";
		}
		else if( file.getName().endsWith(".ttl")) {
		   return RdfUtils.CONTENT_TYPE_TURTLE;
		}
		return RdfUtils.CONTENT_TYPE_TURTLE;
	}
}

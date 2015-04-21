package util;

import java.util.TreeSet;

/**
 * @author jsaveta, Foundation for Research and Technology-Hellas (FORTH)
 *
 * @date Mar 30, 2015
 */
public class StructureUtil {
	public static TreeSet<String> intersection( TreeSet<String> AL1, TreeSet<String> AL2){   
		TreeSet<String> returnTreeSet = new TreeSet<String>();
	    for(String test : AL1)
	    {
	        if(!returnTreeSet.contains(test))
	        {
	            if(AL2.contains(test))
	            {
	            	returnTreeSet.add(test);
	            }
	        }
	    }
	    return returnTreeSet;
	}
}


package transformations.value;

import java.util.Random;

/**
 * @author Alfio Ferrara, Universita` degli Studi di Milano
 * @date 18/mag/2010
 */
public class Utils {
	
	public static char pickChar(){
		String chars = "ABCDEFGHILMNOPGRSTUVZKJXYW1234567890abcdefghilmnopqrstuvzjkwxy?!";
		Random g = new Random();
		int index = g.nextInt(chars.length());
		return chars.charAt(index);
	}

}

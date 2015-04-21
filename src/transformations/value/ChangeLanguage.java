package transformations.value;

import main.TestDriver;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import com.gtranslate.Language;
import com.gtranslate.Translator;

import properties.Configurations;
import transformations.DataValueTransformation;
import transformations.InvalidTransformation;

/**
 * @author jsaveta, Foundation for Research and Technology-Hellas (FORTH)
 * https://code.google.com/p/java-google-translate-text-to-speech/
 * 
 * @date Dec 19, 2014
 */
public class ChangeLanguage implements DataValueTransformation{
	public ChangeLanguage(){}
	
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg){
		String input = (String)arg;
		String translated = "";
		if(arg instanceof String){
		Translator translate = Translator.getInstance();
		String outputLanguage = TestDriver.getConfigurations().getString(Configurations.OUTPUT_LANGUAGE).toLowerCase();
		translated = translate.translate(input, Language.ENGLISH, outputLanguage);
		}else{
			try {
				throw new InvalidTransformation();
			} catch (InvalidTransformation e) {
				e.printStackTrace();
			}finally{
				return input;
			}
		}
		return translated; 
		
	}	
	@Override
	public String print() {
		return null;
	}

	@Override
	public Model executeStatement(Statement statement) {
		return null;
	}
	
//	public static void main(String[] args) throws Exception {
//		ValueFactory sesameValueFactory = ValueFactoryImpl.getInstance();
//		Translator translate = Translator.getInstance();
//		
////		String prefix = translate.detect("I am a bus");
////		System.out.println(prefix); //en
//		
//		String arg = sesameValueFactory.createLiteral("I am programmer").stringValue();
//		String text = translate.translate(arg, Language.ENGLISH, "el");
//		System.out.println(text); 
//
//	}
}

/*		
AFRIKAANS = af
ALBANIAN = sq
ARABIC = ar
ARMENIAN = hy
AZERBAIJANI = az
BASQUE = eu
BELARUSIAN = be
BENGALI = bn
BULGARIAN = bg
CATALAN = ca
CHINESE = zh-CN
CROATIAN = hr
CZECH = cs
DANISH = da
DUTCH = nl
ENGLISH = en
ESTONIAN = et
FILIPINO = tl
FINNISH = fi
FRENCH = fr
GALICIAN = gl
GEORGIAN = ka
GERMAN = de
GREEK = el
GUJARATI = gu
HAITIAN_CREOLE = ht
HEBREW = iw
HINDI = hi
HUNGARIAN = hu
ICELANDIC = is
INDONESIAN = id
IRISH = ga
ITALIAN = it
JAPANESE = ja
KANNADA = kn
KOREAN = ko
LATIN = la
LATVIAN = lv
LITHUANIAN = lt
MACEDONIAN = mk
MALAY = ms
MALTESE = mt
NORWEGIAN = no
PERSIAN = fa
POLISH = pl
PORTUGUESE = pt
ROMANIAN = ro
RUSSIAN = ru
SERBIAN = sr
SLOVAK = sk
SLOVENIAN = sl
SPANISH = es
SWAHILI = sw
SWEDISH = sv
TAMIL = ta
TELUGU = te
THAI = th
TURKISH = tr
UKRAINIAN = uk
URDU = ur
VIETNAMESE = vi
WELSH = cy
YIDDISH = yi
CHINESE_SIMPLIFIED = zh-CN
CHINESE_TRADITIONAL = zh-TW
*/


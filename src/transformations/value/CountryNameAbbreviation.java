
package transformations.value;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import transformations.DataValueTransformation;
import transformations.InvalidTransformation;

public class CountryNameAbbreviation implements DataValueTransformation {

	private int surnames;
	private Map<String, String> countryAbbreviation;
	/*
	 * surnames is the number of surnames that is foreseen
	 */
	public CountryNameAbbreviation(){
		 countryAbbreviation = new HashMap<String, String>();

		 countryAbbreviation.put("ascension island","ac");
		 countryAbbreviation.put("andorra","ad");
		 countryAbbreviation.put("united arab emirates","ae");
		 countryAbbreviation.put("afghanistan","af");
		 countryAbbreviation.put("antigua and barbuda","ag");
		 countryAbbreviation.put("anguilla","ai");
		 countryAbbreviation.put("albania","al");
		 countryAbbreviation.put("armenia","am");
		 countryAbbreviation.put("netherlands antilles","an");
		 countryAbbreviation.put("angola","ao");
		 countryAbbreviation.put("antarctica","aq");
		 countryAbbreviation.put("argentina","ar");
		 countryAbbreviation.put("american samoa","as");
		 countryAbbreviation.put("austria","at");
		 countryAbbreviation.put("australia","au");
		 countryAbbreviation.put("aruba","aw");
		 countryAbbreviation.put("aland","ax");
		 countryAbbreviation.put("azerbaijan","az");
		 countryAbbreviation.put("bosnia and herzegovina","ba");
		 countryAbbreviation.put("barbados","bb");
		 countryAbbreviation.put("belgium","be");
		 countryAbbreviation.put("bangladesh","bd");
		 countryAbbreviation.put("burkina faso","bf");
		 countryAbbreviation.put("bulgaria","bg");
		 countryAbbreviation.put("bahrain","bh");
		 countryAbbreviation.put("burundi","bi");
		 countryAbbreviation.put("benin","bj");
		 countryAbbreviation.put("bermuda","bm");
		 countryAbbreviation.put("brunei darussalam","bn");
		 countryAbbreviation.put("bolivia","bo");
		 countryAbbreviation.put("brazil","br");
		 countryAbbreviation.put("bahamas","bs");
		 countryAbbreviation.put("bhutan","bt");
		 countryAbbreviation.put("bouvet island","bv");
		 countryAbbreviation.put("botswana","bw");
		 countryAbbreviation.put("belarus","by");
		 countryAbbreviation.put("belize","bz");
		 countryAbbreviation.put("canada","ca");
		 countryAbbreviation.put("cocos (keeling) islands","cc");
		 countryAbbreviation.put("congo (democratic republic)","cd");
		 countryAbbreviation.put("central african republic","cf");
		 countryAbbreviation.put("congo (republic)","cg");
		 countryAbbreviation.put("switzerland","ch");
		 countryAbbreviation.put("cook islands","ck");
		 countryAbbreviation.put("chile","cl");
		 countryAbbreviation.put("cameroon","cm");
		 countryAbbreviation.put("colombia","co");
		 countryAbbreviation.put("costa rica","cr");
		 countryAbbreviation.put("cuba","cu");
		countryAbbreviation.put("cape verde","cv");
		countryAbbreviation.put("christmas island","cx");
		countryAbbreviation.put("cyprus","cy");
		countryAbbreviation.put("czech republic","cz");
		countryAbbreviation.put("germany","de");
		countryAbbreviation.put("djibouti","dj");
		countryAbbreviation.put("denmark","dk");
		countryAbbreviation.put("dominica","dm");
		countryAbbreviation.put("dominican republic","do");
		countryAbbreviation.put("algeria","dz");
		countryAbbreviation.put("ecuador","ec");
		countryAbbreviation.put("estonia","ee");
		countryAbbreviation.put("egypt","eg");
		countryAbbreviation.put("eritrea","er");
		countryAbbreviation.put("spain","es");
		countryAbbreviation.put("ethiopia","et");
		countryAbbreviation.put("european union","eu");
		countryAbbreviation.put("finland","fi");
		countryAbbreviation.put("fiji","fj");
		countryAbbreviation.put("falkland islands (malvinas)","fk");
		countryAbbreviation.put("micronesia, federated states of","fm");
		countryAbbreviation.put("faroe islands","fo");
		countryAbbreviation.put("france","fr");
		countryAbbreviation.put("gabon","ga");
		countryAbbreviation.put("united kingdom","gb");
		countryAbbreviation.put("grenada","gd");
		countryAbbreviation.put("georgia","ge");
		countryAbbreviation.put("french guiana","gf");
		countryAbbreviation.put("guernsey","gg");
		countryAbbreviation.put("ghana","gh");
		countryAbbreviation.put("gibraltar","gi");
		countryAbbreviation.put("greenland","gl");
		countryAbbreviation.put("gambia","gm");
		countryAbbreviation.put("guinea","gn");
		countryAbbreviation.put("guadeloupe","gp");
		countryAbbreviation.put("equatorial guinea","gq");
		countryAbbreviation.put("greece","gr");
		countryAbbreviation.put("south georgia and the south sandwich islands","gs");
		countryAbbreviation.put("guatemala","gt");
		countryAbbreviation.put("guam","gu");
		countryAbbreviation.put("guinea-bissau","gw");
		countryAbbreviation.put("guyana","gy");
		countryAbbreviation.put("hong kong","hk");
		countryAbbreviation.put("heard and mc donald islands","hm");
		countryAbbreviation.put("honduras","hn");
		countryAbbreviation.put("croatia (local name: hrvatska)","hr");
		countryAbbreviation.put("haiti","ht");
		countryAbbreviation.put("hungary","hu");
		countryAbbreviation.put("indonesia","id");
		countryAbbreviation.put("ireland","ie");
		countryAbbreviation.put("israel","il");
		countryAbbreviation.put("isle of man","im");
		countryAbbreviation.put("india","in");
		countryAbbreviation.put("british indian ocean territory","io");
		countryAbbreviation.put("iraq","iq");
		countryAbbreviation.put("iran (islamic republic of)","ir");
		countryAbbreviation.put("iceland","is");
		countryAbbreviation.put("italy","it");
		countryAbbreviation.put("jersey","je");
		countryAbbreviation.put("jamaica","jm");
		countryAbbreviation.put("jordan","jo");
		countryAbbreviation.put("japan","jp");
		countryAbbreviation.put("kenya","ke");
		countryAbbreviation.put("kyrgyzstan","kg");
		countryAbbreviation.put("cambodia","kh");
		countryAbbreviation.put("kiribati","ki");
		countryAbbreviation.put("comoros","km");
		countryAbbreviation.put("saint kitts and nevis","kn");
		countryAbbreviation.put("korea, republic of","kr");
		countryAbbreviation.put("kuwait","kw");
		countryAbbreviation.put("cayman islands","ky");
		countryAbbreviation.put("kazakhstan","kz");
		countryAbbreviation.put("lebanon","lb");
		countryAbbreviation.put("saint lucia","lc");
		countryAbbreviation.put("liechtenstein","li");
		countryAbbreviation.put("sri lanka","lk");
		countryAbbreviation.put("liberia","lr");
		countryAbbreviation.put("lesotho","ls");
		countryAbbreviation.put("lithuania","lt");
		countryAbbreviation.put("luxembourg","lu");
		countryAbbreviation.put("latvia","lv");
		countryAbbreviation.put("libyan arab jamahiriya","ly");
		countryAbbreviation.put("morocco","ma");
		countryAbbreviation.put("monaco","mc");
		countryAbbreviation.put("moldova, republic of","md");
		countryAbbreviation.put("montenegro","me");
		countryAbbreviation.put("madagascar","mg");
		countryAbbreviation.put("marshall islands","mh");
		countryAbbreviation.put("macedonia, the former yugoslav republic of","mk");
		countryAbbreviation.put("mali","ml");
		countryAbbreviation.put("myanmar","mm");
		countryAbbreviation.put("mongolia","mn");
		countryAbbreviation.put("macau","mo");
		countryAbbreviation.put("northern mariana islands","mp");
		countryAbbreviation.put("martinique","mq");
		countryAbbreviation.put("mauritania","mr");
		countryAbbreviation.put("montserrat","ms");
		countryAbbreviation.put("malta","mt");
		countryAbbreviation.put("mauritius","mu");
		countryAbbreviation.put("maldives","mv");
		countryAbbreviation.put("malawi","mw");
		countryAbbreviation.put("mexico","mx");
		countryAbbreviation.put("malaysia","my");
		countryAbbreviation.put("mozambique","mz");
		countryAbbreviation.put("namibia","na");
		countryAbbreviation.put("new caledonia","nc");
		countryAbbreviation.put("niger","ne");
		countryAbbreviation.put("norfolk island","nf");
		countryAbbreviation.put("nigeria","ng");
		countryAbbreviation.put("nicaragua","ni");
		countryAbbreviation.put("netherlands","nl");
		countryAbbreviation.put("norway","no");
		countryAbbreviation.put("nepal","np");
		countryAbbreviation.put("nauru","nr");
		countryAbbreviation.put("niue","nu");
		countryAbbreviation.put("new zealand","nz");
		countryAbbreviation.put("oman","om");
		countryAbbreviation.put("panama","pa");
		countryAbbreviation.put("peru","pe");
		countryAbbreviation.put("french polynesia","pf");
		countryAbbreviation.put("papua new guinea","pg");
		countryAbbreviation.put("philippines, republic of the","ph");
		countryAbbreviation.put("pakistan","pk");
		countryAbbreviation.put("poland","pl");
		countryAbbreviation.put("st. pierre and miquelon","pm");
		countryAbbreviation.put("pitcairn","pn");
		countryAbbreviation.put("puerto rico","pr");
		countryAbbreviation.put("palestine","ps");
		countryAbbreviation.put("portugal","pt");
		countryAbbreviation.put("palau","pw");
		countryAbbreviation.put("paraguay","py");
		countryAbbreviation.put("qatar","qa");
		countryAbbreviation.put("reunion","re");
		countryAbbreviation.put("romania","ro");
		countryAbbreviation.put("serbia","rs");
		countryAbbreviation.put("russian federation","ru");
		countryAbbreviation.put("rwanda","rw");
		countryAbbreviation.put("saudi arabia","sa");
		countryAbbreviation.put("scotland","uk");
		countryAbbreviation.put("solomon islands","sb");
		countryAbbreviation.put("seychelles","sc");
		countryAbbreviation.put("sudan","sd");
		countryAbbreviation.put("sweden","se");
		countryAbbreviation.put("singapore","sg");
		countryAbbreviation.put("st. helena","sh");
		countryAbbreviation.put("slovenia","si");
		countryAbbreviation.put("svalbard and jan mayen islands","sj");
		countryAbbreviation.put("slovakia (slovak republic)","sk");
		countryAbbreviation.put("sierra leone","sl");
		countryAbbreviation.put("san marino","sm");
		countryAbbreviation.put("senegal","sn");
		countryAbbreviation.put("somalia","so");
		countryAbbreviation.put("suriname","sr");
		countryAbbreviation.put("sao tome and principe","st");
		countryAbbreviation.put("soviet union","su");
		countryAbbreviation.put("el salvador","sv");
		countryAbbreviation.put("syrian arab republic","sy");
		countryAbbreviation.put("swaziland","sz");
		countryAbbreviation.put("turks and caicos islands","tc");
		countryAbbreviation.put("chad","td");
		countryAbbreviation.put("french southern territories","tf");
		countryAbbreviation.put("togo","tg");
		countryAbbreviation.put("thailand","th");
		countryAbbreviation.put("tajikistan","tj");
		countryAbbreviation.put("tokelau","tk");
		countryAbbreviation.put("east timor","ti");
		countryAbbreviation.put("turkmenistan","tm");
		countryAbbreviation.put("tunisia","tn");
		countryAbbreviation.put("tonga","to");
		countryAbbreviation.put("east timor","tp");
		countryAbbreviation.put("turkey","tr");
		countryAbbreviation.put("trinidad and tobago","tt");
		countryAbbreviation.put("tuvalu","tv");
		countryAbbreviation.put("taiwan","tw");
		countryAbbreviation.put("tanzania, united republic of","tz");
		countryAbbreviation.put("ukraine","ua");
		countryAbbreviation.put("uganda","ug");
		countryAbbreviation.put("united kingdom","uk");
		countryAbbreviation.put("united states minor outlying islands","um");
		countryAbbreviation.put("united states","us");
		countryAbbreviation.put("uruguay","uy");
		countryAbbreviation.put("uzbekistan","uz");
		countryAbbreviation.put("vatican city state (holy see)","va");
		countryAbbreviation.put("saint vincent and the grenadines","vc");
		countryAbbreviation.put("venezuela","ve");
		countryAbbreviation.put("virgin islands (british)","vg");
		countryAbbreviation.put("virgin islands (u.s.)","vi");
		countryAbbreviation.put("viet nam","vn");
		countryAbbreviation.put("vanuatu","vu");
		countryAbbreviation.put("wallis and futuna islands","wf");
		countryAbbreviation.put("samoa","ws");
		countryAbbreviation.put("yemen","ye");
		countryAbbreviation.put("mayotte","yt");
		countryAbbreviation.put("south africa","za");
		countryAbbreviation.put("zambia","zm");
	}

	public String print(){
		String name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1);
		return name + "\t" + this.surnames;
	}

	/* (non-Javadoc)
	 * @see it.unimi.dico.islab.iimb.transfom.Transformation#execute(java.lang.Object)
	 */
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		String newS = "";
		String s = (String)arg;
		if(arg instanceof String){
			String[] tokens = s.split(" ");
			for(int i = 0; i < tokens.length; i++){
				if(countryAbbreviation.containsKey(tokens[i].toLowerCase())){
					newS += countryAbbreviation.get(tokens[i].toLowerCase());
					newS += " ";
		    	}
				else{
					newS += tokens[i];
					newS += " ";
				}
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
		return newS;
	}

	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}

}

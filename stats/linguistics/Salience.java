package stats.linguistics;

//public class  {

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Salience {

	public static void main(String[] args) {
		// main just a left over from TDD : now I suppose it is just a test
		Salience salience = new Salience();
		
		HashMap<String, Gram> map = new HashMap<String, Gram>();
		String[] k = new String[] { "b", "c", "d", "e", "d", "z", "g", "a" };
		double[] d = new double[] { 5.0, 4.0, 3.0, 2.0, 1.0, 110.1, 0.01, 8.0 };
		for (int i = 0; i < d.length; i++) {
			Gram g = new Gram();
			g.tfidf = d[i];
			g.seen = d[i];
			map.put(k[i], g);
		}

		String result = salience.sortByTFIDF(map, 3);
		System.out.println( result );
	}
	
	public String sortByTFIDF(Map<String, Gram> map, int limit ) {
		GramComparator vc = new GramComparator(map);
		TreeMap<String, Gram> sortedMap = new TreeMap<String, Gram>(vc);
		sortedMap.putAll(map);
		
		if ( limit > sortedMap.size() ) {
			limit = sortedMap.size();
		}
		int loop = 0; 
		String out = "";
		for (String key : sortedMap.keySet()) {
			if ( loop < limit ) {
				//out += key + ":" + map.get(key).tfidf;
				out += key;
				if ( loop < limit - 1 ) {
					out += ", ";
				}
			} else {
				break;
			}
			loop++;
		}
			
		return out;
	}
}

class GramComparator implements Comparator<String> {

	Map<String, Gram> map;

	public GramComparator(Map<String, Gram> map) {
		this.map = map;
	}

	public int compare(String a, String b) {
		if (map.get(a).tfidf >= map.get(b).tfidf) {
			return -1;
		} else {
			return 1;
		}
	}
}
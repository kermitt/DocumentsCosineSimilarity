package stats.linguistics;

import java.util.HashMap;
import java.util.Map;

public class DocumentSimilarityTest {
	private DocumentSimilarity documentSimilarity = new DocumentSimilarity();

	public static void main(String... strings) {
		DocumentSimilarityTest test = new DocumentSimilarityTest();
		test.parseFiles();
		test.sortGrams();
	}

	private void sortGrams() {

		Salience salience = new Salience();

		Map<String, Gram> map = new HashMap<String, Gram>();
		String[] k = new String[] { "b", "c", "d", "e", "d", "z", "g", "a" };
		double[] d = new double[] { 5.0, 4.0, 3.0, 2.0, 1.0, 110.1, 0.01, 8.0 };
		for (int i = 0; i < d.length; i++) {
			Gram g = new Gram();
			g.tfidf = d[i];
			g.seen = d[i];
			map.put(k[i], g);
		}

		String result = salience.sortByTFIDF(map, 3);
		// System.out.println( result );

		boolean isOk = "z:110.1, a:8.0, b:5.0".equals(result);
		log(isOk, "sortGrams");

	}

	private void parseFiles() {
		documentSimilarity.parseFiles("C://1000/1000//text//test//");
		boolean isOk = documentSimilarity.HoH_documentsStringMap.size() == 3
				&& documentSimilarity.grams.size() == 5;
		log(isOk, "parseFiles");
	}

	private void log(String s) {
		System.out.println(s);
	}

	public void log(boolean b, String s) {
		log((b ? "PASS" : "FAIL") + "  " + s);
	}
}

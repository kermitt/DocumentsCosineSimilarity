package stats.linguistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentSimilarity {

	// file names
	public List<String> fileNames = new ArrayList<String>();

	// all observed grams and their counts
	// note - not doing anything yet with the gram counts, but I've got plans...
	public Map<String, Integer> grams = new HashMap<String, Integer>();

	// Assoc array of each document its sparse term-vector
	public Map<String, double[]> HoL_documentsVectors = new HashMap<String, double[]>();

	// Assoc Assoc array of each document its sparse term-vector
	public Map<String, Map<String, Gram>> HoH_documentsStringMap = new HashMap<String, Map<String, Gram>>();

	public void parseFiles(String filePath) {
		try {
			File[] allfiles = new File(filePath).listFiles();
			BufferedReader in = null;
			for (File f : allfiles) {
				if (f.getName().endsWith(".txt")) {
					fileNames.add(f.getName());

				//	log("Reading " + f.getName());

					in = new BufferedReader(new FileReader(f));
					StringBuilder sb = new StringBuilder();
					String s = null;
					while ((s = in.readLine()) != null) {
						sb.append(s);
						sb.append("\n");
					}

					// TODO! regex to fetch sentences... ...would be fun to do
					// sentence level analysis also
					String[] tokenizedTerms = sb.toString()
							.replaceAll("[\\W&&[^\\s]]", "").split("\\W+");
					Map<String, Gram> map = new HashMap<String, Gram>();
					for (String term : tokenizedTerms) {

						term = term.toLowerCase();

						if (grams.containsKey(term)) {
							grams.put(term, new Integer(1 + grams.get(term)));
						} else {
							grams.put(term, new Integer(1));
						}

						if (map.containsKey(term)) {
							map.get(term).seen++;
						} else {
							map.put(term, new Gram());
						}
					}
					HoH_documentsStringMap.put(f.getName(), map);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setHoL_documentsTFIDFvectors() {
		double tf;
		double idf;
		double tfidf;

		for (String fileName : HoH_documentsStringMap.keySet()) {

			Map<String, Gram> docTermsArray = HoH_documentsStringMap
					.get(fileName);

			double[] tfidfvectors = new double[grams.size()];
			int count = 0;

			long t1 = System.currentTimeMillis();

			for (String gram : grams.keySet()) {
				

				
				tf = getTF(docTermsArray, gram, fileName);
				idf = getIDF(gram);
				tfidf = tf * idf;
				
				if ( HoH_documentsStringMap.get( fileName ).containsKey( gram )) {
					HoH_documentsStringMap.get(fileName ).get( gram ).tfidf = 10 + tfidf; 
					System.out.println( fileName + "   " + gram + "   "  );
				}
				
				//grams.get(gram).seen = 1.1;
//				HoH_documentsStringMap.get( fileName ).get( gram ).seen = 1.0;//tfidf;
				
				tfidfvectors[count] = tfidf;
				count++;
			}

			HoL_documentsVectors.put(fileName, tfidfvectors);

		//	log("Passing TFIDF fileName " + fileName + "   milsec "
		//			+ (System.currentTimeMillis() - t1));

		}
	}

	private double getIDF(String gram) {
		double countGramOccurances = 0;
		for (String fileName : HoH_documentsStringMap.keySet()) {
			Map<String, Gram> map = HoH_documentsStringMap.get(fileName);
			if (map.containsKey(gram)) {
				countGramOccurances++;
			}
		}
		// log( countGramOccurances + " ??  " + gram );
		return Math.log(HoH_documentsStringMap.size() / countGramOccurances);
	}

	private double getTF(Map<String, Gram> totalterms, String gram,
			String fileName) {
		double countGramOccurances = 0;
		if (totalterms.containsKey(gram)) {
			countGramOccurances = totalterms.get(gram).seen;
		}

		return countGramOccurances / totalterms.size();
	}

	public void display() {
		// No important logic - merely to get sorted terms ( on tfidf ) to print
		// out
		Salience salience = new Salience();

		// for (int i = 0; i < fileNames.size(); i++) {
		int i = fileNames.size() - 1;
		log(" ------ ");

		String keyI = fileNames.get(i);
		double[] vectorI = HoL_documentsVectors.get(keyI);
		for (int j = 0; j < fileNames.size(); j++) {
			String keyJ = fileNames.get(j);

			double[] vectorJ = HoL_documentsVectors.get(keyJ);
			double tfidfScore = cosineSimilarity(vectorI, vectorJ);

			// String desc = describeVector( vectorJ );
			// log(fileNames.get(j) + " vs. " + fileNames.get(i) + "  =  "
			// + result + "  " + desc);
			String fileName = fileNames.get(j);
			String sorted_terms = salience.sortByTFIDF(
					HoH_documentsStringMap.get(fileName), 3);

			log(fileName + ": " + tfidfScore + " : " + sorted_terms );

		}
		// }
	}

	public String describeVector(double[] v) {
		int zero = 0;

		for (int i = 0; i < v.length; i++) {
			if (v[i] == 0) {
				zero++;
			}
		}
		return " unique terms=" + v.length + " vector contains "
				+ (v.length - zero) + " of them";
	}

	public void log(String s) {
		System.out.println(s);
	}

	public double cosineSimilarity(double[] docVector1, double[] docVector2) {
		double dotProduct = 0.0;
		double magnitude1 = 0.0;
		double magnitude2 = 0.0;
		double cosineSimilarity = 0.0;

		for (int i = 0; i < docVector1.length; i++) {
			dotProduct += docVector1[i] * docVector2[i]; // a.b
			magnitude1 += Math.pow(docVector1[i], 2); // (a^2)
			magnitude2 += Math.pow(docVector2[i], 2); // (b^2)
		}

		magnitude1 = Math.sqrt(magnitude1);// sqrt(a^2)
		magnitude2 = Math.sqrt(magnitude2);// sqrt(b^2)

		if (magnitude1 != 0.0 | magnitude2 != 0.0) {
			cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
		} else {
			return 0.0;
		}
		return cosineSimilarity;
	}

	public static void main(String... strings) {
		DocumentSimilarity sim = new DocumentSimilarity();
		long t0 = System.currentTimeMillis();
		long t1 = System.currentTimeMillis();

		// sim.parseFiles("C://1000/1000//text//test//");
		sim.parseFiles("C://1000/1000//text//");

		long t2 = System.currentTimeMillis();
		//System.out.println("Reading files took milsec " + (t2 - t1));

		t1 = System.currentTimeMillis();

		sim.setHoL_documentsTFIDFvectors();

		t2 = System.currentTimeMillis();
		System.out.println("Calculating TFIDF took milsec" + (t2 - t1));

		sim.display();

		System.out.println(" The end " + (System.currentTimeMillis() - t0));
	}
}
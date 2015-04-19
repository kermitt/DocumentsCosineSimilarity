package stats.linguistics;

public class DocumentSimilarityTest {
	private DocumentSimilarity documentSimilarity = new DocumentSimilarity(); 
	
	public static void main(String...strings) { 
		DocumentSimilarityTest test = new DocumentSimilarityTest();
		test.parseFiles();
	}
	private void parseFiles() { 
		documentSimilarity.parseFiles("C://1000/1000//text//test//");
		boolean isOk = documentSimilarity.HoL_documentsStringArrays.size() == 3 && documentSimilarity.grams.size() == 36;
		
		log(isOk, "parseFiles");
	}
	
	private void log( String s ) { 
		System.out.println( s ); 
	}
	
	public void log( boolean b, String s ) { 
		log( (b ? "PASS" : "FAIL") + "  " + s );
	}
}

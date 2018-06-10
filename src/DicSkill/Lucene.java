package DicSkill;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 09.06.2018
 * NEW:
 * -	set up class and most of the code
 * TO DO:
 * -	search for phrase instead of words to limit output
 * -	indexing (or search??) takes too much time, how solve?
 * -	improve output by removing unwanted characters
 * @author Lia
 */

public class Lucene {
	
	/** VARIABLES **/
	Directory directory;
	Analyzer analyzer;
	
	String[] lastResults;
	
	/** CONSTRUCTOR **/
	public Lucene() {
		try {
			createIndex();
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		
	}
	
	/** Methods **/
	public String[] translate(String ww, int NOW) {
		try {
			ArrayList<String> results = searchIndex(ww);
			
			//returns the best results
			return getBestResults(results, NOW, ww);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String[] getBestResults(ArrayList<String> results, int NOW, String ww) {
		String[] bestResults = new String[NOW];
		bestResults[0] = "";
		
		// searches for exact match and sets it as first result upon finding it
		int i=1;
		Iterator<String> resultIterator1 = results.iterator();
		while(resultIterator1.hasNext() && i<NOW) {
			String result = resultIterator1.next();
			
			
			if(result.equals(ww)) {
				bestResults[0] = resultIterator1.next();
			}
			i++;
		}
		
		//resets i depending on weather or not an exact match was found
		if(bestResults[0].isEmpty()) {
			i = 0;
		}
		else {
			i = 1;
		}
		// fills up bestResults with other results
		Iterator<String> resultIterator2 = results.iterator();
		while(resultIterator2.hasNext() && i<NOW) {
			String result = resultIterator2.next();
			if(!result.equals(bestResults[0]))
				bestResults[i++] = result;
			i++;
		}
		
		lastResults = bestResults;
		
		return bestResults;
	}
	
	private void createIndex() throws IOException, ParseException {
		analyzer = new StandardAnalyzer();

		// Store the index in memory:
		//Directory directory = new RAMDirectory();
		// To store an index on disk, use this instead:
		directory = FSDirectory.open(Paths.get("./dict/German/indexDirectory"));
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);
	
		List<String> text = Files.readAllLines(Paths.get("./dict/German/filesToIndex/de-en.txt"));
		Iterator<String> textIterator = text.iterator();
		textIterator.next(); // have to skip, because first output "?" fucks everything up
		while(textIterator.hasNext()) {
			String nextLine = textIterator.next();
			
			if(nextLine.isEmpty()) continue;
			
			String[] parts = nextLine.split("::");
			
			/*for(int i=0; i<parts.length; i++) {
				System.out.println(parts[i]);
			}*/
			
			Document doc = new Document();
			doc.add(new Field("english", parts[1], TextField.TYPE_STORED));
			doc.add(new Field("german", parts[0], TextField.TYPE_STORED));
			iwriter.addDocument(doc);
		}
		iwriter.close();
	}

	private ArrayList<String> searchIndex(String ww) throws IOException, ParseException {
		ArrayList<String> results = new ArrayList<String>();
		
		/* this currently returns all hits for "cat" and "flap", instead of "cat flap".
		 * To accomplish best results, we would have to search for the phrase "| cat flap "
		 * later it must be replaced with "| "+ww+" "
		 */
		String searchWord = "cat flap";
		
		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
				
		// Parse a simple query that searches for the searchWord:
		QueryParser parser = new QueryParser("english", analyzer);
		Query query = parser.parse( searchWord );
		ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
				
		// Iterate through the results:
		assert(1 == hits.length);
		for (int i = 0; i < hits.length; i++) {
			Document hitDoc = isearcher.doc(hits[i].doc);
			Iterator<IndexableField> it = hitDoc.iterator();
					
			while(it.hasNext()) {
				IndexableField f = it.next();
				String result = f.stringValue();
				results.add( result );
				System.out.println(f.name() + " = " + result);
			}
		}
		ireader.close();
		directory.close();
		
		return results;
	}
	
}

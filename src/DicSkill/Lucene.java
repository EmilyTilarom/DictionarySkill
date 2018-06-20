package DicSkill;

import java.io.File;
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
 * 19.06.2018
 * NEW:
 * -	New system to filter results and get the best matches
 * -	removal of unwanted/invalid characters is in a separate function now
 * @author Lia (& Adrian assisted on some parts)
 */

/**
 * 18.06.2018
 * NEW:
 * -	Indexing has been improved, so it wont do it every time
 * -	unwanted/invalid characters removed
 * -	attempts to filter results has been made
 * TO DO:
 * -	improve filtering of results
 * @author Adrian
 */

/**
 * 09.06.2018
 * NEW:
 * -	set up class and most of the code
 * TO DO:
 * -	search for phrase instead of words to limit output
 * -	indexing (or search??) takes too much time, how solve?
 * -	improve output by removing unwanted characters
 * -    when not asking for a word program crashes
 * @author Lia
 */

public class Lucene {
	
	/** VARIABLES **/
	Directory directory;
	Analyzer analyzer;
	
	ArrayList<String> leftoverResults = new ArrayList<String>();
	
	/** CONSTRUCTOR **/
	public Lucene() {
		
		// Creates index in specifies directory
		try {
			File directory = new File("./dict/German/indexDirectory");

			if(directory.isDirectory()) {

				if (directory.list().length != 11) {//11 files are created and used for indexing
					//start indexing
					System.out.println("Start indexing");

					//delete all files in folder to avoid corrupt/outdated files
					File[] files = directory.listFiles(); // get all files in directory
					//delete all files
					for(File file : files)
					{
						if(!file.delete())
						{
							System.out.println("Failed to delete file");
						}
						else
							System.out.println("Success to delete file");
					}

					createIndex();
					System.out.println("Indexing done");
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		
	}
	
	/** Methods **/
	public String[] translate(String ww, int nOW) {
		//System.out.println("Lucene 89 ww: "+ww+" "+nOW);
		try {
			ArrayList<String> results = searchIndex(ww);

			// returns result without additional characters and sorted
			return getResults(results, nOW, ww);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}


    /**
     * returns perfect matches word by word in Array, returns null if no perfect is found
     * adds not perfect but fittin results to leftover results
	 *
     * @param results ArrayList of fount entries containing the ww
     * @param ww WishedWord
     * @param numberOfWishedResults number of wished results
     * @return returns array of results or null if no perfect match was found
     */
    private ArrayList<String> getMatches(ArrayList<String> results, String ww, int numberOfWishedResults)
    {
    	leftoverResults.clear(); // needs to be cleared before any new results are gathered
    	
    	/*	to get a matching result, each result needs to be divided at the | character. The position of the match
         *  will have the same position in the english version and everything else can therefore be removed.
         *  Afterwards there will be a matching result, which may contain synonyms divided by the ; character
         */
    	
    	ArrayList<String> resultArrayList = new ArrayList<String>();
    	int j = 1; // just a counter for the loop, starting at the first english content
    	while(j<results.size()) // only every 2nd, so it only checks english results
        {
    		String[] englishLine = results.get(j).split(" \\|");
    		String[] germanLine = results.get(j-1).split(" \\|");
    		
    		//for testing
    		if(englishLine.length != germanLine.length) {
    			System.out.println("oops, sth went wrong");
    		}
    		
    		//adds the German equivalent for the found String to matches
    		for(int i=0; i < englishLine.length; i++) {
    			
    			if(englishLine[i].contains(" "+ww+";") || englishLine[i].contains(";"+ww+" ")) {
	    			
    				// results are split at ; as they divide synonyms
	    			String[] translations = germanLine[i].split(";");
	    			
	    			// adds found translation depending on: contains phrase or starts with phrase
	    			for(int x=0; x<translations.length; x++) {
	    				
	    				if(englishLine[i].startsWith(" "+ww+";")) { // adds perfect matches to resultArrayList
	    					resultArrayList.add(translations[x]);
	    				}
	    				else {
	    					leftoverResults.add(translations[x]); // adds leftover matches to leftoverResults
	    				}
	    			
	    				//System.out.println(translations[x]); // prints added line (for testing)
	    			}
	    		}
    		}
    		j +=2;
        }
    	
    	return resultArrayList;
    	
    	/*
    	 // removes additional characters which do not belong in the result
        String[] endArray = new String[resultArrayList.size()];
        
        for(int i=0; i<resultArrayList.size(); i++){
            if(resultArrayList.get(i).contains("{")) { // removes everything after { character
                endArray[i] = resultArrayList.get(i).substring(0, resultArrayList.get(i).indexOf("{"));
            }
            else if(resultArrayList.get(i).contains("(")){
                endArray[i] = resultArrayList.get(i).substring(0, resultArrayList.get(i).indexOf("("));
            }
            else{
                endArray[i] = resultArrayList.get(i);
            }
        }

        return  endArray;*/
    }

    

    /**
     * sorts the results to display the perfect results (first). perfect results are which match ww 100%, 
     * after that combinations of ww with other words from leftover results
     * @param results ArrayList of all entries found in the dictionary containing the ww
     * @param nOW number of words, tells the functions how many words are wished
     * @param ww wished word we are looking for in the data base
     * @return returns Array of results or null if word was not found
     */
	private String[] getResults(ArrayList<String> results, int nOW, String ww) {


		ArrayList<String> perfectResults;
		ArrayList<String> endResult = new ArrayList<String>();

        perfectResults = getMatches(results, ww, nOW);
   
        if(perfectResults.size() < nOW) { // adds leftover results in case not enough matches were found.
            
        	int i = 0; //counter for NOW
        	
        	// add perfect Results
        	while(i<nOW && i<perfectResults.size()) {
        		endResult.add( perfectResults.get(i) );
        		i++;
        	}
        	
        	// add leftover Results and removes them from ArrayList<String> leftoverResults
        	while(i<nOW && !leftoverResults.isEmpty()) {
        		endResult.add( leftoverResults.get( 0 ) );
        		leftoverResults.remove( 0 );
        		i++;
        	}
        	
        	//returns null if no matches were found
            if(endResult.size() == 0 || endResult == null) {
            	return null;
            }
        	
        	return removeInvalidChars(endResult);
        }

        // returns perfect results if there are enough results
        return removeInvalidChars(perfectResults);
	}

    /**
     * creates index of the dictionary file
     * @throws IOException
     * @throws ParseException
     */
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

			/*
			for(int i=0; i<parts.length; i++) {
				System.out.println(parts[i]);
			}*/
			
			Document doc = new Document();
			//System.out.println(parts[0]);
			doc.add(new Field("german", parts[0], TextField.TYPE_STORED));
			doc.add(new Field("english", parts[1], TextField.TYPE_STORED));
			iwriter.addDocument(doc);
		}
		iwriter.close();
		System.out.println("closed");
	}

    /**
     *looks in the database for all entries containing the ww
     * @param ww wished word, which we are looking for in the database
     * @return List of all entries in the database which contain the wished word
     *          every word/ phrase has its own field in the Array, alternating german/english
     * @throws IOException
     * @throws ParseException
     */
	private ArrayList<String> searchIndex(String ww) throws IOException, ParseException {
		ArrayList<String> results = new ArrayList<String>();
		analyzer = new StandardAnalyzer();
		directory = FSDirectory.open(Paths.get("./dict/German/indexDirectory"));
		
		/* this currently returns all hits for "cat" and "flap", instead of "cat flap".
		 * To accomplish best results, we would have to search for the phrase "| cat flap "
		 * later it must be replaced with "| "+ww+" "
		 */
		//String searchWord = "cat flap";

		String searchWord = ww;
		
		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
				
		// Parse a simple query that searches for the searchWord:
		QueryParser parser = new QueryParser("english", analyzer);
		Query query = parser.parse( searchWord );
		ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
				
		// Iterate through the results:
		//assert(1 == hits.length); //Wat this doin?
		for (int i = 0; i < hits.length; i++) {
			Document hitDoc = isearcher.doc(hits[i].doc);
			Iterator<IndexableField> it = hitDoc.iterator();
					
			while(it.hasNext()) {
				IndexableField f = it.next();
				String result = f.stringValue();
				results.add( result );
				//System.out.println(f.name() + " " + i + " = " + result);
			}
		}
		ireader.close();
		directory.close();

		/*
        System.out.println("result0 " + results.get(0));
        System.out.println("result1 " + results.get(1));
        System.out.println("result2 " + results.get(2));
        System.out.println("result3 " + results.get(3));
		*/

		return results;
	}
	

	/**
     * removes all characters after an invalid char such as { and converts from ArrayList to Array on the way
     * @param phrases: Phrases in ArrayList<String> which may contain invalid chars
     * @return phrases as Array, characters after an incalid character excluded
     */
	private String[] removeInvalidChars(ArrayList<String> phrases) {
		
		String[] invalidChars = {"{", "[", "(", "  "};
		
        String[] result = new String[phrases.size()];
        
        for(int i=0; i<phrases.size(); i++){
            
        	for(String invChar : invalidChars) {
        		if(phrases.get(i).contains(invChar)) { 
        			// removes everything after invalid character
                	phrases.set(i, phrases.get(i).substring(0, phrases.get(i).indexOf(invChar)) ); 
                }
        	}
            result[i] = phrases.get(i);
            
        }
        
        return result;
	}

	
}

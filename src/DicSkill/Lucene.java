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
 * 23.06.2018
 * NEW:
 * -	Improved results for translations
 * -	Improved the removal of invalid characters
 * -	Added the possibility to get synonyms (English) from lucene too
 * -	Added function getMoreResults
 * -	Improved commentary 
 * @author Lia 
 */

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
     * adds not perfect but fitting results to leftover results
	 *
     * @param results ArrayList of fount entries containing the ww
     * @param ww WishedWord
     * @param numberOfWishedResults number of wished results
     * @return returns array of results or null if no perfect match was found
     */
    private ArrayList<String> getMatches(ArrayList<String> results, String ww, int numberOfWishedResults)
    {
    	leftoverResults.clear(); // needs to be cleared before any new results are gathered
    	
    	/*	NOTE: 
    	 * 	to get a matching result, each result needs to be divided at the | character. The position of the match
         *  will have the same position in the english version and everything else can therefore be removed.
         *  Afterwards there will be a matching result, which may contain synonyms divided by the ; character
         *  Example:
         *  english line: "exact | exactly; precisely" -> [0]:"exact " [1]:" exactly; precisely"
         *  german line: "präzise; genau | genau" -> [0]:"präzise; genau " [1]:" genau"
         *  german line [n] and english line [n] contain matching results. All results divided by ; in [n] 
         *  are perfect results. Everything else are alternative results, which may not be perfect. This makes
         *  "präzise" and "genau" perfect results when asking for a translation for "exact".
         */
    	
    	ArrayList<String> resultArrayList = new ArrayList<String>();
    	int j = 1; // just a counter for the loop, starting at the first English content
    	while(j<results.size()) // only every 2nd, so it only checks English results
        {
    		String[] englishLine = results.get(j).split(" \\|");
    		String[] germanLine = results.get(j-1).split(" \\|");
    		
    		//this should never happen and would mean the content of the database is faulty
    		if(englishLine.length != germanLine.length) {
    			System.out.println("Sorry, our database let you down on this.");
    		}
    		
    		//adds the German equivalent for the found String to matches
    		for(int i=0; i < englishLine.length; i++) {
    			englishLine[i] = removeInvalidChars(englishLine[i]); // removes invalid chars for better comparisons
    			
    			/*
    			 * Perfect matches:
    			 * -	equals ww 				(ww |) 		BUT NOT e.g.: "dog food; food for dog"
    			 * -	start with ww+";" 		(ww; othermatches ... |)
    			 * -	end with "; "+ww		( othermatches ... ; ww |)
    			 * -	contain "; "+ww+";"		( othermatches; ww; othermatches |)
    			 * Other matches only contain the ww anywhere in the string, e.g. as second word
    			 */
    			if(englishLine[i].contains(ww)) {
	    			
    				// results are split at ; as they divide synonyms
	    			String[] translations = germanLine[i].split(";");
	 
	    			// adds found translation depending on: imperect match or perfect match
	    			for(int x=0; x<translations.length; x++) {
	    				
	    				// perfect matches
	    				if(		englishLine[i].equals(ww)
	    						|| englishLine[i].endsWith("; "+ww) 
	    						|| englishLine[i].startsWith(ww+";")
	    						|| englishLine[i].contains("; "+ww+";") ) { // adds perfect matches to resultArrayList
	    					
	    					System.out.println("perfect: "+englishLine[i] + " = "+ translations[x]);
	    					
	    					resultArrayList.add(removeInvalidChars(translations[x]));
	    				}
	    				else { // imperfect matches
	    					System.out.println("imperfect: "+englishLine[i] + " = "+ translations[x]);
	    					if(leftoverResults.size() == 0) {
	    						leftoverResults.add(". Other results you may like are "+ removeInvalidChars(translations[x]) );
	    					}
	    					else {
	    						leftoverResults.add(removeInvalidChars(translations[x])); // adds leftover matches to leftoverResults
	    					}
	    					
	    				}
	    			
	    				//System.out.println(translations[x]); // prints added line (for testing)
	    			}
	    		}
    		}
    		j +=2;
        }
    	
    	return resultArrayList;
    	
    }

    /**
     * removes all characters after an invalid char such as { and converts from ArrayList to Array on the way
     * @param phrases: Phrases in ArrayList<String> which may contain invalid chars
     * @return phrases as Array, characters after an invalid character excluded
     */
	private String[] removeInvalidChars(ArrayList<String> phrases) {
		
		String[] result = new String[phrases.size()];
        
        for(int i=0; i<phrases.size(); i++){
            
            result[i] = removeInvalidChars(phrases.get(i));
        }
        
        return result;
	}
	
	/**
     * removes all characters after an invalid char such as { and converts from the String
     * @param phrase: String, which may contain invalid chars
     * @return phrase as String, characters after an invalid character excluded
     */
	private String removeInvalidChars(String phrase) {
		
		String[] invalidChars = {"{", "[", "("};
		String[] charCounterpart = {"}", "]", ")"};
		
		// for each invalid char and counterpart
        for(int i=0; i<invalidChars.length; i++ ) {
	        if(phrase.contains(invalidChars[i]) && phrase.contains(charCounterpart[i]) ) { 
	        	// removes everything between invalid character and its counterpart
	        	phrase = phrase.replaceAll("\\"+invalidChars[i]+".*\\"+charCounterpart[i], "");
	        }
		}
        
        //removes whitespace(s) at start or end of phrase
        phrase = phrase.trim();
        return phrase;
	}
    
	/**
	 * Returns nOW more results for last search carried out with lucene. These results were previously saved in
	 * leftoverResults. If leftoverResult.size() < nOW, it will only return leftoverResult.size() results
	 * @param nOW: number of words requested from lucene
	 * @return result: results from lucene
	 */
	public String[] getMoreResults(int nOW) {
		
		ArrayList<String> endResult = new ArrayList<String>();
		
		// returns null in case there are no more results.
		if(leftoverResults.isEmpty()) {
			return null;
		}
		
		// adds nOW results from leftoverResults (but max. leftoverResults.size() ) to endResult
		int i=0;
		while(i<nOW && !leftoverResults.isEmpty()) {
    		endResult.add( leftoverResults.get( 0 ) );
    		leftoverResults.remove( 0 );
    		i++;
    	}

		return removeInvalidChars(endResult);
		
	}
    
	/**
     * Creates an end results from perfect matches (perfectResults) and imperfect matches (leftoverResults).
     * The end results will only contain as many results as were requested (not all)
     * @param results: ArrayList of all entries found in the dictionary containing the ww
     * @param nOW: number of words, tells the functions how many words are wished
     * @param ww: wished word we are looking for in the data base
     * @return returns Array of results or null if no results were found
     */
	private String[] getResults(ArrayList<String> results, int nOW, String ww) {


		ArrayList<String> perfectResults;
		ArrayList<String> endResult = new ArrayList<String>();

        perfectResults = getMatches(results, ww, nOW);
        
        // adds leftover results in case not enough matches were found.
        if(perfectResults.size() < nOW) { 
            
        	int i = 0; //counter for NOW
        	
        	// add perfect Results to endResult
        	while(i<nOW && i<perfectResults.size()) {
        		endResult.add( perfectResults.get(i) );
        		i++;
        	}
        	
        	// add leftover Results to endResult and remove them from ArrayList<String> leftoverResults
        	while(i<nOW && !leftoverResults.isEmpty()) {
        		endResult.add( leftoverResults.get( 0 ) );
        		leftoverResults.remove( 0 );
        		i++;
        	}
        	
        	//returns null if no matches were found
            if(endResult.size() == 0 || endResult == null) {
            	return null;
            }
        	
            // returns end result
        	return removeInvalidChars(endResult);
        }
        
        // if perfectResults include more results than needed, they are added to beginning of leftoverResults
        while (perfectResults.size() > nOW) {
        	leftoverResults.add(0, perfectResults.remove(perfectResults.size()-1));
        }

        // returns perfect results (perfectResults.size() is equal to nOW )
        return removeInvalidChars(perfectResults);
	}

    /**
     * creates index of the dictionary file
     * @throws IOException
     * @throws ParseException
     */
	private void createIndex() throws IOException, ParseException {
		analyzer = new StandardAnalyzer();
		
		//Directory directory = new RAMDirectory(); // Stores the index in memory (RAM)
		directory = FSDirectory.open(Paths.get("./dict/German/indexDirectory")); // Stores indexes on disk
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);
	
		// gets file(s) which require indexing
		List<String> text = Files.readAllLines(Paths.get("./dict/German/filesToIndex/de-en.txt"));
		
		Iterator<String> textIterator = text.iterator();
		textIterator.next(); // have to skip, because first output "?" fucks everything up
		while(textIterator.hasNext()) {
			String nextLine = textIterator.next();
			
			if(nextLine.isEmpty()) continue; // skip line in case it's empty
			
			String[] parts = nextLine.split("::"); // :: separates English and German
			
			Document doc = new Document();
			// Two parts: English and German
			doc.add(new Field("german", parts[0], TextField.TYPE_STORED));
			doc.add(new Field("english", parts[1], TextField.TYPE_STORED));
			iwriter.addDocument(doc);
		}
		// closes iwriter
		iwriter.close();
	}

    /**
     * looks for all entries containing ww in the database 
     * @param ww wished word, which we are looking for in the database
     * @return 	List of all entries in the database which contain the wished word.
     * 			Odd numbers contain the German part, while even numbers contain the English part
     * @throws IOException
     * @throws ParseException
     */
	private ArrayList<String> searchIndex(String ww) throws IOException, ParseException {
		/* NOTE:
		 * this returns all hits for the wished word including results which are irrelevant for the
		 * search term. For example "exact" will also return results for "exactly", "cat" will return results
		 * such as "cat flap" and "domestic cat" too. And "cat flap" will return all results containing "cat"
		 * OR "flap" (separate search).
		 * This means, the results must be filtered manually.
		 */
		
		ArrayList<String> results = new ArrayList<String>();
		analyzer = new StandardAnalyzer();
		directory = FSDirectory.open(Paths.get("./dict/German/indexDirectory"));
		
		String searchWord = ww;
		
		// Search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
				
		// Parse a simple query that searches for the searchWord:
		QueryParser parser = new QueryParser("english", analyzer);
		Query query = parser.parse( searchWord );
		ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
				
		// Iterate through the results:
		for (int i = 0; i < hits.length; i++) {
			Document hitDoc = isearcher.doc(hits[i].doc);
			Iterator<IndexableField> it = hitDoc.iterator();
					
			while(it.hasNext()) {
				IndexableField f = it.next();
				String result = f.stringValue();
				results.add( result );
				//System.out.println(f.name() + " " + i + " = " + result); // for testing, prints all results
			}
		}
		
		// Close ireader and directory
		ireader.close();
		directory.close();
		
		// returns Array containing results
		return results;
	}
	
	 /**
     * return synonyms for the ww found in the de-en database
     * @param ww: WishedWord
     * @param nOW: number of words/results requested
     * @return returns array of results or null if no synonyms were
     */
	public String[] getSynonyms(String ww, int nOW) {
		leftoverResults.clear(); // needs to be cleared before any new results are gathered
		ArrayList<String> results = new ArrayList<String>();
		try {
			results = searchIndex(ww); // gets results from database
			if(results.isEmpty()) { // return null if no results were found
				return null;
			}
			
			ArrayList<String> resultArrayList = new ArrayList<String>();
	    	int j = 1; // just a counter for the loop, starting at the first English content
	    	while(j<results.size()) // only every 2nd, so it only checks English results
	        {
	    		String[] englishLine = results.get(j).split("\\|");
	    		
	    		// Searches for ww between the | symbols
	    		for(int i=0; i < englishLine.length; i++) {
	    			englishLine[i] = removeInvalidChars(englishLine[i]);
	    			
	    			/*
	    			 *  only exact matches which have one or more synonyms: (exact being ww in examples)
	    			 *  -	englishLine[i] ends with "; "+ww	(e.g.: "precise; exact)
	    			 *  -	englishLine[i] starts with ww+";"	(e.g.: "exact; precise)
	    			 *  -	englishLine[i] contains "; "+ww+";"	(e.g.: "precise; exact; accurate")
	    			 */
	    			if(	englishLine[i].endsWith("; "+ww) 
    						|| englishLine[i].startsWith(ww+";")
    						|| englishLine[i].contains("; "+ww+";") ) {
		    			
	    				// results are split at ; as they divide synonyms
		    			String[] synonyms = englishLine[i].split(";");
		    			
		    			// iterates through all synonyms and checks them before adding them to ResultArrayList or leftoverResults
		    			int x=0;
		    			while(x<synonyms.length) {
		    				if(!removeInvalidChars(synonyms[x]).equals(ww)) { // if found synonym isnt the same as ww
		    					
		    					if(x<nOW) { // adds to result if nOW results werent found yet
		    						resultArrayList.add(synonyms[x]);
		    					}
		    					else { //adds to leftover results, if already nOW results were found
		    						leftoverResults.add(synonyms[x]);
		    					}	
		    				}
		    				x++;
		    			}
		    		}
	    		}
	    		j +=2;
	        }
	    	
	    	// return null if no results were found
	    	if(resultArrayList.isEmpty()) {
	    		return null;
	    	}
	    	
	    	//return results without invalid chars
	    	return removeInvalidChars(resultArrayList);
	    	
		} catch (IOException e) {
			return null;
		} catch (ParseException e) {
			return null;
		}
		
		
	}

	
}
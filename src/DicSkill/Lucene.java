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
	
	String[] lastResults;
	
	/** CONSTRUCTOR **/
	public Lucene() {
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
				else{
					System.out.println("Already indexed");
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


			
			//returns the best results
			return getBestResults(results, nOW, ww);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

    /**
     * returns the number of perfect entries in the database
     * @param results ArrayList<String> list of all matched entries
     * @param ww wished word
     * @return
     */

    /**
     * returns perfect matches word by word in Array, returns null if no perfect is found
     * @param results ArrayList of fount entries containing the ww
     * @param ww WishedWord
     * @param numberOfWishedResults number of wished results
     * @return returns array of results or null if no perfect match was found
     */
    private String[] getPerfectMatches(ArrayList<String> results, String ww, int numberOfWishedResults)
    {
        //int existingPerfect = countPerfect(results, ww);
        ArrayList<String> resultArrayList = new ArrayList<String>();
        ArrayList<String> perfect = new ArrayList<String>();
        String[] endArray = new String[numberOfWishedResults];

        //returns line with perfect result
        int stelle = 0;
        for(int i=0; i<results.size(); i++)
        {
            //System.out.println("getperf for "+results.get(i));
            if(results.get(i).startsWith(" "+ww+";") || results.get(i).startsWith(" "+ww+" |")){
                if(i != 0 && i%2 == 1){
                    perfect.add(results.get(i-1)); //works as long as first german word is different to english word
                    stelle++;
                }
                else if(i==0){
                    perfect.add(results.get(i)); //works as long as first german word is different to english word
                    stelle+=2;
                }
                else{ //i!=0 && i%2==0
                    System.out.println("ICH DARF NICHT, MAMA!");
                }
            }
        }

        //System.out.println("HIII"+perfect.get(0));
        if(perfect.size()>0){
            for(String perfect1 : perfect) {
                String[] arr = perfect1.split(";");
                for (String str : arr) {
                    resultArrayList.add(str);
                }
            }
            //resultArrayList.remove(resultArrayList.size()-1);
        }
        else
            return null;

        for(int i=0; i<numberOfWishedResults; i++){
            if(resultArrayList.get(i).contains("{")){
                endArray[i] = resultArrayList.get(i).substring(0, resultArrayList.get(i).indexOf("{"));
            }
            else if(resultArrayList.get(i).contains("|")){
                endArray[i] = resultArrayList.get(i).substring(0, resultArrayList.get(i).indexOf("|"));
            }
            else{
                endArray[i] = resultArrayList.get(i);
            }
        }

        return  endArray;
    }

    /**
     * returns not perfect matches
     * @param results ArrayList of fount entries containing the ww
     * @param ww WishedWord
     * @param numberOfWishedResults number of wished results
     * @return returns array of results or null if no perfect match was found
     */
    private String[] getNotPerfMatches(ArrayList<String> results, String ww, int numberOfWishedResults)
    {
        String[] endArray= new String[numberOfWishedResults];
        if(results.size()>0){
            for(int i = 0; i<numberOfWishedResults; i++){
                if(results.get(i).contains("{")){
                    endArray[i] = results.get(i).substring(0, results.get(i).indexOf("{"));
                }
                else if(results.get(i).contains("|")){
                    endArray[i] = results.get(i).substring(0, results.get(i).indexOf("|"));
                }
                else{
                    endArray[i] = results.get(i);
                }
            }
        }
        else
            return null;

        return endArray;
    }

    /**
     * sorts the results to display the best results. best results are which match ww 100%, after that combinations of ww with other words
     * @param results ArrayList of all entries found in the dictionary containing the ww
     * @param nOW number of words, tells the functions how many words are wished
     * @param ww wished word we are looking for in the data base
     * @return returns Array of results or null if word was not found
     */
	private String[] getBestResults(ArrayList<String> results, int nOW, String ww) {


		String[] perfectResults;
		String[] notPerfectResults;
        String[] bestResults = new String[nOW];
        int startForNonPerfectTranslations = 0;

        perfectResults = getPerfectMatches(results, ww, nOW);


        //check if onlyPerf is enough or if we need non-perfect translations
        if(perfectResults != null && perfectResults.length == nOW) {
            System.out.println("enough perfect");
            return perfectResults;
        }
        else if(perfectResults == null || perfectResults.length < nOW){
            System.out.println("not enough perfect");
            if(perfectResults != null) {
                int counter = 0;
                for (String a : perfectResults) {
                    bestResults[counter] = a;
                    counter++;
                }
                startForNonPerfectTranslations = perfectResults.length;

                //fill with bs translations
                notPerfectResults = getNotPerfMatches(results, ww, nOW-perfectResults.length);
            }
            else{//if perfectResults == null
                startForNonPerfectTranslations = 0;
                //fill with bs translations
                notPerfectResults = getNotPerfMatches(results, ww, nOW-0);
            }

            if(notPerfectResults != null){
                for(String a : notPerfectResults){
                    bestResults[startForNonPerfectTranslations] = a;
                    startForNonPerfectTranslations++;
                }
            }
            else return null;
        }
        else{
            //Should never happen
            System.out.println("HELLO SOMETING WRONG WITH perfectResults");
            for(int i=0; i<nOW; i++){
                bestResults[i] = perfectResults[i];
            }
        }

        /*
        int counter = 0;
        while(counter < nOW && counter < perfectResults.length){
            bestResults[counter] = perfectResults[counter];
            counter++;
        }*/

        /*
        if(existingPerfect < nOW) { //if less perfect translations than wished trans exist, we need "non-perfect" translations, AFTER the perfect translations
            perfect = new String[existingPerfect]; //create Array for perfect translations
            startForNonPerfectTranslations = existingPerfect-1;//starting point for non perfect translations
        }
        */

		// searches for exact match and sets it as first result upon finding it
		/*
		int i=1;

		Iterator<String> resultIterator1 = results.iterator();
		while(resultIterator1.hasNext() && i<nOW) {
			String result = resultIterator1.next();

			if(result.equals(ww)) {
				//bestResults[0] = resultIterator1.next();
			}
			i++;
		}
		*/
		//resets i depending on weather or not an exact match was found
		/*if(bestResults[0].isEmpty()) {
			i = 0;
		}
		else {
			i = 1;
		}
		// fills up bestResults with other results
		Iterator<String> resultIterator2 = results.iterator();
		while(resultIterator2.hasNext() && i<nOW) {
			String result = resultIterator2.next();
			if(!result.equals(bestResults[0]))
				bestResults[i++] = result;
			i++;
		}*/
		
		//lastResults = bestResults;
		
		return bestResults;
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
				System.out.println(f.name() + " " + i + " = " + result);
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
	
}

package DicSkill;

import java.util.ArrayList;
import java.util.Arrays;
import rita.RiWordNet;

/**
 * 27.06.2018
 * NEW:
 * - 	improved ritas output for the scrabbe function (e.g. only returns results without special characters)
 * @author Lia
 */
/**
 * 26.06.2018
 * NEW:
 * - 	Added function to get more results from last result and needed leftoverResults array for this
 * -	function extractResult now also handles leftoverResults.
 * -	giveSynonyms now also gets results from lucene
 * @author Lia
 */

/**
 * 15.06.2018
 * TO DO:
 * - After receiving output, evaluate it according to preferred category
 * NEW:
 * - Added Lucene and function translation
 * - Bug fixes
 *
 * @author Walter
 *
 */

/**
 * 01.06.2018
 * TO DO:
 * - Implement Lucene to implement translate
 * - After receiving output, evaluate it according to preferred category
 * NEW:
 * - Added function example.
 * - Fixed a couple of bugs concerning ArraySize.
 * - Deleted unused functions.
 *
 * @author Walter
 *
 */

/**
 * 21.05.2018
 * TO DO:
 * - Get a translation database.
 * - Improve database output.
 * - Add a category for words.
 * NEW:
 * - Implemented all functions except for translate.
 *
 * @author Walter
 */

/**
 * 29.04.2018
 * TO DO:
 * -	make use of database.
 * -	implement all functions.
 * -	function translate is a rough template of what the functions (except extractResult) should look like.
 * NEW:
 * - Created template for this class.
 *
 * @author Lia
 *
 */



/**
 * DatabaseCommunicator gets function specific calls. The databases will be searched accordingly
 * and the result will be extracted. Then the answer will be returned to the MessageManager.
 *
 * If there is no answer or an empty answer, null will be returned for every function.
 *
 * Used databases with API:
 *  - WordNet database with Rita
 *  - Lucene
 */
public class DatabaseCommunicator {

	/** VARIABLES **/
	private final RiWordNet ritaDB;
	private final Lucene lucene;
	private String pos;                // Rita specific: PartsOfSpeech. e.g.: noun, adjective, verb ...
	private String[] leftoverResults;
	private int MAX_RESULTS = 100; // otherwise there will be no leftover result for scrabble functions

	/** Constructor **/
	public DatabaseCommunicator() {

		ritaDB = new RiWordNet("./dict/English/");
		lucene = new Lucene();
		pos = null;
	}

	/** Methods **/

	/**
	 * Shortens the Array to fit the NOW. If the output size is smaller,
	 * the NOW will be ignored.
	 * If the query did not provide an answer or an empty array null will be returned.
	 *
	 * @param dbOutput answer of the database
	 * @return String[] shortened Array
	 */
	private String[] extractArray(String dbOutput[], int NOW) {

		if(isQueryBad(dbOutput, NOW)) {
			return null;
		}
		
		String[] returnArray;

		if(NOW <= dbOutput.length) {
			returnArray = new String[NOW];
			
			// saves leftover Results in leftoverResults array
			leftoverResults = new String[dbOutput.length-returnArray.length];
			System.arraycopy(dbOutput, returnArray.length, leftoverResults, 0, dbOutput.length-returnArray.length);
		}
		else {
			returnArray = new String[dbOutput.length];
			leftoverResults = null;
		}
		System.arraycopy(dbOutput, 0, returnArray, 0, returnArray.length);

		return returnArray;
	}

	/**
	 * In one of the following cases the array is useless and mus return null.
	 * If there is no output, the output is empty or the numberOfWords is equal to or smaller than 0;
	 *
	 * @param dbOutput query result from the database
	 * @param NOW numberOfWords for the ouput
	 * @return boolean true if query result is bad
	 */
	private boolean isQueryBad(String dbOutput[], int NOW) {

		if(dbOutput == null) {
			return true;
		}
		else if(dbOutput.length == 0) {
			return true;
		}

		if(NOW <= 0) {
			return true;
		}

		return false;
	}

	/**
	 * ---WIP--- Translate provides the translation to the wished word
	 *
	 * @param ww  wishedWor
	 * @param NOW numberOfWords
	 * @return String[] databaseOutput[]
	 */
	public String[] translate(String ww, int NOW) {

		String result[];

		result = lucene.translate(ww, NOW);
		result = extractArray(result, NOW);
		leftoverResults = lucene.getLeftoverResults();

		return result;
	}

	/**
	 * Define provides a definition of the wished word.
	 * Rita offers several definitions, the first one seems to be the best.
	 *
	 * @param ww  wishedWord
	 * @param NOW numberOfWords
	 * @return String[] databaseOutput[]
	 */
	public String[] define(String ww, int NOW) {

		if (!ritaDB.exists(ww)) {
			return null;
		}

		String result[] = null;

		try {
			pos = ritaDB.getBestPos(ww);
			result = ritaDB.getAllGlosses(ww, pos);
		}
		catch(Exception e) {}
		
		result = extractArray(result, NOW);
		
		return result;
	}


	/**
	 * GiveSynonyms provides a synonym of the wished word.
	 * (WordNet does not seem to have a lot of synonyms).
	 *
	 * @param ww  wishedWord
	 * @param NOW numberOfWords
	 * @return String[] databaseOutput[]
	 */
	public String[] giveSynonyms(String ww, int NOW) {

		ArrayList<String> listResults = new ArrayList<String>();
		
		String luceneResult[] = lucene.getSynonyms(ww);
		
		if (!ritaDB.exists(ww) && luceneResult.length==0) {
			return null;
		}
		else if (!ritaDB.exists(ww)) {
			return luceneResult;
		}

		String ritaResult[];
		pos = ritaDB.getBestPos(ww);
		ritaResult = ritaDB.getAllSimilar(ww, pos);

		// adds ritas and lucenes results to end result, while making sure there are no duplicates
		String result[] = new String[ritaResult.length+luceneResult.length];
		for(int i=0; i<ritaResult.length; i++) {
			listResults.add(ritaResult[i]);
			result[i] = ritaResult[i];
		}
		for(int i=0; i<luceneResult.length; i++) {
			if(!listResults.contains(luceneResult[i])) {
				listResults.add(luceneResult[i]);
				result[i+ritaResult.length] = luceneResult[i];
			}
		}
		
		result = extractArray(result, NOW);

		return result;
	}

	/**
	 * GiveExamples provides an example for the wished word. Our database does not provide many example sentences
	 * and therefore, this may return null as a result a lot of the times.
	 *
	 * @param ww  wishedWord
	 * @param NOW numberOfWords
	 * @return String[] databaseOutput[]
	 */
	public String[] giveExamples(String ww, int NOW) {

		if (!ritaDB.exists(ww)) {
			return null;
		}

		String result[];

		pos = ritaDB.getBestPos(ww);

		result = ritaDB.getAllExamples(ww, pos);
		result = extractArray(result, NOW);

		return result;
	}

	/**
	 * Spell provides the spelling of a word.
	 * This function does not need any database
	 *
	 * @param ww wishedWord
	 * @return String[] databaseOutput[]
	 */
	public String[] spell(String ww) {

		int length = ww.length();
		String result[] = new String[length];

		for (int i = 0; i < length; i++) {
			result[i] = ww.substring(i, i + 1);
		}

		return result;
	}

	/**
	 * Returns words that start with [letters]
	 *
	 * @param letters the word starts with
	 * @param NOW     numberOfWords
	 * @return String[] result
	 */
	public String[] scrabble_start(String letters, int NOW) {

		String result[];
		
		// adds results from rita to arraylist, because arraylist can be searched better
		pos = ritaDB.getBestPos(letters);
		ArrayList<String> unfilteredResults = new ArrayList<String>(Arrays.asList(ritaDB.getStartsWith(letters, pos, MAX_RESULTS)));
		
		/*
		 * Results with 
		 * 		-	special characters (e.g.: "a.e.")
		 * 		-	a space after the sequence of letters (e.g.: "a horizon")
		 *	are removed from the ArrayList of results
		 */
		int counter = 0; // using this instead of for each loop because of java.util.ConcurrentModificationException
		while(counter < unfilteredResults.size()) {
			// if the result is not a sequence of letters which starts with letters, delete the result
			if(!unfilteredResults.get(counter).matches("^"+letters+"[A-Za-z][ A-Za-z]*$")) {
				unfilteredResults.remove(counter);
			}
			else {
				counter++;
			}
		}
		
		// results are copied to String[] results
		result = new String[unfilteredResults.size()];
		result = unfilteredResults.toArray(result);
		
		// adds leftover results to String[] leftover results and NOW results
		result = extractArray(result, NOW);

		return result;
	}

	/**
	 * returns words that ends with [letters]
	 *
	 * @param letters the word starts with
	 * @param NOW     numberOfWords
	 * @return String[] result
	 */
	public String[] scrabble_end(String letters, int NOW) {

		String result[];
		
		// adds results from rita to arraylist, because arraylist can be searched better
		pos = ritaDB.getBestPos(letters);
		ArrayList<String> unfilteredResults = new ArrayList<String>(Arrays.asList(ritaDB.getStartsWith(letters, pos, MAX_RESULTS)));
		
		/*
		 * Results with 
		 * 		-	special characters (e.g.: "a.e.")
		 * 		-	a space before the sequence of letters (e.g.: "xyz a")
		 *	are removed from the ArrayList of results
		 */
		int counter = 0; // using this instead of for each loop because of java.util.ConcurrentModificationException
		while(counter < unfilteredResults.size()) {
			// if the result is not a sequence of letters which starts with letters, delete the result
			if(!unfilteredResults.get(counter).matches("^[ A-Za-z]*[A-Za-z]+"+letters+"$")) {
				unfilteredResults.remove(counter);
			}
			else {
				counter++;
			}
		}
		
		// results are copied to String[] results
		result = new String[unfilteredResults.size()];
		result = unfilteredResults.toArray(result);
		
		// adds leftover results to String[] leftover results and NOW results
		result = extractArray(result, NOW);

		return result;
	}

	/**
	 * returns words that contains [letters]
	 *
	 * @param letters the word starts with
	 * @param NOW     numberOfWords
	 * @return String[] result
	 */
	public String[] scrabble_contain(String letters, int NOW) {

		String result[];
		
		// adds results from rita to arraylist, because arraylist can be searched better
		pos = ritaDB.getBestPos(letters);
		ArrayList<String> unfilteredResults = new ArrayList<String>(Arrays.asList(ritaDB.getStartsWith(letters, pos, MAX_RESULTS)));
		
		/*
		 * Results with (examples for letters=a)
		 * 		-	special characters (e.g.: "a.e.")
		 * 		-	a space before or after the sequence of letters (e.g.: "a horizon")
		 *	are removed from the ArrayList of results
		 */
		int counter = 0; // using this instead of for each loop because of java.util.ConcurrentModificationException
		while(counter < unfilteredResults.size()) {
			// if the result is not a sequence of letters which starts with letters, delete the result
			if(!unfilteredResults.get(counter).matches("^[A-Za-z ]*[A-Za-z]+"+letters+"[A-Za-z]+[ A-Za-z]*$")) {
				unfilteredResults.remove(counter);
			}
			else {
				counter++;
			}
		}
		
		// results are copied to String[] results
		result = new String[unfilteredResults.size()];
		result = unfilteredResults.toArray(result);
		
		// adds leftover results to String[] leftover results and NOW results
		result = extractArray(result, NOW);

		return result;
	}

	public String[] getMoreResults(int NOW) {
		
		if(leftoverResults == null || leftoverResults.length == 0) {
			return null;
		}
		
		String[] returnArray;

		if(NOW < leftoverResults.length) {
			returnArray = new String[NOW];
			
			// copies NOW results from leftoverResults to returnArray, then shortens leftoverResults
			System.arraycopy(leftoverResults, 0, returnArray, 0, returnArray.length);
			String[] tmpArray = new String[leftoverResults.length-NOW];
			System.arraycopy(leftoverResults, returnArray.length, tmpArray, 0, tmpArray.length);
			leftoverResults = tmpArray.clone();
		}
		else {
			// "copies" all results from leftoverResults to returnArray and then deletes content of leftoverResults
			returnArray = leftoverResults.clone();
			leftoverResults = null;
		}

		return returnArray;
	}
}
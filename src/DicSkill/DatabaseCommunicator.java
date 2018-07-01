package DicSkill;

import java.util.ArrayList;
import java.util.Arrays;
import rita.RiWordNet;

/**
 * 28.06.2018
 * NEW:
 * -	improved code documentation
 *	@author Lia, Walter
 */

/**
 * 27.06.2018
 * NEW:
 * - 	improved Ritas output for the scrabble function (e.g. only returns results without special characters)
 * -	added categories, which do not work with the database, but may improve the output
 * -	categories are now considered for output
 * @author Lia
 */

/**
 * 26.06.2018
 * TO DO:
 * - 	After receiving output, evaluate it according to preferred category
 * NEW:
 * - 	Added function to get more results from last result (added leftoverResults array for this)
 * -	function extractResult now also handles leftoverResults
 * -	giveSynonyms now also gets results from lucene
 * @author Lia
 */

/**
 * 15.06.2018
 * TO DO:
 * - 	After receiving output, evaluate it according to preferred category
 * NEW:
 * - 	Added Lucene and function translation
 * - 	Bug fixes
 * @author Walter
 */

/**
 * 01.06.2018
 * TO DO:
 * - 	Implement Lucene to implement translate
 * - 	After receiving output, evaluate it according to preferred category
 * NEW:
 * - 	Added function example
 * - 	Fixed a couple of bugs concerning ArraySize
 * - 	Deleted unused functions
 * @author Walter
 */

/**
 * 21.05.2018
 * TO DO:
 * - 	Get a translation database
 * - 	Improve database output
 * - 	Add a category for words
 * NEW:
 * - 	Implemented all functions except for translate
 * @author Walter
 */

/**
 * 29.04.2018
 * TO DO:
 * -	make use of database
 * -	implement all functions
 * -	function translate is a rough template of what the functions (except extractResult) should look like
 * NEW:
 * - 	Created template for this class
 * @author Lia
 */

/**
 * DatabaseCommunicator gets function-specific calls. The databases will be searched accordingly if needed
 * and the result will be extracted. Then the answer will be returned to the MessageManager.
 *
 * If there is no answer or an empty answer, null will be returned.
 *
 * Used databases with API:
 *  - WordNet database with Rita
 *  - Lucene
 */
public class DatabaseCommunicator {

	/** VARIABLES **/
	private final RiWordNet ritaDB;
	private final Lucene lucene;
	private String pos;                	// Rita specific: PartsOfSpeech. e.g.: noun, adjective, verb ...
	private String[] leftoverResults;	// otherwise there will be no leftover result for spelling
	
	/*
	 *  need MAX_RESULTS to improve performance for scrabble functions significantly. 
	 *  Having no limit, will usually result in faster response, however there are 
	 *  several seconds(!) waiting time upon the first request which would not meet the requirements.
	 */
	private int MAX_RESULTS = 100; 		

	/** CONSTRUCTOR **/
	public DatabaseCommunicator() {

		ritaDB = new RiWordNet("./dict/English/");
		lucene = new Lucene();
		pos = null;
	}

	/** METHODS **/

	/**
	 * Shortens the results to fit the number of results requested.
	 * If the query did not provide an answer or an empty array null will be returned.
	 * If there are more results available than NOW, they will be added to leftoverResults.
	 *
	 * @param dbOutput result from the database
	 * @return String[] new adapted Array
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
	 * In one of the following cases the array is useless and must return null:
	 * -	there is no output, 
	 * -	the output is empty or 
	 * -	the numberOfWords is equal to or smaller than 0
	 *
	 * @param dbOutput is the result from the database
	 * @param NOW stands for numberOfWords for the ouput
	 * @return boolean true if query result is bad, otherwise false
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
	 * Translate provides the translation for the wished word using lucene.
	 * Updates leftoverResults in the process.
	 *
	 * @param ww  wishedWord
	 * @param NOW numberOfWords
	 * @return String[] returns translation(s) for the ww or null
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
	 * However, if there are preferred categories, the results will be sorted by definitions matching the category first.
	 *
	 * @param ww  wishedWord
	 * @param NOW numberOfWords
	 * @return String[] databaseOutput[]
	 */
	public String[] define(String ww, int NOW, Context context) {

		if (!ritaDB.exists(ww)) {
			return null;
		}

		String result[] = null;

		try {
			pos = ritaDB.getBestPos(ww);
			result = ritaDB.getAllGlosses(ww, pos);
		}
		catch(Exception e) {}
		
		result = sortByPrefCategoryFirst(context, result);
		
		result = extractArray(result, NOW);
		
		return result;
	}

	/**
	 * GiveSynonyms provides one or more synonyms for the wished word.
	 *
	 * WordNet only provides a limited amount of synonyms. Lucene adds synonyms found in the de-en database. The results are then combined.
	 *
	 * @param ww  wishedWord
	 * @param NOW numberOfWords
	 * @return String[] synonyms for the wished word
	 */
	public String[] giveSynonyms(String ww, int NOW) {

		ArrayList<String> listResults = new ArrayList<String>();
		
		String luceneResult[] = lucene.getSynonyms(ww);
		
		if (!ritaDB.exists(ww) && luceneResult == null) {
			return null;
		}
		else if (!ritaDB.exists(ww)) {
			return luceneResult;
		}

		String ritaResult[];
		pos = ritaDB.getBestPos(ww);
		ritaResult = ritaDB.getAllSimilar(ww, pos);


		// adds ritas and lucenes results to end result, while making sure there are no duplicates
		String[] result = null;

		if(luceneResult != null) {
			result = new String[ritaResult.length + luceneResult.length];

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
		}
		else {
			result = new String[ritaResult.length];

			for(int i=0; i<ritaResult.length; i++) {
				listResults.add(ritaResult[i]);
				result[i] = ritaResult[i];
			}
		}

		result = extractArray(result, NOW);

		return result;
	}

	/**
	 * GiveExamples provides one or more example sentences for the wished word. The database does not provide many example sentences
	 * and therefore, this function may return null as a result in many cases.
	 *
	 * @param ww  wishedWord
	 * @param NOW numberOfWords
	 * @return String[] examples for the wished word
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
	 * "Spell" provides the spelling of a word.
	 * This function does not need any databases.
	 *
	 * @param ww wishedWord
	 * @return String[] each result[index] contains one letter of the ww
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
	 * Returns words that start with a sequence of letters
	 * Results with
	 *		-	special characters (e.g.: "a.e.")
	 * 		-	a space after the sequence of letters (e.g.: "a horizon")
	 * are removed from the ArrayList of results
	 *
	 * @param letters the word starts with
	 * @param NOW numberOfWords
	 * @return String[] words which start with the requested letters
	 */
	public String[] scrabble_start(String letters, int NOW) {

		String result[];

		pos = ritaDB.getBestPos(letters);
		ArrayList<String> unfilteredResults = new ArrayList<String>(Arrays.asList(ritaDB.getStartsWith(letters, pos, MAX_RESULTS)));

		int counter = 0;

		while(counter < unfilteredResults.size()) {

			/*
			 * Regex explained for those new to regex:	"^"+letters+"[A-Za-z][ A-Za-z]*$"
			 * ^+letters means the String starts with the string letters
			 * [A-Za-z] means an alphabetical letter must follow
			 * [ A-Za-z]* means any number of alphabetical letters or spaces can follow
			 * $ marks the end of the string
			 */
			if(!unfilteredResults.get(counter).matches("^"+letters+"[A-Za-z][ A-Za-z]*$")) {
				unfilteredResults.remove(counter);
			}
			else {
				counter++;
			}
		}

		result = new String[unfilteredResults.size()];
		result = unfilteredResults.toArray(result);

		result = extractArray(result, NOW);

		return result;
	}

	/**
	 * Returns words that end with a sequence of letters
	 * Results with
	 *		-	special characters (e.g.: "a.e.")
	 * 		-	a space after the sequence of letters (e.g.: "a horizon")
	 * are removed from the ArrayList of results
	 *
	 * @param letters the word starts with
	 * @param NOW numberOfWords
	 * @return String[] words which end with the requested letters
	 */
	public String[] scrabble_end(String letters, int NOW) {

		String result[];

		pos = ritaDB.getBestPos(letters);
		ArrayList<String> unfilteredResults = new ArrayList<String>(Arrays.asList(ritaDB.getStartsWith(letters, pos, MAX_RESULTS)));

		int counter = 0;

		while(counter < unfilteredResults.size()) {

			/*
			 * Regex explained for those new to regex:	"^[ A-Za-z]*[A-Za-z]+"+letters+"$"
			 * ^[ A-Za-z]* means the String starts with any number of alphabetical letters or spaces
			 * [A-Za-z]+ means there it at least one alphabetical letter (no space)
			 * +"+letters+" means the string letters must follow
			 * $ marks the end of the string
			 */
			if(!unfilteredResults.get(counter).matches("^[ A-Za-z]*[A-Za-z]+"+letters+"$")) {
				unfilteredResults.remove(counter);
			}
			else {
				counter++;
			}
		}

		result = new String[unfilteredResults.size()];
		result = unfilteredResults.toArray(result);

		result = extractArray(result, NOW);

		return result;
	}

	/**
	 * Returns words which contain a sequence of letters
	 * Results with
	 *		-	special characters (e.g.: "a.e.")
	 * 		-	a space after the sequence of letters (e.g.: "a horizon")
	 * are removed from the ArrayList of results
	 *
	 * @param letters the word starts with
	 * @param NOW numberOfWords
	 * @return String[] words which contain the requested letters
	 */
	public String[] scrabble_contain(String letters, int NOW) {

		String result[];

		pos = ritaDB.getBestPos(letters);
		ArrayList<String> unfilteredResults = new ArrayList<String>(Arrays.asList(ritaDB.getStartsWith(letters, pos, MAX_RESULTS)));

		int counter = 0;

		while(counter < unfilteredResults.size()) {

			/*
			 * Regex explained for those new to regex:	"^[A-Za-z ]*[A-Za-z]+"+letters+"[A-Za-z]+[ A-Za-z]*$"
			 * ^[ A-Za-z]* means the String starts with any number of alphabetical letters or spaces
			 * [A-Za-z]+ means there it at least one alphabetical letter (no space)
			 * +letters+" means the string letters must follow
			 * $ marks the end of the string
			 */
			if(!unfilteredResults.get(counter).matches("^[A-Za-z ]*[A-Za-z]+"+letters+"[A-Za-z]+[ A-Za-z]*$")) {
				unfilteredResults.remove(counter);
			}
			else {
				counter++;
			}
		}

		result = new String[unfilteredResults.size()];
		result = unfilteredResults.toArray(result);

		result = extractArray(result, NOW);

		return result;
	}

	/**
	 * This function returns more results for the last request. It will remove the returned results from the leftoverResults array.
	 *
	 * @param NOW number of words
	 * @return results for the last request
	 */
	public String[] getMoreResults(int NOW) {
		
		if(leftoverResults == null || leftoverResults.length == 0) {
			return null;
		}
		
		String[] returnArray;

		if(NOW < leftoverResults.length) {
			returnArray = new String[NOW];

			System.arraycopy(leftoverResults, 0, returnArray, 0, returnArray.length);
			String[] tmpArray = new String[leftoverResults.length-NOW];
			System.arraycopy(leftoverResults, returnArray.length, tmpArray, 0, tmpArray.length);
			leftoverResults = tmpArray.clone();
		}
		else {
			returnArray = leftoverResults.clone();
			leftoverResults = null;
		}

		return returnArray;
	}
	
	/**
	 * This function sorts the results by putting definitions, which contain the preferred category first.
	 *
	 * @param context to get the preferred categories
	 * @param results, which contains the results received by the database
	 * @return results sorted by preferred category first
	 */
	private String[] sortByPrefCategoryFirst(Context context, String[] results) {
		if(results == null) {
			return null;
		}
		if(context.getPreferredCategory() == null || context.getPreferredCategory().isEmpty()) {
			return results;
		}
		
		String[] prefResultsFirst = new String[results.length];
		int prefResultsCounter = 0;
		
		// adds all definitions which include the word category to results
		for(String category : context.getPreferredCategory() ) {
			
			for(int i=0; i<results.length; i++) {
				if(results[i].matches(".*"+category+".*")) {
					prefResultsFirst[prefResultsCounter++] = results[i];
					results[i] = "";
					
				}
			}
			
		}
		
		// adds all other results afterwards
		for(int i=0; i<results.length; i++) {
			if(!results[i].equals("")) {
				prefResultsFirst[prefResultsCounter++] = results[i];
			}
		}
		
		return prefResultsFirst;
	}
}
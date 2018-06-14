package DicSkill;

/**
 * 01.06.2018
 * TO DO:
 * - Implement Lucene to implement translate
 * - After recieving output, evaluate it according to preffered category
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

import rita.RiWordNet;

/**
 * DatabaseCommunicator gets function specific calls. The databases will be searched accordingly
 * and the result will be extracted. Then the answer will be returned to the Messagemanager.
 *
 * Used databases with API:
 *  - WordNet database with Rita
 *  - (Lucene)
 */
public class DatabaseCommunicator {

	/**
	 * VARIABlES
	 **/
	private RiWordNet RitaDB;
	private String pos;                // Rita specific: PartsOfSpeach. e.g.: noun, adjective, verb ...

	/**
	 * Constructor
	 **/
	public DatabaseCommunicator() {

		RitaDB = new RiWordNet("./dict/English/");
		pos = null;
	}

	/**
	 * Methods
	 **/

	/**
	 * Shortens the Array to fit the NOW. If the output size is smaller,
	 * the NOW will be ignored.
	 *
	 * @param dbOutput
	 * @return String[] shortened Array
	 */
	private String[] extractArray(String dbOutput[], int NOW) {

		String[] returnArray;

		if(NOW < 0) {
			return null;
		}

		if(NOW < dbOutput.length) {
			returnArray = new String[NOW];
		}
		else {
			returnArray = new String[dbOutput.length];
		}

		for (int i=0; i < returnArray.length; i++) {
			returnArray[i] = dbOutput[i];
		}

		return returnArray;
	}

	/**
	 * ---WIP--- Translate provides the translation to the wished word
	 *
	 * @param ww  wishedWor
	 * @param NOW numberOfWords
	 * @return String[] databaseOutput[]
	 */
	public String[] translate(String ww, int NOW) {

		String result[] = null;

		// Lucene s job

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

		if (!RitaDB.exists(ww)) {
			return null;
		}

		String result[];

		pos = RitaDB.getBestPos(ww);
		result = RitaDB.getAllGlosses(ww, pos);

		result = extractArray(result, NOW);

		return result;
	}

	// returns synonyms for the wished word

	/**
	 * GiveSynonyms provides a synonym of the wished word.
	 * (WordNet does not seem to have a lot of synonyms).
	 *
	 * @param ww  wishedWord
	 * @param NOW numberOfWords
	 * @return String[] databaseOutput[]
	 */
	public String[] giveSynonyms(String ww, int NOW) {

		if (!RitaDB.exists(ww)) {
			return null;
		}

		String result[];

		pos = RitaDB.getBestPos(ww);
		result = RitaDB.getAllSimilar(ww, pos);

		result = extractArray(result, NOW);

		return result;
	}

	/**
	 * GiveExamples provides an example to the wished word.
	 *
	 * @param ww  wishedWord
	 * @param NOW numberOfWords
	 * @return String[] databaseOutput[]
	 */
	public String[] giveExamples(String ww, int NOW) {

		if (!RitaDB.exists(ww)) {
			return null;
		}

		String result[];

		pos = RitaDB.getBestPos(ww);
		result = RitaDB.getAllExamples(ww, pos);

		result = extractArray(result, NOW);

		return result;
	}

	/**
	 * Spell provides the spelling of a word.
	 * This function does not call a database.
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

		pos = RitaDB.getBestPos(letters);
		result = RitaDB.getStartsWith(letters, pos, NOW);

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

		pos = RitaDB.getBestPos(letters);
		result = RitaDB.getEndsWith(letters, pos, NOW);

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

		pos = RitaDB.getBestPos(letters);
		result = RitaDB.getContains(letters, pos, NOW);

		result = extractArray(result, NOW);

		return result;
	}
}
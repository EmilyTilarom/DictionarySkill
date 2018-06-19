package DicSkill;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 15.06.2018
 * TO DO:
 * -	consider category in code? -> in DatabaseCommunicator?
 * NEW:
 * -	bug fixes
 * @author Walter, Adrian
 */

/**
 * 10.06.2018
 * TO DO:
 * -	consider category in code? -> in DatabaseCommunicator?
 * NEW:
 * -	documentation improved
 * -	bug fixes
 * @author Walter
 */

/**
 * 27.05.2018
 * TO DO:
 * -	consider category in code? -> in DatabaseCommunicator?
 * NEW:
 * -	added: function shortenWishedWord (shortens wished word by one word)
 * -	when ww ends is taken into account
 * -	function to add/remove preferred cat
 * @author Lia
 */

/**
 * 23.05.2018
 * TO DO:
 * -	finish function findWishedWord
 * ->	take into account when ww ends
 * -	consider category in code
 * NEW:
 * -	added: example, settings, whatcanyoudo and adjustments as a result
 * -	findFunction function finished
 * -	added function to change settings
 * @author Lia
 */

/**
 * 29.04.2018
 * TO DO:
 * -	finish function findWishedWord
 * -	finish function findFunction
 * -	finish setting all keywords in constructor
 * -	consider category in code
 * -	add functions to change settings
 * @author Lia
 */

/**
 * MessageManager is the first class to receive the users query.
 * It then proceeds to prepare the message, searches for the wished word and the appropriate functions.
 * When an answer is returned from the DatabaseCommunicator, MessageManager creates a humanly understandable answer.
 */
public class MessageManager {
	
	/** VARIABLES **/
    // Keywords define which function will be called.
	private ArrayList<String> keywords_translation;
	private ArrayList<String> keywords_definition;
	private ArrayList<String> keywords_synonyms;
	private ArrayList<String> keywords_scrabble_start;
	private ArrayList<String> keywords_scrabble_end;
	private ArrayList<String> keywords_scrabble_contain;
	private ArrayList<String> keywords_spelling;
	private ArrayList<String> keywords_example;
	
	private ArrayList<String> keywords_setting;
	private ArrayList<String> keywords_whatCanYouDo;
	private ArrayList<String> keywords_changePrefCat;

	/** Constructor **/
	public MessageManager() {
		
		// All keywords are set here
		
		// TRANSLATION
		keywords_translation = new ArrayList<String>();
		keywords_translation.add("translate");
		keywords_translation.add("translation");
		keywords_translation.add("in German");
		
		// DEFINITION
		keywords_definition = new ArrayList<String>();
		keywords_definition.add("define");
		keywords_definition.add("definition");
		keywords_definition.add("meaning");
		keywords_definition.add("mean");
		
		// SYNONYMS
		keywords_synonyms = new ArrayList<String>();
		keywords_synonyms.add("synonym");
		keywords_synonyms.add("synonyms");
		
		// SCRABBLE START WITH
		keywords_scrabble_start = new ArrayList<String>();
		keywords_scrabble_start.add("start");
		
		// SCRABBLE END WITH
		keywords_scrabble_end = new ArrayList<String>();
		keywords_scrabble_end.add("end");
				
		// SCRABBLE CONTAIN WITH
		keywords_scrabble_contain = new ArrayList<String>();
		keywords_scrabble_contain.add("contain");
		
		// SPELLING
		keywords_spelling = new ArrayList<String>();
		keywords_spelling.add("spell");
		keywords_spelling.add("spelling");
		
		// EXAMPLE
		keywords_example = new ArrayList<String>();
		keywords_example.add("example"); 
		
		// SETTINGS
		keywords_setting = new ArrayList<String>();
		keywords_setting.add("change number of words");
		keywords_setting.add("set number of words");
		keywords_setting.add("change number of results");
		keywords_setting.add("set number of results");
		
		// WHAT CAN YOU DO?
		keywords_whatCanYouDo = new ArrayList<String>();
		keywords_whatCanYouDo.add("what can you do");
		
		// CHANGE PREFERRED CATEGORIES
		keywords_changePrefCat = new ArrayList<String>();
		keywords_changePrefCat.add("preferred category");
		keywords_changePrefCat.add("preferred categories");
	}

	/** Methods **/

    /**
	 * This method is the primary method for the interface. A message is received and
	 * will be decoded. Depending on the result the appropriate function will be called.
	 *
     * Also updates the context in the process.
	 *
	 * Error messages will be returned when neither a function or a wishedWord was found.
	 *
	 * While a result for the wished word (consisting of multiple(!) words) could not be found,
	 * the wished word will be shortened (via shortenWishedWord()) by one word and the search for a result will be repeated.
	 * When the wished word is null, it means that a result could not be found for the wished word.
     *
     * @param msg Message from the user
     * @param settings
     * @param dbC
     * @param context
     * @return answer for the user
     */
	public String decodeMsg(String msg, Settings settings, DatabaseCommunicator dbC, Context context) {

		if(msg == null)
			return "Sorry, I did not understand you.";

		String wishedWord;
		Function function;
		String[] result; 		// the result received from the DatabaseCommunicator

		wishedWord = findWishedWord(msg, context);
		function = findFunction(msg);

		if(function == null)
			return "Sorry, I don't know which function you are asking for.";

		do {
			switch(function) {
				
				case TRANSLATION: 
					result = dbC.translate(wishedWord, settings.getNOW_translation());
					break;
				case DEFINITION:
					result = dbC.define(wishedWord, settings.getNOW_definition());
					break;
				case SPELLING:
					result = dbC.spell(wishedWord);
					break;
				case SYNONYMS:
					result = dbC.giveSynonyms(wishedWord, settings.getNOW_synonyms());
					break;
				case EXAMPLE:
					result = dbC.giveExamples(wishedWord, settings.getNOW_examples());
					break;
				case SCRABBLE_START:
					result = dbC.scrabble_start(wishedWord, settings.getNOW_scrabble());
					break;
				case SCRABBLE_CONTAIN:
					result = dbC.scrabble_contain(wishedWord, settings.getNOW_scrabble());
					break;
				case SCRABBLE_END:
					result = dbC.scrabble_end(wishedWord, settings.getNOW_scrabble());
					break;
				case SETTING:
					settings.setNOW(msg);
					result = new String[1];
					result[0] = "";
					break;
				case WHATCANYOUDO:
					result = new String[1];
					result[0] = "";
					break;
				case CHANGE_PREF_CAT:
					context.changePrefCat(msg);
					result = new String[1];
					result[0] = "";
					break;
				default:
					result = new String[1];
					result[0] = "";
					System.out.println("Sorry, message could not be computed.");
				}
			
			if(result == null)
				wishedWord = shortenWishedWord(wishedWord); // removes the last word from the wished word

		}while(result == null && wishedWord != null);
		
		// Updates the context
		context.setLastFunctionUsed(function);
		context.setLastWishedWord(wishedWord);

		return createMsg(context, resultToString(result));
	}

	/**
	 * Finds and returns the requested function in the message.
	 * If no function was found, null will be returned.
	 *
	 * @param msg message from the user
	 * @return Function
	 */
	private Function findFunction(String msg){
		
		// setting
		Iterator<String> setting_iterator = keywords_setting.iterator();
		while(setting_iterator.hasNext()) {
			if(msg.contains(setting_iterator.next())) {
				return Function.SETTING;
			}
		}
		
		// translation
		Iterator<String> translation_iterator = keywords_translation.iterator();
		int positionOfFunction;
		while(translation_iterator.hasNext()) {
			String tmp = translation_iterator.next();
			if(msg.contains(tmp)) {
				positionOfFunction = msg.lastIndexOf(tmp);
				return Function.TRANSLATION;
			}
		}
		
		// definition
		Iterator<String> definition_iterator = keywords_definition.iterator();
		while(definition_iterator.hasNext()) {
			String tmp = definition_iterator.next();
			if(msg.contains(tmp)) {
				positionOfFunction = msg.lastIndexOf(tmp);
				return Function.DEFINITION;
			}
		}
		
		// spelling
		Iterator<String> spelling_iterator = keywords_spelling.iterator();
		while(spelling_iterator.hasNext()) {
			if(msg.contains(spelling_iterator.next())) {
				return Function.SPELLING;
			}
		}
		
		// synonyms
		Iterator<String> synonyms_iterator = keywords_synonyms.iterator();
		while(synonyms_iterator.hasNext()) {
			if(msg.contains(synonyms_iterator.next())) {
				return Function.SYNONYMS;
			}
		}
		
		// scrabble_start
		Iterator<String> scrabble_start_iterator = keywords_scrabble_start.iterator();
		while(scrabble_start_iterator.hasNext()) {
			if(msg.contains(scrabble_start_iterator.next())) {
				return Function.SCRABBLE_START;
			}
		}
		
		// scrabble_end
		Iterator<String> scrabble_end_iterator = keywords_scrabble_end.iterator();
		while(scrabble_end_iterator.hasNext()) {
			if(msg.contains(scrabble_end_iterator.next())) {
				return Function.SCRABBLE_END;
			}
		}
		
		// scrabble_contain
		Iterator<String> scrabble_contain_iterator = keywords_scrabble_contain.iterator();
		while(scrabble_contain_iterator.hasNext()) {
			if(msg.contains(scrabble_contain_iterator.next())) {
				return Function.SCRABBLE_CONTAIN;
			}
		}
		
		// example
		Iterator<String> example_iterator = keywords_example.iterator();
		while(example_iterator.hasNext()) {
			if(msg.contains(example_iterator.next())) {
				return Function.EXAMPLE;
			}
		}
				
		// WHAT CAN YOU DO?
		Iterator<String> whatCanYouDo_iterator = keywords_whatCanYouDo.iterator();
		while(whatCanYouDo_iterator.hasNext()) {
			if(msg.contains(whatCanYouDo_iterator.next())) {
				return Function.WHATCANYOUDO;
			}
		}
		
		// change preferred category
		Iterator<String> changePrefCat_iterator = keywords_changePrefCat.iterator();
		while(changePrefCat_iterator.hasNext()) {
			if(msg.contains(changePrefCat_iterator.next())) {
				return Function.CHANGE_PREF_CAT;
			}
		}

		return null;
	}

	/**
	 * Finds and returns the wished word in the message. Everything after the function keyword will be the wished word
     * until further change in shortenWishedWord(). Example output: (define) "apple juice please".
	 *
	 * If the msg contains such a word, a substring will be created which starts at the
	 * msg.lastIndexOf the found word + the length of the word + 1
	 *
	 * If there is no (new) wished word the last used wished word will be used.
	 * In this case Context will be called.
	 *
	 * @param msg message from the user
	 * @param context
	 * @return String wishedWord that was found
	 */
	private String findWishedWord(String msg, Context context){
		
		String wishedWord;

		try {
			// *of* ; e.g. meaning/translation/spelling of
			if (msg.contains("of")) {
				wishedWord = msg.substring(msg.lastIndexOf("of") + 3);
				context.setLastWishedWord(wishedWord);
				return wishedWord;
			}
		}
		catch(Exception e) {}

		try {
			// *with*
			if(msg.contains("with")) {
				wishedWord = msg.substring(msg.lastIndexOf("with")+5);
				context.setLastWishedWord(wishedWord);
				return wishedWord;
			}
		}
		catch(Exception e) {}

		try {
			// ** keywords after which is directly followed by the ww (translate, define, spell, contain)
			if(msg.contains("translate")) {
				wishedWord = msg.substring(msg.lastIndexOf("translate")+10);
				context.setLastWishedWord(wishedWord);
				return wishedWord;
			}
		}
		catch(Exception e) {}

		try {
			if(msg.contains("define")) {
				wishedWord = msg.substring(msg.lastIndexOf("define")+7);
				context.setLastWishedWord(wishedWord);
				return wishedWord;
			}
		}
		catch(Exception e) {}

		try {
			if(msg.contains("spell")) {
				wishedWord = msg.substring(msg.lastIndexOf("spell")+6);
				context.setLastWishedWord(wishedWord);
				return wishedWord;
			}
		}
		catch(Exception e) {}

		try {
			if(msg.contains("contain")) {
				wishedWord = msg.substring(msg.lastIndexOf("contain")+8);
				context.setLastWishedWord(wishedWord);
				return wishedWord;
			}
		}
		catch(Exception e) {}

		return context.getLastWishedWord();
	}

	/**
	 * This method creates a message for the user after an appropriate answer was found.
	 * The answer depends on the used function, wished word and the result.
	 *
	 * @param context
	 * @param result output from the function
	 * @return String the final message for the user
	 */
	private String createMsg(Context context, String result) {

	    if(result == null) {
	        return "Sorry, we did not find any entry matching your query";
        }

		switch(context.getLastFunctionUsed()) {
			case TRANSLATION: 
				return "The translation of " + context.getLastWishedWord() + " is " + result;
			
			case DEFINITION:
				return "The definition is " + result;
			
			case SPELLING:
				return "" + context.getLastWishedWord() + " is spelled " + result;
			
			case SYNONYMS:
				return "Synonyms for " + context.getLastWishedWord() + " are " + result;

			case EXAMPLE:
				return "Examples for " + context.getLastWishedWord() + " are " + result;
			
			case SCRABBLE_START:
				return "Words which start with " + context.getLastWishedWord() + " are " + result;
			
			case SCRABBLE_CONTAIN:
				return "Words which contain " + context.getLastWishedWord() + " are " + result;
			
			case SCRABBLE_END:
				return "Words which end with " + context.getLastWishedWord() + " are " + result;
			
			case SETTING:
				return "";
			
			// may have to be adjusted for added functions
			case WHATCANYOUDO:
				return "The dictionary skill can give translations, definitions, synonyms, spellings, "
						+ "example sentences and change the number of words for your results.";
			
			default:
				return "Error: Message could not be created.";
		}
	}

	/**
	 * All possible outputs from the array are transformed into one String.
	 *
	 * If there is no output null will be returned.
	 *
	 * @param result from the function
	 * @return String answer
	 */
	private String resultToString(String[] result) {
		
		if(result == null) {
			return null;
		}
		
		StringBuilder allResults = new StringBuilder();

		for(int i=0; i<result.length; i++) {
			allResults.append(result[i]).append(" ");
		}
		
		return allResults.toString();
	}

	/**
	 * If the wished word consists of multiple words ( e.g: apple juice please.),
     * decodeMessage starts multiple queries. This function shortens the wished word, one word at a time.
     * Thus an adequate match can be found.
	 *
	 * If the ww is empty, null will be returned. If shortenPosTo is too small, null will be returned too.
	 *
	 * @param ww wished word
	 * @return String shortened wished word
	 */
	private String shortenWishedWord(String ww) {

		int shortenPosTo = ww.length();
		
		// finds the position of the first " " starting from the end/right of the ww
		while( shortenPosTo > 0 && !(ww.substring(shortenPosTo-1, shortenPosTo).equals(" ") )) {
			shortenPosTo--;
		}

		if(shortenPosTo < 1 || shortenPosTo == ww.length()) {
			return null;
		}
		else {
			return ww.substring(0, shortenPosTo-1);
		}
	}
}
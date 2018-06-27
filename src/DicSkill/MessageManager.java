package DicSkill;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 26.06.2018
 * NEW:
 * -	swapped keywords with regex 
 * 		->	made changes to functions findFunction, findWishedWord as a result 
 * -	added the option to ask for more results
 * -	context and settings are now saved by this class
 * -	improve createMsg depending on number of results
 * TO DO:
 * -	delete preferred categories or make regex expressions
 * @author Lia
 */

/**
 * 15.06.2018
 * NEW:
 * -	bug fixes
 * @author Walter, Adrian
 */

/**
 * 10.06.2018
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
	String regexExpression1; // define word
	String regexExpression2; // definition of word
	String regexExpression3; // how do you spell word?
	String regexExpression4; // how do you say word in German?
	String regexExpression5; // Whats a word?
	String regexExpressionSettings; 
	String regexExpressionHelper;
	String regexExpressionMore;
	
	//private ArrayList<String> keywords_setting;
	private ArrayList<String> keywords_whatCanYouDo;
	private ArrayList<String> keywords_changePrefCat;

	/** Constructor **/
	public MessageManager() {
		
		
		// expressions for one or multiple functions (translation, definition, spelling, scrabble, examples, synonyms)
		regexExpression1 = ".*\\b(define|translate|spell|contain|start with|end with|contains|starts with|ends with)\\b .+"; // define word
		regexExpression2 = ".*\\b(definition|spelling|translation|example( sentence)?|synonym)s?\\b \\b(of|for)\\b (.+)"; // definition of word
		regexExpression3 = ".*what does (.+) mean.*"; // what does word mean?
		regexExpression4 = "(.+) in german.*"; // how do you say word in German?
		regexExpression5 = ".*what( is| are|'s) (a )?(.+)";
		
		// settings
		regexExpressionSettings = ".*\\b(set|change|put)\\b (the )?\\b(number|amount)\\b of \\b(words|results)\\b.*"; // set numbr of words for definitions to 6
		
		// helper function
		regexExpressionHelper = ".*what can you do.*";
		
		// asking for more sesults
		regexExpressionMore = ".*more.*";
		
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
	public String decodeMsg(String msg, Settings settings, DatabaseCommunicator dbC, Context context, State state) {

		if(msg == null)
			return "Oops, your message did not arrive.";

		String wishedWord;
		Function function;
		String[] result; 		// the result received from the DatabaseCommunicator

		function = findFunction(msg, context);
		wishedWord = findWishedWord(msg, context);
		wishedWord = checkWishedWord(wishedWord, context, function); // checks if "this, it, that" is probably to mean the last ww and changed it if so

		if(function == null) // if function wasnt found, try if last one could fit the input
			return "Sorry, I don't know which function you are asking for.";

		do {
			switch(function) {
				
				case TRANSLATION: 
					result = dbC.translate(wishedWord, settings.getNOW_translation());
					break;
				case DEFINITION:
					result = dbC.define(wishedWord, settings.getNOW_definition());
					state.save(settings, context);
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
					state.save(settings, context);
					break;
				case WHATCANYOUDO:
					result = new String[1];
					result[0] = "";
					break;
				case CHANGE_PREF_CAT:
					context.changePrefCat(msg);
					result = new String[1];
					result[0] = "";
					state.save(settings, context);
					break;
				case MORE:
					result = dbC.getMoreResults(settings.getNOW(context.getLastFunctionUsed()));
					break;
				default:
					result = new String[1];
					result[0] = "";
					System.out.println("Sorry, message could not be computed.");
				}
			
			if(result == null)
				wishedWord = shortenWishedWord(wishedWord, msg); // removes the last word from the wished word

		}while(result == null && wishedWord != null);
		
		// Updates the context
		context.setLastFunctionUsed(function);
		context.setLastWishedWord(wishedWord);

		return createMsg(context, result);
	}

	/**
	 * Finds and returns the requested function in the message.
	 * If no function was found, null will be returned.
	 *
	 * @param msg message from the user
	 * @return Function
	 */
	private Function findFunction(String msg, Context context){
		
		
		/*
		 * SPEECH PATTERNS:
		 * 
		 * 
		 * (x|y) means one of the option within the ()
		 * ww is the wished word
		 * ?x? means x is optional
		 * 
		 * (define|translate|spell|contain|start with|end with) ww
		 * ?give me? (definition|spelling|translation|example|synonym) (of|for) ww
		 * what(is|are|'s) ?a ?ww in (German|otherLanguages)
		 * what does ww mean
		 * how do you say ww in (German|otherLanguages)
		 * what can you do
		 * (set|change|put) ?the? (number|amount) of (words|results)
		 */
		
		/*
		 * regexExpression1 = ".*\\b(define|translate|spell|contain|start with|end with|contains|starts with|ends with)\\b .+"; // define word
		 * regexExpression2 = ".*\\b(definition|spelling|translation|example( sentence)?|synonym)s?\\b \\b(of|for)\\b (.+)"; // definition of word
		 * regexExpression3 = ".*what does (.+) mean.*"; // what does word mean?
		 * regexExpression4 = ".+ in German.*"; // how do you say word in German?
		 * regexExpression5 = ".*what( is| are|'s) (a )?.+";
		 * regexExpressionSettings = ".*\\b(set|change|put)\\b (the )?\\b(number|amount)\\b of \\b(words|results)\\b.*"; // set numbr of words for definitions to 6
		 * regexExpressionHelper = ".*what can you do.*";
		*/
		
		
		// finds foundFunction for expression1 		".*\\b(define|translate|spell|contain|start with|end with)\\b .+"
		Pattern pattern = Pattern.compile(regexExpression1);
		Matcher matcher = pattern.matcher(msg);
		if (matcher.matches())
		{
			String functionKeyword= matcher.group(1);
			
			if(functionKeyword.contains("define")) {
					return Function.DEFINITION;
			}
			if(functionKeyword.contains("translate")) {
					return Function.TRANSLATION;
			}
			if(functionKeyword.contains("spell")) {
					return Function.SPELLING;
			}
			if(functionKeyword.contains("contain")) {
					return Function.SCRABBLE_CONTAIN;
			}
			if(functionKeyword.contains("start")) {
					return Function.SCRABBLE_START;
			}
			if(functionKeyword.contains("end")) {
					return Function.SCRABBLE_END;
			}
			
		}
		
		// finds foundFunction for expression2 		".*\\b(definition|spelling|translation|example|synonym)s?\\b \\b(of|for)\\b .+"
		pattern = Pattern.compile(regexExpression2);
		matcher = pattern.matcher(msg);
		if (matcher.matches())
		{
			String functionKeyword= matcher.group(1);
			
			if(functionKeyword.contains("definition")) {
					return Function.DEFINITION;
			}
			if(functionKeyword.contains("translation")) {
					return Function.TRANSLATION;
			}
			if(functionKeyword.contains("spelling")) {
					return Function.SPELLING;
			}
			if(functionKeyword.contains("example")) {
					return Function.EXAMPLE;
			}
			if(functionKeyword.contains("synonym")) {
					return Function.SYNONYMS;
			}
		}
		
		// finds function for expression 3 		".*what does (.+) mean.*"
		pattern = Pattern.compile(regexExpression3);
		matcher = pattern.matcher(msg);
		if (matcher.matches())
		{
			return Function.DEFINITION;
		}
		// finds function for expression 4		".+ in German.*"
		pattern = Pattern.compile(regexExpression4);
		matcher = pattern.matcher(msg);
		if (matcher.matches())
		{
			return Function.TRANSLATION;
		}
		
		// find function for expression 5	 	".*what( is| are|'s) a? .+"
		pattern = Pattern.compile(regexExpression5);
		matcher = pattern.matcher(msg);
		if (matcher.matches())
		{
			if(msg.contains("in german")) { // should not happen, because expression 4 is checked first
				return Function.TRANSLATION;
			}
			else {
				return Function.DEFINITION;
			}
		}
		
		
		// helper function
		if(msg.matches(regexExpressionHelper)) {
			return Function.WHATCANYOUDO;
		}
		
		// SETTINGS
		if(msg.matches(regexExpressionSettings)) { // new msg
			return Function.SETTING;
		}
		if(msg.matches(".*(?:to )?([1-9]+).*") // settings follow up answer, number was missing
				&& context.getLastFunctionUsed() == Function.SETTING) { // number was missing
			return Function.SETTING;
		}
		if(msg.matches(".*\\b(definition|translation|synonym|example|scrabble function)s?\\b.*")
				&& context.getLastFunctionUsed() == Function.SETTING) { // function was missing
			return Function.SETTING;
		}
		
		if(msg.matches(regexExpressionMore)) {
			return Function.MORE;
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
		
		/*
		 * SPEECH PATTERNS:
		 * 
		 * 
		 * (x) means x is optional.
		 * ww is the wished word
		 * + connects strings
		 * || means or (one of the options)
		 * ?x? means x is optional
		 * 
		 * (define|translate|spell|contain|start with|end with) + ww
		 * ?give me? (definition|spelling|translation|example|synonym) (of|for) ww
		 * what(is|are|'s) ?a ?ww in (German|otherLanguages)
		 * what does ww mean
		 * how do you (define|spell) ww
		 * how do you say ww in (German|otherLanguages)
		 */
		
		/*
		 * regexExpression1 = ".*\\b(define|translate|spell|contain|start with|end with)\\b .+"; // define word
		 * regexExpression2 = ".*\\b(definition|spelling|translation|example|synonym)s?\\b \\b(of|for)\\b .+"; // definition of word
		 * regexExpression3 = ".*what does .+ mean.*"; // what does word mean?
		 * regexExpression4 = ".+ in German.*"; // how do you say word in German?
		*/
		
		
		// finds ww for expression1
		Pattern pattern = Pattern.compile(regexExpression1);
		Matcher matcher = pattern.matcher(msg);
		if (matcher.matches())
		{
			int rightIndex = msg.indexOf(matcher.group(1)) + matcher.group(1).length(); // index of last word before ww
			wishedWord = msg.substring( rightIndex , msg.length() ); // possible
			wishedWord = wishedWord.trim(); // removes spaces at the start and end of string
			wishedWord = wishedWord.replaceAll("[^a-zA-Z\\s]", ""); // removes all characters which arent letters or spaces
			return wishedWord;
		}
		
		// finds ww for expression2
		pattern = Pattern.compile(regexExpression2);
		matcher = pattern.matcher(msg);
		if (matcher.matches())
		{
			int rightIndex = msg.indexOf(matcher.group(3)) + matcher.group(3).length()+1; // index of last word before ww
			wishedWord = msg.substring( rightIndex , msg.length() ); // possible
			wishedWord = wishedWord.trim(); // removes spaces at the start and end of string
			wishedWord = wishedWord.replaceAll("[^a-zA-Z\\s]", ""); // removes all characters which arent letters or spaces
			return wishedWord;
		}
		
		// finds ww for expression3
		pattern = Pattern.compile(regexExpression3);
		matcher = pattern.matcher(msg);
		if (matcher.matches()) // if the msg matches the regex
		{
			wishedWord = matcher.group(1);
			wishedWord = wishedWord.trim(); // removes spaces at the start and end of string
			wishedWord = wishedWord.replaceAll("[^a-zA-Z\\s]", ""); // removes all characters which arent letters or spaces
			return wishedWord;
		}
		
		//find ww for expression 4
		pattern = Pattern.compile(regexExpression4);
		matcher = pattern.matcher(msg);
		if (matcher.matches()) // if the msg matches the regex
		{
			wishedWord = matcher.group(1);
			wishedWord = wishedWord.trim(); // removes spaces at the start and end of string
			wishedWord = wishedWord.replaceAll("[^a-zA-Z\\s]", ""); // removes all characters which arent letters or spaces
			return wishedWord;
		}
		
		//find ww for expression 5
		pattern = Pattern.compile(regexExpression5);
		matcher = pattern.matcher(msg);
		if (matcher.matches()) // if the msg matches the regex
		{
			wishedWord = matcher.group(3);
			wishedWord = wishedWord.trim(); // removes spaces at the start and end of string
			wishedWord = wishedWord.replaceAll("[^a-zA-Z\\s]", ""); // removes all characters which arent letters or spaces
			return wishedWord;
		}
		
		// msg which doesnt have a ww
		if(msg.matches(regexExpressionHelper) || msg.matches(regexExpressionSettings)) {
			context.setLastWishedWord(null);
			return null;
		}

		return context.getLastWishedWord();
	}

	/**
	 * If the user asks for another function of the same word, he may refer to the word as
	 * "this", "that", "it". If this is the case, the ww will be changed to the last ww (context)
	 *
	 * @param ww wished Word
	 * @param context is needed in case it, that or this refers to last ww a request was made with
	 * @param f is the function which was currently found
	 * @return String ww the user probably means
	 */
	private String checkWishedWord(String ww, Context context, Function f) {
		/*
		 * These words may be a follow up question:
		 * that, it
		 */
		if(ww == null || f == null) {
			return null;
		}
		
		if(ww.matches("(this|it|that).*") && context.getLastFunctionUsed() != f) {
			return context.getLastWishedWord();
		}
		
		return ww;
		
	}
	
	/**
	 * This method creates a message for the user after an appropriate answer was found.
	 * The answer depends on the used function, wished word and the result.
	 *
	 * @param context
	 * @param result output from the function
	 * @return String the final message for the user
	 */
	private String createMsg(Context context, String[] resultAsArray) {

		String result = resultToString(resultAsArray);
		if(result == null && context.getLastFunctionUsed()==Function.MORE) {
			 return "Sorry, there are no more results.";
		}
		
	    if(result == null || result.isEmpty()) {
	        return "Sorry, I don't have any results for your request.";
        }

		switch(context.getLastFunctionUsed()) {
			case TRANSLATION: 
				if(resultAsArray.length>1) {
					return "The translations of " + context.getLastWishedWord() + " are " + result;
				}
				return "The translation of " + context.getLastWishedWord() + " is " + result;
				
			case DEFINITION:
				if(resultAsArray.length>1) {
					return "Definitions of " + context.getLastWishedWord() + " are " + result;
				}
				return "The definition is " + result;
				
			case SPELLING:
				return "" + context.getLastWishedWord() + " is spelled " + result;
				
			case SYNONYMS:
				if(resultAsArray.length>1) {
					return "Synonyms for " + context.getLastWishedWord() + " are " + result;
				}
				return "A synonym for " + context.getLastWishedWord() + " is " + result;

			case EXAMPLE:
				if(resultAsArray.length>1) {
					return "Examples for " + context.getLastWishedWord() + " are " + result;
				}
				return "An example for " + context.getLastWishedWord() + " is " + result;
			
			case SCRABBLE_START:
				if(resultAsArray.length>1) {
					return "Words, which start with " + context.getLastWishedWord() + " are " + result;
				}
				return "A word, which starts with " + context.getLastWishedWord() + " is " + result;
			
			case SCRABBLE_CONTAIN:
				if(resultAsArray.length>1) {
					return "Words, which contain " + context.getLastWishedWord() + " are " + result;
				}
				return "A word, which contains " + context.getLastWishedWord() + " is " + result;
			
			case SCRABBLE_END:
				if(resultAsArray.length>1) {
					return "Words, which end with " + context.getLastWishedWord() + " are " + result;
				}
				return "A word, which ends with " + context.getLastWishedWord() + " is " + result;
			
			case SETTING:
				return "";
			
			// may have to be adjusted for added functions
			case WHATCANYOUDO:
				return "The dictionary skill can give translations, definitions, synonyms, spellings, "
						+ "example sentences, change the number of words for your results and give you more results for your last request.";
			
			case MORE:
				if(result != null) {
					return result;
				}
				
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
	private String shortenWishedWord(String ww, String originalMsg) {

		if(ww == null) {
			return null;
		}
		
		if(originalMsg.matches("(.+) in german.*") || originalMsg.matches(".*what does (.+) mean.*")) { // then shorten from left
			
			int shortenPosTo = 0;
			
			// finds the position of the first " " starting from the start/left of the ww
			while( shortenPosTo < ww.length() && !(ww.substring(shortenPosTo, shortenPosTo+1).equals(" ") )) {
				shortenPosTo++;
			}

			if(shortenPosTo == 0) {
				return null;
			}
			else {
				return ww.substring(shortenPosTo+1, ww.length());
			}
			
		}
		else { // shorten from right
			
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
}
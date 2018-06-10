package DicSkill;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 03.06.2018	
 * NEW:
 * -	changes necessary for Lucene made
 * @author Lia
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

public class MessageManager {
	
	/** VARIABlES **/
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
	
	int positionOfFunction;
	
	
	
	/** Constructor **/
	public MessageManager() {
		
		//All keywords are set here
		
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
		keywords_whatCanYouDo.add("What can you do");
		
		// CHANGE PREFERRED CATEGORIES
		keywords_changePrefCat = new ArrayList<String>();
		keywords_changePrefCat.add("preferred category");
		keywords_changePrefCat.add("preferred categories");
		
	}
	
	
	/** Methods **/
	
	
	/*
	 * receives the msg, decodes it and calls the corresponding function
	 * Also updates the context in the process
	 */
	public String decodeMsg(String msg, Settings settings, DatabaseCommunicator dbC, Context context, Lucene lucene) {
		
		String ww; // the wished word
		Function f; // the function
		String[] result; // the result received from the DatabaseCommunicator 
		
		
		/* 	Calls the functions, which finds and returns the wished word and function to set parameters.
			Returns an error msg if function or ww could not be found (== null) */
		ww = findWishedWord(msg, context);
		f = findFunction(msg);
		if(f == null)
			return "Sorry, I don't know which function you are asking for.";
		/*if(ww == null) {
			return "Sorry, I don't know which word you're asking for.";
		}*/
		

		/*
		 *  while a result for the wished word (consisting of multiple(!) words) could not be found,
		 *  the wished word will be shortened by one word and the search for a result will be repeated.
		 *  When the wished word is null, it means that a result could not be found for the wished word.
		 */
		do {			
			// Calls the corresponding function depending on the result of the evaluation above
			switch(f) {
				
				case TRANSLATION: 
					result = lucene.translate(ww, settings.getNOW_translation());
					break;
				case DEFINITION:
					result = dbC.define(ww, settings.getNOW_definition());
					break;
				case SPELLING:
					result = dbC.spell(ww);
					break;
				case SYNONYMS:
					result = dbC.giveSynonyms(ww, settings.getNOW_synonyms());
					break;
				case SCRABBLE_START:
					result = dbC.scrabble_start(ww, settings.getNOW_scrabble());
					break;
				case SCRABBLE_CONTAIN:
					result = dbC.scrabble_contain(ww, settings.getNOW_scrabble());
					break;
				case SCRABBLE_END:
					result = dbC.scrabble_end(ww, settings.getNOW_scrabble());
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
				ww = shortenWishedWord(ww); // removes the last word from the wished word
		
		}while(result == null && ww != null);
		
		// Updates the context
		context.setLastFunctionUsed(f);
		context.setLastWishedWord(ww);
		
		// returns the msg
		return createMsg(context, resultToString(result));
	}
	
	/*
	 * finds and returns the requested function in the msg
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

	
	/*
	 * finds and returns the wished word in the msg. if there is no (new) wished word, it sets the last wished word
	 */
	private String findWishedWord(String msg, Context context){
		
		String ww;
		/*
		 * all different words which indicate the position of the ww will be tested here.
		 * if the msg contains such a word, a substring will be created which starts at the 
		 * msg.lastIndexOf the found word + the length of the word + 1
		 * When the ww ends will still have to be taken in account later on
		 */
		
		// *of* ; e.g. meaning/translation/spelling of
		if(msg.contains("of")) {
			ww = msg.substring(msg.lastIndexOf("of")+3);
			context.setLastWishedWord(ww);
			return ww;
		}
		
		// *with*
		if(msg.contains("with")) {
			ww = msg.substring(msg.lastIndexOf("with")+5);
			context.setLastWishedWord(ww);
			return ww;
		}
		
		// ** keywords after which is directly followed by the ww (translate, define, spell, contain)
		if(msg.contains("translate")) {
			ww = msg.substring(msg.lastIndexOf("translate")+10);
			context.setLastWishedWord(ww);
			return ww;
		}
		if(msg.contains("define")) {
			ww = msg.substring(msg.lastIndexOf("define")+7);
			context.setLastWishedWord(ww);
			return ww;
		}
		if(msg.contains("spell")) {
			ww = msg.substring(msg.lastIndexOf("spell")+6);
			context.setLastWishedWord(ww);
			return ww;
		}
		if(msg.contains("contain")) {
			ww = msg.substring(msg.lastIndexOf("contain")+8);
			context.setLastWishedWord(ww);
			return ww;
		}
		
		
		// return ww from context in case no ww found
		return context.getLastWishedWord();
		
	}
	
	
	// creates a msg depending on the used function, the wished word and the result
	private String createMsg(Context context, String result) {
		
		switch(context.getLastFunctionUsed()) {
			case TRANSLATION: 
				return "The translation of " + context.getLastWishedWord() + " is " + result;
			
			case DEFINITION:
				return "" + result;
			
			case SPELLING:
				return "" + context.getLastWishedWord() + " is spelled " + result;
			
			case SYNONYMS:
				return "Synonyms for " + context.getLastWishedWord() + " are " + result;
			
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
	
	
	//converts the array of results into one string to simplyfy msg generator
	private String resultToString(String[] result){
		
		if(result == null)
		{
			return null;
		}
		
		String allResults = "";
		for(int i=0; i<result.length; i++) {
			allResults += result[i]+" ";
		}
		
		return allResults;
	}
	
	/*
	 * This function removes the last word from the wished word by looking for a space and then
	 * removing the space and everything that comes afterwards.
	 */
	public String shortenWishedWord(String ww) {
		
		int shortenPosTo = ww.length(); // shortenPosTo equals the last pos of the ww
		
		// finds the position of the first " " starting from the end/right of the ww
		while( shortenPosTo > 0 && !(ww.substring(shortenPosTo-1, shortenPosTo).equals(new String(" ")) )) {
			shortenPosTo--;
		}
		
		/*
		 *  shortenPosTo == 1 would mean that the shortenedWishedWord would be an empty String.
		 *  shortenPosTo == ww.length() would mean that the shortenedWishedWord would be unchanged.
		 *  Therefore the function will return null in this case to indicate that no valid wished word
		 *  would be found after an attempt to shorten the wished word.
		 */
		if(shortenPosTo == 1 || shortenPosTo == ww.length()) {
			return null;
		}
		else {
			String shortenedWishedWord = ww.substring(0, shortenPosTo-1);
			return shortenedWishedWord;
		}
	}
		
	
	/** Setters and Getters **/

}

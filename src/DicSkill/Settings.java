package DicSkill;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 28.06.2018
 * NEW:
 * -	improved code documentation
 *	@author Lia
 */

/**
 * 25.06.2018
 * NEW:
 * -	Settings now uses Regex instead of searching for keywords
 * -	improved dialog; whole msg does not have to be repeated anymore
 * -	new Function which returns NOW for function depending on function
 * @author Lia
 *
 */

/**
 * 15.06.2018
 * NEW:
 * -	Function Example added
 * -	Bug fixes
 * @author Walter
 */

/**
 * 03.06.2018
 * NEW:
 * -	Settings is now savable
 * @author Lia
 */

/**
 * 23.05.2018
 * TO DO:
 * -	make settings saveable
 * NEW:
 * -	setNOW function
 * @author Lia
 */

/**
 * 29.04.2018
 * TO DO:
 * -	make settings saveable
 * @author Lia
 */

/**
 * This class enabled the user to modify the answers. He can change the NOW (Number of words)
 * to get a specific number of results for translations, definitions, synonyms, examples, scrabble functions.
 */
public class Settings implements Serializable  
{
	/** VARIABLES **/
	private static final long serialVersionUID = 5L;

	// NOW = Number Of Words
	private int NOW_translation;
	private int NOW_definition;
	private int NOW_synonyms;
	private int NOW_examples;
	private int NOW_scrabble;
	private int lastFailedNumberChange; // remembers the number after a failed change
	private String lastFailedFunctionChange; // remembers the function after a failed change
	private String regexExpressionSettings;
	
	/** CONSTRUCTOR **/
	public Settings()  {

		this.NOW_translation = 1;
		this.NOW_definition = 1;
		this.NOW_synonyms = 3;
		this.NOW_examples = 3;
		this.NOW_scrabble = 3;
		
		lastFailedFunctionChange = null;
		lastFailedNumberChange = -1;
		
		/*
		 * regex explained for those new to regex:
		 * ".*\\b(set|change|put)\\b (the )?\\b(number|amount)\\b of \\b(words|results)\\b for \\b(definition|translation|synonym|example|scrabble function)s?\\b to ([1-9])([0-9]*).*
		 * .* means any number of any characters ma be here
		 * \\b(set|change|put)\\b one of the options must follow, but as seperate word ("pset" would not match for ".*\\b(set|change|put)\\b")
		 * ? means whatever is before ? is optional
		 * then to must follow
		 * ([1-9])([0-9]*) means any number between 1 and 9 must follow. Then any number between 0 and 9 can follow any amount of times.
		 * .* means the string may end with any character, any amount of times
		 */
		regexExpressionSettings = ".*\\b(set|change|put)\\b (the )?\\b(number|amount)\\b of \\b(words|results)\\b for "
				+ "\\b(definition|translation|synonym|example|scrabble function|all|all functions)s?\\b to ([1-9])([0-9]*).*";
	}
	
	/** Methods **/

	/**
	 * The NOW (Number of Words) for all functions will be changed.
	 *
	 * @param newNOW
	 */
	private void setAll(int newNOW) {

		this.NOW_translation = newNOW;
		this.NOW_definition = newNOW;
		this.NOW_synonyms = newNOW;
		this.NOW_examples = newNOW;
		this.NOW_scrabble = newNOW;
	}

	/**
	 * This function is called to set the NOW (Number of Words) for a specific function.
	 *
	 * @param msg message from the user
	 */
	public void setNOW(String msg) {
		
		String foundFunction = null;
		int newNOW=-1;
		Pattern pattern;
		Matcher matcher;
		
		// all info was provided
		pattern = Pattern.compile(regexExpressionSettings);
		matcher = pattern.matcher(msg);
		if (matcher.matches()) // checks if all info was provided
		{
			foundFunction = matcher.group(5); // gets function from msg
			try {
				newNOW = Integer.parseInt(matcher.group(6)); // gets number from msg
			}
			catch (NumberFormatException e) {
				System.out.print("Sorry, you must set the number of words to a certain number.");
			}
			setForFunction(foundFunction, newNOW);
			
		}
		

		// if function was not named, but number was
		pattern = Pattern.compile(".*\\b(set|change|put)\\b (the )?\\b(number|amount)\\b of \\b(words|results)\\b to ([1-9])([0-9]*).*");
		matcher = pattern.matcher(msg);
		if(matcher.matches()) {
			String number = matcher.group().replaceAll("\\D*",""); // gets number from msg
			lastFailedNumberChange = Integer.parseInt(number); //remember number
			newNOW = lastFailedNumberChange;
			System.out.print("What function do you want to change the number of results for? You may choose: definitions, translations, synonyms, example, scrabble or all.");
		}
		// if number was not named, but function was
		pattern = Pattern.compile(".*\\b(set|change|put)\\b (the )?\\b(number|amount)\\b of \\b(words|results)\\b for "
				+ "\\b(definition|translation|synonym|example|scrabble function|all|all functions)s?\\b[set\\d]*");
		matcher = pattern.matcher(msg);
		if(matcher.matches()) {
			lastFailedFunctionChange = matcher.group(5); //remember function
			foundFunction = lastFailedFunctionChange;
			System.out.print("What number do you want to set it to?");
		}
		
		
		// Msg was unclear before and is being cleared up now
		pattern = Pattern.compile(".*(?:to )?([1-9])([0-9]*).*"); //OPTION 1: number was missing
		matcher = pattern.matcher(msg);
		if (matcher.matches() && lastFailedFunctionChange != null)
		{
			String number = matcher.group().replaceAll("\\D*",""); // gets number from msg as string
			foundFunction = lastFailedFunctionChange;
			newNOW = Integer.parseInt(number);
			setForFunction(foundFunction, newNOW);
		}
		pattern = Pattern.compile(".*\\b(definition|translation|synonym|example|scrabble function)s?\\b.*");
		matcher = pattern.matcher(msg);
		if (matcher.matches() && lastFailedNumberChange!=-1)//OPTION 2: function was missing
		{
			foundFunction = matcher.group(1); // gets function from msg
			newNOW = lastFailedNumberChange;
			setForFunction(foundFunction, lastFailedNumberChange);
		}
		
		
		if(newNOW==-1 && foundFunction==null) {
			System.out.print("Sorry, you need to be more precise. I need to know how many results you want to set for which function.");
		}
		
	}
	
	/**
	 * changes settings depending on which function was found and prints msg.
	 * 
	 * @param function is the functions settings will be changed for
	 * @param nOW is the number the number of words for that function will be changed to
	 */
	public void setForFunction(String function, int nOW) {
		
		if(function.contains("translation")) {
			NOW_translation = nOW;
			//System.out.println("Number of words for translations have been set to "+nOW);
		}
		if(function.contains("definition")) {
			NOW_definition = nOW;
			//System.out.println("Number of words for definitions have been set to "+nOW);
		}
		if(function.contains("synonym")) {
			NOW_synonyms = nOW;
			//System.out.println("Number of words for synonyms have been set to "+nOW);
		}
		if(function.contains("example")) {
			NOW_examples = nOW;
			//System.out.println("Number of words for examples have been set to "+nOW);
		}
		if(function.contains("scrabble")) {
			NOW_definition = nOW;
			//System.out.println("Number of words for scrabble have been set to "+nOW);
		}
		if(function.contains("all")) {
			setAll(nOW);
			//System.out.println("Number of words for all have been set to "+nOW);
		}
		
		//Reset after succesful attempt
		lastFailedNumberChange = -1; 
		lastFailedFunctionChange = null;
	}
	
	/**
	 * returns the current number of words for the function given
	 * @param f is the function
	 * @return the now for f
	 */
	public int getNOW(Function f) {
		
		this.NOW_translation = 1;
		this.NOW_definition = 1;
		this.NOW_synonyms = 3;
		this.NOW_examples = 3;
		this.NOW_scrabble = 3;
		switch(f) {
			case TRANSLATION:
				return getNOW_translation();
			case DEFINITION:
				return getNOW_definition();
			case SYNONYMS:
				return getNOW_synonyms();
			case EXAMPLE:
				return getNOW_examples();
			case SCRABBLE_START:
				return getNOW_scrabble();
			case SCRABBLE_END:
				return getNOW_scrabble();
			case SCRABBLE_CONTAIN:
				return getNOW_scrabble();
			default:
				return 0;
		}
	}
	
	/** SETTERS AND GETTERS **/
	public int getNOW_translation() {
		return NOW_translation;
	}

	public int getNOW_definition() {
		return NOW_definition;
	}

	public int getNOW_synonyms() {
		return NOW_synonyms;
	}

	public int getNOW_scrabble() {
		return NOW_scrabble;
	}

	public int getNOW_examples() {
		return NOW_examples;
	}

}

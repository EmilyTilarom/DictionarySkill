package DicSkill;

import java.io.Serializable;

/**
 * 03.06.2018
 * NEW:
 * -	Settings is now savable
 * @author Lia
 *
 */

/**
 * 23.05.2018
 * TO DO:
 * -	make settings saveable
 * NEW:
 * -	setNOW function
 * 
 * @author Lia
 *
 */

/**
 * 29.04.2018
 * TO DO:
 * -	make settings saveable
 * @author Lia
 *
 */

/**
 * This class enabled the user to modify the ouput. He can change the NOW (Number of words)
 * to get a specific number of results for each function. 
 */
public class Settings implements Serializable  
{
	/** VARIABlES **/
	private static final long serialVersionUID = 5L;

	// NOW = Number Of Words
	private int NOW_translation;
	private int NOW_definition;
	private int NOW_synonyms;
	private int NOW_scrabble;
	
	/** Constructor **/
	public Settings()
	{
		this.NOW_translation = 3;
		this.NOW_definition = 3;
		this.NOW_synonyms = 3;
		this.NOW_scrabble = 3;
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
		this.NOW_scrabble = newNOW;
	}

	/**
	 * This function is called to set the NOW (Number of Words) for a specific function.
	 *
	 * @param msg message from the user
	 */
	public void setNOW(String msg) {

		boolean functionFound = false;
		int newNOW;

		try {
			newNOW = Integer.parseInt( msg.substring(msg.lastIndexOf("to")+3) );
			
			if(msg.contains("translations")) {
				NOW_translation = newNOW;
				functionFound = true;
				System.out.println("Number of words for translations have been set to "+newNOW);
			}
			if(msg.contains("definitions")) {
				NOW_definition = newNOW;
				functionFound = true;
				System.out.println("Number of words for definitions have been set to "+newNOW);
			}
			if(msg.contains("synonyms")) {
				NOW_synonyms = newNOW;
				functionFound = true;
				System.out.println("Number of words for synonyms have been set to "+newNOW);
			}
			if(msg.contains("scrabble")) {
				NOW_definition = newNOW;
				functionFound = true;
				System.out.println("Number of words for scrabble have been set to "+newNOW);
			}
			if(msg.contains("all")) {
				setAll(newNOW);
				functionFound = true;
				System.out.println("Number of words for all have been set to "+newNOW);
			}
		}
		catch (NumberFormatException e) {
			System.out.print("Sorry, you must set the number of words to a certain number.");
		}		
		
		if(functionFound == false) {
			System.out.print("Sorry, you must indicate which function you want to change the "
					+ "number of words for. You may choose: definitons, translations, synonyms, scrabble or all.");
		}
	}
	
	/** Setters and Getters **/
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
	
}

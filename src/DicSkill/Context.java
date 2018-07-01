package DicSkill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 01.07.2018
 * NEW:
 * -	bugfix concering regex.
 *	@author Walter
 */

/**
 * 28.06.2018
 * NEW:
 * -	improved code documentation
 *	@author Lia, Walter
 */

/**
 * 27.06.2018
 * NEW:
 * -	changed how changing pref categories works.
 *	@author Lia
 */

/**
 * 10.06.2018
 * NEW:
 * -	Documentation improved.
 * @author Walter
 */

/**
 * 03.06.2018
 * NEW:
 * -	Context is now savable
 * @author Lia
 */

/**
 * 27.05.2018
 * NEW:
 * - 	preferredCategories is now of type ArrayList<String>
 * -	added functions: addPrefCat, removePrefCat, deleteAllPrefCat, changePrefCat
 * TO DO:
 * -	make context saveable
 * @author Lia
 */

/**
 * 29.04.2018
 * TO DO:
 * -	make context saveable
 * @author Lia
 */

/**
 * Context remembers the important information of the last user queries.
 * This includes the last wished word, preferred categories and the last function. The information is used to improve the answer to the user.
 *
 * Context is persistently saved through State in class MessageManager
 */
public class Context implements Serializable {

	/** VARIABLES **/
	private static final long serialVersionUID = 4L;
	
	private Function lastFunctionUsed;
	private ArrayList<String> preferredCategory;
	private String lastWishedWord;

	/** CONSTRUCTOR **/
	public Context(){
		preferredCategory = new ArrayList<String>();
	}
	
	/** METHODS **/

	/**
	 * This function finds out what change the user wants to make to the preferred categories
	 * by checking for keywords using regex. Then the function performing the change is called.
	 * Categories are words, which will be used to put results which probably match the category first.
	 * 
	 * They can be added, removed or all deleted. If this function cannot find which of the three changes the user wants 
	 * to make, it will tell the user which changes he/she can make.
	 *
	 * @param msg from the user
	 */
	public void changePrefCat(String msg) {
		
		/*
		 * Regex explained for those new to regex: "^.*(add|remove|delete all)([ a-zA-Z][a-zA-Z]*)( to | from )?(my |the )?(preferred category|preferred categories).*$"
		 * ^.* means the string may start with any sequence of character or no characters
		 * (add|remove|delete all) means one of the options must follow
		 * ([ a-zA-Z][a-zA-Z]*) means any alphabetical letter may follow, but only the first character may be a space
		 * ( to | from )? AND (my |the )? means one of the options may follow, but does not have to
		 * (preferred category|preferred categories) means one of the options must follow
		 * .*$ means the string may end with any character, no matter how many.
		 */

		Pattern pattern = Pattern.compile("^.*(add|remove|delete all|remove all)([ a-zA-Z][a-zA-Z]*)( to | from )?(my |the )?(category|categories|preferred category|preferred categories).*$");
		Matcher matcher = pattern.matcher(msg);
		
		// note: matcher.group(2) returns the string of the third group. A group is marked by (). If there are more than one option, it will return the option used in the msg.
		if(matcher.matches()) {
			
			if(matcher.group(1).matches("add")) {
				String cat = matcher.group(2);
				addPrefCat( cat.trim() );
			}
			
			else if(matcher.group(1).matches("remove")) {
				String cat = matcher.group(2);
				removePrefCat( cat.trim() );
			}
			
			else if(matcher.group(1).matches("delete all")) {
				deleteAllPrefCat();
			}	
		}
		else {
			System.out.println("Sorry, I could not understand which changes you want to "
					+ "make to your preferred categories. You may add a category, remove a "
					+ "category or delete all preferred categories.");
		}
	}

	/**
	 * A category the user names will be added to preferred categories. If the category was already added,
	 * it will not add it again and tell the user.
	 *
	 * @param cat category the user wants to add
	 */
	private void addPrefCat(String cat) {
		if(!preferredCategory.contains(cat)) {
			preferredCategory.add(cat);
			//System.out.println(cat + " was added to your preferred categories.");
		}
		else {
			//System.out.println(cat + " is already in your preferred categories.");
		}
	}

	/**
	 * The category the user names will be removed. If none was found the user will be notified.
	 *
	 * @param cat category the user wants to remove
	 */
	private void removePrefCat(String cat) {
		if(preferredCategory.contains(cat)) {
			preferredCategory.remove(cat);
			//System.out.println(cat + " was removed from your preferred categories.");
		}
		else {
			//System.out.println(cat + " could not be found in your preferred categories.");
		}
	}

	/**
	 * All categories will be removed from preferred categories.
	 */
	private void deleteAllPrefCat() {
		preferredCategory.clear();
		//System.out.println("All your preferred categories were deleted.");
	}
	
	/** SETTERS AND GETTERS **/

	public Function getLastFunctionUsed() {
		return lastFunctionUsed;
	}

	public void setLastFunctionUsed(Function lastFunctionUsed) {
		this.lastFunctionUsed = lastFunctionUsed;
	}

	public ArrayList<String> getPreferredCategory() {
		return preferredCategory;
	}

	public void setPreferredCategory(ArrayList<String> preferredCategory) {
		this.preferredCategory = preferredCategory;
	}

	public String getLastWishedWord() {
		return lastWishedWord;
	}

	public void setLastWishedWord(String lastWishedWord) {
		this.lastWishedWord = lastWishedWord;
	}
	
}


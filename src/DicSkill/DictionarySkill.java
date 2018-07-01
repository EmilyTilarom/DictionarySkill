package DicSkill;

import java.util.Scanner;

/**
 * 28.06.2018
 * NEW:
 * -	improved code documentation
 *	@author Lia, Walter
 */

/**
 * 26.06.2018
 * NEW:
 * -	Object of class state is now given to MessageManager, which will save Settings and context
 * @author Lia
 */

/**
 * 03.06.2018
 * NEW:
 * -	Class will now save and load settings and context
 * TO DO:
 * -	reconsider when context and settings shall be saved.
 * @author Lia
 */

/**
 * This class is the one that will be run and will open a dialog with the user.
 * Say bye to close the dialog.
 */
public class DictionarySkill {

	public static void main(String[] args) {

		State state = new State();
		Context context = state.loadContext(); // loads context. If no context is found, creates new one.
		Settings settings = state.loadSettings(); // loads settings. If no settings is found, creates new one.
		MessageManager tb = new MessageManager();
		DatabaseCommunicator dbC = new DatabaseCommunicator();
		
		String msg = "";
		Scanner read = new Scanner(System.in);
		
		System.out.println("Hello! How may I help you?");
		
		msg = read.nextLine().toLowerCase();
		
		while(!msg.equals("bye"))
		{
			System.out.println( tb.decodeMsg(msg, settings, dbC, context, state) );
			msg = read.nextLine().toLowerCase();
		}
		
		System.out.println("Goodbye");
		read.close();
	}
}

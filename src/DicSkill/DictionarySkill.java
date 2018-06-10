package DicSkill;

import java.util.Scanner;

/**
 * 03.06.2018
 * NEW:
 * -	Class will now save and load settings and context
 * TO DO:
 * -	adapt, so the message may be received from the main system
 * -	reconsider when context and settings shall be saved.
 * @author Lia
 *
 */

/**
 * 29.04.2018
 * This class is the one that can be run and will open a dialog with the user
 * say bye to close the dialog
 * @author Lia
 *
 */

public class DictionarySkill {

	
	public static void main(String[] args)
	{
		Lucene lucene = new Lucene();
		State state = new State();
		Context context = state.loadContext(); // loads context. If no context is found, creates new one.
		Settings settings = state.loadSettings(); // loads settings. If no settings is found, creates new one.
		MessageManager tb = new MessageManager();
		DatabaseCommunicator dbC = new DatabaseCommunicator();
		
		String msg = "";
		Scanner read = new Scanner(System.in);
		
		System.out.println("Hello! How may I help you?");
		
		msg = read.nextLine();
		msg.toLowerCase();
		
		while(!msg.equals("bye"))
		{
			System.out.println( tb.decodeMsg(msg, settings, dbC, context, lucene) );
			msg = read.nextLine();
			msg.toLowerCase();
		}
		
		state.save(settings, context); // saves the updated context
		
		System.out.println("Goodbye");
	}
	
}

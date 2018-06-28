package DicSkill;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * 10.06.2018
 * NEW:
 * -	Documentation improved.
 * @author Walter
 */

/**
 * 03.06.2018
 * NEW:
 * -	State can now save and load context and settings
 * @author Lia
 */

/**
 * 29.04.2018
 * TO DO:
 * -	Make class to save context and settings
 * @author Lia
 */

/**
 * This class saves and loads the context and settings from the files.
 */
public class State {
	
	/** VARIABLES **/
	private File context_file;
	private File settings_file;

	/** CONSTRUCTOR **/
	public State() {
		context_file = new File("/State", "context_file.ser");
		settings_file = new File("/State", "settings_file.ser");
	}

	/** METHODS **/

	/**
	 * Settings and Context are persistently saved in a .ser file.
	 *
	 * @param settings
	 * @param context
	 */
	public void save(Settings settings, Context context) {

		try {
			FileOutputStream fos = new FileOutputStream("context_file.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(context);

			fos = new FileOutputStream("settings_file.ser");
			oos = new ObjectOutputStream(fos);
			oos.writeObject(settings);
			
			oos.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the previously saved Context with the configuration.
	 * If no Context was found a new one will be created.
	 *
 	 * @return Context
	 */
	public Context loadContext() {
		
		Context context;
			
		try{
			FileInputStream fis = new FileInputStream("context_file.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			context = (Context) ois.readObject();
			ois.close();
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
			return new Context();
		}
		catch(IOException e) {
			return new Context();
		}

		return context;
	}

	/**
	 * Loads the previously saved Settings with the configuration.
	 * If no Settings was found a new one will be created.
	 *
	 * @return Settings
	 */
	public Settings loadSettings() {
		
		Settings settings;
			
		try{
			FileInputStream fis = new FileInputStream("settings_file.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			settings = (Settings) ois.readObject();
			ois.close();
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
			return new Settings();
		}
		catch(IOException e) {
			return new Settings();
		}
		
		return settings;
	}
}

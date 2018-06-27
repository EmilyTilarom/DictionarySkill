package DicSkill;

import de.iisys.pippa.core.skill.Skill;
import de.iisys.pippa.core.skill.SkillRegex;
import de.iisys.pippa.core.speech_out.SpeechOut;
import DicSkill.DictionarySkillExecutableImpl;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import de.iisys.pippa.core.message.AMessage;
import de.iisys.pippa.core.message.speech_message.SkillSpeechMessage;
import de.iisys.pippa.core.message.stop_message.StopMessage;
import de.iisys.pippa.core.message_processor.AMessageProcessor;
import de.iisys.pippa.core.skill.Skill;
import de.iisys.pippa.core.skill.SkillRegex;
import de.iisys.pippa.core.skill_executable.ASkillExecutable;
import de.iisys.pippa.core.speech_out.SpeechOut;

/**
 * Try to integrate the skill in the main system
 * @author Adrian
 */

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

public class DictionarySkillImpl extends AMessageProcessor implements Skill {

	private boolean isClosed = false;
	
	SkillRegex[] skillRegexes = new SkillRegex[] { new SkillRegex(this, "(translate)|(define)|(synonyms)|(translation)") };
	private State state = new State();
	private Context context = state.loadContext(); // loads context. If no context is found, creates new one.
	private Settings settings = state.loadSettings(); // loads settings. If no settings is found, creates new one.
	private DatabaseCommunicator dbC = new DatabaseCommunicator();
	private MessageManager tb = new MessageManager();
    
	ASkillExecutable dictionarySkillExecutable = null;

	SpeechOut speechOut = null;

    @Override
    public SkillRegex[] getRegexes() {
        return this.skillRegexes;
    }
    
    private void getSpeechOut() {
		if (this.speechOut == null) {

			Collection<ServiceReference<SpeechOut>> serviceReferences = null;

			try {

				BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

				serviceReferences = context.getServiceReferences(SpeechOut.class, "(name=SpeechOut)");

				SpeechOut service = context.getService(((List<ServiceReference<SpeechOut>>) serviceReferences).get(0));

				this.speechOut = service;

			} catch (InvalidSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
    }
		
	//
	public void run() {
		this.getSpeechOut();

		while (!this.isClosed) {

			AMessage nextMessage = null;

			try {

				nextMessage = this.getIncomingQueue().take();

				if (nextMessage != null) {

					if (nextMessage instanceof StopMessage) {
						StopMessage stopMessage = (StopMessage) nextMessage;
						if (stopMessage.isStopAndClose()) {
							this.isClosed = true;				
						}
						this.getOutgoingQueue().put(nextMessage);
					}
					else if (nextMessage instanceof SkillSpeechMessage) {
						SkillSpeechMessage speechMessage = (SkillSpeechMessage) nextMessage;

						System.out.println("DictionarySkill Received SkillSpeechMessage");

						// TODO
						dictionarySkillExecutable = new DictionarySkillExecutableImpl(this.speechOut, state, context, settings, dbC, tb);

						speechMessage.setConfidence((float) 1.0);
						speechMessage.setSkillExecutable(dictionarySkillExecutable);

						this.getOutgoingQueue().put((AMessage) speechMessage);
					}
				}
			}catch(Exception e) {//interrupted exception?
				
			}
		}
	}
	/*
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
			System.out.println( tb.decodeMsg(msg, settings, dbC, context) );
			msg = read.nextLine().toLowerCase();
		}
		
		state.save(settings, context); // saves the updated context
		
		System.out.println("Goodbye");
	}
	*/
	
}

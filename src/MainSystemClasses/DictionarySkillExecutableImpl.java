package MainSystemClasses;

import DicSkill.Context;
import DicSkill.DatabaseCommunicator;
import DicSkill.MessageManager;
import DicSkill.Settings;
import DicSkill.State;
import de.iisys.pippa.core.skill_executable.ASkillExecutable;
import de.iisys.pippa.core.speech_out.SpeechOut;
import de.iisys.pippa.core.speech_out.SpeechOutListener;

public class DictionarySkillExecutableImpl extends ASkillExecutable implements SpeechOutListener{
	
	static String skillId = "DictionarySkillExecutable_0.1";
	
	protected SpeechOut speechOut = null;

	private final Object lockObject = new Object();
	
	private State state = null;
	private Context context = null;
	private Settings settings = null;
	private DatabaseCommunicator dbC = null;
	private MessageManager tb = null;

	public DictionarySkillExecutableImpl(SpeechOut speechOut, State state, Context context, Settings settings, DatabaseCommunicator dbC, MessageManager tb) {
		super(skillId);
		this.speechOut = speechOut;
		this.state = state;
		this.context = context;
		this.settings = settings;
		this.dbC = dbC;
		this.tb = tb;
		
	}
	
	//actual code to run when activated
	public void doRun() {
		
		//text to be spoken
		this.speechOut.setOutputText(this, tb.decodeMsg(speechOut.toString(), settings, dbC, context, state), false);
		
		//play
		this.speechOut.play(this);
		
		System.out.println("before wait");

		synchronized (lockObject) {
			try {
				lockObject.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("after wait");
	}
	
	@Override
	public void speechOutStarted() {
		System.out.println("clock speechOutStarted");
	}

	@Override
	public void speechOutPaused() {
		System.out.println("clock speechOutPaused");
	}

	@Override
	public void speechOutResumed() {
		System.out.println("clock speechOutResumed");
	}

	@Override
	public void speechOutStopped() {
		System.out.println("clock speechOutStopped");
		synchronized (lockObject) {
			lockObject.notify();
		}
	}

	@Override
	public void speechOutFinished() {
		System.out.println("clock speechOutFinished");
		synchronized (lockObject) {
			lockObject.notify();
		}
	}

}

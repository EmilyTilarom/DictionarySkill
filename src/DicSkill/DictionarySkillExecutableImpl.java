package DicSkill;

import de.iisys.pippa.core.skill_executable.ASkillExecutable;
import de.iisys.pippa.core.speech_out.SpeechOut;
import de.iisys.pippa.core.speech_out.SpeechOutListener;

public class DictionarySkillExecutableImpl extends ASkillExecutable implements SpeechOutListener{
	
	static String skillId = "DictionarySkillExecutable_0.1";
	
	protected SpeechOut speechOut = null;

	private final Object lockObject = new Object();

	public DictionarySkillExecutableImpl(SpeechOut speechOut) {
		super(skillId);
		this.speechOut = speechOut;
	}
	
	State state = null;
	Context context = state.loadContext(); // loads context. If no context is found, creates new one.
	Settings settings = state.loadSettings(); // loads settings. If no settings is found, creates new one.
	MessageManager tb = null;
	DatabaseCommunicator dbC = new DatabaseCommunicator();
	
	//actual code to run when activated
	public void doRun() {
		state = new State();
		tb = new MessageManager();
		
		String msg = "";
		
		tb.decodeMsg(msg, settings, dbC, context);
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

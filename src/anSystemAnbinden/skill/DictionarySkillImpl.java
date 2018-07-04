package anSystemAnbinden.skill;

import DicSkill.*;

import java.util.Scanner;

    private String regexExpression1 = ".*\\b(define|translate|spell|contain|start with|end with|contains|starts with|ends with)\\b .+"; 
	private String regexExpression2 = ".*\\b(definition|spelling|translation|example( sentence)?|synonym)s?\\b \\b(of|for)\\b (.+)"; 
	private String regexExpression3 = ".*what does (.+) mean.*"; 
	private String regexExpression4 = "(.+) in german.*"; 
	private String regexExpression5 = ".*what( is| are|'s) (a )?(.+)";
	private String regexExpressionSettings = ".*\\b(set|change|put)\\b (the )?\\b(settings|\\b(number|amount)\\b of \\b(words|results)\\b).*"; // may need to put flag
	private String regexExpressionHelper = ".*what can you do.*"; 
	private String regexExpressionMore = ".*more.*"; // skill needs to be last skill called
	private String regexExpressionCategory = "^.*([ a-zA-Z]*)( to| from)?( my | the )?(preferred category|preferred categories).*$"; // may need to put flag
	
	
	private boolean isClosed = false;
	
	SkillRegex[] skillRegexes = new SkillRegex[] { new SkillRegex(this, "(regexExpression1|regexExpression2|regexExpression3|"
			+ "regexExpression4|regexExpression5|regexExpressionSettings|regexExpressionHelper|regexExpressionMore"
			+ "|regexExpressionCategory)") };
    //ASkillExecutable clockSkillExecutable = null;
    //SpeechOut speechOut = null;

    @Override
    public SkillRegex[] getRegexes() {
        return this.skillRegexes;
    }

    //@Override
    public void run(){
        State state = new State();
        Context context = state.loadContext(); // loads context. If no context is found, creates new one.
        Settings settings = state.loadSettings(); // loads settings. If no settings is found, creates new one.
        MessageManager tb = new MessageManager();
        DatabaseCommunicator dbC = new DatabaseCommunicator();


        String msg = "";

        //Should be substituted with the message from the skill
        Scanner read = new Scanner(System.in);

        System.out.println("Hello! How may I help you?");

        msg = read.nextLine().toLowerCase();

        while(!msg.equals("bye"))
        {
            System.out.println( tb.decodeMsg(msg, settings, dbC, context) );
            msg = read.nextLine().toLowerCase();

        }

        state.save(settings, context); // saves the updated context

        //this.getSpeechOut();

        while (!this.isClosed) {

            //AMessage nextMessage = null;

            try {
                //nextMessage = this.getIncomingQueue().take();

                /*
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

                        System.out.println("ClockSkill Received SkillSpeechMessage");

                        // TODO
                        clockSkillExecutable = new ClockSkillExecutableImpl(this.speechOut);

                        speechMessage.setConfidence((float) 1.0);
                        speechMessage.setSkillExecutable(clockSkillExecutable);

                        this.getOutgoingQueue().put((AMessage) speechMessage);
                    }

                }*/

            } catch (Exception e) {

            }
        }
        System.out.println("Goodbye!");
    }
}

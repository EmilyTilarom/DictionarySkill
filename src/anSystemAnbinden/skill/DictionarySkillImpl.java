package anSystemAnbinden.skill;

import DicSkill.*;

import java.util.Scanner;

public class DictionarySkillImpl implements Skill{
    private boolean isClosed = false;
    SkillRegex[] skillRegexes = new SkillRegex[] { new SkillRegex(this, "(translate|define|synonyms|translation)") };
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

package Tests;

import DicSkill.*;

public class MainTestSkript {

    public static void main(String[]args) {

        MessageManager msgManager = new MessageManager();
        Settings sett = new Settings();
        DatabaseCommunicator dataComm = new DatabaseCommunicator();
        Context context = new Context();
        State state = new State();

        System.out.println(msgManager.decodeMsg("definition of dog", sett, dataComm, context, state));

    }


}
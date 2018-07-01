package Tests;

import DicSkill.*;
import java.util.Scanner;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * In here the manual tests will be scripted for the sake of presenting them.
 * Automates test are also included.
 */
public class MainTestSkript {

    private MessageManager messageManager = new MessageManager();
    private Settings settings = new Settings();
    private DatabaseCommunicator dataComm = new DatabaseCommunicator();
    private Context context = new Context();
    private State state = new State();
    private Scanner reader = new Scanner(System.in);

    public static void main(String[]args) {

        MainTestSkript test = new MainTestSkript();
        MainTest testMain = new MainTest();

        System.out.println("--- Starting Performance Tests ---\n");

        test.testPerformance();

        System.out.println("--- Starting Manual Tests ---\n");

        // Translate
        test.startQuestion("Please translate enlightenment");
        test.startQuestion("give me the translation of apple juice please");

        // Definition
        test.startQuestion("what is the definition of orange juice");

        // Settings & Definition
        test.startQuestion("set the number of words for definitions to 5");
        test.startQuestion("definition of master please");

        // Synonyms
        test.startQuestion("provide me with synonyms of certain");

        // More results & Synonyms
        test.startQuestion("more results please");

        // Scrabble function
        test.startQuestion("words that start with coo");
        test.startQuestion("words that contain en please");

        // Settings
        test.startQuestion("set the number of words for all functions to 3");

        // Examples NOT IN DATABASE
        test.startQuestion("give me an example of workout");
        test.startQuestion("an example of date");

        // Context
        test.startQuestion("provide me with synonyms of it");
        test.startQuestion("translate it");

        // Settings & Failure & synonym
        test.startQuestion("Set the number of words to 1");
        test.startQuestion("set the number of words for all to 1");
        test.startQuestion("what are synonyms of happy");

        // Garbage input
        test.startQuestion("Please define ThereIsNoWord");
        test.startQuestion("Give me the ThereIsNoFunction of orange juice");
        test.startQuestion("iHaveNoQuestion");
        test.startQuestion("21313 .-,=§$§");
        test.startQuestion(")=§/)$=/=)(/");

        System.out.println("--- Job Done! ---\n");

        System.out.println("---> All automated tests <---\n");

        test.testMessageManager();
        test.testContext();
        test.testDatabaseCommunicator();

    }

    public void startQuestion(String msg) {
        System.out.println("   Question: " + msg);
        System.out.print("   Result:   " + messageManager.decodeMsg(msg, settings, dataComm, context, state));
        System.out.println(" ...");
        reader.nextLine();
    }

    public void testPerformance() {

        Result result = JUnitCore.runClasses(PerformanceTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println("    -> Failure: " + failure.toString());
        }

        System.out.println("\n    ---> Performance tests finished ...");
        reader.nextLine();
    }

    public void testMessageManager() {

        System.out.println("--- Testing MessageManager ---");

        Result result = JUnitCore.runClasses(MessageManagerTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println("    -> Failure: " + failure.toString());
        }

        if(result.getFailureCount() == 0) {
            System.out.println("    -> Everything works as expected :)");
        }

        System.out.println("--- Finished testing MessageManager ---\n");
    }

    public void testContext() {

        System.out.println("--- Testing Context ---");

        Result result = JUnitCore.runClasses(ContextTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println("    -> Failure: " + failure.toString());
        }

        if(result.getFailureCount() == 0) {
            System.out.println("    -> Everything works as expected :)");
        }

        System.out.println("--- Finished testing Context ---\n");
    }

    public void testDatabaseCommunicator() {

        System.out.println("--- Testing DatabaseCommunicator ---");

        Result result = JUnitCore.runClasses(DatabaseCommunicatorTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println("    -> Failure: " + failure.toString());
        }

        if(result.getFailureCount() == 0) {
            System.out.println("    -> Everything works as expected :)");
        }

        System.out.println("--- Finished testing DatabaseCommunicator ---\n");
    }
}

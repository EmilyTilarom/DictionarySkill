package Tests;

import org.junit.runner.*;
import org.junit.runner.notification.Failure;

public class MainTest {

    public static void main(String[]args) {

//        Result result0 = testMessageManager();

  //      Result result1 = testDatabaseCommunicator();

        Result result2 = testContext();

    }

    private static Result testMessageManager() {

        System.out.println("--- Testing MessageManager ---");

        Result result = JUnitCore.runClasses(MessageManagerTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println("    -> Failure: " + failure.toString());
        }

        if(result.getFailureCount() == 0) {
            System.out.println("    -> Everything works as expected :)");
        }

        System.out.println("--- Finished testing MessageManager ---\n");

        return result;
    }

    private static Result testDatabaseCommunicator() {

        System.out.println("--- Testing DatabaseCommunicator ---");

        Result result = JUnitCore.runClasses(DatabaseCommunicatorTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println("    -> Failure: " + failure.toString());
        }

        if(result.getFailureCount() == 0) {
            System.out.println("    -> Everything works as expected :)");
        }

        System.out.println("--- Finished testing DatabaseCommunicator ---\n");

        return result;
    }

    private static Result testContext() {

        System.out.println("--- Testing Context ---");

        Result result = JUnitCore.runClasses(DatabaseCommunicatorTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println("    -> Failure: " + failure.toString());
        }

        if(result.getFailureCount() == 0) {
            System.out.println("    -> Everything works as expected :)");
        }

        System.out.println("--- Finished testing Context ---\n");

        return result;
    }
}
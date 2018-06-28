/**
 * 27.06.2018
 * NEW:
 * -    Performance test cases
 *
 * @author Walter
 *
 */

package Tests;

import DicSkill.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the performance of the skill based on the time it takes to produce an output.
 * One second os the best case scenario, three seconds the average case and five seconds the worst case scenario.
 * For this test the maximum number of seconds will be set to three.
 *
 * All queries are long so that the performance can be evaluated in a useful manner.
 */
public class PerformanceTest {

    public static long MAX_TIME = 1;                // Maximum time for a query to be processed

    private MessageManager mm;
    private State state;
    private Settings settings;
    private DatabaseCommunicator databaseCom;
    private Context context;

    @Before
    public void setUp() throws Exception {

        mm = new MessageManager();
        state = new State();
        settings = new Settings();
        databaseCom = new DatabaseCommunicator();
        context = new Context();
    }

    @After
    public void tearDown() throws Exception {

        mm = null;
        settings = null;
        databaseCom = null;
        context = null;
    }

    @Test
    public void testDefinitionOnPerformance() {

        String message = "What is the definition of luck";

        long startTime = System.currentTimeMillis();

        mm.decodeMsg(message, settings, databaseCom, context, state);

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;      // Transform to seconds

        Assert.assertTrue(duration < MAX_TIME);
    }

    @Test
    public void tesTranslateOnPerformance() {

        String message = "What is the translation of hardware";

        long startTime = System.currentTimeMillis();

        mm.decodeMsg(message, settings, databaseCom, context, state);

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;      // Transform to seconds

        Assert.assertTrue(duration < MAX_TIME);
    }

    @Test
    public void testSynonymsOnPerformance() {

        String message = "please give me synonyms of true";

        long startTime = System.currentTimeMillis();

        mm.decodeMsg(message, settings, databaseCom, context, state);

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;      // Transform to seconds

        Assert.assertTrue(duration < MAX_TIME);
    }

    @Test
    public void testExampleOnPerformance() {

        String message = "examples of happy yes margaret, i will bring out the trash";

        long startTime = System.currentTimeMillis();

        mm.decodeMsg(message, settings, databaseCom, context, state);

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;      // Transform to seconds

        Assert.assertTrue(duration < MAX_TIME);
    }

    @Test
    public void testSpellOnPerformance() {

        String message = "please spell bright";

        long startTime = System.currentTimeMillis();

        mm.decodeMsg(message, settings, databaseCom, context, state);

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;      // Transform to seconds

        Assert.assertTrue(duration < MAX_TIME);
    }

    @Test
    public void testScrabble_StartOnPerformance() {

        String message = "words that start with joy yes margaret, i will bring out the trash";

        long startTime = System.currentTimeMillis();

        mm.decodeMsg(message, settings, databaseCom, context, state);

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;      // Transform to seconds

        Assert.assertTrue(duration < MAX_TIME);
    }

    @Test
    public void testWhatCanYouDoOnPerformance() {

        String message = "What can you do";

        long startTime = System.currentTimeMillis();

        mm.decodeMsg(message, settings, databaseCom, context, state);

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;      // Transform to seconds

        Assert.assertTrue(duration < MAX_TIME);
    }

    /**
     * The counter starts with the second query. The first query merely provides a context to look for.
     */
    @Test
    public void testMoreResultsOnPerformance() {

        String message = "examples of happy";
        mm.decodeMsg(message, settings, databaseCom, context, state);

        long startTime = System.currentTimeMillis();

        message = "more results";
        mm.decodeMsg(message, settings, databaseCom, context, state);

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;      // Transform to seconds

        Assert.assertTrue(duration < MAX_TIME);
    }
}
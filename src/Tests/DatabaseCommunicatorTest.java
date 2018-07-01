/**
 * 27.06.2018
 * NEW:
 * -    implemented translate
 * -    bugfixes
 *
 * @author Walter
 *
 */

/**
 * 13.06.2018
 * TODO:
 * -    Implement translate test case
 * NEW:
 * -    Added test case for all functions (except translate)
 *
 * @author Walter
 *
 */

package Tests;

import DicSkill.Context;
import DicSkill.DatabaseCommunicator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * These tests run multiple number of different Strings paired
 * with multiple numbers for the expected number of outputs (now).
 *
 * Tests are divided between words that make sense and those that do not.
 * Words that do not make sense or cant be found must always return null
 * Those that make sense should return either an answer or null depending on the numberOfWords expected.
 *
 */
public class DatabaseCommunicatorTest {

    private DatabaseCommunicator dc;
    private String[] wishedWordsThatMakeSense;
    private String[] wishedWordsThatMakeNOSense;

    @Before
    public void setUp() throws Exception {

        dc = new DatabaseCommunicator();

    }

    @After
    public void tearDown() throws Exception {

        dc = null;
        wishedWordsThatMakeSense = null;
        wishedWordsThatMakeNOSense = null;
    }

    @Test
    public void testDefineForWishedWordsThatMakeSense() {

        wishedWordsThatMakeSense = new String[] {"success", "running", "certain", "apple juice"};
        Context context = new Context();
        String[] result;

        for (String ww: wishedWordsThatMakeSense) {

            result = dc.define(ww, -1, context);
            Assert.assertNull(result);

            result = dc.define(ww, 0, context);
            Assert.assertNull(result);

            result = dc.define(ww, 1, context);
            Assert.assertNotNull(result[0]);

            result = dc.define(ww, 100, context);
            Assert.assertNotNull(result[0]);
        }

    }

    @Test
    public void testDefineForWishedWordsThatDoNotMakeSense() {

        wishedWordsThatMakeNOSense = new String[] {"apple juice please", "thisWordDoesNotExist", "!?", "-.", "420", " ", "" };
        Context context = new Context();
        String[] result;

        for (String ww: wishedWordsThatMakeNOSense) {

            result = dc.define(ww, -1, context);
            Assert.assertNull(result);

            result = dc.define(ww, 0, context);
            Assert.assertNull(result);

            result = dc.define(ww, 1, context);
            Assert.assertNull(result);

            result = dc.define(ww, 100, context);
            Assert.assertNull(result);
        }

    }

    /**
     * There are only a certain number of synonyms in the WordNet database.
     * Thus wishedWordsThatMakeSense has to be adapted.
     * Tests for these are in the lower loop.
     */
    @Test
    public void testSynonymsForWishedWordsThatMakeSense() {

        wishedWordsThatMakeSense = new String[] {"certain", "true", "happy", "walking", "love"};

        String[] result;

        for (String ww: wishedWordsThatMakeSense) {

            result = dc.giveSynonyms(ww, -1);

            Assert.assertNull(result);

            result = dc.giveSynonyms(ww, 0);
            Assert.assertNull(result);

            result = dc.giveSynonyms(ww, 1);
            Assert.assertNotNull(result[0]);

            result = dc.giveSynonyms(ww, 100);
            Assert.assertNotNull(result[0]);
        }
    }

    @Test
    public void testSynonymsForWishedWordsThatDoNotMakeSense() {

        wishedWordsThatMakeNOSense = new String[] {"apple juice please", "thisWordDoesNotExist", "!?", "-.", "420", " ", "" };

        String[] result;

        for (String ww: wishedWordsThatMakeNOSense) {

            result = dc.giveSynonyms(ww, -1);
            Assert.assertNull(result);

            result = dc.giveSynonyms(ww, 0);
            Assert.assertNull(result);

            result = dc.giveSynonyms(ww, 1);
            Assert.assertNull(result);

            result = dc.giveSynonyms(ww, 100);
            Assert.assertNull(result);
        }

    }

    /**
     * There are no examples for words consisting of multiple words.
     * Thus wishedWordsThatMakeSense has to be adapted.
     * Tests for these are in the lower loop.
     */
    @Test
    public void testExamplesForWishedWordsThatMakeSense() {

        wishedWordsThatMakeSense = new String[] {"success", "running", "certain"};

        String[] result;

        for (String ww: wishedWordsThatMakeSense) {

            result = dc.giveExamples(ww, -1);
            Assert.assertNull(result);

            result = dc.giveExamples(ww, 0);
            Assert.assertNull(result);

            result = dc.giveExamples(ww, 1);
            Assert.assertNotNull(result[0]);

            result = dc.giveExamples(ww, 100);
            Assert.assertNotNull(result[0]);
        }

    }

    @Test
    public void testExamplesForWishedWordsThatDoNotMakeSense() {

        wishedWordsThatMakeNOSense = new String[] {"apple juice please", "thisWordDoesNotExist", "!?", "-.", "420", " ", "" };

        String[] result;

        for (String ww: wishedWordsThatMakeNOSense) {

            result = dc.giveExamples(ww, -1);
            Assert.assertNull(result);

            result = dc.giveExamples(ww, 0);
            Assert.assertNull(result);

            result = dc.giveExamples(ww, 1);
            Assert.assertNull(result);

            result = dc.giveExamples(ww, 100);
            Assert.assertNull(result);
        }

    }


    @Test
    public void testSpellForWishedWordsThatMakeSense() {

        wishedWordsThatMakeSense = new String[] {"success", "running", "certain", "apple juice"};

        String[] result;

        for (String ww: wishedWordsThatMakeSense) {

            result = dc.spell(ww);
            Assert.assertNotNull(result[0]);
            Assert.assertNotNull(result[ww.length()-1]);
            Assert.assertEquals(result.length, ww.length());
        }

    }

    @Test
    public void testSpellForWishedWordsThatDoNotMakeSense() {

        wishedWordsThatMakeNOSense = new String[] {"apple juice please", "thisWordDoesNotExist", "!?", "-.", "420", " ", "" };

        String[] result;

        for (String ww: wishedWordsThatMakeNOSense) {

            result = dc.spell(ww);
            Assert.assertNotNull(result);
            Assert.assertEquals(result.length, ww.length());
        }

    }

    /**
     * There are only a limited number of possibilities.
     * Thus wishedWordsThatMakeSense has to be adapted.
     */
    @Test
    public void testScrabble_StartForWishedWordsThatMakeSense() {

        wishedWordsThatMakeSense = new String[] {"success", "fa", "cat"};

        String[] result;

        for (String ww: wishedWordsThatMakeSense) {

            result = dc.scrabble_start(ww, -1);
            Assert.assertNull(result);

            result = dc.scrabble_start(ww, 0);
            Assert.assertNull(result);

            result = dc.scrabble_start(ww, 1);
            Assert.assertTrue(result[0].contains(ww));

            result = dc.scrabble_start(ww, 100);
            Assert.assertTrue(result[0].contains(ww));
        }

    }

    @Test
    public void testScrabble_StartForWishedWordsThatDoNotMakeSense() {

        wishedWordsThatMakeNOSense = new String[] {"apple juice please", "thisWordDoesNotExist", "!?", "-.", "420", " ", "" };

        String[] result;

        for (String ww: wishedWordsThatMakeNOSense) {

            result = dc.scrabble_start(ww, -1);
            Assert.assertNull(result);

            result = dc.scrabble_start(ww, 0);
            Assert.assertNull(result);

            result = dc.scrabble_start(ww, 1);
            Assert.assertNull(result);

            result = dc.scrabble_start(ww, 100);
            Assert.assertNull(result);
        }

    }

    /**
     * There are only a limited number of possibilities.
     * Thus wishedWordsThatMakeSense has to be adapted.
     */
    @Test
    public void testScrabble_EndForWishedWordsThatMakeSense() {

        wishedWordsThatMakeSense = new String[] {"ic", "a", "al"};

        String[] result;

        for (String ww: wishedWordsThatMakeSense) {

            result = dc.scrabble_end(ww, -1);
            Assert.assertNull(result);

            result = dc.scrabble_end(ww, 0);
            Assert.assertNull(result);


            result = dc.scrabble_end(ww, 1);
            Assert.assertTrue(result[0].contains(ww));

            result = dc.scrabble_end(ww, 100);
            Assert.assertTrue(result[0].contains(ww));
        }

    }

    @Test
    public void testScrabble_EndForWishedWordsThatDoNotMakeSense() {

        wishedWordsThatMakeNOSense = new String[]{"apple juice please", "thisWordDoesNotExist", "!?", "-.", "420", " ", ""};

        String[] result;

        for (String ww : wishedWordsThatMakeNOSense) {

            result = dc.scrabble_end(ww, -1);
            Assert.assertNull(result);

            result = dc.scrabble_end(ww, 0);
            Assert.assertNull(result);

            result = dc.scrabble_end(ww, 1);
            Assert.assertNull(result);

            result = dc.scrabble_end(ww, 100);
            Assert.assertNull(result);
        }

    }

    /**
     * There are only a limited number of possibilities.
     * Thus wishedWordsThatMakeSense has to be adapted.
     */
    @Test
    public void testScrabble_ContainForWishedWordsThatMakeSense() {

        wishedWordsThatMakeSense = new String[] {"ic", "l", "al"};

        String[] result;

        for (String ww: wishedWordsThatMakeSense) {

            result = dc.scrabble_contain(ww, -1);
            Assert.assertNull(result);

            result = dc.scrabble_contain(ww, 0);
            Assert.assertNull(result);

            result = dc.scrabble_contain(ww, 1);
            Assert.assertTrue(result[0].contains(ww));

            result = dc.scrabble_contain(ww, 100);
            Assert.assertTrue(result[0].contains(ww));
        }

    }

    @Test
    public void testScrabble_ContainForWishedWordsThatDoNotMakeSense() {

        wishedWordsThatMakeNOSense = new String[]{"apple juice please", "thisWordDoesNotExist", "!?", "-.", "420", " ", ""};

        String[] result;

        for (String ww : wishedWordsThatMakeNOSense) {

            result = dc.scrabble_contain(ww, -1);
            Assert.assertNull(result);

            result = dc.scrabble_contain(ww, 0);
            Assert.assertNull(result);

            result = dc.scrabble_contain(ww, 1);
            Assert.assertNull(result);

            result = dc.scrabble_contain(ww, 100);
            Assert.assertNull(result);
        }

    }
}
/**
 * 01.07.2018
 * NEW:
 * -    Bugfixes
 * @author Walter
 */

/**
 * 26.06.2018
 * NEW:
 * -    made necessary changes due to additional parameter for decode msg, parameter being an Object of class "State"
 * @author Lia
 */

/**
 * 16.06.2018
 * NEW:
 * -    Added test case for all public functions
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

import java.util.ArrayList;

/**
 * context together with mm cuz last functiton last ww
 *
 */
public class ContextTest {

    private Context con;
    private State state;
    private MessageManager mm;
    private Settings settings;
    private DatabaseCommunicator databaseCom;

    @Before
    public void setUp() throws Exception {

        con = new Context();
        state = new State();
        mm = new MessageManager();
        settings = new Settings();
        databaseCom = new DatabaseCommunicator();
    }

    @After
    public void tearDown() throws Exception {

        con = null;

        mm = null;
        settings = null;
        databaseCom = null;
    }

    @Test
    public void testAddingCategory() {

        String[] message = {"medicine", "animals", "sports"};

        // No trail
        con.changePrefCat("add " + message[0] + " to categories");

        // Trail before
        con.changePrefCat("would you be so kind to add " +  message[1] + " to my categories ");

        // Trail after
        con.changePrefCat("add " + message[2] + " to my categories because i enjoy watching sports");

        Assert.assertTrue(con.getPreferredCategory().contains(message[0]));
        Assert.assertTrue(con.getPreferredCategory().contains(message[1]));
        Assert.assertTrue(con.getPreferredCategory().contains(message[2]));
    }

    /**
     * For this case we first must add categories.
     */
    @Test
    public void testRemovingCategory() {

        testAddingCategory();

        String[] message = {"medicine", "animals", "sports"};

        // No trail
        con.changePrefCat("remove " + message[0] + " from my categories" );

        // Trail before
        con.changePrefCat("would you be so kind to remove " + message[1] + " from my categories");

        // Trail after
        con.changePrefCat("remove " + message[2] + " from my categories because i dont want it");

        Assert.assertFalse(con.getPreferredCategory().contains(message[0]));
        Assert.assertFalse(con.getPreferredCategory().contains(message[1]));
        Assert.assertFalse(con.getPreferredCategory().contains(message[2]));

    }

    @Test
    public void testRemoveAllCategories() {

        testAddingCategory();
        con.changePrefCat("delete all categories ");

        Assert.assertTrue(con.getPreferredCategory().isEmpty());

        testAddingCategory();
        con.changePrefCat("please delete all my categories ");

        Assert.assertTrue(con.getPreferredCategory().isEmpty());
    }

    @Test
    public void testIfWishedWordWasRemembered() {

        String ww;
        String message;

        ww = "dog";
        message = "What is the definition of " + ww;
        mm.decodeMsg(message, settings, databaseCom, con, state);
        Assert.assertEquals(con.getLastWishedWord(), ww);

        ww = "cat";
        message = "What is the definition of " + ww + " please i need the answer";
        mm.decodeMsg(message, settings, databaseCom, con, state);
        Assert.assertEquals(con.getLastWishedWord(), ww);

        ww = "walking";
        message = "Give me an example of " + ww;
        mm.decodeMsg(message, settings, databaseCom, con, state);
        Assert.assertEquals(con.getLastWishedWord(), ww);
    }

    @Test
    public void testIfFunctionWordWasRemembered() {

        String function;
        String message;

        function = "definition of";
        message = "What is the " + function + " dog";
        mm.decodeMsg(message, settings, databaseCom, con, state);
        Assert.assertEquals(con.getLastFunctionUsed(), Function.DEFINITION);

        function = "synonym of";
        message = "What is the " + function + " certain please i need the answer";
        mm.decodeMsg(message, settings, databaseCom, con, state);
        Assert.assertEquals(con.getLastFunctionUsed(), Function.SYNONYMS);

        function = "example of";
        message = "Give me an " + function + " walking";
        mm.decodeMsg(message, settings, databaseCom, con, state);
        Assert.assertEquals(con.getLastFunctionUsed(), Function.EXAMPLE);
    }
}
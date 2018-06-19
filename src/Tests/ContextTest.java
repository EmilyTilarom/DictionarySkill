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

/**
 * context together with mm cuz last functiton last ww
 *
 */
public class ContextTest {

    private Context con;

    private MessageManager mm;
    private Settings settings;
    private DatabaseCommunicator databaseCom;

    @Before
    public void setUp() throws Exception {

        con = new Context();

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
        con.changePrefCat("add category " + message[0]);

        // Trail before
        con.changePrefCat("would you be so kind to add category " + message[1]);

        // Trail after
        con.changePrefCat("add category " + message[2] + "because i enjoy watching sports");

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
        con.changePrefCat("remove category " + message[0]);

        // Trail before
        con.changePrefCat("would you be so kind to remove category " + message[1]);

        // Trail after
        con.changePrefCat("remove category " + message[2] + "because i enjoy watching sports");

        Assert.assertFalse(con.getPreferredCategory().contains(message[0]));
        Assert.assertFalse(con.getPreferredCategory().contains(message[1]));
        Assert.assertFalse(con.getPreferredCategory().contains(message[2]));

    }

    @Test
    public void testRemoveAllCategories() {

        testAddingCategory();

        String[] message = {"medicine", "animals", "sports"};

        con.changePrefCat("remove all categories " + message[0]);

        Assert.assertTrue(con.getPreferredCategory().isEmpty());
    }

    @Test
    public void testIfWishedWordWasRemembered() {

        String ww;
        String message;

        ww = "dog";
        message = "What is the definition of " + ww;
        mm.decodeMsg(message, settings, databaseCom, con);
        Assert.assertEquals(con.getLastWishedWord(), ww);

        ww = "cat";
        message = "What is the definition of " + ww + " please i need the answer";
        mm.decodeMsg(message, settings, databaseCom, con);
        Assert.assertEquals(con.getLastWishedWord(), ww);

        ww = "enlightenment";
        message = "Give me an example of" + ww;
        mm.decodeMsg(message, settings, databaseCom, con);
        Assert.assertEquals(con.getLastWishedWord(), ww);
    }

    @Test
    public void testIfFunctionWordWasRemembered() {

        String function;
        String message;

        function = "definition of";
        message = "What is the " + function + " dog";
        mm.decodeMsg(message, settings, databaseCom, con);
        Assert.assertEquals(con.getLastFunctionUsed(), Function.DEFINITION);

        function = "definition of";
        message = "What is the " + function + " cat please i need the answer";
        mm.decodeMsg(message, settings, databaseCom, con);
        Assert.assertEquals(con.getLastWishedWord(), Function.DEFINITION);

        function = "example of";
        message = "Give me an " + function + " walking";
        mm.decodeMsg(message, settings, databaseCom, con);
        Assert.assertEquals(con.getLastWishedWord(), Function.EXAMPLE);
    }
}
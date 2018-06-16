/**
 * 15.06.2018
 * NEW:
 * -    Added test case for all public functions
 *
 * @author Walter
 *
 */

package Tests;

import DicSkill.Context;
import DicSkill.DatabaseCommunicator;
import DicSkill.MessageManager;
import DicSkill.Settings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * All functions are tested on multiple inputs.
 * If the input is garbage an ErrorMessage should be generated. There always has to be an answer.
 * The output is deterministic if the system works, thus most cases look for equal Strings.
 *
 * Error messages:
 *      - Sorry, we did not find any entry matching your query
 *      - Sorry, I don't know which function you are asking for
 *      - Sorry, I did not understand you.
 */
public class MessageManagerTest {

    private MessageManager mm;

    private Settings settings;
    private DatabaseCommunicator databaseCom;
    private Context context;

    @Before
    public void setUp() throws Exception {

        mm = new MessageManager();

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
    public void decodeBullshitInput() {

        String result;
        String message;

        // One word only
        message = "single";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Sorry, I don't know which function you are asking for." ,result);

        // No word
        message = "";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Sorry, I don't know which function you are asking for." ,result);

        // null
        message = null;
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Sorry, I did not understand you.", result);

        // Function makes no sense
        message = "definiiitiiioon of today";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Sorry, I don't know which function you are asking for." ,result);


    }

    /**
     * WIP
     */
    @Test
    public void decodeFunctionTranslateOnDifferentInputs() {
/*
        String result;
        String message;

        // no trail
        message = "translate important";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("The definition is an event that accomplishes its intended purpose ", result);

        // Long trail before
        message = "What is the definition of luck";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("The definition is your overall circumstances or condition in life (including everything that happens to you) " , result);

        // Long trail after
        message = "definition of health What the fuck jimmy put that down instantly or i will";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("The definition is a healthy state of wellbeing free from disease " ,result);

        // WishedWord makes no sense
        message = "definition of thisWordDoesNotExist";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Sorry, we did not find any entry matching your query" ,result);
*/
    }

    @Test
    public void decodeFunctionDefineOnDifferentInputs() {

        String result;
        String message;

        // no trail
        message = "define success";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("The definition is an event that accomplishes its intended purpose ", result);

        // Short trail before
        message = "Please define enlightenment";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("The definition is education that results in understanding and the spread of knowledge ", result);

        // Long trail before
        message = "What is the definition of luck";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("The definition is your overall circumstances or condition in life (including everything that happens to you) " , result);

        // Long trail after
        message = "definition of health What the fuck jimmy put that down instantly or i will";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("The definition is a healthy state of wellbeing free from disease " ,result);

        // WishedWord makes no sense
        message = "definition of thisWordDoesNotExist";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Sorry, we did not find any entry matching your query" ,result);
    }

    @Test
    public void decodeFunctionSynonymsOnDifferentInputs() {

        String result;
        String message;

        // no trail
        message = "synonym of certain";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Synonyms for certain are definite convinced positive ", result);

        // Long trail before
        message = "please give me synonyms of true";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Synonyms for true are actual genuine literal ", result);

        // Long trail after
        message = "synonym of happy yes margaret, i will bring out the trash";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Synonyms for happy are blessed blissful bright " ,result);


        // WishedWord makes no sense
        message = "synonym of thisWordDoesNotExist";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Sorry, we did not find any entry matching your query" ,result);

    }

    @Test
    public void decodeFunctionExampleOnDifferentInputs() {

        String result;
        String message;

        // no trail
        message = "example of luck";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Examples for luck are the luck of the Irish bad luck caused his downfall it was my good luck to be there ", result);

        // Long trail before
        message = "please give me examples of true";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Examples for true are the story is true it is undesirable to believe a proposition when there is no ground whatever for supposing it true the true meaning of the statement ", result);

        // Long trail after
        message = "examples of happy yes margaret, i will bring out the trash";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Examples for happy are a happy smile spent many happy days on the beach a happy marriage " ,result);

        // WishedWord makes no sense
        message = "example of thisWordDoesNotExist";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Sorry, we did not find any entry matching your query" ,result);
    }

    @Test
    public void decodeFunctionSpellOnDifferentInputs() {

        String result;
        String message;

        // no trail
        message = "spell luck";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("luck is spelled l u c k ", result);

        // Long trail before
        message = "please spell bright";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("bright is spelled b r i g h t ", result);
    }

    @Test
    public void decodeFunctionScrabble_StartOnDifferentInputs() {

        String result;
        String message;

        // no trail
        message = "start with fun";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Words which start with fun are fun run funafuti funambulism ", result);

        // Long trail before
        message = "please give me words that start with imp";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Words which start with imp are impact impact printer impacted fracture ", result);

        // Long trail after
        message = "words that start with joy yes margaret, i will bring out the trash";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Words which start with joy are joyce joyce carol oates joyfulness " ,result);

        // WishedWord makes no sense
        message = "example of thisWordDoesNotExist";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Sorry, we did not find any entry matching your query" ,result);
    }

    @Test
    public void decodeFunctionWhatCanYouDoOnDifferentInputs() {

        String result;
        String message;

        // no trail
        message = "start with fun";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Words which start with fun are fun run funafuti funambulism ", result);

        // Long trail before
        message = "please give me words that start with imp";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Words which start with imp are impact impact printer impacted fracture ", result);

        // Long trail after
        message = "words that start with joy yes margaret, i will bring out the trash";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Words which start with joy are joyce joyce carol oates joyfulness " ,result);

        // WishedWord makes no sense
        message = "example of thisWordDoesNotExist";
        result = mm.decodeMsg(message, settings, databaseCom, context);
        Assert.assertEquals("Sorry, we did not find any entry matching your query" ,result);
    }

    @Test
    public void shortenWishedWord() {
    }
}
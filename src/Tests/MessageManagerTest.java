/**
 * 27.06.2018
 * NEW:
 * -    added testSettings and testMoreResults
 * -    bug fixes
 * @author Walter
 */

/**
 * 26.06.2018
 * NEW:
 * -    made necessary changes due to additional parameter for decode msg, parameter being an Object of class "State"
 * @author Lia
 */

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
import DicSkill.State;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * All functions are tested on multiple inputs.
 * If the input is garbage an ErrorMessage should be generated. There always has to be an answer.
 * The output is deterministic if the system works, thus most cases look for equal Strings.
 *
 * If the wished word does not make sense, the last one will be used. Tests are prepared accordingly.
 *
 */
public class MessageManagerTest {

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
    public void decodeBadInput() {

        String result;
        String message;

        // One word only
        message = "single";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Sorry, I don't know which function you are asking for." ,result);

        // No word
        message = "";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Sorry, I don't know which function you are asking for." ,result);

        // null
        message = null;
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Oops, your message did not arrive.", result);

        // Function makes no sense
        message = "definiiitiiioon of today";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Sorry, I don't know which function you are asking for." ,result);
    }


    @Test
    public void testMoreResultsForWorkingContext() {

        String result;
        String message;

        // Prepare to remember context
        message = "translation of beautiful";
        mm.decodeMsg(message, settings, databaseCom, context, state);

        // Looking for context
        message = "provide me with more words";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("The translation of beautiful is wunderhübsch." ,result);

        // Keep looking for context
        message = "provide me with even more words";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("The translation of beautiful is wunderschön.", result);

        // Evaluate if wished words was remembered
        message = "what are synonyms for it";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Synonyms for beautiful are beauteous, bonny, bonnie." ,result);
    }

    @Test
    public void testSettingsOnDifferentInputs() {

        String result;
        String message;

        // Change function define
        message = "set the number of words for definitions to 4";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        message = "what is the definition of dog";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Definitions of dog are a member of the genus Canis (probably descended from the common wolf) that has been domesticated by man since prehistoric times; occurs in many breeds, a dull unattractive unpleasant girl or woman, informal term for a man, someone who is morally reprehensible." ,result);

        // Change function translate
        message = "set the number of words for translations to 6";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        message = "what is the translation of great";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("The translations of great are groß, bedeutend, großartig, fabelhaft, fantastisch, phantastisch." ,result);


        // Retverting changes for other tests
        message = "set the number of words for definitions to 1";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);

        message = "set the number of words for translations to 1";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
    }


    @Test
    public void decodeFunctionTranslateOnDifferentInputs() {

        String result;
        String message;

        // no trail
        message = "translate important";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("The translation of important is belangvoll.", result);

        // Long trail before
        message = "What is the translation of luck";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("The translation of luck is Glück." , result);

        // Long trail after
        message = "translation of cool What the fuck jimmy put that down instantly or i will";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("The translation of cool is gelassen." ,result);

        // WishedWord makes no sense
        message = "translate thisWordDoesNotExist";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Sorry, I don't have any results for your request." ,result);
    }

    @Test
    public void decodeFunctionDefineOnDifferentInputs() {

        String result;
        String message;

        // no trail
        message = "define success";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("The definition is an event that accomplishes its intended purpose.", result);

        // Short trail before
        message = "Please define enlightenment";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("The definition is education that results in understanding and the spread of knowledge.", result);

        // Long trail before
        message = "What is the definition of luck";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("The definition is your overall circumstances or condition in life (including everything that happens to you)." , result);

        // Long trail after
        message = "definition of health What the fuck jimmy put that down instantly or i will";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("The definition is a healthy state of wellbeing free from disease." ,result);

        // WishedWord makes no sense
        message = "definition of thisWordDoesNotExist";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Sorry, I don't have any results for your request." ,result);
    }

    @Test
    public void decodeFunctionSynonymsOnDifferentInputs() {

        String result;
        String message;

        // no trail
        message = "synonym of certain";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);

        Assert.assertEquals("Synonyms for certain are definite, convinced, positive.", result);

        // Long trail before
        message = "please give me synonyms of true";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Synonyms for true are actual, genuine, literal.", result);

        // Long trail after
        message = "synonym of happy yes margaret, i will bring out the trash";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Synonyms for happy are blessed, blissful, bright." ,result);

        // WishedWord makes no sense
        message = "synonym of thisWordDoesNotExist";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Sorry, I don't have any results for your request." ,result);

    }

    @Test
    public void decodeFunctionExampleOnDifferentInputs() {

        String result;
        String message;

        // no trail
        message = "example of luck";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);

        Assert.assertEquals("Examples for luck are the luck of the Irish, bad luck caused his downfall, it was my good luck to be there.", result);

        // Long trail before
        message = "please give me examples of true";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Examples for true are the story is true, it is undesirable to believe a proposition when there is no ground whatever for supposing it true, the true meaning of the statement.", result);

        // Long trail after
        message = "examples of happy yes margaret, i will bring out the trash";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Examples for happy are a happy smile, spent many happy days on the beach, a happy marriage." ,result);

        // WishedWord makes no sense
        message = "example of thisWordDoesNotExist";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Sorry, I don't have any results for your request." ,result);
    }

    @Test
    public void decodeFunctionSpellOnDifferentInputs() {

        String result;
        String message;

        // no trail
        message = "spell luck";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("luck is spelled l, u, c, k.", result);

        // Long trail before
        message = "please spell bright";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("bright is spelled b, r, i, g, h, t.", result);
    }

    @Test
    public void decodeFunctionScrabble_StartOnDifferentInputs() {

        String result;
        String message;

        // no trail
        message = "start with fun";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Words, which start with fun are funafuti, funambulism, funambulist.", result);

        // Long trail before
        message = "please give me words that start with imp";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Words, which start with imp are impact, impact printer, impacted fracture.", result);

        // Long trail after
        message = "words that start with joy yes margaret, i will bring out the trash";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("Words, which start with joy are joyce, joyce carol oates, joyfulness." ,result);

        // WishedWord makes no sense
        message = "example of thisWordDoesNotExist";
        result = mm.decodeMsg(message, settings, databaseCom, context, state);
        Assert.assertEquals("An example for joy is a joy to behold." ,result);
    }

}
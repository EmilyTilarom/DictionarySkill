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

import DicSkill.DatabaseCommunicator;
import org.junit.Assert;

public class DatabaseCommunicatorTest {

    private DatabaseCommunicator dc;

    @org.junit.Test
    public void translate() {

        dc = new DatabaseCommunicator();

        // WIP Waiting for lucene
    }

    @org.junit.Test
    public void define() {

        dc = new DatabaseCommunicator();

        String ww = "passion";
        String[] ar;

        ar = dc.define(ww, -1);
        Assert.assertTrue(ar == null);

        ar = dc.define(ww, 0);
        Assert.assertTrue(ar.length == 0);

        ar = dc.define(ww, 1);
        Assert.assertTrue(ar[0] != null);

        ar = dc.define(ww, 100);
        Assert.assertTrue(ar[2] != null);
    }

    @org.junit.Test
    public void giveSynonyms() {

        dc = new DatabaseCommunicator();

        String ww = "certain";
        String[] ar;

        ar = dc.giveSynonyms(ww, -1);
        Assert.assertTrue(ar == null);

        ar = dc.giveSynonyms(ww, 0);
        Assert.assertTrue(ar.length == 0);

        ar = dc.giveSynonyms(ww, 1);
        Assert.assertTrue(ar[0] != null);

        ar = dc.giveSynonyms(ww, 100);
        Assert.assertTrue(ar[2] != null);
    }

    @org.junit.Test
    public void giveExamples() {

        dc = new DatabaseCommunicator();

        String ww = "trust";
        String[] ar;

        ar = dc.giveExamples(ww, -1);
        Assert.assertTrue(ar == null);

        ar = dc.giveExamples(ww, 0);
        Assert.assertTrue(ar.length == 0);

        ar = dc.giveExamples(ww, 1);
        Assert.assertTrue(ar[0] != null);

        ar = dc.giveExamples(ww, 100);
        Assert.assertTrue(ar[2] != null);

    }

    @org.junit.Test
    public void spell() {

        dc = new DatabaseCommunicator();

        String ww = "success";
        String[] ar;

        ar = dc.spell(ww);
        Assert.assertTrue(ar[0] != null);
        Assert.assertTrue(ar[6] != null);
        Assert.assertTrue(ar.length == ww.length());
    }

    @org.junit.Test
    public void scrabble_start() {

        dc = new DatabaseCommunicator();

        String ww = "cat";
        String[] ar;

        ar = dc.scrabble_start(ww, -1);
        Assert.assertTrue(ar == null);

        ar = dc.scrabble_start(ww, 1);
        Assert.assertTrue(ar[0].contains("cat"));

        ar = dc.scrabble_start(ww, 100);
        Assert.assertTrue(ar[2].contains("cat"));
    }

    @org.junit.Test
    public void scrabble_end() {

        dc = new DatabaseCommunicator();

        String ww = "sea";
        String[] ar;

        ar = dc.scrabble_end(ww, -1);
        Assert.assertTrue(ar == null);

        ar = dc.scrabble_start(ww, 1);
        Assert.assertTrue(ar[0].contains("sea"));

        ar = dc.scrabble_start(ww, 100);
        Assert.assertTrue(ar[2].contains("sea"));
    }

    @org.junit.Test
    public void scrabble_contain() {

        dc = new DatabaseCommunicator();

        String ww = "sea";
        String[] ar;

        ar = dc.scrabble_contain(ww, -1);
        Assert.assertTrue(ar == null);

        ar = dc.scrabble_contain(ww, 1);
        Assert.assertTrue(ar[0].contains("sea"));

        ar = dc.scrabble_contain(ww, 100);
        Assert.assertTrue(ar[2].contains("sea"));
    }
}
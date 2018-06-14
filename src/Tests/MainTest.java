package Tests;

public class MainTest {

    public static void main(String[]args) {

        DatabaseCommunicatorTest dcT = new DatabaseCommunicatorTest();

        // dcT.translate();
        dcT.define();
        dcT.giveExamples();
        dcT.giveSynonyms();
        dcT.spell();
        dcT.scrabble_start();
        dcT.scrabble_end();


    }
}

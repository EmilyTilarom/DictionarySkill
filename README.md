## DictionarySkill


This Project is a students project for an offline smart speaker.


This is a dictionary skill. This project can be imported in eclipse. Everything, including test classes,
is included to make viewing and changing the code easier.
Note, that if you import the project in your IDE, you may have to
import the jars again. All general jars are found in the folder "ReferenceLibraries". rita.jar is found 
in "dict/English".


## Motivation
This is a students project in the course SWE Praktikum under the lead of Professor Peinl.
The goal is to create a skill for a voice activated environment (a smart speaker).

## Code style
At the beginning of each file is a list of comments which each include a date, a list of changes made, 
a list of changes, which still have to be made and the person who made the changes as well as the comment.

## Tech/framework used
<b>Built with</b>
- [WordNet](https://wordnet.princeton.edu/)
- [Lucene](https://lucene.apache.org/)
- [OSGI](https://www.osgi.org/)

## Features
Following features are implemented:
- Translation
- Definition
- Synonyms
- Spelling
- Example sentences
- Scrabble functions (words which start with/end with/contain a sequence of letters)
- More results for the same function
- Preffered Categories (add, remove, delete all)
- Changing the output number via settings for one or all functions



## How to use?
1. Download the repository
2. Open cmd
3. Change your directory to be inside the "DictionarySkill" folder
4. type
	java -jar Dictionary.jar
   in cmd.

"Error: Unable to access jarfile"
If you get this error message you may want to try using the complete path and putting it in quotes.
Example: Your path is C:\Users\Username\Desktop\New Folder\DictionarySkill". "New Folder" includes a space, 
this can cause this error.
type
	java -jar "C:\Users\Username\Desktop\New Folder\DictionarySkill\Dictionary.jar"
in cmd.

## Tests
The second jar is a TestSkript. Junit version 4 was used. To run the TestSkript run the 2nd jar "DictionaryTest.jar" as explained above.

## Credits
Fellow students who helped us with a lot of advice and Prof. Peinl.



Thank your for using the dictionary skill.

HOF UNIVERSITY OF APPLIED SCIENCES © [Adrian Häussler, Lia Frischholz, Walter Ehrenberger]()

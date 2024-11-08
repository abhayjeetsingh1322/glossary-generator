import java.util.Comparator;

import components.map.Map;
import components.map.Map3;
import components.queue.Queue;
import components.queue.Queue2;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * This program takes a input file that contains words and definitions (specific
 * format of file is provided in program). And it generates individual HTML
 * pages that contains the word and definition, the program links words that are
 * within the definition as well. Lastly, the program generates an index page
 * HTML page that contains all of the words.
 *
 * @author A. Singh
 */
public final class Glossary {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private Glossary() {
        // no code needed here
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {
        SimpleWriter out = new SimpleWriter1L();
        SimpleReader in = new SimpleReader1L();

        //Getting file location and folder to save generated files
        out.print("Please enter the name of the file "
                + "(including the folder name and .txt extension): ");
        String filename = in.nextLine();

        out.print(
                "Please enter the name of the folder you would like the output file: ");
        String outputFolder = in.nextLine();

        //Creating variables needed for the methods
        SimpleReader inFile = new SimpleReader1L(filename);

        Queue<String> word = new Queue2<>();
        Map<String, String> wordAndMeaning = new Map3<>();

        //Calling methods
        extractFromFile(inFile, word, wordAndMeaning);

        sortWords(word);

        generateWordPages(outputFolder, word, wordAndMeaning);

        generateIndexPage(outputFolder, word);

        //Telling user that the program has terminated
        out.println(
                "Valid HTML pages have been generated and the program has termainted.");

        out.close();
        in.close();
    }

    /**
     * Extracts the words and definitions from inFile (@code SimpleReader), and
     * adds them to word (@code Queue) that contains words and to wordAndMeaning
     * (@code Map) that contains the word (key) and the definition (value).
     *
     * @param inFile
     *            the given (@code SimpleReader) that has the file to read from
     * @param word
     *            the given (@code Queue) that will store the words
     * @param wordAndMeaning
     *            the given (@code Map) that will store the words as keys and
     *            definitions as values
     *
     * @updates word & wordAndMeaning
     *
     * @requires inFile is open & has correct format, word is empty,
     *           wordAndMeaning is empty
     *
     * @ensures all words will be stored in a word, words will be stored as keys
     *          and definitions as values in wordAndMeaning
     */
    public static void extractFromFile(SimpleReader inFile, Queue<String> word,
            Map<String, String> wordAndMeaning) {

        //Entering loop until end of file
        while (!inFile.atEOS()) {

            //Extracting lines
            String wordStr = inFile.nextLine();
            String meaningStr = inFile.nextLine();
            String nextStr = inFile.nextLine();

            //Check if there is more than one line for definition
            while (!nextStr.equals("") && !inFile.atEOS()) {
                meaningStr = meaningStr + nextStr;
                nextStr = inFile.nextLine();
            }

            //Adding word to queue
            word.enqueue(wordStr);

            //Checking if word is already in set if not then adding it with definition
            if (!wordAndMeaning.hasKey(wordStr)) {
                wordAndMeaning.add(wordStr, meaningStr);
            }
        }
        inFile.close();
    }

    /**
     * Sorts all words contained in word (@code Queue) in alphabetic order.
     *
     * @param word
     *            the given (@code Queue) containing words
     *
     * @updates word
     *
     * @requires word contains all of the words that appear from the input file
     *
     * @ensures all words in word are sorted in alphabetic order
     */
    public static void sortWords(Queue<String> word) {

        //Creating comparator object and calling sort method
        Comparator<String> stringComparatorObj = new StringComparator();
        word.sort(stringComparatorObj);
    }

    /**
     * Generates pages HTML pages adhering to the following guidelines: word is
     * red, definition is provided with links to other word pages if needed, and
     * a link to the index page. Saved to the folder that was provided.
     *
     * @param outputFolder
     *            the given (@code String) for folder name
     *
     * @param word
     *            the given (@code Queue) containing words
     *
     * @param wordAndMeaning
     *            the given (@code Map) that contains words (keys) and
     *            definitions (values)
     *
     * @requires outputFolder contains name for a existing folder,
     *           wordAndMeaning contains all words from word as keys and
     *           associated definitions as values
     *
     * @ensures a valid HTML page is generated and saved to the provided folder
     */
    public static void generateWordPages(String outputFolder,
            Queue<String> word, Map<String, String> wordAndMeaning) {

        //Counter to iterate through the queue
        int counter = 0;
        while (counter < word.length()) {

            //Getting the word from queue
            String wordName = word.dequeue();
            word.enqueue(wordName);

            //Declaring a writer to write to file
            SimpleWriter wordWriter = new SimpleWriter1L(
                    outputFolder + "/" + wordName + ".html");

            //Writing to file
            wordWriter.println("<html>");
            wordWriter.println("<head>");
            wordWriter.println("<title>" + wordName + "</title>");
            wordWriter.println("</head>");
            wordWriter.println("<body>");
            wordWriter.println("<h2><b><i><font color=\"red\">" + wordName
                    + "</font></i></b></h2>");
            wordWriter.print("<blockquote>");

            //Getting definition from map
            String meaningDefinition = wordAndMeaning.value(wordName);

            //Declaring a set of characters, and calling method to generate elements
            final String separatorStr = " \t, ";
            Set<Character> separatorSet = new Set1L<>();
            generateElements(separatorStr, separatorSet);

            //Loop to check if the definition contains any reference words
            int position = 0;
            while (position < meaningDefinition.length()) {

                //Calling method to get the word or separators
                String token = nextWordOrSeparator(meaningDefinition, position,
                        separatorSet);

                //Checking if its separator or word
                if (separatorSet.contains(token.charAt(0))) {
                    wordWriter.print(token);
                } else {

                    //Checking if the word is of reference or not
                    if (wordAndMeaning.hasKey(token)) {
                        wordWriter.print("<a href=\"" + token + ".html\">"
                                + token + "</a>");
                    } else {
                        wordWriter.print(token);
                    }
                }
                position += token.length();
            }

            //Writing to file
            wordWriter.println("</blockquote>");
            wordWriter.println("<hr />");
            wordWriter.println(
                    "<p>Return to <a href=\"index.html\">index</a>.</p>");
            wordWriter.println("</body>");
            wordWriter.println("</html>");

            wordWriter.close();
            counter++;
        }
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param charSet
     *            the {@code Set} to be replaced
     * @replaces charSet
     * @ensures charSet = entries(str)
     */
    private static void generateElements(String str, Set<Character> charSet) {
        assert str != null : "Violation of: str is not null";
        assert charSet != null : "Violation of: charSet is not null";

        //Entering loop that will iterate over all characters of string
        int index = 0;
        while (index < str.length()) {
            char x = str.charAt(index);

            //If to check if the set contains x if not then adding it
            if (!charSet.contains(x)) {
                charSet.add(x);
            }

            index++;
        }
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        //Declared variables for method
        String nextStr = "";
        int index = position;

        //If-else statement to check the text is of separators or word
        if (separators.contains(text.charAt(position))) {
            while (index < text.length()
                    && separators.contains(text.charAt(index))) {
                index++;
            }
        } else {
            while (index < text.length()
                    && !separators.contains(text.charAt(index))) {
                index++;
            }
        }

        //Calling substring method to extract the relevant string
        nextStr = text.substring(position, index);

        //Returning the substring
        return nextStr;
    }

    /**
     * Generates HTML page that contains the links to all of the HTML pages of
     * words, in alphabetical order.
     *
     * @param outputFolder
     *            the given (@code String) for the folder name
     *
     * @param word
     *            the given (@code Queue) that contains the words
     *
     * @requires word is sorted in alphabetical order
     *
     * @ensures the page generated is has words in alphabetical order and
     *          associated links
     */
    public static void generateIndexPage(String outputFolder,
            Queue<String> word) {

        //Declaring a writer to the file
        SimpleWriter indexWriter = new SimpleWriter1L(
                outputFolder + "/index.html");

        //Writing to file
        indexWriter.println("<html>");
        indexWriter.println("<head>");
        indexWriter.println("<title>Sample Glossay</title>");
        indexWriter.println("</head>");
        indexWriter.println("<body>");
        indexWriter.println("<h2>Sample Glossary</h2>");
        indexWriter.println("<hr />");
        indexWriter.println("<h3>Index</h3>");
        indexWriter.println("<u1>");

        //Entering loop to generate links to the word pages
        int counter = 0;
        while (counter < word.length()) {
            String wordStr = word.dequeue();
            word.enqueue(wordStr);
            indexWriter.println("<li><a href=\"" + wordStr + ".html\">"
                    + wordStr + "</a></li>");
            counter++;
        }

        //Writing to file
        indexWriter.println("</ul>");
        indexWriter.println("</body>");
        indexWriter.println("</html>");

        indexWriter.close();
    }

    /**
     * A private class that implements the Comparator interface for strings.
     * Called from the Glossary class using an object of type of this class.
     * Contains a override method to compare two strings.
     *
     * @author A. Singh
     *
     */
    private static class StringComparator implements Comparator<String> {

        /**
         * Returns zero if strings are equal. Negative integer if str1 comes
         * first not str2, which means correct order. Positive integer if str2
         * comes first not str1, which means not the correct order. Order is
         * determined alphabetically.
         *
         * @param str1
         *            the given (@code String)
         * @param str2
         *            the given (@code String)
         *
         */
        @Override
        public int compare(String str1, String str2) {
            return (str1.compareToIgnoreCase(str2));
        }
    }

}

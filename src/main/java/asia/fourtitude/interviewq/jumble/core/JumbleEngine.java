package asia.fourtitude.interviewq.jumble.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class JumbleEngine {

    private static final Logger LOG = LoggerFactory.getLogger(JumbleEngine.class);

    private static final String fileName = "words.txt";
    private static Collection<String> globalWords;

    /**
     * From the input `word`, produces/generates a copy which has the same
     * letters, but in different ordering.
     *
     * Example: from "elephant" to "lehnaetp".
     *
     * Evaluation/Grading:
     * a) pass unit test: JumbleEngineTest#scramble()
     * b) scrambled letters/output must not be the same as input
     *
     * @param word  The input word to scramble the letters.
     * @return  The scrambled output/letters.
     */
    public String scramble(String word) {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */
        String result;
        do{
            char[] letters = word.toCharArray();
            int wordLength = letters.length;
            char[] resultArray = new char[wordLength];
            Random random = new Random();
            for(int i = 0; i < letters.length; i++){
                int randomIndex;
                do{
                    randomIndex = random.nextInt(wordLength);
                }while(resultArray[randomIndex] != '\0');
                resultArray[randomIndex] = letters[i];
            }
            result = String.valueOf(resultArray);
        }while(word.equals(result));

        return result;
    }

    /**
     * Retrieves the palindrome words from the internal
     * word list/dictionary ("src/main/resources/words.txt").
     *
     * Word of single letter is not considered as valid palindrome word.
     *
     * Examples: "eye", "deed", "level".
     *
     * Evaluation/Grading:
     * a) able to access/use resource from classpath
     * b) using inbuilt Collections
     * c) using "try-with-resources" functionality/statement
     * d) pass unit test: JumbleEngineTest#palindrome()
     *
     * @return  The list of palindrome words found in system/engine.
     * @see https://www.google.com/search?q=palindrome+meaning
     */
    public Collection<String> retrievePalindromeWords() {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */
        Collection<String> words = new ArrayList<>(readWord());
        words.removeIf(w -> {
            if(w.length() <= 1){
                return true;
            }
            String reverseWord = new StringBuilder(w).reverse().toString();
            return !reverseWord.equals(w);
        });

        return words;
    }

    /**
     * Picks one word randomly from internal word list.
     *
     * Evaluation/Grading:
     * a) pass unit test: JumbleEngineTest#randomWord()
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param length  The word picked, must of length.
     *                When length is null, then return random word of any length.
     * @return  One of the word (randomly) from word list.
     *          Or null if none matching.
     */
    public String pickOneRandomWord(Integer length) {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */
        Collection<String> words = new ArrayList<>(readWord());
        if(length != null){
            words = words.stream().
                    filter((word) -> word.length() == length).
                    collect(Collectors.toList());

        }

        if(words.isEmpty()){
            return null;
        }

        List<String> wordList = new ArrayList<>(words);
        return wordList.get(new Random().nextInt(words.size()));
    }

    /**
     * Checks if the `word` exists in internal word list.
     * Matching is case insensitive.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param word  The input word to check.
     * @return  true if `word` exists in internal word list.
     */
    public boolean exists(String word) {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */
        Collection<String> words = new ArrayList<>(readWord());
        return words.stream().anyMatch(w -> w.equalsIgnoreCase(word));
    }

    /**
     * Finds all the words from internal word list which begins with the
     * input `prefix`.
     * Matching is case insensitive.
     *
     * Invalid `prefix` (null, empty string, blank string, non letter) will
     * return empty list.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param prefix  The prefix to match.
     * @return  The list of words matching the prefix.
     */
    public Collection<String> wordsMatchingPrefix(String prefix) {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */

        if(prefix == null || prefix.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<String> words = new ArrayList<>(readWord());

        return words.stream().filter(w -> {

            if(w.length() < prefix.length()){
                return false;
            }

            for (int i = 0; i < prefix.length(); i++) {
                if (Character.toLowerCase(w.charAt(i)) != Character.toLowerCase(prefix.charAt(i))) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    /**
     * Finds all the words from internal word list that is matching
     * the searching criteria.
     *
     * `startChar` and `endChar` must be 'a' to 'z' only. And case insensitive.
     * `length`, if have value, must be positive integer (>= 1).
     *
     * Words are filtered using `startChar` and `endChar` first.
     * Then apply `length` on the result, to produce the final output.
     *
     * Must have at least one valid value out of 3 inputs
     * (`startChar`, `endChar`, `length`) to proceed with searching.
     * Otherwise, return empty list.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param startChar  The first character of the word to search for.
     * @param endChar    The last character of the word to match with.
     * @param length     The length of the word to match.
     * @return  The list of words matching the searching criteria.
     */
    public Collection<String> searchWords(Character startChar, Character endChar, Integer length) {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */
        boolean startCharCriteria = false, endCharCriteria = false, lengthCriteria = false;

        if(startChar != null && startChar.toString().matches("^[a-zA-Z]")) {
            startCharCriteria = true;
        }

        if(endChar != null && endChar.toString().matches("^[a-zA-Z]")) {
            endCharCriteria = true;
        }

        if(length != null && length >= 1) {
            lengthCriteria = true;
        }

        if(!startCharCriteria && !endCharCriteria && !lengthCriteria){
            LOG.debug(new StringBuilder()
                    .append("startCharCriteria: " + startCharCriteria)
                    .append("\nstartChar: " + startChar)
                    .append("\nendCharCriteria: " + endCharCriteria)
                    .append("\nendChar: " + endChar)
                    .append("\nlengthCriteria: " + lengthCriteria)
                    .append("\nlength: " + length).toString());
            return Collections.emptyList();
        }

        Collection<String> words = new ArrayList<>(readWord());

        if(lengthCriteria){
            words.removeIf(w -> w.length() != length);
        }

        if(startCharCriteria){
            words.removeIf(w ->
                    Character.toLowerCase(w.charAt(0))!= Character.toLowerCase(startChar));
        }

        if(endCharCriteria){
            words.removeIf(w ->
                    Character.toLowerCase(w.charAt(w.length() - 1)) != Character.toLowerCase(endChar));
        }

        return words;
    }

    /**
     * Generates all possible combinations of smaller/sub words using the
     * letters from input word.
     *
     * The `minLength` set the minimum length of sub word that is considered
     * as acceptable word.
     *
     * If length of input `word` is less than `minLength`, then return empty list.
     *
     * The sub words must exist in internal word list.
     *
     * Example: From "yellow" and `minLength` = 3, the output sub words:
     *     low, lowly, lye, ole, owe, owl, well, welly, woe, yell, yeow, yew, yowl
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param word       The input word to use as base/seed.
     * @param minLength  The minimum length (inclusive) of sub words.
     *                   When zero, return empty list.
     *                   Default is 3.
     * @return  The list of sub words constructed from input `word`.
     */
    public Collection<String> generateSubWords(String word, Integer minLength) {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */
        if(minLength == null){
            minLength = 3;
        }

        if(word == null || minLength == 0 || word.length() < minLength){
            return Collections.emptyList();
        }

        HashMap<Character, Integer> wordMap = new HashMap<>();
        for(char c: word.toCharArray()){
            if(wordMap.containsKey(c)){
                wordMap.compute(c, (k, wordCount) -> wordCount + 1);
            }else{
                wordMap.put(c, 1);
            }
        }

        Collection<String> words = new ArrayList<>(readWord());
        Collection<String> subWords = new ArrayList<>();

        Integer finalMinLength = minLength;
        words.removeIf(w -> w.equals(word));
        words.removeIf(w -> w.length() < finalMinLength);

        words.parallelStream().forEach(w -> {
            HashMap<Character, Integer> wordMapTemp = new HashMap<>(wordMap);
            boolean wordFlag = true;

            for (int i = 0; i < w.length(); i++) {
                if (!wordMapTemp.containsKey(w.charAt(i))) {
                    wordFlag = false;
                    break;
                }

                int wordCount = wordMapTemp.get(w.charAt(i));

                if (--wordCount < 0) {
                    wordFlag = false;
                    break;
                }

                wordMapTemp.put(w.charAt(i), wordCount);
            }

            if(wordFlag){
                subWords.add(w);
            }
        });

        return subWords;
    }

    /**
     * Creates a game state with word to guess, scrambled letters, and
     * possible combinations of words.
     *
     * Word is of length 6 characters.
     * The minimum length of sub words is of length 3 characters.
     *
     * @param length     The length of selected word.
     *                   Expects >= 3.
     * @param minLength  The minimum length (inclusive) of sub words.
     *                   Expects positive integer.
     *                   Default is 3.
     * @return  The game state.
     */
    public GameState createGameState(Integer length, Integer minLength) {
        Objects.requireNonNull(length, "length must not be null");
        if (minLength == null) {
            minLength = 3;
        } else if (minLength <= 0) {
            throw new IllegalArgumentException("Invalid minLength=[" + minLength + "], expect positive integer");
        }
        if (length < 3) {
            throw new IllegalArgumentException("Invalid length=[" + length + "], expect greater than or equals 3");
        }
        if (minLength > length) {
            throw new IllegalArgumentException("Expect minLength=[" + minLength + "] greater than length=[" + length + "]");
        }
        String original = this.pickOneRandomWord(length);
        if (original == null) {
            throw new IllegalArgumentException("Cannot find valid word to create game state");
        }
        String scramble = this.scramble(original);
        Map<String, Boolean> subWords = new TreeMap<>();
        for (String subWord : this.generateSubWords(original, minLength)) {
            subWords.put(subWord, Boolean.FALSE);
        }
        return new GameState(original, scramble, subWords);
    }

    /**
     * Read file words.txt
     * @return word
     */
    private Collection<String> readWord(){
        if(globalWords == null || globalWords.isEmpty()) {
            try {
                ClassLoader classLoader = ResourceLoader.class.getClassLoader();
                File file = new File(classLoader.getResource(fileName).getFile());
                globalWords = Files.readAllLines(file.toPath());
            } catch (IOException e) {
                System.out.println("File not found: " + fileName);
                e.printStackTrace();
            }
        }
        return globalWords;
    }

}

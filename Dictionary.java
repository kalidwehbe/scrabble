import java.io.*;
import java.util.*;

/**
 * Dictionary class for validating Scrabble words.
 * Loads a dictionary file and provides fast word validation using a HashSet.
 */
public class Dictionary {
    private final Set<String> words;
    private boolean loadedSuccessfully;
    private String loadError;

    /**
     * Constructs a Dictionary by loading words from the specified file.
     * Each line in the file should contain one valid word.
     * @param filename Path to the dictionary file
     */
    public Dictionary(String filename) {
        words = new HashSet<>();
        loadedSuccessfully = false;
        loadError = null;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim(); // remove leading/trailing whitespace and newlines
                if (!line.isEmpty()) {
                    words.add(line.toUpperCase());
                }
            }
            loadedSuccessfully = true;
        } catch (FileNotFoundException e) {
            loadError = "Dictionary file not found: " + filename;
            System.err.println("ERROR: " + loadError);
        } catch (IOException e) {
            loadError = "Error reading dictionary file: " + e.getMessage();
            System.err.println("ERROR: " + loadError);
        }

        if (loadedSuccessfully && words.isEmpty()) {
            loadError = "Dictionary file is empty!";
            loadedSuccessfully = false;
            System.err.println("ERROR: " + loadError);
        }
    }

    /**
     * Checks if a word is valid according to the loaded dictionary.
     * @param word The word to validate
     * @return true if the word exists in the dictionary, false otherwise
     */
    public boolean isValidWord(String word) {
        if (word == null || word.trim().isEmpty()) return false;
        word = word.trim().toUpperCase(); // trim spaces and convert to uppercase
        return words.contains(word);
    }

    /**
     * Returns whether the dictionary was loaded successfully.
     * @return true if dictionary loaded without errors, false otherwise
     */
    public boolean isLoaded() {
        return loadedSuccessfully;
    }

    /**
     * Returns any error message that occurred during dictionary loading.
     * @return Error message string, or null if no error occurred
     */
    public String getLoadError() {
        return loadError;
    }

    /**
     * Returns the number of words in the dictionary.
     * @return Count of valid words loaded
     */
    public int getWordCount() {
        return words.size();
    }
}
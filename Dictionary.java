import java.io.*;
import java.util.*;

public class Dictionary implements Serializable {
    private static final long serialVersionUID = 1L;
    private Set<String> words;

    public Dictionary(String filename) {
        words = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim(); // remove leading/trailing whitespace and newlines
                if (!line.isEmpty()) {
                    words.add(line.toUpperCase());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading dictionary: " + e.getMessage());
        }
    }

    public Set<String> getAllWords() {
        return new HashSet<>(words); // returns a copy of all words
    }

    public boolean isValidWord(String word) {
        if (word == null) return false;
        word = word.trim().toUpperCase(); // trim spaces and convert to uppercase
        return words.contains(word);
    }

}

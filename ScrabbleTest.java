// ScrambleTest.java — JUnit 4
import org.junit.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ScrambleTest {

    private Path dictPath;
    private GameModel model;
    private CaptureObserver observer;

    /** Observer to capture latest Board + Players after each notifyObservers() */
    private static class CaptureObserver implements GameObserver {
        Board board;
        List<Player> players;

        @Override
        public void update(Board board, List<Player> players) {
            this.board = board;
            this.players = players;
        }
    }

    /** Write a small dictionary file we control */
    private Path writeDictionary(String... words) throws IOException {
        Path p = Files.createTempFile("dict-", ".txt");
        Files.write(p, String.join(System.lineSeparator(), words).getBytes());
        return p;
    }

    /** Build a model with two players and our temp dictionary */
    private GameModel buildModel(List<String> names, Path dict) {
        return new GameModel(names, dict.toString());
    }

    /** Force a player's rack to specific letters (deterministic tests) */
    private void setRack(Player p, String letters) {
        p.getRack().clear();
        for (char c : letters.toCharArray()) {
            p.getRack().add(new Tile(Character.toUpperCase(c), 1));
        }
    }

    /** Assert that the board contains the word starting at (row,col) in given orientation (0-based) */
    private void assertBoardHasWord(Board b, String word, int row, int col, boolean horizontal) {
        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            assertEquals("Mismatch at (" + r + "," + c + ")",
                    Character.toUpperCase(word.charAt(i)),
                    b.getLetterAt(r, c));
        }
    }

    @Before
    public void setUp() throws Exception {
        // Dictionary includes words we’ll use
        dictPath = writeDictionary("CAT", "DOG", "HELLO", "ZOO");

        model = buildModel(Arrays.asList("Alice", "Bob"), dictPath);
        observer = new CaptureObserver();
        model.addObserver(observer);

        // Trigger initial update so observer has references
        model.start();

        assertNotNull("Observer should capture board on start()", observer.board);
        assertNotNull("Observer should capture players on start()", observer.players);
        assertEquals("Expect 2 players", 2, observer.players.size());
    }

    @After
    public void tearDown() throws Exception {
        if (dictPath != null) {
            try { Files.deleteIfExists(dictPath); } catch (Exception ignored) {}
        }
    }

    @Test
    public void firstMove_placesWord_scores_and_advancesTurn() {
        Player current = model.getCurrentPlayer(); // Alice
        setRack(current, "CAT");
        int prevScore = current.getScore();

        // Place at (7,7) horizontally (0-based)
        model.placeWord("CAT", 7, 7, true);

        // Word appears
        assertBoardHasWord(observer.board, "CAT", 7, 7, true);

        // Score = number of new tiles (simplified scoring)
        assertEquals(prevScore + 3, current.getScore());

        // Rack stays 3 after placement
        assertEquals(3, current.getRack().size());

        // Turn advanced to Bob
        assertEquals("Bob", model.getCurrentPlayer().getName());
    }

    @Test
    public void invalidWord_isRejected_noBoard_noScore_noTurnAdvance() {
        Player current = model.getCurrentPlayer(); // Alice
        setRack(current, "ZZZ");

        int r = 5, c = 5;
        assertEquals(0, observer.board.getLetterAt(r, c));

        int prevScore = current.getScore();
        String currentName = current.getName();

        model.placeWord("ZZZ", r, c, true); // not in dictionary

        assertEquals("Should not place tiles for invalid word", 0, observer.board.getLetterAt(r, c));
        assertEquals("Score should not change", prevScore, current.getScore());
        assertEquals("Turn should not advance", currentName, model.getCurrentPlayer().getName());
    }

    @Test
    public void disconnectedSecondMove_isRejected() {
        // First move by Alice
        Player alice = model.getCurrentPlayer();
        setRack(alice, "CAT");
        model.placeWord("CAT", 7, 7, true);

        // Bob tries a disconnected word far away
        Player bob = model.getCurrentPlayer();
        setRack(bob, "DOG");
        int r = 1, c = 1; // far from (7,7)
        assertEquals(0, observer.board.getLetterAt(r, c));
        int prevScore = bob.getScore();
        String bobName = bob.getName();

        model.placeWord("DOG", r, c, true);

        assertEquals("Disconnected placement should fail", 0, observer.board.getLetterAt(r, c));
        assertEquals("No score change on failure", prevScore, bob.getScore());
        assertEquals("Turn should not advance on failure", bobName, model.getCurrentPlayer().getName());
    }

    @Test
    public void passTurn_advancesToNextPlayer() {
        String first = model.getCurrentPlayer().getName();
        model.passTurn();
        String second = model.getCurrentPlayer().getName();
        assertNotEquals(first, second);
    }

    @Test
    public void outOfBounds_isRejected() {
        Player current = model.getCurrentPlayer();
        setRack(current, "HELLO"); // 5 letters
        int prevScore = current.getScore();
        String name = current.getName();

        // Starting at col 13 horizontally (13..17) would overflow 15x15
        model.placeWord("HELLO", 0, 13, true);

        // The attempted start cell should still be empty
        assertEquals(0, observer.board.getLetterAt(0, 13));
        assertEquals(prevScore, current.getScore());
        assertEquals(name, model.getCurrentPlayer().getName());
    }
}

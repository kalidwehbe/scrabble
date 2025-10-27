import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Simple JUnit tests for the Scrabble text-based implementation.
 * Uses the real dictionary.txt file instead of generating one.
 */
public class GameModelTest {

    private GameModel model;
    private GameObserver observer;
    private List<String> playerNames;

    /** Basic observer that captures the latest board and players after each change. */
    private static class TestObserver implements GameObserver {
        Board board;
        List<Player> players;

        @Override
        public void update(Board board, List<Player> players) {
            this.board = board;
            this.players = players;
        }
    }

    /** Runs before each test — sets up the game model. */
    @Before
    public void setUp() {
        playerNames = Arrays.asList("Alice", "Bob");
        model = new GameModel(playerNames, "dictionary.txt");
        observer = new TestObserver();
        model.addObserver(observer);
        model.start();
    }

    /** Runs after each test — clean up if needed. */
    @After
    public void tearDown() {
        model = null;
        observer = null;
    }

    /** Test that a valid word is placed and scored correctly. */
    @Test
    public void testValidWordPlacement() {
        Player player = model.getCurrentPlayer();
        // Give player the letters to form a valid word
        player.getRack().clear();
        player.getRack().add(new Tile('C', 1));
        player.getRack().add(new Tile('A', 1));
        player.getRack().add(new Tile('T', 1));

        boolean placed = model.placeWord("CAT", 7, 7, true);
        assertTrue("Valid word should be placed", placed);
        assertEquals("Score should increase by word length", 3, player.getScore());
    }

    /** Test that an invalid word is rejected. */
    @Test
    public void testInvalidWordPlacement() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('X', 1));
        player.getRack().add(new Tile('Z', 1));
        player.getRack().add(new Tile('Q', 1));

        boolean placed = model.placeWord("XZQ", 7, 7, true);
        assertFalse("Invalid word should not be placed", placed);
        assertEquals("Score should remain unchanged", 0, player.getScore());
    }

    /** Test that passing turn moves to the next player. */
    @Test
    public void testPassTurn() {
        String firstPlayer = model.getCurrentPlayer().getName();
        model.passTurn();
        String nextPlayer = model.getCurrentPlayer().getName();
        assertNotEquals("Turn should advance to next player", firstPlayer, nextPlayer);
    }

    /** Test that out-of-bounds placement is rejected. */
    @Test
    public void testOutOfBoundsPlacement() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('H', 1));
        player.getRack().add(new Tile('E', 1));
        player.getRack().add(new Tile('L', 1));
        player.getRack().add(new Tile('L', 1));
        player.getRack().add(new Tile('O', 1));

        boolean placed = model.placeWord("HELLO", 0, 13, true);
        assertFalse("Word going out of bounds should not be placed", placed);
    }
}

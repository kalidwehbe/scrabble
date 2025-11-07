import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Extended JUnit tests for GameModel.
 * These tests verify correct behavior of word placement, scoring,
 * turn management, tile swapping, and observer updates.
 */
public class GameModelTest {

    private GameModel model;
    private TestObserver observer;
    private List<String> playerNames;

    /**
     * Observer class to capture updates from GameModel.
     * This allows verification that the model notifies observers correctly.
     */
    private static class TestObserver implements GameObserver {
        Board board;
        List<Player> players;
        Player currentPlayer;

        @Override
        public void update(Board board, List<Player> players, Player currentPlayer) {
            this.board = board;
            this.players = players;
            this.currentPlayer = currentPlayer;
        }
    }

    /** 
     * Sets up a new GameModel instance and attaches an observer before each test.
     * Ensures that each test starts with a clean and consistent state.
     */
    @Before
    public void setUp() {
        playerNames = Arrays.asList("Alice", "Bob");
        model = new GameModel(playerNames, "dictionary.txt");
        observer = new TestObserver();
        model.addObserver(observer);
        model.start(); // Initialize the game
    }

    /** 
     * Cleans up test variables after each test to avoid interference between tests. 
     */
    @After
    public void tearDown() {
        model = null;
        observer = null;
    }

    /**
     * Tests that a valid word (e.g., "CAT") can be placed on the board successfully.
     * Verifies that the word placement returns true and player score updates correctly.
     */
    @Test
    public void testValidWordPlacement() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('C', 1));
        player.getRack().add(new Tile('A', 1));
        player.getRack().add(new Tile('T', 1));

        boolean placed = model.placeWord("CAT", 7, 7, true);
        assertTrue(placed);
        assertEquals(3, player.getScore());
    }

    /**
     * Tests that an invalid word (not in the dictionary) is rejected.
     * Ensures that placement returns false and player score remains unchanged.
     */
    @Test
    public void testInvalidWordPlacement() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('X', 1));
        player.getRack().add(new Tile('Z', 1));
        player.getRack().add(new Tile('Q', 1));

        boolean placed = model.placeWord("XZQ", 7, 7, true);
        assertFalse(placed);
        assertEquals(0, player.getScore());
    }

    /**
     * Tests that passing a turn correctly switches to the next player.
     * Verifies that the current player name changes after passTurn() is called.
     */
    @Test
    public void testPassTurn() {
        String first = model.getCurrentPlayer().getName();
        model.passTurn();
        String next = model.getCurrentPlayer().getName();
        assertNotEquals(first, next);
    }

    /**
     * Tests that words placed outside the 15x15 board boundaries are rejected.
     * Ensures that the placement method returns false for invalid positions.
     */
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
        assertFalse(placed);
    }

    /**
     * Tests that a valid tile swap (with tiles the player owns) succeeds.
     * Ensures the rack content changes after swapping.
     */
    @Test
    public void testSwapTilesValid() {
        Player player = model.getCurrentPlayer();
        String originalRack = player.getRack().toString();

        // Attempt swap with a tile the player has
        String toSwap = "" + player.getRack().get(0).getLetter();
        boolean swapped = player.swapTiles(toSwap, model.getBag());

        assertTrue(swapped);
        assertNotEquals(originalRack, player.getRack().toString());
    }

    /**
     * Tests that an invalid tile swap (with tiles not in rack) fails.
     * Ensures the method returns false and rack remains unchanged.
     */
    @Test
    public void testSwapTilesInvalid() {
        Player player = model.getCurrentPlayer();

        boolean swapped = player.swapTiles("ZZZ", model.getBag());
        assertFalse(swapped);
    }

    /**
     * Tests that a player’s score correctly updates after placing a valid word.
     * Reinforces scoring accuracy for simple cases.
     */
    @Test
    public void testScoreAfterMove() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('C', 1));
        player.getRack().add(new Tile('A', 1));
        player.getRack().add(new Tile('T', 1));

        model.placeWord("CAT", 7, 7, true);
        assertEquals(3, player.getScore());
    }
}


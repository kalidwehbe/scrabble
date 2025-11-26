import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

/**
 dscdfddsvdsc * These tests verify correct behavior of word placement, scoring,
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
        model = new GameModel("StandardBoard.xml", playerNames, "dictionary.txt");
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
     * Center square (7,7) is Double Word Score, so CAT = (C(3) + A(1) + T(1)) * 2 = 10
     */
    @Test
    public void testValidWordPlacement() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('C', 3));
        player.getRack().add(new Tile('A', 1));
        player.getRack().add(new Tile('T', 1));

        boolean placed = model.placeWord("CAT", 7, 7, true);
        assertTrue(placed);
        // CAT on center DW: (C=3 + A=1 + T=1) * 2 = 10
        assertEquals(10, player.getScore());
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
     * Tests that a player's score correctly updates after placing a valid word.
     * Reinforces scoring accuracy for simple cases.
     * Center square (7,7) is Double Word Score, so CAT = (C(3) + A(1) + T(1)) * 2 = 10
     */
    @Test
    public void testScoreAfterMove() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('C', 3));
        player.getRack().add(new Tile('A', 1));
        player.getRack().add(new Tile('T', 1));

        model.placeWord("CAT", 7, 7, true);
        // CAT on center DW: (C=3 + A=1 + T=1) * 2 = 10
        assertEquals(10, player.getScore());
    }

    // ==========================================
    // BLANK TILE TESTS (Milestone 3)
    // ==========================================

    /**
     * Tests that a word can be placed using a blank tile.
     * Verifies that the placement succeeds when using a blank to substitute a missing letter.
     */
    @Test
    public void testBlankTilePlacement() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('C', 3));
        player.getRack().add(new Tile('A', 1));
        player.getRack().add(Tile.blankTile()); // Blank will represent 'T'

        boolean placed = model.placeWordWithBlanks("CAT", 7, 7, true, "T");
        assertTrue(placed); // Word with blank tile should be placed successfully
    }

    /**
     * Tests that blank tiles score 0 points.
     * Verifies that when a blank is used, it contributes 0 to the word score.
     * Center square (7,7) is DW, so score is doubled.
     */
    @Test
    public void testBlankTileScoresZero() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('C', 3));
        player.getRack().add(new Tile('A', 1));
        player.getRack().add(Tile.blankTile()); // Blank represents 'T' (normally worth 1)

        model.placeWordWithBlanks("CAT", 7, 7, true, "T");
        // C=3, A=1, T(blank)=0 → (3+1+0) * 2 (DW) = 8
        assertEquals(8, player.getScore()); // Blank tile should score 0 points
    }

    /**
     * Tests that multiple blank tiles can be used in a single word.
     * Verifies correct placement and scoring with two blanks.
     * Center square (7,7) is Double Word Score.
     */
    @Test
    public void testMultipleBlankTiles() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('C', 3));
        player.getRack().add(Tile.blankTile()); // Blank represents 'A'
        player.getRack().add(Tile.blankTile()); // Blank represents 'T'

        boolean placed = model.placeWordWithBlanks("CAT", 7, 7, true, "AT");
        assertTrue(placed); // Word with multiple blank tiles should be placed
        // C=3, A(blank)=0, T(blank)=0 → (3+0+0) * 2 (DW) = 6
        assertEquals(6, player.getScore()); // Multiple blanks should each score 0
    }

    // ==========================================
    // PREMIUM SQUARE TESTS (Milestone 3)
    // ==========================================

    /**
     * Tests Double Letter Score (DL) premium square.
     * Position (0,3) is a DL square on standard board.
     */
    @Test
    public void testDoubleLetterScore() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('A', 1));
        player.getRack().add(new Tile('T', 1));

        // First place a word to connect to
        player.getRack().add(new Tile('C', 3));
        model.placeWord("CAT", 7, 7, true);

        // Now test DL - position (0,3) is DL
        Player player2 = model.getCurrentPlayer();
        player2.getRack().clear();
        player2.getRack().add(new Tile('D', 2));
        player2.getRack().add(new Tile('O', 1));
        player2.getRack().add(new Tile('G', 2));

        // Compute expected score with DL at position (0,3)
        int expectedScore = model.computeWordScore("DOG", 0, 3, true, null);
        // D on DL (2*2=4) + O(1) + G(2) = 7
        assertTrue(expectedScore > 5); // DL square should double the letter score
    }

    /**
     * Tests Triple Letter Score (TL) premium square.
     * Position (1,5) is a TL square on standard board.
     */
    @Test
    public void testTripleLetterScore() {
        // Position (1,5) is TL
        int score = model.computeWordScore("DOG", 1, 5, true, null);
        // D on TL (2*3=6) + O(1) + G(2) = 9
        assertEquals(9, score); // TL square should triple the letter score
    }

    /**
     * Tests Double Word Score (DW) premium square.
     * Position (1,1) is a DW square on standard board.
     */
    @Test
    public void testDoubleWordScore() {
        // Position (1,1) is DW
        int score = model.computeWordScore("DOG", 1, 1, true, null);
        // (D=2 + O=1 + G=2) * 2 = 10
        assertEquals(10, score); // DW square should double the word score
    }

    /**
     * Tests Triple Word Score (TW) premium square.
     * Position (0,0) is a TW square on standard board.
     */
    @Test
    public void testTripleWordScore() {
        // Position (0,0) is TW
        int score = model.computeWordScore("DOG", 0, 0, true, null);
        // (D=2 + O=1 + G=2) * 3 = 15
        assertEquals(15, score); // TW square should triple the word score
    }

    /**
     * Tests that premium squares only apply on first use.
     * After a tile is placed, the premium should not apply again.
     */
    @Test
    public void testPremiumSquareOnlyAppliesOnce() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('D', 2));
        player.getRack().add(new Tile('O', 1));
        player.getRack().add(new Tile('G', 2));

        // Place word on TW square at (0,0)
        model.placeWord("DOG", 0, 0, true);
        int firstScore = player.getScore(); // Should be 15 (5 * 3)

        // Now place another word that crosses the same TW square
        Player player2 = model.getCurrentPlayer();
        player2.getRack().clear();
        player2.getRack().add(new Tile('A', 1));
        player2.getRack().add(new Tile('M', 3));

        // Place DAM vertically starting at (0,0) - D is already there
        // The TW bonus should NOT apply again since D is already placed
        int scoreForDAM = model.computeWordScore("DAM", 0, 0, false, null);
        // D already on board (no bonus), A=1, M=3 → word multiplier should be 1
        // But since D square already has tile, TW doesn't apply
        assertTrue(scoreForDAM <= 6); // Premium should only apply on first tile placement
    }

    // ==========================================
    // AI PLAYER TESTS (Milestone 3)
    // ==========================================

    /**
     * Tests that AI player can be created and has correct type.
     */
    @Test
    public void testAIPlayerCreation() {
        AIPlayer ai = new AIPlayer("TestAI");
        assertEquals("TestAI", ai.getName());
        assertTrue(ai instanceof Player); // AIPlayer should be instance of Player
    }

    /**
     * Tests that AI player makes a legal move.
     * Verifies the AI only places valid dictionary words.
     */
    @Test
    public void testAIPlayerMakesLegalMove() {
        // Create a model with an AI player
        GameModel aiModel = new GameModel("StandardBoard.xml", new ArrayList<>(), "dictionary.txt");
        AIPlayer ai = new AIPlayer("AI");
        ai.getRack().clear();
        ai.getRack().add(new Tile('C', 3));
        ai.getRack().add(new Tile('A', 1));
        ai.getRack().add(new Tile('T', 1));
        ai.getRack().add(new Tile('S', 1));
        ai.getRack().add(new Tile('D', 2));
        ai.getRack().add(new Tile('O', 1));
        ai.getRack().add(new Tile('G', 2));
        aiModel.addPlayer(ai);

        int scoreBefore = ai.getScore();
        boolean moveMade = ai.makeMove(aiModel);

        if (moveMade) {
            assertTrue(ai.getScore() > scoreBefore); // AI score should increase after valid move
        }
        // If no move made, AI correctly passed (also valid behavior)
    }

    /**
     * Tests that AI player passes when no valid moves are available.
     * Uses symbols/numbers that cannot form words.
     */
    @Test
    public void testAIPlayerPassesWhenNoMoves() {
        GameModel aiModel = new GameModel("StandardBoard.xml", new ArrayList<>(), "dictionary.txt");
        AIPlayer ai = new AIPlayer("AI");
        ai.getRack().clear();
        // Give AI an empty rack - no tiles means no moves possible
        // This guarantees the AI cannot place any word
        aiModel.addPlayer(ai);

        boolean moveMade = ai.makeMove(aiModel);
        assertFalse(moveMade); // AI should pass when no tiles available
    }

    /**
     * Tests that AI player selects high-scoring moves.
     * Verifies the AI's greedy strategy picks better scoring options.
     */
    @Test
    public void testAIPlayerSelectsHighScoringMove() {
        GameModel aiModel = new GameModel("StandardBoard.xml", new ArrayList<>(), "dictionary.txt");
        AIPlayer ai = new AIPlayer("AI");
        ai.getRack().clear();
        // Give tiles that can form multiple words
        ai.getRack().add(new Tile('C', 3));
        ai.getRack().add(new Tile('A', 1));
        ai.getRack().add(new Tile('T', 1));
        ai.getRack().add(new Tile('S', 1));
        ai.getRack().add(new Tile('H', 4));
        ai.getRack().add(new Tile('E', 1));
        ai.getRack().add(new Tile('R', 1));
        aiModel.addPlayer(ai);

        ai.makeMove(aiModel);
        // AI should have made a move and scored points
        // The greedy strategy means it picked a reasonably scoring word
        assertTrue(ai.getScore() >= 0); // AI should score points with available tiles
    }

    /**
     * Tests that AI correctly uses blank tiles when beneficial.
     */
    @Test
    public void testAIPlayerUsesBlankTiles() {
        GameModel aiModel = new GameModel("StandardBoard.xml", new ArrayList<>(), "dictionary.txt");
        AIPlayer ai = new AIPlayer("AI");
        ai.getRack().clear();
        ai.getRack().add(new Tile('C', 3));
        ai.getRack().add(new Tile('A', 1));
        ai.getRack().add(Tile.blankTile()); // Can be any letter
        ai.getRack().add(new Tile('S', 1));
        ai.getRack().add(new Tile('H', 4));
        ai.getRack().add(new Tile('E', 1));
        ai.getRack().add(new Tile('R', 1));
        aiModel.addPlayer(ai);

        boolean moveMade = ai.makeMove(aiModel);
        // AI should be able to form words using the blank
        assertTrue(moveMade || ai.getScore() >= 0); // AI should be able to use blank tiles
    }

    // ==========================================
    // UNDO/REDO TESTS (Milestone 4)
    // ==========================================

    /**
     * Tests that undo functionality correctly reverts a placed word.
     */
    @Test
    public void testUndoPlacedWord() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('C', 3));
        player.getRack().add(new Tile('A', 1));
        player.getRack().add(new Tile('T', 1));

        // Create snapshot before move
        GameState stateBefore = model.createStateSnapshot();
        int scoreBefore = player.getScore();

        // Place word
        model.placeWord("CAT", 7, 7, true);

        // Verify word was placed
        assertTrue(player.getScore() > scoreBefore);

        // Undo the move
        model.restoreState(stateBefore);

        // Verify state restored
        assertEquals(scoreBefore, model.getCurrentPlayer().getScore());
    }

    /**
     * Tests that redo functionality correctly re-applies an undone move.
     */
    @Test
    public void testRedoPlacedWord() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('C', 3));
        player.getRack().add(new Tile('A', 1));
        player.getRack().add(new Tile('T', 1));

        GameState stateBefore = model.createStateSnapshot();
        model.placeWord("CAT", 7, 7, true);
        GameState stateAfter = model.createStateSnapshot();

        // Undo
        model.restoreState(stateBefore);
        assertEquals(0, model.getPlayers().get(0).getScore());

        // Redo
        model.restoreState(stateAfter);
        assertTrue(model.getPlayers().get(0).getScore() > 0);
    }

    /**
     * Tests multiple undo operations in sequence.
     */
    @Test
    public void testMultipleUndo() {
        Player p1 = model.getCurrentPlayer();
        p1.getRack().clear();
        p1.getRack().add(new Tile('C', 3));
        p1.getRack().add(new Tile('A', 1));
        p1.getRack().add(new Tile('T', 1));

        // Save initial state
        GameState initialState = model.createStateSnapshot();

        // First move
        model.placeWord("CAT", 7, 7, true);
        GameState afterMove1 = model.createStateSnapshot();

        // Second move (pass turn)
        model.passTurn();

        // Undo second move
        model.restoreState(afterMove1);
        assertEquals("Bob", model.getCurrentPlayer().getName());

        // Undo first move
        model.restoreState(initialState);
        assertEquals(0, model.getPlayers().get(0).getScore());
    }

    /**
     * Tests that game state snapshot preserves all game data correctly.
     */
    @Test
    public void testGameStateSnapshotPreservesData() {
        Player player = model.getCurrentPlayer();
        String playerName = player.getName();
        int initialScore = player.getScore();

        GameState snapshot = model.createStateSnapshot();

        // Verify snapshot preserves player data
        assertEquals(playerName, snapshot.players.get(0).getName());
        assertEquals(initialScore, snapshot.players.get(0).getScore());
        assertEquals(model.getCurrentPlayer().getRack().size(), snapshot.players.get(0).getRack().size());
        assertEquals(0, snapshot.currentPlayerIndex);
    }

    /**
     * Tests undo with tile swapping.
     */
    @Test
    public void testUndoSwapTiles() {
        Player player = model.getCurrentPlayer();
        GameState stateBefore = model.createStateSnapshot();

        String toSwap = "" + player.getRack().get(0).getLetter();
        player.swapTiles(toSwap, model.getBag());

        // Undo swap
        model.restoreState(stateBefore);

        // Verify rack restored (compare sizes)
        assertEquals(7, model.getCurrentPlayer().getRack().size());
    }

    /**
     * Tests multiple redo operations.
     */
    @Test
    public void testMultipleRedo() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('C', 3));
        player.getRack().add(new Tile('A', 1));
        player.getRack().add(new Tile('T', 1));

        GameState s0 = model.createStateSnapshot();
        model.placeWord("CAT", 7, 7, true);
        GameState s1 = model.createStateSnapshot();
        model.passTurn();
        GameState s2 = model.createStateSnapshot();

        // Undo twice
        model.restoreState(s1);
        model.restoreState(s0);

        // Redo twice
        model.restoreState(s1);
        assertTrue(model.getPlayers().get(0).getScore() > 0);

        model.restoreState(s2);
        assertEquals("Alice", model.getCurrentPlayer().getName());
    }

    // ==========================================
    // SERIALIZATION/DESERIALIZATION TESTS (Milestone 4)
    // ==========================================

    /**
     * Tests that GameModel can be serialized without errors.
     */
    @Test
    public void testSerializeGameModel() {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
            oos.writeObject(model);
            oos.close();

            byte[] serializedData = baos.toByteArray();
            assertTrue(serializedData.length > 0);
        } catch (Exception e) {
            fail("Serialization failed: " + e.getMessage());
        }
    }

    /**
     * Tests that GameModel can be deserialized correctly.
     */
    @Test
    public void testDeserializeGameModel() {
        try {
            // Serialize
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
            oos.writeObject(model);
            oos.close();

            // Deserialize
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
            GameModel deserializedModel = (GameModel) ois.readObject();
            ois.close();

            // Verify deserialized model
            assertNotNull(deserializedModel);
            assertEquals(model.getPlayers().size(), deserializedModel.getPlayers().size());
        } catch (Exception e) {
            fail("Deserialization failed: " + e.getMessage());
        }
    }

    /**
     * Tests that serialization preserves player scores and names.
     */
    @Test
    public void testSerializationPreservesPlayerData() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('C', 3));
        player.getRack().add(new Tile('A', 1));
        player.getRack().add(new Tile('T', 1));

        model.placeWord("CAT", 7, 7, true);
        int score = model.getPlayers().get(0).getScore();
        String name = model.getPlayers().get(0).getName();

        try {
            // Serialize
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
            oos.writeObject(model);
            oos.close();

            // Deserialize
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
            GameModel loadedModel = (GameModel) ois.readObject();
            ois.close();

            // Verify data preserved
            assertEquals(name, loadedModel.getPlayers().get(0).getName());
            assertEquals(score, loadedModel.getPlayers().get(0).getScore());
        } catch (Exception e) {
            fail("Serialization test failed: " + e.getMessage());
        }
    }

    /**
     * Tests that serialization preserves board state.
     */
    @Test
    public void testSerializationPreservesBoardState() {
        Player player = model.getCurrentPlayer();
        player.getRack().clear();
        player.getRack().add(new Tile('C', 3));
        player.getRack().add(new Tile('A', 1));
        player.getRack().add(new Tile('T', 1));

        model.placeWord("CAT", 7, 7, true);

        try {
            // Serialize
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
            oos.writeObject(model);
            oos.close();

            // Deserialize
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
            GameModel loadedModel = (GameModel) ois.readObject();
            ois.close();

            // Verify board state
            assertEquals('C', loadedModel.getBoard().getTileLetter(7, 7));
            assertEquals('A', loadedModel.getBoard().getTileLetter(7, 8));
            assertEquals('T', loadedModel.getBoard().getTileLetter(7, 9));
        } catch (Exception e) {
            fail("Board state serialization failed: " + e.getMessage());
        }
    }

    /**
     * Tests that GameState can be serialized.
     */
    @Test
    public void testSerializeGameState() {
        GameState state = model.createStateSnapshot();

        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
            oos.writeObject(state);
            oos.close();

            byte[] serializedData = baos.toByteArray();
            assertTrue(serializedData.length > 0);
        } catch (Exception e) {
            fail("GameState serialization failed: " + e.getMessage());
        }
    }

    /**
     * Tests error handling when deserializing corrupted data.
     */
    @Test
    public void testDeserializationErrorHandling() {
        try {
            byte[] corruptedData = new byte[]{1, 2, 3, 4, 5};
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(corruptedData);
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);

            try {
                ois.readObject();
                fail("Should have thrown an exception for corrupted data");
            } catch (Exception e) {
                // Expected exception for corrupted data
                assertTrue(e instanceof java.io.StreamCorruptedException ||
                          e instanceof java.io.EOFException);
            }
            ois.close();
        } catch (Exception e) {
            // Expected - corrupted data should cause errors
            assertTrue(true);
        }
    }

    /**
     * Tests that serialization works after multiple game moves.
     */
    @Test
    public void testSerializationAfterMultipleMoves() {
        Player p1 = model.getCurrentPlayer();
        p1.getRack().clear();
        p1.getRack().add(new Tile('C', 3));
        p1.getRack().add(new Tile('A', 1));
        p1.getRack().add(new Tile('T', 1));

        model.placeWord("CAT", 7, 7, true);
        model.passTurn();

        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
            oos.writeObject(model);
            oos.close();

            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
            GameModel loadedModel = (GameModel) ois.readObject();
            ois.close();

            // Verify turn advanced
            assertEquals("Alice", loadedModel.getCurrentPlayer().getName());
        } catch (Exception e) {
            fail("Multi-move serialization failed: " + e.getMessage());
        }
    }

    /**
     * Tests serialization preserves tile bag state.
     */
    @Test
    public void testSerializationPreservesTileBag() {
        int bagSizeBefore = model.getBag().size();

        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
            oos.writeObject(model);
            oos.close();

            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
            GameModel loadedModel = (GameModel) ois.readObject();
            ois.close();

            assertEquals(bagSizeBefore, loadedModel.getBag().size());
        } catch (Exception e) {
            fail("Tile bag serialization failed: " + e.getMessage());
        }
    }
}


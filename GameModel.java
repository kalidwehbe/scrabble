import java.util.*;

/**
 * GameModel represents the state of a Scrabble game.
 * It manages the board, players, tile bag, dictionary, and turn order.
 * Observers can register to be notified of changes to the game state.
 */
public class GameModel {

    private Board board;                     // The Scrabble board
    private List<Player> players;            // List of players in the game
    private Queue<Tile> bag;                 // Bag of remaining tiles
    private int currentPlayerIndex;          // Index of the player whose turn it is
    private List<GameObserver> observers;    // List of registered observers
    private Dictionary dictionary;           // Game dictionary for word validation
    private boolean firstMove = true;        // ability to tell if we are on the first move

    // Official Scrabble letter values (blanks = 0)
    public static final Map<Character, Integer> LETTER_VALUES = Map.ofEntries(
            Map.entry('A', 1), Map.entry('E', 1), Map.entry('I', 1), Map.entry('O', 1),
            Map.entry('U', 1), Map.entry('L', 1), Map.entry('N', 1), Map.entry('S', 1),
            Map.entry('T', 1), Map.entry('R', 1),
            Map.entry('D', 2), Map.entry('G', 2),
            Map.entry('B', 3), Map.entry('C', 3), Map.entry('M', 3), Map.entry('P', 3),
            Map.entry('F', 4), Map.entry('H', 4), Map.entry('V', 4), Map.entry('W', 4),
            Map.entry('Y', 4),
            Map.entry('K', 5),
            Map.entry('J', 8), Map.entry('X', 8),
            Map.entry('Q', 10), Map.entry('Z', 10)
    );

    /**
     * Constructs a new GameModel with the given players and dictionary file.
     *
     * @param names List of player names
     * @param dictionaryFile Path to dictionary file for valid words
     */
    public GameModel(String boardFile, List<String> names, String dictionaryFile) {
        board = new Board(boardFile);
        players = new ArrayList<>();
        observers = new ArrayList<>();
        bag = createTileBag();
        dictionary = new Dictionary(dictionaryFile);

        for (String name : names) {
            Player p = new Player(name);
            p.drawTiles(bag, 7);  // Draw initial 7 tiles
            players.add(p);
        }
        currentPlayerIndex = 0;
    }

    /**
     * Creates and returns a shuffled bag of Scrabble tiles.
     *
     * @return Queue of Tile objects representing the tile bag
     */
    private Queue<Tile> createTileBag() {
        List<Tile> tiles = new ArrayList<>();
        addTiles(tiles, 'A', 9); addTiles(tiles, 'B', 2); addTiles(tiles, 'C', 2);
        addTiles(tiles, 'D', 4); addTiles(tiles, 'E', 12); addTiles(tiles, 'F', 2);
        addTiles(tiles, 'G', 3); addTiles(tiles, 'H', 2); addTiles(tiles, 'I', 9);
        addTiles(tiles, 'J', 1); addTiles(tiles, 'K', 1); addTiles(tiles, 'L', 4);
        addTiles(tiles, 'M', 2); addTiles(tiles, 'N', 6); addTiles(tiles, 'O', 8);
        addTiles(tiles, 'P', 2); addTiles(tiles, 'Q', 1); addTiles(tiles, 'R', 6);
        addTiles(tiles, 'S', 4); addTiles(tiles, 'T', 6); addTiles(tiles, 'U', 4);
        addTiles(tiles, 'V', 2); addTiles(tiles, 'W', 2); addTiles(tiles, 'X', 1);
        addTiles(tiles, 'Y', 2); addTiles(tiles, 'Z', 1);

        // Add 2 blanks (wildcards) - standard Scrabble rules
        for (int i = 0; i < 2; i++) tiles.add(Tile.blankTile());

        Collections.shuffle(tiles);
        return new LinkedList<>(tiles);
    }

    /**
     * Adds a specified number of tiles with a given letter to a list.
     * Tile score now uses the official LETTER_VALUES map.
     *
     * @param list List to add tiles to
     * @param letter Character representing the tile
     * @param count Number of tiles to add
     */
    private void addTiles(List<Tile> list, char letter, int count) {
        int score = LETTER_VALUES.getOrDefault(Character.toUpperCase(letter), 1);
        for (int i = 0; i < count; i++) list.add(new Tile(letter, score));
    }

    /**
     * Returns the current player whose turn it is.
     *
     * @return Player object representing the current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public boolean isFirstMove() { return firstMove; }
    public void setFirstMoveDone() { firstMove = false; }
    public Board getBoard() {
        return board;
    }

    /**
     * Returns the tile bag.
     *
     * @return Queue of Tile objects remaining in the bag
     */
    public Queue<Tile> getBag() {
        return bag;
    }

    /**
     * Registers an observer to be notified of game state changes.
     *
     * @param obs Observer implementing GameObserver interface
     */
    public void addObserver(GameObserver obs) {
        observers.add(obs);
    }

    /**
     * Notifies all registered observers of the current game state.
     */
    private void notifyObservers() {
        Player current = getCurrentPlayer();
        for (GameObserver obs : observers) {
            obs.update(board, players, current);
        }
    }


    private Stack<GameState> undoStack;
    private Stack<GameState> redoStack;

    public void saveStateForUndo() {
        undoStack.push(createStateSnapshot());
        redoStack.clear(); // new action invalidates redo history
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(createStateSnapshot());
            GameState prev = undoStack.pop();
            restoreState(prev);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(createStateSnapshot());
            GameState next = redoStack.pop();
            restoreState(next);
        }
    }

    public GameState createStateSnapshot() {
        Board boardCopy = board.copy();
        List<Player> playersCopy = new ArrayList<>();
        for (Player p : players) playersCopy.add(p.copy());
        Queue<Tile> bagCopy = new LinkedList<>();
        for (Tile t : bag) bagCopy.add(t.copy());
        return new GameState(boardCopy, playersCopy, bagCopy, currentPlayerIndex, firstMove);
    }

    public void restoreState(GameState state) {
        this.board = state.board;
        this.players = state.players;
        this.bag = state.bag;
        this.currentPlayerIndex = state.currentPlayerIndex;
        this.firstMove = state.firstMove;
        notifyObservers();
    }


    /**
     * Simple helper to compute score for a word without premium squares.
     * blankIndices lists positions (0-based) of blanks used in this placement.
     * This sums the letter values for the complete main word (including letters already on the board).
     */
    public int computeWordScore(String word, int row, int col, boolean horizontal, List<Integer> blankIndices) {
        int wordMultiplier = 1;
        int total = 0;

        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            char ch = word.charAt(i);

            Square sq = board.getSquare(r, c);
            int letterScore = (blankIndices != null && blankIndices.contains(i)) ? 0 : LETTER_VALUES.getOrDefault(ch, 0);
            if (!sq.hasTile()){
                switch (sq.getBonus()) {
                    case DL: letterScore *= 2; break;
                    case TL: letterScore *= 3; break;
                    case DW: wordMultiplier *= 2; break;
                    case TW: wordMultiplier *= 3; break;
                }
            }
            total += letterScore;
        }
        return total * wordMultiplier;
    }

    /**
     * Attempts to place a word on the board for the current player (no blanks).
     * Updates scores, player tiles, and notifies observers.
     *
     * @param word Word to place on the board
     * @param row Starting row index (0-based)
     * @param col Starting column index (0-based)
     * @param horizontal True if word is placed horizontally, false for vertical
     * @return True if the word was successfully placed, false otherwise
     */
    public boolean placeWord(String word, int row, int col, boolean horizontal) {
        Player p = getCurrentPlayer();
        word = word.toUpperCase();

        // Validate dictionary
        if (!dictionary.isValidWord(word)) {
            p.setLastError("Invalid word! Not in dictionary.");
            notifyObservers();
            return false;
        }

        // Check if player has the necessary tiles
        if (!board.canPlaceWordWithRack(word, row, col, horizontal, p)) {
            p.setLastError("You don't have the necessary tiles for this word!");
            notifyObservers();
            return false;
        }

        // Determine newly placed positions
        List<Integer> newlyPlacedIndices = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (!board.squareHasTile(r, c)) newlyPlacedIndices.add(i);
        }
        int scoreGained = computeWordScore(word, row, col, horizontal, new ArrayList<>()); // no blanks, so null
        // Place tiles on the board
        if (board.placeWord(word, row, col, horizontal, p)) {

            // Bingo bonus: 50 points if player placed all 7 tiles this turn
            if (newlyPlacedIndices.size() == 7) scoreGained += 50;
            p.addScore(scoreGained);

            // Refill player's rack with as many tiles as they placed
            p.drawTiles(bag, newlyPlacedIndices.size());

            notifyObservers();
            nextTurn();
            p.setLastError(""); // clear previous errors
            return true;
        }

        p.setLastError("Invalid placement!");
        notifyObservers();
        return false;
    }


    /**
     * Place word variant that accepts blank letter assignments (blanks string).
     * 'blanks' string contains letters (e.g. "LO") which are assigned in order
     * to the missing letters in the word.
     *
     * @param word Word to place
     * @param row start row (0-based)
     * @param col start col (0-based)
     * @param horizontal orientation
     * @param blanks letters used to represent blanks, in order
     * @return true on success
     */
    public boolean placeWordWithBlanks(String word, int row, int col, boolean horizontal, String blanks) {
        Player p = getCurrentPlayer();
        word = word.toUpperCase();
        blanks = (blanks == null) ? "" : blanks.toUpperCase();

        // validate dictionary
        if (!dictionary.isValidWord(word)) { p.setLastError("Not in dictionary."); notifyObservers(); return false; }

        // Check rack availability including blanks
        List<Tile> rackCopy = new ArrayList<>(p.getRack());
        List<Integer> blankIndices = new ArrayList<>();
        String blanksRemaining = blanks;

        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            char letter = word.charAt(i);

            if (!board.squareHasTile(r, c)) {
                boolean found = false;
                for (int j = 0; j < rackCopy.size(); j++) {
                    Tile t = rackCopy.get(j);
                    if (!t.isBlank() && t.getLetter() == letter) { rackCopy.remove(j); found = true; break; }
                }
                if (!found) {
                    if (!blanksRemaining.isEmpty()) {
                        blanksRemaining = blanksRemaining.substring(1);
                        blankIndices.add(i);
                        boolean consumed = false;
                        for (int j = 0; j < rackCopy.size(); j++) {
                            if (rackCopy.get(j).isBlank()) { rackCopy.remove(j); consumed = true; break; }
                        }
                        if (!consumed) { p.setLastError("Missing blank tiles"); notifyObservers(); return false; }
                    } else { p.setLastError("Missing tiles"); notifyObservers(); return false; }
                }
            } else {
                if (board.getLetterAt(r, c) != letter) { p.setLastError("Conflict with board"); notifyObservers(); return false; }
            }
        }

        // Place tiles on board
        List<Integer> newlyPlacedIndices = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (!board.squareHasTile(r, c)) newlyPlacedIndices.add(i);
        }
        int score = computeWordScore(word, row, col, horizontal, blankIndices);
        if (board.placeWord(word, row, col, horizontal, p)) {
            // Compute premium score


            // Bingo bonus
            if (newlyPlacedIndices.size() == 7) score += 50;

            p.addScore(score);

            // Draw new tiles
            p.drawTiles(bag, newlyPlacedIndices.size());

            notifyObservers();
            nextTurn();
            p.setLastError("");
            return true;
        }

        p.setLastError("Invalid placement!");
        notifyObservers();
        return false;
    }


    /**
     * Passes the current player's turn without making a move.
     */
    public void passTurn() {
        getCurrentPlayer().setLastError("Turn passed.");
        nextTurn();
    }

    /**
     * Advances the turn to the next player and notifies observers.
     */
    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        notifyObservers();
    }

    /**
     * Starts the game by notifying all observers of the initial state.
     */
    public void start() {
        notifyObservers();
    }



    public Dictionary getDictionary() {
        return dictionary;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player p) {
        players.add(p);
    }


}

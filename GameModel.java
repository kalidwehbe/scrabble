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
    private boolean firstMove = true;        //ability to tell if we are on the first move to automatically invoke placement on middle of board

    /**
     * Constructs a new GameModel with the given players and dictionary file.
     *
     * @param names List of player names
     * @param dictionaryFile Path to dictionary file for valid words
     */
    public GameModel(List<String> names, String dictionaryFile) {
        board = new Board();
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

        Collections.shuffle(tiles);
        return new LinkedList<>(tiles);
    }

    /**
     * Adds a specified number of tiles with a given letter to a list.
     *
     * @param list List to add tiles to
     * @param letter Character representing the tile
     * @param count Number of tiles to add
     */
    private void addTiles(List<Tile> list, char letter, int count) {
        for (int i = 0; i < count; i++) list.add(new Tile(letter, 1));
    }

    /**
     * Returns the current player whose turn it is.
     *
     * @return Player object representing the current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
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

    /**
     * Attempts to place a word on the board for the current player.
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

        if (!dictionary.isValidWord(word)) {
            p.setLastError("Invalid word! Not in dictionary.");
            notifyObservers();
            return false;
        }

        if (!board.canPlaceWordWithRack(word, row, col, horizontal, p)) {
            p.setLastError("You don't have the necessary tiles for this word!");
            notifyObservers();
            return false;
        }

        List<int[]> placedPositions = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (!board.squareHasTile(r, c)) placedPositions.add(new int[]{r, c, i});
        }

        if (board.placeWord(word, row, col, horizontal, p)) {
            for (int[] pos : placedPositions) {
                char letter = word.charAt(pos[2]);
                p.useTilesForWord(Character.toString(letter));
            }
            p.addScore(placedPositions.size());
            p.drawTiles(bag, placedPositions.size());

            notifyObservers();
            nextTurn();
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

    public boolean isFirstMove() { return firstMove; }
    public void setFirstMoveDone() { firstMove = false; }
}

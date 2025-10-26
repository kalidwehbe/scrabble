import java.util.*;

public class GameModel {
    private Board board;
    private List<Player> players;
    private Queue<Tile> bag;
    private int currentPlayerIndex;
    private List<GameObserver> observers;
    private Dictionary dictionary;

    public GameModel(List<String> names, String dictionaryFile) {
        board = new Board();
        players = new ArrayList<>();
        observers = new ArrayList<>();
        bag = createTileBag();
        dictionary = new Dictionary(dictionaryFile);

        for (String name : names) {
            Player p = new Player(name);
            p.drawTiles(bag, 7);
            players.add(p);
        }

        currentPlayerIndex = 0;
    }
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

    private void addTiles(List<Tile> list, char letter, int count) {
        for (int i = 0; i < count; i++) list.add(new Tile(letter, 1));
    }

    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }
    public void addObserver(GameObserver obs) { observers.add(obs); }

    private void notifyObservers() {
        for (GameObserver obs : observers) obs.update(board, players);
    }

    // Place a word on the board
    public void placeWord(String word, int row, int col, boolean horizontal) {
        Player p = getCurrentPlayer();

        // Check dictionary first
        if (!dictionary.isValidWord(word)) {
            System.out.println("Invalid word! Not in dictionary.");
            return;
        }

        // Check if player has necessary tiles including board reuse
        if (!board.canPlaceWordWithRack(word, row, col, horizontal, p)) {
            System.out.println("You don't have the necessary tiles for this word!");
            return;
        }

        // Record which positions are empty before placement
        List<int[]> placedPositions = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (!board.squareHasTile(r, c)) {
                placedPositions.add(new int[]{r, c, i}); // row, col, index in word
            }
        }

        // Place the word
        if (board.placeWord(word, row, col, horizontal)) {
            // Remove tiles from player's rack
            for (int[] pos : placedPositions) {
                char letter = word.charAt(pos[2]); // get letter from word
                p.useTilesForWord(Character.toString(letter));
            }

            // Update score (simplified: 1 point per tile placed)
            p.addScore(placedPositions.size());

            // Refill player's rack
            p.drawTiles(bag, placedPositions.size());

            notifyObservers();
            nextTurn();
        } else {
            System.out.println("Invalid placement!");
        }
    }

    public void passTurn() {
        System.out.println(getCurrentPlayer().getName() + " passed.");
        nextTurn();
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        notifyObservers();
    }

    public void start() {
        notifyObservers();
    }
}

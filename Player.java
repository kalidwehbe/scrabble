import java.util.*;

/**
 * Represents a player in the Scrabble game.
 * Manages the player's name, tile rack, score, and error messages.
 * Each player maintains up to 7 tiles and can perform operations like
 * drawing tiles, placing words, and swapping tiles.
 */
public class Player {
    private String name;
    private List<Tile> rack;
    private int score;
    private String lastError = "";

    /**
     * Constructs a new Player with the specified name.
     * Initializes the player with an empty rack and zero score.
     * @param name The player's name
     */
    public Player(String name) { this.name = name; rack = new ArrayList<>(); score = 0; }

    /**
     * Returns the player's name.
     * @return The name of this player
     */
    public String getName() { return name; }

    /**
     * Returns the player's current score.
     * @return The player's score
     */
    public int getScore() { return score; }

    /**
     * Returns the player's current tile rack.
     * @return List of tiles in the player's rack
     */
    public List<Tile> getRack() { return rack; }

    /**
     * Returns the last error message for this player.
     * @return The most recent error message, or empty string if none
     */
    public String getLastError() { return lastError; }

    /**
     * Sets an error message for this player.
     * Used to communicate invalid moves or actions to the player.
     * @param msg The error message to set
     */
    public void setLastError(String msg) { lastError = msg; }

    /**
     * Adds points to the player's score.
     * @param pts The number of points to add
     */
    public void addScore(int pts) { score += pts; }

    /**
     * Draws tiles from the bag to fill the player's rack.
     * Continues drawing until the rack has 7 tiles, the bag is empty,
     * or the specified count is reached.
     * @param bag The tile bag to draw from
     * @param count Maximum number of tiles to draw
     */
    public void drawTiles(Queue<Tile> bag, int count) {
        while (rack.size() < 7 && !bag.isEmpty() && count > 0) { rack.add(bag.poll()); count--; }
    }

    /**
     * Checks if the player has all the tiles needed to form a word.
     * This method accounts for tiles already on the board.
     * @param word The word to check
     * @return true if the player has all necessary tiles, false otherwise
     */
    public boolean hasTilesForWord(String word) {
        List<Character> copy = new ArrayList<>();
        for (Tile t : rack) copy.add(t.getLetter());
        for (char c : word.toCharArray()) if (!copy.remove((Character)c)) return false;
        return true;
    }

    /**
     * Removes the tiles needed for a word from the player's rack.
     * Should only be called after confirming the player has the tiles.
     * @param word The word whose tiles should be removed from the rack
     */
    public void useTilesForWord(String word) {
        for (char c : word.toCharArray())
            for (int i = 0; i < rack.size(); i++)
                if (rack.get(i).getLetter() == c) { rack.remove(i); break; }
    }

    /**
     * Swaps specified tiles from the player's rack with tiles from the bag.
     * Returns tiles to the bag and draws new ones. This counts as a turn.
     * @param letters String of letters to swap (e.g., "ABC")
     * @param bag The tile bag to swap with
     * @return true if swap was successful, false if player doesn't have those tiles
     */
    public boolean swapTiles(String letters, Queue<Tile> bag) {
        if (!hasTilesForWord(letters)) return false;
        for (char c : letters.toCharArray())
            for (int i = 0; i < rack.size(); i++)
                if (rack.get(i).getLetter() == c) { bag.offer(rack.remove(i)); break; }
        drawTiles(bag, letters.length());
        return true;
    }
}

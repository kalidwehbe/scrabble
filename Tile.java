/**
 * Tile represents a single letter tile in the Scrabble game.
 * Each tile has a letter, a point value (score), and may be a blank tile.
 * Blank tiles can represent any letter but are worth 0 points.
 */
public class Tile {
    private char letter;
    private int score;
    private boolean blank; // true for blank tiles

    /**
     * Constructs a regular tile with a specified letter and score.
     *
     * @param letter The letter on the tile
     * @param score The point value of the tile
     */
    public Tile(char letter, int score) {
        this.letter = Character.toUpperCase(letter);
        this.score = score;
        this.blank = false;
    }

    /**
     * Internal constructor used for creating blank tiles.
     *
     * @param letter The letter (or '*' for unassigned blank)
     * @param score The point value (always 0 for blanks)
     * @param blank Whether this is a blank tile
     */
    private Tile(char letter, int score, boolean blank) {
        this.letter = Character.toUpperCase(letter);
        this.score = score;
        this.blank = blank;
    }

    /**
     * Creates an unassigned blank tile for the bag or rack.
     * The blank is represented as '*' until assigned a letter.
     *
     * @return A new blank tile
     */
    public static Tile blankTile() {
        return new Tile('*', 0, true); // '*' used internally to represent an unused blank
    }

    /**
     * Creates a placed blank tile representing a specific letter.
     * The tile displays the chosen letter but scores 0 points.
     *
     * @param chosenLetter The letter this blank will represent
     * @return A new blank tile assigned to the chosen letter
     */
    public static Tile placedBlank(char chosenLetter) {
        return new Tile(chosenLetter, 0, true);
    }

    /**
     * Returns the letter on this tile.
     *
     * @return The tile's letter
     */
    public char getLetter() { return letter; }

    /**
     * Returns the point value of this tile.
     * Blank tiles always return 0.
     *
     * @return The tile's score
     */
    public int getScore() { return score; }

    /**
     * Sets the point value of this tile.
     *
     * @param point The new score value
     */
    public void setScore(int point) {
        score = point;
    }

    /**
     * Checks if this tile is a blank tile.
     *
     * @return true if this is a blank tile, false otherwise
     */
    public boolean isBlank() { return blank; }

    public Tile copy() {
        return new Tile(this.letter, this.score, this.blank);
    }

    /**
     * Returns a string representation of the tile (just the letter).
     *
     * @return The tile's letter as a string
     */
    @Override
    public String toString() {
        return Character.toString(letter);
    }
}

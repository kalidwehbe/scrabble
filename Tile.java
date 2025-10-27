/**
 * The Tile class represents an individual Scrabble tile.
 * Each tile has a letter and a corresponding score value.
 * 
 * This class is immutable — once created, a tile’s letter and score cannot change.
 */
public class Tile {
    /** The letter printed on the tile (e.g., 'A', 'B', 'C'). */
    private char letter;
    /** The score value associated with this tile’s letter. */
    private int score;
    /**
     * Constructs a new Tile with the specified letter and score.
     *
     * @param letter the letter on the tile
     * @param score the point value of the tile
     */
    public Tile(char letter, int score) {
        this.letter = letter;
        this.score = score;
    }
    /**
     * Gets the letter on this tile.
     *
     * @return the tile’s letter as a char
     */
    public char getLetter() {
        return letter;
    }
    /**
     * Gets the score value of this tile.
     *
     * @return the tile’s score as an integer
     */
    public int getScore() {
        return score;
    }
    /**
     * Returns a string representation of the tile.
     * In this case, it simply returns the letter.
     *
     * @return the tile’s letter as a String
     */
    @Override
    public String toString() {
        return Character.toString(letter);
    }
}
